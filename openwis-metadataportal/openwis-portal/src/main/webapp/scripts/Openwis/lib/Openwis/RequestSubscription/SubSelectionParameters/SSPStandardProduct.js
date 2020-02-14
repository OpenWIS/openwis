

Ext.ns('Openwis.RequestSubscription.SubSelectionParameters');

Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
			border: false,
			cls: 'SSPPanel',
			itemCls: 'SSPItemsPanel',
			style: {
			    padding: '20px'
			}
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
			    if(item.code != this.getCatalog().getScheduleCode() && (item.code != "FAILURE" && item.code != "SUCCESS")) {
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
					if (config.parameters && config.parameters.length > 0 && config.parameters[0].code == 'FAILURE') {
						// Particular case: 1st parameter is a failure
						Openwis.Utils.MessageBox.displayErrorMsg(config.parameters[0].label);
					} else {
						this.refresh(config);
					}
				},
				failure: function(config) {
					this.close();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	hasDefaultValues: function(code) {
		var parameter = this.getCatalog().getParameterByCode(code);
		var comp = this.getCatalog().getComponent(code);	
		var isDefaultValues = false;
		
		if (this.getEditValue(code) != undefined){
			isDefaultValues = true;
		}
		else if (parameter.values) {
			if (parameter.values.length > 0) {
				Ext.each(parameter.values, 
		    		    function(item,index,allItems) {
		    		        if(item.selected) {
		    		        	isDefaultValues = true;
		                	    return;
		    		        }
		    		    }, 
		    		    this
		    		);
			}
		}  
		return isDefaultValues;
	},
	
	replaceCurrentElement: function(code, subSelectionParameter, extComponent) {
		var nextParameterCode = this.getCatalog().getNextParameterCode(code);

		if(nextParameterCode) {
			
			//Replace current element with re-initialized element.
			var sspToExtConfig = {};
			if (this.getCatalog().isInteractive) {
				if (this.isSubscription && (nextParameterCode == 'SUCCESS')) {

					sspToExtConfig.parameter = {selectionType: 'ScheduleSelection', code: this.getCatalog().getScheduleCode(), label: Openwis.i18n('RequestSubscription.SSP.Schedule.Title')};
					var emptyComponent = this.getSSPToExtHelper().createEmptyComponent(sspToExtConfig.parameter.label);
	    			this.add(emptyComponent);
	    			
	    			this.getCatalog().registerComponent(sspToExtConfig.parameter.code, emptyComponent, this.getIdx());
	    			
				} else {
					sspToExtConfig.parameter = this.getCatalog().getParameterByCode(nextParameterCode);
				}
			}
			else {
				sspToExtConfig.parameter = this.getCatalog().getParameterByCode(nextParameterCode);
			}
						
			if(this.isEdition()) {
        	    sspToExtConfig.editValue = this.getEditValue(sspToExtConfig.parameter.code);
        	}
			
			sspToExtConfig.previous = {};
			sspToExtConfig.previous.type = subSelectionParameter.selectionType;
			// Selected Values
			sspToExtConfig.previous.selection = Ext.isArray(extComponent.buildValue()) ? extComponent.buildValue() : [extComponent.buildValue()];
			
			
			var followingCatalogComponent = this.getCatalog().getComponent(nextParameterCode);
			if(followingCatalogComponent) {
    			sspToExtConfig.currentElementSelection = followingCatalogComponent.component.buildValue();
			}
			
			//Reset the component.
			this.resetComponent(sspToExtConfig);
	        
	        //Trigger the valueChanged event to refresh the following parameters.
	        
	        // Check default values
	        if (this.getCatalog().isInteractive) {
	        	if (this.hasDefaultValues(nextParameterCode)) {
		        	this.onValueChanged(nextParameterCode);
		        }
	        } else {
	        	this.onValueChanged(nextParameterCode);
	        }
			 
	        	
	        
        } else {
            //Wizard is over.
            this.fireEvent('nextActive', true);
        }

	},
	
	getInfosForNextParameter: function(code, subSelectionParameter, catalogComponent, extComponent) {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.get.request.subselectionparameters',
			params: {
			    urn: this.productMetadataUrn,
			    subscription: this.isSubscription,
			    parameters: this.buildSSPs()
			},
			listeners: {
				success: function(config) {
					
					if (config.parameters[0].code == 'FAILURE') {
						Openwis.Utils.MessageBox.displayErrorMsg(config.parameters[0].label);
					} else {
						// Add next parameter in catalog
						
						this.getCatalog().setSubSelectionParameters(config.parameters);
	
						this.getCatalog().getParameterByCode(config.parameters[0].code).values = config.parameters[0].values;
						var parameter = this.getCatalog().getParameterByCode(config.parameters[0].code);
		    			var emptyComponent = this.getSSPToExtHelper().createEmptyComponent(parameter.label);
		    			this.add(emptyComponent);
	
						this.idx = this.getIdx() + 1;
		    			this.getCatalog().registerComponent(parameter.code, emptyComponent, this.getIdx());
		    			
		    			this.replaceCurrentElement(code, subSelectionParameter, extComponent);
					}
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
		
		// --Normal Mode
		var start = null;

	    var subSelectionParameters = [];
	    var isInteractive;
	    isInteractive = response.interactive;
	    if (response.startParameter && response.parameters || !isInteractive) { 		// --Normal Mode
	    	start = response.startParameter;
	        subSelectionParameters = response.parameters
	        
	        if (!start) {
		        this.add(this.getSSPToExtHelper().createNoSSPAvailableContainer());
	        }
	        
	        if(this.isSubscription) {
		        if(!start) {
		            start = this.getCatalog().getScheduleCode();
		        }
		        subSelectionParameters.push({selectionType: 'ScheduleSelection', code: this.getCatalog().getScheduleCode(), label: Openwis.i18n('RequestSubscription.SSP.Schedule.Title')});
		    }
	    }
	    else if(response.parameters) {			// --Interactive Mode
	        subSelectionParameters = response.parameters;
	    } else {
	        this.add(this.getSSPToExtHelper().createNoSSPAvailableContainer());
	    }
		this.getCatalog().setSubSelectionParameters(subSelectionParameters);
		this.getCatalog().isInteractive = isInteractive;
		
		//At least one element.
		if (start) {  // --Normal Mode
			 //Create a set of containers to display the labels.
    		var parameterCode = start, nextParameterCode = null;
    		do {

    			var parameter = this.setParameterToComponent(parameterCode);
    			this.idx++;
    			
    			nextParameterCode = parameter.nextParameter;
    			if(!nextParameterCode && this.isSubscription && parameterCode != this.getCatalog().getScheduleCode()) {
    			     nextParameterCode = this.getCatalog().getScheduleCode();
    			}
    		} while(parameterCode = nextParameterCode);
		    
		    this.setSspToExtConfig(start);
		    
		} else if(response.parameters) { // --Interactive Mode
		    //Create a set of containers to display the labels.
    		var parameterCode = subSelectionParameters[0].code;

    		var parameter = this.setParameterToComponent(parameterCode);
    		
    		this.setSspToExtConfig(parameterCode);
		} else {
		    this.fireEvent('nextActive', true);
		}
		
		this.fireEvent('panelInitialized');
	},
	
	setParameterToComponent: function(parameterCode) {
		var parameter = this.getCatalog().getParameterByCode(parameterCode);
		
		var emptyComponent = this.getSSPToExtHelper().createEmptyComponent(parameter.label);
		this.add(emptyComponent);
		
		this.getCatalog().registerComponent(parameter.code, emptyComponent, this.getIdx());
		
		return parameter;
	},
	
	setSspToExtConfig: function(parameterCode) {
		var sspToExtConfig = {parameter: this.getCatalog().getParameterByCode(parameterCode)};
		if(this.isEdition()) {
            sspToExtConfig.editValue = this.getEditValue(sspToExtConfig.parameter.code); 
        }
		this.resetComponent(sspToExtConfig);
		
		if (this.getCatalog().isInteractive) {
			// Check default values
        	if (this.hasDefaultValues(parameterCode)) {
	        	this.onValueChanged(parameterCode);
	        }
        } else {
        	this.onValueChanged(parameterCode);
        }
	},

	removeFollowingComponents: function(code){

		while (code != this.getCatalog().getComponentsCatalog()[this.getCatalog().getComponentsCatalog().length-1].code) {
			// Delete last item form
			var catalogCmp = this.getCatalog().getComponent(this.getCatalog().getComponentsCatalog()[this.getCatalog().getComponentsCatalog().length-1].code);
		    var cmp = catalogCmp.component;
			this.remove(cmp);
			
			// Erase array
			this.getCatalog().getComponentsCatalog().pop();
			//this.getParametersCatalog().pop();

		}
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
		if (idx == 0 && parameter.code == this.getCatalog().getScheduleCode()) {
			// If no parameters, put schedule param after the 'no parameter' message
			this.insert(1, sspToExtComponent);
		} else {
			this.insert(idx, sspToExtComponent);
		}
		
		
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

				//If component has a following parameter.
				if (this.getCatalog().isInteractive)  {
					// get next parameter from server in interactive mode
					this.removeFollowingComponents(code);
				    this.getInfosForNextParameter(code,subSelectionParameter,catalogComponent,extComponent);
				} else {
					this.replaceCurrentElement(code, subSelectionParameter, extComponent);
				}
				
			}
	    } else {
	        //No Value defined. Remove all following SS parameters. 
	        var currentIndex = this.getCatalog().indexOf(code);
    	    var hasCleaned = false;
    		Ext.each(this.getCatalog().getComponentsCatalog(), 
    		    function(item,index,allItems) {
    		        if(index > currentIndex) {
                	    var cmp = item.component;
                		this.remove(cmp);
                		
                	    //Register the component and add it to the form.
                	    var parameter = this.getCatalog().getParameterByCode(item.code);
                	    
                		var emptyComponent = this.getSSPToExtHelper().createEmptyComponent(parameter.label);
    			        this.getCatalog().registerComponent(item.code, emptyComponent, this.getIdx());
    			        
    			        this.insert(this.getIdx(), emptyComponent);
    		        
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
	
	getIdx : function() {
		if(!this.idx)  {
			this.idx = 0;
		}
		return this.idx;
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