Ext.ns('Openwis.Admin.Availability');

Openwis.Admin.Availability.Statistics = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Availability.Statistics.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},

	initialize: function() {
		// Create Header.
		this.add(this.getHeader());

		// Create search text form
        this.add(this.getSearchFormPanel());

        // Create stat grid.
		this.add(this.getAvailabilityStatsGrid());
		
        // Create session count grid.
		this.add(this.getSessionCountGrid());
		
		// Layout
		this.doLayout();
	},

	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Availability.Statistics.Title'),
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
                fieldLabel: Openwis.i18n('Availability.Statistics.Service.Search'),
                name: 'serviceNameFilter',
                enableKeyEvents: true, // is that the best? or change event?
                width: 150,
                listeners: {
                    keyup: function(){
                        var searchOn = Ext.isEmpty(this.getSearchTextField().getValue().trim()); 
                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        if (searchOn) {
                            this.getAvailabilityStatsStore().setBaseParam(
                                this.getSearchTextField().getName(),
                                this.getSearchTextField().getValue()
                            );
                            this.getAvailabilityStatsStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
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
                    this.getAvailabilityStatsStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getAvailabilityStatsStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
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
                    this.getAvailabilityStatsStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getAvailabilityStatsStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    this.getSearchAction().setDisabled(true);
                    this.getResetAction().setDisabled(true);
                }
            });
        }
        return this.resetAction;
    },

    createLabel: function(idVal,label,value,unit) {
        return new Ext.Container({
        	id: idVal,
            border: false,
			cls: 'formItems',
            html: label + ': ' + value + ' ' + unit
        });
    },

    //-- Grid and Store for availability.    
    getAvailabilityStatsGrid: function() {
		if(!this.availabilityStatsGrid) {
			this.availabilityStatsGrid = new Ext.grid.GridPanel({
				id: 'availabilityStatsGrid',
				height: 250,
				border: true,
				store: this.getAvailabilityStatsStore(),
				loadMask: true,
				columns: [
					{id:'date', header:Openwis.i18n('Availability.Statistics.grid.date'), dataIndex:'date', sortable: true, width: 100},
					{id:'task', header:Openwis.i18n('Availability.Statistics.grid.task'), dataIndex:'task', sortable: true, width: 300},
					{id:'availability', header:Openwis.i18n('Availability.Statistics.grid.availability'), dataIndex:'available', sortable: false, width: 100, renderer: this.availabilityRenderer}
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
		            store: this.getAvailabilityStatsStore(),
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
			this.availabilityStatsGrid.addButton(new Ext.Button(this.getExportAction()));
		}
		return this.availabilityStatsGrid;
	},
	
	availabilityRenderer: function(value, metadata, record) {
		var availability = (record.data.available * 100) / (record.data.available + record.data.notAvailable);
		var str = Math.round(availability) + ' %';
		return str;
	},

	getAvailabilityStatsStore: function() {
        if (!this.availabilityStatsStore) {
            this.availabilityStatsStore = new Openwis.Data.JeevesJsonStore({	
                url: configOptions.locService+ '/xml.availability.getstatistics',
                remoteSort: true,
                // reader configs
                root: 'items',
                totalProperty: 'count',
                fields: [
                    {name: 'date'},
                    {name: 'task', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'available'},
                    {name: 'notAvailable'}
                ],
                sortInfo: {
                   field: 'date',
                   direction: 'DESC'
                }
            });
        }
        return this.availabilityStatsStore;
    },

    getExportAction: function() {
        if(!this.exportAction) {
            this.exportAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getAvailabilityStatsStore().data.length > 0 ) {   		
                        window.open(configOptions.locService +  
                        		"/xml.availability.getstatistics?start=0&xml=true&serviceNameFilter=" + this.getSearchTextField().getValue() + 
                        		"&sort=" + this.getAvailabilityStatsStore().sortInfo.field + 
                        		"&dir=" + this.getAvailabilityStatsStore().sortInfo.direction, 
                        	'_blank', '');
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
    },
    
    //-- Grid and Store for session count.    
    getSessionCountGrid: function() {
		if(!this.sessionCountGrid) {
			this.sessionCountGrid = new Ext.grid.GridPanel({
				id: 'sessionCountGrid',
				height: 250,
				border: true,
				store: this.getSessionCountStore(),
				loadMask: true,
				columns: [
					{id:'date', header:Openwis.i18n('Availability.Statistics.SessionCount.grid.date'), dataIndex:'date', sortable: true, width: 100},
					{id:'totalSessions', header:Openwis.i18n('Availability.Statistics.SessionCount.grid.totalSessions'), dataIndex:'notAvailable', sortable: true, width: 150},
					{id:'authenticatedSessions', header:Openwis.i18n('Availability.Statistics.SessionCount.grid.authenticatedSessions'), dataIndex:'available', sortable: true, width: 180}
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
		            store: this.getSessionCountStore(),
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
			this.sessionCountGrid.addButton(new Ext.Button(this.getExportSessionCountAction()));
		}
		return this.sessionCountGrid;
	},

	getSessionCountStore: function() {
        if (!this.sessionCountStore) {
            this.sessionCountStore = new Openwis.Data.JeevesJsonStore({	
                url: configOptions.locService+ '/xml.availability.getstatistics',
                remoteSort: true,
                // reader configs
                root: 'items',
                totalProperty: 'count',
                fields: [
                    {name: 'date'},
                    {name: 'task', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'available'},
                    {name: 'notAvailable'}
                ],
                sortInfo: {
                   field: 'date',
                   direction: 'DESC'
                }
            });
            this.sessionCountStore.setBaseParam("sessionCount", "true");
        }
        return this.sessionCountStore;
    },

    getExportSessionCountAction: function() {
        if(!this.exportSessionCountAction) {
            this.exportSessionCountAction = new Ext.Action({
            	text: Openwis.i18n('Common.Btn.Export'),
                scope: this,
                handler: function() {
                	if (this.getSessionCountStore().data.length > 0 ) {   		
                        window.open(configOptions.locService +  
                        		"/xml.availability.getstatistics?start=0&xml=true&sessionCount=true" + 
                        		"&sort=" + this.getSessionCountStore().sortInfo.field + 
                        		"&dir=" + this.getSessionCountStore().sortInfo.direction, 
                        	'_blank', '');
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
        return this.exportSessionCountAction;
    }

});