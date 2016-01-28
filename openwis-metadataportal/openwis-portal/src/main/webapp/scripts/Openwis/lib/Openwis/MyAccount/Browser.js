Ext.ns('Openwis.MyAccount');

Openwis.MyAccount.Browser = Ext.extend(Ext.ux.GroupTabPanel, {
	
	initComponent: function() {
	    var items = [];
	    if (this.getMetadataServiceMenu()) {
	        items.push(this.getMetadataServiceMenu());
		}
	    
	    if (this.getTrackMyRequestsMenu()) {
	        items.push(this.getTrackMyRequestsMenu());
		}
	    
		if (this.getPersonalInformationMenu()) {
		    items.push(this.getPersonalInformationMenu());
		}
		Ext.apply(this, {
			tabWidth : 200,
			activeGroup : 0,
			items : items,
			listeners: {
				afterrender: function(ct) {
					//-- Set the Recent events tab by default.
					//ct.getAlarmsMenu().setActiveTab(ct.getAlarmsRecentEventsMenu());
				}
			}	
		});
		Openwis.MyAccount.Browser.superclass.initComponent.apply(this, arguments);
	},
	
	/**
	 *	Test if the service is accessible.
	 */
	isServiceAccessible: function(service) {
	    var isAccessible = accessibleServices.indexOf(service);
	    return isAccessible != -1;
	},
	
	/**
	 *	'Metadata service' MENU.
	 */
	getMetadataServiceMenu: function() {
		if(!this.metadataServiceMenu) {
		
		    var metadataServiceCreateMetadata = this.isServiceAccessible("xml.metadata.create.form");
			var metadataServiceInsertMetadata = this.isServiceAccessible("xml.metadata.insert.form");
		    var metadataServiceBrowseMyMetadata = this.isServiceAccessible("xml.metadata.all");
		    
		    if (metadataServiceCreateMetadata || metadataServiceInsertMetadata || metadataServiceBrowseMyMetadata) {
    		    this.metadataServiceMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('MyAccount.Browser.MetadataService'),
    						tabTip : Openwis.i18n('MyAccount.Browser.MetadataService')
    					}
    				]
    			});
    			
    			if (metadataServiceCreateMetadata) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceCreateMetadataMenu());
    			}
    			if (metadataServiceInsertMetadata) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceInsertMetadataMenu());
    			}
    			if (metadataServiceBrowseMyMetadata) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceBrowseMyMetadata());
    			}
		    }
		}
		return this.metadataServiceMenu;
	},
	
	getMetadataServiceCreateMetadataMenu: function() {
		if(!this.metadataServiceCreateMetadataMenu) {
			this.metadataServiceCreateMetadataMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.MetadataService.Create'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Common.Metadata.Create());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceCreateMetadataMenu;
	},

	getMetadataServiceInsertMetadataMenu: function() {
		if(!this.metadataServiceInsertMetadataMenu) {
			this.metadataServiceInsertMetadataMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.MetadataService.Insert'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Common.Metadata.Insert());
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceInsertMetadataMenu;
	},

	getMetadataServiceBrowseMyMetadata: function() {
        if(!this.metadataServiceBrowseMyMetadataMenu) {
            this.metadataServiceBrowseMyMetadataMenu = new Ext.Panel({
                title: Openwis.i18n('MyAccount.Browser.MetadataService.Browse'),
                listeners : {
                    activate: function(ct) {
                        ct.add(new Openwis.Common.Metadata.MonitorCatalog({isAdmin:false}));
                        ct.doLayout();
                    },
                    deactivate: function(ct) {
                        ct.remove(ct.items.first(), true);
                    }
                }
            });
        }
        return this.metadataServiceBrowseMyMetadataMenu;
    },
    
	/**
	 *	'Track my requests' MENU.
	 */
	getTrackMyRequestsMenu: function() {
		if(!this.trackMyRequestsMenu) {
		    var trackMyRequestsAdhocs = this.isServiceAccessible("xml.follow.my.adhocs") && this.isServiceAccessible("xml.follow.my.remote.adhocs");
		    var trackMyRequestsSubscriptions = this.isServiceAccessible("xml.follow.my.subscriptions") && this.isServiceAccessible("xml.follow.my.remote.subscriptions");
		    var trackMyRequestsMssFss = this.isServiceAccessible("allowedMSSFSS") && this.isServiceAccessible("xml.follow.my.mssfss.subscriptions") && this.isServiceAccessible("xml.follow.my.remote.mssfss.subscriptions");
		    if (trackMyRequestsAdhocs || trackMyRequestsSubscriptions || trackMyRequestsMssFss) {
		        this.trackMyRequestsMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('MyAccount.Browser.TrackMyRequests'),
    						tabTip : Openwis.i18n('MyAccount.Browser.TrackMyRequests')
    					}
    				]
    			});
    			
    			if (trackMyRequestsAdhocs) {
    			    this.trackMyRequestsMenu.add(this.getTrackMyRequestsAdhocsMenu());
    			}
    			if (trackMyRequestsSubscriptions) {
    			    this.trackMyRequestsMenu.add(this.getTrackMyRequestsSubscriptionsMenu());
    			}
    			if (trackMyRequestsMssFss) {
    			    this.trackMyRequestsMenu.add(this.getTrackMyRequestsMssFssMenu());
    			}
		    }
		}
		return this.trackMyRequestsMenu;
	},
	
	
	getTrackMyRequestsAdhocsMenu: function() {
		if(!this.trackMyRequestsAdhocsMenu) {
			this.trackMyRequestsAdhocsMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.TrackMyRequests.Request'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.MyAccount.TrackMyRequests.TrackMyAdhocs());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.trackMyRequestsAdhocsMenu;
	},
	
	getTrackMyRequestsSubscriptionsMenu: function() {
		if(!this.trackMyRequestsSubscriptionsMenu) {
			this.trackMyRequestsSubscriptionsMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.TrackMyRequests.Subscription'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.MyAccount.TrackMyRequests.TrackMySubscriptions());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.trackMyRequestsSubscriptionsMenu;
	},
	
	getTrackMyRequestsMssFssMenu: function() {
		if(!this.trackMyRequestsMssFssMenu) {
			this.trackMyRequestsMssFssMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.TrackMyRequests.MSSFSS'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.MyAccount.TrackMyRequests.TrackMyMSSFSSSubscriptions());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.trackMyRequestsMssFssMenu;
	},
	
	/**
	 *	Personal information MENU.
	 */
	getPersonalInformationMenu: function() {
		if(!this.personalInformationMenu) {
		    var userInfoAccessible = this.isServiceAccessible("xml.user.saveSelf");
			var changePswd = this.isServiceAccessible("xml.user.changePassword");
			
			if (userInfoAccessible || changePswd) {
    			this.personalInformationMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : 'Personal information',
    						tabTip : 'Personal information'
    					}
    				]
    			});
    			
    			if (userInfoAccessible) {
    			    this.personalInformationMenu.add(this.getPersonalInformationUserInformationMenu());
    			}
    			
    			if (changePswd) {
    			    this.personalInformationMenu.add(this.getPersonalInformationChangeMyPasswordMenu());
    			}
			}
		}
		return this.personalInformationMenu;
	},
	
	getPersonalInformationUserInformationMenu: function() {
		if(!this.personalInformationUserInformationMenu) {
			this.personalInformationUserInformationMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.PersonalInformation.UserInfo'),
				autoScroll: true,
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Common.User.UserInformation({
	    			    	hidePassword: true
	    			    }));
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.personalInformationUserInformationMenu;
	},
	
	getPersonalInformationChangeMyPasswordMenu: function() {
		if(!this.personalInformationChangeMyPasswordMenu) {
			this.personalInformationChangeMyPasswordMenu = new Ext.Panel({
				title: Openwis.i18n('MyAccount.Browser.PersonalInformation.ChangeMyPassword'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Common.User.ChangePassword());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.personalInformationChangeMyPasswordMenu;
	}
});