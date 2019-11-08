Ext.ns('Openwis.HomePage.Search');

Openwis.HomePage.Search.AbstractSearchPanel = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    border: false,
		    autoHeight: true
		});
		Openwis.HomePage.Search.AbstractSearchPanel.superclass.initComponent.apply(this, arguments);
		
		//Initialize static elements.
		//this.addEvents('searchResultsDisplayed', 'guiChanged');
		
		this.initializeCommons();
	},
	
	initializeCommons: function() {
	    //FieldSet options.
	    this.getOptionsFieldSet().add(this.getSortDirectionCombobox());
	    this.getOptionsFieldSet().add(this.getHitsCombobox());
	    this.getOptionsPanel().add(this.getOptionsFieldSet());
	    
	    //Buttons.
	    this.getButtonsPanel().addButton(new Ext.Button(this.getResetAction()));
	    this.getButtonsPanel().addButton(new Ext.Button(this.getSearchAction()));
    },
    
    isLoaded: function() {
    	return this.getButtonsPanel().rendered;
    },
    
    getWhatLabel: function() {
        if(!this.whatlabel) {
            this.whatlabel = new Ext.Container({
                border: false, 
                cls: 'mainLabelCls', 
                html: Openwis.i18n('HomePage.Search.Criteria.What'),
                style: {
                    padding: '2px'
                }
            });       
        }
        return this.whatlabel;
    },
    
    getWhereLabel: function() {
        if(!this.wherelabel) {
            this.wherelabel = new Ext.Container({
                border: false, 
                cls: 'mainLabelCls', 
                html: Openwis.i18n('HomePage.Search.Criteria.Where'),
                style: {
                    padding: '2px'
                }
            });       
        }
        return this.wherelabel;
    },
    
    //----------------------------------------------------------------- Initialization of the panels.
    
    getOptionsPanel: function() {
        if(!this.optionsPanel) {
            this.optionsPanel = new Ext.Panel({
                layout: 'form',
                border: false,
                style: {
                    padding: '2px'
                }
            });
        }
        return this.optionsPanel;
    },
    
    getOptionsFieldSet: function() {
        if(!this.optionsFieldSet) {
            this.optionsFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('HomePage.Search.Criteria.Options'),
				autoHeight:true,
				collapsed: true,
				collapsible: true,
				labelWidth: 80,
				listeners: {
				    afterrender: function() {
				        this.optionsFieldSet.addListener('collapse', this.onGuiChanged, this);
                        this.optionsFieldSet.addListener('expand', this.onGuiChanged, this);
				    },
				    scope: this
				}
            });
            
        }
        return this.optionsFieldSet;
    },
    
    getButtonsPanel: function() {
        if(!this.buttonsPanel) {
            this.buttonsPanel = new Ext.Panel({
                border: false,
                buttonAlign: 'center'
            });
        }
        return this.buttonsPanel;
    },
    
    getSortDirectionCombobox: function() {
        if(!this.sortDirectionCombobox) {
            this.sortDirectionCombobox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['changeDate', Openwis.i18n('HomePage.Search.Criteria.Options.SortDirection.ChangeDate')], 
					    ['popularity', Openwis.i18n('HomePage.Search.Criteria.Options.SortDirection.Popularity')], 
					    ['rating',     Openwis.i18n('HomePage.Search.Criteria.Options.SortDirection.Rating')], 
					    ['relevance',  Openwis.i18n('HomePage.Search.Criteria.Options.SortDirection.Relevance')], 
					    ['title',      Openwis.i18n('HomePage.Search.Criteria.Options.SortDirection.Title')]
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: 'relevance',
                name: 'sortDirection',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 95,
				fieldLabel: Openwis.i18n('HomePage.Search.Criteria.Options.SortDirection')
            });
        }
        return this.sortDirectionCombobox;
    },
    
    getHitsCombobox: function() {
        if(!this.hitsCombobox) {
            this.hitsCombobox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['10', '10'], 
					    ['20', '20'], 
					    ['50',  '50'],
					    ['100', '100'] 
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: '10',
                name: 'hitsPerPage',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 95,
				fieldLabel: Openwis.i18n('HomePage.Search.Criteria.Options.HitsPerPage')
            });
        }
        return this.hitsCombobox;
    },
    
    getMapPanel: function() {
        if(!this.mapPanel) {
            this.mapPanel = new Openwis.Common.Components.GeographicalExtentSelection({
                geoExtentType: 'RECTANGLE',
                wmsUrl: "http://vmap0.tiles.osgeo.org/wms/vmap0?",
                layerName: 'basic',
                maxExtent: new OpenLayers.Bounds(-180,-90,180,90),
                width: 225,
			    height: 200,
                listeners: {
                    valueChanged: function(bounds) {
                    	var latMin = bounds.bottom;
                    	var latMax = bounds.top;
                    	var longMin = bounds.left;
                    	var longMax = bounds.right;

                    	// Fix coords
                    	bounds.bottom = Math.max(Math.min(latMin, 90), -90);
                    	bounds.top = Math.max(Math.min(latMax, 90), -90);
                    	bounds.left = this.getLongitude(longMin);
                    	bounds.right = this.getLongitude(longMax);
                    	
                        this.updateMapFields(bounds, true);
                    },
                    scope: this
                }
            });
        }
        return this.mapPanel;
    },
    
    getRegionsCombobox: function() {
        if(!this.regionsCombobox) {
            var regionsStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.get.home.page.region.all',
    			idProperty: 'id',
    			fields: [
    				{
    					name:'id'
    				},{
    					name:'name'
    				},{
    					name:'extent'
    				}
    			],
    			listeners: {
    			    load: function(store, records, options) {
    			        var anyRecord = new Ext.data.Record({
        		            id: 'Any',
        		            name: Openwis.i18n('Common.List.Any'),
        		            extent: {
        		                left: -180,
        		                bottom: -90,
        		                right: 180,
        		                top: 90
        		            }
        		        });
        		        var userDefinedRecord = new Ext.data.Record({
        		            id: 'UserDefined',
        		            name: Openwis.i18n('HomePage.Search.Criteria.Where.Region.UserDefined')
        		        });
        		        store.insert(0, [anyRecord, userDefinedRecord]);
    			    }
    			}
    		});
        
            this.regionsCombobox = new Ext.form.ComboBox({
                store: regionsStore,
				valueField: 'id',
				displayField:'name',
                name: 'region',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 225,
    			listeners : {
    				select: function(combobox, rec, index) {
    				    if(rec.get('id') != 'UserDefined') {
    				        var bounds = rec.get('extent');
    					    this.getMapPanel().drawExtent(bounds);
    					    this.getMapPanel().zoomToExtent(bounds);
    					    this.updateMapFields(bounds, false);
    					}
    				},
    				scope: this
    			}
            });
        }
        return this.regionsCombobox;
    },
    
    //----------------------------------------------------------------- Helper methods
    
    createCriteriaLabel: function(label) {
        return new Ext.Container({
            border: false, 
            cls: 'critLabelCls', 
            html: label
        });
    },
    
    //----------------------------------------------------------------- Actions.
    
    getResetAction: function() {
        if(!this.resetAction) {
            this.resetAction = new Ext.Action({
                text: Openwis.i18n('Common.Btn.Reset'),
                iconCls: 'iconBtnReset',
                cls: 'openwisAction homePageSearchAction',
				scope: this,
				handler: function() {
					this.reset();
				}
            });
        }
        return this.resetAction;
    },
    
    getSearchAction: function() {
        if(!this.searchAction) {
            this.searchAction = new Ext.Action({
                text: Openwis.i18n('Common.Btn.Search'),
                iconCls: 'iconBtnSearch',
                cls: 'openwisAction homePageSearchAction',
				scope: this,
				handler: function() {
					if (this.validate())
					{
						var params = this.buildSearchParams();
					    var url = this.searchUrl();
					    this.targetResult.loadSearchResults(url, params);
					}
				}
            });
        }
        return this.searchAction;
    },
    
    setRegionToUserDefined: function() {
        if(this.getRegionsCombobox().getStore().getCount() > 0) {
            this.getRegionsCombobox().setValue('UserDefined');
        } else {
            this.getRegionsCombobox().getStore().load({
                callback: function() {
                    this.getRegionsCombobox().setValue('UserDefined');
                },
                scope: this
            });
        }
    },

	getLongitude : function(lg) {
		var de = (lg + 180) % 360;
		while(de<0) {
			de += 360;
		}
		return de - 180
	},
    
    onGuiChanged: function() {
        this.fireEvent('guiChanged', false, true);
    }
    
});