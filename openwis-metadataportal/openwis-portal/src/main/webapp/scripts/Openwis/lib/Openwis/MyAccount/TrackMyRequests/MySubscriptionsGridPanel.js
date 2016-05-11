Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel = Ext.extend(Ext.grid.GridPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    style: {
                margin: '10px 30px 10px 30px'
            },
			height: 250,
			border: true,
			store: this.getSubscriptionsStore(),
			loadMask: true,
			columns: [
			    {id: 'statusImg', header:'', dataIndex:'state', renderer: Openwis.Common.Request.Utils.stateRendererImg, width: 50, sortable: false},
				{id: 'deployment', header: Openwis.i18n('TrackMySubscriptions.Deployment'), dataIndex: 'deployment', sortable: false, renderer: Openwis.Common.Request.Utils.backupRenderer, hidden: this.isLocal},
                {id: 'title', header: Openwis.i18n('TrackMySubscriptions.ProductMetadata.Title'), dataIndex: 'title', sortable: true, renderer: Openwis.Common.Request.Utils.htmlSafeRenderer },
                {id: 'backup', header: Openwis.i18n('TrackMySubscriptions.ProductMetadata.Backup'), dataIndex: 'backup', sortable: true},
                {id: 'id', header: Openwis.i18n('TrackMySubscriptions.Subscription.ID'), dataIndex: 'id', width: 100, sortable: true},
                {id: 'startingDate', header: Openwis.i18n('TrackMySubscriptions.StartDate'), dataIndex: 'startingDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: true},
                {id: 'lastProcessingDate', header:  Openwis.i18n('TrackMySubscriptions.LastEventDate'), dataIndex: 'lastProcessingDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: false}
			],
			autoExpandColumn: 'title',
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
                        sm.grid.getSuspendAction().setDisabled(sm.getCount() != 1 || !record.get('valid') || !(record.get('state')=='ACTIVE') );
                        sm.grid.getResumeAction().setDisabled(sm.getCount() != 1 || !record.get('valid') ||  !(record.get('state')=='SUSPENDED'));
                        sm.grid.getDiscardAction().setDisabled(sm.getCount() == 0);
                        sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount() != 1);
                    },
                    rowdeselect: function (sm, rowIndex, record) {
                        sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getSuspendAction().setDisabled(sm.getCount() != 1 || !record.get('valid') || !(record.get('state')=='ACTIVE'));
                        sm.grid.getResumeAction().setDisabled(sm.getCount() != 1 || !record.get('valid') || !(record.get('state')=='SUSPENDED'));
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
		var viewSubscription;
		Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel.superclass.initComponent.apply(this, arguments);
		
		if(this.isLocal) {
    		this.addButton(new Ext.Button(this.getViewSubscriptionAction()));
            this.addButton(new Ext.Button(this.getViewMetadataAction()));
            this.addButton(new Ext.Button(this.getSuspendAction()));
            this.addButton(new Ext.Button(this.getResumeAction()));
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
                        name:'title',
                        mapping:'productMetadataTitle',
						sortType: Ext.data.SortTypes.asUCString
                    },{
                        name:'startingDate', mapping:'startingDate'
                    },{
                        name:'id', mapping:'requestID'
                    },{
                        name:'lastProcessingDate', mapping:'lastProcessingDate'
                    },{
                        name:'valid'
                    },{
                        name:'extractMode'
                    },{
                        name:'backup'
                    },{
                        name:'state'
                    }
    			],
                sortInfo: {
                   field: 'title',
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
	                wizard.initialize(rec.get('urn'), 'SUBSCRIPTION', rec.get('extractMode') == 'CACHE', 'Edit', rec.get('id'), false);
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
	
	getSuspendAction: function() {
	    if(!this.suspendAction) {
	        this.suspendAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Suspend'),
	            disabled: true,
	            iconCls: 'icon-suspend-subscription',
				scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    
            		new Openwis.Handler.Save({
            			url: configOptions.locService+ '/xml.set.subscription.state',
            			params: { 
            				requestID: rec.get('id'),
            				typeStateSet: 'SUSPEND'
            			},
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
	    return this.suspendAction;
	},
	
	getResumeAction: function() {
	    if(!this.resumeAction) {
	        this.resumeAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Resume'),
	            disabled: true,
	            iconCls: 'icon-resume-subscription',
				scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    
				    new Openwis.Handler.Save({
            			url: configOptions.locService+ '/xml.set.subscription.state',
            			params: { 
            				requestID: rec.get('id'),
            				typeStateSet: 'RESUME'
            			},
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
	    return this.resumeAction;
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
						params.discardRequests.push({requestID: item.get('id'), typeRequest: 'SUBSCRIPTION'});
					}, this);
					
					new Openwis.Handler.Remove({
            			url: configOptions.locService+ '/xml.discard.request',
            			params: params,
            			listeners: {
            				success: function() {
						if (this.userAlarmGridPanel) {
							this.userAlarmGridPanel.reload();
						}
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
	}
});