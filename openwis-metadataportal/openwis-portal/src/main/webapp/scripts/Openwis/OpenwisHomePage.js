Ext.ns("Openwis.Common.Dissemination");
Openwis.Common.Dissemination.MailDiffusion=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{border:false,allowAddressEdition:true});
Openwis.Common.Dissemination.MailDiffusion.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getAddressTextField());
this.getAdvancedFieldSet().add(this.getHeaderLineTextField());
this.getAdvancedFieldSet().add(this.getDispatchModeComboBox());
this.getAdvancedFieldSet().add(this.getSubjectTextField());
this.getAdvancedFieldSet().add(this.getFileNameTextField());
this.add(this.getAdvancedFieldSet());
this.mailFields={};
this.mailFields.address=this.getAddressTextField();
this.mailFields.headerLine=this.getHeaderLineTextField();
this.mailFields.mailDispatchMode=this.getDispatchModeComboBox();
this.mailFields.subject=this.getSubjectTextField();
this.mailFields.fileName=this.getFileNameTextField()
},getAddressTextField:function(){if(!this.addressTextField){var addressStore=new Ext.data.JsonStore({id:0,fields:[{name:"address"},{name:"headerLine"},{name:"mailDispatchMode"},{name:"subject"},{name:"mailAttachmentMode"},{name:"fileName"}]});
this.addressTextField=new Ext.form.ComboBox({store:addressStore,valueField:"address",displayField:"address",typeAhead:true,mode:"local",triggerAction:"all",selectOnFocus:true,fieldLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.MailAddress.label"),allowBlank:false,disabled:!this.allowAddressEdition,vtype:"email",width:210,validateOnChange:true,listeners:{select:function(){this.notifyMailSelected()
},scope:this}})
}return this.addressTextField
},getAdvancedFieldSet:function(){if(!this.advancedFieldSet){this.advancedFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Common.Dissemination.MailDiffusion.Advanced.label"),autoHeight:true,collapsed:true,collapsible:true,labelWidth:90})
}return this.advancedFieldSet
},getHeaderLineTextField:function(){if(!this.headerLineTextField){this.headerLineTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.HeaderLine.label"),name:"headerLine",allowBlank:true})
}return this.headerLineTextField
},getDispatchModeComboBox:function(){if(!this.dispatchModeComboBox){this.dispatchModeComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["TO","TO:"],["CC","CC:"],["BCC","BCC:"]]}),valueField:"id",displayField:"value",value:"TO",typeAhead:true,mode:"local",triggerAction:"all",selectOnFocus:true,fieldLabel:"Email TO: / CC: / BCC",width:50,editable:false})
}return this.dispatchModeComboBox
},getSubjectTextField:function(){if(!this.subjectTextField){this.subjectTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.Subject.label"),name:"subject",allowBlank:true})
}return this.subjectTextField
},getAttachmentModeRadioGroup:function(){if(!this.attachementModeRadioGroup){this.attachementModeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.AttachmentMode.label"),items:[this.getAsAttachementRadio(),this.getEmbeddedInBodyRadio()]})
}return this.attachementModeRadioGroup
},getAsAttachementRadio:function(){if(!this.asAttachmentRadio){this.asAttachmentRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.AsAttachment.label"),name:"attachmentMode",inputValue:"AS_ATTACHMENT",checked:true})
}return this.asAttachmentRadio
},getEmbeddedInBodyRadio:function(){if(!this.embeddedInBodyRadio){this.embeddedInBodyRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.EmbeddedInBody.label"),name:"attachmentMode",inputValue:"EMBEDDED_IN_BODY"})
}return this.embeddedInBodyRadio
},getFileNameTextField:function(){if(!this.fileNameTextField){this.fileNameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.MailDiffusion.FileName.label"),name:"fileName",allowBlank:true,width:150})
}return this.fileNameTextField
},notifyMailSelected:function(){var mailAddressSelected=this.getAddressTextField().getValue();
if(mailAddressSelected){var mailSelected=null;
for(var i=0;
i<this.getAddressTextField().getStore().getCount();
i++){if(this.getAddressTextField().getStore().getAt(i).get("address")==mailAddressSelected){mailSelected=this.getAddressTextField().getStore().getAt(i).data;
break
}}this.initializeFields(mailSelected)
}},initializeFields:function(mail){Ext.iterate(mail,function(key,value){if(this.mailFields[key]){this.mailFields[key].setValue(value)
}},this)
},getDisseminationValue:function(){var mail={mailAttachmentMode:"AS_ATTACHMENT"};
this.getAddressTextField().setValue(this.getAddressTextField().getRawValue());
Ext.iterate(this.mailFields,function(key,field){if(!Ext.isObject(field.getValue())){if(field.getValue()=="null"||field.getValue()==null){mail[key]=""
}else{mail[key]=field.getValue()
}}else{mail[key]=field.getValue().inputValue
}},this);
return mail
},refresh:function(favoritesMails){this.getAddressTextField().getStore().loadData(favoritesMails)
}});Ext.ns("Openwis.Common.Dissemination");
Openwis.Common.Dissemination.FTPDiffusion=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{border:false,allowHostEdition:true});
Openwis.Common.Dissemination.FTPDiffusion.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHostTextField());
this.add(this.getPathTextField());
this.add(this.getUserTextField());
this.add(this.getPasswordTextField());
this.getAdvancedFieldSet().add(this.getPortTextField());
this.getAdvancedFieldSet().add(this.getOptionsCheckboxGroup());
this.getAdvancedFieldSet().add(this.getFileNameTextField());
this.add(this.getAdvancedFieldSet());
this.ftpFields={};
this.ftpFields.uuid=this.getUUID();
this.ftpFields.host=this.getHostTextField();
this.ftpFields.path=this.getPathTextField();
this.ftpFields.user=this.getUserTextField();
this.ftpFields.password=this.getPasswordTextField();
this.ftpFields.port=this.getPortTextField();
this.ftpFields.checkFileSize=this.getCheckFileSizeCheckbox();
this.ftpFields.fileName=this.getFileNameTextField()
},getHostTextField:function(){if(!this.hostTextField){var hostStore=new Ext.data.JsonStore({id:0,fields:[{name:"uuid"},{name:"host"},{name:"path"},{name:"user"},{name:"password"},{name:"port"},{name:"checkFileSize"},{name:"fileName"},]});
this.hostTextField=new Ext.form.ComboBox({store:hostStore,valueField:"host",displayField:"host",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,fieldLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.Host.label"),allowBlank:false,disabled:!this.allowHostEdition,width:210,listeners:{select:function(){this.notifyFTPSelected()
},scope:this}})
}return this.hostTextField
},getPathTextField:function(){if(!this.pathTextField){this.pathTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.Path.label"),name:"ftpPath",allowBlank:false})
}return this.pathTextField
},getUserTextField:function(){if(!this.userTextField){this.userTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.User.label"),name:"ftpUser",allowBlank:false,width:100})
}return this.userTextField
},getPasswordTextField:function(){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.Password.label"),name:"ftpPwd",inputType:"password",allowBlank:false,width:100})
}return this.passwordTextField
},getAdvancedFieldSet:function(){if(!this.advancedFieldSet){this.advancedFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Common.Dissemination.FTPDiffusion.Advanced.label"),autoHeight:true,collapsed:true,collapsible:true,labelWidth:90})
}return this.advancedFieldSet
},getOptionsCheckboxGroup:function(){if(!this.optionsCheckboxGroup){this.optionsCheckboxGroup=new Ext.form.CheckboxGroup({title:Openwis.i18n("Common.Dissemination.FTPDiffusion.Options.label"),items:[this.getCheckFileSizeCheckbox(),]})
}return this.optionsCheckboxGroup
},getPortTextField:function(){if(!this.portTextField){this.portTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.Port.label"),name:"ftpPort",allowBlank:true,width:50})
}return this.portTextField
},getPassiveCheckbox:function(){if(!this.passiveCheckbox){this.passiveCheckbox=new Ext.form.Checkbox({boxLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.Passive.label"),name:"ftpPassive"})
}return this.passiveCheckbox
},getCheckFileSizeCheckbox:function(){if(!this.checkFileSizeCheckbox){this.checkFileSizeCheckbox=new Ext.form.Checkbox({boxLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.CheckFileSize.label"),name:"ftpCheckFileSize"})
}return this.checkFileSizeCheckbox
},getEncryptedCheckbox:function(){if(!this.encryptedCheckbox){this.encryptedCheckbox=new Ext.form.Checkbox({boxLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.Encrypted.label"),name:"encrypted"})
}return this.encryptedCheckbox
},getFileNameTextField:function(){if(!this.fileNameTextField){this.fileNameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Common.Dissemination.FTPDiffusion.FileName.label"),name:"fileName",allowBlank:true,width:200})
}return this.fileNameTextField
},getUUID:function(){if(!this.UUIDField){this.UUIDField={uuid:Openwis.Utils.UUID.generateUUID(),setValue:function(uuid){if(uuid){this.uuid=uuid
}},getValue:function(){return this.uuid
}}
}return this.UUIDField
},notifyFTPSelected:function(){var ftpHostSelected=this.getHostTextField().getValue();
if(ftpHostSelected){var ftpSelected=null;
for(var i=0;
i<this.getHostTextField().getStore().getCount();
i++){if(this.getHostTextField().getStore().getAt(i).get("host")==ftpHostSelected){ftpSelected=this.getHostTextField().getStore().getAt(i).data;
break
}}this.initializeFields(ftpSelected)
}},initializeFields:function(ftp){Ext.iterate(ftp,function(key,value){if(this.ftpFields[key]){this.ftpFields[key].setValue(value)
}},this)
},getDisseminationValue:function(){var ftp={passive:false,encrypted:false,};
Ext.iterate(this.ftpFields,function(key,field){ftp[key]=field.getValue()
},this);
return ftp
},refresh:function(favoritesFtps){this.getHostTextField().getStore().loadData(favoritesFtps)
}});Ext.ns("Openwis.Common.Request.Utils");
Openwis.Common.Request.Utils.statusRendererImg=function(val){if(val=="FAILED"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_failed.png"/>'
}else{if(val=="IN_PROGRESS"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_inprogress.png"/>'
}else{if(val=="COMPLETE"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_complete.png"/>'
}else{return'<img src="'+configOptions.url+'/images/openwis/icons/status_unknown.png"/>'
}}}};
Openwis.Common.Request.Utils.stateRendererImg=function(val){if(val=="INVALID"){return'<img src="'+configOptions.url+'/images/openwis/icons/subscription_invalid.png" title="'+Openwis.i18n("Subscription.state.invalid")+'"/>'
}else{if(val=="ACTIVE"){return'<img src="'+configOptions.url+'/images/openwis/icons/subscription_active.gif" title="'+Openwis.i18n("Subscription.state.active")+'"/>'
}else{if(val=="SUSPENDED"){return'<img src="'+configOptions.url+'/images/openwis/icons/subscription_inactive.gif" title="'+Openwis.i18n("Subscription.state.suspended")+'"/>'
}else{if(val=="SUSPENDED_BACKUP"){return'<img src="'+configOptions.url+'/images/openwis/icons/subscription_inactive_backup.gif" title="'+Openwis.i18n("Subscription.state.suspended.backup")+'"/>'
}else{return'<img src="'+configOptions.url+'/images/openwis/icons/state_unknown.png" title="'+Openwis.i18n("Subscription.state.unkown")+'"/>'
}}}}};
Openwis.Common.Request.Utils.processedRequestStatusRendererImg=function(val){if(val=="FAILED"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_failed.png"/>'
}else{if(val=="DISSEMINATED"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_complete.png"/>'
}else{return'<img src="'+configOptions.url+'/images/openwis/icons/status_inprogress.png"/>'
}}};
Openwis.Common.Request.Utils.statusRenderer=function(val){if(val=="FAILED"){return Openwis.i18n("Common.Status.Failed")
}else{if(val=="IN_PROGRESS"){return Openwis.i18n("Common.Status.InProgress")
}else{if(val=="COMPLETE"){return Openwis.i18n("Common.Status.Complete")
}else{return Openwis.i18n("Common.Status.Unknown")
}}}};
Openwis.Common.Request.Utils.sizeRenderer=function(val){if(val==0){return""
}else{return Openwis.Utils.Misc.bytesToKMG(val)
}};
Openwis.Common.Request.Utils.backupRenderer=function(val){if(val&&val.name&&val.name.trim()!=""){return val.name
}else{return""
}};
Openwis.Common.Request.Utils.requestTypeRenderer=function(val){if(val=="ADHOC"){return"R"
}return"S"
};
Openwis.Common.Request.Utils.htmlSafeRenderer=function(val){return Ext.util.Format.htmlEncode(val)
};
Openwis.Common.Request.Utils.accountStatusRendererImg=function(val){if(val=="INACTIVE"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_failed.png"/>'
}else{if(val=="ACTIVE"){return'<img src="'+configOptions.url+'/images/openwis/icons/status_complete.png"/>'
}}};Ext.ns("Openwis.Common.Components");
Openwis.Common.Components.DateTimeExtentSelection=Ext.extend(Ext.form.CompositeField,{dateSelection:true,defaultDateFrom:null,defaultDateTo:null,dateMin:null,dateMax:null,excludedDates:null,timeSelection:true,allowBlankTimeSelection:false,defaultTimeFrom:null,defaultTimeTo:null,timeMin:null,timeMax:null,timeEditable:false,initComponent:function(){Ext.apply(this,{labelWidth:120,items:[{xtype:"container",border:false,html:Openwis.i18n("Common.Extent.Temporal.From")},this.getFromDateField(),this.getFromTimeField(),{xtype:"container",border:false,html:Openwis.i18n("Common.Extent.Temporal.To")},this.getToDateField(),this.getToTimeField()]});
this.initialize();
Openwis.Common.Components.DateTimeExtentSelection.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged")
},initialize:function(){if(this.defaultDateFrom){this.getFromDateField().setValue(this.defaultDateFrom)
}if(this.defaultDateTo){this.getToDateField().setValue(this.defaultDateTo)
}if(this.dateMin){this.getFromDateField().setMinValue(this.dateMin);
this.getToDateField().setMinValue(this.dateMin)
}if(this.dateMax){this.getFromDateField().setMaxValue(this.dateMax);
this.getToDateField().setMaxValue(this.dateMax)
}if(this.excludedDates&&!Ext.isEmpty(this.excludedDates)){this.getFromDateField().setDisabledDates(this.excludedDates);
this.getToDateField().setDisabledDates(this.excludedDates)
}if(this.defaultTimeFrom){this.getFromTimeField().setValue(this.defaultTimeFrom)
}if(this.defaultTimeTo){this.getToTimeField().setValue(this.defaultTimeTo)
}if(this.timeMin){this.getFromTimeField().setMinValue(this.timeMin);
this.getToTimeField().setMinValue(this.timeMin)
}if(this.timeMax){this.getFromTimeField().setMaxValue(this.timeMax);
this.getToTimeField().setMaxValue(this.timeMax)
}},getFromDateField:function(){if(!this.fromDateField){this.fromDateField=this.createDateField()
}return this.fromDateField
},getFromTimeField:function(){if(!this.fromTimeField){this.fromTimeField=this.createTimeField()
}return this.fromTimeField
},getToDateField:function(){if(!this.toDateField){this.toDateField=this.createDateField()
}return this.toDateField
},getToTimeField:function(){if(!this.toTimeField){this.toTimeField=this.createTimeField()
}return this.toTimeField
},createDateField:function(){return new Ext.form.DateField({allowBlank:false,editable:false,hidden:!this.dateSelection,disabled:!this.dateSelection,format:"m/d/Y",listeners:{select:function(){this.fireEvent("valueChanged")
},scope:this}})
},createTimeField:function(){return new Ext.form.TimeField({allowBlank:false,editable:this.timeEditable,hidden:!this.timeSelection,disabled:!this.timeSelection,increment:15,format:"H:i",width:60,listeners:{select:function(){this.fireEvent("valueChanged")
},scope:this}})
},getRawValue:function(){var value={};
if(this.dateSelection){value.dateFrom=this.getFromDateField().getValue();
value.dateTo=this.getToDateField().getValue()
}if(this.timeSelection){value.timeFrom=this.getFromTimeField().getValue();
value.timeTo=this.getToTimeField().getValue()
}return value
},getValue:function(){return this.processValue(this.getRawValue())
},buildValue:function(){return this.getValue()
},processValue:function(value){return value
},validateValue:function(){Ext.QuickTips.init();
var simpleValidation=Openwis.Common.Components.DateTimeExtentSelection.superclass.validateValue.call(this);
if(simpleValidation){var value=this.getRawValue();
if(this.dateSelection){if(value.dateFrom>value.dateTo){this.getFromDateField().markInvalid(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
this.getToDateField().markInvalid(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
return false
}else{if(value.dateFrom==value.dateTo&&value.timeFrom>value.timeTo){this.getFromTimeField().markInvalid(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
this.getToTimeField().markInvalid(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
return false
}}}else{if(this.timeSelection){if(value.timeFrom>value.timeTo){this.getFromTimeField().markInvalid(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
this.getToTimeField().markInvalid(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
return false
}}}this.getFromDateField().clearInvalid();
this.getToDateField().clearInvalid();
this.getFromTimeField().clearInvalid();
this.getToTimeField().clearInvalid();
return true
}else{return false
}}});Ext.ns("Openwis.Common.Components");
Openwis.Common.Components.GeographicalExtentSelection=Ext.extend(Ext.form.Field,{defaultAutoCreate:{tag:"div"},geoWKTSelection:null,wmsUrl:null,layerName:null,geoExtentType:null,scrollRef:null,maxResolution:null,readOnly:false,width:350,height:200,initComponent:function(){Ext.apply(this,{border:false,allowBlank:false,listeners:{beforedestroy:function(){if(this.scrollRef){Ext.EventManager.un(this.scrollRef,"scroll",this.updateMapSize,this)
}},afterrender:function(){this.scrollRef=this.scrollRef||window.document.body;
Ext.EventManager.on(this.scrollRef,"scroll",this.updateMapSize,this)
},scope:this}});
Openwis.Common.Components.GeographicalExtentSelection.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged")
},onRender:function(ct,position){Openwis.Common.Components.GeographicalExtentSelection.superclass.onRender.call(this,ct,position);
this.assignMap()
},assignMap:function(){if(!pageLoaded){var task=new Ext.util.DelayedTask(this.assignMap,this);
task.delay(200)
}else{this.getMapPanel().doLayout();
this.mapLoaded=true
}},getRawValue:function(){return this.feature
},getValue:function(){return this.getRawValue()
},buildValue:function(){return this.getValue()
},validateValue:function(value){if(value==null){if(this.allowBlank){this.clearInvalid();
return true
}else{this.markInvalid(Openwis.i18n("Common.Extent.Geo.Mandatory"));
return false
}}return true
},reset:function(){this.getVector().destroyFeatures();
this.feature=null;
this.getMap().zoomToMaxExtent()
},getMapPanel:function(){if(!this.mapPanel){this.mapPanel=new GeoExt.MapPanel({renderTo:this.el,height:this.height,width:this.width,map:this.getMap(),tbar:this.getMapToolbar(),tbarCssClass:"mapCtrlToolbar"})
}return this.mapPanel
},getMap:function(){if(!this.map){this.mapLoaded=false;
var mapOptions={controls:[]};
if(this.maxExtent!=null){mapOptions.maxExtent=this.maxExtent
}if(this.maxResolution){mapOptions.maxResolution=this.maxResolution
}this.map=new OpenLayers.Map(mapOptions);
var wms=new OpenLayers.Layer.WMS("Background layer",this.wmsUrl,{layers:this.layerName,format:"image/png"},{isBaseLayer:true});
this.map.addLayers([wms,this.getVector()]);
if(this.geoWKTSelection!=null){var ft=Openwis.Utils.Geo.WKTtoFeature(this.geoWKTSelection);
this.getVector().addFeatures([ft]);
this.feature=this.geoWKTSelection
}}return this.map
},getVector:function(){if(!this.vector){this.vector=new OpenLayers.Layer.Vector("Vector layer");
this.vector.events.on({sketchstarted:function(){this.vector.destroyFeatures()
},scope:this})
}return this.vector
},getMapToolbar:function(){if(!this.mapToolbar){var action={};
this.mapToolbar=[];
action=new GeoExt.Action({control:new OpenLayers.Control.ZoomToMaxExtent(),map:this.getMap(),iconCls:"mapCtrlZoomFull",tooltip:{title:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomFull.Title"),text:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomFull.Text")}});
this.mapToolbar.push(action);
this.mapToolbar.push("-");
action=new GeoExt.Action({control:new OpenLayers.Control.ZoomBox(),map:this.getMap(),toggleGroup:"move",allowDepress:false,iconCls:"mapCtrlZoomIn",tooltip:{title:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomIn.Title"),text:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomIn.Text")}});
this.mapToolbar.push(action);
action=new GeoExt.Action({control:new OpenLayers.Control.ZoomBox({displayClass:"ZoomOut",out:true}),map:this.getMap(),toggleGroup:"move",allowDepress:false,iconCls:"mapCtrlZoomOut",tooltip:{title:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomOut.Title"),text:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.ZoomOut.Text")}});
this.mapToolbar.push(action);
this.mapToolbar.push("-");
action=new GeoExt.Action({control:new OpenLayers.Control.DragPan({isDefault:true}),toggleGroup:"move",allowDepress:false,pressed:true,map:this.getMap(),iconCls:"mapCtrlDrag",tooltip:{title:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.DragPan.Title"),text:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.DragPan.Text")}});
this.mapToolbar.push(action);
if(!this.readOnly){this.mapToolbar.push("-");
action=new GeoExt.Action({control:new OpenLayers.Control.DrawFeature(this.getVector(),OpenLayers.Handler.RegularPolygon,{handlerOptions:{irregular:true,sides:4},featureAdded:function(feature){this.scope.feature=Openwis.Utils.Geo.featureToWKT(feature);
this.scope.fireEvent("valueChanged",feature.geometry.getBounds())
},scope:this}),map:this.getMap(),toggleGroup:"move",allowDepress:false,iconCls:"mapCtrlDrawRectangleExtent",tooltip:Openwis.i18n("HomePage.Search.Criteria.Where.Map.Ctrl.DrawExtent.Tooltip"),group:"draw"});
this.mapToolbar.push(action);
if(this.geoExtentType==="POLYGON"){action=new GeoExt.Action({control:new OpenLayers.Control.DrawFeature(this.getVector(),OpenLayers.Handler.Polygon,{featureAdded:function(feature){this.scope.feature=Openwis.Utils.Geo.featureToWKT(feature);
this.scope.fireEvent("valueChanged",feature.geometry.getBounds())
},scope:this}),map:this.getMap(),toggleGroup:"move",allowDepress:false,iconCls:"mapCtrlDrawPolygonExtent",tooltip:"Draw polygon",group:"draw"});
this.mapToolbar.push(action)
}}}return this.mapToolbar
},updateMapSize:function(){this.getMap().updateSize()
},drawExtent:function(extent){this.getVector().destroyFeatures();
if(extent.left>extent.right){var bounds1=new OpenLayers.Bounds(extent.left,extent.bottom,180,extent.top);
var geo1=bounds1.toGeometry();
var polFeature1=new OpenLayers.Feature.Vector(geo1);
var bounds2=new OpenLayers.Bounds(-180,extent.bottom,extent.right,extent.top);
var geo2=bounds2.toGeometry();
var polFeature2=new OpenLayers.Feature.Vector(geo2);
this.getVector().addFeatures(polFeature1);
this.getVector().addFeatures(polFeature2);
var geo=new OpenLayers.Geometry.MultiPolygon([geo1,geo2]);
var polFeature=new OpenLayers.Feature.Vector(geo);
this.feature=Openwis.Utils.Geo.featureToWKT(polFeature)
}else{var bounds=new OpenLayers.Bounds(extent.left,extent.bottom,extent.right,extent.top);
var polFeature=new OpenLayers.Feature.Vector(bounds.toGeometry());
this.getVector().addFeatures([polFeature]);
this.feature=Openwis.Utils.Geo.featureToWKT(polFeature)
}this.getVector().refresh()
},zoomToExtent:function(extent){if(extent.left>extent.right){this.getMap().zoomToMaxExtent()
}else{var bounds=new OpenLayers.Bounds(extent.left,extent.bottom,extent.right,extent.top);
this.getMap().zoomToExtent(bounds)
}}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.Helper");
Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher=function(){this.matches=function(availableFor,type,values){if(Ext.isEmpty(availableFor)){return true
}if(type=="SingleSelection"||type=="MultipleSelection"){return this.simpleMatch(availableFor,values)
}else{if(type=="DayPeriodSelection"){return this.periodMatch(availableFor,values)
}}return true
};
this.simpleMatch=function(availableFor,values){return Openwis.Utils.Array.containsAny(availableFor,values)
};
this.periodMatch=function(availableFor,values){for(var i=0;
i<availableFor.length;
i++){var period=availableFor[i].split("/");
for(var j=0;
j<values.length;
j++){if(values[j]>=period[0]&&values[j]<=period[1]){return true
}}}return false
}
};Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.Helper");
Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel=Ext.extend(Ext.form.DisplayField,{initComponent:function(){Ext.apply(this,{value:"TOTOOOOO",fieldLabel:this.config.parameter.label});
Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel.superclass.initComponent.apply(this,arguments);
this.initialize()
},valueDefined:true,initialize:function(){this.addEvents("valueChanged");
var matchingElements=this.getMatchingSelection(this.config.previousElementSelection)
},isValueDefined:function(){return this.valueDefined
},getSelectedValue:function(){return[]
},validate:function(){return{ok:true}
},getMatchingSelection:function(values){return values
},buildSSP:function(){return{code:this.config.parameter.code,values:["mockValue01","mockValue02"]}
},getTestItems:function(){var testItems=[];
for(var i=0;
i<2;
i++){testItems.push(new Ext.Panel({html:"Pan"+i,border:false}))
}return testItems
},getSetUnsetDefinedButton:function(){if(!this.setUnsetDefinedButton){this.setUnsetDefinedButton=new Ext.Button({text:"Set/Unset for "+this.config.parameter.code+" "+new Date().getTime(),handler:function(){this.valueDefined=!this.valueDefined;
this.fireEvent("valueChanged",this.config.parameter.code)
},scope:this})
}return this.setUnsetDefinedButton
},getFireChangedEventButton:function(){if(!this.fireChangedEventButton){this.fireChangedEventButton=new Ext.Button({text:"Changed for "+this.config.parameter.code+" "+new Date().getTime(),handler:function(){this.fireEvent("valueChanged",this.config.parameter.code)
},scope:this})
}return this.fireChangedEventButton
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.Helper");
Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPCatalog=function(){this.setSubSelectionParameters=function(subSelectionParameters){this.subSelectionParameters={};
Ext.each(subSelectionParameters,function(item){this.subSelectionParameters[item.code]=item
},this)
};
this.getParameterByCode=function(code){return this.subSelectionParameters[code]
};
this.registerComponent=function(code,cmp,idx){var index=this.indexOf(code);
var struct={code:code,component:cmp,indexInForm:idx};
if(index==-1){this.getComponentsCatalog().push(struct)
}else{this.getComponentsCatalog()[index]=struct
}};
this.getComponent=function(code){var itemToFind=null;
Ext.each(this.getComponentsCatalog(),function(item,index,allItems){if(item.code==code){itemToFind=item
}},this);
return itemToFind
};
this.getNextParameterCode=function(code){var indexToFind=this.indexOf(code)+1;
if(indexToFind>=0&&indexToFind<this.getComponentsCatalog().length){return this.getComponentsCatalog()[indexToFind].code
}return null
};
this.indexOf=function(code){var indexOf=-1;
Ext.iterate(this.getComponentsCatalog(),function(item,index,allItems){if(item.code==code){indexOf=index
}},this);
return indexOf
};
this.getComponentsCatalog=function(){if(!this.componentsCatalog){this.componentsCatalog=[]
}return this.componentsCatalog
};
this.getScheduleCode=function(){return"CODE.SCHEDULE"
}
};Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.Helper");
Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPToExt=function(){this.createComponent=function(config){var panel=null;
var panelConfig={config:config,fieldLabel:config.parameter.label};
if(config.parameter.selectionType=="SingleSelection"){if(config.parameter.type=="RADIO"){panel=new Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.Radio(panelConfig)
}else{if(config.parameter.type=="DROPDOWNLIST"){panel=new Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ComboBox(panelConfig)
}else{if(config.parameter.type=="LISTBOX"){panel=new Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ListBox(panelConfig)
}}}}else{if(config.parameter.selectionType=="MultipleSelection"){if(config.parameter.type=="CHECKBOX"){panel=new Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.Checkbox(panelConfig)
}else{if(config.parameter.type=="LISTBOX"){panel=new Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.ListBox(panelConfig)
}}}else{if(config.parameter.selectionType=="ScheduleSelection"){panel=new Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule(panelConfig)
}else{if(config.parameter.selectionType=="GeographicalAreaSelection"){panel=new Openwis.Common.Components.GeographicalExtentSelection({fieldLabel:config.parameter.label,maxExtent:Openwis.Utils.Geo.getBoundsFromWKT(config.parameter.geoWKTMaxExtent),geoWKTSelection:this.toSingletonValue(config.editValue)||config.parameter.geoWKTSelection,wmsUrl:config.parameter.geoConfig.wmsUrl,layerName:config.parameter.geoConfig.layerName,geoExtentType:config.parameter.geoExtentType,maxResolution:"auto"})
}else{if(config.parameter.selectionType=="SourceSelection"){panel=Openwis.RequestSubscription.SubSelectionParameters.SourceSelection.Source(panelConfig)
}else{if(config.parameter.selectionType=="DayPeriodSelection"){panel=new Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day(panelConfig)
}else{if(config.parameter.selectionType=="DatePeriodSelection"){var defaultDateFrom=config.parameter.from;
var defaultDateTo=config.parameter.to;
if(config.editValue){var period=config.editValue.split("/");
defaultDateFrom=period[0];
defaultDateTo=period[1]
}panel=new Openwis.Common.Components.DateTimeExtentSelection({fieldLabel:config.parameter.label,dateSelection:true,defaultDateFrom:defaultDateFrom,defaultDateTo:defaultDateTo,dateMin:config.parameter.periodMinExtent,dateMax:config.parameter.periodMaxExtent,excludedDates:config.parameter.excludedDates,timeSelection:false,processValue:function(value){if(value.dateFrom&&value.dateTo){return Openwis.Utils.Date.formatDateInterval(value.dateFrom,value.dateTo)
}else{return null
}}})
}else{if(config.parameter.selectionType=="TimePeriodSelection"){var defaultTimeFrom=config.parameter.from;
var defaultTimeTo=config.parameter.to;
if(config.editValue){var period=config.editValue.split("/");
defaultTimeFrom=period[0].replace(/Z/ig,"");
defaultTimeTo=period[1].replace(/Z/ig,"")
}panel=new Openwis.Common.Components.DateTimeExtentSelection({fieldLabel:config.parameter.label,dateSelection:false,timeSelection:true,defaultTimeFrom:defaultTimeFrom,defaultTimeTo:defaultTimeTo,timeMin:config.parameter.periodMinExtent,timeMax:config.parameter.periodMaxExtent,processValue:function(value){if(value.timeFrom&&value.timeTo){return Openwis.Utils.Date.formatTimeInterval(value.timeFrom,value.timeTo)
}else{return null
}}})
}}}}}}}}if(!panel){panel=new Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel(panelConfig)
}return panel
};
this.createReadOnlyComponent=function(config){var labelValue="N/A";
if(config.parameter.selectionType=="SingleSelection"){for(var i=0;
i<config.parameter.values.length;
i++){var value=config.parameter.values[i];
if(value.code==this.toSingletonValue(config.editValue)){labelValue=value.value;
break
}}}else{if(config.parameter.selectionType=="MultipleSelection"){var selectedValues=config.editValue;
labelValue="";
for(var i=0;
i<config.parameter.values.length;
i++){var value=config.parameter.values[i];
if(selectedValues.indexOf(value.code)>-1){labelValue+=value.value+" "
}}}else{if(config.parameter.selectionType=="GeographicalAreaSelection"){return new Openwis.Common.Components.GeographicalExtentSelection({fieldLabel:config.parameter.label,maxExtent:Openwis.Utils.Geo.getBoundsFromWKT(config.parameter.geoWKTMaxExtent),geoWKTSelection:this.toSingletonValue(config.editValue),wmsUrl:config.parameter.geoConfig.wmsUrl,layerName:config.parameter.geoConfig.layerName,geoExtentType:config.parameter.geoExtentType,maxResolution:"auto",readOnly:true})
}else{if(config.parameter.selectionType=="SourceSelection"){labelValue=this.toSingletonValue(config.editValue)
}else{if(config.parameter.selectionType=="DayPeriodSelection"){labelValue=this.toSingletonValue(config.editValue)
}else{if(config.parameter.selectionType=="DatePeriodSelection"){var period=this.toSingletonValue(config.editValue).split("/");
labelValue=Openwis.i18n("Common.Extent.Temporal.From.To",{from:period[0],to:period[1]})
}else{if(config.parameter.selectionType=="TimePeriodSelection"){var period=this.toSingletonValue(config.editValue).split("/");
labelValue=Openwis.i18n("Common.Extent.Temporal.From.To",{from:period[0],to:period[1]})
}}}}}}}return new Ext.form.DisplayField({fieldLabel:config.parameter.label,value:labelValue,buildValue:function(){return labelValue
}})
};
this.toSingletonValue=function(value){if((value!=null)&&(value.constructor===Array)){return value[0]
}else{return value
}};
this.createEmptyComponent=function(label){return new Ext.form.DisplayField({fieldLabel:label,value:" ",buildValue:function(){return null
}})
};
this.createMock=function(config){var panel=null;
var panelConfig={config:config};
panel=new Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel(panelConfig);
return panel
};
this.createLabel=function(label){return new Ext.Container({html:label+":",width:100,border:false,cellCls:"SSPCell",style:{margin:"10px"}})
};
this.createSSPContainer=function(){return new Ext.Container({width:442,border:false,cellCls:"SSPCell",style:{margin:"10px"}})
};
this.createNoSSPAvailableContainer=function(){return new Ext.Container({width:542,colspan:2,border:false,cellCls:"SSPCell",style:{margin:"10px"},html:Openwis.i18n("RequestSubscription.SSP.Unavailable")})
}
};Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection");
Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule=Ext.extend(Ext.form.Field,{defaultAutoCreate:{tag:"div"},initComponent:function(){Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule.superclass.initComponent.apply(this,arguments);
if(!this.frequency){this.frequency=(this.config?this.config.editValue:null)||{type:"ON_PRODUCT_ARRIVAL"}
}},onRender:function(ct,position){Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule.superclass.onRender.call(this,ct,position);
this.getSchedulePanel().add(this.getScheduleModeRadioGroup());
this.getStartingDateFieldSet().add(this.getStartingDateCompositeField());
this.getSchedulePanel().add(this.getStartingDateFieldSet());
this.getRecurrentProcessingFieldSet().add(this.getRecurrentProcessingCompositeField());
this.getSchedulePanel().add(this.getRecurrentProcessingFieldSet());
this.getSchedulePanel().doLayout()
},getRecurrentProcessingFieldSet:function(){if(!this.recurrentProcessingFieldSet){this.recurrentProcessingFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod"),collapsed:false,collapsible:false,border:false,width:470})
}return this.recurrentProcessingFieldSet
},getStartingDateFieldSet:function(){if(!this.startingDateFieldSet){this.startingDateFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.StartingAt"),collapsed:false,collapsible:false,border:false,width:470})
}return this.startingDateFieldSet
},getRawValue:function(){var obj={};
obj.type=this.getScheduleModeRadioGroup().getValue().inputValue;
if(obj.type=="RECURRENT_PROCESSING"){obj.recurrentScale=this.getRecurrentProcessingFrequencyCombobox().getValue();
obj.recurrencePeriod=this.getRecurrentProcessingNumberField().getValue();
obj.startingDate=this.getStartingDateField().getValue().format("Y-m-d")+"T"+this.getStartingDateTimeField().getValue()+":00Z"
}return obj
},getValue:function(){return this.getRawValue()
},buildValue:function(){return this.getValue()
},validateValue:function(value){if(value.type=="RECURRENT_PROCESSING"){if(Ext.num(value.recurrencePeriod,-1)<0){this.getRecurrentProcessingNumberField().markInvalid();
return false
}}return true
},reset:function(){this.getScheduleModeRadioGroup().reset()
},getSchedulePanel:function(){if(!this.schedulePanel){this.schedulePanel=new Ext.Container({renderTo:this.el})
}return this.schedulePanel
},getScheduleModeRadioGroup:function(){if(!this.scheduleModeRadioGroup){this.scheduleModeRadioGroup=new Ext.form.RadioGroup({hideLabel:true,columns:1,items:[{boxLabel:Openwis.i18n("RequestSubscription.SSP.Schedule.OnProductArrival"),inputValue:"ON_PRODUCT_ARRIVAL",name:"scheduleMode",checked:this.frequency.type=="ON_PRODUCT_ARRIVAL"},{boxLabel:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentProcessing"),inputValue:"RECURRENT_PROCESSING",name:"scheduleMode",checked:this.frequency.type=="RECURRENT_PROCESSING"}],listeners:{change:function(radioGroup,radio){if(radio.inputValue=="RECURRENT_PROCESSING"){this.getRecurrentProcessingCompositeField().enable();
this.getStartingDateCompositeField().enable()
}else{this.getRecurrentProcessingCompositeField().disable();
this.getStartingDateCompositeField().disable()
}},scope:this}})
}return this.scheduleModeRadioGroup
},getRecurrentProcessingCompositeField:function(){if(!this.recurrentProcessingCompositeField){this.recurrentProcessingCompositeField=new Ext.form.CompositeField({disabled:(this.frequency.type!="RECURRENT_PROCESSING"),items:[{xtype:"container",style:{paddingLeft:"20px"}},new Ext.form.Label({html:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Label")}),this.getRecurrentProcessingNumberField(),this.getRecurrentProcessingFrequencyCombobox()]})
}return this.recurrentProcessingCompositeField
},getRecurrentProcessingNumberField:function(){if(!this.recurrentProcessingNumberField){this.recurrentProcessingNumberField=new Ext.form.NumberField({name:"frequencyNumber",width:40,allowDecimals:false,allowNegative:false,minValue:1,value:(this.frequency.type=="RECURRENT_PROCESSING")?this.frequency.recurrencePeriod:""})
}return this.recurrentProcessingNumberField
},getRecurrentProcessingFrequencyCombobox:function(){if(!this.recurrentProcessingFrequencyCombobox){this.recurrentProcessingFrequencyCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["DAY",Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Day")],["HOUR",Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Hour")]]}),valueField:"id",displayField:"value",value:(this.frequency.type=="RECURRENT_PROCESSING")?this.frequency.recurrentScale:"HOUR",name:"frequencyComboBox",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:80})
}return this.recurrentProcessingFrequencyCombobox
},getStartingDateCompositeField:function(){if(!this.startingDateCompositeField){this.startingDateCompositeField=new Ext.form.CompositeField({disabled:(this.frequency.type!="RECURRENT_PROCESSING"),items:[{xtype:"container",style:{paddingLeft:"20px"}},this.getStartingDateField(),this.getStartingDateTimeField()]})
}return this.startingDateCompositeField
},getStartingDateField:function(){if(!this.startingDateField){this.startingDateField=new Ext.form.DateField({name:"startingDate",editable:false,format:"Y-m-d",value:(this.frequency.type=="RECURRENT_PROCESSING")?Openwis.Utils.Date.ISODateToCalendar(this.frequency.startingDate):new Date()})
}return this.startingDateField
},getStartingDateTimeField:function(){if(!this.startingDateTimeField){this.startingDateTimeField=new Ext.form.TimeField({name:"startingDateTimeField",increment:15,format:"H:i",value:(this.frequency.type=="RECURRENT_PROCESSING")?Openwis.Utils.Date.ISODateToTime(this.frequency.startingDate):"00:00",width:60})
}return this.startingDateTimeField
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection");
Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day=Ext.extend(Ext.form.DateField,{initComponent:function(){Ext.apply(this,{allowBlank:false,width:150,format:"m/d/Y",listeners:{select:function(){this.fireEvent("valueChanged")
}}});
Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged");
this.initialize()
},initialize:function(){if(this.config.editValue){this.setValue(this.config.editValue.toString())
}else{if(this.config.parameter.date){this.setValue(this.config.parameter.date)
}}if(this.config.parameter.periodMinExtent){this.setMinValue(this.config.parameter.periodMinExtent)
}if(this.config.parameter.periodMaxExtent){this.setMaxValue(this.config.parameter.periodMaxExtent)
}if(this.config.parameter.excludedDates&&!Ext.isEmpty(this.config.parameter.excludedDates)){this.setDisabledDates(this.config.parameter.excludedDates)
}},buildValue:function(){var tmp=Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day.superclass.getValue.call(this);
if(tmp!=""){return Openwis.Utils.Date.formatDateForServer(tmp)
}else{return null
}}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.Cache");
Openwis.RequestSubscription.SubSelectionParameters.Cache.Period=Ext.extend(Ext.form.Field,{defaultAutoCreate:{tag:"div"},initComponent:function(){Openwis.RequestSubscription.SubSelectionParameters.Cache.Period.superclass.initComponent.apply(this,arguments);
this.initEditConfig()
},onRender:function(ct,position){Openwis.RequestSubscription.SubSelectionParameters.Cache.Period.superclass.onRender.call(this,ct,position);
this.getTimeIntervalFieldSet().add(this.getTimeIntervalCheckboxGroup());
this.getTimeIntervalFieldSet().add(new Ext.Button(this.getSelectAllAction()));
this.getCachePeriodPanel().add(this.getTimeIntervalFieldSet());
this.getTimePeriodFieldSet().add(this.getTimeExtentSelection());
this.getCachePeriodPanel().add(this.getTimePeriodFieldSet());
this.getCachePeriodPanel().doLayout()
},getSelectAllAction:function(){if(!this.selectAction){this.selectAction=new Ext.Action({text:"Select All",scope:this,handler:function(){if(!this.selected||this.selected==false){this.selected=true;
this.selectedText="Unselect All"
}else{this.selected=false;
this.selectedText="Select All"
}for(var i=0;
i<this.getTimeIntervalCheckboxGroup().items.length;
i++){this.getTimeIntervalCheckboxGroup().items.get(i).setValue(this.selected)
}this.getSelectAllAction().setText(this.selectedText)
}})
}return this.selectAction
},getCachePeriodPanel:function(){if(!this.cachePeriodPanel){this.cachePeriodPanel=new Ext.Container({renderTo:this.el})
}return this.cachePeriodPanel
},getTimeIntervalFieldSet:function(){if(!this.timeIntervalFieldSet){this.timeIntervalFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("RequestSubscription.SSP.Cache.TimeInterval"),collapsed:this.editConfig.type!="INTERVALS",collapsible:true,width:470,listeners:{beforeexpand:function(panel,animate){this.getTimePeriodFieldSet().collapse(false)
},scope:this}})
}return this.timeIntervalFieldSet
},getTimeIntervalCheckboxGroup:function(){if(!this.timeIntervalCheckboxGroup){var timeIntervalCheckboxes=[];
for(var i=0;
i<24;
i++){var timeIntervalId="";
timeIntervalId+=String.leftPad(i,2,"0")+":00Z/";
timeIntervalId+=String.leftPad(i,2,"0")+":59Z";
timeIntervalCheckboxes.push({boxLabel:"["+i+","+(i+1)+"]",name:"timeInterval",id:timeIntervalId,checked:(this.editConfig.intervals.indexOf(timeIntervalId)!=-1)})
}this.timeIntervalCheckboxGroup=new Ext.form.CheckboxGroup({hideLabel:true,columns:4,items:timeIntervalCheckboxes,allowBlank:false})
}return this.timeIntervalCheckboxGroup
},getTimePeriodFieldSet:function(){if(!this.timePeriodFieldSet){this.timePeriodFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("RequestSubscription.SSP.Cache.TimePeriod"),collapsed:this.editConfig.type!="PERIOD",collapsible:true,width:470,listeners:{beforeexpand:function(panel,animate){this.getTimeIntervalFieldSet().collapse(false)
},scope:this}})
}return this.timePeriodFieldSet
},getTimeExtentSelection:function(){if(!this.timeExtentSelection){this.timeExtentSelection=new Openwis.Common.Components.DateTimeExtentSelection({hideLabel:true,dateSelection:false,timeSelection:true,defaultTimeFrom:this.editConfig.defaultTimeFrom,defaultTimeTo:this.editConfig.defaultTimeTo,processValue:function(value){if(value.timeFrom&&value.timeTo){var tf=value.timeFrom;
var tt=value.timeTo;
return Openwis.Utils.Date.formatTimeInterval(tf,tt)
}else{return null
}}})
}return this.timeExtentSelection
},initEditConfig:function(){this.editConfig={};
this.editConfig.type="INTERVALS";
this.editConfig.defaultTimeFrom=null;
this.editConfig.defaultTimeTo=null;
this.editConfig.intervals=[];
if(this.ssp){var intervals=this.ssp.value;
if(intervals.length==1){var period=intervals[0].split("/");
period[0]=period[0].replace(/Z/gi,"");
period[1]=period[1].replace(/Z/gi,"");
this.editConfig.type="PERIOD";
this.editConfig.defaultTimeFrom=period[0];
this.editConfig.defaultTimeTo=period[1]
}else{this.editConfig.type="INTERVALS";
this.editConfig.intervals=intervals
}}},getRawValue:function(){if(!this.getTimeIntervalFieldSet().collapsed){var out=[];
this.getTimeIntervalCheckboxGroup().eachItem(function(item){if(item.checked){out.push(item.id)
}});
return out
}else{if(!this.getTimePeriodFieldSet().collapsed){return this.getTimeExtentSelection().getValue()
}}},getValue:function(){return this.getRawValue()
},buildValue:function(){return this.getValue()
},validateValue:function(){if(!this.getTimeIntervalFieldSet().collapsed){return this.getTimeIntervalCheckboxGroup().validate()
}else{if(!this.getTimePeriodFieldSet().collapsed){return this.getTimeExtentSelection().validateValue()
}}},reset:function(){this.getTimeIntervalFieldSet().reset();
this.getTimeIntervalCheckboxGroup().reset();
this.getTimePeriodFieldSet().reset();
this.getTimeExtentSelection().reset()
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.Cache");
Openwis.RequestSubscription.SubSelectionParameters.Cache.File=Ext.extend(Ext.form.Field,{defaultAutoCreate:{tag:"div"},onRender:function(ct,position){Openwis.RequestSubscription.SubSelectionParameters.Cache.File.superclass.onRender.call(this,ct,position);
this.getCacheFilePanel().add(this.getHeader());
this.getCacheFilePanel().add(this.getTimeExtentSelection());
this.getCacheFilePanel().add(this.getCacheFileGrid());
this.getCacheFilePanel().doLayout();
if(this.getTimeExtentSelection().validate()){var interval=this.getTimeExtentSelection().getValue();
this.getCacheFileStore().setBaseParam("startDate",interval.startDate);
this.getCacheFileStore().setBaseParam("endDate",interval.endDate);
this.getCacheFileStore().load()
}},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("RequestSubscription.SSP.Cache.File.Title")+"<br><br><br>"})
}return this.header
},getCacheFilePanel:function(){if(!this.cacheFilePanel){this.cacheFilePanel=new Ext.Container({renderTo:this.el})
}return this.cacheFilePanel
},getTimeExtentSelection:function(){if(!this.timeExtentSelection){var dateFrom=new Date().add(Date.HOUR,Openwis.Conf.REQUEST_CACHE_HOUR);
var dateTo=new Date();
this.timeExtentSelection=new Openwis.Common.Components.DateTimeExtentSelection({hideLabel:true,dateSelection:true,defaultDateFrom:Openwis.Utils.Date.dateToISOUTC(dateFrom),defaultDateTo:Openwis.Utils.Date.dateToISOUTC(dateTo),timeSelection:true,defaultTimeFrom:Openwis.Utils.Date.timeToUTC(dateFrom),defaultTimeTo:Openwis.Utils.Date.timeToUTC(dateTo),timeEditable:true,processValue:function(value){var interval={};
if(value.dateFrom&&value.dateTo){interval.startDate=value.dateFrom.format("Y-m-d")+"T"+value.timeFrom+":00Z";
interval.endDate=value.dateTo.format("Y-m-d")+"T"+value.timeTo+":00Z"
}return interval
},listeners:{valueChanged:function(){if(this.getTimeExtentSelection().validate()){var interval=this.getTimeExtentSelection().getValue();
this.getCacheFileStore().setBaseParam("startDate",interval.startDate);
this.getCacheFileStore().setBaseParam("endDate",interval.endDate);
this.getCacheFileStore().load()
}},scope:this}})
}return this.timeExtentSelection
},getCacheFileGrid:function(){if(!this.cacheFileGrid){this.cacheFileGrid=new Ext.grid.GridPanel({id:"cacheFileGrid",height:300,width:550,border:true,store:this.getCacheFileStore(),loadMask:true,columns:[{id:"filename",header:Openwis.i18n("RequestSubscription.SSP.Cache.File.Name"),dataIndex:"filename",width:300,sortable:true},{id:"checksum",header:Openwis.i18n("RequestSubscription.SSP.Cache.File.Checksum"),dataIndex:"checksum",width:250,sortable:true}],autoExpandColumn:"filename",style:{marginTop:"10px"}})
}return this.cacheFileGrid
},getCacheFileStore:function(){if(!this.cacheFileStore){this.cacheFileStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.cache.subselectionparameters",fields:[{name:"id"},{name:"checksum"},{name:"filename"},{name:"insertionDate"}]});
this.cacheFileStore.setBaseParam("urn",this.productMetadataUrn)
}return this.cacheFileStore
},getRawValue:function(){var selections=this.getCacheFileGrid().getSelectionModel().getSelections();
var files=[];
Ext.each(selections,function(record){files.push(record.get("id"))
},this);
return files
},getValue:function(){return this.getRawValue()
},buildValue:function(){return this.getValue()
},validateValue:function(value){return !Ext.isEmpty(value)
},reset:function(){this.getTimeExtentSelection().reset()
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection");
Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.Checkbox=Ext.extend(Ext.form.CheckboxGroup,{initComponent:function(){Ext.apply(this,{allowBlank:false,columns:2,items:this.getItems(),listeners:{change:function(){this.fireEvent("valueChanged")
}}});
Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.Checkbox.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged")
},getItems:function(){var matcher=new Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher();
var matchingValues=[];
if(this.config.previous){Ext.each(this.config.parameter.values,function(item,index,allItems){if(matcher.matches(item.availableFor,this.config.previous.type,this.config.previous.selection)){matchingValues.push(item)
}},this)
}else{matchingValues=this.config.parameter.values
}var overridenSelection={};
if(this.config.editValue||this.config.currentElementSelection){var elementsToKeepSelected=[];
if(this.config.editValue){Ext.each(this.config.editValue,function(item,index,allItems){elementsToKeepSelected.push(item)
},this)
}else{if(this.config.currentElementSelection){Ext.each(matchingValues,function(item,index,allItems){if(this.config.currentElementSelection.indexOf(item.code)!=-1){elementsToKeepSelected.push(item.code)
}},this)
}}if(!Ext.isEmpty(elementsToKeepSelected)){Ext.each(matchingValues,function(item,index,allItems){overridenSelection[item.code]=(elementsToKeepSelected.indexOf(item.code)!=-1)
},this)
}}var items=[];
Ext.each(matchingValues,function(item,index,allItems){var selected=overridenSelection[item.code]!=null?overridenSelection[item.code]:item.selected;
items.push({boxLabel:item.value,name:this.config.parameter.code,id:item.code,checked:selected,height:25,width:200})
},this);
return items
},buildValue:function(){var out=[];
this.eachItem(function(item){if(item.checked){out.push(item.id)
}});
return out
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection");
Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.ListBox=Ext.extend(Ext.ux.form.MultiSelect,{initComponent:function(){Ext.apply(this,{store:this.getSSPStore(),displayField:"value",valueField:"code",width:300,height:150,style:{overflow:"visible"},listeners:{change:function(){this.fireEvent("valueChanged")
},afterrender:function(){var items=this.getItems();
this.getSSPStore().loadData(items);
var selectedItemCodes=[];
Ext.each(items,function(item,index,allItems){if(item.selected){selectedItemCodes.push(item.code)
}},this);
this.setValue(selectedItemCodes.join())
},scope:this}});
Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.ListBox.superclass.initComponent.apply(this,arguments)
},getSSPStore:function(){if(!this.sspStore){this.sspStore=new Ext.data.JsonStore({idProperty:"code",fields:[{name:"code"},{name:"value"}]})
}return this.sspStore
},getItems:function(){var matcher=new Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher();
var matchingValues=[];
if(this.config.previous){Ext.each(this.config.parameter.values,function(item,index,allItems){if(matcher.matches(item.availableFor,this.config.previous.type,this.config.previous.selection)){matchingValues.push(item)
}},this)
}else{matchingValues=this.config.parameter.values
}var overridenSelection={};
if(this.config.editValue||this.config.currentElementSelection){var elementsToKeepSelected=[];
if(this.config.editValue){Ext.each(this.config.editValue,function(item,index,allItems){elementsToKeepSelected.push(item)
},this)
}else{if(this.config.currentElementSelection){Ext.each(matchingValues,function(item,index,allItems){if(this.config.currentElementSelection.indexOf(item.code)!=-1){elementsToKeepSelected.push(item.code)
}},this)
}}if(!Ext.isEmpty(elementsToKeepSelected)){Ext.each(matchingValues,function(item,index,allItems){overridenSelection[item.code]=(elementsToKeepSelected.indexOf(item.code)!=-1)
},this)
}}var items=[];
Ext.each(matchingValues,function(item,index,allItems){var selected=overridenSelection[item.code]!=null?overridenSelection[item.code]:item.selected;
items.push({code:item.code,value:item.value,selected:selected})
},this);
return items
},buildValue:function(){return this.getRawValue()
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.SingleSelection");
Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.Radio=Ext.extend(Ext.form.RadioGroup,{initComponent:function(){Ext.apply(this,{allowBlank:false,columns:2,items:this.getItems(),listeners:{change:function(){this.fireEvent("valueChanged")
}}});
Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.Radio.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged")
},getItems:function(){var matcher=new Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher();
var matchingValues=[];
if(this.config.previous){Ext.each(this.config.parameter.values,function(item,index,allItems){if(matcher.matches(item.availableFor,this.config.previous.type,this.config.previous.selection)){matchingValues.push(item)
}},this)
}else{matchingValues=this.config.parameter.values
}var overridenSelection={};
if(this.config.editValue||this.config.currentElementSelection){var elementsToKeepSelected=[];
if(this.config.editValue){elementsToKeepSelected.push(this.config.editValue[0])
}else{if(this.config.currentElementSelection){Ext.each(matchingValues,function(item,index,allItems){if(this.config.currentElementSelection.indexOf(item.code)!=-1){elementsToKeepSelected.push(item.code)
}},this)
}}if(!Ext.isEmpty(elementsToKeepSelected)){Ext.each(matchingValues,function(item,index,allItems){overridenSelection[item.code]=(elementsToKeepSelected.indexOf(item.code)!=-1)
},this)
}}var items=[];
Ext.each(matchingValues,function(item,index,allItems){var selected=overridenSelection[item.code]!=null?overridenSelection[item.code]:item.selected;
items.push({boxLabel:item.value,name:this.config.parameter.code,id:item.code,checked:selected,height:25,width:200})
},this);
return items
},buildValue:function(){var out=null;
this.eachItem(function(item){if(item.checked){out=item.id;
return false
}});
return out
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.SingleSelection");
Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ComboBox=Ext.extend(Ext.form.ComboBox,{initComponent:function(){Ext.apply(this,{store:this.getSSPStore(),valueField:"code",displayField:"value",typeAhead:true,mode:"local",triggerAction:"all",selectOnFocus:true,allowBlank:false,editable:false,width:200,listeners:{select:function(){this.fireEvent("valueChanged")
}}});
Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ComboBox.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged");
var items=this.getItems();
this.getSSPStore().loadData(items);
Ext.each(items,function(item,index,allItems){if(item.selected){this.setValue(item.code);
return false
}},this)
},getSSPStore:function(){if(!this.sspStore){this.sspStore=new Ext.data.JsonStore({idProperty:"code",fields:[{name:"code"},{name:"value"}]})
}return this.sspStore
},getItems:function(){var matcher=new Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher();
var matchingValues=[];
if(this.config.previous){Ext.each(this.config.parameter.values,function(item,index,allItems){if(matcher.matches(item.availableFor,this.config.previous.type,this.config.previous.selection)){matchingValues.push(item)
}},this)
}else{matchingValues=this.config.parameter.values
}var overridenSelection={};
if(this.config.editValue||this.config.currentElementSelection){var elementsToKeepSelected=[];
if(this.config.editValue){elementsToKeepSelected.push(this.config.editValue[0])
}else{if(this.config.currentElementSelection){Ext.each(matchingValues,function(item,index,allItems){if(this.config.currentElementSelection.indexOf(item.code)!=-1){elementsToKeepSelected.push(item.code)
}},this)
}}if(!Ext.isEmpty(elementsToKeepSelected)){Ext.each(matchingValues,function(item,index,allItems){overridenSelection[item.code]=(elementsToKeepSelected.indexOf(item.code)!=-1)
},this)
}}var items=[];
Ext.each(matchingValues,function(item,index,allItems){var selected=overridenSelection[item.code]!=null?overridenSelection[item.code]:item.selected;
items.push({code:item.code,value:item.value,selected:selected})
},this);
return items
},buildValue:function(){return this.getValue()
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.SingleSelection");
Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ListBox=Ext.extend(Ext.ux.form.MultiSelect,{initComponent:function(){Ext.apply(this,{store:this.getSSPStore(),displayField:"value",valueField:"code",allowBlank:false,width:300,maxSelections:1,style:{overflow:"visible"},listeners:{change:function(){this.fireEvent("valueChanged")
},afterrender:function(){var items=this.getItems();
this.getSSPStore().loadData(items);
var selectedItemCodes=[];
Ext.each(items,function(item,index,allItems){if(item.selected){selectedItemCodes.push(item.code)
}},this);
this.setValue(selectedItemCodes.join())
},scope:this}});
Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ListBox.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged")
},getSSPStore:function(){if(!this.sspStore){this.sspStore=new Ext.data.JsonStore({idProperty:"code",fields:[{name:"code"},{name:"value"}]})
}return this.sspStore
},getItems:function(){var matcher=new Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher();
var matchingValues=[];
if(this.config.previous){Ext.each(this.config.parameter.values,function(item,index,allItems){if(matcher.matches(item.availableFor,this.config.previous.type,this.config.previous.selection)){matchingValues.push(item)
}},this)
}else{matchingValues=this.config.parameter.values
}var overridenSelection={};
if(this.config.editValue||this.config.currentElementSelection){var elementsToKeepSelected=[];
if(this.config.editValue){elementsToKeepSelected.push(this.config.editValue[0])
}else{if(this.config.currentElementSelection){Ext.each(matchingValues,function(item,index,allItems){if(this.config.currentElementSelection.indexOf(item.code)!=-1){elementsToKeepSelected.push(item.code)
}},this)
}}if(!Ext.isEmpty(elementsToKeepSelected)){Ext.each(matchingValues,function(item,index,allItems){overridenSelection[item.code]=(elementsToKeepSelected.indexOf(item.code)!=-1)
},this)
}}var items=[];
Ext.each(matchingValues,function(item,index,allItems){var selected=overridenSelection[item.code]!=null?overridenSelection[item.code]:item.selected;
items.push({code:item.code,value:item.value,selected:selected})
},this);
return items
},buildValue:function(){return this.getRawValue()
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters.SourceSelection");
Openwis.RequestSubscription.SubSelectionParameters.SourceSelection.Source=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{layout:"column"});
Openwis.RequestSubscription.SubSelectionParameters.SourceSelection.Source.superclass.initComponent.apply(this,arguments);
this.addEvents("valueChanged")
},initialize:function(){this.add(this.getGeographicalExtentContainer());
this.add(this.getListBoxContainer())
},getGeographicalExtentContainer:function(){if(!this.geographicalExtentContainer){this.geographicalExtentContainer=new Ext.Container({columnWidth:0.5,style:{margin:"5px"}});
this.add(this.getGeographicalExtentRadio());
this.add(this.getGeographicalExtent())
}return this.geographicalExtentContainer
},getGeographicalExtentRadio:function(){if(!this.geographicalExtentRadio){this.geographicalExtentRadio=new Ext.form.Radio({name:"rb-col",inputValue:"GEO_EXTENT",boxLabel:"In the map",checked:true,listeners:{check:function(checkbox,checked){if(checked){this.getGeographicalExtent().enable()
}else{this.getGeographicalExtent().disable()
}},scope:this}})
}return this.geographicalExtentRadio
},getGeographicalExtent:function(){if(!this.geographicalExtent){var configGeo={};
Ext.apply(configGeo,this.config,{width:200,height:150,style:{marginTop:"5px"}});
this.geographicalExtent=new Openwis.RequestSubscription.SubSelectionParameters.GeographicalSelection.GeographicalExtent(configGeo);
this.geographicalExtent.on("valueChanged",function(args){this.fireEvent("valueChanged",args)
},this)
}return this.geographicalExtent
},getListBoxContainer:function(){if(!this.listBoxContainer){this.listBoxContainer=new Ext.Container({columnWidth:0.5,style:{margin:"5px"}});
this.add(this.getListBoxRadio());
this.add(this.getListBox())
}return this.listBoxContainer
},getListBoxRadio:function(){if(!this.listBoxRadio){this.listBoxRadio=new Ext.form.Radio({name:"rb-col",inputValue:"LIST",boxLabel:"In the list",checked:true,listeners:{check:function(checkbox,checked){if(checked){this.getListBox().enable()
}else{this.getListBox().disable()
}},scope:this}})
}return this.listBoxRadio
},getListBox:function(){if(!this.listBox){var configListBox={};
Ext.apply(configListBox,this.config,{disabled:true,style:{marginTop:"5px"}});
this.listBox=new Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.ListBox(configListBox);
this.listBox.on("valueChanged",function(args){this.fireEvent("valueChanged",args)
},this)
}return this.listBox
},isValueDefined:function(){if(this.getGeographicalExtentRadio().checked){return this.getGeographicalExtent().isValueDefined()
}else{return this.getListBox().isValueDefined()
}},getSelectedValue:function(){if(this.getGeographicalExtentRadio().checked){return this.getGeographicalExtent().getSelectedValue()
}else{return this.getListBox().getSelectedValue()
}},buildSSP:function(){if(this.getGeographicalExtentRadio().checked){return this.getGeographicalExtent().buildSSP()
}else{return this.getListBox().buildSSP()
}},validate:function(){if(this.getGeographicalExtentRadio().checked){return this.getGeographicalExtent().validate()
}else{return this.getListBox().validate()
}}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters");
Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{border:false,cls:"SSPPanel",itemCls:"SSPItemsPanel",style:{padding:"20px"}});
Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct.superclass.initComponent.apply(this,arguments);
this.addEvents("panelInitialized","nextActive");
this.fireEvent("nextActive",false);
this.isInitialized=false
},initializeAndShow:function(){if(!this.isInitialized){this.isInitialized=true;
this.getInfosAndRefresh()
}},buildSSPs:function(){var ssps=[];
Ext.each(this.getCatalog().getComponentsCatalog(),function(item,index,allItems){if(item.code!=this.getCatalog().getScheduleCode()&&(item.code!="FAILURE"&&item.code!="SUCCESS")){var ssp={};
ssp.code=item.code;
var value=item.component.buildValue();
ssp.values=Ext.isArray(value)?value:[value];
ssps.push(ssp)
}},this);
return ssps
},buildFrequency:function(){if(this.isSubscription){var scheduleItem=this.getCatalog().getComponent(this.getCatalog().getScheduleCode());
return scheduleItem.component.buildValue()
}else{return null
}},getInfosAndRefresh:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.get.request.subselectionparameters",params:{urn:this.productMetadataUrn,subscription:this.isSubscription},listeners:{success:function(config){if(config.parameters&&config.parameters.length>0&&config.parameters[0].code=="FAILURE"){Openwis.Utils.MessageBox.displayErrorMsg(config.parameters[0].label)
}else{this.refresh(config)
}},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},hasDefaultValues:function(code){var parameter=this.getCatalog().getParameterByCode(code);
var comp=this.getCatalog().getComponent(code);
var isDefaultValues=false;
if(this.getEditValue(code)!=undefined){isDefaultValues=true
}else{if(parameter.values){if(parameter.values.length>0){Ext.each(parameter.values,function(item,index,allItems){if(item.selected){isDefaultValues=true;
return
}},this)
}}}return isDefaultValues
},replaceCurrentElement:function(code,subSelectionParameter,extComponent){var nextParameterCode=this.getCatalog().getNextParameterCode(code);
if(nextParameterCode){var sspToExtConfig={};
if(this.getCatalog().isInteractive){if(this.isSubscription&&(nextParameterCode=="SUCCESS")){sspToExtConfig.parameter={selectionType:"ScheduleSelection",code:this.getCatalog().getScheduleCode(),label:Openwis.i18n("RequestSubscription.SSP.Schedule.Title")};
var emptyComponent=this.getSSPToExtHelper().createEmptyComponent(sspToExtConfig.parameter.label);
this.add(emptyComponent);
this.getCatalog().registerComponent(sspToExtConfig.parameter.code,emptyComponent,this.getIdx())
}else{sspToExtConfig.parameter=this.getCatalog().getParameterByCode(nextParameterCode)
}}else{sspToExtConfig.parameter=this.getCatalog().getParameterByCode(nextParameterCode)
}if(this.isEdition()){sspToExtConfig.editValue=this.getEditValue(sspToExtConfig.parameter.code)
}sspToExtConfig.previous={};
sspToExtConfig.previous.type=subSelectionParameter.selectionType;
sspToExtConfig.previous.selection=Ext.isArray(extComponent.buildValue())?extComponent.buildValue():[extComponent.buildValue()];
var followingCatalogComponent=this.getCatalog().getComponent(nextParameterCode);
if(followingCatalogComponent){sspToExtConfig.currentElementSelection=followingCatalogComponent.component.buildValue()
}this.resetComponent(sspToExtConfig);
if(this.getCatalog().isInteractive){if(this.hasDefaultValues(nextParameterCode)){this.onValueChanged(nextParameterCode)
}}else{this.onValueChanged(nextParameterCode)
}}else{this.fireEvent("nextActive",true)
}},getInfosForNextParameter:function(code,subSelectionParameter,catalogComponent,extComponent){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.get.request.subselectionparameters",params:{urn:this.productMetadataUrn,subscription:this.isSubscription,parameters:this.buildSSPs()},listeners:{success:function(config){if(config.parameters[0].code=="FAILURE"){Openwis.Utils.MessageBox.displayErrorMsg(config.parameters[0].label)
}else{this.getCatalog().setSubSelectionParameters(config.parameters);
this.getCatalog().getParameterByCode(config.parameters[0].code).values=config.parameters[0].values;
var parameter=this.getCatalog().getParameterByCode(config.parameters[0].code);
var emptyComponent=this.getSSPToExtHelper().createEmptyComponent(parameter.label);
this.add(emptyComponent);
this.idx=this.getIdx()+1;
this.getCatalog().registerComponent(parameter.code,emptyComponent,this.getIdx());
this.replaceCurrentElement(code,subSelectionParameter,extComponent)
}},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},refresh:function(response){var start=null;
var subSelectionParameters=[];
var isInteractive;
isInteractive=response.interactive;
if(response.startParameter&&response.parameters||!isInteractive){start=response.startParameter;
subSelectionParameters=response.parameters;
if(!start){this.add(this.getSSPToExtHelper().createNoSSPAvailableContainer())
}if(this.isSubscription){if(!start){start=this.getCatalog().getScheduleCode()
}subSelectionParameters.push({selectionType:"ScheduleSelection",code:this.getCatalog().getScheduleCode(),label:Openwis.i18n("RequestSubscription.SSP.Schedule.Title")})
}}else{if(response.parameters){subSelectionParameters=response.parameters
}else{this.add(this.getSSPToExtHelper().createNoSSPAvailableContainer())
}}this.getCatalog().setSubSelectionParameters(subSelectionParameters);
this.getCatalog().isInteractive=isInteractive;
if(start){var parameterCode=start,nextParameterCode=null;
do{var parameter=this.setParameterToComponent(parameterCode);
this.idx++;
nextParameterCode=parameter.nextParameter;
if(!nextParameterCode&&this.isSubscription&&parameterCode!=this.getCatalog().getScheduleCode()){nextParameterCode=this.getCatalog().getScheduleCode()
}}while(parameterCode=nextParameterCode);
this.setSspToExtConfig(start)
}else{if(response.parameters){var parameterCode=subSelectionParameters[0].code;
var parameter=this.setParameterToComponent(parameterCode);
this.setSspToExtConfig(parameterCode)
}else{this.fireEvent("nextActive",true)
}}this.fireEvent("panelInitialized")
},setParameterToComponent:function(parameterCode){var parameter=this.getCatalog().getParameterByCode(parameterCode);
var emptyComponent=this.getSSPToExtHelper().createEmptyComponent(parameter.label);
this.add(emptyComponent);
this.getCatalog().registerComponent(parameter.code,emptyComponent,this.getIdx());
return parameter
},setSspToExtConfig:function(parameterCode){var sspToExtConfig={parameter:this.getCatalog().getParameterByCode(parameterCode)};
if(this.isEdition()){sspToExtConfig.editValue=this.getEditValue(sspToExtConfig.parameter.code)
}this.resetComponent(sspToExtConfig);
if(this.getCatalog().isInteractive){if(this.hasDefaultValues(parameterCode)){this.onValueChanged(parameterCode)
}}else{this.onValueChanged(parameterCode)
}},removeFollowingComponents:function(code){while(code!=this.getCatalog().getComponentsCatalog()[this.getCatalog().getComponentsCatalog().length-1].code){var catalogCmp=this.getCatalog().getComponent(this.getCatalog().getComponentsCatalog()[this.getCatalog().getComponentsCatalog().length-1].code);
var cmp=catalogCmp.component;
this.remove(cmp);
this.getCatalog().getComponentsCatalog().pop()
}},resetComponent:function(sspToExtConfig){var parameter=this.getCatalog().getParameterByCode(sspToExtConfig.parameter.code);
var sspToExtComponent=null;
if(this.readOnly){sspToExtComponent=this.getSSPToExtHelper().createReadOnlyComponent(sspToExtConfig)
}else{sspToExtComponent=this.getSSPToExtHelper().createComponent(sspToExtConfig);
sspToExtComponent.on("valueChanged",function(){this.onValueChanged(sspToExtConfig.parameter.code)
},this)
}var catalogCmp=this.getCatalog().getComponent(sspToExtConfig.parameter.code);
var idx=catalogCmp.indexInForm;
var cmp=catalogCmp.component;
this.remove(cmp);
this.getCatalog().registerComponent(sspToExtConfig.parameter.code,sspToExtComponent,idx);
sspToExtComponent.scrollRef=this.ownerCt.body;
if(idx==0&&parameter.code==this.getCatalog().getScheduleCode()){this.insert(1,sspToExtComponent)
}else{this.insert(idx,sspToExtComponent)
}this.doLayout()
},onValueChanged:function(code){var subSelectionParameter=this.getCatalog().getParameterByCode(code);
var catalogComponent=this.getCatalog().getComponent(code);
var extComponent=catalogComponent.component;
if(extComponent.buildValue()){if(extComponent.isValid()){if(this.getCatalog().isInteractive){this.removeFollowingComponents(code);
this.getInfosForNextParameter(code,subSelectionParameter,catalogComponent,extComponent)
}else{this.replaceCurrentElement(code,subSelectionParameter,extComponent)
}}}else{var currentIndex=this.getCatalog().indexOf(code);
var hasCleaned=false;
Ext.each(this.getCatalog().getComponentsCatalog(),function(item,index,allItems){if(index>currentIndex){var cmp=item.component;
this.remove(cmp);
var parameter=this.getCatalog().getParameterByCode(item.code);
var emptyComponent=this.getSSPToExtHelper().createEmptyComponent(parameter.label);
this.getCatalog().registerComponent(item.code,emptyComponent,this.getIdx());
this.insert(this.getIdx(),emptyComponent);
hasCleaned=true
}},this);
if(hasCleaned){this.doLayout();
this.fireEvent("nextActive",false)
}}},isEdition:function(){return this.ssp!=null
},getCatalog:function(){if(!this.catalog){this.catalog=new Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPCatalog()
}return this.catalog
},getSSPToExtHelper:function(){if(!this.sspToExtHelper){this.sspToExtHelper=new Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPToExt()
}return this.sspToExtHelper
},getIdx:function(){if(!this.idx){this.idx=0
}return this.idx
},getEditValue:function(code){var itemToFind=null;
if(this.isEdition()){if(code!=this.getCatalog().getScheduleCode()){Ext.each(this.ssp,function(item,index,allItems){if(item.code==code){itemToFind=item.value;
return
}},this)
}else{return this.frequency
}}return itemToFind
}});Ext.ns("Openwis.RequestSubscription.SubSelectionParameters");
Openwis.RequestSubscription.SubSelectionParameters.SSPGlobalProduct=Ext.extend(Ext.form.FormPanel,{initComponent:function(){var labelWidth={};
if(this.ssp&&this.ssp[0]||this.isSubscription){labelWidth=100
}else{labelWidth=1
}Ext.apply(this,{border:false,cls:"SSPPanel",itemCls:"SSPItemsPanel",style:{padding:"10px"},labelWidth:labelWidth});
Openwis.RequestSubscription.SubSelectionParameters.SSPGlobalProduct.superclass.initComponent.apply(this,arguments);
this.addEvents("panelInitialized","nextActive");
this.fireEvent("nextActive",false);
this.isInitialized=false
},initialize:function(){if(this.isSubscription){this.add(this.getCachePeriodPanel());
this.add(this.getSchedulePanel())
}else{this.add(this.getCacheFilePanel())
}this.isInitialized=true
},getCachePeriodPanel:function(){if(!this.cachePeriodPanel){var ssp=null;
if(this.ssp&&this.ssp[0]){ssp=this.ssp[0]
}this.cachePeriodPanel=new Openwis.RequestSubscription.SubSelectionParameters.Cache.Period({fieldLabel:Openwis.i18n("RequestSubscription.SSP.Cache.Period.Title"),ssp:ssp})
}return this.cachePeriodPanel
},getSchedulePanel:function(){if(!this.schedulePanel){this.schedulePanel=new Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule({fieldLabel:Openwis.i18n("RequestSubscription.SSP.Schedule.Title"),frequency:this.frequency})
}return this.schedulePanel
},getCacheFilePanel:function(){if(!this.cacheFilePanel){var ssp=null;
if(this.ssp&&this.ssp[0]){this.cacheFilePanel=new Ext.form.DisplayField({fieldLabel:Openwis.i18n("RequestSubscription.SSP.Cache.Type"),value:Openwis.i18n("RequestSubscription.SSP.Cache.Type.Label"),buildValue:function(){return labelValue
}})
}else{this.cacheFilePanel=new Openwis.RequestSubscription.SubSelectionParameters.Cache.File({fieldLabel:"",productMetadataUrn:this.productMetadataUrn})
}}return this.cacheFilePanel
},initializeAndShow:function(){if(!this.isInitialized){this.initialize()
}},buildSSPs:function(){var ssps=[];
var ssp={};
var value=null;
if(this.isSubscription){ssp.code="parameter.time.interval";
value=this.getCachePeriodPanel().buildValue()
}else{ssp.code="parameter.product.id";
value=this.getCacheFilePanel().buildValue()
}ssp.values=Ext.isArray(value)?value:[value];
ssps.push(ssp);
return ssps
},buildFrequency:function(){if(this.isSubscription){return this.getSchedulePanel().buildValue()
}else{return null
}}});Ext.ns("Openwis.RequestSubscription.BackUp");
Openwis.RequestSubscription.BackUp.BackupSelection=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{width:650,layout:"table",layoutConfig:{columns:2},style:{padding:"10px 10px 10px 30px"}});
Openwis.RequestSubscription.BackUp.BackupSelection.superclass.initComponent.apply(this,arguments)
},initializeAndShow:function(){this.add(this.getDeploymentsComboBox());
this.add(this.getBackUpCentreButton());
this.doLayout()
},getDeploymentsComboBox:function(){if(!this.deploymentsComboBox){var deploymentStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.user.backup.centres",idProperty:"name",fields:[{name:"name"},{name:"url"}],listeners:{load:function(store,records,options){if(store.getCount()==0){this.getBackUpCentreButton().disable()
}},scope:this}});
this.deploymentsComboBox=new Ext.form.ComboBox({store:deploymentStore,valueField:"url",displayField:"name",name:"deployment",emptyText:Openwis.i18n("TrackMySubscriptions.Remote.Select.Deployment"),typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.deploymentsComboBox
},getBackUpCentreButton:function(){if(!this.backUpCentreButton){this.backUpCentreButton=new Ext.Button(this.getGoToBackupAction())
}return this.backUpCentreButton
},getGoToBackupAction:function(){if(!this.goToBackupAction){this.goToBackupAction=new Ext.Action({text:"Go to Back Up centre",scope:this,handler:function(){var logicalRemoteDeploymentURL=this.getDeploymentsComboBox().getValue();
var requestIdentifier=this.config.requestID;
window.open(logicalRemoteDeploymentURL+"/retrieve/subscribe/"+this.config.productMetadataURN+"?backupRequestId="+requestIdentifier+"&deployment="+localCentreName)
}})
}return this.goToBackupAction
}});Ext.ns("Openwis.RequestSubscription.DisseminationParameters.Components");
Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{title:this.getTitle(),layout:"form",items:[]});
Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion.superclass.initComponent.apply(this,arguments);
this.addEvents("processRefresh");
this.initialize()
},initialize:function(){this.add(this.getZipMode());
this.getFTPFieldSet().add(this.getFTPDiffusionPanel());
this.add(this.getFTPFieldSet());
this.getMailFieldSet().add(this.getMailDiffusionPanel());
this.add(this.getMailFieldSet())
},getFTPFieldSet:function(){if(!this.ftpFieldSet){this.ftpFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("RequestSubscription.Dissemination.Diffusion.FTP.Title"),collapsed:true,collapsible:true,listeners:{beforeexpand:function(panel,animate){this.getMailFieldSet().collapse(false);
this.getFTPFieldSet().doLayout()
},scope:this}});
if(this.operationMode=="Create"){this.ftpFieldSet.addButton(new Ext.Button(this.getBookmarkAction()))
}}return this.ftpFieldSet
},getFTPDiffusionPanel:function(){if(!this.ftpDiffusionPanel){this.ftpDiffusionPanel=new Openwis.Common.Dissemination.FTPDiffusion()
}return this.ftpDiffusionPanel
},getMailFieldSet:function(){if(!this.mailFieldSet){this.mailFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("RequestSubscription.Dissemination.Diffusion.Mail.Title"),collapsed:true,collapsible:true,listeners:{beforeexpand:function(panel,animate){this.getFTPFieldSet().collapse(false);
this.getMailFieldSet().doLayout()
},scope:this}});
if(this.operationMode=="Create"){this.mailFieldSet.addButton(new Ext.Button(this.getBookmarkAction()))
}}return this.mailFieldSet
},getMailDiffusionPanel:function(){if(!this.mailDiffusionPanel){this.mailDiffusionPanel=new Openwis.Common.Dissemination.MailDiffusion()
}return this.mailDiffusionPanel
},getBookmarkAction:function(){if(!this.bookmarkAction){this.bookmarkAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){this.bookmark()
}})
}return this.bookmarkAction
},getTitle:function(){if(this.disseminationTool=="RMDCN"){return Openwis.i18n("RequestSubscription.Dissemination.Diffusion.RMDCN.Title")
}else{if(this.disseminationTool=="PUBLIC"){return Openwis.i18n("RequestSubscription.Dissemination.Diffusion.PUBLIC.Title")
}}},getZipMode:function(){if(!this.zipMode){this.zipMode=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["zipMode"],data:[["NONE"],["ZIPPED"],["WMO_FTP"]]}),valueField:"zipMode",displayField:"zipMode",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,fieldLabel:Openwis.i18n("RequestSubscription.Dissemination.ZippedMode.Title"),editable:false,value:"NONE",width:210})
}return this.zipMode
},refresh:function(favoritesDiseminationParams){if(favoritesDiseminationParams.ftp){this.getFTPDiffusionPanel().refresh(favoritesDiseminationParams.ftp)
}if(favoritesDiseminationParams.mail){this.getMailDiffusionPanel().refresh(favoritesDiseminationParams.mail)
}this.setVisible(favoritesDiseminationParams.authorizedFtp||favoritesDiseminationParams.authorizedMail);
this.getFTPFieldSet().setVisible(favoritesDiseminationParams.authorizedFtp);
this.getMailFieldSet().setVisible(favoritesDiseminationParams.authorizedMail)
},getForm:function(){if(!this.getFTPFieldSet().collapsed){return this.getFTPDiffusionPanel().getForm()
}else{if(!this.getMailFieldSet().collapsed){return this.getMailDiffusionPanel().getForm()
}}},getDisseminationValue:function(){var dissemValue={};
if(!this.getFTPFieldSet().collapsed){dissemValue.ftp=this.getFTPDiffusionPanel().getDisseminationValue()
}else{if(!this.getMailFieldSet().collapsed){dissemValue.mail=this.getMailDiffusionPanel().getDisseminationValue()
}}dissemValue.zipMode=this.getZipMode().getValue();
return dissemValue
},initializeFields:function(configObject){this.getZipMode().setValue(configObject.zipMode);
if(configObject.diffusion.host){this.getFTPDiffusionPanel().initializeFields(configObject.diffusion)
}else{if(configObject.diffusion.address){this.getMailDiffusionPanel().initializeFields(configObject.diffusion)
}}},bookmark:function(){if(this.getForm().isValid()){var dissemValue={};
if(!this.getFTPFieldSet().collapsed){dissemValue.ftp=this.getFTPDiffusionPanel().getDisseminationValue();
dissemValue.ftp.disseminationTool=this.disseminationTool
}else{if(!this.getMailFieldSet().collapsed){dissemValue.mail=this.getMailDiffusionPanel().getDisseminationValue();
dissemValue.mail.disseminationTool=this.disseminationTool
}}var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.save.favorite.dissemination.parameter",params:dissemValue,listeners:{success:function(){this.fireEvent("processRefresh")
},scope:this}});
saveHandler.proceed()
}else{Openwis.Utils.MessageBox.displayErrorMsg(validation.errorMsg)
}}});Ext.ns("Openwis.RequestSubscription.DisseminationParameters.Components");
Openwis.RequestSubscription.DisseminationParameters.Components.MSSFSS=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("RequestSubscription.Dissemination.MSSFSS.Title"),border:false});
Openwis.RequestSubscription.DisseminationParameters.Components.MSSFSS.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getChannelComboBox())
},getChannelComboBox:function(){if(!this.channelComboBox){var channelStore=new Ext.data.JsonStore({fields:["code","label"]});
this.channelComboBox=new Ext.form.ComboBox({store:channelStore,valueField:"code",displayField:"label",mode:"local",selectOnFocus:true,typeAhead:true,triggerAction:"all",fieldLabel:Openwis.i18n("RequestSubscription.Dissemination.MSSFSS.Channel"),allowBlank:false,editable:false,width:350})
}return this.channelComboBox
},getDisseminationValue:function(){var obj={};
obj.channel={channel:this.getChannelComboBox().getValue()};
return obj
},refresh:function(mssFss){var channels=[];
Ext.each(mssFss.mssFssChannels,function(item,index,allItems){channels.push({code:item,label:item})
},this);
this.getChannelComboBox().getStore().loadData(channels);
this.setVisible(mssFss.authorized)
},initializeFields:function(configObject){this.getChannelComboBox().setValue(configObject)
}});Ext.ns("Openwis.RequestSubscription.DisseminationParameters.Components");
Openwis.RequestSubscription.DisseminationParameters.Components.StagingPost=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("RequestSubscription.Dissemination.StagingPost.Title"),border:false});
Openwis.RequestSubscription.DisseminationParameters.Components.StagingPost.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getZipMode());
this.add(this.getInformationPanel())
},getZipMode:function(){if(!this.zipMode){this.zipMode=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["zipMode"],data:[["NONE"],["ZIPPED"],["WMO_FTP"]]}),valueField:"zipMode",displayField:"zipMode",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,fieldLabel:Openwis.i18n("RequestSubscription.Dissemination.ZippedMode.Title"),editable:false,value:"NONE",width:210})
}return this.zipMode
},getInformationPanel:function(){if(!this.informationPanel){this.informationPanel=new Ext.form.DisplayField({hideLabel:true,value:Openwis.i18n("RequestSubscription.Dissemination.StagingPost.Warning",{purgeDays:5})})
}return this.informationPanel
},getDisseminationValue:function(){return{zipMode:this.getZipMode().getValue()}
},validate:function(){return{ok:true}
},initializeFields:function(configObject){this.getZipMode().setValue(configObject.zipMode)
}});Ext.ns("Openwis.RequestSubscription.DisseminationParameters");
Openwis.RequestSubscription.DisseminationParameters.Selection=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{layout:"form",border:false});
Openwis.RequestSubscription.DisseminationParameters.Selection.superclass.initComponent.apply(this,arguments);
this.addEvents("panelInitialized","disseminationChanged");
this.isInitialized=false
},getAccordionMainPanel:function(){if(!this.accordionMainPanel){this.accordionMainPanel=new Ext.Panel({layout:"accordion",hidden:this.optional,listeners:{afterrender:function(){if(this.config){this.getAccordionMainPanel().getLayout().setActiveItem(this.getPanelByType(this.config.type))
}else{this.getAccordionMainPanel().getLayout().setActiveItem(this.getStagingPostPanel())
}},scope:this}})
}return this.accordionMainPanel
},getDisseminationInfo:function(){if(!this.disseminationInfo){var identifier=null;
var identifierLabel=null;
if(this.config.type=="RMDCN"||this.config.type=="PUBLIC"){if(this.config.o.diffusion.host){identifier=this.config.o.diffusion.host;
identifierLabel=Openwis.i18n("RequestSubscription.Dissemination.Diffusion.FTP.Title")
}else{if(this.config.o.diffusion.address){identifier=this.config.o.diffusion.address;
identifierLabel=Openwis.i18n("RequestSubscription.Dissemination.Diffusion.Mail.Title")
}}}this.disseminationInfo=new Ext.form.FormPanel({border:false,labelWidth:120,items:[{xtype:"displayfield",value:this.config.type,fieldLabel:Openwis.i18n("RequestSubscription.Dissemination.Type")},{xtype:"displayfield",value:identifier,hidden:identifier,fieldLabel:identifierLabel}]})
}return this.disseminationInfo
},getSpecifyDissemination:function(){if(!this.specifyDissemination){this.specifyDissemination=new Ext.form.Checkbox({boxLabel:Openwis.i18n("RequestSubscription.Specify.Dissemination"),hideLabel:true,handler:function(){if(this.specifyDissemination.checked){this.getAccordionMainPanel().show()
}else{this.getAccordionMainPanel().hide()
}},scope:this})
}return this.specifyDissemination
},initializeAndShow:function(){if(!this.isInitialized){this.isInitialized=true;
if(!this.readOnly){this.getInfosAndRefresh(true)
}else{if(this.config){this.add(this.getDisseminationInfo())
}}this.doLayout()
}},getMssFssPanel:function(){if(!this.mssFssPanel){this.mssFssPanel=new Openwis.RequestSubscription.DisseminationParameters.Components.MSSFSS({listeners:{activate:function(){this.fireEvent("disseminationChanged","MSS_FSS");
this.mssFssPanel.addClass("dissemination-selected")
},collapse:function(){this.mssFssPanel.removeClass("dissemination-selected")
},scope:this}})
}return this.mssFssPanel
},getRmdcnDiffusionPanel:function(){if(!this.rmdcnDiffusionPanel){this.rmdcnDiffusionPanel=new Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion({disseminationTool:"RMDCN",operationMode:this.operationMode,listeners:{processRefresh:function(){this.getInfosAndRefresh(false)
},activate:function(){this.fireEvent("disseminationChanged","RMDCN");
this.rmdcnDiffusionPanel.addClass("dissemination-selected")
},collapse:function(){this.rmdcnDiffusionPanel.removeClass("dissemination-selected")
},scope:this}})
}return this.rmdcnDiffusionPanel
},getPublicDiffusionPanel:function(){if(!this.publicDiffusion){this.publicDiffusion=new Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion({disseminationTool:"PUBLIC",operationMode:this.operationMode,listeners:{processRefresh:function(){this.getInfosAndRefresh(false)
},activate:function(){this.fireEvent("disseminationChanged","PUBLIC");
this.publicDiffusion.addClass("dissemination-selected")
},collapse:function(){this.publicDiffusion.removeClass("dissemination-selected")
},scope:this}})
}return this.publicDiffusion
},getStagingPostPanel:function(){if(!this.stagingPostPanel){this.stagingPostPanel=new Openwis.RequestSubscription.DisseminationParameters.Components.StagingPost({listeners:{activate:function(){this.fireEvent("disseminationChanged","STAGING_POST");
this.stagingPostPanel.addClass("dissemination-selected")
},collapse:function(){this.stagingPostPanel.removeClass("dissemination-selected")
},scope:this}})
}return this.stagingPostPanel
},getForm:function(){if((!this.optional||(this.optional&&this.getSpecifyDissemination().checked))&&this.getAccordionMainPanel().layout.activeItem){return this.getAccordionMainPanel().layout.activeItem.getForm()
}else{if(!this.optional&&!this.getAccordionMainPanel().layout.activeItem&&!this.config){return this.getStagingPostPanel().getForm()
}else{if(this.config){return this.getPanelByType(this.config.type).getForm()
}else{return null
}}}},buildDissemination:function(){var disseminationObject={};
if(this.optional&&!this.getSpecifyDissemination().checked){return null
}var activePanel=null;
if(this.getAccordionMainPanel().layout.activeItem){activePanel=this.getAccordionMainPanel().layout.activeItem
}else{if(!this.optional&&!this.getAccordionMainPanel().layout.activeItem&&!this.config){activePanel=this.getStagingPostPanel()
}else{if(this.config){activePanel=this.getPanelByType(this.config.type)
}}}if(activePanel){if(this.getMssFssPanel()==activePanel){disseminationObject.mssFssDissemination=this.getMssFssPanel().getDisseminationValue()
}else{if(this.getRmdcnDiffusionPanel()==activePanel){disseminationObject.rmdcnDiffusion=this.getRmdcnDiffusionPanel().getDisseminationValue()
}else{if(this.getPublicDiffusionPanel()==activePanel){disseminationObject.publicDiffusion=this.getPublicDiffusionPanel().getDisseminationValue()
}else{if(this.getStagingPostPanel()==activePanel){disseminationObject.shoppingCartDissemination=this.getStagingPostPanel().getDisseminationValue()
}}}}}return disseminationObject
},getInfosAndRefresh:function(isFirst){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.get.user.dissemination.parameters",params:{urn:this.productMetadataUrn,subscription:this.isSubscription},listeners:{success:function(config){this.refresh(config,isFirst)
},failure:function(config){if(this.ownerCt){this.ownerCt.ownerCt.close()
}},scope:this}});
getHandler.proceed()
},refresh:function(dissParams,isFirst){if(isFirst){if(this.optional){this.add(this.getSpecifyDissemination())
}this.getAccordionMainPanel().add(this.getMssFssPanel());
this.getAccordionMainPanel().add(this.getRmdcnDiffusionPanel());
this.getAccordionMainPanel().add(this.getPublicDiffusionPanel());
this.getAccordionMainPanel().add(this.getStagingPostPanel());
this.add(new Ext.Panel({layout:"fit",height:480,border:false,items:[this.getAccordionMainPanel()]}))
}this.getMssFssPanel().refresh(dissParams.mssFss);
this.getRmdcnDiffusionPanel().refresh(dissParams.rmdcnDiffusion);
this.getPublicDiffusionPanel().refresh(dissParams.publicDiffusion);
if(this.config){this.getSpecifyDissemination().setValue(true);
this.getAccordionMainPanel().show();
this.doLayout();
this.setDisseminationVisible("MSS_FSS",this.config.type=="MSS_FSS");
this.getPanelByType(this.config.type).initializeFields(this.config.o)
}if(isFirst){this.fireEvent("panelInitialized")
}},setDisseminationVisible:function(type,isVisible){this.getPanelByType(type).setVisible(isVisible)
},getPanelByType:function(type){if(type=="MSS_FSS"){return this.getMssFssPanel()
}else{if(type=="RMDCN"){return this.getRmdcnDiffusionPanel()
}else{if(type=="PUBLIC"){return this.getPublicDiffusionPanel()
}else{if(type=="STAGING_POST"){return this.getStagingPostPanel()
}}}}}});Ext.ns("Openwis.RequestSubscription");
Openwis.RequestSubscription.Summary=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{width:650,layout:"table",layoutConfig:{columns:1},style:{padding:"10px 10px 10px 30px"}});
Openwis.RequestSubscription.Summary.superclass.initComponent.apply(this,arguments)
},initializeAndShow:function(){this.add(this.getRequestInfo());
this.add(this.getProcessedRequestsGridPanel())
},getRequestInfo:function(){if(!this.requestInfo){this.requestInfo=new Ext.form.FormPanel({border:false,labelWidth:120,items:[{xtype:"displayfield",value:this.config.userName,fieldLabel:Openwis.i18n("RequestSubscription.Summary.User")},{xtype:"displayfield",value:this.config.requestID,fieldLabel:Openwis.i18n("RequestSubscription.Summary.RequestID")},{xtype:"displayfield",value:this.config.extractMode,fieldLabel:Openwis.i18n("RequestSubscription.Summary.DataSource")},{xtype:"displayfield",value:Ext.util.Format.htmlEncode(this.config.productMetadataURN),fieldLabel:Openwis.i18n("RequestSubscription.Summary.ProductMetadataURN")},{xtype:"displayfield",value:Ext.util.Format.htmlEncode(this.config.productMetadataTitle),fieldLabel:Openwis.i18n("RequestSubscription.Summary.ProductMetadataTitle")}]})
}return this.requestInfo
},getProcessedRequestsGridPanel:function(){if(!this.processedRequestsGridPanel){this.processedRequestsGridPanel=new Ext.grid.GridPanel({id:"processedRequestsGridPanel",height:250,width:550,border:true,store:this.getProcessedRequestsStore(),loadMask:true,columns:[{id:"statusImg",header:"",dataIndex:"status",renderer:Openwis.Common.Request.Utils.processedRequestStatusRendererImg,width:40,sortable:false},{id:"creationDateUtc",header:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.CreationDate"),dataIndex:"creationDateUtc",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:110,sortable:true},{id:"submittedDisseminationDateUtc",header:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.SubmittedDisseminationDate"),dataIndex:"submittedDisseminationDateUtc",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:120,sortable:true},{id:"completedDateUtc",header:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.CompletedDate"),dataIndex:"completedDateUtc",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:110,sortable:true},{id:"message",header:Openwis.i18n("RequestSubscription.Summary.Message"),dataIndex:"message",renderer:Openwis.Utils.Tooltip.Display,width:100,sortable:true},{id:"size",header:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.Size"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:100,sortable:true}],autoExpandColumn:"submittedDisseminationDateUtc",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getDownloadAction().setDisabled(sm.getCount()!=1||record.get("uri")==null||record.get("uri")=="")
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getDownloadAction().setDisabled(sm.getCount()!=1||record.get("uri")==null||record.get("uri")=="")
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getProcessedRequestsStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.Range"),emptyMsg:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.No.Elements")})});
this.processedRequestsGridPanel.addButton(new Ext.Button(this.getDownloadAction()));
if(this.config.isSubscription){this.processedRequestsGridPanel.addButton(new Ext.Button(this.getDiscardProcessedRequestAction()))
}}return this.processedRequestsGridPanel
},getProcessedRequestsStore:function(){if(!this.processedRequestsStore){this.processedRequestsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.processed.requests.all",idProperty:"id",remoteSort:true,root:"rows",totalProperty:"total",fields:[{name:"id"},{name:"message"},{name:"creationDateUtc",mapping:"creationDateUtc"},{name:"submittedDisseminationDateUtc",mapping:"submittedDisseminationDateUtc"},{name:"completedDateUtc",mapping:"completedDateUtc"},{name:"status",mapping:"requestResultStatus"},{name:"uri"},{name:"size"}],sortInfo:{field:"creationDateUtc",direction:"DESC"}});
this.processedRequestsStore.setBaseParam("id",this.config.requestID)
}return this.processedRequestsStore
},getDownloadAction:function(){if(!this.downloadAction){this.downloadAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Download"),disabled:true,iconCls:"icon-download-adhoc",scope:this,handler:function(){var rec=this.getProcessedRequestsGridPanel().getSelectionModel().getSelected();
window.open(rec.get("uri"))
}})
}return this.downloadAction
},getDiscardProcessedRequestAction:function(){if(!this.discardProcessedRequestAction){this.discardProcessedRequestAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Discard"),disabled:true,iconCls:"icon-discard-processedrequest",scope:this,handler:function(){var selection=this.getProcessedRequestsGridPanel().getSelectionModel().getSelections();
var params={discardRequests:[]};
Ext.each(selection,function(item,index,allItems){params.discardRequests.push({requestID:item.get("id")})
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.processed.requests.delete",params:params,listeners:{success:function(){this.getProcessedRequestsStore().reload()
},scope:this}}).proceed()
}})
}return this.discardProcessedRequestAction
},validate:function(){return true
}});Ext.ns("Openwis.RequestSubscription");
Openwis.RequestSubscription.MSSFSSSummary=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{width:650,layout:"table",layoutConfig:{columns:1},style:{padding:"10px 10px 10px 30px"}});
Openwis.RequestSubscription.MSSFSSSummary.superclass.initComponent.apply(this,arguments)
},initializeAndShow:function(){this.add(this.getRequestInfo());
this.add(this.getProcessedRequestsGridPanel())
},getRequestInfo:function(){if(!this.requestInfo){this.requestInfo=new Ext.form.FormPanel({border:false,labelWidth:120,items:[{xtype:"displayfield",value:this.config.userName,fieldLabel:Openwis.i18n("RequestSubscription.Summary.User")},{xtype:"displayfield",value:this.config.requestID,fieldLabel:Openwis.i18n("RequestSubscription.Summary.RequestID")},{xtype:"displayfield",value:this.config.productMetadataURN,fieldLabel:Openwis.i18n("RequestSubscription.Summary.ProductMetadataURN")}]})
}return this.requestInfo
},getProcessedRequestsGridPanel:function(){if(!this.processedRequestsGridPanel){this.processedRequestsGridPanel=new Ext.grid.GridPanel({id:"processedRequestsGridPanel",height:250,width:550,border:true,store:this.getProcessedRequestsStore(),loadMask:true,columns:[{id:"creationDate",header:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.CreationDate"),dataIndex:"creationDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:120,sortable:true},{id:"message",header:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.Message"),dataIndex:"message",sortable:true}],autoExpandColumn:"message",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getProcessedRequestsStore(),displayInfo:true,displayMsg:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.Range"),emptyMsg:Openwis.i18n("RequestSubscription.Summary.ProcessedRequest.No.Elements")})})
}return this.processedRequestsGridPanel
},getProcessedRequestsStore:function(){if(!this.processedRequestsStore){this.processedRequestsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.mssfss.processed.requests.all",idProperty:"creationDate",remoteSort:true,root:"rows",totalProperty:"total",fields:[{name:"creationDate"},{name:"message"}],sortInfo:{field:"creationDate",direction:"DESC"}});
this.processedRequestsStore.setBaseParam("id",this.config.requestID)
}return this.processedRequestsStore
},validate:function(){return true
}});Ext.ns("Openwis.RequestSubscription.Acknowlegment");
Openwis.RequestSubscription.Acknowlegment.Acknowlegment=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{layout:"fit"});
Openwis.RequestSubscription.Acknowlegment.Acknowlegment.superclass.initComponent.apply(this,arguments);
this.addEvents("panelInitialized");
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.create.request.subscription",params:this.request,successWindow:false,listeners:{success:function(acknowledgement){this.config=acknowledgement;
this.initialize()
},scope:this}});
saveHandler.proceed()
},initialize:function(){var msg="";
if(this.request.isSubscription){msg+="<p>"+Openwis.i18n("RequestSubscription.Acknowledgement.Line1.Subscription",{requestID:this.config.requestID})+"</p>";
msg+="<br/>";
msg+="<p>"+Openwis.i18n("RequestSubscription.Acknowledgement.Line2.Subscription",{locService:configOptions.locService})+"</p>";
msg+="<br/>";
msg+="<p>"+Openwis.i18n("RequestSubscription.Acknowledgement.Line3.Subscription")+"</p>"
}else{msg+="<p>"+Openwis.i18n("RequestSubscription.Acknowledgement.Line1.Request",{requestID:this.config.requestID})+"</p>";
msg+="<br/>";
msg+="<p>"+Openwis.i18n("RequestSubscription.Acknowledgement.Line2.Request",{locService:configOptions.locService})+"</p>"
}this.add(new Ext.Container({html:msg}));
this.fireEvent("panelInitialized")
},validate:function(){return true
}});Ext.ns("Openwis.RequestSubscription");
Openwis.RequestSubscription.Wizard=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{width:620,height:620,closeAction:"close",closable:false,autoScroll:true,resizable:true,plain:true,layout:"fit"});
Openwis.RequestSubscription.Wizard.superclass.initComponent.apply(this,arguments)
},initialize:function(productMetadataUrn,typeRequest,isGlobal,operationMode,requestID,isMssFss,backupRequestId,backupDeployment){this.productMetadataUrn=productMetadataUrn;
this.isSubscription=(typeRequest=="SUBSCRIPTION");
this.isGlobal=isGlobal;
this.operationMode=operationMode;
this.requestID=requestID;
this.isMssFss=isMssFss;
this.backupRequestId=backupRequestId;
this.backupDeployment=backupDeployment;
this.resetTitle();
this.readOnly=(operationMode=="View"&&typeRequest=="ADHOC");
if(operationMode!="Create"){var url=configOptions.locService;
if(this.isMssFss){url+="/xml.mssfss.subscription.get"
}else{if(typeRequest=="ADHOC"){url+="/xml.adhoc.get"
}else{url+="/xml.subscription.get"
}}new Openwis.Handler.Get({url:url,params:{id:this.requestID},listeners:{success:function(config){this.config=config;
this.config.isSubscription=this.isSubscription;
this.refresh()
},failure:function(){this.close()
},scope:this}}).proceed()
}else{this.refresh()
}},refresh:function(){this.initializeTabs();
this.add(this.getMainTabPanel());
this.addButton(new Ext.Button(this.getPreviousAction()));
this.addButton(new Ext.Button(this.getNextAction()));
if(!this.readOnly){this.addButton(new Ext.Button(this.getSaveAction()))
}this.addButton(new Ext.Button(this.getCloseWindowAction()));
this.y=20;
this.show()
},getMainTabPanel:function(){if(!this.mainTabPanel){this.mainTabPanel=new Ext.TabPanel({border:false,autoScroll:true,listeners:{tabchange:function(tabPanel,tab){this.activateActions();
tab.initializeAndShow()
},scope:this}})
}return this.mainTabPanel
},initializeTabs:function(){this.getMainTabPanel().add(this.getSubSelectionParametersPanel());
this.getMainTabPanel().add(this.getPrimaryDisseminationPanel());
this.getMainTabPanel().add(this.getSecondaryDisseminationPanel());
if(this.operationMode=="Create"||this.isGlobal){this.getSubSelectionParametersPanel().initializeAndShow()
}this.getPrimaryDisseminationPanel().initializeAndShow();
this.getSecondaryDisseminationPanel().initializeAndShow();
var indexOfSummaryTab=3;
if(this.isSubscription&&!this.backupRequestId&&this.isGlobal){this.getMainTabPanel().add(this.getSelectBackupPanel());
this.getSelectBackupPanel().initializeAndShow();
indexOfSummaryTab=4
}this.getMainTabPanel().add(this.getSummaryPanel());
if(this.operationMode=="Create"){this.getSummaryPanel().disable();
this.getSelectBackupPanel().disable();
this.getMainTabPanel().setActiveTab(0)
}else{this.getSelectBackupPanel().enable();
this.getMainTabPanel().setActiveTab(indexOfSummaryTab)
}},getSummaryPanel:function(){if(!this.summaryPanel){if(this.isMssFss){this.summaryPanel=new Openwis.RequestSubscription.MSSFSSSummary({config:this.config,title:Openwis.i18n("RequestSubscription.Summary.Step")})
}else{this.summaryPanel=new Openwis.RequestSubscription.Summary({config:this.config,title:Openwis.i18n("RequestSubscription.Summary.Step")})
}}return this.summaryPanel
},getSubSelectionParametersPanel:function(){if(!this.subSelectionParametersPanel){if(this.isGlobal){this.subSelectionParametersPanel=new Openwis.RequestSubscription.SubSelectionParameters.SSPGlobalProduct({title:Openwis.i18n("RequestSubscription.SSP.Step"),productMetadataUrn:this.productMetadataUrn,isSubscription:this.isSubscription,ssp:this.config?this.config.ssp:null,frequency:this.config?this.config.frequency:null,readOnly:this.readOnly,listeners:{panelInitialized:function(){this.doLayout()
},scope:this}})
}else{this.subSelectionParametersPanel=new Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct({title:Openwis.i18n("RequestSubscription.SSP.Step"),productMetadataUrn:this.productMetadataUrn,isSubscription:this.isSubscription,readOnly:this.readOnly,ssp:this.config?this.config.ssp:null,frequency:this.config?this.config.frequency:null,listeners:{panelInitialized:function(){this.doLayout()
},scope:this}})
}}return this.subSelectionParametersPanel
},getPrimaryDisseminationPanel:function(){if(!this.primaryDisseminationPanel){this.primaryDisseminationPanel=new Openwis.RequestSubscription.DisseminationParameters.Selection({title:Openwis.i18n("RequestSubscription.Dissemination.Step1"),productMetadataUrn:this.productMetadataUrn,isSubscription:this.isSubscription,config:this.config?this.config.primaryDissemination:null,optional:false,readOnly:this.readOnly,operationMode:this.operationMode,listeners:{panelInitialized:function(){if(this.isMssFss){this.getPrimaryDisseminationPanel().setDisseminationVisible("RMDCN",false);
this.getPrimaryDisseminationPanel().setDisseminationVisible("PUBLIC",false);
this.getPrimaryDisseminationPanel().setDisseminationVisible("STAGING_POST",false)
}this.doLayout()
},disseminationChanged:function(type){if(type=="MSS_FSS"){this.getSecondaryDisseminationPanel().disable()
}else{this.getSecondaryDisseminationPanel().enable()
}},scope:this}})
}return this.primaryDisseminationPanel
},getSecondaryDisseminationPanel:function(){if(!this.secondaryDisseminationPanel){this.secondaryDisseminationPanel=new Openwis.RequestSubscription.DisseminationParameters.Selection({title:Openwis.i18n("RequestSubscription.Dissemination.Step2"),productMetadataUrn:this.productMetadataUrn,readOnly:this.readOnly,optional:true,isSubscription:this.isSubscription,config:this.config?this.config.secondaryDissemination:null,disabled:this.isMssFss,operationMode:this.operationMode,listeners:{panelInitialized:function(){this.getSecondaryDisseminationPanel().setDisseminationVisible("MSS_FSS",false);
this.doLayout()
},scope:this}})
}return this.secondaryDisseminationPanel
},getSelectBackupPanel:function(){if(!this.selectBackupPanel){this.selectBackupPanel=new Openwis.RequestSubscription.BackUp.BackupSelection({config:this.config,title:Openwis.i18n("RequestSubscription.Backup.Step")})
}return this.selectBackupPanel
},getRequest:function(){var request={};
request.requestID=this.requestID;
request.productMetadataURN=this.productMetadataUrn;
request.subscription=this.isSubscription;
request.extractMode=this.isGlobal?"GLOBAL":"NOT_IN_LOCAL_CACHE";
if(!this.getSubSelectionParametersPanel().isInitialized){request.parameters=this.createParametersForNotInitialized();
request.frequency=this.config.frequency
}else{request.parameters=this.getSubSelectionParametersPanel().buildSSPs();
request.frequency=this.getSubSelectionParametersPanel().buildFrequency()
}request.primaryDissemination=this.getPrimaryDisseminationPanel().buildDissemination();
try{delete (request.primaryDissemination.publicDifussion.ftp.uuid)
}catch(error){}request.secondaryDissemination=this.secondaryDisseminationPanel?this.getSecondaryDisseminationPanel().buildDissemination():null;
request.backupRequestId=this.backupRequestId;
request.backupDeployment=this.backupDeployment;
return request
},createParametersForNotInitialized:function(){var ssps=[];
Ext.each(this.config.ssp,function(item,index,allItems){var ssp={};
ssp.code=item.code;
var valueArray=[];
Ext.each(item.value,function(item,index,allItems){valueArray.push(item)
},this);
ssp.values=valueArray;
ssps.push(ssp)
},this);
return ssps
},activateActions:function(){var currentPanel=this.getCurrentPanel();
if(currentPanel==this.getSummaryPanel()){this.getCloseWindowAction().setText(Openwis.i18n("Common.Btn.Close"));
this.getNextAction().disable();
this.getPreviousAction().enable()
}else{if(currentPanel==this.getSubSelectionParametersPanel()){this.getCloseWindowAction().setText(Openwis.i18n("Common.Btn.Cancel"));
this.getNextAction().enable();
this.getPreviousAction().disable()
}else{if(currentPanel==this.getPrimaryDisseminationPanel()){this.getCloseWindowAction().setText(Openwis.i18n("Common.Btn.Cancel"));
this.getNextAction().enable();
this.getPreviousAction().enable()
}else{if(currentPanel==this.getSecondaryDisseminationPanel()){this.getCloseWindowAction().setText(Openwis.i18n("Common.Btn.Cancel"));
if(this.getSummaryPanel().disabled){this.getNextAction().disable()
}else{this.getNextAction().enable()
}this.getPreviousAction().enable()
}else{if(currentPanel==this.getSelectBackupPanel()){this.getCloseWindowAction().setText(Openwis.i18n("Common.Btn.Close"));
this.getNextAction().enable();
this.getPreviousAction().enable()
}}}}}},getCurrentPanel:function(){return this.getMainTabPanel().getActiveTab()
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){var task=new Ext.util.DelayedTask(this.performSave,this);
task.delay(300)
}})
}return this.saveAction
},performSave:function(){var violations=[];
var sspFormPanel=this.getSubSelectionParametersPanel();
if(!sspFormPanel.getForm()||!sspFormPanel.getForm().isValid()){violations.push(Openwis.i18n("RequestSubscription.Validation.SSP"))
}var primaryDissFormPanel=this.getPrimaryDisseminationPanel();
if(!primaryDissFormPanel.getForm()||!primaryDissFormPanel.getForm().isValid()){violations.push(Openwis.i18n("RequestSubscription.Validation.PrimaryDissemination"))
}var secondaryDissFormPanel=this.getSecondaryDisseminationPanel();
if(secondaryDissFormPanel.getForm()&&!secondaryDissFormPanel.getForm().isValid()){violations.push(Openwis.i18n("RequestSubscription.Validation.SecondaryDissemination"))
}if(Ext.isEmpty(violations)){var url=configOptions.locService;
if(this.operationMode=="Create"){url+="/xml.create.request.subscription"
}else{url+="/xml.update.request.subscription"
}new Openwis.Handler.Save({url:url,params:this.getRequest(),listeners:{success:function(config){var wizard=new Openwis.RequestSubscription.Wizard();
if(!this.backupRequestId){wizard.initialize(this.productMetadataUrn,this.isSubscription?"SUBSCRIPTION":"ADHOC",this.isGlobal,"View",config.requestID,this.getRequest().primaryDissemination.mssFssDissemination!=null)
}else{wizard.initialize(this.productMetadataUrn,this.isSubscription?"SUBSCRIPTION":"ADHOC",this.isGlobal,"View",config.requestID,this.getRequest().primaryDissemination.mssFssDissemination!=null,this.backupRequestId,this.backupDeployment)
}this.close()
},scope:this}}).proceed()
}else{var msg=Openwis.i18n("RequestSubscription.Validation.Failed")+"<br/>";
Ext.each(violations,function(item,index){msg+=(index+1)+". "+item+"<br/>"
},this);
Openwis.Utils.MessageBox.displayErrorMsg(msg)
}},getCloseWindowAction:function(){if(!this.closeWindowAction){this.closeWindowAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.closeWindowAction
},getNextAction:function(){if(!this.nextAction){this.nextAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Next"),scope:this,handler:function(){var currentPanel=this.getCurrentPanel();
if(currentPanel==this.getSubSelectionParametersPanel()){this.getMainTabPanel().setActiveTab(this.getPrimaryDisseminationPanel())
}else{if(currentPanel==this.getPrimaryDisseminationPanel()){this.getMainTabPanel().setActiveTab(this.getSecondaryDisseminationPanel())
}else{if(currentPanel==this.getSecondaryDisseminationPanel()){this.getMainTabPanel().setActiveTab(this.getSummaryPanel())
}}}}})
}return this.nextAction
},getPreviousAction:function(){if(!this.previousAction){this.previousAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Previous"),scope:this,handler:function(){var currentPanel=this.getCurrentPanel();
if(currentPanel==this.getPrimaryDisseminationPanel()){this.getMainTabPanel().setActiveTab(this.getSubSelectionParametersPanel())
}else{if(currentPanel==this.getSecondaryDisseminationPanel()){this.getMainTabPanel().setActiveTab(this.getPrimaryDisseminationPanel())
}else{if(currentPanel==this.getSummaryPanel()){this.getMainTabPanel().setActiveTab(this.getSecondaryDisseminationPanel())
}}}}})
}return this.previousAction
},resetTitle:function(){if(this.requestID){if(this.isSubscription){this.setTitle(Openwis.i18n("RequestSubscription.ViewEdit.Subscription",{requestID:this.requestID}))
}else{this.setTitle(Openwis.i18n("RequestSubscription.View.Request",{requestID:this.requestID}))
}}else{if(this.isGlobal){if(this.isSubscription){this.setTitle(Openwis.i18n("RequestSubscription.Create.Subscription.Cache"))
}else{this.setTitle(Openwis.i18n("RequestSubscription.Create.Request.Cache"))
}}else{if(this.isSubscription){this.setTitle(Openwis.i18n("RequestSubscription.Create.Subscription"))
}else{this.setTitle(Openwis.i18n("RequestSubscription.Create.Request"))
}}}}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.AbstractSearchPanel=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{border:false,autoHeight:true});
Openwis.HomePage.Search.AbstractSearchPanel.superclass.initComponent.apply(this,arguments);
this.initializeCommons()
},initializeCommons:function(){this.getOptionsFieldSet().add(this.getSortDirectionCombobox());
this.getOptionsFieldSet().add(this.getHitsCombobox());
this.getOptionsPanel().add(this.getOptionsFieldSet());
this.getButtonsPanel().addButton(new Ext.Button(this.getResetAction()));
this.getButtonsPanel().addButton(new Ext.Button(this.getSearchAction()))
},isLoaded:function(){return this.getButtonsPanel().rendered
},getWhatLabel:function(){if(!this.whatlabel){this.whatlabel=new Ext.Container({border:false,cls:"mainLabelCls whatLabel",html:"<i class='iconIOS7-bt_quetion_off'></i>"+Openwis.i18n("HomePage.Search.Criteria.What"),style:{padding:"2px"}})
}return this.whatlabel
},getWhereLabel:function(){if(!this.wherelabel){this.wherelabel=new Ext.Container({border:false,cls:"mainLabelCls whereLabel",html:"<i class='iconIOS7-bt_mark_off'></i>"+Openwis.i18n("HomePage.Search.Criteria.Where")})
}return this.wherelabel
},getOptionsPanel:function(){if(!this.optionsPanel){this.optionsPanel=new Ext.Panel({layout:"form",border:false,cls:"optionsPanel"})
}return this.optionsPanel
},getOptionsFieldSet:function(){if(!this.optionsFieldSet){this.optionsFieldSet=new Ext.form.FieldSet({title:"<i class='iconIOS7-bt_tool_off'></i>"+Openwis.i18n("HomePage.Search.Criteria.Options"),autoHeight:true,collapsed:true,collapsible:true,labelWidth:80,listeners:{afterrender:function(){this.optionsFieldSet.addListener("collapse",this.onGuiChanged,this);
this.optionsFieldSet.addListener("expand",this.onGuiChanged,this)
},scope:this},baseCls:"optFieldSet"})
}return this.optionsFieldSet
},getButtonsPanel:function(){if(!this.buttonsPanel){this.buttonsPanel=new Ext.Panel({border:false,buttonAlign:"center"})
}return this.buttonsPanel
},getSortDirectionCombobox:function(){if(!this.sortDirectionCombobox){this.sortDirectionCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["changeDate",Openwis.i18n("HomePage.Search.Criteria.Options.SortDirection.ChangeDate")],["popularity",Openwis.i18n("HomePage.Search.Criteria.Options.SortDirection.Popularity")],["rating",Openwis.i18n("HomePage.Search.Criteria.Options.SortDirection.Rating")],["relevance",Openwis.i18n("HomePage.Search.Criteria.Options.SortDirection.Relevance")],["title",Openwis.i18n("HomePage.Search.Criteria.Options.SortDirection.Title")]]}),valueField:"id",displayField:"value",value:"relevance",name:"sortDirection",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:194,fieldLabel:Openwis.i18n("HomePage.Search.Criteria.Options.SortDirection")})
}return this.sortDirectionCombobox
},getHitsCombobox:function(){if(!this.hitsCombobox){this.hitsCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["10","10"],["20","20"],["50","50"],["100","100"]]}),valueField:"id",displayField:"value",value:"10",name:"hitsPerPage",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:194,fieldLabel:Openwis.i18n("HomePage.Search.Criteria.Options.HitsPerPage")})
}return this.hitsCombobox
},getMapPanel:function(){if(!this.mapPanel){this.mapPanel=new Openwis.Common.Components.GeographicalExtentSelection({geoExtentType:"RECTANGLE",wmsUrl:"https://vmap0.tiles.osgeo.org/wms/vmap0?",layerName:"basic",maxExtent:new OpenLayers.Bounds(-180,-90,180,90),width:280,height:250,listeners:{valueChanged:function(bounds){var latMin=bounds.bottom;
var latMax=bounds.top;
var longMin=bounds.left;
var longMax=bounds.right;
bounds.bottom=Math.max(Math.min(latMin,90),-90);
bounds.top=Math.max(Math.min(latMax,90),-90);
bounds.left=this.getLongitude(longMin);
bounds.right=this.getLongitude(longMax);
this.updateMapFields(bounds,true)
},scope:this}})
}return this.mapPanel
},getRegionsCombobox:function(){if(!this.regionsCombobox){var regionsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.home.page.region.all",idProperty:"id",fields:[{name:"id"},{name:"name"},{name:"extent"}],listeners:{load:function(store,records,options){var anyRecord=new Ext.data.Record({id:"Any",name:Openwis.i18n("Common.List.Any"),extent:{left:-180,bottom:-90,right:180,top:90}});
var userDefinedRecord=new Ext.data.Record({id:"UserDefined",name:Openwis.i18n("HomePage.Search.Criteria.Where.Region.UserDefined")});
store.insert(0,[anyRecord,userDefinedRecord])
}}});
this.regionsCombobox=new Ext.form.ComboBox({store:regionsStore,valueField:"id",displayField:"name",name:"region",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:280,cls:"regionsSel",triggerClass:"regionsSel",listeners:{select:function(combobox,rec,index){if(rec.get("id")!="UserDefined"){var bounds=rec.get("extent");
this.getMapPanel().drawExtent(bounds);
this.getMapPanel().zoomToExtent(bounds);
this.updateMapFields(bounds,false)
}},scope:this}})
}return this.regionsCombobox
},createCriteriaLabel:function(label){return new Ext.Container({border:false,cls:"critLabelCls",html:label})
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Reset"),iconCls:"iconBtnReset",cls:"openwisAction homePageSearchAction",scope:this,handler:function(){this.reset();
document.title="ASEAN | WIS Portal";
document.getElementById("produit").innerHTML=""
}})
}return this.resetAction
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Search"),iconCls:"iconBtnSearch",cls:"openwisAction homePageSearchAction",scope:this,handler:function(){if(this.validate()){var params=this.buildSearchParams();
var url=this.searchUrl();
this.targetResult.loadSearchResults(url,params)
}}})
}return this.searchAction
},setRegionToUserDefined:function(){if(this.getRegionsCombobox().getStore().getCount()>0){this.getRegionsCombobox().setValue("UserDefined")
}else{this.getRegionsCombobox().getStore().load({callback:function(){this.getRegionsCombobox().setValue("UserDefined")
},scope:this})
}},getLongitude:function(lg){var de=(lg+180)%360;
while(de<0){de+=360
}return de-180
},onGuiChanged:function(){this.fireEvent("guiChanged",false,true)
}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.AdvancedSearchPanel=Ext.extend(Openwis.HomePage.Search.AbstractSearchPanel,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("HomePage.Search.Advanced.Title")});
Openwis.HomePage.Search.AdvancedSearchPanel.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getWhatLabel());
this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.Either")));
this.getWhatOthersCriteriaFieldSet().add(this.getEitherTextField());
this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.ExactPhrase")));
this.getWhatOthersCriteriaFieldSet().add(this.getExactPhraseTextField());
this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.What")));
this.getWhatOthersCriteriaFieldSet().add(this.getWhatTextField());
this.getWhatOthersCriteriaFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.Without")));
this.getWhatOthersCriteriaFieldSet().add(this.getWithoutTextField());
this.add(this.getWhatOthersCriteriaFieldSet());
this.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.Title")));
this.add(this.getTitleTextField());
this.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.Abstract")));
this.add(this.getAbstractTextField());
this.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.Keywords")));
this.add(this.getKeywordsTextField());
this.getWhatMapTypeFieldSet().add(this.getWhatMapTypeCheckboxGroup());
this.add(this.getWhatMapTypeFieldSet());
this.getWhatSearchAccuracyFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.SearchAccuracy.Imprecise")));
this.getWhatSearchAccuracyFieldSet().add(this.getWhatSearchAccuracyRadioGroup());
this.getWhatSearchAccuracyFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.What.SearchAccuracy.Precise")));
this.add(this.getWhatSearchAccuracyFieldSet());
this.add(this.getWhereLabel());
this.add(this.getMapPanel());
this.add(this.getWhereBoundsPanel());
this.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Type")));
this.add(this.getWhereTypeCombobox());
this.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Region")));
this.add(this.getRegionsCombobox());
this.getWhenFieldSet().add(this.getWhenAnytimeRadio());
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.When.Any")));
this.getWhenFieldSet().add(this.getWhenMetadataChangeDateRadio());
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.When.MetadataChangeDate")));
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.From")));
this.getWhenFieldSet().add(this.getWhenMetadataChangeDateFromDateField());
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.To")));
this.getWhenFieldSet().add(this.getWhenMetadataChangeDateToDateField());
this.getWhenFieldSet().add(this.getWhenTemporalExtentRadio());
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.When.TemporalExtent")));
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.From")));
this.getWhenFieldSet().add(this.getWhenTemporalExtentFromDateField());
this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.To")));
this.getWhenFieldSet().add(this.getWhenTemporalExtentToDateField());
this.add(this.getWhenFieldSet());
this.getRestrictToFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.RestrictTo.Category")));
this.getRestrictToFieldSet().add(this.getRestrictToCategoryComboBox());
this.getRestrictToFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.RestrictTo.Kind")));
this.getRestrictToFieldSet().add(this.getRestrictToKindComboBox());
this.add(this.getRestrictToFieldSet());
this.add(this.getOptionsPanel());
this.getInspireFieldSet().add(this.getOnlyInspireMetadataCheckbox());
this.getInspireFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex")));
this.getInspireFieldSet().add(this.getInspireAnnexComboBox());
this.getInspireFieldSet().add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType")));
this.getInspireFieldSet().add(this.getInspireServiceTypeComboBox());
this.add(this.getInspireFieldSet());
this.add(this.getButtonsPanel())
},getEitherTextField:function(){if(!this.eitherTextField){this.eitherTextField=new Ext.form.TextField({name:"or",allowBlank:true,width:280})
}return this.eitherTextField
},getTitleTextField:function(){if(!this.titleTextField){this.titleTextField=new Ext.form.TextField({name:"title",allowBlank:true,width:280})
}return this.titleTextField
},getAbstractTextField:function(){if(!this.abstractTextField){this.abstractTextField=new Ext.form.TextField({name:"abstract",allowBlank:true,width:280})
}return this.abstractTextField
},getKeywordsTextField:function(){if(!this.keywordsTextField){this.keywordsTextField=new Ext.form.TriggerField({name:"themekey",allowBlank:true,width:280,onTriggerClick:function(e){new Openwis.Common.Search.KeywordsSearch({keywordsFromTf:this.getValue(),isXML:false,listeners:{keywordsSelection:function(records){this.setValue(records)
},scope:this}})
}})
}return this.keywordsTextField
},getExactPhraseTextField:function(){if(!this.exactPhraseTextField){this.exactPhraseTextField=new Ext.form.TextField({name:"phrase",allowBlank:true,width:278})
}return this.exactPhraseTextField
},getWhatTextField:function(){if(!this.whatTextField){this.whatTextField=new Ext.form.TextField({name:"all",allowBlank:true,width:278})
}return this.whatTextField
},getWithoutTextField:function(){if(!this.withoutPhraseTextField){this.withoutPhraseTextField=new Ext.form.TextField({name:"without",allowBlank:true,width:278})
}return this.withoutPhraseTextField
},getWhatOthersCriteriaFieldSet:function(){if(!this.whatOthersCriteriaFieldSet){this.whatOthersCriteriaFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.What.TextSearchOptions"),layout:"table",layoutConfig:{columns:1},autoHeight:true,collapsed:true,collapsible:true});
this.whatOthersCriteriaFieldSet.addListener("collapse",this.onGuiChanged,this);
this.whatOthersCriteriaFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.whatOthersCriteriaFieldSet
},getWhatMapTypeFieldSet:function(){if(!this.whatMapTypeFieldSet){this.whatMapTypeFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.What.MapType"),layout:"table",layoutConfig:{columns:1},autoHeight:true,collapsed:true,collapsible:true});
this.whatMapTypeFieldSet.addListener("collapse",this.onGuiChanged,this);
this.whatMapTypeFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.whatMapTypeFieldSet
},getWhatMapTypeCheckboxGroup:function(){if(!this.whatMapTypeCheckboxGroup){this.whatMapTypeCheckboxGroup=new Ext.form.CheckboxGroup({columns:2,items:[{boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Digital"),name:"digital"},{boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Dynamic"),name:"dynamic"},{boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Paper"),name:"paper"},{boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Download"),name:"download"}],width:200})
}return this.whatMapTypeCheckboxGroup
},getWhatSearchAccuracyFieldSet:function(){if(!this.whatSearchAccuracyFieldSet){this.whatSearchAccuracyFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.What.SearchAccuracy"),layout:"table",layoutConfig:{columns:3},autoHeight:true,collapsed:true,collapsible:true,cls:"searchSliderCont"});
this.whatSearchAccuracyFieldSet.addListener("collapse",this.onGuiChanged,this);
this.whatSearchAccuracyFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.whatSearchAccuracyFieldSet
},getWhatSearchAccuracyRadioGroup:function(){if(!this.whatSearchAccuracyRadioGroup2){this.whatSearchAccuracyRadioGroup2=new Ext.form.SliderField({name:"similarity",value:80,width:150,cls:"searchSlider"});
this.whatSearchAccuracyRadioGroup2.slider.topThumbZIndex=8000
}return this.whatSearchAccuracyRadioGroup2
},getWhereBoundsPanel:function(){if(!this.whereBoundsPanel){this.whereBoundsPanel=new Ext.Panel({layout:"table",layoutConfig:{columns:4},defaults:{style:{margin:"4px"}},border:false});
this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LatMin")));
this.whereBoundsPanel.add(this.getWhereBoundsLatMinTextField());
this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LongMin")));
this.whereBoundsPanel.add(this.getWhereBoundsLongMinTextField());
this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LatMax")));
this.whereBoundsPanel.add(this.getWhereBoundsLatMaxTextField());
this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LongMax")));
this.whereBoundsPanel.add(this.getWhereBoundsLongMaxTextField())
}return this.whereBoundsPanel
},getWhereBoundsLatMinTextField:function(){if(!this.whereBoundsLatMinTextField){this.whereBoundsLatMinTextField=new Ext.form.TextField({name:"southBL",allowBlank:true,autoCreate:{tag:"input",type:"text",size:"5",autocomplete:"off"},listeners:{change:this.coordsChanged,scope:this},validator:function(value){if(value.trim()!=""&&isNaN(value)){return Openwis.i18n("Common.Validation.NotANumber",{value:value})
}return true
}})
}return this.whereBoundsLatMinTextField
},getWhereBoundsLatMaxTextField:function(){if(!this.whereBoundsLatMaxTextField){this.whereBoundsLatMaxTextField=new Ext.form.TextField({name:"northBL",allowBlank:true,autoCreate:{tag:"input",type:"text",size:"5",autocomplete:"off"},listeners:{change:this.coordsChanged,scope:this},validator:function(value){if(value.trim()!=""&&isNaN(value)){return Openwis.i18n("Common.Validation.NotANumber",{value:value})
}return true
}})
}return this.whereBoundsLatMaxTextField
},getWhereBoundsLongMinTextField:function(){if(!this.whereBoundsLongMinTextField){this.whereBoundsLongMinTextField=new Ext.form.TextField({name:"westBL",allowBlank:true,autoCreate:{tag:"input",type:"text",size:"5",autocomplete:"off"},listeners:{change:this.coordsChanged,scope:this},validator:function(value){if(value.trim()!=""&&isNaN(value)){return Openwis.i18n("Common.Validation.NotANumber",{value:value})
}return true
}})
}return this.whereBoundsLongMinTextField
},getWhereBoundsLongMaxTextField:function(){if(!this.whereBoundsLongMaxTextField){this.whereBoundsLongMaxTextField=new Ext.form.TextField({name:"eastBL",allowBlank:true,autoCreate:{tag:"input",type:"text",size:"5",autocomplete:"off"},listeners:{change:this.coordsChanged,scope:this},validator:function(value){if(value.trim()!=""&&isNaN(value)){return Openwis.i18n("Common.Validation.NotANumber",{value:value})
}return true
}})
}return this.whereBoundsLongMaxTextField
},getWhereTypeCombobox:function(){if(!this.whereTypeCombobox){this.whereTypeCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["intersects",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Intersection")],["contains",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Contains")],["iswithin",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Within")]]}),valueField:"id",displayField:"value",value:"iswithin",name:"relation",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:280,cls:"withinField"})
}return this.whereTypeCombobox
},getWhenAnytimeRadio:function(){if(!this.whenAnytimeRadio){this.whenAnytimeRadio=new Ext.form.Radio({name:"whenMode",inputValue:"Anytime",checked:true})
}return this.whenAnytimeRadio
},getWhenMetadataChangeDateRadio:function(){if(!this.whenMetadataChangeDateRadio){this.whenMetadataChangeDateRadio=new Ext.form.Radio({name:"whenMode",inputValue:"MetadataChangeDate",checked:false,listeners:{check:function(checkbox,checked){if(checked){this.getWhenMetadataChangeDateFromDateField().enable();
this.getWhenMetadataChangeDateToDateField().enable()
}else{this.getWhenMetadataChangeDateFromDateField().disable();
this.getWhenMetadataChangeDateToDateField().disable()
}},scope:this}})
}return this.whenMetadataChangeDateRadio
},getWhenMetadataChangeDateFromDateField:function(){if(!this.whenMetadataChangeDateFromDateField){this.whenMetadataChangeDateFromDateField=new Ext.form.DateField({allowBlank:false,name:"MetadataChangeDateFrom",editable:false,format:"Y-m-d",disabled:true,width:165})
}return this.whenMetadataChangeDateFromDateField
},getWhenMetadataChangeDateToDateField:function(){if(!this.whenMetadataChangeDateToDateField){this.whenMetadataChangeDateToDateField=new Ext.form.DateField({allowBlank:false,name:"MetadataChangeDateTo",editable:false,format:"Y-m-d",disabled:true,width:165})
}return this.whenMetadataChangeDateToDateField
},getWhenTemporalExtentRadio:function(){if(!this.whenTemporalExtentRadio){this.whenTemporalExtentRadio=new Ext.form.Radio({name:"whenMode",inputValue:"TemporalExtent",checked:false,listeners:{check:function(checkbox,checked){if(checked){this.getWhenTemporalExtentFromDateField().enable();
this.getWhenTemporalExtentToDateField().enable()
}else{this.getWhenTemporalExtentFromDateField().disable();
this.getWhenTemporalExtentToDateField().disable()
}},scope:this}})
}return this.whenTemporalExtentRadio
},getWhenTemporalExtentFromDateField:function(){if(!this.whenTemporalExtentFromDateField){this.whenTemporalExtentFromDateField=new Ext.form.DateField({allowBlank:false,name:"TemporalExtentFrom",editable:false,format:"Y-m-d",disabled:true,width:165})
}return this.whenTemporalExtentFromDateField
},getWhenTemporalExtentToDateField:function(){if(!this.whenTemporalExtentToDateField){this.whenTemporalExtentToDateField=new Ext.form.DateField({allowBlank:false,name:"TemporalExtentTo",editable:false,format:"Y-m-d",disabled:true,width:165})
}return this.whenTemporalExtentToDateField
},getWhenFieldSet:function(){if(!this.whenFieldSet){this.whenFieldSet=new Ext.form.FieldSet({title:"<i class='iconIOS7-bt_history_off'></i>"+Openwis.i18n("HomePage.Search.Criteria.When"),layout:"table",layoutConfig:{columns:2},cls:"mainLabelCls whenFieldSet",autoHeight:true,collapsed:true,collapsible:true});
this.whenFieldSet.addListener("collapse",this.onGuiChanged,this);
this.whenFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.whenFieldSet
},getRestrictToFieldSet:function(){if(!this.restrictToFieldSet){this.restrictToFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.RestrictTo"),layout:"table",layoutConfig:{columns:1},autoHeight:true,collapsed:true,collapsible:true});
this.restrictToFieldSet.addListener("collapse",this.onGuiChanged,this);
this.restrictToFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.restrictToFieldSet
},getRestrictToCatalogComboBox:function(){if(!this.restrictToCatalogComboBox){var catalogStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.home.page.site.all",idProperty:"id",fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){var anyRecord=new Ext.data.Record({id:"",name:Openwis.i18n("Common.List.Any")});
store.insert(0,[anyRecord])
}}});
this.restrictToCatalogComboBox=new Ext.form.ComboBox({store:catalogStore,valueField:"id",displayField:"name",name:"siteId",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:280})
}return this.restrictToCatalogComboBox
},getRestrictToCategoryComboBox:function(){if(!this.restrictToCategoryComboBox){var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.home.page.category.all",idProperty:"id",fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){var anyRecord=new Ext.data.Record({id:"",name:Openwis.i18n("Common.List.Any")});
store.insert(0,[anyRecord])
}}});
this.restrictToCategoryComboBox=new Ext.form.ComboBox({store:categoryStore,valueField:"id",displayField:"name",name:"category",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:280})
}return this.restrictToCategoryComboBox
},getRestrictToKindComboBox:function(){if(!this.restrictToKindComboBox){this.restrictToKindComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["metadata",Openwis.i18n("HomePage.Search.Criteria.RestrictTo.Kind.Metadata")],["template",Openwis.i18n("HomePage.Search.Criteria.RestrictTo.Kind.Template")]]}),valueField:"id",displayField:"value",name:"restrictToKind",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:280});
this.restrictToKindComboBox.setValue("metadata")
}return this.restrictToKindComboBox
},getInspireFieldSet:function(){if(!this.inspireFieldSet){this.inspireFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.Inspire"),layout:"table",layoutConfig:{columns:1},autoHeight:true,collapsed:true,collapsible:true,cls:"OInspCont"});
this.inspireFieldSet.addListener("collapse",this.onGuiChanged,this);
this.inspireFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.inspireFieldSet
},getOnlyInspireMetadataCheckbox:function(){if(!this.onlyInspireMetadataCheckbox){this.onlyInspireMetadataCheckbox=new Ext.form.Checkbox({name:"onlyInspireMetadata",checked:false,boxLabel:Openwis.i18n("HomePage.Search.Criteria.Inspire.InspireMetadataOnly"),ctCls:"OInspCHeck"})
}return this.onlyInspireMetadataCheckbox
},getInspireAnnexComboBox:function(){if(!this.inspireAnnexComboBox){this.inspireAnnexComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["i",Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex.I")],["ii",Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex.II")],["iii",Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex.III")]]}),valueField:"id",displayField:"value",value:"",name:"inspireAnnex",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.inspireAnnexComboBox
},getInspireSourceTypeComboBox:function(){if(!this.inspireSourceTypeComboBox){this.inspireSourceTypeComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["dataset",Openwis.i18n("HomePage.Search.Criteria.Inspire.SourceType.Dataset")],["service",Openwis.i18n("HomePage.Search.Criteria.Inspire.SourceType.Service")]]}),valueField:"id",displayField:"value",value:"",name:"inspireSourceType",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.inspireSourceTypeComboBox
},getInspireServiceTypeComboBox:function(){if(!this.inspireServiceTypeComboBox){this.inspireServiceTypeComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["ESRI:AIMS--http--configuration",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.ArcIMSAXL")],["ESRI:AIMS--http-get-feature",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.ArcIMSFMS")],["GLG:KML-2.0-http-get-map",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.GoogleEarthKMLV2")],["OGC:WCS-1.1.0-http-get-capabilities",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.OGCWCSV110")],["OGC:WFS-1.0.0-http-get-capabilities",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.OGCWFSV100")],["OGC:WMC-1.1.0-http-get-capabilities",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.OGCWMCV11")],["WWW:LINK-1.0-http--ical",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.iCalendar")],["WWW:LINK-1.0-http--link",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.WebAddress")],["WWW:LINK-1.0-http--partners",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.PartnerWebAddress")],["WWW:LINK-1.0-http--related",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.RelatedLink")],["WWW:LINK-1.0-http--rss",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.RSS")],["WWW:LINK-1.0-http--samples",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.ShowcaseProduct")]]}),valueField:"id",displayField:"value",value:"",name:"inspireServiceType",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.inspireServiceTypeComboBox
},reset:function(){this.getEitherTextField().reset();
this.getExactPhraseTextField().reset();
this.getWhatTextField().reset();
this.getWithoutTextField().reset();
this.getTitleTextField().reset();
this.getAbstractTextField().reset();
this.getKeywordsTextField().reset();
this.getWhatMapTypeCheckboxGroup().reset();
this.getWhatSearchAccuracyRadioGroup().reset();
this.getMapPanel().reset();
this.getWhereBoundsLatMinTextField().reset();
this.getWhereBoundsLongMinTextField().reset();
this.getWhereBoundsLatMaxTextField().reset();
this.getWhereBoundsLongMaxTextField().reset();
this.getWhereTypeCombobox().reset();
this.getRegionsCombobox().reset();
this.getWhenAnytimeRadio().reset();
this.getRestrictToCategoryComboBox().reset();
this.getRestrictToKindComboBox().reset();
this.getSortDirectionCombobox().reset();
this.getHitsCombobox().reset();
this.getOnlyInspireMetadataCheckbox().reset();
this.getInspireAnnexComboBox().reset();
this.getInspireSourceTypeComboBox().reset();
this.getInspireServiceTypeComboBox().reset()
},coordsChanged:function(){var latMin=parseFloat(this.getWhereBoundsLatMinTextField().getValue());
var longMin=parseFloat(this.getWhereBoundsLongMinTextField().getValue());
var latMax=parseFloat(this.getWhereBoundsLatMaxTextField().getValue());
var longMax=parseFloat(this.getWhereBoundsLongMaxTextField().getValue());
if(!(isNaN(latMin)||isNaN(latMax)||isNaN(longMin)||isNaN(longMax))){var latMin=Math.max(Math.min(latMin,90),-90);
var latMax=Math.max(Math.min(latMax,90),-90);
var longMin=this.getLongitude(longMin);
var longMax=this.getLongitude(longMax);
var bounds={};
bounds.bottom=latMin;
bounds.top=latMax;
bounds.left=longMin;
bounds.right=longMax;
this.getMapPanel().drawExtent(bounds);
this.getMapPanel().zoomToExtent(bounds);
this.updateMapFields(bounds,true)
}else{this.getMapPanel().reset()
}},updateMapFields:function(bounds,setRegionToUserDefined){this.getWhereBoundsLatMinTextField().setValue(bounds.bottom);
this.getWhereBoundsLatMaxTextField().setValue(bounds.top);
this.getWhereBoundsLongMinTextField().setValue(bounds.left);
this.getWhereBoundsLongMaxTextField().setValue(bounds.right);
if(bounds.bottom.constrain(-90,90)!=bounds.bottom){this.getWhereBoundsLatMinTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-90,to:90}))
}if(bounds.top.constrain(-90,90)!=bounds.top){this.getWhereBoundsLatMaxTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-90,to:90}))
}if(bounds.left.constrain(-180,180)!=bounds.left){this.getWhereBoundsLongMinTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-180,to:180}))
}if(bounds.right.constrain(-180,180)!=bounds.right){this.getWhereBoundsLongMaxTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-180,to:180}))
}if(setRegionToUserDefined){this.setRegionToUserDefined()
}if(bounds.left>bounds.right){this.getMapPanel().drawExtent(bounds);
this.getMapPanel().zoomToExtent(bounds)
}},buildSearchParams:function(){var params={};
params.all="";
params.region="";
params.kind="";
params.or="";
params.without="";
params.phrase="";
params.relation="";
params.extFrom="";
params.extTo="";
params.dateFrom="";
params.dateTo="";
params.category="";
params["abstract"]="";
params.similarity="";
params.siteId="";
params.title="";
params.digital="";
params.download="";
params.dynamic="";
params.paper="";
params.intermap="";
params.themekey="";
params.sortBy=this.getSortDirectionCombobox().getValue();
params.hitsPerPage=this.getHitsCombobox().getValue();
params.from=0;
params.to=parseInt(params.hitsPerPage)-1;
params.or=this.getEitherTextField().getValue();
params.phrase=this.getExactPhraseTextField().getValue();
params.all=this.getWhatTextField().getValue();
params.without=this.getWithoutTextField().getValue();
params.title=this.getTitleTextField().getValue();
params["abstract"]=this.getAbstractTextField().getValue();
params.similarity=this.getWhatSearchAccuracyRadioGroup().value/100;
if(this.getRegionsCombobox().getValue()!="Any"){params.relation=this.getWhereTypeCombobox().getValue();
var geometry=this.getMapPanel().getRawValue();
if(geometry){params.attrset="geo";
params.geometry=geometry
}else{params.attrset="";
params.geometry=""
}if(this.getRegionsCombobox().getValue()!="UserDefined"&&this.getRegionsCombobox().getValue()!=""){params.region=this.getRegionsCombobox().getValue()
}else{params.region=""
}}var mapTypes=this.getWhatMapTypeCheckboxGroup().getValue();
Ext.each(mapTypes,function(item){params[item.name]="on"
},this);
if(this.getWhenMetadataChangeDateRadio().checked){params.dateFrom=Openwis.Utils.Date.formatDateForServer(this.getWhenMetadataChangeDateFromDateField().getValue());
params.dateTo=Openwis.Utils.Date.formatDateForServer(this.getWhenMetadataChangeDateToDateField().getValue());
params.extFrom="";
params.extTo=""
}else{if(this.getWhenTemporalExtentRadio().checked){params.extFrom=Openwis.Utils.Date.formatDateForServer(this.getWhenTemporalExtentFromDateField().getValue());
params.extTo=Openwis.Utils.Date.formatDateForServer(this.getWhenTemporalExtentToDateField().getValue());
params.dateFrom="";
params.dateTo=""
}}params.category=this.getRestrictToCategoryComboBox().getValue();
params.kind=this.getRestrictToKindComboBox().getValue();
params.inspireOnly=this.getOnlyInspireMetadataCheckbox().getValue();
params.inspireAnnex=this.getInspireAnnexComboBox().getValue();
params.themekey=this.getKeywordsTextField().getValue();
params.protocol=this.getInspireServiceTypeComboBox().getValue();
return params
},searchUrl:function(){return configOptions.locService+"/main.search.embedded"
},validate:function(){if(this.getWhenMetadataChangeDateRadio().checked){return this.getWhenMetadataChangeDateFromDateField().isValid()&&this.getWhenMetadataChangeDateToDateField().isValid()
}else{if(this.getWhenTemporalExtentRadio().checked){return this.getWhenTemporalExtentFromDateField().isValid()&&this.getWhenTemporalExtentToDateField().isValid()
}}return true
}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.NormalSearchPanel=Ext.extend(Openwis.HomePage.Search.AbstractSearchPanel,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("HomePage.Search.Normal.Title")});
Openwis.HomePage.Search.NormalSearchPanel.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getWhatLabel());
this.add(this.getWhatTextField());
this.add(this.getWhereLabel());
this.add(this.getMapPanel());
this.add(this.getRegionsCombobox());
this.add(this.getOptionsPanel());
this.add(this.getButtonsPanel())
},getWhatTextField:function(){if(!this.whatTextField){this.whatTextField=new Ext.form.TextField({name:"what",allowBlank:true,width:278,cls:"textField",listeners:{specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this},render:function(){Ext.form.TextField.superclass.render.apply(this,arguments);
this.tTip=new Ext.ToolTip({target:this.el,width:120,html:Openwis.i18n("HomePage.Search.Normal.What.ToolTip"),dismissDelay:5000})
}})
}return this.whatTextField
},reset:function(){this.getWhatTextField().reset();
this.getMapPanel().reset();
this.getRegionsCombobox().reset();
this.getSortDirectionCombobox().reset();
this.getHitsCombobox().reset()
},buildSearchParams:function(){var params={};
params.any=this.getWhatTextField().getValue();
params.sortBy=this.getSortDirectionCombobox().getValue();
params.hitsPerPage=this.getHitsCombobox().getValue();
params.from=0;
params.to=parseInt(params.hitsPerPage)-1;
params.similarity="0.8";
if(this.getRegionsCombobox().getValue()!="Any"){params.relation="iswithin";
var geometry=this.getMapPanel().getRawValue();
if(geometry){params.attrset="geo";
params.geometry=geometry
}else{params.geometry="";
params.attrset="normal"
}if(this.getRegionsCombobox().getValue()!="UserDefined"&&this.getRegionsCombobox().getValue()!=""){params.region=this.getRegionsCombobox().getValue()
}else{params.region=""
}}else{params.relation=""
}params.all="";
params.region="";
params.kind="metadata";
params.or="";
params.without="";
params.phrase="";
params.extFrom="";
params.extTo="";
params.dateFrom="";
params.dateTo="";
params.category="";
params["abstract"]="";
params.siteId="";
params.title="";
params.digital="";
params.download="";
params.dynamic="";
params.paper="";
params.intermap="";
params.themekey="";
params.inspireOnly="";
params.inspireAnnex="";
params.protocol="";
return params
},searchUrl:function(){return configOptions.locService+"/main.search.embedded"
},updateMapFields:function(bounds,setRegionToUserDefined){if(setRegionToUserDefined){this.setRegionToUserDefined()
}if(bounds.left>bounds.right){this.getMapPanel().drawExtent(bounds);
this.getMapPanel().zoomToExtent(bounds)
}},validate:function(){return true
}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.WhatsNewPanel=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{border:false,bodyStyle:"padding:20px",cls:"homePageMenuPanelCls",width:320});
Openwis.HomePage.Search.WhatsNewPanel.superclass.initComponent.apply(this,arguments);
this.initialize();
this.getInfosAndInitialize()
},initialize:function(){this.add(new Ext.Container({cls:"top title",height:19,html:"<i class='iconIOS7-bt_news_off'></i>"+Openwis.i18n("HomePage.Search.WhatsNew.Title")}));
this.add(this.getContentPanel());
this.add(new Ext.Container({cls:"bottom",height:19}))
},getContentPanel:function(){if(!this.contentPanel){this.contentPanel=new Ext.Container({cls:"content"})
}return this.contentPanel
},refreshContent:function(productMetadatas){var productMetadatasSize=productMetadatas.length;
var existingProductMetadatas=[];
if(!this.getContentPanel().items){var task=new Ext.util.DelayedTask(this.getInfosAndInitialize,this);
task.delay(5000);
return
}if(this.getContentPanel().items){for(var i=0;
i<this.getContentPanel().items.length;
i++){existingProductMetadatas.push(this.getContentPanel().get(i).id)
}}for(var i=0;
i<productMetadatas.length;
i++){var id="whats-new-"+productMetadatas[i].id;
if(!this.getContentPanel().findById(id)){var htmlContent="<a>"+productMetadatas[i].title+"</a>";
var container=new Ext.Container({cls:"line",style:"cursor:pointer;",id:id,html:htmlContent});
this.getContentPanel().insert(i,container)
}else{existingProductMetadatas.remove(id)
}}Ext.each(existingProductMetadatas,function(id){this.getContentPanel().remove(id)
},this);
this.doLayout();
this.fireEvent("sizeChanged");
for(var i=0;
i<productMetadatas.length;
i++){var id="whats-new-"+productMetadatas[i].id;
var el=Ext.get(id);
if(!el){continue
}el.on("click",function(evt,el,o){this.fireEvent("metadataClicked",o.productMetadata)
},this,{productMetadata:productMetadatas[i]})
}var task=new Ext.util.DelayedTask(this.getInfosAndInitialize,this);
task.delay(600000)
},getInfosAndInitialize:function(){new Openwis.Handler.GetWithoutError({url:configOptions.locService+"/xml.get.home.page.whats.new",maskEl:this.getContentPanel().el,useLoadMask:(this.getContentPanel().el!=undefined),listeners:{success:function(productMetadatas){this.refreshContent(productMetadatas)
},scope:this}}).proceed()
}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.LastProductsPanel=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{border:false,bodyStyle:"padding:20px",cls:"homePageMenuPanelCls",width:320});
Openwis.HomePage.Search.LastProductsPanel.superclass.initComponent.apply(this,arguments);
this.initialize();
if(g_userConnected){this.getInfosAndInitialize()
}},initialize:function(){this.add(new Ext.Container({cls:"top title",height:19,html:Openwis.i18n("HomePage.Search.LastProducts.Title")}));
this.add(this.getContentPanel());
this.add(new Ext.Container({cls:"bottom",height:19}))
},getContentPanel:function(){if(!this.contentPanel){this.contentPanel=new Ext.Container({cls:"content"})
}return this.contentPanel
},refreshContent:function(processedRequests){var processedRequestsSize=processedRequests.length;
var existingLastProducts=[];
for(var i=0;
i<this.getContentPanel().items.length;
i++){existingLastProducts.push(this.getContentPanel().get(i).id)
}for(var i=0;
i<processedRequests.length;
i++){var lastProductDto=processedRequests[i];
var id="last-product-"+lastProductDto.id;
if(!this.getContentPanel().findById(id)){var content=Openwis.Utils.Date.formatDateTimeUTC(lastProductDto.date)+" - "+lastProductDto.name;
this.getContentPanel().insert(i,new Ext.Container({cls:"line",style:"cursor:pointer;",id:id,html:content}))
}else{existingLastProducts.remove(id)
}}Ext.each(existingLastProducts,function(id){this.getContentPanel().remove(id)
},this);
var newSize=this.getContentPanel().items.length;
this.doLayout();
this.fireEvent("sizeChanged");
for(var i=0;
i<processedRequests.length;
i++){var id="last-product-"+processedRequests[i].id;
var el=Ext.get(id);
if(!el){continue
}el.on("click",function(evt,el,o){this.fireEvent("productClicked",o.processedRequest)
},this,{processedRequest:processedRequests[i]})
}var task=new Ext.util.DelayedTask(this.getInfosAndInitialize,this);
task.delay(600000)
},getInfosAndInitialize:function(){new Openwis.Handler.GetWithoutError({url:configOptions.locService+"/xml.get.home.page.last.products",maskEl:this.getContentPanel().el,useLoadMask:(this.getContentPanel().el!=undefined),listeners:{success:function(processedRequests){this.refreshContent(processedRequests)
},scope:this}}).proceed()
}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.SearchPanel=Ext.extend(Ext.TabPanel,{initComponent:function(){Ext.apply(this,{border:false,width:320,activeTab:((showAdvancedSearchFirst)?1:0),bodyStyle:"padding:20px",cls:"tabSearchType",listeners:{afterrender:function(){this.addListener("tabchange",function(){this.fireEvent("guiChanged",false,true)
},this)
},scope:this}});
Openwis.HomePage.Search.SearchPanel.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getNormalSearchPanel());
this.add(this.getAdvancedSearchPanel())
},getNormalSearchPanel:function(){if(!this.normalSearchPanel){this.normalSearchPanel=new Openwis.HomePage.Search.NormalSearchPanel({targetResult:this.targetResult,listeners:{searchResultsDisplayed:function(){this.fireEvent("searchResultsDisplayed")
},guiChanged:function(){if(pageLoaded&&this.normalSearchPanel.isLoaded()&&this.normalSearchPanel.getMapPanel().mapLoaded){this.fireEvent("guiChanged")
}},scope:this}})
}return this.normalSearchPanel
},getAdvancedSearchPanel:function(){if(!this.advancedSearchPanel){this.advancedSearchPanel=new Openwis.HomePage.Search.AdvancedSearchPanel({targetResult:this.targetResult,listeners:{searchResultsDisplayed:function(){this.fireEvent("searchResultsDisplayed")
},guiChanged:function(){if(pageLoaded&&this.advancedSearchPanel.isLoaded()&&this.advancedSearchPanel.getMapPanel().mapLoaded){this.fireEvent("guiChanged")
}},scope:this}})
}return this.advancedSearchPanel
}});Ext.ns("Openwis.HomePage.Search");
Openwis.HomePage.Search.SearchResultsPanel=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{region:"center",border:false,boxMinHeight:400,html:"<div class='mainContentText'>"+Openwis.i18n("HomePage.Main.Content")+"</div>"});
Openwis.HomePage.Search.SearchResultsPanel.superclass.initComponent.apply(this,arguments)
},loadSearchResults:function(url,params){var getHandler=new Openwis.Handler.GetNoJson({url:url,params:params,useLoadMask:false,useHTMLMask:true,loadingMessage:Openwis.i18n("HomePage.Search.Loading"),maskEl:this,listeners:{success:function(result){this.body.dom.innerHTML=result;
this.fireEvent("searchResultsDisplayed")
},scope:this}});
getHandler.proceed()
}});function showRelatedServicesPanel(id,uuid){var divElt=document.getElementById("serviceElt"+id);
if(divElt.style.display=="none"){divElt.style.display="block";
var posBtn=document.getElementById("services"+id).positionedOffset();
divElt.style.top=30+posBtn.top+"px";
divElt.style.left=posBtn.left+"px";
divElt.style.width="280px"
}else{divElt.style.display="none"
}if(false){new Openwis.Handler.GetWithoutError({url:configOptions.locService+"/xml.relatedservices.search",params:{uuid:uuid},listeners:{success:function(res){var metadataList=res.metadataList;
var divElt=document.getElementById("serviceElt"+id);
divElt.innerHTML="";
for(var i=0;
i<metadataList.length;
i++){var md=metadataList[i];
var aElt=document.createElement("a");
aElt.id="id_lien";
aElt.href="#";
aElt.innerHTML=rsp_getTitle(md);
var divMd=document.createElement("div");
divMd.className="otherActionTarget";
divMd.appendChild(aElt);
divElt.appendChild(divMd)
}divElt.style.display="block";
var posBtn=document.getElementById("services"+id).positionedOffset();
divElt.style.top=30+posBtn.top+"px";
divElt.style.left=posBtn.left+"px";
divElt.style.width="280px"
},scope:this}}).proceed()
}}function rsp_getTitle(md){var maxLength=50;
if(md.title.length<maxLength){return md.title
}return md.title.substring(0,maxLength)
};Ext.ns("Openwis.HomePage");
Openwis.HomePage.Viewport=Ext.extend(Ext.Viewport,{initComponent:function(){Ext.apply(this,{border:false,autoScroll:true,layout:"fit",listeners:{afterlayout:function(){this.relayoutViewport(true,true)
},scope:this}});
Openwis.HomePage.Viewport.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.getViewportPanel().add(this.getWestPanel());
this.getCenterPanel().add(this.getHeaderPanel());
this.getCenterPanel().add(this.getHeaderImage());
this.getContentPanel().add(this.getContentWestPanel());
this.getContentPanel().add(this.getContentCenterPanel());
this.getCenterPanel().add(this.getContentPanel());
this.getViewportPanel().add(this.getCenterPanel());
this.getViewportPanel().add(this.getEastPanel());
this.add(this.getViewportPanel())
},getViewportPanel:function(){if(!this.viewportPanel){this.viewportPanel=new Ext.Panel({layout:"border",border:false,autoScroll:false,cls:"viewportCls"})
}return this.viewportPanel
},getSearchPanel:function(){if(!this.searchPanel){this.searchPanel=new Openwis.HomePage.Search.SearchPanel({targetResult:this.getSearchResultsPanel(),listeners:{guiChanged:function(){this.relayoutViewport(false,true)
},searchResultsDisplayed:function(){this.relayoutViewport(true,true)
},scope:this}})
}return this.searchPanel
},getWhatsNewPanel:function(){if(!this.whatsNewPanel){this.whatsNewPanel=new Openwis.HomePage.Search.WhatsNewPanel({searchResultsPanel:this.getSearchResultsPanel(),listeners:{sizeChanged:function(){this.relayoutViewport(false,true)
},metadataClicked:function(productMetadata){this.doSearch(productMetadata)
},scope:this}})
}return this.whatsNewPanel
},doSearch:function(productMetadata){var params={};
params.any=productMetadata.urn;
params.sortBy="relevance";
params.geometry=null;
params.title=null;
params["abstract"]=null;
params.themekey=null;
params.hitsPerPage=10;
params.from=0;
params.to=9;
params.relation="within";
var url=configOptions.locService+"/main.search.embedded";
this.getSearchResultsPanel().load(url,params)
},getLastProductsPanel:function(){if(!this.lastProductsPanel){this.lastProductsPanel=new Openwis.HomePage.Search.LastProductsPanel({listeners:{sizeChanged:function(){this.relayoutViewport(false,true)
},productClicked:function(lastProduct){this.viewLastProduct(lastProduct)
},scope:this}});
this.lastProductsPanel.setVisible(g_userConnected)
}return this.lastProductsPanel
},viewLastProduct:function(lastProduct){var requestType=lastProduct.requestType;
if(requestType=="ADHOC"){var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(lastProduct.productMetadataURN,"ADHOC",lastProduct.extractMode=="CACHE","View",lastProduct.requestId,false)
}else{if(requestType=="SUBSCRIPTION"){var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(lastProduct.productMetadataURN,"SUBSCRIPTION",lastProduct.extractMode=="CACHE","Edit",lastProduct.requestId,false)
}}},getSearchResultsPanel:function(){if(!this.searchResultsPanel){this.searchResultsPanel=new Openwis.HomePage.Search.SearchResultsPanel({listeners:{searchResultsDisplayed:function(){this.relayoutViewport(true,true)
},scope:this},cls:"searchResPan"})
}return this.searchResultsPanel
},getContentCenterHeaderPanel:function(){if(!this.contentCenterHeaderPanel){this.contentCenterHeaderPanel=new Ext.Panel({region:"north",border:false,boxMaxHeight:20,cls:"homePageMainContentHeader",html:Openwis.i18n("HomePage.Main.Header")})
}return this.contentCenterHeaderPanel
},getContentWestPanel:function(){if(!this.contentWestPanel){this.contentWestPanel=new Ext.Panel({region:"west",layout:"table",width:322,border:false,cls:"westContentPanel",layoutConfig:{columns:1},defaults:{style:{width:"322px"}},items:[this.getSearchPanel(),this.getWhatsNewPanel(),this.getLastProductsPanel()]})
}return this.contentWestPanel
},getContentCenterPanel:function(){if(!this.contentCenterPanel){this.contentCenterPanel=new Ext.Panel({region:"center",border:false,cls:"homePageMainContent",bodyCls:"homePageMainContentB",items:[this.getContentCenterHeaderPanel(),this.getSearchResultsPanel()]})
}return this.contentCenterPanel
},getContentPanel:function(){if(!this.contentPanel){this.contentPanel=new Ext.Panel({region:"center",autoScroll:true,border:false,layout:"border",bodyCssClass:"contentPanelCls"})
}return this.contentPanel
},getHeaderPanel:function(){if(!this.headerPanel){this.headerPanel=new Ext.Container({region:"north",border:false,contentEl:"header",cls:"headerCtCls",height:"auto"})
}return this.headerPanel
},getHeaderImage:function(){if(!this.headerImage){this.headerImage={tag:"div",cls:"header_img SearchPage",id:"header_img",html:"<a onclick='redirectWarning()' style=\"text-decoration:none;\" href=\"http://asmc.asean.org/asmc-about/\"><div id='urlTitleCont'><div id='urlTitle'>About <strong>ASMC</strong></div><div id='urlSubTitle'>"+Openwis.i18n("HomePage.Main.Header")+"</div></div></a>"}
}return this.headerImage
},getCenterPanel:function(){if(!this.centerPanel){this.centerPanel=new Ext.Panel({cls:"body-center-panel",region:"center",border:false,width:992,layout:"border",id:"bodyCenPan"})
}return this.centerPanel
},getWestPanel:function(){if(!this.westPanel){this.westPanel=new Ext.Container({cls:"body-west-panel",region:"west",border:false,html:"&nbsp;",})
}return this.westPanel
},getEastPanel:function(){if(!this.eastPanel){this.eastPanel=new Ext.Container({cls:"body-east-panel",region:"east",border:false,html:"&nbsp;"})
}return this.eastPanel
},relayoutViewport:function(relayoutWidth,relayoutHeight){this.suspendEvents();
if(relayoutWidth){var contentWidth=993;
var size=this.getEl().getViewSize(),w=size.width;
var westP=this.getWestPanel();
var eastP=this.getEastPanel();
var centerP=this.getCenterPanel();
if(w<contentWidth){westP.setWidth(0);
eastP.setWidth(0)
}else{var panelSideWidth=(w-contentWidth)/2;
westP.setWidth(panelSideWidth-8);
eastP.setWidth(panelSideWidth-9)
}this.getViewportPanel().boxMaxWidth=w-17;
this.doLayout()
}if(relayoutHeight){var leftPanelHeight=this.getSearchPanel().getHeight()+this.getWhatsNewPanel().getHeight()+this.getLastProductsPanel().getHeight()+75;
var contentPanelHeight=this.getSearchResultsPanel().getHeight()+350;
var compMinHeight=(leftPanelHeight>contentPanelHeight)?leftPanelHeight:contentPanelHeight;
compMinHeight+=550;
this.getViewportPanel().boxMinHeight=compMinHeight;
this.doLayout()
}this.resumeEvents()
}});var homePageViewport;
Ext.onReady(function(){Ext.QuickTips.init();
homePageViewport=new Openwis.HomePage.Viewport();
doMenuSearch(homePageViewport);
if(remoteSearch.url){var params={};
if(!remoteSearch.connection){params.any=remoteSearch.urn;
params.sortBy="relevance";
params.hitsPerPage=10;
params.relation="overlaps";
params.permanentLink=true
}homePageViewport.getSearchResultsPanel().loadSearchResults(remoteSearch.url,params);
if(remoteSearch.type=="ADHOC"){doAdhocRequest(remoteSearch.urn)
}else{if(remoteSearch.type=="SUBSCRIPTION"){if(!remoteSearch.backupRequestId){doSubscription(remoteSearch.urn)
}else{doSubscriptionFromCache(remoteSearch.urn,null,remoteSearch.backupRequestId,remoteSearch.backupDeployment)
}}}}});
function doMenuSearch(searchObj){const urlParams=new URLSearchParams(window.location.search);
const productKey=urlParams.get("productKey");
if(productKey!==null){var product={urn:getSearchKey(productKey)};
searchObj.getSearchPanel().getNormalSearchPanel().whatTextField.setValue(getSearchKey(productKey));
searchObj.doSearch(product)
}}function getParameterByName(name,url){if(!url){url=window.location.href
}name=name.replace(/[\[\]]/g,"\\$&");
var regex=new RegExp("[?&]"+name+"(=([^&#]*)|&|#|$)"),results=regex.exec(url);
if(!results){return null
}if(!results[2]){return""
}return decodeURIComponent(results[2].replace(/\+/g," "))
}function getSearchKey(productKey){const searchKeys={noaa20:"noaa-20 image",suomiNpp:"suomi npp image",aqua:"Satellite Imagery AQUA",terra:"Satellite Imagery TERRA",haze:"haze_map",hotspotNoaa20:"noaa20 record",hotspotNpp:"npp record",hotspotAqua:"AQUA_hotspot_location",hotspotTerra:"TERRA_hotspot_location",hazeDispersion:"haze_dispersion",};
return searchKeys[productKey]
};