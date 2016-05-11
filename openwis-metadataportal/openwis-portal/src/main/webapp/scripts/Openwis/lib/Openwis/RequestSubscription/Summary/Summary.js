

Ext.ns('Openwis.RequestSubscription');

Openwis.RequestSubscription.Summary = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
		    width: 650,
			layout:'table',
			layoutConfig: {
			    columns: 1
			},
			style: {
                padding: '10px 10px 10px 30px'
            }
		});
		Openwis.RequestSubscription.Summary.superclass.initComponent.apply(this, arguments);
		
	},
	
	initializeAndShow: function() {
	    //Request information.
	    this.add(this.getRequestInfo());
	    
	    //Processed requests grid panel.
	    this.add(this.getProcessedRequestsGridPanel());
	},
	
	getRequestInfo: function() {
	    if(!this.requestInfo) {
	        this.requestInfo = new Ext.form.FormPanel({
	            border: false,
				labelWidth: 120,
				items: [
				    {xtype: 'displayfield', value: this.config.userName, fieldLabel: Openwis.i18n('RequestSubscription.Summary.User')},
				    {xtype: 'displayfield', value: this.config.requestID, fieldLabel: Openwis.i18n('RequestSubscription.Summary.RequestID')},
				    {xtype: 'displayfield', value: this.config.extractMode, fieldLabel: Openwis.i18n('RequestSubscription.Summary.DataSource')},
				    {xtype: 'displayfield', value: Ext.util.Format.htmlEncode(this.config.productMetadataURN), fieldLabel: Openwis.i18n('RequestSubscription.Summary.ProductMetadataURN')},
				    {xtype: 'displayfield', value: Ext.util.Format.htmlEncode(this.config.productMetadataTitle), fieldLabel: Openwis.i18n('RequestSubscription.Summary.ProductMetadataTitle')}
				]
	        });
	    }
	    return this.requestInfo;
	},
	
	getProcessedRequestsGridPanel: function(){
	    if(!this.processedRequestsGridPanel) {
	        this.processedRequestsGridPanel = new Ext.grid.GridPanel({
	        	id: 'processedRequestsGridPanel',
    			height: 250,
    			width: 550,
    			border: true,
    			store: this.getProcessedRequestsStore(),
    			loadMask: true,
    			columns: [
    				{id: 'statusImg', header:'', dataIndex:'status', renderer: Openwis.Common.Request.Utils.processedRequestStatusRendererImg, width: 40, sortable: false},
                    {id: 'creationDateUtc', header: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.CreationDate'), dataIndex: 'creationDateUtc', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 110, sortable: true},
                    {id: 'submittedDisseminationDateUtc', header: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.SubmittedDisseminationDate'), dataIndex: 'submittedDisseminationDateUtc', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 120, sortable: true},
                    {id: 'completedDateUtc', header: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.CompletedDate'), dataIndex: 'completedDateUtc', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 110, sortable: true},
                    {id: 'message', header: Openwis.i18n('RequestSubscription.Summary.Message'), dataIndex:'message', renderer: Openwis.Utils.Tooltip.Display , width: 100, sortable: true},
                    {id: 'size', header:  Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.Size'), dataIndex: 'size', renderer: Openwis.Common.Request.Utils.sizeRenderer, width: 100, sortable: true}
    			],
    			autoExpandColumn: 'submittedDisseminationDateUtc',
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
                            sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getDownloadAction().setDisabled(sm.getCount() != 1 || record.get('uri') == null || record.get('uri') == "");
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getDownloadAction().setDisabled(sm.getCount() != 1 || record.get('uri') == null || record.get('uri') == "");
                        }
                    }
                }),
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getProcessedRequestsStore(),
                    displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.Range'),
                    emptyMsg: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.No.Elements')
                })
	        });
	        this.processedRequestsGridPanel.addButton(new Ext.Button(this.getDownloadAction()));
	        if(this.config.isSubscription) {
    	        this.processedRequestsGridPanel.addButton(new Ext.Button(this.getDiscardProcessedRequestAction()));
	        }
	    }
	    return this.processedRequestsGridPanel;
	},
	
	getProcessedRequestsStore: function() {
	    if(!this.processedRequestsStore) {
    		this.processedRequestsStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService + '/xml.processed.requests.all',
    			idProperty: 'id',
                remoteSort: true,
                root: 'rows',
                totalProperty: 'total',
                fields: [
                    {
                        name:'id'
                    },{
                        name:'message'
                    },{
                        name:'creationDateUtc', mapping:'creationDateUtc'
                    },{
                        name:'submittedDisseminationDateUtc', mapping:'submittedDisseminationDateUtc'
                    },{
                        name:'completedDateUtc', mapping:'completedDateUtc'
                    },{
                        name:'status', mapping: 'requestResultStatus'
                    },{
                        name:'uri'
                    },{
                        name:'size'
                    }
    			],
                sortInfo: {
                   field: 'creationDateUtc',
                   direction: 'DESC'
                }
    		});
    		this.processedRequestsStore.setBaseParam("id", this.config.requestID);
		}
		return this.processedRequestsStore;
	},
	
	//------------------------------------------------------------------------------ Actions
	
	getDownloadAction: function() {
	    if(!this.downloadAction) {
	        this.downloadAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Download'),
	            disabled: true,
	            iconCls: 'icon-download-adhoc',
				scope: this,
				handler: function() {
				    var rec = this.getProcessedRequestsGridPanel().getSelectionModel().getSelected();
				    window.open(rec.get('uri'));
				}
	        });
	    }
	    return this.downloadAction;
	},
	
	getDiscardProcessedRequestAction: function() {
	    if(!this.discardProcessedRequestAction) {
	        this.discardProcessedRequestAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Discard'),
	            disabled: true,
	            iconCls: 'icon-discard-processedrequest',
				scope: this,
				handler: function() {
					//Get the id to delete.
					var selection = this.getProcessedRequestsGridPanel().getSelectionModel().getSelections();
					var params = {discardRequests: []};
					Ext.each(selection, function(item, index, allItems) {
						params.discardRequests.push({requestID: item.get('id')});
					}, this);
					
					new Openwis.Handler.Remove({
            			url: configOptions.locService+ '/xml.processed.requests.delete',
            			params: params,
            			listeners: {
            				success: function() {
            					this.getProcessedRequestsStore().reload();
            				},
            				scope: this
            			}
            		}).proceed();
				}
	        });
	    }
	    return this.discardProcessedRequestAction;
	},
	
	
	//------------------------------------------------------------------------------------------
	
	validate: function() {
		return true;
	}
});