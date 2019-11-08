Ext.ns('Openwis.Admin.DataService');

Openwis.Admin.DataService.CacheConfiguration = Ext.extend(Ext.Container, {

	ingestionService: '/xml.management.cache.configure.ingest',
	feedingService: '/xml.management.cache.configure.feed',
	replicationService: '/xml.management.cache.configure.replic',
	disseminationService: '/xml.management.cache.configure.diss',
	
	ingestionServiceCheckBoxID: 'ingestionServiceCheckBox',
	feedingServiceCheckBoxID: 'feedingServiceCheckBox',
	replicationServiceCheckBoxID: 'replicationServiceCheckBox',
	disseminationServiceCheckBoxID: 'disseminationServiceCheckBox',

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.DataService.CacheConfiguration.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		this.add(this.getConfigurationMssFssPanel());
		this.add(this.getConfigurationReplicationPanel());
		this.add(this.getConfigurationDisseminationPanel());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('CacheConfiguration.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}7
		return this.header;
	},
	
	getConfigurationMssFssPanel: function() {
		if(!this.configurationMssFssPanel) {
			this.configurationMssFssPanel = new Ext.Panel({
				id: 'configurationMssFssPanel',
				title: Openwis.i18n('CacheConfiguration.MSSFSS.Title'),
				border: true
			});
			this.configurationMssFssPanel.add(this.getIngestionFilterPanel());
			this.configurationMssFssPanel.add(this.getFeedingFilterPanel());
		}
		return this.configurationMssFssPanel;		
	},

	getConfigurationReplicationPanel: function() {
		if(!this.configurationReplicationPanel) {
			this.configurationReplicationPanel = new Ext.Panel({
				id: 'configurationReplicationPanel',
				title: Openwis.i18n('CacheConfiguration.Replication.Title'),
				border: true,
				style: {
					marginTop: '20px'
				}
			});
			this.configurationReplicationPanel.add(this.getReplicationFilterPanel());
	    }
	    return this.configurationReplicationPanel;				
	},

	getConfigurationDisseminationPanel: function() {
		if(!this.configurationDisseminationPanel) {
			this.configurationDisseminationPanel = new Ext.Panel({
				id: 'configurationDisseminationPanel',
				title: Openwis.i18n('CacheConfiguration.Dissemination.Title'),
				border: true,
				style: {
					marginTop: '20px'
				}
			});
			this.configurationDisseminationPanel.add(this.getDisseminationPanel());
	    }
	    return this.configurationDisseminationPanel;						
	},
	
	getIngestionFilterPanel: function() {
		if (!this.ingestionFilterPanel) {
			this.ingestionFilterPanel = new Ext.form.FormPanel({
				id: 'ingestionFilterPanel',
				border: false,
				style: {
					marginLeft: '10px',
					marginRight: '10px'
				}	        	
			});			
			this.ingestionFilterPanel.add(new Ext.form.Checkbox({
				id: this.ingestionServiceCheckBoxID,
				fieldLabel: Openwis.i18n('CacheConfiguration.MSSFSS.EnableIngestion'),				
				handler: this.setServiceStatus,
				service: this.ingestionService,
				active: true
			}));
			this.ingestionFilterPanel.add(new Ext.Container({
				html: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Title'),
				cls: 'administrationTitle2'
			}));
			this.ingestionFilterPanel.add(this.getIngestionFilterGrid());
			this.ingestionFilterPanel.addButton(new Ext.Button(this.getIngestionFilterNewAction()));
			this.ingestionFilterPanel.addButton(new Ext.Button(this.getIngestionFilterRemoveAction()));
		}
		return this.ingestionFilterPanel;
	},
	
	getFeedingFilterPanel: function() {
		if (!this.feedingFilterPanel) {
			this.feedingFilterPanel = new Ext.form.FormPanel({
				id: 'feedingFilterPanel',
				border: false,
				style: {
					marginTop: '10px',
					marginLeft: '10px',
					marginRight: '10px',
					marginBottom: '10px'
				}	        	
			});						
			this.feedingFilterPanel.add(new Ext.form.Checkbox({
				id: this.feedingServiceCheckBoxID,
				fieldLabel: Openwis.i18n('CacheConfiguration.MSSFSS.EnableFeeding'),
				handler: this.setServiceStatus,
				service:  this.feedingService,
				active: true
			}));
			this.feedingFilterPanel.add(new Ext.Container({
				html: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Title'),
				cls: 'administrationTitle2'
			}));
			this.feedingFilterPanel.add(this.getFeedingFilterGrid());
			this.feedingFilterPanel.addButton(new Ext.Button(this.getFeedingFilterNewAction()));
			this.feedingFilterPanel.addButton(new Ext.Button(this.getFeedingFilterRemoveAction()));
			this.feedingFilterPanel.addButton(new Ext.Button(this.getFeedingFilterResetAction()));
		}
		return this.feedingFilterPanel;		
	},
	
	getReplicationFilterPanel: function() {
		if (!this.replicationFilterPanel) {
			this.replicationFilterPanel = new Ext.form.FormPanel({
				id: 'replicationFilterPanel',
				border: false,
				style: {
					// marginTop: '10px',
					marginLeft: '10px',
					marginRight: '10px',
					marginBottom: '10px'
				}	        	
			});			
			this.replicationFilterPanel.add(new Ext.form.Checkbox({
				id: this.replicationServiceCheckBoxID,
				fieldLabel: Openwis.i18n('CacheConfiguration.Replication.EnableReplication'),
				handler: this.setServiceStatus,
				service:  this.replicationService,
				active: true
			}));
			this.replicationFilterPanel.add(this.getReplicationGrid());
			this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterNewAction()));
			this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterEditAction()));
			this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterRemoveAction()));
			this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterActivateAction()));
			this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterDeactivateAction()));
		}
		return this.replicationFilterPanel;		
	},

	getDisseminationPanel: function() {
		if (!this.disseminationPanel) {
			this.disseminationPanel = new Ext.form.FormPanel({
				id: 'disseminationPanel',
				border: false,
				style: {
					marginLeft: '10px',
					marginRight: '10px'
				}	        	
			});			
			this.disseminationPanel.add(new Ext.form.Checkbox({
				id: this.disseminationServiceCheckBoxID,
				fieldLabel: Openwis.i18n('CacheConfiguration.Dissemination.EnableDissemination'),
				handler: this.setServiceStatus,
				service: this.disseminationService,
				active: true
			}));
			this.disseminationPanel.addButton(new Ext.Button(this.getDisseminationUpdateAction()));
		}
		return this.disseminationPanel;
	},

	getIngestionFilterGrid: function() {
		if (!this.ingestionFilterGrid) {
			this.ingestionFilterGrid = new Ext.grid.GridPanel({
				id: 'ingestionFilterGrid',
				height: 150,
				border: true,
				store: this.getIngestionFilterRequestsStore(),
				loadMask: true,
				columns: [
					{id:'regex', header:Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.RegExp'), dataIndex:'regex', width: 400, sortable: true, hideable:false},
					{id:'description', header: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Description'), dataIndex:'description', width: 300, sortable: true, hideable:false}
				],
				listeners: {
					afterrender: function (grid) {
						grid.loadMask.show();
						grid.getStore().load({
							params: {
								start: 0, 
								limit: Openwis.Conf.PAGE_SIZE
							}
						});
						var checkbox = this.getIngestionFilterPanel().get(this.ingestionServiceCheckBoxID);
						this.updateServiceStatus(checkbox);
					},
					scope:this
				},
				// paging bar on the bottom
				bbar: new Ext.PagingToolbar({
					pageSize: Openwis.Conf.PAGE_SIZE,
					store: this.getIngestionFilterRequestsStore(),
					displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Range'),
                    emptyMsg: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.No.Data'),
					listeners: { 
						beforechange: function(toolbar, params) {
							// clear grid selection
							var sm = this.getIngestionFilterGrid().getSelectionModel();
							sm.clearSelections(true);
							// disable/enable ingestion button(s)
							this.disableIngestionButtons(sm);
							// update service check box
							var checkbox = this.getIngestionFilterPanel().get(this.ingestionServiceCheckBoxID);
							this.updateServiceStatus(checkbox);
						},
						scope: this
					}
				}),
				selModel: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							this.disableIngestionButtons(sm);
						},
						rowdeselect: function (sm, rowIndex, record) {
							this.disableIngestionButtons(sm);
						},
						scope: this
					},
					singleSelect: true
				})
			});
		}
		return this.ingestionFilterGrid;
	},
	
	getFeedingFilterGrid: function() {
		if (!this.feedingFilterGrid) {
			this.feedingFilterGrid = new Ext.grid.GridPanel({
				id: 'feedingFilterGrid',
				height: 150,
				border: true,
				store: this.getFeedingFilterRequestsStore(),
				loadMask: true,
				columns: [
					{id:'regex', header:Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.RegExp'), dataIndex:'regex', width: 400, sortable: true, hideable:false},
					{id:'description', header: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Description'), dataIndex:'description', width: 300, sortable: true, hideable:false}
				],
				listeners: {
					afterrender: function (grid) {
						grid.loadMask.show();
						grid.getStore().load({
							params: {
								start: 0, 
								limit: Openwis.Conf.PAGE_SIZE
							}
						});
						var checkbox = this.getFeedingFilterPanel().get(this.feedingServiceCheckBoxID);
						this.updateServiceStatus(checkbox);
					},
					scope:this
				},
				// paging bar on the bottom
				bbar: new Ext.PagingToolbar({
					pageSize: Openwis.Conf.PAGE_SIZE,
					store: this.getFeedingFilterRequestsStore(),
					displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Range'),
                    emptyMsg: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.No.Data'),
					listeners: { 
						beforechange: function(toolbar, params) {
							// clear grid selection
							var sm = this.getFeedingFilterGrid().getSelectionModel();
							sm.clearSelections(true);
							// disable/enable feeding button(s)
							this.disableFeedingButtons(sm);
							// update service check box
							var checkbox = this.getFeedingFilterPanel().get(this.feedingServiceCheckBoxID);
							this.updateServiceStatus(checkbox);
						},
						scope: this
					}
				}),
				selModel: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							this.disableFeedingButtons(sm);
						},
						rowdeselect: function (sm, rowIndex, record) {
							this.disableFeedingButtons(sm);
						},
						scope: this
					},
					singleSelect: true
				})
			});
		}
		return this.feedingFilterGrid;
	},
	
	getReplicationGrid: function() {
		if (!this.replicationGrid) {
			var selectionModel = new Ext.grid.CheckboxSelectionModel({
				checkOnly: false,
				header: '',
				width: 22,
				listeners: { 
					rowselect: function (sm, rowIndex, record) {
						this.disableReplicationButtons(sm);
					},
					rowdeselect: function (sm, rowIndex, record) {
						this.disableReplicationButtons(sm);
					},
					scope: this
				}
			});
			this.replicationGrid = new Ext.grid.GridPanel({
				id: 'replicationGrid',
				height: 200,
				border: true,
				store: this.getReplicationRequestsStore(),
				loadMask: true,
				columns: [
					selectionModel,	// renders and maintains the check box
					{id: 'gisc', header: Openwis.i18n('CacheConfiguration.Replication.Gisc'), dataIndex: 'source', width: 100, sortable: true, hideable: false},
					{id: 'regex', header: Openwis.i18n('CacheConfiguration.Replication.RegExp'), dataIndex: 'regex', width: 140, sortable: true, hideable: false},
					{id: 'description', header: Openwis.i18n('CacheConfiguration.Replication.Description'), dataIndex: 'description', width: 200, sortable: true},
					{id: 'lastrun', header: Openwis.i18n('CacheConfiguration.Replication.LastRun'), dataIndex: 'uptime', renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong, width: 120, sortable: true, hideable: false},
					{id: 'status', header: Openwis.i18n('CacheConfiguration.Replication.Status'), dataIndex: 'active', renderer: this.activeRenderer, width: 90, sortable: true, hideable: false}
				],
				listeners: {
					afterrender: function (grid) {
						grid.loadMask.show();
						grid.getStore().load({
							params: {
								start: 0, 
								limit: Openwis.Conf.PAGE_SIZE
							}
						});
						var checkbox = this.getReplicationFilterPanel().get(this.replicationServiceCheckBoxID);
						this.updateServiceStatus(checkbox);
						checkbox = this.getDisseminationPanel().get(this.disseminationServiceCheckBoxID);
						this.updateServiceStatus(checkbox);
					},
					scope:this
				},
				// paging bar on the bottom
				bbar: new Ext.PagingToolbar({
					pageSize: Openwis.Conf.PAGE_SIZE,
					store: this.getReplicationRequestsStore(),
					displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Range'),
                    emptyMsg: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.No.Data'),
					listeners: { 
						beforechange: function(toolbar, params) {
							// clear grid selection
							var sm = this.getReplicationGrid().getSelectionModel();
							sm.clearSelections(true);
							// enable/disable buttons
							this.disableReplicationButtons(sm);
							// update service check box
							var checkbox = this.getReplicationFilterPanel().get(this.replicationServiceCheckBoxID);
							this.updateServiceStatus(checkbox);
						},
						scope:this
					}
				}),
				selModel: selectionModel
			});
		}
		return this.replicationGrid;
	},
	
	getIngestionFilterRequestsStore: function() {
		if (!this.ingestionFilterRequestsStore) {
			this.ingestionFilterRequestsStore = new Openwis.Data.JeevesJsonStore({
				service: this.ingestionService,
				url: configOptions.locService + this.ingestionService,
				root: 'rows',
				totalProperty: 'total',
				idProperty: 'regex',
				fields: [
					{name: 'regex'},
					{name: 'description'}
				],
				sortInfo: {
					field: 'regex',
					direction: 'ASC'
				}
			});			
		}
		return this.ingestionFilterRequestsStore;
	},
	
	getFeedingFilterRequestsStore: function() {
		if (!this.feedingFilterRequestsStore) {
			this.feedingFilterRequestsStore = new Openwis.Data.JeevesJsonStore({
				service: this.feedingService,
				url: configOptions.locService + this.feedingService,
				root: 'rows',
				totalProperty: 'total',
				idProperty: 'regex',
				fields: [
					{name: 'regex'},
					{name: 'description'}
				],
				sortInfo: {
					field: 'regex',
					direction: 'ASC'
				}
			});			
		}
		return this.feedingFilterRequestsStore;		
	},

	getReplicationRequestsStore: function() {
		if (!this.replicationRequestsStore) {
			this.replicationRequestsStore = new Openwis.Data.JeevesJsonStore({
				service: this.replicationService,
				url: configOptions.locService + this.replicationService,
				root: 'rows',
				totalProperty: 'total',
				// idProperty: 'source',
				fields: [
					{name: 'active'},
					{name: 'source'},
					{name: 'type'},
					{name: 'uptime'},
					{name: 'regex'},
					{name: 'description'}
				],
				sortInfo: {
					field: 'source',
					direction: 'ASC'
				}
			});			
		}
		return this.replicationRequestsStore;		
	},
	
	getIngestionFilterNewAction: function() {
		if (!this.ingestionFilterNewAction) {
			this.ingestionFilterNewAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				disabled: false,
				scope: this,
				handler: function() {
					var store = this.getIngestionFilterRequestsStore();
					this.addFilter(store, 'Ingestion');
				}	            
			});
		}
		return this.ingestionFilterNewAction;
	},
	
	getIngestionFilterRemoveAction: function() {
		if (!this.ingestionFilterRemoveAction) {
			this.ingestionFilterRemoveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Remove'),
				disabled: true,
				scope: this,
				handler: function() { 	
					var selectedFilter = this.getIngestionFilterGrid().getSelectionModel().getSelected();
					var store = this.getIngestionFilterRequestsStore();
					this.removeFilter(selectedFilter, store, false);
				}
			});
		}
		return this.ingestionFilterRemoveAction;
	},
	
	getFeedingFilterNewAction: function() {
		if (!this.feedingFilterNewAction) {
			this.feedingFilterNewAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				disabled: false,
				scope: this,
				handler: function() {
					var store = this.getFeedingFilterRequestsStore();
					this.addFilter(store,  'Feeding');
				}	            
			});
		}
		return this.feedingFilterNewAction;
	},
	
	getFeedingFilterRemoveAction: function() {
		if (!this.feedingFilterRemoveAction) {
			this.feedingFilterRemoveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Remove'),
				disabled: true,
				scope: this,
				handler: function() { 	
					var selectedFilter = this.getFeedingFilterGrid().getSelectionModel().getSelected();
					var store = this.getFeedingFilterRequestsStore();					
					this.removeFilter(selectedFilter, store, false);
				}
			});
		}
		return this.feedingFilterRemoveAction;
	},
	
	getFeedingFilterResetAction: function() {
		if (!this.feedingFilterResetAction) {
			this.feedingFilterResetAction = new Ext.Action({
				text: Openwis.i18n('CacheConfiguration.Btn.ResetToDefault'),
				disabled: false,
				scope: this,
				handler: function() {
					this.getFeedingFilterGrid().getStore().load({
						params: {
							start: 0, 
							limit: Openwis.Conf.PAGE_SIZE,
							requestType: 'RESET_TO_DEFAULT'
						}                    	
					});	            	
				}
			});
		}
		return this.feedingFilterResetAction;
	},

	getReplicationFilterNewAction: function() {
		if (!this.replicationFilterNewAction) {
			this.replicationFilterNewAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				disabled: false,
				scope: this,
				handler: function() { 	
					var newFilter = {}
					newFilter.source = Openwis.i18n('CacheConfiguration.Replication.New.Source');
					newFilter.type = Openwis.i18n('CacheConfiguration.Replication.New.Type');
					newFilter.regex = Openwis.i18n('CacheConfiguration.Replication.New.RegExp');
					newFilter.description = Openwis.i18n('CacheConfiguration.Replication.New.Description');
					newFilter.active = false;
					var store = this.getReplicationRequestsStore();
					this.editFilter(newFilter, store, 'New');
				}
			});
		}
		return this.replicationFilterNewAction;
	},
	
	getReplicationFilterEditAction: function() {
		if (!this.replicationFilterEditAction) {
			this.replicationFilterEditAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Edit'),
				disabled: true,
				scope: this,
				handler: function() { 	
					var selectedFilter = this.getReplicationGrid().getSelectionModel().getSelected();
					var store = this.getReplicationRequestsStore();
					if (selectedFilter) {
						this.editFilter(selectedFilter.data, store, 'Edit');
					}
				}
			});
		}
		return this.replicationFilterEditAction;
	},
	
	getReplicationFilterRemoveAction: function() {
		if (!this.replicationFilterRemoveAction) {
			this.replicationFilterRemoveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Remove'),
				disabled: true,
				scope: this,
				handler: function() { 	
					var store = this.getReplicationRequestsStore();
					var selectedFilters = this.getReplicationGrid().getSelectionModel().getSelections();
					this.removeFilters(selectedFilters, store, true);
				}
			});
		}
		return this.replicationFilterRemoveAction;
	},
	
	getReplicationFilterActivateAction: function() {
		if (!this.replicationFilterActivateAction) {
			this.replicationFilterActivateAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Activate'),
				disabled: true,
				scope: this,
				handler: function() { 	
					var store = this.getReplicationRequestsStore();
					var reload = false;
					var activate = true;
					var selections = this.getReplicationGrid().getSelectionModel().getSelections();
					for (var i = 0; i < selections.length; i++) {
						var filter = selections[i];
						if (i == selections.length - 1) {
							reload = true;
						}
						this.activateFilter(filter, activate, store, reload);
					}
				}
			});
		}
		return this.replicationFilterActivateAction;
	},
	
	getReplicationFilterDeactivateAction: function() {
		if (!this.replicationFilterDeactivateAction) {
			this.replicationFilterDeactivateAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Deactivate'),
				disabled: true,
				scope: this,
				handler: function() { 	
					var store = this.getReplicationRequestsStore();
					var reload = false;
					var activate = false;
					var selections = this.getReplicationGrid().getSelectionModel().getSelections();
					for (var i = 0; i < selections.length; i++) {
						var filter = selections[i];
						if (i == selections.length - 1) {
							reload = true;
						}
						this.activateFilter(filter, activate, store, reload);
					}
				}
			});
		}
		return this.replicationFilterDeactivateAction;
	},

	getDisseminationUpdateAction: function() {
		if (!this.disseminationUpdateAction) {
			this.disseminationUpdateAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				disabled: false,
				scope: this,
				handler: function() { 	
					// update service check box
					var checkbox = this.getDisseminationPanel().get(this.disseminationServiceCheckBoxID);
					this.updateServiceStatus(checkbox);
				}
			});
		}
		return this.disseminationUpdateAction;		
	},

	disableIngestionButtons: function(sm) {
		if (sm) {
			var disabled = (sm.getCount() != 1);	// single selection
			this.getIngestionFilterRemoveAction().setDisabled(disabled);
		}
	},

	disableFeedingButtons: function(sm) {
		if (sm) {
			var disabled = (sm.getCount() != 1);	// single selection
			this.getFeedingFilterRemoveAction().setDisabled(disabled);
		}
	},
	
	disableReplicationButtons: function(sm) {
		if (sm) {
			var disabled = (sm.getCount() == 0);	// multi selection
			this.getReplicationFilterRemoveAction().setDisabled(disabled);
			this.getReplicationFilterEditAction().setDisabled(disabled);
			this.getReplicationFilterActivateAction().setDisabled(disabled);
			this.getReplicationFilterDeactivateAction().setDisabled(disabled);
		}
	},
	
	/**
	 * Prompts the user to enter data for a new ingestion or feeding filter.
	 * @param store (object) the store to be reloaded
	 * @param type (string) type of filter to be added
	 */
	addFilter: function(store, type) {
		var newFilter = {};
		newFilter.regex = Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Edit.RegExp.Value');
		newFilter.description = Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Edit.Description.Value');
		
		var filterDialog = new Openwis.Admin.DataService.FilterInputDialog({
			operationMode: 'New',
			filterType: type,
			selectedFilter: newFilter,
			locationService: store.service,
			store: store,
			listeners: {			
				filterSaved: function(msg, isError) {
					if (isError)	{
						Ext.Msg.show({
							title: Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Edit.Title'),
							msg: msg,
							buttons: Ext.Msg.OK,
							scope: this,
							icon: Ext.MessageBox.ERROR
						});
					}
					else	{
						/*
						Ext.Msg.show({
							title: 'Add filter',
							msg: msg,
							buttons: Ext.Msg.OK,
							scope: this,
							icon: Ext.MessageBox.INFO
						});
						*/
						store.reload();
					}
				},
				scope: this
			}
		});
		filterDialog.show();		
	},

	/**
	 * Prompts the user to enter data for a new or existing replication filter.
	 * @param filter (object)filter to be edited or new filter
	 * @param store (object) the store to be reloaded
	 * @param mode (String) new or edit
	 */
	editFilter: function(filter, store, mode) {
		if (filter) {
			var filterDialog = new Openwis.Admin.DataService.ReplicationFilterDialog({
				operationMode: mode,
				selectedFilter: filter,
				locationService: store.service,
				store: store,
				listeners: {
					filterSaved: function() {
						store.reload();
					},
					scope: this
				}
			});
			filterDialog.show();
		}
	},
	
	/**
	 * Prompts the user to remove a selected filter (if not null). Requests a commit for removing.
	 * @param filter (object) the selected filter or null
	 * @param store (object) the store to be reloaded
	 * @param isReplicationFilter (boolean) true if filter is a ReplicationFilter else false
	 */
	removeFilter: function(filter, store, isReplicFilter) {
		if (filter) {
			Ext.MessageBox.confirm(Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Title'), 
					Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Msg') , 
			   function(btnClicked) {
					if (btnClicked == 'yes') {
						this.proceedRemoveFilter(filter, store, isReplicFilter);
					}
				},
				this
			);
		}		
	},

	/**
	 * Prompts the user to removes a collection of selected filters (if not null). Requests a commit for removing.
	 * @param filters the selected filters or null
	 * @param store (object) the store to be reloaded
	 * @param isReplicationFilter (boolean) true if filter is a ReplicationFilter else false
	 */
	removeFilters: function(filters, store, isReplicFilter) {
		if (filters) {

			Ext.MessageBox.confirm(Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Title'), 
				Openwis.i18n('CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Msg'), 
			   function(btnClicked) {
					if (btnClicked == 'yes') {
						for (var i = 0; i < filters.length; i++) {
							var filter = filters[i];
							this.proceedRemoveFilter(filter, store, isReplicFilter);
						}
					}
				},
				this
			);
		}		
	},
	
	/**
	 * Removes a selected filter (if not null). Requests a commit for removing.
	 * @param filter (object) the selected filter or null
	 * @param store (object) the store to be reloaded
	 * @param isReplicationFilter (boolean) true if filter is a ReplicationFilter else false
	 */
	proceedRemoveFilter: function(filter, store, isReplicFilter) {
		if (filter) {
			handler = new Openwis.Handler.GetNoJson({
				url: configOptions.locService + store.service,
				params: { 
					requestType: 'REMOVE_FILTER',
					regex: filter.get('regex'),
					description: filter.get('description'),
					source: isReplicFilter ? filter.get('source') : null,
					type: isReplicFilter ? filter.get('type') : null,
					active: isReplicFilter ? filter.get('active') : null
				},
				listeners: {
					success: function(responseText) {
						store.reload();
					},
					scope: this
				}
			});
			handler.proceed();
		}	
	},

	/**
	 * Activates/de-activates a ingestion, feeding or replication service
	 * @param checkbox (object) check box that triggered the event
	 * @param checked (boolean) current check box value
	 * @param service (string) execution service
	 */
	setServiceStatus: function(checkbox, checked) {
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + checkbox.service,
			params: {
				requestType: 'SET_SERVICE_STATUS',								
				checked: checked
			},
			listeners: {
				success: function(responseText) {
					var resultElement = Openwis.Utils.Xml.getElement(responseText, 'result');
					var attributes = resultElement.attributes;
					var success = Openwis.Utils.Xml.getAttribute(attributes, 'success');
					var isError = false;
					if (success != null) {
						if (!success.nodeValue == 'true') {
							isError = true;
						}
					}
					else {
						isError = true;
					}
					if (isError) {
						//var errorMsg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
						//var msg =  (errorMsg != null) ? errorMsg : "Invalid response";
						Openwis.Utils.MessageBox.displayInternalError();		
						// reset status
						checkbox.active = false;
						checkbox.setValue(!checked);
						checkbox.active = true;						
					}
				},
				failure: function(responseText) {
					Openwis.Utils.MessageBox.displayInternalError();		
					checkbox.active = false;
					checkbox.setValue(!checked);
					checkbox.active = true;
				},
				scope: this
			}
		});
		if (checkbox.active) {
			handler.proceed();	
		}
	},
	
	/**
	 * Queries the current service status from a given service
	 * and updates the given check box value.
	 * @param checkbox (object) check box that shall be updated
	 */
	updateServiceStatus: function(checkbox) {
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + checkbox.service,
			useLoadMask: false,
			params: {
				requestType: 'GET_SERVICE_STATUS'
			},
			listeners: {
				success: function(responseText) {
					var resultElement = Openwis.Utils.Xml.getElement(responseText, 'result');
					var attributes = resultElement.attributes;
					var success = Openwis.Utils.Xml.getAttribute(attributes, 'success');					
					var isError = false;
					if (success != null) {
						if (success.nodeValue == 'true') {
							var status = Openwis.Utils.Xml.getAttributeValue(attributes, 'status');
							var enabled = status == 'ENABLED';
							checkbox.active = false;
							checkbox.setValue(enabled);
							checkbox.active = true;
						}
						else {
							var isError = true;
						}
					}
					else {
						var isError = true;
					}
					if (isError) {
						//var errorMsg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
						//var msg =  (errorMsg != null) ? errorMsg : "Invalid response";
						Openwis.Utils.MessageBox.displayInternalError();
					}					
				},
				failure: function(responseText) {
					Openwis.Utils.MessageBox.displayInternalError();		
				},
				scope: this
			}
		});
		handler.proceed();		
	},
		
	/**
	 * Activates/de-activates a replication filter
	 * @param filter (object) filter to be activated deactivated
	 * @param active (boolean) new activated status
	 * @param store (object) store to be reloaded after request
	 * @param reload (boolean) shall reload or not
	 */
	activateFilter: function(filter, active, store, reload) {
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + this.replicationService,
			params: {
				requestType: 'SET_FILTER_STATUS',								
				regex: filter.get('regex'),
				description: filter.get('description'),
				source: filter.get('source'),
				regex: filter.get('regex'),
				active: filter.get('active'),
				checked: active
			},
			listeners: {
				success: function(responseText) {
					var resultElement = Openwis.Utils.Xml.getElement(responseText, 'result');
					var attributes = resultElement.attributes;
					var success = Openwis.Utils.Xml.getAttribute(attributes, 'success');					
					var isError = false;
					if (success != null) {
						if (success.nodeValue == 'true') {
							if (reload) {
								store.reload();
							}
						}
						else {
							isError = true;							
						}
					}
					else {
						isError = true;
					}					
					if (isError) {
						//var errorMsg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
						//var msg =  (errorMsg != null) ? errorMsg : "Invalid response";
						Openwis.Utils.MessageBox.displayInternalError();		
					}					
				},
				failure: function(responseText) {
				},
				scope: this
			}
		});
		handler.proceed();	
	},
	
	/**
	 * Returns a selected or unselected check box according the store value.
	 * @param value (boolean) store value
	 */
	activeRenderer: function(value) {
		return value == true ? Openwis.i18n('CacheConfiguration.MSSFSS.Replication.activeRenderer.Active') 
				: Openwis.i18n('CacheConfiguration.MSSFSS.Replication.activeRenderer.Suspended');
	},
	
	/**
	* Returns a check box to provide a selection renderer
	* @param value not used
	*/
	checkRenderer: function(value) {
		return new Ext.form.Checkbox({
			boxLabel: '',
			checked: false
		});
	}
	
});
