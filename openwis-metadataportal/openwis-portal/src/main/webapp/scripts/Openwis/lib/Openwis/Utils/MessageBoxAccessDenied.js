Ext.ns('Openwis.Utils');

Openwis.Utils.MessageBoxAccessDenied = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			closeAction:'close',
			autoScroll: false,
			resizable: false,
			layout: 'table',
			title: Openwis.i18n('MessageBoxAccessDenied.title'),
			height: 310,
			width: 409,
			params: this.urn,
			layoutConfig: {
		        columns:1
		    },
		    items: [
			    this.getAccessDeniedMsgText(),
				this.getExtensionPrivilegeFieldSet(),
		    	this.getOKButton()
		    ]
		});
		Openwis.Utils.MessageMustLogin.superclass.initComponent.apply(this, arguments);
		this.initialize();
		this.show();
		
	},
	
	initialize: function() {
		if (this.urn) {
			this.getProductTitleAreaText().setValue(this.urn);
		}
	},
	
	//----------------------------------------------------------------- Panel references.
	
	getAccessDeniedMsgText: function() {
		return new Ext.Container({
				html: Openwis.i18n('MessageBoxAccessDenied.msg'),
				style: {
					marginTop: '20px',
					marginLeft: '20px',
					marginRight: '20px',
					marginBottom: '20px',
					textAlign: 'center'
				}
			});
	},
	
	 getExtensionPrivilegeFieldSet: function() {
        if(!this.extensionprivilegesFieldSet) {
            this.extensionprivilegesFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('MessageBoxAccessDenied.extension.title'),
				width:300,
				layout: 'table',
				layoutConfig: {
			        columns:1
			    },
			    style: {
					marginLeft: '50px'
				},
				collapsed: false,
				collapsible: true,
				buttons: [
					new Ext.Button(this.getSendMailToAdminAction())
				]
            });
            this.extensionprivilegesFieldSet.add(new Ext.Container({
               html: Openwis.i18n('MessageBoxAccessDenied.extension.note'),
               border: false,
               cls: 'infoMsg',
               style: {
                   margin: '0px 0px 5px 0px'
               }
            }));
            this.extensionprivilegesFieldSet.add(this.getProductTitleAreaText());
        }
        return this.extensionprivilegesFieldSet;
    },
    
	
	getOKButton: function() {
		this.okButton = new Ext.Panel({
			buttonAlign:"center",
			border: false,
			buttons:[new Ext.Button(this.getOKAction())]
		});		
		return this.okButton;
	},

	getProductTitleAreaText: function() {
		if (!this.productTitleAreaText) {
			this.productTitleAreaText = new Ext.form.TextArea({
				border: true,
				autoscroll: false,
				width : 250
			});
		}
		return this.productTitleAreaText;
	},
	
	//----------------------------------------------------------------- Actions.
	
	getOKAction: function() {
		if(!this.okAction) {
			this.okAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.OK'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.okAction;
	},
	
	getSendMailToAdminAction: function() {
		if(!this.sendMailToAdminAction) {
			this.sendMailToAdminAction = new Ext.Action({
				text: Openwis.i18n('MessageBoxAccessDenied.extension.button'),
				scope: this,
				handler: function() {
					var params = {};
					params.content = this.getProductTitleAreaText().getValue()
    				// Fields correctly filled.
					var saveHandler = new Openwis.Handler.Save({
						url: configOptions.locService+ '/user.extends.privileges.submit',
						params: params,
						listeners: {
							success: function(config) {
								this.close();
								new Openwis.Utils.MessageBox.displaySuccessMsg(Openwis.i18n('MessageBoxAccessDenied.extension.mail.success'));
							},
							scope: this
						}
					});
					saveHandler.proceed();
				}
			});
		}
		return this.sendMailToAdminAction;
	}

});