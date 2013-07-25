Ext.ns('Openwis.RequestSubscription.DisseminationParameters.Components');

Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: this.getTitle(),
			layout: 'form',
			items:[]
		});
		Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion.superclass.initComponent.apply(this, arguments);
		
		this.addEvents("processRefresh");
		
		this.initialize();
	},
	
	//----------------------------------------------------------------- Initialization.
	
	initialize: function() {
		this.add(this.getZipMode());
		
		this.getFTPFieldSet().add(this.getFTPDiffusionPanel());
		this.add(this.getFTPFieldSet());
		
		this.getMailFieldSet().add(this.getMailDiffusionPanel());
		this.add(this.getMailFieldSet());
	},
	
	//----------------------------------------------------------------- Components.
	
	getFTPFieldSet: function() {
		if(!this.ftpFieldSet) {
			this.ftpFieldSet = new Ext.form.FieldSet({
				title: Openwis.i18n('RequestSubscription.Dissemination.Diffusion.FTP.Title'),
				collapsed: true,
				collapsible: true,
				listeners: {
					"beforeexpand": function (panel, animate) {
						this.getMailFieldSet().collapse(false);
						this.getFTPFieldSet().doLayout();
					},
					scope: this
				}
			});
			if (this.operationMode == 'Create') {
				this.ftpFieldSet.addButton(new Ext.Button(this.getBookmarkAction()));
			}
		}
		return this.ftpFieldSet;
	},
	
	getFTPDiffusionPanel: function() {
		if(!this.ftpDiffusionPanel) {
			this.ftpDiffusionPanel = new Openwis.Common.Dissemination.FTPDiffusion();
		}
		return this.ftpDiffusionPanel;
	},
	
	getMailFieldSet: function() {
		if(!this.mailFieldSet) {
			this.mailFieldSet = new Ext.form.FieldSet({
				title: Openwis.i18n('RequestSubscription.Dissemination.Diffusion.Mail.Title'),
				collapsed: true,
				collapsible: true,
				listeners: {
					"beforeexpand": function (panel, animate) {
						this.getFTPFieldSet().collapse(false);
						this.getMailFieldSet().doLayout();
					},
					scope: this
				}
			});
			if (this.operationMode == 'Create') {
				this.mailFieldSet.addButton(new Ext.Button(this.getBookmarkAction()));
			}
		}
		return this.mailFieldSet;
	},
	
	getMailDiffusionPanel: function() {
		if(!this.mailDiffusionPanel) {
			this.mailDiffusionPanel = new Openwis.Common.Dissemination.MailDiffusion();
		}
		return this.mailDiffusionPanel;
	},
	
	getBookmarkAction: function() {
		if(!this.bookmarkAction) {
			this.bookmarkAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
					this.bookmark();
				}
			});
		}
		return this.bookmarkAction;
	},
	
	getTitle: function() {
	     if(this.disseminationTool == 'RMDCN') {
	         return Openwis.i18n('RequestSubscription.Dissemination.Diffusion.RMDCN.Title');
	     } else if(this.disseminationTool == 'PUBLIC') {
	         return Openwis.i18n('RequestSubscription.Dissemination.Diffusion.PUBLIC.Title');
	     }
	},
	
	getZipMode:function() {
		if (!this.zipMode) {
			this.zipMode =  new Ext.form.ComboBox({
				store: new Ext.data.ArrayStore({
			        id: 0,
			        fields: [
			            'zipMode'
			        ],
			        data: [['NONE'], ['ZIPPED'], ['WMO_FTP']]
			    }),
				valueField: 'zipMode',
				displayField:'zipMode',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				fieldLabel: Openwis.i18n('RequestSubscription.Dissemination.ZippedMode.Title'),
				editable: false,
				value: 'NONE',
				width: 210
			});
		}
		return this.zipMode;
	},
	
	//----------------------------------------------------------------- Utility methods.
	
	refresh: function(favoritesDiseminationParams) {
		if(favoritesDiseminationParams.ftp) {
			this.getFTPDiffusionPanel().refresh(favoritesDiseminationParams.ftp);
		}
		if(favoritesDiseminationParams.mail) {
			this.getMailDiffusionPanel().refresh(favoritesDiseminationParams.mail);
		}
		
		this.setVisible(favoritesDiseminationParams.authorizedFtp || favoritesDiseminationParams.authorizedMail);
		this.getFTPFieldSet().setVisible(favoritesDiseminationParams.authorizedFtp);
		this.getMailFieldSet().setVisible(favoritesDiseminationParams.authorizedMail);
	},
	
	getForm: function() {
		if(!this.getFTPFieldSet().collapsed) {
			return this.getFTPDiffusionPanel().getForm();
		} else if(!this.getMailFieldSet().collapsed) {
			return this.getMailDiffusionPanel().getForm();
		}
	},
	
	getDisseminationValue: function() {
		var dissemValue = {};
		if(!this.getFTPFieldSet().collapsed) {
			dissemValue.ftp = this.getFTPDiffusionPanel().getDisseminationValue();
		} else if(!this.getMailFieldSet().collapsed) {
			dissemValue.mail = this.getMailDiffusionPanel().getDisseminationValue();
		}
		dissemValue.zipMode = this.getZipMode().getValue();
		return dissemValue;
	},
	
	initializeFields: function(configObject) {
	    this.getZipMode().setValue(configObject.zipMode);
	    if(configObject.diffusion.host) {
			this.getFTPDiffusionPanel().initializeFields(configObject.diffusion);
			//this.getFTPFieldSet().expand(false);
		} else if(configObject.diffusion.address) {
			this.getMailDiffusionPanel().initializeFields(configObject.diffusion);
			//this.getMailFieldSet().expand(false);
		}
	},
	
	bookmark: function() {
		if(this.getForm().isValid()) {
			var dissemValue = {};
			if(!this.getFTPFieldSet().collapsed) {
				dissemValue.ftp = this.getFTPDiffusionPanel().getDisseminationValue();
				dissemValue.ftp.disseminationTool = this.disseminationTool;
			} else if(!this.getMailFieldSet().collapsed) {
				dissemValue.mail = this.getMailDiffusionPanel().getDisseminationValue();
				dissemValue.mail.disseminationTool = this.disseminationTool;
			}
			
			var saveHandler = new Openwis.Handler.Save({
				url: configOptions.locService+ '/xml.save.favorite.dissemination.parameter',
				params: dissemValue,
				listeners: {
					success: function() {
						this.fireEvent("processRefresh");
					},
					scope: this
				}
			});
			saveHandler.proceed();
		} else {
			Openwis.Utils.MessageBox.displayErrorMsg(validation.errorMsg);
		}
	}
});