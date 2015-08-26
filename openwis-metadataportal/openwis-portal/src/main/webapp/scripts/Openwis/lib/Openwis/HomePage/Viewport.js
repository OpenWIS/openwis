Ext.ns('Openwis.HomePage');

Openwis.HomePage.Viewport = Ext.extend(Ext.Viewport, {
	
	initComponent: function() {
		Ext.apply(this, {
			border: false,
			autoScroll: true,
			layout: 'fit',
			listeners : {
				afterlayout: function() {
				    this.relayoutViewport(true, true);
				},
				scope: this
			}
		});
		Openwis.HomePage.Viewport.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
	    // West Panel to center the main content.
		this.getViewportPanel().add(this.getWestPanel());
		
		// Center Panel for the main content.
		this.getCenterPanel().add(this.getHeaderPanel());
		
		this.getContentPanel().add(this.getContentWestPanel());
		this.getContentPanel().add(this.getContentCenterPanel());
		
		this.getCenterPanel().add(this.getContentPanel());
		
		this.getViewportPanel().add(this.getCenterPanel());
		
		// East Panel to center the main content.
		this.getViewportPanel().add(this.getEastPanel());
		
		this.add(this.getViewportPanel());
		
		this.add(this.getStartPanel());
		
	},
	
	getViewportPanel: function() {
	    if(!this.viewportPanel) {
	        this.viewportPanel = new Ext.Panel({
        		/*region: 'center',*/
				layout:'border',
				border:false,
				autoScroll: false,
				cls: 'viewportCls'
        	});
	    }
	    return this.viewportPanel;
	},
	
	getSearchPanel: function() {
	    if(!this.searchPanel) {
	        this.searchPanel = new Openwis.HomePage.Search.SearchPanel({
	            targetResult: this.getSearchResultsPanel(),
	            listeners: {
	                guiChanged: function() {
    				    this.relayoutViewport(false, true);
    				},
	                searchResultsDisplayed: function() {
    				    this.relayoutViewport(true, true);
    				},
	                scope: this
	            }
	        });
	    }
	    return this.searchPanel;
	},
	
	getWhatsNewPanel: function() {
	    if(!this.whatsNewPanel) {
	        this.whatsNewPanel = new Openwis.HomePage.Search.WhatsNewPanel({
	        	searchResultsPanel: this.getSearchResultsPanel(),
		        listeners : {
		        	sizeChanged: function() {
					    this.relayoutViewport(false, true);
					},
					metadataClicked: function(productMetadata) {
						this.doSearch(productMetadata);
					},
					scope: this
				}
	        });
	    }
	    return this.whatsNewPanel;
	},
	
	doSearch : function(productMetadata) {
		var params = {};
    	params.any = productMetadata.urn;
        params.sortBy = 'relevance';
        params.hitsPerPage = 10;
        params.from = 0;
        params.to = 9;
        params.relation = 'overlaps';
        var url = configOptions.locService + '/main.search.embedded';
        this.getSearchResultsPanel().load(url, params);
	},
	
	getLastProductsPanel: function() {
	    if(!this.lastProductsPanel) {
	        this.lastProductsPanel = new Openwis.HomePage.Search.LastProductsPanel({
		        listeners : {
		        	sizeChanged: function() {
					    this.relayoutViewport(false, true);
					},
					productClicked: function(lastProduct) {
						this.viewLastProduct(lastProduct);
					},
					scope: this
				}
	        });
	        this.lastProductsPanel.setVisible(g_userConnected);
	    }
	    return this.lastProductsPanel;
	},
	
	viewLastProduct: function(lastProduct) {
	    var requestType = lastProduct.requestType;
	    if(requestType == 'ADHOC') {
	        var wizard = new Openwis.RequestSubscription.Wizard();
            wizard.initialize(lastProduct.productMetadataURN, 'ADHOC', lastProduct.extractMode == 'CACHE', 'View', lastProduct.requestId, false);
	    } else if(requestType == 'SUBSCRIPTION')  {
		    var wizard = new Openwis.RequestSubscription.Wizard();
            wizard.initialize(lastProduct.productMetadataURN, 'SUBSCRIPTION', lastProduct.extractMode == 'CACHE', 'Edit', lastProduct.requestId, false);
	    }
	},
	
	getSearchResultsPanel: function() {
	    if(!this.searchResultsPanel) {
	        this.searchResultsPanel = new Openwis.HomePage.Search.SearchResultsPanel({
	            listeners: {
	                searchResultsDisplayed: function() {
    				    this.relayoutViewport(true, true);
    				},
	                scope: this
	            }
	        });
	    }
	    return this.searchResultsPanel;
	},
	
	getContentCenterHeaderPanel: function() {
	    if(!this.contentCenterHeaderPanel) {
	        this.contentCenterHeaderPanel = new Ext.Panel({
        		region:'north',
        		border: false,
        		boxMaxHeight: 20,
        		width: 664,
        		cls: 'homePageMainContentHeader top_hdp_txt',
        		html: Openwis.i18n('HomePage.Main.Header')
        	});
	    }
	    return this.contentCenterHeaderPanel;
	},
	
	//-- The basic layout page.
	getContentWestPanel: function() {
	    if(!this.contentWestPanel) {
	        this.contentWestPanel = new Ext.Panel({
        		region:'west',
        		layout: 'table',
            	width: 300,
            	border: false,
            	cls: 'westContentPanel',
        		layoutConfig: {
        		    columns: 1
        		},
        		defaults: {
            		style: {
            			marginLeft: '28px',
                        marginRight: '28px',
                        width: '244px'
        	        }
                },
        		items:
        		[
        		    this.getSearchPanel(),
        		    this.getWhatsNewPanel(),
        		    this.getLastProductsPanel()
        		]
        	});
	    }
	    return this.contentWestPanel;
	},
	
	getContentCenterPanel: function() {
	    if(!this.contentCenterPanel) {
	        this.contentCenterPanel = new Ext.Panel({
	            region:'center',
        		border: false,
        		cls: 'homePageMainContent',
        		items:
        		[
        		    // this.getContentCenterHeaderPanel(),
        		    this.getSearchResultsPanel()
        		]
        	});
	    }
	    return this.contentCenterPanel;
	},
	
	getContentPanel: function() {
	    if(!this.contentPanel) {
	        this.contentPanel = new Ext.Panel({
				region:'center',
				autoScroll:true,
				border:false,
				layout: 'border',
				bodyCssClass: 'contentPanelCls'
        	});
	    }
	    return this.contentPanel;
	},
	
	
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
	
	getCenterPanel: function() {
		if(!this.centerPanel) {
			this.centerPanel = new Ext.Panel({
				cls: 'body-center-panel',
				region: 'center',
				border: false,
				width: 992,
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
				html: '&nbsp;',
            	height: 1200
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
        
	    if(relayoutWidth) {
            var contentWidth = 992;
            //var contentHeight = this.el.dom.scrollHeight; 
        	
        	var size = this.getEl().getViewSize(), w = size.width;
        	
        	var westP = this.getWestPanel();
        	var eastP = this.getEastPanel();
        	var centerP = this.getCenterPanel();
        	
        	if (w < contentWidth) {
        		westP.setWidth(0);
        		eastP.setWidth(0);
        	} else {
        		var panelSideWidth = (w - contentWidth) / 2; /**/
        		westP.setWidth(panelSideWidth - 8);
        		eastP.setWidth(panelSideWidth - 9);
        	}
        	
        	this.getViewportPanel().boxMaxWidth = w - 17;
        	this.doLayout();
		}
		
		if(relayoutHeight) {
        	var leftPanelHeight = this.getSearchPanel().getHeight() + 
                this.getWhatsNewPanel().getHeight() + this.getLastProductsPanel().getHeight() + 75;
            
            var contentPanelHeight = this.getSearchResultsPanel().getHeight() + 350;
            this.getCenterPanel().boxMinHeight = contentPanelHeight;
            
            var compMinHeight = (leftPanelHeight > contentPanelHeight) ? leftPanelHeight : contentPanelHeight;
            compMinHeight += 115;
            
        	this.getViewportPanel().boxMinHeight = compMinHeight;
        	
        	this.doLayout();
	    }
				
		this.resumeEvents();
	},
	
	getStartPanel : function() {
		if (!this.startPanel) {
			this.startPanel = new Ext.Panel({
				region : 'center',
				autoScroll : true,
				border : false,
				layout : 'fit',
				width: '100%',
				id : 'main-center',
				items : [ this.getStartSearchPanel(),
						this.getStartMapPanel() ]
			});
		}
		return this.startPanel;
	},
	
	getStartSearchPanel : function() {
		if (!this.startSearchPanel) {
			this.startSearchPanel = new Openwis.HomePage.Search.StartSearchPanel(
					{
						targetResult : this.getSearchResultsPanel(),
						listeners : {
							guiChanged : function() {
								this.relayoutViewport(false,
										true);
							},
							searchResultsDisplayed : function() {
								this.relayoutViewport(true,
										true);
							},
							scope : this
						}
					});
		}
		return this.startSearchPanel;
	},
	
	getStartMapPanel : function() {
		if (!this.startMapPanel) {
			this.startMapPanel = new Ext.Panel({
				layout : 'fit',
				width : '100%',
				border : false,
				cls : 'start-map'
			});
		}
		return this.startMapPanel;
	},

	getStartRemoteSearchWindow : function(srv, title) {
		var startRemoteSearchPanel = this.getStartRemoteSearchPanel();
		startRemoteSearchPanel.getServerComboBox().setValue(srv);
		var startRemoteSearchWindow = new Ext.Window({
			title : title,
			layout : 'table',
			layoutConfig : {
				columns : 1
			},
			autoScroll : false,
			width : 260,
			closeAction : 'hide',
			cls : 'start-search-window tabSearchType',
			items : [ startRemoteSearchPanel ]
		});
		startRemoteSearchWindow.on('show', function() {
			startRemoteSearchWindow.center();
		});
		startRemoteSearchWindow.show();
	},
	
	getStartRemoteSearchPanel : function() {
		if (!this.startRemoteSearchPanel) {
			this.startRemoteSearchPanel = new Openwis.HomePage.Search.StartRemoteSearchPanel(
					{
						targetResult : this.getSearchResultsPanel()
					});
		}
		return this.startRemoteSearchPanel;
	}
});