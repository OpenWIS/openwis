Ext.ns('Openwis.Admin.MetaInfo');

Openwis.Admin.MetaInfo.Manage = Ext.extend(Ext.Window, {
	
	ingestionService: '/xml.management.cache.configure.ingest',
	feedingService: '/xml.management.cache.configure.feed',

	initComponent: function() {
		Ext.apply(this, 
		{
			title: 'Edit MetaInfo ...',
			layout: 'fit',
			width:650,
			height:450,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.MetaInfo.Manage.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		// TODO if(this.multiple) 
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.metainfo.get',
			params: this.metadataURNs,
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
				},
				failure: function(config) {
					this.close();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add metaInfo saved event.
		this.addEvents("metaInfoSaved");
		
		//-- Create single or multiple form panel.
		if (this.multiple) {
		    this.add(this.getMultipleMetaInfoFormPanel());
	    } else {
	        this.add(this.getSingleMetaInfoFormPanel());
	    }
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		this.show();
	},
	
	getSingleMetaInfoFormPanel: function() {
		if(!this.metaInfoFormPanel) {
			this.metaInfoFormPanel = new Ext.Panel({
				layout:'table',
                layoutConfig: {
                    columns: 4,
                    tableAttrs: {
                        style: {
                            width: '100%',
                            padding: '20px'
                        }
                    }
                },
                border: false
			});

            // Fill the table layout panel
            var selectedProductMetadata = this.config.productsMetadata[0];
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.MetadataURN') + ':', colspan:2}));
			this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.urn), colspan:2}));
			
    		this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.Title') + ':', colspan:2}));
			this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.title), colspan:2}));
			
			this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.Originator') + ':', colspan:2}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.originator), colspan:2}));
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:'<br>', colspan:4}));

            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.GTSCategory') + ':'}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.gtsCategory)}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenGtsCategory') + ':'}));
            this.metaInfoFormPanel.add(this.getOverGtsCategoryTextField());
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.DataPolicy') + ':'}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:selectedProductMetadata.dataPolicy}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenDataPolicy') + ':'}));
            this.metaInfoFormPanel.add(this.getDataPoliciesComboBox());
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.FNCPattern') + ':'}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:selectedProductMetadata.fncPattern}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverridenFNCPattern') + ':'}));
            this.metaInfoFormPanel.add(this.getOverFncPatternTextField());
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.GTSpriority') + ':'}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:''+selectedProductMetadata.priority}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenGTSpriority') + ':'}));
            this.metaInfoFormPanel.add(this.getOverGtsPriorityTextField());
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.FileExtension') + ':'}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:selectedProductMetadata.fileExtension}));
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenFileExtension') + ':'}));
            this.metaInfoFormPanel.add(this.getOverFileExtensionTextField());

            this.metaInfoFormPanel.add(this.getSimpleContainer({html:'<br>', colspan:4}));
            
            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.Ingested') + ':'}));
            this.metaInfoFormPanel.add(new Ext.Button(this.getIngestionFilterNewAction(selectedProductMetadata.urn)));

            this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.Fed') + ':'}));
            this.metaInfoFormPanel.add(new Ext.Button(this.getFeedingFilterNewAction(selectedProductMetadata.urn)));
            
		}
		return this.metaInfoFormPanel;
	},

	/**
	 * Add ingestion filter for this metadata
	 */
	getIngestionFilterNewAction: function(regex) {
	    if(!this.ingestionFilterNewAction) {
	        this.ingestionFilterNewAction = new Ext.Action({
			text: 'Add to ingestion filter',
			disabled: false,
			scope: this,
			handler: function() {
				this.addFilter(regex, this.ingestionService, 'Ingestion');
			}	            
	        });
	    }
	    return this.ingestionFilterNewAction;
	},

	/**
	 * Add feeding filter for this metadata
	 */
	getFeedingFilterNewAction: function(regex) {
	    if(!this.feedingFilterNewAction) {
	        this.feedingFilterNewAction = new Ext.Action({
			text: 'Add to feeding filter',
			disabled: false,
			scope: this,
			handler: function() {
				this.addFilter(regex, this.feedingService, 'Feeding');
			}	            
	        });
	    }
	    return this.feedingFilterNewAction;
	},

	/**
	 * Prompts the user to enter data for a new ingestion or feeding filter.
	 * @param store (object) the store to be reloaded
	 * @param type (string) type of filter to be added
	 */
	addFilter: function(regex, service, type) {
		var newFilter = {};
		newFilter.regex = '^' + regex + '$';
		newFilter.description = 'Description';
		
		var filterDialog = new Openwis.Admin.DataService.FilterInputDialog({
			operationMode: 'New',
			filterType: type,
			selectedFilter: newFilter,
			locationService: service,
			listeners: {
				filterSaved: function(msg, isError) {
					if (isError)
					{
						Ext.Msg.show({
	                        title: 'Add filter',
	                        msg: msg,
	                        buttons: Ext.Msg.OK,
	                        scope: this,
	                        icon: Ext.MessageBox.ERROR
	                     });
					}
					else
					{
						Ext.Msg.show({
	                        title: 'Add filter',
	                        msg: msg,
	                        buttons: Ext.Msg.OK,
	                        scope: this,
	                        icon: Ext.MessageBox.INFO
	                     });
					}
				},
				scope: this
			}
		});
		filterDialog.show();		
	},

	getMultipleMetaInfoFormPanel: function() {
        if(!this.multipleMetaInfoFormPanel) {
            this.multipleMetaInfoFormPanel = new Ext.Panel({
                layout:'table',
                layoutConfig: {
                    columns: 3,
                    tableAttrs: {
                        style: {
                            width: '100%',
                            padding: '20px'
                        }
                    }
                },
                border: false
            });

            // Fill the table layout panel
            this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenGtsCategory') + ':'}));
            this.multipleMetaInfoFormPanel.add(this.getOverGtsCategoryTextField());
            this.multipleMetaInfoFormPanel.add(new Ext.Button(this.getResetAllGtsCategoriesAction()));
            
            this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:'<br><br>', colspan:3}));
            this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenDataPolicy') + ':'}));
            this.multipleMetaInfoFormPanel.add(this.getDataPoliciesComboBox());
            this.multipleMetaInfoFormPanel.add(new Ext.Button(this.getResetAllDataPoliciesAction()));
            
            this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:'<br><br>', colspan:3}));
            
            this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n('Metadata.MetaInfo.OverriddenGTSpriority') + ':'}));
            this.multipleMetaInfoFormPanel.add(this.getOverGtsPriorityTextField());
            this.multipleMetaInfoFormPanel.add(new Ext.Button(this.getResetAllPrioritiesAction()));
            
            
        }
        return this.multipleMetaInfoFormPanel;
    },
	
	
	/**
	 *  Display HTML labels and fields
	 *  via a simple generic container.
	 */
	getSimpleContainer: function(config) {
	    return new Ext.Container({
	        html: config.html,
	        border : config.border || false,
	        colspan : config.colspan || 1,
            style: {
                padding: config.padding || '5px'
            }
	    });
	},
	
	/**
     * The text field for the overriden GTS Category
     */
    getOverGtsCategoryTextField: function() {
        if(!this.overGtsCategoryTextField) {
            this.overGtsCategoryTextField = new Ext.form.TextField({
                name: 'overridenGtsCategory',
                value: this.config.productsMetadata[0].overridenGtsCategory
            });
        }
        return this.overGtsCategoryTextField;
    },
    
	/**
     * The text field for the overriden FNC pattern
     */
    getOverFncPatternTextField: function() {
        if(!this.overFncPatternTextField) {
            this.overFncPatternTextField = new Ext.form.TextField({
                name: 'overridenFncPattern',
                value: this.config.productsMetadata[0].overridenFncPattern
            });
        }
        return this.overFncPatternTextField;
    },
    
    /**
     * The text field for the overriden GTS priority
     */
    getOverGtsPriorityTextField: function() {
        if(!this.overGtsPriorityTextField) {
            this.overGtsPriorityTextField = new Ext.form.TextField({
                name: 'overridenPriority',
                value: this.multiple ? '':this.config.productsMetadata[0].overridenPriority
            });
        }
        return this.overGtsPriorityTextField;
    },
    
    /**
     * The text field for the overriden file extension 
     */
    getOverFileExtensionTextField: function() {
        if(!this.overFileExtensionTextField) {
            this.overFileExtensionTextField = new Ext.form.TextField({
                name: 'overridenFileExtension',
                value: this.config.productsMetadata[0].overridenFileExtension
            });
        }
        return this.overFileExtensionTextField;
    },
	
	/**
     * The data policies combo box.
     */
    getDataPoliciesComboBox: function() {   
        if(!this.dataPoliciesComboBox) {
            var dataPoliciesStore = new Ext.data.JsonStore({
                // store configs
                autoDestroy: true,
                // reader configs
                idProperty: 'id',
                fields: [
                    {name:'id'}, {name: 'name'}
                ]
            });
        
            this.dataPoliciesComboBox = new Ext.form.ComboBox({
                name: 'overridenDataPolicy',
                mode: 'local',
                typeAhead: true,
                triggerAction: 'all',
                selectOnFocus:true,
                store: dataPoliciesStore,
                editable: false,
                width: 250,
                displayField: 'name',
                valueField: 'id'
            });
            
            //Load Data into store.
            this.dataPoliciesComboBox.getStore().loadData(this.config.dataPolicies);
            
            if (!this.multiple) {
                // Add an empty value for overridden data policy value reset. 
                this.dataPoliciesComboBox.getStore().insert(0, 
                    [new Ext.data.Record({id:'-1',  name:Openwis.i18n('Metadata.MetaInfo.NoOverridenDataPolicy')})]);
                // If overridden value already exists, selects it.
                if (!Ext.isEmpty(this.config.productsMetadata[0].overridenDataPolicy)) {
                    this.dataPoliciesComboBox.setValue(this.config.productsMetadata[0].overridenDataPolicy);
                }
            }
        }
        
        return this.dataPoliciesComboBox;
    },
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
					
					// Initialize parameters to save. 
					var params= {
					    productsMetadata:[],
					    dataPolicies:[]
				    };
				    
				    
				    if (this.multiple) {
				        // get value to set ..
				        
				    	// Handle overridden priority to save
				        var overGtsCategory;
				        if (!this.resetOverridenGtsCategories && !Ext.isEmpty(this.getOverGtsCategoryTextField().getValue().trim())) {
				        	overGtsCategory = this.getOverGtsCategoryTextField().getValue().trim();
				        } else if (this.resetOverridenGtsCategories) {
				        	overGtsCategory = -1;
			            }
				        
				        // Handle overridden priority to save
				        var overPriority;
				        if (!this.resetOverridenPriorities && !Ext.isEmpty(this.getOverGtsPriorityTextField().getValue().trim())) {
				            overPriority = this.getOverGtsPriorityTextField().getValue().trim();
				        } else if (this.resetOverridenPriorities) {
				            overPriority = -1;
			            }
			            
			            
			            // Handle overridden data policy to save
			            var overDP;
				        if (!this.resetOverridenDPs && !Ext.isEmpty(this.getDataPoliciesComboBox().getRawValue())) {
    		                overDP = {
    		                    id: this.getDataPoliciesComboBox().getValue(), 
                                name: this.getDataPoliciesComboBox().getRawValue()
                            };
				        } else if (this.resetOverridenDPs) {
	                        overDP = {id:'-1'};
				        }

                        if (overGtsCategory || overPriority || overDP) { 
    		                // Loop over product metadata to update overriden priority for each one.
    		                Ext.each(this.metadataURNs, function(urn, index, urns) {
    		                    var tmpPM = {};
    		                    tmpPM.urn = urn;
    		                    if (overGtsCategory) {
    		                    	tmpPM.overridenGtsCategory = overGtsCategory;
    		                    }
                                if (overPriority) {
                                    tmpPM.overridenPriority = overPriority;
                                }
                                params.productsMetadata.push(tmpPM);
                            }, this);
                            if (overDP) {
                                params.dataPolicies.push(overDP);
                            }
                        }
				    } else {
				        var pm = this.config.productsMetadata[0];
				        var tmpPM = {};
    				    
                        if (this.getOverGtsCategoryTextField().getValue().trim() != pm.overridenGtsCategory &&
                            (!Ext.isEmpty(this.getOverGtsCategoryTextField().getValue().trim()) || pm.overridenGtsCategory)) {
                        	if (Ext.isEmpty(this.getOverGtsCategoryTextField().getValue().trim())) {
                        		tmpPM.overridenGtsCategory = "-1";
                        	} else {
                            	tmpPM.overridenGtsCategory = this.getOverGtsCategoryTextField().getValue().trim();
                        	}
                        }

                        if (this.getOverFncPatternTextField().getValue().trim() != pm.overridenFncPattern &&
    				        (!Ext.isEmpty(this.getOverFncPatternTextField().getValue().trim()) || pm.overridenFncPattern)) {
                        	if (Ext.isEmpty(this.getOverFncPatternTextField().getValue().trim())) {
                        		tmpPM.overridenFncPattern = "-1";
                        	} else {
                            	tmpPM.overridenFncPattern = this.getOverFncPatternTextField().getValue().trim();
                        	}
                        }
                        
                        if (this.getOverGtsPriorityTextField().getValue().trim() != pm.overridenPriority &&
                            (!Ext.isEmpty(this.getOverGtsPriorityTextField().getValue().trim()) || pm.overridenPriority)) {
                        	if (Ext.isEmpty(this.getOverGtsPriorityTextField().getValue().trim())) {
                        		tmpPM.overridenPriority = "-1";
                        	} else {
                            	tmpPM.overridenPriority = this.getOverGtsPriorityTextField().getValue().trim();
                        	}
                        }
                        
                        if (this.getOverFileExtensionTextField().getValue().trim() != pm.overridenFileExtension &&
                                (!Ext.isEmpty(this.getOverFileExtensionTextField().getValue().trim()) || pm.overridenFileExtension)) {
                            	if (Ext.isEmpty(this.getOverFileExtensionTextField().getValue().trim())) {
                            		tmpPM.overridenFileExtension = "-1";
                            	} else {
                                	tmpPM.overridenFileExtension = this.getOverFileExtensionTextField().getValue().trim();
                            	}
                            }
                        
                        if (!Ext.isEmpty(tmpPM.overridenGtsCategory) 
                        		|| !Ext.isEmpty(tmpPM.overridenFncPattern) 
                        		|| !Ext.isEmpty(tmpPM.overridenPriority)
                        		|| !Ext.isEmpty(tmpPM.overridenFileExtension)) {
                            tmpPM.urn = pm.urn;
                            params.productsMetadata.push(tmpPM);
                        }
                        
                        if (
                            // Define new overriden DP
                            (Ext.isEmpty(pm.overridenDataPolicy) && !Ext.isEmpty(this.getDataPoliciesComboBox().getRawValue()) && this.getDataPoliciesComboBox().getValue() != -1)
                            ||
                            // Update an overriden DP
                            (!Ext.isEmpty(pm.overridenDataPolicy) && this.getDataPoliciesComboBox().getRawValue().trim() != pm.overridenDataPolicy)
                            ||
                            // Delete an overriden DP
                            (!Ext.isEmpty(pm.overridenDataPolicy) && this.getDataPoliciesComboBox().getValue() === -1)
                            ) 
                        {
                            params.dataPolicies.push({
                                id: this.getDataPoliciesComboBox().getValue(), 
                                name: this.getDataPoliciesComboBox().getRawValue()
                            });
                            if (Ext.isEmpty(params.productsMetadata)) {
                                params.productsMetadata.push({urn:pm.urn});
                            }
                        }
                        
				    }
                    
                    if (Ext.isEmpty(params.dataPolicies) && Ext.isEmpty(params.productsMetadata)) {
                        this.close();
                    } else {
    					var saveHandler = new Openwis.Handler.Save({
    						url: configOptions.locService+ '/xml.metainfo.save',
    						params: params,
    						listeners: {
    							success: function(config) {
    								this.fireEvent("metaInfoSaved");
    								this.close();
    							},
    							scope: this
    						}
    					});
    					saveHandler.proceed();
                    }
				}
			});
		}
		return this.saveAction;
	},
	
	/**
     * The reset all overridden GTS categories action
     */
    getResetAllGtsCategoriesAction: function() {
        if (!this.resetAllCategoriesAction) {
            this.resetAllCategoriesAction = new Ext.Action({
                text: Openwis.i18n('Metadata.MetaInfo.ResetOverriddenGTSCategories'),
                enableToggle: true,
                scope: this,
                toggleHandler : function (button, state) {
                    this.getOverGtsCategoryTextField().setDisabled(state);
                    this.resetOverridenGtsCategories = state;
                }
            });
        }
        return this.resetAllCategoriesAction;
    },
	
	/**
	 * The reset all overridden data policies action
	 */
    getResetAllDataPoliciesAction: function() {
        if (!this.resetAllDpAction) {
            this.resetAllDpAction = new Ext.Action({
                text: Openwis.i18n('Metadata.MetaInfo.ResetOverriddenDataPolicies'),
                enableToggle: true,
                scope: this,
                toggleHandler : function (button, state) {
                    this.getDataPoliciesComboBox().setDisabled(state);
                    this.resetOverridenDPs = state;
                }
            });
        }
        return this.resetAllDpAction;
    },
    
    /**
     * The reset all overridden GTS priorities action
     */
    getResetAllPrioritiesAction: function() {
        if (!this.resetAllPrioritiesAction) {
            this.resetAllPrioritiesAction = new Ext.Action({
                text: Openwis.i18n('Metadata.MetaInfo.ResetOverriddenGTSPriorities'),
                enableToggle: true,
                scope: this,
                toggleHandler : function (button, state) {
                    this.getOverGtsPriorityTextField().setDisabled(state);
                    this.resetOverridenPriorities = state;
                }
            });
        }
        return this.resetAllPrioritiesAction;
    },
    
	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	}
	
});
