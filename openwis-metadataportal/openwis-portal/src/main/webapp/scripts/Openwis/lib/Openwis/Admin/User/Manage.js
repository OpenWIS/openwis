Ext.ns('Openwis.Admin.User');

Openwis.Admin.User.Manage = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Security.User.Manage.Title'),
			layout: 'fit',
			width:600,
			height:500,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.User.Manage.superclass.initComponent.apply(this, arguments);

	    this.getInfosAndInitialize();

	},
	
	getInfosAndInitialize: function() {
		var params = {};
		params.user = {};
		params.user.username = this.editUserName;
		
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.user.get',
			params: params,
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
				},
				failure: function(config) {
					this.close();
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
		this.addEvents("userSaved");
		
		var tabs = new Ext.TabPanel({
            width:450,
            activeTab: 0,
            frame:true,
            defaults:{autoHeight: true},
            items:[
                this.getPersonalInformationFormPanel(),
                this.getPrivilegesFormPanel(),
                this.getFavoritesPanel()
            ]
        });
		
		
		//-- Create form panel.

		this.add(tabs);
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		// Init Panels
		this.getPrivilegesFormPanel().init(this.config.profiles, this.config.groups, this.config.backups, this.config.classOfServices);

		if(this.isEdition()) {
			this.getPersonalInformationFormPanel().setUserInformation(this.config.user);
            this.getPrivilegesFormPanel().setUserInformation(this.config.user);
            this.getFavoritesPanel().setFavorites(this.config.user);
		} else {
		    this.getPrivilegesFormPanel().setUserInformation(this.config.user);
		}
		
		this.show();
	},
	
	getPersonalInformationFormPanel: function() {
        if(!this.personalInformationFormPanel) {
            this.personalInformationFormPanel = new Openwis.Common.User.PersonalInformation({isEdition: this.isEdition()});
        }
        return this.personalInformationFormPanel;
    },
    
    getPrivilegesFormPanel: function() {
        if(!this.privilegesFormPanel) {
            this.privilegesFormPanel = new Openwis.Admin.User.Privileges();
        }
        return this.privilegesFormPanel;
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
				    var privilegesValid = this.getPrivilegesFormPanel().getForm().isValid();
					if( persoInfoValid && privilegesValid) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.user.save',
							params: this.getUser(),
							listeners: {
								success: function(config) {
									this.fireEvent("userSaved");
									this.close();
								},
								scope: this
							}
						});
						saveHandler.proceed();
					} else {
					    Ext.Msg.show({
    					    title: Openwis.i18n('Security.User.Manage.ErrorDlg.Title'),
	    				    msg: Openwis.i18n('Security.User.Manage.ErrorDlg.Msg'),
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
		
		//Set if the mode is in creation or in edition...
		user.creationMode = this.isCreation();
		
        user.user = this.getPersonalInformationFormPanel().getUser(user.user);
        user.user = this.getPrivilegesFormPanel().getUser(user.user);
        user.user = this.getFavoritesPanel().getUser(user.user);
		
		return user;
	},
	
	/**
	 * Returns true if it is a creation, false otherwise.
	 */
	isCreation: function() {
		return (this.operationMode == 'Create');
	},
	
	/**
	 * Returns true if it is an edition, false otherwise.
	 */
	isEdition: function() {
		return (this.operationMode == 'Edit');
	}
});