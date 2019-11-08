Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel = Ext.extend(Ext.grid.GridPanel, {

	initComponent: function() {
		var requestTitle;
		var viewRequestAction;

		if (this.isSubscription) {
			requestTitle = Openwis.i18n('TrackMySubscriptions.Subscription.ID');
			viewRequestAction = this.getViewSubscriptionAction();
		} else {
			requestTitle = Openwis.i18n('TrackMyRequests.Request.ID');
			viewRequestAction = this.getViewRequestAction();
		}

		Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            },
			height: 250,
			border: true,
			store: this.getUserAlarmStore(),
			loadMask: true,
			columns: [
				{id: 'date', header: Openwis.i18n('TrackMyRequests.UserAlarms.Date'), renderer: Openwis.Utils.Date.formatDateTimeUTC, dataIndex: "date", width: 100, sortable: true },
				{id: 'requestId', header: requestTitle, dataIndex: "requestId", width: 100, sortable: true },
				{id: 'message', header: Openwis.i18n('TrackMyRequests.UserAlarms.Message'), renderer: this.renderMessage.createDelegate(this), dataIndex: "message", width: 100, sortable: true }
			],
			autoExpandColumn: 'message',
            listeners: {
                afterrender: function (grid) {
                   grid.loadMask.show();
                   grid.getStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
                },
                scope:this
            },
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: false,
                listeners: {
                    rowselect: function (sm, rowIndex, record) {
			sm.grid.getAcknowledgeAction().setDisabled(sm.getCount() == 0);
			sm.grid.getDownloadAction().setDisabled(sm.getCount() != 1);
			viewRequestAction.setDisabled(sm.getCount() != 1);
                    },
                    rowdeselect: function (sm, rowIndex, record) {
			sm.grid.getAcknowledgeAction().setDisabled(sm.getCount() == 0);
			sm.grid.getDownloadAction().setDisabled(sm.getCount() != 1);
			viewRequestAction.setDisabled(sm.getCount() != 1);
                    }
                }
            }),
            // paging bar on the bottom
            bbar: new Ext.PagingToolbar({
                pageSize: Openwis.Conf.PAGE_SIZE,
                store: this.getUserAlarmStore(),
                displayInfo: true,
                displayMsg: Openwis.i18n('TrackMyRequests.UserAlarms.Display.Range'),
                emptyMsg: Openwis.i18n('TrackMyRequests.UserAlarms.No.Alarms'),
            })
		});
		Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel.superclass.initComponent.apply(this, arguments);
		this.addButton(new Ext.Button(viewRequestAction));
		this.addButton(new Ext.Button(this.getDownloadAction()));
		this.addButton(new Ext.Button(this.getAcknowledgeAction()));
		this.addButton(new Ext.Button(this.getAcknowledgeAllAction()));
	},

	getUserAlarmStore: function() {
	    if(!this.userAlarmStore) {
		var url;

		if (this.isSubscription) {
			url = configOptions.locService + "/xml.useralarms.getsubscriptions";
		} else {
			url = configOptions.locService + "/xml.useralarms.getrequests";
		}

		this.userAlarmStore = new Openwis.Data.JeevesJsonStore({
			url: url,
			idProperty: 'id',
                remoteSort: true,
                root: 'rows',
                fields: [
                    {
                        name:'id'
                    },{
                        name:'date'
                    },{
                        name:'requestId'
                    },{
                        name:'alarmType'
                    },{
                        name:'message',
			sortType: Ext.data.SortTypes.asUCString
                    },{
			name:'urn'
                    },{
			name:'extractMode'
                    },{
			name:'downloadUrl'
                    }
			],
                sortInfo: {
                   field: 'date',
                   direction: 'DESC'
                },
                listeners: {
			load: function(store, records, successful, operation, eOpts) {
				this.getAcknowledgeAllAction().setDisabled(store.getCount() == 0);
			},
			scope: this
                }
		});
		}
		return this.userAlarmStore;
	},

	getDownloadAction: function() {
	    if(!this.downloadAction) {
	        this.downloadAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Download'),
	            disabled: true,
	            iconCls: 'icon-download-adhoc',
				scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    window.open(rec.get('downloadUrl'));
				}
	        });
	    }
	    return this.downloadAction;
	},

	getAcknowledgeAction: function() {
		if (!this.acknowledgeAction) {
			this.acknowledgeAction = new Ext.Action({
				text: Openwis.i18n('TrackMyRequests.UserAlarms.Action.Acknowledge'),
				iconCls: 'icon-discard-adhoc',
				disabled: true,
				scope: this,
				handler: function() {
				    var selection = this.getSelectionModel().getSelections();
					var params = {alarmIds: []};
					Ext.each(selection, function(item, index, allItems) {
						params.alarmIds.push(item.get('id'));
					}, this);

					new Openwis.Handler.Remove({
				url: configOptions.locService+ '/xml.useralarms.acknowledge',
				params: params,
				listeners: {
					success: function() {
						this.getStore().reload();
					},
					scope: this
				}
			}).proceed();
				}
			});
		}
		return this.acknowledgeAction;
	},

	getAcknowledgeAllAction: function() {
		if (!this.acknowledgeAllAction) {
			this.acknowledgeAllAction = new Ext.Action({
				text: Openwis.i18n('TrackMyRequests.UserAlarms.Action.AcknowledgeAll'),
				iconCls: 'icon-discard-adhoc',
				scope: this,
				disabled: true,
				handler: function() {
					var params = { subscription: this.isSubscription };
					new Openwis.Handler.Remove({
				url: configOptions.locService+ '/xml.useralarms.acknowledgeall',
				params: params,
				listeners: {
					success: function() {
						this.getStore().reload();
					},
					scope: this
				}
			}).proceed();
				}
			});
		}
		return this.acknowledgeAllAction;
	},

	reload: function() {
		this.getStore().reload();
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


	getViewRequestAction: function() {
	    if(!this.viewRequestAction) {
	        this.viewRequestAction = new Ext.Action({
	            text: Openwis.i18n('TrackMyRequests.Action.ViewRequest'),
	            disabled: true,
	            iconCls: 'icon-view-adhoc',
	            scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    var wizard = new Openwis.RequestSubscription.Wizard();
	                wizard.initialize(rec.get('urn'), 'ADHOC', rec.get('extractMode') == 'CACHE', 'View', rec.get('requestId'), false);
				}
	        });
	    }
	    return this.viewRequestAction;
	},

	getViewSubscriptionAction: function() {
	    if(!this.viewSubscriptionAction) {
	        this.viewSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('TrackMySubscriptions.Action.ViewEditSubscription'),
	            disabled: true,
	            iconCls: 'icon-view-subscription',
	            scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    var wizard = new Openwis.RequestSubscription.Wizard();
	                wizard.initialize(rec.get('urn'), 'SUBSCRIPTION', rec.get('extractMode') == 'CACHE', 'Edit', rec.get('requestId'), false);

				}
	        });
	    }
	    return this.viewSubscriptionAction;
	}
});