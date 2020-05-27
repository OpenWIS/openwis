Ext.namespace("app");
app.CRS={};
var CRS=Ext.data.Record.create([{name:"authority"},{name:"code"},{name:"version"},{name:"codeSpace"},{name:"description"}]);
app.CRS.crsStore=new Ext.data.Store({proxy:new Ext.data.HttpProxy({url:"crs.search",method:"GET"}),baseParams:{name:"",type:"",maxResults:50},reader:new Ext.data.XmlReader({record:"crs",id:"code"},CRS),fields:["code","codeSpace","authority","description","version"],sortInfo:{field:"description"}});
app.CRSSelectionPanel=Ext.extend(Ext.FormPanel,{border:false,first:null,itemSelector:null,loadingMask:null,crsCount:null,ref:null,crsSelected:"",initComponent:function(){this.items=[{xtype:"panel",layout:"fit",bodyStyle:"padding: 5px;",border:false,tbar:[this.getCRSTypeCombo()," ",this.getCRS(),"->",translate("maxResults"),this.getLimitInput()],items:[this.getCRSItemSelector()]}];
app.CRS.crsStore.on({loadexception:function(){},beforeload:function(store,options){if(Ext.getCmp("maxResults")){store.baseParams.maxResults=Ext.getCmp("maxResults").getValue()
}if(!this.loadingMask){this.loadingMask=new Ext.LoadMask(this.itemSelector.fromMultiselect.getEl(),{msg:translate("searching")})
}this.loadingMask.show()
},load:function(){this.loadingMask.hide()
},scope:this});
this.addEvents("crsSelected");
this.bbar=["->",{id:"crsSearchValidateButton",iconCls:"addIcon",disabled:true,text:translate("add"),handler:function(){this.buildCRSXmlList()
},scope:this}];
app.CRSSelectionPanel.superclass.initComponent.call(this)
},getCRS:function(){return new Ext.app.SearchField({id:"crsSearchField",width:240,store:app.CRS.crsStore,paramName:"name"})
},getLimitInput:function(){return{xtype:"textfield",name:"maxResults",id:"maxResults",value:50,width:40}
},getCRSTypeCombo:function(){var CRSType=Ext.data.Record.create([{name:"id"}]);
app.CRS.crsTypeStore=new Ext.data.Store({url:"crs.types",reader:new Ext.data.XmlReader({record:"type"},CRSType),fields:["id"]});
var record=new CRSType({filename:translate("any")});
record.set("id","");
app.CRS.crsTypeStore.add(record);
app.CRS.crsTypeStore.load({add:true});
return{xtype:"combo",width:150,id:"search-crs",value:0,store:app.CRS.crsTypeStore,triggerAction:"all",mode:"local",displayField:"id",valueField:"id",listWidth:250,listeners:{select:function(combo,record,index){app.CRS.crsStore.removeAll();
app.CRS.crsStore.baseParams.type=combo.getValue();
var value=Ext.getCmp("crsSearchField").getValue();
if(value.length<1){app.CRS.crsStore.baseParams.name=""
}else{app.CRS.crsStore.baseParams.name=value
}app.CRS.crsStore.reload()
},clear:function(combo){app.CRS.crsStore.load()
},scope:this}}
},getCRSItemSelector:function(){var tpl='<tpl for="."><div class="ux-mselect-item';
if(Ext.isIE||Ext.isIE7){tpl+='" unselectable=on'
}else{tpl+=' x-unselectable"'
}tpl+=">{description}</div></tpl>";
this.itemSelector=new Ext.ux.ItemSelector({name:"itemselector",fieldLabel:"ItemSelector",dataFields:["code","codeSpace","authority","description","version"],toData:[],msWidth:320,msHeight:230,valueField:"code",fromTpl:tpl,toTpl:tpl,toLegend:translate("selectedCRS"),fromLegend:translate("foundCRS"),fromStore:app.CRS.crsStore,fromAllowTrash:false,fromAllowDup:true,toAllowDup:false,drawUpIcon:false,drawDownIcon:false,drawTopIcon:false,drawBotIcon:false,imagePath:"/scripts/ext-ux/MultiselectItemSelector-3.0/icons",toTBar:[{text:translate("clear"),handler:function(){var i=this.getForm().findField("itemselector");
i.reset.call(i)
},scope:this}]});
this.itemSelector.on({change:function(component){Ext.getCmp("crsSearchValidateButton").setDisabled(component.toStore.getCount()<1)
}});
return this.itemSelector
},setRef:function(ref){this.ref=ref
},buildCRSXmlList:function(){this.crsSelected="";
var store=this.itemSelector.toMultiselect.store;
this.first=true;
store.each(function(record){var tpl="<gmd:MD_ReferenceSystem xmlns:gmd='http://www.isotc211.org/2005/gmd'  xmlns:gco='http://www.isotc211.org/2005/gco'><gmd:referenceSystemIdentifier><gmd:RS_Identifier><gmd:code><gco:CharacterString>"+record.data.description+"</gco:CharacterString></gmd:code><gmd:codeSpace><gco:CharacterString>"+record.data.codeSpace+"</gco:CharacterString></gmd:codeSpace><gmd:version><gco:CharacterString>"+record.data.version+"</gco:CharacterString></gmd:version></gmd:RS_Identifier></gmd:referenceSystemIdentifier></gmd:MD_ReferenceSystem>";
this.crsSelected+=(this.first?"":"&amp;&amp;&amp;")+tpl;
this.first=false
},this);
if(this.crsSelected!=""){this.fireEvent("crsSelected",this.crsSelected);
this.ownerCt.hide()
}}});Ext.namespace("app");
app.keyword={};
var Keyword=Ext.data.Record.create([{name:"value"},{name:"thesaurus"},{name:"uri"}]);
app.keyword.keywordStore=new Ext.data.Store({proxy:new Ext.data.HttpProxy({url:"xml.search.keywords",method:"GET"}),baseParams:{pNewSearch:true,pTypeSearch:1,pThesauri:"",pMode:"searchBox"},reader:new Ext.data.XmlReader({record:"keyword",id:"uri"},Keyword),fields:["value","thesaurus","uri"],sortInfo:{field:"thesaurus"}});
app.KeywordSelectionPanel=Ext.extend(Ext.FormPanel,{border:false,itemSelector:null,loadingMask:null,ThesaurusCount:null,ref:null,keywordsSelected:[],initComponent:function(){this.items=[{xtype:"panel",layout:"fit",bodyStyle:"padding: 5px;",border:false,tbar:[this.getThesaurusCombo()," ",this.getKeyword(),"->",translate("maxResults")+" "+translate("perThesaurus"),this.getLimitInput()],items:[this.getKeywordsItemSelector()]}];
app.keyword.keywordStore.on({loadexception:function(){},beforeload:function(store,options){if(Ext.getCmp("maxResults")){store.baseParams.maxResults=Ext.getCmp("maxResults").getValue()
}if(!this.loadingMask){this.loadingMask=new Ext.LoadMask(this.itemSelector.fromMultiselect.getEl(),{msg:translate("searching")})
}this.loadingMask.show()
},load:function(){this.loadingMask.hide()
},scope:this});
this.addEvents("keywordselected");
this.bbar=["->",{id:"keywordSearchValidateButton",iconCls:"addIcon",disabled:true,text:translate("add"),handler:function(){this.buildKeywordXmlList()
},scope:this}];
app.KeywordSelectionPanel.superclass.initComponent.call(this)
},getKeyword:function(){return new Ext.app.SearchField({id:"keywordSearchField",width:240,store:app.keyword.keywordStore,paramName:"pKeyword"})
},setRef:function(ref){this.ref=ref
},getLimitInput:function(){return{xtype:"textfield",name:"maxResults",id:"maxResults",value:50,width:40}
},getThesaurusCombo:function(){var Thesaurus=Ext.data.Record.create([{name:"filename"},{name:"value",mapping:"key"}]);
app.keyword.thesaurusStore=new Ext.data.Store({url:"xml.thesaurus.getList",reader:new Ext.data.XmlReader({record:"thesaurus"},Thesaurus),fields:["filename","id"]});
var record=new Thesaurus({filename:translate("anyThesaurus")});
record.set("value","");
app.keyword.thesaurusStore.add(record);
app.keyword.thesaurusStore.load({add:true});
return{xtype:"combo",width:150,id:"search-thesauri",value:0,store:app.keyword.thesaurusStore,triggerAction:"all",mode:"local",displayField:"filename",valueField:"value",listWidth:250,listeners:{select:function(combo,record,index){app.keyword.keywordStore.removeAll();
app.keyword.keywordStore.baseParams.pThesauri=combo.getValue();
var value=Ext.getCmp("keywordSearchField").getValue();
if(value.length<1){app.keyword.keywordStore.baseParams.pKeyword="*"
}else{app.keyword.keywordStore.baseParams.pKeyword=value
}app.keyword.keywordStore.reload()
},clear:function(combo){app.keyword.keywordStore.load()
},scope:this}}
},getKeywordsItemSelector:function(){var tpl='<tpl for="."><div class="ux-mselect-item';
if(Ext.isIE||Ext.isIE7){tpl+='" unselectable=on'
}else{tpl+=' x-unselectable"'
}tpl+='>{id} {value} <span class="ux-mselect-item-thesaurus">({thesaurus})</span></div></tpl>';
this.itemSelector=new Ext.ux.ItemSelector({name:"itemselector",fieldLabel:"ItemSelector",dataFields:["value","thesaurus"],toData:[],msWidth:320,msHeight:230,valueField:"value",fromTpl:tpl,toTpl:tpl,toLegend:translate("selectedKeywords"),fromLegend:translate("foundKeywords"),fromStore:app.keyword.keywordStore,fromAllowTrash:false,fromAllowDup:true,toAllowDup:false,drawUpIcon:false,drawDownIcon:false,drawTopIcon:false,drawBotIcon:false,imagePath:"/scripts/ext-ux/MultiselectItemSelector-3.0/icons",toTBar:[{text:translate("clear"),handler:function(){var i=this.getForm().findField("itemselector");
i.reset.call(i)
},scope:this}]});
this.itemSelector.on({change:function(component){Ext.getCmp("keywordSearchValidateButton").setDisabled(component.toStore.getCount()<1)
}});
return this.itemSelector
},buildKeywordXmlList:function(){this.keywordsSelected=[];
var self=this;
this.ThesaurusCount=0;
var thesaurusCollection=[];
var store=this.itemSelector.toMultiselect.store;
thesaurusCollection=store.collect("thesaurus");
Ext.each(thesaurusCollection,function(thesaurus,index,thesauri){store.filter("thesaurus",thesaurus);
var values=store.collect("uri");
Ext.each(values,function(item,index){values[index]=item.replace("#","%23")
});
var serviceUrl="xml.keyword.get";
var multiple=(values.length>1)?true:false;
var inputValue=serviceUrl+"?thesaurus="+thesaurus+"&id="+values.join(",")+"&multiple="+multiple;
++self.ThesaurusCount;
self.retrieveKeywordData(inputValue)
});
store.clearFilter()
},retrieveKeywordData:function(url){Ext.getCmp("keywordSearchValidateButton").disable();
Ext.Ajax.request({url:url,method:"GET",scope:this,success:function(response){var keyword=response.responseText;
if(keyword.indexOf("<gmd:MD_Keywords")!=-1){this.keywordsSelected.push(response.responseText)
}Ext.getCmp("keywordSearchValidateButton").enable();
this.ThesaurusCount-=1;
if(this.ThesaurusCount==0){this.fireEvent("keywordselected",this,this.keywordsSelected);
this.ownerCt.hide()
}}})
}});Ext.namespace("app");
app.Filter={SERVICE:[{name:"E_type",value:"service"}],DATASET:[{name:"E_type",value:"dataset"}],FEATURE_CATALOGUE:[{name:"E__schema",value:"iso19110"}]};
app.Utility={convertSubjectAsCommaSeparatedValues:function(v,record){if(record.subject){return app.Utility.convertSeparatedValues(record.subject," ,")
}else{return""
}},convertSeparatedValues:function(values,s){var result="";
for(var i=0;
i<values.length;
i++){if(i!=0){result+=s
}result+=values[i].value
}return result
},checkUriNullValues:function(v,record){if(record.URI){return record.URI[0].value
}else{return""
}}};
app.linkedMetadata={};
app.linkedMetadata.linkedMetadataStore=new Ext.data.JsonStore({fields:[{name:"title",mapping:"title[0].value",defaultValue:""},{name:"subject",convert:app.Utility.convertSubjectAsCommaSeparatedValues,defaultValue:""},{name:"uuid",mapping:"identifier[0].value",defaultValue:""},{name:"uri",convert:app.Utility.checkUriNullValues}]});
app.LinkedMetadataSelectionPanel=Ext.extend(Ext.FormPanel,{border:false,layout:"fit",createIfNotExistURL:null,hiddenParameters:app.Filter.DATASET,singleSelect:true,loadingMask:null,ref:null,proxy:null,mode:null,serviceUrl:null,capabilitiesStore:null,initComponent:function(){this.addEvents("linkedmetadataselected");
if(this.mode=="attachService"||this.mode=="coupledResource"){this.capabilitiesStore=new GeoExt.data.WMSCapabilitiesStore({proxy:new Ext.data.HttpProxy({method:"GET",prettyUrls:false,url:this.proxy}),baseParams:{url:this.serviceUrl},id:"capabilitiesStore",listeners:{exception:function(proxy,type,action,options,res,arg){Ext.MessageBox.alert(translate("error"))
},beforeload:function(){if(this.mode=="attachService"){var selected=this.linkedMetadataGrid.getSelectionModel().getSelections();
if(selected==undefined||selected.length==0||selected[0].data.uri==""){Ext.MessageBox.alert(translate("NoServiceURLError"));
return false
}this.capabilitiesStore.baseParams.url=selected[0].data.uri+"?&SERVICE=WMS&REQUEST=GetCapabilities&VERSION=1.1.1"
}else{if(this.mode=="coupledResource"){this.capabilitiesStore.baseParams.url=this.serviceUrl
}}},loadexception:function(){Ext.MessageBox.alert(translate("GetCapabilitiesDocumentError")+this.capabilitiesStore.baseParams.url)
},scope:this}})
}if(this.mode=="attachService"){this.hiddenParameters=app.Filter.SERVICE
}else{if(this.mode=="iso19110"){this.hiddenParameters=app.Filter.FEATURE_CATALOGUE
}}var checkboxSM=new Ext.grid.CheckboxSelectionModel({singleSelect:this.singleSelect,header:"",listeners:{selectionchange:function(){Ext.getCmp("linkedMetadataValidateButton").setDisabled(this.getSelections().length<1)
}}});
var tbarItems=[this.getSearchInput(),"->",translate("maxResults"),this.getLimitInput()];
this.addHiddenFormInput(tbarItems);
app.linkedMetadata.linkedMetadataStore.removeAll();
this.linkedMetadataGrid=new Ext.grid.GridPanel({xtype:"grid",layout:"fit",height:280,bodyStyle:"padding: 0px;",border:true,loadMask:true,tbar:tbarItems,store:app.linkedMetadata.linkedMetadataStore,columns:[checkboxSM,{id:"title",header:translate("mdTitle"),dataIndex:"title"},{id:"subject",header:translate("keywords"),dataIndex:"subject"},{id:"uri",header:translate("uri"),dataIndex:"uri"}],sm:checkboxSM,autoExpandColumn:"title",listeners:{rowclick:function(grid,rowIndex,e){if(this.capabilitiesStore!=null&&this.mode!="coupledResource"){this.serviceUrl=grid.getStore().getAt(rowIndex).data.uri;
if(this.serviceUrl==""){this.capabilitiesStore.removeAll()
}else{this.capabilitiesStore.baseParams.url=this.serviceUrl;
this.capabilitiesStore.reload()
}}},scope:this}});
if(this.mode=="attachService"||this.mode=="coupledResource"){this.items=this.getScopedNamePanel(this.linkedMetadataGrid)
}else{this.items=this.linkedMetadataGrid
}this.bbar=["->",{id:"linkedMetadataValidateButton",iconCls:"linkIcon",text:translate("createRelation"),disabled:true,handler:function(){var selected=this.linkedMetadataGrid.getSelectionModel().getSelections();
this.fireEvent("linkedmetadataselected",this,selected);
this.ownerCt.close()
},scope:this}];
app.linkedMetadata.linkedMetadataStore.on({load:function(){if(this.loadingMask!=null){this.loadingMask.hide()
}},scope:this});
app.LinkedMetadataSelectionPanel.superclass.initComponent.apply(this)
},getCreateIfNotExistButton:function(){if(this.createIfNotExistURL==null){return""
}return{id:"createIfNotExistButton",iconCls:"addIcon",text:translate("createIfNotExistButton"),handler:function(){window.location.replace(this.createIfNotExistURL)
},scope:this}
},setRef:function(ref){this.ref=ref
},addHiddenFormInput:function(items){for(var i=0;
i<this.hiddenParameters.length;
i++){items.push({xtype:"textfield",fieldLabel:this.hiddenParameters[i].name,name:this.hiddenParameters[i].name,value:this.hiddenParameters[i].value,hidden:true})
}return items
},getSearchInput:function(){return new Ext.app.SearchField({name:"E_AnyText",width:240,store:app.linkedMetadata.linkedMetadataStore,triggerAction:function(scope){scope.doSearch()
},scope:this})
},getLimitInput:function(){return{xtype:"textfield",name:"nbResultPerPage",id:"nbResultPerPage",value:20,width:40}
},getScopedNamePanel:function(grid){var combo={xtype:"combo",fieldLabel:translate("getCapabilitiesLayer"),store:this.capabilitiesStore,valueField:"name",displayField:"title",triggerAction:"all",listeners:{select:function(combo,record,index){getLayerName().setValue(combo.getValue())
},beforequery:function(qe){delete qe.combo.lastQuery
}}};
var panel={xtype:"panel",layout:"form",labelWidth:120,bodyStyle:"padding: 2px;",border:true,items:[grid,combo,this.getLayerName()]};
return panel
},getLayerName:function(){if(!this.layerName){this.layerName=new Ext.form.TextField({fieldLabel:translate("layerName"),valueField:"name",displayField:"title"})
}return this.layerName
},doSearch:function(){if(!this.loadingMask){this.loadingMask=new Ext.LoadMask(this.getEl(),{msg:translate("searching")})
}this.loadingMask.show();
var url=Env.locService+"/csw";
app.nbResultPerPage=20;
if(Ext.getCmp("nbResultPerPage")){app.nbResultPerPage=Ext.getCmp("nbResultPerPage").getValue()
}CSWSearchTools.doCSWQueryFromForm(this.id,url,1,this.showResults,null,Ext.emptyFn)
},showResults:function(response){var getRecordsFormat=new OpenLayers.Format.CSWGetRecords();
var r=getRecordsFormat.read(response.responseText);
var values=r.records;
if(values!=undefined){app.linkedMetadata.linkedMetadataStore.loadData(values)
}}});Ext.namespace("app");
Ext.app.SearchField=Ext.extend(Ext.form.TwinTriggerField,{initComponent:function(){if(!this.store.baseParams){this.store.baseParams={}
}Ext.app.SearchField.superclass.initComponent.call(this);
this.on("specialkey",function(f,e){if(e.getKey()==e.ENTER){this.onTrigger2Click()
}},this)
},validationEvent:false,validateOnBlur:false,trigger1Class:"x-form-clear-trigger",trigger2Class:"x-form-search-trigger",hideTrigger1:true,width:180,hasSearch:false,paramName:"query",onTrigger1Click:function(){if(this.hasSearch){this.store.baseParams[this.paramName]="";
this.store.removeAll();
this.el.dom.value="";
this.triggers[0].hide();
this.hasSearch=false;
this.focus();
var conf=Ext.get("conf");
if(conf){conf.enableDisplayMode().show()
}}},onTrigger2Click:function(){var v=this.getRawValue();
if(v.length<1){this.store.baseParams[this.paramName]="*"
}else{this.store.baseParams[this.paramName]=v
}if(this.triggerAction){this.triggerAction(this.scope,v)
}else{this.store.reload()
}this.hasSearch=true;
this.triggers[0].show();
this.focus();
var conf=Ext.get("conf");
if(conf){conf.enableDisplayMode().hide()
}}});var CSWSearchTools={cswMethod:"POST",resultsMode:"results",doCSWQueryFromForm:function(formId,url,recordNum,onSuccess,onFailure,addFilters){var query=CSWSearchTools.buildCSWQueryFromForm(CSWSearchTools.cswMethod,Ext.getCmp(formId),recordNum,app.sortBy,addFilters);
if(CSWSearchTools.cswMethod=="POST"){var getQuery=CSWSearchTools.buildCSWQueryFromForm("GET",Ext.getCmp(formId),recordNum,app.sortBy,addFilters);
OpenLayers.Request.POST({url:url,data:query,success:function(result){onSuccess(result,getQuery)
},failure:onFailure})
}else{OpenLayers.Request.GET({url:url,params:query,success:function(result){onSuccess(result,query)
},failure:onFailure})
}},buildCSWQueryFromForm:function(method,form,startRecord,sortBy,addFilters){var values=CSWSearchTools.getFormValues(form);
var filters=[];
CSWSearchTools.addFiltersFromPropertyMap(values,filters);
addFilters(values,filters);
if(filters.length==0){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.LIKE,property:"anyText",value:".*"}))
}var and=new OpenLayers.Filter.Logical({type:OpenLayers.Filter.Logical.AND,filters:filters});
if(method=="POST"){return CSWSearchTools.buildCSWQueryPOST(and,startRecord,sortBy)
}else{return CSWSearchTools.buildCSWQueryGET(and,startRecord,sortBy)
}},addFiltersFromPropertyMap:function(values,filters){var defaultSimilarity=".8";
var similarity=values.E_similarity;
if(similarity!=null){defaultSimilarity=values.E_similarity;
CSWSearchTools.addFilter(filters,"E_similarity",defaultSimilarity,defaultSimilarity)
}for(var key in values){var value=values[key];
if(value!=""&&key!="E_similarity"){CSWSearchTools.addFilter(filters,key,value,defaultSimilarity)
}}},addFilter:function(filters,key,value,defaultSimilarity){var field=key.match("^(\\[?)([^_]+)_(.*)$");
if(field){if(field[1]=="["){var or=[];
var values=value.split(",");
for(var i=0;
i<values.length;
++i){CSWSearchTools.addFilterImpl(values.length>1?or:filters,field[2],field[3],values[i],defaultSimilarity)
}if(values.length>1){filters.push(new OpenLayers.Filter.Logical({type:OpenLayers.Filter.Logical.OR,filters:or}))
}}else{CSWSearchTools.addFilterImpl(filters,field[2],field[3],value,defaultSimilarity)
}}},addFilterImpl:function(filters,type,name,value,defaultSimilarity){if(type=="S"){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.LIKE,property:name,value:value+".*"}))
}else{if(type=="C"){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.LIKE,property:name,value:".*"+value+".*"}))
}else{if(type.charAt(0)=="E"){if(type.length>1){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:"similarity",value:type.substring(1)}))
}filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:name,value:value}));
if(type.length>1){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:"similarity",value:defaultSimilarity}))
}}else{if(type==">="){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.GREATER_THAN_OR_EQUAL_TO,property:name,value:value}))
}else{if(type=="<="){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.LESS_THAN_OR_EQUAL_TO,property:name,value:value}))
}else{if(type=="T"){var splitted=value.split(" ");
for(var i=0;
i<splitted.length;
++i){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:name,value:splitted[i]}))
}}else{if(type=="B"){filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:name,value:value?1:0}))
}else{if(type=="V"){var subField=value.match("^([^/]+)/(.*)$");
filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:"similarity",value:"1.0"}));
filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:subField[1],value:subField[2]}));
filters.push(new OpenLayers.Filter.Comparison({type:OpenLayers.Filter.Comparison.EQUAL_TO,property:"similarity",value:defaultSimilarity}))
}else{alert("Cannot parse "+type)
}}}}}}}}},sortByMappings:{relevance:{name:"relevance",order:"D"},rating:{name:"rating",order:"D"},popularity:{name:"popularity",order:"D"},date:{name:"date",order:"D"},title:{name:"title",order:"A"}},buildCSWQueryPOST:function(filter,startRecord,sortBy){var result='<?xml version="1.0" encoding="UTF-8"?>\n<csw:GetRecords xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" service="CSW" version="2.0.2" resultType="'+this.resultsMode+'" startPosition="'+startRecord+'" maxRecords="'+app.nbResultPerPage+'">\n  <csw:Query typeNames="csw:Record">\n    <csw:ElementSetName>full</csw:ElementSetName>\n';
if(sortBy){var searchInfo=CSWSearchTools.sortByMappings[sortBy];
result+='    <ogc:SortBy xmlns:ogc="http://www.opengis.net/ogc">\n      <ogc:SortProperty>\n        <ogc:PropertyName>'+searchInfo.name+"</ogc:PropertyName>\n        <ogc:SortOrder>"+searchInfo.order+"</ogc:SortOrder>\n      </ogc:SortProperty>\n    </ogc:SortBy>\n"
}if(filter){var filterXML=new OpenLayers.Format.XML().write(new OpenLayers.Format.Filter().write(filter));
filterXML=filterXML.replace(/^<\?xml[^?]*\?>/,"");
result+='    <csw:Constraint version="1.0.0">\n';
result+=filterXML;
result+="    </csw:Constraint>\n"
}result+="  </csw:Query>\n</csw:GetRecords>";
return result
},buildCSWQueryGET:function(filter,startRecord,sortBy){var result={request:"GetRecords",service:"CSW",version:"2.0.2",resultType:this.resultsMode,namespace:"csw:http://www.opengis.net/cat/csw/2.0.2",typeNames:"csw:Record",constraintLanguage:"FILTER",constraint_language_version:"1.1.0",elementSetName:"full",startPosition:startRecord,maxRecords:app.nbResultPerPage};
if(sortBy){var searchInfo=CSWSearchTools.sortByMappings[sortBy];
result.sortBy=searchInfo.name+":"+searchInfo.order
}if(filter){var filterXML=new OpenLayers.Format.XML().write(new OpenLayers.Format.Filter().write(filter));
filterXML=filterXML.replace(/^<\?xml[^?]*\?>/,"");
result.constraint=filterXML
}return result
},getFormValues:function(form){var result=form.getForm().getValues()||{};
form.cascade(function(cur){if(cur.disabled!=true){if(cur.isXType("boxselect")){if(cur.getValue&&cur.getValue()){result[cur.getName()]=cur.getValue()
}}else{if(cur.isXType("combo")){if(cur.getValue&&cur.getValue()){result[cur.getName()]=cur.getValue()
}}else{if(cur.isXType("fieldset")){if(cur.checkbox){result[cur.checkboxName]=!cur.collapsed
}}else{if(cur.isXType("radiogroup")){var first=cur.items.get(0);
result[first.getName()]=first.getGroupValue()
}else{if(cur.isXType("checkbox")){result[cur.getName()]=cur.getValue()
}else{if(cur.isXType("datefield")){if(cur.getValue()!=""){result[cur.getName()]=cur.getValue().format("Y-m-d")+(cur.postfix?cur.postfix:"")
}}else{if(cur.getName){if(cur.getValue&&cur.getValue()!=""){result[cur.getName()]=cur.getValue()
}}}}}}}}}return true
});
return result
}};var getGNServiceURL=function(service){return configOptions.locService+"/"+service
};
var getProxy=function(){return"../../proxy?"
};
function findPos(obj){var curtop=0;
if(obj){var arr=obj.cumulativeOffset();
curtop=arr[1]
}return curtop
}var Checks={message:Openwis.i18n("Common.Editor.LoseYourChanges"),_setMessage:function(str){this.message=str
},_onbeforeunload:function(e){if(opener){editRed=opener.$$(".editing");
if(editRed&&editRed.length>0){editRed.invoke("removeClassName","editing")
}e.returnValue=this.message
}}};
function unloadMess(){mess="loseYourChange";
return mess
}var bfu=Checks._onbeforeunload.bindAsEventListener(Checks);
function setBunload(on){if(on){Event.observe(window,"beforeunload",bfu);
Checks._setMessage(unloadMess())
}else{Event.stopObserving(window,"beforeunload",bfu);
Checks._setMessage(null)
}}function doEditorLoadActions(){setBunload(true);
var map=new Ext.KeyMap(document,[{key:"s",ctrl:true,shift:true,fn:function(){$("btnSave").onclick()
}},{key:"q",ctrl:true,shift:true,fn:function(){$("btnSaveAndClose").onclick()
}},{key:"v",ctrl:true,shift:true,fn:function(){$("btnValidate").onclick()
}},{key:"t",ctrl:true,shift:true,fn:function(){$("btnThumbnails").onclick()
}},{key:"r",ctrl:true,shift:true,fn:function(){$("btnReset").onclick()
}},{key:"c",ctrl:true,shift:true,fn:function(){$("btnCancel").onclick()
}},{key:112,fn:function(){displayBox(null,"shortcutHelp",true)
}}]);
map.enable()
}Event.observe(window,"load",doEditorLoadActions);
Ajax.Responders.register({onCreate:function(){if(Ajax.activeRequestCount===1){var eBusy=$("editorBusy");
if(eBusy){eBusy.show()
}}},onComplete:function(){if(Ajax.activeRequestCount===0){var eBusy=$("editorBusy");
if(eBusy){eBusy.hide()
}}}});
function doAction(action){if(metadata.edit.embedded.doAction!==undefined){metadata.edit.embedded.doAction(action)
}else{setBunload(false);
document.mainForm.action=action;
goSubmit("mainForm")
}}function doTabAction(action,tab){if(metadata.edit.embedded.doTabAction!==undefined){metadata.edit.embedded.doTabAction(action,tab)
}else{disableEditForm();
document.mainForm.currTab.value=tab;
doAction(action)
}}function doCommonsAction(action,name,licenseurl,type,id){var top=findPos($(id));
setBunload(false);
document.mainForm.name.value=name;
document.mainForm.licenseurl.value=licenseurl;
document.mainForm.type.value=type;
document.mainForm.position.value=top;
doAction(action)
}function doResetCommonsAction(action,name,licenseurl,type,id,ref){$(ref).value="";
document.mainForm.ref.value="";
doCommonsAction(action,name,licenseurl,type,id)
}function getControlsFromElement(el){var id=el.getAttribute("id");
elButtons=$("buttons_"+id);
return elButtons.immediateDescendants()
}function topElement(el){if(el.previous()==undefined){return true
}else{return(!isSameElement(el.previous(),el))
}}function bottomElement(el){if(el.next()==undefined){return true
}else{return(!isSameElement(el.next(),el))
}}function getIdSplit(el){var id=el.getAttribute("id");
if(id==null){return null
}return id.split("_")
}function orElement(el){if(el.next()==undefined){return false
}else{var nextEl=getIdSplit(el.next());
var thisEl=getIdSplit(el);
if(nextEl==null||thisEl==null){return false
}if(nextEl[0]=="child"&&nextEl[1]==thisEl[0]){return true
}else{return false
}}}function isSameElement(el1,el2){var i1=getIdSplit(el1);
var i2=getIdSplit(el2);
if(i1==null||i2==null){return false
}if(i1[0]==i2[0]){return true
}else{return false
}}function topControls(el,min){var elDescs=getControlsFromElement(el);
var index=0;
if(elDescs.length==5){index=1
}if(bottomElement(el)&&!orElement(el)){elDescs[0].show()
}else{elDescs[0].hide()
}if(index==1){if(bottomElement(el)&&!orElement(el)){elDescs[index].show()
}else{elDescs[index].hide()
}}if(bottomElement(el)){if(min==0){elDescs[1+index].show()
}else{elDescs[1+index].hide()
}}else{elDescs[1+index].show()
}elDescs[2+index].hide();
if(bottomElement(el)){elDescs[3+index].hide()
}else{elDescs[3+index].show()
}}function doRemoveAttributeAction(action,ref,parentref){var metadataId=document.mainForm.id.value;
var thisElement=$(ref+"_block");
var myExtAJaxRequest=Ext.Ajax.request({url:getGNServiceURL(action),method:"GET",params:{id:metadataId,ref:ref},success:function(result,request){var html=result.responseText;
doSaveAction("metadata.update");
setBunload(true)
},failure:function(result,request){Ext.MessageBox.alert(translate("errorDeleteAttribute")+name+" "+translate("errorFromDoc")+" / status "+result.status+" text: "+result.statusText+" - "+translate("tryAgain"));
setBunload(true)
}})
}function doRemoveElementAction(action,ref,parentref,id,min){var metadataId=document.mainForm.id.value;
var thisElement=$(id);
var nextElement=thisElement.next();
var prevElement=thisElement.previous();
var myExtAJaxRequest=Ext.Ajax.request({url:getGNServiceURL(action),method:"GET",params:{id:metadataId,ref:ref,parent:parentref},success:function(result,request){var html=result.responseText;
if(html.blank()){if(bottomElement(thisElement)&&document.mainForm.currTab.value!="simple"){swapControls(thisElement,prevElement);
thisElement.remove();
thisElement=prevElement
}else{thisElement.remove();
thisElement=nextElement
}if(topElement(thisElement)){topControls(thisElement,min)
}}else{if(orElement(thisElement)){thisElement.remove()
}else{thisElement.replace(html)
}}setBunload(true)
},failure:function(result,request){Ext.MessageBox.alert(translate("errorDeleteElement")+name+" "+translate("errorFromDoc")+" / status "+result.status+" text: "+result.statusText+" - "+translate("tryAgain"));
setBunload(true)
}})
}function swapControls(el1,el2){var el1Descs=getControlsFromElement(el1);
var el2Descs=getControlsFromElement(el2);
for(var index=0;
index<el1Descs.length;
++index){var visible1=el1Descs[index].visible();
var visible2=el2Descs[index].visible();
if(visible1){el2Descs[index].show()
}else{el2Descs[index].hide()
}if(visible2){el1Descs[index].show()
}else{el1Descs[index].hide()
}}}function doMoveElementAction(action,ref,id){var metadataId=document.mainForm.id.value;
var pars="&id="+metadataId+"&ref="+ref;
var thisElement=$(id);
var myAjax=new Ajax.Request(getGNServiceURL(action),{method:"get",parameters:pars,onSuccess:function(req){if(action.include("elem.up")){var upElement=thisElement.previous();
upElement=upElement.remove();
thisElement.insert({after:upElement});
swapControls(thisElement,upElement)
}else{var downElement=thisElement.next();
downElement=downElement.remove();
thisElement.insert({before:downElement});
swapControls(thisElement,downElement)
}setBunload(true)
},onFailure:function(req){alert(translate("errorMoveElement")+ref+" / status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"));
setBunload(true)
}});
setBunload(true)
}function doNewElementAction(action,ref,name,id,what,max){var child=null;
var orElement=false;
doNewElementAjax(action,ref,name,child,id,what,max,orElement)
}function doNewAttributeAction(action,ref,name,id,what){var child="geonet:attribute";
var max=null;
var orElement=false;
doNewElementAjax(action,ref,name,child,id,what,max,orElement)
}function doNewORElementAction(action,ref,name,child,id,what,max){var orElement=true;
doNewElementAjax(action,ref,name,child,id,what,max,orElement)
}function setAddControls(el,orElement){elDescs=getControlsFromElement(el);
var index=0;
if(elDescs.length==5){index=1
}if(orElement){elDescs[0].hide()
}else{elDescs[0].show()
}if(index==1){if(orElement){elDescs[index].hide()
}else{elDescs[index].show()
}}elDescs[1+index].show();
elDescs[2+index].show();
elDescs[3+index].hide();
if(topElement(el)){elDescs[2+index].hide()
}else{var prevEl=el.previous();
var prevDescs=getControlsFromElement(prevEl);
var prevIndex=0;
if(prevDescs.length==5){prevIndex=1
}prevDescs[0].hide();
if(prevIndex==1){prevDescs[prevIndex].hide()
}prevDescs[1+prevIndex].show();
if(topElement(prevEl)){prevDescs[2+prevIndex].hide()
}else{prevDescs[2+prevIndex].show()
}prevDescs[3+prevIndex].show()
}}function doNewElementAjax(action,ref,name,child,id,what,max,orElement){var metadataId=document.mainForm.id.value;
var pars="&id="+metadataId+"&ref="+ref+"&name="+name;
if(child!=null){pars+="&child="+child
}var thisElement=$(id);
var myAjax=new Ajax.Request(getGNServiceURL(action),{method:"get",parameters:pars,onSuccess:function(req){var html=req.responseText;
if(child=="geonet:attribute"){doSaveAction("metadata.update");
return
}if(what=="replace"){thisElement.replace(html)
}else{if(what=="add"||what=="after"){thisElement.insert({after:html});
setAddControls(thisElement.next(),orElement)
}else{if(what=="before"){thisElement.insert({before:html});
setAddControls(thisElement.previous(),orElement)
}else{alert("doNewElementAjax: invalid what: "+what+" should be one of replace, after or before.")
}}}if(name=="gmd:geographicElement"||name=="gmd:polygon"){extentMap.initMapDiv()
}initCalendar();
validateMetadataFields();
setBunload(true)
},onFailure:function(req){alert(translate("errorAddElement")+name+translate("errorFromDoc")+" / status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"));
setBunload(true)
}})
}function disableEditForm(){var editorOverlay=new Element("div",{id:"editorOverlay"});
$("editFormTable").insert({top:editorOverlay});
$("editorOverlay").setStyle({opacity:"0.65"})
}function doSaveAction(action,validateAction){disableEditForm();
if(typeof validateAction!="undefined"){document.mainForm.showvalidationerrors.value="true"
}else{document.mainForm.showvalidationerrors.value="false"
}var metadataId=document.mainForm.id.value;
var divToRestore=null;
if(action.include("finish")){var myAjax=new Ajax.Request(getGNServiceURL(action),{method:"post",parameters:$("editForm").serialize(true),onSuccess:function(req){var html=req.responseText;
if(divToRestore){divToRestore.removeClassName("editing")
}if(html.startsWith("<?xml")<0){alert(translate("errorSaveFailed")+html)
}setBunload(false);
location.replace(getGNServiceURL("metadata.show?id="+metadataId))
},onFailure:function(req){alert(translate("errorSaveFailed")+"/ status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"));
$("editorBusy").hide();
setBunload(true)
}})
}else{var w=Ext.getCmp("validationReportBox");
if(w){w.destroy()
}var myAjax=new Ajax.Updater({success:document.body},getGNServiceURL(action),{method:"post",parameters:$("editForm").serialize(true),evalScripts:true,onComplete:function(req){if(req.status==200){if(document.mainForm.showvalidationerrors.value=="true"){getValidationReport()
}setBunload(true);
initCalendar();
validateMetadataFields()
}},onFailure:function(req){alert(translate("errorSaveFailed")+"/ status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"));
Element.remove($("editorOverlay"));
setBunload(true)
}})
}}function doCancelAction(action,message){if(confirm(message)){doSaveAction(action);
return true
}return false
}function doConfirm(action,message){if(confirm(message)){doAction(action);
return true
}return false
}function doEditorAlert(divId,imgId){$(divId).style.display="block";
setBunload(true)
}function checkForFileUpload(fref,pref){var fileName=$("_"+fref);
var protoSelect=$("s_"+pref);
var protoIn=$("_"+pref);
var fileUploaded=(fileName!=null&&fileName.value.length>0);
var protocol=protoSelect.value;
var protocolDownload=(protocol.startsWith("WWW:DOWNLOAD")&&protocol.indexOf("http")>0);
if(fileUploaded){if(!protocolDownload){alert(translate("errorChangeProtocol"));
protoSelect.value=protoIn.value
}else{protoIn.value=protoSelect.value
}return
}finput=$("di_"+fref);
fbuttn=$("db_"+fref);
if(protocolDownload){if(finput!=null){finput.hide()
}if(fbuttn!=null){fbuttn.show()
}}else{if(finput!=null){finput.show()
}if(fbuttn!=null){fbuttn.hide()
}}protoIn.value=protoSelect.value
}function startFileUpload(id,fref){Modalbox.show(getGNServiceURL("resources.prepare.upload")+"?ref="+fref+"&id="+id,{title:translate("insertFileMode"),height:200,width:600})
}function doFileUploadSubmit(form){setBunload(false);
var fid=$("fileUploadForm");
var ref=fid.ref;
var fref=fid["f_"+$F(ref)];
var fileName=$F(fref);
if(fileName==""){alert(translate("selectOneFile"));
return false
}AIM.submit(form,{onStart:function(){Modalbox.deactivate()
},onComplete:function(doc){Modalbox.activate();
if(doc.body==null){alert(translate("uploadFailed")+doc)
}else{$("uploadresponse").innerHTML=doc.body.innerHTML
}var fname=$("filename_uploaded");
if(fname!=null){var name=$("_"+$F(ref));
if(name!=null){name.value=fname.getAttribute("title");
$("di_"+$F(ref)).show();
$("db_"+$F(ref)).hide();
Modalbox.show(doc.body.innerHTML,{width:600})
}else{alert(translate("uploadSetFileNameFailed"))
}}}})
}function doFileRemoveAction(action,ref,access,id){var top=findPos($(id));
setBunload(false);
document.mainForm.access.value=access;
document.mainForm.ref.value=ref;
document.mainForm.position.value=top;
document.mainForm.action=action;
goSubmit("mainForm")
}function handleCheckboxAsBoolean(input,ref){if(input.checked){$(ref).value="true"
}else{$(ref).value="false"
}}function setRegion(westField,eastField,southField,northField,region,eltRef,descriptionRef){var choice=region.value;
var w="";
var e="";
var s="";
var n="";
if(choice!=undefined&&choice!=""){coords=choice.split(",");
w=coords[0];
e=coords[1];
s=coords[2];
n=coords[3];
$("_"+westField).value=w;
$("_"+eastField).value=e;
$("_"+southField).value=s;
$("_"+northField).value=n;
if($("_"+descriptionRef)!=null){$("_"+descriptionRef).value=region.text
}}else{$("_"+westField).value="";
$("_"+eastField).value="";
$("_"+southField).value="";
$("_"+northField).value=""
}var viewers=Ext.DomQuery.select(".extentViewer");
for(var idx=0;
idx<viewers.length;
++idx){var viewer=viewers[idx];
if(eltRef==viewer.getAttribute("elt_ref")){extentMap.updateBbox(extentMap.maps[eltRef],westField+","+southField+","+eastField+","+northField,eltRef,true)
}}}function clearRef(ref){setBunload(false);
var ourRef="_"+ref+"_cal";
$(ourRef).clear();
setBunload(true)
}var lastclick=0;
function noDoubleClick(){var now=(new Date()).valueOf();
if((now-lastclick)>500){setBunload(false);
lastclick=now;
return true
}else{return false
}}function buildDuration(ref){if($("Y"+ref).value==""){$("Y"+ref).value=0
}if($("M"+ref).value==""){$("M"+ref).value=0
}if($("D"+ref).value==""){$("D"+ref).value=0
}if($("H"+ref).value==""){$("H"+ref).value=0
}if($("MI"+ref).value==""){$("MI"+ref).value=0
}if($("S"+ref).value==""){$("S"+ref).value=0
}$("_"+ref).value=($("N"+ref).checked?"-":"")+"P"+$("Y"+ref).value+"Y"+$("M"+ref).value+"M"+$("D"+ref).value+"DT"+$("H"+ref).value+"H"+$("MI"+ref).value+"M"+$("S"+ref).value+"S"
}function validateNumber(input,nullValue,decimals){var text=input.value;
var validChars="0123456789";
if(!nullValue){if(!validateNonEmpty(input)){return false
}}if(decimals){validChars+="."
}var isNumber=true;
var c;
for(i=0;
i<text.length&&isNumber;
i++){c=text.charAt(i);
if(c=="-"||c=="+"){if(i<0){isNumber=false
}}else{if(validChars.indexOf(c)==-1){isNumber=false
}}}if(!isNumber){input.addClassName("error");
return false
}else{input.removeClassName("error");
return true
}}function validateNonEmpty(input){if(input.value.length<1){input.addClassName("error");
return false
}else{input.removeClassName("error");
return true
}}function validateEmail(input){if(!isEmail(input.value)){input.addClassName("error");
return false
}else{input.removeClassName("error");
return true
}}function validateMetadataFields(){$$("select.lang_selector").each(function(input){for(i=0;
i<input.options.length;
i++){if(input.options[i].getAttribute("code").toLowerCase()==Env.lang){input.options[i].selected=true
}}enableLocalInput(input,false)
});
$$("input,textarea,select").each(function(input){validateMetadataField(input)
})
}function initCalendar(){var calendars=Ext.DomQuery.select("div.cal");
for(var i=0;
i<calendars.length;
i++){var cal=calendars[i];
var id=cal.id;
cal.id=id+"Id";
if(cal.firstChild==null||cal.childNodes.length==1){var format="Y-m-d";
var formatEl=Ext.getDom(id+"_format");
if(formatEl){format=formatEl.value
}var value=Ext.getDom(id+"_cal").value;
var showTime=(format.indexOf("T")==-1?false:true);
if(showTime){new Ext.ux.form.DateTime({renderTo:cal.id,name:id,id:id,value:value,dateFormat:"Y-m-d",timeFormat:"H:i",hiddenFormat:"Y-m-d\\TH:i:s",dtSeparator:"T"})
}else{new Ext.form.DateField({renderTo:cal,name:id,id:id,width:160,value:value,format:"Y-m-d"})
}}}}var keywordSelectionWindow;
function showKeywordSelectionPanel(ref,name){if(!keywordsSearch){var keywordsSearch=new Openwis.Common.Search.KeywordsSearch({keywordsFromTf:"",isXML:true,listeners:{keywordsSelection:function(records){var id="_X"+ref+"_"+name.replace(":","COLON");
var xml;
var first=true;
Ext.each(records,function(item,index){records[index]=item.replace('<?xml version="1.0" encoding="UTF-8"?>',"").replace(/\"/g,"&quot;").replace(/\r\n/g,"");
if(first){xml=records[index];
first=false
}else{xml+="&amp;&amp;&amp;"+records[index]
}});
var input={tag:"input",type:"hidden",id:id,name:id,value:xml};
var dh=Ext.DomHelper;
dh.append(Ext.get("hiddenFormElements"),input);
doAction("metadata.update")
},scope:this}})
}}var searchKeywordSelectionWindow;
function showSearchKeywordSelectionPanel(){if(!searchKeywordSelectionWindow){var searchKeywordSelectionPanel=new app.KeywordSelectionPanel({listeners:{keywordselected:function(panel,keywords){var xml;
var first=true;
Ext.each(keywords,function(item,index){xml=keywords[index];
var doc;
if(window.ActiveXObject){var doc=new ActiveXObject("Microsoft.XMLDOM");
doc.async="false";
doc.loadXML(xml)
}else{var doc=new DOMParser().parseFromString(xml,"text/xml")
}var keys=doc.getElementsByTagName("gmd:keyword");
var kw;
Ext.each(keys,function(item,index){var kw=keys[index].childNodes[1].childNodes[0].nodeValue;
addKeyword(kw,first);
first=false
})
})
}}});
searchKeywordSelectionWindow=new Ext.Window({width:620,height:300,title:translate("keywordSelectionWindowTitle"),layout:"fit",items:searchKeywordSelectionPanel,closeAction:"hide",constrain:true,iconCls:"searchIcon"})
}searchKeywordSelectionWindow.show()
}function addKeyword(k,first){k='"'+k+'"';
if(first){$("themekey").value=""
}if($("themekey").value!=""){$("themekey").value+=" or "+k
}else{$("themekey").value=k
}}function showLinkedMetadataSelectionPanel(ref,name){var single=((name=="uuidref"||name=="iso19110"||name=="")?true:false);
var linkedMetadataSelectionPanel=new app.LinkedMetadataSelectionPanel({ref:ref,singleSelect:single,mode:name,listeners:{linkedmetadataselected:function(panel,metadata){if(single){if(this.ref!=null){$("_"+this.ref+(name!=""?"_"+name:"")).value=metadata[0].data.uuid
}else{if(this.mode=="iso19110"){var url="xml.relation.insert?parentId="+document.mainForm.id.value+"&childUuid="+metadata[0].data.uuid;
var myExtAJaxRequest=Ext.Ajax.request({url:url,method:"GET",success:function(result,request){var html=result.responseText;
doAction("metadata.update")
},failure:function(result,request){Ext.MessageBox.alert(translate("error")+" / status "+result.status+" text: "+result.statusText+" - "+translate("tryAgain"));
setBunload(true)
}})
}}}else{var inputs=[];
var multi=metadata.length>1?true:false;
Ext.each(metadata,function(md,index){if(multi){name=name+"_"+index
}inputs.push({tag:"input",type:"hidden",id:name,name:name,value:md.data.uuid})
});
var dh=Ext.DomHelper;
dh.append(Ext.get("hiddenFormElements"),inputs)
}}}});
var linkedMetadataSelectionWindow=new Ext.Window({title:translate("linkedMetadataSelectionWindowTitle"),width:620,height:300,layout:"fit",items:linkedMetadataSelectionPanel,closeAction:"hide",constrain:true,iconCls:"linkIcon",modal:true});
linkedMetadataSelectionWindow.show()
}function removeLinkedServiceMetadata(uuid,uuidref){var serviceUpdateUrl="xml.metadata.processing?uuid="+uuid+"&process=update-srv-detachDataset&uuidref="+uuidref;
setBunload(false);
Ext.Ajax.request({url:serviceUpdateUrl,method:"GET",success:function(result,request){var response=result.responseText;
if(response.indexOf("Not owner")!=-1){alert(translate("NotOwnerError"))
}else{if(response.indexOf("error")!=-1){alert(translate("error")+response)
}}doAction("metadata.edit")
},failure:function(result,request){Ext.MessageBox.alert(translate("ServiceUpdateError"));
setBunload(true)
}})
}function removeLinkedFeatureMetadata(parentUuid,childUuid){var serviceUpdateUrl="xml.relation.delete?parentUuid="+parentUuid+"&childUuid="+childUuid;
setBunload(false);
Ext.Ajax.request({url:serviceUpdateUrl,method:"GET",success:function(result,request){var response=result.responseText;
if(response.indexOf("Not owner")!=-1){alert(translate("NotOwnerError"))
}else{if(response.indexOf("error")!=-1){alert(translate("error")+response)
}}doAction("metadata.edit")
},failure:function(result,request){Ext.MessageBox.alert(translate("ServiceUpdateError"));
setBunload(true)
}})
}function showLinkedServiceMetadataSelectionPanel(name,serviceUrl,uuid){var linkedMetadataSelectionPanel=new app.LinkedMetadataSelectionPanel({mode:name,autoWidth:true,ref:null,proxy:getProxy(),serviceUrl:serviceUrl,region:"north",uuid:uuid,createIfNotExistURL:"metadata.create.form?type="+(name=="attachService"?"service":"dataset"),singleSelect:true,listeners:{linkedmetadataselected:function(panel,metadata){var layerName=panel.layerName.getValue();
if(name=="attachService"){var serviceUpdateUrl="xml.metadata.processing?uuid="+metadata[0].data.uuid+"&process=update-srv-attachDataset&uuidref="+uuid+"&scopedName="+layerName;
Ext.Ajax.request({url:serviceUpdateUrl,method:"GET",success:function(result,request){var response=result.responseText;
if(response.indexOf("Not owner")!=-1){alert(translate("NotOwnerError"))
}else{if(response.indexOf("error")!=-1){alert(translate("error")+response)
}}doAction("metadata.edit")
},failure:function(result,request){Ext.MessageBox.alert(translate("ServiceUpdateError"));
setBunload(true)
}})
}else{var datasetUpdateUrl="xml.metadata.processing?uuid="+uuid+"&process=update-srv-attachDataset&uuidref="+metadata[0].data.uuid+"&scopedName="+layerName;
Ext.Ajax.request({url:datasetUpdateUrl,method:"GET",success:function(result,request){var response=result.responseText;
if(response.indexOf("Not owner")!=-1){alert(translate("NotOwnerError"))
}else{if(response.indexOf("error")!=-1){alert(translate("error")+response)
}}doAction("metadata.edit")
},failure:function(result,request){Ext.MessageBox.alert(translate("ServiceUpdateError"));
setBunload(true)
}})
}},scope:this}});
var linkedMetadataSelectionWindow=new Ext.Window({title:(name=="attachService"?translate("associateService"):translate("associateDataset")),layout:"fit",width:620,height:400,items:linkedMetadataSelectionPanel,closeAction:"hide",constrain:true,iconCls:"linkIcon",modal:true});
linkedMetadataSelectionWindow.show()
}var crsSelectionWindow;
function showCRSSelectionPanel(ref,name){if(!crsSelectionWindow){var crsSelectionPanel=new app.CRSSelectionPanel({listeners:{crsSelected:function(xml){var id="_X"+ref+"_"+name.replace(":","COLON");
var input={tag:"input",type:"hidden",id:id,name:id,value:xml};
var dh=Ext.DomHelper;
dh.append(Ext.get("hiddenFormElements"),input);
doAction("metadata.update")
}}});
crsSelectionWindow=new Ext.Window({title:translate("crsSelectionWindowTitle"),layout:"fit",width:620,height:300,items:crsSelectionPanel,closeAction:"hide",constrain:true,iconCls:"searchIcon"})
}crsSelectionWindow.items.get(0).setRef(ref);
crsSelectionWindow.show()
}function validateMetadataField(input){var ch=input.getAttribute("onchange");
var ku=input.getAttribute("onkeyup");
if(typeof(ch)=="function"){ch=ch.toString()
}if(typeof(ku)=="function"){ku=ku.toString()
}if(!input||(ch!=null&&ch.indexOf("validate")==-1)||(ku!=null&&ku.indexOf("validate")==-1)){return
}if(input.onkeyup){input.onkeyup()
}if(input.onchange){input.onchange()
}}function enableLocalInput(node,focus){var ref=node.value;
var parent=node.parentNode.parentNode;
var nodes=parent.getElementsByTagName("input");
var textarea=parent.getElementsByTagName("textarea");
show(nodes,ref,focus);
show(textarea,ref,focus)
}function clearSuggestion(divSuggestion){if($(divSuggestion)!=null){$(divSuggestion).innerHTML=""
}}function show(nodes,ref,focus){for(index in nodes){var input=nodes[index];
if(input.style!=null&&input.style.display!="none"){input.style.display="none"
}}for(index in nodes){var input=nodes[index];
if(input.name==ref){input.style.display="block";
if(focus){input.focus()
}}}}function googleTranslate(ref,divSuggestion,target,fromLang,toLang){var map={GE:"de",SP:"es",CH:"zh"};
if(map[fromLang]){fromLang=map[fromLang]
}if(map[toLang]){toLang=map[toLang]
}if($(ref).value==""){alert(translate("translateWithGoogle.emptyInput"));
return
}if($(ref).value.length>5000){alert(translate("translateWithGoogle.maxSize"));
return
}if($(divSuggestion)!=null){$(divSuggestion).innerHTML=""
}google.language.translate($(ref).value,fromLang,toLang,function(result){if(!result.error){var suggestion=result.translation.replace(/&#39;/g,"'").replace(/&quot;/g,'"');
if($(target)!=null){$(target).value=suggestion
}if($(divSuggestion)!=null){$(divSuggestion).innerHTML=suggestion;
$(divSuggestion).style.display="block"
}}else{alert(result.error.message+" ("+result.error.code+")")
}validateMetadataField($(target))
})
}function updateUpperCardinality(ref,value){var isInf=ref+"_isInfinite";
if(value=="0"||value=="1"){$(ref).value=value;
$(isInf).value="false"
}else{if(value=="n"){$(ref).value="";
$(isInf).value="true"
}else{$(ref).value="";
$(isInf).value="false"
}}}function updateValidationReportVisibilityRules(errorOnly){$("validationReport").descendants().each(function(el){if(el.nodeName=="SPAN"){errors=$(el).next().descendants().filter(function(possibleError){return(possibleError.nodeName=="LI"&&possibleError.getAttribute("name")=="error")
});
if(errors.length==0&&errorOnly){el.style.display="none"
}else{el.style.display="block"
}}else{if(el.nodeName=="LI"){if(el.getAttribute("name")=="pass"&&errorOnly){el.style.display="none"
}else{el.style.display="block"
}}}})
}function getValidationReport(){var metadataId=document.mainForm.id.value;
var pars="&id="+metadataId;
var action="metadata.validate";
var myAjax=new Ajax.Request(getGNServiceURL(action),{method:"get",parameters:pars,onSuccess:function(req){var html=req.responseText;
displayBox(html,"validationReport",false);
updateValidationReportVisibilityRules($("checkError").checked);
setBunload(true)
},onFailure:function(req){alert(translate("errorOnAction")+action+" / status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"));
$("editorBusy").hide();
setBunload(true)
}})
}function computeExtentFromKeywords(mode){window.location.replace("metadata.processing?id="+document.mainForm.id.value+"&process=add-extent-from-geokeywords&url="+Env.host+Env.locService+"&replace="+mode)
};var getGNServiceURL=function(service){return Env.locService+"/"+service
};
function setAll(id){var list=$(id).getElementsByTagName("input");
for(var i=0;
i<list.length;
i++){list[i].checked=true
}}function clearAll(id){var list=$(id).getElementsByTagName("input");
for(var i=0;
i<list.length;
i++){list[i].checked=false
}}function checkBoxModalUpdate(div,service,modalbox,title){var boxes=$(div).getElementsBySelector('input[type="checkbox"]');
var pars="&id="+$("metadataid").value;
boxes.each(function(s){if(s.checked){pars+="&"+s.name+"=on"
}});
if(modalbox!=null&&modalbox){service=getGNServiceURL(service)+"?"+pars;
Modalbox.show(service,{title:title,width:600})
}else{var myAjax=new Ajax.Request(getGNServiceURL(service),{method:"get",parameters:pars,onSuccess:function(){},onFailure:function(req){alert(translate("error")+service+" / status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"))
}});
window.Modalbox.hide()
}}function checkCreate(service,id){descs=$("groups").getValue();
if(descs.length==0){alert(translate("userAtLeastOneGroup"));
return false
}return true
}function doConfirmDelete(url,message,title,id,boxTitle){if(confirm(message+" ("+title+")")){var divToHide;
if(opener){divToHide=opener.$(id)
}else{divToHide=$(id)
}if(divToHide){divToHide.hide()
}Modalbox.show(url,{title:boxTitle,width:600,afterHide:function(){if($("simple_search_pnl").visible()){runSimpleSearch()
}else{if($("advanced_search_pnl").visible()){runAdvancedSearch()
}else{location.replace(getGNServiceURL("main.home"))
}}runRssSearch()
}});
return true
}return false
}function doOtherButton(url,title,width){Modalbox.show(url,{title:title,width:width,height:400});
return true
}function doAction(action){document.mainForm.action=action;
goSubmit("mainForm")
}function doTabAction(action,tab){document.mainForm.currTab.value=tab;
doAction(action)
}function setBunload(on){}function runFileDownload(href,title){if(href.include("resources.get")){location.replace(getGNServiceURL(href))
}else{Modalbox.show(getGNServiceURL(href),{title:title,height:400,width:600})
}}function runFileDownloadSummary(uuid,title){pars="&uuid="+uuid;
var myAjax=new Ajax.Request(getGNServiceURL("prepare.file.download"),{method:"get",parameters:pars,onSuccess:function(req){Modalbox.show(req.responseText,{title:title,height:400,width:600})
},onFailure:function(req){alert(translate("error")+" "+getGNServiceURL("prepare.file.download")+" failed: status "+req.status+" text: "+req.statusText+" - "+translate("tryAgain"))
}})
}function massiveUpdateChildren(service,title,width){var url=getGNServiceURL(service);
Modalbox.show(url,{title:title,width:width})
}function updateChildren(div,url,onFailureMsg){var pars="&id="+$("id").value+"&parentUuid="+$("parentUuid").value+"&schema="+$("schema").value+"&childrenIds="+$("childrenIds").value;
var boxes=$(div).getElementsBySelector('input[type="checkbox"]');
boxes.each(function(s){if(s.checked){pars+="&"+s.name+"=true"
}});
var radios=$(div).getElementsBySelector('input[type="radio"]');
radios.each(function(radio){if(radio.checked){pars+="&"+radio.name+"="+radio.value
}});
Ext.Ajax.request({url:Env.locService+"/"+url,method:"GET",params:pars,success:function(result,request){var xmlNode=result.responseXML;
if(xmlNode.childNodes.length!=0&&xmlNode.childNodes[0].localName=="response"){var response=xmlNode.childNodes[0].childNodes[0].nodeValue;
alert(response);
window.Modalbox.hide()
}else{alert(onFailureMsg)
}},failure:function(result,request){alert(onFailureMsg)
}})
};function toolTip(spanId){elem=$(spanId);
if(elem.childElements().length==0){var tokens=elem.getAttribute("id").split("|");
var schema=tokens[0].substring(5);
var name=tokens[1];
var context=tokens[2];
var isoType=tokens[3];
var request=str.substitute(toolTipRequestTemp,{SCHEMA:schema,NAME:name,CONTEXT:context,ISOTYPE:isoType});
ker.send("xml.schema.info",request,ker.wrap(this,function(xmlRes){var htmlTip="";
tip=document.createElement("div");
tip.className="toolTipOverlay";
if(xmlRes.nodeName=="error"){htmlTip=translate("cannotGetTooltip")
}else{htmlTip=getHtmlTip(xmlRes.getElementsByTagName("element")[0])
}tip.innerHTML=htmlTip;
elem.appendChild(tip)
}))
}else{childs=elem.childElements();
childs[0].toggle()
}}function getHtmlTip(node){var err=node.getAttribute("error");
if(err!=null){var temp=toolTipErrorTemp;
var msg="ERROR : "+err;
var data={ERROR:msg};
return str.substitute(toolTipErrorTemp,data)
}else{var temp=toolTipTemp;
var label=xml.evalXPath(node,"label");
var descr=xml.evalXPath(node,"description");
var cond=xml.evalXPath(node,"condition");
var help=xml.evalXPath(node,"help");
if(cond==null){cond=""
}if(help==null){help=""
}var data={LABEL:label,DESCRIPTION:descr,CONDITION:cond,HELP:help};
return str.substitute(toolTipTemp,data)
}}var toolTipRequestTemp='<request xmlns:gmd="http://www.isotc211.org/2005/gmd"         xmlns:gts="http://www.isotc211.org/2005/gts"         xmlns:srv="http://www.isotc211.org/2005/srv"         xmlns:gml="http://www.opengis.net/gml"         xmlns:gfc="http://www.isotc211.org/2005/gfc"         xmlns:gco="http://www.isotc211.org/2005/gco"         xmlns:dct="http://purl.org/dc/terms/"         xmlns:dc ="http://purl.org/dc/elements/1.1/">   <element schema="{SCHEMA}" name="{NAME}" context="{CONTEXT}" isoType="{ISOTYPE}"/></request>';
var toolTipTemp='   <b>{LABEL}</b>   <br/>   <span>{DESCRIPTION}</span>   <br/>   <font color="#C00000">{CONDITION}</font>   <i>{HELP}</i>';
var toolTipErrorTemp='   <font color="#C00000">{ERROR}</font>';function Tooltip(ldr,el){var loader=ldr;
var elem=el;
var shown=false;
var exited=false;
var tip=null;
var timer=null;
var initDelay=1000;
Event.observe(elem,"mouseover",ker.wrap(this,mouseIn));
Event.observe(elem,"mouseout",ker.wrap(this,mouseOut));
function mouseIn(event){if(shown){return
}var x=Event.pointerX(event)+12;
var y=Event.pointerY(event)+12;
if(tip==null){setupTooltip(x,y)
}else{tip.style.left=x;
tip.style.top=y;
timer=setTimeout(ker.wrap(this,mouseIn_CB),initDelay)
}}function mouseIn_CB(){Element.show(tip);
shown=true;
timer=null
}function mouseOut(event){exited=true;
if(timer){clearTimeout(timer);
timer=null
}if(!shown){return
}Element.hide(tip);
shown=false
}function setupTooltip(x,y){var id=elem.getAttribute("id");
var tokens=id.substring(4).split("|");
var schema=tokens[0];
var name=tokens[1];
var context=tokens[2];
var isoType=tokens[3];
var request=str.substitute(requestTemp,{SCHEMA:schema,NAME:name,CONTEXT:context,ISOTYPE:isoType});
exited=false;
ker.send("xml.schema.info",request,ker.wrap(this,function(xmlRes){if(xmlRes.nodeName=="error"){ker.showError(loader.getText("cannotGet"),xmlRes)
}else{var htmlTip=getHtmlTip(xmlRes.getElementsByTagName("element")[0]);
tip=document.createElement("div");
tip.className="tooltip";
tip.innerHTML=htmlTip;
tip.style.display="none";
tip.style.zIndex=32000;
document.body.appendChild(tip);
tip.style.left=x;
tip.style.top=y;
if(!exited){timer=setTimeout(ker.wrap(this,mouseIn_CB),300)
}}}))
}function getHtmlTip(node){var err=node.getAttribute("error");
if(err!=null){var temp=errorTemp;
var msg=loader.getText("error")+" : "+err;
var data={ERROR:msg};
return str.substitute(errorTemp,data)
}else{var temp=tooltipTemp;
var label=xml.evalXPath(node,"label");
var descr=xml.evalXPath(node,"description");
var cond=xml.evalXPath(node,"condition");
var help=xml.evalXPath(node,"help");
if(cond==null){cond=""
}if(help==null){help=""
}var data={LABEL:label,DESCRIPTION:descr,CONDITION:cond,HELP:help};
return str.substitute(tooltipTemp,data)
}}var requestTemp='<request xmlns:gmd="http://www.isotc211.org/2005/gmd"         xmlns:gts="http://www.isotc211.org/2005/gts"         xmlns:srv="http://www.isotc211.org/2005/srv"         xmlns:gml="http://www.opengis.net/gml"         xmlns:gco="http://www.isotc211.org/2005/gco"         xmlns:dct="http://purl.org/dc/terms/"         xmlns:dc = "http://purl.org/dc/elements/1.1/">   <element schema="{SCHEMA}" name="{NAME}" context="{CONTEXT}" isoType="{ISOTYPE}"/></request>';
var tooltipTemp='   <b>{LABEL}</b>   <br>   {DESCRIPTION}   <br>   <font color="#C00000">{CONDITION}</font>   <i>{HELP}</i>';
var errorTemp='   <font color="#C00000">{ERROR}</font>'
};var tipMan=null;
function init(){tipMan=new TooltipManager();
ker.loadMan.wait(tipMan)
}function TooltipManager(){var loader=new XMLLoader(Env.locUrl+"/xml/editor.xml");
this.init=init;
function init(){var list=document.getElementsByTagName("SPAN");
for(var i=0;
i<list.length;
i++){var id=list[i].getAttribute("id");
if(id!=null){if(id.startsWith("tip.")){new Tooltip(loader,list[i])
}}}}};