Ext.ns("Openwis.Common.Metadata");
Openwis.Common.Metadata.Create=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Common.Metadata.Create.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.metadata.create.form",params:{},listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.getCreateMetadataFormPanel().add(this.getUrnInfoFormPanel());
this.getCreateMetadataFormPanel().add(this.getTemplatesComboBox());
this.getCreateMetadataFormPanel().add(this.getCategoriesComboBox());
this.add(this.getCreateMetadataFormPanel());
this.doLayout()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("MetadataCreate.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getCreateMetadataFormPanel:function(){if(!this.createMetadataFormPanel){this.createMetadataFormPanel=new Ext.form.FormPanel({itemCls:"formItems",border:false,buttons:[{text:Openwis.i18n("Common.Btn.Create"),handler:function(btn,e){if(this.getCreateMetadataFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.metadata.create",params:this.getMetadataCreationInfos(),listeners:{success:function(pm){doEditMetadataByUrn(pm.urn,pm.title)
},scope:this}});
saveHandler.proceed()
}},scope:this}]})
}return this.createMetadataFormPanel
},getUrnInfoFormPanel:function(){if(!this.urnInfoFormPanel){this.urnInfoFormPanel=new Ext.Panel({fieldLabel:Openwis.i18n("MetadataCreate.URN"),border:false});
this.urnInfoFormPanel.add(this.getUrnAuthLabel());
this.urnInfoFormPanel.add(this.getUrnAuthTextField());
this.urnInfoFormPanel.add(this.getUrnIdLabel());
this.urnInfoFormPanel.add(this.getUrnIdTextField())
}return this.urnInfoFormPanel
},getUrnAuthLabel:function(){if(!this.urnAuthLabel){this.urnAuthLabel=new Ext.form.Label({text:Openwis.i18n("MetadataCreate.URN.Prefix")})
}return this.urnAuthLabel
},getUrnIdLabel:function(){if(!this.urnIdLabel){this.urnIdLabel=new Ext.form.Label({text:Openwis.i18n("MetadataCreate.URN.Middle")})
}return this.urnIdLabel
},getUrnAuthTextField:function(){if(!this.urnAuthTextField){this.urnAuthTextField=new Ext.form.TextField({name:"urnAuth",allowBlank:false,width:120,emptyText:Openwis.i18n("MetadataCreate.Authority")})
}return this.urnAuthTextField
},getUrnIdTextField:function(){if(!this.urnIdTextField){this.urnIdTextField=new Ext.form.TextField({name:"urnId",allowBlank:false,width:120,emptyText:Openwis.i18n("MetadataCreate.Id")})
}return this.urnIdTextField
},getDataPoliciesComboBox:function(){if(!this.dataPoliciesComboBox){var dataPoliciesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.dataPoliciesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataCreate.Data.Policy"),name:"dataPolicy",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:dataPoliciesStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"});
this.dataPoliciesComboBox.getStore().loadData(this.config.dataPolicies)
}return this.dataPoliciesComboBox
},getTemplatesComboBox:function(){var templatesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"title"}]});
if(!this.templatesComboBox){this.templatesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataCreate.Template"),name:"template",store:templatesStore,mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,editable:false,allowBlank:false,width:330,displayField:"title",valueField:"id"});
this.templatesComboBox.getStore().loadData(this.config.templates)
}return this.templatesComboBox
},getCategoriesComboBox:function(){if(!this.categoriesComboBox){var categoriesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.categoriesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataCreate.Category"),name:"categories",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:categoriesStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"});
this.categoriesComboBox.getStore().loadData(this.config.categories)
}return this.categoriesComboBox
},getMetadataCreationInfos:function(){var metadata={};
metadata.uuid=Openwis.i18n("MetadataCreate.URN.Prefix")+this.getUrnAuthTextField().getValue()+Openwis.i18n("MetadataCreate.URN.Middle")+this.getUrnIdTextField().getValue();
metadata.template=this.getTemplatesComboBox().getStore().getById(this.getTemplatesComboBox().getValue()).data;
metadata.category=this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data;
return metadata
}});Ext.ns("Openwis.Common.Metadata");
Openwis.Common.Metadata.Insert=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Common.Metadata.Insert.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.metadata.insert.form",params:{},listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.add(this.getUploadForm());
this.doLayout()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("MetadataInsert.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getUploadForm:function(){var the=this;
if(!this.uploadForm){var newFile=new Ext.ux.form.FileUploadField({xtype:"fileuploadfield",allowBlank:false,buttonCfg:{text:Openwis.i18n("Common.Btn.Browse")},fieldLabel:Openwis.i18n("MetadataInsert.Metadata"),width:360});
this.getFileUploadArray().push(newFile);
this.getUploadTablePanel().add(new Ext.form.Label({text:Openwis.i18n("MetadataInsert.Metadata")+":"}));
this.getUploadTablePanel().add(newFile);
this.getUploadTablePanel().add(new Ext.form.Label({text:""}));
this.uploadForm=new Ext.FormPanel({itemCls:"formItems",fileUpload:true,border:false,errorReader:new Ext.data.XmlReader({record:"field",success:"@success"},["id","msg"]),items:[this.getUploadTablePanel(),new Ext.Button(this.getNewAction()),this.getFileTypeRadioGroup(),this.getStyleSheetComboBox(),this.getValidationCombobox(),this.getCategoriesComboBox()],buttons:[{text:Openwis.i18n("Common.Btn.Insert"),scope:the,handler:function(){if(this.uploadForm.getForm().isValid()){this.uploadForm.getForm().submit({url:configOptions.locService+"/xml.metadata.insert.upload",scope:this,params:this.getMetadataInsertInfos(),success:function(fp,action){var jsonData=fp.errorReader.xmlData.getElementsByTagName("message")[0].childNodes[0].nodeValue;
var result=Ext.decode(jsonData);
new Openwis.Common.Metadata.Report({lastResult:result})
},failure:function(response){Openwis.Utils.MessageBox.displayInternalError()
}})
}}}]})
}return this.uploadForm
},getUploadTablePanel:function(){if(!this.uploadTablePanel){this.uploadTablePanel=new Ext.Panel({layout:"table",layoutConfig:{columns:3},defaults:{style:{width:"100%"}},border:false})
}return this.uploadTablePanel
},getNewAction:function(){if(!this.newAction){this.newAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),scope:this,handler:function(){if(this.getFileUploadArray().size()<Openwis.Conf.UPLOAD_SIZE){var newFile=new Ext.ux.form.FileUploadField({xtype:"fileuploadfield",allowBlank:false,fieldLabel:Openwis.i18n("MetadataInsert.Metadata"),width:360});
var metadataLabel=new Ext.form.Label({text:Openwis.i18n("MetadataInsert.Metadata")+":"});
this.getFileUploadArray().push(newFile);
this.getUploadTablePanel().add(metadataLabel);
this.getUploadTablePanel().add(newFile);
var newRemoveBtn=new Ext.Button(new Ext.Action({iconCls:"icon-discard-fileUpload",scope:this,handler:function(){this.getUploadTablePanel().remove(newFile);
this.getUploadTablePanel().remove(newRemoveBtn);
this.getFileUploadArray().remove(newFile);
this.getUploadTablePanel().remove(metadataLabel);
this.doLayout()
}}));
this.getUploadTablePanel().add(newRemoveBtn);
this.doLayout()
}}})
}return this.newAction
},getFileUploadArray:function(){if(!this.fileUploadArray){this.fileUploadArray=new Array()
}return this.fileUploadArray
},getFileTypeRadioGroup:function(){if(!this.fileTypeRadioGroup){this.fileTypeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("MetadataInsert.FileType"),items:[this.getSingleFileRadio(),this.getMefFileRadio()]})
}return this.fileTypeRadioGroup
},getSingleFileRadio:function(){if(!this.singleFileRadio){this.singleFileRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("MetadataInsert.FileType.SingleFile"),name:"fileType",inputValue:"single",checked:true})
}return this.singleFileRadio
},getMefFileRadio:function(){if(!this.mefFileRadio){this.mefFileRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("MetadataInsert.FileType.MefFile"),name:"fileType",inputValue:"mef"})
}return this.mefFileRadio
},getStyleSheetComboBox:function(){if(!this.styleSheetComboBox){var styleSheetStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.styleSheetComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataInsert.StyleSheet"),name:"stylesheetName",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:styleSheetStore,editable:false,allowBlank:false,width:330,value:"NONE",displayField:"name",valueField:"id"});
this.styleSheetComboBox.getStore().loadData(this.config.styleSheet);
this.styleSheetComboBox.getStore().insert(0,[new Ext.data.Record({id:"NONE",name:Openwis.i18n("Common.List.None")})]);
this.styleSheetComboBox.setValue("NONE")
}return this.styleSheetComboBox
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationModeCombobox",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("MetadataInsert.Validate")})
}return this.validationCombobox
},getCategoriesComboBox:function(){if(!this.categoriesComboBox){var categoriesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.categoriesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataInsert.Category"),name:"categoryCombobox",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:categoriesStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"});
this.categoriesComboBox.getStore().loadData(this.config.categories)
}return this.categoriesComboBox
},getMetadataInsertInfos:function(){var metadataInsert={};
var files=new Array();
Ext.each(this.getFileUploadArray(),function(fileUpload,index){files.push(fileUpload.getValue())
});
metadataInsert.files=files;
metadataInsert.fileType=this.getFileTypeRadioGroup().getValue().inputValue;
if(this.getStyleSheetComboBox().getValue()=="NONE"){metadataInsert.stylesheet=null
}else{metadataInsert.stylesheet=this.getStyleSheetComboBox().getValue()
}metadataInsert.validationMode=this.getValidationCombobox().getValue();
metadataInsert.categoryId=this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data.id;
metadataInsert.categoryName=this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data.name;
return metadataInsert
}});Ext.ns("Openwis.Common.Metadata.Report");
Openwis.Common.Metadata.Report=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Metadata.Report"),layout:"fit",width:720,height:480,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Common.Metadata.Report.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){if(this.lastResult!=null&&this.lastResult!=""){this.initialize()
}else{Openwis.Utils.MessageBox.displaySuccessMsg("No Report available.",this.fireSuccessEvent,this)
}},initialize:function(){this.add(this.getReportFormPanel());
this.addButton(new Ext.Button(this.getCancelAction()));
this.getDateDisplayField().setValue(Openwis.Utils.Date.formatDateTimeUTCfromLong(this.lastResult.date));
this.getTotalDisplayField().setValue(this.lastResult.total);
if(this.lastResult.fail){this.getFailDisplayField().setValue("Task Failed")
}this.getAddedDisplayField().setValue(this.lastResult.added);
this.getUpdatedDisplayField().setValue(this.lastResult.updated);
this.getUnchangedDisplayField().setValue(this.lastResult.unchanged);
this.getLocallyRemovedDisplayField().setValue(this.lastResult.locallyRemoved);
this.getUnknownSchemaDisplayField().setValue(this.lastResult.unknownSchema);
this.getUnexpectedDisplayField().setValue(this.lastResult.unexpected);
this.getBadFormatDisplayField().setValue(this.lastResult.badFormat);
this.getDoesNotValidateDisplayField().setValue(this.lastResult.doesNotValidate);
this.getIgnoredDisplayField().setValue(this.lastResult.ignored);
this.show()
},getReportFormPanel:function(){if(!this.reportFormPanel){this.reportFormPanel=new Ext.form.FormPanel({itemCls:"formItems",width:600,border:false,items:[{layout:"table",border:false,layoutConfig:{columns:4},defaults:{bodyStyle:"padding:0 18px 0 0"},items:[this.getFailDisplayField(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),this.createLabel(Openwis.i18n("Metadata.Report.Date")),this.getDateDisplayField(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),new Openwis.Utils.Misc.createDummy(),this.createLabel(Openwis.i18n("Metadata.Report.Total")),this.getTotalDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.UnknownSchema")),this.getUnknownSchemaDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.Added")),this.getAddedDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.Unexpected")),this.getUnexpectedDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.Updated")),this.getUpdatedDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.BadFormat")),this.getBadFormatDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.LocallyRemoved")),this.getLocallyRemovedDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.DoesNotValidate")),this.getDoesNotValidateDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.Unchanged")),this.getUnchangedDisplayField(),this.createLabel(Openwis.i18n("Metadata.Report.Ignored")),this.getIgnoredDisplayField()]},this.addDownloadReportButton(),new Ext.Container({html:Openwis.i18n("Metadata.Report.Error.Info"),border:false,cls:"infoMsg",style:{margin:"0px 0px 5px 0px"}}),this.getReportErrorGrid()]})
}return this.reportFormPanel
},createLabel:function(label){return new Ext.Container({border:false,width:200,html:label+": ",style:{padding:"5px"}})
},getDateDisplayField:function(){if(!this.dateDisplayField){this.dateDisplayField=new Ext.form.DisplayField({name:"date"})
}return this.dateDisplayField
},getTotalDisplayField:function(){if(!this.totalDisplayField){this.totalDisplayField=new Ext.form.DisplayField({name:"total"})
}return this.totalDisplayField
},getFailDisplayField:function(){if(!this.failDisplayField){this.failDisplayField=new Ext.form.DisplayField({hideLabel:true,cls:"errorMsg",name:"fail"})
}return this.failDisplayField
},getAddedDisplayField:function(){if(!this.addedDisplayField){this.addedDisplayField=new Ext.form.DisplayField({name:"added"})
}return this.addedDisplayField
},getUpdatedDisplayField:function(){if(!this.updatedDisplayField){this.updatedDisplayField=new Ext.form.DisplayField({name:"updated"})
}return this.updatedDisplayField
},getUnchangedDisplayField:function(){if(!this.unchangedDisplayField){this.unchangedDisplayField=new Ext.form.DisplayField({name:"unchanged"})
}return this.unchangedDisplayField
},getLocallyRemovedDisplayField:function(){if(!this.locallyRemovedDisplayField){this.locallyRemovedDisplayField=new Ext.form.DisplayField({name:"locallyRemoved"})
}return this.locallyRemovedDisplayField
},getUnknownSchemaDisplayField:function(){if(!this.unknownSchemaDisplayField){this.unknownSchemaDisplayField=new Ext.form.DisplayField({name:"unknownSchema"})
}return this.unknownSchemaDisplayField
},getUnexpectedDisplayField:function(){if(!this.unexpectedDisplayField){this.unexpectedDisplayField=new Ext.form.DisplayField({name:"unexpected"})
}return this.unexpectedDisplayField
},getBadFormatDisplayField:function(){if(!this.badFormatDisplayField){this.badFormatDisplayField=new Ext.form.DisplayField({name:"badFormat"})
}return this.badFormatDisplayField
},getDoesNotValidateDisplayField:function(){if(!this.doesNotValidateDisplayField){this.doesNotValidateDisplayField=new Ext.form.DisplayField({name:"doesNotValidate"})
}return this.doesNotValidateDisplayField
},getIgnoredDisplayField:function(){if(!this.ignoredDisplayField){this.ignoredDisplayField=new Ext.form.DisplayField({name:"ignored"})
}return this.ignoredDisplayField
},getReportErrorGrid:function(){if(!this.reportErrorGrid){var reportErrorStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"urn",fields:[{name:"urn"},{name:"message"}]});
this.reportErrorGrid=new Ext.grid.GridPanel({id:"reportErrorGrid",height:200,width:700,border:true,store:reportErrorStore,loadMask:true,columns:[{id:"urn",header:Openwis.i18n("Metadata.Report.Error.Urn"),dataIndex:"urn",sortable:true,width:150},{id:"error",header:Openwis.i18n("Metadata.Report.Error.Name"),dataIndex:"message",renderer:Openwis.Utils.Tooltip.Display,sortable:true,width:150}],autoExpandColumn:"error"});
this.reportErrorGrid.getStore().loadData(this.lastResult.errors)
}return this.reportErrorGrid
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Close"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},addDownloadReportButton:function(){if(this.harvestingTaskId){return this.addButton(new Ext.Button(this.getReportAction()))
}return new Ext.Container({border:false,width:10,html:"",style:{padding:"2px"}})
},getReportAction:function(){if(!this.reportAction){this.reportAction=new Ext.Action({disabled:false,text:Openwis.i18n("Metadata.Report.Download.Last.Report"),scope:this,handler:function(){window.location.href=configOptions.locService+"/xml.harvesting.last.report.file?id="+this.harvestingTaskId
}})
}return this.reportAction
}});Ext.ns("Openwis.Common.Metadata");
Openwis.Common.Metadata.BatchImport=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Common.Metadata.BatchImport.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.metadata.batchimport.form",params:{},listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.getBatchImportMetadataFormPanel().add(this.getMetadataDirectoryTextField());
this.getBatchImportMetadataFormPanel().add(this.getFileTypeRadioGroup());
this.getBatchImportMetadataFormPanel().add(this.getStyleSheetComboBox());
this.getBatchImportMetadataFormPanel().add(this.getValidationCombobox());
this.getBatchImportMetadataFormPanel().add(this.getCategoriesComboBox());
this.add(this.getBatchImportMetadataFormPanel());
this.doLayout()
},initializeRes:function(){this.add(this.getHeader());
this.getBatchImportMetadataFormPanel().add(this.getResultatLabel());
this.add(this.getBatchImportMetadataFormPanel());
this.doLayout()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("MetadataBatchImport.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getBatchImportMetadataFormPanel:function(){if(!this.batchImportMetadataFormPanel){this.batchImportMetadataFormPanel=new Ext.form.FormPanel({itemCls:"formItems",border:false,buttons:[{text:Openwis.i18n("Common.Btn.Upload"),handler:function(btn,e){if(this.getBatchImportMetadataFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.metadata.batchimport",params:this.getMetadataBatchImportInfos(),listeners:{success:function(config){this.configRes=config;
this.initializeRes()
},scope:this}});
saveHandler.proceed()
}},scope:this}]})
}return this.batchImportMetadataFormPanel
},getResultatLabel:function(){if(!this.resultatLabel){this.resultatLabel=new Ext.form.Label({text:this.configRes,labelStyle:"font-weight:bold;"})
}return this.resultatLabel
},getFileTypeRadioGroup:function(){if(!this.fileTypeRadioGroup){this.fileTypeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("MetadataBatchImport.FileType"),items:[this.getSingleFileRadio(),this.getMefFileRadio()]})
}return this.fileTypeRadioGroup
},getSingleFileRadio:function(){if(!this.singleFileRadio){this.singleFileRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("MetadataBatchImport.FileType.SingleFile"),name:"fileType",inputValue:"single",checked:true})
}return this.singleFileRadio
},getMefFileRadio:function(){if(!this.mefFileRadio){this.mefFileRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("MetadataBatchImport.FileType.MefFile"),name:"fileType",inputValue:"mef"})
}return this.mefFileRadio
},getMetadataDirectoryTextField:function(){if(!this.metadataDirectoryTextField){this.metadataDirectoryTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("MetadataBatchImport.Directory"),allowBlank:false,width:330})
}return this.metadataDirectoryTextField
},getStyleSheetComboBox:function(){if(!this.styleSheetComboBox){var styleSheetStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.styleSheetComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataBatchImport.StyleSheet"),name:"stylesheet",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:styleSheetStore,editable:false,allowBlank:false,width:330,value:"NONE",displayField:"name",valueField:"id"});
this.styleSheetComboBox.getStore().loadData(this.config.styleSheet);
this.styleSheetComboBox.getStore().insert(0,[new Ext.data.Record({id:"NONE",name:Openwis.i18n("Common.List.None")})])
}return this.styleSheetComboBox
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("MetadataBatchImport.Validate")})
}return this.validationCombobox
},getCategoriesComboBox:function(){if(!this.categoriesComboBox){var categoriesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.categoriesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataBatchImport.Category"),name:"category",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:categoriesStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"});
this.categoriesComboBox.getStore().loadData(this.config.categories)
}return this.categoriesComboBox
},getMetadataBatchImportInfos:function(){var metadataBatchImport={};
metadataBatchImport.directory=this.getMetadataDirectoryTextField().getValue();
metadataBatchImport.fileType=this.getFileTypeRadioGroup().getValue().inputValue;
if(this.getStyleSheetComboBox().getValue()=="NONE"){metadataBatchImport.stylesheet=null
}else{metadataBatchImport.stylesheet=this.getStyleSheetComboBox().getStore().getById(this.getStyleSheetComboBox().getValue()).data
}metadataBatchImport.validationMode=this.getValidationCombobox().getValue();
metadataBatchImport.category=this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data;
return metadataBatchImport
}});Ext.ns("Openwis.Common.Metadata");
Openwis.Common.Metadata.MonitorCatalog=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Common.Metadata.MonitorCatalog.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSearchFormPanel());
this.add(this.getMetadataGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Metadata.CatalogContent.Title"),cls:"administrationTitle1"})
}return this.header
},getMetadataGrid:function(){if(!this.metadataGrid){var columns=[];
columns.push(new Ext.grid.Column({id:"urn",header:Openwis.i18n("Metadata.CatalogContent.Header.MetadataIdentifier"),dataIndex:"urn",sortable:true,hideable:false,renderer:Openwis.Common.Request.Utils.htmlSafeRenderer}));
columns.push(new Ext.grid.Column({id:"title",header:Openwis.i18n("Metadata.CatalogContent.Header.MetadataTitle"),dataIndex:"title",sortable:true,renderer:Openwis.Common.Request.Utils.htmlSafeRenderer}));
columns.push(new Ext.grid.Column({id:"category",header:Openwis.i18n("Metadata.CatalogContent.Header.MetadataCategory"),dataIndex:"category",sortable:true}));
if(this.isAdmin){columns.push(new Ext.grid.Column({id:"originator",header:Openwis.i18n("Metadata.CatalogContent.Header.Originator"),dataIndex:"originator",sortable:true,width:60,renderer:Openwis.Common.Request.Utils.htmlSafeRenderer}));
columns.push(new Ext.grid.Column({id:"process",header:Openwis.i18n("Metadata.CatalogContent.Header.Process"),dataIndex:"process",sortable:true,width:50}))
}columns.push(new Ext.grid.Column({id:"gtsCategory",header:Openwis.i18n("Metadata.CatalogContent.Header.GTSCategory"),dataIndex:"gtsCategory",sortable:true,width:80,renderer:this.renderGtsCategory}));
columns.push(new Ext.grid.Column({id:"fncPattern",header:Openwis.i18n("Metadata.CatalogContent.Header.FNCPattern"),dataIndex:"fncPattern",sortable:false,width:70,renderer:this.renderFncPattern}));
columns.push(new Ext.grid.Column({id:"priority",header:Openwis.i18n("Metadata.CatalogContent.Header.Priority"),tooltip:"GTS Priority",dataIndex:"priority",sortable:false,width:45,renderer:this.renderPriority}));
columns.push(new Ext.grid.Column({id:"dataPolicy",header:Openwis.i18n("Metadata.CatalogContent.Header.DataPolicy"),dataIndex:"dataPolicy",sortable:false,width:70,renderer:this.renderDataPolicy}));
columns.push(new Ext.grid.Column({id:"localDataSource",header:Openwis.i18n("Metadata.CatalogContent.Header.LocalDataSource"),dataIndex:"localDataSource",sortable:true}));
if(this.isAdmin){columns.push(new Ext.grid.Column({id:"ingested",header:Openwis.i18n("Metadata.CatalogContent.Header.Ingested"),dataIndex:"ingested",sortable:true,width:40,hidden:true,renderer:this.renderBoolean}));
columns.push(new Ext.grid.Column({id:"fed",header:Openwis.i18n("Metadata.CatalogContent.Header.Fed"),dataIndex:"fed",sortable:true,width:40,hidden:true,renderer:this.renderBoolean}));
columns.push(new Ext.grid.Column({id:"fileExtension",header:Openwis.i18n("Metadata.CatalogContent.Header.FileExtension"),dataIndex:"fileExtension",sortable:false,width:40,hidden:true,renderer:this.renderFileExtension}))
}this.metadataGrid=new Ext.grid.GridPanel({id:"metadataGrid",height:400,border:true,store:this.getMetadataStore(),loadMask:true,viewConfig:{forceFit:true},columns:columns,listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().setBaseParam("myMetadataOnly",!this.isAdmin);
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getDuplicateMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getEditMetadataAction().setDisabled(sm.getCount()!=1||record.get("process")!="LOCAL");
if(sm.grid.ownerCt.isAdmin){sm.grid.ownerCt.getEditMetaInfoAction().setDisabled(sm.getCount()<=0);
sm.grid.ownerCt.getEditCategoryAction().setDisabled(sm.getCount()<=0)
}sm.grid.ownerCt.getRemoveMetadataAction().setDisabled(sm.getCount()<=0);
sm.grid.ownerCt.getAsXmlAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getAsWmoCoreProfileAction().setDisabled(sm.getCount()!=1);
if(sm.getCount()==1&&sm.getSelected().get("schema")=="iso19139"){sm.grid.ownerCt.getAsWmoCoreProfileAction().setDisabled(true)
}sm.grid.ownerCt.getAsMefAction().setDisabled(sm.getCount()<=0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getDuplicateMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getEditMetadataAction().setDisabled(sm.getCount()!=1||record.get("process")!="LOCAL");
if(sm.grid.ownerCt.isAdmin){sm.grid.ownerCt.getEditMetaInfoAction().setDisabled(sm.getCount()<=0);
sm.grid.ownerCt.getEditCategoryAction().setDisabled(sm.getCount()<=0)
}sm.grid.ownerCt.getRemoveMetadataAction().setDisabled(sm.getCount()<=0);
sm.grid.ownerCt.getAsXmlAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getAsWmoCoreProfileAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getAsMefAction().setDisabled(sm.getCount()<=0)
}}}),bbar:this.getPagingToolbar()});
this.metadataGrid.addButton(new Ext.Button(this.getDuplicateMetadataAction()));
this.metadataGrid.addButton(new Ext.Button(this.getViewMetadataAction()));
this.metadataGrid.addButton(new Ext.Button(this.getEditMetadataAction()));
if(this.isAdmin){this.metadataGrid.addButton(new Ext.Button(this.getEditMetaInfoAction()));
this.metadataGrid.addButton(new Ext.Button(this.getEditCategoryAction()))
}this.metadataGrid.addButton(new Ext.Button(this.getRemoveMetadataAction()));
this.metadataGrid.addButton(new Ext.Button(this.getExportMetadataMenuButton()))
}return this.metadataGrid
},getPagingToolbar:function(){if(!this.pagingToolbar){this.pagingToolbar=new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getMetadataStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Metadata.CatalogContent.Display.Range"),emptyMsg:Openwis.i18n("Metadata.CatalogContent.No.Metadata")})
}return this.pagingToolbar
},getMetadataStore:function(){if(!this.metadataStore){this.metadataStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.metadata.all",remoteSort:true,root:"metadatas",totalProperty:"count",idProperty:"urn",fields:[{name:"id"},{name:"urn",sortType:Ext.data.SortTypes.asUCString},{name:"title",sortType:Ext.data.SortTypes.asUCString},{name:"category",mapping:"category.name",sortType:Ext.data.SortTypes.asUCString},{name:"originator"},{name:"process"},{name:"gtsCategory"},{name:"fncPattern"},{name:"priority"},{name:"dataPolicy",mapping:"dataPolicy.name"},{name:"localDataSource"},{name:"ingested"},{name:"fed"},{name:"fileExtension"},{name:"overridenGtsCategory"},{name:"overridenFncPattern"},{name:"overridenPriority"},{name:"overridenDataPolicy"},{name:"overridenFileExtension"},{name:"schema"}],sortInfo:{field:"urn",direction:"ASC"}})
}return this.metadataStore
},getDuplicateMetadataAction:function(){if(!this.duplicateAction){this.duplicateAction=new Ext.Action({disabled:true,text:Openwis.i18n("Metadata.MetaInfo.Btn.Duplicate"),tooltip:"Create a new metadata from the selected one.",scope:this,handler:function(){var selectedRec=this.getMetadataGrid().getSelectionModel().getSelected();
Ext.MessageBox.prompt("Metadata URN","Please enter a valid URN :",this.handleCreation,this)
}})
}return this.duplicateAction
},getViewMetadataAction:function(){if(!this.viewAction){this.viewAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.View"),scope:this,handler:function(){var selectedRec=this.getMetadataGrid().getSelectionModel().getSelected();
var editable=selectedRec.get("process")=="LOCAL";
doShowMetadataByUrn(selectedRec.get("urn"),selectedRec.get("title"),editable)
}})
}return this.viewAction
},getEditMetadataAction:function(){if(!this.editMetadataAction){this.editMetadataAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getMetadataGrid().getSelectionModel().getSelected();
doEditMetadataByUrn(selectedRec.get("urn"),selectedRec.get("title"))
}})
}return this.editMetadataAction
},getEditMetaInfoAction:function(){if(!this.editMetaInfoAction){this.editMetaInfoAction=new Ext.Action({disabled:true,text:Openwis.i18n("Metadata.MetaInfo.Btn.EditMetaInfo"),scope:this,handler:function(){var selections=this.getMetadataGrid().getSelectionModel().getSelections();
var urns=[];
Ext.each(selections,function(item,index,allItems){urns.push(item.get("urn"))
},this);
new Openwis.Admin.MetaInfo.Manage({multiple:urns.length>1,metadataURNs:urns,listeners:{metaInfoSaved:function(){this.getMetadataGrid().getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this}})
}})
}return this.editMetaInfoAction
},getEditCategoryAction:function(){if(!this.editCategoryAction){this.editCategoryAction=new Ext.Action({disabled:true,text:Openwis.i18n("Metadata.MetaInfo.Btn.EditCategory"),scope:this,handler:function(){var selections=this.getMetadataGrid().getSelectionModel().getSelections();
var categoryName="";
var urns=[];
Ext.each(selections,function(item,index,allItems){urns.push(item.get("urn"));
categoryName=item.get("category")
},this);
if(urns.length>1){categoryName=""
}new Openwis.Admin.Category.Edit({multiple:urns.length>1,metadataURNs:urns,categoryName:categoryName,listeners:{editCategorySaved:function(){Openwis.Utils.MessageBox.displaySuccessMsg("Update category was successful.",this.fireSuccessEvent,this);
this.getPagingToolbar().doRefresh()
},scope:this}})
}})
}return this.editCategoryAction
},getRemoveMetadataAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selections=this.getMetadataGrid().getSelectionModel().getSelections();
var params=[];
Ext.each(selections,function(item,index,allItems){params.push(item.get("urn"))
},this);
var msg="Metadata with the followings URNs will be removed : "+params+"<br> Do you confirm the action?";
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.metadata.remove",params:params,confirmMsg:msg,listeners:{success:function(){this.getMetadataGrid().getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},failure:function(){this.getMetadataGrid().getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this}});
removeHandler.proceed()
}})
}return this.removeAction
},getExportMetadataMenuButton:function(){if(!this.exportMetadataMenuButton){this.exportMetadataMenuButton=new Ext.Button({text:Openwis.i18n("Common.Btn.Export"),menu:new Ext.menu.Menu({items:[this.getAsXmlAction(),this.getAsWmoCoreProfileAction(),this.getAsMefAction()]})})
}return this.exportMetadataMenuButton
},getAsXmlAction:function(){if(!this.asXmlAction){this.asXmlAction=new Ext.menu.Item({disabled:true,text:Openwis.i18n("Metadata.MetaInfo.Item.AsXml"),scope:this,handler:function(){if(this.getMetadataGrid().getSelectionModel().getCount()==1){var selectedRec=this.getMetadataGrid().getSelectionModel().getSelected();
var schema=selectedRec.get("schema");
var id=selectedRec.get("id");
if(schema=="dublin-core"){window.open(configOptions.locService+"/dc.xml?id="+id,"_blank","")
}else{if(schema=="fgdc-std"){window.open(configOptions.locService+"/fgdc.xml?id="+id,"_blank","")
}else{if(schema=="iso19115"){window.open(configOptions.locService+"/iso19115to19139.xml?id="+id,"_blank","")
}else{if(schema=="iso19139"){window.open(configOptions.locService+"/iso19139.xml?id="+id,"_blank","")
}else{if(schema=="iso19110"){window.open(configOptions.locService+"/iso19110.xml?id="+id,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("Metadata.MetaInfo.NoSchema.WarnDlg.Title"),msg:Openwis.i18n("Metadata.MetaInfo.NoSchema.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}}}}}}})
}return this.asXmlAction
},getAsWmoCoreProfileAction:function(){if(!this.asWmoCoreProfileAction){this.asWmoCoreProfileAction=new Ext.menu.Item({disabled:true,text:Openwis.i18n("Metadata.MetaInfo.Item.AsWmoCoreProfile"),scope:this,handler:function(){if(this.getMetadataGrid().getSelectionModel().getCount()==1){var selectedRec=this.getMetadataGrid().getSelectionModel().getSelected();
var schema=selectedRec.get("schema");
var id=selectedRec.get("id");
window.open(configOptions.locService+"/"+schema+"_to_coreProfile.xml?id="+id,"_blank","")
}}})
}return this.asWmoCoreProfileAction
},getAsMefAction:function(){if(!this.asMefAction){this.asMefAction=new Ext.menu.Item({disabled:true,text:Openwis.i18n("Metadata.MetaInfo.Item.AsMef"),scope:this,handler:function(){var selection=this.getMetadataGrid().getSelectionModel().getSelections();
var uuids=[];
Ext.each(selection,function(item,index,allItems){uuids.push(item.get("urn"))
},this);
window.location.href=configOptions.locService+"/xml.metadata.export?uuid="+uuids+"&format=full&version=2"
}})
}return this.asMefAction
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:100,border:false,buttonAlign:"center"});
this.searchFormPanel.add(this.getSearchTextField());
this.searchFormPanel.add(this.getSearchFieldCombo());
this.searchFormPanel.add(this.getCategoriesComboBox());
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchTextField:function(){if(!this.searchTextField){this.searchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Metadata.CatalogContent.TextSearch"),name:"any",enableKeyEvents:true,width:150,listeners:{keyup:function(){var searchOn=Ext.isEmpty(this.getSearchTextField().getValue().trim());
Ext.isEmpty(this.getCategoriesComboBox().getRawValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getMetadataStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getMetadataStore().setBaseParam("myMetadataOnly",!this.isAdmin);
this.getMetadataStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}})
}return this.searchTextField
},getSearchFieldCombo:function(){if(!this.searchFieldCombo){var columns=[];
columns.push(["",Openwis.i18n("Metadata.CatalogContent.SearchField.any")]);
columns.push(["uuid",Openwis.i18n("Metadata.CatalogContent.SearchField.uuid")]);
columns.push(["title",Openwis.i18n("Metadata.CatalogContent.SearchField.title")]);
if(this.isAdmin){columns.push(["_originator",Openwis.i18n("Metadata.CatalogContent.SearchField.originator")]);
columns.push(["_process",Openwis.i18n("Metadata.CatalogContent.SearchField.process")])
}columns.push(["_gtsCategory",Openwis.i18n("Metadata.CatalogContent.SearchField.gtsCategory")]);
columns.push(["_overriddenGtsCategory",Openwis.i18n("Metadata.CatalogContent.SearchField.overriddenGtsCategory")]);
columns.push(["_fncPattern",Openwis.i18n("Metadata.CatalogContent.SearchField.fncPattern")]);
columns.push(["_overriddenFncPattern",Openwis.i18n("Metadata.CatalogContent.SearchField.overriddenFncPattern")]);
columns.push(["_priority",Openwis.i18n("Metadata.CatalogContent.SearchField.priority")]);
columns.push(["_overriddenPriority",Openwis.i18n("Metadata.CatalogContent.SearchField.overriddenPriority")]);
columns.push(["_datapolicy",Openwis.i18n("Metadata.CatalogContent.SearchField.datapolicy")]);
columns.push(["_overriddenDatapolicy",Openwis.i18n("Metadata.CatalogContent.SearchField.overriddenDatapolicy")]);
columns.push(["_localDataSource",Openwis.i18n("Metadata.CatalogContent.SearchField.localDataSource")]);
if(this.isAdmin){columns.push(["_isIngested",Openwis.i18n("Metadata.CatalogContent.SearchField.isIngested")]);
columns.push(["_isFed",Openwis.i18n("Metadata.CatalogContent.SearchField.isFed")]);
columns.push(["_fileExtension",Openwis.i18n("Metadata.CatalogContent.SearchField.fileExtension")])
}this.searchFieldCombo=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Metadata.CatalogContent.SearchField"),name:"searchField",enableKeyEvents:true,width:150,mode:"local",store:new Ext.data.ArrayStore({id:"_priority",fields:["fieldKey","fieldName"],data:columns}),valueField:"fieldKey",displayField:"fieldName",triggerAction:"all",listeners:{select:function(){var searchOn=Ext.isEmpty(this.getSearchFieldCombo().getRawValue().trim())||Ext.isEmpty(this.getCategoriesComboBox().getRawValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getMetadataStore().setBaseParam(this.getSearchFieldCombo().getName(),this.getSearchFieldCombo().getValue());
this.getMetadataStore().setBaseParam("myMetadataOnly",!this.isAdmin);
this.getMetadataStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}});
this.searchFieldCombo.setValue("")
}return this.searchFieldCombo
},getCategoriesComboBox:function(){if(!this.categoriesComboBox){var anyRecord=new Ext.data.Record({id:"",name:Openwis.i18n("Common.List.Any")});
var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.categoriesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataCreate.Category"),name:"categories",store:categoryStore,valueField:"id",displayField:"name",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200,listeners:{select:function(){var searchOn=Ext.isEmpty(this.getSearchFieldCombo().getRawValue().trim())||Ext.isEmpty(this.getCategoriesComboBox().getRawValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getMetadataStore().setBaseParam(this.getCategoriesComboBox().getName(),this.getCategoriesComboBox().getValue());
this.getMetadataStore().setBaseParam("myMetadataOnly",!this.isAdmin);
this.getMetadataStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}});
var me=this.categoriesComboBox;
categoryStore.on("load",function(store,records,options){store.insert(0,[anyRecord]);
me.setValue("")
});
categoryStore.load()
}return this.categoriesComboBox
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){this.getMetadataStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getMetadataStore().setBaseParam(this.getSearchFieldCombo().getName(),this.getSearchFieldCombo().getValue());
this.getMetadataStore().setBaseParam(this.getCategoriesComboBox().getName(),this.getCategoriesComboBox().getValue());
this.getMetadataStore().setBaseParam("myMetadataOnly",!this.isAdmin);
this.getMetadataStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Reset"),scope:this,handler:function(){this.getSearchTextField().setValue("");
this.getSearchFieldCombo().setValue("");
this.getCategoriesComboBox().setValue("");
this.getMetadataStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getMetadataStore().setBaseParam(this.getSearchFieldCombo().getName(),this.getSearchFieldCombo().getValue());
this.getMetadataStore().setBaseParam(this.getCategoriesComboBox().getName(),this.getCategoriesComboBox().getValue());
this.getMetadataStore().setBaseParam("myMetadataOnly",!this.isAdmin);
this.getMetadataStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
this.getSearchAction().setDisabled(true);
this.getResetAction().setDisabled(true)
}})
}return this.resetAction
},handleCreation:function(btn,text){if(btn=="ok"&&this.isValidURN(text)){var duplicateParams={};
duplicateParams.toURN=text;
duplicateParams.fromURN=this.getMetadataGrid().getSelectionModel().getSelected().data.urn;
var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.metadata.duplicate",params:duplicateParams,listeners:{success:function(config){this.getMetadataStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE},callback:function(){var duplicate=this.getMetadataStore().getById(text);
if(Ext.isDefined(duplicate)){this.getMetadataGrid().getSelectionModel().selectRecords([duplicate]);
doEditMetadataByUrn(duplicate.get("urn"),duplicate.get("title"))
}},scope:this})
},scope:this}});
saveHandler.proceed()
}},isValidURN:function(value){return true
},renderBoolean:function(value){return value==true?"yes":"no"
},renderGtsCategory:function(value,metadata,record){var overridenValue=record.data.overridenGtsCategory;
if(overridenValue!=undefined&&overridenValue!=""){value=overridenValue;
metadata.attr='style="color:red;"'
}return Ext.util.Format.htmlEncode(value)
},renderFncPattern:function(value,metadata,record){var overridenValue=record.data.overridenFncPattern;
if(overridenValue!=undefined&&overridenValue!=""){value=overridenValue;
metadata.attr='style="color:red;"'
}return Ext.util.Format.htmlEncode(value)
},renderPriority:function(value,metadata,record){var overridenValue=record.data.overridenPriority;
if(overridenValue!=undefined&&overridenValue!=""){value=overridenValue;
metadata.attr='style="color:red;"'
}return Ext.util.Format.htmlEncode(value)
},renderDataPolicy:function(value,metadata,record){var overridenValue=record.data.overridenDataPolicy;
if(overridenValue!=undefined&&overridenValue!=""){value=overridenValue;
metadata.attr='style="color:red;"'
}return Ext.util.Format.htmlEncode(value)
},renderFileExtension:function(value,metadata,record){var overridenValue=record.data.overridenFileExtension;
if(overridenValue!=undefined&&overridenValue!=""){value=overridenValue;
metadata.attr='style="color:red;"'
}return Ext.util.Format.htmlEncode(value)
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
}}};Ext.ns("Openwis.Common.Dissemination");
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
}});Ext.ns("Openwis.Common.Components");
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
request.secondaryDissemination=this.secondaryDisseminationPanel?this.getSecondaryDisseminationPanel().buildDissemination():null;
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
}}}}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel=Ext.extend(Ext.grid.GridPanel,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"},height:250,border:true,store:this.getRequestsStore(),loadMask:true,columns:[{id:"statusImg",header:"",dataIndex:"status",renderer:Openwis.Common.Request.Utils.statusRendererImg,width:50,sortable:false},{id:"deployment",header:Openwis.i18n("TrackMyRequests.Deployment"),dataIndex:"deployment",renderer:Openwis.Common.Request.Utils.backupRenderer,sortable:false,hidden:this.isLocal},{id:"title",header:Openwis.i18n("TrackMyRequests.ProductMetadata.Title"),dataIndex:"title",renderer:Openwis.Common.Request.Utils.htmlSafeRenderer,sortable:true},{id:"creationDate",header:Openwis.i18n("TrackMyRequests.ProductMetadata.CreationDate"),dataIndex:"creationDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:true},{id:"id",header:Openwis.i18n("TrackMyRequests.Request.ID"),dataIndex:"id",width:100,sortable:true},{id:"status",header:Openwis.i18n("TrackMyRequests.Request.Status"),dataIndex:"status",renderer:Openwis.Common.Request.Utils.statusRenderer,width:100,sortable:true},{id:"size",header:Openwis.i18n("TrackMyRequests.Request.Volume"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:100,sortable:false}],autoExpandColumn:"title",listeners:{afterrender:function(grid){if(this.isLocal){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.getViewRequestAction().setDisabled(sm.getCount()!=1);
sm.grid.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.getDownloadAction().setDisabled(sm.getCount()!=1||record.get("downloadUrl")==null||record.get("downloadUrl")=="");
sm.grid.getDiscardAction().setDisabled(sm.getCount()==0);
sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){sm.grid.getViewRequestAction().setDisabled(sm.getCount()!=1);
sm.grid.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.getDownloadAction().setDisabled(sm.getCount()!=1||record.get("downloadUrl")==null||record.get("downloadUrl")=="");
sm.grid.getDiscardAction().setDisabled(sm.getCount()==0);
sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount()!=1)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getRequestsStore(),displayInfo:true,displayMsg:Openwis.i18n("TrackMyRequests.Display.Range"),emptyMsg:Openwis.i18n("TrackMyRequests.No.Request")})});
Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel.superclass.initComponent.apply(this,arguments);
if(this.isLocal){this.addButton(new Ext.Button(this.getViewRequestAction()));
this.addButton(new Ext.Button(this.getViewMetadataAction()));
this.addButton(new Ext.Button(this.getDownloadAction()));
this.addButton(new Ext.Button(this.getDiscardAction()))
}else{this.addButton(new Ext.Button(this.getGoToDeploymentAction()))
}},getRequestsStore:function(){if(!this.requestsStore){var url=configOptions.locService;
if(this.isLocal){url+="/xml.follow.my.adhocs"
}else{url+="/xml.follow.my.remote.adhocs"
}this.requestsStore=new Openwis.Data.JeevesJsonStore({url:url,idProperty:"id",remoteSort:true,root:"rows",fields:[{name:"deployment"},{name:"urn",mapping:"productMetadataURN"},{name:"title",mapping:"productMetadataTitle",sortType:Ext.data.SortTypes.asUCString},{name:"creationDate",mapping:"processedRequestDTO.creationDate"},{name:"id",mapping:"requestID"},{name:"status",mapping:"processedRequestDTO.status"},{name:"downloadUrl",mapping:"processedRequestDTO.url"},{name:"size",mapping:"processedRequestDTO.size"},{name:"requestType"},{name:"extractMode"}],sortInfo:{field:"title",direction:"ASC"}})
}return this.requestsStore
},getViewRequestAction:function(){if(!this.viewRequestAction){this.viewRequestAction=new Ext.Action({text:Openwis.i18n("TrackMyRequests.Action.ViewRequest"),disabled:true,iconCls:"icon-view-adhoc",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"ADHOC",rec.get("extractMode")=="CACHE","View",rec.get("id"),false)
}})
}return this.viewRequestAction
},getViewMetadataAction:function(){if(!this.viewMetadataAction){this.viewMetadataAction=new Ext.Action({text:Openwis.i18n("TrackMyRequests.Action.ViewMetadata"),disabled:true,iconCls:"icon-viewmd-adhoc",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
doShowMetadataByUrn(rec.get("urn"),rec.get("title"))
}})
}return this.viewMetadataAction
},getDownloadAction:function(){if(!this.downloadAction){this.downloadAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Download"),disabled:true,iconCls:"icon-download-adhoc",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
window.open(rec.get("downloadUrl"))
}})
}return this.downloadAction
},getDiscardAction:function(){if(!this.discardAction){this.discardAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Discard"),disabled:true,iconCls:"icon-discard-adhoc",scope:this,handler:function(){var selection=this.getSelectionModel().getSelections();
var params={discardRequests:[]};
Ext.each(selection,function(item,index,allItems){params.discardRequests.push({requestID:item.get("id"),typeRequest:"ADHOC"})
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.discard.request",params:params,listeners:{success:function(){if(this.userAlarmGridPanel){this.userAlarmGridPanel.reload()
}this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.discardAction
},getGoToDeploymentAction:function(){if(!this.goToDeploymentAction){this.goToDeploymentAction=new Ext.Action({text:Openwis.i18n("Common.Btn.GoToDeployment"),disabled:true,scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var deployment=rec.get("deployment");
if(deployment){window.open(deployment.url)
}}})
}return this.goToDeploymentAction
}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel=Ext.extend(Ext.grid.GridPanel,{initComponent:function(){var requestTitle;
var viewRequestAction;
if(this.isSubscription){requestTitle=Openwis.i18n("TrackMySubscriptions.Subscription.ID");
viewRequestAction=this.getViewSubscriptionAction()
}else{requestTitle=Openwis.i18n("TrackMyRequests.Request.ID");
viewRequestAction=this.getViewRequestAction()
}Ext.apply(this,{style:{margin:"10px 30px 10px 30px"},height:250,border:true,store:this.getUserAlarmStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("TrackMyRequests.UserAlarms.Date"),renderer:Openwis.Utils.Date.formatDateTimeUTC,dataIndex:"date",width:100,sortable:true},{id:"requestId",header:requestTitle,dataIndex:"requestId",width:100,sortable:true},{id:"message",header:Openwis.i18n("TrackMyRequests.UserAlarms.Message"),renderer:this.renderMessage.createDelegate(this),dataIndex:"message",width:100,sortable:true}],autoExpandColumn:"message",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.getAcknowledgeAction().setDisabled(sm.getCount()==0);
sm.grid.getDownloadAction().setDisabled(sm.getCount()!=1);
viewRequestAction.setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){sm.grid.getAcknowledgeAction().setDisabled(sm.getCount()==0);
sm.grid.getDownloadAction().setDisabled(sm.getCount()!=1);
viewRequestAction.setDisabled(sm.getCount()!=1)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getUserAlarmStore(),displayInfo:true,displayMsg:Openwis.i18n("TrackMyRequests.UserAlarms.Display.Range"),emptyMsg:Openwis.i18n("TrackMyRequests.UserAlarms.No.Alarms"),})});
Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel.superclass.initComponent.apply(this,arguments);
this.addButton(new Ext.Button(viewRequestAction));
this.addButton(new Ext.Button(this.getDownloadAction()));
this.addButton(new Ext.Button(this.getAcknowledgeAction()));
this.addButton(new Ext.Button(this.getAcknowledgeAllAction()))
},getUserAlarmStore:function(){if(!this.userAlarmStore){var url;
if(this.isSubscription){url=configOptions.locService+"/xml.useralarms.getsubscriptions"
}else{url=configOptions.locService+"/xml.useralarms.getrequests"
}this.userAlarmStore=new Openwis.Data.JeevesJsonStore({url:url,idProperty:"id",remoteSort:true,root:"rows",fields:[{name:"id"},{name:"date"},{name:"requestId"},{name:"alarmType"},{name:"message",sortType:Ext.data.SortTypes.asUCString},{name:"urn"},{name:"extractMode"},{name:"downloadUrl"}],sortInfo:{field:"date",direction:"DESC"},listeners:{load:function(store,records,successful,operation,eOpts){this.getAcknowledgeAllAction().setDisabled(store.getCount()==0)
},scope:this}})
}return this.userAlarmStore
},getDownloadAction:function(){if(!this.downloadAction){this.downloadAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Download"),disabled:true,iconCls:"icon-download-adhoc",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
window.open(rec.get("downloadUrl"))
}})
}return this.downloadAction
},getAcknowledgeAction:function(){if(!this.acknowledgeAction){this.acknowledgeAction=new Ext.Action({text:Openwis.i18n("TrackMyRequests.UserAlarms.Action.Acknowledge"),iconCls:"icon-discard-adhoc",disabled:true,scope:this,handler:function(){var selection=this.getSelectionModel().getSelections();
var params={alarmIds:[]};
Ext.each(selection,function(item,index,allItems){params.alarmIds.push(item.get("id"))
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.useralarms.acknowledge",params:params,listeners:{success:function(){this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.acknowledgeAction
},getAcknowledgeAllAction:function(){if(!this.acknowledgeAllAction){this.acknowledgeAllAction=new Ext.Action({text:Openwis.i18n("TrackMyRequests.UserAlarms.Action.AcknowledgeAll"),iconCls:"icon-discard-adhoc",scope:this,disabled:true,handler:function(){var params={subscription:this.isSubscription};
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.useralarms.acknowledgeall",params:params,listeners:{success:function(){this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.acknowledgeAllAction
},reload:function(){this.getStore().reload()
},renderMessage:function(value,cell,record){var data=record.data;
var msg=data.message;
return'<div qtip="'+msg+'">'+value+"</div>"
},getViewRequestAction:function(){if(!this.viewRequestAction){this.viewRequestAction=new Ext.Action({text:Openwis.i18n("TrackMyRequests.Action.ViewRequest"),disabled:true,iconCls:"icon-view-adhoc",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"ADHOC",rec.get("extractMode")=="CACHE","View",rec.get("requestId"),false)
}})
}return this.viewRequestAction
},getViewSubscriptionAction:function(){if(!this.viewSubscriptionAction){this.viewSubscriptionAction=new Ext.Action({text:Openwis.i18n("TrackMySubscriptions.Action.ViewEditSubscription"),disabled:true,iconCls:"icon-view-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"SUBSCRIPTION",rec.get("extractMode")=="CACHE","Edit",rec.get("requestId"),false)
}})
}return this.viewSubscriptionAction
}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.TrackMyAdhocs=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.MyAccount.TrackMyRequests.TrackMyAdhocs.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(new Ext.Container({html:Openwis.i18n("TrackMyRequests.UserAlarms.Title"),cls:"myAccountTitle2"}));
this.add(this.getUserAlarmsGrid());
this.add(new Ext.Container({html:Openwis.i18n("TrackMyRequests.Local.Title"),cls:"myAccountTitle2"}));
this.add(this.getLocalRequestsGrid());
this.add(new Ext.Container({html:Openwis.i18n("TrackMyRequests.Remote.Title"),cls:"myAccountTitle2"}));
this.add(this.getDeploymentsComboBox());
this.add(this.getRemoteRequestsGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("TrackMyRequests.Title"),cls:"myAccountTitle1"})
}return this.header
},getUserAlarmsGrid:function(){if(!this.userAlarmsGrid){this.userAlarmsGrid=new Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel({isSubscription:false})
}return this.userAlarmsGrid
},getLocalRequestsGrid:function(){if(!this.localRequestsGrid){this.localRequestsGrid=new Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel({isLocal:true,userAlarmGridPanel:this.getUserAlarmsGrid()})
}return this.localRequestsGrid
},getDeploymentsComboBox:function(){if(!this.deploymentsComboBox){var deploymentStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.deployment.cot.all",idProperty:"name",fields:[{name:"name"}]});
this.deploymentsComboBox=new Ext.form.ComboBox({store:deploymentStore,valueField:"name",displayField:"name",name:"deployment",emptyText:Openwis.i18n("TrackMyRequests.Remote.Select.Deployment"),typeAhead:true,triggerAction:"all",editable:false,style:{margin:"0px 0px 0px 30px"},selectOnFocus:true,width:200,listeners:{select:function(combo,record,index){this.getRemoteRequestsGrid().getStore().setBaseParam("deployment",record.get("name"));
this.getRemoteRequestsGrid().getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this}})
}return this.deploymentsComboBox
},getRemoteRequestsGrid:function(){if(!this.remoteRequestsGrid){this.remoteRequestsGrid=new Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel({isLocal:false})
}return this.remoteRequestsGrid
}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel=Ext.extend(Ext.grid.GridPanel,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"},height:250,border:true,store:this.getSubscriptionsStore(),loadMask:true,columns:[{id:"statusImg",header:"",dataIndex:"state",renderer:Openwis.Common.Request.Utils.stateRendererImg,width:50,sortable:false},{id:"deployment",header:Openwis.i18n("TrackMySubscriptions.Deployment"),dataIndex:"deployment",sortable:false,renderer:Openwis.Common.Request.Utils.backupRenderer,hidden:this.isLocal},{id:"title",header:Openwis.i18n("TrackMySubscriptions.ProductMetadata.Title"),dataIndex:"title",sortable:true,renderer:Openwis.Common.Request.Utils.htmlSafeRenderer},{id:"backup",header:Openwis.i18n("TrackMySubscriptions.ProductMetadata.Backup"),dataIndex:"backup",sortable:true},{id:"id",header:Openwis.i18n("TrackMySubscriptions.Subscription.ID"),dataIndex:"id",width:100,sortable:true},{id:"startingDate",header:Openwis.i18n("TrackMySubscriptions.StartDate"),dataIndex:"startingDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:true},{id:"lastProcessingDate",header:Openwis.i18n("TrackMySubscriptions.LastEventDate"),dataIndex:"lastProcessingDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:false}],autoExpandColumn:"title",listeners:{afterrender:function(grid){if(this.isLocal){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount()!=1);
sm.grid.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.getSuspendAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="ACTIVE"));
sm.grid.getResumeAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="SUSPENDED"));
sm.grid.getDiscardAction().setDisabled(sm.getCount()==0);
sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount()!=1);
sm.grid.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.getSuspendAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="ACTIVE"));
sm.grid.getResumeAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="SUSPENDED"));
sm.grid.getDiscardAction().setDisabled(sm.getCount()==0);
sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount()!=1)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getSubscriptionsStore(),displayInfo:true,displayMsg:Openwis.i18n("TrackMySubscriptions.Display.Range"),emptyMsg:Openwis.i18n("TrackMySubscriptions.No.Subscription")})});
var viewSubscription;
Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel.superclass.initComponent.apply(this,arguments);
if(this.isLocal){this.addButton(new Ext.Button(this.getViewSubscriptionAction()));
this.addButton(new Ext.Button(this.getViewMetadataAction()));
this.addButton(new Ext.Button(this.getSuspendAction()));
this.addButton(new Ext.Button(this.getResumeAction()));
this.addButton(new Ext.Button(this.getDiscardAction()))
}else{this.addButton(new Ext.Button(this.getGoToDeploymentAction()))
}},getSubscriptionsStore:function(){if(!this.subscriptionsStore){this.subscriptionsStore=new Openwis.Data.JeevesJsonStore({url:this.url,idProperty:"id",remoteSort:true,root:"rows",fields:[{name:"deployment"},{name:"urn",mapping:"productMetadataURN"},{name:"title",mapping:"productMetadataTitle",sortType:Ext.data.SortTypes.asUCString},{name:"startingDate",mapping:"startingDate"},{name:"id",mapping:"requestID"},{name:"lastProcessingDate",mapping:"lastProcessingDate"},{name:"valid"},{name:"extractMode"},{name:"backup"},{name:"state"}],sortInfo:{field:"title",direction:"ASC"}})
}return this.subscriptionsStore
},getViewSubscriptionAction:function(){if(!this.viewSubscriptionAction){this.viewSubscriptionAction=new Ext.Action({text:Openwis.i18n("TrackMySubscriptions.Action.ViewEditSubscription"),disabled:true,iconCls:"icon-view-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"SUBSCRIPTION",rec.get("extractMode")=="CACHE","Edit",rec.get("id"),false)
}})
}return this.viewSubscriptionAction
},getViewMetadataAction:function(){if(!this.viewMetadataAction){this.viewMetadataAction=new Ext.Action({text:Openwis.i18n("TrackMySubscriptions.Action.ViewMetadata"),disabled:true,iconCls:"icon-viewmd-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
doShowMetadataByUrn(rec.get("urn"),rec.get("title"))
}})
}return this.viewMetadataAction
},getSuspendAction:function(){if(!this.suspendAction){this.suspendAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Suspend"),disabled:true,iconCls:"icon-suspend-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.set.subscription.state",params:{requestID:rec.get("id"),typeStateSet:"SUSPEND"},listeners:{success:function(){this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.suspendAction
},getResumeAction:function(){if(!this.resumeAction){this.resumeAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Resume"),disabled:true,iconCls:"icon-resume-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.set.subscription.state",params:{requestID:rec.get("id"),typeStateSet:"RESUME"},listeners:{success:function(){this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.resumeAction
},getDiscardAction:function(){if(!this.discardAction){this.discardAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Discard"),disabled:true,iconCls:"icon-discard-subscription",scope:this,handler:function(){var selection=this.getSelectionModel().getSelections();
var params={discardRequests:[]};
Ext.each(selection,function(item,index,allItems){params.discardRequests.push({requestID:item.get("id"),typeRequest:"SUBSCRIPTION"})
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.discard.request",params:params,listeners:{success:function(){if(this.userAlarmGridPanel){this.userAlarmGridPanel.reload()
}this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.discardAction
},getGoToDeploymentAction:function(){if(!this.goToDeploymentAction){this.goToDeploymentAction=new Ext.Action({text:Openwis.i18n("Common.Btn.GoToDeployment"),disabled:true,scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var deployment=rec.get("deployment");
if(deployment){window.open(deployment.url)
}}})
}return this.goToDeploymentAction
}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.TrackMySubscriptions=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.MyAccount.TrackMyRequests.TrackMySubscriptions.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(new Ext.Container({html:Openwis.i18n("TrackMyRequests.UserAlarms.Title"),cls:"myAccountTitle2"}));
this.add(this.getUserAlarmsGrid());
this.add(new Ext.Container({html:Openwis.i18n("TrackMySubscriptions.Local.Title"),cls:"myAccountTitle2"}));
this.add(this.getLocalSubscriptionsGrid());
this.add(new Ext.Container({html:Openwis.i18n("TrackMySubscriptions.Remote.Title"),cls:"myAccountTitle2"}));
this.add(this.getDeploymentsComboBox());
this.add(this.getRemoteSubscriptionsGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("TrackMySubscriptions.Title"),cls:"myAccountTitle1"})
}return this.header
},getUserAlarmsGrid:function(){if(!this.userAlarmsGrid){this.userAlarmsGrid=new Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel({isSubscription:true})
}return this.userAlarmsGrid
},getLocalSubscriptionsGrid:function(){if(!this.localSubscriptionsGrid){this.localSubscriptionsGrid=new Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel({isLocal:true,userAlarmGridPanel:this.getUserAlarmsGrid(),url:configOptions.locService+"/xml.follow.my.subscriptions"})
}return this.localSubscriptionsGrid
},getDeploymentsComboBox:function(){if(!this.deploymentsComboBox){var deploymentStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.deployment.cot.all",idProperty:"name",fields:[{name:"name"}]});
this.deploymentsComboBox=new Ext.form.ComboBox({store:deploymentStore,valueField:"name",displayField:"name",name:"deployment",emptyText:Openwis.i18n("TrackMySubscriptions.Remote.Select.Deployment"),typeAhead:true,triggerAction:"all",editable:false,style:{margin:"0px 0px 0px 30px"},selectOnFocus:true,width:200,listeners:{select:function(combo,record,index){this.getRemoteSubscriptionsGrid().getStore().setBaseParam("deployment",record.get("name"));
this.getRemoteSubscriptionsGrid().getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this}})
}return this.deploymentsComboBox
},getRemoteSubscriptionsGrid:function(){if(!this.remoteSubscriptionsGrid){this.remoteSubscriptionsGrid=new Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel({isLocal:false,url:configOptions.locService+"/xml.follow.my.remote.subscriptions"})
}return this.remoteSubscriptionsGrid
}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.MyMSSFSSSubscriptionsGridPanel=Ext.extend(Ext.grid.GridPanel,{initComponent:function(){Ext.apply(this,{height:250,border:true,store:this.getSubscriptionsStore(),loadMask:true,columns:[{id:"deployment",header:Openwis.i18n("TrackMySubscriptions.Deployment"),dataIndex:"deployment",sortable:false,renderer:Openwis.Common.Request.Utils.backupRenderer,hidden:this.isLocal},{id:"urn",header:Openwis.i18n("TrackMySubscriptions.ProductMetadata.Title"),dataIndex:"urn",sortable:true},{id:"channel",header:Openwis.i18n("TrackMySubscriptions.MSSFSS.Channel"),dataIndex:"channel",sortable:true},{id:"id",header:Openwis.i18n("TrackMySubscriptions.Subscription.ID"),dataIndex:"id",width:100,sortable:true},{id:"creationDate",header:Openwis.i18n("TrackMySubscriptions.CreationDate"),dataIndex:"creationDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:true},{id:"lastProcessingDate",header:Openwis.i18n("TrackMySubscriptions.LastEventDate"),dataIndex:"lastProcessingDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:true},{id:"state",header:Openwis.i18n("TrackMySubscriptions.MSSFSSState"),dataIndex:"state",renderer:this.routingStateRenderer,width:100,sortable:true}],autoExpandColumn:"urn",listeners:{afterrender:function(grid){if(this.isLocal){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount()!=1);
sm.grid.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.getDiscardAction().setDisabled(sm.getCount()==0);
sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){sm.grid.getViewSubscriptionAction().setDisabled(sm.getCount()!=1);
sm.grid.getViewMetadataAction().setDisabled(sm.getCount()!=1);
sm.grid.getDiscardAction().setDisabled(sm.getCount()==0);
sm.grid.getGoToDeploymentAction().setDisabled(sm.getCount()!=1)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getSubscriptionsStore(),displayInfo:true,displayMsg:Openwis.i18n("TrackMySubscriptions.Display.Range"),emptyMsg:Openwis.i18n("TrackMySubscriptions.No.Subscription")})});
Openwis.MyAccount.TrackMyRequests.MyMSSFSSSubscriptionsGridPanel.superclass.initComponent.apply(this,arguments);
if(this.isLocal){this.addButton(new Ext.Button(this.getViewSubscriptionAction()));
this.addButton(new Ext.Button(this.getViewMetadataAction()));
this.addButton(new Ext.Button(this.getDiscardAction()))
}else{this.addButton(new Ext.Button(this.getGoToDeploymentAction()))
}},getSubscriptionsStore:function(){if(!this.subscriptionsStore){this.subscriptionsStore=new Openwis.Data.JeevesJsonStore({url:this.url,idProperty:"id",remoteSort:true,root:"rows",fields:[{name:"deployment"},{name:"urn",mapping:"productMetadataURN"},{name:"channel",mapping:"primaryDissemination.o"},{name:"creationDate",mapping:"startingDate"},{name:"id",mapping:"requestID"},{name:"lastProcessingDate",mapping:"lastProcessingDate"},{name:"state"}],sortInfo:{field:"urn",direction:"ASC"}})
}return this.subscriptionsStore
},getViewSubscriptionAction:function(){if(!this.viewSubscriptionAction){this.viewSubscriptionAction=new Ext.Action({text:Openwis.i18n("TrackMySubscriptions.Action.ViewEditSubscription"),disabled:true,iconCls:"icon-view-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"SUBSCRIPTION",true,"Edit",rec.get("id"),true)
}})
}return this.viewSubscriptionAction
},getViewMetadataAction:function(){if(!this.viewMetadataAction){this.viewMetadataAction=new Ext.Action({text:Openwis.i18n("TrackMySubscriptions.Action.ViewMetadata"),disabled:true,iconCls:"icon-viewmd-subscription",scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
doShowMetadataByUrn(rec.get("urn"),rec.get("title"))
}})
}return this.viewMetadataAction
},getDiscardAction:function(){if(!this.discardAction){this.discardAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Discard"),disabled:true,iconCls:"icon-discard-subscription",scope:this,handler:function(){var selection=this.getSelectionModel().getSelections();
var params={discardRequests:[]};
Ext.each(selection,function(item,index,allItems){params.discardRequests.push({requestID:item.get("id"),typeRequest:"ROUTING"})
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.discard.request",params:params,listeners:{success:function(){this.getStore().reload()
},scope:this}}).proceed()
}})
}return this.discardAction
},getGoToDeploymentAction:function(){if(!this.goToDeploymentAction){this.goToDeploymentAction=new Ext.Action({text:Openwis.i18n("Common.Btn.GoToDeployment"),disabled:true,scope:this,handler:function(){var rec=this.getSelectionModel().getSelected();
var deployment=rec.get("deployment");
if(deployment){window.open(deployment.url)
}}})
}return this.goToDeploymentAction
},routingStateRenderer:function(val){if(val=="ACTIVE"){return Openwis.i18n("TrackMySubscriptions.MSSFSSState.Active")
}else{return Openwis.i18n("TrackMySubscriptions.MSSFSSState.Pending")
}}});Ext.ns("Openwis.MyAccount.TrackMyRequests");
Openwis.MyAccount.TrackMyRequests.TrackMyMSSFSSSubscriptions=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.MyAccount.TrackMyRequests.TrackMyMSSFSSSubscriptions.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(new Ext.Container({html:Openwis.i18n("TrackMyMSSFSSSubscriptions.Local.Title"),cls:"myAccountTitle2"}));
this.add(this.getLocalSubscriptionsGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("TrackMyMSSFSSSubscriptions.Title"),cls:"myAccountTitle1"})
}return this.header
},getLocalSubscriptionsGrid:function(){if(!this.localSubscriptionsGrid){this.localSubscriptionsGrid=new Openwis.MyAccount.TrackMyRequests.MyMSSFSSSubscriptionsGridPanel({isLocal:true,url:configOptions.locService+"/xml.follow.my.mssfss.subscriptions"})
}return this.localSubscriptionsGrid
}});Ext.ns("Openwis.MyAccount");
Openwis.MyAccount.Browser=Ext.extend(Ext.ux.GroupTabPanel,{initComponent:function(){var items=[];
if(this.getMetadataServiceMenu()){items.push(this.getMetadataServiceMenu())
}if(this.getTrackMyRequestsMenu()){items.push(this.getTrackMyRequestsMenu())
}if(this.getPersonalInformationMenu()){items.push(this.getPersonalInformationMenu())
}Ext.apply(this,{tabWidth:200,activeGroup:0,items:items,listeners:{afterrender:function(ct){}}});
Openwis.MyAccount.Browser.superclass.initComponent.apply(this,arguments)
},isServiceAccessible:function(service){var isAccessible=accessibleServices.indexOf(service);
return isAccessible!=-1
},getMetadataServiceMenu:function(){if(!this.metadataServiceMenu){var metadataServiceCreateMetadata=this.isServiceAccessible("xml.metadata.create.form");
var metadataServiceInsertMetadata=this.isServiceAccessible("xml.metadata.insert.form");
var metadataServiceBrowseMyMetadata=this.isServiceAccessible("xml.metadata.all");
if(metadataServiceCreateMetadata||metadataServiceInsertMetadata||metadataServiceBrowseMyMetadata){this.metadataServiceMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("MyAccount.Browser.MetadataService"),tabTip:Openwis.i18n("MyAccount.Browser.MetadataService")}]});
if(metadataServiceCreateMetadata){this.metadataServiceMenu.add(this.getMetadataServiceCreateMetadataMenu())
}if(metadataServiceInsertMetadata){this.metadataServiceMenu.add(this.getMetadataServiceInsertMetadataMenu())
}if(metadataServiceBrowseMyMetadata){this.metadataServiceMenu.add(this.getMetadataServiceBrowseMyMetadata())
}}}return this.metadataServiceMenu
},getMetadataServiceCreateMetadataMenu:function(){if(!this.metadataServiceCreateMetadataMenu){this.metadataServiceCreateMetadataMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.MetadataService.Create"),listeners:{activate:function(ct){ct.add(new Openwis.Common.Metadata.Create());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceCreateMetadataMenu
},getMetadataServiceInsertMetadataMenu:function(){if(!this.metadataServiceInsertMetadataMenu){this.metadataServiceInsertMetadataMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.MetadataService.Insert"),listeners:{activate:function(ct){ct.add(new Openwis.Common.Metadata.Insert());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceInsertMetadataMenu
},getMetadataServiceBrowseMyMetadata:function(){if(!this.metadataServiceBrowseMyMetadataMenu){this.metadataServiceBrowseMyMetadataMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.MetadataService.Browse"),listeners:{activate:function(ct){ct.add(new Openwis.Common.Metadata.MonitorCatalog({isAdmin:false}));
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceBrowseMyMetadataMenu
},getTrackMyRequestsMenu:function(){if(!this.trackMyRequestsMenu){var trackMyRequestsAdhocs=this.isServiceAccessible("xml.follow.my.adhocs")&&this.isServiceAccessible("xml.follow.my.remote.adhocs");
var trackMyRequestsSubscriptions=this.isServiceAccessible("xml.follow.my.subscriptions")&&this.isServiceAccessible("xml.follow.my.remote.subscriptions");
var trackMyRequestsMssFss=this.isServiceAccessible("allowedMSSFSS")&&this.isServiceAccessible("xml.follow.my.mssfss.subscriptions")&&this.isServiceAccessible("xml.follow.my.remote.mssfss.subscriptions");
if(trackMyRequestsAdhocs||trackMyRequestsSubscriptions||trackMyRequestsMssFss){this.trackMyRequestsMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("MyAccount.Browser.TrackMyRequests"),tabTip:Openwis.i18n("MyAccount.Browser.TrackMyRequests")}]});
if(trackMyRequestsAdhocs){this.trackMyRequestsMenu.add(this.getTrackMyRequestsAdhocsMenu())
}if(trackMyRequestsSubscriptions){this.trackMyRequestsMenu.add(this.getTrackMyRequestsSubscriptionsMenu())
}if(trackMyRequestsMssFss){this.trackMyRequestsMenu.add(this.getTrackMyRequestsMssFssMenu())
}}}return this.trackMyRequestsMenu
},getTrackMyRequestsAdhocsMenu:function(){if(!this.trackMyRequestsAdhocsMenu){this.trackMyRequestsAdhocsMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.TrackMyRequests.Request"),listeners:{activate:function(ct){ct.add(new Openwis.MyAccount.TrackMyRequests.TrackMyAdhocs());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.trackMyRequestsAdhocsMenu
},getTrackMyRequestsSubscriptionsMenu:function(){if(!this.trackMyRequestsSubscriptionsMenu){this.trackMyRequestsSubscriptionsMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.TrackMyRequests.Subscription"),listeners:{activate:function(ct){ct.add(new Openwis.MyAccount.TrackMyRequests.TrackMySubscriptions());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.trackMyRequestsSubscriptionsMenu
},getTrackMyRequestsMssFssMenu:function(){if(!this.trackMyRequestsMssFssMenu){this.trackMyRequestsMssFssMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.TrackMyRequests.MSSFSS"),listeners:{activate:function(ct){ct.add(new Openwis.MyAccount.TrackMyRequests.TrackMyMSSFSSSubscriptions());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.trackMyRequestsMssFssMenu
},getPersonalInformationMenu:function(){if(!this.personalInformationMenu){var userInfoAccessible=this.isServiceAccessible("xml.user.saveSelf");
var changePswd=this.isServiceAccessible("xml.user.changePassword");
if(userInfoAccessible||changePswd){this.personalInformationMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:"Personal information",tabTip:"Personal information"}]});
if(userInfoAccessible){this.personalInformationMenu.add(this.getPersonalInformationUserInformationMenu())
}if(changePswd){this.personalInformationMenu.add(this.getPersonalInformationChangeMyPasswordMenu())
}}}return this.personalInformationMenu
},getPersonalInformationUserInformationMenu:function(){if(!this.personalInformationUserInformationMenu){this.personalInformationUserInformationMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.PersonalInformation.UserInfo"),autoScroll:true,listeners:{activate:function(ct){ct.add(new Openwis.Common.User.UserInformation({hidePassword:true}));
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.personalInformationUserInformationMenu
},getPersonalInformationChangeMyPasswordMenu:function(){if(!this.personalInformationChangeMyPasswordMenu){this.personalInformationChangeMyPasswordMenu=new Ext.Panel({title:Openwis.i18n("MyAccount.Browser.PersonalInformation.ChangeMyPassword"),listeners:{activate:function(ct){ct.add(new Openwis.Common.User.ChangePassword());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.personalInformationChangeMyPasswordMenu
}});Ext.ns("Openwis.MyAccount");
Openwis.MyAccount.Viewport=Ext.extend(Ext.Viewport,{initComponent:function(){Ext.apply(this,{border:false,layout:"fit",autoScroll:true,listeners:{afterlayout:function(){this.relayoutViewport(true,true)
},scope:this}});
Openwis.MyAccount.Viewport.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.getViewportPanel().add(this.getWestPanel());
this.getCenterPanel().add(this.getHeaderPanel());
this.getContentPanel().add(this.getBrowserPanel());
this.getCenterPanel().add(this.getContentPanel());
this.getViewportPanel().add(this.getCenterPanel());
this.getViewportPanel().add(this.getEastPanel());
this.add(this.getViewportPanel())
},getViewportPanel:function(){if(!this.viewportPanel){this.viewportPanel=new Ext.Panel({layout:"border",border:false,autoScroll:false,cls:"viewportCls"})
}return this.viewportPanel
},getHeaderPanel:function(){if(!this.headerPanel){this.headerPanel=new Ext.Container({region:"north",border:false,contentEl:"header",cls:"headerCtCls"})
}return this.headerPanel
},getContentPanel:function(){if(!this.contentPanel){this.contentPanel=new Ext.Panel({region:"center",autoScroll:true,border:false,layout:"fit"})
}return this.contentPanel
},getBrowserPanel:function(){if(!this.browserPanel){this.browserPanel=new Openwis.MyAccount.Browser({width:993,listeners:{tabchange:function(){this.relayoutViewport(true,true)
},guiChanged:function(){this.relayoutViewport(true,true)
},scope:this}})
}return this.browserPanel
},getCenterPanel:function(){if(!this.centerPanel){this.centerPanel=new Ext.Panel({cls:"body-center-panel",region:"center",border:false,width:993,layout:"border"})
}return this.centerPanel
},getWestPanel:function(){if(!this.westPanel){this.westPanel=new Ext.Container({cls:"body-west-panel",region:"west",border:false,html:"&nbsp;"})
}return this.westPanel
},getEastPanel:function(){if(!this.eastPanel){this.eastPanel=new Ext.Container({cls:"body-east-panel",region:"east",border:false,html:"&nbsp;"})
}return this.eastPanel
},relayoutViewport:function(relayoutWidth,relayoutHeight){this.suspendEvents();
var browserPanelHeight=350;
var headerHeight=this.getBrowserPanel().header.getHeight();
var activeTab=this.getBrowserPanel().activeGroup.activeTab;
if(activeTab){var items=activeTab.items;
var activeComp=activeTab.items.items[0];
if(activeComp){browserPanelHeight=activeComp.getHeight()
}}var bodyHeight=this.getHeight();
if(headerHeight>browserPanelHeight){browserPanelHeight=headerHeight
}var height=browserPanelHeight+150;
if(relayoutWidth){var contentWidth=993;
var size=this.getEl().getViewSize(),w=size.width;
var westP=this.getWestPanel();
var eastP=this.getEastPanel();
var centerP=this.getCenterPanel();
if(w<contentWidth){westP.setWidth(0);
eastP.setWidth(0)
}else{var panelSideWidth=(w-contentWidth)/2;
if(height>bodyHeight){panelSideWidth-=8
}westP.setWidth(panelSideWidth);
eastP.setWidth(panelSideWidth)
}if(height>bodyHeight){w-=17
}this.getViewportPanel().boxMaxWidth=w;
this.doLayout()
}if(relayoutHeight){this.getViewportPanel().boxMinHeight=height;
this.doLayout()
}this.resumeEvents()
}});Ext.onReady(function(){Ext.QuickTips.init();
var myAccountViewport=new Openwis.MyAccount.Viewport()
});