Ext.ns('Openwis.Admin.Statistics');

Openwis.Admin.Statistics.GlobalReports = Ext.extend(Ext.Container, {
	
	disseminatedService: '/xml.management.alarms.reports.disseminated',
	exchangedService: '/xml.management.alarms.reports.extracted',
	ingestedService: '/xml.management.cache.statistics.ingest',
	replicatedService: '/xml.management.cache.statistics.replic',

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Statistics.GlobalReports.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		// Create Header.
		this.add(this.getHeader());

		// Create filter panel.
		this.add(this.getFilterPanel());
		
		this.add(new Ext.Container({
			html: Openwis.i18n('Alarms.GlobalReports.Disseminated.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '20px'
			}
		}));
		this.add(this.getDataDisseminatedGrid());
		
		this.add(new Ext.Container({
			html: Openwis.i18n('Alarms.GlobalReports.Extracted.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'
			}
		}));
		this.add(this.getDataExtractedGrid());
		
		this.add(new Ext.Container({
			html: Openwis.i18n('Alarms.GlobalReports.Ingested.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'
			}
		}));
		this.add(this.getDataIngestedGrid());
		
		this.add(new Ext.Container({
			html: Openwis.i18n('Alarms.GlobalReports.Replicated.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'}
		}));
		this.add(this.getDataReplicatedGrid());		
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Alarms.GlobalReports.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	getFilterPanel: function() {
		if (!this.filterPanel) {
			this.filterPanel = new Ext.form.FormPanel({
				labelWidth: 100,
				border: false
			});
			this.filterPanel.add(this.getFilterDayComboBox());
		}
		return this.filterPanel;
	},
	
	getFilterDayComboBox: function() {
		if (!this.filterDayComboBox) {
			this.filterDayComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('Alarms.GlobalReports.Filter.Label'),
				name: 'filterDayComboBox',
				width: 150,
				allowBlank: false,
				editable: false,
				mode: 'local',
				store: new Ext.data.ArrayStore({
					id: 0,
					fields: [
					    'day',
					    'displayText'
					],
					data: [
					       [1, '1 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Day')], 
					       [2, '2 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [3, '3 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [4, '4 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [5, '5 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [6, '6 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [7, '7 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [8, '8 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [9, '9 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')],
					       [10, '10 ' + Openwis.i18n('Alarms.GlobalReports.Filter.Days')]
					]
				}),
				valueField: 'day',
				displayField: 'displayText',
				triggerAction : 'all',
				listeners: {
					scope: this,
					select: function(combo, rcord, index) {
						this.reloadAll();
					}
				}
			});
		}
		return this.filterDayComboBox;
	},
	
	//----------------------------- Monitor data disseminated requests.
	
	getDataDisseminatedGrid: function() {
		if (!this.dataDisseminatedGrid) {
			this.dataDisseminatedGrid = new Ext.grid.GridPanel({
				id: 'dataDisseminatedGrid',
				height: 250,
				border: true,
				store: this.getDataDisseminatedStore(),
				loadMask: true,
				columns: [
					{id: 'date', header:  Openwis.i18n('Alarms.GlobalReports.Disseminated.Grid.Date'), dataIndex: 'date', renderer: Openwis.Utils.Date.formatDateUTCfromLong, width: 100, sortable: true, hideable: false},
					{id: 'user', header: Openwis.i18n('Alarms.GlobalReports.Disseminated.Grid.User'), dataIndex: 'userId', width: 150, sortable: true, hideable: false},
					{id: 'size', header: Openwis.i18n('Alarms.GlobalReports.Disseminated.Grid.Size'), dataIndex: 'dissToolSize', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true},
					{id: 'threshold', header: Openwis.i18n('Alarms.GlobalReports.Disseminated.Grid.Threshold'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 200, sortable: true}
				],
				listeners: {
					afterrender: function(grid) {
						// this.loadData(grid);
					},
					scope: this
				},
				// paging bar on the bottom
				bbar: this.createToolbar(this.getDataDisseminatedStore())
			});
			this.dataDisseminatedGrid.addButton(new Ext.Button(this.getDataDisseminatedExportAction()));
		}
		return this.dataDisseminatedGrid;
	},
	
	getDataDisseminatedStore: function() {
		if (!this.dataDisseminatedStore) {
			this.dataDisseminatedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService + this.disseminatedService,
				remoteSort: true,
				// reader configs
				root: 'rows',
				totalProperty: 'total',
				fields: [
					{name: 'date'},
					{name: 'userId', sortType: Ext.data.SortTypes.asUCString},
					{name: 'size', sortType: Ext.data.SortTypes.asInt},
					{name: 'dissToolSize', sortType: Ext.data.SortTypes.asInt}
				],
				sortInfo: {
					field: 'date',
					direction: 'DESC'
				}
			});
		}
		return this.dataDisseminatedStore;
	}, 

	getDataDisseminatedExportAction: function() {
        if (!this.dataDisseminatedExportAction) {
            this.dataDisseminatedExportAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataDisseminatedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService + this.disseminatedService + "?start=0&xml=true&sort=" + this.getDataDisseminatedStore().sortInfo.field + "&dir=" + this.getDataDisseminatedStore().sortInfo.direction + "&period=" + this.getFilterPeriod(), '_blank', '');
                    }
                	else {
                		Ext.Msg.show({
    					    title: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Title'),
	    				    msg: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
            		}
                }
            });
        }
        return this.dataDisseminatedExportAction;
    },
	
	//--------------------------- Monitor data disseminated and extracted requests.
	
	getDataExtractedGrid: function() {
		if (!this.dataExtractedGrid) {
			this.dataExtractedGrid = new Ext.grid.GridPanel({
				id: 'dataExtractedGrid',
				height: 250,
				border: true,
				store: this.getDataExtractedStore(),
				loadMask: true,
				columns: [
					{id: 'date', header: Openwis.i18n('Alarms.GlobalReports.Extracted.Grid.Date'), dataIndex: 'date', renderer: Openwis.Utils.Date.formatDateUTCfromLong, width: 100, sortable: true, hideable: false},
					{id: 'extracted', header: Openwis.i18n('Alarms.GlobalReports.Extracted.Grid.Extracted'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true},
					{id: 'disseminated', header:Openwis.i18n('Alarms.GlobalReports.Extracted.Grid.Disseminated'), dataIndex: 'dissToolSize', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true}
				],
				listeners: {
					afterrender: function(grid) {
						// this.loadData(grid);
					},
					scope:this
				},
				// paging bar on the bottom
				bbar: this.createToolbar(this.getDataExtractedStore())
			});
			this.dataExtractedGrid.addButton(new Ext.Button(this.getDataExtractedExportAction()));
		}
		return this.dataExtractedGrid;
	},

	getDataExtractedStore: function() {
		if (!this.dataExtractedStore) {
			this.dataExtractedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService + this.exchangedService,
				remoteSort: true,
				root: 'rows',
				totalProperty: 'total',
				idProperty: 'date',
				fields: [
					{name: 'date'},
					{name: 'size', sortType: Ext.data.SortTypes.asInt},
					{name: 'dissToolSize', sortType: Ext.data.SortTypes.asInt}
				],
				sortInfo: {
					field: 'date',
					direction: 'DESC'
				}
			});
		}
		return this.dataExtractedStore;
	},

	getDataExtractedExportAction: function() {
        if (!this.dataExtractedExportAction) {
            this.dataExtractedExportAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataExtractedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService + this.exchangedService + "?start=0&xml=true&sort=" + this.getDataExtractedStore().sortInfo.field + "&dir=" + this.getDataExtractedStore().sortInfo.direction + "&period=" + this.getFilterPeriod(), '_blank', '');
                    }
                	else {
                		Ext.Msg.show({
    					    title: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Title'),
	    				    msg: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
            		}
                }
            });
        }
        return this.dataExtractedExportAction;
    },
	
	//--------------------------- Monitor data ingested requests.
	
	getDataIngestedGrid: function() {
		if (!this.dataIngestedGrid) {
			this.dataIngestedGrid = new Ext.grid.GridPanel({
				id: 'dataIngestedGrid',
				height: 250,
				border: true,
				store: this.getDataIngestedStore(),
				loadMask: true,
				columns: [
					{id: 'date', header: Openwis.i18n('Alarms.GlobalReports.Ingested.Grid.Date'), dataIndex: 'date', renderer: Openwis.Utils.Date.formatDateUTCfromLong, width: 100, sortable: true, hideable: false},
					{id: 'size', header: Openwis.i18n('Alarms.GlobalReports.Ingested.Grid.Size'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true}
				],
				listeners: {
					afterrender: function(grid) {
						// this.loadData(grid);
					},
					scope: this
				},
				// paging bar on the bottom
				bbar: this.createToolbar(this.getDataIngestedStore())
			});
			this.dataIngestedGrid.addButton(new Ext.Button(this.getDataIngestedExportAction()));
		}
		return this.dataIngestedGrid;
	},
	
	getDataIngestedStore: function() {
		if (!this.dataIngestedStore) {
			this.dataIngestedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService + this.ingestedService,
				remoteSort: true,
				// reader configs
				root: 'rows',
				totalProperty: 'total',
				idProperty: 'date',
				fields: [
					{name: 'date'},
					{name: 'size', sortType: Ext.data.SortTypes.asInt}
				],
				sortInfo: {
					field: 'date',
					direction: 'DESC'
				}
			});
		}
		return this.dataIngestedStore;
	}, 

	getDataIngestedExportAction: function() {
        if (!this.dataIngestedExportAction) {
            this.dataIngestedExportAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataIngestedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService + this.ingestedService + "?start=0&xml=true&sort=" + this.getDataIngestedStore().sortInfo.field + "&dir=" + this.getDataIngestedStore().sortInfo.direction + "&period=" + this.getFilterPeriod(), '_blank', '');
                    }
                	else {
                		Ext.Msg.show({
    					    title: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Title'),
	    				    msg: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
            		}
                }
            });
        }
        return this.dataIngestedExportAction;
    },
	
	//--------------------------- Monitor data replicated requests.
	
	getDataReplicatedGrid: function() {
		if (!this.dataReplicatedGrid) {
			this.dataReplicatedGrid = new Ext.grid.GridPanel({
				id: 'dataReplicatedGrid',
				height: 250,
				border: true,
				store: this.getDataReplicatedStore(),
				loadMask: true,
				columns: [
					{id: 'date', header: Openwis.i18n('Alarms.GlobalReports.Replicated.Grid.Date'), dataIndex: 'date', renderer: Openwis.Utils.Date.formatDateUTCfromLong, width: 100, sortable: true, hideable: false},
					{id: 'source', header: Openwis.i18n('Alarms.GlobalReports.Replicated.Grid.Source'), dataIndex: 'source', width: 120, sortable: true},
					{id: 'size', header: Openwis.i18n('Alarms.GlobalReports.Replicated.Grid.Size'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true}
				],
				listeners: {
					afterrender: function(grid) {
						// triggers the initial reload of all stores
						// this.updateSelectionPeriod();
						this.reloadAll();
					},
					scope: this
				},
				// paging bar on the bottom
				bbar: this.createToolbar(this.getDataReplicatedStore())
			});
			this.dataReplicatedGrid.addButton(new Ext.Button(this.getDataReplicatedExportAction()));
		}
		return this.dataReplicatedGrid;
	},

	getDataReplicatedStore: function() {
		if (!this.dataReplicatedStore) {
			this.dataReplicatedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService + this.replicatedService,
				remoteSort: true,
				root: 'rows',
				totalProperty: 'total',
				fields: [
					{name: 'date'},
					{name: 'source'},
					{name: 'size', sortType: Ext.data.SortTypes.asInt}
				],
				sortInfo: {
					field: 'date',
					direction: 'DESC'
				}
			});
		}
		return this.dataReplicatedStore;
	},

	getDataReplicatedExportAction: function() {
        if (!this.dataReplicatedExportAction) {
            this.dataReplicatedExportAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataReplicatedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService + this.replicatedService + "?start=0&xml=true&sort=" + this.getDataReplicatedStore().sortInfo.field + "&dir=" + this.getDataReplicatedStore().sortInfo.direction + "&period=" + this.getFilterPeriod(), '_blank', '');
                    }
                	else {
                		Ext.Msg.show({
    					    title: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Title'),
	    				    msg: Openwis.i18n('RequestsStatistics.NoDataToExport.WarnDlg.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
            		}
                }
            });
        }
        return this.dataReplicatedExportAction;
    },
	
	/**
	 * Gets the last selected filter period from the service.
	 * Updates the selection combo box.
	 */
	updateSelectionPeriod: function() {
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + this.disseminatedService,
			params: {
				requestType: 'GET_FILTER_PERIOD'								
			},
			listeners: {
				success: function(responseText) {
					var resultElement = Openwis.Utils.Xml.getElement(responseText, 'filter');
					var attributes = resultElement.attributes;
					var success = Openwis.Utils.Xml.getAttributeValue(attributes, 'success');					
					var value = '1';	// default
					
					if (success == 'true') {
						value = Openwis.Utils.Xml.getAttributeValue(attributes, 'period');
					}
					else {
						Openwis.Utils.MessageBox.displayInternalError();	
					}					
					this.setSelectionPeriod(value, true);
				},
				failure: function(responseText) {
					Openwis.Utils.MessageBox.displayInternalError();	
					this.setSelectionPeriod('1', true);
				},
				scope: this
			}
		});
		handler.proceed();
	},
	
	/**
	 * Sets the selection period to the combo box.
	 * Suppresses the select handler.
	 * @param period (string) selection period
	 * @param reload (boolean) flag to reload the stores
	 */
	setSelectionPeriod: function(period, reload) {
		this.getFilterDayComboBox().setValue(period);
		if (reload) {
			this.reloadAll();
		}
	},

	/**
	 * Reloads all stores.
	 */
	reloadAll: function() {
		this.loadData(this.getDataDisseminatedGrid());
		this.loadData(this.getDataExtractedGrid());
		this.loadData(this.getDataIngestedGrid());
		this.loadData(this.getDataReplicatedGrid());
	},
	
	/**
	 * Loads th data for a given grid.
	 * @param grid (Ext.grid.GridPanel) the grid that should be populated with data
	 */
	loadData: function(grid) {
		// gets the current user selection (1 - 10 days)
		var period = this.getFilterPeriod();
		grid.loadMask.show();
		grid.getStore().setBaseParam('period', period);
		grid.getStore().load({
			params: {
				start: 0, 
				limit: Openwis.Conf.PAGE_SIZE
			}                    	   
		});
	},
	
	/**
	 * Creates a paging tool bar for a given grid
	 * @param store (Openwis.Data.JeevesJsonStore) store controlled by the tool bar
	 * @return Ext.PagingToolbar
	 */
	createToolbar: function(store) {
		return new Ext.PagingToolbar({
			pageSize: Openwis.Conf.PAGE_SIZE,
			store: store,
			displayInfo: true,
            beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
	        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
	        firstText: Openwis.i18n('Common.Grid.FirstText'),
	        lastText: Openwis.i18n('Common.Grid.LastText'),
	        nextText: Openwis.i18n('Common.Grid.NextText'),
	        prevText: Openwis.i18n('Common.Grid.PrevText'),
	        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
            displayMsg: Openwis.i18n('Common.Grid.Range'),
            emptyMsg: Openwis.i18n('Common.Grid.No.Data')
		});
	},
	
	/**
	 * Gets the selected value from the filter combo box.
	 * @return filter period
	 */
	getFilterPeriod: function() {
		var period = this.getFilterDayComboBox().getValue();
		if (period == "") {
			period = 1;
		}
		return period;
	}
	
});