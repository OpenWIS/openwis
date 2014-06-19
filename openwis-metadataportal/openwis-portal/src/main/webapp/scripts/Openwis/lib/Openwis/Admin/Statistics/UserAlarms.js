Ext.ns('Openwis.Admin.Statistics');

Openwis.Admin.Statistics.UserAlarms = Ext.extend(Ext.Container, {

	dateFormat: 'Y-m-d H:i',
	alarmService: '/xml.useralarms.getalluseralarms',
	alarmReportService: '/xml.useralarms.getuseralarmreport',
    //-- Initialization

    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Admin.Statistics.UserAlarms.superclass.initComponent.apply(this, arguments);

        this.initialize();
    },

    initialize: function() {
        // Create Header.
        this.add(this.getHeader());

		this.add(new Ext.Container({
			html: 'User alarms messages:',
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'}
		}));

        // search form panel
        //this.add(this.getSearchFormPanel());
        // Create Grid.
        this.add(this.getUserAlarmGrid());

		this.add(new Ext.Container({
			html: 'Users with alarms report:',
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'}
		}));

		this.add(this.getUserAlarmReportGrid());
    },

    getHeader: function() {
        if (!this.header) {
            this.header = new Ext.Container({
                html: 'User Alarms',
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },

    getUserAlarmGrid: function() {
        // build an array of columns.
        if (!this.userAlarmGrid) {
            /* Build columns table for event grid. */
            var columns = [];

            columns.push(new Ext.grid.Column({
		id: 'date',
		header: 'Date',
		dataIndex: 'date',
		renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong,
		width: 120,
		sortable: true,
		hideable: false
            }));
            columns.push(new Ext.grid.Column({
		id: 'user',
		header: 'User',
		dataIndex: 'userId',
		width: 100,
		sortable: true
            }));
            columns.push(new Ext.grid.Column({
		id: 'alarmtype',
		header: 'Alarm Type',
		dataIndex: 'alarmType',
		width: 100,
		sortable: true,
            }));
            columns.push(new Ext.grid.Column({
		id: 'requestid',
		header: 'Req/Sub ID',
		dataIndex: 'requestId',
		width: 100,
		sortable: true
            }));
            columns.push(new Ext.grid.Column({
		id: 'message',
		header: 'Message',
		dataIndex: 'message',
		width: 300,
		sortable: true,
		hideable: false,
		renderer: this.renderMessage.createDelegate(this)
            }));

            this.userAlarmSelectionModel = new Ext.grid.RowSelectionModel({
                singleSelect: false,
                listeners: {
                    rowselect: function (sm, rowIndex, record) {
			this.getDeleteAction().setDisabled(sm.getCount() == 0);
                    },
                    rowdeselect: function (sm, rowIndex, record) {
			this.getDeleteAction().setDisabled(sm.getCount() == 0);
                    },
                    scope: this
                }
            });

            this.userAlarmGrid = new Ext.grid.GridPanel({
		id: 'eventGrid',
                height: 250,
                border: true,
                store: this.getAlarmEventStore(),
                loadMask: true,
                view: this.getGridView(),
                columns: columns,
                listeners: {
                    afterrender: function(grid) {
			this.reload();
                    },
                    scope:this
                },
                sm: this.userAlarmSelectionModel,
                // paging bar on the bottom
                bbar: this.getPagingToolbar()
            });

		this.userAlarmGrid.addButton(new Ext.Button(this.getDeleteAction()));
		this.userAlarmGrid.addButton(new Ext.Button(this.getDeleteAllAction()));
        }
        return this.userAlarmGrid;
    },


    // PagingToolbar
    getPagingToolbar: function() {
	if (!this.pagingToolbar) {
		this.pagingToolbar = new Ext.PagingToolbar({
			pageSize: Openwis.Conf.PAGE_SIZE,
			store: this.getAlarmEventStore(),
			displayInfo: true,
			displayMsg: 'Displaying alarm {0} - {1} of {2}',
			emptyMsg: "No alarms to display"
		});
	}
	return this.pagingToolbar;
    },

    // Event store
    getAlarmEventStore: function() {
        if (!this.alarmEventStore) {
            this.alarmEventStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService + this.alarmService,
                remoteSort: true,
                // reader configs
                root: 'rows',
                totalProperty: 'total',
                idProperty: 'id',
                fields: [
                    {name: 'id'},
                    {name: 'date' /*, sortType: Ext.data.SortTypes.asDate */ },
                    {name: 'userId', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'alarmType', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'requestId', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'message', sortType: Ext.data.SortTypes.asUCString}
                ],
                sortInfo: {
                    field: 'date',
                    direction: 'DESC'
                }
            });
        }
        return this.alarmEventStore;
    },

    getGridView: function() {
        if (!this.gridView) {
		this.gridView = new Ext.grid.GridView({
				emptyText: 'No results to display.',
				forceFit: true,
		});
        }
        return this.gridView;
    },

    getUserAlarmReportGrid: function() {
        if (!this.userAlarmReportGrid) {
            var columns = [
                new Ext.grid.Column({ id: 'userId', header: 'User ID', dataIndex: 'userId', width: 200, sortable: true, hideable: false }),
                new Ext.grid.Column({ id: 'requestCount', header: 'Requests', dataIndex: 'requestCount', width: 200, sortable: true, hideable: false }),
                new Ext.grid.Column({ id: 'subscriptionCount', header: 'Subscriptions', dataIndex: 'subscriptionCount', width: 200, sortable: true, hideable: false }),
                new Ext.grid.Column({ id: 'totalCount', header: 'Total', dataIndex: 'totalCount', width: 200, sortable: true, hideable: false })
            ];

		var userReportGridView = new Ext.grid.GridView({
				emptyText: 'No results to display.',
				forceFit: true
		});

		var userReportPagingToolbar = new Ext.PagingToolbar({
			pageSize: Openwis.Conf.PAGE_SIZE,
			store: this.getUserAlarmReportStore(),
			displayInfo: true,
			displayMsg: 'Displaying users {0} - {1} of {2}',
			emptyMsg: "No users to display"
		});

            this.userAlarmReportGrid = new Ext.grid.GridPanel({
		id: 'userReportGrid',
                height: 250,
                border: true,
                store: this.getUserAlarmReportStore(),
                loadMask: true,
                view: userReportGridView,
                columns: columns,
                listeners: {
                    afterrender: function(grid) {
			this.getUserAlarmReportStore().load({ params: { start: 0, limit: Openwis.Conf.PAGE_SIZE }});
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: userReportPagingToolbar
            });
        }
        return this.userAlarmReportGrid;

    },

    getUserAlarmReportStore: function() {
        if (!this.userAlarmReportStore) {
            this.userAlarmReportStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService + this.alarmReportService,
                remoteSort: true,
                // reader configs
                root: 'rows',
                totalProperty: 'total',
                idProperty: 'id',
                fields: [
                    { name: 'userId' },
                    { name: 'requestCount' },
                    { name: 'subscriptionCount' },
                    { name: 'totalCount' },
                ],
                sortInfo: {
                    field: 'totalCount',
                    direction: 'DESC'
                }
            });
        }
        return this.userAlarmReportStore;
    },


    /**
     * Performs a filtered or unfiltered reload of the grid data
     */
    reload: function() {
	var params = {
			start: 0,
			limit: Openwis.Conf.PAGE_SIZE
	};

		this.getAlarmEventStore().load({
			params: params
		});
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
	return '<div qtip="' + msg +'">' + value + '</div>';
    },

	getDeleteAction: function() {
		if (!this.deleteAction) {
			this.deleteAction = new Ext.Action({
				text: "Delete",
				iconCls: 'icon-discard-adhoc',
				disabled: true,
				scope: this,
				handler: function() {
				    var selection = this.userAlarmSelectionModel.getSelections();
					var params = {alarmIds: []};
					Ext.each(selection, function(item, index, allItems) {
						params.alarmIds.push(item.get('id'));
					}, this);

					new Openwis.Handler.Remove({
				url: configOptions.locService+ '/xml.useralarms.delete',
				params: params,
				listeners: {
					success: function() {
						this.getAlarmEventStore().reload();
						this.getUserAlarmReportStore().reload();
					},
					scope: this
				}
			}).proceed();
				}
			});
		}
		return this.deleteAction;
	},

	getDeleteAllAction: function() {
		if (!this.deleteAllAction) {
			this.deleteAllAction = new Ext.Action({
				text: "Delete All",
				iconCls: 'icon-discard-adhoc',
				scope: this,
				handler: function() {
					var params = { };
					new Openwis.Handler.Remove({
				url: configOptions.locService+ '/xml.useralarms.deleteall',
				params: params,
				listeners: {
					success: function() {
						this.getAlarmEventStore().reload();
						this.getUserAlarmReportStore().reload();
					},
					scope: this
				}
			}).proceed();
				}
			});
		}
		return this.deleteAllAction;
	}
});