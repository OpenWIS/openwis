Ext.ns('Openwis.Admin');

Openwis.Admin.Browser = Ext.extend(Ext.ux.GroupTabPanel, {
	
	initComponent: function() {
	    var items = [];
	    if (this.getAlarmsMenu()) {
	        items.push(this.getAlarmsMenu());
		}
	    
	    if (this.getMetadataServiceMenu()) {
	        items.push(this.getMetadataServiceMenu());
		}
	    
		if (this.getDataServiceMenu()) {
		    items.push(this.getDataServiceMenu());
		}
		
		if (this.getSecurityServiceMenu()) {
		    items.push(this.getSecurityServiceMenu());
		}
		
		if (this.getBackupMenu()) {
		    items.push(this.getBackupMenu());
		}
		
		if (this.getSystemMenu()) {
		    items.push(this.getSystemMenu());
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
					//Set the Recent events tab by default.
					this.getAlarmsMenu().setActiveTab(this.getAlarmsRecentEventsMenu());
				},
				scope: this
			}	
		});
		Openwis.Admin.Browser.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		this.addEvents('panelInitialized');
	},
	
	/**
	 *	Test if the service is accessible.
	 */
	isServiceAccessible: function(service) {
	    var isAccessible = accessibleServices.indexOf(service);
	    return isAccessible != -1;
	},
	
	/**
	 *	Alarms MENU.
	 */
	getAlarmsMenu: function() {
		if(!this.alarmsMenu) {
		    var alarmsRecentEvents = this.isServiceAccessible("xml.management.alarms.events");
		    var alarmsGlobalReports = this.isServiceAccessible("xml.management.alarms.events");
		    if (alarmsRecentEvents || alarmsGlobalReports) {
		        this.alarmsMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.Alarms'),
    						tabTip : Openwis.i18n('Admin.Browser.Alarms')
    					}
    				]
    			});
    			
    			if (alarmsRecentEvents) {
    			    this.alarmsMenu.add(this.getAlarmsRecentEventsMenu());
    			}
    			
    			if (alarmsGlobalReports) {
    			    this.alarmsMenu.add(this.getAlarmsGlobalReportsMenu());
    			}

			this.alarmsMenu.add(this.getAlarmsUserAlarmMenu());
		    }
			
		}
		return this.alarmsMenu;
	},
	
	getAlarmsRecentEventsMenu: function() {
		if(!this.alarmsRecentEventsMenu) {
			this.alarmsRecentEventsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.Alarms.RecentEvents'),
				listeners : {
					activate: function(ct) {
						//console.log("Show Recent events");
						ct.add(new Openwis.Admin.Statistics.RecentEvents());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.alarmsRecentEventsMenu;
	},

	getAlarmsGlobalReportsMenu: function() {
		if(!this.alarmsGlobalReportsMenu) {
			this.alarmsGlobalReportsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.Alarms.GlobalReports'),
				listeners : {
					activate: function(ct) {
						//console.log("Show Global reports");
						// ct.add(new Ext.Panel({ html: 'Global reports'}));
						ct.add(new Openwis.Admin.Statistics.GlobalReports());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.alarmsGlobalReportsMenu;
	},
	
	getAlarmsUserAlarmMenu: function() {
		if (!this.alarmsUserAlarmsMenu) {
			this.alarmsUserAlarmsMenu = new Ext.Panel({
				title: "User Alarms",			// <<<<==== TODO: I18N
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Statistics.UserAlarms());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.alarmsUserAlarmsMenu;
	},

	/**
	 *	'Metadata service' MENU.
	 */
	getMetadataServiceMenu: function() {
		if(!this.metadataServiceMenu) {
		
		    var metadataServiceCreateMetadata = this.isServiceAccessible("xml.metadata.create.form");
			var metadataServiceInsertMetadata = this.isServiceAccessible("xml.metadata.insert.form");
			var metadataServiceConfigureHarvesting = this.isServiceAccessible("xml.harvest.all");
			var metadataServiceConfigureSynchronization = this.isServiceAccessible("xml.harvest.all");
			var metadataServiceMonitorCatalogContent = this.isServiceAccessible("xml.metadata.all");
			var metadataServiceCatalogStatistics = this.isServiceAccessible("xml.catalogstatistics.all");
			var metadataServiceTemplates = this.isServiceAccessible("xml.template.all");
			var metadataServiceIndex = this.isServiceAccessible("metadata.admin.index.rebuild");
			var metadataServiceThesauriManagement = this.isServiceAccessible("xml.thesaurus.list");
			var metadataServiceCategoryManagement = this.isServiceAccessible("xml.category.all");
		
		
    		if (metadataServiceCreateMetadata || metadataServiceInsertMetadata || metadataServiceConfigureHarvesting
    		    || metadataServiceConfigureSynchronization || metadataServiceMonitorCatalogContent || metadataServiceCatalogStatistics || metadataServiceTemplates
    		    ||metadataServiceIndex ||metadataServiceThesauriManagement || metadataServiceCategoryManagement) {
    		    this.metadataServiceMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.MetadataService'),
    						tabTip : Openwis.i18n('Admin.Browser.MetadataService')
    					}
    				]
    			});
    			
    			if (metadataServiceCreateMetadata) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceCreateMetadataMenu());
    			}
    			if (metadataServiceInsertMetadata) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceInsertMetadataMenu());
    			}
    			if (metadataServiceConfigureHarvesting) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceConfigureHarvestingMenu());
    			}
    			if (metadataServiceConfigureSynchronization) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceConfigureSynchronizationMenu());
    			}
    			if (metadataServiceMonitorCatalogContent) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceMonitorCatalogContentMenu());
    			}
    			if (metadataServiceCatalogStatistics) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceCatalogStatisticsMenu());
    			}
    			if (metadataServiceTemplates) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceTemplatesMenu());
    			}
    			if (metadataServiceIndex) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceIndexMenu());
    			}
    			if (metadataServiceThesauriManagement) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceThesauriManagementMenu());
    			}
    			if (metadataServiceCategoryManagement) {
    			    this.metadataServiceMenu.add(this.getMetadataServiceCategoryManagementMenu());
    			}
    		}
		}
		return this.metadataServiceMenu;
	},
	
	getMetadataServiceCreateMetadataMenu: function() {
		if(!this.metadataServiceCreateMetadataMenu) {
			this.metadataServiceCreateMetadataMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Create'),
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
				title: Openwis.i18n('Admin.Browser.MetadataService.Insert'),
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

	getMetadataServiceConfigureHarvestingMenu: function() {
		if(!this.metadataServiceConfigureHarvestingMenu) {
			this.metadataServiceConfigureHarvestingMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Harvesting'),
				listeners : {
					activate: function(ct) {
					    ct.add(new Openwis.Admin.Harvesting.All());
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceConfigureHarvestingMenu;
	},
	
	getMetadataServiceConfigureSynchronizationMenu: function() {
		if(!this.metadataServiceConfigureSynchronizationMenu) {
			this.metadataServiceConfigureSynchronizationMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Synchronization'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Synchro.All());
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceConfigureSynchronizationMenu;
	},
	
	getMetadataServiceMonitorCatalogContentMenu: function() {
		if(!this.metadataServiceMonitorCatalogContentMenu) {
			this.metadataServiceMonitorCatalogContentMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.CatalogContent'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Common.Metadata.MonitorCatalog({isAdmin:true}));
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceMonitorCatalogContentMenu;
	},
	
	getMetadataServiceCatalogStatisticsMenu: function() {
		if(!this.metadataServiceCatalogStatisticsMenu) {
			this.metadataServiceCatalogStatisticsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.CatalogStatistics'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.CatalogStatistics.All());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceCatalogStatisticsMenu;
	},
	
	getMetadataServiceTemplatesMenu: function() {
		if(!this.metadataServiceTemplatesMenu) {
			this.metadataServiceTemplatesMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Templates'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Template.All());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceTemplatesMenu;
	},
	
	getMetadataServiceIndexMenu: function() {
		if(!this.metadataServiceIndexMenu) {
			this.metadataServiceIndexMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Index'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Index.Manage());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceIndexMenu;
	},
	
	getMetadataServiceThesauriManagementMenu: function() {
		if(!this.metadataServiceThesauriManagementMenu) {
			this.metadataServiceThesauriManagementMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Thesauri'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Thesauri.Manage({
				            listeners: {
				                guiChanged: function() {
				                	this.ownerCt.ownerCt.fireEvent('guiChanged', false, true);
			    				},
				                scope: this
				            }
				        }));
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceThesauriManagementMenu;
	},
	
	getMetadataServiceCategoryManagementMenu: function() {
		if(!this.metadataServiceCategoryManagementMenu) {
			this.metadataServiceCategoryManagementMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.MetadataService.Category'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Category.All());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.metadataServiceCategoryManagementMenu;
	},
	
	/**
	 *	'Data service' MENU.
	 */
	getDataServiceMenu: function() {
		if(!this.dataServiceMenu) {
		
		    var dataServiceMonitorCurrentRequests = this.isServiceAccessible("xml.monitor.current.requests");
		    
			var dataServiceRequestStatistics = this.isServiceAccessible("xml.requestsStatistics.allDataExtracted");
			var dataServiceCacheConfiguration = this.isServiceAccessible("xml.management.cache.configure.ingest");
			var dataServiceBrowseContent = this.isServiceAccessible("xml.management.cache.browse");
			var dataServiceCacheStatistics = this.isServiceAccessible("xml.management.cache.statistics.ingest");
			var dataServiceBlacklisting = this.isServiceAccessible("xml.blacklist.all");
			
			if (dataServiceMonitorCurrentRequests || dataServiceRequestStatistics || dataServiceCacheConfiguration || dataServiceBrowseContent || dataServiceCacheStatistics || dataServiceBlacklisting) {
			    this.dataServiceMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.DataService'),
    						tabTip : Openwis.i18n('Admin.Browser.DataService')
    					}
    				]
    			});
    			
    			if (dataServiceMonitorCurrentRequests) {
        			this.dataServiceMenu.add(this.getDataServiceMonitorCurrentRequestsMenu());
        		}
        		if (dataServiceRequestStatistics) {
    			    this.dataServiceMenu.add(this.getDataServiceRequestStatisticsMenu());
    			}
        		if (dataServiceCacheConfiguration) {
    			    this.dataServiceMenu.add(this.getDataServiceCacheConfigurationMenu());
    			}
        		if (dataServiceBrowseContent) {
    			    this.dataServiceMenu.add(this.getDataServiceBrowseContentMenu());
    			}
        		if (dataServiceCacheStatistics) {
    			    this.dataServiceMenu.add(this.getDataServiceCacheStatisticsMenu());
    			}
        		if (dataServiceBlacklisting) {
        			this.dataServiceMenu.add(this.getDataServiceBlacklistingMenu());
        		}
			}
		}
		return this.dataServiceMenu;
	},
	
	getDataServiceMonitorCurrentRequestsMenu: function() {
		if(!this.dataServiceMonitorCurrentRequestsMenu) {
			this.dataServiceMonitorCurrentRequestsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.DataService.MonitorCurrentRequests'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.DataService.MonitorCurrentRequests());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.dataServiceMonitorCurrentRequestsMenu;
	},
	
	getDataServiceRequestStatisticsMenu: function() {
		if(!this.dataServiceRequestStatisticsMenu) {
			this.dataServiceRequestStatisticsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.DataService.RequestStatistics'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.DataService.RequestsStatistics());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.dataServiceRequestStatisticsMenu;
	},
	
	getDataServiceCacheConfigurationMenu: function() {
		if(!this.dataServiceCacheConfigurationMenu) {
			this.dataServiceCacheConfigurationMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.DataService.CacheConfiguration'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.DataService.CacheConfiguration());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.dataServiceCacheConfigurationMenu;
	},
	
	getDataServiceBrowseContentMenu: function() {
		if(!this.dataServiceBrowseContentMenu) {
			this.dataServiceBrowseContentMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.DataService.BrowseContent'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.DataService.BrowseContent());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.dataServiceBrowseContentMenu;
	},

	getDataServiceCacheStatisticsMenu: function() {
		if(!this.dataServiceCacheStatisticsMenu) {
			this.dataServiceCacheStatisticsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.DataService.CacheStatistics'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Statistics.CacheStatistics());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.dataServiceCacheStatisticsMenu;
	},

	getDataServiceBlacklistingMenu: function() {
		if(!this.dataServiceBlacklistingMenu) {
			this.dataServiceBlacklistingMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.DataService.Blacklisting'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.DataService.Blacklist());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.dataServiceBlacklistingMenu;
	},

	/**
	 *	'Security service' MENU.
	 */
	getSecurityServiceMenu: function() {
		if(!this.securityServiceMenu) {
		    var securityServiceSSOManagement = this.isServiceAccessible("xml.sso.management");
			var securityServiceUserManagement = this.isServiceAccessible("xml.user.all");
			var securityServiceGroupManagement = this.isServiceAccessible("xml.group.remove");
			var securityServiceDataPolicyManagement = this.isServiceAccessible("xml.datapolicy.all");
			
			if (securityServiceSSOManagement || securityServiceUserManagement || securityServiceGroupManagement || securityServiceDataPolicyManagement) {
			    this.securityServiceMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.SecurityService'),
    						tabTip : Openwis.i18n('Admin.Browser.SecurityService')
    					}
    				]
    			});
    			
    			if (securityServiceSSOManagement) {
    			    this.securityServiceMenu.add(this.getSecurityServiceSSOManagementMenu());
    			}
    			
    			if (securityServiceUserManagement) {
    			    this.securityServiceMenu.add(this.getSecurityServiceUserManagementMenu());
    			}
    			
    			if (securityServiceGroupManagement) {
    			    this.securityServiceMenu.add(this.getSecurityServiceGroupManagementMenu());
    			}
    			
    			if (securityServiceDataPolicyManagement) {
    			    this.securityServiceMenu.add(this.getSecurityServiceDataPolicyManagementMenu());
    			}
			}
		}
		return this.securityServiceMenu;
	},
	
	getSecurityServiceSSOManagementMenu: function() {
		if(!this.securityServiceSSOManagementMenu) {
			this.securityServiceSSOManagementMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.SecurityService.SSOManagement'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.SSOManagement.SSOManagement());
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.securityServiceSSOManagementMenu;
	},
	
	getSecurityServiceUserManagementMenu: function() {
		if(!this.securityServiceUserManagementMenu) {
			this.securityServiceUserManagementMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.SecurityService.UserManagement'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.User.All());
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.securityServiceUserManagementMenu;
	},
	
	getSecurityServiceGroupManagementMenu: function() {
		if(!this.securityServiceGroupManagementMenu) {
			this.securityServiceGroupManagementMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.SecurityService.GroupManagement'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Group.All());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.securityServiceGroupManagementMenu;
	},
	
	getSecurityServiceDataPolicyManagementMenu: function() {
		if(!this.securityServiceDataPolicyManagementMenu) {
			this.securityServiceDataPolicyManagementMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.SecurityService.DPManagement'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.DataPolicy.All());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.securityServiceDataPolicyManagementMenu;
	},
	
	/**
	 *	Backup MENU.
	 */
	getBackupMenu: function() {
		if(!this.backupMenu) {
		    var backupAvailabilityLocal = this.isServiceAccessible("xml.avalaibility.get");
			var backupAvailabilityRemote = this.isServiceAccessible("xml.avalaibility.remote.get");
			var availabilityStatistics = this.isServiceAccessible("xml.availability.getstatistics");

            if (backupAvailabilityLocal || backupAvailabilityRemote || availabilityStatistics) {
			    this.backupMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.Backup'),
    						tabTip : Openwis.i18n('Admin.Browser.Backup')
    					}
    				]
    			});
    			
    			if (backupAvailabilityLocal) {
        			this.backupMenu.add(this.getBackupAvailabilityLocalMenu());
    			}
    			if (backupAvailabilityRemote) {
    			    this.backupMenu.add(this.getBackupAvailabilityRemoteMenu());
    			}
    			if (availabilityStatistics) {
    			    this.backupMenu.add(this.getAvailabilityStatisticsMenu());
    			}
			}

			
		}
		return this.backupMenu;
	},
	
	getBackupAvailabilityLocalMenu: function() {
		if(!this.backupAvailabilityLocalMenu) {
			this.backupAvailabilityLocalMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.Backup.Local'),
				listeners : {
					activate: function(ct) {
						var localAvailabilityPanel = new Openwis.Admin.Availability.LocalAvailability();
						ct.add(localAvailabilityPanel);
						localAvailabilityPanel.addListener('panelInitialized',function() {
							// fire event on browser panel.
						    this.ownerCt.ownerCt.fireEvent('panelInitialized');
						}, this);
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.backupAvailabilityLocalMenu;
	},
	
	getBackupAvailabilityRemoteMenu: function() {
		if(!this.backupAvailabilityRemoteMenu) {
			this.backupAvailabilityRemoteMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.Backup.Remote'),
				listeners : {
					activate: function(ct) {
						var remoteAvailabilityPanel = new Openwis.Admin.Availability.RemoteAvailability();
						ct.add(remoteAvailabilityPanel);

						remoteAvailabilityPanel.addListener('panelInitialized',function() {
							// fire event on browser panel.
						    this.ownerCt.ownerCt.fireEvent('panelInitialized');
						}, this);
						
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.backupAvailabilityRemoteMenu;
	},
	
	getAvailabilityStatisticsMenu: function() {
		if(!this.availabilityStatisticsMenu) {
			this.availabilityStatisticsMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.Backup.Statistics'),
				listeners : {
					activate: function(ct) {
						ct.add(new Openwis.Admin.Availability.Statistics());
						ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.availabilityStatisticsMenu;
	},
	
	/**
	 *	System MENU.
	 */
	getSystemMenu: function() {
		if(!this.systemMenu) {
		    var systemConfiguration = this.isServiceAccessible("xml.system.configuration.form");
		    if (systemConfiguration) {
    		     this.systemMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.System'),
    						tabTip : Openwis.i18n('Admin.Browser.System')
    					}
    				]
    			});
    			
    			if (systemConfiguration) {
    			    this.systemMenu.add(this.getSystemConfigurationMenu());
    			}
		    }
		}
		return this.systemMenu;
	},
	
	getSystemConfigurationMenu: function() {
		if(!this.systemConfigurationMenu) {
			this.systemConfigurationMenu = new Ext.Panel({
				title: Openwis.i18n('Admin.Browser.System.Configuration'),
				listeners : {
					activate: function(ct) {
					    var systemPanel = new Openwis.Admin.System.SystemConfiguration({isAdmin:true});
						ct.add(systemPanel);
						
						systemPanel.addListener('panelInitialized',function() {
						    this.fireEvent('panelInitialized');
						}, this);
						
                        ct.doLayout();
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					},
					scope: this
				}
			});
		}
		return this.systemConfigurationMenu;
	},
	
	getSystemLocalizationMenu: function() {
		if(!this.systemLocalizationMenu) {
			this.systemLocalizationMenu = new Ext.Panel({
				title: 'Localization',
				listeners : {
					activate: function(ct) {
						//console.log("Show System i18n");
					},
					deactivate: function(ct) {
						ct.remove(ct.items.first(), true);
					}
				}
			});
		}
		return this.systemLocalizationMenu;
	},
	
	/**
	 *	Personal information MENU.
	 */
	getPersonalInformationMenu: function() {
		if(!this.personalInformationMenu) {
		    var userInfoAccessible = this.isServiceAccessible("xml.user.save");
			var changePswd = this.isServiceAccessible("xml.user.changePassword");
			
			if (userInfoAccessible || changePswd) {
    			this.personalInformationMenu = new Ext.ux.GroupTab({
    				expanded: true,
    				items: 
    				[
    					{
    						title : Openwis.i18n('Admin.Browser.PersonalInformation'),
    						tabTip : Openwis.i18n('Admin.Browser.PersonalInformation')
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
				title: Openwis.i18n('Admin.Browser.PersonalInformation.UserInfo'),
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
				title: Openwis.i18n('Admin.Browser.PersonalInformation.ChangeMyPassword'),
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