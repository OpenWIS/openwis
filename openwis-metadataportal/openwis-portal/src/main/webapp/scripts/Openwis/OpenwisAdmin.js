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
}});Ext.ns("Openwis.Admin.MetaInfo");
Openwis.Admin.MetaInfo.Manage=Ext.extend(Ext.Window,{ingestionService:"/xml.management.cache.configure.ingest",feedingService:"/xml.management.cache.configure.feed",initComponent:function(){Ext.apply(this,{title:"Edit MetaInfo ...",layout:"fit",width:650,height:450,modal:true,closeAction:"close"});
Openwis.Admin.MetaInfo.Manage.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.metainfo.get",params:this.metadataURNs,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("metaInfoSaved");
if(this.multiple){this.add(this.getMultipleMetaInfoFormPanel())
}else{this.add(this.getSingleMetaInfoFormPanel())
}this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.show()
},getSingleMetaInfoFormPanel:function(){if(!this.metaInfoFormPanel){this.metaInfoFormPanel=new Ext.Panel({layout:"table",layoutConfig:{columns:4,tableAttrs:{style:{width:"100%",padding:"20px"}}},border:false});
var selectedProductMetadata=this.config.productsMetadata[0];
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.MetadataURN")+":",colspan:2}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.urn),colspan:2}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.Title")+":",colspan:2}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.title),colspan:2}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.Originator")+":",colspan:2}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.originator),colspan:2}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:"<br>",colspan:4}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.GTSCategory")+":"}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Ext.util.Format.htmlEncode(selectedProductMetadata.gtsCategory)}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenGtsCategory")+":"}));
this.metaInfoFormPanel.add(this.getOverGtsCategoryTextField());
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.DataPolicy")+":"}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:selectedProductMetadata.dataPolicy}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenDataPolicy")+":"}));
this.metaInfoFormPanel.add(this.getDataPoliciesComboBox());
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.FNCPattern")+":"}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:selectedProductMetadata.fncPattern}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverridenFNCPattern")+":"}));
this.metaInfoFormPanel.add(this.getOverFncPatternTextField());
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.GTSpriority")+":"}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:""+selectedProductMetadata.priority}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenGTSpriority")+":"}));
this.metaInfoFormPanel.add(this.getOverGtsPriorityTextField());
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.FileExtension")+":"}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:selectedProductMetadata.fileExtension}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenFileExtension")+":"}));
this.metaInfoFormPanel.add(this.getOverFileExtensionTextField());
this.metaInfoFormPanel.add(this.getSimpleContainer({html:"<br>",colspan:4}));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.Ingested")+":"}));
this.metaInfoFormPanel.add(new Ext.Button(this.getIngestionFilterNewAction(selectedProductMetadata.urn)));
this.metaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.Fed")+":"}));
this.metaInfoFormPanel.add(new Ext.Button(this.getFeedingFilterNewAction(selectedProductMetadata.urn)))
}return this.metaInfoFormPanel
},getIngestionFilterNewAction:function(regex){if(!this.ingestionFilterNewAction){this.ingestionFilterNewAction=new Ext.Action({text:"Add to ingestion filter",disabled:false,scope:this,handler:function(){this.addFilter(regex,this.ingestionService,"Ingestion")
}})
}return this.ingestionFilterNewAction
},getFeedingFilterNewAction:function(regex){if(!this.feedingFilterNewAction){this.feedingFilterNewAction=new Ext.Action({text:"Add to feeding filter",disabled:false,scope:this,handler:function(){this.addFilter(regex,this.feedingService,"Feeding")
}})
}return this.feedingFilterNewAction
},addFilter:function(regex,service,type){var newFilter={};
newFilter.regex="^"+regex+"$";
newFilter.description="Description";
var filterDialog=new Openwis.Admin.DataService.FilterInputDialog({operationMode:"New",filterType:type,selectedFilter:newFilter,locationService:service,listeners:{filterSaved:function(msg,isError){if(isError){Ext.Msg.show({title:"Add filter",msg:msg,buttons:Ext.Msg.OK,scope:this,icon:Ext.MessageBox.ERROR})
}else{Ext.Msg.show({title:"Add filter",msg:msg,buttons:Ext.Msg.OK,scope:this,icon:Ext.MessageBox.INFO})
}},scope:this}});
filterDialog.show()
},getMultipleMetaInfoFormPanel:function(){if(!this.multipleMetaInfoFormPanel){this.multipleMetaInfoFormPanel=new Ext.Panel({layout:"table",layoutConfig:{columns:3,tableAttrs:{style:{width:"100%",padding:"20px"}}},border:false});
this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenGtsCategory")+":"}));
this.multipleMetaInfoFormPanel.add(this.getOverGtsCategoryTextField());
this.multipleMetaInfoFormPanel.add(new Ext.Button(this.getResetAllGtsCategoriesAction()));
this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:"<br><br>",colspan:3}));
this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenDataPolicy")+":"}));
this.multipleMetaInfoFormPanel.add(this.getDataPoliciesComboBox());
this.multipleMetaInfoFormPanel.add(new Ext.Button(this.getResetAllDataPoliciesAction()));
this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:"<br><br>",colspan:3}));
this.multipleMetaInfoFormPanel.add(this.getSimpleContainer({html:Openwis.i18n("Metadata.MetaInfo.OverriddenGTSpriority")+":"}));
this.multipleMetaInfoFormPanel.add(this.getOverGtsPriorityTextField());
this.multipleMetaInfoFormPanel.add(new Ext.Button(this.getResetAllPrioritiesAction()))
}return this.multipleMetaInfoFormPanel
},getSimpleContainer:function(config){return new Ext.Container({html:config.html,border:config.border||false,colspan:config.colspan||1,style:{padding:config.padding||"5px"}})
},getOverGtsCategoryTextField:function(){if(!this.overGtsCategoryTextField){this.overGtsCategoryTextField=new Ext.form.TextField({name:"overridenGtsCategory",value:this.config.productsMetadata[0].overridenGtsCategory})
}return this.overGtsCategoryTextField
},getOverFncPatternTextField:function(){if(!this.overFncPatternTextField){this.overFncPatternTextField=new Ext.form.TextField({name:"overridenFncPattern",value:this.config.productsMetadata[0].overridenFncPattern})
}return this.overFncPatternTextField
},getOverGtsPriorityTextField:function(){if(!this.overGtsPriorityTextField){this.overGtsPriorityTextField=new Ext.form.TextField({name:"overridenPriority",value:this.multiple?"":this.config.productsMetadata[0].overridenPriority})
}return this.overGtsPriorityTextField
},getOverFileExtensionTextField:function(){if(!this.overFileExtensionTextField){this.overFileExtensionTextField=new Ext.form.TextField({name:"overridenFileExtension",value:this.config.productsMetadata[0].overridenFileExtension})
}return this.overFileExtensionTextField
},getDataPoliciesComboBox:function(){if(!this.dataPoliciesComboBox){var dataPoliciesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.dataPoliciesComboBox=new Ext.form.ComboBox({name:"overridenDataPolicy",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:dataPoliciesStore,editable:false,width:250,displayField:"name",valueField:"id"});
this.dataPoliciesComboBox.getStore().loadData(this.config.dataPolicies);
if(!this.multiple){this.dataPoliciesComboBox.getStore().insert(0,[new Ext.data.Record({id:"-1",name:Openwis.i18n("Metadata.MetaInfo.NoOverridenDataPolicy")})]);
if(!Ext.isEmpty(this.config.productsMetadata[0].overridenDataPolicy)){this.dataPoliciesComboBox.setValue(this.config.productsMetadata[0].overridenDataPolicy)
}}}return this.dataPoliciesComboBox
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){var params={productsMetadata:[],dataPolicies:[]};
if(this.multiple){var overGtsCategory;
if(!this.resetOverridenGtsCategories&&!Ext.isEmpty(this.getOverGtsCategoryTextField().getValue().trim())){overGtsCategory=this.getOverGtsCategoryTextField().getValue().trim()
}else{if(this.resetOverridenGtsCategories){overGtsCategory=-1
}}var overPriority;
if(!this.resetOverridenPriorities&&!Ext.isEmpty(this.getOverGtsPriorityTextField().getValue().trim())){overPriority=this.getOverGtsPriorityTextField().getValue().trim()
}else{if(this.resetOverridenPriorities){overPriority=-1
}}var overDP;
if(!this.resetOverridenDPs&&!Ext.isEmpty(this.getDataPoliciesComboBox().getRawValue())){overDP={id:this.getDataPoliciesComboBox().getValue(),name:this.getDataPoliciesComboBox().getRawValue()}
}else{if(this.resetOverridenDPs){overDP={id:"-1"}
}}if(overGtsCategory||overPriority||overDP){Ext.each(this.metadataURNs,function(urn,index,urns){var tmpPM={};
tmpPM.urn=urn;
if(overGtsCategory){tmpPM.overridenGtsCategory=overGtsCategory
}if(overPriority){tmpPM.overridenPriority=overPriority
}params.productsMetadata.push(tmpPM)
},this);
if(overDP){params.dataPolicies.push(overDP)
}}}else{var pm=this.config.productsMetadata[0];
var tmpPM={};
if(this.getOverGtsCategoryTextField().getValue().trim()!=pm.overridenGtsCategory&&(!Ext.isEmpty(this.getOverGtsCategoryTextField().getValue().trim())||pm.overridenGtsCategory)){if(Ext.isEmpty(this.getOverGtsCategoryTextField().getValue().trim())){tmpPM.overridenGtsCategory="-1"
}else{tmpPM.overridenGtsCategory=this.getOverGtsCategoryTextField().getValue().trim()
}}if(this.getOverFncPatternTextField().getValue().trim()!=pm.overridenFncPattern&&(!Ext.isEmpty(this.getOverFncPatternTextField().getValue().trim())||pm.overridenFncPattern)){if(Ext.isEmpty(this.getOverFncPatternTextField().getValue().trim())){tmpPM.overridenFncPattern="-1"
}else{tmpPM.overridenFncPattern=this.getOverFncPatternTextField().getValue().trim()
}}if(this.getOverGtsPriorityTextField().getValue().trim()!=pm.overridenPriority&&(!Ext.isEmpty(this.getOverGtsPriorityTextField().getValue().trim())||pm.overridenPriority)){if(Ext.isEmpty(this.getOverGtsPriorityTextField().getValue().trim())){tmpPM.overridenPriority="-1"
}else{tmpPM.overridenPriority=this.getOverGtsPriorityTextField().getValue().trim()
}}if(this.getOverFileExtensionTextField().getValue().trim()!=pm.overridenFileExtension&&(!Ext.isEmpty(this.getOverFileExtensionTextField().getValue().trim())||pm.overridenFileExtension)){if(Ext.isEmpty(this.getOverFileExtensionTextField().getValue().trim())){tmpPM.overridenFileExtension="-1"
}else{tmpPM.overridenFileExtension=this.getOverFileExtensionTextField().getValue().trim()
}}if(!Ext.isEmpty(tmpPM.overridenGtsCategory)||!Ext.isEmpty(tmpPM.overridenFncPattern)||!Ext.isEmpty(tmpPM.overridenPriority)||!Ext.isEmpty(tmpPM.overridenFileExtension)){tmpPM.urn=pm.urn;
params.productsMetadata.push(tmpPM)
}if((Ext.isEmpty(pm.overridenDataPolicy)&&!Ext.isEmpty(this.getDataPoliciesComboBox().getRawValue())&&this.getDataPoliciesComboBox().getValue()!=-1)||(!Ext.isEmpty(pm.overridenDataPolicy)&&this.getDataPoliciesComboBox().getRawValue().trim()!=pm.overridenDataPolicy)||(!Ext.isEmpty(pm.overridenDataPolicy)&&this.getDataPoliciesComboBox().getValue()===-1)){params.dataPolicies.push({id:this.getDataPoliciesComboBox().getValue(),name:this.getDataPoliciesComboBox().getRawValue()});
if(Ext.isEmpty(params.productsMetadata)){params.productsMetadata.push({urn:pm.urn})
}}}if(Ext.isEmpty(params.dataPolicies)&&Ext.isEmpty(params.productsMetadata)){this.close()
}else{var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.metainfo.save",params:params,listeners:{success:function(config){this.fireEvent("metaInfoSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getResetAllGtsCategoriesAction:function(){if(!this.resetAllCategoriesAction){this.resetAllCategoriesAction=new Ext.Action({text:Openwis.i18n("Metadata.MetaInfo.ResetOverriddenGTSCategories"),enableToggle:true,scope:this,toggleHandler:function(button,state){this.getOverGtsCategoryTextField().setDisabled(state);
this.resetOverridenGtsCategories=state
}})
}return this.resetAllCategoriesAction
},getResetAllDataPoliciesAction:function(){if(!this.resetAllDpAction){this.resetAllDpAction=new Ext.Action({text:Openwis.i18n("Metadata.MetaInfo.ResetOverriddenDataPolicies"),enableToggle:true,scope:this,toggleHandler:function(button,state){this.getDataPoliciesComboBox().setDisabled(state);
this.resetOverridenDPs=state
}})
}return this.resetAllDpAction
},getResetAllPrioritiesAction:function(){if(!this.resetAllPrioritiesAction){this.resetAllPrioritiesAction=new Ext.Action({text:Openwis.i18n("Metadata.MetaInfo.ResetOverriddenGTSPriorities"),enableToggle:true,scope:this,toggleHandler:function(button,state){this.getOverGtsPriorityTextField().setDisabled(state);
this.resetOverridenPriorities=state
}})
}return this.resetAllPrioritiesAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
}});Ext.ns("Openwis.Admin.DataPolicy");
Openwis.Admin.DataPolicy.Manage=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Security.DataPolicy.Manage.Title"),layout:"fit",width:750,height:600,modal:true,closeAction:"close"});
Openwis.Admin.DataPolicy.Manage.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var params={};
if(this.isEdition()){params.name=this.editDataPolicyName
}var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.datapolicy.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("dataPolicySaved");
this.add(this.getDataPolicyFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.dataPolicy.name);
this.getDescriptionTextArea().setValue(this.config.dataPolicy.description)
}this.show()
},getDataPolicyFormPanel:function(){if(!this.dataPolicyFormPanel){this.dataPolicyFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:250});
this.dataPolicyFormPanel.add(this.getNameTextField());
this.dataPolicyFormPanel.add(this.getDescriptionTextArea());
this.dataPolicyFormPanel.add(this.getAliasesGrid());
this.dataPolicyFormPanel.add(new Ext.form.Label({fieldLabel:Openwis.i18n("Security.DataPolicy.Manage.OpAllowedLabel")}));
this.dataPolicyFormPanel.add(this.getOperationsAllowedGrid())
}return this.dataPolicyFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.DataPolicy.Manage.Name"),name:"name",allowBlank:false,disabled:this.isEdition(),width:250})
}return this.nameTextField
},getDescriptionTextArea:function(){if(!this.descriptionTextArea){this.descriptionTextArea=new Ext.form.TextArea({fieldLabel:Openwis.i18n("Security.DataPolicy.Manage.Description"),allowBlank:true,name:"description",width:250})
}return this.descriptionTextArea
},getOperationsAllowedGrid:function(){if(!this.operationsAllowedGrid){var columns=[];
var fieldsStore=[];
columns.push(new Ext.grid.Column({id:"group",header:Openwis.i18n("Security.DataPolicy.Manage.Header.Groups"),dataIndex:"groupName",sortable:true}));
fieldsStore.push(new Ext.data.Field({name:"groupName",mapping:"group.name"}));
for(var i=0;
i<this.config.operations.length;
i++){var operation=(this.config.operations)[i];
if(operation.name!="FTPSecured"){columns.push(new Ext.ux.grid.CheckColumn({id:operation.name,header:operation.name,dataIndex:operation.name,sortable:false,align:"center"}))
}fieldsStore.push(new Ext.data.Field({name:operation.name,type:"boolean",convert:function(v,record){for(var i=0;
i<record.privilegesPerOp.length;
i++){if(this.name==record.privilegesPerOp[i].operation.name){return record.privilegesPerOp[i].authorized
}}return false
}}))
}var colModel=new Ext.grid.ColumnModel({defaults:{menuDisabled:true,width:70},columns:columns});
var operationsAllowedStore=new Ext.data.JsonStore({autoDestroy:true,idIndex:0,fields:fieldsStore});
operationsAllowedStore.loadData(this.config.dataPolicy.dpOpPerGroup);
this.operationsAllowedGrid=new Ext.grid.GridPanel({id:"operationsAllowedGrid",height:200,border:true,loadMask:true,colModel:colModel,store:operationsAllowedStore,style:{margin:"0px 10px 0px 10px"},autoExpandColumn:"group"})
}return this.operationsAllowedGrid
},getAliasesGrid:function(){if(!this.aliasesGrid){var aliasesStore=new Ext.data.JsonStore({autoDestroy:true,idIndex:0,fields:[{name:"alias"}]});
aliasesStore.loadData(this.config.dataPolicy.aliases);
this.aliasesGrid=new Ext.grid.GridPanel({height:150,width:250,border:true,loadMask:true,fieldLabel:Openwis.i18n("Security.DataPolicy.Manage.Aliases"),colModel:new Ext.grid.ColumnModel({defaults:{width:120,sortable:true},columns:[{id:"aliases",header:Openwis.i18n("Security.DataPolicy.Header.Aliases"),sortable:true,dataIndex:"alias"}]}),store:aliasesStore,style:{margin:"0px 10px 0px 0px"},autoExpandColumn:"aliases",sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.ownerCt.getAliasesRemoveAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.ownerCt.getAliasesRemoveAction().setDisabled(sm.getCount()==0)
}}})});
this.aliasesGrid.addButton(new Ext.Button(this.getAliasesAddAction()));
this.aliasesGrid.addButton(new Ext.Button(this.getAliasesRemoveAction()))
}return this.aliasesGrid
},getAliasesAddAction:function(){if(!this.aliasesAddAction){this.aliasesAddAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Add"),scope:this,handler:function(){var msgPrompt=Ext.Msg.prompt(Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Title"),Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.AliasLabel"),function(btn,text){if(btn=="ok"){if(Ext.isEmpty(text)){Ext.Msg.show({title:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error"),msg:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error.NameMandatory"),buttons:Ext.Msg.OK,icon:Ext.MessageBox.ERROR})
}else{if(text.trim()==this.getNameTextField().getValue().trim()){Ext.Msg.show({title:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error"),msg:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error.NameNotEquals"),buttons:Ext.Msg.OK,icon:Ext.MessageBox.ERROR})
}else{if(this.getAliasesGrid().getStore().findExact("alias",text.trim())!=-1){Ext.Msg.show({title:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error"),msg:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error.AlreadyExists"),buttons:Ext.Msg.OK,icon:Ext.MessageBox.ERROR})
}else{this.getAliasesGrid().getStore().add(new Ext.data.Record({alias:text}))
}}}}},this)
}})
}return this.aliasesAddAction
},getAliasesRemoveAction:function(){if(!this.aliasesRemoveAction){this.aliasesRemoveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Remove"),scope:this,disabled:true,handler:function(){var selection=this.getAliasesGrid().getSelectionModel().getSelections();
this.getAliasesGrid().getStore().remove(selection)
}})
}return this.aliasesRemoveAction
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.datapolicy.save",params:this.getDataPolicy(),listeners:{success:function(config){this.fireEvent("dataPolicySaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},isValid:function(){if(this.getDataPolicyFormPanel().getForm().isValid()){if(this.getAliasesGrid().getStore().findExact("alias",this.getNameTextField().getValue().trim())!=-1){Ext.Msg.show({title:Openwis.i18n("Security.DataPolicy.Manage.Aliases.Add.Error"),msg:Openwis.i18n("The alias cannot be equals to the data policy name."),buttons:Ext.Msg.OK,icon:Ext.MessageBox.ERROR});
return false
}return true
}else{return false
}},getDataPolicy:function(){var dataPolicy={};
if(this.isEdition()){dataPolicy.id=this.config.dataPolicy.id
}dataPolicy.name=this.getNameTextField().getValue();
dataPolicy.description=this.getDescriptionTextArea().getValue();
dataPolicy.aliases=[];
this.getAliasesGrid().getStore().each(function(rec){dataPolicy.aliases.push(rec.data)
},this);
dataPolicy.dpOpPerGroup=[];
for(var i=0;
i<this.getOperationsAllowedGrid().getStore().getCount();
i++){var rec=this.getOperationsAllowedGrid().getStore().getAt(i);
var opAllowed=rec.json;
if(rec.dirty){Ext.iterate(rec.modified,function(key,value){for(var i=0;
i<opAllowed.privilegesPerOp.length;
i++){if(opAllowed.privilegesPerOp[i].operation.name==key){opAllowed.privilegesPerOp[i].authorized=rec.data[key];
break
}}})
}dataPolicy.dpOpPerGroup.push(opAllowed)
}return dataPolicy
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.DataPolicy");
Openwis.Admin.DataPolicy.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.DataPolicy.All.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getDataPolicyGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.DataPolicy.Title"),cls:"administrationTitle1"})
}return this.header
},getDataPolicyGrid:function(){if(!this.dataPolicyGrid){this.dataPolicyGrid=new Ext.grid.GridPanel({id:"dataPolicyGrid",height:400,border:true,store:this.getDataPolicyStore(),loadMask:true,columns:[{id:"name",header:Openwis.i18n("Security.DataPolicy.Header.DataPolicy"),dataIndex:"name",sortable:true},{id:"description",header:Openwis.i18n("Security.DataPolicy.Header.Description"),dataIndex:"description",sortable:true},{id:"aliases",header:Openwis.i18n("Security.DataPolicy.Header.Aliases"),dataIndex:"aliases",sortable:true,renderer:this.renderAliases}],autoExpandColumn:"aliases",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0)
}}})});
this.dataPolicyGrid.addButton(new Ext.Button(this.getNewAction()));
this.dataPolicyGrid.addButton(new Ext.Button(this.getEditAction()));
this.dataPolicyGrid.addButton(new Ext.Button(this.getRemoveAction()));
this.dataPolicyGrid.addButton(new Ext.Button(this.getExportAction()))
}return this.dataPolicyGrid
},getDataPolicyStore:function(){if(!this.dataPolicyStore){this.dataPolicyStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.datapolicy.all",idProperty:"id",fields:[{name:"id"},{name:"name"},{name:"description"},{name:"aliases"}]})
}return this.dataPolicyStore
},getNewAction:function(){if(!this.newAction){this.newAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),scope:this,handler:function(){new Openwis.Admin.DataPolicy.Manage({operationMode:"Create",listeners:{dataPolicySaved:function(){this.getDataPolicyGrid().getStore().reload()
},scope:this}})
}})
}return this.newAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getDataPolicyGrid().getSelectionModel().getSelected();
new Openwis.Admin.DataPolicy.Manage({operationMode:"Edit",editDataPolicyName:selectedRec.get("name"),listeners:{dataPolicySaved:function(){this.getDataPolicyGrid().getStore().reload()
},scope:this}})
}})
}return this.editAction
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getDataPolicyGrid().getSelectionModel().getSelections();
var dataPolicies=[];
Ext.each(selection,function(item,index,allItems){dataPolicies.push({id:item.get("id"),name:item.get("name")})
},this);
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.datapolicy.remove",params:{dataPolicies:dataPolicies},listeners:{success:function(){this.getDataPolicyGrid().getStore().reload()
},scope:this}});
removeHandler.proceed()
}})
}return this.removeAction
},getImportAction:function(){if(!this.importAction){this.importAction=new Ext.Action({text:Openwis.i18n("Security.DataPolicy.Btn.Import"),scope:this,handler:function(){}})
}return this.importAction
},getExportAction:function(){if(!this.exportAction){this.exportAction=new Ext.Action({text:Openwis.i18n("Security.DataPolicy.Btn.Export"),scope:this,handler:function(){window.open("./datapolicy.export","DataPolicies Export")
}})
}return this.exportAction
},renderAliases:function(val){var arr=[];
for(var i=0;
i<val.length;
i++){arr.push(val[i].alias)
}return arr.join()
}});Ext.ns("Openwis.Admin.Harvesting.Harvester");
Openwis.Admin.Harvesting.Harvester.Geonetwork20=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Harvesting.Geonetwork20.Title"),layout:"fit",width:450,height:680,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Admin.Harvesting.Harvester.Geonetwork20.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params=this.editTaskId;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("harvestingTaskSaved");
this.add(this.getTaskFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getHostTextField().setValue(this.config.configuration.host);
this.getPortTextField().setValue(this.config.configuration.port);
this.getServletTextField().setValue(this.config.configuration.servlet);
var username=this.config.configuration.userName;
var password=this.config.configuration.password;
if((username&&username.trim()!="")||(password&&password.trim()!="")){this.getUseAccountCheckBox().setValue(true);
this.getUsernameTextField().setValue(username);
this.getPasswordTextField().setValue(password);
this.getUsernameTextField().show();
this.getPasswordTextField().show()
}else{this.getUseAccountCheckBox().setValue(false)
}if(this.config.runMode.recurrent){this.getRunModeRadioGroup().setValue("RECURRENT");
var recurrencePeriod=this.config.runMode.recurrentPeriod;
var days=Math.floor(recurrencePeriod/(24*3600));
if(days>0){recurrencePeriod%=(24*3600);
this.getFrequencyRecurrentDayTextField().setValue(days)
}var hours=Math.floor(recurrencePeriod/3600);
if(hours>0){this.getFrequencyRecurrentHourTextField().setValue(hours);
recurrencePeriod%=3600
}var minuts=recurrencePeriod/60;
if(minuts>0){this.getFrequencyRecurrentMinuteTextField().setValue(minuts)
}this.getFrequencyRunModeCompositeField().show()
}else{this.getRunModeRadioGroup().setValue("USER_TRIGERRED")
}this.getProviderConfigurationOptionsSearchAnyTextField().setValue(this.config.configuration.any);
this.getProviderConfigurationOptionsSearchTitleTextField().setValue(this.config.configuration.title);
this.getProviderConfigurationOptionsSearchAbstractTextField().setValue(this.config.configuration["abstract"]);
this.getProviderConfigurationOptionsSearchKeywordsTextField().setValue(this.config.configuration.themekey);
this.getProviderConfigurationOptionsCheckBoxGroup().setValue("digital",this.config.configuration.digital);
this.getProviderConfigurationOptionsCheckBoxGroup().setValue("hardcopy",this.config.configuration.hardcopy);
this.getValidationCombobox().setValue(this.config.validationMode);
if(this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}}this.show()
},getTaskFormPanel:function(){if(!this.taskFormPanel){this.taskFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,border:false});
this.taskFormPanel.add(this.getNameTextField());
this.taskFormPanel.add(this.getHostTextField());
this.taskFormPanel.add(this.getPortTextField());
this.taskFormPanel.add(this.getServletTextField());
this.taskFormPanel.add(this.getUseAccountCheckBox());
this.taskFormPanel.add(this.getUseAccountCompositeField());
this.taskFormPanel.add(this.getProviderConfigurationFieldSet());
this.taskFormPanel.add(this.getRunModeRadioGroup());
this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
this.taskFormPanel.add(this.getBackupsCombobox());
this.taskFormPanel.add(this.getValidationCombobox());
this.taskFormPanel.add(this.getCategoryCombobox())
}return this.taskFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getHostTextField:function(){if(!this.hostTextField){this.hostTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Host"),name:"host",allowBlank:false,width:150})
}return this.hostTextField
},getPortTextField:function(){if(!this.portTextField){this.portTextField=new Ext.form.NumberField({fieldLabel:Openwis.i18n("Harvesting.Port"),name:"port",allowBlank:false,width:60})
}return this.portTextField
},getServletTextField:function(){if(!this.servletTextField){this.servletTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Servlet"),name:"servlet",allowBlank:false,width:100})
}return this.servletTextField
},getUseAccountCheckBox:function(){if(!this.useAccountCheckBox){this.useAccountCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Harvesting.Account.Use"),name:"useAccount",width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getUseAccountCompositeField().hide()
}else{this.getUseAccountCompositeField().show()
}},scope:this}})
}return this.useAccountCheckBox
},getUseAccountCompositeField:function(){if(!this.useAccountCompositeField){this.useAccountCompositeField=new Ext.form.CompositeField({name:"useAccount",hidden:true,allowBlank:false,width:330,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.UserName")+":",cls:"formItems"}),this.getUsernameTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.Password")+":",cls:"formItems"}),this.getPasswordTextField()]})
}return this.useAccountCompositeField
},getUsernameTextField:function(){if(!this.usernameTextField){this.usernameTextField=new Ext.form.TextField({name:"username",allowBlank:true,width:100})
}return this.usernameTextField
},getPasswordTextField:function(){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({name:"password",allowBlank:true,inputType:"password",width:100})
}return this.passwordTextField
},getRunModeRadioGroup:function(){if(!this.runModeRadioGroup){this.runModeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("Harvesting.Options.RunMode"),name:"runMode",allowBlank:false,columns:1,width:150,items:[{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.UserTriggerred"),name:"runMode",inputValue:"USER_TRIGERRED",checked:true,id:"USER_TRIGERRED"},{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.Recurrent"),name:"runMode",inputValue:"RECURRENT",id:"RECURRENT"}],listeners:{change:function(group,radioChecked){if(radioChecked.inputValue=="USER_TRIGERRED"){this.getFrequencyRunModeCompositeField().hide()
}else{this.getFrequencyRunModeCompositeField().show()
}},scope:this}})
}return this.runModeRadioGroup
},getFrequencyRunModeCompositeField:function(){if(!this.frequencyRunModeCompositeField){this.frequencyRunModeCompositeField=new Ext.form.CompositeField({name:"recurrentRunMode",hidden:true,allowBlank:false,width:350,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency")+":",cls:"formItems"}),this.getFrequencyRecurrentDayTextField(),this.getFrequencyRecurrentHourTextField(),this.getFrequencyRecurrentMinuteTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency.Detail"),cls:"formItems"})]})
}return this.frequencyRunModeCompositeField
},getFrequencyRecurrentDayTextField:function(){if(!this.frequencyRecurrentDayTextField){this.frequencyRecurrentDayTextField=new Ext.form.TextField({name:"day",allowBlank:true,width:50})
}return this.frequencyRecurrentDayTextField
},getFrequencyRecurrentHourTextField:function(){if(!this.frequencyRecurrentHourTextField){this.frequencyRecurrentHourTextField=new Ext.form.TextField({name:"hour",allowBlank:true,width:50})
}return this.frequencyRecurrentHourTextField
},getFrequencyRecurrentMinuteTextField:function(){if(!this.frequencyRecurrentMinuteTextField){this.frequencyRecurrentMinuteTextField=new Ext.form.TextField({name:"minute",allowBlank:true,width:50})
}return this.frequencyRecurrentMinuteTextField
},getCategoryCombobox:function(){if(!this.categoryCombobox){var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.category){this.getCategoryCombobox().setValue(this.config.category.id)
}},scope:this}});
this.categoryCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Category.After.Harvest"),store:categoryStore,valueField:"id",displayField:"name",name:"category",mode:"local",allowBlank:false,typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.categoryCombobox
},getBackupsCombobox:function(){if(!this.backupsCombobox){var backupsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.all.backup.centres",idProperty:"name",autoLoad:true,fields:[{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}},scope:this}});
this.backupsCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Backup"),store:backupsStore,valueField:"name",displayField:"name",name:"backups",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200,height:120})
}return this.backupsCombobox
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("Harvesting.Validation")})
}return this.validationCombobox
},getProviderConfigurationFieldSet:function(){if(!this.providerConfigurationFieldSet){this.providerConfigurationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Harvesting.ProviderConfiguration"),autoHeight:true,collapsed:true,collapsible:true});
this.providerConfigurationFieldSet.add(new Ext.Container({html:Openwis.i18n("Harvesting.ProviderConfiguration.Fetch.Note"),border:false,cls:"infoMsg",style:{margin:"0px 0px 5px 0px"}}));
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchAnyTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchTitleTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchAbstractTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchKeywordsTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsCheckBoxGroup());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationSitesCombobox());
this.providerConfigurationFieldSet.addButton(new Ext.Button(this.getFetchRemoteInfoAction()))
}return this.providerConfigurationFieldSet
},getProviderConfigurationOptionsSearchAnyTextField:function(){if(!this.providerConfigurationOptionsSearchAnyTextField){this.providerConfigurationOptionsSearchAnyTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Any"),name:"any",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchAnyTextField
},getProviderConfigurationOptionsSearchTitleTextField:function(){if(!this.providerConfigurationOptionsSearchTitleTextField){this.providerConfigurationOptionsSearchTitleTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Title"),name:"title",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchTitleTextField
},getProviderConfigurationOptionsSearchAbstractTextField:function(){if(!this.providerConfigurationOptionsSearchAbstractTextField){this.providerConfigurationOptionsSearchAbstractTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Abstract"),name:"abstract",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchAbstractTextField
},getProviderConfigurationOptionsSearchKeywordsTextField:function(){if(!this.providerConfigurationOptionsSearchKeywordsTextField){this.providerConfigurationOptionsSearchKeywordsTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Keywords"),name:"keywords",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchKeywordsTextField
},getProviderConfigurationOptionsCheckBoxGroup:function(){if(!this.providerConfigurationOptionsCheckboxGroup){this.providerConfigurationOptionsCheckboxGroup=new Ext.form.CheckboxGroup({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options"),name:"options",columns:2,width:175,columns:1,items:[{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Digital"),name:"digital",id:"digital"},{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Hardcopy"),name:"hardcopy",id:"hardcopy"}]})
}return this.providerConfigurationOptionsCheckboxGroup
},getProviderConfigurationSitesCombobox:function(){if(!this.providerConfigurationSitesCombobox){this.providerConfigurationSitesCombobox=new Ext.form.ComboBox({store:new Openwis.Data.JeevesJsonStore({idProperty:"id",fields:[{name:"id"},{name:"name"}]}),displayField:"name",valueField:"id",disabled:true,fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Site"),name:"siteId",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.providerConfigurationSitesCombobox
},getFetchRemoteInfoAction:function(){if(!this.fetchRemoteInfoAction){this.fetchRemoteInfoAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Fetch"),scope:this,handler:function(){var url="http://"+this.getHostTextField().getValue()+":"+this.getPortTextField().getValue()+"/"+this.getServletTextField().getValue();
if(url.trim()!=""){var params={content:url};
new Openwis.Handler.Get({url:configOptions.locService+"/xml.get.geonetwork.sources.info",params:params,listeners:{success:function(remoteConfig){this.getProviderConfigurationSitesCombobox().getStore().loadData(remoteConfig);
this.getProviderConfigurationSitesCombobox().setDisabled(false);
if(this.isEdition()&&this.config.configuration.siteId){this.getProviderConfigurationSitesCombobox().setValue(this.config.configuration.siteId)
}},scope:this}}).proceed()
}else{Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Harvesting.ProviderConfiguration.Fetch.Remote.Url.Mandatory"))
}}})
}return this.fetchRemoteInfoAction
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getTaskFormPanel().getForm().isValid()&&this.validate()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.save",params:this.getHarvestingTask(),listeners:{success:function(config){this.fireEvent("harvestingTaskSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getHarvestingTask:function(){var task={};
if(this.isEdition()){task.id=this.editTaskId;
task.status=this.config.status
}else{task.status="ACTIVE"
}task.name=this.getNameTextField().getValue();
task.type="geonetwork20";
if(this.getBackupsCombobox().getValue()!=""){task.backup={name:this.getBackupsCombobox().getValue()}
}task.synchronizationTask=false;
task.incremental=false;
task.validationMode=this.getValidationCombobox().getValue();
task.runMode={};
task.runMode.recurrent=this.getRunModeRadioGroup().getValue().inputValue=="RECURRENT";
if(task.runMode.recurrent){var recurrentDay=Ext.num(this.getFrequencyRecurrentDayTextField().getValue(),0);
var recurrentHour=Ext.num(this.getFrequencyRecurrentHourTextField().getValue(),0);
var recurrentMinute=Ext.num(this.getFrequencyRecurrentMinuteTextField().getValue(),0);
var period=0;
if(recurrentMinute>0){period+=recurrentMinute*60
}if(recurrentHour>0){period+=recurrentHour*3600
}if(recurrentDay>0){period+=recurrentDay*24*3600
}task.runMode.recurrentPeriod=period
}task.configuration={};
task.configuration.host=this.getHostTextField().getValue();
task.configuration.port=this.getPortTextField().getValue();
task.configuration.servlet=this.getServletTextField().getValue();
if(this.getUseAccountCheckBox().checked){task.configuration.userName=this.getUsernameTextField().getValue();
task.configuration.password=this.getPasswordTextField().getValue()
}if(this.getCategoryCombobox().getValue()){task.category={id:this.getCategoryCombobox().getValue()}
}task.configuration.any=this.getProviderConfigurationOptionsSearchAnyTextField().getValue();
task.configuration.title=this.getProviderConfigurationOptionsSearchTitleTextField().getValue();
task.configuration["abstract"]=this.getProviderConfigurationOptionsSearchAbstractTextField().getValue();
task.configuration.themekey=this.getProviderConfigurationOptionsSearchKeywordsTextField().getValue();
var values=this.getProviderConfigurationOptionsCheckBoxGroup().getValue();
Ext.each(values,function(item,index,allItems){if(item.name=="digital"){task.configuration.digital=true
}else{if(item.name=="hardcopy"){task.configuration.hardcopy=true
}}},this);
if(this.isEdition()){if(this.getProviderConfigurationSitesCombobox().disabled){task.configuration.siteId=this.config.configuration.siteId
}else{if(this.getProviderConfigurationSitesCombobox().getValue()!=""){task.configuration.siteId=this.getProviderConfigurationSitesCombobox().getValue()
}}}else{if(this.getProviderConfigurationSitesCombobox().getValue()!=""){task.configuration.siteId=this.getProviderConfigurationSitesCombobox().getValue()
}}return task
},validate:function(){return true
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Harvesting.Harvester");
Openwis.Admin.Harvesting.Harvester.FileSystem=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Harvesting.FileSystem.Title"),layout:"fit",width:450,height:620,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Admin.Harvesting.Harvester.FileSystem.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params=this.editTaskId;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("harvestingTaskSaved");
this.add(this.getTaskFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getDirectoryTextField().setValue(this.config.configuration.dir);
if(this.config.runMode.recurrent){this.getRunModeRadioGroup().setValue("RECURRENT");
var recurrencePeriod=this.config.runMode.recurrentPeriod;
var days=Math.floor(recurrencePeriod/(24*3600));
if(days>0){recurrencePeriod%=(24*3600);
this.getFrequencyRecurrentDayTextField().setValue(days)
}var hours=Math.floor(recurrencePeriod/3600);
if(hours>0){this.getFrequencyRecurrentHourTextField().setValue(hours);
recurrencePeriod%=3600
}var minuts=recurrencePeriod/60;
if(minuts>0){this.getFrequencyRecurrentMinuteTextField().setValue(minuts)
}this.getFrequencyRunModeCompositeField().show()
}else{this.getRunModeRadioGroup().setValue("USER_TRIGERRED")
}this.getStyleSheetComboBox().setValue(this.config.configuration.styleSheet);
this.getValidationCombobox().setValue(this.config.validationMode);
this.getConfigurationOptionsCheckBoxGroup().setValue({recursive:this.config.configuration.recursive,keepLocalIfDeleted:this.config.configuration.keepLocalIfDeleted,localImport:this.config.configuration.localImport});
this.getConfigurationFileTypeRadioGroup().setValue(this.config.configuration.fileType,true)
}this.show()
},getTaskFormPanel:function(){if(!this.taskFormPanel){this.taskFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,border:false});
this.taskFormPanel.add(this.getNameTextField());
this.taskFormPanel.add(this.getDirectoryTextField());
this.taskFormPanel.add(this.getConfigurationFieldSet());
this.taskFormPanel.add(this.getRunModeRadioGroup());
this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
this.taskFormPanel.add(this.getStyleSheetComboBox());
this.taskFormPanel.add(this.getValidationCombobox());
this.taskFormPanel.add(this.getCategoryCombobox())
}return this.taskFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getDirectoryTextField:function(){if(!this.directoryTextField){this.directoryTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Directory"),name:"directory",allowBlank:false,width:300})
}return this.directoryTextField
},getRunModeRadioGroup:function(){if(!this.runModeRadioGroup){this.runModeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("Harvesting.Options.RunMode"),name:"runMode",allowBlank:false,columns:1,width:150,items:[{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.UserTriggerred"),name:"runMode",inputValue:"USER_TRIGERRED",checked:true,id:"USER_TRIGERRED"},{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.Recurrent"),name:"runMode",inputValue:"RECURRENT",id:"RECURRENT"}],listeners:{change:function(group,radioChecked){if(radioChecked.inputValue=="USER_TRIGERRED"){this.getFrequencyRunModeCompositeField().hide()
}else{this.getFrequencyRunModeCompositeField().show()
}},scope:this}})
}return this.runModeRadioGroup
},getFrequencyRunModeCompositeField:function(){if(!this.frequencyRunModeCompositeField){this.frequencyRunModeCompositeField=new Ext.form.CompositeField({name:"recurrentRunMode",hidden:true,allowBlank:false,width:350,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency")+":",cls:"formItems"}),this.getFrequencyRecurrentDayTextField(),this.getFrequencyRecurrentHourTextField(),this.getFrequencyRecurrentMinuteTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency.Detail"),cls:"formItems"})]})
}return this.frequencyRunModeCompositeField
},getFrequencyRecurrentDayTextField:function(){if(!this.frequencyRecurrentDayTextField){this.frequencyRecurrentDayTextField=new Ext.form.TextField({name:"day",allowBlank:true,width:50})
}return this.frequencyRecurrentDayTextField
},getFrequencyRecurrentHourTextField:function(){if(!this.frequencyRecurrentHourTextField){this.frequencyRecurrentHourTextField=new Ext.form.TextField({name:"hour",allowBlank:true,width:50})
}return this.frequencyRecurrentHourTextField
},getFrequencyRecurrentMinuteTextField:function(){if(!this.frequencyRecurrentMinuteTextField){this.frequencyRecurrentMinuteTextField=new Ext.form.TextField({name:"minute",allowBlank:true,width:50})
}return this.frequencyRecurrentMinuteTextField
},getCategoryCombobox:function(){if(!this.categoryCombobox){var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.category){this.getCategoryCombobox().setValue(this.config.category.id)
}},scope:this}});
this.categoryCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Category.After.Harvest"),store:categoryStore,valueField:"id",displayField:"name",name:"category",mode:"local",allowBlank:false,typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.categoryCombobox
},getStyleSheetComboBox:function(){if(!this.styleSheetComboBox){var styleSheetStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.stylesheet.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){store.insert(0,[new Ext.data.Record({id:"NONE",name:Openwis.i18n("Common.List.None")})]);
if(this.isEdition()&&this.config.configuration.styleSheet){this.getStyleSheetComboBox().setValue(this.config.configuration.styleSheet)
}else{this.getStyleSheetComboBox().setValue("NONE")
}},scope:this}});
this.styleSheetComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataBatchImport.StyleSheet"),name:"stylesheet",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:styleSheetStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"})
}return this.styleSheetComboBox
},getConfigurationFieldSet:function(){if(!this.configurationFieldSet){this.configurationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Harvesting.ProviderConfiguration.Options"),autoHeight:true,collapsed:true,collapsible:true});
this.configurationFieldSet.add(this.getConfigurationOptionsCheckBoxGroup());
this.configurationFieldSet.add(this.getConfigurationFileTypeRadioGroup())
}return this.configurationFieldSet
},getConfigurationOptionsCheckBoxGroup:function(){if(!this.configurationOptionsCheckboxGroup){this.configurationOptionsCheckboxGroup=new Ext.form.CheckboxGroup({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options"),name:"options",width:175,columns:1,items:[{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Recursive"),name:"recursive",id:"recursive",checked:false},{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.KeepLocal"),name:"keepLocalIfDeleted",id:"keepLocalIfDeleted",checked:false},{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.LocalImport"),name:"localImport",id:"localImport",checked:false}]})
}return this.configurationOptionsCheckboxGroup
},getConfigurationFileTypeRadioGroup:function(){if(!this.configurationFileTypeRadioGroup){this.configurationFileTypeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("MetadataBatchImport.FileType"),name:"fileType",allowBlank:false,columns:1,width:150,items:[{boxLabel:Openwis.i18n("MetadataBatchImport.FileType.SingleFile"),name:"fileType",inputValue:"single",checked:true,id:"single"},{boxLabel:Openwis.i18n("MetadataBatchImport.FileType.MefFile"),name:"fileType",inputValue:"mef",id:"mef"}]})
}return this.configurationFileTypeRadioGroup
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("Harvesting.Validation")})
}return this.validationCombobox
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getTaskFormPanel().getForm().isValid()&&this.validate()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.save",params:this.getHarvestingTask(),listeners:{success:function(config){this.fireEvent("harvestingTaskSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getHarvestingTask:function(){var task={};
if(this.isEdition()){task.id=this.editTaskId;
task.status=this.config.status
}else{task.status="ACTIVE"
}task.name=this.getNameTextField().getValue();
task.type="localfilesystem";
task.synchronizationTask=false;
task.incremental=false;
task.validationMode=this.getValidationCombobox().getValue();
task.runMode={};
task.runMode.recurrent=this.getRunModeRadioGroup().getValue().inputValue=="RECURRENT";
if(task.runMode.recurrent){var recurrentDay=Ext.num(this.getFrequencyRecurrentDayTextField().getValue(),0);
var recurrentHour=Ext.num(this.getFrequencyRecurrentHourTextField().getValue(),0);
var recurrentMinute=Ext.num(this.getFrequencyRecurrentMinuteTextField().getValue(),0);
var period=0;
if(recurrentMinute>0){period+=recurrentMinute*60
}if(recurrentHour>0){period+=recurrentHour*3600
}if(recurrentDay>0){period+=recurrentDay*24*3600
}task.runMode.recurrentPeriod=period
}task.configuration={};
task.configuration.dir=this.getDirectoryTextField().getValue();
if(this.getCategoryCombobox().getValue()){task.category={id:this.getCategoryCombobox().getValue()}
}if(this.getStyleSheetComboBox().getValue()!="NONE"){task.configuration.styleSheet=this.getStyleSheetComboBox().getValue()
}task.configuration.fileType=this.getConfigurationFileTypeRadioGroup().getValue().getId();
task.configuration.recursive=false;
task.configuration.keepLocalIfDeleted=false;
task.configuration.localImport=false;
var values=this.getConfigurationOptionsCheckBoxGroup().getValue();
Ext.each(values,function(item,index,allItems){if(item.name=="recursive"){task.configuration.recursive=true
}else{if(item.name=="keepLocalIfDeleted"){task.configuration.keepLocalIfDeleted=true
}else{if(item.name=="localImport"){task.configuration.localImport=true
}}}},this);
return task
},validate:function(){return true
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Harvesting.Harvester");
Openwis.Admin.Harvesting.Harvester.Oaipmh=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Harvesting.Oaipmh.Title"),layout:"fit",width:450,height:620,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Admin.Harvesting.Harvester.Oaipmh.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params=this.editTaskId;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("harvestingTaskSaved");
this.add(this.getTaskFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getUrlTextField().setValue(this.config.configuration.url);
var username=this.config.configuration.userName;
var password=this.config.configuration.password;
if((username&&username.trim()!="")||(password&&password.trim()!="")){this.getUseAccountCheckBox().setValue(true);
this.getUsernameTextField().setValue(username);
this.getPasswordTextField().setValue(password);
this.getUsernameTextField().show();
this.getPasswordTextField().show()
}else{this.getUseAccountCheckBox().setValue(false)
}this.getDateFrom().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.configuration.dateFrom));
this.getDateTo().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.configuration.dateTo));
if(this.config.runMode.recurrent){this.getRunModeRadioGroup().setValue("RECURRENT");
var recurrencePeriod=this.config.runMode.recurrentPeriod;
var recurrencePeriodHour=recurrencePeriod/3600;
if(recurrencePeriodHour%24==0){this.getRecurrentProcessingFrequencyCombobox().setValue("DAY");
this.getRecurrentProcessingNumberField().setValue(recurrencePeriodHour/24)
}else{this.getRecurrentProcessingFrequencyCombobox().setValue("HOUR");
this.getRecurrentProcessingNumberField().setValue(recurrencePeriodHour)
}this.getStartingDateField().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.runMode.startingDate));
this.getStartingDateTimeField().setValue(Openwis.Utils.Date.ISODateToTime(this.config.runMode.startingDate));
this.getFrequencyRunModeCompositeField().show()
}else{this.getRunModeRadioGroup().setValue("USER_TRIGERRED")
}this.getProviderConfigurationOptionsCheckBoxGroup().setValue("deletionSupport",this.config.incremental);
this.getProviderConfigurationOptionsCheckBoxGroup().setValue("incremental",this.config.incremental);
this.getValidationCombobox().setValue(this.config.validationMode);
if(this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}}this.show()
},getTaskFormPanel:function(){if(!this.taskFormPanel){this.taskFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,border:false});
this.taskFormPanel.add(this.getNameTextField());
this.taskFormPanel.add(this.getUrlTextField());
this.taskFormPanel.add(this.getUseAccountCheckBox());
this.taskFormPanel.add(this.getUseAccountCompositeField());
this.taskFormPanel.add(this.getDateInterval());
this.taskFormPanel.add(this.getProviderConfigurationFieldSet());
this.taskFormPanel.add(this.getRunModeRadioGroup());
this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
this.taskFormPanel.add(this.getBackupsCombobox());
this.taskFormPanel.add(this.getValidationCombobox());
this.taskFormPanel.add(this.getCategoryCombobox())
}return this.taskFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getUrlTextField:function(){if(!this.urlTextField){this.urlTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.URL"),name:"url",allowBlank:false,width:300})
}return this.urlTextField
},getUseAccountCheckBox:function(){if(!this.useAccountCheckBox){this.useAccountCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Harvesting.Account.Use"),name:"useAccount",width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getUseAccountCompositeField().hide()
}else{this.getUseAccountCompositeField().show()
}},scope:this}})
}return this.useAccountCheckBox
},getUseAccountCompositeField:function(){if(!this.useAccountCompositeField){this.useAccountCompositeField=new Ext.form.CompositeField({name:"useAccount",hidden:true,allowBlank:false,width:330,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.UserName")+":",cls:"formItems"}),this.getUsernameTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.Password")+":",cls:"formItems"}),this.getPasswordTextField()]})
}return this.useAccountCompositeField
},getUsernameTextField:function(){if(!this.usernameTextField){this.usernameTextField=new Ext.form.TextField({name:"username",allowBlank:true,width:100})
}return this.usernameTextField
},getPasswordTextField:function(){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({name:"password",allowBlank:true,inputType:"password",width:100})
}return this.passwordTextField
},getRunModeRadioGroup:function(){if(!this.runModeRadioGroup){this.runModeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("Harvesting.Options.RunMode"),name:"runMode",allowBlank:false,columns:1,width:150,items:[{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.UserTriggerred"),name:"runMode",inputValue:"USER_TRIGERRED",checked:false,id:"USER_TRIGERRED"},{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.Recurrent"),name:"runMode",inputValue:"RECURRENT",checked:true,id:"RECURRENT"}],listeners:{change:function(group,radioChecked){if(radioChecked.inputValue=="USER_TRIGERRED"){this.getFrequencyRunModeCompositeField().hide()
}else{this.getFrequencyRunModeCompositeField().show()
}},scope:this}})
}return this.runModeRadioGroup
},getFrequencyRunModeCompositeField:function(){if(!this.frequencyRunModeCompositeField){this.frequencyRunModeCompositeField=new Ext.form.FieldSet({name:"recurrentRunMode",hidden:false,allowBlank:false,width:400,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency")+":",cls:"formItems"}),this.getStartingDateCompositeField(),this.getRecurrentProcessingCompositeField()]})
}return this.frequencyRunModeCompositeField
},getFrequencyRecurrentDayTextField:function(){if(!this.frequencyRecurrentDayTextField){this.frequencyRecurrentDayTextField=new Ext.form.TextField({name:"day",allowBlank:true,width:50})
}return this.frequencyRecurrentDayTextField
},getFrequencyRecurrentHourTextField:function(){if(!this.frequencyRecurrentHourTextField){this.frequencyRecurrentHourTextField=new Ext.form.TextField({name:"hour",allowBlank:true,width:50})
}return this.frequencyRecurrentHourTextField
},getFrequencyRecurrentMinuteTextField:function(){if(!this.frequencyRecurrentMinuteTextField){this.frequencyRecurrentMinuteTextField=new Ext.form.TextField({name:"minute",allowBlank:true,width:50})
}return this.frequencyRecurrentMinuteTextField
},getCategoryCombobox:function(){if(!this.categoryCombobox){var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.category){this.getCategoryCombobox().setValue(this.config.category.id)
}},scope:this}});
this.categoryCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Category.After.Harvest"),store:categoryStore,valueField:"id",displayField:"name",name:"category",mode:"local",allowBlank:false,typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.categoryCombobox
},getBackupsCombobox:function(){if(!this.backupsCombobox){var backupsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.all.backup.centres",idProperty:"name",autoLoad:true,fields:[{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}},scope:this}});
this.backupsCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Backup"),store:backupsStore,valueField:"name",displayField:"name",name:"backups",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200,height:120})
}return this.backupsCombobox
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("Harvesting.Validation")})
}return this.validationCombobox
},getDateInterval:function(){if(!this.dateInterval){this.dateInterval=new Ext.form.CompositeField({name:"dateInterval",fieldLabel:Openwis.i18n("Harvesting.DateInterval"),style:{margin:"0px 5px 0px 5px"},items:[new Ext.Container({border:false,html:Openwis.i18n("Common.Extent.Temporal.From"),cls:"formItems"}),this.getDateFrom(),new Ext.Container({border:false,html:Openwis.i18n("Common.Extent.Temporal.To"),cls:"formItems"}),this.getDateTo()]})
}return this.dateInterval
},getDateFrom:function(){if(!this.dateFrom){this.dateFrom=new Ext.form.DateField({name:"dateFrom",editable:true,format:"Y-m-d",width:120})
}return this.dateFrom
},getDateTo:function(){if(!this.dateTo){this.dateTo=new Ext.form.DateField({name:"dateTo",editable:true,format:"Y-m-d",width:120})
}return this.dateTo
},getProviderConfigurationFieldSet:function(){if(!this.providerConfigurationFieldSet){this.providerConfigurationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Harvesting.ProviderConfiguration"),autoHeight:true,collapsed:true,collapsible:true});
this.providerConfigurationFieldSet.add(new Ext.Container({html:Openwis.i18n("Harvesting.ProviderConfiguration.Fetch.Note"),border:false,cls:"infoMsg",style:{margin:"0px 0px 5px 0px"}}));
this.providerConfigurationFieldSet.add(this.getProviderConfigurationSetCombobox());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationPrefixCombobox());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsCheckBoxGroup());
this.providerConfigurationFieldSet.addButton(new Ext.Button(this.getFetchRemoteInfoAction()))
}return this.providerConfigurationFieldSet
},getProviderConfigurationSetCombobox:function(){if(!this.providerConfigurationSetCombobox){this.providerConfigurationSetCombobox=new Ext.form.ComboBox({store:new Ext.data.JsonStore({idProperty:"name",fields:[{name:"name"},{name:"label"}]}),fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Set"),valueField:"name",displayField:"label",disabled:true,name:"set",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.providerConfigurationSetCombobox
},getProviderConfigurationPrefixCombobox:function(){if(!this.providerConfigurationPrefixCombobox){this.providerConfigurationPrefixCombobox=new Ext.form.ComboBox({store:[],disabled:true,fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Prefix"),name:"prefix",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.providerConfigurationPrefixCombobox
},getProviderConfigurationOptionsCheckBoxGroup:function(){if(!this.providerConfigurationOptionsCheckboxGroup){this.providerConfigurationOptionsCheckboxGroup=new Ext.form.CheckboxGroup({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options"),name:"options",width:175,columns:1,items:[{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.DeletionSupport"),name:"deletionSupport",id:"deletionSupport",disabled:true},{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Incremental"),name:"incremental",id:"incremental",disabled:true}]})
}return this.providerConfigurationOptionsCheckboxGroup
},getFetchRemoteInfoAction:function(){if(!this.fetchRemoteInfoAction){this.fetchRemoteInfoAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Fetch"),scope:this,handler:function(){var url=this.getUrlTextField().getValue();
if(url.trim()!=""){var params={url:url};
new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.oaipmh.info",params:params,listeners:{success:function(remoteConfig){this.getProviderConfigurationSetCombobox().getStore().loadData(remoteConfig.sets);
this.getProviderConfigurationSetCombobox().setDisabled(false);
if(this.isEdition()&&this.config.configuration.criteriaSet){this.getProviderConfigurationSetCombobox().setValue(this.config.configuration.criteriaSet)
}this.getProviderConfigurationPrefixCombobox().getStore().loadData(remoteConfig.formats);
this.getProviderConfigurationPrefixCombobox().setDisabled(false);
if(this.isEdition()&&this.config.configuration.criteriaPrefix){this.getProviderConfigurationPrefixCombobox().setValue(this.config.configuration.criteriaPrefix)
}if(!this.isEdition()){this.getProviderConfigurationOptionsCheckBoxGroup().setValue("deletionSupport",remoteConfig.deletionSupport);
this.getProviderConfigurationOptionsCheckBoxGroup().setValue("incremental",remoteConfig.deletionSupport)
}this.getProviderConfigurationOptionsCheckBoxGroup().getBox("incremental").setDisabled(!remoteConfig.deletionSupport)
},scope:this}}).proceed()
}else{Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Harvesting.ProviderConfiguration.Fetch.Remote.Url.Mandatory"))
}}})
}return this.fetchRemoteInfoAction
},getRecurrentProcessingCompositeField:function(){if(!this.recurrentProcessingCompositeField){this.recurrentProcessingCompositeField=new Ext.form.CompositeField({width:360,items:[{xtype:"container",style:{paddingLeft:"20px"}},new Ext.form.Label({html:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Label")}),this.getRecurrentProcessingNumberField(),this.getRecurrentProcessingFrequencyCombobox()]})
}return this.recurrentProcessingCompositeField
},getRecurrentProcessingNumberField:function(){if(!this.recurrentProcessingNumberField){this.recurrentProcessingNumberField=new Ext.form.NumberField({name:"frequencyNumber",width:40,allowDecimals:false,allowNegative:false,minValue:1,value:""})
}return this.recurrentProcessingNumberField
},getRecurrentProcessingFrequencyCombobox:function(){if(!this.recurrentProcessingFrequencyCombobox){this.recurrentProcessingFrequencyCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["DAY",Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Day")],["HOUR",Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Hour")]]}),valueField:"id",displayField:"value",value:"HOUR",name:"frequencyComboBox",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:80})
}return this.recurrentProcessingFrequencyCombobox
},getStartingDateCompositeField:function(){if(!this.startingDateCompositeField){this.startingDateCompositeField=new Ext.form.CompositeField({width:470,items:[{xtype:"container",style:{paddingLeft:"20px"}},new Ext.form.Label({html:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.StartingAt")}),this.getStartingDateField(),this.getStartingDateTimeField()]})
}return this.startingDateCompositeField
},getStartingDateField:function(){if(!this.startingDateField){this.startingDateField=new Ext.form.DateField({name:"startingDate",editable:false,format:"Y-m-d",value:new Date()})
}return this.startingDateField
},getStartingDateTimeField:function(){if(!this.startingDateTimeField){this.startingDateTimeField=new Ext.form.TimeField({name:"startingDateTimeField",increment:15,format:"H:i",value:"00:00",width:60})
}return this.startingDateTimeField
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getTaskFormPanel().getForm().isValid()&&this.validate()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.save",params:this.getHarvestingTask(),listeners:{success:function(config){this.fireEvent("harvestingTaskSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getHarvestingTask:function(){var task={};
if(this.isEdition()){task.id=this.editTaskId;
task.status=this.config.status
}else{task.status="ACTIVE"
}task.name=this.getNameTextField().getValue();
task.type="oaipmh";
if(this.getBackupsCombobox().getValue()!=""){task.backup={name:this.getBackupsCombobox().getValue()}
}task.synchronizationTask=false;
task.validationMode=this.getValidationCombobox().getValue();
task.incremental=false;
var values=this.getProviderConfigurationOptionsCheckBoxGroup().getValue();
Ext.each(values,function(item,index,allItems){if(item.name=="incremental"){task.incremental=true
}},this);
task.runMode={};
task.runMode.recurrent=this.getRunModeRadioGroup().getValue().inputValue=="RECURRENT";
if(task.runMode.recurrent){var recurrenceValue;
var frequencyUnit=this.getRecurrentProcessingFrequencyCombobox().getValue();
if(frequencyUnit=="HOUR"){recurrenceValue=this.getRecurrentProcessingNumberField().getValue()*3600
}else{recurrenceValue=this.getRecurrentProcessingNumberField().getValue()*3600*24
}task.runMode.recurrentScale=this.getRecurrentProcessingFrequencyCombobox().getValue();
task.runMode.recurrencePeriod=recurrenceValue;
task.runMode.startingDate=this.getStartingDateField().getValue().format("Y-m-d")+"T"+this.getStartingDateTimeField().getValue()+":00Z"
}task.configuration={};
task.configuration.url=this.getUrlTextField().getValue();
if(this.getUseAccountCheckBox().checked){task.configuration.userName=this.getUsernameTextField().getValue();
task.configuration.password=this.getPasswordTextField().getValue()
}if(this.getCategoryCombobox().getValue()){task.category={id:this.getCategoryCombobox().getValue()}
}if(this.getDateFrom().getValue()!=""){task.configuration.dateFrom=Openwis.Utils.Date.formatToISODate(this.getDateFrom().getValue())
}if(this.getDateTo().getValue()!=""){task.configuration.dateTo=Openwis.Utils.Date.formatToISODate(this.getDateTo().getValue())
}if(this.isEdition()){if(this.getProviderConfigurationSetCombobox().disabled){task.configuration.criteriaSet=this.config.configuration.criteriaSet
}else{if(this.getProviderConfigurationSetCombobox().getValue()!=""){task.configuration.criteriaSet=this.getProviderConfigurationSetCombobox().getValue()
}}if(this.getProviderConfigurationPrefixCombobox().disabled){task.configuration.criteriaPrefix=this.config.configuration.criteriaPrefix
}else{if(this.getProviderConfigurationPrefixCombobox().getValue()!=""){task.configuration.criteriaPrefix=this.getProviderConfigurationPrefixCombobox().getValue()
}}}else{if(this.getProviderConfigurationSetCombobox().getValue()!=""){task.configuration.criteriaSet=this.getProviderConfigurationSetCombobox().getValue()
}if(this.getProviderConfigurationPrefixCombobox().getValue()!=""){task.configuration.criteriaPrefix=this.getProviderConfigurationPrefixCombobox().getValue()
}}return task
},validate:function(){if(this.getDateFrom().getValue()!=""&&this.getDateTo().getValue()!=""&&this.getDateFrom().getValue()>this.getDateTo().getValue()){Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
return false
}return true
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Harvesting.Harvester");
Openwis.Admin.Harvesting.Harvester.CSW=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Harvesting.CSW.Title"),layout:"fit",width:450,height:580,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Admin.Harvesting.Harvester.CSW.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params=this.editTaskId;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("harvestingTaskSaved");
this.add(this.getTaskFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getServiceURLTextField().setValue(this.config.configuration.serviceURL);
var username=this.config.configuration.userName;
var password=this.config.configuration.password;
if((username&&username.trim()!="")||(password&&password.trim()!="")){this.getUseAccountCheckBox().setValue(true);
this.getUsernameTextField().setValue(username);
this.getPasswordTextField().setValue(password);
this.getUsernameTextField().show();
this.getPasswordTextField().show()
}else{this.getUseAccountCheckBox().setValue(false)
}if(this.config.runMode.recurrent){this.getRunModeRadioGroup().setValue("RECURRENT");
var recurrencePeriod=this.config.runMode.recurrentPeriod;
var days=Math.floor(recurrencePeriod/(24*3600));
if(days>0){recurrencePeriod%=(24*3600);
this.getFrequencyRecurrentDayTextField().setValue(days)
}var hours=Math.floor(recurrencePeriod/3600);
if(hours>0){this.getFrequencyRecurrentHourTextField().setValue(hours);
recurrencePeriod%=3600
}var minuts=recurrencePeriod/60;
if(minuts>0){this.getFrequencyRecurrentMinuteTextField().setValue(minuts)
}this.getFrequencyRunModeCompositeField().show()
}else{this.getRunModeRadioGroup().setValue("USER_TRIGERRED")
}this.getProviderConfigurationOptionsSearchAnyTextField().setValue(this.config.configuration.any);
this.getProviderConfigurationOptionsSearchTitleTextField().setValue(this.config.configuration.title);
this.getProviderConfigurationOptionsSearchAbstractTextField().setValue(this.config.configuration["abstract"]);
this.getProviderConfigurationOptionsSearchKeywordsTextField().setValue(this.config.configuration.subject);
this.getValidationCombobox().setValue(this.config.validationMode);
if(this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}}this.show()
},getTaskFormPanel:function(){if(!this.taskFormPanel){this.taskFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,border:false});
this.taskFormPanel.add(this.getNameTextField());
this.taskFormPanel.add(this.getServiceURLTextField());
this.taskFormPanel.add(this.getUseAccountCheckBox());
this.taskFormPanel.add(this.getUseAccountCompositeField());
this.taskFormPanel.add(this.getProviderConfigurationFieldSet());
this.taskFormPanel.add(this.getRunModeRadioGroup());
this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
this.taskFormPanel.add(this.getBackupsCombobox());
this.taskFormPanel.add(this.getValidationCombobox());
this.taskFormPanel.add(this.getCategoryCombobox())
}return this.taskFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getServiceURLTextField:function(){if(!this.serviceURLTextField){this.serviceURLTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ServiceURL"),name:"serviceURL",allowBlank:false,width:150})
}return this.serviceURLTextField
},getUseAccountCheckBox:function(){if(!this.useAccountCheckBox){this.useAccountCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Harvesting.Account.Use"),name:"useAccount",width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getUseAccountCompositeField().hide()
}else{this.getUseAccountCompositeField().show()
}},scope:this}})
}return this.useAccountCheckBox
},getUseAccountCompositeField:function(){if(!this.useAccountCompositeField){this.useAccountCompositeField=new Ext.form.CompositeField({name:"useAccount",hidden:true,allowBlank:false,width:330,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.UserName")+":",cls:"formItems"}),this.getUsernameTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.Password")+":",cls:"formItems"}),this.getPasswordTextField()]})
}return this.useAccountCompositeField
},getUsernameTextField:function(){if(!this.usernameTextField){this.usernameTextField=new Ext.form.TextField({name:"username",allowBlank:true,width:100})
}return this.usernameTextField
},getPasswordTextField:function(){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({name:"password",allowBlank:true,inputType:"password",width:100})
}return this.passwordTextField
},getRunModeRadioGroup:function(){if(!this.runModeRadioGroup){this.runModeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("Harvesting.Options.RunMode"),name:"runMode",allowBlank:false,columns:1,width:150,items:[{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.UserTriggerred"),name:"runMode",inputValue:"USER_TRIGERRED",checked:true,id:"USER_TRIGERRED"},{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.Recurrent"),name:"runMode",inputValue:"RECURRENT",id:"RECURRENT"}],listeners:{change:function(group,radioChecked){if(radioChecked.inputValue=="USER_TRIGERRED"){this.getFrequencyRunModeCompositeField().hide()
}else{this.getFrequencyRunModeCompositeField().show()
}},scope:this}})
}return this.runModeRadioGroup
},getFrequencyRunModeCompositeField:function(){if(!this.frequencyRunModeCompositeField){this.frequencyRunModeCompositeField=new Ext.form.CompositeField({name:"recurrentRunMode",hidden:true,allowBlank:false,width:350,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency")+":",cls:"formItems"}),this.getFrequencyRecurrentDayTextField(),this.getFrequencyRecurrentHourTextField(),this.getFrequencyRecurrentMinuteTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency.Detail"),cls:"formItems"})]})
}return this.frequencyRunModeCompositeField
},getFrequencyRecurrentDayTextField:function(){if(!this.frequencyRecurrentDayTextField){this.frequencyRecurrentDayTextField=new Ext.form.TextField({name:"day",allowBlank:true,width:50})
}return this.frequencyRecurrentDayTextField
},getFrequencyRecurrentHourTextField:function(){if(!this.frequencyRecurrentHourTextField){this.frequencyRecurrentHourTextField=new Ext.form.TextField({name:"hour",allowBlank:true,width:50})
}return this.frequencyRecurrentHourTextField
},getFrequencyRecurrentMinuteTextField:function(){if(!this.frequencyRecurrentMinuteTextField){this.frequencyRecurrentMinuteTextField=new Ext.form.TextField({name:"minute",allowBlank:true,width:50})
}return this.frequencyRecurrentMinuteTextField
},getCategoryCombobox:function(){if(!this.categoryCombobox){var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.category){this.getCategoryCombobox().setValue(this.config.category.id)
}},scope:this}});
this.categoryCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Category.After.Harvest"),store:categoryStore,valueField:"id",displayField:"name",name:"category",mode:"local",allowBlank:false,typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.categoryCombobox
},getBackupsCombobox:function(){if(!this.backupsCombobox){var backupsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.all.backup.centres",idProperty:"name",autoLoad:true,fields:[{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}},scope:this}});
this.backupsCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Backup"),store:backupsStore,valueField:"name",displayField:"name",name:"backups",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200,height:120})
}return this.backupsCombobox
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("Harvesting.Validation")})
}return this.validationCombobox
},getProviderConfigurationFieldSet:function(){if(!this.providerConfigurationFieldSet){this.providerConfigurationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Harvesting.ProviderConfiguration"),autoHeight:true,collapsed:true,collapsible:true});
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchAnyTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchTitleTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchAbstractTextField());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchKeywordsTextField())
}return this.providerConfigurationFieldSet
},getProviderConfigurationOptionsSearchAnyTextField:function(){if(!this.providerConfigurationOptionsSearchAnyTextField){this.providerConfigurationOptionsSearchAnyTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Any"),name:"any",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchAnyTextField
},getProviderConfigurationOptionsSearchTitleTextField:function(){if(!this.providerConfigurationOptionsSearchTitleTextField){this.providerConfigurationOptionsSearchTitleTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Title"),name:"title",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchTitleTextField
},getProviderConfigurationOptionsSearchAbstractTextField:function(){if(!this.providerConfigurationOptionsSearchAbstractTextField){this.providerConfigurationOptionsSearchAbstractTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Abstract"),name:"abstract",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchAbstractTextField
},getProviderConfigurationOptionsSearchKeywordsTextField:function(){if(!this.providerConfigurationOptionsSearchKeywordsTextField){this.providerConfigurationOptionsSearchKeywordsTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Search.Keywords"),name:"keywords",allowBlank:true,width:150})
}return this.providerConfigurationOptionsSearchKeywordsTextField
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getTaskFormPanel().getForm().isValid()&&this.validate()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.save",params:this.getHarvestingTask(),listeners:{success:function(config){this.fireEvent("harvestingTaskSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getHarvestingTask:function(){var task={};
if(this.isEdition()){task.id=this.editTaskId;
task.status=this.config.status
}else{task.status="ACTIVE"
}task.name=this.getNameTextField().getValue();
task.type="csw";
if(this.getBackupsCombobox().getValue()!=""){task.backup={name:this.getBackupsCombobox().getValue()}
}task.synchronizationTask=false;
task.incremental=false;
task.validationMode=this.getValidationCombobox().getValue();
task.runMode={};
task.runMode.recurrent=this.getRunModeRadioGroup().getValue().inputValue=="RECURRENT";
if(task.runMode.recurrent){var recurrentDay=Ext.num(this.getFrequencyRecurrentDayTextField().getValue(),0);
var recurrentHour=Ext.num(this.getFrequencyRecurrentHourTextField().getValue(),0);
var recurrentMinute=Ext.num(this.getFrequencyRecurrentMinuteTextField().getValue(),0);
var period=0;
if(recurrentMinute>0){period+=recurrentMinute*60
}if(recurrentHour>0){period+=recurrentHour*3600
}if(recurrentDay>0){period+=recurrentDay*24*3600
}task.runMode.recurrentPeriod=period
}task.configuration={};
task.configuration.name=this.getNameTextField().getValue();
task.configuration.serviceURL=this.getServiceURLTextField().getValue();
if(this.getUseAccountCheckBox().checked){task.configuration.userName=this.getUsernameTextField().getValue();
task.configuration.password=this.getPasswordTextField().getValue()
}if(this.getCategoryCombobox().getValue()){task.category={id:this.getCategoryCombobox().getValue()}
}task.configuration.any=this.getProviderConfigurationOptionsSearchAnyTextField().getValue();
task.configuration.title=this.getProviderConfigurationOptionsSearchTitleTextField().getValue();
task.configuration["abstract"]=this.getProviderConfigurationOptionsSearchAbstractTextField().getValue();
task.configuration.subject=this.getProviderConfigurationOptionsSearchKeywordsTextField().getValue();
return task
},validate:function(){return true
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Harvesting.Harvester");
Openwis.Admin.Harvesting.Harvester.WebDav=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Harvesting.WebDav.Title"),layout:"fit",width:450,height:620,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Admin.Harvesting.Harvester.WebDav.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params=this.editTaskId;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("harvestingTaskSaved");
this.add(this.getTaskFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getURLTextField().setValue(this.config.configuration.dir);
if(this.config.runMode.recurrent){this.getRunModeRadioGroup().setValue("RECURRENT");
var recurrencePeriod=this.config.runMode.recurrentPeriod;
var days=Math.floor(recurrencePeriod/(24*3600));
if(days>0){recurrencePeriod%=(24*3600);
this.getFrequencyRecurrentDayTextField().setValue(days)
}var hours=Math.floor(recurrencePeriod/3600);
if(hours>0){this.getFrequencyRecurrentHourTextField().setValue(hours);
recurrencePeriod%=3600
}var minuts=recurrencePeriod/60;
if(minuts>0){this.getFrequencyRecurrentMinuteTextField().setValue(minuts)
}this.getFrequencyRunModeCompositeField().show()
}else{this.getRunModeRadioGroup().setValue("USER_TRIGERRED")
}this.getStyleSheetComboBox().setValue(this.config.configuration.styleSheet);
this.getValidationCombobox().setValue(this.config.validationMode);
this.getConfigurationOptionsCheckBoxGroup().setValue("recursive",this.config.configuration.recursive);
this.getConfigurationOptionsCheckBoxGroup().setValue("validate",this.config.configuration.validate)
}this.show()
},getTaskFormPanel:function(){if(!this.taskFormPanel){this.taskFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,border:false});
this.taskFormPanel.add(this.getNameTextField());
this.taskFormPanel.add(this.getURLTextField());
this.taskFormPanel.add(this.getConfigurationFieldSet());
this.taskFormPanel.add(this.getRunModeRadioGroup());
this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
this.taskFormPanel.add(this.getStyleSheetComboBox());
this.taskFormPanel.add(this.getValidationCombobox());
this.taskFormPanel.add(this.getCategoryCombobox())
}return this.taskFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getURLTextField:function(){if(!this.urlTextField){this.urlTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.URL"),name:"url",allowBlank:false,width:300})
}return this.urlTextField
},getRunModeRadioGroup:function(){if(!this.runModeRadioGroup){this.runModeRadioGroup=new Ext.form.RadioGroup({fieldLabel:Openwis.i18n("Harvesting.Options.RunMode"),name:"runMode",allowBlank:false,columns:1,width:150,items:[{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.UserTriggerred"),name:"runMode",inputValue:"USER_TRIGERRED",checked:true,id:"USER_TRIGERRED"},{boxLabel:Openwis.i18n("Harvesting.Options.RunMode.Recurrent"),name:"runMode",inputValue:"RECURRENT",id:"RECURRENT"}],listeners:{change:function(group,radioChecked){if(radioChecked.inputValue=="USER_TRIGERRED"){this.getFrequencyRunModeCompositeField().hide()
}else{this.getFrequencyRunModeCompositeField().show()
}},scope:this}})
}return this.runModeRadioGroup
},getFrequencyRunModeCompositeField:function(){if(!this.frequencyRunModeCompositeField){this.frequencyRunModeCompositeField=new Ext.form.CompositeField({name:"recurrentRunMode",hidden:true,allowBlank:false,width:350,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency")+":",cls:"formItems"}),this.getFrequencyRecurrentDayTextField(),this.getFrequencyRecurrentHourTextField(),this.getFrequencyRecurrentMinuteTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency.Detail"),cls:"formItems"})]})
}return this.frequencyRunModeCompositeField
},getFrequencyRecurrentDayTextField:function(){if(!this.frequencyRecurrentDayTextField){this.frequencyRecurrentDayTextField=new Ext.form.TextField({name:"day",allowBlank:true,width:50})
}return this.frequencyRecurrentDayTextField
},getFrequencyRecurrentHourTextField:function(){if(!this.frequencyRecurrentHourTextField){this.frequencyRecurrentHourTextField=new Ext.form.TextField({name:"hour",allowBlank:true,width:50})
}return this.frequencyRecurrentHourTextField
},getFrequencyRecurrentMinuteTextField:function(){if(!this.frequencyRecurrentMinuteTextField){this.frequencyRecurrentMinuteTextField=new Ext.form.TextField({name:"minute",allowBlank:true,width:50})
}return this.frequencyRecurrentMinuteTextField
},getCategoryCombobox:function(){if(!this.categoryCombobox){var categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.category){this.getCategoryCombobox().setValue(this.config.category.id)
}},scope:this}});
this.categoryCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Category.After.Harvest"),store:categoryStore,valueField:"id",displayField:"name",name:"category",mode:"local",allowBlank:false,typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.categoryCombobox
},getStyleSheetComboBox:function(){if(!this.styleSheetComboBox){var styleSheetStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.stylesheet.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}],listeners:{load:function(store,records,options){store.insert(0,[new Ext.data.Record({id:"NONE",name:Openwis.i18n("Common.List.None")})]);
if(this.isEdition()&&this.config.configuration.styleSheet){this.getStyleSheetComboBox().setValue(this.config.configuration.styleSheet)
}else{this.getStyleSheetComboBox().setValue("NONE")
}},scope:this}});
this.styleSheetComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataBatchImport.StyleSheet"),name:"stylesheet",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:styleSheetStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"})
}return this.styleSheetComboBox
},getConfigurationFieldSet:function(){if(!this.configurationFieldSet){this.configurationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Harvesting.ProviderConfiguration.Options"),autoHeight:true,collapsed:true,collapsible:true});
this.configurationFieldSet.add(this.getConfigurationOptionsCheckBoxGroup())
}return this.configurationFieldSet
},getConfigurationOptionsCheckBoxGroup:function(){if(!this.configurationOptionsCheckboxGroup){this.configurationOptionsCheckboxGroup=new Ext.form.CheckboxGroup({fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options"),name:"options",width:175,columns:1,items:[{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Recursive"),name:"recursive",id:"recursive",checked:true},{boxLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Options.Validate"),name:"validate",id:"validate",checked:false}]})
}return this.configurationOptionsCheckboxGroup
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("Harvesting.Validation")})
}return this.validationCombobox
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getTaskFormPanel().getForm().isValid()&&this.validate()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.save",params:this.getHarvestingTask(),listeners:{success:function(config){this.fireEvent("harvestingTaskSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getHarvestingTask:function(){var task={};
if(this.isEdition()){task.id=this.editTaskId;
task.status=this.config.status
}else{task.status="ACTIVE"
}task.name=this.getNameTextField().getValue();
task.type="webdav";
task.synchronizationTask=false;
task.incremental=false;
task.validationMode=this.getValidationCombobox().getValue();
task.runMode={};
task.runMode.recurrent=this.getRunModeRadioGroup().getValue().inputValue=="RECURRENT";
if(task.runMode.recurrent){var recurrentDay=Ext.num(this.getFrequencyRecurrentDayTextField().getValue(),0);
var recurrentHour=Ext.num(this.getFrequencyRecurrentHourTextField().getValue(),0);
var recurrentMinute=Ext.num(this.getFrequencyRecurrentMinuteTextField().getValue(),0);
var period=0;
if(recurrentMinute>0){period+=recurrentMinute*60
}if(recurrentHour>0){period+=recurrentHour*3600
}if(recurrentDay>0){period+=recurrentDay*24*3600
}task.runMode.recurrentPeriod=period
}task.configuration={};
task.configuration.dir=this.getURLTextField().getValue();
if(this.getCategoryCombobox().getValue()){task.category={id:this.getCategoryCombobox().getValue()}
}if(this.getStyleSheetComboBox().getValue()!="NONE"){task.configuration.styleSheet=this.getStyleSheetComboBox().getValue()
}task.configuration.recursive=false;
task.configuration.validate=false;
var values=this.getConfigurationOptionsCheckBoxGroup().getValue();
Ext.each(values,function(item,index,allItems){if(item.name=="recursive"){task.configuration.recursive=true
}else{if(item.name=="validate"){task.configuration.validate=true
}}},this);
return task
},validate:function(){return true
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Harvesting");
Openwis.Admin.Harvesting.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Harvesting.All.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getHarvestingTaskGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Harvesting.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getHarvestingTaskGrid:function(){if(!this.harvestingTaskGrid){this.harvestingTaskGrid=new Ext.grid.GridPanel({id:"harvestingGrid",height:400,border:true,store:this.getHarvestingTaskStore(),loadMask:true,columns:[{id:"monitorStatus",header:" ",dataIndex:"monitor",sortable:false,hideable:false,renderer:this.monitorImg,width:30},{id:"monitorProgress",header:Openwis.i18n("Harvesting.Processed"),dataIndex:"progress",sortable:false,hideable:false,renderer:this.monitorProgress,width:55},{id:"name",header:Openwis.i18n("Harvesting.Name"),dataIndex:"name",sortable:true,hideable:false},{id:"type",header:Openwis.i18n("Harvesting.Type"),dataIndex:"type",sortable:true,hideable:false},{id:"lastRun",header:Openwis.i18n("Harvesting.LastRun"),dataIndex:"lastRun",sortable:true,hideable:false,renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong},{id:"backup",header:Openwis.i18n("Harvesting.Backup"),dataIndex:"backup",sortable:true,hideable:false,renderer:this.backupRenderer}],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:true,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getReportAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getActivateAction().setDisabled(sm.getCount()==0||record.get("status")!="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getDeactivateAction().setDisabled(sm.getCount()==0||record.get("status")=="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getRunAction().setDisabled(sm.getCount()==0||record.get("status")!="ACTIVE")
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getReportAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getActivateAction().setDisabled(sm.getCount()==0||record.get("status")!="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getDeactivateAction().setDisabled(sm.getCount()==0||record.get("status")=="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getRunAction().setDisabled(sm.getCount()==0||record.get("status")!="ACTIVE")
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getHarvestingTaskStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Harvesting.Administration.All.Display.Range"),emptyMsg:Openwis.i18n("Harvesting.Administration.All.No.Task")})});
this.harvestingTaskGrid.addButton(this.getNewMenuButton());
this.harvestingTaskGrid.addButton(new Ext.Button(this.getReportAction()));
this.harvestingTaskGrid.addButton(new Ext.Button(this.getEditAction()));
this.harvestingTaskGrid.addButton(new Ext.Button(this.getRemoveAction()));
this.harvestingTaskGrid.addButton(new Ext.Button(this.getActivateAction()));
this.harvestingTaskGrid.addButton(new Ext.Button(this.getDeactivateAction()));
this.harvestingTaskGrid.addButton(new Ext.Button(this.getRunAction()))
}return this.harvestingTaskGrid
},getHarvestingTaskStore:function(){if(!this.harvestingTaskStore){this.harvestingTaskStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.harvest.all",remoteSort:true,root:"rows",fields:[{name:"id",mapping:"object.id"},{name:"uuid",mapping:"object.uuid"},{name:"name",mapping:"object.name",sortType:Ext.data.SortTypes.asUCString},{name:"type",mapping:"object.type"},{name:"lastRun",mapping:"object.lastRun"},{name:"backup",mapping:"object.backup"},{name:"status",mapping:"object.status"},{name:"lastResult",mapping:"object.lastResult"},{name:"running"},{name:"progress"},{name:"monitor",convert:this.convertMonitor}],sortInfo:{field:"name",direction:"ASC"}})
}return this.harvestingTaskStore
},getNewMenuButton:function(){if(!this.newMenuButton){this.newMenuButton=new Ext.Button({text:Openwis.i18n("Common.Btn.New"),menu:new Ext.menu.Menu({items:[this.getNewOaipmhAction(),this.getNewFileSystemAction(),this.getNewGeonetwork20Action(),this.getNewCSWAction(),this.getNewWebDavAction()]})})
}return this.newMenuButton
},getNewOaipmhAction:function(){if(!this.newOaipmhAction){this.newOaipmhAction=new Ext.menu.Item({text:Openwis.i18n("Harvesting.Menu.Create.Oaipmh"),scope:this,handler:function(){new Openwis.Admin.Harvesting.Harvester.Oaipmh({operationMode:"Create",listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.newOaipmhAction
},getNewFileSystemAction:function(){if(!this.newFileSystemAction){this.newFileSystemAction=new Ext.menu.Item({text:Openwis.i18n("Harvesting.Menu.Create.FileSystem"),scope:this,handler:function(){new Openwis.Admin.Harvesting.Harvester.FileSystem({operationMode:"Create",listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.newFileSystemAction
},getNewGeonetwork20Action:function(){if(!this.newGeonetwork20Action){this.newGeonetwork20Action=new Ext.menu.Item({text:Openwis.i18n("Harvesting.Menu.Create.Geonetwork20"),scope:this,handler:function(){new Openwis.Admin.Harvesting.Harvester.Geonetwork20({operationMode:"Create",listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.newGeonetwork20Action
},getNewCSWAction:function(){if(!this.newCSWAction){this.newCSWAction=new Ext.menu.Item({text:Openwis.i18n("Harvesting.Menu.Create.CSW"),scope:this,handler:function(){new Openwis.Admin.Harvesting.Harvester.CSW({operationMode:"Create",listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.newCSWAction
},getNewWebDavAction:function(){if(!this.newWebDavAction){this.newWebDavAction=new Ext.menu.Item({text:Openwis.i18n("Harvesting.Menu.Create.WebDav"),scope:this,handler:function(){new Openwis.Admin.Harvesting.Harvester.WebDav({operationMode:"Create",listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.newWebDavAction
},getReportAction:function(){if(!this.reportAction){this.reportAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Report"),scope:this,handler:function(){var rec=this.getHarvestingTaskGrid().getSelectionModel().getSelected();
new Openwis.Common.Metadata.Report({lastResult:rec.get("lastResult"),harvestingTaskId:rec.get("id")})
}})
}return this.reportAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var rec=this.getHarvestingTaskGrid().getSelectionModel().getSelected();
if(rec.get("type")=="oaipmh"){new Openwis.Admin.Harvesting.Harvester.Oaipmh({operationMode:"Edit",editTaskId:rec.get("id"),listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}else{if(rec.get("type")=="localfilesystem"){new Openwis.Admin.Harvesting.Harvester.FileSystem({operationMode:"Edit",editTaskId:rec.get("id"),listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}else{if(rec.get("type")=="geonetwork20"){new Openwis.Admin.Harvesting.Harvester.Geonetwork20({operationMode:"Edit",editTaskId:rec.get("id"),listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}else{if(rec.get("type")=="csw"){new Openwis.Admin.Harvesting.Harvester.CSW({operationMode:"Edit",editTaskId:rec.get("id"),listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}else{if(rec.get("type")=="webdav"){new Openwis.Admin.Harvesting.Harvester.WebDav({operationMode:"Edit",editTaskId:rec.get("id"),listeners:{harvestingTaskSaved:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}})
}}}}}}})
}return this.editAction
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getHarvestingTaskGrid().getSelectionModel().getSelected();
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.harvest.remove",params:selection.get("id"),listeners:{success:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}}).proceed()
}})
}return this.removeAction
},getActivateAction:function(){if(!this.activateAction){this.activateAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Activate"),scope:this,handler:function(){var selectedRec=this.getHarvestingTaskGrid().getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.activation",params:{id:selectedRec.get("id"),activate:true},listeners:{success:function(){this.getHarvestingTaskGrid().getStore().reload();
this.getHarvestingTaskGrid().getSelectionModel().clearSelections(false)
},scope:this}}).proceed()
}})
}return this.activateAction
},getDeactivateAction:function(){if(!this.deactivateAction){this.deactivateAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Deactivate"),scope:this,handler:function(){var selectedRec=this.getHarvestingTaskGrid().getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.activation",params:{id:selectedRec.get("id"),activate:false},listeners:{success:function(){this.getHarvestingTaskGrid().getStore().reload();
this.getHarvestingTaskGrid().getSelectionModel().clearSelections(false)
},scope:this}}).proceed()
}})
}return this.deactivateAction
},getRunAction:function(){if(!this.runAction){this.runAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Run"),scope:this,handler:function(){var selectedRec=this.getHarvestingTaskGrid().getSelectionModel().getSelected();
var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.run",params:selectedRec.get("id"),listeners:{success:function(){this.getHarvestingTaskGrid().getStore().reload()
},scope:this}});
saveHandler.proceed()
}})
}return this.runAction
},statusRenderer:function(status){if(status=="ACTIVE"){return Openwis.i18n("Harvesting.Status.Active")
}else{if(status=="SUSPENDED"){return Openwis.i18n("Harvesting.Status.Suspended")
}else{if(status=="SUSPENDED_BACKUP"){return Openwis.i18n("Harvesting.Status.SuspendedBackup")
}else{return status
}}}},backupRenderer:function(backup){if(backup&&backup.name){return backup.name
}else{return""
}},convertMonitor:function(v,rec){if(rec.object.status=="ACTIVE"&&rec.running){return"RUNNING"
}else{return rec.object.status
}},monitorImg:function(status){if(status=="ACTIVE"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_active.gif"/>'
}else{if(status=="RUNNING"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_inprogress.png"/>'
}else{if(status=="SUSPENDED"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_inactive.gif"/>'
}else{if(status=="SUSPENDED_BACKUP"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_inactive_backup.gif"/>'
}}}}},monitorProgress:function(progress){result=" ";
if(Ext.isNumber(progress)){if(progress>0){result=progress
}}return result
}});Ext.ns("Openwis.Admin.Synchro");
Openwis.Admin.Synchro.Manage=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Synchro.Manage.Title"),layout:"fit",width:500,height:620,modal:true,border:false,autoScroll:true,closeAction:"close"});
Openwis.Admin.Synchro.Manage.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params=this.editTaskId;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("synchroTaskSaved");
this.add(this.getTaskFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.getLocalCategoryStore().load();
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getUrlTextField().setValue(this.config.configuration.url);
var username=this.config.configuration.userName;
var password=this.config.configuration.password;
if((username&&username.trim()!="")||(password&&password.trim()!="")){this.getUseAccountCheckBox().setValue(true);
this.getUsernameTextField().setValue(username);
this.getPasswordTextField().setValue(password);
this.getUsernameTextField().show();
this.getPasswordTextField().show()
}else{this.getUseAccountCheckBox().setValue(false)
}this.getDateFrom().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.configuration.dateFrom));
this.getDateTo().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.configuration.dateTo));
var recurrencePeriod=this.config.runMode.recurrentPeriod;
var recurrencePeriodHour=recurrencePeriod/3600;
if(recurrencePeriodHour%24==0){this.getRecurrentProcessingFrequencyCombobox().setValue("DAY");
this.getRecurrentProcessingNumberField().setValue(recurrencePeriodHour/24)
}else{this.getRecurrentProcessingFrequencyCombobox().setValue("HOUR");
this.getRecurrentProcessingNumberField().setValue(recurrencePeriodHour)
}this.getStartingDateField().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.runMode.startingDate));
this.getStartingDateTimeField().setValue(Openwis.Utils.Date.ISODateToTime(this.config.runMode.startingDate));
this.getValidationCombobox().setValue(this.config.validationMode);
if(this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}}this.show()
},getTaskFormPanel:function(){if(!this.taskFormPanel){this.taskFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,border:false});
this.taskFormPanel.add(this.getNameTextField());
this.taskFormPanel.add(this.getUrlTextField());
this.taskFormPanel.add(this.getUseAccountCheckBox());
this.taskFormPanel.add(this.getUseAccountCompositeField());
this.taskFormPanel.add(this.getDateInterval());
this.taskFormPanel.add(this.getProviderConfigurationFieldSet());
this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
this.taskFormPanel.add(this.getBackupsCombobox());
this.taskFormPanel.add(this.getValidationCombobox())
}return this.taskFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getUrlTextField:function(){if(!this.urlTextField){this.urlTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Harvesting.URL"),name:"url",allowBlank:false,width:300})
}return this.urlTextField
},getUseAccountCheckBox:function(){if(!this.useAccountCheckBox){this.useAccountCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Harvesting.Account.Use"),name:"useAccount",width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getUseAccountCompositeField().hide()
}else{this.getUseAccountCompositeField().show()
}},scope:this}})
}return this.useAccountCheckBox
},getUseAccountCompositeField:function(){if(!this.useAccountCompositeField){this.useAccountCompositeField=new Ext.form.CompositeField({name:"useAccount",hidden:true,allowBlank:false,width:330,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.UserName")+":",cls:"formItems"}),this.getUsernameTextField(),new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Account.Password")+":",cls:"formItems"}),this.getPasswordTextField()]})
}return this.useAccountCompositeField
},getUsernameTextField:function(){if(!this.usernameTextField){this.usernameTextField=new Ext.form.TextField({name:"username",allowBlank:true,width:100})
}return this.usernameTextField
},getPasswordTextField:function(){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({name:"password",allowBlank:true,inputType:"password",width:100})
}return this.passwordTextField
},getFrequencyRunModeCompositeField:function(){if(!this.frequencyRunModeCompositeField){this.frequencyRunModeCompositeField=new Ext.form.FieldSet({name:"recurrentRunMode",hidden:false,allowBlank:false,fieldLabel:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency"),width:400,items:[new Ext.Container({border:false,html:Openwis.i18n("Harvesting.Options.RunMode.Recurrent.Frequency")+":",cls:"formItems"}),this.getStartingDateCompositeField(),this.getRecurrentProcessingCompositeField()]})
}return this.frequencyRunModeCompositeField
},getFrequencyRecurrentDayTextField:function(){if(!this.frequencyRecurrentDayTextField){this.frequencyRecurrentDayTextField=new Ext.form.TextField({name:"day",allowBlank:true,width:50})
}return this.frequencyRecurrentDayTextField
},getFrequencyRecurrentHourTextField:function(){if(!this.frequencyRecurrentHourTextField){this.frequencyRecurrentHourTextField=new Ext.form.TextField({name:"hour",allowBlank:true,width:50})
}return this.frequencyRecurrentHourTextField
},getFrequencyRecurrentMinuteTextField:function(){if(!this.frequencyRecurrentMinuteTextField){this.frequencyRecurrentMinuteTextField=new Ext.form.TextField({name:"minute",allowBlank:true,width:50})
}return this.frequencyRecurrentMinuteTextField
},getLocalCategoryStore:function(){if(!this.categoryStore){this.categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name"}]})
}return this.categoryStore
},getBackupsCombobox:function(){if(!this.backupsCombobox){var backupsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.all.backup.centres",idProperty:"name",autoLoad:true,fields:[{name:"name"}],listeners:{load:function(store,records,options){if(this.isEdition()&&this.config.backup){this.getBackupsCombobox().setValue(this.config.backup.name)
}},scope:this}});
this.backupsCombobox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Harvesting.Backup"),store:backupsStore,valueField:"name",displayField:"name",name:"backups",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200,height:120})
}return this.backupsCombobox
},getValidationCombobox:function(){if(!this.validationCombobox){this.validationCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["NONE",Openwis.i18n("Metadata.Validation.None")],["XSD_ONLY",Openwis.i18n("Metadata.Validation.XsdOnly")],["FULL",Openwis.i18n("Metadata.Validation.Full")]]}),valueField:"id",displayField:"value",value:"NONE",name:"validationMode",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,fieldLabel:Openwis.i18n("Harvesting.Validation")})
}return this.validationCombobox
},getDateInterval:function(){if(!this.dateInterval){this.dateInterval=new Ext.form.CompositeField({name:"dateInterval",fieldLabel:Openwis.i18n("Harvesting.DateInterval"),style:{margin:"0px 5px 0px 5px"},items:[new Ext.Container({border:false,html:Openwis.i18n("Common.Extent.Temporal.From"),cls:"formItems"}),this.getDateFrom(),new Ext.Container({border:false,html:Openwis.i18n("Common.Extent.Temporal.To"),cls:"formItems"}),this.getDateTo()]})
}return this.dateInterval
},getDateFrom:function(){if(!this.dateFrom){this.dateFrom=new Ext.form.DateField({name:"dateFrom",editable:true,format:"Y-m-d",width:120})
}return this.dateFrom
},getDateTo:function(){if(!this.dateTo){this.dateTo=new Ext.form.DateField({name:"dateTo",editable:true,format:"Y-m-d",width:120})
}return this.dateTo
},getProviderConfigurationFieldSet:function(){if(!this.providerConfigurationFieldSet){this.providerConfigurationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Harvesting.ProviderConfiguration"),autoHeight:true,collapsed:false,collapsible:true});
this.providerConfigurationFieldSet.add(new Ext.Container({html:Openwis.i18n("Harvesting.ProviderConfiguration.Fetch.Note"),border:false,cls:"infoMsg",style:{margin:"0px 0px 5px 0px"}}));
this.providerConfigurationFieldSet.add(this.getProviderConfigurationSetCombobox());
this.providerConfigurationFieldSet.add(this.getProviderConfigurationPrefixCombobox());
this.providerConfigurationFieldSet.addButton(new Ext.Button(this.getFetchRemoteInfoAction()))
}return this.providerConfigurationFieldSet
},getProviderConfigurationSetCombobox:function(){if(!this.providerConfigurationSetCombobox){this.providerConfigurationSetCombobox=new Ext.form.ComboBox({store:new Ext.data.JsonStore({idProperty:"name",fields:[{name:"name"},{name:"label"}]}),fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Set"),valueField:"name",displayField:"label",disabled:true,name:"set",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.providerConfigurationSetCombobox
},getProviderConfigurationPrefixCombobox:function(){if(!this.providerConfigurationPrefixCombobox){this.providerConfigurationPrefixCombobox=new Ext.form.ComboBox({store:[],disabled:true,fieldLabel:Openwis.i18n("Harvesting.ProviderConfiguration.Prefix"),name:"prefix",mode:"local",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.providerConfigurationPrefixCombobox
},getFetchRemoteInfoAction:function(){if(!this.fetchRemoteInfoAction){this.fetchRemoteInfoAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Fetch"),scope:this,handler:function(){var url=this.getUrlTextField().getValue();
if(url.trim()!=""){var params={url:url,synchronization:true};
new Openwis.Handler.Get({url:configOptions.locService+"/xml.harvest.oaipmh.info",params:params,listeners:{success:function(remoteConfig){this.getProviderConfigurationSetCombobox().getStore().loadData(remoteConfig.sets);
this.getProviderConfigurationSetCombobox().setDisabled(false);
if(this.isEdition()&&this.config.configuration.criteriaSet){this.getProviderConfigurationSetCombobox().setValue(this.config.configuration.criteriaSet)
}this.getProviderConfigurationPrefixCombobox().getStore().loadData(remoteConfig.formats);
this.getProviderConfigurationPrefixCombobox().setDisabled(false);
if(this.isEdition()&&this.config.configuration.criteriaPrefix){this.getProviderConfigurationPrefixCombobox().setValue(this.config.configuration.criteriaPrefix)
}},scope:this}}).proceed()
}else{Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Harvesting.ProviderConfiguration.Fetch.Remote.Url.Mandatory"))
}}})
}return this.fetchRemoteInfoAction
},getRecurrentProcessingCompositeField:function(){if(!this.recurrentProcessingCompositeField){this.recurrentProcessingCompositeField=new Ext.form.CompositeField({width:360,items:[{xtype:"container",style:{paddingLeft:"20px"}},new Ext.form.Label({html:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Label")}),this.getRecurrentProcessingNumberField(),this.getRecurrentProcessingFrequencyCombobox()]})
}return this.recurrentProcessingCompositeField
},getRecurrentProcessingNumberField:function(){if(!this.recurrentProcessingNumberField){this.recurrentProcessingNumberField=new Ext.form.NumberField({name:"frequencyNumber",width:40,allowDecimals:false,allowNegative:false,minValue:1,value:""})
}return this.recurrentProcessingNumberField
},getRecurrentProcessingFrequencyCombobox:function(){if(!this.recurrentProcessingFrequencyCombobox){this.recurrentProcessingFrequencyCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["DAY",Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Day")],["HOUR",Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.Hour")]]}),valueField:"id",displayField:"value",value:"HOUR",name:"frequencyComboBox",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:80})
}return this.recurrentProcessingFrequencyCombobox
},getStartingDateCompositeField:function(){if(!this.startingDateCompositeField){this.startingDateCompositeField=new Ext.form.CompositeField({width:470,items:[{xtype:"container",style:{paddingLeft:"20px"}},new Ext.form.Label({html:Openwis.i18n("RequestSubscription.SSP.Schedule.RecurrentPeriod.StartingAt")}),this.getStartingDateField(),this.getStartingDateTimeField()]})
}return this.startingDateCompositeField
},getStartingDateField:function(){if(!this.startingDateField){this.startingDateField=new Ext.form.DateField({name:"startingDate",editable:false,format:"Y-m-d",value:new Date()})
}return this.startingDateField
},getStartingDateTimeField:function(){if(!this.startingDateTimeField){this.startingDateTimeField=new Ext.form.TimeField({name:"startingDateTimeField",increment:15,format:"H:i",value:"00:00",width:60})
}return this.startingDateTimeField
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getTaskFormPanel().getForm().isValid()&&this.validate()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.save",params:this.getHarvestingTask(),listeners:{success:function(config){this.fireEvent("synchroTaskSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getHarvestingTask:function(){var task={};
if(this.isEdition()){task.id=this.editTaskId;
task.status=this.config.status
}else{task.status="ACTIVE"
}task.name=this.getNameTextField().getValue();
task.type="oaipmh";
if(this.getBackupsCombobox().getValue()!=""){task.backup={name:this.getBackupsCombobox().getValue()}
}task.synchronizationTask=true;
task.validationMode=this.getValidationCombobox().getValue();
task.incremental=true;
task.runMode={};
task.runMode.recurrent=true;
if(task.runMode.recurrent){var recurrenceValue;
var frequencyUnit=this.getRecurrentProcessingFrequencyCombobox().getValue();
if(frequencyUnit=="HOUR"){recurrenceValue=this.getRecurrentProcessingNumberField().getValue()*3600
}else{recurrenceValue=this.getRecurrentProcessingNumberField().getValue()*3600*24
}task.runMode.recurrentScale=this.getRecurrentProcessingFrequencyCombobox().getValue();
task.runMode.recurrencePeriod=recurrenceValue;
task.runMode.startingDate=this.getStartingDateField().getValue().format("Y-m-d")+"T"+this.getStartingDateTimeField().getValue()+":00Z"
}task.configuration={};
task.configuration.url=this.getUrlTextField().getValue();
if(this.getUseAccountCheckBox().checked){task.configuration.userName=this.getUsernameTextField().getValue();
task.configuration.password=this.getPasswordTextField().getValue()
}if(this.getDateFrom().getValue()!=""){task.configuration.dateFrom=Openwis.Utils.Date.formatToISODate(this.getDateFrom().getValue())
}if(this.getDateTo().getValue()!=""){task.configuration.dateTo=Openwis.Utils.Date.formatToISODate(this.getDateTo().getValue())
}if(this.isEdition()){if(this.getProviderConfigurationSetCombobox().disabled){task.configuration.criteriaSet=this.config.configuration.criteriaSet
}else{if(this.getProviderConfigurationSetCombobox().getValue()!=""){task.configuration.criteriaSet=this.getProviderConfigurationSetCombobox().getValue()
}}if(this.getProviderConfigurationPrefixCombobox().disabled){task.configuration.criteriaPrefix=this.config.configuration.criteriaPrefix
}else{if(this.getProviderConfigurationPrefixCombobox().getValue()!=""){task.configuration.criteriaPrefix=this.getProviderConfigurationPrefixCombobox().getValue()
}}}else{if(this.getProviderConfigurationSetCombobox().getValue()!=""){task.configuration.criteriaSet=this.getProviderConfigurationSetCombobox().getValue()
}if(this.getProviderConfigurationPrefixCombobox().getValue()!=""){task.configuration.criteriaPrefix=this.getProviderConfigurationPrefixCombobox().getValue()
}}var indexOfLocalCategory=this.getLocalCategoryStore().findExact("name",task.configuration.criteriaSet);
var localCategory=this.getLocalCategoryStore().getAt(indexOfLocalCategory);
task.category={};
task.category.id=localCategory.get("id");
task.category.name=localCategory.get("name");
return task
},validate:function(){if(this.getDateFrom().getValue()!=""&&this.getDateTo().getValue()!=""&&this.getDateFrom().getValue()>this.getDateTo().getValue()){Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Common.Extent.Temporal.Error.From.After.To"));
return false
}return true
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Synchro");
Openwis.Admin.Synchro.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Synchro.All.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSynchroTaskGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Synchro.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getSynchroTaskGrid:function(){if(!this.synchroTaskGrid){this.synchroTaskGrid=new Ext.grid.GridPanel({id:"synchroGrid",height:400,border:true,store:this.getSynchroTaskStore(),loadMask:true,columns:[{id:"monitorStatus",header:" ",dataIndex:"monitor",sortable:false,hideable:false,renderer:this.monitorImg,width:30},{id:"monitorProgress",header:Openwis.i18n("Synchro.Processed"),dataIndex:"progress",sortable:false,hideable:false,renderer:this.monitorProgress,width:55},{id:"name",header:Openwis.i18n("Synchro.Name"),dataIndex:"name",sortable:true,hideable:false},{id:"lastRun",header:Openwis.i18n("Synchro.LastRun"),dataIndex:"lastRun",sortable:true,hideable:false,renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong},{id:"backup",header:Openwis.i18n("Synchro.Backup"),dataIndex:"backup",sortable:true,hideable:false,renderer:this.backupRenderer}],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},sm:new Ext.grid.RowSelectionModel({singleSelect:true,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getReportAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getActivateAction().setDisabled(sm.getCount()==0||record.get("status")!="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getDeactivateAction().setDisabled(sm.getCount()==0||record.get("status")=="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getRunAction().setDisabled(sm.getCount()==0||record.get("status")!="ACTIVE")
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getReportAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getActivateAction().setDisabled(sm.getCount()==0||record.get("status")!="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getDeactivateAction().setDisabled(sm.getCount()==0||record.get("status")=="SUSPENDED"||record.get("status")=="SUSPENDED_BACKUP");
sm.grid.ownerCt.getRunAction().setDisabled(sm.getCount()==0||record.get("status")!="ACTIVE")
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getSynchroTaskStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Synchro.Administration.All.Display.Range"),emptyMsg:Openwis.i18n("Synchro.Administration.All.No.Task")})});
this.synchroTaskGrid.addButton(new Ext.Button(this.getNewAction()));
this.synchroTaskGrid.addButton(new Ext.Button(this.getReportAction()));
this.synchroTaskGrid.addButton(new Ext.Button(this.getEditAction()));
this.synchroTaskGrid.addButton(new Ext.Button(this.getRemoveAction()));
this.synchroTaskGrid.addButton(new Ext.Button(this.getActivateAction()));
this.synchroTaskGrid.addButton(new Ext.Button(this.getDeactivateAction()));
this.synchroTaskGrid.addButton(new Ext.Button(this.getRunAction()))
}return this.synchroTaskGrid
},getSynchroTaskStore:function(){if(!this.synchroTaskStore){this.synchroTaskStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.harvest.all",remoteSort:true,root:"rows",fields:[{name:"id",mapping:"object.id"},{name:"uuid",mapping:"object.uuid"},{name:"name",mapping:"object.name",sortType:Ext.data.SortTypes.asUCString},{name:"type",mapping:"object.type"},{name:"lastRun",mapping:"object.lastRun"},{name:"backup",mapping:"object.backup"},{name:"status",mapping:"object.status"},{name:"lastResult",mapping:"object.lastResult"},{name:"running"},{name:"progress"},{name:"monitor",convert:this.convertMonitor}],sortInfo:{field:"name",direction:"ASC"}});
this.synchroTaskStore.setBaseParam("isSynchronization",true)
}return this.synchroTaskStore
},getNewAction:function(){if(!this.newAction){this.newAction=new Ext.menu.Item({text:Openwis.i18n("Common.Btn.New"),scope:this,handler:function(){new Openwis.Admin.Synchro.Manage({operationMode:"Create",listeners:{synchroTaskSaved:function(){this.getSynchroTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.newAction
},getReportAction:function(){if(!this.reportAction){this.reportAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Report"),scope:this,handler:function(){var rec=this.getSynchroTaskGrid().getSelectionModel().getSelected();
new Openwis.Common.Metadata.Report({lastResult:rec.get("lastResult"),harvestingTaskId:rec.get("id")})
}})
}return this.reportAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var rec=this.getSynchroTaskGrid().getSelectionModel().getSelected();
new Openwis.Admin.Synchro.Manage({operationMode:"Edit",editTaskId:rec.get("id"),listeners:{synchroTaskSaved:function(){this.getSynchroTaskGrid().getStore().reload()
},scope:this}})
}})
}return this.editAction
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getSynchroTaskGrid().getSelectionModel().getSelected();
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.harvest.remove",params:selection.get("id"),listeners:{success:function(){this.getSynchroTaskGrid().getStore().reload()
},scope:this}}).proceed()
}})
}return this.removeAction
},getActivateAction:function(){if(!this.activateAction){this.activateAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Activate"),scope:this,handler:function(){var selectedRec=this.getSynchroTaskGrid().getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.activation",params:{id:selectedRec.get("id"),activate:true},listeners:{success:function(){this.getSynchroTaskGrid().getStore().reload();
this.getSynchroTaskGrid().getSelectionModel().clearSelections(false)
},scope:this}}).proceed()
}})
}return this.activateAction
},getDeactivateAction:function(){if(!this.deactivateAction){this.deactivateAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Deactivate"),scope:this,handler:function(){var selectedRec=this.getSynchroTaskGrid().getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.activation",params:{id:selectedRec.get("id"),activate:false},listeners:{success:function(){this.getSynchroTaskGrid().getStore().reload();
this.getSynchroTaskGrid().getSelectionModel().clearSelections(false)
},scope:this}}).proceed()
}})
}return this.deactivateAction
},getRunAction:function(){if(!this.runAction){this.runAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Run"),scope:this,handler:function(){var selectedRec=this.getSynchroTaskGrid().getSelectionModel().getSelected();
var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.harvest.run",params:selectedRec.get("id"),listeners:{success:function(){this.getSynchroTaskGrid().getStore().reload()
},scope:this}});
saveHandler.proceed()
}})
}return this.runAction
},statusRenderer:function(status){if(status=="ACTIVE"){return Openwis.i18n("Synchro.Status.Active")
}else{if(status=="SUSPENDED"){return Openwis.i18n("Synchro.Status.Suspended")
}else{if(status=="SUSPENDED_BACKUP"){return Openwis.i18n("Synchro.Status.SuspendedBackup")
}else{return status
}}}},backupRenderer:function(backup){if(backup&&backup.name){return backup.name
}else{return""
}},convertMonitor:function(v,rec){if(rec.object.status=="ACTIVE"&&rec.running){return"RUNNING"
}else{return rec.object.status
}},monitorImg:function(status){if(status=="ACTIVE"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_active.gif"/>'
}else{if(status=="RUNNING"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_inprogress.png"/>'
}else{if(status=="SUSPENDED"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_inactive.gif"/>'
}else{if(status=="SUSPENDED_BACKUP"){return'<img src="'+configOptions.url+'/images/openwis/icons/harvesting_inactive_backup.gif"/>'
}}}}},monitorProgress:function(progress){result=" ";
if(Ext.isNumber(progress)){if(progress>0){result=progress
}}return result
}});Ext.ns("Openwis.Admin.Thesauri");
Openwis.Admin.Thesauri.Manage=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Thesauri.Manage.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.thesaurus.list",params:this.getManageThesaurusInfos(),listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
Ext.each(this.config,function(thesauris,index){var thesaurisGrid=this.getThesauriGrid(thesauris);
var thesaurisFieldSet=new Ext.form.FieldSet({title:thesauris.label,autoHeight:true,collapsed:true,collapsible:true,listeners:{afterrender:function(){thesaurisFieldSet.addListener("collapse",this.onGuiChanged,this);
thesaurisFieldSet.addListener("expand",this.onGuiChanged,this)
},scope:this}});
var addBtn=new Ext.Button(new Ext.Action({iconCls:"icon-add",scope:this,handler:function(){createFormPanel.setVisible(true);
addBtn.setVisible(false);
this.doLayout()
}}));
var newFileTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("ThesauriManagement.Local"),allowBlank:false,width:200});
var createFormPanel=new Ext.form.FormPanel({itemCls:"formItems",border:false,buttons:[{text:Openwis.i18n("Common.Btn.Create"),handler:function(btn,e){if(createFormPanel.getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.thesaurus.add",params:this.getThesaurisCreateInfos(newFileTextField,thesauris),listeners:{success:function(config){newFileTextField.reset();
thesauris.thesaurusListDTO.push(config);
thesaurisGrid.getStore().loadData(thesauris.thesaurusListDTO);
createFormPanel.setVisible(false);
addBtn.setVisible(true);
this.doLayout()
},scope:this}});
saveHandler.proceed()
}},scope:this}]});
createFormPanel.add(newFileTextField);
createFormPanel.setVisible(false);
thesaurisFieldSet.add(addBtn);
thesaurisFieldSet.add(createFormPanel);
thesaurisFieldSet.add(thesaurisGrid);
this.add(thesaurisFieldSet)
},this);
this.add(this.getUploadHeader());
this.getUploadFormPanel().add(this.getThesaurusCategoryCombobox());
this.getUploadFormPanel().add(this.getThesaurusFileUploadField());
this.add(this.getUploadFormPanel());
this.doLayout()
},getThesaurisCreateInfos:function(newFileTextField,thesauris){var thesaurusInfos={};
thesaurusInfos.fname=newFileTextField.getValue();
thesaurusInfos.dname=thesauris.label;
thesaurusInfos.type="local";
return thesaurusInfos
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("ThesauriManagement.Manage.Title"),cls:"administrationTitle1"})
}return this.header
},getUploadHeader:function(){if(!this.uploadHeader){this.uploadHeader=new Ext.Container({html:Openwis.i18n("ThesauriManagement.Upload.Title"),cls:"administrationTitle1"})
}return this.uploadHeader
},getThesauriGrid:function(thesauris){var thesauriGrid=new Ext.grid.GridPanel({store:new Ext.data.JsonStore({autoDestroy:true,idProperty:"fname",fields:[{name:"type"},{name:"fname"},{name:"value"},{name:"dname"}]}),id:"thesauri"+thesauris.label+"Grid",height:160,border:true,loadMask:true,columns:[{id:"type",header:Openwis.i18n("ThesauriManagement.Type"),dataIndex:"type",sortable:true,width:100},{id:"fname",header:Openwis.i18n("ThesauriManagement.Name"),dataIndex:"fname",sortable:true,width:100}],autoExpandColumn:"fname",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().loadData(thesauris.thesaurusListDTO)
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){if(sm.getCount()!=1){viewEditAction.setText(Openwis.i18n("Common.Btn.ViewEdit"))
}else{if(record.data.type=="local"){viewEditAction.setText(Openwis.i18n("Common.Btn.Edit"))
}else{viewEditAction.setText(Openwis.i18n("Common.Btn.View"))
}}downloadAction.setDisabled(sm.getCount()!=1);
deleteAction.setDisabled(sm.getCount()==0);
viewEditAction.setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){if(sm.getCount()!=1){viewEditAction.setText(Openwis.i18n("Common.Btn.ViewEdit"))
}else{if(record.data.type=="local"){viewEditAction.setText(Openwis.i18n("Common.Btn.Edit"))
}else{viewEditAction.setText(Openwis.i18n("Common.Btn.View"))
}}downloadAction.setDisabled(sm.getCount()!=1);
deleteAction.setDisabled(sm.getCount()==0);
viewEditAction.setDisabled(sm.getCount()!=1)
}}})});
var downloadAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Download"),scope:this,handler:function(){var selectedRec=thesauriGrid.getSelectionModel().getSelected();
window.location.href=configOptions.locService+"/xml.thesaurus.download?thesaurus="+selectedRec.data.value
}});
var deleteAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Delete"),scope:this,handler:function(){var selection=thesauriGrid.getSelectionModel().getSelections();
var params={keywordListDTO:[]};
Ext.each(selection,function(item,index,allItems){params.keywordListDTO.push({thesaurus:item.get("value")})
},this);
var msg=null;
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.thesaurus.delete",params:params,confirmMsg:msg,listeners:{success:function(){Ext.each(selection,function(item,index,allItems){thesauris.thesaurusListDTO.remove(item.json)
},this);
thesauriGrid.getStore().loadData(thesauris.thesaurusListDTO);
downloadAction.setDisabled(true);
deleteAction.setDisabled(true);
viewEditAction.setDisabled(true)
},scope:this}});
removeHandler.proceed()
}});
var viewEditAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.ViewEdit"),scope:this,handler:function(){var selectedRec=thesauriGrid.getSelectionModel().getSelected();
var params=this.getViewEditThesaurusInfos(selectedRec.data);
new Openwis.Admin.Thesauri.ViewEdit({title:selectedRec.data.value,thesaurusType:thesauris.label,mode:selectedRec.data.type,params:params})
}});
thesauriGrid.addButton(new Ext.Button(downloadAction));
thesauriGrid.addButton(new Ext.Button(deleteAction));
thesauriGrid.addButton(new Ext.Button(viewEditAction));
return thesauriGrid
},getUploadFormPanel:function(){if(!this.uploadFormPanel){this.uploadFormPanel=new Ext.FormPanel({fileUpload:true,itemCls:"formItems",border:false,errorReader:new Ext.data.XmlReader({record:"field",success:"@success"},["id","msg"]),buttons:[{text:Openwis.i18n("Common.Btn.Upload"),scope:this,handler:function(){if(this.getUploadFormPanel().getForm().isValid()){this.getUploadFormPanel().getForm().submit({url:configOptions.locService+"/xml.thesaurus.upload",scope:this,params:this.getUploadThesaurusInfos(),success:function(fp,action){var gridToReload=Ext.getCmp("thesauri"+this.getUploadThesaurusInfos().dname+"Grid");
var jsonData=fp.errorReader.xmlData.getElementsByTagName("jsonData")[0].childNodes[0].nodeValue;
var result=Ext.decode(jsonData);
if(result.ok){gridToReload.getStore().add(new Ext.data.Record(result.o))
}else{Openwis.Utils.MessageBox.displayErrorMsg(result.o)
}},failure:function(response){Openwis.Utils.MessageBox.displayInternalError()
}})
}}}]})
}return this.uploadFormPanel
},getThesaurusCategoryCombobox:function(){if(!this.thesaurusCategoryCombobox){this.thesaurusCategoryCombobox=new Ext.form.ComboBox({store:new Ext.data.JsonStore({autoDestroy:true,idProperty:"label",fields:[{name:"label"}]}),valueField:"label",displayField:"label",name:"thesaurusCategory",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200,allowBlank:false,fieldLabel:Openwis.i18n("ThesauriManagement.Category")});
this.thesaurusCategoryCombobox.getStore().loadData(this.config)
}return this.thesaurusCategoryCombobox
},getThesaurusFileUploadField:function(){if(!this.thesaurusFileUploadField){this.thesaurusFileUploadField=new Ext.ux.form.FileUploadField({xtype:"fileuploadfield",allowBlank:false,buttonCfg:{text:Openwis.i18n("Common.Btn.Browse")},fieldLabel:Openwis.i18n("ThesauriManagement.File"),width:360})
}return this.thesaurusFileUploadField
},getViewEditThesaurusInfos:function(selectedRec){var viewEditThesaurusInfos={};
viewEditThesaurusInfos.thesaurus=selectedRec.value;
viewEditThesaurusInfos.type="all-thesauri";
return viewEditThesaurusInfos
},getUploadThesaurusInfos:function(){var uploadThesaurusInfos={};
uploadThesaurusInfos.file=this.getThesaurusFileUploadField().getValue();
uploadThesaurusInfos.dname=this.getThesaurusCategoryCombobox().getValue();
uploadThesaurusInfos.type="external";
return uploadThesaurusInfos
},getManageThesaurusInfos:function(){var manageThesaurusInfos={};
manageThesaurusInfos.type="all-thesauri";
return manageThesaurusInfos
},onGuiChanged:function(){this.fireEvent("guiChanged",false,true)
}});Ext.ns("Openwis.Admin.Thesauri");
Openwis.Admin.Thesauri.ViewEdit=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:this.title,width:650,height:500,modal:true,closeAction:"close"});
Openwis.Admin.Thesauri.ViewEdit.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var params=this.params;
var thesaurusType=this.thesaurusType;
var mode=this.mode;
var isBn=this.isBn;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.thesaurus.list",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getSearchFormPanel());
this.add(this.getSearchResultPanel());
if(this.isBn){this.addButton(new Ext.Button(this.getSubmitAction()))
}this.addButton(new Ext.Button(this.getCancelAction()));
this.show()
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,style:{padding:"5px"}});
this.searchFormPanel.add(this.getSearchInfo());
this.searchFormPanel.add(this.getKeyWordsTextField());
this.searchFormPanel.add(this.getSearchRadioGroup());
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
if(this.isEdition()){this.searchFormPanel.addButton(new Ext.Button(this.getAddElementAction()))
}}return this.searchFormPanel
},getSearchInfo:function(){if(!this.searchInfo){this.searchInfo=new Ext.Container({html:Openwis.i18n("ThesauriManagement.ViewEdit.SearchInfo"),border:false,cls:"infoMsg",style:{margin:"0px 0px 5px 0px"}})
}return this.searchInfo
},getSearchResultPanel:function(){if(!this.searchResultPanel){this.searchResultPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:120,border:false,style:{padding:"5px"}});
this.searchResultPanel.add(this.getSearchResultDisplayField());
this.searchResultPanel.add(this.getSearchResultGrid())
}return this.searchResultPanel
},getKeyWordsTextField:function(){if(!this.keyWordsTextField){this.keyWordsTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("ThesauriManagement.ViewEdit.KeyWords"),name:"name",width:150})
}return this.keyWordsTextField
},getSearchRadioGroup:function(){if(!this.searchRadioGroup){this.searchRadioGroup=new Ext.form.RadioGroup({items:[this.getStartWithRadio(),this.getContainsRadio(),this.getExactTermRadio()]})
}return this.searchRadioGroup
},getStartWithRadio:function(){if(!this.startWithRadio){this.startWithRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("ThesauriManagement.ViewEdit.StartWith"),name:"searchType",inputValue:0})
}return this.startWithRadio
},getContainsRadio:function(){if(!this.containsRadio){this.containsRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("ThesauriManagement.ViewEdit.Contains"),name:"searchType",inputValue:1,checked:true})
}return this.containsRadio
},getExactTermRadio:function(){if(!this.exactTermRadio){this.exactTermRadio=new Ext.form.Radio({boxLabel:Openwis.i18n("ThesauriManagement.ViewEdit.ExactTerm"),name:"searchType",inputValue:2})
}return this.exactTermRadio
},getAddElementAction:function(){if(!this.addElementAction){this.addElementAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Add"),scope:this,handler:function(){new Openwis.Admin.Thesauri.EditElement({thesaurus:this.params.thesaurus,thesaurusType:this.thesaurusType,params:this.params,mode:this.mode,isUpdate:false,listeners:{elementSubmitted:function(){this.getSearchAction().execute()
},scope:this}})
}})
}return this.addElementAction
},getSearchResultDisplayField:function(){if(!this.searchResultDisplayField){this.searchResultDisplayField=new Ext.form.DisplayField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.NbTerms"),name:"searchResultDisplayField",width:150});
this.searchResultDisplayField.setVisible(false)
}return this.searchResultDisplayField
},getSearchResultGrid:function(){if(!this.searchResultGrid){this.searchResultGrid=new Ext.grid.GridPanel({store:new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"value"},{name:"thesaurus"},{name:"code"}]}),style:{padding:"5px"},id:"searchResultGrid",height:260,border:true,loadMask:true,columns:[{id:"value",header:Openwis.i18n("ThesauriManagement.ViewEdit.Label"),dataIndex:"value",sortable:true,width:100}],autoExpandColumn:"value",sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){editElementAction.setDisabled(sm.getCount()!=1);
deleteElementAction.setDisabled(sm.getCount()==0);
this.getSubmitAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){editElementAction.setDisabled(sm.getCount()!=1);
deleteElementAction.setDisabled(sm.getCount()==0);
this.getSubmitAction().setDisabled(sm.getCount()==0)
},scope:this}})});
var viewEditButtonLabel=Openwis.i18n("Common.Btn.View");
if(this.isEdition()){viewEditButtonLabel=Openwis.i18n("Common.Btn.Edit")
}var editElementAction=new Ext.Action({disabled:true,text:viewEditButtonLabel,scope:this,handler:function(){var selectedRec=this.getSearchResultGrid().getSelectionModel().getSelected();
var oldParams=this.params;
new Openwis.Admin.Thesauri.EditElement({params:oldParams,thesaurus:this.params.thesaurus,thesaurusType:this.thesaurusType,mode:this.mode,isUpdate:true,keywordSelected:selectedRec.json,listeners:{elementSubmitted:function(){this.getSearchAction().execute()
},scope:this}})
}});
var deleteElementAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Delete"),scope:this,handler:function(){var selection=this.getSearchResultGrid().getSelectionModel().getSelections();
var params={keywordListDTO:[]};
Ext.each(selection,function(item,index,allItems){params.keywordListDTO.push({thesaurus:item.get("thesaurus"),code:item.get("code")})
},this);
var msg=null;
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.thesaurus.deleteElement",params:params,confirmMsg:msg,listeners:{success:function(){this.getSearchAction().execute();
editElementAction.setDisabled(true);
deleteElementAction.setDisabled(true)
},scope:this}});
removeHandler.proceed()
}});
if(!this.isBn){if(this.isEdition()){this.searchResultGrid.addButton(new Ext.Button(this.getAddElementAction()));
this.searchResultGrid.addButton(new Ext.Button(deleteElementAction))
}this.searchResultGrid.addButton(new Ext.Button(editElementAction))
}this.searchResultGrid.setVisible(false)
}return this.searchResultGrid
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){if(this.getSearchFormPanel().getForm().isValid()){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.thesaurus.viewEdit.search",params:this.getViewEditSearchInfos(),listeners:{success:function(config){this.getSearchResultDisplayField().setValue(config.size());
this.getSearchResultDisplayField().setVisible(true);
this.getSearchResultGrid().getStore().loadData(config);
this.getSearchResultGrid().setVisible(true);
this.doLayout()
},failure:function(config){alert("failed")
},scope:this}});
getHandler.proceed()
}}})
}return this.searchAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Close"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getSubmitAction:function(){if(!this.submitAction){this.submitAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Submit"),scope:this,disabled:true,handler:function(){var selectionBn=this.getSearchResultGrid().getSelectionModel().getSelections();
this.fireEvent("viewEditBnSelection",selectionBn);
this.close()
}})
}return this.submitAction
},isEdition:function(){return(this.mode=="local")
},getViewEditSearchInfos:function(selectedRec){var viewEditSearchInfos={};
if(this.getKeyWordsTextField().getValue().trim()==""){viewEditSearchInfos.keyword="*"
}else{viewEditSearchInfos.keyword=this.getKeyWordsTextField().getValue()
}viewEditSearchInfos.thesauri=this.params.thesaurus;
viewEditSearchInfos.typeSearch=this.getSearchRadioGroup().getValue().inputValue;
viewEditSearchInfos.maxResults="200";
return viewEditSearchInfos
}});Ext.ns("Openwis.Admin.Thesauri");
Openwis.Admin.Thesauri.EditElement=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("ThesauriManagement.EditElement.Title"),width:650,height:720,modal:true,closeAction:"close"});
Openwis.Admin.Thesauri.EditElement.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var params=this.params;
var keywordSelected=this.keywordSelected;
var thesaurus=this.thesaurus;
var thesaurusType=this.thesaurusType;
var mode=this.mode;
var isUpdate=this.isUpdate;
if(!this.newBnList){this.newBnList=[]
}if(!this.deleteBnList){this.deleteBnList=[]
}var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.thesaurus.editElement",params:this.getKeywordInfos(),listeners:{success:function(config){this.config=config;
this.initialize(config)
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(config){this.add(this.getEditElementFormPanel(config));
if(this.isEdition()){this.addButton(new Ext.Button(this.getSubmitEltAction()))
}this.addButton(new Ext.Button(this.getCancelAction()));
this.doLayout();
this.show()
},getEditElementFormPanel:function(config){if(!this.editElementFormPanel){this.editElementFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,style:{padding:"5px"}});
if(config.keywordRef.edition){this.getIdentifierTextField().setValue(config.keywordRef.relativeCode);
this.getLabelTextField().setValue(config.keywordRef.value);
this.getDefinitionTextField().setValue(config.keywordRef.definition);
this.editElementFormPanel.add(this.getIdentifierTextField());
this.editElementFormPanel.add(this.getLabelTextField());
this.editElementFormPanel.add(this.getDefinitionTextField())
}else{this.getIdentifierDisplayField().setValue(config.keywordRef.relativeCode);
this.getLabelDisplayField().setValue(config.keywordRef.value);
this.getDefinitionDisplayField().setValue(config.keywordRef.definition);
this.editElementFormPanel.add(this.getIdentifierDisplayField());
this.editElementFormPanel.add(this.getLabelDisplayField());
this.editElementFormPanel.add(this.getDefinitionDisplayField())
}if(this.thesaurusType=="place"){this.getNBCoordinateTextField().setValue(config.keywordRef.coordNorth);
this.getWBCoordinateTextField().setValue(config.keywordRef.coordWest);
this.getEBCoordinateTextField().setValue(config.keywordRef.coordEast);
this.getSBCoordinateTextField().setValue(config.keywordRef.coordSouth);
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.createLabel(Openwis.i18n("ThesauriManagement.EditElement.NBCoord")));
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.getNBCoordinateTextField());
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.createLabel(Openwis.i18n("ThesauriManagement.EditElement.WBCoord")));
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.createLabel(Openwis.i18n("ThesauriManagement.EditElement.EBCoord")));
this.getCoordPanel().add(this.getWBCoordinateTextField());
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.getEBCoordinateTextField());
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.createLabel(Openwis.i18n("ThesauriManagement.EditElement.SBCoord")));
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.createDummy());
this.getCoordPanel().add(this.getSBCoordinateTextField());
this.getCoordPanel().add(this.createDummy());
this.editElementFormPanel.add(this.getCoordPanel())
}Ext.each(config.broadNarrListDTO,function(broadNarrListDTO,index){if(broadNarrListDTO.keywordType=="broader"){this.editElementFormPanel.add(this.getBroadGrid(config,broadNarrListDTO))
}else{if(broadNarrListDTO.keywordType=="narrower"){this.editElementFormPanel.add(this.getNarrGrid(config,broadNarrListDTO))
}}},this)
}return this.editElementFormPanel
},getCoordPanel:function(){if(!this.coordPanel){this.coordPanel=new Ext.Panel({layout:"table",style:{padding:"5px"},layoutConfig:{columns:3,tableAttrs:{style:{width:"100%",padding:"20px"}}}})
}return this.coordPanel
},createLabel:function(label){return new Openwis.Utils.Misc.createLabel(label)
},createDummy:function(){return new Openwis.Utils.Misc.createDummy()
},getIdentifierDisplayField:function(){if(!this.identifierDisplayField){this.identifierDisplayField=new Ext.form.DisplayField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.Id"),name:"id",width:150})
}return this.identifierDisplayField
},getLabelDisplayField:function(){if(!this.labelDisplayField){this.labelDisplayField=new Ext.form.DisplayField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.Label"),name:"label",width:150})
}return this.labelDisplayField
},getDefinitionDisplayField:function(){if(!this.definitionDisplayField){this.definitionDisplayField=new Ext.form.DisplayField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.Definition"),name:"def",width:150})
}return this.definitionDisplayField
},getIdentifierTextField:function(){if(!this.identifierTextField){this.identifierTextField=new Ext.form.NumberField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.Id"),allowDecimals:false,allowNegative:false,name:"id",allowBlank:false,width:150})
}return this.identifierTextField
},getLabelTextField:function(){if(!this.labelTextField){this.labelTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.Label"),name:"label",allowBlank:false,width:150})
}return this.labelTextField
},getDefinitionTextField:function(){if(!this.definitionTextField){this.definitionTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("ThesauriManagement.EditElement.Definition"),name:"def",allowBlank:false,width:150})
}return this.definitionTextField
},getNBCoordinateTextField:function(){if(!this.nBCoordinateTextField){this.nBCoordinateTextField=new Ext.form.TextField({name:"nbc",allowBlank:!this.isEdition(),width:100})
}return this.nBCoordinateTextField
},getWBCoordinateTextField:function(){if(!this.wBCoordinateTextField){this.wBCoordinateTextField=new Ext.form.TextField({name:"wbc",allowBlank:!this.isEdition(),width:100})
}return this.wBCoordinateTextField
},getEBCoordinateTextField:function(){if(!this.eBCoordinateTextField){this.eBCoordinateTextField=new Ext.form.TextField({name:"ebc",allowBlank:!this.isEdition(),width:100})
}return this.eBCoordinateTextField
},getSBCoordinateTextField:function(){if(!this.sBCoordinateTextField){this.sBCoordinateTextField=new Ext.form.TextField({name:"sbc",allowBlank:!this.isEdition(),width:100})
}return this.sBCoordinateTextField
},getBroadGrid:function(config,broadNarrListDTO){if(!this.broadGrid){this.broadGrid=new Ext.grid.GridPanel({store:new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"value",sortType:Ext.data.SortTypes.asUCString}],sortInfo:{field:"value",direction:"ASC"}}),style:{padding:"5px"},id:"broadGrid",height:120,border:true,loadMask:true,columns:[{id:"value",header:broadNarrListDTO.keywordType+" "+Openwis.i18n("ThesauriManagement.EditElement.Term"),dataIndex:"value",sortable:true,width:100}],autoExpandColumn:"value",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().loadData(broadNarrListDTO.keywordListDTO)
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){this.grid.ownerCt.ownerCt.getDeleteElementAction().setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){this.grid.ownerCt.ownerCt.getDeleteElementAction().setDisabled(sm.getCount()!=1)
}}})});
if(config.keywordRef.edition){this.broadGrid.addButton(new Ext.Button(this.getAddElementAction()));
this.broadGrid.addButton(new Ext.Button(this.getDeleteElementAction()))
}}return this.broadGrid
},getDeleteElementAction:function(){if(!this.deleteElementAction){this.deleteElementAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Delete"),scope:this,handler:function(){var selectedRec=this.getBroadGrid().getSelectionModel().getSelected();
this.getBroadGrid().getStore().remove(selectedRec);
this.newBnList.remove(selectedRec);
this.deleteBnList.push(selectedRec);
this.getBroadGrid().getView().refresh();
this.deleteElementAction.setDisabled(true);
this.doLayout()
}})
}return this.deleteElementAction
},getAddElementAction:function(){if(!this.addElementAction){this.addElementAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Add"),scope:this,handler:function(){new Openwis.Admin.Thesauri.ViewEdit({title:Openwis.i18n("ThesauriManagement.EditElement.AddBN"),thesaurusType:this.thesaurus,mode:"edit",isBn:true,params:this.params,listeners:{viewEditBnSelection:function(records){for(var i=0;
i<records.length;
i++){var record=new Ext.data.Record(records[i].json);
this.getBroadGrid().getStore().add(record);
this.newBnList.push(record);
this.getBroadGrid().getView().refresh();
this.doLayout()
}},scope:this}})
}})
}return this.addElementAction
},getNarrGrid:function(config,broadNarrListDTO){var narrGrid=new Ext.grid.GridPanel({store:new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"value",sortType:Ext.data.SortTypes.asUCString}],sortInfo:{field:"value",direction:"ASC"}}),style:{padding:"5px"},id:"narrGrid",height:160,border:true,loadMask:true,columns:[{id:"value",header:broadNarrListDTO.keywordType+" "+Openwis.i18n("ThesauriManagement.EditElement.Term"),dataIndex:"value",sortable:true,width:100}],autoExpandColumn:"value",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().loadData(broadNarrListDTO.keywordListDTO)
}}});
return narrGrid
},getSubmitEltAction:function(){if(!this.submitEltAction){this.submitEltAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Submit"),scope:this,handler:function(){var urlServ="/xml.thesaurus.addElement";
if(this.isUpdate){urlServ="/xml.thesaurus.updateElement"
}if(this.getEditElementFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+urlServ,params:this.getEltInfos(),listeners:{success:function(config){this.fireEvent("elementSubmitted");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.submitEltAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Close"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},isEdition:function(){return(this.mode=="local")
},getKeywordInfos:function(){var keywordInfos={};
if(this.keywordSelected!=null){keywordInfos.id=this.keywordSelected.id;
keywordInfos.value=this.keywordSelected.value;
keywordInfos.lang=this.keywordSelected.lang;
keywordInfos.definition=this.keywordSelected.definition;
keywordInfos.code=this.keywordSelected.code;
keywordInfos.coordEast=this.keywordSelected.coordEast;
keywordInfos.coordWest=this.keywordSelected.coordWest;
keywordInfos.coordSouth=this.keywordSelected.coordSouth;
keywordInfos.coordNorth=this.keywordSelected.coordNorth;
keywordInfos.thesaurus=this.keywordSelected.thesaurus
}else{keywordInfos.thesaurus=this.thesaurus
}keywordInfos.edition=this.isEdition();
return keywordInfos
},getEltInfos:function(selectedRec){var eltsInfos={};
if(this.isUpdate){eltsInfos.code=this.keywordSelected.code
}eltsInfos.id=this.getIdentifierTextField().getValue();
eltsInfos.value=this.getLabelTextField().getValue();
eltsInfos.definition=this.getDefinitionTextField().getValue();
eltsInfos.coordSouth=this.getSBCoordinateTextField().getValue();
eltsInfos.coordEast=this.getEBCoordinateTextField().getValue();
eltsInfos.coordWest=this.getWBCoordinateTextField().getValue();
eltsInfos.coordNorth=this.getNBCoordinateTextField().getValue();
var newBnArray=new Array();
Ext.each(this.newBnList,function(bn,index){var n={};
n.code=bn.data.code;
newBnArray.push(n)
});
var bnList={};
bnList.keywordListDTO=newBnArray;
eltsInfos.broadNarrListDTO=bnList;
var delBnArray=new Array();
Ext.each(this.deleteBnList,function(bn,index){var n={};
n.code=bn.json.code;
delBnArray.push(n)
});
var delBnList={};
delBnList.keywordListDTO=delBnArray;
eltsInfos.delBroadNarrListDTO=delBnList;
eltsInfos.thesaurus=this.thesaurus;
return eltsInfos
}});Ext.ns("Openwis.Admin.Category");
Openwis.Admin.Category.Manage=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("CategoryManagement.Manage.Title"),layout:"fit",width:350,height:150,modal:true,closeAction:"close"});
Openwis.Admin.Category.Manage.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params={};
params.name=this.editCategoryName;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.category.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("categorySaved");
this.add(this.getCategoryFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name)
}this.show()
},getCategoryFormPanel:function(){if(!this.categoryFormPanel){this.categoryFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:125});
this.categoryFormPanel.add(this.getNameTextField())
}return this.categoryFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CategoryManagement.Manage.Name"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getCategoryFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.category.save",params:this.getCategory(),listeners:{success:function(config){this.fireEvent("categorySaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getCategory:function(){var category={};
if(this.isEdition()){category.id=this.config.id
}category.name=this.getNameTextField().getValue();
return category
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Category");
Openwis.Admin.Category.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Category.All.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getCategoryGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("CategoryManagement.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getCategoryGrid:function(){if(!this.categoryGrid){this.categoryGrid=new Ext.grid.GridPanel({id:"categoryGrid",height:400,border:true,store:this.getCategoryStore(),loadMask:true,columns:[{id:"name",header:Openwis.i18n("CategoryManagement.Name"),dataIndex:"name",sortable:true,width:300}],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0)
}}})});
this.categoryGrid.addButton(new Ext.Button(this.getNewAction()));
this.categoryGrid.addButton(new Ext.Button(this.getEditAction()));
this.categoryGrid.addButton(new Ext.Button(this.getRemoveAction()))
}return this.categoryGrid
},getCategoryStore:function(){if(!this.categoryStore){this.categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",fields:[{name:"id"},{name:"name",sortType:Ext.data.SortTypes.asUCString}],sortInfo:{field:"name",direction:"ASC"}})
}return this.categoryStore
},getNewAction:function(){if(!this.newAction){this.newAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),scope:this,handler:function(){new Openwis.Admin.Category.Manage({operationMode:"Create",listeners:{categorySaved:function(){this.getCategoryGrid().getStore().reload()
},scope:this}})
}})
}return this.newAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getCategoryGrid().getSelectionModel().getSelected();
new Openwis.Admin.Category.Manage({operationMode:"Edit",editCategoryName:selectedRec.get("name"),listeners:{categorySaved:function(){this.getCategoryGrid().getStore().reload()
},scope:this}})
}})
}return this.editAction
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getCategoryGrid().getSelectionModel().getSelections();
var params={categories:[]};
Ext.each(selection,function(item,index,allItems){params.categories.push({id:item.get("id"),name:item.get("name")})
},this);
var msg=null;
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.category.remove",params:params,confirmMsg:msg,listeners:{success:function(){this.getCategoryGrid().getStore().reload()
},scope:this}});
removeHandler.proceed()
}})
}return this.removeAction
}});Ext.ns("Openwis.Admin.Category");
Openwis.Admin.Category.Edit=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:"Edit Category ...",layout:"fit",width:480,height:140,modal:true,closeAction:"close"});
Openwis.Admin.Category.Edit.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){this.initialize()
},initialize:function(){this.addEvents("editCategorySaved");
this.add(this.getCategoryFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.show()
},getCategoryFormPanel:function(){if(!this.categoryFormPanel){this.categoryFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:125});
this.categoryFormPanel.add(this.getCategoriesComboBox())
}return this.categoryFormPanel
},getCategoriesComboBox:function(){if(!this.categoriesComboBox){this.categoriesComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("MetadataInsert.Category"),name:"category",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,editable:false,allowBlank:false,width:330,store:this.getCategoryStore(),displayField:"name",value:this.categoryName,valueField:"id"});
this.categoriesComboBox.getStore().load()
}return this.categoriesComboBox
},getCategoryStore:function(){if(!this.categoryStore){this.categoryStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.category.all",idProperty:"id",fields:[{name:"id"},{name:"name",sortType:Ext.data.SortTypes.asUCString}],sortInfo:{field:"name",direction:"ASC"}})
}return this.categoryStore
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){var params={productsMetadataUrn:this.metadataURNs,category:this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data};
var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.category.edit",params:params,listeners:{success:function(config){this.fireEvent("editCategorySaved");
this.close()
},scope:this}});
saveHandler.proceed()
}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
}});Ext.ns("Openwis.Admin.Group");
Openwis.Admin.Group.Manage=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Security.Group.Manage.Title"),layout:"fit",width:350,height:150,modal:true,closeAction:"close"});
Openwis.Admin.Group.Manage.superclass.initComponent.apply(this,arguments);
if(this.isEdition()){this.getInfosAndInitialize()
}else{this.initialize()
}},getInfosAndInitialize:function(){var params={};
params.name=this.editGroupName;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.group.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("groupSaved");
this.add(this.getGroupFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition()){this.getNameTextField().setValue(this.config.name);
this.getGlobalCheckBox().setValue(this.config.global);
this.getGlobalCheckBox().disable()
}this.show()
},getGroupFormPanel:function(){if(!this.groupFormPanel){this.groupFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:125});
this.groupFormPanel.add(this.getNameTextField());
this.groupFormPanel.add(this.getGlobalCheckBox())
}return this.groupFormPanel
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.Group.Manage.GroupName.Label"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getGlobalCheckBox:function(){if(!this.globalCheckBox){this.globalCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Security.Group.Manage.GlobalCheckBox.Label"),name:"global",width:125})
}return this.globalCheckBox
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:"Save",scope:this,handler:function(){if(this.getGroupFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.group.save",params:this.getGroup(),listeners:{success:function(config){this.fireEvent("groupSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:"Cancel",scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getGroup:function(){var group={};
if(this.isEdition()){group.id=this.config.id
}group.name=this.getNameTextField().getValue();
group.global=this.getGlobalCheckBox().getValue();
return group
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.Group");
Openwis.Admin.Group.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Group.All.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getGroupGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.Group.Title"),cls:"administrationTitle1"})
}return this.header
},getGroupGrid:function(){if(!this.groupGrid){this.groupGrid=new Ext.grid.GridPanel({id:"groupGrid",height:400,border:true,store:this.getGroupStore(),loadMask:true,columns:[{id:"name",header:Openwis.i18n("Security.Group.GroupName.Column"),dataIndex:"name",sortable:true,width:300},{id:"global",header:Openwis.i18n("Security.Group.Global.Column"),dataIndex:"global",sortable:true,width:100,xtype:"booleancolumn"}],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1||record.data.global);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0)
}}})});
this.groupGrid.addButton(new Ext.Button(this.getNewAction()));
this.groupGrid.addButton(new Ext.Button(this.getEditAction()));
this.groupGrid.addButton(new Ext.Button(this.getRemoveAction()));
this.groupGrid.addButton(new Ext.Button(this.getPrepareSynchronizeAction()))
}return this.groupGrid
},getGroupStore:function(){if(!this.groupStore){this.groupStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.group.all",idProperty:"id",fields:[{name:"id"},{name:"name",sortType:Ext.data.SortTypes.asUCString},{name:"global"}],sortInfo:{field:"name",direction:"ASC"}})
}return this.groupStore
},getNewAction:function(){if(!this.newAction){this.newAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),scope:this,handler:function(){new Openwis.Admin.Group.Manage({operationMode:"Create",listeners:{groupSaved:function(){this.getGroupGrid().getStore().reload()
},scope:this}})
}})
}return this.newAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getGroupGrid().getSelectionModel().getSelected();
new Openwis.Admin.Group.Manage({operationMode:"Edit",editGroupName:selectedRec.get("name"),listeners:{groupSaved:function(){this.getGroupGrid().getStore().reload()
},scope:this}})
}})
}return this.editAction
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getGroupGrid().getSelectionModel().getSelections();
var params={groups:[]};
var global=false;
Ext.each(selection,function(item,index,allItems){params.groups.push({id:item.get("id"),name:item.get("name"),global:item.get("global")});
if(item.get("global")){global=true
}},this);
var msg=null;
if(global){msg="Global group(s) will be removed. This modification will impact all other deployments of the circle of trust. Do you confirm the action ?"
}var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.group.remove",params:params,confirmMsg:msg,listeners:{success:function(){this.getGroupGrid().getStore().reload()
},scope:this}});
removeHandler.proceed()
}})
}return this.removeAction
},getPrepareSynchronizeAction:function(){if(!this.prepareSynchronizeAction){this.prepareSynchronizeAction=new Ext.Action({text:Openwis.i18n("Security.Group.Synchronize"),scope:this,handler:function(){var saveHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.group.synchronize",params:{perform:false},listeners:{success:function(prepareResult){var msg="";
for(var i=0;
i<prepareResult.prepSynchro.length;
i++){msg+=prepareResult.prepSynchro[i]+"<br/>"
}if(prepareResult.prepSynchro.length==0){Ext.Msg.show({title:Openwis.i18n("Security.Group.Synchronize.NoGroupDlg.Title"),msg:Openwis.i18n("Security.Group.Synchronize.NoGroupDlg.Msg"),buttons:Ext.Msg.OK,scope:this,icon:Ext.MessageBox.INFO})
}else{Ext.Msg.show({title:"Confirm Synchronize",msg:msg,buttons:Ext.Msg.YESNO,fn:function(buttonId){if(buttonId=="yes"){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.group.synchronize",params:{perform:true},listeners:{success:function(){this.getGroupGrid().getStore().reload()
},scope:this}});
saveHandler.proceed()
}},scope:this,icon:Ext.MessageBox.QUESTION})
}},scope:this}});
saveHandler.proceed()
}})
}return this.prepareSynchronizeAction
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
}}}}});Ext.ns("Openwis.Common.Dissemination");
Openwis.Common.Dissemination.MailDiffusion=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{border:false,allowAddressEdition:true});
Openwis.Common.Dissemination.MailDiffusion.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getAddressTextField());
this.getAdvancedFieldSet().add(this.getHeaderLineTextField());
this.getAdvancedFieldSet().add(this.getDispatchModeComboBox());
this.getAdvancedFieldSet().add(this.getSubjectTextField());
this.getAdvancedFieldSet().add(this.getAttachmentModeRadioGroup());
this.getAdvancedFieldSet().add(this.getFileNameTextField());
this.add(this.getAdvancedFieldSet());
this.mailFields={};
this.mailFields.address=this.getAddressTextField();
this.mailFields.headerLine=this.getHeaderLineTextField();
this.mailFields.mailDispatchMode=this.getDispatchModeComboBox();
this.mailFields.subject=this.getSubjectTextField();
this.mailFields.mailAttachmentMode=this.getAttachmentModeRadioGroup();
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
},getDisseminationValue:function(){var mail={};
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
this.ftpFields.host=this.getHostTextField();
this.ftpFields.path=this.getPathTextField();
this.ftpFields.user=this.getUserTextField();
this.ftpFields.password=this.getPasswordTextField();
this.ftpFields.port=this.getPortTextField();
this.ftpFields.passive=this.getPassiveCheckbox();
this.ftpFields.checkFileSize=this.getCheckFileSizeCheckbox();
this.ftpFields.fileName=this.getFileNameTextField();
this.ftpFields.encrypted=this.getEncryptedCheckbox()
},getHostTextField:function(){if(!this.hostTextField){var hostStore=new Ext.data.JsonStore({id:0,fields:[{name:"host"},{name:"path"},{name:"user"},{name:"password"},{name:"port"},{name:"passive"},{name:"checkFileSize"},{name:"fileName"},{name:"encrypted"}]});
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
},getOptionsCheckboxGroup:function(){if(!this.optionsCheckboxGroup){this.optionsCheckboxGroup=new Ext.form.CheckboxGroup({title:Openwis.i18n("Common.Dissemination.FTPDiffusion.Options.label"),items:[this.getPassiveCheckbox(),this.getCheckFileSizeCheckbox(),this.getEncryptedCheckbox()]})
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
},notifyFTPSelected:function(){var ftpHostSelected=this.getHostTextField().getValue();
if(ftpHostSelected){var ftpSelected=null;
for(var i=0;
i<this.getHostTextField().getStore().getCount();
i++){if(this.getHostTextField().getStore().getAt(i).get("host")==ftpHostSelected){ftpSelected=this.getHostTextField().getStore().getAt(i).data;
break
}}this.initializeFields(ftpSelected)
}},initializeFields:function(ftp){Ext.iterate(ftp,function(key,value){if(this.ftpFields[key]){this.ftpFields[key].setValue(value)
}},this)
},getDisseminationValue:function(){var ftp={};
Ext.iterate(this.ftpFields,function(key,field){ftp[key]=field.getValue()
},this);
return ftp
},refresh:function(favoritesFtps){this.getHostTextField().getStore().loadData(favoritesFtps)
}});Ext.ns("Openwis.Admin.User");
Openwis.Admin.User.Privileges=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{itemCls:"formItems",title:Openwis.i18n("Security.User.Privileges.Title"),width:600,height:500,items:[{layout:"table",border:false,layoutConfig:{columns:2},items:[this.createLabel(Openwis.i18n("Security.User.Privileges.Profile")),this.getProfileComboBox(),this.createLabel(Openwis.i18n("Security.User.Privileges.Groups")),this.getGroupsMultiSelector(),this.createLabel(Openwis.i18n("Security.User.Privileges.ClassOfService")),this.getClassOfServiceComboBox(),this.createLabel(Openwis.i18n("Security.User.Privileges.NeedUserAccount")),this.getNeedUserAccountCheckBox(),this.createLabel(Openwis.i18n("Security.User.Privileges.Backup")),this.getBackupMultiSelector()]}]});
Openwis.Admin.User.Privileges.superclass.initComponent.apply(this,arguments)
},createLabel:function(label){return new Openwis.Utils.Misc.createLabel(label)
},createDummy:function(){return new Openwis.Utils.Misc.createDummy()
},init:function(profiles,groups,backups,classOfServices){this.getProfileComboBox().getStore().loadData(profiles);
this.getGroupsMultiSelector().multiselects[0].store.loadData(groups);
this.getBackupMultiSelector().multiselects[0].store.loadData(backups);
this.getClassOfServiceComboBox().getStore().loadData(classOfServices)
},setUserInformation:function(user){this.getNeedUserAccountCheckBox().setValue(user.needUserAccount);
this.getProfileComboBox().setValue(user.profile);
this.getClassOfServiceComboBox().setValue(user.classOfService);
this.getBackupMultiSelector().multiselects[1].store.loadData(user.backUps);
this.getGroupsMultiSelector().multiselects[1].store.loadData(user.groups)
},getProfileComboBox:function(){if(!this.profileComboBox){var profilesStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.profileComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Security.User.Privileges.Profile"),name:"profile",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:profilesStore,editable:false,allowBlank:false,width:250,displayField:"name",valueField:"id"})
}return this.profileComboBox
},getClassOfServiceComboBox:function(){if(!this.classOfServiceComboBox){var classOfServiceStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.classOfServiceComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Security.User.Privileges.ClassOfService"),name:"Class Of Service",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:classOfServiceStore,editable:false,allowBlank:false,width:250,displayField:"name",valueField:"id"})
}return this.classOfServiceComboBox
},getNeedUserAccountCheckBox:function(){if(!this.needUserAccountCheckBox){this.needUserAccountCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Security.User.Privileges.NeedUserAccount"),name:"needUserAccount",width:125})
}return this.needUserAccountCheckBox
},getGroupsMultiSelector:function(){if(!this.isForm){var ds=new Ext.data.JsonStore({autoDestroy:true,idProperty:"name",fields:[{name:"id"},{name:"name"},{name:"global"}]});
var ds2=new Ext.data.JsonStore({autoDestroy:true,idProperty:"name",fields:[{name:"id"},{name:"name"},{name:"global"}]});
this.isForm=new Ext.ux.form.ItemSelector({xtype:"itemselector",name:"itemselector",fieldLabel:"ItemSelector",width:400,drawUpIcon:false,drawDownIcon:false,drawTopIcon:false,drawBotIcon:false,imagePath:"../../scripts/ext-ux/images/",multiselects:[{width:150,height:150,store:ds,legend:Openwis.i18n("Security.User.Privileges.Available"),displayField:"name",valueField:"name"},{width:150,height:150,store:ds2,legend:Openwis.i18n("Security.User.Privileges.Selected"),displayField:"name",valueField:"name"}]})
}return this.isForm
},getBackupMultiSelector:function(){if(!this.isBackupForm){var ds=new Ext.data.JsonStore({autoDestroy:true,idProperty:"name",fields:[{name:"name"}]});
var ds2=new Ext.data.JsonStore({autoDestroy:true,idProperty:"name",fields:[{name:"name"}]});
this.isBackupForm=new Ext.ux.form.ItemSelector({xtype:"itemselector",name:"itemselector",fieldLabel:"ItemSelector",width:400,drawUpIcon:false,drawDownIcon:false,drawTopIcon:false,drawBotIcon:false,imagePath:"../../scripts/ext-ux/images/",multiselects:[{width:150,height:150,store:ds,legend:Openwis.i18n("Security.User.Privileges.Available"),displayField:"name",valueField:"name"},{width:150,height:150,store:ds2,legend:Openwis.i18n("Security.User.Privileges.Selected"),displayField:"name",valueField:"name"}]})
}return this.isBackupForm
},getUser:function(user){if(!user){user={}
}user.needUserAccount=this.getNeedUserAccountCheckBox().getValue();
user.profile=this.getProfileComboBox().getValue();
user.classOfService=this.getClassOfServiceComboBox().getValue();
user.groups=[];
var groupStore=this.getGroupsMultiSelector().multiselects[1].store;
for(var i=0;
i<groupStore.getCount();
i++){var group=groupStore.getAt(i);
user.groups.push({name:group.get("name"),global:group.get("global"),id:group.get("id")})
}var backUps=this.getBackupMultiSelector().multiselects[1].store;
user.backUps=[];
for(var i=0;
i<backUps.getCount();
i++){var backUp=backUps.getAt(i);
user.backUps.push({name:backUp.get("name")})
}return user
}});Ext.ns("Openwis.Admin.User");
Openwis.Admin.User.ImportUser=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Security.User.Import.Title"),width:600,height:700,modal:true,closeAction:"close"});
Openwis.Admin.User.ImportUser.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("userImported");
this.add(this.getFilterFormPanel());
this.add(this.getImportUserGrid());
this.show()
},getImportUserStore:function(){if(!this.userImportStore){this.userImportStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.user.allImport",idProperty:"userName",fields:[{name:"userName"},{name:"name"},{name:"surName"},{name:"profile"}]})
}return this.userImportStore
},getImportUserGrid:function(){if(!this.importUsergrid){this.importUsergrid=new Ext.grid.GridPanel({id:"importUserGrid",height:600,border:true,store:this.getImportUserStore(),loadMask:true,columns:[{id:"username",header:Openwis.i18n("Security.User.UserName.Column"),dataIndex:"userName",sortable:true,width:150},{id:"surname",header:Openwis.i18n("Security.User.LastName.Column"),dataIndex:"name",sortable:true,width:150},{id:"name",header:Openwis.i18n("Security.User.FirstName.Column"),dataIndex:"surName",sortable:true,width:150},{id:"profile",header:Openwis.i18n("Security.User.Profile.Column"),dataIndex:"profile",sortable:true,width:150}],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getImportAction().setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getImportAction().setDisabled(sm.getCount()!=1)
}}})});
this.importUsergrid.addButton(new Ext.Button(this.getImportAction()));
this.importUsergrid.addButton(new Ext.Button(this.getCancelAction()))
}return this.importUsergrid
},getImportAction:function(){if(!this.importAction){this.importAction=new Ext.Action({text:Openwis.i18n("Security.User.Import.Validate.Button"),scope:this,handler:function(){var selection=this.getImportUserGrid().getSelectionModel().getSelections();
var params={users:[]};
Ext.each(selection,function(item,index,allItems){params.users.push({username:item.get("userName")})
},this);
var importHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.user.import",params:params,listeners:{success:function(config){this.fireEvent("userImported");
this.close()
},scope:this}});
importHandler.proceed()
}})
}return this.importAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getFilterFormPanel:function(){if(!this.filterFormPanel){this.filterFormPanel=new Ext.form.FormPanel({border:false,buttonAlign:"center",labelWidth:200});
this.filterFormPanel.add(this.getUsernameSearchTextField());
this.filterFormPanel.addButton(new Ext.Button(this.getSearchAction()))
}return this.filterFormPanel
},getUsernameSearchTextField:function(){if(!this.usernameSearchTextField){this.usernameSearchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.Filter.User"),name:"filter",width:300})
}return this.usernameSearchTextField
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){this.getImportUserStore().baseParams={};
var username=this.getUsernameSearchTextField().getValue();
if(username){this.getImportUserStore().setBaseParam("userFilter",username)
}this.getImportUserStore().load()
}})
}return this.searchAction
}});Ext.ns("Openwis.Admin.User");
Openwis.Admin.User.Manage=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Security.User.Manage.Title"),layout:"fit",width:600,height:500,modal:true,closeAction:"close"});
Openwis.Admin.User.Manage.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var params={};
params.user={};
params.user.username=this.editUserName;
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.user.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.addEvents("userSaved");
var tabs=new Ext.TabPanel({width:450,activeTab:0,frame:true,defaults:{autoHeight:true},items:[this.getPersonalInformationFormPanel(),this.getPrivilegesFormPanel(),this.getFavoritesPanel()]});
this.add(tabs);
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.getPrivilegesFormPanel().init(this.config.profiles,this.config.groups,this.config.backups,this.config.classOfServices);
if(this.isEdition()){this.getPersonalInformationFormPanel().setUserInformation(this.config.user);
this.getPrivilegesFormPanel().setUserInformation(this.config.user);
this.getFavoritesPanel().setFavorites(this.config.user)
}else{this.getPrivilegesFormPanel().setUserInformation(this.config.user)
}this.show()
},getPersonalInformationFormPanel:function(){if(!this.personalInformationFormPanel){this.personalInformationFormPanel=new Openwis.Common.User.PersonalInformation({isEdition:this.isEdition()})
}return this.personalInformationFormPanel
},getPrivilegesFormPanel:function(){if(!this.privilegesFormPanel){this.privilegesFormPanel=new Openwis.Admin.User.Privileges()
}return this.privilegesFormPanel
},getFavoritesPanel:function(){if(!this.favoritesPanel){this.favoritesPanel=new Openwis.Common.Dissemination.Favorites()
}return this.favoritesPanel
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){var persoInfoValid=this.getPersonalInformationFormPanel().getForm().isValid();
var privilegesValid=this.getPrivilegesFormPanel().getForm().isValid();
if(persoInfoValid&&privilegesValid){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.user.save",params:this.getUser(),listeners:{success:function(config){this.fireEvent("userSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}else{Ext.Msg.show({title:Openwis.i18n("Security.User.Manage.ErrorDlg.Title"),msg:Openwis.i18n("Security.User.Manage.ErrorDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getUser:function(){var user={};
user.creationMode=this.isCreation();
user.user=this.getPersonalInformationFormPanel().getUser(user.user);
user.user=this.getPrivilegesFormPanel().getUser(user.user);
user.user=this.getFavoritesPanel().getUser(user.user);
return user
},isCreation:function(){return(this.operationMode=="Create")
},isEdition:function(){return(this.operationMode=="Edit")
}});Ext.ns("Openwis.Admin.User");
Openwis.Admin.User.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.User.All.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getFilterFormPanel());
this.add(this.getLimitWarningLabel());
this.add(this.getUserGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.User.Title"),cls:"administrationTitle1"})
}return this.header
},getLimitWarningLabel:function(){if(!this.limitWarningLabel){this.limitWarningLabel=new Ext.Container({html:Openwis.i18n("Security.User.Grid.Label"),cls:"administrationTitle2"});
this.limitWarningLabel.setVisible(false)
}return this.limitWarningLabel
},getUserGrid:function(){var that=this;
if(!this.userGrid){this.userGrid=new Ext.grid.GridPanel({id:"userGrid",height:400,border:true,store:this.getUserStore(),loadMask:true,columns:[{id:"inetUserStatus",header:Openwis.i18n("Security.User.Active.Column"),dataIndex:"inetUserStatus",renderer:Openwis.Common.Request.Utils.accountStatusRendererImg,width:50,sortable:true},{id:"username",header:Openwis.i18n("Security.User.UserName.Column"),dataIndex:"username",sortable:true,width:180},{id:"name",header:Openwis.i18n("Security.User.LastName.Column"),dataIndex:"surname",sortable:true,width:180},{id:"surname",header:Openwis.i18n("Security.User.FirstName.Column"),dataIndex:"name",sortable:true,width:180},{id:"profile",header:Openwis.i18n("Security.User.Profile.Column"),dataIndex:"profile",sortable:true,width:180},{id:"lastLogin",header:Openwis.i18n("Security.User.LastLogin.Column"),dataIndex:"lastLogin",sortable:true,width:180},{id:"pwdChangedTime",header:Openwis.i18n("Security.User.PasswordChangedTime.Column"),dataIndex:"pwdChangedTime",sortable:true,width:180},{id:"pwdExpireTime",header:Openwis.i18n("Security.User.PasswordExpiredTime.Column"),dataIndex:"pwdExpireTime",sortable:true,width:180},{id:"pwdExpired",header:Openwis.i18n("Security.User.PasswordExpired.Column"),dataIndex:"pwdExpired",sortable:true,width:180},],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0);
if(sm.getCount()==1){sm.grid.ownerCt.getLockAccountAction().setDisabled(!that.canLockAccount(record.get("profile")));
sm.grid.ownerCt.getLockAccountAction().setText(that.getLockAccountActionText(record.get("inetUserStatus")))
}else{sm.grid.ownerCt.getLockAccountAction().setDisabled(true)
}},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount()==0);
if(sm.getCount()==1){sm.grid.ownerCt.getLockAccountAction().setDisabled(!that.canLockAccount(record.get("profile")));
sm.grid.ownerCt.getLockAccountAction().setText(that.getLockAccountActionText(record.get("inetUserStatus")))
}else{sm.grid.ownerCt.getLockAccountAction().setDisabled(true)
}}}})});
this.userGrid.addButton(new Ext.Button(this.getNewAction()));
this.userGrid.addButton(new Ext.Button(this.getEditAction()));
this.userGrid.addButton(new Ext.Button(this.getRemoveAction()));
this.userGrid.addButton(new Ext.Button(this.getLockAccountAction()));
this.userGrid.addButton(new Ext.Button(this.getImportUserAction()))
}return this.userGrid
},getUserStore:function(){if(!this.userStore){this.userStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.user.all",idProperty:"username",fields:[{name:"username",sortType:"asUCString"},{name:"name",sortType:"asUCString"},{name:"surname",sortType:"asUCString"},{name:"profile",sortType:"asUCString"},{name:"lastLogin",sortType:"asUCString"},{name:"inetUserStatus",sortType:"asUCString"},{name:"pwdChangedTime",sortType:"asUCString"},{name:"pwdExpireTime",sortType:"asUCString"},{name:"pwdExpired",sortType:"asUCString"},],listeners:{load:function(records){if(records&&records.totalLength>999){this.getLimitWarningLabel().setVisible(true)
}else{this.getLimitWarningLabel().setVisible(false)
}},scope:this}})
}return this.userStore
},getFilterFormPanel:function(){if(!this.filterFormPanel){this.filterFormPanel=new Ext.form.FormPanel({border:false,buttonAlign:"center",labelWidth:200});
this.filterFormPanel.add(this.getGroupsListbox());
this.filterFormPanel.add(this.getUsernameSearchTextField());
this.filterFormPanel.addButton(new Ext.Button(this.getSearchAction()))
}return this.filterFormPanel
},getUsernameSearchTextField:function(){if(!this.usernameSearchTextField){this.usernameSearchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.Filter.User"),name:"filter",width:300})
}return this.usernameSearchTextField
},getGroupsListbox:function(){if(!this.groupsListBox){this.groupsListBox=new Ext.ux.form.MultiSelect({store:new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.group.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name",sortType:Ext.data.SortTypes.asUCString},{name:"global"}]}),fieldLabel:Openwis.i18n("MonitorCurrentRequests.FilterByGroups"),displayField:"name",valueField:"id",width:300})
}return this.groupsListBox
},getNewAction:function(){if(!this.newAction){this.newAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),scope:this,handler:function(){new Openwis.Admin.User.Manage({operationMode:"Create",listeners:{userSaved:function(){this.getUserGrid().getStore().reload()
},scope:this}})
}})
}return this.newAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getUserGrid().getSelectionModel().getSelected();
new Openwis.Admin.User.Manage({operationMode:"Edit",editUserName:selectedRec.get("username"),listeners:{userSaved:function(){this.getUserGrid().getStore().reload()
},scope:this}})
}})
}return this.editAction
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getUserGrid().getSelectionModel().getSelections();
var params={users:[]};
Ext.each(selection,function(item,index,allItems){params.users.push({username:item.get("username")})
},this);
var msg=null;
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.user.remove",params:params,confirmMsg:msg,listeners:{success:function(){this.getUserGrid().getStore().reload()
},scope:this}});
removeHandler.proceed()
}})
}return this.removeAction
},getLockAccountAction:function(){if(!this.lockAction){this.lockAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Lock"),scope:this,handler:function(){var selectedRec=this.getUserGrid().getSelectionModel().getSelected();
var params={};
params.user={};
params.user.username=selectedRec.get("username");
var msg=null;
var lockHandler=new Openwis.Handler.Lock({url:configOptions.locService+"/xml.user.lock",params:params,confirmMsg:msg,listeners:{success:function(){this.getUserGrid().getStore().reload()
},scope:this}});
lockHandler.proceed()
}})
}return this.lockAction
},getImportUserAction:function(){if(!this.importUserAction){this.importUserAction=new Ext.Action({text:Openwis.i18n("Security.User.Import.Button"),scope:this,handler:function(){new Openwis.Admin.User.ImportUser({listeners:{userImported:function(){this.getUserGrid().getStore().reload()
},scope:this}})
}})
}return this.importUserAction
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){var selectedGroups=this.getGroupsListbox().getValue();
this.getUserStore().baseParams={};
if(selectedGroups){this.getUserStore().setBaseParam("groups",selectedGroups)
}var username=this.getUsernameSearchTextField().getValue();
if(username){this.getUserStore().setBaseParam("userFilter",username)
}this.getUserStore().load()
}})
}return this.searchAction
},getLockAccountActionText:function(inetUserStatus){if(inetUserStatus==="ACTIVE"){return Openwis.i18n("Common.Btn.Lock")
}else{return Openwis.i18n("Common.Btn.Unlock")
}},canLockAccount:function(userProfile){return userProfile!=="Administrator"
}});Ext.ns("Openwis.Admin.User");
Openwis.Admin.User.Report=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.User.Report.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getLimitWarningLabel());
this.add(this.getReportGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.Report.Title"),cls:"administrationTitle1"})
}return this.header
},getLimitWarningLabel:function(){if(!this.limitWarningLabel){this.limitWarningLabel=new Ext.Container({html:Openwis.i18n("Security.User.Grid.Label"),cls:"administrationTitle2"});
this.limitWarningLabel.setVisible(false)
}return this.limitWarningLabel
},getReportGrid:function(){var that=this;
if(!this.reportGrid){this.reportGrid=new Ext.grid.GridPanel({id:"reportGrid",height:400,border:true,store:this.getReportStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Security.Report.Date.Column"),dataIndex:"date",sortable:true,width:100,renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong},{id:"username",header:Openwis.i18n("Security.Report.UserName.Column"),dataIndex:"username",sortable:true,width:180},{id:"action",header:Openwis.i18n("Security.Report.Action.Column"),dataIndex:"action",sortable:true,width:100},{id:"attribute",header:Openwis.i18n("Security.Report.Attribute.Column"),dataIndex:"attribute",sortable:true,width:100},{id:"actioner",header:Openwis.i18n("Security.Report.Actioner.Column"),dataIndex:"actioner",sortable:true,width:180},],autoExpandColumn:"date",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},})
}return this.reportGrid
},getReportStore:function(){if(!this.reportStore){this.reportStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.user.report",idProperty:"id",fields:[{id:"id",sortType:"asUCString"},{name:"date",},{name:"username",sortType:"asUCString"},{name:"action",sortType:"asUCString"},{name:"attribute",sortType:"asUCString"},{name:"actioner",sortType:"asUCString"},],sortInfo:{field:"date",direction:"DESC"},listeners:{load:function(records){if(records&&records.totalLength>999){this.getLimitWarningLabel().setVisible(true)
}else{this.getLimitWarningLabel().setVisible(false)
}},scope:this}})
}return this.reportStore
},});Ext.ns("Openwis.Admin.SSOManagement");
Openwis.Admin.SSOManagement.SSOManagement=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.SSOManagement.SSOManagement.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.sso.management",listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSSOLink());
this.doLayout()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.SSOManagement.title"),cls:"administrationTitle1"})
}return this.header
},getSSOLink:function(){return new Ext.Container({border:false,width:500,html:"<a href = "+this.config+' target="_blank">'+Openwis.i18n("Security.SSOManagement.msg")+"</a>",style:{padding:"5px"}})
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
}}};Ext.ns("Openwis.Admin.DataService");
Openwis.Admin.DataService.MonitorCurrentRequests=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.DataService.MonitorCurrentRequests.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getFilterByUserGroupFormPanel());
this.add(new Ext.Container({html:Openwis.i18n("MonitorCurrentRequests.Title.ProcessedRequest"),cls:"administrationTitle2"}));
this.add(this.getProcessedRequestFilterCombo());
this.add(this.getMonitorCurrentProcessedRequestsGrid());
this.add(new Ext.Container({html:Openwis.i18n("MonitorCurrentRequests.Title.Subscriptions"),cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getMonitorCurrentSubscriptionsGrid());
this.add(new Ext.Panel({items:[this.getImportSubscriptionFormPanel()],style:{marginTop:"15px"}}))
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("MonitorCurrentRequests.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getFilterByUserGroupFormPanel:function(){if(!this.filterByUserGroupFormPanel){this.filterByUserGroupFormPanel=new Ext.form.FormPanel({border:false,buttonAlign:"center"});
this.filterByUserGroupFormPanel.add(this.getGroupsListbox());
this.filterByUserGroupFormPanel.addButton(new Ext.Button(this.getSearchAction()))
}return this.filterByUserGroupFormPanel
},getGroupsListbox:function(){if(!this.groupsListBox){this.groupsListBox=new Ext.ux.form.MultiSelect({store:new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.group.all",idProperty:"id",autoLoad:true,fields:[{name:"id"},{name:"name",sortType:Ext.data.SortTypes.asUCString},{name:"global"}]}),fieldLabel:Openwis.i18n("MonitorCurrentRequests.FilterByGroups"),displayField:"name",valueField:"id",width:170,listeners:{afterrender:function(grid){this.performSearch()
},scope:this}})
}return this.groupsListBox
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){this.performSearch()
}})
}return this.searchAction
},performSearch:function(){var selectedGroups=this.getGroupsListbox().getValue();
this.getMonitorCurrentProcessedRequestsStore().setBaseParam("groups",selectedGroups);
this.getMonitorCurrentProcessedRequestsStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
this.getMonitorCurrentSubscriptionsStore().setBaseParam("groups",selectedGroups);
this.getMonitorCurrentSubscriptionsStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},getProcessedRequestFilterCombo:function(){if(!this.processedRequestFilterCombo){var columns=[];
columns.push(["ADHOC",Openwis.i18n("MonitorCurrentRequests.PRFilter.ADHOC")]);
columns.push(["SUBSCRIPTION",Openwis.i18n("MonitorCurrentRequests.PRFilter.SUBSCRIPTION")]);
columns.push(["BOTH",Openwis.i18n("MonitorCurrentRequests.PRFilter.BOTH")]);
this.processedRequestFilterCombo=new Ext.form.ComboBox({fieldLabel:"Request Filter",name:"processedRequestFilterCombo",editable:false,mode:"local",store:new Ext.data.ArrayStore({id:"_prFilterStore",fields:["filterKey","filterName"],data:columns}),valueField:"filterKey",displayField:"filterName",triggerAction:"all",listeners:{select:function(){var selectedGroups=this.getGroupsListbox().getValue();
this.getMonitorCurrentProcessedRequestsStore().setBaseParam("prFilter",this.processedRequestFilterCombo.getValue());
this.getMonitorCurrentProcessedRequestsStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this}});
this.processedRequestFilterCombo.setValue("BOTH")
}return this.processedRequestFilterCombo
},getMonitorCurrentProcessedRequestsGrid:function(){if(!this.monitorCurrentProcessedRequestsGrid){this.monitorCurrentProcessedRequestsGrid=new Ext.grid.GridPanel({id:"currentProcessedRequestsGrid",height:250,border:true,store:this.getMonitorCurrentProcessedRequestsStore(),view:this.getCurrentProcessedRequestsGridView(),loadMask:true,columns:[{id:"statusImg",header:"",dataIndex:"status",renderer:Openwis.Common.Request.Utils.statusRendererImg,width:30,sortable:false},{id:"requestType",header:"",dataIndex:"requestType",renderer:Openwis.Common.Request.Utils.requestTypeRenderer,width:20,sortable:false},{id:"user",header:Openwis.i18n("MonitorCurrentRequests.User"),dataIndex:"user",sortable:true,hideable:false},{id:"title",header:Openwis.i18n("MonitorCurrentRequests.ProductMetadata.Title"),dataIndex:"title",sortable:true,hideable:false,width:100},{id:"id",header:Openwis.i18n("MonitorCurrentRequests.Request.ID"),dataIndex:"id",sortable:true,hideable:false,width:80},{id:"processedRequestID",header:Openwis.i18n("MonitorCurrentRequests.ProcessRequest.ID"),dataIndex:"processedRequestID",sortable:true,hideable:false,width:80},{id:"status",header:Openwis.i18n("MonitorCurrentRequests.Status"),dataIndex:"status",renderer:Openwis.Common.Request.Utils.statusRenderer,width:50,sortable:true},{id:"size",header:Openwis.i18n("MonitorCurrentRequests.Volume"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:80,sortable:true},{id:"creationDate",header:Openwis.i18n("MonitorCurrentRequests.CreationDate"),dataIndex:"creationDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:true}],autoExpandColumn:"title",sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getViewProcessedRequestAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getViewProcessedRequestAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getDiscardProcessedRequestAction().setDisabled(sm.getCount()==0)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getMonitorCurrentProcessedRequestsStore(),displayInfo:true,displayMsg:Openwis.i18n("MonitorCurrentRequests.Display.Range"),emptyMsg:Openwis.i18n("MonitorCurrentRequests.No.Request")})});
this.monitorCurrentProcessedRequestsGrid.addButton(new Ext.Button(this.getViewProcessedRequestAction()));
this.monitorCurrentProcessedRequestsGrid.addButton(new Ext.Button(this.getDiscardProcessedRequestAction()))
}return this.monitorCurrentProcessedRequestsGrid
},getCurrentProcessedRequestsGridView:function(){if(!this.gridView){this.gridView=new Ext.grid.GridView({emptyText:"",getRowClass:function(record,index,rowParams,store){}})
}return this.gridView
},getMonitorCurrentProcessedRequestsStore:function(){if(!this.monitorCurrentProcessedRequestsStore){this.monitorCurrentProcessedRequestsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.monitor.current.requests",remoteSort:true,root:"rows",idProperty:"id",fields:[{name:"id",mapping:"requestID"},{name:"requestType",mapping:"requestType"},{name:"processedRequestID",mapping:"processedRequestDTO.id"},{name:"user",mapping:"userName",sortType:Ext.data.SortTypes.asUCString},{name:"title",mapping:"productMetadataURN",sortType:Ext.data.SortTypes.asUCString},{name:"creationDate",mapping:"processedRequestDTO.creationDate"},{name:"status",mapping:"processedRequestDTO.status"},{name:"size",mapping:"processedRequestDTO.size"},{name:"dataSource",mapping:"productMetadataDataSource"},{name:"extractMode"},{name:"urn",mapping:"productMetadataURN"}],sortInfo:{field:"id",direction:"DESC"}})
}return this.monitorCurrentProcessedRequestsStore
},getViewProcessedRequestAction:function(){if(!this.viewProcessedRequestAction){this.viewProcessedRequestAction=new Ext.Action({text:Openwis.i18n("Common.Btn.View"),disabled:true,iconCls:"icon-view-processedrequest",scope:this,handler:function(){var rec=this.getMonitorCurrentProcessedRequestsGrid().getSelectionModel().getSelected();
var requestType=rec.get("requestType");
if(requestType=="ADHOC"){var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"ADHOC",rec.get("extractMode")=="CACHE","View",rec.get("id"),false)
}else{if(requestType=="SUBSCRIPTION"){var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"SUBSCRIPTION",rec.get("extractMode")=="CACHE","Edit",rec.get("id"),false)
}}}})
}return this.viewProcessedRequestAction
},getDiscardProcessedRequestAction:function(){if(!this.discardProcessedRequestAction){this.discardProcessedRequestAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Discard"),disabled:true,iconCls:"icon-discard-processedrequest",scope:this,handler:function(){var selection=this.getMonitorCurrentProcessedRequestsGrid().getSelectionModel().getSelections();
var params={discardRequests:[]};
Ext.each(selection,function(item,index,allItems){params.discardRequests.push({requestID:item.get("processedRequestID"),typeRequest:"PROCESSED_REQUEST"})
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.discard.request",params:params,listeners:{success:function(){this.getMonitorCurrentProcessedRequestsStore().reload()
},scope:this}}).proceed()
}})
}return this.discardProcessedRequestAction
},getMonitorCurrentSubscriptionsGrid:function(){if(!this.monitorCurrentSubscriptionsGrid){this.monitorCurrentSubscriptionsGrid=new Ext.grid.GridPanel({id:"currentSubscriptionsGrid",height:250,border:true,store:this.getMonitorCurrentSubscriptionsStore(),loadMask:true,columns:[{id:"statusImg",header:"",dataIndex:"state",renderer:Openwis.Common.Request.Utils.stateRendererImg,width:50,sortable:false},{id:"user",header:Openwis.i18n("MonitorCurrentRequests.User"),dataIndex:"user",sortable:true,hideable:false},{id:"title",header:Openwis.i18n("TrackMySubscriptions.ProductMetadata.Title"),dataIndex:"urn",sortable:true},{id:"id",header:Openwis.i18n("TrackMySubscriptions.Subscription.ID"),dataIndex:"id",width:100,sortable:true},{id:"creationDate",header:Openwis.i18n("TrackMySubscriptions.CreationDate"),dataIndex:"creationDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:true},{id:"lastProcessingDate",header:Openwis.i18n("TrackMySubscriptions.LastEventDate"),dataIndex:"lastProcessingDate",renderer:Openwis.Utils.Date.formatDateTimeUTC,width:100,sortable:false}],autoExpandColumn:"title",sm:new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getViewSubscriptionAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getSuspendSubscriptionAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="ACTIVE"));
sm.grid.ownerCt.getResumeSubscriptionAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="SUSPENDED"));
sm.grid.ownerCt.getDiscardSubscriptionAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getExportSubscriptionAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getViewSubscriptionAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getSuspendSubscriptionAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="ACTIVE"));
sm.grid.ownerCt.getResumeSubscriptionAction().setDisabled(sm.getCount()!=1||!record.get("valid")||!(record.get("state")=="SUSPENDED"));
sm.grid.ownerCt.getDiscardSubscriptionAction().setDisabled(sm.getCount()==0);
sm.grid.ownerCt.getExportSubscriptionAction().setDisabled(sm.getCount()==0)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getMonitorCurrentSubscriptionsStore(),displayInfo:true,displayMsg:Openwis.i18n("TrackMySubscriptions.Display.Range"),emptyMsg:Openwis.i18n("TrackMySubscriptions.No.Subscription")})});
this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getViewSubscriptionAction()));
this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getSuspendSubscriptionAction()));
this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getResumeSubscriptionAction()));
this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getDiscardSubscriptionAction()));
this.monitorCurrentSubscriptionsGrid.addButton(new Ext.Button(this.getExportSubscriptionAction()))
}return this.monitorCurrentSubscriptionsGrid
},getMonitorCurrentSubscriptionsStore:function(){if(!this.monitorCurrentSubscriptionsStore){this.monitorCurrentSubscriptionsStore=new Openwis.Data.JeevesJsonStore({url:configOptions+"/xml.monitor.current.subscriptions",idProperty:"id",remoteSort:true,root:"rows",fields:[{name:"user",mapping:"userName",sortType:Ext.data.SortTypes.asUCString},{name:"urn",mapping:"productMetadataURN"},{name:"title",mapping:"productMetadataTitle",sortType:Ext.data.SortTypes.asUCString},{name:"creationDate",mapping:"startingDate"},{name:"id",mapping:"requestID"},{name:"lastProcessingDate",mapping:"lastProcessingDate"},{name:"valid"},{name:"state"},{name:"extractMode"}],sortInfo:{field:"title",direction:"ASC"}})
}return this.monitorCurrentSubscriptionsStore
},getViewSubscriptionAction:function(){if(!this.viewSubscriptionAction){this.viewSubscriptionAction=new Ext.Action({text:Openwis.i18n("Common.Btn.ViewEdit"),disabled:true,iconCls:"icon-view-processedrequest",scope:this,handler:function(){var rec=this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelected();
var wizard=new Openwis.RequestSubscription.Wizard();
wizard.initialize(rec.get("urn"),"SUBSCRIPTION",rec.get("extractMode")=="CACHE","Edit",rec.get("id"),false)
}})
}return this.viewSubscriptionAction
},getSuspendSubscriptionAction:function(){if(!this.suspendSubscriptionAction){this.suspendSubscriptionAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Suspend"),disabled:true,iconCls:"icon-suspend-subscription",scope:this,handler:function(){var rec=this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.set.subscription.state",params:{requestID:rec.get("id"),typeStateSet:"SUSPEND"},listeners:{success:function(){this.getMonitorCurrentSubscriptionsStore().reload()
},scope:this}}).proceed()
}})
}return this.suspendSubscriptionAction
},getResumeSubscriptionAction:function(){if(!this.resumeSubscriptionAction){this.resumeSubscriptionAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Resume"),disabled:true,iconCls:"icon-resume-subscription",scope:this,handler:function(){var rec=this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelected();
new Openwis.Handler.Save({url:configOptions.locService+"/xml.set.subscription.state",params:{requestID:rec.get("id"),typeStateSet:"RESUME"},listeners:{success:function(){this.getMonitorCurrentSubscriptionsStore().reload()
},scope:this}}).proceed()
}})
}return this.resumeSubscriptionAction
},getDiscardSubscriptionAction:function(){if(!this.discardSubscriptionAction){this.discardSubscriptionAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Discard"),disabled:true,iconCls:"icon-discard-subscription",scope:this,handler:function(){var selection=this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelections();
var params={discardRequests:[]};
Ext.each(selection,function(item,index,allItems){params.discardRequests.push({requestID:item.get("id"),typeRequest:"SUBSCRIPTION"})
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.discard.request",params:params,listeners:{success:function(){this.getMonitorCurrentSubscriptionsStore().reload()
},scope:this}}).proceed()
}})
}return this.discardSubscriptionAction
},getExportSubscriptionAction:function(){if(!this.exportSubscriptionAction){this.exportSubscriptionAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),disabled:true,scope:this,handler:function(){var selection=this.getMonitorCurrentSubscriptionsGrid().getSelectionModel().getSelections();
var params={exportRequests:[]};
Ext.each(selection,function(item,index,allItems){params.exportRequests.push({requestID:item.get("id"),typeRequest:"SUBSCRIPTION"})
},this);
if(params.exportRequests.length>0){window.open(configOptions.locService+"/xml.monitor.current.subscriptions.export?subscriptionId="+params.exportRequests[0].requestID,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportSubscriptionAction
},getImportSubscriptionFormPanel:function(){if(!this.importSubscriptionFormPanel){this.importSubscriptionFormPanel=new Ext.form.FormPanel({border:false,fileUpload:true,bodyStyle:" margin: 10px 10px 0px 10px; ",errorReader:new Ext.data.XmlReader({record:"field",success:"@success"},["id","msg"])});
var newFile=new Ext.ux.form.FileUploadField({xtype:"fileuploadfield",allowBlank:false,buttonCfg:{text:Openwis.i18n("Common.Btn.Browse")},fieldLabel:Openwis.i18n("MonitorCurrentRequests.Import.Label"),width:360});
this.importSubscriptionFormPanel.add(newFile);
this.importSubscriptionFormPanel.addButton(new Ext.Button(this.getImportSubscriptionAction()))
}return this.importSubscriptionFormPanel
},getImportSubscriptionAction:function(){if(!this.importSubscriptionAction){this.importSubscriptionAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Insert"),scope:this,handler:function(){if(this.importSubscriptionFormPanel.getForm().isValid()){this.importSubscriptionFormPanel.getForm().submit({url:configOptions.locService+"/xml.monitor.current.subscriptions.import",scope:this,params:{},success:function(fp,action){var jsonData=fp.errorReader.xmlData.getElementsByTagName("message")[0].childNodes[0].nodeValue;
var result=Ext.decode(jsonData);
if(result.result){Openwis.Utils.MessageBox.displaySaveSuccessful()
}else{Openwis.Utils.MessageBox.displayErrorMsg(result.message)
}this.getMonitorCurrentSubscriptionsStore().reload()
},failure:function(response){Openwis.Utils.MessageBox.displayInternalError()
}})
}}})
}return this.importSubscriptionAction
}});Ext.ns("Openwis.Admin.DataService");
Openwis.Admin.DataService.BrowseContent=Ext.extend(Ext.Container,{cacheService:"/xml.management.cache.browse",initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.DataService.BrowseContent.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSearchFormPanel());
this.add(this.getFileGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("BrowseContent.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getFileGrid:function(){if(!this.fileGrid){var columns=[];
columns.push(new Ext.grid.Column({id:"date",header:Openwis.i18n("BrowseContent.Administration.Grid.date"),dataIndex:"insertionDate",renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong,width:120,sortable:true}));
columns.push(new Ext.grid.Column({id:"filename",header:Openwis.i18n("BrowseContent.Administration.Grid.filename"),dataIndex:"filename",sortable:true,width:200,hideable:false}));
columns.push(new Ext.grid.Column({id:"checksum",header:Openwis.i18n("BrowseContent.Administration.Grid.checksum"),dataIndex:"checksum",sortable:true,width:115}));
columns.push(new Ext.grid.Column({id:"origin",header:Openwis.i18n("BrowseContent.Administration.Grid.origin"),dataIndex:"origin",sortable:true,width:65}));
columns.push(new Ext.grid.Column({id:"metadata",header:Openwis.i18n("BrowseContent.Administration.Grid.metadata"),dataIndex:"metadataUrn",sortable:true,width:225}));
this.fileGrid=new Ext.grid.GridPanel({id:"fileGrid",height:475,border:true,store:this.getFileStore(),loadMask:true,viewConfig:{forceFit:true},columns:columns,listeners:{afterrender:function(grid){grid.loadMask.show();
this.reset();
this.reload()
},scope:this},bbar:this.getPagingToolbar()})
}return this.fileGrid
},getPagingToolbar:function(){if(!this.pagingToolbar){this.pagingToolbar=new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getFileStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})
}return this.pagingToolbar
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:75,border:false,layout:"column",defaults:{columnWidth:0.5,layout:"form",border:false,bodyStyle:"padding:0 18px 0 0"},items:[{defaults:{anchor:"100%"},items:[this.getSearchFileNameField()]},{defaults:{anchor:"100%"},items:[this.getSearchMetadataIdField()]}]});
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchFileNameField:function(){if(!this.searchFileNameTextField){this.searchFileNameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("BrowseContent.Administration.Search.file"),name:"searchFile",width:150})
}return this.searchFileNameTextField
},getSearchMetadataIdField:function(){if(!this.searchMetadataIdTextField){this.searchMetadataIdTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("BrowseContent.Administration.Search.metadata"),name:"searchMetadata",width:150})
}return this.searchMetadataIdTextField
},getFileStore:function(){if(!this.fileStore){this.fileStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.management.cache.browse",remoteSort:true,root:"rows",totalProperty:"total",idProperty:"filename",fields:[{name:"filename",sortType:Ext.data.SortTypes.asUCString,mapping:"name"},{name:"checksum",sortType:Ext.data.SortTypes.asUCString},{name:"origin"},{name:"metadataUrn"},{name:"insertionDate"}],sortInfo:{field:"insertionDate",direction:"DESC"}})
}return this.fileStore
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:false,text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:this.reload})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:false,text:Openwis.i18n("Common.Btn.Reset"),scope:this,handler:function(){this.reset();
this.reload()
}})
}return this.resetAction
},renderFileOrigin:function(value){return value=="1"?Openwis.i18n("BrowseContent.Administration.renderFileOrigin.replication"):""
},reload:function(){var filename=this.getSearchFileNameField().getValue();
var metadataID=this.getSearchMetadataIdField().getValue();
this.setBaseParams();
this.getFileStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE,filename:filename,metadataUrn:metadataID}})
},reset:function(){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.cacheService,params:{requestType:"RESET_FILTER"}});
handler.proceed();
var filter={};
this.updateFilterFields(filter);
this.setBaseParams()
},getFilterParams:function(reload){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.cacheService,params:{requestType:"GET_FILTER_PARAMS"},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"filter");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttributeValue(attributes,"success");
if(success=="true"){var filter={filename:Openwis.Utils.Xml.getAttributeValue(attributes,"filename"),metadataid:Openwis.Utils.Xml.getAttributeValue(attributes,"metadataUrn")};
this.updateFilterFields(filter);
if(reload){this.reload()
}}else{Openwis.Utils.MessageBox.displayInternalError()
}},failure:function(responseText){Openwis.Utils.MessageBox.displayInternalError()
},scope:this}});
handler.proceed()
},updateFilterFields:function(filter){this.getSearchFileNameField().setValue(filter.filename);
this.getSearchMetadataIdField().setValue(filter.metadataUrn)
},setBaseParams:function(){this.getFileStore().setBaseParam("filename",this.getSearchFileNameField().getValue());
this.getFileStore().setBaseParam("metadataUrn",this.getSearchMetadataIdField().getValue())
}});Ext.ns("Openwis.Admin.DataService");
Openwis.Admin.DataService.CacheConfiguration=Ext.extend(Ext.Container,{ingestionService:"/xml.management.cache.configure.ingest",feedingService:"/xml.management.cache.configure.feed",replicationService:"/xml.management.cache.configure.replic",disseminationService:"/xml.management.cache.configure.diss",ingestionServiceCheckBoxID:"ingestionServiceCheckBox",feedingServiceCheckBoxID:"feedingServiceCheckBox",replicationServiceCheckBoxID:"replicationServiceCheckBox",disseminationServiceCheckBoxID:"disseminationServiceCheckBox",initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.DataService.CacheConfiguration.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getConfigurationMssFssPanel());
this.add(this.getConfigurationReplicationPanel());
this.add(this.getConfigurationDisseminationPanel())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("CacheConfiguration.Administration.Title"),cls:"administrationTitle1"})
}7;
return this.header
},getConfigurationMssFssPanel:function(){if(!this.configurationMssFssPanel){this.configurationMssFssPanel=new Ext.Panel({id:"configurationMssFssPanel",title:Openwis.i18n("CacheConfiguration.MSSFSS.Title"),border:true});
this.configurationMssFssPanel.add(this.getIngestionFilterPanel());
this.configurationMssFssPanel.add(this.getFeedingFilterPanel())
}return this.configurationMssFssPanel
},getConfigurationReplicationPanel:function(){if(!this.configurationReplicationPanel){this.configurationReplicationPanel=new Ext.Panel({id:"configurationReplicationPanel",title:Openwis.i18n("CacheConfiguration.Replication.Title"),border:true,style:{marginTop:"20px"}});
this.configurationReplicationPanel.add(this.getReplicationFilterPanel())
}return this.configurationReplicationPanel
},getConfigurationDisseminationPanel:function(){if(!this.configurationDisseminationPanel){this.configurationDisseminationPanel=new Ext.Panel({id:"configurationDisseminationPanel",title:Openwis.i18n("CacheConfiguration.Dissemination.Title"),border:true,style:{marginTop:"20px"}});
this.configurationDisseminationPanel.add(this.getDisseminationPanel())
}return this.configurationDisseminationPanel
},getIngestionFilterPanel:function(){if(!this.ingestionFilterPanel){this.ingestionFilterPanel=new Ext.form.FormPanel({id:"ingestionFilterPanel",border:false,style:{marginLeft:"10px",marginRight:"10px"}});
this.ingestionFilterPanel.add(new Ext.form.Checkbox({id:this.ingestionServiceCheckBoxID,fieldLabel:Openwis.i18n("CacheConfiguration.MSSFSS.EnableIngestion"),handler:this.setServiceStatus,service:this.ingestionService,active:true}));
this.ingestionFilterPanel.add(new Ext.Container({html:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Title"),cls:"administrationTitle2"}));
this.ingestionFilterPanel.add(this.getIngestionFilterGrid());
this.ingestionFilterPanel.addButton(new Ext.Button(this.getIngestionFilterNewAction()));
this.ingestionFilterPanel.addButton(new Ext.Button(this.getIngestionFilterRemoveAction()))
}return this.ingestionFilterPanel
},getFeedingFilterPanel:function(){if(!this.feedingFilterPanel){this.feedingFilterPanel=new Ext.form.FormPanel({id:"feedingFilterPanel",border:false,style:{marginTop:"10px",marginLeft:"10px",marginRight:"10px",marginBottom:"10px"}});
this.feedingFilterPanel.add(new Ext.form.Checkbox({id:this.feedingServiceCheckBoxID,fieldLabel:Openwis.i18n("CacheConfiguration.MSSFSS.EnableFeeding"),handler:this.setServiceStatus,service:this.feedingService,active:true}));
this.feedingFilterPanel.add(new Ext.Container({html:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Title"),cls:"administrationTitle2"}));
this.feedingFilterPanel.add(this.getFeedingFilterGrid());
this.feedingFilterPanel.addButton(new Ext.Button(this.getFeedingFilterNewAction()));
this.feedingFilterPanel.addButton(new Ext.Button(this.getFeedingFilterRemoveAction()));
this.feedingFilterPanel.addButton(new Ext.Button(this.getFeedingFilterResetAction()))
}return this.feedingFilterPanel
},getReplicationFilterPanel:function(){if(!this.replicationFilterPanel){this.replicationFilterPanel=new Ext.form.FormPanel({id:"replicationFilterPanel",border:false,style:{marginLeft:"10px",marginRight:"10px",marginBottom:"10px"}});
this.replicationFilterPanel.add(new Ext.form.Checkbox({id:this.replicationServiceCheckBoxID,fieldLabel:Openwis.i18n("CacheConfiguration.Replication.EnableReplication"),handler:this.setServiceStatus,service:this.replicationService,active:true}));
this.replicationFilterPanel.add(this.getReplicationGrid());
this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterNewAction()));
this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterEditAction()));
this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterRemoveAction()));
this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterActivateAction()));
this.replicationFilterPanel.addButton(new Ext.Button(this.getReplicationFilterDeactivateAction()))
}return this.replicationFilterPanel
},getDisseminationPanel:function(){if(!this.disseminationPanel){this.disseminationPanel=new Ext.form.FormPanel({id:"disseminationPanel",border:false,style:{marginLeft:"10px",marginRight:"10px"}});
this.disseminationPanel.add(new Ext.form.Checkbox({id:this.disseminationServiceCheckBoxID,fieldLabel:Openwis.i18n("CacheConfiguration.Dissemination.EnableDissemination"),handler:this.setServiceStatus,service:this.disseminationService,active:true}));
this.disseminationPanel.addButton(new Ext.Button(this.getDisseminationUpdateAction()))
}return this.disseminationPanel
},getIngestionFilterGrid:function(){if(!this.ingestionFilterGrid){this.ingestionFilterGrid=new Ext.grid.GridPanel({id:"ingestionFilterGrid",height:150,border:true,store:this.getIngestionFilterRequestsStore(),loadMask:true,columns:[{id:"regex",header:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.RegExp"),dataIndex:"regex",width:400,sortable:true,hideable:false},{id:"description",header:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Description"),dataIndex:"description",width:300,sortable:true,hideable:false}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
var checkbox=this.getIngestionFilterPanel().get(this.ingestionServiceCheckBoxID);
this.updateServiceStatus(checkbox)
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getIngestionFilterRequestsStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Range"),emptyMsg:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.No.Data"),listeners:{beforechange:function(toolbar,params){var sm=this.getIngestionFilterGrid().getSelectionModel();
sm.clearSelections(true);
this.disableIngestionButtons(sm);
var checkbox=this.getIngestionFilterPanel().get(this.ingestionServiceCheckBoxID);
this.updateServiceStatus(checkbox)
},scope:this}}),selModel:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){this.disableIngestionButtons(sm)
},rowdeselect:function(sm,rowIndex,record){this.disableIngestionButtons(sm)
},scope:this},singleSelect:true})})
}return this.ingestionFilterGrid
},getFeedingFilterGrid:function(){if(!this.feedingFilterGrid){this.feedingFilterGrid=new Ext.grid.GridPanel({id:"feedingFilterGrid",height:150,border:true,store:this.getFeedingFilterRequestsStore(),loadMask:true,columns:[{id:"regex",header:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.RegExp"),dataIndex:"regex",width:400,sortable:true,hideable:false},{id:"description",header:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Description"),dataIndex:"description",width:300,sortable:true,hideable:false}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
var checkbox=this.getFeedingFilterPanel().get(this.feedingServiceCheckBoxID);
this.updateServiceStatus(checkbox)
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getFeedingFilterRequestsStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Range"),emptyMsg:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.No.Data"),listeners:{beforechange:function(toolbar,params){var sm=this.getFeedingFilterGrid().getSelectionModel();
sm.clearSelections(true);
this.disableFeedingButtons(sm);
var checkbox=this.getFeedingFilterPanel().get(this.feedingServiceCheckBoxID);
this.updateServiceStatus(checkbox)
},scope:this}}),selModel:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){this.disableFeedingButtons(sm)
},rowdeselect:function(sm,rowIndex,record){this.disableFeedingButtons(sm)
},scope:this},singleSelect:true})})
}return this.feedingFilterGrid
},getReplicationGrid:function(){if(!this.replicationGrid){var selectionModel=new Ext.grid.CheckboxSelectionModel({checkOnly:false,header:"",width:22,listeners:{rowselect:function(sm,rowIndex,record){this.disableReplicationButtons(sm)
},rowdeselect:function(sm,rowIndex,record){this.disableReplicationButtons(sm)
},scope:this}});
this.replicationGrid=new Ext.grid.GridPanel({id:"replicationGrid",height:200,border:true,store:this.getReplicationRequestsStore(),loadMask:true,columns:[selectionModel,{id:"gisc",header:Openwis.i18n("CacheConfiguration.Replication.Gisc"),dataIndex:"source",width:100,sortable:true,hideable:false},{id:"regex",header:Openwis.i18n("CacheConfiguration.Replication.RegExp"),dataIndex:"regex",width:140,sortable:true,hideable:false},{id:"description",header:Openwis.i18n("CacheConfiguration.Replication.Description"),dataIndex:"description",width:200,sortable:true},{id:"lastrun",header:Openwis.i18n("CacheConfiguration.Replication.LastRun"),dataIndex:"uptime",renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong,width:120,sortable:true,hideable:false},{id:"status",header:Openwis.i18n("CacheConfiguration.Replication.Status"),dataIndex:"active",renderer:this.activeRenderer,width:90,sortable:true,hideable:false}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
var checkbox=this.getReplicationFilterPanel().get(this.replicationServiceCheckBoxID);
this.updateServiceStatus(checkbox);
checkbox=this.getDisseminationPanel().get(this.disseminationServiceCheckBoxID);
this.updateServiceStatus(checkbox)
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getReplicationRequestsStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Range"),emptyMsg:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.No.Data"),listeners:{beforechange:function(toolbar,params){var sm=this.getReplicationGrid().getSelectionModel();
sm.clearSelections(true);
this.disableReplicationButtons(sm);
var checkbox=this.getReplicationFilterPanel().get(this.replicationServiceCheckBoxID);
this.updateServiceStatus(checkbox)
},scope:this}}),selModel:selectionModel})
}return this.replicationGrid
},getIngestionFilterRequestsStore:function(){if(!this.ingestionFilterRequestsStore){this.ingestionFilterRequestsStore=new Openwis.Data.JeevesJsonStore({service:this.ingestionService,url:configOptions.locService+this.ingestionService,root:"rows",totalProperty:"total",idProperty:"regex",fields:[{name:"regex"},{name:"description"}],sortInfo:{field:"regex",direction:"ASC"}})
}return this.ingestionFilterRequestsStore
},getFeedingFilterRequestsStore:function(){if(!this.feedingFilterRequestsStore){this.feedingFilterRequestsStore=new Openwis.Data.JeevesJsonStore({service:this.feedingService,url:configOptions.locService+this.feedingService,root:"rows",totalProperty:"total",idProperty:"regex",fields:[{name:"regex"},{name:"description"}],sortInfo:{field:"regex",direction:"ASC"}})
}return this.feedingFilterRequestsStore
},getReplicationRequestsStore:function(){if(!this.replicationRequestsStore){this.replicationRequestsStore=new Openwis.Data.JeevesJsonStore({service:this.replicationService,url:configOptions.locService+this.replicationService,root:"rows",totalProperty:"total",fields:[{name:"active"},{name:"source"},{name:"type"},{name:"uptime"},{name:"regex"},{name:"description"}],sortInfo:{field:"source",direction:"ASC"}})
}return this.replicationRequestsStore
},getIngestionFilterNewAction:function(){if(!this.ingestionFilterNewAction){this.ingestionFilterNewAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),disabled:false,scope:this,handler:function(){var store=this.getIngestionFilterRequestsStore();
this.addFilter(store,"Ingestion")
}})
}return this.ingestionFilterNewAction
},getIngestionFilterRemoveAction:function(){if(!this.ingestionFilterRemoveAction){this.ingestionFilterRemoveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Remove"),disabled:true,scope:this,handler:function(){var selectedFilter=this.getIngestionFilterGrid().getSelectionModel().getSelected();
var store=this.getIngestionFilterRequestsStore();
this.removeFilter(selectedFilter,store,false)
}})
}return this.ingestionFilterRemoveAction
},getFeedingFilterNewAction:function(){if(!this.feedingFilterNewAction){this.feedingFilterNewAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),disabled:false,scope:this,handler:function(){var store=this.getFeedingFilterRequestsStore();
this.addFilter(store,"Feeding")
}})
}return this.feedingFilterNewAction
},getFeedingFilterRemoveAction:function(){if(!this.feedingFilterRemoveAction){this.feedingFilterRemoveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Remove"),disabled:true,scope:this,handler:function(){var selectedFilter=this.getFeedingFilterGrid().getSelectionModel().getSelected();
var store=this.getFeedingFilterRequestsStore();
this.removeFilter(selectedFilter,store,false)
}})
}return this.feedingFilterRemoveAction
},getFeedingFilterResetAction:function(){if(!this.feedingFilterResetAction){this.feedingFilterResetAction=new Ext.Action({text:Openwis.i18n("CacheConfiguration.Btn.ResetToDefault"),disabled:false,scope:this,handler:function(){this.getFeedingFilterGrid().getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE,requestType:"RESET_TO_DEFAULT"}})
}})
}return this.feedingFilterResetAction
},getReplicationFilterNewAction:function(){if(!this.replicationFilterNewAction){this.replicationFilterNewAction=new Ext.Action({text:Openwis.i18n("Common.Btn.New"),disabled:false,scope:this,handler:function(){var newFilter={};
newFilter.source=Openwis.i18n("CacheConfiguration.Replication.New.Source");
newFilter.type=Openwis.i18n("CacheConfiguration.Replication.New.Type");
newFilter.regex=Openwis.i18n("CacheConfiguration.Replication.New.RegExp");
newFilter.description=Openwis.i18n("CacheConfiguration.Replication.New.Description");
newFilter.active=false;
var store=this.getReplicationRequestsStore();
this.editFilter(newFilter,store,"New")
}})
}return this.replicationFilterNewAction
},getReplicationFilterEditAction:function(){if(!this.replicationFilterEditAction){this.replicationFilterEditAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Edit"),disabled:true,scope:this,handler:function(){var selectedFilter=this.getReplicationGrid().getSelectionModel().getSelected();
var store=this.getReplicationRequestsStore();
if(selectedFilter){this.editFilter(selectedFilter.data,store,"Edit")
}}})
}return this.replicationFilterEditAction
},getReplicationFilterRemoveAction:function(){if(!this.replicationFilterRemoveAction){this.replicationFilterRemoveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Remove"),disabled:true,scope:this,handler:function(){var store=this.getReplicationRequestsStore();
var selectedFilters=this.getReplicationGrid().getSelectionModel().getSelections();
this.removeFilters(selectedFilters,store,true)
}})
}return this.replicationFilterRemoveAction
},getReplicationFilterActivateAction:function(){if(!this.replicationFilterActivateAction){this.replicationFilterActivateAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Activate"),disabled:true,scope:this,handler:function(){var store=this.getReplicationRequestsStore();
var reload=false;
var activate=true;
var selections=this.getReplicationGrid().getSelectionModel().getSelections();
for(var i=0;
i<selections.length;
i++){var filter=selections[i];
if(i==selections.length-1){reload=true
}this.activateFilter(filter,activate,store,reload)
}}})
}return this.replicationFilterActivateAction
},getReplicationFilterDeactivateAction:function(){if(!this.replicationFilterDeactivateAction){this.replicationFilterDeactivateAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Deactivate"),disabled:true,scope:this,handler:function(){var store=this.getReplicationRequestsStore();
var reload=false;
var activate=false;
var selections=this.getReplicationGrid().getSelectionModel().getSelections();
for(var i=0;
i<selections.length;
i++){var filter=selections[i];
if(i==selections.length-1){reload=true
}this.activateFilter(filter,activate,store,reload)
}}})
}return this.replicationFilterDeactivateAction
},getDisseminationUpdateAction:function(){if(!this.disseminationUpdateAction){this.disseminationUpdateAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),disabled:false,scope:this,handler:function(){var checkbox=this.getDisseminationPanel().get(this.disseminationServiceCheckBoxID);
this.updateServiceStatus(checkbox)
}})
}return this.disseminationUpdateAction
},disableIngestionButtons:function(sm){if(sm){var disabled=(sm.getCount()!=1);
this.getIngestionFilterRemoveAction().setDisabled(disabled)
}},disableFeedingButtons:function(sm){if(sm){var disabled=(sm.getCount()!=1);
this.getFeedingFilterRemoveAction().setDisabled(disabled)
}},disableReplicationButtons:function(sm){if(sm){var disabled=(sm.getCount()==0);
this.getReplicationFilterRemoveAction().setDisabled(disabled);
this.getReplicationFilterEditAction().setDisabled(disabled);
this.getReplicationFilterActivateAction().setDisabled(disabled);
this.getReplicationFilterDeactivateAction().setDisabled(disabled)
}},addFilter:function(store,type){var newFilter={};
newFilter.regex=Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Edit.RegExp.Value");
newFilter.description=Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Edit.Description.Value");
var filterDialog=new Openwis.Admin.DataService.FilterInputDialog({operationMode:"New",filterType:type,selectedFilter:newFilter,locationService:store.service,store:store,listeners:{filterSaved:function(msg,isError){if(isError){Ext.Msg.show({title:Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Edit.Title"),msg:msg,buttons:Ext.Msg.OK,scope:this,icon:Ext.MessageBox.ERROR})
}else{store.reload()
}},scope:this}});
filterDialog.show()
},editFilter:function(filter,store,mode){if(filter){var filterDialog=new Openwis.Admin.DataService.ReplicationFilterDialog({operationMode:mode,selectedFilter:filter,locationService:store.service,store:store,listeners:{filterSaved:function(){store.reload()
},scope:this}});
filterDialog.show()
}},removeFilter:function(filter,store,isReplicFilter){if(filter){Ext.MessageBox.confirm(Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Title"),Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Msg"),function(btnClicked){if(btnClicked=="yes"){this.proceedRemoveFilter(filter,store,isReplicFilter)
}},this)
}},removeFilters:function(filters,store,isReplicFilter){if(filters){Ext.MessageBox.confirm(Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Title"),Openwis.i18n("CacheConfiguration.MSSFSS.OriginatorFilter.Remove.Msg"),function(btnClicked){if(btnClicked=="yes"){for(var i=0;
i<filters.length;
i++){var filter=filters[i];
this.proceedRemoveFilter(filter,store,isReplicFilter)
}}},this)
}},proceedRemoveFilter:function(filter,store,isReplicFilter){if(filter){handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+store.service,params:{requestType:"REMOVE_FILTER",regex:filter.get("regex"),description:filter.get("description"),source:isReplicFilter?filter.get("source"):null,type:isReplicFilter?filter.get("type"):null,active:isReplicFilter?filter.get("active"):null},listeners:{success:function(responseText){store.reload()
},scope:this}});
handler.proceed()
}},setServiceStatus:function(checkbox,checked){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+checkbox.service,params:{requestType:"SET_SERVICE_STATUS",checked:checked},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"result");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttribute(attributes,"success");
var isError=false;
if(success!=null){if(!success.nodeValue=="true"){isError=true
}}else{isError=true
}if(isError){Openwis.Utils.MessageBox.displayInternalError();
checkbox.active=false;
checkbox.setValue(!checked);
checkbox.active=true
}},failure:function(responseText){Openwis.Utils.MessageBox.displayInternalError();
checkbox.active=false;
checkbox.setValue(!checked);
checkbox.active=true
},scope:this}});
if(checkbox.active){handler.proceed()
}},updateServiceStatus:function(checkbox){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+checkbox.service,useLoadMask:false,params:{requestType:"GET_SERVICE_STATUS"},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"result");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttribute(attributes,"success");
var isError=false;
if(success!=null){if(success.nodeValue=="true"){var status=Openwis.Utils.Xml.getAttributeValue(attributes,"status");
var enabled=status=="ENABLED";
checkbox.active=false;
checkbox.setValue(enabled);
checkbox.active=true
}else{var isError=true
}}else{var isError=true
}if(isError){Openwis.Utils.MessageBox.displayInternalError()
}},failure:function(responseText){Openwis.Utils.MessageBox.displayInternalError()
},scope:this}});
handler.proceed()
},activateFilter:function(filter,active,store,reload){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.replicationService,params:{requestType:"SET_FILTER_STATUS",regex:filter.get("regex"),description:filter.get("description"),source:filter.get("source"),regex:filter.get("regex"),active:filter.get("active"),checked:active},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"result");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttribute(attributes,"success");
var isError=false;
if(success!=null){if(success.nodeValue=="true"){if(reload){store.reload()
}}else{isError=true
}}else{isError=true
}if(isError){Openwis.Utils.MessageBox.displayInternalError()
}},failure:function(responseText){},scope:this}});
handler.proceed()
},activeRenderer:function(value){return value==true?Openwis.i18n("CacheConfiguration.MSSFSS.Replication.activeRenderer.Active"):Openwis.i18n("CacheConfiguration.MSSFSS.Replication.activeRenderer.Suspended")
},checkRenderer:function(value){return new Ext.form.Checkbox({boxLabel:"",checked:false})
}});Ext.ns("Openwis.Admin.DataService");
Openwis.Admin.DataService.FilterInputDialog=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("CacheConfiguration.FilterInputDialog.Title"),layout:"fit",width:350,height:135,modal:true,closeAction:"close",resizable:false});
Openwis.Admin.DataService.FilterInputDialog.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("filterSaved");
this.add(this.getFilterInputFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.getRegExTextField().setValue(this.selectedFilter.regex);
this.getDescriptionTextField().setValue(this.selectedFilter.description);
this.setTitle(Openwis.i18n("CacheConfiguration.FilterInputDialog.Title"))
},getFilterInputFormPanel:function(){if(!this.filterInputFormPanel){this.filterInputFormPanel=new Ext.form.FormPanel({border:false,itemCls:"formItems",labelWidth:70,style:{marginTop:"3px",marginLeft:"6px"}});
this.filterInputFormPanel.add(this.getRegExTextField());
this.filterInputFormPanel.add(this.getDescriptionTextField())
}return this.filterInputFormPanel
},getRegExTextField:function(){if(!this.regExTextField){this.regExTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CacheConfiguration.FilterInputDialog.RegEx"),name:"regex",width:250})
}return this.regExTextField
},getDescriptionTextField:function(){if(!this.descriptionTextField){this.descriptionTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CacheConfiguration.FilterInputDialog.Description"),name:"decription",width:250})
}return this.descriptionTextField
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getFilterInputFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.locationService,params:{requestType:"ADD_FILTER",regex:this.getRegExTextField().getValue(),description:this.getDescriptionTextField().getValue()},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"result");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttribute(attributes,"success");
var msg="The filter has been successfully added.";
var isError=false;
if(success!=null){if(!success.nodeValue=="true"){isError=true;
msg=Openwis.Utils.Xml.getAttributeValue(attributes,"error");
Openwis.Utils.MessageBox.displayInternalError()
}}else{isError=true;
msg=Openwis.Utils.Xml.getAttributeValue(attributes,"error");
Openwis.Utils.MessageBox.displayInternalError()
}this.fireEvent("filterSaved",msg,isError);
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
}});Ext.ns("Openwis.Admin.DataService");
Openwis.Admin.DataService.ReplicationFilterDialog=Ext.extend(Ext.Window,{saveRequestType:"ADD_FILTER",isNewFilter:true,initComponent:function(){Ext.apply(this,{title:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.Title"),layout:"fit",width:350,height:222,modal:true,closeAction:"close",resizable:false});
Openwis.Admin.DataService.ReplicationFilterDialog.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("filterSaved");
this.add(this.getFilterInputFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.getRegExTextField().setValue(this.selectedFilter.regex);
this.getDescriptionTextField().setValue(this.selectedFilter.description);
this.getSourceTextField().setValue(this.selectedFilter.source);
this.getTypeTextField().setValue(this.selectedFilter.type);
this.getActiveCheckBox().setValue(this.selectedFilter.active);
this.setTitle(Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.Title"));
this.isNewFilter=this.operationMode=="New";
this.saveRequestType=this.isNewFilter?"ADD_FILTER":"UPDATE_FILTER"
},getFilterInputFormPanel:function(){if(!this.filterInputFormPanel){this.filterInputFormPanel=new Ext.form.FormPanel({border:false,itemCls:"formItems",labelWidth:70,style:{marginTop:"3px",marginLeft:"6px"}});
this.filterInputFormPanel.add(this.getActiveCheckBox());
this.filterInputFormPanel.add(this.getSourceTextField());
this.filterInputFormPanel.add(this.getTypeTextField());
this.filterInputFormPanel.add(this.getRegExTextField());
this.filterInputFormPanel.add(this.getDescriptionTextField())
}return this.filterInputFormPanel
},getRegExTextField:function(){if(!this.regExTextField){this.regExTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.RegEx"),name:"regex",width:250})
}return this.regExTextField
},getDescriptionTextField:function(){if(!this.descriptionTextField){this.descriptionTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.Description"),name:"decription",width:250})
}return this.descriptionTextField
},getSourceTextField:function(){if(!this.sourceTextField){this.sourceTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.Gisc"),name:"source",width:250})
}return this.sourceTextField
},getTypeTextField:function(){if(!this.sourceTypeField){this.sourceTypeField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.Type"),name:"type",width:250})
}return this.sourceTypeField
},getActiveCheckBox:function(){if(!this.activeCheckBox){this.activeCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.Active"),name:"active",width:250})
}return this.activeCheckBox
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getFilterInputFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.locationService,params:{requestType:this.saveRequestType,regex:this.getRegExTextField().getValue(),description:this.getDescriptionTextField().getValue(),source:this.getSourceTextField().getValue(),type:this.getTypeTextField().getValue(),active:this.getActiveCheckBox().getValue(),editSource:this.isNewFilter?null:this.selectedFilter.source,editRegex:this.isNewFilter?null:this.selectedFilter.regex},listeners:{success:function(responseText){if(this.checkSaveResponse(responseText)){this.fireEvent("filterSaved");
this.close()
}},failure:function(){this.showError(Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.ErrorDuplicate"))
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},checkSaveResponse:function(responseText){var result=false;
var resultElement=Openwis.Utils.Xml.getElement(responseText,"result");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttribute(attributes,"success");
var msg=null;
if(success!=null){if(success.nodeValue=="true"){result=true
}else{var errorMsg=Openwis.Utils.Xml.getAttributeValue(attributes,"error");
if(errorMsg!=null){msg=errorMsg
}else{msg=Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.ErrorDuplicate")
}}}else{var errorMsg=Openwis.Utils.Xml.getAttributeValue(attributes,"error");
if(errorMsg!=null){msg=errorMsg
}else{msg=Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.ErrorDuplicate")
}}if(msg!=null){this.showError(msg)
}return result
},showError:function(msg){var msgText=Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.ErrorMsg")+msg;
Ext.Msg.show({title:Openwis.i18n("CacheConfiguration.ReplicationFilterDialog.ErrorTitle"),msg:msgText,buttons:Ext.Msg.OK,scope:this,icon:Ext.MessageBox.ERROR})
}});Ext.ns("Openwis.Admin.Category");
Openwis.Admin.DataService.RequestsStatistics=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.DataService.RequestsStatistics.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(new Ext.Container({html:Openwis.i18n("RequestsStatistics.DataDisseminated.Title"),cls:"administrationTitle2",style:{marginBottom:"5px"}}));
this.add(this.getSearchFormPanel());
this.add(this.getDataDisseminatedGrid());
this.add(new Ext.Container({html:Openwis.i18n("RequestsStatistics.DataExtracted.Title"),cls:"administrationTitle2",style:{marginTop:"30px",marginBottom:"5px"}}));
this.add(this.getDataExtractedGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("RequestsStatistics.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:100,border:false,buttonAlign:"center"});
this.searchFormPanel.add(this.getSearchTextField());
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchTextField:function(){if(!this.searchTextField){this.searchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("RequestsStatistics.UserName.Search"),name:"any",enableKeyEvents:true,width:150,listeners:{keyup:function(){var searchOn=Ext.isEmpty(this.getSearchTextField().getValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getDataDisseminatedStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getDataDisseminatedStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}})
}return this.searchTextField
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){this.getDataDisseminatedStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getDataDisseminatedStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Reset"),scope:this,handler:function(){this.getSearchTextField().setValue("");
this.getDataDisseminatedStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getDataDisseminatedStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
this.getSearchAction().setDisabled(true);
this.getResetAction().setDisabled(true)
}})
}return this.resetAction
},getDataDisseminatedGrid:function(){if(!this.dataDisseminatedGrid){this.dataDisseminatedGrid=new Ext.grid.GridPanel({id:"dataDisseminatedGrid",height:200,border:true,store:this.getDataDisseminatedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("RequestsStatistics.DataDisseminated.Date"),dataIndex:"date",sortable:true,width:140,renderer:Openwis.Utils.Date.formatDateUTCfromLong},{id:"userId",header:Openwis.i18n("RequestsStatistics.DataDisseminated.UserId"),dataIndex:"userId",sortable:true,width:140},{id:"dissToolSize",header:Openwis.i18n("RequestsStatistics.DataDisseminated.Size"),dataIndex:"dissToolSize",sortable:true,width:140,renderer:Openwis.Common.Request.Utils.sizeRenderer},{id:"dissToolNbFiles",header:Openwis.i18n("RequestsStatistics.DataDisseminated.NbFiles"),dataIndex:"dissToolNbFiles",sortable:true,width:140}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getDataDisseminatedStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})});
this.dataDisseminatedGrid.addButton(new Ext.Button(this.getExportDataDisseminatedAction()))
}return this.dataDisseminatedGrid
},getDataDisseminatedStore:function(){if(!this.dataDisseminatedStore){this.dataDisseminatedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.requestsStatistics.allDataDisseminated",remoteSort:true,root:"allDataDisseminated",totalProperty:"count",idProperty:"id",fields:[{name:"id"},{name:"userId",sortType:Ext.data.SortTypes.asUCString},{name:"date"},{name:"dissToolNbFiles"},{name:"dissToolSize"}],sortInfo:{field:"date",direction:"DESC"}})
}return this.dataDisseminatedStore
},getExportDataDisseminatedAction:function(){if(!this.exportDataDisseminatedAction){this.exportDataDisseminatedAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataDisseminatedStore().data.length>0){window.open(configOptions.locService+"/xml.requestsStatistics.allDataDisseminated.export?start=0&xml=true&any="+this.getSearchTextField().getValue()+"&sort="+this.getDataDisseminatedStore().sortInfo.field+"&dir="+this.getDataDisseminatedStore().sortInfo.direction,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportDataDisseminatedAction
},getDataExtractedGrid:function(){if(!this.dataExtractedGrid){this.dataExtractedGrid=new Ext.grid.GridPanel({id:"dataExtractedGrid",height:200,border:true,store:this.getDataExtractedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("RequestsStatistics.DataExtracted.Date"),dataIndex:"date",sortable:true,width:140,renderer:Openwis.Utils.Date.formatDateUTCfromLong},{id:"size",header:Openwis.i18n("RequestsStatistics.DataExtracted.Extracted"),dataIndex:"size",sortable:true,width:140,renderer:Openwis.Common.Request.Utils.sizeRenderer},{id:"dissToolSize",header:Openwis.i18n("RequestsStatistics.DataExtracted.Disseminated"),dataIndex:"dissToolSize",sortable:true,width:140,renderer:Openwis.Common.Request.Utils.sizeRenderer}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getDataExtractedStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})});
this.dataExtractedGrid.addButton(new Ext.Button(this.getExportDataExtractedAction()))
}return this.dataExtractedGrid
},getDataExtractedStore:function(){if(!this.dataExtractedStore){this.dataExtractedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.requestsStatistics.allDataExtracted",remoteSort:true,root:"allDataExtracted",totalProperty:"count",idProperty:"id",fields:[{name:"id"},{name:"date",sortType:Ext.data.SortTypes.asUCString},{name:"size"},{name:"dissToolSize"}],sortInfo:{field:"date",direction:"DESC"}})
}return this.dataExtractedStore
},getExportDataExtractedAction:function(){if(!this.exportDataExtractedAction){this.exportDataExtractedAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataExtractedStore().data.length>0){window.open(configOptions.locService+"/xml.requestsStatistics.allDataExtracted.export?start=0&xml=true&sort="+this.getDataExtractedStore().sortInfo.field+"&dir="+this.getDataExtractedStore().sortInfo.direction,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportDataExtractedAction
}});Ext.ns("Openwis.Admin.Category");
Openwis.Admin.DataService.Blacklist=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.DataService.Blacklist.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSearchFormPanel());
this.add(this.getBlacklistGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Blacklist.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:100,border:false,buttonAlign:"center"});
this.searchFormPanel.add(this.getSearchTextField());
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchTextField:function(){if(!this.searchTextField){this.searchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Blacklist.UserName.Search"),name:"any",enableKeyEvents:true,width:150,listeners:{keyup:function(){var searchOn=Ext.isEmpty(this.getSearchTextField().getValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getBlacklistStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getBlacklistStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}})
}return this.searchTextField
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:function(){this.getBlacklistStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getBlacklistStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Reset"),scope:this,handler:function(){this.getSearchTextField().setValue("");
this.getBlacklistStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getBlacklistStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
this.getSearchAction().setDisabled(true);
this.getResetAction().setDisabled(true)
}})
}return this.resetAction
},getBlacklistGrid:function(){if(!this.blacklistGrid){this.blacklistGrid=new Ext.grid.GridPanel({id:"blacklistGrid",height:400,border:true,store:this.getBlacklistStore(),loadMask:true,columns:[{id:"user",header:Openwis.i18n("Blacklist.user"),dataIndex:"user",sortable:true,width:120},{id:"nbDisseminationWarnThreshold",header:Openwis.i18n("Blacklist.nbWarn"),dataIndex:"nbDisseminationWarnThreshold",sortable:true,width:90},{id:"volDisseminationWarnThreshold",header:Openwis.i18n("Blacklist.volWarn"),dataIndex:"volDisseminationWarnThreshold",sortable:true,width:90},{id:"nbDisseminationBlacklistThreshold",header:Openwis.i18n("Blacklist.nbBlacklist"),dataIndex:"nbDisseminationBlacklistThreshold",sortable:true,width:90},{id:"volDisseminationBlacklistThreshold",header:Openwis.i18n("Blacklist.volBlacklist"),dataIndex:"volDisseminationBlacklistThreshold",sortable:true,width:90},{id:"userDisseminatedDataDTO.dissToolNbFiles",header:Openwis.i18n("Blacklist.nbCurrent"),dataIndex:"userDisseminatedDataDTO.dissToolNbFiles",width:90},{id:"userDisseminatedDataDTO.dissToolSize",header:Openwis.i18n("Blacklist.volCurrent"),dataIndex:"userDisseminatedDataDTO.dissToolSize",width:90},{id:"blacklisted",header:Openwis.i18n("Blacklist.blacklist"),dataIndex:"blacklisted",sortable:true,width:50,renderer:this.renderBlacklistState}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount()!=1)
}}}),bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getBlacklistStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})});
this.blacklistGrid.addButton(new Ext.Button(this.getEditAction()))
}return this.blacklistGrid
},getBlacklistStore:function(){if(!this.blacklistStore){this.blacklistStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.blacklist.all",remoteSort:true,root:"allBlackList",totalProperty:"count",idProperty:"id",fields:[{name:"id"},{name:"user",sortType:Ext.data.SortTypes.asUCString},{name:"nbDisseminationWarnThreshold"},{name:"volDisseminationWarnThreshold"},{name:"nbDisseminationBlacklistThreshold"},{name:"volDisseminationBlacklistThreshold"},{name:"userDisseminatedDataDTO.dissToolNbFiles"},{name:"userDisseminatedDataDTO.dissToolSize"},{name:"blacklisted"}],sortInfo:{field:"user",direction:"ASC"}})
}return this.blacklistStore
},renderBlacklistState:function(value){return(value)?Openwis.i18n("Blacklist.renderBlacklistState.True"):Openwis.i18n("Blacklist.renderBlacklistState.False")
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getBlacklistGrid().getSelectionModel().getSelected();
new Openwis.Admin.DataService.EditBlacklist({operationMode:"Edit",selectedRec:selectedRec.json,listeners:{blacklistSaved:function(){this.getBlacklistGrid().getStore().reload()
},scope:this}})
}})
}return this.editAction
}});Ext.ns("Openwis.Admin.Category");
Openwis.Admin.DataService.EditBlacklist=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Blacklist.Edit.Title"),layout:"fit",width:350,height:380,modal:true,closeAction:"close"});
Openwis.Admin.DataService.EditBlacklist.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("blacklistSaved");
this.add(this.getBlacklistFormPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.getUserTextField().setValue(this.selectedRec.user);
this.getNbWarnNumberField().setValue(this.selectedRec.nbDisseminationWarnThreshold);
this.getVolWarnNumberField().setValue(this.selectedRec.volDisseminationWarnThreshold);
this.getNbBlNumberField().setValue(this.selectedRec.nbDisseminationBlacklistThreshold);
this.getVolBlNumberField().setValue(this.selectedRec.volDisseminationBlacklistThreshold);
this.getNbCurrentTextField().setValue(this.selectedRec.userDisseminatedDataDTO.nbFiles);
this.getVolCurrentTextField().setValue(this.selectedRec.userDisseminatedDataDTO.size);
this.getBlacklistCheckBox().setValue(this.selectedRec.blacklisted);
this.show()
},getBlacklistFormPanel:function(){if(!this.blacklistFormPanel){this.blacklistFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:125});
this.blacklistFormPanel.add(this.getUserTextField());
this.blacklistFormPanel.add(this.getNbWarnNumberField());
this.blacklistFormPanel.add(this.getVolWarnNumberField());
this.blacklistFormPanel.add(this.getNbBlNumberField());
this.blacklistFormPanel.add(this.getVolBlNumberField());
this.blacklistFormPanel.add(this.getNbCurrentTextField());
this.blacklistFormPanel.add(this.getVolCurrentTextField());
this.blacklistFormPanel.add(this.getBlacklistCheckBox())
}return this.blacklistFormPanel
},getUserTextField:function(){if(!this.userTextField){this.userTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Blacklist.user"),name:"username",disabled:true,width:150})
}return this.userTextField
},getNbWarnNumberField:function(){if(!this.nbWarnNumberField){this.nbWarnNumberField=new Ext.form.NumberField({allowBlank:false,fieldLabel:Openwis.i18n("Blacklist.nbDisseminationWarnThreshold"),name:"nbWarn",width:150,autoCreate:{tag:"input",type:"text",size:"20",autocomplete:"off",maxlength:"16"}})
}return this.nbWarnNumberField
},getVolWarnNumberField:function(){if(!this.volWarnNumberField){this.volWarnNumberField=new Ext.form.NumberField({allowBlank:false,fieldLabel:Openwis.i18n("Blacklist.volDisseminationWarnThreshold"),name:"volWarn",maxLength:"16",width:150,autoCreate:{tag:"input",type:"text",size:"20",autocomplete:"off",maxlength:"16"}})
}return this.volWarnNumberField
},getNbBlNumberField:function(){if(!this.nbBlNumberField){this.nbBlNumberField=new Ext.form.NumberField({allowBlank:false,fieldLabel:Openwis.i18n("Blacklist.nbDisseminationBlacklistThreshold"),name:"nbBl",width:150,autoCreate:{tag:"input",type:"text",size:"20",autocomplete:"off",maxlength:"16"}})
}return this.nbBlNumberField
},getVolBlNumberField:function(){if(!this.volBlNumberField){this.volBlNumberField=new Ext.form.NumberField({allowBlank:false,fieldLabel:Openwis.i18n("Blacklist.volDisseminationBlacklistThreshold"),name:"volBl",maxLength:"16",width:150,autoCreate:{tag:"input",type:"text",size:"20",autocomplete:"off",maxlength:"16"}})
}return this.volBlNumberField
},getNbCurrentTextField:function(){if(!this.nbCurrentTextField){this.nbCurrentTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Blacklist.nbDisseminationCurrent"),name:"nbCurrent",disabled:true,width:150})
}return this.nbCurrentTextField
},getVolCurrentTextField:function(){if(!this.volCurrentTextField){this.volCurrentTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Blacklist.volDisseminationCurrent"),name:"volCurrent",disabled:true,width:150})
}return this.volCurrentTextField
},getBlacklistCheckBox:function(){if(!this.blacklistCheckBox){this.blacklistCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("Blacklist.blacklist"),name:"blacklistCb",width:150,listeners:{check:function(checkbox,checked){if(!checked){}else{}},scope:this}})
}return this.blacklistCheckBox
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getBlacklistFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.editBlacklist.save",params:this.getBlacklistUpdated(),listeners:{success:function(config){this.fireEvent("blacklistSaved");
this.close()
},scope:this}});
saveHandler.proceed()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getBlacklistUpdated:function(){var blacklistUpdated={};
blacklistUpdated.user=this.selectedRec.user;
blacklistUpdated.nbDisseminationWarnThreshold=this.getNbWarnNumberField().getValue();
blacklistUpdated.volDisseminationWarnThreshold=this.getVolWarnNumberField().getValue();
blacklistUpdated.nbDisseminationBlacklistThreshold=this.getNbBlNumberField().getValue();
blacklistUpdated.volDisseminationBlacklistThreshold=this.getVolBlNumberField().getValue();
blacklistUpdated.blacklisted=this.getBlacklistCheckBox().checked;
return blacklistUpdated
}});Ext.ns("Openwis.Admin.Statistics");
Openwis.Admin.Statistics.RecentEvents=Ext.extend(Ext.Container,{dateFormat:"Y-m-d H:i",alarmService:"/xml.management.alarms.events",initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Statistics.RecentEvents.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSearchFormPanel());
this.add(this.getEventGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Alarms.RecentEvents.Title"),cls:"administrationTitle1"})
}return this.header
},getEventGrid:function(){if(!this.eventGrid){var columns=[];
columns.push(new Ext.grid.Column({id:"date",header:Openwis.i18n("Alarms.RecentEvents.Grid.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong,width:120,sortable:true,hideable:false}));
columns.push(new Ext.grid.Column({id:"component",header:Openwis.i18n("Alarms.RecentEvents.Grid.Component"),dataIndex:"module",width:110,sortable:true}));
columns.push(new Ext.grid.Column({id:"process",header:Openwis.i18n("Alarms.RecentEvents.Grid.Process"),dataIndex:"source",width:130,sortable:true,renderer:this.renderProcess.createDelegate(this)}));
columns.push(new Ext.grid.Column({id:"severtity",header:Openwis.i18n("Alarms.RecentEvents.Grid.Severity"),dataIndex:"severity",width:70,sortable:true}));
columns.push(new Ext.grid.Column({id:"description",header:Openwis.i18n("Alarms.RecentEvents.Grid.Description"),dataIndex:"message",width:300,sortable:true,hideable:false,renderer:this.renderMessage.createDelegate(this)}));
this.eventGrid=new Ext.grid.GridPanel({id:"eventGrid",height:400,border:true,store:this.getEventStore(),loadMask:true,view:this.getGridView(),columns:columns,listeners:{afterrender:function(grid){grid.loadMask.show();
this.reset();
this.reload()
},scope:this},bbar:this.getPagingToolbar()})
}return this.eventGrid
},getPagingToolbar:function(){if(!this.pagingToolbar){this.pagingToolbar=new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getEventStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Alarms.RecentEvents.Grid.Range"),emptyMsg:Openwis.i18n("Alarms.RecentEvents.Grid.No.Data")})
}return this.pagingToolbar
},getEventStore:function(){if(!this.eventStore){this.eventStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.alarmService,remoteSort:true,root:"rows",totalProperty:"total",idProperty:"id",fields:[{name:"id"},{name:"date"},{name:"source",sortType:Ext.data.SortTypes.asUCString},{name:"module",sortType:Ext.data.SortTypes.asUCString},{name:"severity",sortType:Ext.data.SortTypes.asUCString},{name:"message",sortType:Ext.data.SortTypes.asUCString}],sortInfo:{field:"date",direction:"DESC"}})
}return this.eventStore
},getGridView:function(){if(!this.gridView){this.gridView=new Ext.grid.GridView({emptyText:Openwis.i18n("Alarms.RecentEvents.Grid.No.Data"),forceFit:true,getRowClass:function(record,index,rowParams,store){if(Ext.util.Format.lowercase(record.data.severity)=="error"){return"eventErrorGridRow"
}}})
}return this.gridView
},renderMessage:function(value,cell,record){var data=record.data;
var msg=data.message;
return'<div qtip="'+Ext.util.Format.htmlEncode(msg)+'">'+Ext.util.Format.htmlEncode(value)+"</div>"
},renderProcess:function(value,cell,record){var data=record.data;
var process=data.source;
return'<div qtip="'+process+'">'+value+"</div>"
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:75,border:false,layout:"column",defaults:{columnWidth:0.5,layout:"form",border:false,bodyStyle:"padding:0 18px 0 0"},items:[{defaults:{anchor:"100%"},items:[this.getSearchFromDateField(),this.getSearchSeverityComboField(),this.getSearchComponentTextField()]},{defaults:{anchor:"100%"},items:[this.getSearchToDateField(),this.getSearchProcessTextField(),this.getSearchDescriptionTextField()]}]});
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchFromDateField:function(){if(!this.searchFromDateField){this.searchFromDateField=new Ext.form.DateField({fieldLabel:Openwis.i18n("Alarms.RecentEvents.Filter.DateFrom"),name:"searchFromDate",width:150,allowBlank:true,format:this.dateFormat})
}return this.searchFromDateField
},getSearchToDateField:function(){if(!this.searchToDateField){this.searchToDateField=new Ext.form.DateField({fieldLabel:Openwis.i18n("Alarms.RecentEvents.Filter.DateTo"),name:"searchToDate",width:150,allowBlank:true,format:this.dateFormat})
}return this.searchToDateField
},getSearchSeverityComboField:function(){if(!this.searchSeverityComboField){this.searchSeverityComboField=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Alarms.RecentEvents.Filter.Severity"),name:"searchSeverity",width:150,allowBlank:true,mode:"local",store:["INFO","WARN","ERROR"],triggerAction:"all"})
}return this.searchSeverityComboField
},getSearchComponentTextField:function(){if(!this.searchComponentTextField){this.searchComponentTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Alarms.RecentEvents.Filter.Component"),name:"searchComponent",width:150})
}return this.searchComponentTextField
},getSearchProcessTextField:function(){if(!this.searchProcessTextField){this.searchProcessTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Alarms.RecentEvents.Filter.Process"),name:"searchProcess",width:150})
}return this.searchProcessTextField
},getSearchDescriptionTextField:function(){if(!this.searchDescriptionTextField){this.searchDescriptionTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Alarms.RecentEvents.Filter.Description"),name:"searchDecription",width:150})
}return this.searchDescriptionTextField
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:false,text:Openwis.i18n("Common.Btn.Search"),scope:this,handler:this.reload})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:false,text:Openwis.i18n("Common.Btn.Reset"),scope:this,handler:function(){this.reset();
this.reload()
}})
}return this.resetAction
},reload:function(){if(!this.getSearchFromDateField().isValid()){Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Alarms.RecentEvents.Error.DateFormat")+this.dateFormat);
return
}if(!this.getSearchToDateField().isValid()){Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("Alarms.RecentEvents.Error.DateFormat")+this.dateFormat+"'!");
return
}var date1=this.getSearchFromDateField().getValue();
var date2=this.getSearchToDateField().getValue();
var severity=this.getSearchSeverityComboField().getValue();
var component=this.getSearchComponentTextField().getValue();
var process=this.getSearchProcessTextField().getValue();
var text=this.getSearchDescriptionTextField().getValue();
var fromDate="";
var toDate="";
if(date1!=null&&date1!=""){fromDate=date1.format(this.dateFormat)
}if(date2!=null&&date2!=""){toDate=date2.format(this.dateFormat)
}this.setBaseParams();
this.getEventStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE,date_from:fromDate,date_to:toDate,severity:severity,module:component,source:process,message:text}})
},reset:function(){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.alarmService,params:{requestType:"RESET_FILTER"}});
handler.proceed();
var filter={};
this.updateFilterFields(filter);
this.setBaseParams()
},getFilterParams:function(){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.alarmService,params:{requestType:"GET_FILTER_PARAMS"},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"filter");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttributeValue(attributes,"success");
if(success=="true"){var filter={date_from:Openwis.Utils.Xml.getAttributeValue(attributes,"date_from"),date_to:Openwis.Utils.Xml.getAttributeValue(attributes,"date_to"),severity:Openwis.Utils.Xml.getAttributeValue(attributes,"severity"),module:Openwis.Utils.Xml.getAttributeValue(attributes,"module"),source:Openwis.Utils.Xml.getAttributeValue(attributes,"source"),message:Openwis.Utils.Xml.getAttributeValue(attributes,"message")};
this.updateFilterFields(filter)
}else{Openwis.Utils.MessageBox.displayInternalError()
}},failure:function(responseText){Openwis.Utils.MessageBox.displayErrorMsg()
},scope:this}});
handler.proceed()
},updateFilterFields:function(filter){this.getSearchFromDateField().setValue(filter.date_from);
this.getSearchToDateField().setValue(filter.date_to);
this.getSearchSeverityComboField().setValue(filter.severity);
this.getSearchComponentTextField().setValue(filter.module);
this.getSearchProcessTextField().setValue(filter.source);
this.getSearchDescriptionTextField().setValue(filter.message)
},setBaseParams:function(){this.getEventStore().setBaseParam("date_from",this.getSearchFromDateField().getValue());
this.getEventStore().setBaseParam("date_to",this.getSearchToDateField().getValue());
this.getEventStore().setBaseParam("severity",this.getSearchSeverityComboField().getValue());
this.getEventStore().setBaseParam("module",this.getSearchComponentTextField().getValue());
this.getEventStore().setBaseParam("source",this.getSearchProcessTextField().getValue());
this.getEventStore().setBaseParam("message",this.getSearchDescriptionTextField().getValue())
}});Ext.ns("Openwis.Admin.Statistics");
Openwis.Admin.Statistics.GlobalReports=Ext.extend(Ext.Container,{disseminatedService:"/xml.management.alarms.reports.disseminated",exchangedService:"/xml.management.alarms.reports.extracted",ingestedService:"/xml.management.cache.statistics.ingest",replicatedService:"/xml.management.cache.statistics.replic",initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Statistics.GlobalReports.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getFilterPanel());
this.add(new Ext.Container({html:Openwis.i18n("Alarms.GlobalReports.Disseminated.Title"),cls:"administrationTitle2",style:{marginTop:"20px"}}));
this.add(this.getDataDisseminatedGrid());
this.add(new Ext.Container({html:Openwis.i18n("Alarms.GlobalReports.Extracted.Title"),cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getDataExtractedGrid());
this.add(new Ext.Container({html:Openwis.i18n("Alarms.GlobalReports.Ingested.Title"),cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getDataIngestedGrid());
this.add(new Ext.Container({html:Openwis.i18n("Alarms.GlobalReports.Replicated.Title"),cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getDataReplicatedGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Alarms.GlobalReports.Title"),cls:"administrationTitle1"})
}return this.header
},getFilterPanel:function(){if(!this.filterPanel){this.filterPanel=new Ext.form.FormPanel({labelWidth:100,border:false});
this.filterPanel.add(this.getFilterDayComboBox())
}return this.filterPanel
},getFilterDayComboBox:function(){if(!this.filterDayComboBox){this.filterDayComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("Alarms.GlobalReports.Filter.Label"),name:"filterDayComboBox",width:150,allowBlank:false,editable:false,mode:"local",store:new Ext.data.ArrayStore({id:0,fields:["day","displayText"],data:[[1,"1 "+Openwis.i18n("Alarms.GlobalReports.Filter.Day")],[2,"2 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[3,"3 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[4,"4 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[5,"5 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[6,"6 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[7,"7 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[8,"8 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[9,"9 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")],[10,"10 "+Openwis.i18n("Alarms.GlobalReports.Filter.Days")]]}),valueField:"day",displayField:"displayText",triggerAction:"all",listeners:{scope:this,select:function(combo,rcord,index){this.reloadAll()
}}})
}return this.filterDayComboBox
},getDataDisseminatedGrid:function(){if(!this.dataDisseminatedGrid){this.dataDisseminatedGrid=new Ext.grid.GridPanel({id:"dataDisseminatedGrid",height:250,border:true,store:this.getDataDisseminatedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Alarms.GlobalReports.Disseminated.Grid.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateUTCfromLong,width:100,sortable:true,hideable:false},{id:"user",header:Openwis.i18n("Alarms.GlobalReports.Disseminated.Grid.User"),dataIndex:"userId",width:150,sortable:true,hideable:false},{id:"size",header:Openwis.i18n("Alarms.GlobalReports.Disseminated.Grid.Size"),dataIndex:"dissToolSize",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true},{id:"threshold",header:Openwis.i18n("Alarms.GlobalReports.Disseminated.Grid.Threshold"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:200,sortable:true}],listeners:{afterrender:function(grid){},scope:this},bbar:this.createToolbar(this.getDataDisseminatedStore())});
this.dataDisseminatedGrid.addButton(new Ext.Button(this.getDataDisseminatedExportAction()))
}return this.dataDisseminatedGrid
},getDataDisseminatedStore:function(){if(!this.dataDisseminatedStore){this.dataDisseminatedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.disseminatedService,remoteSort:true,root:"rows",totalProperty:"total",fields:[{name:"date"},{name:"userId",sortType:Ext.data.SortTypes.asUCString},{name:"size",sortType:Ext.data.SortTypes.asInt},{name:"dissToolSize",sortType:Ext.data.SortTypes.asInt}],sortInfo:{field:"date",direction:"DESC"}})
}return this.dataDisseminatedStore
},getDataDisseminatedExportAction:function(){if(!this.dataDisseminatedExportAction){this.dataDisseminatedExportAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataDisseminatedStore().data.length>0){window.open(configOptions.locService+this.disseminatedService+"?start=0&xml=true&sort="+this.getDataDisseminatedStore().sortInfo.field+"&dir="+this.getDataDisseminatedStore().sortInfo.direction+"&period="+this.getFilterPeriod(),"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.dataDisseminatedExportAction
},getDataExtractedGrid:function(){if(!this.dataExtractedGrid){this.dataExtractedGrid=new Ext.grid.GridPanel({id:"dataExtractedGrid",height:250,border:true,store:this.getDataExtractedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Alarms.GlobalReports.Extracted.Grid.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateUTCfromLong,width:100,sortable:true,hideable:false},{id:"extracted",header:Openwis.i18n("Alarms.GlobalReports.Extracted.Grid.Extracted"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true},{id:"disseminated",header:Openwis.i18n("Alarms.GlobalReports.Extracted.Grid.Disseminated"),dataIndex:"dissToolSize",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true}],listeners:{afterrender:function(grid){},scope:this},bbar:this.createToolbar(this.getDataExtractedStore())});
this.dataExtractedGrid.addButton(new Ext.Button(this.getDataExtractedExportAction()))
}return this.dataExtractedGrid
},getDataExtractedStore:function(){if(!this.dataExtractedStore){this.dataExtractedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.exchangedService,remoteSort:true,root:"rows",totalProperty:"total",idProperty:"date",fields:[{name:"date"},{name:"size",sortType:Ext.data.SortTypes.asInt},{name:"dissToolSize",sortType:Ext.data.SortTypes.asInt}],sortInfo:{field:"date",direction:"DESC"}})
}return this.dataExtractedStore
},getDataExtractedExportAction:function(){if(!this.dataExtractedExportAction){this.dataExtractedExportAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataExtractedStore().data.length>0){window.open(configOptions.locService+this.exchangedService+"?start=0&xml=true&sort="+this.getDataExtractedStore().sortInfo.field+"&dir="+this.getDataExtractedStore().sortInfo.direction+"&period="+this.getFilterPeriod(),"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.dataExtractedExportAction
},getDataIngestedGrid:function(){if(!this.dataIngestedGrid){this.dataIngestedGrid=new Ext.grid.GridPanel({id:"dataIngestedGrid",height:250,border:true,store:this.getDataIngestedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Alarms.GlobalReports.Ingested.Grid.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateUTCfromLong,width:100,sortable:true,hideable:false},{id:"size",header:Openwis.i18n("Alarms.GlobalReports.Ingested.Grid.Size"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true}],listeners:{afterrender:function(grid){},scope:this},bbar:this.createToolbar(this.getDataIngestedStore())});
this.dataIngestedGrid.addButton(new Ext.Button(this.getDataIngestedExportAction()))
}return this.dataIngestedGrid
},getDataIngestedStore:function(){if(!this.dataIngestedStore){this.dataIngestedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.ingestedService,remoteSort:true,root:"rows",totalProperty:"total",idProperty:"date",fields:[{name:"date"},{name:"size",sortType:Ext.data.SortTypes.asInt}],sortInfo:{field:"date",direction:"DESC"}})
}return this.dataIngestedStore
},getDataIngestedExportAction:function(){if(!this.dataIngestedExportAction){this.dataIngestedExportAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataIngestedStore().data.length>0){window.open(configOptions.locService+this.ingestedService+"?start=0&xml=true&sort="+this.getDataIngestedStore().sortInfo.field+"&dir="+this.getDataIngestedStore().sortInfo.direction+"&period="+this.getFilterPeriod(),"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.dataIngestedExportAction
},getDataReplicatedGrid:function(){if(!this.dataReplicatedGrid){this.dataReplicatedGrid=new Ext.grid.GridPanel({id:"dataReplicatedGrid",height:250,border:true,store:this.getDataReplicatedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Alarms.GlobalReports.Replicated.Grid.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateUTCfromLong,width:100,sortable:true,hideable:false},{id:"source",header:Openwis.i18n("Alarms.GlobalReports.Replicated.Grid.Source"),dataIndex:"source",width:120,sortable:true},{id:"size",header:Openwis.i18n("Alarms.GlobalReports.Replicated.Grid.Size"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true}],listeners:{afterrender:function(grid){this.reloadAll()
},scope:this},bbar:this.createToolbar(this.getDataReplicatedStore())});
this.dataReplicatedGrid.addButton(new Ext.Button(this.getDataReplicatedExportAction()))
}return this.dataReplicatedGrid
},getDataReplicatedStore:function(){if(!this.dataReplicatedStore){this.dataReplicatedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.replicatedService,remoteSort:true,root:"rows",totalProperty:"total",fields:[{name:"date"},{name:"source"},{name:"size",sortType:Ext.data.SortTypes.asInt}],sortInfo:{field:"date",direction:"DESC"}})
}return this.dataReplicatedStore
},getDataReplicatedExportAction:function(){if(!this.dataReplicatedExportAction){this.dataReplicatedExportAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataReplicatedStore().data.length>0){window.open(configOptions.locService+this.replicatedService+"?start=0&xml=true&sort="+this.getDataReplicatedStore().sortInfo.field+"&dir="+this.getDataReplicatedStore().sortInfo.direction+"&period="+this.getFilterPeriod(),"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.dataReplicatedExportAction
},updateSelectionPeriod:function(){var handler=new Openwis.Handler.GetNoJson({url:configOptions.locService+this.disseminatedService,params:{requestType:"GET_FILTER_PERIOD"},listeners:{success:function(responseText){var resultElement=Openwis.Utils.Xml.getElement(responseText,"filter");
var attributes=resultElement.attributes;
var success=Openwis.Utils.Xml.getAttributeValue(attributes,"success");
var value="1";
if(success=="true"){value=Openwis.Utils.Xml.getAttributeValue(attributes,"period")
}else{Openwis.Utils.MessageBox.displayInternalError()
}this.setSelectionPeriod(value,true)
},failure:function(responseText){Openwis.Utils.MessageBox.displayInternalError();
this.setSelectionPeriod("1",true)
},scope:this}});
handler.proceed()
},setSelectionPeriod:function(period,reload){this.getFilterDayComboBox().setValue(period);
if(reload){this.reloadAll()
}},reloadAll:function(){this.loadData(this.getDataDisseminatedGrid());
this.loadData(this.getDataExtractedGrid());
this.loadData(this.getDataIngestedGrid());
this.loadData(this.getDataReplicatedGrid())
},loadData:function(grid){var period=this.getFilterPeriod();
grid.loadMask.show();
grid.getStore().setBaseParam("period",period);
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},createToolbar:function(store){return new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:store,displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})
},getFilterPeriod:function(){var period=this.getFilterDayComboBox().getValue();
if(period==""){period=1
}return period
}});Ext.ns("Openwis.Admin.Statistics");
Openwis.Admin.Statistics.CacheStatistics=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Statistics.CacheStatistics.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(new Ext.Container({html:Openwis.i18n("CacheStatistics.DataIngested.Title"),cls:"administrationTitle2"}));
this.add(this.getDataIngestedGrid());
this.add(new Ext.Container({html:Openwis.i18n("CacheStatistics.DataReplicated.Title"),cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getDataReplicatedGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("CacheStatistics.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getDataIngestedGrid:function(){if(!this.dataIngestedGrid){this.dataIngestedGrid=new Ext.grid.GridPanel({id:"dataIngestedGrid",height:250,border:true,store:this.getDataIngestedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("CacheStatistics.DataIngested.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateUTCfromLong,width:100,sortable:true,hideable:false},{id:"size",header:Openwis.i18n("CacheStatistics.DataIngested.Size"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getDataIngestedStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})});
this.dataIngestedGrid.addButton(new Ext.Button(this.getExportDataIngestedAction()))
}return this.dataIngestedGrid
},getDataIngestedStore:function(){if(!this.dataIngestedStore){this.dataIngestedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.management.cache.statistics.ingest",remoteSort:true,root:"rows",totalProperty:"total",idProperty:"date",fields:[{name:"date"},{name:"size",sortType:Ext.data.SortTypes.asInt}],sortInfo:{field:"date",direction:"DESC"},baseParams:{}})
}return this.dataIngestedStore
},getExportDataIngestedAction:function(){if(!this.exportDataIngestedAction){this.exportDataIngestedAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataIngestedStore().data.length>0){window.open(configOptions.locService+"/xml.management.cache.statistics.ingest.export?start=0&xml=true&sort="+this.getDataIngestedStore().sortInfo.field+"&dir="+this.getDataIngestedStore().sortInfo.direction,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportDataIngestedAction
},getDataReplicatedGrid:function(){if(!this.dataReplicatedGrid){this.dataReplicatedGrid=new Ext.grid.GridPanel({id:"dataReplicatedGrid",height:250,border:true,store:this.getDataReplicatedStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("CacheStatistics.DataReplicated.Date"),dataIndex:"date",renderer:Openwis.Utils.Date.formatDateUTCfromLong,width:100,sortable:true,hideable:false},{id:"source",header:Openwis.i18n("CacheStatistics.DataReplicated.Source"),dataIndex:"source",width:120,sortable:true},{id:"size",header:Openwis.i18n("CacheStatistics.DataReplicated.Size"),dataIndex:"size",renderer:Openwis.Common.Request.Utils.sizeRenderer,width:120,sortable:true}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getDataReplicatedStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("Common.Grid.Range"),emptyMsg:Openwis.i18n("Common.Grid.No.Data")})});
this.dataReplicatedGrid.addButton(new Ext.Button(this.getExportDataReplicatedAction()))
}return this.dataReplicatedGrid
},getDataReplicatedStore:function(){if(!this.dataReplicatedStore){this.dataReplicatedStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.management.cache.statistics.replic",remoteSort:true,root:"rows",totalProperty:"total",idProperty:"date",fields:[{name:"date"},{name:"source"},{name:"size",sortType:Ext.data.SortTypes.asInt}],sortInfo:{field:"date",direction:"DESC"},baseParams:{}})
}return this.dataReplicatedStore
},getExportDataReplicatedAction:function(){if(!this.exportDataReplicatedAction){this.exportDataReplicatedAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getDataReplicatedStore().data.length>0){window.open(configOptions.locService+"/xml.management.cache.statistics.replic.export?start=0&xml=true&sort="+this.getDataReplicatedStore().sortInfo.field+"&dir="+this.getDataReplicatedStore().sortInfo.direction,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("RequestsStatistics.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportDataReplicatedAction
}});Ext.ns("Openwis.Admin.Statistics");
Openwis.Admin.Statistics.UserAlarms=Ext.extend(Ext.Container,{dateFormat:"Y-m-d H:i",alarmService:"/xml.useralarms.getalluseralarms",alarmReportService:"/xml.useralarms.getuseralarmreport",initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Statistics.UserAlarms.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(new Ext.Container({html:"User alarms messages:",cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getUserAlarmGrid());
this.add(new Ext.Container({html:"Users with alarms report:",cls:"administrationTitle2",style:{marginTop:"30px"}}));
this.add(this.getUserAlarmReportGrid())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:"User Alarms",cls:"administrationTitle1"})
}return this.header
},getUserAlarmGrid:function(){if(!this.userAlarmGrid){var columns=[];
columns.push(new Ext.grid.Column({id:"date",header:"Date",dataIndex:"date",renderer:Openwis.Utils.Date.formatDateTimeUTCfromLong,width:120,sortable:true,hideable:false}));
columns.push(new Ext.grid.Column({id:"user",header:"User",dataIndex:"userId",width:100,sortable:true}));
columns.push(new Ext.grid.Column({id:"alarmtype",header:"Alarm Type",dataIndex:"alarmType",width:100,sortable:true,}));
columns.push(new Ext.grid.Column({id:"requestid",header:"Req/Sub ID",dataIndex:"requestId",width:100,sortable:true}));
columns.push(new Ext.grid.Column({id:"message",header:"Message",dataIndex:"message",width:300,sortable:true,hideable:false,renderer:this.renderMessage.createDelegate(this)}));
this.userAlarmSelectionModel=new Ext.grid.RowSelectionModel({singleSelect:false,listeners:{rowselect:function(sm,rowIndex,record){this.getDeleteAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){this.getDeleteAction().setDisabled(sm.getCount()==0)
},scope:this}});
this.userAlarmGrid=new Ext.grid.GridPanel({id:"eventGrid",height:250,border:true,store:this.getAlarmEventStore(),loadMask:true,view:this.getGridView(),columns:columns,listeners:{afterrender:function(grid){this.reload()
},scope:this},sm:this.userAlarmSelectionModel,bbar:this.getPagingToolbar()});
this.userAlarmGrid.addButton(new Ext.Button(this.getDeleteAction()));
this.userAlarmGrid.addButton(new Ext.Button(this.getDeleteAllAction()))
}return this.userAlarmGrid
},getPagingToolbar:function(){if(!this.pagingToolbar){this.pagingToolbar=new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getAlarmEventStore(),displayInfo:true,displayMsg:"Displaying alarm {0} - {1} of {2}",emptyMsg:"No alarms to display"})
}return this.pagingToolbar
},getAlarmEventStore:function(){if(!this.alarmEventStore){this.alarmEventStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.alarmService,remoteSort:true,root:"rows",totalProperty:"total",idProperty:"id",fields:[{name:"id"},{name:"date"},{name:"userId",sortType:Ext.data.SortTypes.asUCString},{name:"alarmType",sortType:Ext.data.SortTypes.asUCString},{name:"requestId",sortType:Ext.data.SortTypes.asUCString},{name:"message",sortType:Ext.data.SortTypes.asUCString}],sortInfo:{field:"date",direction:"DESC"}})
}return this.alarmEventStore
},getGridView:function(){if(!this.gridView){this.gridView=new Ext.grid.GridView({emptyText:"No results to display.",forceFit:true,})
}return this.gridView
},getUserAlarmReportGrid:function(){if(!this.userAlarmReportGrid){var columns=[new Ext.grid.Column({id:"userId",header:"User ID",dataIndex:"userId",width:200,sortable:true,hideable:false}),new Ext.grid.Column({id:"requestCount",header:"Requests",dataIndex:"requestCount",width:200,sortable:true,hideable:false}),new Ext.grid.Column({id:"subscriptionCount",header:"Subscriptions",dataIndex:"subscriptionCount",width:200,sortable:true,hideable:false}),new Ext.grid.Column({id:"totalCount",header:"Total",dataIndex:"totalCount",width:200,sortable:true,hideable:false})];
var userReportGridView=new Ext.grid.GridView({emptyText:"No results to display.",forceFit:true});
var userReportPagingToolbar=new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getUserAlarmReportStore(),displayInfo:true,displayMsg:"Displaying users {0} - {1} of {2}",emptyMsg:"No users to display"});
this.userAlarmReportGrid=new Ext.grid.GridPanel({id:"userReportGrid",height:250,border:true,store:this.getUserAlarmReportStore(),loadMask:true,view:userReportGridView,columns:columns,listeners:{afterrender:function(grid){this.getUserAlarmReportStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
},scope:this},bbar:userReportPagingToolbar})
}return this.userAlarmReportGrid
},getUserAlarmReportStore:function(){if(!this.userAlarmReportStore){this.userAlarmReportStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+this.alarmReportService,remoteSort:true,root:"rows",totalProperty:"total",idProperty:"id",fields:[{name:"userId"},{name:"requestCount"},{name:"subscriptionCount"},{name:"totalCount"},],sortInfo:{field:"totalCount",direction:"DESC"}})
}return this.userAlarmReportStore
},reload:function(){var params={start:0,limit:Openwis.Conf.PAGE_SIZE};
this.getAlarmEventStore().load({params:params})
},renderMessage:function(value,cell,record){var data=record.data;
var msg=data.message;
return'<div qtip="'+msg+'">'+value+"</div>"
},getDeleteAction:function(){if(!this.deleteAction){this.deleteAction=new Ext.Action({text:"Delete",iconCls:"icon-discard-adhoc",disabled:true,scope:this,handler:function(){var selection=this.userAlarmSelectionModel.getSelections();
var params={alarmIds:[]};
Ext.each(selection,function(item,index,allItems){params.alarmIds.push(item.get("id"))
},this);
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.useralarms.delete",params:params,listeners:{success:function(){this.getAlarmEventStore().reload();
this.getUserAlarmReportStore().reload()
},scope:this}}).proceed()
}})
}return this.deleteAction
},getDeleteAllAction:function(){if(!this.deleteAllAction){this.deleteAllAction=new Ext.Action({text:"Delete All",iconCls:"icon-discard-adhoc",scope:this,handler:function(){var params={};
new Openwis.Handler.Remove({url:configOptions.locService+"/xml.useralarms.deleteall",params:params,listeners:{success:function(){this.getAlarmEventStore().reload();
this.getUserAlarmReportStore().reload()
},scope:this}}).proceed()
}})
}return this.deleteAllAction
}});Ext.ns("Openwis.Admin");
Openwis.Admin.Browser=Ext.extend(Ext.ux.GroupTabPanel,{initComponent:function(){var items=[];
if(this.getAlarmsMenu()){items.push(this.getAlarmsMenu())
}if(this.getMetadataServiceMenu()){items.push(this.getMetadataServiceMenu())
}if(this.getDataServiceMenu()){items.push(this.getDataServiceMenu())
}if(this.getSecurityServiceMenu()){items.push(this.getSecurityServiceMenu())
}if(this.getBackupMenu()){items.push(this.getBackupMenu())
}if(this.getSystemMenu()){items.push(this.getSystemMenu())
}if(this.getPersonalInformationMenu()){items.push(this.getPersonalInformationMenu())
}Ext.apply(this,{tabWidth:200,activeGroup:0,items:items,listeners:{afterrender:function(ct){this.getAlarmsMenu().setActiveTab(this.getAlarmsRecentEventsMenu())
},scope:this}});
Openwis.Admin.Browser.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("panelInitialized")
},isServiceAccessible:function(service){var isAccessible=accessibleServices.indexOf(service);
return isAccessible!=-1
},getAlarmsMenu:function(){if(!this.alarmsMenu){var alarmsRecentEvents=this.isServiceAccessible("xml.management.alarms.events");
var alarmsGlobalReports=this.isServiceAccessible("xml.management.alarms.events");
if(alarmsRecentEvents||alarmsGlobalReports){this.alarmsMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.Alarms"),tabTip:Openwis.i18n("Admin.Browser.Alarms")}]});
if(alarmsRecentEvents){this.alarmsMenu.add(this.getAlarmsRecentEventsMenu())
}if(alarmsGlobalReports){this.alarmsMenu.add(this.getAlarmsGlobalReportsMenu())
}this.alarmsMenu.add(this.getAlarmsUserAlarmMenu())
}}return this.alarmsMenu
},getAlarmsRecentEventsMenu:function(){if(!this.alarmsRecentEventsMenu){this.alarmsRecentEventsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.Alarms.RecentEvents"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Statistics.RecentEvents());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.alarmsRecentEventsMenu
},getAlarmsGlobalReportsMenu:function(){if(!this.alarmsGlobalReportsMenu){this.alarmsGlobalReportsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.Alarms.GlobalReports"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Statistics.GlobalReports());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.alarmsGlobalReportsMenu
},getAlarmsUserAlarmMenu:function(){if(!this.alarmsUserAlarmsMenu){this.alarmsUserAlarmsMenu=new Ext.Panel({title:"User Alarms",listeners:{activate:function(ct){ct.add(new Openwis.Admin.Statistics.UserAlarms());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.alarmsUserAlarmsMenu
},getMetadataServiceMenu:function(){if(!this.metadataServiceMenu){var metadataServiceCreateMetadata=this.isServiceAccessible("xml.metadata.create.form");
var metadataServiceInsertMetadata=this.isServiceAccessible("xml.metadata.insert.form");
var metadataServiceConfigureHarvesting=this.isServiceAccessible("xml.harvest.all");
var metadataServiceConfigureSynchronization=this.isServiceAccessible("xml.harvest.all");
var metadataServiceMonitorCatalogContent=this.isServiceAccessible("xml.metadata.all");
var metadataServiceCatalogStatistics=this.isServiceAccessible("xml.catalogstatistics.all");
var metadataServiceTemplates=this.isServiceAccessible("xml.template.all");
var metadataServiceIndex=this.isServiceAccessible("metadata.admin.index.rebuild");
var metadataServiceThesauriManagement=this.isServiceAccessible("xml.thesaurus.list");
var metadataServiceCategoryManagement=this.isServiceAccessible("xml.category.all");
if(metadataServiceCreateMetadata||metadataServiceInsertMetadata||metadataServiceConfigureHarvesting||metadataServiceConfigureSynchronization||metadataServiceMonitorCatalogContent||metadataServiceCatalogStatistics||metadataServiceTemplates||metadataServiceIndex||metadataServiceThesauriManagement||metadataServiceCategoryManagement){this.metadataServiceMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.MetadataService"),tabTip:Openwis.i18n("Admin.Browser.MetadataService")}]});
if(metadataServiceCreateMetadata){this.metadataServiceMenu.add(this.getMetadataServiceCreateMetadataMenu())
}if(metadataServiceInsertMetadata){this.metadataServiceMenu.add(this.getMetadataServiceInsertMetadataMenu())
}if(metadataServiceConfigureHarvesting){this.metadataServiceMenu.add(this.getMetadataServiceConfigureHarvestingMenu())
}if(metadataServiceConfigureSynchronization){this.metadataServiceMenu.add(this.getMetadataServiceConfigureSynchronizationMenu())
}if(metadataServiceMonitorCatalogContent){this.metadataServiceMenu.add(this.getMetadataServiceMonitorCatalogContentMenu())
}if(metadataServiceCatalogStatistics){this.metadataServiceMenu.add(this.getMetadataServiceCatalogStatisticsMenu())
}if(metadataServiceTemplates){this.metadataServiceMenu.add(this.getMetadataServiceTemplatesMenu())
}if(metadataServiceIndex){this.metadataServiceMenu.add(this.getMetadataServiceIndexMenu())
}if(metadataServiceThesauriManagement){this.metadataServiceMenu.add(this.getMetadataServiceThesauriManagementMenu())
}if(metadataServiceCategoryManagement){this.metadataServiceMenu.add(this.getMetadataServiceCategoryManagementMenu())
}}}return this.metadataServiceMenu
},getMetadataServiceCreateMetadataMenu:function(){if(!this.metadataServiceCreateMetadataMenu){this.metadataServiceCreateMetadataMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Create"),listeners:{activate:function(ct){ct.add(new Openwis.Common.Metadata.Create());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceCreateMetadataMenu
},getMetadataServiceInsertMetadataMenu:function(){if(!this.metadataServiceInsertMetadataMenu){this.metadataServiceInsertMetadataMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Insert"),listeners:{activate:function(ct){ct.add(new Openwis.Common.Metadata.Insert());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceInsertMetadataMenu
},getMetadataServiceConfigureHarvestingMenu:function(){if(!this.metadataServiceConfigureHarvestingMenu){this.metadataServiceConfigureHarvestingMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Harvesting"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Harvesting.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceConfigureHarvestingMenu
},getMetadataServiceConfigureSynchronizationMenu:function(){if(!this.metadataServiceConfigureSynchronizationMenu){this.metadataServiceConfigureSynchronizationMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Synchronization"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Synchro.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceConfigureSynchronizationMenu
},getMetadataServiceMonitorCatalogContentMenu:function(){if(!this.metadataServiceMonitorCatalogContentMenu){this.metadataServiceMonitorCatalogContentMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.CatalogContent"),listeners:{activate:function(ct){ct.add(new Openwis.Common.Metadata.MonitorCatalog({isAdmin:true}));
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceMonitorCatalogContentMenu
},getMetadataServiceCatalogStatisticsMenu:function(){if(!this.metadataServiceCatalogStatisticsMenu){this.metadataServiceCatalogStatisticsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.CatalogStatistics"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.CatalogStatistics.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceCatalogStatisticsMenu
},getMetadataServiceTemplatesMenu:function(){if(!this.metadataServiceTemplatesMenu){this.metadataServiceTemplatesMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Templates"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Template.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceTemplatesMenu
},getMetadataServiceIndexMenu:function(){if(!this.metadataServiceIndexMenu){this.metadataServiceIndexMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Index"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Index.Manage());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceIndexMenu
},getMetadataServiceThesauriManagementMenu:function(){if(!this.metadataServiceThesauriManagementMenu){this.metadataServiceThesauriManagementMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Thesauri"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Thesauri.Manage({listeners:{guiChanged:function(){this.ownerCt.ownerCt.fireEvent("guiChanged",false,true)
},scope:this}}))
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceThesauriManagementMenu
},getMetadataServiceCategoryManagementMenu:function(){if(!this.metadataServiceCategoryManagementMenu){this.metadataServiceCategoryManagementMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.MetadataService.Category"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Category.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.metadataServiceCategoryManagementMenu
},getDataServiceMenu:function(){if(!this.dataServiceMenu){var dataServiceMonitorCurrentRequests=this.isServiceAccessible("xml.monitor.current.requests");
var dataServiceRequestStatistics=this.isServiceAccessible("xml.requestsStatistics.allDataExtracted");
var dataServiceCacheConfiguration=this.isServiceAccessible("xml.management.cache.configure.ingest");
var dataServiceBrowseContent=this.isServiceAccessible("xml.management.cache.browse");
var dataServiceCacheStatistics=this.isServiceAccessible("xml.management.cache.statistics.ingest");
var dataServiceBlacklisting=this.isServiceAccessible("xml.blacklist.all");
if(dataServiceMonitorCurrentRequests||dataServiceRequestStatistics||dataServiceCacheConfiguration||dataServiceBrowseContent||dataServiceCacheStatistics||dataServiceBlacklisting){this.dataServiceMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.DataService"),tabTip:Openwis.i18n("Admin.Browser.DataService")}]});
if(dataServiceMonitorCurrentRequests){this.dataServiceMenu.add(this.getDataServiceMonitorCurrentRequestsMenu())
}if(dataServiceRequestStatistics){this.dataServiceMenu.add(this.getDataServiceRequestStatisticsMenu())
}if(dataServiceCacheConfiguration){this.dataServiceMenu.add(this.getDataServiceCacheConfigurationMenu())
}if(dataServiceBrowseContent){this.dataServiceMenu.add(this.getDataServiceBrowseContentMenu())
}if(dataServiceCacheStatistics){this.dataServiceMenu.add(this.getDataServiceCacheStatisticsMenu())
}if(dataServiceBlacklisting){this.dataServiceMenu.add(this.getDataServiceBlacklistingMenu())
}}}return this.dataServiceMenu
},getDataServiceMonitorCurrentRequestsMenu:function(){if(!this.dataServiceMonitorCurrentRequestsMenu){this.dataServiceMonitorCurrentRequestsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.DataService.MonitorCurrentRequests"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.DataService.MonitorCurrentRequests());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.dataServiceMonitorCurrentRequestsMenu
},getDataServiceRequestStatisticsMenu:function(){if(!this.dataServiceRequestStatisticsMenu){this.dataServiceRequestStatisticsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.DataService.RequestStatistics"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.DataService.RequestsStatistics());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.dataServiceRequestStatisticsMenu
},getDataServiceCacheConfigurationMenu:function(){if(!this.dataServiceCacheConfigurationMenu){this.dataServiceCacheConfigurationMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.DataService.CacheConfiguration"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.DataService.CacheConfiguration());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.dataServiceCacheConfigurationMenu
},getDataServiceBrowseContentMenu:function(){if(!this.dataServiceBrowseContentMenu){this.dataServiceBrowseContentMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.DataService.BrowseContent"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.DataService.BrowseContent());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.dataServiceBrowseContentMenu
},getDataServiceCacheStatisticsMenu:function(){if(!this.dataServiceCacheStatisticsMenu){this.dataServiceCacheStatisticsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.DataService.CacheStatistics"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Statistics.CacheStatistics());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.dataServiceCacheStatisticsMenu
},getDataServiceBlacklistingMenu:function(){if(!this.dataServiceBlacklistingMenu){this.dataServiceBlacklistingMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.DataService.Blacklisting"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.DataService.Blacklist());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.dataServiceBlacklistingMenu
},getSecurityServiceMenu:function(){if(!this.securityServiceMenu){var securityServiceSSOManagement=this.isServiceAccessible("xml.sso.management");
var securityServiceUserManagement=this.isServiceAccessible("xml.user.all");
var securityServiceGroupManagement=this.isServiceAccessible("xml.group.remove");
var securityServiceDataPolicyManagement=this.isServiceAccessible("xml.datapolicy.all");
var securityServiceReport=this.isServiceAccessible("xml.user.report");
if(securityServiceSSOManagement||securityServiceUserManagement||securityServiceGroupManagement||securityServiceDataPolicyManagement||securityServiceReport){this.securityServiceMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.SecurityService"),tabTip:Openwis.i18n("Admin.Browser.SecurityService")}]});
if(securityServiceSSOManagement){this.securityServiceMenu.add(this.getSecurityServiceSSOManagementMenu())
}if(securityServiceUserManagement){this.securityServiceMenu.add(this.getSecurityServiceUserManagementMenu())
}if(securityServiceGroupManagement){this.securityServiceMenu.add(this.getSecurityServiceGroupManagementMenu())
}if(securityServiceDataPolicyManagement){this.securityServiceMenu.add(this.getSecurityServiceDataPolicyManagementMenu())
}if(securityServiceReport){this.securityServiceMenu.add(this.getSecurityServiceReportMenu())
}}}return this.securityServiceMenu
},getSecurityServiceSSOManagementMenu:function(){if(!this.securityServiceSSOManagementMenu){this.securityServiceSSOManagementMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.SecurityService.SSOManagement"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.SSOManagement.SSOManagement());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.securityServiceSSOManagementMenu
},getSecurityServiceUserManagementMenu:function(){if(!this.securityServiceUserManagementMenu){this.securityServiceUserManagementMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.SecurityService.UserManagement"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.User.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.securityServiceUserManagementMenu
},getSecurityServiceGroupManagementMenu:function(){if(!this.securityServiceGroupManagementMenu){this.securityServiceGroupManagementMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.SecurityService.GroupManagement"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Group.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.securityServiceGroupManagementMenu
},getSecurityServiceDataPolicyManagementMenu:function(){if(!this.securityServiceDataPolicyManagementMenu){this.securityServiceDataPolicyManagementMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.SecurityService.DPManagement"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.DataPolicy.All());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.securityServiceDataPolicyManagementMenu
},getSecurityServiceReportMenu:function(){if(!this.securityServiceReportMenu){this.securityServiceReportMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.SecurityService.Report"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.User.Report());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.securityServiceReportMenu
},getBackupMenu:function(){if(!this.backupMenu){var backupAvailabilityLocal=this.isServiceAccessible("xml.avalaibility.get");
var backupAvailabilityRemote=this.isServiceAccessible("xml.avalaibility.remote.get");
var availabilityStatistics=this.isServiceAccessible("xml.availability.getstatistics");
if(backupAvailabilityLocal||backupAvailabilityRemote||availabilityStatistics){this.backupMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.Backup"),tabTip:Openwis.i18n("Admin.Browser.Backup")}]});
if(backupAvailabilityLocal){this.backupMenu.add(this.getBackupAvailabilityLocalMenu())
}if(backupAvailabilityRemote){this.backupMenu.add(this.getBackupAvailabilityRemoteMenu())
}if(availabilityStatistics){this.backupMenu.add(this.getAvailabilityStatisticsMenu())
}}}return this.backupMenu
},getBackupAvailabilityLocalMenu:function(){if(!this.backupAvailabilityLocalMenu){this.backupAvailabilityLocalMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.Backup.Local"),listeners:{activate:function(ct){var localAvailabilityPanel=new Openwis.Admin.Availability.LocalAvailability();
ct.add(localAvailabilityPanel);
localAvailabilityPanel.addListener("panelInitialized",function(){this.ownerCt.ownerCt.fireEvent("panelInitialized")
},this);
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.backupAvailabilityLocalMenu
},getBackupAvailabilityRemoteMenu:function(){if(!this.backupAvailabilityRemoteMenu){this.backupAvailabilityRemoteMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.Backup.Remote"),listeners:{activate:function(ct){var remoteAvailabilityPanel=new Openwis.Admin.Availability.RemoteAvailability();
ct.add(remoteAvailabilityPanel);
remoteAvailabilityPanel.addListener("panelInitialized",function(){this.ownerCt.ownerCt.fireEvent("panelInitialized")
},this);
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.backupAvailabilityRemoteMenu
},getAvailabilityStatisticsMenu:function(){if(!this.availabilityStatisticsMenu){this.availabilityStatisticsMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.Backup.Statistics"),listeners:{activate:function(ct){ct.add(new Openwis.Admin.Availability.Statistics());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.availabilityStatisticsMenu
},getSystemMenu:function(){if(!this.systemMenu){var systemConfiguration=this.isServiceAccessible("xml.system.configuration.form");
if(systemConfiguration){this.systemMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.System"),tabTip:Openwis.i18n("Admin.Browser.System")},]});
if(systemConfiguration){this.systemMenu.add(this.getSystemConfigurationMenu());
this.systemMenu.add(this.getSystemMaintenance())
}}}return this.systemMenu
},getSystemConfigurationMenu:function(){if(!this.systemConfigurationMenu){this.systemConfigurationMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.System.Configuration"),listeners:{activate:function(ct){var systemPanel=new Openwis.Admin.System.SystemConfiguration({isAdmin:true});
ct.add(systemPanel);
systemPanel.addListener("panelInitialized",function(){this.fireEvent("panelInitialized")
},this);
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
},scope:this}})
}return this.systemConfigurationMenu
},getSystemMaintenance:function(){if(!this.systemMaintenancePanel){this.systemMaintenancePanel=new Ext.Panel({title:Openwis.i18n("Admin.Browser.System.Maintenance"),listeners:{activate:function(ct){var maintenancePanel=new Openwis.Admin.System.Maintenance();
ct.add(systemMaintenancePanel);
maintenancePanel.addListener("panelInitialized",function(){this.fireEvent("panelInitialized")
},this);
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
},scope:this}})
}return this.systemMaintenancePanel
},getSystemLocalizationMenu:function(){if(!this.systemLocalizationMenu){this.systemLocalizationMenu=new Ext.Panel({title:"Localization",listeners:{activate:function(ct){},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.systemLocalizationMenu
},getPersonalInformationMenu:function(){if(!this.personalInformationMenu){var userInfoAccessible=this.isServiceAccessible("xml.user.save");
var changePswd=this.isServiceAccessible("xml.user.changePassword");
if(userInfoAccessible||changePswd){this.personalInformationMenu=new Ext.ux.GroupTab({expanded:true,items:[{title:Openwis.i18n("Admin.Browser.PersonalInformation"),tabTip:Openwis.i18n("Admin.Browser.PersonalInformation")}]});
if(userInfoAccessible){this.personalInformationMenu.add(this.getPersonalInformationUserInformationMenu())
}if(changePswd){this.personalInformationMenu.add(this.getPersonalInformationChangeMyPasswordMenu())
}}}return this.personalInformationMenu
},getPersonalInformationUserInformationMenu:function(){if(!this.personalInformationUserInformationMenu){this.personalInformationUserInformationMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.PersonalInformation.UserInfo"),listeners:{activate:function(ct){ct.add(new Openwis.Common.User.UserInformation({hidePassword:true}));
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.personalInformationUserInformationMenu
},getPersonalInformationChangeMyPasswordMenu:function(){if(!this.personalInformationChangeMyPasswordMenu){this.personalInformationChangeMyPasswordMenu=new Ext.Panel({title:Openwis.i18n("Admin.Browser.PersonalInformation.ChangeMyPassword"),listeners:{activate:function(ct){ct.add(new Openwis.Common.User.ChangePassword());
ct.doLayout()
},deactivate:function(ct){ct.remove(ct.items.first(),true)
}}})
}return this.personalInformationChangeMyPasswordMenu
}});Ext.ns("Openwis.Admin");
Openwis.Admin.Viewport=Ext.extend(Ext.Viewport,{initComponent:function(){Ext.apply(this,{border:false,layout:"fit",autoScroll:true,listeners:{afterlayout:function(){this.relayoutViewport(true,true)
},scope:this}});
Openwis.Admin.Viewport.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.getViewportPanel().add(this.getWestPanel());
this.getCenterPanel().add(this.getHeaderPanel());
if(g_userConnected){this.getContentPanel().add(this.getBrowserPanel())
}else{this.getContentPanel().add(new Ext.Panel({border:false,style:{textAlign:"center",marginTop:"30px"},html:Openwis.i18n("AdminHomePage.Main.Content")}))
}this.getCenterPanel().add(this.getContentPanel());
this.getViewportPanel().add(this.getCenterPanel());
this.getViewportPanel().add(this.getEastPanel());
this.add(this.getViewportPanel())
},getViewportPanel:function(){if(!this.viewportPanel){this.viewportPanel=new Ext.Panel({layout:"border",border:false,autoScroll:false,cls:"viewportCls"})
}return this.viewportPanel
},getContentPanel:function(){if(!this.contentPanel){this.contentPanel=new Ext.Panel({region:"center",border:false,layout:"fit"})
}return this.contentPanel
},getBrowserPanel:function(){if(!this.browserPanel){this.browserPanel=new Openwis.Admin.Browser({listeners:{tabchange:function(){this.relayoutViewport(true,true)
},groupchange:function(){this.relayoutViewport(true,true)
},panelInitialized:function(){this.relayoutViewport(true,true)
},guiChanged:function(){this.relayoutViewport(true,true)
},scope:this}})
}return this.browserPanel
},getHeaderPanel:function(){if(!this.headerPanel){this.headerPanel=new Ext.Container({region:"north",border:false,contentEl:"header",cls:"headerCtCls"})
}return this.headerPanel
},getCenterPanel:function(){if(!this.centerPanel){this.centerPanel=new Ext.Panel({cls:"body-center-panel",region:"center",border:false,width:993,layout:"border"})
}return this.centerPanel
},getWestPanel:function(){if(!this.westPanel){this.westPanel=new Ext.Container({cls:"body-west-panel",region:"west",border:false,html:"&nbsp;"})
}return this.westPanel
},getEastPanel:function(){if(!this.eastPanel){this.eastPanel=new Ext.Container({cls:"body-east-panel",region:"east",border:false,html:"&nbsp;"})
}return this.eastPanel
},relayoutViewport:function(relayoutWidth,relayoutHeight){this.suspendEvents();
var browserPanelHeight=350;
if(g_userConnected){var headerHeight=this.getBrowserPanel().header.getHeight();
var activeTab=this.getBrowserPanel().activeGroup.activeTab;
if(activeTab){var items=activeTab.items;
var activeComp=activeTab.items.items[0];
if(activeComp&&activeComp.getResizeEl()){browserPanelHeight=activeComp.getHeight()
}}var bodyHeight=this.getHeight();
if(headerHeight>browserPanelHeight){browserPanelHeight=headerHeight
}}var height=browserPanelHeight+150;
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
var adminViewport=new Openwis.Admin.Viewport()
});Ext.ns("Openwis.Admin.System.SystemConfiguration");
Openwis.Admin.System.SystemConfiguration=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.System.SystemConfiguration.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.system.configuration.form",params:{},listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.getSystemConfigurationFormPanel().add(this.getServerFieldSet());
this.getSystemConfigurationFormPanel().add(this.getIndexFieldSet());
this.getSystemConfigurationFormPanel().add(this.getZ39ServerFieldSet());
this.getSystemConfigurationFormPanel().add(this.getXlinkResolverFieldSet());
this.getSystemConfigurationFormPanel().add(this.getCswFieldSet());
this.getSystemConfigurationFormPanel().add(this.getInspireFieldSet());
this.getSystemConfigurationFormPanel().add(this.getProxyFieldSet());
this.getSystemConfigurationFormPanel().add(this.getFeedBackFieldSet());
this.getSystemConfigurationFormPanel().add(this.getAuthenticationFieldSet());
this.add(this.getSystemConfigurationFormPanel());
this.doLayout();
this.fireEvent("panelInitialized")
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("SystemConfiguration.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getSystemConfigurationFormPanel:function(){if(!this.systemConfigurationFormPanel){this.systemConfigurationFormPanel=new Ext.form.FormPanel({itemCls:"formItems",border:false,buttons:[{text:Openwis.i18n("Common.Btn.Save"),handler:function(btn,e){if(this.getSystemConfigurationFormPanel().getForm().isValid()){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.system.configuration",params:this.getSystemConfigInfos(),listeners:{success:function(config){},scope:this}});
saveHandler.proceed()
}},scope:this}]})
}return this.systemConfigurationFormPanel
},getSiteFieldSet:function(){if(!this.siteFieldSet){this.siteFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Site.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.siteFieldSet.add(this.getSiteNameTextField())
}return this.siteFieldSet
},getSiteNameTextField:function(){if(!this.siteNameTextField){this.siteNameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Site.Name"),allowBlank:false,border:false,disabled:true,width:220,value:this.config.siteName,style:{margin:"0px 0px 5px 0px"}})
}return this.siteNameTextField
},getServerFieldSet:function(){if(!this.serverFieldSet){this.serverFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Server.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.serverFieldSet.add(this.getServerHostTextField());
this.serverFieldSet.add(this.getServerPortTextField())
}return this.serverFieldSet
},getServerHostTextField:function(){if(!this.serverHostTextField){this.serverHostTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Server.Host.Name"),allowBlank:false,border:false,width:220,value:this.config.serverHost,style:{margin:"0px 0px 5px 0px"}})
}return this.serverHostTextField
},getServerPortTextField:function(){if(!this.serverPortTextField){this.serverPortTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Server.Port.Name"),border:false,width:220,value:this.config.serverPort,style:{margin:"0px 0px 5px 0px"}})
}return this.serverPortTextField
},getIndexFieldSet:function(){if(!this.indexFieldSet){this.indexFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Index.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.indexFieldSet.add(this.getIndexEnableCheckBox());
this.indexFieldSet.add(this.getIndexRunAtFormPanel());
this.indexFieldSet.add(this.getIndexRunAgainCombobox());
if(!this.config.indexEnable){this.getIndexRunAtFormPanel().hide();
this.getIndexRunAgainCombobox().hide()
}}return this.indexFieldSet
},getIndexEnableCheckBox:function(){if(!this.indexEnableCheckBox){this.indexEnableCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Index.Enable"),allowBlank:false,checked:this.config.indexEnable,width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getIndexRunAtFormPanel().hide();
this.getIndexRunAgainCombobox().hide()
}else{this.getIndexRunAtFormPanel().show();
this.getIndexRunAgainCombobox().show()
}},scope:this}})
}return this.indexEnableCheckBox
},getIndexRunAtFormPanel:function(){if(!this.indexRunAtFormPanel){this.indexRunAtFormPanel=new Ext.Panel({fieldLabel:Openwis.i18n("SystemConfiguration.Index.RunAt"),layout:"table",defaults:{style:{width:"100%"}},layoutConfig:{columns:4},border:false});
this.indexRunAtFormPanel.add(this.getIndexRunAtHourComboBox());
this.indexRunAtFormPanel.add(new Ext.form.Label({text:" : "}));
this.indexRunAtFormPanel.add(this.getIndexRunAtMinuteComboBox());
this.indexRunAtFormPanel.add(this.getIndexRunAtHelpLabel())
}return this.indexRunAtFormPanel
},getIndexRunAtHourComboBox:function(){if(!this.indexRunAtHourComboBox){this.indexRunAtHourComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["0",0],["1",1],["2",2],["3",3],["4",4],["5",5],["6",6],["7",7],["8",8],["9",9],["10",10],["11",11],["12",12],["13",13],["14",14],["15",15],["16",16],["17",17],["18",18],["19",19],["20",20],["21",21],["22",22],["23",23]]}),style:{margin:"0px 0px 5px 0px"},allowBlank:false,name:"runAtHour",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,editable:false,allowBlank:false,width:40,displayField:"value",valueField:"id",value:this.config.indexRunAtHour})
}return this.indexRunAtHourComboBox
},getIndexRunAtMinuteComboBox:function(){if(!this.indexRunAtMinuteComboBox){this.indexRunAtMinuteComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["0",0],["15",15],["30",30],["45",45]]}),allowBlank:false,name:"runAtMinute",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,editable:false,allowBlank:false,width:40,displayField:"value",valueField:"id",value:this.config.indexRunAtMinute})
}return this.indexRunAtMinuteComboBox
},getIndexRunAtHelpLabel:function(){if(!this.indexRunAtHelpLabel){this.indexRunAtHelpLabel=new Ext.form.Label({text:Openwis.i18n("SystemConfiguration.Index.RunAt.help"),labelStyle:"font-weight:bold;"})
}return this.indexRunAtHelpLabel
},getIndexRunAgainCombobox:function(){if(!this.indexRunAgainCombobox){this.indexRunAgainCombobox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["1","1 hour (not recommended)"],["3","3 hours"],["6","6 hours"],["12","12 hours"],["24","1 day"],["48","2 days"],["72","3 days"],["96","4 days"]]}),allowBlank:false,fieldLabel:Openwis.i18n("SystemConfiguration.Index.RunAgain"),name:"runAgain",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,editable:false,allowBlank:false,width:200,displayField:"value",valueField:"id",value:this.config.indexRunAgain})
}return this.indexRunAgainCombobox
},getZ39ServerFieldSet:function(){if(!this.z39ServerFieldSet){this.z39ServerFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Z39Server.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.z39ServerFieldSet.add(this.getZ39ServerEnableCheckBox());
this.z39ServerFieldSet.add(this.getZ39ServerPortTextField());
if(!this.config.z3950ServerEnable){this.getZ39ServerPortTextField().hide()
}}return this.z39ServerFieldSet
},getZ39ServerEnableCheckBox:function(){if(!this.z39ServerEnableCheckBox){this.z39ServerEnableCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Z39Server.Enable"),allowBlank:false,checked:this.config.z3950ServerEnable,width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getZ39ServerPortTextField().hide()
}else{this.getZ39ServerPortTextField().show()
}},scope:this}})
}return this.z39ServerEnableCheckBox
},getZ39ServerPortTextField:function(){if(!this.z39ServerPortTextField){this.z39ServerPortTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Z39Server.Port"),border:false,width:220,value:this.config.z3950ServerPort,style:{margin:"0px 0px 5px 0px"}})
}return this.z39ServerPortTextField
},getXlinkResolverFieldSet:function(){if(!this.xlinkResolverFieldSet){this.xlinkResolverFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Xlink.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.xlinkResolverFieldSet.add(this.getXlinkResolverEnableCheckBox())
}return this.xlinkResolverFieldSet
},getXlinkResolverEnableCheckBox:function(){if(!this.xlinkResolverEnableCheckBox){this.xlinkResolverEnableCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Xlink.Enable"),allowBlank:false,checked:this.config.xlinkResolverEnable,width:125})
}return this.xlinkResolverEnableCheckBox
},getCswFieldSet:function(){if(!this.cswFieldSet){this.cswFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Csw.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.cswFieldSet.add(this.getCswEnableCheckBox());
this.cswFieldSet.add(this.getCswTitleFormTextField());
this.cswFieldSet.add(this.getCswAbstractTextField());
this.cswFieldSet.add(this.getCswFeesTextField());
this.cswFieldSet.add(this.getCswAccessTextField())
}return this.cswFieldSet
},getCswEnableCheckBox:function(){if(!this.cswEnableCheckBox){this.cswEnableCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Csw.Enable"),allowBlank:false,checked:this.config.cswEnable,width:125})
}return this.cswEnableCheckBox
},getCswContactCombobox:function(){if(!this.cswContactComboBox){var cswContactStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"id",fields:[{name:"id"},{name:"name"}]});
this.cswContactComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("SystemConfiguration.Csw.Contact"),name:"cswContact",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:cswContactStore,editable:false,allowBlank:false,width:330,displayField:"name",valueField:"id"});
this.cswContactComboBox.getStore().loadData(this.config.cswContactAllUsers)
}return this.categoriesComboBox
},getCswTitleFormTextField:function(){if(!this.cswTitleFormTextField){this.cswTitleFormTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Csw.TitleForm"),border:false,width:220,value:this.config.cswTitle,style:{margin:"0px 0px 5px 0px"}})
}return this.cswTitleFormTextField
},getCswAbstractTextField:function(){if(!this.cswAbstractTextField){this.cswAbstractTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Csw.Abstract"),border:false,width:220,value:this.config.cswAbstract,style:{margin:"0px 0px 5px 0px"}})
}return this.cswAbstractTextField
},getCswFeesTextField:function(){if(!this.cswFeesTextField){this.cswFeesTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Csw.Fees"),border:false,width:220,value:this.config.cswFees,style:{margin:"0px 0px 5px 0px"}})
}return this.cswFeesTextField
},getCswAccessTextField:function(){if(!this.cswAccessTextField){this.cswAccessTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Csw.Access"),border:false,width:220,value:this.config.cswAccess,style:{margin:"0px 0px 5px 0px"}})
}return this.cswAccessTextField
},getInspireFieldSet:function(){if(!this.inspireFieldSet){this.inspireFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Inspire.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.inspireFieldSet.add(this.getInspireEnableCheckBox())
}return this.inspireFieldSet
},getInspireEnableCheckBox:function(){if(!this.inspireEnableCheckBox){this.inspireEnableCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Inspire.Enable"),allowBlank:false,checked:this.config.inspireEnable,width:125})
}return this.inspireEnableCheckBox
},getProxyFieldSet:function(){if(!this.proxyFieldSet){this.proxyFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Proxy.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.proxyFieldSet.add(this.getProxyUseCheckBox());
this.proxyFieldSet.add(this.getProxyHostTextField());
this.proxyFieldSet.add(this.getProxyPortTextField());
this.proxyFieldSet.add(this.getProxyUserNameTextField());
this.proxyFieldSet.add(this.getProxyPasswordTextField());
if(!this.config.proxyUse){this.getProxyHostTextField().hide();
this.getProxyPortTextField().hide();
this.getProxyUserNameTextField().hide();
this.getProxyPasswordTextField().hide()
}}return this.proxyFieldSet
},getProxyUseCheckBox:function(){if(!this.proxyUseCheckBox){this.proxyUseCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Proxy.Use"),allowBlank:false,checked:this.config.proxyUse,width:125,listeners:{check:function(checkbox,checked){if(!checked){this.getProxyHostTextField().hide();
this.getProxyPortTextField().hide();
this.getProxyUserNameTextField().hide();
this.getProxyPasswordTextField().hide()
}else{this.getProxyHostTextField().show();
this.getProxyPortTextField().show();
this.getProxyUserNameTextField().show();
this.getProxyPasswordTextField().show()
}},scope:this}})
}return this.proxyUseCheckBox
},getProxyHostTextField:function(){if(!this.proxyHostTextField){this.proxyHostTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Proxy.Host"),border:false,width:220,value:this.config.proxyHost,style:{margin:"0px 0px 5px 0px"}})
}return this.proxyHostTextField
},getProxyPortTextField:function(){if(!this.proxyPortTextField){this.proxyPortTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Proxy.Port"),border:false,width:220,value:this.config.proxyPort,style:{margin:"0px 0px 5px 0px"}})
}return this.proxyPortTextField
},getProxyUserNameTextField:function(){if(!this.proxyUserNameTextField){this.proxyUserNameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Proxy.UserName"),border:false,width:220,value:this.config.proxyUserName,style:{margin:"0px 0px 5px 0px"}})
}return this.proxyUserNameTextField
},getProxyPasswordTextField:function(){if(!this.proxyPasswordTextField){this.proxyPasswordTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Proxy.Password"),border:false,width:220,value:this.config.proxyPassword,style:{margin:"0px 0px 5px 0px"}})
}return this.proxyPasswordTextField
},getFeedBackFieldSet:function(){if(!this.feedBackFieldSet){this.feedBackFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Feedback.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.feedBackFieldSet.add(this.getFeedBackEmailTextField());
this.feedBackFieldSet.add(this.getFeedBackSmtpHostTextField());
this.feedBackFieldSet.add(this.getFeedBackSmtpPortTextField())
}return this.feedBackFieldSet
},getFeedBackEmailTextField:function(){if(!this.feedBackEmailTextField){this.feedBackEmailTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Feedback.mail"),border:false,width:220,value:this.config.feedBackEmail,style:{margin:"0px 0px 5px 0px"}})
}return this.feedBackEmailTextField
},getFeedBackSmtpHostTextField:function(){if(!this.feedBackSmtpHostTextField){this.feedBackSmtpHostTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Feedback.SMTP.Host"),border:false,width:220,value:this.config.feedBackSmtpHost,style:{margin:"0px 0px 5px 0px"}})
}return this.feedBackSmtpHostTextField
},getFeedBackSmtpPortTextField:function(){if(!this.feedBackSmtpPortTextField){this.feedBackSmtpPortTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("SystemConfiguration.Feedback.SMTP.Port"),border:false,width:220,value:this.config.feedBackSmtpPort,style:{margin:"0px 0px 5px 0px"}})
}return this.feedBackSmtpPortTextField
},getAuthenticationFieldSet:function(){if(!this.authenticationFieldSet){this.authenticationFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("SystemConfiguration.Authentication.Title"),autoHeight:true,collapsed:false,collapsible:true});
this.authenticationFieldSet.add(this.getUserSelfRegistrationEnableCheckBox())
}return this.authenticationFieldSet
},getUserSelfRegistrationEnableCheckBox:function(){if(!this.userSelfRegistrationEnableCheckBox){this.userSelfRegistrationEnableCheckBox=new Ext.form.Checkbox({fieldLabel:Openwis.i18n("SystemConfiguration.Authentication.UserSelfRegistration.Enable"),allowBlank:false,checked:this.config.userSelfRegistrationEnable,width:125})
}return this.userSelfRegistrationEnableCheckBox
},getSystemConfigInfos:function(){var systemConfigInfos={};
systemConfigInfos.siteName=this.getSiteNameTextField().getValue();
systemConfigInfos.serverHost=this.getServerHostTextField().getValue();
systemConfigInfos.serverPort=this.getServerPortTextField().getValue();
systemConfigInfos.indexEnable=this.getIndexEnableCheckBox().getValue();
systemConfigInfos.indexRunAtHour=this.getIndexRunAtHourComboBox().getValue();
systemConfigInfos.indexRunAtMinute=this.getIndexRunAtMinuteComboBox().getValue();
systemConfigInfos.indexRunAgain=this.getIndexRunAgainCombobox().getValue();
systemConfigInfos.z3950ServerEnable=this.getZ39ServerEnableCheckBox().getValue();
systemConfigInfos.z3950ServerPort=this.getZ39ServerPortTextField().getValue();
systemConfigInfos.xlinkResolverEnable=this.getXlinkResolverEnableCheckBox().getValue();
systemConfigInfos.cswEnable=this.getCswEnableCheckBox().getValue();
systemConfigInfos.cswTitle=this.getCswTitleFormTextField().getValue();
systemConfigInfos.cswAbstract=this.getCswAbstractTextField().getValue();
systemConfigInfos.cswFees=this.getCswFeesTextField().getValue();
systemConfigInfos.cswAccess=this.getCswAccessTextField().getValue();
systemConfigInfos.inspireEnable=this.getInspireEnableCheckBox().getValue();
systemConfigInfos.proxyUse=this.getProxyUseCheckBox().getValue();
systemConfigInfos.proxyHost=this.getProxyHostTextField().getValue();
systemConfigInfos.proxyPort=this.getProxyPortTextField().getValue();
systemConfigInfos.proxyUserName=this.getProxyUserNameTextField().getValue();
systemConfigInfos.proxyPassword=this.getProxyPasswordTextField().getValue();
systemConfigInfos.feedBackEmail=this.getFeedBackEmailTextField().getValue();
systemConfigInfos.feedBackSmtpHost=this.getFeedBackSmtpHostTextField().getValue();
systemConfigInfos.feedBackSmtpPort=this.getFeedBackSmtpPortTextField().getValue();
systemConfigInfos.userSelfRegistrationEnable=this.getUserSelfRegistrationEnableCheckBox().getValue();
return systemConfigInfos
}});Ext.ns("Openwis.Admin.Availability.DeploymentAvailabilityUtils");
Openwis.Admin.Availability.DeploymentAvailabilityUtils.simpleAvailabilityRenderer=function(availability){return Openwis.i18n("Availability.Level."+availability.level)
};
Openwis.Admin.Availability.DeploymentAvailabilityUtils.harvestingAvailabilityRenderer=function(availability){var msg=Openwis.i18n("Availability.Level."+availability.level);
if(availability.additionalInfo){msg+=" "+Openwis.i18n("Availability.Harvesting",availability.additionalInfo)
}return msg
};
Openwis.Admin.Availability.DeploymentAvailabilityUtils.getAvailabilityRenderer=function(availability,serviceName){if((serviceName=="synchronization"||serviceName=="harvesting")&&Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(availability)){return Openwis.Admin.Availability.DeploymentAvailabilityUtils.harvestingAvailabilityRenderer(availability)
}else{return Openwis.Admin.Availability.DeploymentAvailabilityUtils.simpleAvailabilityRenderer(availability)
}};
Openwis.Admin.Availability.DeploymentAvailabilityUtils.clsAvailability=function(availability){if(availability.level=="UP"){return"availabilityLevelUp"
}else{if(availability.level=="WARN"){return"availabilityLevelWarn"
}else{if(availability.level=="DOWN"){return"availabilityLevelDown"
}else{if(availability.level=="UNKNOWN"){return"availabilityLevelUnknown"
}else{if(availability.level=="STOPPED"){return"availabilityLevelStopped"
}else{if(availability.level=="ALL_SUSPENDED"){return"availabilityLevelAllSuspended"
}else{if(availability.level=="NONE"){return"availabilityLevelNone"
}}}}}}}};
Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted=function(service){return service.level=="UP"||service.level=="WARN"||service.level=="DOWN"||service.level=="UNKNOWN"
};
Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceEnabled=function(service){return service.level!="NONE"
};
Openwis.Admin.Availability.DeploymentAvailabilityUtils.isStartStopButtonDisplayed=function(local,serviceType,serviceName,service){var displayButton=false;
if(local){if(serviceType=="Data"){displayButton=true
}else{if(serviceType=="Metadata"){if(serviceName!="indexing"&&Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceEnabled(service)){displayButton=true
}}}}return displayButton
};Ext.ns("Openwis.Admin.Availability");
Openwis.Admin.Availability.DeploymentAvailability=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Availability.DeploymentAvailability.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.getAvailabilityFormPanel().add(this.getMetadataServiceFieldSet());
this.getAvailabilityFormPanel().add(this.getDataServiceFieldSet());
this.getAvailabilityFormPanel().add(this.getSecurityServiceFieldSet());
this.add(this.getAvailabilityFormPanel());
this.doLayout();
this.fireEvent("panelInitialized")
},getAvailabilityFormPanel:function(){if(!this.availabilityFormPanel){this.availabilityFormPanel=new Ext.form.FormPanel({itemCls:"formItems",border:false,labelWidth:150})
}return this.availabilityFormPanel
},getMetadataServiceFieldSet:function(){if(!this.metadataServiceFieldSet){this.metadataServiceFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Availability.MetadataService"),autoHeight:true,collapsed:false,collapsible:true,items:[this.getServicePanel(this.config.metadataServiceAvailability.userPortal,Openwis.i18n("Availability.MetadataService.UserPortal"),"userPortal","Metadata"),this.getServicePanel(this.config.metadataServiceAvailability.synchronization,Openwis.i18n("Availability.MetadataService.Synchro"),"synchronization","Metadata"),this.getServicePanel(this.config.metadataServiceAvailability.harvesting,Openwis.i18n("Availability.MetadataService.Harvesting"),"harvesting","Metadata"),this.getServicePanel(this.config.metadataServiceAvailability.indexing,Openwis.i18n("Availability.MetadataService.Indexing"),"indexing","Metadata")]})
}return this.metadataServiceFieldSet
},getDataServiceFieldSet:function(){if(!this.dataServiceFieldSet){this.dataServiceFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Availability.DataService"),autoHeight:true,collapsed:false,collapsible:true,items:[this.getServicePanel(this.config.dataServiceAvailability.replicationProcess,Openwis.i18n("Availability.DataService.ReplicationProcess"),"replication","Data"),this.getServicePanel(this.config.dataServiceAvailability.ingestion,Openwis.i18n("Availability.DataService.Ingestion"),"ingestion","Data"),this.getServicePanel(this.config.dataServiceAvailability.subscriptionQueue,Openwis.i18n("Availability.DataService.SubscriptionQueue"),"subscriptionProcessing","Data"),this.getServicePanel(this.config.dataServiceAvailability.disseminationQueue,Openwis.i18n("Availability.DataService.DisseminationQueue"),"dissemination","Data")]})
}return this.dataServiceFieldSet
},getSecurityServiceFieldSet:function(){if(!this.securityServiceFieldSet){this.securityServiceFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("Availability.SecurityService"),autoHeight:true,collapsed:false,collapsible:true,items:[this.getServicePanel(this.config.securityServiceAvailability.securityService,Openwis.i18n("Availability.SecurityService"),"Security"),this.getServicePanel(this.config.securityServiceAvailability.ssoService,Openwis.i18n("Availability.SecurityService.SSO"),"Security")]})
}return this.securityServiceFieldSet
},getServicePanel:function(service,serviceLabel,serviceName,serviceType){var value=Openwis.Admin.Availability.DeploymentAvailabilityUtils.getAvailabilityRenderer(service,serviceName);
var serviceAvailability={xtype:"displayfield",cls:Openwis.Admin.Availability.DeploymentAvailabilityUtils.clsAvailability(service),value:value,fieldLabel:serviceLabel,width:420};
var items={};
if(Openwis.Admin.Availability.DeploymentAvailabilityUtils.isStartStopButtonDisplayed(this.local,serviceType,serviceName,service)){items=[serviceAvailability,new Ext.Button(this.getStartStopServiceAction(serviceName,service,serviceType))]
}else{items=[serviceAvailability]
}var servicePanel=new Ext.form.CompositeField({items:items});
return servicePanel
},getStartStopServiceAction:function(serviceName,service,serviceType){var params={};
params.serviceName=serviceName;
var text={};
if(Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(service)){text="Stop"
}else{text="Start"
}var url={};
if(serviceType=="Metadata"){url=configOptions.locService+"/xml.backup.start.stop.metadata.service"
}else{url=configOptions.locService+"/xml.backup.start.stop.data.service"
}this.startStopMetadataServiceAction=new Ext.Action({text:text,handler:function(){params.started=Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(service);
new Openwis.Handler.Get({button:this,url:url,params:params,listeners:{success:function(config){if(Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(config)){this.setText("Stop")
}else{this.setText("Start")
}service=config;
this.ownerCt.items.items[0].setValue(Openwis.Admin.Availability.DeploymentAvailabilityUtils.getAvailabilityRenderer(config,serviceName));
this.ownerCt.items.items[0].removeClass("availabilityLevelUp");
this.ownerCt.items.items[0].removeClass("availabilityLevelWarn");
this.ownerCt.items.items[0].removeClass("availabilityLevelDown");
this.ownerCt.items.items[0].removeClass("availabilityLevelUnknown");
this.ownerCt.items.items[0].removeClass("availabilityLevelStopped");
this.ownerCt.items.items[0].removeClass("availabilityLevelAllSuspended");
this.ownerCt.items.items[0].removeClass("availabilityLevelNone");
this.ownerCt.items.items[0].addClass(Openwis.Admin.Availability.DeploymentAvailabilityUtils.clsAvailability(config));
this.ownerCt.items.items[0].show()
},scope:this}}).proceed()
}});
return this.startStopMetadataServiceAction
}});Ext.ns("Openwis.Admin.Availability");
Openwis.Admin.Availability.LocalAvailability=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Availability.LocalAvailability.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.avalaibility.get",params:{},listeners:{success:function(config){this.config=config;
this.initialize()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.add(this.getDeploymentAvailabilityPanel());
this.doLayout();
this.fireEvent("panelInitialized")
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Availability.Local.Title"),cls:"administrationTitle1"})
}return this.header
},getDeploymentAvailabilityPanel:function(){if(!this.deploymentAvailabilityPanel){this.deploymentAvailabilityPanel=new Openwis.Admin.Availability.DeploymentAvailability({config:this.config,local:true})
}return this.deploymentAvailabilityPanel
}});Ext.ns("Openwis.Admin.Availability");
Openwis.Admin.Availability.RemoteAvailability=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Availability.RemoteAvailability.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getDeploymentsComboBox());
this.doLayout();
this.fireEvent("panelInitialized")
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Availability.Remote.Title"),cls:"administrationTitle1"})
}return this.header
},getDeploymentsComboBox:function(){if(!this.deploymentsComboBox){var deploymentStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.all.backup.centres",idProperty:"name",fields:[{name:"name"}]});
this.deploymentsComboBox=new Ext.form.ComboBox({store:deploymentStore,valueField:"name",displayField:"name",name:"deployment",emptyText:Openwis.i18n("TrackMySubscriptions.Remote.Select.Deployment"),typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200,listeners:{select:function(combo,record,index){var logicalRemoteDeploymentName=this.getDeploymentsComboBox().getValue();
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.avalaibility.remote.get",params:{content:logicalRemoteDeploymentName},listeners:{success:function(config){if(this.deploymentAvailabilityPanel){this.remove(this.deploymentAvailabilityPanel);
this.deploymentAvailabilityPanel=null;
this.remove(this.switchToBackupModeInfo);
this.switchToBackupModeInfo=null;
this.remove(this.switchToBackupModeButton);
this.switchToBackupModeButton=null
}this.deploymentAvailabilityPanel=new Openwis.Admin.Availability.DeploymentAvailability({config:config.deploymentAvailability});
this.add(this.getDeploymentAvailabilityPanel());
this.getSwitchToBackupModeInfo().setInitialState(config.backupedByLocalServer);
this.add(this.getSwitchToBackupModeInfo());
this.getSwitchToBackupModeButton().setInitialState(config.backupedByLocalServer);
this.add(this.getRetroProcessHourContainer());
if(!this.retroProcessHourField){this.getRetroProcessHourContainer().add(new Ext.form.Label({html:Openwis.i18n("Availability.Remote.retro.process.start")}));
this.getRetroProcessHourContainer().add(this.getRetroProcessHourField());
this.getRetroProcessHourContainer().add(new Ext.form.Label({html:Openwis.i18n("Availability.Remote.retro.process.end")}))
}this.getRetroProcessHourContainer().setInitialState(config.backupedByLocalServer);
this.add(this.getSwitchToBackupModeButton());
this.doLayout();
this.fireEvent("panelInitialized")
},scope:this}});
getHandler.proceed()
},scope:this}})
}return this.deploymentsComboBox
},getDeploymentAvailabilityPanel:function(){if(!this.deploymentAvailabilityPanel){this.deploymentAvailabilityPanel=new Openwis.Admin.Availability.DeploymentAvailability({config:this.config,local:false})
}return this.deploymentAvailabilityPanel
},getSwitchToBackupModeInfo:function(){if(!this.switchToBackupModeInfo){this.switchToBackupModeInfo=new Ext.Container({border:false,setInitialState:function(isBackupSwitchedOn){if(isBackupSwitchedOn){this.html=Openwis.i18n("Availability.Remote.SwitchToBackupInfo.BackupModeOn");
this.addClass("backupModeOn");
this.removeClass("backupModeOff")
}else{this.html=Openwis.i18n("Availability.Remote.SwitchToBackupInfo.BackupModeOff");
this.addClass("backupModeOff");
this.removeClass("backupModeOn")
}}})
}return this.switchToBackupModeInfo
},getSwitchToBackupModeButton:function(){if(!this.switchToBackupModeButton){this.switchToBackupModeButton=new Ext.Button({text:Openwis.i18n("Availability.Remote.SwitchToBackupBtn"),scope:this,handler:function(){if(!this.getRetroProcessHourField().isValid()){Ext.Msg.alert("Error",this.getRetroProcessHourField().invalidText);
return
}var deploymentName=this.getDeploymentsComboBox().getValue();
var isBackupSwitchedOn=this.getSwitchToBackupModeButton().isBackupSwitchedOn;
var msg="";
if(isBackupSwitchedOn){msg=Openwis.i18n("Availability.Remote.SwitchToBackupTurnOffConfirmation",{deploymentName:deploymentName})
}else{msg=Openwis.i18n("Availability.Remote.SwitchToBackupTurnOnConfirmation",{deploymentName:deploymentName})
}var h=this.getRetroProcessHourField().getValue();
if(!h){h=2
}Ext.MessageBox.confirm(Openwis.i18n("Common.Confirm.Title"),msg,function(btnClicked){if(btnClicked=="yes"){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.avalaibility.switch.to.backup",params:{deploymentName:deploymentName,switchedOn:!isBackupSwitchedOn,hour:h},listeners:{success:function(config){this.getDeploymentsComboBox().fireEvent("select")
},scope:this}});
saveHandler.proceed()
}},this)
},setInitialState:function(isBackupSwitchedOn){this.isBackupSwitchedOn=isBackupSwitchedOn;
if(isBackupSwitchedOn){this.setText(Openwis.i18n("Availability.Remote.SwitchToBackupOffBtn"))
}else{this.setText(Openwis.i18n("Availability.Remote.SwitchToBackupOnBtn"))
}}})
}return this.switchToBackupModeButton
},getRetroProcessHourContainer:function(){if(!this.retroProcessHourContainer){this.retroProcessHourContainer=new Ext.Container({border:false,setInitialState:function(isBackupSwitchedOn){if(isBackupSwitchedOn){this.disable()
}else{this.enable()
}}})
}return this.retroProcessHourContainer
},getRetroProcessHourField:function(){if(!this.retroProcessHourField){this.retroProcessHourField=new Ext.form.NumberField({width:20,name:"hour",allowDecimals:false,allowNegative:false,minValue:0,maxValue:6,value:2,setInitialState:function(isBackupSwitchedOn){if(isBackupSwitchedOn){this.disable()
}else{this.enable()
}}})
}return this.retroProcessHourField
}});Ext.ns("Openwis.Admin.Availability");
Openwis.Admin.Availability.Statistics=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Availability.Statistics.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getSearchFormPanel());
this.add(this.getAvailabilityStatsGrid());
this.add(this.getSessionCountGrid());
this.doLayout()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Availability.Statistics.Title"),cls:"administrationTitle1"})
}return this.header
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:100,border:false,buttonAlign:"center"});
this.searchFormPanel.add(this.getSearchTextField());
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchTextField:function(){if(!this.searchTextField){this.searchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Availability.Statistics.Service.Search"),name:"serviceNameFilter",enableKeyEvents:true,width:150,listeners:{keyup:function(){var searchOn=Ext.isEmpty(this.getSearchTextField().getValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getAvailabilityStatsStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getAvailabilityStatsStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}})
}return this.searchTextField
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:true,text:"Search",scope:this,handler:function(){this.getAvailabilityStatsStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getAvailabilityStatsStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:true,text:"Reset",scope:this,handler:function(){this.getSearchTextField().setValue("");
this.getAvailabilityStatsStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getAvailabilityStatsStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
this.getSearchAction().setDisabled(true);
this.getResetAction().setDisabled(true)
}})
}return this.resetAction
},createLabel:function(idVal,label,value,unit){return new Ext.Container({id:idVal,border:false,cls:"formItems",html:label+": "+value+" "+unit})
},getAvailabilityStatsGrid:function(){if(!this.availabilityStatsGrid){this.availabilityStatsGrid=new Ext.grid.GridPanel({id:"availabilityStatsGrid",height:250,border:true,store:this.getAvailabilityStatsStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Availability.Statistics.grid.date"),dataIndex:"date",sortable:true,width:100},{id:"task",header:Openwis.i18n("Availability.Statistics.grid.task"),dataIndex:"task",sortable:true,width:300},{id:"availability",header:Openwis.i18n("Availability.Statistics.grid.availability"),dataIndex:"available",sortable:false,width:100,renderer:this.availabilityRenderer}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getAvailabilityStatsStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("CatalogStatisticsManagement.All.Display.Range"),emptyMsg:Openwis.i18n("CatalogStatisticsManagement.All.No.Task")})});
this.availabilityStatsGrid.addButton(new Ext.Button(this.getExportAction()))
}return this.availabilityStatsGrid
},availabilityRenderer:function(value,metadata,record){var availability=(record.data.available*100)/(record.data.available+record.data.notAvailable);
var str=Math.round(availability)+" %";
return str
},getAvailabilityStatsStore:function(){if(!this.availabilityStatsStore){this.availabilityStatsStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.availability.getstatistics",remoteSort:true,root:"items",totalProperty:"count",fields:[{name:"date"},{name:"task",sortType:Ext.data.SortTypes.asUCString},{name:"available"},{name:"notAvailable"}],sortInfo:{field:"date",direction:"DESC"}})
}return this.availabilityStatsStore
},getExportAction:function(){if(!this.exportAction){this.exportAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getAvailabilityStatsStore().data.length>0){window.open(configOptions.locService+"/xml.availability.getstatistics?start=0&xml=true&serviceNameFilter="+this.getSearchTextField().getValue()+"&sort="+this.getAvailabilityStatsStore().sortInfo.field+"&dir="+this.getAvailabilityStatsStore().sortInfo.direction,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("CatalogStatisticsManagement.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("CatalogStatisticsManagement.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportAction
},getSessionCountGrid:function(){if(!this.sessionCountGrid){this.sessionCountGrid=new Ext.grid.GridPanel({id:"sessionCountGrid",height:250,border:true,store:this.getSessionCountStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("Availability.Statistics.SessionCount.grid.date"),dataIndex:"date",sortable:true,width:100},{id:"totalSessions",header:Openwis.i18n("Availability.Statistics.SessionCount.grid.totalSessions"),dataIndex:"notAvailable",sortable:true,width:150},{id:"authenticatedSessions",header:Openwis.i18n("Availability.Statistics.SessionCount.grid.authenticatedSessions"),dataIndex:"available",sortable:true,width:180}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getSessionCountStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("CatalogStatisticsManagement.All.Display.Range"),emptyMsg:Openwis.i18n("CatalogStatisticsManagement.All.No.Task")})});
this.sessionCountGrid.addButton(new Ext.Button(this.getExportSessionCountAction()))
}return this.sessionCountGrid
},getSessionCountStore:function(){if(!this.sessionCountStore){this.sessionCountStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.availability.getstatistics",remoteSort:true,root:"items",totalProperty:"count",fields:[{name:"date"},{name:"task",sortType:Ext.data.SortTypes.asUCString},{name:"available"},{name:"notAvailable"}],sortInfo:{field:"date",direction:"DESC"}});
this.sessionCountStore.setBaseParam("sessionCount","true")
}return this.sessionCountStore
},getExportSessionCountAction:function(){if(!this.exportSessionCountAction){this.exportSessionCountAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getSessionCountStore().data.length>0){window.open(configOptions.locService+"/xml.availability.getstatistics?start=0&xml=true&sessionCount=true&sort="+this.getSessionCountStore().sortInfo.field+"&dir="+this.getSessionCountStore().sortInfo.direction,"_blank","")
}else{Ext.Msg.show({title:Openwis.i18n("CatalogStatisticsManagement.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("CatalogStatisticsManagement.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportSessionCountAction
}});Ext.ns("Openwis.Admin.Index");
Openwis.Admin.Index.Manage=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Index.Manage.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.getIndexFormPanel().addButton(new Ext.Button(this.getRebuildAction()));
this.getIndexFormPanel().addButton(new Ext.Button(this.getOptimizeAction()));
this.add(this.getIndexFormPanel());
this.doLayout()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Index.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getRebuildAction:function(){if(!this.rebuildAction){this.rebuildAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Rebuild"),scope:this,handler:function(){var getHandler=new Openwis.Handler.Index({url:configOptions.locService+"/metadata.admin.index.rebuild",params:{}});
getHandler.proceed()
}})
}return this.rebuildAction
},getOptimizeAction:function(){if(!this.optimizeAction){this.optimizeAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Optimize"),scope:this,handler:function(){var getHandler=new Openwis.Handler.Index({url:configOptions.locService+"/metadata.admin.index.optimize",params:{}});
getHandler.proceed()
}})
}return this.optimizeAction
},getIndexFormPanel:function(){if(!this.indexFormPanel){this.indexFormPanel=new Ext.form.FormPanel({itemCls:"formItems",border:false})
}return this.indexFormPanel
}});Ext.ns("Openwis.Admin.CatalogStatistics");
Openwis.Admin.CatalogStatistics.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.CatalogStatistics.All.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var params={};
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.catalogstatistics.get",params:params,listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){alert("failed")
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
this.add(this.createLabel("sizeLabel",Openwis.i18n("CatalogStatisticsManagement.size"),this.config.catalogSize,""));
this.add(this.createLabel("numberLabel",Openwis.i18n("CatalogStatisticsManagement.numberMetadata"),this.config.nbMetadata,""));
this.add(this.getSearchFormPanel());
this.add(this.getCatalogGrid());
this.doLayout()
},toDoEdit:function(){this.add(this.getHeader());
var p=new Ext.Panel({vertical:true});
var totalSizeTplMarkup=[Openwis.i18n("CatalogStatisticsManagement.totalSize")+": {size}<br/>",Openwis.i18n("CatalogStatisticsManagement.totalNumber")+": {nbRecords}<br/>"];
var totalSizeTpl=new Ext.Template(totalSizeTplMarkup);
var totalSizeContainer=new Ext.Panel({id:"totalSizeContainer",border:false,tpl:totalSizeTpl,cls:"formItems",html:"",style:{padding:"5px"}});
catalogGrid.getSelectionModel().on("rowselect",function(sm,rowIdx,r){var tsContainer=Ext.getCmp("totalSizeContainer");
var records=sm.getSelections();
var data=new Object({size:0,nbRecords:0});
Ext.each(records,function(item,index,allItems){data.size+=item.get("size");
data.nbRecords+=item.get("nbRecords")
},this);
totalSizeTpl.overwrite(tsContainer.body,data)
});
catalogGrid.getSelectionModel().on("rowdeselect",function(sm,rowIdx,r){var tsContainer=Ext.getCmp("totalSizeContainer");
var data=new Object({size:0,nbRecords:0});
totalSizeTpl.overwrite(tsContainer.body,data)
});
p.add(totalSizeContainer)
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("CatalogStatisticsManagement.Administration.Title"),cls:"administrationTitle1"})
}return this.header
},getSearchFormPanel:function(){if(!this.searchFormPanel){this.searchFormPanel=new Ext.form.FormPanel({labelWidth:100,border:false,buttonAlign:"center"});
this.searchFormPanel.add(this.getSearchTextField());
this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()))
}return this.searchFormPanel
},getSearchTextField:function(){if(!this.searchTextField){this.searchTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("CatalogStatisticsManagement.Source.Search"),name:"any",enableKeyEvents:true,width:150,listeners:{keyup:function(){var searchOn=Ext.isEmpty(this.getSearchTextField().getValue().trim());
this.getSearchAction().setDisabled(searchOn);
this.getResetAction().setDisabled(searchOn);
if(searchOn){this.getAllCatalogStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getAllCatalogStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},specialkey:function(f,e){if(e.getKey()==e.ENTER){this.getSearchAction().execute()
}},scope:this}})
}return this.searchTextField
},getSearchAction:function(){if(!this.searchAction){this.searchAction=new Ext.Action({disabled:true,text:"Search",scope:this,handler:function(){this.getAllCatalogStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getAllCatalogStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}})
}return this.searchAction
},getResetAction:function(){if(!this.resetAction){this.resetAction=new Ext.Action({disabled:true,text:"Reset",scope:this,handler:function(){this.getSearchTextField().setValue("");
this.getAllCatalogStore().setBaseParam(this.getSearchTextField().getName(),this.getSearchTextField().getValue());
this.getAllCatalogStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}});
this.getSearchAction().setDisabled(true);
this.getResetAction().setDisabled(true)
}})
}return this.resetAction
},getCatalogGrid:function(){if(!this.catalogGrid){this.catalogGrid=new Ext.grid.GridPanel({id:"catalogGrid",height:200,border:true,store:this.getAllCatalogStore(),loadMask:true,columns:[{id:"date",header:Openwis.i18n("CatalogStatisticsManagement.grid.date"),dataIndex:"date",sortable:true,width:100,renderer:Openwis.Utils.Date.formatDateUTCfromLong},{id:"source",header:Openwis.i18n("CatalogStatisticsManagement.grid.source"),dataIndex:"source",sortable:true,width:300},{id:"totalSize",header:Openwis.i18n("CatalogStatisticsManagement.grid.size"),dataIndex:"totalSize",sortable:true,width:100,renderer:this.volumeRenderer},{id:"nbMetadata",header:Openwis.i18n("CatalogStatisticsManagement.grid.nbrecords"),dataIndex:"nbMetadata",sortable:true,width:100}],listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load({params:{start:0,limit:Openwis.Conf.PAGE_SIZE}})
}},bbar:new Ext.PagingToolbar({pageSize:Openwis.Conf.PAGE_SIZE,store:this.getAllCatalogStore(),displayInfo:true,beforePageText:Openwis.i18n("Common.Grid.BeforePageText"),afterPageText:Openwis.i18n("Common.Grid.AfterPageText"),firstText:Openwis.i18n("Common.Grid.FirstText"),lastText:Openwis.i18n("Common.Grid.LastText"),nextText:Openwis.i18n("Common.Grid.NextText"),prevText:Openwis.i18n("Common.Grid.PrevText"),refreshText:Openwis.i18n("Common.Grid.RefreshText"),displayMsg:Openwis.i18n("CatalogStatisticsManagement.All.Display.Range"),emptyMsg:Openwis.i18n("CatalogStatisticsManagement.All.No.Task")})});
this.catalogGrid.addButton(new Ext.Button(this.getExportAction()))
}return this.catalogGrid
},volumeRenderer:function(vol){var volKB=parseInt(vol/1024);
return volKB
},createLabel:function(idVal,label,value,unit){return new Ext.Container({id:idVal,border:false,cls:"formItems",html:label+": "+value+" "+unit})
},getAllCatalogStore:function(){if(!this.allCatalogStore){this.allCatalogStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.catalogstatistics.all",remoteSort:true,root:"allExchangedData",totalProperty:"count",fields:[{name:"date"},{name:"source",sortType:Ext.data.SortTypes.asUCString},{name:"totalSize"},{name:"nbMetadata"}],sortInfo:{field:"date",direction:"DESC"}})
}return this.allCatalogStore
},getExportAction:function(){if(!this.exportAction){this.exportAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Export"),scope:this,handler:function(){if(this.getAllCatalogStore().data.length>0){window.location.href=configOptions.locService+"/xml.catalogstatistics.export?start=0&xml=true&any="+this.getSearchTextField().getValue()+"&sort="+this.getAllCatalogStore().sortInfo.field+"&dir="+this.getAllCatalogStore().sortInfo.direction
}else{Ext.Msg.show({title:Openwis.i18n("CatalogStatisticsManagement.NoDataToExport.WarnDlg.Title"),msg:Openwis.i18n("CatalogStatisticsManagement.NoDataToExport.WarnDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.exportAction
}});Ext.ns("Openwis.Admin.Template");
Openwis.Admin.Template.All=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Admin.Template.All.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var params={};
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.template.allShema",params:params,listeners:{success:function(config){this.allShemaStore=config;
this.initialize()
},failure:function(config){alert("failed")
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
var p=new Ext.Panel({layout:"column"});
var upDownPanel=new Ext.Panel({itemCls:"formItems",border:false,vertical:true,columnWidth:0.25,style:{padding:"10px"},items:[new Ext.Button(this.getUpAction()),new Ext.Button(this.getDownAction()),new Ext.Button(this.getSaveOrderAction())]});
p.add(this.getTemplateGrid());
p.add(upDownPanel);
this.add(p);
this.doLayout();
this.getSaveOrderAction().setDisabled(1)
},getAddMenuButton:function(){if(!this.addMenuButton){this.addMenuButton=new Ext.Button({text:Openwis.i18n("TemplateManagement.Btn.Add"),menu:new Ext.menu.Menu()})
}for(var i=0;
i<this.allShemaStore.length;
i++){var menuitm=new Ext.menu.Item({text:this.allShemaStore[i].name,scope:this,handler:function(item){var msg=null;
var params={content:item.text};
var addHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.template.addDefault",params:params,confirmMsg:msg,listeners:{success:function(){this.getTemplateGrid().getStore().reload()
},scope:this}});
addHandler.proceed()
}});
var item=this.addMenuButton.menu.add(menuitm)
}return this.addMenuButton
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("TemplateManagement.Template.Title"),cls:"administrationTitle1"})
}return this.header
},getTemplateGrid:function(){if(!this.templateGrid){this.templateGrid=new Ext.grid.GridPanel({id:"templateGrid",height:400,columnWidth:0.75,border:true,store:this.getAllTemplateStore(),loadMask:true,columns:[{id:"name",header:Openwis.i18n("TemplateManagement.Name"),dataIndex:"title",sortable:false,width:200},{id:"schema",header:Openwis.i18n("TemplateManagement.Schema"),dataIndex:"schema",sortable:false,width:100}],autoExpandColumn:"name",listeners:{afterrender:function(grid){grid.loadMask.show();
grid.getStore().load()
}},sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.ownerCt.getDuplicateAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.ownerCt.getRemoveAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.ownerCt.disableUpAndDown(rowIndex)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.ownerCt.getDuplicateAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.ownerCt.getEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.ownerCt.getRemoveAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.ownerCt.disableUpAndDown(rowIndex)
}}})});
this.templateGrid.addButton(this.getAddMenuButton());
this.templateGrid.addButton(this.getDuplicateAction());
this.templateGrid.addButton(this.getEditAction());
this.templateGrid.addButton(this.getRemoveAction())
}return this.templateGrid
},disableUpAndDown:function(rowIndex){if(rowIndex==0){this.getUpAction().setDisabled(true);
this.getDownAction().setDisabled(this.getTemplateGrid().getStore().getCount()==1)
}else{if(rowIndex==this.getTemplateGrid().getStore().getCount()-1){this.getUpAction().setDisabled(false);
this.getDownAction().setDisabled(true)
}else{this.getUpAction().setDisabled(false);
this.getDownAction().setDisabled(false)
}}},getAllTemplateStore:function(){if(!this.allTemplateStore){this.allTemplateStore=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.template.all",autoDestroy:true,idProperty:"urn",fields:["id","urn","title","schema"]})
}return this.allTemplateStore
},getSaveOrderAction:function(){if(!this.saveOrderAction){this.saveOrderAction=new Ext.Action({text:Openwis.i18n("Common.Btn.SaveOrder"),scope:this,width:100,handler:function(){var records=this.getTemplateGrid().getStore().data.items;
var params=[];
Ext.each(records,function(item,index,allItems){params.push(item.get("urn"))
},this);
var msg=null;
var saveOrderHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.template.saveOrder",params:params,confirmMsg:msg,listeners:{success:function(){this.getSaveOrderAction().setDisabled(1);
this.getTemplateGrid().getStore().reload()
},scope:this}});
saveOrderHandler.proceed()
}})
}return this.saveOrderAction
},getUpAction:function(){if(!this.upAction){this.upAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Up"),scope:this,width:100,handler:function(){this.up()
}})
}return this.upAction
},up:function(){var record=null;
var selectedIndex=-1;
for(var i=0;
i<this.templateGrid.store.getCount();
i++){if(this.getTemplateGrid().getSelectionModel().isSelected(i)){selectedIndex=i
}}if(selectedIndex!=-1){record=this.templateGrid.store.getAt(selectedIndex);
if((selectedIndex-1)>=0){this.templateGrid.store.remove(record);
this.templateGrid.store.insert(selectedIndex-1,record)
}this.templateGrid.getView().refresh();
this.templateGrid.getSelectionModel().selectRow(selectedIndex-1);
this.getSaveOrderAction().setDisabled(0);
this.disableUpAndDown(selectedIndex-1)
}},getDownAction:function(){if(!this.downAction){this.downAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Down"),scope:this,width:100,handler:function(){this.down()
}})
}return this.downAction
},down:function(){var record=null;
var selectedIndex=-1;
for(var i=0;
i<this.templateGrid.store.getCount();
i++){if(this.getTemplateGrid().getSelectionModel().isSelected(i)){selectedIndex=i
}}if(selectedIndex!=-1){record=this.templateGrid.store.getAt(selectedIndex);
if((selectedIndex+1)<this.templateGrid.store.getCount()){this.templateGrid.store.remove(record);
this.templateGrid.store.insert(selectedIndex+1,record)
}this.templateGrid.getView().refresh();
this.templateGrid.getSelectionModel().selectRow(selectedIndex+1);
this.getSaveOrderAction().setDisabled(0);
this.disableUpAndDown(selectedIndex+1)
}},getDuplicateAction:function(){if(!this.duplicateAction){this.duplicateAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Duplicate"),tooltip:"Create a new metadata from the selected one.",scope:this,handler:function(){var selectedRec=this.getTemplateGrid().getSelectionModel().getSelected();
Ext.MessageBox.confirm("Confirm ?",Openwis.i18n("TemplateManagement.Confirm.Duplicate"),function(btnClicked){if(btnClicked=="yes"){new Openwis.Handler.Save({url:configOptions.locService+"/xml.template.duplicate",params:{urn:selectedRec.get("urn")},listeners:{success:function(config){this.getAllTemplateStore().load()
},scope:this}}).proceed()
}},this)
}})
}return this.duplicateAction
},getEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Edit"),scope:this,handler:function(){var selectedRec=this.getTemplateGrid().getSelectionModel().getSelected();
doEditMetadataById(selectedRec.get("id"),selectedRec.get("title"));
addMetadataDialogCloseListener(this.dialogCloseListener,this)
}})
}return this.editAction
},dialogCloseListener:function(ct){this.getTemplateGrid().getStore().reload();
removeMetadataDialogCloseListener(this.dialogCloseListener,this)
},getRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Btn.Remove"),scope:this,handler:function(){var selection=this.getTemplateGrid().getSelectionModel().getSelected();
var msg=null;
var removeHandler=new Openwis.Handler.Remove({url:configOptions.locService+"/xml.template.remove",params:{urn:selection.get("urn")},confirmMsg:msg,listeners:{success:function(){this.getTemplateGrid().getStore().reload()
},scope:this}});
removeHandler.proceed()
}})
}return this.removeAction
},isValidURN:function(value){return true
}});