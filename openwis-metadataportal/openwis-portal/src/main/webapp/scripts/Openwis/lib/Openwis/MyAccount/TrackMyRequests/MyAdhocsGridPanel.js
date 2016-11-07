Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel = Ext.extend(Ext.grid.GridPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            },
			height: 250,
			border: true,
			store: this.getRequestsStore(),
			loadMask: true,
			columns: [
				{id: 'statusImg', header:'', dataIndex:'status', renderer: Openwis.Common.Request.Utils.statusRendererImg, width: 50, sortable: false},
				{id: 'deployment', header: Openwis.i18n('TrackMyRequests.Deployment'), dataIndex: 'deployment', renderer: Openwis.Common.Request.Utils.backupRenderer, sortable: false, hidden: this.isLocal},
                {id: 'title', header: Openwis.i18n('TrackMyRequests.ProductMetadata.Title'), dataIndex: 'title', renderer: Openwis.Common.Request.Utils.htmlSafeRenderer, sortable: true },
                {id: 'creationDate', header: Openwis.i18n('TrackMyRequests.ProductMetadata.CreationDate'), dataIndex: 'creationDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: true},
                {id: 'id', header: Openwis.i18n('TrackMyRequests.Request.ID'), dataIndex: 'id', width: 100, sortable: true},
                {id: 'status', header: Openwis.i18n('TrackMyRequests.Request.Status'), dataIndex: 'status', renderer: Openwis.Common.Request.Utils.statusRenderer, width: 100, sortable: true},
                {id: 'size', header:  Openwis.i18n('TrackMyRequests.Request.Volume'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 100, sortable: false}
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
                        sm.grid.getViewRequestAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getDownloadAction().setDisabled(sm.getCount() != 1 || record.get('downloadUrl') == null || record.get('downloadUrl') == "");
                        sm.grid.getDiscardAction().setDisabled(sm.getCount() == 0);
                        sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount() != 1);
                    },
                    rowdeselect: function (sm, rowIndex, record) {
                        sm.grid.getViewRequestAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                        sm.grid.getDownloadAction().setDisabled(sm.getCount() != 1 || record.get('downloadUrl') == null || record.get('downloadUrl') == "");
                        sm.grid.getDiscardAction().setDisabled(sm.getCount() == 0);
                        sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount() != 1);
                    }
                }
            }),
            // paging bar on the bottom
            bbar: new Ext.PagingToolbar({
                pageSize: Openwis.Conf.PAGE_SIZE,
                store: this.getRequestsStore(),
                displayInfo: true,
                displayMsg: Openwis.i18n('TrackMyRequests.Display.Range'),
                emptyMsg: Openwis.i18n('TrackMyRequests.No.Request')
            })
		});
		Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel.superclass.initComponent.apply(this, arguments);
		
		if(this.isLocal) {
    		this.addButton(new Ext.Button(this.getViewRequestAction()));
		    this.addButton(new Ext.Button(this.getViewMetadataAction()));
		    this.addButton(new Ext.Button(this.getDownloadAction()));
		    this.addButton(new Ext.Button(this.getDiscardAction()));
        } else {
            this.addButton(new Ext.Button(this.getGoToDeploymentAction()));
        }
	},
	
	getRequestsStore: function() {
	    if(!this.requestsStore) {
    		var url = configOptions.locService;
    		if(this.isLocal) {
    			url += '/xml.follow.my.adhocs';
    		} else {
    			url += '/xml.follow.my.remote.adhocs';
    		}
    	
    		this.requestsStore = new Openwis.Data.JeevesJsonStore({
    			url: url,
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
                        name:'creationDate', mapping:'processedRequestDTO.creationDate'
                    },{
                        name:'id', mapping:'requestID'
                    },{
                        name:'status', mapping:'processedRequestDTO.status'
                    },{
                        name:'downloadUrl', mapping:'processedRequestDTO.url'
                    },{
                        name:'size', mapping:'processedRequestDTO.size'
                    },{
                        name: 'requestType'
                    },{
                        name: 'extractMode'
                    }
    			],
                sortInfo: {
                   field: 'title',
                   direction: 'ASC'
                }
    		});
		}
		return this.requestsStore;
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
	                wizard.initialize(rec.get('urn'), 'ADHOC', rec.get('extractMode') == 'CACHE', 'View', rec.get('id'), false);
				}
	        });
	    }
	    return this.viewRequestAction;
	},
	
	getViewMetadataAction: function() {
	    if(!this.viewMetadataAction) {
	        this.viewMetadataAction = new Ext.Action({
	            text: Openwis.i18n('TrackMyRequests.Action.ViewMetadata'),
	            disabled: true,
	            iconCls: 'icon-viewmd-adhoc',
				scope: this,
				handler: function() {
				    var rec = this.getSelectionModel().getSelected();
				    doShowMetadataByUrn(rec.get('urn'), rec.get('title'));
				}
	        });
	    }
	    return this.viewMetadataAction;
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
	
	getDiscardAction: function() {
	    if(!this.discardAction) {
	        this.discardAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Discard'),
	            disabled: true,
	            iconCls: 'icon-discard-adhoc',
				scope: this,
				handler: function() {
				    var selection = this.getSelectionModel().getSelections();
					var params = {discardRequests: []};
					Ext.each(selection, function(item, index, allItems) {
						params.discardRequests.push({requestID: item.get('id'), typeRequest: 'ADHOC'});
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