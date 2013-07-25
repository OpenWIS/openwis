Ext.ns('Openwis.Common.Dissemination');

Openwis.Common.Dissemination.FTPDiffusion = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			border: false,
			allowHostEdition: true
		});
		Openwis.Common.Dissemination.FTPDiffusion.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	//----------------------------------------------------------------- Initialization.
	
	initialize: function() {
		this.add(this.getHostTextField());
		this.add(this.getPathTextField());
		this.add(this.getUserTextField());
		this.add(this.getPasswordTextField());
		
		
		this.getAdvancedFieldSet().add(this.getPortTextField());
		
		this.getAdvancedFieldSet().add(this.getOptionsCheckboxGroup());
		
		this.getAdvancedFieldSet().add(this.getFileNameTextField());
		
		this.add(this.getAdvancedFieldSet());
		
		
		//Associate each component to a value.
		this.ftpFields = {};
		this.ftpFields['host'] = this.getHostTextField();
		this.ftpFields['path'] = this.getPathTextField();
		this.ftpFields['user'] = this.getUserTextField();
		this.ftpFields['password'] = this.getPasswordTextField();
		this.ftpFields['port'] = this.getPortTextField();
		this.ftpFields['passive'] = this.getPassiveCheckbox();
		this.ftpFields['checkFileSize'] = this.getCheckFileSizeCheckbox();
		this.ftpFields['fileName'] = this.getFileNameTextField();
		this.ftpFields['encrypted'] = this.getEncryptedCheckbox();
	},
	
	//----------------------------------------------------------------- Components.
	getHostTextField: function() {
		if(!this.hostTextField) {
			var hostStore = new Ext.data.JsonStore ({
				id: 0,
				fields: [
					{name:'host'},
					{name:'path'},
					{name:'user'},
					{name:'password'},
					{name:'port'},
					{name:'passive'},
					{name:'checkFileSize'},
					{name:'fileName'},
					{name:'encrypted'}
				]
			});
			
			this.hostTextField = new Ext.form.ComboBox({
				store: hostStore,
				valueField: 'host',
				displayField:'host',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				fieldLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.Host.label'),
				allowBlank: false,
				disabled : !this.allowHostEdition,
				width: 210,
				listeners : {
					select: function() {
						this.notifyFTPSelected();
					},
					scope: this
				}
			});
		}
		return this.hostTextField;
	},
    
	getPathTextField: function() {
		if(!this.pathTextField) {
			this.pathTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.Path.label'),
				name: 'ftpPath',
				allowBlank:false
			});
		}
		return this.pathTextField;
	},
	
	getUserTextField: function() {
		if(!this.userTextField) {
			this.userTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.User.label'),
				name: 'ftpUser',
				allowBlank: false,
				width: 100
			});
		}
		return this.userTextField;
	},
	
	getPasswordTextField: function() {
		if(!this.passwordTextField) {
			this.passwordTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.Password.label'),
				name: 'ftpPwd',
				inputType: 'password',
				allowBlank: false,
				width: 100
			});
		}
		return this.passwordTextField;
	},
	
	getAdvancedFieldSet: function() {
		if(!this.advancedFieldSet) {
			this.advancedFieldSet = new Ext.form.FieldSet({
				title: Openwis.i18n('Common.Dissemination.FTPDiffusion.Advanced.label'),
				autoHeight:true,
				collapsed: true,
				collapsible: true,
				labelWidth: 90
			});
		}
		return this.advancedFieldSet;
	},
	
	getOptionsCheckboxGroup: function() {
		if(!this.optionsCheckboxGroup) {
			this.optionsCheckboxGroup = new Ext.form.CheckboxGroup({
				title: Openwis.i18n('Common.Dissemination.FTPDiffusion.Options.label'),
				items:
				[
					this.getPassiveCheckbox(),
					this.getCheckFileSizeCheckbox(),
					this.getEncryptedCheckbox()
				]
			});
		}
		return this.optionsCheckboxGroup;
	},
	
	getPortTextField: function() {
		if(!this.portTextField) {
			this.portTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.Port.label'),
				name: 'ftpPort',
				allowBlank: true,
				width: 50
			});
		}
		return this.portTextField;
	},
	
	getPassiveCheckbox: function() {
		if(!this.passiveCheckbox) {
			this.passiveCheckbox = new Ext.form.Checkbox({
				boxLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.Passive.label'),
				name: 'ftpPassive'
			});
		}
		return this.passiveCheckbox;
	},
	
	getCheckFileSizeCheckbox: function() {
		if(!this.checkFileSizeCheckbox) {
			this.checkFileSizeCheckbox = new Ext.form.Checkbox({
				boxLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.CheckFileSize.label'),
				name: 'ftpCheckFileSize'
			});
		}
		return this.checkFileSizeCheckbox;
	},
	
	getEncryptedCheckbox: function() {
		if(!this.encryptedCheckbox) {
			this.encryptedCheckbox = new Ext.form.Checkbox({
				boxLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.Encrypted.label'),
				name: 'encrypted'
			});
		}
		return this.encryptedCheckbox;
	},
	
	getFileNameTextField: function() {
		if(!this.fileNameTextField) {
			this.fileNameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.FTPDiffusion.FileName.label'),
				name: 'fileName',
				allowBlank: true,
				width: 200
			});
		}
		return this.fileNameTextField;
	},

	//----------------------------------------------------------------- Utility methods.
	
	notifyFTPSelected: function() {
		var ftpHostSelected = this.getHostTextField().getValue();
		if(ftpHostSelected) {
			var ftpSelected = null;
			for(var i = 0; i < this.getHostTextField().getStore().getCount(); i++) {
				if(this.getHostTextField().getStore().getAt(i).get('host') == ftpHostSelected) {
					ftpSelected = this.getHostTextField().getStore().getAt(i).data;
					break;
				}
			}
			this.initializeFields(ftpSelected);
		}
	},
	
	initializeFields: function(ftp) {
		Ext.iterate(ftp, function(key, value) {
			if(this.ftpFields[key]) {
				this.ftpFields[key].setValue(value);
			}
		}, this);
	},
	
	getDisseminationValue: function() {
		var ftp = {};
		
		Ext.iterate(this.ftpFields, function(key, field) {
			ftp[key] = field.getValue();
		}, this);
		
		return ftp;
	},

	refresh: function(favoritesFtps) {
		this.getHostTextField().getStore().loadData(favoritesFtps);
	}
	
});