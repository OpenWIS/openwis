Ext.ns('Openwis.Common.Components');

Openwis.Common.Components.GeographicalExtentSelection = Ext.extend(Ext.form.Field, {

    defaultAutoCreate : {tag: "div"},
	
    geoWKTSelection: null,
    
    wmsUrl: null,
    
    layerName: null,
    
    geoExtentType: null,
    
    scrollRef: null,
    
    maxResolution : null,
    
    readOnly: false,
    
    width: 350,
    
	height: 200,
	
	initComponent: function() {
		Ext.apply(this, 
		{
			border: false,
			allowBlank: false,
			listeners : {
				beforedestroy: function() {
				    if(this.scrollRef) {
					    Ext.EventManager.un(this.scrollRef, 'scroll', this.updateMapSize, this);
					}
				},
				afterrender: function() {
				    this.scrollRef = this.scrollRef || window.document.body;
				    Ext.EventManager.on(this.scrollRef, 'scroll', this.updateMapSize, this);
				},
				scope: this
			}
		});
		Openwis.Common.Components.GeographicalExtentSelection.superclass.initComponent.apply(this, arguments);
		
	    this.addEvents("valueChanged");
	},
	
	//--------------------------------------------------------------------- Overriden methods.
	
	onRender: function(ct, position){
        Openwis.Common.Components.GeographicalExtentSelection.superclass.onRender.call(this, ct, position);
        
        // delay map creation in case the page is not fully loaded
        this.assignMap();      
    },
   
    // Create the map and assign the map panel to the map
    assignMap: function() {
    	if (!pageLoaded) {
        	var task = new Ext.util.DelayedTask(this.assignMap, this);
        	task.delay(200);
    	} else {
    		this.getMapPanel().doLayout();
    		this.mapLoaded = true;
    	}	
    },
    
    getRawValue: function() {
		return this.feature;
	},
	
	getValue: function() {
		return this.getRawValue();
	},
	
	buildValue: function() {
		return this.getValue();
	},
	
	validateValue : function(value){
        if (value == null) { // if it has no value
             if (this.allowBlank) {
                 this.clearInvalid();
                 return true;
             } else {
                 this.markInvalid(Openwis.i18n('Common.Extent.Geo.Mandatory'));
                 return false;
             }
        }
        return true;
    },
    
    reset: function() {
	    this.getVector().destroyFeatures();
	    this.feature = null;
	    this.getMap().zoomToMaxExtent();
	},
	
	//----------------------------------------------------------------- Initialization of the panels.
    
	getMapPanel: function() {
	    if(!this.mapPanel) {
	        this.mapPanel = new GeoExt.MapPanel({
	            renderTo: this.el,
	            height: this.height,
                width: this.width,
                map: this.getMap(),
                tbar: this.getMapToolbar(),
                tbarCssClass: 'mapCtrlToolbar'
	        });
	    }
	    return this.mapPanel;	    
	},
	
	getMap: function() {
	    if(!this.map) {
	    	this.mapLoaded = false;
            // Set options to the map
            // TODO handle srs options for the map
            var mapOptions = {
                controls: []
                /*,
                projection: "EPSG:4326",
                units: "m"*/
            };
            if (this.maxExtent != null) {
                mapOptions.maxExtent = this.maxExtent;
            }
            if (this.maxResolution) {
                mapOptions.maxResolution = this.maxResolution;
            }
            
            // Create the map
            this.map = new OpenLayers.Map(mapOptions);
            
            var wms = new OpenLayers.Layer.WMS("Background layer",
                this.wmsUrl,
                {layers: this.layerName, format: 'image/png'}, 
                {isBaseLayer: true}
            );

            this.map.addLayers([wms, this.getVector()]);

            
             // Set default geoWKTSelection 
            if (this.geoWKTSelection != null ) {
                var ft = Openwis.Utils.Geo.WKTtoFeature(this.geoWKTSelection);   
                this.getVector().addFeatures([ft]);
                this.feature = this.geoWKTSelection;
            }
        }
        return this.map;
	},
	
	getVector: function() {
	    if(!this.vector) {
	        this.vector = new OpenLayers.Layer.Vector("Vector layer");
            
            this.vector.events.on({
                // Clean existing features before drawing
                'sketchstarted': function() {
                    this.vector.destroyFeatures();
                },
                scope :this
            });
	    
	    }
	    return this.vector;
	},
	
	getMapToolbar: function() {
	    if(!this.mapToolbar) {
    	    var action = {};
    	    this.mapToolbar = [];
    	    
    	    action = new GeoExt.Action({
    	    	control: new OpenLayers.Control.ZoomToMaxExtent(),
                map: this.getMap(),
                iconCls: 'mapCtrlZoomFull',
                tooltip: {
                    title: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomFull.Title"), 
                    text: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomFull.Text")
                }
            });
        
            this.mapToolbar.push(action);
    
            this.mapToolbar.push("-");
            
            action = new GeoExt.Action({
                control: new OpenLayers.Control.ZoomBox(),
                map: this.getMap(),
                toggleGroup: "move",
                allowDepress: false,
                iconCls: 'mapCtrlZoomIn',
                tooltip: {
                    title: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomIn.Title"), 
                    text: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomIn.Text")
                }
            });
    
            this.mapToolbar.push(action);
    
            action = new GeoExt.Action({
                control:  new OpenLayers.Control.ZoomBox({
                    displayClass: 'ZoomOut',
                    out: true
                }),
                map: this.getMap(),
                toggleGroup: "move",
                allowDepress: false,
                iconCls: 'mapCtrlZoomOut',
                tooltip: {
                    title: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomOut.Title"), 
                    text: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomOut.Text")
                }
            });
    
            this.mapToolbar.push(action);
    
            this.mapToolbar.push("-");
            
             action = new GeoExt.Action({
                control: new OpenLayers.Control.DragPan({
                        isDefault: true
                    }),
                toggleGroup: "move",
                allowDepress: false,
                pressed: true,
                map: this.getMap(),
                iconCls: 'mapCtrlDrag',
                tooltip:  {
                    title: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.DragPan.Title"), 
                    text: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.DragPan.Text")
                }
            });
    
            this.mapToolbar.push(action);
    
            if(!this.readOnly) {
            
                this.mapToolbar.push("-");
                
                // Draw box control (default)
                action = new GeoExt.Action({
                    control: new OpenLayers.Control.DrawFeature(
                        this.getVector(), OpenLayers.Handler.RegularPolygon, 
                        {
                            handlerOptions: {
                                irregular: true,
                                sides: 4
                            },
                            featureAdded: function(feature) {
                                this.scope.feature = Openwis.Utils.Geo.featureToWKT(feature);
        						this.scope.fireEvent("valueChanged", feature.geometry.getBounds());
                            }, 
                            scope : this
                        }
                    ),
                    // text: "Draw regular polygon",
                    map: this.getMap(),
                    toggleGroup: "move",
                    allowDepress: false,
                    iconCls: "mapCtrlDrawRectangleExtent",
                    tooltip: Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.DrawExtent.Tooltip"),
                    group: "draw"
                });
                this.mapToolbar.push(action);
                
                // Draw polygon control (optional)
                if (this.geoExtentType === "POLYGON") {
                    action = new GeoExt.Action({
                        control: new OpenLayers.Control.DrawFeature(
                            this.getVector(), OpenLayers.Handler.Polygon,
                            {
                                featureAdded: function(feature) {
                                    this.scope.feature = Openwis.Utils.Geo.featureToWKT(feature);
        						    this.scope.fireEvent("valueChanged", feature.geometry.getBounds());
                                }, 
                                scope : this
                            }
                        ),
                        // text: "Draw poly"?
                        map: this.getMap(),
                        // FIXME button options, style
                        toggleGroup: "move",
                        allowDepress: false,
                        iconCls: "mapCtrlDrawPolygonExtent",
                        tooltip: "Draw polygon", // FIXME il8n
                        group: "draw"
                    });
                    this.mapToolbar.push(action);
                }
            }
	    }
	    return this.mapToolbar;
        
	},
	
	updateMapSize: function() {
		this.getMap().updateSize();
	},
	
	/**
	 * Extent = left, right, bottom, top
	 */
	drawExtent: function(extent) {
        this.getVector().destroyFeatures();
        if (extent.left > extent.right) {
        	var bounds1 = new OpenLayers.Bounds(extent.left, extent.bottom, 180, extent.top);
        	var geo1 = bounds1.toGeometry();
        	var polFeature1 = new OpenLayers.Feature.Vector(geo1);
        	
        	var bounds2 = new OpenLayers.Bounds(-180, extent.bottom, extent.right, extent.top);
        	var geo2 = bounds2.toGeometry();
        	var polFeature2 = new OpenLayers.Feature.Vector(geo2);

        	this.getVector().addFeatures(polFeature1);
        	this.getVector().addFeatures(polFeature2);
        	
        	var geo = new OpenLayers.Geometry.MultiPolygon([geo1,geo2]);
        	var polFeature = new OpenLayers.Feature.Vector(geo);
        	this.feature = Openwis.Utils.Geo.featureToWKT(polFeature);
        } else {
        	var bounds = new OpenLayers.Bounds(extent.left, extent.bottom, extent.right, extent.top);
        	var polFeature = new OpenLayers.Feature.Vector(bounds.toGeometry());
        	this.getVector().addFeatures([polFeature]);
        	this.feature = Openwis.Utils.Geo.featureToWKT(polFeature);
        }
        this.getVector().refresh();
    },
    
    /**
	 * Extent = left, right, bottom, top
	 */
    zoomToExtent: function(extent) {
    	if (extent.left>extent.right) {
    		this.getMap().zoomToMaxExtent();
    	} else {
	        var bounds = new OpenLayers.Bounds(extent.left, extent.bottom, extent.right, extent.top);
	        this.getMap().zoomToExtent(bounds);
    	}
    }
});
