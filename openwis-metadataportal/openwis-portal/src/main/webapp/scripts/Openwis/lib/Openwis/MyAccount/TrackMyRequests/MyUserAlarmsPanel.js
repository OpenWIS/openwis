Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel = Ext.extend(Ext.grid.GridPanel, {

	initComponent: function() {
		var requestTitle;
		var viewRequestAction;

		if (this.isSubscription) {
			requestTitle = "Subscription ID";
			viewRequestAction = this.getViewSubscriptionAction();
		} else {
			requestTitle = "Request ID";
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
				{id: 'date', header: "Raised on", renderer: Openwis.Utils.Date.formatDateTimeUTC, dataIndex: "date", width: 100},
				{id: 'alarmType', header: "Type", dataIndex: "alarmType", width: 100},
				{id: 'requestId', header: requestTitle, dataIndex: "requestId", width: 100},
				{id: 'message', header: "Message", dataIndex: "message", width: 100}
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
                displayMsg: 'Displaying alarm {0} - {1} of {2}',
                emptyMsg: 'No alarms to display'
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
                        name:'message'
                    }
			],
                sortInfo: {
                   field: 'id',
                   direction: 'ASC'
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
				    alert("Download data from staging post");
				}
	        });
	    }
	    return this.downloadAction;
	},

	getAcknowledgeAction: function() {
		if (!this.acknowledgeAction) {
			this.acknowledgeAction = new Ext.Action({
				text: "Acknowledge",
				disabled: true,
				scope: this,
				handler: function() {
					alert("Alarms acknowledged");
				}
			});
		}
		return this.acknowledgeAction;
	},

	getAcknowledgeAllAction: function() {
		if (!this.acknowledgeAllAction) {
			this.acknowledgeAllAction = new Ext.Action({
				text: "Acknowledge All",
				scope: this,
				handler: function() {
					alert("All alarms acknowledged");
				}
			});
		}
		return this.acknowledgeAllAction;
	},

	getViewRequestAction: function() {
	    if(!this.viewRequestAction) {
	        this.viewRequestAction = new Ext.Action({
	            text: Openwis.i18n('TrackMyRequests.Action.ViewRequest'),
	            disabled: true,
	            iconCls: 'icon-view-adhoc',
	            scope: this,
				handler: function() {
					alert("View request");
					/*
				    var rec = this.getSelectionModel().getSelected();
				    var wizard = new Openwis.RequestSubscription.Wizard();
	                wizard.initialize(rec.get('urn'), 'ADHOC', rec.get('extractMode') == 'CACHE', 'View', rec.get('id'), false);
	                */
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
					alert("View subscription");
					/*
				    var rec = this.getSelectionModel().getSelected();
				    var wizard = new Openwis.RequestSubscription.Wizard();
	                wizard.initialize(rec.get('urn'), 'ADHOC', rec.get('extractMode') == 'CACHE', 'View', rec.get('id'), false);
	                */
				}
	        });
	    }
	    return this.viewSubscriptionAction;
	}
});