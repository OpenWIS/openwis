

Ext.ns('Openwis.RequestSubscription.SubSelectionParameters');

Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
			border: false,
			cls: 'SSPPanel',
			itemCls: 'SSPItemsPanel'
		});
		Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('panelInitialized', 'nextActive');
        this.fireEvent("nextActive", false);
		this.isInitialized = false;
	},
	
	//----------------------------------------------------------------- Initialization of the panels.
	
	
	
	//----------------------------------------------------------------- Generic methods used by the wizard.
	
	initializeAndShow: function() {
	    if(!this.isInitialized) {
			this.isInitialized = true;
	        this.getInfosAndRefresh();
	    }
	},
	
	buildSSPs: function() {
		var ssps = [];
		Ext.each(this.getCatalog().getComponentsCatalog(), 
		    function(item, index, allItems) {
			    if(item.code != this.getCatalog().getScheduleCode()) {
			        var ssp = {};
            	    ssp.code = item.code;
            	    
            	    var value = item.component.buildValue();
            		ssp.values =  Ext.isArray(value) ? value : [value];
    				ssps.push(ssp);	
			    }
		    },
		    this
		);
		return ssps;
	},
	
	buildFrequency: function() {
	    if(this.isSubscription) {
	        var scheduleItem = this.getCatalog().getComponent(this.getCatalog().getScheduleCode());
	        return scheduleItem.component.buildValue();
	    } else {
	        return null;
	    }
	},
	
	getInfosAndRefresh: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.get.request.subselectionparameters',
			params: {
			    urn: this.productMetadataUrn,
			    subscription: this.isSubscription
			},
			listeners: {
				success: function(config) {
					this.refresh(config);
				},
				failure: function(config) {
					this.close();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	refresh: function(response) {
	    var start = null;
	    var subSelectionParameters = [];
	    if(response.startParameter && response.parameters) {
	        start = response.startParameter;
	        subSelectionParameters = response.parameters
	    } else {
	        this.add(this.getSSPToExtHelper().createNoSSPAvailableContainer());
	    }
	    
	    if(this.isSubscription) {
	        if(!start) {
	            start = this.getCatalog().getScheduleCode();
	        }
	        subSelectionParameters.push({selectionType: 'ScheduleSelection', code: this.getCatalog().getScheduleCode(), label: Openwis.i18n('RequestSubscription.SSP.Schedule.Title')});
	    }
		this.getCatalog().setSubSelectionParameters(subSelectionParameters);
		
		//At least one element.
		if(start) {
		    //Create a set of containers to display the labels.
    		var parameterCode = start, nextParameterCode = null;
    		var idx = 0;
    		do {
    			var parameter = this.getCatalog().getParameterByCode(parameterCode);
    			
    			var emptyComponent = this.getSSPToExtHelper().createEmptyComponent(parameter.label);
    			this.add(emptyComponent);
    			
    			this.getCatalog().registerComponent(parameter.code, emptyComponent, idx);
    			idx++;
    			
    			nextParameterCode = parameter.nextParameter;
    			if(!nextParameterCode && this.isSubscription && parameterCode != this.getCatalog().getScheduleCode()) {
    			     nextParameterCode = this.getCatalog().getScheduleCode();
    			}
    		} while(parameterCode = nextParameterCode);
		
    		var sspToExtConfig = {parameter: this.getCatalog().getParameterByCode(start)};
    		if(this.isEdition()) {
                sspToExtConfig.editValue = this.getEditValue(sspToExtConfig.parameter.code); 
            }
    		this.resetComponent(sspToExtConfig);
		
		    this.onValueChanged(start);
		} else {
		    this.fireEvent('nextActive', true);
		}
		
		this.fireEvent('panelInitialized');
	},
	
	//----------------------------------------------------------------- Sub-Selection parameters wizard management.
	
	resetComponent: function(sspToExtConfig) {
        //Creates the component.
        var parameter = this.getCatalog().getParameterByCode(sspToExtConfig.parameter.code);
        
		var sspToExtComponent = null;
		if(this.readOnly) {
		    sspToExtComponent = this.getSSPToExtHelper().createReadOnlyComponent(sspToExtConfig);
		} else {
		    sspToExtComponent = this.getSSPToExtHelper().createComponent(sspToExtConfig);
		
    		//Add a listener for event 'valueChanged' to trigger following parameter display.
    		sspToExtComponent.on('valueChanged', function() {
    		    this.onValueChanged(sspToExtConfig.parameter.code);
    		}, this);
		}
		
		//Update it in the window.
	    var catalogCmp = this.getCatalog().getComponent(sspToExtConfig.parameter.code);
	    var idx = catalogCmp.indexInForm;
	    var cmp = catalogCmp.component;
		this.remove(cmp);
		
	    //Register the component and add it to the form.
		this.getCatalog().registerComponent(sspToExtConfig.parameter.code, sspToExtComponent, idx);
		sspToExtComponent.scrollRef = this.ownerCt.body;
		this.insert(idx, sspToExtComponent);
		
		//Re-layout the panel
		this.doLayout();
	},
	
	onValueChanged: function(code) {
	    var subSelectionParameter = this.getCatalog().getParameterByCode(code);
	    var catalogComponent = this.getCatalog().getComponent(code);
	    var extComponent = catalogComponent.component;
	    
	    //Check if value is defined.
	    if(extComponent.buildValue()) {
	        //Validate the user selection.
			if(extComponent.isValid()) {
				//User selection is valid.
				var nextParameterCode = this.getCatalog().getNextParameterCode(code);
				
				//If component has a following parameter.
				if(nextParameterCode) {
        			//Replace current element with re-initialized element.
        			var sspToExtConfig = {};
        			sspToExtConfig.parameter = this.getCatalog().getParameterByCode(nextParameterCode);
        			if(this.isEdition()) {
                	    sspToExtConfig.editValue = this.getEditValue(sspToExtConfig.parameter.code); 
                	}
        			
        			sspToExtConfig.previous = {};
        			sspToExtConfig.previous.type = subSelectionParameter.selectionType;
        			sspToExtConfig.previous.selection = Ext.isArray(extComponent.buildValue()) ? extComponent.buildValue() : [extComponent.buildValue()];
        			
    				var followingCatalogComponent = this.getCatalog().getComponent(nextParameterCode);
        			if(followingCatalogComponent) {
            			sspToExtConfig.currentElementSelection = followingCatalogComponent.component.buildValue();
        			}
                    
                    //Reset the component.
    		        this.resetComponent(sspToExtConfig);
    		        
    		        //Trigger the valueChanged event to refresh the following parameters.
    		        this.onValueChanged(sspToExtConfig.parameter.code);
    				
		        } else {
		            //Wizard is over.
		            this.fireEvent('nextActive', true);
		        }
			}
	    } else {
	        //No Value defined. Remove all following SS parameters. 
	        var currentIndex = this.getCatalog().indexOf(code);
    	    var hasCleaned = false;
    		Ext.each(this.getCatalog().getComponentsCatalog(), 
    		    function(item,index,allItems) {
    		        if(index > currentIndex) {
                	    var idx = item.indexInForm;
                	    var cmp = item.component;
                		this.remove(cmp);
                		
                	    //Register the component and add it to the form.
                	    var parameter = this.getCatalog().getParameterByCode(item.code);
                	    
                		var emptyComponent = this.getSSPToExtHelper().createEmptyComponent(parameter.label);
    			        this.getCatalog().registerComponent(item.code, emptyComponent, idx);
    			        
    			        this.insert(idx, emptyComponent);
    		        
    				    hasCleaned = true;
    		        }
    		    }, 
    		    this
    		);
	        
	        if(hasCleaned) {
	            this.doLayout();
	            this.fireEvent("nextActive", false);
	        }
	    }
	},
	
	//----------------------------------------------------------------- Helpers.
	isEdition: function() {
	    return this.ssp != null;
	},
	
	getCatalog: function() {
	    if(!this.catalog) {
	        this.catalog = new Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPCatalog();
	    }
	    return this.catalog;
	},
	
	getSSPToExtHelper: function() {
	    if(!this.sspToExtHelper) {
	        this.sspToExtHelper = new Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPToExt();
	    }
	    return this.sspToExtHelper;
	},
	
	getEditValue: function(code) {
	    var itemToFind = null;
	    if(this.isEdition()) {
	        if(code != this.getCatalog().getScheduleCode()) {
    	        Ext.each(this.ssp, 
        		    function(item,index,allItems) {
        		        if(item.code == code) {
                    	    itemToFind = item.value;
                    	    return;
        		        }
        		    }, 
        		    this
        		);
    		} else {
    		    return this.frequency;
    		}
	    }
	    return itemToFind;
	}
	
});