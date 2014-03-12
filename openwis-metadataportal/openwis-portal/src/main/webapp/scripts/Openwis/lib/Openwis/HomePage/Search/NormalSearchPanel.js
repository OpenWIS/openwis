Ext.ns('Openwis.HomePage.Search');

Openwis.HomePage.Search.NormalSearchPanel = Ext.extend(Openwis.HomePage.Search.AbstractSearchPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    title: Openwis.i18n('HomePage.Search.Normal.Title')
		});
		Openwis.HomePage.Search.NormalSearchPanel.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	
	initialize: function() {
	    //What.
	    this.add(this.getWhatLabel());
	    this.add(this.getWhatTextField());
	    
	    //Where
	    this.add(this.getWhereLabel());
	    this.add(this.getMapPanel());
	    this.add(this.getRegionsCombobox());
	
	    //FieldSet options.
	    this.add(this.getOptionsPanel());
	    
	    //Buttons.
	    this.add(this.getButtonsPanel());
    },
    
    //----------------------------------------------------------------- Initialization of the panels.
    
    getWhatTextField: function() {
        if(!this.whatTextField) {
            this.whatTextField = new Ext.form.TextField({
                name: 'what',
				allowBlank: true,
				width: 210,
                listeners: {
                  specialkey: function(f,e){
                    if (e.getKey() == e.ENTER) {
                      this.getSearchAction().execute();
                    }
                  },
                  scope: this
                },
                render: function() {
                      Ext.form.TextField.superclass.render.apply(this,arguments);
						
						this.tTip = new Ext.ToolTip({
						       target: this.el,
						       width: 120,
						       html: Openwis.i18n('HomePage.Search.Normal.What.ToolTip'),
						       dismissDelay: 5000 // auto hide after 5 seconds
						   });
                }
            });
        }
        return this.whatTextField;
    },
    
    //----------------------------------------------------------------- Actions.
    
    reset: function() {
        this.getWhatTextField().reset();
        this.getMapPanel().reset();
        this.getRegionsCombobox().reset();
        this.getSortDirectionCombobox().reset();
        this.getHitsCombobox().reset();
    },
    
    buildSearchParams: function() {
        var params = {};
		params.any = this.getWhatTextField().getValue();
		params.sortBy = this.getSortDirectionCombobox().getValue();
		params.hitsPerPage = this.getHitsCombobox().getValue();
		params.from = 0;
		params.to = parseInt(params.hitsPerPage) - 1;
		params.similarity = '0.8';
		
		if(this.getRegionsCombobox().getValue() != 'Any') {
		    params.relation = 'overlaps';
		    
		    var geometry = this.getMapPanel().getRawValue();
		    if(geometry) {
		        params.attrset = 'geo';
    		    params.geometry = geometry;
    		} else {
    			params.geometry = '';
		        params.attrset = 'normal';
    		}
		    
		    if(this.getRegionsCombobox().getValue() != 'UserDefined' && this.getRegionsCombobox().getValue() != '') {
		        params.region = this.getRegionsCombobox().getValue();
		    } else {
		    	params.region = '';
		    }
		} else {
			params.relation = '';
		}
		
		// Reset Advanced SearchParams
		params.all = '';
		params.region = '';
		params.kind = '';
		params.or = '';
		params.without = '';
		params.phrase = '';
		params.extFrom = '';
		params.extTo = '';
		params.dateFrom = '';
		params.dateTo = '';
		params.category = '';
		params['abstract'] = '';
		params.siteId = '';
		params.title = '';
		params.digital = '';
		params.download = '';
		params.dynamic = '';
		params.paper = '';
		params.intermap = '';
		params.themekey = '';
		
		params.inspireOnly = '';
		params.inspireAnnex = '';
		//params.inspireSourceType = '';
		params.protocol = '';
		
		// Indicates that this search should reset the default parameters
		params.useSessionDefaults = 'false';

		return params;
    },
    
    searchUrl: function() {
        return configOptions.locService + '/main.search.embedded';
    },
    
    updateMapFields: function(bounds, setRegionToUserDefined) {
        if(setRegionToUserDefined) {
            this.setRegionToUserDefined();
        }
        if (bounds.left>bounds.right) {
        	this.getMapPanel().drawExtent(bounds);
        	this.getMapPanel().zoomToExtent(bounds);
        }
    },

    validate: function() {
    	return true;
    }
});