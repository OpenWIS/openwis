Ext.ns('Openwis.Admin.Category');

Openwis.Admin.DataService.RequestsStatistics = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.DataService.RequestsStatistics.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());

		//Create Processed requests grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('RequestsStatistics.DataDisseminated.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginBottom: '5px'
			}
		}));
		// create search text form
        this.add(this.getSearchFormPanel());
		this.add(this.getDataDisseminatedGrid());
		this.add(new Ext.Container({
			html: Openwis.i18n('RequestsStatistics.DataExtracted.Title'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px',
			    marginBottom: '5px'
			}
		}));
		this.add(this.getDataExtractedGrid());

	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('RequestsStatistics.Administration.Title'),
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
                fieldLabel: Openwis.i18n('RequestsStatistics.UserName.Search'),
                name: 'any',
                enableKeyEvents: true, // is that the best? or change event?
                width: 150,
                listeners: {
                    keyup: function(){
                        var searchOn = Ext.isEmpty(this.getSearchTextField().getValue().trim()); 
                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        if (searchOn) {
                            this.getDataDisseminatedStore().setBaseParam(
                                this.getSearchTextField().getName(),
                                this.getSearchTextField().getValue()
                            );
                            this.getDataDisseminatedStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
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
                text:Openwis.i18n('Common.Btn.Search'),
                scope: this,
                handler: function() {
                    this.getDataDisseminatedStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getDataDisseminatedStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
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
                text:Openwis.i18n('Common.Btn.Reset'),
                scope: this,
                handler: function() {
                    this.getSearchTextField().setValue('');
                    this.getDataDisseminatedStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getDataDisseminatedStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    this.getSearchAction().setDisabled(true);
                    this.getResetAction().setDisabled(true);
                }
            });
        }
        return this.resetAction;
    },

	//-- Grid and Store.
	
	getDataDisseminatedGrid: function() {
		if(!this.dataDisseminatedGrid) {
			this.dataDisseminatedGrid = new Ext.grid.GridPanel({
				id: 'dataDisseminatedGrid',
				height: 200,
				border: true,
				store: this.getDataDisseminatedStore(),
				loadMask: true,
				columns: [
					{id:'date', header:Openwis.i18n('RequestsStatistics.DataDisseminated.Date'), dataIndex:'date', sortable: true, width: 140, renderer: Openwis.Utils.Date.formatDateUTCfromLong},
					{id:'userId', header:Openwis.i18n('RequestsStatistics.DataDisseminated.UserId'), dataIndex:'userId', sortable: true, width: 140},
					{id:'dissToolSize', header:Openwis.i18n('RequestsStatistics.DataDisseminated.Size'), dataIndex:'dissToolSize', sortable: true, width: 140, renderer: Openwis.Common.Request.Utils.sizeRenderer},
					{id:'dissToolNbFiles', header:Openwis.i18n('RequestsStatistics.DataDisseminated.NbFiles'), dataIndex:'dissToolNbFiles', sortable: true, width: 140}
				],
				listeners: { 
                    afterrender: function (grid) {
                       grid.loadMask.show();
                       grid.getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getDataDisseminatedStore(),
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
			this.dataDisseminatedGrid.addButton(new Ext.Button(this.getExportDataDisseminatedAction()));
		}
		return this.dataDisseminatedGrid;
	},

	getDataDisseminatedStore: function() {
		if(!this.dataDisseminatedStore) {
			this.dataDisseminatedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.requestsStatistics.allDataDisseminated',
				remoteSort: true,
				root: 'allDataDisseminated',
                totalProperty: 'count',
				idProperty: 'id',
				fields: [
					{
						name:'id'
					},{
						name:'userId',
						sortType: Ext.data.SortTypes.asUCString
					},{
						name:'date'
					},{
						name:'dissToolNbFiles'
					},{
						name:'dissToolSize'
					}
				],
				sortInfo: {
				    field: 'date',
				    direction: 'DESC'
				}

			});
		}
		return this.dataDisseminatedStore;
	},

	getExportDataDisseminatedAction: function() {
        if(!this.exportDataDisseminatedAction) {
            this.exportDataDisseminatedAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataDisseminatedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService +  "/xml.requestsStatistics.allDataDisseminated.export?start=0&xml=true&any=" + this.getSearchTextField().getValue() + "&sort=" + this.getDataDisseminatedStore().sortInfo.field + "&dir=" + this.getDataDisseminatedStore().sortInfo.direction, '_blank', '');
                    }
                	else
            		{
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
        return this.exportDataDisseminatedAction;
    },

	getDataExtractedGrid: function() {
		if(!this.dataExtractedGrid) {
			this.dataExtractedGrid = new Ext.grid.GridPanel({
				id: 'dataExtractedGrid',
				height: 200,
				border: true,
				store: this.getDataExtractedStore(),
				loadMask: true,
				columns: [
					{id:'date', header:Openwis.i18n('RequestsStatistics.DataExtracted.Date'), dataIndex:'date', sortable: true, width: 140, renderer: Openwis.Utils.Date.formatDateUTCfromLong},
					{id:'size', header:Openwis.i18n('RequestsStatistics.DataExtracted.Extracted'), dataIndex:'size', sortable: true, width: 140, renderer: Openwis.Common.Request.Utils.sizeRenderer},
					{id:'dissToolSize', header:Openwis.i18n('RequestsStatistics.DataExtracted.Disseminated'), dataIndex:'dissToolSize', sortable: true, width: 140, renderer: Openwis.Common.Request.Utils.sizeRenderer}
				],
				listeners: { 
                    afterrender: function (grid) {
                       grid.loadMask.show();
                       grid.getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getDataExtractedStore(),
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
			this.dataExtractedGrid.addButton(new Ext.Button(this.getExportDataExtractedAction()));
		}
		return this.dataExtractedGrid;
	},

	getDataExtractedStore: function() {
		if(!this.dataExtractedStore) {
			this.dataExtractedStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.requestsStatistics.allDataExtracted',
				remoteSort: true,
				root: 'allDataExtracted',
                totalProperty: 'count',
                idProperty: 'id',
				fields: [
					{
						name:'id'
					},{
						name:'date',
						sortType: Ext.data.SortTypes.asUCString
					},{
						name:'size'
					},{
						name:'dissToolSize'
					}
				],
				sortInfo: {
				    field: 'date',
				    direction: 'DESC'
				}

			});
		}
		return this.dataExtractedStore;
	},

	getExportDataExtractedAction: function() {
        if(!this.exportDataExtractedAction) {
            this.exportDataExtractedAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getDataExtractedStore().data.length > 0 ) {   		
                        window.open(configOptions.locService +  "/xml.requestsStatistics.allDataExtracted.export?start=0&xml=true" + "&sort=" + this.getDataExtractedStore().sortInfo.field + "&dir=" + this.getDataExtractedStore().sortInfo.direction, '_blank', '');
                    }
                	else
            		{
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
        return this.exportDataExtractedAction;
    }
});