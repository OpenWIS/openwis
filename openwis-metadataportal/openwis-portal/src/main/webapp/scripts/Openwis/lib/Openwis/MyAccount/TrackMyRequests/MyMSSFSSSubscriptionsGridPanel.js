Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.MyMSSFSSSubscriptionsGridPanel = Ext.extend(Ext.grid.GridPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
			height: 250,
			border: true,
			store: this.getSubscriptionsStore(),
			loadMask: true,
			columns: [
				{id: 'deployment', header: Openwis.i18n('TrackMySubscriptions.Deployment'), dataIndex: 'deployment', sortable: false, renderer: Openwis.Common.Request.Utils.backupRenderer, hidden: this.isLocal},
                {id: 'urn', header: Openwis.i18n('TrackMySubscriptions.ProductMetadata.Title'), dataIndex: 'urn', sortable: true},
                {id: 'channel', header: Openwis.i18n('TrackMySubscriptions.MSSFSS.Channel'), dataIndex: 'channel', sortable: true},
                {id: 'id', header: Openwis.i18n('TrackMySubscriptions.Subscription.ID'), dataIndex: 'id', width: 100, sortable: true},
                {id: 'creationDate', header: Openwis.i18n('TrackMySubscriptions.CreationDate'), dataIndex: 'creationDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: true},
                {id: 'lastProcessingDate', header:  Openwis.i18n('TrackMySubscriptions.LastEventDate'), dataIndex: 'lastProcessingDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: true},
                {id: 'state', header:  Openwis.i18n('TrackMySubscriptions.MSSFSSState'), dataIndex: 'state', renderer: this.routingStateRenderer, width: 100, sortable: true}
			],
			autoExpandColumn: 'urn',
            listeners: { 
                afterrender: function (grid) {
                   if(this.isLocal) {
                       grid.loadMask.show();
                       grid.getStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
                   }
                },
                scope:this
            },
            sm: new Ext.grid.RowSelectionModel({
                singleSelect: false,
                listeners: {
                    rowselect: function (sm, rowIndex, record) {
                        sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getDiscardAction().setDisabled(sm.getCount() == 0);
                        sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount() != 1);
                    },
                    rowdeselect: function (sm, rowIndex, record) {
                        sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getDiscardAction().setDisabled(sm.getCount() == 0);
                        sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount() != 1);
                    }
                }
            }),
            // paging bar on the bottom
            bbar: new Ext.PagingToolbar({
                pageSize: Openwis.Conf.PAGE_SIZE,
                store: this.getSubscriptionsStore(),
                displayInfo: true,
                displayMsg: Openwis.i18n('TrackMySubscriptions.Display.Range'),
                emptyMsg: Openwis.i18n('TrackMySubscriptions.No.Subscription')
            })
		});
		Openwis.MyAccount.TrackMyRequests.MyMSSFSSSubscriptionsGridPanel.superclass.initComponent.apply(this, arguments);
		
		if(this.isLocal) {
    		this.addButton(new Ext.Button(this.getViewSubscriptionAction()));
            this.addButton(new Ext.Button(this.getViewMetadataAction()));
            this.addButton(new Ext.Button(this.getDiscardAction()));
        } else {
            this.addButton(new Ext.Button(this.getGoToDeploymentAction()));
        }
	},
	
	getSubscriptionsStore: function() {
	    if(!this.subscriptionsStore) {
    		this.subscriptionsStore = new Openwis.Data.JeevesJsonStore({
    			url: this.url,
    			idProperty: 'id',
                remoteSort: true,
                root: 'rows',
                fields: [
                    {
                        name:'deployment'
                    },
    				{
                        name:'urn', mapping:'productMetadataURN'
                    },{
                        name:'channel',
                        mapping:'primaryDissemination.o'
                    },{
                        name:'creationDate', mapping:'startingDate'
                    },{
                        name:'id', mapping:'requestID'
                    },{
                        name:'lastProcessingDate', mapping:'lastProcessingDate'
                    },{
                        name:'state'
                    }
    			],
                sortInfo: {
                   field: 'urn',
                   direction: 'ASC'
                }
    		});
		}
		return this.subscriptionsStore;
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
	                wizard.initialize(rec.get('urn'), 'SUBSCRIPTION', true, 'Edit', rec.get('id'), true);
				}
	        });
	    }
	    return this.viewSubscriptionAction;
	},

	getViewMetadataAction: function() {
	    if(!this.viewMetadataAction) {
	        this.viewMetadataAction = new Ext.Action({
	            text: Openwis.i18n('TrackMySubscriptions.Action.ViewMetadata'),
	            disabled: true,
	            iconCls: 'icon-viewmd-subscription',
				scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    doShowMetadataByUrn(rec.get('urn'), rec.get('title'));
				}
	        });
	    }
	    return this.viewMetadataAction;
	},
	
	getDiscardAction: function() {
	    if(!this.discardAction) {
	        this.discardAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Discard'),
	            disabled: true,
	            iconCls: 'icon-discard-subscription',
				scope: this,
				handler: function() {
					//Get the id to delete.
					var selection = this.getSelectionModel().getSelections();
					var params = {discardRequests: []};
					Ext.each(selection, function(item, index, allItems) {
						params.discardRequests.push({requestID: item.get('id'), typeRequest: 'ROUTING'});
					}, this);
					
					new Openwis.Handler.Remove({
            			url: configOptions.locService+ '/xml.discard.request',
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
	    return this.discardAction;
	},
	
	getGoToDeploymentAction: function() {
	    if(!this.goToDeploymentAction) {
	        this.goToDeploymentAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.GoToDeployment'),
	            disabled: true,
				scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    var deployment = rec.get('deployment');
				    if(deployment) {
    					window.open(deployment.url);
				    }
				}
	        });
	    }
	    return this.goToDeploymentAction;
	},
	
	routingStateRenderer: function(val) {
	    if(val == 'ACTIVE') {
	        return Openwis.i18n('TrackMySubscriptions.MSSFSSState.Active');
	    } else {
	        return Openwis.i18n('TrackMySubscriptions.MSSFSSState.Pending');
	    }
	}
});