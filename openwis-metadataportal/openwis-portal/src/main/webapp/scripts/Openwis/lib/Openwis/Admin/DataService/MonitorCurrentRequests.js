Ext.ns('Openwis.Admin.DataService');

Openwis.Admin.DataService.MonitorCurrentRequests = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.DataService.MonitorCurrentRequests.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		this.add(this.getFilterByUserGroupFormPanel());
		
		//Create Processed requests grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('MonitorCurrentRequests.Title.ProcessedRequest'),
			cls: 'administrationTitle2'
		}));
		// processed request filter
		this.add(this.getProcessedRequestFilterCombo());
		this.add(this.getMonitorCurrentProcessedRequestsGrid());
		
		this.add(new Ext.Container({
			html: Openwis.i18n('MonitorCurrentRequests.Title.Subscriptions'),
			cls: 'administrationTitle2',
			style: {
			    marginTop: '30px'
			}
		}));
		this.add(this.getMonitorCurrentSubscriptionsGrid());
		this.add(new Ext.Panel({
			items: [ this.getImportSubscriptionFormPanel() ],
			style: {
			    marginTop: '15px'
			}
		}));
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('MonitorCurrentRequests.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	getFilterByUserGroupFormPanel: function() {
	    if(!this.filterByUserGroupFormPanel) {
	        this.filterByUserGroupFormPanel = new Ext.form.FormPanel({
	            border: false,
	            buttonAlign: 'center'
	        });
	        this.filterByUserGroupFormPanel.add(this.getGroupsListbox());
	        this.filterByUserGroupFormPanel.addButton(new Ext.Button(this.getSearchAction()));
	    }
	    return this.filterByUserGroupFormPanel;
	},
	
	getGroupsListbox: function() {
	    if(!this.groupsListBox) {
	        this.groupsListBox = new Ext.ux.form.MultiSelect({
    			store: new Openwis.Data.JeevesJsonStore({
    				url: configOptions.locService+ '/xml.group.all',
    				idProperty: 'id',
    				autoLoad: true,
    				fields: [
    					{
    						name:'id'
    					},{
    						name:'name',
    						sortType: Ext.data.SortTypes.asUCString
    					},{
    						name:'global'
    					}
    				]
    			}),
    			fieldLabel: Openwis.i18n('MonitorCurrentRequests.FilterByGroups'),
    			displayField: 'name',
    			valueField: 'id',
    			width: 170,
    			listeners: { 
					afterrender: function (grid) {
						this.performSearch();
					},
					scope: this
				}
	        });
	    }
	    return this.groupsListBox;
	},
	
	getSearchAction: function() {
	    if(!this.searchAction) {
	        this.searchAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Search'),
				scope: this,
				handler: function() {
					this.performSearch();
				}
			});
		}
		return this.searchAction;
	},
	
	
	performSearch: function() {
		var selectedGroups = this.getGroupsListbox().getValue(); 
        this.getMonitorCurrentProcessedRequestsStore().setBaseParam(
            'groups',
            selectedGroups
        );
        this.getMonitorCurrentProcessedRequestsStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
        
        this.getMonitorCurrentSubscriptionsStore().setBaseParam(
            'groups',
            selectedGroups
        );
        this.getMonitorCurrentSubscriptionsStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
	},
	
	//------------------------------------------------------------- Monitor processed requests.
	
	getProcessedRequestFilterCombo: function() {
		if(!this.processedRequestFilterCombo) {
        	var columns = [];
        	columns.push(['ADHOC', Openwis.i18n('MonitorCurrentRequests.PRFilter.ADHOC')]);
        	columns.push(['SUBSCRIPTION', Openwis.i18n('MonitorCurrentRequests.PRFilter.SUBSCRIPTION')]);
        	columns.push(['BOTH', Openwis.i18n('MonitorCurrentRequests.PRFilter.BOTH')]);
            this.processedRequestFilterCombo = new Ext.form.ComboBox({
                fieldLabel: 'Request Filter',
                name: 'processedRequestFilterCombo',
                editable: false,
                mode: 'local',
                store: new Ext.data.ArrayStore({
                    id: '_prFilterStore',
                    fields: [
                        'filterKey', 
                        'filterName'
                    ],
                    data: columns
                }),
                valueField: 'filterKey',
                displayField: 'filterName',
                triggerAction: 'all',
                listeners: {
                    'select': function(){
                    	var selectedGroups = this.getGroupsListbox().getValue(); 
                    	this.getMonitorCurrentProcessedRequestsStore().setBaseParam(
                                'prFilter',
                                this.processedRequestFilterCombo.getValue()
                            );
                    	this.getMonitorCurrentProcessedRequestsStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
                    },
                    scope: this
                }
            });
            this.processedRequestFilterCombo.setValue('BOTH');
        }
        return this.processedRequestFilterCombo;
    },
		
	getMonitorCurrentProcessedRequestsGrid: function() {
		if(!this.monitorCurrentProcessedRequestsGrid) {
			this.monitorCurrentProcessedRequestsGrid = new Ext.grid.GridPanel({
			    id: 'currentProcessedRequestsGrid',
				height: 250,
                border: true,
                store: this.getMonitorCurrentProcessedRequestsStore(),
                view: this.getCurrentProcessedRequestsGridView(),
                loadMask: true,
                columns: [
                    {id:'statusImg', header:'', dataIndex:'status', renderer: Openwis.Common.Request.Utils.statusRendererImg, width: 30, sortable: false},
                    {id:'requestType', header:'', dataIndex:'requestType', renderer: Openwis.Common.Request.Utils.requestTypeRenderer, width: 20, sortable: false},
                    {id:'user', header: Openwis.i18n('MonitorCurrentRequests.User'), dataIndex:'user', sortable: true, hideable:false},
                    {id:'title', header: Openwis.i18n('MonitorCurrentRequests.ProductMetadata.Title'), dataIndex:'title', sortable: true, hideable:false, width: 100},
                    {id:'id', header: Openwis.i18n('MonitorCurrentRequests.Request.ID'), dataIndex:'id', sortable: true, hideable:false, width: 80},
                    {id:'processedRequestID', header: Openwis.i18n('MonitorCurrentRequests.ProcessRequest.ID'), dataIndex:'processedRequestID', sortable: true, hideable:false, width: 80},
                    {id:'status', header: Openwis.i18n('MonitorCurrentRequests.Status'), dataIndex: 'status', renderer: Openwis.Common.Request.Utils.statusRenderer, width: 50, sortable: true},
                    {id: 'size', header:  Openwis.i18n('MonitorCurrentRequests.Volume'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 80, sortable: true},
                    {id:'creationDate', header: Openwis.i18n('MonitorCurrentRequests.CreationDate'), dataIndex: 'creationDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: true}
                ],
                autoExpandColumn: 'title',
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect: false,
                    listeners: {
                        rowselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getViewProcessedRequestAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount() == 0);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getViewProcessedRequestAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount() == 0);
                        }
                    }
                }),
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getMonitorCurrentProcessedRequestsStore(),
                    displayInfo: true,
                    displayMsg: Openwis.i18n('MonitorCurrentRequests.Display.Range'),
                    emptyMsg: Openwis.i18n('MonitorCurrentRequests.No.Request')
                })
			});
			this.monitorCurrentProcessedRequestsGrid.addButton(new Ext.Button(this.getViewProcessedRequestAction()));
			this.monitorCurrentProcessedRequestsGrid.addButton(new Ext.Button(this.getDiscardProcessedRequestAction()));
		}
		return this.monitorCurrentProcessedRequestsGrid;
	},
	
	/**
     * Grid view to render a processed requests coming from subscription
     */
    getCurrentProcessedRequestsGridView: function() {
        if (!this.gridView) {
        	this.gridView = new Ext.grid.GridView({
			emptyText: '',
			getRowClass: function(record, index, rowParams, store) {
				// hide selection
				// if (record.json.requestType == 'SUBSCRIPTION') {
				// 	return 'subscriptionRequestGridRow';
				// }
			}
        	});
        }
        return this.gridView;
    },
    
	getMonitorCurrentProcessedRequestsStore: function() {
		if(!this.monitorCurrentProcessedRequestsStore) {
			this.monitorCurrentProcessedRequestsStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.monitor.current.requests',
				remoteSort: true,
                // reader configs
                root: 'rows',
				idProperty: 'id',
				fields: [
					{name: 'id', mapping: 'requestID'},
					{name: 'requestType', mapping: 'requestType'},
					{name: 'processedRequestID', mapping: 'processedRequestDTO.id'},
                    {name: 'user', mapping: 'userName', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'title', mapping: 'productMetadataURN', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'creationDate', mapping:'processedRequestDTO.creationDate'},
                    {name: 'status', mapping:'processedRequestDTO.status'},
                    {name: 'size', mapping:'processedRequestDTO.size'},
                    {name: 'dataSource', mapping:'productMetadataDataSource'},
                    {name: 'extractMode'},
                    {name:'urn', mapping:'productMetadataURN'}
				],
                sortInfo: {
                   field: 'id',
                   direction: 'DESC'
                }
			});
		}
		return this.monitorCurrentProcessedRequestsStore;
	},
	
	getViewProcessedRequestAction: function() {
	    if(!this.viewProcessedRequestAction) {
	        this.viewProcessedRequestAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.View'),
	            disabled: true,
	            iconCls: 'icon-view-processedrequest',
				scope: this,
				handler: function() {
				    var rec = this.getMonitorCurrentProcessedRequestsGrid().getSelectionModel().getSelected();
				    var requestType = rec.get('requestType');
				    if(requestType == 'ADHOC') {
				        var wizard = new Openwis.RequestSubscription.Wizard();
	                    wizard.initialize(rec.get('urn'), 'ADHOC', rec.get('extractMode') == 'CACHE', 'View', rec.get('id'), false);
				    } else if(requestType == 'SUBSCRIPTION')  {
					    var wizard = new Openwis.RequestSubscription.Wizard();
	                    wizard.initialize(rec.get('urn'), 'SUBSCRIPTION', rec.get('extractMode') == 'CACHE', 'Edit', rec.get('id'), false);
				    }
				}
	        });
	    }
	    return this.viewProcessedRequestAction;
	},
	
	getDiscardProcessedRequestAction: function() {
	    if(!this.discardProcessedRequestAction) {
	        this.discardProcessedRequestAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Discard'),
	            disabled: true,
	            iconCls: 'icon-discard-processedrequest',
				scope: this,
				handler: function() {
				    var selection = this.getMonitorCurrentProcessedRequestsGrid().getSelectionModel().getSelections();
					var params = {discardRequests: []};
					Ext.each(selection, function(item, index, allItems) {
						params.discardRequests.push({requestID: item.get('processedRequestID'), typeRequest: 'PROCESSED_REQUEST'});
					}, this);
					new Openwis.Handler.Remove({
            			url: configOptions.locService+ '/xml.discard.request',
            			params: params,
            			listeners: {
            				success: function() {
            					this.getMonitorCurrentProcessedRequestsStore().reload();
            				},
            				scope: this
            			}
            		}).proceed();
				}
	        });
	    }
	    return this.discardProcessedRequestAction;
	},
	
	//------------------------------------------------------------- Monitor subscriptions.
	
    getMonitorCurrentSubscriptionsGrid: function() {
		if(!this.monitorCurrentSubscriptionsGrid) {
			this.monitorCurrentSubscriptionsGrid = new Ext.grid.GridPanel({
    		    id: 'currentSubscriptionsGrid',
    			height: 250,
    			border: true,
    			store: this.getMonitorCurrentSubscriptionsStore(),
    			loadMask: true,
    			columns: [
    			    {id: 'statusImg', header:'', dataIndex:'state', renderer: Openwis.Common.Request.Utils.stateRendererImg, width: 50, sortable: false},
    				{id:'user', header: Openwis.i18n('MonitorCurrentRequests.User'), dataIndex:'user', sortable: true, hideable:false},
                    {id: 'title', header: Openwis.i18n('TrackMySubscriptions.ProductMetadata.Title'), dataIndex: 'urn', sortable: true},
                    {id: 'id', header: Openwis.i18n('TrackMySubscriptions.Subscription.ID'), dataIndex: 'id', width: 100, sortable: true},
                    {id: 'creationDate', header: Openwis.i18n('TrackMySubscriptions.CreationDate'), dataIndex: 'creationDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: true},
                    {id: 'lastProcessingDate', header:  Openwis.i18n('TrackMySubscriptions.LastEventDate'), dataIndex: 'lastProcessingDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 100, sortable: false}
    			],
    			autoExpandColumn: 'title',
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect: false,
                    listeners: {
                        rowselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getViewSubscriptionAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getSuspendSubscriptionAction().setDisabled(sm.getCount() != 1 || !record.get('valid') || !(record.get('state')=='ACTIVE') );
                            sm.grid.ownerCt.getResumeSubscriptionAction().setDisabled(sm.getCount() != 1 || !record.get('valid') ||  !(record.get('state')=='SUSPENDED'));
                            sm.grid.ownerCt.getDiscardSubscriptionAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getExportSubscriptionAction().setDisabled(sm.getCount() == 0);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getViewSubscriptionAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getSuspendSubscriptionAction().setDisabled(sm.getCount() != 1 || !record.get('valid') || !(record.get('state')=='ACTIVE'));
                            sm.grid.ownerCt.getResumeSubscriptionAction().setDisabled(sm.getCount() != 1 || !record.get('valid') || !(record.get('state')=='SUSPENDED'));
                            sm.grid.ownerCt.getDiscardSubscriptionAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getExportSubscriptionAction().setDisabled(sm.getCount() == 0);
                        }
                    }
                }),
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getMonitorCurrentSubscriptionsStore(),
                    displayInfo: true,
                    displayMsg: Openwis.i18n('TrackMySubscriptions.Display.Range'),
                    emptyMsg: Openwis.i18n('TrackMySubscriptions.No.Subscription')
                })
		    });
			this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getViewSubscriptionAction()));
			this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getSuspendSubscriptionAction()));
			this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getResumeSubscriptionAction()));
			this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getDiscardSubscriptionAction()));
			this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getExportSubscriptionAction()));
        }
        return this.monitorCurrentSubscriptionsGrid;
	},
	
	getMonitorCurrentSubscriptionsStore: function() {
	    if(!this.monitorCurrentSubscriptionsStore) {
    		this.monitorCurrentSubscriptionsStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions+ '/xml.monitor.current.subscriptions',
    			idProperty: 'id',
                remoteSort: true,
                root: 'rows',
                fields: [
                    {name: 'user', mapping: 'userName', sortType: Ext.data.SortTypes.asUCString},
    				{
                        name:'urn', mapping:'productMetadataURN'
                    },{
                        name:'title',
                        mapping:'productMetadataTitle',
						sortType: Ext.data.SortTypes.asUCString
                    },{
                        name:'creationDate', mapping:'startingDate'
                    },{
                        name:'id', mapping:'requestID'
                    },{
                        name:'lastProcessingDate', mapping:'lastProcessingDate'
                    },{
                        name:'valid'
                    },{
                        name:'state'
                    },
                    {
                        name: 'extractMode'
                    }
    			],
                sortInfo: {
                   field: 'title',
                   direction: 'ASC'
                }
    		});
		}
		return this.monitorCurrentSubscriptionsStore;
	},
	
	getViewSubscriptionAction: function() {
	    if(!this.viewSubscriptionAction) {
	        this.viewSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.ViewEdit'),
	            disabled: true,
	            iconCls: 'icon-view-processedrequest',
				scope: this,
				handler: function() {
				    var rec = this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelected();
					var wizard = new Openwis.RequestSubscription.Wizard();
	                wizard.initialize(rec.get('urn'), 'SUBSCRIPTION', rec.get('extractMode') == 'CACHE', 'Edit', rec.get('id'), false);
				}
	        });
	    }
	    return this.viewSubscriptionAction;
	},
	
	getSuspendSubscriptionAction: function() {
	    if(!this.suspendSubscriptionAction) {
	        this.suspendSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Suspend'),
	            disabled: true,
	            iconCls: 'icon-suspend-subscription',
				scope: this,
				handler: function() {
				    var rec = this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelected();
				    
            		new Openwis.Handler.Save({
            			url: configOptions.locService+ '/xml.set.subscription.state',
            			params: { 
            				requestID: rec.get('id'),
            				typeStateSet: 'SUSPEND'
            			},
            			listeners: {
            				success: function() {
            					this.getMonitorCurrentSubscriptionsStore().reload();
            				},
            				scope: this
            			}
            		}).proceed();
				}
	        });
	    }
	    return this.suspendSubscriptionAction;
	},
	
	getResumeSubscriptionAction: function() {
	    if(!this.resumeSubscriptionAction) {
	        this.resumeSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Resume'),
	            disabled: true,
	            iconCls: 'icon-resume-subscription',
				scope: this,
				handler: function() {
				    var rec = this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelected();
				    
				    new Openwis.Handler.Save({
            			url: configOptions.locService+ '/xml.set.subscription.state',
            			params: { 
            				requestID: rec.get('id'),
            				typeStateSet: 'RESUME'
            			},
            			listeners: {
            				success: function() {
            					this.getMonitorCurrentSubscriptionsStore().reload();
            				},
            				scope: this
            			}
            		}).proceed();
				}
	        });
	    }
	    return this.resumeSubscriptionAction;
	},
	
	getDiscardSubscriptionAction: function() {
	    if(!this.discardSubscriptionAction) {
	        this.discardSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Discard'),
	            disabled: true,
	            iconCls: 'icon-discard-subscription',
				scope: this,
				handler: function() {
				   	var selection = this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelections();
					var params = {discardRequests: []};
					Ext.each(selection, function(item, index, allItems) {
						params.discardRequests.push({requestID: item.get('id'), typeRequest: 'SUBSCRIPTION'});
					}, this);
					new Openwis.Handler.Remove({
            			url: configOptions.locService+ '/xml.discard.request',
            			params: params,
            			listeners: {
            				success: function() {
            					this.getMonitorCurrentSubscriptionsStore().reload();
            				},
            				scope: this
            			}
            		}).proceed();
				}
	        });
	    }
	    return this.discardSubscriptionAction;
	},
	
	getExportSubscriptionAction: function() {
	    if(!this.exportSubscriptionAction) {
	        this.exportSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Export'),
	            disabled: true,
				scope: this,
				handler: function() {
					var selection = this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelections();
					var params = {exportRequests: []};
					Ext.each(selection, function(item, index, allItems) {
						params.exportRequests.push({requestID: item.get('id'), typeRequest: 'SUBSCRIPTION'});
					}, this);
                	if (params.exportRequests.length > 0 ) {   		
                        window.open(configOptions.locService + 
                        		"/xml.monitor.current.subscriptions.export?subscriptionId=" + params.exportRequests[0].requestID,'_blank', '');
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
	    return this.exportSubscriptionAction;
	},
	
	getImportSubscriptionFormPanel: function() {
	    if(!this.importSubscriptionFormPanel) {
	        this.importSubscriptionFormPanel = new Ext.form.FormPanel({
	            border: false,
	            fileUpload : true,
	        	bodyStyle : ' margin: 10px 10px 0px 10px; ',
	        	errorReader: new Ext.data.XmlReader({
                    record : 'field',
                    success: '@success'
                }, [
                    'id', 'msg'
                ])
	        });
	        
	        var newFile = new Ext.ux.form.FileUploadField(
	    		    {
	        		    xtype: 'fileuploadfield',
	                    allowBlank : false,
	                    buttonCfg: {
	                        text: Openwis.i18n('Common.Btn.Browse')
	                    },
	                    fieldLabel: Openwis.i18n('MonitorCurrentRequests.Import.Label'),
	                    width: 360
	                }
			    );
	        
	        this.importSubscriptionFormPanel.add(newFile);
	        this.importSubscriptionFormPanel.addButton(new Ext.Button(this.getImportSubscriptionAction()));
	    }
	    return this.importSubscriptionFormPanel;
	},
	
	getImportSubscriptionAction: function() {
	    if(!this.importSubscriptionAction) {
	        this.importSubscriptionAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Insert'),
	            scope: this,
				handler: function() {
					if (this.importSubscriptionFormPanel.getForm().isValid()) {
						this.importSubscriptionFormPanel.getForm().submit({
 	                        url : configOptions.locService+ '/xml.monitor.current.subscriptions.import',
 	                        scope : this,
 	                        params: {},
 	                        success : function(fp, action) {
 	                            var jsonData = fp.errorReader.xmlData.getElementsByTagName("message")[0].childNodes[0].nodeValue;
 	                            var result = Ext.decode(jsonData);
 	                            if (result.result){
 	 	                            Openwis.Utils.MessageBox.displaySaveSuccessful();
 	                            }else{
 	 	                            Openwis.Utils.MessageBox.displayErrorMsg(result.message);
 	                            }
            					this.getMonitorCurrentSubscriptionsStore().reload();
 	                        },
 	                        failure : function(response) {
 	                            Openwis.Utils.MessageBox.displayInternalError();
 	                        }
 	                    });
					}
                }
	        });
	    }
	    return this.importSubscriptionAction;
	}
	
});