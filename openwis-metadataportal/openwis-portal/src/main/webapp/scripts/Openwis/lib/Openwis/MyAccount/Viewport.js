Ext.ns('Openwis.MyAccount');

Openwis.MyAccount.Viewport = Ext.extend(Ext.Viewport, {
	
	initComponent: function() {
		Ext.apply(this, {
			border: false,
			layout: 'fit',
			autoScroll: true,
			listeners : {
				afterlayout: function() {
				    this.relayoutViewport(true, true);
				},
				scope: this
			}
		});
		Openwis.MyAccount.Viewport.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		// West Panel to center the main content.
		this.getViewportPanel().add(this.getWestPanel());
		
		// Center Panel for the main content.
		this.getCenterPanel().add(this.getHeaderPanel());
		
		this.getContentPanel().add(this.getBrowserPanel());
		this.getCenterPanel().add(this.getContentPanel());
		
		this.getViewportPanel().add(this.getCenterPanel());
		
		// East Panel to center the main content.
		this.getViewportPanel().add(this.getEastPanel());

		this.add(this.getViewportPanel());
	},
	
	getViewportPanel: function() {
	    if(!this.viewportPanel) {
	        this.viewportPanel = new Ext.Panel({
				layout:'border',
				border:false,
				autoScroll: false,
				cls: 'viewportCls'
        	});
	    }
	    return this.viewportPanel;
	},
	
	//-- The content panels.
	getHeaderPanel: function() {
		if(!this.headerPanel) {
			this.headerPanel = new Ext.Container({
				region: 'north',
				border: false,
				contentEl: 'header',
				cls: 'headerCtCls'
			});
		}
		return this.headerPanel;
	},
	
	getContentPanel: function() {
		if(!this.contentPanel) {
			this.contentPanel = new Ext.Panel({
				region: 'center',
				autoScroll:true,
				border: false,
				layout: 'fit'
			});
		}
		return this.contentPanel;
	},
	
	getBrowserPanel: function() {
		if(!this.browserPanel) {
			this.browserPanel = new Openwis.MyAccount.Browser({
	            width: 993,
				listeners : {
					tabchange: function() {
					    this.relayoutViewport(true, true);
					},
					guiChanged: function() {
    				    this.relayoutViewport(true, true);
    				},
					scope: this
				}
			});
		}
		return this.browserPanel;
	},
		
	//-- First panels to center the page.
	getCenterPanel: function() {
		if(!this.centerPanel) {
			this.centerPanel = new Ext.Panel({
				cls: 'body-center-panel',
				region: 'center',
				border: false,
				width: 993,
				layout: 'border'
			});
		}
		return this.centerPanel;
	},
	
	getWestPanel: function() {
		if(!this.westPanel) {
			this.westPanel = new Ext.Container({
				cls: 'body-west-panel',
				region: 'west',
				border: false,
				html: '&nbsp;'
			});
		}
		return this.westPanel;
	},
	
	getEastPanel: function() {
		if(!this.eastPanel) {
			this.eastPanel = new Ext.Container({
				cls: 'body-east-panel',
				region: 'east',
				border: false,
				html: '&nbsp;'
			});
		}
		return this.eastPanel;
	},
	
	relayoutViewport: function(relayoutWidth, relayoutHeight) {
        this.suspendEvents();
        
		var browserPanelHeight = 350; // min height
		var headerHeight = this.getBrowserPanel().header.getHeight();
		var activeTab = this.getBrowserPanel().activeGroup.activeTab;
		if (activeTab) {
			var items = activeTab.items;
			var activeComp = activeTab.items.items[0];
			if (activeComp) {
				browserPanelHeight = activeComp.getHeight();
			}
		}
		var bodyHeight = this.getHeight();
		if (headerHeight > browserPanelHeight) {
			browserPanelHeight = headerHeight;
		}
		var height = browserPanelHeight + 150;
		
	    if(relayoutWidth) {
            var contentWidth = 993;
        	
        	var size = this.getEl().getViewSize(), w = size.width;
        	
        	var westP = this.getWestPanel();
        	var eastP = this.getEastPanel();
        	var centerP = this.getCenterPanel();
        	
        	if (w < contentWidth) {
        		westP.setWidth(0);
        		eastP.setWidth(0);
        	} else {
        		var panelSideWidth = (w - contentWidth) / 2; /**/
        		// if scrollbar present
        		if (height > bodyHeight) {
        			panelSideWidth -= 8;
        		}
        		westP.setWidth(panelSideWidth);
        		eastP.setWidth(panelSideWidth);
        	}
        	
        	// if scrollbar present
        	if (height > bodyHeight) {
    			w -= 17;
    		}
        	
        	this.getViewportPanel().boxMaxWidth = w;
        	this.doLayout();
		}
		
		if(relayoutHeight) {
        	this.getViewportPanel().boxMinHeight = height;
        	this.doLayout();
	    }
				
		this.resumeEvents();
	}
});