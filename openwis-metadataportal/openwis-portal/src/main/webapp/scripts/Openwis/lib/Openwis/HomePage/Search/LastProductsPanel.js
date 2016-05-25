Ext.ns('Openwis.HomePage.Search');

/**
 *    => DATA SERVICE (last processed requests).
 */
Openwis.HomePage.Search.LastProductsPanel = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    border: false,
			bodyStyle: 'padding:10px',
			cls: 'homePageMenuPanelCls'
		});
		Openwis.HomePage.Search.LastProductsPanel.superclass.initComponent.apply(this, arguments);
		
		//Initialize static elements.
		this.initialize();
		if (g_userConnected) {			
			this.getInfosAndInitialize();
		}
	},
	
	initialize: function() {
	    this.add(new Ext.Container({
	        cls: 'top title', 
	        height: 19, 
	        html: Openwis.i18n('HomePage.Search.LastProducts.Title')
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
    
    refreshContent: function(processedRequests) {
        var processedRequestsSize = processedRequests.length;
        
        //Gets all previous ids.
        var existingLastProducts = [];
        for(var i = 0; i < this.getContentPanel().items.length; i++) {
	        existingLastProducts.push(this.getContentPanel().get(i).id);
	    }
        
	    for(var i = 0; i < processedRequests.length; i++) {
	    	var lastProductDto = processedRequests[i];
	        var id = 'last-product-' + lastProductDto.id;
	        if(!this.getContentPanel().findById(id)) {
	        	var content = Openwis.Utils.Date.formatDateTimeUTC(lastProductDto.date) + ' - ' + Ext.util.Format.htmlEncode(lastProductDto.name);
	            this.getContentPanel().insert(i, new Ext.Container({cls: 'line', style: 'cursor:pointer;', id: id, html: content}));
	        } else {
	            existingLastProducts.remove(id);
	        }
	    }
	    
	    Ext.each(existingLastProducts, function(id) {
            this.getContentPanel().remove(id);
        }, this);
	    
	    var newSize = this.getContentPanel().items.length;
        
	    this.doLayout();
	    
	    this.fireEvent('sizeChanged');
	    
	    // Add click listeners on products
	    for(var i = 0; i < processedRequests.length; i++) {
	        var id = 'last-product-' + processedRequests[i].id;
	        var el = Ext.get(id);
	        if (!el) {
	        	continue;
	        }
	    	el.on('click', function(evt, el, o){
	    			this.fireEvent('productClicked', o.processedRequest);
	    		}, this, {
	    			processedRequest: processedRequests[i]
	    		});
	    }
	    
	    var task = new Ext.util.DelayedTask(this.getInfosAndInitialize, this);
	    task.delay(600000);
    },
    
    getInfosAndInitialize: function() {
       new Openwis.Handler.GetWithoutError({
			url: configOptions.locService+ '/xml.get.home.page.last.products',
            maskEl: this.getContentPanel().el,
            useLoadMask: (this.getContentPanel().el != undefined),
			listeners: {
				success: function(processedRequests) {
					this.refreshContent(processedRequests);
				},
				scope: this
			}
		}).proceed();
    }
});