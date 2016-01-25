Ext.ns('Openwis.Common.User.UserInformation');

Openwis.Common.User.UserInformation = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Common.User.UserInformation.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	getInfosAndInitialize: function() {
		var params = {};
		
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.user.getSelf',
			params: params,
			listeners: {
				success: function(config) {
					this.config = config;
					
					this.getPersonalInformationFormPanel().setUserInformation(this.config.user);
                    this.getFavoritesPanel().setFavorites(this.config.user);
				},
				failure: function(config) {
					Ext.Msg.show({
    					    title: Openwis.i18n('Security.User.UserInfo.ErrorDlg.Title'),
	    				    msg: Openwis.i18n('Security.User.UserInfo.ErrorDlg.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		
		//Create Header.
		this.add(this.getHeader());
		
		var tabs = new Ext.TabPanel({
            width:600,
            height:400,
            activeTab: 0,
            frame:true,
            defaults:{autoHeight: true},
            items:[
                this.getPersonalInformationFormPanel(),
                this.getFavoritesPanel()
            ],
            buttons: [
                //-- Add buttons.
		        this.add(new Ext.Button(this.getSaveAction())),
		        this.add(new Ext.Button(this.getCancelAction()))
            ]
        });
		
		//-- Create form panel.
		this.add(tabs);
		
		// Init Panels
		
		this.getInfosAndInitialize();
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Security.User.UserInfo.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	getPersonalInformationFormPanel: function() {
        if(!this.personalInformationFormPanel) {
            this.personalInformationFormPanel = new Openwis.Common.User.PersonalInformation({
            	hidePassword: this.hidePassword,
            	isEdition: 'true'
    		});
        }
        return this.personalInformationFormPanel;
    },
    
    getFavoritesPanel: function() {
        if(!this.favoritesPanel) {
            this.favoritesPanel = new Openwis.Common.Dissemination.Favorites();
        }
        return this.favoritesPanel;
    },

	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
				    var persoInfoValid = this.getPersonalInformationFormPanel().getForm().isValid();
					if( persoInfoValid) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.user.saveSelf',
							params: this.getUser(),
							listeners: {
								success: function(config) {
									this.fireEvent("userSaved");
								},
								scope: this
							}
						});
						saveHandler.proceed();
					} else {
					    Ext.Msg.show({
    					    title: Openwis.i18n('Security.User.UserInfo.Validation.Title'),
	    				    msg: Openwis.i18n('Security.User.UserInfo.Validation.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
					}
				}
			});
		}
		return this.saveAction;
	},

	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	//----- Utility methods.
	
	getUser: function() {
		var user = {};
		
        user.user = this.getPersonalInformationFormPanel().getUser(user.user);
        user.user = this.getFavoritesPanel().getUser(user.user);
		
		return user;
	}
});