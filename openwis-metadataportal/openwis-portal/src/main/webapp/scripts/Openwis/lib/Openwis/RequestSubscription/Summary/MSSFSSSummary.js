

Ext.ns('Openwis.RequestSubscription');

Openwis.RequestSubscription.MSSFSSSummary = Ext.extend(Ext.Panel, {
	
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
		Openwis.RequestSubscription.MSSFSSSummary.superclass.initComponent.apply(this, arguments);
		
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
				    {xtype: 'displayfield', value: this.config.productMetadataURN, fieldLabel: Openwis.i18n('RequestSubscription.Summary.ProductMetadataURN')}
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
                    {id: 'creationDate', header: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.CreationDate'), dataIndex: 'creationDate', renderer: Openwis.Utils.Date.formatDateTimeUTC, width: 120, sortable: true},
                    {id: 'message', header: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.Message'), dataIndex: 'message', sortable: true}
    			],
    			autoExpandColumn: 'message',
                listeners: { 
                    afterrender: function (grid) {
                       grid.loadMask.show();
                       grid.getStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
                    },
                    scope:this
                },
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getProcessedRequestsStore(),
                    displayInfo: true,
                    displayMsg: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.Range'),
                    emptyMsg: Openwis.i18n('RequestSubscription.Summary.ProcessedRequest.No.Elements')
                })
	        });
	    }
	    return this.processedRequestsGridPanel;
	},
	
	getProcessedRequestsStore: function() {
	    if(!this.processedRequestsStore) {
    		this.processedRequestsStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService + '/xml.mssfss.processed.requests.all',
    			idProperty: 'creationDate',
                remoteSort: true,
                root: 'rows',
                totalProperty: 'total',
                fields: [
                    {
                        name:'creationDate'
                    },{
                        name:'message'
                    }
    			],
                sortInfo: {
                   field: 'creationDate',
                   direction: 'DESC'
                }
    		});
    		this.processedRequestsStore.setBaseParam("id", this.config.requestID);
		}
		return this.processedRequestsStore;
	},
	
	//------------------------------------------------------------------------------ Actions
	
	validate: function() {
		return true;
	}
});