Ext.ns('Openwis.HomePage.Search');

/**
 *    Last updates metadata. (latestupdated).
 */
Openwis.HomePage.Search.WhatsNewPanel = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    border: false,
			bodyStyle: 'padding:10px',
			cls: 'homePageMenuPanelCls'
		});
		Openwis.HomePage.Search.WhatsNewPanel.superclass.initComponent.apply(this, arguments);
		
		//Initialize static elements.
		this.initialize();
		
		this.getInfosAndInitialize();
	},
	
	
	initialize: function() {
	    this.add(new Ext.Container({
	        cls: 'top title', 
	        height: 19, 
	        html: Openwis.i18n('HomePage.Search.WhatsNew.Title')
	    }));
	    
	    this.add(this.getContentPanel());
	    
	    this.add(new Ext.Container({cls: 'bottom', height: 19}));
    },
    
    getContentPanel: function() {
        if(!this.contentPanel) {
            this.contentPanel = new Ext.Container({
                cls: 'content'
            });
        }
        return this.contentPanel;
    },
    
    refreshContent: function(productMetadatas) {
        var productMetadatasSize = productMetadatas.length;
        
        //Gets all previous ids.
        var existingProductMetadatas = [];
        
        // Ensure content panel is properly loaded before filling the items
        if (!this.getContentPanel().items) {
        	var task = new Ext.util.DelayedTask(this.getInfosAndInitialize, this);
    	    task.delay(5000);
    	    return;
    	}
        if (this.getContentPanel().items) { // avoid getContentPanel().items null
	        for(var i = 0; i < this.getContentPanel().items.length; i++) {
		        existingProductMetadatas.push(this.getContentPanel().get(i).id);
		    }
        }
        
	    for(var i = 0; i < productMetadatas.length; i++) {
	        var id = 'whats-new-' + productMetadatas[i].id;
	        if(!this.getContentPanel().findById(id)) {
	            var htmlContent = '<a>' + Ext.util.Format.htmlEncode(productMetadatas[i].title) + '</a>';
	        	var container = new Ext.Container({cls: 'line', style: 'cursor:pointer;', id: id, html: htmlContent});
	        	this.getContentPanel().insert(i, container);
	        } else {
	            existingProductMetadatas.remove(id);
	        }
	    }
	    
	    Ext.each(existingProductMetadatas, function(id) {
            this.getContentPanel().remove(id);
        }, this);
	    
	    this.doLayout();
	    
	    this.fireEvent('sizeChanged');
	    
	    // Add click listeners on metadata
	    for(var i = 0; i < productMetadatas.length; i++) {
	        var id = 'whats-new-' + productMetadatas[i].id;
	        var el = Ext.get(id);
	        if (!el) {
	        	continue;
	        }
	    	el.on('click', function(evt, el, o){
	    			this.fireEvent('metadataClicked', o.productMetadata);
	    		}, this, {
	    			productMetadata: productMetadatas[i]
	    		});
	    }
	    
	    var task = new Ext.util.DelayedTask(this.getInfosAndInitialize, this);
	    task.delay(600000);
    },
    
    getInfosAndInitialize: function() {
        new Openwis.Handler.GetWithoutError({
			url: configOptions.locService+ '/xml.get.home.page.whats.new',
            maskEl: this.getContentPanel().el,
            useLoadMask: (this.getContentPanel().el != undefined),
			listeners: {
				success: function(productMetadatas) {
					this.refreshContent(productMetadatas);
				},
				scope: this
			}
		}).proceed();
    }
});
