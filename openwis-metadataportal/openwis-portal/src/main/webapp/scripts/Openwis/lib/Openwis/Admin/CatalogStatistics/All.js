Ext.ns('Openwis.Admin.CatalogStatistics');

Openwis.Admin.CatalogStatistics.All = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.CatalogStatistics.All.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var params = {};
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.catalogstatistics.get',
			params: params,
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
				},
				failure: function(config) {
        			alert("failed");
				},
				scope: this
			}
		});
		getHandler.proceed();
	},

	initialize: function() {
		// Create Header.
		this.add(this.getHeader());

		// Create label
		this.add(this.createLabel("sizeLabel", Openwis.i18n('CatalogStatisticsManagement.size'),
				this.config.catalogSize, ''));
		this.add(this.createLabel("numberLabel", Openwis.i18n('CatalogStatisticsManagement.numberMetadata'),
				this.config.nbMetadata, ''));

		// Create search text form
        this.add(this.getSearchFormPanel());

        // Create Processed requests grid.
		this.add(this.getCatalogGrid());

		// Layout
		this.doLayout();
	},

	toDoEdit: function() {
		//Create Header.
		this.add(this.getHeader());
		
        var p = new Ext.Panel({
			vertical: true
        });

    	// totalSize template 
    	var totalSizeTplMarkup = [
    		Openwis.i18n('CatalogStatisticsManagement.totalSize')+': {size}<br/>',
    		Openwis.i18n('CatalogStatisticsManagement.totalNumber')+': {nbRecords}<br/>'
    	];
    	var totalSizeTpl = new Ext.Template(totalSizeTplMarkup);
    	
    	var totalSizeContainer = new Ext.Panel({
        	id: "totalSizeContainer",
            border: false,
            tpl: totalSizeTpl,
			cls: 'formItems',
			html: '',
			style : {
               padding: '5px'
            }
        });

		catalogGrid.getSelectionModel().on('rowselect', function(sm, rowIdx, r) {
			var tsContainer = Ext.getCmp('totalSizeContainer');
			var records = sm.getSelections();
			var data = new Object({size:0,nbRecords:0});
			Ext.each(records, function(item, index, allItems) {
				data.size += item.get('size');
				data.nbRecords += item.get('nbRecords');
			}, this);
			totalSizeTpl.overwrite(tsContainer.body, data);
		});

		catalogGrid.getSelectionModel().on('rowdeselect', function(sm, rowIdx, r) {
			var tsContainer = Ext.getCmp('totalSizeContainer');
			var data = new Object({size:0,nbRecords:0});
			totalSizeTpl.overwrite(tsContainer.body, data);
		});

		p.add(totalSizeContainer);

	},

	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('CatalogStatisticsManagement.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},

	/****************************/
    /*  Search text form Panel  */
    
    getSearchFormPanel: function() {
        if(!this.searchFormPanel) {
            this.searchFormPanel = new Ext.form.FormPanel({
                labelWidth: 100,
                border: false,
                buttonAlign: 'center'
            });
            this.searchFormPanel.add(this.getSearchTextField());
            this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
            this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()));
        }
        return this.searchFormPanel;
    },

	// The text field for the text search accross metadata.
    getSearchTextField: function() {
        if(!this.searchTextField) {
            this.searchTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('CatalogStatisticsManagement.Source.Search'),
                name: 'any',
                enableKeyEvents: true, // is that the best? or change event?
                width: 150,
                listeners: {
                    keyup: function(){
                        var searchOn = Ext.isEmpty(this.getSearchTextField().getValue().trim()); 
                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        if (searchOn) {
                            this.getAllCatalogStore().setBaseParam(
                                this.getSearchTextField().getName(),
                                this.getSearchTextField().getValue()
                            );
                            this.getAllCatalogStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                        }
                    },
                    specialkey: function(f,e){
	                    if (e.getKey() == e.ENTER) {
	                      this.getSearchAction().execute();
	                    }
	                  },
                    scope: this
                }
            });
        }
        return this.searchTextField;
    },

    // Performs search
    getSearchAction: function() {
        if (!this.searchAction) {
            this.searchAction = new Ext.Action({
                disabled: true,
                text:'Search',
                scope: this,
                handler: function() {
                    this.getAllCatalogStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getAllCatalogStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                }
            });
        }
        return this.searchAction;
    },

    // Reset search and update grid
    getResetAction: function() {
        if (!this.resetAction) {
            this.resetAction = new Ext.Action({
                disabled: true,
                text:'Reset',
                scope: this,
                handler: function() {
                    this.getSearchTextField().setValue('');
                    this.getAllCatalogStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getAllCatalogStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    this.getSearchAction().setDisabled(true);
                    this.getResetAction().setDisabled(true);
                }
            });
        }
        return this.resetAction;
    },

    //-- Grid and Store.
    
	getCatalogGrid: function() {
		if(!this.catalogGrid) {
			this.catalogGrid = new Ext.grid.GridPanel({
				id: 'catalogGrid',
				height: 200,
				border: true,
				store: this.getAllCatalogStore(),
				loadMask: true,
				columns: [
					{id:'date', header:Openwis.i18n('CatalogStatisticsManagement.grid.date'), dataIndex:'date', sortable: true, width: 100,renderer: Openwis.Utils.Date.formatDateUTCfromLong},
					{id:'source', header:Openwis.i18n('CatalogStatisticsManagement.grid.source'), dataIndex:'source', sortable: true, width: 300},
					{id:'totalSize', header:Openwis.i18n('CatalogStatisticsManagement.grid.size'), dataIndex:'totalSize', sortable: true, width: 100, renderer: this.volumeRenderer},
					{id:'nbMetadata', header:Openwis.i18n('CatalogStatisticsManagement.grid.nbrecords'), dataIndex:'nbMetadata', sortable: true, width: 100}
				],
				listeners: { 
					afterrender: function (grid) {
					   grid.loadMask.show();
					   grid.getStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
					}
				},
		        // paging bar on the bottom
		        bbar: new Ext.PagingToolbar({
		            pageSize: Openwis.Conf.PAGE_SIZE,
		            store: this.getAllCatalogStore(),
		            displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
		            displayMsg: Openwis.i18n('CatalogStatisticsManagement.All.Display.Range'),
		            emptyMsg: Openwis.i18n('CatalogStatisticsManagement.All.No.Task')
		        })
			});
			this.catalogGrid.addButton(new Ext.Button(this.getExportAction()));
		}
		return this.catalogGrid;
	},
	
	volumeRenderer: function(vol) {
		var volKB = parseInt(vol / 1024);
		return volKB;
	},

    createLabel: function(idVal,label,value,unit) {
        return new Ext.Container({
        	id: idVal,
            border: false,
			cls: 'formItems',
            html: label + ': ' + value + ' ' + unit
        });
    },

    // Catalog store
	getAllCatalogStore: function() {
        if (!this.allCatalogStore) {
            this.allCatalogStore = new Openwis.Data.JeevesJsonStore({	
                url: configOptions.locService+ '/xml.catalogstatistics.all',
                remoteSort: true,
                // reader configs
                root: 'allExchangedData',
                totalProperty: 'count',
                fields: [
                    {name: 'date'},
                    {name: 'source', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'totalSize'},
                    {name: 'nbMetadata'}
                ],
                sortInfo: {
                   field: 'date',
                   direction: 'DESC'
                }
            });
        }
        return this.allCatalogStore;
    },

    getExportAction: function() {
        if(!this.exportAction) {
            this.exportAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getAllCatalogStore().data.length > 0 ) {   		
                		window.location.href = configOptions.locService 
                			+ "/xml.catalogstatistics.export?start=0&xml=true&any="
                			+ this.getSearchTextField().getValue() + "&sort=" 
                			+ this.getAllCatalogStore().sortInfo.field 
                			+ "&dir=" + this.getAllCatalogStore().sortInfo.direction;
                    }
                	else
            		{
                		Ext.Msg.show({
    					    title: Openwis.i18n('CatalogStatisticsManagement.NoDataToExport.WarnDlg.Title'),
	    				    msg: Openwis.i18n('CatalogStatisticsManagement.NoDataToExport.WarnDlg.Msg'),
		                    buttons: Ext.MessageBox.OK,
		                    icon: Ext.MessageBox.WARNING
		               });
            		}
                }
            });
        }
        return this.exportAction;
    }

});