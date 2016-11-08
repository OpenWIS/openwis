Ext.ns('Openwis.Admin.DataService');

Openwis.Admin.DataService.BrowseContent = Ext.extend(Ext.Container, {

    //-- Initiatization
	cacheService: '/xml.management.cache.browse',

    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Admin.DataService.BrowseContent.superclass.initComponent.apply(this, arguments);

        this.initialize();
    },

    initialize: function() {
        // Create Header.
        this.add(this.getHeader());

        // search form panel
        this.add(this.getSearchFormPanel());
        
        // Create Grid.
        this.add(this.getFileGrid());
    },

    getHeader: function() {
        if (!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('BrowseContent.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },

    //-- Grid and Store.

    getFileGrid: function() {

        // build an array of columns.

        if (!this.fileGrid) {

            /* Build columns table for file grid. */
            var columns = [];
            columns.push(new Ext.grid.Column({id:'date', header:Openwis.i18n('BrowseContent.Administration.Grid.date'), dataIndex:'insertionDate', renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong, width: 120, sortable: true}));
            columns.push(new Ext.grid.Column({id:'filename', header:Openwis.i18n('BrowseContent.Administration.Grid.filename'), dataIndex:'filename', sortable: true, width: 200, hideable:false}));
            columns.push(new Ext.grid.Column({id:'checksum', header:Openwis.i18n('BrowseContent.Administration.Grid.checksum'), dataIndex:'checksum', sortable: true, width: 115}));
            columns.push(new Ext.grid.Column({id:'origin', header:Openwis.i18n('BrowseContent.Administration.Grid.origin'), dataIndex:'origin', sortable: true, width: 65}));
            columns.push(new Ext.grid.Column({id:'metadata', header:Openwis.i18n('BrowseContent.Administration.Grid.metadata'), dataIndex:'metadataUrn', sortable: true, width: 225}));

            this.fileGrid = new Ext.grid.GridPanel({
            	id: 'fileGrid',
                height: 475,
                border: true,
                store: this.getFileStore(),
                loadMask: true,
                viewConfig: {
                    forceFit: true
                },
                columns: columns,
                listeners: {
                    afterrender: function(grid) {
                       	grid.loadMask.show();
                       	// this.getFilterParams(true);
                       	// trigger the reload only if the getFilterParams request has been finished
                       	this.reset();
                       	this.reload();	
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: this.getPagingToolbar()
            });
        }
        return this.fileGrid;
    },

    // PagingToolbar
    getPagingToolbar: function() {
    	if (!this.pagingToolbar) {
    		this.pagingToolbar = new Ext.PagingToolbar({
    			pageSize: Openwis.Conf.PAGE_SIZE,
    			store: this.getFileStore(),
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
    	}
    	return this.pagingToolbar;
    },

    getSearchFormPanel: function() {
        if (!this.searchFormPanel) {
			this.searchFormPanel = new Ext.form.FormPanel({
				// itemCls: 'formItems',
				labelWidth: 75,
				border: false,
				// column layout with 2 columns
				layout: 'column',
				// defaults for the columns
				defaults: {
					columnWidth: 0.5,
					layout: 'form',
					border: false,
					bodyStyle: 'padding:0 18px 0 0'
				},
				items: [{
					// left column
					// defaults for fields
					defaults: {
						anchor: '100%'
					},
					items: [
						this.getSearchFileNameField()
					]
				},
				{
					// right column
					// defaults for fields
					defaults: {
						anchor: '100%'
					},
					items: [
						this.getSearchMetadataIdField()
					]
				}]
			});
			this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
			this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()));
        }
        return this.searchFormPanel;
    },
    
    getSearchFileNameField: function() {
		if(!this.searchFileNameTextField) {
			this.searchFileNameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('BrowseContent.Administration.Search.file'),
				name: 'searchFile',
				width: 150
			});
		}
		return this.searchFileNameTextField;
	},

	getSearchMetadataIdField: function() {
		if(!this.searchMetadataIdTextField) {
			this.searchMetadataIdTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('BrowseContent.Administration.Search.metadata'),
				name: 'searchMetadata',
				width: 150
			});
		}
		return this.searchMetadataIdTextField;
	},
    
    // File store
    getFileStore: function() {
        if (!this.fileStore) {
            this.fileStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.management.cache.browse',
                remoteSort: true,
                // reader configs
                root: 'rows',
                totalProperty: 'total',
                idProperty: 'filename',
                fields: [
                    {name: 'filename', sortType: Ext.data.SortTypes.asUCString, mapping: 'name'}, 
                    {name: 'checksum', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'origin'},
                    {name: 'metadataUrn'},
                    {name: 'insertionDate'}
                ],
                sortInfo: {
                    field: 'insertionDate',
                    direction: 'DESC'
                }
            });
        }
        return this.fileStore;
    },
    
    //-- Actions

    getSearchAction: function() {
        if (!this.searchAction) {
            this.searchAction = new Ext.Action({
                disabled: false,
                text: Openwis.i18n('Common.Btn.Search'),
                scope: this,
                handler: this.reload
            });
        }
        return this.searchAction;
    },

    /**
     * Reset search and update grid
     */
    getResetAction: function() {
        if (!this.resetAction) {
            this.resetAction = new Ext.Action({
                disabled: false,
                text: Openwis.i18n('Common.Btn.Reset'),
                scope: this,
                handler: function() {
                	this.reset();
                	this.reload();
                }
            });
        }
        return this.resetAction;
    },
   
    //-- Utilities
    renderFileOrigin: function(value) {
        return value=='1'? Openwis.i18n('BrowseContent.Administration.renderFileOrigin.replication'):'';
    },
    
    /**
     * Performs a filtered or non-filtered reload of the grid data
     */
    reload: function() {
		// gets values from filter fields and check validity of date input
		var filename = this.getSearchFileNameField().getValue();
		var metadataID = this.getSearchMetadataIdField().getValue();

		// perform a filtered reload
		this.setBaseParams();
		
		this.getFileStore().load({
			params: {
				start: 0, 
				limit: Openwis.Conf.PAGE_SIZE,
				filename: filename,
				metadataUrn: metadataID
			}
		});
	},
    
    
	/**
	 * Resets the configured filter.
	 */
	reset: function() {
		// resets the filter settings in the service
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + this.cacheService,
			params: {
				requestType: 'RESET_FILTER'						
			}
		});
		handler.proceed();
		// flushes storage
		var filter = {};
		// flushes all filter field inputs
		this.updateFilterFields(filter);
		// reset store base parameters
		this.setBaseParams();
	},
	
	
    
	/**
	 * Retrieves the current filter parameters from the service.
	 * If any resets the filter input fields.
	 * @reload flag to trigger the data reload if the result from the request has received
	 */
	getFilterParams: function(reload) {
		// resets the filter settings in the service
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + this.cacheService,
			params: {
				requestType: 'GET_FILTER_PARAMS'								
			},
			listeners: {
				success: function(responseText) {
					var resultElement = Openwis.Utils.Xml.getElement(responseText, 'filter');
					var attributes = resultElement.attributes;
					var success = Openwis.Utils.Xml.getAttributeValue(attributes, 'success');					
					
					if (success == 'true') {
						var filter = {
							filename: Openwis.Utils.Xml.getAttributeValue(attributes, 'filename'),
							metadataid: Openwis.Utils.Xml.getAttributeValue(attributes, 'metadataUrn')
						}						
						this.updateFilterFields(filter);
						if (reload) {
							this.reload();
						}
					}
					else {
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
	 * Updates all filter fields with previous input (on page start)
	 * @param filter stored filter elements
	 */
	updateFilterFields: function(filter) {
		this.getSearchFileNameField().setValue(filter.filename);
		this.getSearchMetadataIdField().setValue(filter.metadataUrn);
	},

	/**
	 * Sets the base parameters for the store
	 */
	setBaseParams: function() {
		this.getFileStore().setBaseParam('filename', this.getSearchFileNameField().getValue());
		this.getFileStore().setBaseParam('metadataUrn', this.getSearchMetadataIdField().getValue());
	}

});