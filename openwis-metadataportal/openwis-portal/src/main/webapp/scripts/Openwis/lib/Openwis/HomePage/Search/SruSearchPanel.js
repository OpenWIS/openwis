Ext.ns('Openwis.HomePage.Search');

Openwis.HomePage.Search.SruSearchPanel = Ext.extend(Openwis.HomePage.Search.AbstractSearchPanel, {
	
	initComponent: function() {
		Ext.apply(this, {
		    title: Openwis.i18n('HomePage.Search.Remote.Title')
		});
		Openwis.HomePage.Search.SruSearchPanel.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
    //----------------------------------------------------------------- Initialization of the panels.
        
    initialize: function(){
    	this.add(this.getSRUServerLabel());
    	this.add(this.getServerComboBox());
    	this.add(this.getWhatOthersCriteriaFieldSet());
    	//this.add(this.getWhereLabel());
    	//this.add(this.getMapPanel());
    	//this.add(this.getBoundsPanel());
    	
    	//this.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Region")));
    	//this.add(this.getRegionsCombobox());
    	
    	
    	this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.From")));
    	this.getWhenFieldSet().add(this.getWhenFromDateField());
    	
    	this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.To")));
    	this.getWhenFieldSet().add(this.getWhenToDateField());
    	
    	this.getWhenFieldSet().add(this.createCriteriaLabel(Openwis.i18n("Common.Extent.Temporal.Userd")));
    	this.getWhenFieldSet().add(this.getDateFieldComboBox());
    	
    	this.add(this.getWhenFieldSet());
    	this.add(this.getSizeResultLabel());
    	this.add(this.getSizeResultField());

    	this.add(this.getButtonsSruPanel())
    	
    },
    	
    getBoundsPanel: function(){
    	if(!this.boundsPanel){
    		this.boundsPanel = new Ext.Panel({
    			layout: 'table',
    			layoutConfig: {columns: 1},
    			defaults: {style: {margin: '2px 0 17px 0'}},
    			border:false,
    			autoScroll:false});
    		this.boundsPanel.add(this.getWhereBoundsPanel());
		}
    	return this.boundsPanel;
	},
	
	getTerm1Field: function(){
		if(!this.term1Field){
			this.term1Field = new Ext.form.TextField({
				name:"or",
				allowBlank:true,
				width:190})
		}
		return this.term1Field
		
	},
	
	getSizeResultField: function(){
		if(!this.sizeResultField){
			this.sizeResultField = new Ext.form.TextField({
				name:"sizeResult",
				allowBlank:true,
				width:224,
				cls:"mg_bi10"})
		}
		return this.sizeResultField
	},
	
	getAbstractTextField: function(){
		if(!this.abstractTextField){
			this.abstractTextField = new Ext.form.TextField({
				name:"abstract",
				allowBlank:true,
				width:210})
		}
		return this.abstractTextField

	},
	
	getKeywordsTextField: function(){
		if(!this.keywordsTextField){
			this.keywordsTextField = new Ext.form.TriggerField({
				name:"themekey",
				allowBlank:true,
				width:210,
				onTriggerClick:function(a){
					new Openwis.Common.Search.KeywordsSearch({
						keywordsFromTf:this.getValue(),
						isXML:false,
						listeners:{
							keywordsSelection:function(b){
							this.setValue(b)
							},
							scope:this
						}
					})
				}
			})
		}
		return this.keywordsTextField
	},
	
	getExactPhraseTextField: function(){
		if(!this.exactPhraseTextField){
			this.exactPhraseTextField = new Ext.form.TextField({
				name:"phrase",
				allowBlank:true,
				width:190
			})
		}
		return this.exactPhraseTextField
	},
	
	getWhatTextField: function(){
		if(!this.whatTextField){
			this.whatTextField = new Ext.form.TextField({
				name:"all",
				allowBlank:true,
				width:190
			})
		}
		return this.whatTextField
	},
	
	
	getWithoutTextField: function(){
		if(!this.withoutPhraseTextField){
			this.withoutPhraseTextField = new Ext.form.TextField({
				name:"without",
				allowBlank:true,
				width:190})
		}
		return this.withoutPhraseTextField
	},
	
	
	getWhatOthersCriteriaFieldSet: function(){
		if(!this.whatOthersCriteriaFieldSet) {
			this.whatOthersCriteriaFieldSet=new Ext.form.FieldSet({
				title:Openwis.i18n("HomePage.Search.Criteria.What"),
				layout:"table",
				layoutConfig:{columns:2, border:false},
				autoHeight:true,
				//collapsed:true,
				collapsible:true,
				items:[
					{
						width : 100,
						xtype : 'combo',
						cls:"mg_bi5",
						store : new Ext.data.ArrayStore({
							id:0,
							fields : ['name', 'value'],
							data   : [
						          ['FullText', ''],
						          ['Title', 'title all'],
						          ['subject', 'subject all'],
						          ['abstract', 'abstract all'],
						          ['Author', 'author all']
							]
						}),
						mode : 'local',
						triggerAction : 'all',
						forceSelection: true,
	                    editable:       false,
	                    fieldLabel:     'Title',
	                    name:           'termField1',
	                    displayField:   'name',
	                    valueField:     'value',
	                    queryMode: 'local',
	                    value:""

					},
					{
						width : 98,
						cls:"mg_l4 mg_bi5",
						xtype : 'textfield',
						colspan : 2
					},
					{
						width : 100,
						xtype : 'combo',
						cls:'mg_bi5',

						store : new Ext.data.ArrayStore({
							id:0,
							fields : ['name', 'value'],
							data   : [
						          ['AND', 'and'],
						          ['OR', 'or'],
						          ['NOT', 'not']
							]
						}),
						mode : 'local',
						triggerAction : 'all',
						forceSelection: true,
	                    editable:       false,
	                    fieldLabel:     'Title',
	                    name:           'termBool1',
	                    displayField:   'name',
	                    valueField:     'value',
	                    queryMode: 'local',
	                    value:"AND"
					},
					{
						html : '<br/>',
						colspan : 2,
						border : false
					},
					{
						width : 100,
						cls:"mg_bi5",
						xtype : 'combo',
						store : new Ext.data.ArrayStore({
							id:0,
							fields : ['name', 'value'],
							data   : [
						          ['FullText', ''],
						          ['Title', 'title all'],
						          ['subject', 'subject all'],
						          ['abstract', 'abstract all'],
						          ['Author', 'author all']
							]
						}),
						mode : 'local',
						triggerAction : 'all',
						forceSelection: true,
	                    editable:       false,
	                    fieldLabel:     'Title',
	                    name:           'termField1',
	                    displayField:   'name',
	                    valueField:     'value',
	                    queryMode: 'local',
	                    value:""
					},
					{
						width : 98,
						cls:"mg_l4 mg_bi5",
						xtype : 'textfield',
						colspan : 2
					},
					{
						width : 100,
						xtype : 'combo',
						cls:'mg_bi5',

						store : new Ext.data.ArrayStore({
							id:0,
							fields : ['name', 'value'],
							data   : [
						          ['AND', 'and'],
						          ['OR', 'or'],
						          ['NOT', 'not']
							]
						}),
						mode : 'local',
						triggerAction : 'all',
						forceSelection: true,
	                    editable:       false,
	                    fieldLabel:     'Title',
	                    name:           'termBool1',
	                    displayField:   'name',
	                    valueField:     'value',
	                    queryMode: 'local',
	                    value:"AND"
					},
					{
						html : '<br/>',
						colspan : 2,
						border : false
					},
					{
						width : 100,
						xtype : 'combo',
						cls: 'mg_bi0',

						store : new Ext.data.ArrayStore({
							id:0,
							fields : ['name', 'value'],
							data   : [
						          ['FullText', ''],
						          ['Title', 'title all'],
						          ['subject', 'subject all'],
						          ['abstract', 'abstract all'],
						          ['Author', 'author all']
							]
						}),
						mode : 'local',
						triggerAction : 'all',
						forceSelection: true,
	                    editable:       false,
	                    fieldLabel:     'Title',
	                    name:           'termField1',
	                    displayField:   'name',
	                    valueField:     'value',
	                    queryMode: 'local',
	                    value:""

					},
					{
						width : 98,
						cls:"mg_l4 mg_bi0",
						xtype : 'textfield'
					}
				]
				}
			);

			this.whatOthersCriteriaFieldSet.addListener("collapse",this.onGuiChanged,this);
			this.whatOthersCriteriaFieldSet.addListener("expand",this.onGuiChanged,this)
		}

		return this.whatOthersCriteriaFieldSet
	},
	
	
	getWhatMapTypeFieldSet: function(){
		if(!this.whatMapTypeFieldSet){
			this.whatMapTypeFieldSet = new Ext.form.FieldSet({
				title:Openwis.i18n("HomePage.Search.Criteria.What.MapType"),
				layout:"table",
				layoutConfig:{columns:1},
				autoHeight:true,
				collapsed:true,
				collapsible:true
			});
			this.whatMapTypeFieldSet.addListener("collapse",this.onGuiChanged,this);
			this.whatMapTypeFieldSet.addListener("expand",this.onGuiChanged,this)
		}
		return this.whatMapTypeFieldSet
	},
	
	
	getWhatMapTypeCheckboxGroup: function(){
		if(!this.whatMapTypeCheckboxGroup){
			this.whatMapTypeCheckboxGroup = new Ext.form.CheckboxGroup({
				columns:2,
				items:[{
					boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Digital"),
					name:"digital"},{
					boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Dynamic"),name:"dynamic"},{boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Paper"),
					name:"paper"},{
					boxLabel:Openwis.i18n("HomePage.Search.Criteria.What.MapType.Download"),
					name:"download"
				}],
				width:200
			})
		}
		return this.whatMapTypeCheckboxGroup
	},
	
	
	getWhatSearchAccuracyFieldSet: function(){
		if(!this.whatSearchAccuracyFieldSet){
			this.whatSearchAccuracyFieldSet = new Ext.form.FieldSet({
				title:Openwis.i18n("HomePage.Search.Criteria.What.SearchAccuracy"),
				layout:"table",
				layoutConfig:{columns:3},
				autoHeight:true,
				collapsed:true,
				collapsible:true
			});
			this.whatSearchAccuracyFieldSet.addListener("collapse",this.onGuiChanged,this);
			this.whatSearchAccuracyFieldSet.addListener("expand",this.onGuiChanged,this)
		}
		return this.whatSearchAccuracyFieldSet
	},
	
	
	getWhatSearchAccuracyRadioGroup: function(){
		if(!this.whatSearchAccuracyRadioGroup2){
			this.whatSearchAccuracyRadioGroup2 = new Ext.form.SliderField({
				name:"similarity",
				value:80
			});
			this.whatSearchAccuracyRadioGroup2.slider.topThumbZIndex=8000
		}
		return this.whatSearchAccuracyRadioGroup2
	},
	
	
	getWhereBoundsPanel: function(){
		if(!this.whereBoundsPanel){
			this.whereBoundsPanel = new Ext.Panel({
				layout:"table",
				layoutConfig:{columns:4},
				defaults:{style:{margin:"4px"}},
				border:false
			});
			this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LatMin")));
			this.whereBoundsPanel.add(this.getWhereBoundsLatMinTextField());
			this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LongMin")));
			this.whereBoundsPanel.add(this.getWhereBoundsLongMinTextField());
			this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LatMax")));
			this.whereBoundsPanel.add(this.getWhereBoundsLatMaxTextField());
			this.whereBoundsPanel.add(this.createCriteriaLabel(Openwis.i18n("HomePage.Search.Criteria.Where.Bounds.LongMax")));
			this.whereBoundsPanel.add(this.getWhereBoundsLongMaxTextField())
			}
		return this.whereBoundsPanel
		
	},
	
	
	getWhereBoundsLatMinTextField: function(){
		if(!this.whereBoundsLatMinTextField){
			this.whereBoundsLatMinTextField = new Ext.form.TextField({
				name:"southBL",
				allowBlank:true,
				autoCreate:{tag:"input",
					type:"text",
					size:"5",
					autocomplete:"off"},
					listeners:{change:this.coordsChanged,scope:this},
					validator:function(a){
						if(a.trim()!=""&&isNaN(a)){
							return Openwis.i18n("Common.Validation.NotANumber",{value:a})
						}
						return true
					}
				})
		}
		return this.whereBoundsLatMinTextField
	},
	
	
	getWhereBoundsLatMaxTextField: function(){
		if(!this.whereBoundsLatMaxTextField){
			this.whereBoundsLatMaxTextField = new Ext.form.TextField({
				name:"northBL",
				allowBlank:true,
				autoCreate:{
					tag:"input",
					type:"text",
					size:"5",
					autocomplete:"off"},
					listeners:{change:this.coordsChanged,scope:this},
					validator:function(a){
						if(a.trim()!=""&&isNaN(a)){
							return Openwis.i18n("Common.Validation.NotANumber",{value:a})
						}
						return true
					}
				})
		}
		return this.whereBoundsLatMaxTextField
	},
	
	
	getWhereBoundsLongMinTextField: function(){
		if(!this.whereBoundsLongMinTextField){
			this.whereBoundsLongMinTextField = new Ext.form.TextField({
				name:"westBL",
				allowBlank:true,
				autoCreate:{
					tag:"input",
					type:"text",
					size:"5",
					autocomplete:"off"},
					listeners:{change:this.coordsChanged,scope:this},
						validator:function(a){
							if(a.trim()!=""&&isNaN(a)){
								return Openwis.i18n("Common.Validation.NotANumber",{value:a})
							}
							return true
						}
			})
		}
		return this.whereBoundsLongMinTextField
	},
	
	
	
	getWhereBoundsLongMaxTextField: function(){
		if(!this.whereBoundsLongMaxTextField){
			this.whereBoundsLongMaxTextField = new Ext.form.TextField({
				name:"eastBL",
				allowBlank:true,
				autoCreate:{
					tag:"input",
					type:"text",
					size:"5",
					autocomplete:"off"},
					listeners:{change:this.coordsChanged,scope:this},
					validator:function(a){
						if(a.trim()!=""&&isNaN(a)){
							return Openwis.i18n("Common.Validation.NotANumber",{value:a})
						}return true
					}
				})
		}
		return this.whereBoundsLongMaxTextField
	},
	
	
	getTerm1Panel: function() {
		if(!this.term1Panel) {
			this.term1Panel = new Ext.Panel({
				layout: 'table',
				layoutConfig: {
                columns: 2
            },
            defaults: {
                style: {
                    margin: '4px'
                }
            },
            border: false
        });

        this.term1Panel.add(this.getTermField1Combobox());
        this.term1Panel.add(this.getTermBool1Combobox());
        colspan:2
    }

    return this.term1Panel;
},

	getServerComboBox:function(){
		if(!this.serverComboBox){
			this.serverComboBox=new Ext.form.ComboBox({
				store:new Ext.data.ArrayStore({
				id:0,
				fields:["id","value"],
				data:[
						["http://gisc.kma.go.kr/openwis-user-portal/srv/en/main.home/portal.sru?",Openwis.i18n("HomePage.Search.Criteria.What.GiscSeoul")],
						["http://www.wis-jma.go.jp/meta/sru.jsp?",Openwis.i18n("HomePage.Search.Criteria.What.GiscTokyo")],
						["http://wisportal.cma.gov.cn/srw/search?",Openwis.i18n("HomePage.Search.Criteria.What.GiscBeijing")],
						["http://wispi.meteo.fr/openwis-user-portal/srv/en/main.home/portal.sru?",Openwis.i18n("HomePage.Search.Criteria.What.GiscToulouse")],
						["http://wis.metoffice.gov.uk/openwis-user-portal/srv/en/main.home/portal.sru?",Openwis.i18n("HomePage.Search.Criteria.What.GiscExeter")],
						["http://wis.bom.gov.au/openwis-user-portal/srv/en/main.home/portal.sru?",Openwis.i18n("HomePage.Search.Criteria.What.GiscMelbourne")],
						["http://gisc.dwd.de/SRU2JDBC/sru?",Openwis.i18n("HomePage.Search.Criteria.What.GiscOffenbach")],
						["http://meta.gisc-msk.wis.mecom.ru/openwis-portal/srv/en/portal.sru?",Openwis.i18n("HomePage.Search.Criteria.What.GiscMoscow")]
				     ]
		     }),
		     valueField:"id",
		     displayField:"value",
		     value:"http://gisc.kma.go.kr/openwis-user-portal/srv/en/main.home/portal.sru?",
		     name:"serverComboBox",
		     typeAhead:true,
		     mode:"local",
		     triggerAction:"all",
		     editable:false,
		     selectOnFocus:true,
		     width:224,
			 cls:"mg_bi10"
			})
		}return this.serverComboBox
	},
	
	getTermField1Combobox:function(){
		if(!this.termField1Combobox){
			this.termField1Combobox = new Ext.form.ComboBox({
				store:new Ext.data.ArrayStore({
				id:0,
				fields:["id","value"],
				data:[["intersection",
				Openwis.i18n("HomePage.Search.Criteria.Where.Type.Intersection")],["overlaps",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Overlaps")],["encloses",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Encloses")],["fullyOutsideOf",Openwis.i18n("HomePage.Search.Criteria.Where.Type.FullyOutsideOf")],["crosses",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Crosses")],["touches",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Touches")],["within",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Within")]]}),valueField:"id",displayField:"value",value:"overlaps",name:"relation",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:50})

		}
		return this.termField1Combobox
	},
	
	getTermBool1Combobox: function(){
		if(!this.termBool1Combobox){
			this.termBool1Combobox = new Ext.form.ComboBox({
				store:new Ext.data.ArrayStore({
					id:0,
					fields:["id","value"],
					data:[["intersection",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Intersection")],
					      ["overlaps",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Overlaps")],
					      ["encloses",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Encloses")],
					      ["fullyOutsideOf",Openwis.i18n("HomePage.Search.Criteria.Where.Type.FullyOutsideOf")],
					      ["crosses",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Crosses")],
					      ["touches",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Touches")],
					      ["within",Openwis.i18n("HomePage.Search.Criteria.Where.Type.Within")]
					]}),
					valueField:"id",
					displayField:"value",
					value:"overlaps",
					name:"relation",
					typeAhead:true,
					mode:"local",
					triggerAction:"all",
					editable:false,
					selectOnFocus:true,
					width:50})
		}
		return this.termBool1Combobox
	},
	
	
	getWhenFromDateField:function(){if(!this.whenFromDateField){this.whenFromDateField=new Ext.form.DateField({allowBlank:false,name:"FromDate",editable:false,format:"Y-m-d",disabled:false,width:139,cls:"mg_bi5"})
}return this.whenFromDateField
},getWhenToDateField:function(){if(!this.whenToDateField){this.whenToDateField=new Ext.form.DateField({allowBlank:false,name:"ToDate",editable:false,format:"Y-m-d",disabled:false,width:139,cls:"mg_bi5"})
}return this.whenToDateField
},getWhenTemporalExtentRadio:function(){if(!this.whenTemporalExtentRadio){this.whenTemporalExtentRadio=new Ext.form.Radio({name:"whenMode",inputValue:"TemporalExtent",checked:false,listeners:{check:function(b,a){if(a){this.getWhenTemporalExtentFromDateField().enable();
this.getWhenTemporalExtentToDateField().enable()
}else{this.getWhenTemporalExtentFromDateField().disable();
this.getWhenTemporalExtentToDateField().disable()
}},scope:this}})
}return this.whenTemporalExtentRadio
},getWhenTemporalExtentFromDateField:function(){if(!this.whenTemporalExtentFromDateField){this.whenTemporalExtentFromDateField=new Ext.form.DateField({allowBlank:false,name:"TemporalExtentFrom",editable:false,format:"Y-m-d",disabled:true,width:139,cls:"mg_bi5"})
}return this.whenTemporalExtentFromDateField
},getWhenTemporalExtentToDateField:function(){if(!this.whenTemporalExtentToDateField){this.whenTemporalExtentToDateField=new Ext.form.DateField({allowBlank:false,name:"TemporalExtentTo",editable:false,format:"Y-m-d",disabled:true,width:139,cls:"mg_bi5"})
}return this.whenTemporalExtentToDateField
},getWhenFieldSet:function(){if(!this.whenFieldSet){this.whenFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.When"),layout:"table",layoutConfig:{columns:2},cls:"mainLabelCls",autoHeight:true,collapsed:true,collapsible:true});
this.whenFieldSet.addListener("collapse",this.onGuiChanged,this);
this.whenFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.whenFieldSet
},getRestrictToFieldSet:function(){if(!this.restrictToFieldSet){this.restrictToFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.RestrictTo"),layout:"table",layoutConfig:{columns:1},autoHeight:true,collapsed:true,collapsible:true});
this.restrictToFieldSet.addListener("collapse",this.onGuiChanged,this);
this.restrictToFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.restrictToFieldSet
},getDateFieldComboBox:function(){if(!this.dateFieldComboBox){this.dateFieldComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["creationDate",Openwis.i18n("HomePage.Search.Criteria.When.CreationDate")],["modificationDate",Openwis.i18n("HomePage.Search.Criteria.When.ModificationDate")],["publicationDate",Openwis.i18n("HomePage.Search.Criteria.When.PublicationDate")],["beginningDate",Openwis.i18n("HomePage.Search.Criteria.When.BeginningDate")],["endingDate",Openwis.i18n("HomePage.Search.Criteria.When.EndingDate")]]}),valueField:"id",displayField:"value",value:"creationDate",name:"relation",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:139,cls:"mg_bi5"})
}return this.dateFieldComboBox
},getRestrictToCatalogComboBox:function(){if(!this.restrictToCatalogComboBox){var a=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.home.page.site.all",idProperty:"id",fields:[{name:"id"},{name:"name"}],listeners:{load:function(d,c,e){var b=new Ext.data.Record({id:"",name:Openwis.i18n("Common.List.Any")});
d.insert(0,[b])
}}});
this.restrictToCatalogComboBox=new Ext.form.ComboBox({store:a,valueField:"id",displayField:"name",name:"siteId",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.restrictToCatalogComboBox
},getRestrictToCategoryComboBox:function(){if(!this.restrictToCategoryComboBox){var a=new Openwis.Data.JeevesJsonStore({url:configOptions.locService+"/xml.get.home.page.category.all",idProperty:"id",fields:[{name:"id"},{name:"name"}],listeners:{load:function(d,c,e){var b=new Ext.data.Record({id:"",name:Openwis.i18n("Common.List.Any")});
d.insert(0,[b])
}}});
this.restrictToCategoryComboBox=new Ext.form.ComboBox({store:a,valueField:"id",displayField:"name",name:"category",typeAhead:true,triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.restrictToCategoryComboBox
},getRestrictToKindComboBox:function(){if(!this.restrictToKindComboBox){this.restrictToKindComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["metadata",Openwis.i18n("HomePage.Search.Criteria.RestrictTo.Kind.Metadata")],["template",Openwis.i18n("HomePage.Search.Criteria.RestrictTo.Kind.Template")]]}),valueField:"id",displayField:"value",name:"restrictToKind",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200});
this.restrictToKindComboBox.setValue("")
}return this.restrictToKindComboBox
},getInspireFieldSet:function(){if(!this.inspireFieldSet){this.inspireFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("HomePage.Search.Criteria.Inspire"),layout:"table",layoutConfig:{columns:1},autoHeight:true,collapsed:true,collapsible:true});
this.inspireFieldSet.addListener("collapse",this.onGuiChanged,this);
this.inspireFieldSet.addListener("expand",this.onGuiChanged,this)
}return this.inspireFieldSet
},getOnlyInspireMetadataCheckbox:function(){if(!this.onlyInspireMetadataCheckbox){this.onlyInspireMetadataCheckbox=new Ext.form.Checkbox({name:"onlyInspireMetadata",checked:false,boxLabel:Openwis.i18n("HomePage.Search.Criteria.Inspire.InspireMetadataOnly")})
}return this.onlyInspireMetadataCheckbox
},getInspireAnnexComboBox:function(){if(!this.inspireAnnexComboBox){this.inspireAnnexComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["i",Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex.I")],["ii",Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex.II")],["iii",Openwis.i18n("HomePage.Search.Criteria.Inspire.Annex.III")]]}),valueField:"id",displayField:"value",value:"",name:"inspireAnnex",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.inspireAnnexComboBox
},getInspireSourceTypeComboBox:function(){if(!this.inspireSourceTypeComboBox){this.inspireSourceTypeComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["dataset",Openwis.i18n("HomePage.Search.Criteria.Inspire.SourceType.Dataset")],["service",Openwis.i18n("HomePage.Search.Criteria.Inspire.SourceType.Service")]]}),valueField:"id",displayField:"value",value:"",name:"inspireSourceType",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.inspireSourceTypeComboBox
},getInspireServiceTypeComboBox:function(){if(!this.inspireServiceTypeComboBox){this.inspireServiceTypeComboBox=new Ext.form.ComboBox({store:new Ext.data.ArrayStore({id:0,fields:["id","value"],data:[["",Openwis.i18n("Common.List.Any")],["ESRI:AIMS--http--configuration",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.ArcIMSAXL")],["ESRI:AIMS--http-get-feature",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.ArcIMSFMS")],["GLG:KML-2.0-http-get-map",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.GoogleEarthKMLV2")],["OGC:WCS-1.1.0-http-get-capabilities",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.OGCWCSV110")],["OGC:WFS-1.0.0-http-get-capabilities",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.OGCWFSV100")],["OGC:WMC-1.1.0-http-get-capabilities",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.OGCWMCV11")],["WWW:LINK-1.0-http--ical",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.iCalendar")],["WWW:LINK-1.0-http--link",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.WebAddress")],["WWW:LINK-1.0-http--partners",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.PartnerWebAddress")],["WWW:LINK-1.0-http--related",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.RelatedLink")],["WWW:LINK-1.0-http--rss",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.RSS")],["WWW:LINK-1.0-http--samples",Openwis.i18n("HomePage.Search.Criteria.Inspire.ServiceType.ShowcaseProduct")]]}),valueField:"id",displayField:"value",value:"",name:"inspireServiceType",typeAhead:true,mode:"local",triggerAction:"all",editable:false,selectOnFocus:true,width:200})
}return this.inspireServiceTypeComboBox
},reset:function(){this.getServerComboBox().reset();
this.getWhatOthersCriteriaFieldSet().get(0).reset();
this.getWhatOthersCriteriaFieldSet().get(2).reset();
this.getWhatOthersCriteriaFieldSet().get(4).reset();
this.getWhatOthersCriteriaFieldSet().get(6).reset();
this.getWhatOthersCriteriaFieldSet().get(8).reset();
this.getWhatOthersCriteriaFieldSet().get(9).reset();
this.getWhatOthersCriteriaFieldSet().get(1).reset();
this.getWhatOthersCriteriaFieldSet().get(5).reset();
//this.getWhereBoundsLatMinTextField().reset();
//this.getWhereBoundsLongMinTextField().reset();
//this.getWhereBoundsLatMaxTextField().reset();
//this.getWhereBoundsLongMaxTextField().reset();
//this.getRegionsCombobox().reset();
this.getWhenFromDateField().reset();
this.getWhenToDateField().reset();
this.getDateFieldComboBox().reset();
this.getSizeResultField().reset();
},coordsChanged:function(){var d=parseFloat(this.getWhereBoundsLatMinTextField().getValue());
var e=parseFloat(this.getWhereBoundsLongMinTextField().getValue());
var a=parseFloat(this.getWhereBoundsLatMaxTextField().getValue());
var b=parseFloat(this.getWhereBoundsLongMaxTextField().getValue());
if(!(isNaN(d)||isNaN(a)||isNaN(e)||isNaN(b))){var d=Math.max(Math.min(d,90),-90);
var a=Math.max(Math.min(a,90),-90);
var e=this.getLongitude(e);
var b=this.getLongitude(b);
var c={};
c.bottom=d;
c.top=a;
c.left=e;
c.right=b;
this.getMapPanel().drawExtent(c);
this.getMapPanel().zoomToExtent(c);
this.updateMapFields(c,true)
}else{this.getMapPanel().reset()
}},updateMapFields:function(b,a){this.getWhereBoundsLatMinTextField().setValue(b.bottom);
this.getWhereBoundsLatMaxTextField().setValue(b.top);
this.getWhereBoundsLongMinTextField().setValue(b.left);
this.getWhereBoundsLongMaxTextField().setValue(b.right);
if(b.bottom.constrain(-90,90)!=b.bottom){this.getWhereBoundsLatMinTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-90,to:90}))
}if(b.top.constrain(-90,90)!=b.top){this.getWhereBoundsLatMaxTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-90,to:90}))
}if(b.left.constrain(-180,180)!=b.left){this.getWhereBoundsLongMinTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-180,to:180}))
}if(b.right.constrain(-180,180)!=b.right){this.getWhereBoundsLongMaxTextField().markInvalid(Openwis.i18n("Common.Validation.NumberOutOfRange",{from:-180,to:180}))
}if(a){this.setRegionToUserDefined()
}if(b.left>b.right){this.getMapPanel().drawExtent(b);
this.getMapPanel().zoomToExtent(b)
}},
buildSearchParams:function(){

	var server = this.getServerComboBox().getValue();

	var termField1 = this.getWhatOthersCriteriaFieldSet().get(0).getValue();
	var termText1 = this.getWhatOthersCriteriaFieldSet().get(1).getValue();
	var termBool1 = "";
	var term1 = "";

	var termField2 = this.getWhatOthersCriteriaFieldSet().get(4).getValue();
	var termText2 = this.getWhatOthersCriteriaFieldSet().get(5).getValue();
	var termBool2 = "";
	var term2 = "";

	var termField3 = this.getWhatOthersCriteriaFieldSet().get(8).getValue();
	var termText3 = this.getWhatOthersCriteriaFieldSet().get(9).getValue();
	var term3 = "";

	if( termText1 != "" )
	{
		if( termText2 != "" || termText3 != "" )
		{
			termBool1 = this.getWhatOthersCriteriaFieldSet().get(2).getValue();
		}

		term1 = termField1 + ' ' + '"' + termText1 + '"' + ' ' + termBool1 + ' ';
	}

	if( termText2 != "" )
	{

		if( termText3 != "" )
		{
			termBool2 = this.getWhatOthersCriteriaFieldSet().get(6).getValue();
		}

		term2 = termField2 + ' ' + '"' + termText2 + '"' + ' ' + termBool2 + ' ';
	}

	if( termText3 != "" )
	{
		term3 = termField3 + ' ' + '"' + termText3 +'"';
	}
	
	var fromDate = this.getWhenFromDateField().getValue();
	var toDate = this.getWhenToDateField().getValue();
	var dateField = this.getDateFieldComboBox().getValue();
	var date = "";

	if( fromDate != "" && toDate != "" )
	{
		fromDate = Openwis.Utils.Date.formatDateForServer(this.getWhenFromDateField().getValue());
		toDate = Openwis.Utils.Date.formatDateForServer(this.getWhenToDateField().getValue());

		date = ' and ' + dateField + '>' + fromDate.split('-').join('') + ' and ' + dateField + '<' + toDate.split('-').join('');
	}

	//var sptialSouth = this.getWhereBoundsLatMinTextField().getValue();
	//var sptialWest = this.getWhereBoundsLongMinTextField().getValue();
	//var sptialNorth = this.getWhereBoundsLatMaxTextField().getValue();
	//var sptialEast = this.getWhereBoundsLongMaxTextField().getValue();
	//var bounds = "";

	//if( sptialNorth != "" && sptialWest != "" && sptialSouth != "" && sptialEast != "")
	//{
		//if( term1 == "" && term2 == "" && term3 == "" && date == "" )
		//{
			//bounds = ' geo.bounds within/partial/nwse "' + sptialNorth + ' ' + sptialWest + ' ' + sptialSouth + ' ' + sptialEast + '"';
		//}
		//else
		//{
			//bounds = ' and geo.bounds within/partial/nwse "' + sptialNorth + ' ' + sptialWest + ' ' + sptialSouth + ' ' + sptialEast + '"';
		//}
		
	//}

	var maxRecords = this.getSizeResultField().getValue();

	if( maxRecords == "" )
	{
		maxRecords = 20;
	}

	var url = ""

	if( server != "http://gisc.dwd.de/SRU2JDBC/sru?" )
	{
		url = server + 'operation=searchRetrieve&version=1.1&query=' + term1 + term2 + term3 + date + '&startRecord=1&maximumRecords=' + maxRecords + '&&';
	}
	else
	{
		url = server + 'operation=searchRetrieve&version=1.1&query=' + term1 + term2 + term3 + date + '&startRecord=1&maximumRecords=' + maxRecords + '&&stylesheet=xsl/dwd-sru.xsl&x-dwd-stylesheetDetailLevel=2';
	}

	window.open(url);
},
//searchUrl:function(){return configOptions.locService+"/main.search.embedded"
//},
	validate:function(){
		return true;
		//this.getWhenFromDateField().isValid()&&this.getWhenToDateField().isValid();
		//return true
	}
});
    
