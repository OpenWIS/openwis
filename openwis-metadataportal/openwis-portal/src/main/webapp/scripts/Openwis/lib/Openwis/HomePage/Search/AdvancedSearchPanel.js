Ext.ns('Openwis.HomePage.Search');

Openwis.HomePage.Search.AdvancedSearchPanel = Ext.extend(Openwis.HomePage.Search.AbstractSearchPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    title: Openwis.i18n('HomePage.Search.Advanced.Title')
		});
		Openwis.HomePage.Search.AdvancedSearchPanel.superclass.initComponent.apply(this, arguments);
		
		//Initialize static elements.
		this.initialize();
	},
	
	
	initialize: function() {
	    //What.
	    this.add(this.getWhatLabel());
	    
	    this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Either')));
	    this.getWhatOthersCriteriaFieldSet().add(this.getEitherTextField());
	    this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.ExactPhrase')));
	    this.getWhatOthersCriteriaFieldSet().add(this.getExactPhraseTextField());
	    this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.What')));
	    this.getWhatOthersCriteriaFieldSet().add(this.getWhatTextField());
	    this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Without')));
	    this.getWhatOthersCriteriaFieldSet().add(this.getWithoutTextField());
	    this.add(this.getWhatOthersCriteriaFieldSet());
	    
	    this.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Title')));
	    this.add(this.getTitleTextField());
	    this.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Abstract')));
	    this.add(this.getAbstractTextField());
	    this.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.Keywords')));
	    this.add(this.getKeywordsTextField());
	    
	    this.getWhatMapTypeFieldSet().add(this.getWhatMapTypeCheckboxGroup());
	    this.add(this.getWhatMapTypeFieldSet());
	    
	    this.getWhatSearchAccuracyFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.SearchAccuracy.Imprecise')));
	    this.getWhatSearchAccuracyFieldSet().add(this.getWhatSearchAccuracyRadioGroup());
	    this.getWhatSearchAccuracyFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.What.SearchAccuracy.Precise')));
	    this.add(this.getWhatSearchAccuracyFieldSet());
	    
	    //Where
	    this.add(this.getWhereLabel());
	    this.add(this.getMapPanel());
	    
	    this.add(this.getWhereBoundsPanel());
	    
	    this.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Where.Type')));
	    this.add(this.getWhereTypeCombobox());
	    
	    this.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Where.Region')));
	    this.add(this.getRegionsCombobox());
	    
	    //WHEN
	    this.getWhenFieldSet().add(this.getWhenAnytimeRadio());
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.When.Any')));
	    
	    this.getWhenFieldSet().add(this.getWhenMetadataChangeDateRadio());
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.When.MetadataChangeDate')));
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('Common.Extent.Temporal.From')));
	    this.getWhenFieldSet().add(this.getWhenMetadataChangeDateFromDateField());
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('Common.Extent.Temporal.To')));
	    this.getWhenFieldSet().add(this.getWhenMetadataChangeDateToDateField());
	    
	    this.getWhenFieldSet().add(this.getWhenTemporalExtentRadio());
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.When.TemporalExtent')));
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('Common.Extent.Temporal.From')));
	    this.getWhenFieldSet().add(this.getWhenTemporalExtentFromDateField());
	    this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n('Common.Extent.Temporal.To')));
	    this.getWhenFieldSet().add(this.getWhenTemporalExtentToDateField());
	    this.add(this.getWhenFieldSet());
	
	    //Restrict to.
	    //this.getRestrictToFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Catalog')));
	    //this.getRestrictToFieldSet().add(this.getRestrictToCatalogComboBox());
	    this.getRestrictToFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Category')));
	    this.getRestrictToFieldSet().add(this.getRestrictToCategoryComboBox());
	    this.getRestrictToFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Kind')));
	    this.getRestrictToFieldSet().add(this.getRestrictToKindComboBox());
	    this.add(this.getRestrictToFieldSet());
	   
	
	    //Options.
	    this.add(this.getOptionsPanel());
	    
	    //Inspire.
	    this.getInspireFieldSet().add(this.getOnlyInspireMetadataCheckbox());
	    this.getInspireFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Inspire.Annex')));
	    this.getInspireFieldSet().add(this.getInspireAnnexComboBox());
	    //this.getInspireFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Inspire.SourceType')));
	    //this.getInspireFieldSet().add(this.getInspireSourceTypeComboBox());
	    this.getInspireFieldSet().add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType')));
	    this.getInspireFieldSet().add(this.getInspireServiceTypeComboBox());
	    this.add(this.getInspireFieldSet());
	    
	    //Buttons.
	    this.add(this.getButtonsPanel());
    },
    
    //----------------------------------------------------------------- WHAT ?
    
    getEitherTextField: function() {
        if(!this.eitherTextField) {
            this.eitherTextField = new Ext.form.TextField({
                name: 'or',
				allowBlank: true,
				width: 190
            });
        }
        return this.eitherTextField;
    },
    
    getTitleTextField: function() {
        if(!this.titleTextField) {
            this.titleTextField = new Ext.form.TextField({
                name: 'title',
				allowBlank: true,
				width: 210
            });
        }
        return this.titleTextField;
    },
    
    getAbstractTextField: function() {
        if(!this.abstractTextField) {
            this.abstractTextField = new Ext.form.TextField({
                name: 'abstract',
				allowBlank: true,
				width: 210
            });
        }
        return this.abstractTextField;
    },
    
    getKeywordsTextField: function() {
        if(!this.keywordsTextField) {
            this.keywordsTextField = new Ext.form.TriggerField({
                name: 'themekey',
				allowBlank: true,
				width: 210,
				onTriggerClick: function(e) {
					new Openwis.Common.Search.KeywordsSearch({
						keywordsFromTf: this.getValue(),
						isXML: false,
						listeners: {
							keywordsSelection: function(records) {
								this.setValue(records);
							},
							scope: this
						}
					});
				}
            });
        }
        return this.keywordsTextField;
    },
    
    getExactPhraseTextField: function() {
        if(!this.exactPhraseTextField) {
            this.exactPhraseTextField = new Ext.form.TextField({
                name: 'phrase',
				allowBlank: true,
				width: 190
            });
        }
        return this.exactPhraseTextField;
    },
    
    getWhatTextField: function() {
        if(!this.whatTextField) {
            this.whatTextField = new Ext.form.TextField({
                name: 'all',
				allowBlank: true,
				width: 190
            });
        }
        return this.whatTextField;
    },
    
    getWithoutTextField: function() {
        if(!this.withoutPhraseTextField) {
            this.withoutPhraseTextField = new Ext.form.TextField({
                name: 'without',
				allowBlank: true,
				width: 190
            });
        }
        return this.withoutPhraseTextField;
    },
    
    getWhatOthersCriteriaFieldSet: function() {
        if(!this.whatOthersCriteriaFieldSet) {
            this.whatOthersCriteriaFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.What.TextSearchOptions'),
                layout: 'table',
                layoutConfig: {
                     columns: 1  
                },
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.whatOthersCriteriaFieldSet.addListener('collapse', this.onGuiChanged, this);
            this.whatOthersCriteriaFieldSet.addListener('expand', this.onGuiChanged, this);
        }
        return this.whatOthersCriteriaFieldSet;
    },
    
    getWhatMapTypeFieldSet: function() {
        if(!this.whatMapTypeFieldSet) {
            this.whatMapTypeFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.What.MapType'),
                layout: 'table',
                layoutConfig: {
                     columns: 1  
                },
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.whatMapTypeFieldSet.addListener('collapse', this.onGuiChanged, this);
            this.whatMapTypeFieldSet.addListener('expand', this.onGuiChanged, this);
        }
        return this.whatMapTypeFieldSet;
    },
    
    getWhatMapTypeCheckboxGroup: function() {
        if(!this.whatMapTypeCheckboxGroup) {
            this.whatMapTypeCheckboxGroup = new Ext.form.CheckboxGroup({
                columns: 2,
                items:
                [
                    {boxLabel: Openwis.i18n('HomePage.Search.Criteria.What.MapType.Digital'), name: 'digital'},
                    {boxLabel: Openwis.i18n('HomePage.Search.Criteria.What.MapType.Dynamic'), name: 'dynamic'},
                    {boxLabel: Openwis.i18n('HomePage.Search.Criteria.What.MapType.Paper'), name: 'paper'},
                    {boxLabel: Openwis.i18n('HomePage.Search.Criteria.What.MapType.Download'), name: 'download'}
                ],
                width: 200
            });
        }
        return this.whatMapTypeCheckboxGroup;
    },
    
    getWhatSearchAccuracyFieldSet: function() {
        if(!this.whatSearchAccuracyFieldSet) {
            this.whatSearchAccuracyFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.What.SearchAccuracy'),
                layout: 'table',
                layoutConfig: {
                     columns: 3  
                },
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.whatSearchAccuracyFieldSet.addListener('collapse', this.onGuiChanged, this);
            this.whatSearchAccuracyFieldSet.addListener('expand', this.onGuiChanged, this);
        }
        return this.whatSearchAccuracyFieldSet;
    },
    
    getWhatSearchAccuracyRadioGroup: function() {
        if(!this.whatSearchAccuracyRadioGroup2) {
            this.whatSearchAccuracyRadioGroup2 = new Ext.form.SliderField({
                name: 'similarity',
                value: 80,
                width: 120
            });
            // Prevent the slider from displaying on top of md viewer/editor
            this.whatSearchAccuracyRadioGroup2.slider.topThumbZIndex = 8000;
            
            /*new Ext.form.RadioGroup({
                columns: 5,
                items:
                [
                    {boxLabel: '', name: 'similarity', inputValue: '1'},
                    {boxLabel: '', name: 'similarity', inputValue: '0.8', checked: true},
                    {boxLabel: '', name: 'similarity', inputValue: '0.6'},
                    {boxLabel: '', name: 'similarity', inputValue: '0.4'},
                    {boxLabel: '', name: 'similarity', inputValue: '0.2'}
                ]
            });*/
        }
        return this.whatSearchAccuracyRadioGroup2;
    },
    
    //----------------------------------------------------------------- WHERE ?
    
    getWhereBoundsPanel: function() {
        if(!this.whereBoundsPanel) {
            this.whereBoundsPanel = new Ext.Panel({
                layout: 'table',
                layoutConfig: {
                    columns: 4
                },
                defaults: {
                    style: {
                        margin: '4px'
                    }
                },
                border: false
            });
            this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Where.Bounds.LatMin')));
            this.whereBoundsPanel.add(this.getWhereBoundsLatMinTextField());
            this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Where.Bounds.LongMin')));
            this.whereBoundsPanel.add(this.getWhereBoundsLongMinTextField());
            this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Where.Bounds.LatMax')));
            this.whereBoundsPanel.add(this.getWhereBoundsLatMaxTextField());
            this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n('HomePage.Search.Criteria.Where.Bounds.LongMax')));
            this.whereBoundsPanel.add(this.getWhereBoundsLongMaxTextField());
        }
        return this.whereBoundsPanel;
    },
    
    getWhereBoundsLatMinTextField: function() {
        if(!this.whereBoundsLatMinTextField) {
            this.whereBoundsLatMinTextField = new Ext.form.TextField({
                name: 'southBL',
				allowBlank: true,
				autoCreate: {tag: 'input', type: 'text', size: '5', autocomplete: 'off'},
				listeners: {
				    change: this.coordsChanged,
				    scope: this
				},
				validator: function(value) {
				    if(value.trim() != '' && isNaN(value)) {
				        return Openwis.i18n('Common.Validation.NotANumber', {value: value});
				    }
				    return true;
				}
            });
        }
        return this.whereBoundsLatMinTextField;
    },
    
    getWhereBoundsLatMaxTextField: function() {
        if(!this.whereBoundsLatMaxTextField) {
            this.whereBoundsLatMaxTextField = new Ext.form.TextField({
                name: 'northBL',
				allowBlank: true,
				autoCreate: {tag: 'input', type: 'text', size: '5', autocomplete: 'off'},
				listeners: {
				    change: this.coordsChanged,
				    scope: this
				},
				validator: function(value) {
				    if(value.trim() != '' && isNaN(value)) {
				        return Openwis.i18n('Common.Validation.NotANumber', {value: value});
				    }
				    return true;
				}
            });
        }
        return this.whereBoundsLatMaxTextField;
    },
    
    getWhereBoundsLongMinTextField: function() {
        if(!this.whereBoundsLongMinTextField) {
            this.whereBoundsLongMinTextField = new Ext.form.TextField({
                name: 'westBL',
				allowBlank: true,
				autoCreate: {tag: 'input', type: 'text', size: '5', autocomplete: 'off'},
				listeners: {
				    change: this.coordsChanged,
				    scope: this
				},
				validator: function(value) {
				    if(value.trim() != '' && isNaN(value)) {
				        return Openwis.i18n('Common.Validation.NotANumber', {value: value});
				    }
				    return true;
				}
            });
        }
        return this.whereBoundsLongMinTextField;
    },
    
    getWhereBoundsLongMaxTextField: function() {
        if(!this.whereBoundsLongMaxTextField) {
            this.whereBoundsLongMaxTextField = new Ext.form.TextField({
                name: 'eastBL',
				allowBlank: true,
				autoCreate: {tag: 'input', type: 'text', size: '5', autocomplete: 'off'},
				listeners: {
				    change: this.coordsChanged,
				    scope: this
				},
				validator: function(value) {
				    if(value.trim() != '' && isNaN(value)) {
				        return Openwis.i18n('Common.Validation.NotANumber', {value: value});
				    }
				    return true;
				}
            });
        }
        return this.whereBoundsLongMaxTextField;
    },
    
    getWhereTypeCombobox: function() {
        if(!this.whereTypeCombobox) {
            this.whereTypeCombobox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['intersection',  Openwis.i18n('HomePage.Search.Criteria.Where.Type.Intersection')], 
					    ['overlaps',      Openwis.i18n('HomePage.Search.Criteria.Where.Type.Overlaps')], 
					    ['encloses',      Openwis.i18n('HomePage.Search.Criteria.Where.Type.Encloses')], 
					    ['fullyOutsideOf',Openwis.i18n('HomePage.Search.Criteria.Where.Type.FullyOutsideOf')], 
					    ['crosses',         Openwis.i18n('HomePage.Search.Criteria.Where.Type.Crosses')], 
					    ['touches',         Openwis.i18n('HomePage.Search.Criteria.Where.Type.Touches')], 
					    ['within',         Openwis.i18n('HomePage.Search.Criteria.Where.Type.Within')]
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: 'overlaps',
                name: 'relation',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 225
            });
        }
        return this.whereTypeCombobox;
    },
    
    //----------------------------------------------------------------- WHEN ?
    
    getWhenAnytimeRadio: function() {
        if(!this.whenAnytimeRadio) {
            this.whenAnytimeRadio = new Ext.form.Radio({
    			name: 'whenMode',
    			inputValue: 'Anytime',
    			checked: true
    		});
    	}
    	return this.whenAnytimeRadio;
    },
    
    getWhenMetadataChangeDateRadio: function() {
        if(!this.whenMetadataChangeDateRadio) {
            this.whenMetadataChangeDateRadio = new Ext.form.Radio({
    			name: 'whenMode',
    			inputValue: 'MetadataChangeDate',
    			checked: false,
    			listeners : {
    				check: function(checkbox, checked) {
    					if(checked) {
    						this.getWhenMetadataChangeDateFromDateField().enable();
    						this.getWhenMetadataChangeDateToDateField().enable();
    					} else {
    						this.getWhenMetadataChangeDateFromDateField().disable();
    						this.getWhenMetadataChangeDateToDateField().disable();
    					}
    				},
    				scope: this
    			}
    		});
    	}
    	return this.whenMetadataChangeDateRadio;
    },
    
    getWhenMetadataChangeDateFromDateField: function() {
        if(!this.whenMetadataChangeDateFromDateField) {
            this.whenMetadataChangeDateFromDateField = new Ext.form.DateField({
            	allowBlank: false,
    			name: 'MetadataChangeDateFrom',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 165
    		});
    	}
    	return this.whenMetadataChangeDateFromDateField;
    },
    
    getWhenMetadataChangeDateToDateField: function() {
        if(!this.whenMetadataChangeDateToDateField) {
            this.whenMetadataChangeDateToDateField = new Ext.form.DateField({
            	allowBlank: false,
    			name: 'MetadataChangeDateTo',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 165
    		});
    	}
    	return this.whenMetadataChangeDateToDateField;
    },
    
    getWhenTemporalExtentRadio: function() {
        if(!this.whenTemporalExtentRadio) {
            this.whenTemporalExtentRadio = new Ext.form.Radio({
    			name: 'whenMode',
    			inputValue: 'TemporalExtent',
    			checked: false,
    			listeners : {
    				check: function(checkbox, checked) {
    					if(checked) {
    						this.getWhenTemporalExtentFromDateField().enable();
    						this.getWhenTemporalExtentToDateField().enable();
    					} else {
    						this.getWhenTemporalExtentFromDateField().disable();
    						this.getWhenTemporalExtentToDateField().disable();
    					}
    				},
    				scope: this
    			}
    		});
    	}
    	return this.whenTemporalExtentRadio;
    },
    
    getWhenTemporalExtentFromDateField: function() {
        if(!this.whenTemporalExtentFromDateField) {
            this.whenTemporalExtentFromDateField = new Ext.form.DateField({
            	allowBlank: false,
    			name: 'TemporalExtentFrom',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 165
    		});
    	}
    	return this.whenTemporalExtentFromDateField;
    },
    
    getWhenTemporalExtentToDateField: function() {
        if(!this.whenTemporalExtentToDateField) {
            this.whenTemporalExtentToDateField = new Ext.form.DateField({
            	allowBlank: false,
    			name: 'TemporalExtentTo',
        		editable: false,
        		format: 'Y-m-d',
        		disabled: true,
        		width: 165
    		});
    	}
    	return this.whenTemporalExtentToDateField;
    },
    
    getWhenFieldSet: function() {
        if(!this.whenFieldSet) {
            this.whenFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.When'),
                layout: 'table',
                layoutConfig: {
                     columns: 2  
                },
                cls: 'mainLabelCls',
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.whenFieldSet.addListener('collapse', this.onGuiChanged, this);
            this.whenFieldSet.addListener('expand', this.onGuiChanged, this);
        }
        return this.whenFieldSet;
    },
    
    
    //----------------------------------------------------------------- RESTRICT TO ?
    
    getRestrictToFieldSet: function() {
        if(!this.restrictToFieldSet) {
            this.restrictToFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.RestrictTo'),
                layout: 'table',
                layoutConfig: {
                     columns: 1  
                },
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.restrictToFieldSet.addListener('collapse', this.onGuiChanged, this);
            this.restrictToFieldSet.addListener('expand', this.onGuiChanged, this);
        }
        return this.restrictToFieldSet;
    },
    
    getRestrictToCatalogComboBox: function() {
        if(!this.restrictToCatalogComboBox) {
            var catalogStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.get.home.page.site.all',
    			idProperty: 'id',
    			fields: [
    				{
    					name:'id'
    				},{
    					name:'name'
    				}
    			],
    			listeners: {
    			    load: function(store, records, options) {
    			        var anyRecord = new Ext.data.Record({
        		            id: '',
        		            name: Openwis.i18n('Common.List.Any')
        		        });
        		        store.insert(0, [anyRecord]);
    			    }
    			}
    		});
        
            this.restrictToCatalogComboBox = new Ext.form.ComboBox({
                store: catalogStore,
				valueField: 'id',
				displayField:'name',
                name: 'siteId',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.restrictToCatalogComboBox;
    },
    
    getRestrictToCategoryComboBox: function() {
        if(!this.restrictToCategoryComboBox) {
            var categoryStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.get.home.page.category.all',
    			idProperty: 'id',
    			fields: [
    				{
    					name:'id'
    				},{
    					name:'name'
    				}
    			],
    			listeners: {
    			    load: function(store, records, options) {
    			        var anyRecord = new Ext.data.Record({
        		            id: '',
        		            name: Openwis.i18n('Common.List.Any')
        		        });
        		        store.insert(0, [anyRecord]);
    			    }
    			}
    		});
        
            this.restrictToCategoryComboBox = new Ext.form.ComboBox({
                store: categoryStore,
				valueField: 'id',
				displayField:'name',
                name: 'category',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.restrictToCategoryComboBox;
    },
    
    getRestrictToKindComboBox: function() {
        if(!this.restrictToKindComboBox) {
            this.restrictToKindComboBox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['',        Openwis.i18n('Common.List.Any')], 
					    ['metadata',Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Kind.Metadata')],
					    ['template',Openwis.i18n('HomePage.Search.Criteria.RestrictTo.Kind.Template')]
					]
				}),
				valueField: 'id',
				displayField:'value',
                name: 'restrictToKind',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
            this.restrictToKindComboBox.setValue('');
        }
        return this.restrictToKindComboBox;
    },
    
    //----------------------------------------------------------------- INSPIRE
    
    getInspireFieldSet: function() {
        if(!this.inspireFieldSet) {
            this.inspireFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.Inspire'),
                layout: 'table',
                layoutConfig: {
                     columns: 1  
                },
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.inspireFieldSet.addListener('collapse', this.onGuiChanged, this);
            this.inspireFieldSet.addListener('expand', this.onGuiChanged, this);
        }
        return this.inspireFieldSet;
    },
    
    getOnlyInspireMetadataCheckbox: function() {
        if(!this.onlyInspireMetadataCheckbox) {
            this.onlyInspireMetadataCheckbox = new Ext.form.Checkbox({
    			name: 'onlyInspireMetadata',
    			checked: false,
    			boxLabel: Openwis.i18n('HomePage.Search.Criteria.Inspire.InspireMetadataOnly')
    		});
    	}
    	return this.onlyInspireMetadataCheckbox;
    },
    
    getInspireAnnexComboBox: function() {
        if(!this.inspireAnnexComboBox) {
            this.inspireAnnexComboBox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['',   Openwis.i18n('Common.List.Any')], 
					    ['i',  Openwis.i18n('HomePage.Search.Criteria.Inspire.Annex.I')], 
					    ['ii', Openwis.i18n('HomePage.Search.Criteria.Inspire.Annex.II')], 
					    ['iii',Openwis.i18n('HomePage.Search.Criteria.Inspire.Annex.III')]
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: '',
                name: 'inspireAnnex',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.inspireAnnexComboBox;
    },
    
    getInspireSourceTypeComboBox: function() {
        if(!this.inspireSourceTypeComboBox) {
            this.inspireSourceTypeComboBox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['',   Openwis.i18n('Common.List.Any')], 
					    ['dataset',  Openwis.i18n('HomePage.Search.Criteria.Inspire.SourceType.Dataset')], 
					    ['service', Openwis.i18n('HomePage.Search.Criteria.Inspire.SourceType.Service')]
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: '',
                name: 'inspireSourceType',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.inspireSourceTypeComboBox;
    },
    
    getInspireServiceTypeComboBox: function() {
        if(!this.inspireServiceTypeComboBox) {
            this.inspireServiceTypeComboBox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['',   Openwis.i18n('Common.List.Any')], 
					    ['ESRI:AIMS--http--configuration', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.ArcIMSAXL')],
                    	['ESRI:AIMS--http-get-feature', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.ArcIMSFMS')],
                    	['GLG:KML-2.0-http-get-map', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.GoogleEarthKMLV2')],
                    	['OGC:WCS-1.1.0-http-get-capabilities', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.OGCWCSV110')],
                    	['OGC:WFS-1.0.0-http-get-capabilities', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.OGCWFSV100')],
                    	['OGC:WMC-1.1.0-http-get-capabilities', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.OGCWMCV11') ],
                    	['WWW:LINK-1.0-http--ical', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.iCalendar') ],
                    	['WWW:LINK-1.0-http--link', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.WebAddress') ],
                    	['WWW:LINK-1.0-http--partners', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.PartnerWebAddress') ],
                    	['WWW:LINK-1.0-http--related', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.RelatedLink') ],
                    	['WWW:LINK-1.0-http--rss', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.RSS') ],
                    	['WWW:LINK-1.0-http--samples', Openwis.i18n('HomePage.Search.Criteria.Inspire.ServiceType.ShowcaseProduct') ]
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: '',
                name: 'inspireServiceType',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.inspireServiceTypeComboBox;
    },
    
    reset: function() {
        this.getEitherTextField().reset();
        this.getExactPhraseTextField().reset();
        this.getWhatTextField().reset();
        this.getWithoutTextField().reset();
	    this.getTitleTextField().reset();
	    this.getAbstractTextField().reset();
	    this.getKeywordsTextField().reset();
	    
	    this.getWhatMapTypeCheckboxGroup().reset();
	    
	    this.getWhatSearchAccuracyRadioGroup().reset();
	    
	    //Where
        this.getMapPanel().reset();
        this.getWhereBoundsLatMinTextField().reset();
        this.getWhereBoundsLongMinTextField().reset();
        this.getWhereBoundsLatMaxTextField().reset();
        this.getWhereBoundsLongMaxTextField().reset();
	    this.getWhereTypeCombobox().reset();
        this.getRegionsCombobox().reset();
	    
	    //WHEN
	    this.getWhenAnytimeRadio().reset();
	
	    //Restrict to.
	    //this.getRestrictToCatalogComboBox().reset();
	    this.getRestrictToCategoryComboBox().reset();
	    this.getRestrictToKindComboBox().reset();
	
	    //Options.
        this.getSortDirectionCombobox().reset();
        this.getHitsCombobox().reset();
        
	    //Inspire.
	    this.getOnlyInspireMetadataCheckbox().reset();
	    this.getInspireAnnexComboBox().reset();
	    this.getInspireSourceTypeComboBox().reset();
	    this.getInspireServiceTypeComboBox().reset();
    },
    
	coordsChanged : function() {
		var latMin = parseFloat(this.getWhereBoundsLatMinTextField().getValue());
		var longMin = parseFloat(this.getWhereBoundsLongMinTextField().getValue());
		var latMax = parseFloat(this.getWhereBoundsLatMaxTextField().getValue());
		var longMax = parseFloat(this.getWhereBoundsLongMaxTextField().getValue());

		// if not all world
		if (!(isNaN(latMin) || isNaN(latMax) || isNaN(longMin) || isNaN(longMax))) {
			// Fix coords
			var latMin = Math.max(Math.min(latMin, 90), -90);
			var latMax = Math.max(Math.min(latMax, 90), -90);
			var longMin = this.getLongitude(longMin);
			var longMax = this.getLongitude(longMax);

			var bounds = {};
			bounds.bottom = latMin;
			bounds.top = latMax;
			bounds.left = longMin;
			bounds.right = longMax;

			this.getMapPanel().drawExtent(bounds);
			this.getMapPanel().zoomToExtent(bounds);
			this.updateMapFields(bounds, true);
		} else {
			// Reset
			this.getMapPanel().reset();
		}
	},
	
    updateMapFields: function(bounds, setRegionToUserDefined) {
        this.getWhereBoundsLatMinTextField().setValue(bounds.bottom);
        this.getWhereBoundsLatMaxTextField().setValue(bounds.top);

        this.getWhereBoundsLongMinTextField().setValue(bounds.left);
        this.getWhereBoundsLongMaxTextField().setValue(bounds.right);
        
        if(bounds.bottom.constrain(-90, 90) != bounds.bottom) {
            this.getWhereBoundsLatMinTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-90, to: 90}));
        }
        
        if(bounds.top.constrain(-90, 90) != bounds.top) {
            this.getWhereBoundsLatMaxTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-90, to: 90}));
        }
        
        if(bounds.left.constrain(-180, 180) != bounds.left) {
            this.getWhereBoundsLongMinTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-180, to: 180}));
        }
        
        if(bounds.right.constrain(-180, 180) != bounds.right) {
            this.getWhereBoundsLongMaxTextField().markInvalid(Openwis.i18n('Common.Validation.NumberOutOfRange', {from:-180, to: 180}));
        }
        
        if(setRegionToUserDefined) {
            this.setRegionToUserDefined();
        }

        if (bounds.left>bounds.right) {
        	this.getMapPanel().drawExtent(bounds);
        	this.getMapPanel().zoomToExtent(bounds);
        }
    },
    
    buildSearchParams: function() {
        var params = {};
        // Reset Advanced SearchParams
		params.all = '';
		params.region = '';
		params.kind = '';
		params.or = '';
		params.without = '';
		params.phrase = '';
		params.relation = '';
		params.extFrom = '';
		params.extTo = '';
		params.dateFrom = '';
		params.dateTo = '';
		params.category = '';
		params['abstract'] = '';
		params.similarity = '';
		params.siteId = '';
		params.title = '';
		params.digital = '';
		params.download = '';
		params.dynamic = '';
		params.paper = '';
		params.intermap = '';
		params.themekey = '';

		// Indicates that this search should reset the default parameters
		params.useSessionDefaults = 'false';

        
        
		params.sortBy = this.getSortDirectionCombobox().getValue();
		params.hitsPerPage = this.getHitsCombobox().getValue();
		params.from = 0;
		params.to = parseInt(params.hitsPerPage) - 1;
		
		params.or = this.getEitherTextField().getValue();
		params.phrase = this.getExactPhraseTextField().getValue();
		params.all = this.getWhatTextField().getValue();
		params.without = this.getWithoutTextField().getValue();
		params.title = this.getTitleTextField().getValue();
		params['abstract'] = this.getAbstractTextField().getValue();
		
		//TODO params.keywords
		params.similarity = this.getWhatSearchAccuracyRadioGroup().value / 100; /**/
		
		if(this.getRegionsCombobox().getValue() != 'Any') {
		    params.relation = this.getWhereTypeCombobox().getValue();
		    
		    var geometry = this.getMapPanel().getRawValue();
		    if(geometry) {
		        params.attrset = 'geo';
    		    params.geometry = geometry;
    		} else {
    			params.attrset = '';
    			params.geometry = '';
    		}
		    
		    if(this.getRegionsCombobox().getValue() != 'UserDefined' && this.getRegionsCombobox().getValue() != '') {
		        params.region = this.getRegionsCombobox().getValue();
		    } else {
		    	params.region = '';
		    }
		}
		
	    var mapTypes = this.getWhatMapTypeCheckboxGroup().getValue();
	    Ext.each(mapTypes, function(item) {
	        params[item.name] = 'on';
	    }, this);
	    
	    //WHEN
	    if(this.getWhenMetadataChangeDateRadio().checked) {
	        params.dateFrom = Openwis.Utils.Date.formatDateForServer(this.getWhenMetadataChangeDateFromDateField().getValue());
	        params.dateTo = Openwis.Utils.Date.formatDateForServer(this.getWhenMetadataChangeDateToDateField().getValue());
	        params.extFrom = '';
	        params.extTo = '';
	    } else if(this.getWhenTemporalExtentRadio().checked) {
	        params.extFrom = Openwis.Utils.Date.formatDateForServer(this.getWhenTemporalExtentFromDateField().getValue());
            params.extTo = Openwis.Utils.Date.formatDateForServer(this.getWhenTemporalExtentToDateField().getValue());
            params.dateFrom = '';
	        params.dateTo = '';
	    }
	    
	    //Restrict to.
	    //params.siteId = this.getRestrictToCatalogComboBox().getValue();
        params.category = this.getRestrictToCategoryComboBox().getValue();
        params.kind = this.getRestrictToKindComboBox().getValue();
	    
	    // Inspire.
        params.inspireOnly = this.getOnlyInspireMetadataCheckbox().getValue();
        params.inspireAnnex = this.getInspireAnnexComboBox().getValue();
        params.themekey = this.getKeywordsTextField().getValue();
        params.protocol = this.getInspireServiceTypeComboBox().getValue(); 
		
		return params;
    },
    
    searchUrl: function() {
        return configOptions.locService + '/main.search.embedded';
    },

    validate: function() {
    	//WHEN
	    if(this.getWhenMetadataChangeDateRadio().checked) {
	    	return this.getWhenMetadataChangeDateFromDateField().isValid() && this.getWhenMetadataChangeDateToDateField().isValid();
	    } else if(this.getWhenTemporalExtentRadio().checked) {
	    	return this.getWhenTemporalExtentFromDateField().isValid() && this.getWhenTemporalExtentToDateField().isValid();
	    }
	    return true;
    }
    
});