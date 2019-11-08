Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.Cache');

Openwis.RequestSubscription.SubSelectionParameters.Cache.File = Ext.extend(Ext.form.Field, {
    
    defaultAutoCreate : {tag: "div"},

    onRender: function(ct, position){
        Openwis.RequestSubscription.SubSelectionParameters.Cache.File.superclass.onRender.call(this, ct, position);
        this.getCacheFilePanel().add(this.getHeader());
        this.getCacheFilePanel().add(this.getTimeExtentSelection());
        this.getCacheFilePanel().add(this.getCacheFileGrid());
        
        this.getCacheFilePanel().doLayout();
        
        // Start the initial request to get the cached file list
        if(this.getTimeExtentSelection().validate()) {
		    var interval = this.getTimeExtentSelection().getValue();
		    this.getCacheFileStore().setBaseParam('startDate', interval.startDate);
		    this.getCacheFileStore().setBaseParam('endDate', interval.endDate);
            this.getCacheFileStore().load();
        }
    },

   getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('RequestSubscription.SSP.Cache.File.Title') + '<br><br><br>'
			});
		}
		return this.header;
	},
    
    getCacheFilePanel: function() {
	    if(!this.cacheFilePanel) {
	        this.cacheFilePanel = new Ext.Container({
	            renderTo: this.el
	        });
	    }
	    return this.cacheFilePanel;
	},
   
    getTimeExtentSelection: function() {
        if(!this.timeExtentSelection) {
            var dateFrom = new Date().add(Date.HOUR, Openwis.Conf.REQUEST_CACHE_HOUR);
            
            var dateTo = new Date();
            
            this.timeExtentSelection = new Openwis.Common.Components.DateTimeExtentSelection({
	            hideLabel: true,
	            dateSelection: true,
	            defaultDateFrom: Openwis.Utils.Date.dateToISOUTC(dateFrom),
	            defaultDateTo: Openwis.Utils.Date.dateToISOUTC(dateTo),
			    timeSelection: true,
			    defaultTimeFrom: Openwis.Utils.Date.timeToUTC(dateFrom),
	            defaultTimeTo: Openwis.Utils.Date.timeToUTC(dateTo),
			    timeEditable: true,
			    processValue: function(value) {
			        var interval = {};
			        if(value.dateFrom && value.dateTo) {
			            interval.startDate = value.dateFrom.format('Y-m-d') + 'T' + value.timeFrom + ':00Z';
			            interval.endDate = value.dateTo.format('Y-m-d') + 'T' + value.timeTo + ':00Z';
			        }
			        return interval;
			    },
			    listeners: {
			        valueChanged: function() {
			            if(this.getTimeExtentSelection().validate()) {
    					    var interval = this.getTimeExtentSelection().getValue();
    					    this.getCacheFileStore().setBaseParam('startDate', interval.startDate);
    					    this.getCacheFileStore().setBaseParam('endDate', interval.endDate);
                            this.getCacheFileStore().load();
                        }
			        },
			        scope: this
			    }
	        });
	    }
	    return this.timeExtentSelection;
    },
    
    getCacheFileGrid: function() {
        if(!this.cacheFileGrid) {
            this.cacheFileGrid = new Ext.grid.GridPanel({
            	id: 'cacheFileGrid',
    			height: 300,
    			width: 550,
    			border: true,
    			store: this.getCacheFileStore(),
    			loadMask: true,
    			columns: [
                    {id: 'filename', header: Openwis.i18n('RequestSubscription.SSP.Cache.File.Name'), dataIndex: 'filename', width: 300, sortable: true},
                    {id: 'checksum', header: Openwis.i18n('RequestSubscription.SSP.Cache.File.Checksum'), dataIndex: 'checksum', width: 250, sortable: true}
    			],
    			autoExpandColumn: 'filename',
    			style : {
	                marginTop: '10px'
    			}
	        });
	    }
	    return this.cacheFileGrid;
	},
	
	getCacheFileStore: function() {
	    if(!this.cacheFileStore) {
    		this.cacheFileStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService + '/xml.get.cache.subselectionparameters',
                fields: [
                    {
                        name:'id'
                    },{
                        name:'checksum'
                    },{
                        name:'filename'
                    },{
                        name:'insertionDate'
                    }
    			]
    		});
    		this.cacheFileStore.setBaseParam("urn", this.productMetadataUrn);
		}
		return this.cacheFileStore;
	},
    
    //-------------------------------------------------------------
    
    getRawValue: function() {
		var selections = this.getCacheFileGrid().getSelectionModel().getSelections();
		var files = [];
		Ext.each(selections, function(record) {
		    files.push(record.get('id'));
		}, this);
		return files;
	},
	
	getValue: function() {
		return this.getRawValue();
	},
	
	buildValue: function() {
		return this.getValue();
	},
	
	validateValue : function(value){
        return !Ext.isEmpty(value);
    },
    
    reset: function() {
	    this.getTimeExtentSelection().reset();
	}
});