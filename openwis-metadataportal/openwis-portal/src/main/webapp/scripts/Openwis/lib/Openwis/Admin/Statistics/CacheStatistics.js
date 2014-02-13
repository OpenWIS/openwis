Ext.ns('Openwis.Admin.Statistics');

Openwis.Admin.Statistics.CacheStatistics = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Statistics.CacheStatistics.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create Processed requests grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('CacheStatistics.DataIngested.Title'),
			cls: 'administrationTitle2'
		}));
		this.add(this.getDataIngestedGrid());
		
		this.add(new Ext.Container({
			html: Openwis.i18n('CacheStatistics.DataReplicated.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'
			}
		}));
		this.add(this.getDataReplicatedGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('CacheStatistics.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	//----------------------------- Monitor data ingested requests.
	
	getDataIngestedGrid: function() {
		if (!this.dataIngestedGrid) {
			this.dataIngestedGrid = new Ext.grid.GridPanel({
			    id: 'dataIngestedGrid',
				height: 250,
                border: true,
                store: this.getDataIngestedStore(),
                loadMask: true,
                columns: [
                    {id: 'date', header:Openwis.i18n('CacheStatistics.DataIngested.Date'), dataIndex: 'date', renderer: Openwis.Utils.Date.formatDateUTCfromLong, width: 100, sortable: true, hideable: false},
                    {id: 'size', header:Openwis.i18n('CacheStatistics.DataIngested.Size'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true}
                ],
                listeners: {
                    afterrender: function(grid) {
                       grid.loadMask.show();
                       grid.getStore().load({
                    	   params: {
                    		   start: 0, 
                    		   limit: Openwis.Conf.PAGE_SIZE
               				}                    	   
                       });
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getDataIngestedStore(),
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
                })
			});
			this.dataIngestedGrid.addButton(new Ext.Button(this.getExportDataIngestedAction()));
		}
		return this.dataIngestedGrid;
	},
	
	getDataIngestedStore: function() {
		if (!this.dataIngestedStore) {
			this.dataIngestedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService + '/xml.management.cache.statistics.ingest',
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
                },
                baseParams: {}
			});
		}
		return this.dataIngestedStore;
	}, 

	getExportDataIngestedAction: function() {
        if (!this.exportDataIngestedAction) {
            this.exportDataIngestedAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataIngestedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService +  "/xml.management.cache.statistics.ingest.export?start=0&xml=true&sort=" + this.getDataIngestedStore().sortInfo.field + "&dir=" + this.getDataIngestedStore().sortInfo.direction, '_blank', '');
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
        return this.exportDataIngestedAction;
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
                    {id: 'date', header: Openwis.i18n('CacheStatistics.DataReplicated.Date'), dataIndex: 'date', renderer: Openwis.Utils.Date.formatDateUTCfromLong, width: 100, sortable: true, hideable: false},
                    {id: 'source', header: Openwis.i18n('CacheStatistics.DataReplicated.Source'), dataIndex: 'source', width: 120, sortable: true},
                    {id: 'size', header: Openwis.i18n('CacheStatistics.DataReplicated.Size'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 120, sortable: true}
    			],
                listeners: {
                    afterrender: function(grid) {
                       grid.loadMask.show();
                       grid.getStore().load({
                    	   params: {
                    		   start: 0, 
                    		   limit: Openwis.Conf.PAGE_SIZE
               				}                    	   
                       });
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getDataReplicatedStore(),
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
                })
		    });
			this.dataReplicatedGrid.addButton(new Ext.Button(this.getExportDataReplicatedAction()));
        }
        return this.dataReplicatedGrid;
	},

	getDataReplicatedStore: function() {
	    if(!this.dataReplicatedStore) {
    		this.dataReplicatedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService + '/xml.management.cache.statistics.replic',
                remoteSort: true,
                root: 'rows',
                totalProperty: 'total',
    			idProperty: 'date',
                fields: [
                     {name: 'date'},
                     {name: 'source'},
                     {name: 'size', sortType: Ext.data.SortTypes.asInt}
    			],
                sortInfo: {
                   field: 'date',
                   direction: 'DESC'
                },
                baseParams: {}
    		});
		}
		return this.dataReplicatedStore;
	},
	
	getExportDataReplicatedAction: function() {
        if (!this.exportDataReplicatedAction) {
            this.exportDataReplicatedAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataReplicatedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService +  "/xml.management.cache.statistics.replic.export?start=0&xml=true&sort=" + this.getDataReplicatedStore().sortInfo.field + "&dir=" + this.getDataReplicatedStore().sortInfo.direction, '_blank', '');
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
        return this.exportDataReplicatedAction;
    }
});