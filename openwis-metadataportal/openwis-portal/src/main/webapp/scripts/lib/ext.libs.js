Ext.ns("Ext.ux.form");
Ext.ux.form.DateTime=Ext.extend(Ext.form.Field,{defaultAutoCreate:{tag:"input",type:"hidden"},timeWidth:100,dtSeparator:" ",hiddenFormat:"Y-m-d H:i:s",otherToNow:true,timePosition:"right",dateFormat:"m/d/y",timeFormat:"g:i A",initComponent:function(){Ext.ux.form.DateTime.superclass.initComponent.call(this);
var dateConfig=Ext.apply({},{id:this.id+"-date",format:this.dateFormat||Ext.form.DateField.prototype.format,width:this.timeWidth,selectOnFocus:this.selectOnFocus,listeners:{blur:{scope:this,fn:this.onBlur},focus:{scope:this,fn:this.onFocus}}},this.dateConfig);
this.df=new Ext.form.DateField(dateConfig);
this.df.ownerCt=this;
delete (this.dateFormat);
var timeConfig=Ext.apply({},{id:this.id+"-time",format:this.timeFormat||Ext.form.TimeField.prototype.format,width:this.timeWidth,selectOnFocus:this.selectOnFocus,listeners:{blur:{scope:this,fn:this.onBlur},focus:{scope:this,fn:this.onFocus}}},this.timeConfig);
this.tf=new Ext.form.TimeField(timeConfig);
this.tf.ownerCt=this;
delete (this.timeFormat);
this.relayEvents(this.df,["focus","specialkey","invalid","valid"]);
this.relayEvents(this.tf,["focus","specialkey","invalid","valid"])
},onRender:function(ct,position){if(this.isRendered){return
}Ext.ux.form.DateTime.superclass.onRender.call(this,ct,position);
var t;
if("below"===this.timePosition||"bellow"===this.timePosition){t=Ext.DomHelper.append(ct,{tag:"table",style:"border-collapse:collapse",children:[{tag:"tr",children:[{tag:"td",style:"padding-bottom:1px",cls:"ux-datetime-date"}]},{tag:"tr",children:[{tag:"td",cls:"ux-datetime-time"}]}]},true)
}else{t=Ext.DomHelper.append(ct,{tag:"table",style:"border-collapse:collapse",children:[{tag:"tr",children:[{tag:"td",style:"padding-right:4px",cls:"ux-datetime-date"},{tag:"td",cls:"ux-datetime-time"}]}]},true)
}this.tableEl=t;
this.wrap=t.wrap({cls:"x-form-field-wrap"});
this.wrap.on("mousedown",this.onMouseDown,this,{delay:10});
this.df.render(t.child("td.ux-datetime-date"));
this.tf.render(t.child("td.ux-datetime-time"));
if(Ext.isIE&&Ext.isStrict){t.select("input").applyStyles({top:0})
}this.on("specialkey",this.onSpecialKey,this);
this.df.el.swallowEvent(["keydown","keypress"]);
this.tf.el.swallowEvent(["keydown","keypress"]);
if("side"===this.msgTarget){var elp=this.el.findParent(".x-form-element",10,true);
this.errorIcon=elp.createChild({cls:"x-form-invalid-icon"});
this.df.errorIcon=this.errorIcon;
this.tf.errorIcon=this.errorIcon
}this.el.dom.name=this.hiddenName||this.name||this.id;
this.df.el.dom.removeAttribute("name");
this.tf.el.dom.removeAttribute("name");
this.isRendered=true;
this.updateHidden()
},adjustSize:Ext.BoxComponent.prototype.adjustSize,alignErrorIcon:function(){this.errorIcon.alignTo(this.tableEl,"tl-tr",[2,0])
},initDateValue:function(){this.dateValue=this.otherToNow?new Date():new Date(1970,0,1,0,0,0)
},clearInvalid:function(){this.df.clearInvalid();
this.tf.clearInvalid()
},markInvalid:function(msg){this.df.markInvalid(msg);
this.tf.markInvalid(msg)
},beforeDestroy:function(){if(this.isRendered){this.wrap.removeAllListeners();
this.wrap.remove();
this.tableEl.remove();
this.df.destroy();
this.tf.destroy()
}},disable:function(){if(this.isRendered){this.df.disabled=this.disabled;
this.df.onDisable();
this.tf.onDisable()
}this.disabled=true;
this.df.disabled=true;
this.tf.disabled=true;
this.fireEvent("disable",this);
return this
},enable:function(){if(this.rendered){this.df.onEnable();
this.tf.onEnable()
}this.disabled=false;
this.df.disabled=false;
this.tf.disabled=false;
this.fireEvent("enable",this);
return this
},focus:function(){this.df.focus()
},getPositionEl:function(){return this.wrap
},getResizeEl:function(){return this.wrap
},getValue:function(){return this.dateValue?new Date(this.dateValue):""
},isValid:function(){return this.df.isValid()&&this.tf.isValid()
},isVisible:function(){return this.df.rendered&&this.df.getActionEl().isVisible()
},onBlur:function(f){if(this.wrapClick){f.focus();
this.wrapClick=false
}if(f===this.df){this.updateDate()
}else{this.updateTime()
}this.updateHidden();
(function(){if(!this.df.hasFocus&&!this.tf.hasFocus){var v=this.getValue();
if(String(v)!==String(this.startValue)){this.fireEvent("change",this,v,this.startValue)
}this.hasFocus=false;
this.fireEvent("blur",this)
}}).defer(100,this)
},onFocus:function(){if(!this.hasFocus){this.hasFocus=true;
this.startValue=this.getValue();
this.fireEvent("focus",this)
}},onMouseDown:function(e){if(!this.disabled){this.wrapClick="td"===e.target.nodeName.toLowerCase()
}},onSpecialKey:function(t,e){var key=e.getKey();
if(key===e.TAB){if(t===this.df&&!e.shiftKey){e.stopEvent();
this.tf.focus()
}if(t===this.tf&&e.shiftKey){e.stopEvent();
this.df.focus()
}}if(key===e.ENTER){this.updateValue()
}},setDate:function(date){this.df.setValue(date)
},setTime:function(date){this.tf.setValue(date)
},setSize:function(w,h){if(!w){return
}if("below"===this.timePosition){this.df.setSize(w,h);
this.tf.setSize(w,h);
if(Ext.isIE){this.df.el.up("td").setWidth(w);
this.tf.el.up("td").setWidth(w)
}}else{this.df.setSize(w-this.timeWidth-4,h);
this.tf.setSize(this.timeWidth,h);
if(Ext.isIE){this.df.el.up("td").setWidth(w-this.timeWidth-4);
this.tf.el.up("td").setWidth(this.timeWidth)
}}},setValue:function(val){if(!val&&true===this.emptyToNow){this.setValue(new Date());
return
}else{if(!val){this.setDate("");
this.setTime("");
this.updateValue();
return
}}if("number"===typeof val){val=new Date(val)
}else{if("string"===typeof val&&this.hiddenFormat){val=Date.parseDate(val,this.hiddenFormat)
}}val=val?val:new Date(1970,0,1,0,0,0);
var da,time;
if(val instanceof Date){this.setDate(val);
this.setTime(val);
this.dateValue=new Date(val)
}else{da=val.split(this.dtSeparator);
this.setDate(da[0]);
if(da[1]){if(da[2]){da[1]+=da[2]
}this.setTime(da[1])
}}this.updateValue()
},setVisible:function(visible){if(visible){this.df.show();
this.tf.show()
}else{this.df.hide();
this.tf.hide()
}return this
},show:function(){return this.setVisible(true)
},hide:function(){return this.setVisible(false)
},updateDate:function(){var d=this.df.getValue();
if(d){if(!(this.dateValue instanceof Date)){this.initDateValue();
if(!this.tf.getValue()){this.setTime(this.dateValue)
}}this.dateValue.setMonth(0);
this.dateValue.setFullYear(d.getFullYear());
this.dateValue.setMonth(d.getMonth(),d.getDate())
}else{this.dateValue="";
this.setTime("")
}},updateTime:function(){var t=this.tf.getValue();
if(t&&!(t instanceof Date)){t=Date.parseDate(t,this.tf.format)
}if(t&&!this.df.getValue()){this.initDateValue();
this.setDate(this.dateValue)
}if(this.dateValue instanceof Date){if(t){this.dateValue.setHours(t.getHours());
this.dateValue.setMinutes(t.getMinutes());
this.dateValue.setSeconds(t.getSeconds())
}else{this.dateValue.setHours(0);
this.dateValue.setMinutes(0);
this.dateValue.setSeconds(0)
}}},updateHidden:function(){if(this.isRendered){var value=this.dateValue instanceof Date?this.dateValue.format(this.hiddenFormat):"";
this.el.dom.value=value
}},updateValue:function(){this.updateDate();
this.updateTime();
this.updateHidden();
return
},validate:function(){return this.df.validate()&&this.tf.validate()
},renderer:function(field){var format=field.editor.dateFormat||Ext.ux.form.DateTime.prototype.dateFormat;
format+=" "+(field.editor.timeFormat||Ext.ux.form.DateTime.prototype.timeFormat);
var renderer=function(val){var retval=Ext.util.Format.date(val,format);
return retval
};
return renderer
}});
Ext.reg("xdatetime",Ext.ux.form.DateTime);/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ux.GroupTab=Ext.extend(Ext.Container,{mainItem:0,expanded:true,deferredRender:true,activeTab:null,idDelimiter:"__",headerAsText:false,frame:false,hideBorders:true,initComponent:function(config){Ext.apply(this,config);
this.frame=false;
Ext.ux.GroupTab.superclass.initComponent.call(this);
this.addEvents("activate","deactivate","changemainitem","beforetabchange","tabchange");
this.setLayout(new Ext.layout.CardLayout({deferredRender:this.deferredRender}));
if(!this.stack){this.stack=Ext.TabPanel.AccessStack()
}this.initItems();
this.on("beforerender",function(){this.groupEl=this.ownerCt.getGroupEl(this)
},this);
this.on("add",this.onAdd,this,{target:this});
this.on("remove",this.onRemove,this,{target:this});
if(this.mainItem!==undefined){var item=(typeof this.mainItem=="object")?this.mainItem:this.items.get(this.mainItem);
delete this.mainItem;
this.setMainItem(item)
}},setActiveTab:function(item){item=this.getComponent(item);
if(!item){return false
}if(!this.rendered){this.activeTab=item;
return true
}if(this.activeTab!=item&&this.fireEvent("beforetabchange",this,item,this.activeTab)!==false){if(this.activeTab&&this.activeTab!=this.mainItem){var oldEl=this.getTabEl(this.activeTab);
if(oldEl){Ext.fly(oldEl).removeClass("x-grouptabs-strip-active")
}}var el=this.getTabEl(item);
Ext.fly(el).addClass("x-grouptabs-strip-active");
this.activeTab=item;
this.stack.add(item);
this.layout.setActiveItem(item);
if(this.layoutOnTabChange&&item.doLayout){item.doLayout()
}if(this.scrolling){this.scrollToTab(item,this.animScroll)
}this.fireEvent("tabchange",this,item);
return true
}return false
},getTabEl:function(item){if(item==this.mainItem){return this.groupEl
}return Ext.TabPanel.prototype.getTabEl.call(this,item)
},onRender:function(ct,position){Ext.ux.GroupTab.superclass.onRender.call(this,ct,position);
this.strip=Ext.fly(this.groupEl).createChild({tag:"ul",cls:"x-grouptabs-sub"});
this.tooltip=new Ext.ToolTip({target:this.groupEl,delegate:"a.x-grouptabs-text",trackMouse:true,renderTo:document.body,listeners:{beforeshow:function(tip){var item=(tip.triggerElement.parentNode===this.mainItem.tabEl)?this.mainItem:this.findById(tip.triggerElement.parentNode.id.split(this.idDelimiter)[1]);
if(!item.tabTip){return false
}tip.body.dom.innerHTML=item.tabTip
},scope:this}});
if(!this.itemTpl){var tt=new Ext.Template('<li class="{cls}" id="{id}">','<a onclick="return false;" class="x-grouptabs-text {iconCls}">{text}</a>',"</li>");
tt.disableFormats=true;
tt.compile();
Ext.ux.GroupTab.prototype.itemTpl=tt
}this.items.each(this.initTab,this)
},afterRender:function(){Ext.ux.GroupTab.superclass.afterRender.call(this);
if(this.activeTab!==undefined){var item=(typeof this.activeTab=="object")?this.activeTab:this.items.get(this.activeTab);
delete this.activeTab;
this.setActiveTab(item)
}},initTab:function(item,index){var before=this.strip.dom.childNodes[index];
var p=Ext.TabPanel.prototype.getTemplateArgs.call(this,item);
if(item===this.mainItem){item.tabEl=this.groupEl;
p.cls+=" x-grouptabs-main-item"
}var el=before?this.itemTpl.insertBefore(before,p):this.itemTpl.append(this.strip,p);
item.tabEl=item.tabEl||el;
item.on("disable",this.onItemDisabled,this);
item.on("enable",this.onItemEnabled,this);
item.on("titlechange",this.onItemTitleChanged,this);
item.on("iconchange",this.onItemIconChanged,this);
item.on("beforeshow",this.onBeforeShowItem,this)
},setMainItem:function(item){item=this.getComponent(item);
if(!item||this.fireEvent("changemainitem",this,item,this.mainItem)===false){return
}this.mainItem=item
},getMainItem:function(){return this.mainItem||null
},onBeforeShowItem:function(item){if(item!=this.activeTab){this.setActiveTab(item);
return false
}},onAdd:function(gt,item,index){if(this.rendered){this.initTab.call(this,item,index)
}},onRemove:function(tp,item){Ext.destroy(Ext.get(this.getTabEl(item)));
this.stack.remove(item);
item.un("disable",this.onItemDisabled,this);
item.un("enable",this.onItemEnabled,this);
item.un("titlechange",this.onItemTitleChanged,this);
item.un("iconchange",this.onItemIconChanged,this);
item.un("beforeshow",this.onBeforeShowItem,this);
if(item==this.activeTab){var next=this.stack.next();
if(next){this.setActiveTab(next)
}else{if(this.items.getCount()>0){this.setActiveTab(0)
}else{this.activeTab=null
}}}},onBeforeAdd:function(item){var existing=item.events?(this.items.containsKey(item.getItemId())?item:null):this.items.get(item);
if(existing){this.setActiveTab(item);
return false
}Ext.TabPanel.superclass.onBeforeAdd.apply(this,arguments);
var es=item.elements;
item.elements=es?es.replace(",header",""):es;
item.border=(item.border===true)
},onItemDisabled:Ext.TabPanel.prototype.onItemDisabled,onItemEnabled:Ext.TabPanel.prototype.onItemEnabled,onItemTitleChanged:function(item){var el=this.getTabEl(item);
if(el){Ext.fly(el).child("a.x-grouptabs-text",true).innerHTML=item.title
}},onItemIconChanged:function(item,iconCls,oldCls){var el=this.getTabEl(item);
if(el){Ext.fly(el).child("a.x-grouptabs-text").replaceClass(oldCls,iconCls)
}},beforeDestroy:function(){Ext.TabPanel.prototype.beforeDestroy.call(this);
this.tooltip.destroy()
}});
Ext.reg("grouptab",Ext.ux.GroupTab);/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns("Ext.ux");
Ext.ux.GroupTabPanel=Ext.extend(Ext.TabPanel,{tabPosition:"left",alternateColor:false,alternateCls:"x-grouptabs-panel-alt",defaultType:"grouptab",deferredRender:false,activeGroup:null,initComponent:function(){Ext.ux.GroupTabPanel.superclass.initComponent.call(this);
this.addEvents("beforegroupchange","groupchange");
this.elements="body,header";
this.stripTarget="header";
this.tabPosition=this.tabPosition=="right"?"right":"left";
this.addClass("x-grouptabs-panel");
if(this.tabStyle&&this.tabStyle!=""){this.addClass("x-grouptabs-panel-"+this.tabStyle)
}if(this.alternateColor){this.addClass(this.alternateCls)
}this.on("beforeadd",function(gtp,item,index){this.initGroup(item,index)
});
this.items.each(function(item){item.on("tabchange",function(item){this.fireEvent("tabchange",this,item.activeTab)
},this)
},this)
},initEvents:function(){this.mon(this.strip,"mousedown",this.onStripMouseDown,this)
},onRender:function(ct,position){Ext.TabPanel.superclass.onRender.call(this,ct,position);
if(this.plain){var pos=this.tabPosition=="top"?"header":"footer";
this[pos].addClass("x-tab-panel-"+pos+"-plain")
}var st=this[this.stripTarget];
this.stripWrap=st.createChild({cls:"x-tab-strip-wrap ",cn:{tag:"ul",cls:"x-grouptabs-strip x-grouptabs-tab-strip-"+this.tabPosition}});
var beforeEl=(this.tabPosition=="bottom"?this.stripWrap:null);
this.strip=new Ext.Element(this.stripWrap.dom.firstChild);
this.header.addClass("x-grouptabs-panel-header");
this.bwrap.addClass("x-grouptabs-bwrap");
this.body.addClass("x-tab-panel-body-"+this.tabPosition+" x-grouptabs-panel-body");
if(!this.groupTpl){var tt=new Ext.Template('<li class="{cls}" id="{id}">','<a class="x-grouptabs-expand" onclick="return false;"></a>','<a class="x-grouptabs-text {iconCls}" href="#" onclick="return false;">',"<span>{text}</span></a>","</li>");
tt.disableFormats=true;
tt.compile();
Ext.ux.GroupTabPanel.prototype.groupTpl=tt
}this.items.each(this.initGroup,this)
},afterRender:function(){Ext.ux.GroupTabPanel.superclass.afterRender.call(this);
this.tabJoint=Ext.fly(this.body.dom.parentNode).createChild({cls:"x-tab-joint"});
this.addClass("x-tab-panel-"+this.tabPosition);
this.header.setWidth(this.tabWidth);
if(this.activeGroup!==undefined){var group=(typeof this.activeGroup=="object")?this.activeGroup:this.items.get(this.activeGroup);
delete this.activeGroup;
this.setActiveGroup(group);
group.setActiveTab(group.getMainItem())
}},getGroupEl:Ext.TabPanel.prototype.getTabEl,findTargets:function(e){var item=null,itemEl=e.getTarget("li",this.strip);
if(itemEl){item=this.findById(itemEl.id.split(this.idDelimiter)[1]);
if(item.disabled){return{expand:null,item:null,el:null}
}}return{expand:e.getTarget(".x-grouptabs-expand",this.strip),isGroup:!e.getTarget("ul.x-grouptabs-sub",this.strip),item:item,el:itemEl}
},onStripMouseDown:function(e){if(e.button!=0){return
}e.preventDefault();
var t=this.findTargets(e);
if(t.expand){this.toggleGroup(t.el)
}else{if(t.item){if(t.isGroup){t.item.setActiveTab(t.item.getMainItem())
}else{t.item.ownerCt.setActiveTab(t.item)
}}}},expandGroup:function(groupEl){if(groupEl.isXType){groupEl=this.getGroupEl(groupEl)
}Ext.fly(groupEl).addClass("x-grouptabs-expanded");
this.syncTabJoint()
},toggleGroup:function(groupEl){if(groupEl.isXType){groupEl=this.getGroupEl(groupEl)
}Ext.fly(groupEl).toggleClass("x-grouptabs-expanded");
this.syncTabJoint()
},collapseGroup:function(groupEl){if(groupEl.isXType){groupEl=this.getGroupEl(groupEl)
}Ext.fly(groupEl).removeClass("x-grouptabs-expanded");
this.syncTabJoint()
},syncTabJoint:function(groupEl){if(!this.tabJoint){return
}groupEl=groupEl||this.getGroupEl(this.activeGroup);
if(groupEl){this.tabJoint.setHeight(Ext.fly(groupEl).getHeight()-2);
var y=Ext.isGecko2?0:1;
if(this.tabPosition=="left"){this.tabJoint.alignTo(groupEl,"tl-tr",[-2,y])
}else{this.tabJoint.alignTo(groupEl,"tr-tl",[1,y])
}}else{this.tabJoint.hide()
}},getActiveTab:function(){if(!this.activeGroup){return null
}return this.activeGroup.getTabEl(this.activeGroup.activeTab)||null
},onResize:function(){Ext.ux.GroupTabPanel.superclass.onResize.apply(this,arguments);
this.syncTabJoint()
},createCorner:function(el,pos){return Ext.fly(el).createChild({cls:"x-grouptabs-corner x-grouptabs-corner-"+pos})
},initGroup:function(group,index){var before=this.strip.dom.childNodes[index],p=this.getTemplateArgs(group);
if(index===0){p.cls+=" x-tab-first"
}p.cls+=" x-grouptabs-main";
p.text=group.getMainItem().title;
var el=before?this.groupTpl.insertBefore(before,p):this.groupTpl.append(this.strip,p),tl=this.createCorner(el,"top-"+this.tabPosition),bl=this.createCorner(el,"bottom-"+this.tabPosition);
group.tabEl=el;
if(group.expanded){this.expandGroup(el)
}if(Ext.isIE6||(Ext.isIE&&!Ext.isStrict)){bl.setLeft("-10px");
bl.setBottom("-5px");
tl.setLeft("-10px");
tl.setTop("-5px")
}this.mon(group,{scope:this,changemainitem:this.onGroupChangeMainItem,beforetabchange:this.onGroupBeforeTabChange})
},setActiveGroup:function(group){group=this.getComponent(group);
if(!group){return false
}if(!this.rendered){this.activeGroup=group;
return true
}if(this.activeGroup!=group&&this.fireEvent("beforegroupchange",this,group,this.activeGroup)!==false){if(this.activeGroup){this.activeGroup.activeTab=null;
var oldEl=this.getGroupEl(this.activeGroup);
if(oldEl){Ext.fly(oldEl).removeClass("x-grouptabs-strip-active")
}}var groupEl=this.getGroupEl(group);
Ext.fly(groupEl).addClass("x-grouptabs-strip-active");
this.activeGroup=group;
this.stack.add(group);
this.layout.setActiveItem(group);
this.syncTabJoint(groupEl);
this.fireEvent("groupchange",this,group);
return true
}return false
},onGroupBeforeTabChange:function(group,newTab,oldTab){if(group!==this.activeGroup||newTab!==oldTab){this.strip.select(".x-grouptabs-sub > li.x-grouptabs-strip-active",true).removeClass("x-grouptabs-strip-active")
}this.expandGroup(this.getGroupEl(group));
if(group!==this.activeGroup){return this.setActiveGroup(group)
}},getFrameHeight:function(){var h=this.el.getFrameWidth("tb");
h+=(this.tbar?this.tbar.getHeight():0)+(this.bbar?this.bbar.getHeight():0);
return h
},adjustBodyWidth:function(w){return w-this.tabWidth
}});
Ext.reg("grouptabpanel",Ext.ux.GroupTabPanel);/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns("Ext.ux.grid");
Ext.ux.grid.CheckColumn=Ext.extend(Ext.grid.Column,{processEvent:function(name,e,grid,rowIndex,colIndex){if(name=="mousedown"){var record=grid.store.getAt(rowIndex);
record.set(this.dataIndex,!record.data[this.dataIndex]);
return false
}else{return Ext.grid.ActionColumn.superclass.processEvent.apply(this,arguments)
}},renderer:function(v,p,record){p.css+=" x-grid3-check-col-td";
return String.format('<div class="x-grid3-check-col{0}">&#160;</div>',v?"-on":"")
},init:Ext.emptyFn});
Ext.preg("checkcolumn",Ext.ux.grid.CheckColumn);
Ext.grid.CheckColumn=Ext.ux.grid.CheckColumn;
Ext.grid.Column.types.checkcolumn=Ext.ux.grid.CheckColumn;/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns("Ext.ux.form");
Ext.ux.form.ItemSelector=Ext.extend(Ext.form.Field,{hideNavIcons:false,imagePath:"",iconUp:"up2.gif",iconDown:"down2.gif",iconLeft:"left2.gif",iconRight:"right2.gif",iconTop:"top2.gif",iconBottom:"bottom2.gif",drawUpIcon:true,drawDownIcon:true,drawLeftIcon:true,drawRightIcon:true,drawTopIcon:true,drawBotIcon:true,delimiter:",",bodyStyle:null,border:false,defaultAutoCreate:{tag:"div"},multiselects:null,initComponent:function(){Ext.ux.form.ItemSelector.superclass.initComponent.call(this);
this.addEvents({rowdblclick:true,change:true})
},onRender:function(ct,position){Ext.ux.form.ItemSelector.superclass.onRender.call(this,ct,position);
var msConfig=[{legend:"Available",draggable:true,droppable:true,width:80,height:100},{legend:"Selected",droppable:true,draggable:true,width:80,height:100}];
this.fromMultiselect=new Ext.ux.form.MultiSelect(Ext.applyIf(this.multiselects[0],msConfig[0]));
this.fromMultiselect.on("dblclick",this.onRowDblClick,this);
this.toMultiselect=new Ext.ux.form.MultiSelect(Ext.applyIf(this.multiselects[1],msConfig[1]));
this.toMultiselect.on("dblclick",this.onRowDblClick,this);
var p=new Ext.Panel({bodyStyle:this.bodyStyle,border:this.border,layout:"table",layoutConfig:{columns:3}});
p.add(this.fromMultiselect);
var icons=new Ext.Panel({header:false});
p.add(icons);
p.add(this.toMultiselect);
p.render(this.el);
icons.el.down("."+icons.bwrapCls).remove();
if(this.imagePath!=""&&this.imagePath.charAt(this.imagePath.length-1)!="/"){this.imagePath+="/"
}this.iconUp=this.imagePath+(this.iconUp||"up2.gif");
this.iconDown=this.imagePath+(this.iconDown||"down2.gif");
this.iconLeft=this.imagePath+(this.iconLeft||"left2.gif");
this.iconRight=this.imagePath+(this.iconRight||"right2.gif");
this.iconTop=this.imagePath+(this.iconTop||"top2.gif");
this.iconBottom=this.imagePath+(this.iconBottom||"bottom2.gif");
var el=icons.getEl();
this.toTopIcon=el.createChild({tag:"img",src:this.iconTop,style:{cursor:"pointer",margin:"2px"}});
el.createChild({tag:"br"});
this.upIcon=el.createChild({tag:"img",src:this.iconUp,style:{cursor:"pointer",margin:"2px"}});
el.createChild({tag:"br"});
this.addIcon=el.createChild({tag:"img",src:this.iconRight,style:{cursor:"pointer",margin:"2px"}});
el.createChild({tag:"br"});
this.removeIcon=el.createChild({tag:"img",src:this.iconLeft,style:{cursor:"pointer",margin:"2px"}});
el.createChild({tag:"br"});
this.downIcon=el.createChild({tag:"img",src:this.iconDown,style:{cursor:"pointer",margin:"2px"}});
el.createChild({tag:"br"});
this.toBottomIcon=el.createChild({tag:"img",src:this.iconBottom,style:{cursor:"pointer",margin:"2px"}});
this.toTopIcon.on("click",this.toTop,this);
this.upIcon.on("click",this.up,this);
this.downIcon.on("click",this.down,this);
this.toBottomIcon.on("click",this.toBottom,this);
this.addIcon.on("click",this.fromTo,this);
this.removeIcon.on("click",this.toFrom,this);
if(!this.drawUpIcon||this.hideNavIcons){this.upIcon.dom.style.display="none"
}if(!this.drawDownIcon||this.hideNavIcons){this.downIcon.dom.style.display="none"
}if(!this.drawLeftIcon||this.hideNavIcons){this.addIcon.dom.style.display="none"
}if(!this.drawRightIcon||this.hideNavIcons){this.removeIcon.dom.style.display="none"
}if(!this.drawTopIcon||this.hideNavIcons){this.toTopIcon.dom.style.display="none"
}if(!this.drawBotIcon||this.hideNavIcons){this.toBottomIcon.dom.style.display="none"
}var tb=p.body.first();
this.el.setWidth(p.body.first().getWidth());
p.body.removeClass();
this.hiddenName=this.name;
var hiddenTag={tag:"input",type:"hidden",value:"",name:this.name};
this.hiddenField=this.el.createChild(hiddenTag)
},doLayout:function(){if(this.rendered){this.fromMultiselect.fs.doLayout();
this.toMultiselect.fs.doLayout()
}},afterRender:function(){Ext.ux.form.ItemSelector.superclass.afterRender.call(this);
this.toStore=this.toMultiselect.store;
this.toStore.on("add",this.valueChanged,this);
this.toStore.on("remove",this.valueChanged,this);
this.toStore.on("load",this.valueChanged,this);
this.valueChanged(this.toStore)
},toTop:function(){var selectionsArray=this.toMultiselect.view.getSelectedIndexes();
var records=[];
if(selectionsArray.length>0){selectionsArray.sort();
for(var i=0;
i<selectionsArray.length;
i++){record=this.toMultiselect.view.store.getAt(selectionsArray[i]);
records.push(record)
}selectionsArray=[];
for(var i=records.length-1;
i>-1;
i--){record=records[i];
this.toMultiselect.view.store.remove(record);
this.toMultiselect.view.store.insert(0,record);
selectionsArray.push(((records.length-1)-i))
}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(selectionsArray)
},toBottom:function(){var selectionsArray=this.toMultiselect.view.getSelectedIndexes();
var records=[];
if(selectionsArray.length>0){selectionsArray.sort();
for(var i=0;
i<selectionsArray.length;
i++){record=this.toMultiselect.view.store.getAt(selectionsArray[i]);
records.push(record)
}selectionsArray=[];
for(var i=0;
i<records.length;
i++){record=records[i];
this.toMultiselect.view.store.remove(record);
this.toMultiselect.view.store.add(record);
selectionsArray.push((this.toMultiselect.view.store.getCount())-(records.length-i))
}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(selectionsArray)
},up:function(){var record=null;
var selectionsArray=this.toMultiselect.view.getSelectedIndexes();
selectionsArray.sort();
var newSelectionsArray=[];
if(selectionsArray.length>0){for(var i=0;
i<selectionsArray.length;
i++){record=this.toMultiselect.view.store.getAt(selectionsArray[i]);
if((selectionsArray[i]-1)>=0){this.toMultiselect.view.store.remove(record);
this.toMultiselect.view.store.insert(selectionsArray[i]-1,record);
newSelectionsArray.push(selectionsArray[i]-1)
}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(newSelectionsArray)
}},down:function(){var record=null;
var selectionsArray=this.toMultiselect.view.getSelectedIndexes();
selectionsArray.sort();
selectionsArray.reverse();
var newSelectionsArray=[];
if(selectionsArray.length>0){for(var i=0;
i<selectionsArray.length;
i++){record=this.toMultiselect.view.store.getAt(selectionsArray[i]);
if((selectionsArray[i]+1)<this.toMultiselect.view.store.getCount()){this.toMultiselect.view.store.remove(record);
this.toMultiselect.view.store.insert(selectionsArray[i]+1,record);
newSelectionsArray.push(selectionsArray[i]+1)
}}this.toMultiselect.view.refresh();
this.toMultiselect.view.select(newSelectionsArray)
}},fromTo:function(){var selectionsArray=this.fromMultiselect.view.getSelectedIndexes();
var records=[];
if(selectionsArray.length>0){for(var i=0;
i<selectionsArray.length;
i++){record=this.fromMultiselect.view.store.getAt(selectionsArray[i]);
records.push(record)
}if(!this.allowDup){selectionsArray=[]
}for(var i=0;
i<records.length;
i++){record=records[i];
if(this.allowDup){var x=new Ext.data.Record();
record.id=x.id;
delete x;
this.toMultiselect.view.store.add(record)
}else{this.fromMultiselect.view.store.remove(record);
this.toMultiselect.view.store.add(record);
selectionsArray.push((this.toMultiselect.view.store.getCount()-1))
}}}this.toMultiselect.view.refresh();
this.fromMultiselect.view.refresh();
var si=this.toMultiselect.store.sortInfo;
if(si){this.toMultiselect.store.sort(si.field,si.direction)
}this.toMultiselect.view.select(selectionsArray)
},toFrom:function(){var selectionsArray=this.toMultiselect.view.getSelectedIndexes();
var records=[];
if(selectionsArray.length>0){for(var i=0;
i<selectionsArray.length;
i++){record=this.toMultiselect.view.store.getAt(selectionsArray[i]);
records.push(record)
}selectionsArray=[];
for(var i=0;
i<records.length;
i++){record=records[i];
this.toMultiselect.view.store.remove(record);
if(!this.allowDup){this.fromMultiselect.view.store.add(record);
selectionsArray.push((this.fromMultiselect.view.store.getCount()-1))
}}}this.fromMultiselect.view.refresh();
this.toMultiselect.view.refresh();
var si=this.fromMultiselect.store.sortInfo;
if(si){this.fromMultiselect.store.sort(si.field,si.direction)
}this.fromMultiselect.view.select(selectionsArray)
},valueChanged:function(store){var record=null;
var values=[];
for(var i=0;
i<store.getCount();
i++){record=store.getAt(i);
values.push(record.get(this.toMultiselect.valueField))
}this.hiddenField.dom.value=values.join(this.delimiter);
this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value)
},getValue:function(){return this.hiddenField.dom.value
},onRowDblClick:function(vw,index,node,e){if(vw==this.toMultiselect.view){this.toFrom()
}else{if(vw==this.fromMultiselect.view){this.fromTo()
}}return this.fireEvent("rowdblclick",vw,index,node,e)
},reset:function(){range=this.toMultiselect.store.getRange();
this.toMultiselect.store.removeAll();
this.fromMultiselect.store.add(range);
var si=this.fromMultiselect.store.sortInfo;
if(si){this.fromMultiselect.store.sort(si.field,si.direction)
}this.valueChanged(this.toMultiselect.store)
}});
Ext.reg("itemselector",Ext.ux.form.ItemSelector);
Ext.ux.ItemSelector=Ext.ux.form.ItemSelector;/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns("Ext.ux.form");
Ext.ux.form.MultiSelect=Ext.extend(Ext.form.Field,{ddReorder:false,appendOnly:false,width:100,height:100,displayField:0,valueField:1,allowBlank:true,minSelections:0,maxSelections:Number.MAX_VALUE,blankText:Ext.form.TextField.prototype.blankText,minSelectionsText:"Minimum {0} item(s) required",maxSelectionsText:"Maximum {0} item(s) allowed",delimiter:",",defaultAutoCreate:{tag:"div"},initComponent:function(){Ext.ux.form.MultiSelect.superclass.initComponent.call(this);
if(Ext.isArray(this.store)){if(Ext.isArray(this.store[0])){this.store=new Ext.data.ArrayStore({fields:["value","text"],data:this.store});
this.valueField="value"
}else{this.store=new Ext.data.ArrayStore({fields:["text"],data:this.store,expandData:true});
this.valueField="text"
}this.displayField="text"
}else{this.store=Ext.StoreMgr.lookup(this.store)
}this.addEvents({dblclick:true,click:true,change:true,drop:true})
},onRender:function(ct,position){Ext.ux.form.MultiSelect.superclass.onRender.call(this,ct,position);
var fs=this.fs=new Ext.form.FieldSet({renderTo:this.el,title:this.legend,height:this.height,width:this.width,style:"padding:0;",tbar:this.tbar});
fs.body.addClass("ux-mselect");
this.view=new Ext.ListView({multiSelect:true,store:this.store,columns:[{header:"Value",width:1,dataIndex:this.displayField}],hideHeaders:true});
fs.add(this.view);
this.view.on("click",this.onViewClick,this);
this.view.on("beforeclick",this.onViewBeforeClick,this);
this.view.on("dblclick",this.onViewDblClick,this);
this.hiddenName=this.name||Ext.id();
var hiddenTag={tag:"input",type:"hidden",value:"",name:this.hiddenName};
this.hiddenField=this.el.createChild(hiddenTag);
this.hiddenField.dom.disabled=this.hiddenName!=this.name;
fs.doLayout()
},afterRender:function(){Ext.ux.form.MultiSelect.superclass.afterRender.call(this);
if(this.ddReorder&&!this.dragGroup&&!this.dropGroup){this.dragGroup=this.dropGroup="MultiselectDD-"+Ext.id()
}if(this.draggable||this.dragGroup){this.dragZone=new Ext.ux.form.MultiSelect.DragZone(this,{ddGroup:this.dragGroup})
}if(this.droppable||this.dropGroup){this.dropZone=new Ext.ux.form.MultiSelect.DropZone(this,{ddGroup:this.dropGroup})
}},onViewClick:function(vw,index,node,e){this.fireEvent("change",this,this.getValue(),this.hiddenField.dom.value);
this.hiddenField.dom.value=this.getValue();
this.fireEvent("click",this,e);
this.validate()
},onViewBeforeClick:function(vw,index,node,e){if(this.disabled||this.readOnly){return false
}},onViewDblClick:function(vw,index,node,e){return this.fireEvent("dblclick",vw,index,node,e)
},getValue:function(valueField){var returnArray=[];
var selectionsArray=this.view.getSelectedIndexes();
if(selectionsArray.length==0){return""
}for(var i=0;
i<selectionsArray.length;
i++){returnArray.push(this.store.getAt(selectionsArray[i]).get((valueField!=null)?valueField:this.valueField))
}return returnArray.join(this.delimiter)
},setValue:function(values){var index;
var selections=[];
this.view.clearSelections();
this.hiddenField.dom.value="";
if(!values||(values=="")){return
}if(!Ext.isArray(values)){values=values.split(this.delimiter)
}for(var i=0;
i<values.length;
i++){index=this.view.store.indexOf(this.view.store.query(this.valueField,new RegExp("^"+values[i]+"$","i")).itemAt(0));
selections.push(index)
}this.view.select(selections);
this.hiddenField.dom.value=this.getValue();
this.validate()
},reset:function(){this.setValue("")
},getRawValue:function(valueField){var tmp=this.getValue(valueField);
if(tmp.length){tmp=tmp.split(this.delimiter)
}else{tmp=[]
}return tmp
},setRawValue:function(values){setValue(values)
},validateValue:function(value){if(value.length<1){if(this.allowBlank){this.clearInvalid();
return true
}else{this.markInvalid(this.blankText);
return false
}}if(value.length<this.minSelections){this.markInvalid(String.format(this.minSelectionsText,this.minSelections));
return false
}if(value.length>this.maxSelections){this.markInvalid(String.format(this.maxSelectionsText,this.maxSelections));
return false
}return true
},disable:function(){this.disabled=true;
this.hiddenField.dom.disabled=true;
this.fs.disable()
},enable:function(){this.disabled=false;
this.hiddenField.dom.disabled=false;
this.fs.enable()
},destroy:function(){Ext.destroy(this.fs,this.dragZone,this.dropZone);
Ext.ux.form.MultiSelect.superclass.destroy.call(this)
}});
Ext.reg("multiselect",Ext.ux.form.MultiSelect);
Ext.ux.Multiselect=Ext.ux.form.MultiSelect;
Ext.ux.form.MultiSelect.DragZone=function(ms,config){this.ms=ms;
this.view=ms.view;
var ddGroup=config.ddGroup||"MultiselectDD";
var dd;
if(Ext.isArray(ddGroup)){dd=ddGroup.shift()
}else{dd=ddGroup;
ddGroup=null
}Ext.ux.form.MultiSelect.DragZone.superclass.constructor.call(this,this.ms.fs.body,{containerScroll:true,ddGroup:dd});
this.setDraggable(ddGroup)
};
Ext.extend(Ext.ux.form.MultiSelect.DragZone,Ext.dd.DragZone,{onInitDrag:function(x,y){var el=Ext.get(this.dragData.ddel.cloneNode(true));
this.proxy.update(el.dom);
el.setWidth(el.child("em").getWidth());
this.onStartDrag(x,y);
return true
},collectSelection:function(data){data.repairXY=Ext.fly(this.view.getSelectedNodes()[0]).getXY();
var i=0;
this.view.store.each(function(rec){if(this.view.isSelected(i)){var n=this.view.getNode(i);
var dragNode=n.cloneNode(true);
dragNode.id=Ext.id();
data.ddel.appendChild(dragNode);
data.records.push(this.view.store.getAt(i));
data.viewNodes.push(n)
}i++
},this)
},onEndDrag:function(data,e){var d=Ext.get(this.dragData.ddel);
if(d&&d.hasClass("multi-proxy")){d.remove()
}},getDragData:function(e){var target=this.view.findItemFromChild(e.getTarget());
if(target){if(!this.view.isSelected(target)&&!e.ctrlKey&&!e.shiftKey){this.view.select(target);
this.ms.setValue(this.ms.getValue())
}if(this.view.getSelectionCount()==0||e.ctrlKey||e.shiftKey){return false
}var dragData={sourceView:this.view,viewNodes:[],records:[]};
if(this.view.getSelectionCount()==1){var i=this.view.getSelectedIndexes()[0];
var n=this.view.getNode(i);
dragData.viewNodes.push(dragData.ddel=n);
dragData.records.push(this.view.store.getAt(i));
dragData.repairXY=Ext.fly(n).getXY()
}else{dragData.ddel=document.createElement("div");
dragData.ddel.className="multi-proxy";
this.collectSelection(dragData)
}return dragData
}return false
},getRepairXY:function(e){return this.dragData.repairXY
},setDraggable:function(ddGroup){if(!ddGroup){return
}if(Ext.isArray(ddGroup)){Ext.each(ddGroup,this.setDraggable,this);
return
}this.addToGroup(ddGroup)
}});
Ext.ux.form.MultiSelect.DropZone=function(ms,config){this.ms=ms;
this.view=ms.view;
var ddGroup=config.ddGroup||"MultiselectDD";
var dd;
if(Ext.isArray(ddGroup)){dd=ddGroup.shift()
}else{dd=ddGroup;
ddGroup=null
}Ext.ux.form.MultiSelect.DropZone.superclass.constructor.call(this,this.ms.fs.body,{containerScroll:true,ddGroup:dd});
this.setDroppable(ddGroup)
};
Ext.extend(Ext.ux.form.MultiSelect.DropZone,Ext.dd.DropZone,{getTargetFromEvent:function(e){var target=e.getTarget();
return target
},getDropPoint:function(e,n,dd){if(n==this.ms.fs.body.dom){return"below"
}var t=Ext.lib.Dom.getY(n),b=t+n.offsetHeight;
var c=t+(b-t)/2;
var y=Ext.lib.Event.getPageY(e);
if(y<=c){return"above"
}else{return"below"
}},isValidDropPoint:function(pt,n,data){if(!data.viewNodes||(data.viewNodes.length!=1)){return true
}var d=data.viewNodes[0];
if(d==n){return false
}if((pt=="below")&&(n.nextSibling==d)){return false
}if((pt=="above")&&(n.previousSibling==d)){return false
}return true
},onNodeEnter:function(n,dd,e,data){return false
},onNodeOver:function(n,dd,e,data){var dragElClass=this.dropNotAllowed;
var pt=this.getDropPoint(e,n,dd);
if(this.isValidDropPoint(pt,n,data)){if(this.ms.appendOnly){return"x-tree-drop-ok-below"
}if(pt){var targetElClass;
if(pt=="above"){dragElClass=n.previousSibling?"x-tree-drop-ok-between":"x-tree-drop-ok-above";
targetElClass="x-view-drag-insert-above"
}else{dragElClass=n.nextSibling?"x-tree-drop-ok-between":"x-tree-drop-ok-below";
targetElClass="x-view-drag-insert-below"
}if(this.lastInsertClass!=targetElClass){Ext.fly(n).replaceClass(this.lastInsertClass,targetElClass);
this.lastInsertClass=targetElClass
}}}return dragElClass
},onNodeOut:function(n,dd,e,data){this.removeDropIndicators(n)
},onNodeDrop:function(n,dd,e,data){if(this.ms.fireEvent("drop",this,n,dd,e,data)===false){return false
}var pt=this.getDropPoint(e,n,dd);
if(n!=this.ms.fs.body.dom){n=this.view.findItemFromChild(n)
}if(this.ms.appendOnly){insertAt=this.view.store.getCount()
}else{insertAt=n==this.ms.fs.body.dom?this.view.store.getCount()-1:this.view.indexOf(n);
if(pt=="below"){insertAt++
}}var dir=false;
if(data.sourceView==this.view){if(pt=="below"){if(data.viewNodes[0]==n){data.viewNodes.shift()
}}else{if(data.viewNodes[data.viewNodes.length-1]==n){data.viewNodes.pop()
}}if(!data.viewNodes.length){return false
}if(insertAt>this.view.store.indexOf(data.records[0])){dir="down";
insertAt--
}}for(var i=0;
i<data.records.length;
i++){var r=data.records[i];
if(data.sourceView){data.sourceView.store.remove(r)
}this.view.store.insert(dir=="down"?insertAt:insertAt++,r);
var si=this.view.store.sortInfo;
if(si){this.view.store.sort(si.field,si.direction)
}}return true
},removeDropIndicators:function(n){if(n){Ext.fly(n).removeClass(["x-view-drag-insert-above","x-view-drag-insert-left","x-view-drag-insert-right","x-view-drag-insert-below"]);
this.lastInsertClass="_noclass"
}},setDroppable:function(ddGroup){if(!ddGroup){return
}if(Ext.isArray(ddGroup)){Ext.each(ddGroup,this.setDroppable,this);
return
}this.addToGroup(ddGroup)
}});/*
 * Ext JS Library 3.3.1
 * Copyright(c) 2006-2010 Sencha Inc.
 * licensing@sencha.com
 * http://www.sencha.com/license
 */
Ext.ns("Ext.ux.form");
Ext.ux.form.FileUploadField=Ext.extend(Ext.form.TextField,{buttonText:"Browse...",buttonOnly:false,buttonOffset:3,readOnly:true,autoSize:Ext.emptyFn,initComponent:function(){Ext.ux.form.FileUploadField.superclass.initComponent.call(this);
this.addEvents("fileselected")
},onRender:function(ct,position){Ext.ux.form.FileUploadField.superclass.onRender.call(this,ct,position);
this.wrap=this.el.wrap({cls:"x-form-field-wrap x-form-file-wrap"});
this.el.addClass("x-form-file-text");
this.el.dom.removeAttribute("name");
this.createFileInput();
var btnCfg=Ext.applyIf(this.buttonCfg||{},{text:this.buttonText});
this.button=new Ext.Button(Ext.apply(btnCfg,{renderTo:this.wrap,cls:"x-form-file-btn"+(btnCfg.iconCls?" x-btn-icon":"")}));
if(this.buttonOnly){this.el.hide();
this.wrap.setWidth(this.button.getEl().getWidth())
}this.bindListeners();
this.resizeEl=this.positionEl=this.wrap
},bindListeners:function(){this.fileInput.on({scope:this,mouseenter:function(){this.button.addClass(["x-btn-over","x-btn-focus"])
},mouseleave:function(){this.button.removeClass(["x-btn-over","x-btn-focus","x-btn-click"])
},mousedown:function(){this.button.addClass("x-btn-click")
},mouseup:function(){this.button.removeClass(["x-btn-over","x-btn-focus","x-btn-click"])
},change:function(){var v=this.fileInput.dom.value;
this.setValue(v);
this.fireEvent("fileselected",this,v)
}})
},createFileInput:function(){this.fileInput=this.wrap.createChild({id:this.getFileInputId(),name:this.name||this.getId(),cls:"x-form-file",tag:"input",type:"file",size:1})
},reset:function(){this.fileInput.remove();
this.createFileInput();
this.bindListeners();
Ext.ux.form.FileUploadField.superclass.reset.call(this)
},getFileInputId:function(){return this.id+"-file"
},onResize:function(w,h){Ext.ux.form.FileUploadField.superclass.onResize.call(this,w,h);
this.wrap.setWidth(w);
if(!this.buttonOnly){var w=this.wrap.getWidth()-this.button.getEl().getWidth()-this.buttonOffset;
this.el.setWidth(w)
}},onDestroy:function(){Ext.ux.form.FileUploadField.superclass.onDestroy.call(this);
Ext.destroy(this.fileInput,this.button,this.wrap)
},onDisable:function(){Ext.ux.form.FileUploadField.superclass.onDisable.call(this);
this.doDisable(true)
},onEnable:function(){Ext.ux.form.FileUploadField.superclass.onEnable.call(this);
this.doDisable(false)
},doDisable:function(disabled){this.fileInput.dom.disabled=disabled;
this.button.setDisabled(disabled)
},preFocus:Ext.emptyFn,alignErrorIcon:function(){this.errorIcon.alignTo(this.wrap,"tl-tr",[2,0])
}});
Ext.reg("fileuploadfield",Ext.ux.form.FileUploadField);
Ext.form.FileUploadField=Ext.ux.form.FileUploadField;