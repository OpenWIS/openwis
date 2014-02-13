Ext.ns('Openwis.Common.Dissemination');

Openwis.Common.Dissemination.MailDiffusion = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			border: false,
			allowAddressEdition: true
		});
		Openwis.Common.Dissemination.MailDiffusion.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	//----------------------------------------------------------------- Initialization.
	
	initialize: function() {
		this.add(this.getAddressTextField());
				
		this.getAdvancedFieldSet().add(this.getHeaderLineTextField());
		this.getAdvancedFieldSet().add(this.getDispatchModeComboBox());
		this.getAdvancedFieldSet().add(this.getSubjectTextField());
		this.getAdvancedFieldSet().add(this.getAttachmentModeRadioGroup());
		this.getAdvancedFieldSet().add(this.getFileNameTextField());
		
		this.add(this.getAdvancedFieldSet());
		
		
		//Associate each component to a value.
		this.mailFields = {};
		this.mailFields['address'] = this.getAddressTextField();
		this.mailFields['headerLine'] = this.getHeaderLineTextField();
		this.mailFields['mailDispatchMode'] = this.getDispatchModeComboBox();
		this.mailFields['subject'] = this.getSubjectTextField();
		this.mailFields['mailAttachmentMode'] = this.getAttachmentModeRadioGroup();//mailAsAttachment;
		this.mailFields['fileName'] = this.getFileNameTextField();
	},
	
	//----------------------------------------------------------------- Components.
	getAddressTextField: function() {
		if(!this.addressTextField) {
			var addressStore = new Ext.data.JsonStore ({
				id: 0,
				fields: [
					{name:'address'},
					{name:'headerLine'},
					{name:'mailDispatchMode'},
					{name:'subject'},
					{name:'mailAttachmentMode'},
					{name:'fileName'}
				]
			});
			
			this.addressTextField = new Ext.form.ComboBox({
				store: addressStore,
				valueField:'address',
				displayField:'address',
				typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				selectOnFocus:true,
				fieldLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.MailAddress.label'),
				allowBlank: false,
				disabled : !this.allowAddressEdition,
                vtype:'email',
				width: 210,
				validateOnChange: true,
				listeners : {
					select: function() {
						this.notifyMailSelected();
					},
					scope:this
				}
			});
		}
		return this.addressTextField;
	},
	
	getAdvancedFieldSet: function() {
		if(!this.advancedFieldSet) {
			this.advancedFieldSet = new Ext.form.FieldSet({
				title: Openwis.i18n('Common.Dissemination.MailDiffusion.Advanced.label'),
				autoHeight:true,
				collapsed: true,
				collapsible: true,
				labelWidth: 90
			});
		}
		return this.advancedFieldSet;
	},
	
	getHeaderLineTextField: function() {
		if(!this.headerLineTextField) {
			this.headerLineTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.HeaderLine.label'),
				name: 'headerLine',
				allowBlank: true
			});
		}
		return this.headerLineTextField;
	},
	
	getDispatchModeComboBox: function() {
		if(!this.dispatchModeComboBox) {
			this.dispatchModeComboBox = new Ext.form.ComboBox({
				store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [['TO', 'TO:'], ['CC','CC:'], ['BCC','BCC:']]
				}),
				valueField: 'id',
				displayField:'value',
				value: 'TO',
				typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				selectOnFocus:true,
				fieldLabel: 'Email TO: / CC: / BCC',
				width: 50,
				editable: false
			});
		}
		return this.dispatchModeComboBox;
	},
	
	getSubjectTextField: function() {
		if(!this.subjectTextField) {
			this.subjectTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.Subject.label'),
				name: 'subject',
				allowBlank: true
			});
		}
		return this.subjectTextField;
	},
	
	getAttachmentModeRadioGroup: function() {
		if(!this.attachementModeRadioGroup) {
			this.attachementModeRadioGroup = new Ext.form.RadioGroup({
				fieldLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.AttachmentMode.label'),
				items:
				[
					this.getAsAttachementRadio(),
					this.getEmbeddedInBodyRadio()
				]
			});
		}
		return this.attachementModeRadioGroup;
	},
	
	getAsAttachementRadio: function() {
		if(!this.asAttachmentRadio) {
			this.asAttachmentRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.AsAttachment.label'), 
				name: 'attachmentMode', 
				inputValue: 'AS_ATTACHMENT',
				checked: true
			});
		}
		return this.asAttachmentRadio;
	},
	
	getEmbeddedInBodyRadio: function() {
		if(!this.embeddedInBodyRadio) {
			this.embeddedInBodyRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.EmbeddedInBody.label'), 
				name: 'attachmentMode', 
				inputValue: 'EMBEDDED_IN_BODY'
			});
		}
		return this.embeddedInBodyRadio;
	},
	
	getFileNameTextField: function() {
		if(!this.fileNameTextField) {
			this.fileNameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Common.Dissemination.MailDiffusion.FileName.label'),
				name: 'fileName',
				allowBlank: true,
				width: 150
			});
		}
		return this.fileNameTextField;
	},
	
	//----------------------------------------------------------------- Utility methods.
	
	notifyMailSelected: function() {
		var mailAddressSelected = this.getAddressTextField().getValue();
		if(mailAddressSelected) {
			var mailSelected = null;
			for(var i = 0; i < this.getAddressTextField().getStore().getCount(); i++) {
				if(this.getAddressTextField().getStore().getAt(i).get('address') == mailAddressSelected) {
					mailSelected = this.getAddressTextField().getStore().getAt(i).data;
					break;
				}
			}
			this.initializeFields(mailSelected);
		}
	},
	
	initializeFields: function(mail) {
		Ext.iterate(mail, function(key, value) {
			if(this.mailFields[key]) {
				this.mailFields[key].setValue(value);
			}
		}, this);
	},

	getDisseminationValue: function() {
		var mail = {};
		// Force address field value with raw value, as value happens to be not initialized sometimes...
		this.getAddressTextField().setValue(this.getAddressTextField().getRawValue());
		Ext.iterate(this.mailFields, function(key, field) {
			if(!Ext.isObject(field.getValue())) {
				if (field.getValue() == 'null' || field.getValue() == null) {
					mail[key] = '';
				} else {
					mail[key] = field.getValue();
				}
			} else {
				mail[key] = field.getValue().inputValue;
			}
		}, this);
		
		return mail;
	},
	
	refresh: function(favoritesMails) {
		this.getAddressTextField().getStore().loadData(favoritesMails);
	}
	
});