Ext.ns('Openwis.HomePage.Search');

Openwis.HomePage.Search.SearchPanel = Ext.extend(Ext.TabPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    border:false,
            width: 250,
        	activeTab: ((showAdvancedSearchFirst) ? 1 : 0),
			bodyStyle: 'padding:5px',
			cls: 'tabSearchType',
			listeners: {
			    afterrender: function() {
			        this.addListener('tabchange',function() {
			            this.fireEvent('guiChanged', false, true);
			        }, this);
			         
			    },
			    scope: this
			}
		});
		Openwis.HomePage.Search.SearchPanel.superclass.initComponent.apply(this, arguments);
		
		//this.addEvents('guiChanged', 'searchResultsDisplayed');
		
		//Initialize static elements.
		this.initialize();
	},
		
	initialize: function() {
	    this.add(this.getNormalSearchPanel());
	    this.add(this.getAdvancedSearchPanel());
    },
    
    getNormalSearchPanel: function() {
        if(!this.normalSearchPanel) {
            this.normalSearchPanel = new Openwis.HomePage.Search.NormalSearchPanel({
                targetResult: this.targetResult,
                listeners: {
                    searchResultsDisplayed: function() {
                        this.fireEvent('searchResultsDisplayed');
                    },
    			    guiChanged: function() {
    			    	if (pageLoaded && this.normalSearchPanel.isLoaded() && this.normalSearchPanel.getMapPanel().mapLoaded) {
    			    		this.fireEvent('guiChanged');
    			    	}
    			    },
                    scope: this
                }
	        });
        }
        return this.normalSearchPanel;
    },
    
    getAdvancedSearchPanel: function() {
         if(!this.advancedSearchPanel) {
            this.advancedSearchPanel = new Openwis.HomePage.Search.AdvancedSearchPanel({
                targetResult: this.targetResult,
                listeners: {
                    searchResultsDisplayed: function() {
                        this.fireEvent('searchResultsDisplayed');
                    },
    			    guiChanged: function() {
    			    	if (pageLoaded && this.advancedSearchPanel.isLoaded() && this.advancedSearchPanel.getMapPanel().mapLoaded) {
    			    		this.fireEvent('guiChanged');
    			    	}
    			    },
                    scope: this
                }
	        });
        }
        return this.advancedSearchPanel;
    }
    
});