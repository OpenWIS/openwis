

Ext.ns('Openwis.RequestSubscription.SubSelectionParameters');

Openwis.RequestSubscription.SubSelectionParameters.SSPGlobalProduct = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		var labelWidth = {};
		if(this.ssp && this.ssp[0] || this.isSubscription) {
			labelWidth = 100;
		} else {
			labelWidth = 1;
		}
		Ext.apply(this, {
			border: false,
			cls: 'SSPPanel',
			itemCls: 'SSPItemsPanel',
			style: {
			    padding: '10px'
			},
			labelWidth: labelWidth
		});
		Openwis.RequestSubscription.SubSelectionParameters.SSPGlobalProduct.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('panelInitialized', 'nextActive');
        this.fireEvent("nextActive", false);
		this.isInitialized = false;
	},
	
	initialize: function() {
	    if(this.isSubscription) {
	        this.add(this.getCachePeriodPanel());
	        this.add(this.getSchedulePanel());
	    } else {
	        this.add(this.getCacheFilePanel());
	    }
	    this.isInitialized = true; 
	},
	
	//----------------------------------------------------------------- Initialization of the panels.
	
	getCachePeriodPanel: function() {
	    if(!this.cachePeriodPanel) {
	        var ssp = null;
	        if(this.ssp && this.ssp[0]) {
	            ssp = this.ssp[0];
	        }
	    
	        this.cachePeriodPanel = new Openwis.RequestSubscription.SubSelectionParameters.Cache.Period({
	            fieldLabel: Openwis.i18n('RequestSubscription.SSP.Cache.Period.Title'),
	            ssp: ssp
	        });
	    }
	    return this.cachePeriodPanel;
	},
	
	getSchedulePanel: function() {
	    if(!this.schedulePanel) {
	        this.schedulePanel = new Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule({
	            fieldLabel: Openwis.i18n('RequestSubscription.SSP.Schedule.Title'),
	            frequency: this.frequency
	        });
	    }
	    return this.schedulePanel;
	},
	
	getCacheFilePanel: function() {
	    if(!this.cacheFilePanel) {
	        var ssp = null;
	        if(this.ssp && this.ssp[0]) {
	            //View mode.
	            this.cacheFilePanel = new Ext.form.DisplayField({
                    fieldLabel: Openwis.i18n('RequestSubscription.SSP.Cache.Type'),
                    value: Openwis.i18n('RequestSubscription.SSP.Cache.Type.Label'),
                    buildValue: function() {return labelValue;}
                });
	        } else {
	        	
	            this.cacheFilePanel = new Openwis.RequestSubscription.SubSelectionParameters.Cache.File({
   	            fieldLabel: '',
    	            productMetadataUrn: this.productMetadataUrn
    	        });
	        }
	    }
	    return this.cacheFilePanel;
	},
	
	//----------------------------------------------------------------- Generic methods used by the wizard.
	
	initializeAndShow: function() {
	    if(!this.isInitialized) {
	        this.initialize();
	    }
	},
	
	buildSSPs: function() {
		var ssps = [];
		var ssp = {};
        
        var value = null;
        if(this.isSubscription) {
            ssp.code = 'parameter.time.interval';
	        value = this.getCachePeriodPanel().buildValue();
	    } else {
	        ssp.code = 'parameter.product.id';
	        value = this.getCacheFilePanel().buildValue();
	    }
        
        ssp.values =  Ext.isArray(value) ? value : [value];
    	ssps.push(ssp);
    	
		return ssps;
	},
	
	buildFrequency: function() {
	    if(this.isSubscription) {
	        return this.getSchedulePanel().buildValue();
	    } else {
	        return null;
	    }
	}
	
});