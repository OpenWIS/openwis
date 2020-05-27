Ext.namespace("GeoExt");
GeoExt.singleFile=true;(function(){var singleFile=(typeof GeoExt=="object"&&GeoExt.singleFile);
var scriptName=singleFile?"GeoExt.js":"lib/GeoExt.js";
var getScriptLocation=function(){var scriptLocation="";
var scripts=document.documentElement.getElementsByTagName("script");
for(var i=0,len=scripts.length;
i<len;
i++){var src=scripts[i].getAttribute("src");
if(src){var index=src.lastIndexOf(scriptName);
var pathLength=src.lastIndexOf("?");
if(pathLength<0){pathLength=src.length
}if((index>-1)&&(index+scriptName.length==pathLength)){scriptLocation=src.slice(0,pathLength-scriptName.length);
break
}}}return scriptLocation
};
if(!singleFile){var jsfiles=new Array("GeoExt/data/AttributeReader.js","GeoExt/data/AttributeStore.js","GeoExt/data/FeatureRecord.js","GeoExt/data/FeatureReader.js","GeoExt/data/FeatureStore.js","GeoExt/data/LayerRecord.js","GeoExt/data/LayerReader.js","GeoExt/data/LayerStore.js","GeoExt/data/ScaleStore.js","GeoExt/data/WMSCapabilitiesReader.js","GeoExt/data/WMSCapabilitiesStore.js","GeoExt/data/WFSCapabilitiesReader.js","GeoExt/data/WFSCapabilitiesStore.js","GeoExt/data/WMSDescribeLayerReader.js","GeoExt/data/WMSDescribeLayerStore.js","GeoExt/data/WMCReader.js","GeoExt/widgets/Action.js","GeoExt/data/ProtocolProxy.js","GeoExt/widgets/MapPanel.js","GeoExt/widgets/Popup.js","GeoExt/widgets/form.js","GeoExt/widgets/form/SearchAction.js","GeoExt/widgets/form/BasicForm.js","GeoExt/widgets/form/FormPanel.js","GeoExt/widgets/tips/SliderTip.js","GeoExt/widgets/tips/LayerOpacitySliderTip.js","GeoExt/widgets/tips/ZoomSliderTip.js","GeoExt/widgets/tree/LayerNode.js","GeoExt/widgets/tree/LayerLoader.js","GeoExt/widgets/tree/LayerContainer.js","GeoExt/widgets/tree/BaseLayerContainer.js","GeoExt/widgets/tree/OverlayLayerContainer.js","GeoExt/widgets/tree/LayerParamNode.js","GeoExt/widgets/tree/LayerParamLoader.js","GeoExt/widgets/LayerOpacitySlider.js","GeoExt/widgets/LegendImage.js","GeoExt/widgets/LegendWMS.js","GeoExt/widgets/LegendPanel.js","GeoExt/widgets/ZoomSlider.js","GeoExt/widgets/grid/FeatureSelectionModel.js");
var agent=navigator.userAgent;
var docWrite=(agent.match("MSIE")||agent.match("Safari"));
if(docWrite){var allScriptTags=new Array(jsfiles.length)
}var host=getScriptLocation()+"lib/";
for(var i=0,len=jsfiles.length;
i<len;
i++){if(docWrite){allScriptTags[i]="<script src='"+host+jsfiles[i]+"'><\/script>"
}else{var s=document.createElement("script");
s.src=host+jsfiles[i];
var h=document.getElementsByTagName("head").length?document.getElementsByTagName("head")[0]:document.body;
h.appendChild(s)
}}if(docWrite){document.write(allScriptTags.join(""))
}}})();Ext.namespace("GeoExt.data");
GeoExt.data.WMSCapabilitiesReader=function(meta,recordType){meta=meta||{};
if(!meta.format){meta.format=new OpenLayers.Format.WMSCapabilities()
}if(typeof recordType!=="function"){recordType=GeoExt.data.LayerRecord.create(recordType||meta.fields||[{name:"name",type:"string"},{name:"title",type:"string"},{name:"abstract",type:"string"},{name:"queryable",type:"boolean"},{name:"opaque",type:"boolean"},{name:"noSubsets",type:"boolean"},{name:"cascaded",type:"int"},{name:"fixedWidth",type:"int"},{name:"fixedHeight",type:"int"},{name:"minScale",type:"float"},{name:"maxScale",type:"float"},{name:"prefix",type:"string"},{name:"formats"},{name:"styles"},{name:"srs"},{name:"dimensions"},{name:"bbox"},{name:"llbbox"},{name:"attribution"},{name:"keywords"},{name:"identifiers"},{name:"authorityURLs"},{name:"metadataURLs"}])
}GeoExt.data.WMSCapabilitiesReader.superclass.constructor.call(this,meta,recordType)
};
Ext.extend(GeoExt.data.WMSCapabilitiesReader,Ext.data.DataReader,{attributionCls:"gx-attribution",read:function(request){var data=request.responseXML;
if(!data||!data.documentElement){data=request.responseText
}return this.readRecords(data)
},serviceExceptionFormat:function(formats){if(OpenLayers.Util.indexOf(formats,"application/vnd.ogc.se_inimage")>-1){return"application/vnd.ogc.se_inimage"
}if(OpenLayers.Util.indexOf(formats,"application/vnd.ogc.se_xml")>-1){return"application/vnd.ogc.se_xml"
}return formats[0]
},imageFormat:function(layer){var formats=layer.formats;
if(layer.opaque&&OpenLayers.Util.indexOf(formats,"image/jpeg")>-1){return"image/jpeg"
}if(OpenLayers.Util.indexOf(formats,"image/png")>-1){return"image/png"
}if(OpenLayers.Util.indexOf(formats,"image/png; mode=24bit")>-1){return"image/png; mode=24bit"
}if(OpenLayers.Util.indexOf(formats,"image/gif")>-1){return"image/gif"
}return formats[0]
},imageTransparent:function(layer){return layer.opaque==undefined||!layer.opaque
},readRecords:function(data){if(typeof data==="string"||data.nodeType){data=this.meta.format.read(data)
}var version=data.version;
var capability=data.capability||{};
var url=capability.request&&capability.request.getmap&&capability.request.getmap.href;
var layers=capability.layers;
var formats=capability.exception?capability.exception.formats:[];
var exceptions=this.serviceExceptionFormat(formats);
var records=[];
if(url&&layers){var fields=this.recordType.prototype.fields;
var layer,values,options,field,v;
for(var i=0,lenI=layers.length;
i<lenI;
i++){layer=layers[i];
if(layer.name){values={};
for(var j=0,lenJ=fields.length;
j<lenJ;
j++){field=fields.items[j];
v=layer[field.mapping||field.name]||field.defaultValue;
v=field.convert(v);
values[field.name]=v
}options={attribution:layer.attribution?this.attributionMarkup(layer.attribution):undefined,minScale:layer.minScale,maxScale:layer.maxScale};
if(this.meta.layerOptions){Ext.apply(options,this.meta.layerOptions)
}values.layer=new OpenLayers.Layer.WMS(layer.title||layer.name,url,{layers:layer.name,exceptions:exceptions,format:this.imageFormat(layer),transparent:this.imageTransparent(layer),version:version},options);
records.push(new this.recordType(values,values.layer.id))
}}}return{totalRecords:records.length,success:true,records:records}
},attributionMarkup:function(attribution){var markup=[];
if(attribution.logo){markup.push("<img class='"+this.attributionCls+"-image' src='"+attribution.logo.href+"' />")
}if(attribution.title){markup.push("<span class='"+this.attributionCls+"-title'>"+attribution.title+"</span>")
}if(attribution.href){for(var i=0;
i<markup.length;
i++){markup[i]="<a class='"+this.attributionCls+"-link' href="+attribution.href+">"+markup[i]+"</a>"
}}return markup.join(" ")
}});Ext.namespace("GeoExt.data");
GeoExt.data.LayerStoreMixin={map:null,reader:null,constructor:function(config){config=config||{};
config.reader=config.reader||new GeoExt.data.LayerReader({},config.fields);
delete config.fields;
var map=config.map instanceof GeoExt.MapPanel?config.map.map:config.map;
delete config.map;
if(config.layers){config.data=config.layers
}delete config.layers;
var options={initDir:config.initDir};
delete config.initDir;
arguments.callee.superclass.constructor.call(this,config);
if(map){this.bind(map,options)
}},bind:function(map,options){if(this.map){return
}this.map=map;
options=options||{};
var initDir=options.initDir;
if(options.initDir==undefined){initDir=GeoExt.data.LayerStore.MAP_TO_STORE|GeoExt.data.LayerStore.STORE_TO_MAP
}var layers=map.layers.slice(0);
if(initDir&GeoExt.data.LayerStore.STORE_TO_MAP){this.each(function(record){this.map.addLayer(record.get("layer"))
},this)
}if(initDir&GeoExt.data.LayerStore.MAP_TO_STORE){this.loadData(layers,true)
}map.events.on({changelayer:this.onChangeLayer,addlayer:this.onAddLayer,removelayer:this.onRemoveLayer,scope:this});
this.on({load:this.onLoad,clear:this.onClear,add:this.onAdd,remove:this.onRemove,update:this.onUpdate,scope:this});
this.data.on({replace:this.onReplace,scope:this})
},unbind:function(){if(this.map){this.map.events.un({changelayer:this.onChangeLayer,addlayer:this.onAddLayer,removelayer:this.onRemoveLayer,scope:this});
this.un("load",this.onLoad,this);
this.un("clear",this.onClear,this);
this.un("add",this.onAdd,this);
this.un("remove",this.onRemove,this);
this.data.un("replace",this.onReplace,this);
this.map=null
}},onChangeLayer:function(evt){var layer=evt.layer;
var recordIndex=this.findBy(function(rec,id){return rec.get("layer")===layer
});
if(recordIndex>-1){var record=this.getAt(recordIndex);
if(evt.property==="order"){if(!this._adding&&!this._removing){var layerIndex=this.map.getLayerIndex(layer);
if(layerIndex!==recordIndex){this._removing=true;
this.remove(record);
delete this._removing;
this._adding=true;
this.insert(layerIndex,[record]);
delete this._adding
}}}else{if(evt.property==="name"){record.set("title",layer.name)
}else{this.fireEvent("update",this,record,Ext.data.Record.EDIT)
}}}},onAddLayer:function(evt){if(!this._adding){var layer=evt.layer;
this._adding=true;
this.loadData([layer],true);
delete this._adding
}},onRemoveLayer:function(evt){if(this.map.unloadDestroy){if(!this._removing){var layer=evt.layer;
this._removing=true;
this.remove(this.getById(layer.id));
delete this._removing
}}else{this.unbind()
}},onLoad:function(store,records,options){if(!Ext.isArray(records)){records=[records]
}if(options&&!options.add){this._removing=true;
for(var i=this.map.layers.length-1;
i>=0;
i--){this.map.removeLayer(this.map.layers[i])
}delete this._removing;
var len=records.length;
if(len>0){var layers=new Array(len);
for(var j=0;
j<len;
j++){layers[j]=records[j].get("layer")
}this._adding=true;
this.map.addLayers(layers);
delete this._adding
}}},onClear:function(store){this._removing=true;
for(var i=this.map.layers.length-1;
i>=0;
i--){this.map.removeLayer(this.map.layers[i])
}delete this._removing
},onAdd:function(store,records,index){if(!this._adding){this._adding=true;
var layer;
for(var i=records.length-1;
i>=0;
--i){layer=records[i].get("layer");
this.map.addLayer(layer);
if(index!==this.map.layers.length-1){this.map.setLayerIndex(layer,index)
}}delete this._adding
}},onRemove:function(store,record,index){if(!this._removing){var layer=record.get("layer");
if(this.map.getLayer(layer.id)!=null){this._removing=true;
this.removeMapLayer(record);
delete this._removing
}}},onUpdate:function(store,record,operation){if(operation===Ext.data.Record.EDIT){var layer=record.get("layer");
var title=record.get("title");
if(title!==layer.name){layer.setName(title)
}}},removeMapLayer:function(record){this.map.removeLayer(record.get("layer"))
},onReplace:function(key,oldRecord,newRecord){this.removeMapLayer(oldRecord)
},destroy:function(){this.unbind();
GeoExt.data.LayerStore.superclass.destroy.call(this)
}};
GeoExt.data.LayerStore=Ext.extend(Ext.data.Store,GeoExt.data.LayerStoreMixin);
GeoExt.data.LayerStore.MAP_TO_STORE=1;
GeoExt.data.LayerStore.STORE_TO_MAP=2;Ext.namespace("GeoExt");
GeoExt.MapPanel=Ext.extend(Ext.Panel,{map:null,layers:null,center:null,zoom:null,extent:null,initComponent:function(){if(!(this.map instanceof OpenLayers.Map)){this.map=new OpenLayers.Map(Ext.applyIf(this.map||{},{allOverlays:true}))
}var layers=this.layers;
if(!layers||layers instanceof Array){this.layers=new GeoExt.data.LayerStore({layers:layers,map:this.map})
}if(typeof this.center=="string"){this.center=OpenLayers.LonLat.fromString(this.center)
}else{if(this.center instanceof Array){this.center=new OpenLayers.LonLat(this.center[0],this.center[1])
}}if(typeof this.extent=="string"){this.extent=OpenLayers.Bounds.fromString(this.extent)
}else{if(this.extent instanceof Array){this.extent=OpenLayers.Bounds.fromArray(this.extent)
}}GeoExt.MapPanel.superclass.initComponent.call(this)
},updateMapSize:function(){if(this.map){this.map.updateSize()
}},renderMap:function(){var map=this.map;
map.render(this.body.dom);
if(map.layers.length>0){if(this.center||this.zoom!=null){map.setCenter(this.center,this.zoom)
}else{if(this.extent){map.zoomToExtent(this.extent)
}else{map.zoomToMaxExtent()
}}}},afterRender:function(){GeoExt.MapPanel.superclass.afterRender.apply(this,arguments);
if(!this.ownerCt){this.renderMap()
}else{this.ownerCt.on("move",this.updateMapSize,this);
this.ownerCt.on({afterlayout:{fn:this.renderMap,scope:this,single:true}})
}},onResize:function(){GeoExt.MapPanel.superclass.onResize.apply(this,arguments);
this.updateMapSize()
},onBeforeAdd:function(item){if(typeof item.addToMapPanel==="function"){item.addToMapPanel(this)
}GeoExt.MapPanel.superclass.onBeforeAdd.apply(this,arguments)
},remove:function(item,autoDestroy){if(typeof item.removeFromMapPanel==="function"){item.removeFromMapPanel(this)
}GeoExt.MapPanel.superclass.remove.apply(this,arguments)
},beforeDestroy:function(){if(this.ownerCt){this.ownerCt.un("move",this.updateMapSize,this)
}if(!this.initialConfig.map||!(this.initialConfig.map instanceof OpenLayers.Map)){if(this.map&&this.map.destroy){this.map.destroy()
}}delete this.map;
GeoExt.MapPanel.superclass.beforeDestroy.apply(this,arguments)
}});
GeoExt.MapPanel.guess=function(){return Ext.ComponentMgr.all.find(function(o){return o instanceof GeoExt.MapPanel
})
};
Ext.reg("gx_mappanel",GeoExt.MapPanel);Ext.namespace("GeoExt.data");
GeoExt.data.WMSCapabilitiesStore=function(c){c=c||{};
GeoExt.data.WMSCapabilitiesStore.superclass.constructor.call(this,Ext.apply(c,{proxy:c.proxy||(!c.data?new Ext.data.HttpProxy({url:c.url,disableCaching:false,method:"GET"}):undefined),reader:new GeoExt.data.WMSCapabilitiesReader(c,c.fields)}))
};
Ext.extend(GeoExt.data.WMSCapabilitiesStore,Ext.data.Store);Ext.namespace("GeoExt.data");
GeoExt.data.LayerRecord=Ext.data.Record.create([{name:"layer"},{name:"title",type:"string",mapping:"name"}]);
GeoExt.data.LayerRecord.prototype.clone=function(id){var layer=this.get("layer")&&this.get("layer").clone();
return new this.constructor(Ext.applyIf({layer:layer},this.data),id||layer.id)
};
GeoExt.data.LayerRecord.create=function(o){var f=Ext.extend(GeoExt.data.LayerRecord,{});
var p=f.prototype;
p.fields=new Ext.util.MixedCollection(false,function(field){return field.name
});
GeoExt.data.LayerRecord.prototype.fields.each(function(f){p.fields.add(f)
});
if(o){for(var i=0,len=o.length;
i<len;
i++){p.fields.add(new Ext.data.Field(o[i]))
}}f.getField=function(name){return p.fields.get(name)
};
return f
};Ext.namespace("GeoExt");
GeoExt.Action=Ext.extend(Ext.Action,{control:null,map:null,uScope:null,uHandler:null,uToggleHandler:null,uCheckHandler:null,constructor:function(config){this.uScope=config.scope;
this.uHandler=config.handler;
this.uToggleHandler=config.toggleHandler;
this.uCheckHandler=config.checkHandler;
config.scope=this;
config.handler=this.pHandler;
config.toggleHandler=this.pToggleHandler;
config.checkHandler=this.pCheckHandler;
var ctrl=this.control=config.control;
delete config.control;
if(ctrl){if(config.map){config.map.addControl(ctrl);
delete config.map
}if((config.pressed||config.checked)&&ctrl.map){ctrl.activate()
}ctrl.events.on({activate:this.onCtrlActivate,deactivate:this.onCtrlDeactivate,scope:this})
}arguments.callee.superclass.constructor.call(this,config)
},pHandler:function(cmp){var ctrl=this.control;
if(ctrl&&ctrl.type==OpenLayers.Control.TYPE_BUTTON){ctrl.trigger()
}if(this.uHandler){this.uHandler.apply(this.uScope,arguments)
}},pToggleHandler:function(cmp,state){this.changeControlState(state);
if(this.uToggleHandler){this.uToggleHandler.apply(this.uScope,arguments)
}},pCheckHandler:function(cmp,state){this.changeControlState(state);
if(this.uCheckHandler){this.uCheckHandler.apply(this.uScope,arguments)
}},changeControlState:function(state){if(state){if(!this._activating){this._activating=true;
this.control.activate();
this._activating=false
}}else{if(!this._deactivating){this._deactivating=true;
this.control.deactivate();
this._deactivating=false
}}},onCtrlActivate:function(){var ctrl=this.control;
if(ctrl.type==OpenLayers.Control.TYPE_BUTTON){this.enable()
}else{this.safeCallEach("toggle",[true]);
this.safeCallEach("setChecked",[true])
}},onCtrlDeactivate:function(){var ctrl=this.control;
if(ctrl.type==OpenLayers.Control.TYPE_BUTTON){this.disable()
}else{this.safeCallEach("toggle",[false]);
this.safeCallEach("setChecked",[false])
}},safeCallEach:function(fnName,args){var cs=this.items;
for(var i=0,len=cs.length;
i<len;
i++){if(cs[i][fnName]){cs[i][fnName].apply(cs[i],args)
}}}});Ext.namespace("GeoExt","GeoExt.data");
GeoExt.data.LayerReader=function(meta,recordType){meta=meta||{};
if(!(recordType instanceof Function)){recordType=GeoExt.data.LayerRecord.create(recordType||meta.fields||{})
}GeoExt.data.LayerReader.superclass.constructor.call(this,meta,recordType)
};
Ext.extend(GeoExt.data.LayerReader,Ext.data.DataReader,{totalRecords:null,readRecords:function(layers){var records=[];
if(layers){var recordType=this.recordType,fields=recordType.prototype.fields;
var i,lenI,j,lenJ,layer,values,field,v;
for(i=0,lenI=layers.length;
i<lenI;
i++){layer=layers[i];
values={};
for(j=0,lenJ=fields.length;
j<lenJ;
j++){field=fields.items[j];
v=layer[field.mapping||field.name]||field.defaultValue;
v=field.convert(v);
values[field.name]=v
}values.layer=layer;
records[records.length]=new recordType(values,layer.id)
}}return{records:records,totalRecords:this.totalRecords!=null?this.totalRecords:records.length}
}});var extentMap={map:null,maps:new Array(),vectorLayer:null,vectorLayerStyle:OpenLayers.Feature.Vector.style["default"],targetPolygon:null,watchedBbox:null,mode:null,edit:null,eltRef:null,digits:5,MultiPolygonReference:OpenLayers.Class(OpenLayers.Geometry,{id:null,initialize:function(id){this.id=id
},CLASS_NAME:"extentMap.MultiPolygonReference"}),mainProjCode:"EPSG:4326",wgsProj:new OpenLayers.Projection("EPSG:4326"),mainProj:null,units:"m",alternateProj:null,initMapDiv:function(){var viewers,idFunc;
extentMap.mainProj=new OpenLayers.Projection(extentMap.mainProjCode);
extentMap.alternateProj=extentMap.mainProj;
if(Ext){viewers=Ext.DomQuery.select(".extentViewer");
idFunc=Ext.id
}else{viewers=$$(".extentViewer");
idFunc=identify()
}for(var idx=0;
idx<viewers.length;
++idx){var viewer=viewers[idx];
extentMap.targetPolygon=viewer.getAttribute("target_polygon");
extentMap.watchedBbox=viewer.getAttribute("watched_bbox");
extentMap.edit=viewer.getAttribute("edit")=="true";
extentMap.eltRef=viewer.getAttribute("elt_ref");
extentMap.mode=viewer.getAttribute("mode");
var children=viewer.childNodes;
var tmp=[];
for(var i=0;
i<children.length;
i++){if(children[i].nodeType==1){tmp.push(children[i])
}}children=tmp;
if(children.length>1){continue
}var id;
if(Ext){id=Ext.id(viewer)
}else{id=viewer.identify()
}var map=extentMap.createMap();
extentMap.maps[extentMap.eltRef]=map;
if(extentMap.edit){var tbarItems=[],control;
if(extentMap.mode=="bbox"){control=new OpenLayers.Control.DrawFeature(extentMap.vectorLayer,OpenLayers.Handler.RegularPolygon,{handlerOptions:{irregular:true,sides:4},featureAdded:function(feature){var bounds=feature.geometry.getBounds();
var boundsReproj=bounds.clone().transform(extentMap.mainProj,extentMap.wgsProj);
var wsen=this.watchedBbox.split(",");
Ext.get("_"+wsen[0]).dom.value=bounds.left;
Ext.get("_"+wsen[1]).dom.value=bounds.bottom;
Ext.get("_"+wsen[2]).dom.value=bounds.right;
Ext.get("_"+wsen[3]).dom.value=bounds.top;
extentMap.watchRadios(this.watchedBbox,this.eltRef)
}.bind({watchedBbox:extentMap.watchedBbox,eltRef:extentMap.eltRef})});
tbarItems.push(new GeoExt.Action({map:map,control:control,text:"draw rectangle",pressed:false,allowDepress:true,toggleGroup:"tool",iconCls:"drawRectangle"}))
}if(extentMap.mode=="polygon"){control=new OpenLayers.Control.DrawFeature(extentMap.vectorLayer,OpenLayers.Handler.Polygon,{featureAdded:function(feature){document.getElementById("_X"+this).value=extentMap.convertToGml(feature,extentMap.mainProjCode)
}.bind(extentMap.targetPolygon)});
tbarItems.push(new GeoExt.Action({map:map,control:control,text:"Draw polygon",pressed:false,allowDepress:true,toggleGroup:"tool",iconCls:"drawPolygon"}));
control=new OpenLayers.Control.DrawFeature(extentMap.vectorLayer,OpenLayers.Handler.RegularPolygon,{handlerOptions:{irregular:true,sides:60},featureAdded:function(feature){document.getElementById("_X"+this).value=extentMap.convertToGml(feature,extentMap.mainProjCode)
}.bind(extentMap.targetPolygon)});
tbarItems.push(new GeoExt.Action({map:map,control:control,text:"Draw circle",pressed:false,allowDepress:true,toggleGroup:"tool",iconCls:"drawCircle"}))
}tbarItems.push({text:"Clear",iconCls:"clearPolygon",handler:function(){this.vectorLayer.destroyFeatures();
var targetPolygonInput=document.getElementById("_X"+this.targetPolygon);
if(targetPolygonInput!=null){targetPolygonInput.value=""
}if(this.targetBbox!=""){var wsen=this.targetBbox.split(",");
Ext.get(wsen[0]).dom.value="";
Ext.get(wsen[1]).dom.value="";
Ext.get(wsen[2]).dom.value="";
Ext.get(wsen[3]).dom.value="";
Ext.get("_"+wsen[0]).dom.value="";
Ext.get("_"+wsen[1]).dom.value="";
Ext.get("_"+wsen[2]).dom.value="";
Ext.get("_"+wsen[3]).dom.value="";
$(wsen[0]).onkeyup();
$(wsen[1]).onkeyup();
$(wsen[2]).onkeyup();
$(wsen[3]).onkeyup()
}},scope:{vectorLayer:extentMap.vectorLayer,targetPolygon:extentMap.targetPolygon,targetBbox:extentMap.watchedBbox,eltRef:extentMap.eltRef}})
}var mapPanel=new GeoExt.MapPanel({renderTo:id,height:300,width:600,map:map,tbar:(extentMap.edit?tbarItems:null)});
if(children.length>0){extentMap.readFeature(children[0].innerHTML,{format:"WKT",zoomToFeatures:true,from:extentMap.wgsProj,to:extentMap.mainProj})
}if(extentMap.watchedBbox!=""){extentMap.watchRadios(extentMap.watchedBbox,extentMap.eltRef);
extentMap.watchBbox(extentMap.vectorLayer,extentMap.watchedBbox,extentMap.eltRef,extentMap.map)
}}},convertToGml:function(feature,proj){var mainProj=new OpenLayers.Projection(proj);
var writer=(mainProj==extentMap.wgsProj?new OpenLayers.Format.GML.v3():new OpenLayers.Format.GML.v3({externalProjection:mainProj,internalProjection:extentMap.wgsProj}));
var child=writer.writeNode("feature:_geometry",feature.geometry);
return OpenLayers.Format.XML.prototype.write.call(writer,child.firstChild)
},createMap:function(){var options={units:extentMap.units,projection:extentMap.mainProjCode,theme:null};
var map=extentMap.map=new OpenLayers.Map(options);
if(!extentMap.edit){var navigationControl=map.getControlsByClass("OpenLayers.Control.Navigation")[0];
navigationControl.disableZoomWheel();
map.removeControl(map.getControlsByClass("OpenLayers.Control.PanZoom")[0])
}map.addControl(new OpenLayers.Control.MousePosition());
var backgroundLayers=[["World Map","http://vmap0.tiles.osgeo.org/wms/vmap0?",{layers:"basic",format:"image/png"},{isBaseLayer:true}]];
for(var i=0;
i<backgroundLayers.length;
i++){var layer=new OpenLayers.Layer.WMS(backgroundLayers[i][0],backgroundLayers[i][1],backgroundLayers[i][2],backgroundLayers[i][3]);
map.addLayer(layer)
}extentMap.vectorLayer=new OpenLayers.Layer.Vector("VectorLayer",{});
map.addLayer(extentMap.vectorLayer);
extentMap.vectorLayer.events.on({sketchstarted:function(){this.destroyFeatures()
},scope:extentMap.vectorLayer});
return map
},watchBbox:function(vectorLayer,watchedBbox,eltRef,map){var wsen=watchedBbox.split(",");
for(var i=0;
i<wsen.length;
++i){Ext.get(wsen[i]).on("change",function(){extentMap.updateBbox(map,watchedBbox,eltRef,false)
});
Ext.get("_"+wsen[i]).on("change",function(){extentMap.updateBbox(map,watchedBbox,eltRef,true)
})
}},updateBbox:function(map,targetBbox,eltRef,mainProj){var vectorLayer=map.getLayersByName("VectorLayer")[0];
var bounds;
var wsen=targetBbox.split(",");
if(mainProj){var values=new Array(wsen.length);
values[0]=Ext.get("_"+wsen[0]).getValue();
values[1]=Ext.get("_"+wsen[1]).getValue();
values[2]=Ext.get("_"+wsen[2]).getValue();
values[3]=Ext.get("_"+wsen[3]).getValue();
bounds=OpenLayers.Bounds.fromArray(values);
if(extentMap.mainProj!=extentMap.wgsProj){bounds=bounds.clone().transform(extentMap.mainProj,extentMap.wgsProj)
}}else{var values=new Array(wsen.length);
values[0]=Ext.get(wsen[0]).getValue();
values[1]=Ext.get(wsen[1]).getValue();
values[2]=Ext.get(wsen[2]).getValue();
values[3]=Ext.get(wsen[3]).getValue();
bounds=OpenLayers.Bounds.fromArray(values);
var toProj=null;
var radio=document.getElementsByName("proj_"+eltRef);
for(i=0;
i<radio.length;
i++){if(radio[i].checked==true){toProj=radio[i].value
}}if(toProj!=extentMap.mainProjCode){bounds.transform(new OpenLayers.Projection(toProj),extentMap.mainProj)
}else{var b=bounds.clone();
b.transform(extentMap.mainProj,extentMap.alternateProj);
values[0]=b.left;
values[1]=b.bottom;
values[2]=b.right;
values[3]=b.top
}Ext.get("_"+wsen[0]).dom.value=bounds.left;
Ext.get("_"+wsen[1]).dom.value=bounds.bottom;
Ext.get("_"+wsen[2]).dom.value=bounds.right;
Ext.get("_"+wsen[3]).dom.value=bounds.top
}Ext.get(wsen[0]).dom.onkeyup();
Ext.get(wsen[1]).dom.onkeyup();
Ext.get(wsen[2]).dom.onkeyup();
Ext.get(wsen[3]).dom.onkeyup();
var feature=new OpenLayers.Feature.Vector(bounds.toGeometry());
vectorLayer.destroyFeatures();
vectorLayer.addFeatures(feature);
extentMap.zoomToFeatures(map,vectorLayer);
extentMap.watchRadios(targetBbox,eltRef)
},watchRadios:function(watchedBbox,eltRef){function updateInputTextFields(watchedBbox,toProj,digits){var wsen=watchedBbox.split(",");
var w=Ext.get("_"+wsen[0]).getValue();
var s=Ext.get("_"+wsen[1]).getValue();
var e=Ext.get("_"+wsen[2]).getValue();
var n=Ext.get("_"+wsen[3]).getValue();
var l=w!=""?w:"0";
var b=s!=""?s:"0";
var r=e!=""?e:"0";
var t=n!=""?n:"0";
var bounds=OpenLayers.Bounds.fromString(l+","+b+","+r+","+t);
if(!toProj.equals(extentMap.mainProj)){bounds.transform(extentMap.mainProj,toProj)
}if(w!=""){w=bounds.left.toFixed(digits)+""
}Ext.get(wsen[0]).dom.value=w;
if(s!=""){s=bounds.bottom.toFixed(digits)+""
}Ext.get(wsen[1]).dom.value=s;
if(e!=""){e=bounds.right.toFixed(digits)+""
}Ext.get(wsen[2]).dom.value=e;
if(n!=""){n=bounds.top.toFixed(digits)+""
}Ext.get(wsen[3]).dom.value=n
}$$("input.proj").each(function(input){if(input.id.indexOf(eltRef)!=-1){if(input.checked){updateInputTextFields(watchedBbox,new OpenLayers.Projection(input.value),extentMap.digits)
}Ext.get(input.id).on("click",function(){updateInputTextFields(watchedBbox,new OpenLayers.Projection(input.value),extentMap.digits)
})
}})
},writeFeature:function(options){var format="WKT";
var from,to;
if(options!=null){format=options.format||"WKT";
from=options.from;
to=options.to
}var writer=new OpenLayers.Format[format]();
if(from!=null&&to!=null){writer.internalProjection=from;
writer.externalProjection=to
}if(!this.vectorLayer.features.length){return null
}var feature=this.vectorLayer.features[this.vectorLayer.features.length-1];
return writer.write(feature)
},readFeature:function(string,options){if(string==""){return false
}var format="WKT";
var from,to;
if(options!=null){format=options.format||"WKT";
from=options.from;
to=options.to
}var reader=new OpenLayers.Format[format]();
if(from!=null&&to!=null){reader.externalProjection=from;
reader.internalProjection=to
}string=string.replace(/\n/g,"");
var feature=reader.read(string);
if(!feature){return false
}if(feature.length){feature=feature[0]
}this.vectorLayer.addFeatures(feature);
if(options.zoomToFeatures){this.zoomToFeatures(this.map,this.vectorLayer)
}return true
},zoomToFeatures:function(map,vectorLayer){var extent=vectorLayer.getDataExtent();
if(extent&&!isNaN(extent.left)){var width=extent.getWidth()/2;
var height=extent.getHeight()/2;
extent.left-=width;
extent.right+=width;
extent.bottom-=height;
extent.top+=height;
map.zoomToExtent(extent)
}else{map.zoomToMaxExtent()
}}};
extentMap.prev_geometry=OpenLayers.Format.GML.Base.prototype.writers.feature._geometry;
OpenLayers.Format.GML.Base.prototype.writers.feature._geometry=function(geometry){if(geometry.CLASS_NAME=="extentMap.MultiPolygonReference"){var gml=this.createElementNS(this.namespaces.gml,"gml:MultiPolygon");
var gmlNode=this.createElementNS(this.namespaces.gml,"gml:MultiPolygon");
gmlNode.setAttribute("gml:id",geometry.id);
gml.appendChild(gmlNode);
return gml
}else{return extentMap.prev_geometry.apply(this,arguments)
}};