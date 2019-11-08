Ext.ns('Openwis.Admin.Statistics');

Openwis.Admin.Statistics.RecentEvents = Ext.extend(Ext.Container, {

	dateFormat: 'Y-m-d H:i',
	alarmService: '/xml.management.alarms.events',
    //-- Initialization

    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Admin.Statistics.RecentEvents.superclass.initComponent.apply(this, arguments);

        this.initialize();
    },

    initialize: function() {
        // Create Header.
        this.add(this.getHeader());

        // search form panel
        this.add(this.getSearchFormPanel());
        // Create Grid.
        this.add(this.getEventGrid());
    },

    getHeader: function() {
        if (!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Alarms.RecentEvents.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },

    //-- Grid and Store.
    getEventGrid: function() {
        // build an array of columns.
        if (!this.eventGrid) {
            /* Build columns table for event grid. */
            var columns = [];
            
            columns.push(new Ext.grid.Column({
            	id: 'date', 
            	header: Openwis.i18n('Alarms.RecentEvents.Grid.Date'),             	 
            	dataIndex: 'date', 
            	renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong, 
            	width: 120,
            	sortable: true,
            	hideable: false
            }));
            columns.push(new Ext.grid.Column({
            	id: 'component', 
            	header: Openwis.i18n('Alarms.RecentEvents.Grid.Component'), 
            	dataIndex: 'module', 
            	width: 110, 
            	sortable: true
            }));
            columns.push(new Ext.grid.Column({
            	id: 'process', 
            	header: Openwis.i18n('Alarms.RecentEvents.Grid.Process'), 
            	dataIndex: 'source', 
            	width: 130, 
            	sortable: true,
            	renderer: this.renderProcess.createDelegate(this)
            }));
            columns.push(new Ext.grid.Column({
            	id: 'severtity', 
            	header: Openwis.i18n('Alarms.RecentEvents.Grid.Severity'), 
            	dataIndex: 'severity', 
            	width: 70, 
            	sortable: true
            }));
            columns.push(new Ext.grid.Column({
            	id: 'description', 
            	header: Openwis.i18n('Alarms.RecentEvents.Grid.Description'), 
            	dataIndex: 'message', 
            	width: 300, 
            	sortable: true, 
            	hideable: false,
            	renderer: this.renderMessage.createDelegate(this)
            }));

            this.eventGrid = new Ext.grid.GridPanel({            	
            	id: 'eventGrid',
                height: 400,
                border: true,
                store: this.getEventStore(),
                loadMask: true,
                view: this.getGridView(),
                columns: columns,
                listeners: {
                    afterrender: function(grid) {
                       	grid.loadMask.show();
						// this.getFilterParams();
						this.reset();
                       	this.reload();
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: this.getPagingToolbar()
            });
        }
        return this.eventGrid;
    },

    // PagingToolbar
    getPagingToolbar: function() {
    	if (!this.pagingToolbar) {
    		this.pagingToolbar = new Ext.PagingToolbar({
    			pageSize: Openwis.Conf.PAGE_SIZE,
    			store: this.getEventStore(),
    			displayInfo: true,
                beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
		        firstText: Openwis.i18n('Common.Grid.FirstText'),
		        lastText: Openwis.i18n('Common.Grid.LastText'),
		        nextText: Openwis.i18n('Common.Grid.NextText'),
		        prevText: Openwis.i18n('Common.Grid.PrevText'),
		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                displayMsg: Openwis.i18n('Alarms.RecentEvents.Grid.Range'),
                emptyMsg: Openwis.i18n('Alarms.RecentEvents.Grid.No.Data')
    		});
    	}
    	return this.pagingToolbar;
    },

    // Event store
    getEventStore: function() {
        if (!this.eventStore) {
            this.eventStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService + this.alarmService,
                remoteSort: true,
                // reader configs
                root: 'rows',
                totalProperty: 'total',
                idProperty: 'id',
                fields: [
                    {name: 'id'},
                    {name: 'date' /*, sortType: Ext.data.SortTypes.asDate */ },
                    {name: 'source', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'module', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'severity', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'message', sortType: Ext.data.SortTypes.asUCString}
                ],
                sortInfo: {
                    field: 'date',
                    direction: 'DESC'
                }
            });
        }
        return this.eventStore;
    },
    
    /**
     * Grid view to render a grid row with red background color if
     * event severity is ERROR
     */
    getGridView: function() {
        if (!this.gridView) {
        	this.gridView = new Ext.grid.GridView({
				emptyText: Openwis.i18n('Alarms.RecentEvents.Grid.No.Data'),
				forceFit: true,
				getRowClass: function(record, index, rowParams, store) {
					if (Ext.util.Format.lowercase(record.data.severity) == 'error') {
						return 'eventErrorGridRow';
					}
				}
        	});
        }
        return this.gridView;
    },
    
    /**
     * Tool tip rendering for message grid cell 
     * @param {Mixed} value value to render
     * @param {Object} cell
     * @param {Ext.data.Record} record
     */
    renderMessage: function(value, cell, record) {
    	// get data
    	var data = record.data;
    	var msg = data.message;
    	
    	// create tool tip
    	return '<div qtip="' + Ext.util.Format.htmlEncode(msg) +'">' + Ext.util.Format.htmlEncode(value) + '</div>';
    },

    /**
     * Tool tip rendering for process grid cell 
     * @param {Mixed} value value to render
     * @param {Object} cell
     * @param {Ext.data.Record} record
     */
    renderProcess: function(value, cell, record) {
    	// get data
    	var data = record.data;
    	var process = data.source;
    	
    	// create tool tip
    	return '<div qtip="' + process +'">' + value + '</div>';
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
					this.getSearchFromDateField(),
					this.getSearchSeverityComboField(),
					this.getSearchComponentTextField()
				]
			},{
				// right column
				// defaults for fields
				defaults: {
					anchor: '100%'
				},
				items: [
					this.getSearchToDateField(),
					this.getSearchProcessTextField(),
					this.getSearchDescriptionTextField()
				]
			}]
		});
		this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
		this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()));
        }
        return this.searchFormPanel;
    },

	getSearchFromDateField: function() {
		if (!this.searchFromDateField) {
			this.searchFromDateField = new Ext.form.DateField({
				fieldLabel: Openwis.i18n('Alarms.RecentEvents.Filter.DateFrom'),
				name: 'searchFromDate',
				width: 150,
				allowBlank: true,
				format: this.dateFormat
			});
		}
		return this.searchFromDateField;
	},

	getSearchToDateField: function() {
		if (!this.searchToDateField) {
			this.searchToDateField = new Ext.form.DateField({
				fieldLabel: Openwis.i18n('Alarms.RecentEvents.Filter.DateTo'),
				name: 'searchToDate',
				width: 150,
				allowBlank: true,
				format: this.dateFormat
			});
		}
		return this.searchToDateField;
	},

	getSearchSeverityComboField: function() {
		if(!this.searchSeverityComboField) {
			this.searchSeverityComboField = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('Alarms.RecentEvents.Filter.Severity'),
				name: 'searchSeverity',
				width: 150,
				allowBlank: true,
				mode: 'local',
				store: ['INFO', 'WARN','ERROR'],
				triggerAction : 'all'
			});
		}
		return this.searchSeverityComboField;
	},

	getSearchComponentTextField: function() {
		if(!this.searchComponentTextField) {
			this.searchComponentTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Alarms.RecentEvents.Filter.Component'),
				name: 'searchComponent',
				width: 150
			});
		}
		return this.searchComponentTextField;
	},

	getSearchProcessTextField: function() {
		if(!this.searchProcessTextField) {
			this.searchProcessTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Alarms.RecentEvents.Filter.Process'),
				name: 'searchProcess',
				width: 150
			});
		}
		return this.searchProcessTextField;
	},

	getSearchDescriptionTextField: function() {
		if(!this.searchDescriptionTextField) {
			this.searchDescriptionTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Alarms.RecentEvents.Filter.Description'),
				name: 'searchDecription',
				width: 150
			});
		}
		return this.searchDescriptionTextField;
	},

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
    /**
     * Performs a filtered or unfiltered reload of the grid data
     */
    reload: function() {
		// gets values from filter fields and check validity of date input
		if (!this.getSearchFromDateField().isValid()) {
			Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n('Alarms.RecentEvents.Error.DateFormat') + this.dateFormat);		
			return;
		}
		if (!this.getSearchToDateField().isValid()) {
			Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n('Alarms.RecentEvents.Error.DateFormat') + this.dateFormat + "'!");		
			return;
		}
		var date1 = this.getSearchFromDateField().getValue();
		var date2 = this.getSearchToDateField().getValue();
		var severity = this.getSearchSeverityComboField().getValue();
		var component = this.getSearchComponentTextField().getValue();
		var process = this.getSearchProcessTextField().getValue();
		var text = this.getSearchDescriptionTextField().getValue();

		var fromDate = '';
		var toDate = '';
		if (date1 != null && date1 != '') {
			fromDate = date1.format(this.dateFormat);
		}
		if (date2 != null && date2 != '') {
			toDate = date2.format(this.dateFormat);
		}
		
		// perform a filtered reload
		this.setBaseParams();
		
		this.getEventStore().load({
			params: {
				start: 0, 
				limit: Openwis.Conf.PAGE_SIZE,
				date_from: fromDate,
				date_to: toDate,
				severity: severity,
				module: component,
				source: process,
				message: text
			}
		});
	},
    
	/**
	* Resets the configured filter.
	*/
	reset: function() {
		// resets the filter settings in the service
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + this.alarmService,
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
	*/
	getFilterParams: function() {
		// resets the filter settings in the service
		var handler = new Openwis.Handler.GetNoJson({
			url: configOptions.locService + this.alarmService,
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
							date_from: Openwis.Utils.Xml.getAttributeValue(attributes, 'date_from'),
							date_to: Openwis.Utils.Xml.getAttributeValue(attributes, 'date_to'),
							severity: Openwis.Utils.Xml.getAttributeValue(attributes, 'severity'),
							module: Openwis.Utils.Xml.getAttributeValue(attributes, 'module'),
							source: Openwis.Utils.Xml.getAttributeValue(attributes, 'source'),
							message: Openwis.Utils.Xml.getAttributeValue(attributes, 'message')
						}						
						this.updateFilterFields(filter);
					}
					else {
						Openwis.Utils.MessageBox.displayInternalError();		
					}					
				},
				failure: function(responseText) {
					Openwis.Utils.MessageBox.displayErrorMsg();		
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
		this.getSearchFromDateField().setValue(filter.date_from);
		this.getSearchToDateField().setValue(filter.date_to);
		this.getSearchSeverityComboField().setValue(filter.severity);
		this.getSearchComponentTextField().setValue(filter.module);
		this.getSearchProcessTextField().setValue(filter.source);
		this.getSearchDescriptionTextField().setValue(filter.message);
	},
	
	/**
	 * Sets the base parameters for the store
	 */
	setBaseParams: function() {
		this.getEventStore().setBaseParam('date_from', this.getSearchFromDateField().getValue());
		this.getEventStore().setBaseParam('date_to', this.getSearchToDateField().getValue());
		this.getEventStore().setBaseParam('severity', this.getSearchSeverityComboField().getValue());
		this.getEventStore().setBaseParam('module', this.getSearchComponentTextField().getValue());
		this.getEventStore().setBaseParam('source', this.getSearchProcessTextField().getValue());
		this.getEventStore().setBaseParam('message', this.getSearchDescriptionTextField().getValue());
	}
	
});