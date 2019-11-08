Ext.ns('Openwis.Utils.Geo');

Openwis.Utils.Geo.featureToWKT = function (feature) {
    var wktFormat = new OpenLayers.Format.WKT();
    var wkt = wktFormat.write(feature);
    return wkt;
};
	
Openwis.Utils.Geo.WKTtoFeature = function (wkt) {
    var wktFormat = new OpenLayers.Format.WKT();
    var feature = wktFormat.read(wkt);
    return feature;
};
	
Openwis.Utils.Geo.getBoundsFromWKT = function(wkt) {
    var feature = Openwis.Utils.Geo.WKTtoFeature(wkt);  
    return feature.geometry.getBounds();
};