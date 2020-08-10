Ext.ns("Openwis.Utils.Array");
Openwis.Utils.Array.inArray=function(array,value){for(var i=0;
i<array.length;
i++){if(array[i]==value){return true
}}return false
};
Openwis.Utils.Array.intersection=function(array1,array2){var intersection=[];
for(var i=0;
i<array1.length;
i++){if(Openwis.Utils.Array.inArray(array2,array1[i])){intersection.push(array1[i])
}}return intersection
};
Openwis.Utils.Array.containsAny=function(array1,array2){return Openwis.Utils.Array.intersection(array1,array2).length>0
};
Openwis.Utils.Array.isEmpty=function(array){return array.length<1
};Ext.ns("Openwis.Utils.Geo");
Openwis.Utils.Geo.featureToWKT=function(feature){var wktFormat=new OpenLayers.Format.WKT();
var wkt=wktFormat.write(feature);
return wkt
};
Openwis.Utils.Geo.WKTtoFeature=function(wkt){var wktFormat=new OpenLayers.Format.WKT();
var feature=wktFormat.read(wkt);
return feature
};
Openwis.Utils.Geo.getBoundsFromWKT=function(wkt){var feature=Openwis.Utils.Geo.WKTtoFeature(wkt);
return feature.geometry.getBounds()
};Ext.ns("Openwis.Utils.Date");
Openwis.Utils.Date.format=function(realDate){var dt=new Date(realDate);
return dt.format("d-M-Y")
};
Openwis.Utils.Date.formatDateTime=function(realDate){if(!realDate){return""
}var dt=new Date(realDate);
return dt.format("d-m-Y H:i")
};
Openwis.Utils.Date.formatDateTimeUTC=function(realDate){if(!realDate){return""
}var d="";
var m="";
var y="";
var heure="";
if(realDate!=null&&realDate.length>0){d=realDate.substring(8,10);
m=realDate.substring(5,7);
y=realDate.substring(0,4);
heure=realDate.substring(11,16)
}return d+"-"+m+"-"+y+" "+heure
};
Openwis.Utils.Date.formatDateTimeUTCfromLong=function(realDate){return Openwis.Utils.Date.formatDateTimeUTCfromLongWithPattern(realDate,"Y-m-d H:i:s")
};
Openwis.Utils.Date.formatDateUTCfromLong=function(realDate){return Openwis.Utils.Date.formatDateTimeUTCfromLongWithPattern(realDate,"Y-m-d")
};
Openwis.Utils.Date.formatDateTimeUTCfromLongWithPattern=function(realDate,pattern){if(!realDate){return""
}var date=new Date(realDate);
var localOffset=date.getTimezoneOffset()*60000;
var localTime=date.getTime();
var utc=localTime+localOffset;
date=new Date(utc);
return date.format(pattern)
};
Openwis.Utils.Date.formatForComponents=function(realDate){var dt=new Date(realDate);
return dt.format("d/m/Y")
};
Openwis.Utils.Date.formatTimeForComponents=function(realTime){var dt=new Date("1970-01-01T"+realTime);
return dt.format("H:i")
};
Openwis.Utils.Date.formatDateInterval=function(dateFrom,dateTo){var formattedFrom=Openwis.Utils.Date.formatDateForServer(dateFrom);
var formattedTo=Openwis.Utils.Date.formatDateForServer(dateTo);
return formattedFrom+"/"+formattedTo
};
Openwis.Utils.Date.formatTimeInterval=function(dateFrom,dateTo){return dateFrom+"Z/"+dateTo+"Z"
};
Openwis.Utils.Date.formatDateForServer=function(date){return date.format("Y-m-d")
};
Openwis.Utils.Date.dateToISOUTC=function(date){return Openwis.Utils.Date.toISOUTC(date,true,false)
};
Openwis.Utils.Date.timeToUTC=function(time){return String.leftPad(time.getUTCHours(),2,"0")+":"+String.leftPad(time.getUTCMinutes(),2,"0")
};
Openwis.Utils.Date.dateTimeToISOUTC=function(date){return Openwis.Utils.Date.toISOUTC(date,true,true)
};
Openwis.Utils.Date.toISOUTC=function(date,withDate,withTime){var dateStr="";
if(withDate){dateStr+=date.getUTCFullYear();
dateStr+="-";
dateStr+=String.leftPad(date.getUTCMonth()+1,2,"0");
dateStr+="-";
dateStr+=String.leftPad(date.getUTCDate(),2,"0")
}if(withTime){if(withDate){dateStr+="T"
}dateStr+=String.leftPad(date.getUTCHours(),2,"0");
dateStr+=":";
dateStr+=String.leftPad(date.getUTCMinutes(),2,"0");
dateStr+=":";
dateStr+=String.leftPad(date.getUTCSeconds(),2,"0");
dateStr+="Z"
}return dateStr
};
Openwis.Utils.Date.formatToISODate=function(date){return date.format("Y-m-d\\TH:i:s\\Z")
};
Openwis.Utils.Date.parseISODate=function(dateStr){return date.format("Y-m-d\\TH:i:s\\Z")
};
Openwis.Utils.Date.ISODateToCalendar=function(dateStr){if(dateStr!=null&&dateStr.length>0){return dateStr.substring(0,10)
}return""
};
Openwis.Utils.Date.ISODateToTime=function(dateStr){if(dateStr!=null&&dateStr.length>0){return dateStr.substring(11,16)
}return""
};Ext.ns("Openwis.Utils.Tooltip");
Openwis.Utils.Tooltip.Display=function addTooltip(value,metadata,record,rowIndex,colIndex,store){metadata.attr='ext:qtip="'+value+'"';
if(value.length>100){return value.substring(0,100)
}return value
};Ext.ns("Openwis.Utils.MessageBox");
Openwis.Utils.MessageBox.displayInternalError=function(resultFn,scope){Ext.MessageBox.show({title:Openwis.i18n("MessageBox.displayInternalError.Title"),msg:Openwis.i18n("MessageBox.displayInternalError.Message"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.ERROR,fn:resultFn,scope:scope})
};
Openwis.Utils.MessageBox.displayErrorMsg=function(errorMsg,resultFn,scope){Ext.MessageBox.show({title:Openwis.i18n("MessageBox.displayErrorMsg.Title"),msg:errorMsg,buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.ERROR,fn:resultFn,scope:scope})
};
Openwis.Utils.MessageBox.displayMustLogin=function(){Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n("MessageBox.displayMustLogin.Message"))
};
Openwis.Utils.MessageBox.displaySaveSuccessful=function(resultFn,scope){Ext.MessageBox.show({title:Openwis.i18n("MessageBox.displaySaveSuccessful.Title"),msg:Openwis.i18n("MessageBox.displaySaveSuccessful.Message"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.INFO,fn:resultFn,scope:scope})
};
Openwis.Utils.MessageBox.displaySuccessMsg=function(msg,resultFn,scope){Ext.MessageBox.show({title:Openwis.i18n("MessageBox.displaySuccessMsg.Title"),msg:msg,buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.INFO,fn:resultFn,scope:scope})
};
Openwis.Utils.MessageBox.warningMsg=function(title,msg,resultFn,scope){Ext.MessageBox.show({title:title,msg:msg,buttons:Ext.MessageBox.YESNO,icon:Ext.MessageBox.WARNING,fn:resultFn,scope:scope})
};if(Ext.form.DateField){Ext.override(Ext.form.DateField,{initComponent:function(){Ext.form.DateField.superclass.initComponent.call(this);
this.addEvents("select");
if(Ext.isString(this.minValue)){this.minValue=this.parseDate(this.minValue)
}if(Ext.isString(this.maxValue)){this.maxValue=this.parseDate(this.maxValue)
}this.initDisabledDays()
},initDisabledDays:function(){if(this.disabledDates){var dd=this.disabledDates,len=dd.length-1,re="(?:";
Ext.each(dd,function(d,i){re+=Ext.isDate(d)?"^"+Ext.escapeRe(d.dateFormat(this.format))+"$":dd[i];
if(i!=len){re+="|"
}},this);
this.disabledDatesRE=new RegExp(re+")")
}else{this.disabledDatesRE=null
}}})
}if(Ext.layout.BorderLayout){Ext.override(Ext.layout.BorderLayout,{onLayout:function(ct,target){var collapsed,i,c,pos,items=ct.items.items,len=items.length;
if(!this.rendered){collapsed=[];
for(i=0;
i<len;
i++){c=items[i];
pos=c.region;
if(c.collapsed){collapsed.push(c)
}c.collapsed=false;
if(!c.rendered){c.render(target,i);
c.getPositionEl().addClass("x-border-panel")
}this[pos]=pos!="center"&&c.split?new Ext.layout.BorderLayout.SplitRegion(this,c.initialConfig,pos):new Ext.layout.BorderLayout.Region(this,c.initialConfig,pos);
this[pos].render(target,c)
}this.rendered=true
}var size=this.getLayoutTargetSize();
if(size.width<this.minWidth){target.setStyle("width",this.minWidth+"px");
size.width=this.minWidth;
target.up("").setStyle("overflow","auto")
}else{target.setStyle("width","")
}if(size.width<20||size.height<20){if(collapsed){this.restoreCollapsed=collapsed
}return
}else{if(this.restoreCollapsed){collapsed=this.restoreCollapsed;
delete this.restoreCollapsed
}}var w=size.width,h=size.height,centerW=w,centerH=h,centerY=0,centerX=0,n=this.north,s=this.south,west=this.west,e=this.east,c=this.center,b,m,totalWidth,totalHeight;
if(!c&&Ext.layout.BorderLayout.WARN!==false){throw"No center region defined in BorderLayout "+ct.id
}if(n&&n.isVisible()){b=n.getSize();
m=n.getMargins();
b.width=w-(m.left+m.right);
b.x=m.left;
b.y=m.top;
centerY=b.height+b.y+m.bottom;
centerH-=centerY;
n.applyLayout(b)
}if(s&&s.isVisible()){b=s.getSize();
m=s.getMargins();
b.width=w-(m.left+m.right);
b.x=m.left;
totalHeight=(b.height+m.top+m.bottom);
b.y=h-totalHeight+m.top;
centerH-=totalHeight;
s.applyLayout(b)
}if(west&&west.isVisible()){b=west.getSize();
m=west.getMargins();
b.height=centerH-(m.top+m.bottom);
b.x=m.left;
b.y=centerY+m.top;
totalWidth=(b.width+m.left+m.right);
centerX+=totalWidth;
centerW-=totalWidth;
west.applyLayout(b)
}if(e&&e.isVisible()){b=e.getSize();
m=e.getMargins();
b.height=centerH-(m.top+m.bottom);
totalWidth=(b.width+m.left+m.right);
b.x=w-totalWidth+m.left;
b.y=centerY+m.top;
centerW-=totalWidth;
e.applyLayout(b)
}if(c){m=c.getMargins();
var centerBox={x:centerX+m.left,y:centerY+m.top,width:centerW-(m.left+m.right),height:centerH-(m.top+m.bottom)};
c.applyLayout(centerBox)
}if(collapsed){for(i=0,len=collapsed.length;
i<len;
i++){collapsed[i].collapse(false)
}}if(Ext.isIE&&Ext.isStrict){target.repaint()
}if(i=target.getStyle("overflow")&&i!="hidden"&&!this.adjustmentPass){var ts=this.getLayoutTargetSize();
if(ts.width!=size.width||ts.height!=size.height){this.adjustmentPass=true;
this.onLayout(ct,target)
}}delete this.adjustmentPass
}})
}if(Ext.ux.GroupTabPanel){Ext.override(Ext.ux.GroupTabPanel,{onRender:function(ct,position){Ext.TabPanel.superclass.onRender.call(this,ct,position);
if(this.plain){var pos=this.tabPosition=="top"?"header":"footer";
this[pos].addClass("x-tab-panel-"+pos+"-plain")
}var st=this[this.stripTarget];
this.stripWrap=st.createChild({cls:"x-tab-strip-wrap ",cn:{tag:"ul",cls:"x-grouptabs-strip x-grouptabs-tab-strip-"+this.tabPosition}});
var beforeEl=(this.tabPosition=="bottom"?this.stripWrap:null);
this.strip=new Ext.Element(this.stripWrap.dom.firstChild);
this.header.addClass("x-grouptabs-panel-header");
this.bwrap.addClass("x-grouptabs-bwrap");
this.body.addClass("x-tab-panel-body-"+this.tabPosition+" x-grouptabs-panel-body");
if(!this.groupTpl){var tt=new Ext.Template('<li class="{cls}" id="{id}">','<a class="x-grouptabs-text {iconCls}" href="#" onclick="return false;">',"<span>{text}</span></a>","</li>");
tt.disableFormats=true;
tt.compile();
Ext.ux.GroupTabPanel.prototype.groupTpl=tt
}this.items.each(this.initGroup,this)
},onGroupBeforeTabChange:function(group,newTab,oldTab){if(group!==this.activeGroup||newTab!==oldTab){this.strip.select(".x-grouptabs-sub > li.x-grouptabs-strip-active",true).removeClass("x-grouptabs-strip-active")
}this.expandGroup(this.getGroupEl(group));
var groupEl=this.getGroupEl(group);
if(this.items){for(var i=0;
i<this.items.getCount();
i++){var tmpVal=this.items.get(i);
if(groupEl!=tmpVal.groupEl){this.collapseGroup(tmpVal.groupEl)
}}}if(group!==this.activeGroup){return this.setActiveGroup(group)
}}})
};Ext.ns("Openwis.Utils");
Openwis.Utils.MessageMustLogin=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{closeAction:"close",autoScroll:false,resizable:false,layout:"table",title:Openwis.i18n("MessageMustLogin.title"),height:145,width:221,layoutConfig:{columns:1},items:[this.getLoginText(),this.getLoginButton(),this.getHaveAccountText(),this.getHaveAccountLink()]});
Openwis.Utils.MessageMustLogin.superclass.initComponent.apply(this,arguments);
this.show()
},getLoginText:function(){return new Ext.Container({html:Openwis.i18n("MessageMustLogin.msg"),style:{marginTop:"10px",marginLeft:"10px",marginRight:"10px",textAlign:"center"}})
},getLoginButton:function(){this.loginButton=new Ext.Panel({buttonAlign:"center",border:false,buttons:[new Ext.Button(this.getLoginAction())]});
return this.loginButton
},getHaveAccountText:function(){var htmlTxt=Openwis.i18n("MessageMustLogin.register.msg");
if(!selfRegistrationEnabled){htmlTxt=""
}return new Ext.Container({html:htmlTxt,style:{marginTop:"10px",textAlign:"center"}})
},getHaveAccountLink:function(){var htmlTxt='<a href="'+configOptions.locService+'/user.register.get">Register</a>';
if(!selfRegistrationEnabled){htmlTxt=""
}return new Ext.Container({html:htmlTxt,style:{textAlign:"center"}})
},getLoginAction:function(){if(!this.loginAction){this.loginAction=new Ext.Action({text:"Login",scope:this,style:{textAlign:"center"},handler:function(){window.location.href=configOptions.locService+"/user.loginCaptcha.get"
}})
}return this.loginAction
}});Ext.ns("Openwis.Utils");
Openwis.Utils.MessageBoxAccessDenied=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{closeAction:"close",autoScroll:false,resizable:false,layout:"table",title:Openwis.i18n("MessageBoxAccessDenied.title"),height:310,width:409,params:this.urn,layoutConfig:{columns:1},items:[this.getAccessDeniedMsgText(),this.getExtensionPrivilegeFieldSet(),this.getOKButton()]});
Openwis.Utils.MessageMustLogin.superclass.initComponent.apply(this,arguments);
this.initialize();
this.show()
},initialize:function(){if(this.urn){this.getProductTitleAreaText().setValue(this.urn)
}},getAccessDeniedMsgText:function(){return new Ext.Container({html:Openwis.i18n("MessageBoxAccessDenied.msg"),style:{marginTop:"20px",marginLeft:"20px",marginRight:"20px",marginBottom:"20px",textAlign:"center"}})
},getExtensionPrivilegeFieldSet:function(){if(!this.extensionprivilegesFieldSet){this.extensionprivilegesFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("MessageBoxAccessDenied.extension.title"),width:300,layout:"table",layoutConfig:{columns:1},style:{marginLeft:"50px"},collapsed:false,collapsible:true,buttons:[new Ext.Button(this.getSendMailToAdminAction())]});
this.extensionprivilegesFieldSet.add(new Ext.Container({html:Openwis.i18n("MessageBoxAccessDenied.extension.note"),border:false,cls:"infoMsg",style:{margin:"0px 0px 5px 0px"}}));
this.extensionprivilegesFieldSet.add(this.getProductTitleAreaText())
}return this.extensionprivilegesFieldSet
},getOKButton:function(){this.okButton=new Ext.Panel({buttonAlign:"center",border:false,buttons:[new Ext.Button(this.getOKAction())]});
return this.okButton
},getProductTitleAreaText:function(){if(!this.productTitleAreaText){this.productTitleAreaText=new Ext.form.TextArea({border:true,autoscroll:false,width:250})
}return this.productTitleAreaText
},getOKAction:function(){if(!this.okAction){this.okAction=new Ext.Action({text:Openwis.i18n("Common.Btn.OK"),scope:this,handler:function(){this.close()
}})
}return this.okAction
},getSendMailToAdminAction:function(){if(!this.sendMailToAdminAction){this.sendMailToAdminAction=new Ext.Action({text:Openwis.i18n("MessageBoxAccessDenied.extension.button"),scope:this,handler:function(){var params={};
params.content=this.getProductTitleAreaText().getValue();
var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/user.extends.privileges.submit",params:params,listeners:{success:function(config){this.close();
new Openwis.Utils.MessageBox.displaySuccessMsg(Openwis.i18n("MessageBoxAccessDenied.extension.mail.success"))
},scope:this}});
saveHandler.proceed()
}})
}return this.sendMailToAdminAction
}});Ext.ns("Openwis.Utils");
Openwis.Utils.MessageBoxServiceNotAllowed=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{closeAction:"close",autoScroll:false,resizable:false,layout:"table",title:Openwis.i18n("MessageBoxServiceNotAllowed.title"),height:120,width:221,layoutConfig:{columns:1},items:[this.getServiceNotAllowedText(),this.getLoginButton()]});
Openwis.Utils.MessageMustLogin.superclass.initComponent.apply(this,arguments);
this.show()
},getServiceNotAllowedText:function(){return new Ext.Container({html:Openwis.i18n("MessageBoxServiceNotAllowed.msg"),style:{marginTop:"10px",marginLeft:"10px",marginRight:"10px",textAlign:"center"}})
},getLoginButton:function(){this.loginButton=new Ext.Panel({buttonAlign:"center",border:false,buttons:[new Ext.Button(this.getLoginAction())]});
return this.loginButton
},getLoginAction:function(){if(!this.loginAction){this.loginAction=new Ext.Action({text:"Reconnect",scope:this,style:{textAlign:"center"},handler:function(){window.location.href=configOptions.url+"/openWisInit"
}})
}return this.loginAction
}});Ext.ns("Openwis.Utils.Xml");
Openwis.Utils.Xml.getAttribute=function(attributes,name){var attribute=null;
if(attributes!=null){attribute=attributes.getNamedItem(name)
}return attribute
};
Openwis.Utils.Xml.getAttributeValue=function(attributes,name){var value="";
var attribute=Openwis.Utils.Xml.getAttribute(attributes,name);
if(attribute!=null){value=attribute.nodeValue
}return value
};
Openwis.Utils.Xml.getElement=function(xmlText,elementName){var element=null;
var parser=new OpenLayers.Format.XML();
var document=parser.read(xmlText);
if(document!=null){element=parser.getChildEl(document,elementName)
}return element
};Ext.ns("Openwis.RegistrationUser");
Openwis.RegistrationUser.RegistrationUserSuccessful=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{closeAction:"close",autoScroll:false,resizable:false,closable:false,layout:"table",title:Openwis.i18n("RegistrationUserSuccessful.title"),layoutConfig:{columns:2},items:[this.getSuccessText(),this.getLoginButton(),this.getHomePageButton()]});
Openwis.RegistrationUser.RegistrationUserSuccessful.superclass.initComponent.apply(this,arguments);
this.show()
},getSuccessText:function(){return new Ext.Container({html:Openwis.i18n("RegistrationUserSuccessful.msg"),colspan:2,style:{marginTop:"10px",marginLeft:"10px",marginRight:"10px",textAlign:"center"}})
},getLoginButton:function(){this.loginButton=new Ext.Panel({buttonAlign:"center",border:false,buttons:[new Ext.Button(this.getLoginAction())]});
return this.loginButton
},getHomePageButton:function(){this.homePageButton=new Ext.Panel({buttonAlign:"center",border:false,buttons:[new Ext.Button(this.getHomePageAction())]});
return this.homePageButton
},getLoginAction:function(){if(!this.loginAction){this.loginAction=new Ext.Action({text:Openwis.i18n("RegistrationUserSuccessful.redirect.login"),scope:this,style:{textAlign:"center"},handler:function(){window.location.href=configOptions.url+"/openWisInit"
}})
}return this.loginAction
},getHomePageAction:function(){if(!this.homePageButton){this.homePageButton=new Ext.Action({text:Openwis.i18n("RegistrationUserSuccessful.redirect.homepage"),scope:this,style:{textAlign:"center"},handler:function(){window.location.href=configOptions.locService+"/main.home"
}})
}return this.homePageButton
}});Ext.ns("Openwis.Utils.Misc");
Openwis.Utils.Misc.bytesToKMG=function(bytes){var precision=2;
var kilobyte=1024;
var megabyte=kilobyte*1024;
var gigabyte=megabyte*1024;
var terabyte=gigabyte*1024;
if((bytes>=0)&&(bytes<kilobyte)){return bytes+" Bytes"
}else{if((bytes>=kilobyte)&&(bytes<megabyte)){return(bytes/kilobyte).toFixed(precision)+" KBytes"
}else{if((bytes>=megabyte)&&(bytes<gigabyte)){return(bytes/megabyte).toFixed(precision)+" MBytes"
}else{if((bytes>=gigabyte)&&(bytes<terabyte)){return(bytes/gigabyte).toFixed(precision)+" GBytes"
}else{if(bytes>=terabyte){return(bytes/terabyte).toFixed(precision)+" TBytes"
}else{return bytes+" Bytes"
}}}}}};
Openwis.Utils.Misc.getMockHtml=function(){var msg="";
for(var i=0;
i<5000;
i++){msg+="Testtt "
}return msg
};
Openwis.Utils.Misc.createLabel=function(label){return new Ext.Container({border:false,width:100,html:label+": ",style:{padding:"5px"}})
};
Openwis.Utils.Misc.createDummy=function(){return new Ext.Container({border:false,html:"&nbsp;"})
};Ext.ns("Openwis.Handler");
Openwis.Handler.Save=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.successWindow=config.successWindow?config.successWindow:false;
this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.Save.superclass.constructor.call(this,config)
},proceed:function(){this.window=Ext.MessageBox.wait("Please wait...","Submitting data");
var h=this.getHeaders();
Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:h,jsonData:this.params,scope:this})
},cbSuccessful:function(ajaxResponse){this.window.hide();
var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var response=responseHandler.handleResponse(ajaxResponse);
if(response.ok){if(this.successWindow){Openwis.Utils.MessageBox.displaySuccessMsg("Changes saved successfully.",this.fireSuccessEvent,this)
}else{this.fireSuccessEvent(response.o)
}}else{Openwis.Utils.MessageBox.displayErrorMsg(response.o)
}},fireSuccessEvent:function(o){if(o){this.fireEvent("success",o)
}else{this.fireEvent("success")
}},cbFailure:function(response){this.window.hide();
Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
},fireFailureEvent:function(){this.fireEvent("failure")
},getHeaders:function(){var headers={"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8",};
var csrf_token=Openwis.Utils.Storage.get("csrf-token");
if(csrf_token!==null){headers["csrf-token"]=csrf_token;
Openwis.Utils.Storage.remove("csrf-token")
}return headers
}});Ext.ns("Openwis.Handler");
Openwis.Handler.Remove=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.confirmMsg=config.confirmMsg;
if(this.confirmMsg==null){this.confirmMsg="Do you confirm the action ?"
}this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.Remove.superclass.constructor.call(this,config)
},proceed:function(){Ext.MessageBox.confirm("Confirm ?",this.confirmMsg,function(btnClicked){if(btnClicked=="yes"){this.loadMask=new Ext.LoadMask(Ext.getBody(),{msg:"Loading... Please wait..."});
this.loadMask.show();
Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:{"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8"},jsonData:this.params,scope:this})
}},this)
},cbSuccessful:function(ajaxResponse){this.loadMask.hide();
var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var response=responseHandler.handleResponse(ajaxResponse);
if(response.ok){Openwis.Utils.MessageBox.displaySuccessMsg("Deletion completed successfully.",this.fireSuccessEvent,this)
}else{Openwis.Utils.MessageBox.displayErrorMsg(response.o,this.fireFailureEvent,this)
}},fireSuccessEvent:function(){this.fireEvent("success")
},cbFailure:function(response){this.loadMask.hide();
Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
},fireFailureEvent:function(){this.fireEvent("failure")
}});Ext.ns("Openwis.Handler");
Openwis.Handler.Lock=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.confirmMsg=config.confirmMsg;
if(this.confirmMsg==null){this.confirmMsg="Do you confirm the action ?"
}this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.Lock.superclass.constructor.call(this,config)
},proceed:function(){Ext.MessageBox.confirm("Confirm ?",this.confirmMsg,function(btnClicked){if(btnClicked=="yes"){this.loadMask=new Ext.LoadMask(Ext.getBody(),{msg:"Loading... Please wait..."});
this.loadMask.show();
Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:{"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8"},jsonData:this.params,scope:this})
}},this)
},cbSuccessful:function(ajaxResponse){this.loadMask.hide();
var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var response=responseHandler.handleResponse(ajaxResponse);
if(response.ok){var action=response.o.charAt(0).toUpperCase()+response.o.slice(1);
Openwis.Utils.MessageBox.displaySuccessMsg(action+" completed successfully.",this.fireSuccessEvent,this)
}else{Openwis.Utils.MessageBox.displayErrorMsg(response.o,this.fireFailureEvent,this)
}},fireSuccessEvent:function(){this.fireEvent("success")
},cbFailure:function(response){this.loadMask.hide();
Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
},fireFailureEvent:function(){this.fireEvent("failure")
}});Ext.ns("Openwis.Handler");
Openwis.Handler.Get=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.maskEl=config.maskEl?config.maskEl:Ext.getBody();
this.useLoadMask=(config.useLoadMask!==undefined)?config.useLoadMask:true;
this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.Get.superclass.constructor.call(this,config)
},proceed:function(){if(this.useLoadMask){this.loadMask=new Ext.LoadMask(this.maskEl,{msg:"Loading... Please wait..."});
this.loadMask.show()
}Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:{"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8"},jsonData:this.params,scope:this})
},cbSuccessful:function(ajaxResponse){if(this.useLoadMask){this.loadMask.hide()
}var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var responseHeaders=Openwis.Utils.Header.getHeaders(ajaxResponse.getAllResponseHeaders());
if(responseHeaders.hasOwnProperty("csrf-token")){Openwis.Utils.Storage.save("csrf-token",responseHeaders["csrf-token"])
}var response=responseHandler.handleResponse(ajaxResponse);
this.fireSuccessEvent(response)
},fireSuccessEvent:function(response){this.fireEvent("success",response)
},cbFailure:function(response){if(this.useLoadMask){this.loadMask.hide()
}if(response.status==401){new Openwis.Utils.MessageBoxServiceNotAllowed()
}else{Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
}},fireFailureEvent:function(){this.fireEvent("failure")
}});Ext.ns("Openwis.Handler");
Openwis.Handler.GetWithoutError=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.maskEl=config.maskEl?config.maskEl:Ext.getBody();
this.useLoadMask=(config.useLoadMask!==undefined)?config.useLoadMask:true;
this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.Get.superclass.constructor.call(this,config)
},proceed:function(){if(this.useLoadMask){this.loadMask=new Ext.LoadMask(this.maskEl,{msg:"Loading... Please wait..."});
this.loadMask.show()
}Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:{"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8"},jsonData:this.params,scope:this})
},cbSuccessful:function(ajaxResponse){if(this.useLoadMask){this.loadMask.hide()
}var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var response=responseHandler.handleResponse(ajaxResponse);
this.fireSuccessEvent(response)
},fireSuccessEvent:function(response){this.fireEvent("success",response)
}});Ext.ns("Openwis.Handler");
Openwis.Handler.Index=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.confirmMsg=config.confirmMsg;
if(this.confirmMsg==null){this.confirmMsg="This operation may take some time on large catalogs and should not be done during peak usage. Continue?"
}this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.Remove.superclass.constructor.call(this,config)
},proceed:function(){Ext.MessageBox.confirm("Confirm ?",this.confirmMsg,function(btnClicked){if(btnClicked=="yes"){this.loadMask=new Ext.LoadMask(Ext.getBody(),{msg:"Loading... Please wait..."});
this.loadMask.show();
Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:{"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8"},jsonData:this.params,scope:this})
}},this)
},cbSuccessful:function(ajaxResponse){this.loadMask.hide();
var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var response=responseHandler.handleResponse(ajaxResponse);
if(!response){new Openwis.Utils.MessageBoxServiceNotAllowed()
}if(response.ok){Openwis.Utils.MessageBox.displaySuccessMsg("Index operation was successful.",this.fireSuccessEvent,this)
}else{Openwis.Utils.MessageBox.displayErrorMsg(response.o)
}},fireSuccessEvent:function(){this.fireEvent("success")
},cbFailure:function(response){this.loadMask.hide();
Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
},fireFailureEvent:function(){this.fireEvent("failure")
}});Ext.ns("Openwis.Handler");
Openwis.Handler.GetNoJson=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.maskEl=config.maskEl?config.maskEl:Ext.getBody();
this.useLoadMask=(config.useLoadMask!="undefined")?config.useLoadMask:true;
this.useHTMLMask=(config.useHTMLMask!="undefined")?config.useHTMLMask:false;
this.loadingMessage=config.loadingMessage||Openwis.i18n("Common.Loading.Message");
this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.GetNoJson.superclass.constructor.call(this,config)
},proceed:function(){if(this.useLoadMask){this.loadMask=new Ext.LoadMask(this.maskEl,{msg:this.loadingMessage});
this.loadMask.show()
}else{if(this.useHTMLMask){var innerHTML=this.maskEl.body.dom.innerHTML;
this.maskEl.body.dom.innerHTML=this.loadingMessage+innerHTML
}}Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",params:this.params,scope:this})
},cbSuccessful:function(ajaxResponse){if(this.useLoadMask){this.loadMask.hide()
}this.fireSuccessEvent(ajaxResponse)
},fireSuccessEvent:function(response){this.fireEvent("success",response.responseText)
},cbFailure:function(response){if(this.useLoadMask){this.loadMask.hide()
}Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
},fireFailureEvent:function(){this.fireEvent("failure")
}});Ext.ns("Openwis.Handler");
Openwis.Handler.GetNoJsonResponse=Ext.extend(Ext.util.Observable,{constructor:function(config){this.url=config.url;
if(typeof(config.params)=="number"){this.params=config.params+""
}else{this.params=config.params||{}
}this.maskEl=config.maskEl?config.maskEl:Ext.getBody();
this.useLoadMask=(config.useLoadMask!==undefined)?config.useLoadMask:true;
this.addEvents("success","failure");
this.listeners=config.listeners;
Openwis.Handler.GetNoJsonResponse.superclass.constructor.call(this,config)
},proceed:function(){if(this.useLoadMask){this.loadMask=new Ext.LoadMask(this.maskEl,{msg:"Loading... Please wait..."});
this.loadMask.show()
}Ext.Ajax.request({url:this.url,success:this.cbSuccessful,failure:this.cbFailure,method:"POST",headers:{"Content-Type":"application/json; charset=utf-8",Accept:"application/json; charset=utf-8"},jsonData:this.params,scope:this})
},cbSuccessful:function(ajaxResponse){if(this.useLoadMask){this.loadMask.hide()
}this.fireSuccessEvent(ajaxResponse.responseText)
},fireSuccessEvent:function(response){this.fireEvent("success",response)
},cbFailure:function(response){if(this.useLoadMask){this.loadMask.hide()
}Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent,this)
},fireFailureEvent:function(){this.fireEvent("failure")
}});Ext.ns("Openwis.Data");
Openwis.Data.JeevesJsonResponseHandler=function(){this.handleResponse=function(response){var jsonData=response.responseText;
var format=new OpenLayers.Format.XML();
var xmlResponse=format.read(jsonData);
var childEl=format.getChildEl(xmlResponse);
switch(childEl.nodeName){case"jsonData":var json="";
for(var i=0;
i<childEl.childNodes.length;
i++){json+=childEl.childNodes[i].nodeValue
}return Ext.decode(json);
break;
case"error":break;
default:}};
this.getJsonText=function(response){var jsonData=response.responseText;
var format=new OpenLayers.Format.XML();
var xmlResponse=format.read(jsonData);
var childEl=format.getChildEl(xmlResponse);
switch(childEl.nodeName){case"jsonData":var json="";
for(var i=0;
i<childEl.childNodes.length;
i++){json+=childEl.childNodes[i].nodeValue
}return json;
break;
case"error":break;
default:}}
};Ext.ns("Openwis.Data");
Openwis.Data.JeevesJsonReader=Ext.extend(Ext.data.JsonReader,{read:function(response){var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var o=responseHandler.handleResponse(response);
if(!o){throw {message:"JsonReader.read: Json object not found"}
}return this.readRecords(o)
}});Ext.ns("Openwis.Data");
Openwis.Data.JeevesJsonStore=Ext.extend(Ext.data.Store,{constructor:function(config){Openwis.Data.JeevesJsonStore.superclass.constructor.call(this,Ext.apply(config,{reader:new Openwis.Data.JeevesJsonReader(config)}))
},listeners:{exception:function(misc,type,action,options,response,arg){if(response.status==401){new Openwis.Utils.MessageBoxServiceNotAllowed()
}}}});Ext.namespace("Openwis.Data");
Openwis.Data.JeevesJsonSubmit=function(form,options){Openwis.Data.JeevesJsonSubmit.superclass.constructor.call(this,form,options)
};
Ext.extend(Openwis.Data.JeevesJsonSubmit,Ext.form.Action.Submit,{type:"jeevesjsonsubmit",run:function(){var o=this.options;
var method=this.getMethod();
var isGet=method=="GET";
if(o.clientValidation===false||this.form.isValid()){var encodedParams=Ext.encode(this.form.getValues());
var box=Ext.MessageBox.wait("Please wait...","Submitting data");
Ext.Ajax.request(Ext.apply(this.createCallback(o),{url:this.getUrl(isGet),method:method,success:function(){box.hide();
o.success.apply(o.scope)
},failure:function(){box.hide();
o.failure.apply(o.scope)
},scope:o.scope,headers:{"Content-Type":"application/json"},params:encodedParams,isUpload:this.form.fileUpload}))
}else{if(o.clientValidation!==false){this.failureType=Ext.form.Action.CLIENT_INVALID;
this.form.afterAction(this,false)
}}}});
Ext.apply(Ext.form.Action.ACTION_TYPES,{jeevesjsonsubmit:Openwis.Data.JeevesJsonSubmit});Ext.ns("Openwis.Data");
Openwis.Data.JeevesJsonTreeLoader=Ext.extend(Ext.tree.TreeLoader,{processResponse:function(response,node,callback,scope){var responseHandler=new Openwis.Data.JeevesJsonResponseHandler();
var json=responseHandler.getJsonText(response);
if(!json){throw {message:"JsonReader.read: Json object not found"}
}try{var o=response.responseData||Ext.decode(json);
node.beginUpdate();
for(var i=0,len=o.length;
i<len;
i++){var n=this.createNode(o[i]);
if(n){node.appendChild(n)
}}node.endUpdate();
this.runCallback(callback,scope||node,[node])
}catch(e){this.handleFailure(response)
}}});Ext.ns("Openwis");
Openwis.Conf={PAGE_SIZE:20,UPLOAD_SIZE:10,REQUEST_CACHE_HOUR:-3};Ext.ns("Openwis.Common.Dissemination");
Openwis.Common.Dissemination.FavoriteFTPWindow=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Common.Dissemination.FTP.Title"),layout:"fit",width:400,height:350,modal:true,closeAction:"close"});
Openwis.Common.Dissemination.FavoriteFTPWindow.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("favoriteFTPSaved");
this.add(this.getFavoriteFtpPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition){this.getFavoriteFtpPanel().initializeFields(this.ftp)
}this.show()
},getFavoriteFtpPanel:function(){if(this.favoriteFtpPanel==null){this.favoriteFtpPanel=new Openwis.Common.Dissemination.FTPDiffusion({allowHostEdition:!this.isEdition})
}return this.favoriteFtpPanel
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getFavoriteFtpPanel().getForm().isValid()){var ftpEdited=this.getFavoriteFtpPanel().getDisseminationValue();
ftpEdited.disseminationTool=this.ftp.disseminationTool;
this.fireEvent("favoriteFTPSaved",ftpEdited);
this.close()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
}});Ext.ns("Openwis.Common.Dissemination");
Openwis.Common.Dissemination.FavoriteEmailWindow=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Common.Dissemination.Email.Title"),layout:"fit",width:400,height:300,modal:true,closeAction:"close"});
Openwis.Common.Dissemination.FavoriteEmailWindow.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.addEvents("favoriteEmailSaved");
this.add(this.getFavoriteEmailPanel());
this.addButton(new Ext.Button(this.getSaveAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
if(this.isEdition){this.getFavoriteEmailPanel().initializeFields(this.mail)
}this.show()
},getFavoriteEmailPanel:function(){if(this.favoriteEmailPanel==null){this.favoriteEmailPanel=new Openwis.Common.Dissemination.MailDiffusion({allowAddressEdition:!this.isEdition})
}return this.favoriteEmailPanel
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){if(this.getFavoriteEmailPanel().getForm().isValid()){var emailEdited=this.getFavoriteEmailPanel().getDisseminationValue();
emailEdited.disseminationTool=this.mail.disseminationTool;
this.fireEvent("favoriteEmailSaved",emailEdited);
this.close()
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
}});Ext.ns("Openwis.Common.Dissemination");
Openwis.Common.Dissemination.Favorites=Ext.extend(Ext.Panel,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("Common.Dissemination.Favorites.Title"),style:{margin:"10px 30px 10px 30px"},width:600,height:500});
Openwis.Common.Dissemination.Favorites.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getFtpFavoritesGrid());
this.add(this.getEmailFavoritesGrid())
},getFtpFavoritesGrid:function(){if(!this.ftpFavoritesGrid){this.ftpFavoritesGrid=new Ext.grid.GridPanel({height:150,width:400,border:true,store:this.getFtpStore(),loadMask:true,columns:[{id:"disseminationTool",header:Openwis.i18n("Common.Dissemination.Favorites.FTP.Tool"),dataIndex:"disseminationTool",sortable:true,width:200},{id:"host",header:Openwis.i18n("Common.Dissemination.Favorites.FTP.Destination"),dataIndex:"host",sortable:true,width:200,renderer:Openwis.Common.Request.Utils.htmlSafeRenderer}],sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getFtpEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getFtpRemoveAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getFtpEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getFtpRemoveAction().setDisabled(sm.getCount()==0)
}}})});
this.ftpFavoritesGrid.addButton(new Ext.Button(this.getNewFtpRMDCNAction()));
this.ftpFavoritesGrid.addButton(new Ext.Button(this.getNewFtpPublicAction()));
this.ftpFavoritesGrid.addButton(new Ext.Button(this.getFtpEditAction()));
this.ftpFavoritesGrid.addButton(new Ext.Button(this.getFtpRemoveAction()))
}return this.ftpFavoritesGrid
},getFtpStore:function(){if(!this.ftpStore){this.ftpStore=new Ext.data.JsonStore({autoDestroy:true,fields:[{name:"host",sortType:Ext.data.SortTypes.asUCString},{name:"path"},{name:"user"},{name:"password"},{name:"port"},{name:"passive"},{name:"checkFileSize"},{name:"fileName"},{name:"encrypted"},{name:"disseminationTool"}]})
}return this.ftpStore
},getNewFtpRMDCNAction:function(){if(!this.newFtpRMDCNAction){this.newFtpRMDCNAction=new Ext.Action({text:Openwis.i18n("Common.Dissemination.Favorites.FTP.NewRMDCNFTP.button"),scope:this,handler:function(){new Openwis.Common.Dissemination.FavoriteFTPWindow({listeners:{favoriteFTPSaved:function(ftpCreated){this.getFtpFavoritesGrid().getStore().add(new Ext.data.Record(ftpCreated))
},scope:this},ftp:{disseminationTool:"RMDCN"},isEdition:false})
}})
}return this.newFtpRMDCNAction
},getNewFtpPublicAction:function(){if(!this.newFtpPublicAction){this.newFtpPublicAction=new Ext.Action({text:Openwis.i18n("Common.Dissemination.Favorites.FTP.NewPublicFTP.button"),scope:this,handler:function(){new Openwis.Common.Dissemination.FavoriteFTPWindow({listeners:{favoriteFTPSaved:function(ftpCreated){this.getFtpFavoritesGrid().getStore().add(new Ext.data.Record(ftpCreated))
},scope:this},ftp:{disseminationTool:"PUBLIC"},isEdition:false})
}})
}return this.newFtpPublicAction
},getFtpEditAction:function(){if(!this.editAction){this.editAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Dissemination.Favorites.FTP.Edit.button"),scope:this,handler:function(){var selectedRec=this.getFtpFavoritesGrid().getSelectionModel().getSelected();
new Openwis.Common.Dissemination.FavoriteFTPWindow({listeners:{favoriteFTPSaved:function(ftpUpdated){var ftpToRemove;
for(var i=0;
i<this.getFtpFavoritesGrid().getStore().getCount();
i++){var record=this.getFtpFavoritesGrid().getStore().getAt(i);
if((record.get("host")==ftpUpdated.host)&&(record.get("disseminationTool")==ftpUpdated.disseminationTool)){ftpToRemove=record
}}if(ftpToRemove){this.getFtpFavoritesGrid().getStore().remove(ftpToRemove)
}this.getFtpFavoritesGrid().getStore().add(new Ext.data.Record(ftpUpdated))
},scope:this},ftp:selectedRec.data,isEdition:true})
}})
}return this.editAction
},getFtpRemoveAction:function(){if(!this.removeAction){this.removeAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Dissemination.Favorites.FTP.Remove.button"),scope:this,handler:function(){var selection=this.getFtpFavoritesGrid().getSelectionModel().getSelections();
this.getFtpFavoritesGrid().getStore().remove(selection)
}})
}return this.removeAction
},getEmailFavoritesGrid:function(){if(!this.emailFavoritesGrid){this.emailFavoritesGrid=new Ext.grid.GridPanel({height:150,width:400,border:true,store:this.getEmailStore(),loadMask:true,columns:[{id:"disseminationTool",header:Openwis.i18n("Common.Dissemination.Favorites.Email.Tool"),dataIndex:"disseminationTool",sortable:true,width:200},{id:"address",header:Openwis.i18n("Common.Dissemination.Favorites.Email.Address"),dataIndex:"address",sortable:true,width:200}],sm:new Ext.grid.RowSelectionModel({listeners:{rowselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEmailEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getEmailRemoveAction().setDisabled(sm.getCount()==0)
},rowdeselect:function(sm,rowIndex,record){sm.grid.ownerCt.getEmailEditAction().setDisabled(sm.getCount()!=1);
sm.grid.ownerCt.getEmailRemoveAction().setDisabled(sm.getCount()==0)
}}})});
this.emailFavoritesGrid.addButton(new Ext.Button(this.getNewEmailRMDCNAction()));
this.emailFavoritesGrid.addButton(new Ext.Button(this.getNewEmailPublicAction()));
this.emailFavoritesGrid.addButton(new Ext.Button(this.getEmailEditAction()));
this.emailFavoritesGrid.addButton(new Ext.Button(this.getEmailRemoveAction()))
}return this.emailFavoritesGrid
},getEmailStore:function(){if(!this.emailStore){this.emailStore=new Ext.data.JsonStore({autoDestroy:true,fields:[{name:"address",sortType:Ext.data.SortTypes.asUCString},{name:"headerLine"},{name:"mailDispatchMode"},{name:"subject"},{name:"mailAttachmentMode"},{name:"fileName"},{name:"disseminationTool"}]})
}return this.emailStore
},getNewEmailRMDCNAction:function(){if(!this.newEmailRMDCNAction){this.newEmailRMDCNAction=new Ext.Action({text:Openwis.i18n("Common.Dissemination.Favorites.Email.NewRMDCN.button"),scope:this,handler:function(){new Openwis.Common.Dissemination.FavoriteEmailWindow({listeners:{favoriteEmailSaved:function(emailCreated){this.getEmailFavoritesGrid().getStore().add(new Ext.data.Record(emailCreated))
},scope:this},mail:{disseminationTool:"RMDCN"},isEdition:false})
}})
}return this.newEmailRMDCNAction
},getNewEmailPublicAction:function(){if(!this.newEmailPublicAction){this.newEmailPublicAction=new Ext.Action({text:Openwis.i18n("Common.Dissemination.Favorites.Email.NewPublic.button"),scope:this,handler:function(){new Openwis.Common.Dissemination.FavoriteEmailWindow({listeners:{favoriteEmailSaved:function(emailCreated){this.getEmailFavoritesGrid().getStore().add(new Ext.data.Record(emailCreated))
},scope:this},mail:{disseminationTool:"PUBLIC"},isEdition:false})
}})
}return this.newEmailPublicAction
},getEmailEditAction:function(){if(!this.editEmailAction){this.editEmailAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Dissemination.Favorites.Email.Edit.button"),scope:this,handler:function(){var selectedRec=this.getEmailFavoritesGrid().getSelectionModel().getSelected();
new Openwis.Common.Dissemination.FavoriteEmailWindow({listeners:{favoriteEmailSaved:function(emailUpdated){var emailToRemove;
for(var i=0;
i<this.getEmailFavoritesGrid().getStore().getCount();
i++){var record=this.getEmailFavoritesGrid().getStore().getAt(i);
if((record.get("address")==emailUpdated.address)&&(record.get("disseminationTool")==emailUpdated.disseminationTool)){emailToRemove=record
}}if(emailToRemove){this.getEmailFavoritesGrid().getStore().remove(emailToRemove)
}this.getEmailFavoritesGrid().getStore().add(new Ext.data.Record(emailUpdated))
},scope:this},mail:selectedRec.data,isEdition:true})
}})
}return this.editEmailAction
},getEmailRemoveAction:function(){if(!this.removeEmailAction){this.removeEmailAction=new Ext.Action({disabled:true,text:Openwis.i18n("Common.Dissemination.Favorites.Email.Remove.button"),scope:this,handler:function(){var selection=this.getEmailFavoritesGrid().getSelectionModel().getSelections();
this.getEmailFavoritesGrid().getStore().remove(selection)
}})
}return this.removeEmailAction
},getUser:function(user){if(!user){user={}
}if(!Ext.isDefined(user.ftps)){user.ftps=[]
}for(var i=0;
i<this.getFtpFavoritesGrid().getStore().getCount();
i++){var ftp=this.getFtpFavoritesGrid().getStore().getAt(i).data;
user.ftps.push(ftp)
}if(!Ext.isDefined(user.emails)){user.emails=[]
}for(var i=0;
i<this.getEmailFavoritesGrid().getStore().getCount();
i++){var email=this.getEmailFavoritesGrid().getStore().getAt(i);
user.emails.push(email.data)
}return user
},setFavorites:function(user){if(!Ext.isEmpty(user.ftps)){this.getFtpFavoritesGrid().getStore().loadData(user.ftps)
}if(!Ext.isEmpty(user.emails)){this.getEmailFavoritesGrid().getStore().loadData(user.emails)
}}});Ext.ns("Openwis.Common.User");
Openwis.Common.User.PersonalInformation=Ext.extend(Ext.form.FormPanel,{initComponent:function(){Ext.apply(this,{itemCls:"formItems",title:Openwis.i18n("Security.User.PersoInfo.Title"),width:600,height:500,items:[{layout:"table",border:false,layoutConfig:{columns:4},items:this.getFormItems()}]});
Openwis.Common.User.PersonalInformation.superclass.initComponent.apply(this,arguments)
},getFormItems:function(){var items=[this.createLabel(Openwis.i18n("Security.User.PersoInfo.UserName.Label")),this.getUserNameTextField(),this.createDummy(),this.createDummy(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.LastName.Label")),this.getSurNameTextField(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.FirstName.Label")),this.getNameTextField()];
if(!this.hidePassword){items=items.concat([this.createPasswordLabel(),this.createPasswordTextField(),this.createDummy(),this.createDummy()])
}items=items.concat([this.createLabel(Openwis.i18n("Security.User.PersoInfo.Address.Label")),this.getAddressTextField(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.City.Label")),this.getCityTextField(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.State.Label")),this.getStateTextField(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.Zip.Label")),this.getZipTextField(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.Country.Label")),this.getCountryTextField(),this.createDummy(),this.createDummy(),this.createLabel(Openwis.i18n("Security.User.PersoInfo.ContactEmail.Label")),this.getEmailTextField()]);
return items
},createLabel:function(label){return new Openwis.Utils.Misc.createLabel(label)
},createDummy:function(){return new Openwis.Utils.Misc.createDummy()
},createPasswordLabel:function(){if(this.isEdition){return this.createLabel(Openwis.i18n("Security.User.PersoInfo.NewPassword.Label"))
}else{return this.createLabel(Openwis.i18n("Security.User.PersoInfo.Password.Label"))
}},createPasswordTextField:function(){if(this.isEdition){return this.getPasswordTextField(true)
}else{return this.getPasswordTextField(false)
}},setUserInformation:function(user){this.getUserNameTextField().setValue(user.username);
this.getUserNameTextField().disable();
this.getNameTextField().setValue(user.name);
this.getSurNameTextField().setValue(user.surname);
this.getEmailTextField().setValue(user.emailContact);
if(!this.hidePassword){this.getPasswordTextField().setValue(user.password)
}this.getAddressTextField().setValue(user.address.address);
this.getZipTextField().setValue(user.address.zip);
this.getStateTextField().setValue(user.address.state);
this.getCityTextField().setValue(user.address.city);
this.getCountryTextField().setValue(user.address.country)
},getUserNameTextField:function(){if(!this.usernameTextField){this.usernameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.UserName.Label"),name:"username",allowBlank:false,width:150})
}return this.usernameTextField
},getNameTextField:function(){if(!this.nameTextField){this.nameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.FirstName.Label"),name:"name",allowBlank:false,width:150})
}return this.nameTextField
},getSurNameTextField:function(){if(!this.surnameTextField){this.surnameTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.LastName.Label"),name:"surname",allowBlank:false,width:150})
}return this.surnameTextField
},getPasswordTextField:function(allowblankValue){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({fieldLabel:"Password",inputType:"password",name:"password",allowBlank:allowblankValue,width:150})
}return this.passwordTextField
},getAddressTextField:function(){if(!this.addressTextField){this.addressTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.Address.Label"),name:"address",allowBlank:true,width:150})
}return this.addressTextField
},getStateTextField:function(){if(!this.stateTextField){this.stateTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.State.Label"),name:"state",allowBlank:true,width:150})
}return this.stateTextField
},getZipTextField:function(){if(!this.zipTextField){this.zipTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.Zip.Label"),name:"zip",allowBlank:true,width:150})
}return this.zipTextField
},getCityTextField:function(){if(!this.cityTextField){this.cityTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.City.Label"),name:"city",allowBlank:true,width:150})
}return this.cityTextField
},getCountryTextField:function(){if(!this.countryTextField){this.countryTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.Country.Label"),name:"country",allowBlank:true,width:150})
}return this.countryTextField
},getEmailTextField:function(){if(!this.emailTextField){this.emailTextField=new Ext.form.TextField({fieldLabel:Openwis.i18n("Security.User.PersoInfo.ContactEmail.Label"),name:"email",vtype:"email",allowBlank:false,width:150})
}return this.emailTextField
},getUser:function(user){if(!user){user={}
}user.name=this.getNameTextField().getValue();
user.username=this.getUserNameTextField().getValue();
user.surname=this.getSurNameTextField().getValue();
user.emailContact=this.getEmailTextField().getValue();
if(!this.hidePassword){user.password=this.getPasswordTextField().getValue()
}user.address={};
user.address.address=this.getAddressTextField().getValue();
user.address.zip=this.getZipTextField().getValue();
user.address.state=this.getStateTextField().getValue();
user.address.city=this.getCityTextField().getValue();
user.address.country=this.getCountryTextField().getValue();
return user
}});Ext.ns("Openwis.Common.User.UserInformation");
Openwis.Common.User.UserInformation=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Common.User.UserInformation.superclass.initComponent.apply(this,arguments);
this.initialize()
},getInfosAndInitialize:function(){var params={};
var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.user.getSelf",params:params,listeners:{success:function(config){this.config=config;
this.getPersonalInformationFormPanel().setUserInformation(this.config.user);
this.getFavoritesPanel().setFavorites(this.config.user)
},failure:function(config){Ext.Msg.show({title:Openwis.i18n("Security.User.UserInfo.ErrorDlg.Title"),msg:Openwis.i18n("Security.User.UserInfo.ErrorDlg.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getHeader());
var tabs=new Ext.TabPanel({width:600,height:400,activeTab:0,frame:true,defaults:{autoHeight:true},items:[this.getPersonalInformationFormPanel(),this.getFavoritesPanel()],buttons:[this.add(new Ext.Button(this.getSaveAction())),this.add(new Ext.Button(this.getCancelAction()))]});
this.add(tabs);
this.getInfosAndInitialize()
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.User.UserInfo.Title"),cls:"administrationTitle1"})
}return this.header
},getPersonalInformationFormPanel:function(){if(!this.personalInformationFormPanel){this.personalInformationFormPanel=new Openwis.Common.User.PersonalInformation({hidePassword:this.hidePassword,isEdition:"true"})
}return this.personalInformationFormPanel
},getFavoritesPanel:function(){if(!this.favoritesPanel){this.favoritesPanel=new Openwis.Common.Dissemination.Favorites()
}return this.favoritesPanel
},getSaveAction:function(){if(!this.saveAction){this.saveAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Save"),scope:this,handler:function(){var persoInfoValid=this.getPersonalInformationFormPanel().getForm().isValid();
if(persoInfoValid){var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.user.saveSelf",params:this.getUser(),listeners:{success:function(config){this.fireEvent("userSaved")
},scope:this}});
saveHandler.proceed()
}else{Ext.Msg.show({title:Openwis.i18n("Security.User.UserInfo.Validation.Title"),msg:Openwis.i18n("Security.User.UserInfo.Validation.Msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.saveAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Cancel"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getUser:function(){var user={};
user.user=this.getPersonalInformationFormPanel().getUser(user.user);
user.user=this.getFavoritesPanel().getUser(user.user);
return user
}});Ext.ns("Openwis.Common.User");
Openwis.Common.User.ChangePassword=Ext.extend(Ext.Container,{initComponent:function(){Ext.apply(this,{style:{margin:"10px 30px 10px 30px"}});
Openwis.Common.User.ChangePassword.superclass.initComponent.apply(this,arguments);
this.initialize()
},initialize:function(){this.add(this.getHeader());
this.add(this.getPswdForm())
},getHeader:function(){if(!this.header){this.header=new Ext.Container({html:Openwis.i18n("Security.User.ChangePswd.title"),cls:"administrationTitle1"})
}return this.header
},getPswdForm:function(){if(!this.pswdForm){this.pswdForm=new Ext.form.FormPanel({layout:"table",border:true,layoutConfig:{columns:2},items:[this.createLabel(Openwis.i18n("Security.User.EnterOldPwd.label")),this.getOldPasswordTextField(),this.createLabel(Openwis.i18n("Security.User.EnterPswd.label")),this.getPasswordTextField(),this.createLabel(Openwis.i18n("Security.User.ConfirmPswd.label")),this.getConfirmPasswordTextField()],buttons:[this.add(new Ext.Button(this.getChangePasswordAction()))]})
}return this.pswdForm
},createLabel:function(label){return new Openwis.Utils.Misc.createLabel(label)
},getPasswordTextField:function(){if(!this.passwordTextField){this.passwordTextField=new Ext.form.TextField({inputType:"password",name:"password",allowBlank:false,width:150})
}return this.passwordTextField
},getConfirmPasswordTextField:function(){if(!this.confirmPasswordTextField){this.confirmPasswordTextField=new Ext.form.TextField({inputType:"password",name:"password",allowBlank:false,width:150})
}return this.confirmPasswordTextField
},getOldPasswordTextField:function(){if(!this.oldPasswordTextField){this.oldPasswordTextField=new Ext.form.TextField({inputType:"password",name:"password",allowBlank:false,width:150})
}return this.oldPasswordTextField
},getChangePasswordAction:function(){if(!this.changePasswordAction){this.changePasswordAction=new Ext.Action({text:Openwis.i18n("Security.User.ChangePswd.Btn"),scope:this,handler:function(){var isValid=this.getPswdForm().getForm().isValid();
var oldPassword=this.getOldPasswordTextField().getValue();
var firstPassword=this.getPasswordTextField().getValue();
var confirmPassword=this.getConfirmPasswordTextField().getValue();
if(isValid&&(firstPassword==confirmPassword)&&(oldPassword!=="")){var params={};
params.password=this.getPasswordTextField().getValue();
params.oldPassword=this.getOldPasswordTextField().getValue();
var saveHandler=new Openwis.Handler.Save({url:configOptions.locService+"/xml.user.changePassword",params:params,listeners:{success:function(){Ext.Msg.show({title:Openwis.i18n("Security.User.ChangePswdDlg.success.title"),msg:Openwis.i18n("Security.User.ChangePswdDlg.success.msg"),buttons:Ext.Msg.OK,scope:this,icon:Ext.MessageBox.INFO})
},scope:this}});
saveHandler.proceed()
}else{Ext.Msg.show({title:Openwis.i18n("Security.User.ChangePswdDlg.failed.title"),msg:Openwis.i18n("Security.User.ChangePswdDlg.failed.msg"),buttons:Ext.MessageBox.OK,icon:Ext.MessageBox.WARNING})
}}})
}return this.changePasswordAction
}});Ext.ns("Openwis.Common.Search");
Openwis.Common.Search.KeywordsSearch=Ext.extend(Ext.Window,{initComponent:function(){Ext.apply(this,{title:Openwis.i18n("ThesauriManagement.Search.Keywords"),width:650,height:520,modal:true,closeAction:"close"});
Openwis.Common.Search.KeywordsSearch.superclass.initComponent.apply(this,arguments);
this.getInfosAndInitialize()
},getInfosAndInitialize:function(){var getHandler=new Openwis.Handler.Get({url:configOptions.locService+"/xml.thesaurus.getList",listeners:{success:function(config){this.config=config;
this.initialize()
},failure:function(config){this.close()
},scope:this}});
getHandler.proceed()
},initialize:function(){this.add(this.getKeywordsFormPanel());
this.add(this.getKeywordsSelectPanel());
this.addButton(new Ext.Button(this.getAddAction()));
this.addButton(new Ext.Button(this.getCancelAction()));
this.show();
this.initKeywordsSelected()
},initKeywordsSelected:function(){this.getKeywordsMultiSelector().fs.addClass("multiSelectFsKeywords");
this.getKeywordsMultiSelector().fs.body.addClass("multiSelectFsBodyKeywords");
this.getKeywordsMultiSelector().view.addClass("multiSelectViewKeywords");
var initialKeywords=this.keywordsFromTf;
if(initialKeywords.length>0){initialKeywords=initialKeywords.split(" | ");
var records=[];
for(var i=0;
i<initialKeywords.length;
i++){textSelected=initialKeywords[i];
record=new Ext.data.Record({text:textSelected});
records.push(record)
}for(var i=0;
i<records.length;
i++){record=records[i];
this.getKeywordsMultiSelector().view.store.add(record)
}this.getKeywordsMultiSelector().view.refresh()
}this.doLayout()
},getKeywordsFormPanel:function(){if(!this.keywordsFormPanel){this.keywordsFormPanel=new Ext.form.FormPanel({itemCls:"formItems",labelWidth:80,style:{padding:"5px"}});
this.keywordsFormPanel.add(this.getThesaurusComboBox())
}return this.keywordsFormPanel
},getKeywordsSelectPanel:function(){if(!this.keywordsSelectPanel){this.keywordsSelectPanel=new Ext.Panel({border:false,autoScroll:true,layout:"table",style:{padding:"5px"},layoutConfig:{columns:3,tableAttrs:{style:{width:"100%",padding:"20px"}}}});
this.keywordsSelectPanel.add(this.getKeywordsTreeFieldSet());
this.keywordsSelectPanel.add(this.getKeywordsButtonsPanel());
this.keywordsSelectPanel.add(this.getKeywordsMultiSelector())
}return this.keywordsSelectPanel
},getKeywordsTreeFieldSet:function(){if(!this.keywordsTreeFieldSet){this.keywordsTreeFieldSet=new Ext.form.FieldSet({title:Openwis.i18n("ThesauriManagement.Search.AvailableKeywords"),width:275,height:340})
}return this.keywordsTreeFieldSet
},getThesaurusComboBox:function(){if(!this.thesaurusComboBox){var thesaurusStore=new Ext.data.JsonStore({autoDestroy:true,idProperty:"key",fields:[{name:"key"},{name:"dname"},{name:"fname"},{name:"type"}],listeners:{load:function(store,records,options){},scope:this}});
this.thesaurusComboBox=new Ext.form.ComboBox({fieldLabel:Openwis.i18n("ThesauriManagement.Search.Thesaurus"),name:"thesaurus",mode:"local",typeAhead:true,triggerAction:"all",selectOnFocus:true,store:thesaurusStore,editable:false,width:330,displayField:"fname",valueField:"key",value:"",listeners:{scope:this,select:function(grid){var thesaurusSelected=this.thesaurusComboBox.getStore().getById(this.thesaurusComboBox.getValue()).data;
var root=new Ext.tree.AsyncTreeNode({text:thesaurusSelected.fname,draggable:false,id:"0"});
if(!this.loader){this.loader=new Openwis.Data.JeevesJsonTreeLoader({url:configOptions.locService+"/xml.thesaurus.getKeywordsNode",createNode:function(attr){attr.iconCls="item-agents";
attr.leaf=attr.keyword.leaf;
attr.text=attr.keyword.value;
return Ext.tree.TreeLoader.prototype.createNode.call(this,attr)
},listeners:{scope:this}})
}this.loader.on("beforeload",function(treeLoader,node){this.loader.baseParams.nodeId=node.attributes.id;
if(node.attributes.id!=0){this.loader.baseParams.code=node.attributes.keyword.relativeCode
}this.loader.baseParams.thesRef=this.getThesaurusComboBox().getValue()
},this);
if(!this.keywordsTreePanel){this.keywordsTreePanel=new Ext.tree.TreePanel({width:250,height:300,autoScroll:true,bodyCssClass:"treeForceVisible",containerScroll:true,animate:true,enableDD:false,loader:this.loader,selModel:new Ext.tree.MultiSelectionModel(),rootVisible:true});
this.keywordsTreePanel.setRootNode(root);
this.getKeywordsTreeFieldSet().add(this.keywordsTreePanel)
}else{this.keywordsTreePanel.setRootNode(root)
}this.doLayout()
}}});
this.thesaurusComboBox.getStore().loadData(this.config.thesaurusListDTO)
}return this.thesaurusComboBox
},getKeywordsMultiSelector:function(data){if(!this.isForm){var ds=new Ext.data.JsonStore({autoDestroy:true,idProperty:"text",fields:[{name:"text"},{name:"keyword"}]});
this.isForm=new Ext.ux.form.MultiSelect(Ext.applyIf({store:ds,displayField:"text",valueField:"text"},{legend:Openwis.i18n("ThesauriManagement.Search.SelectedKeywords"),droppable:true,draggable:true,width:275,height:340}))
}return this.isForm
},getKeywordsButtonsPanel:function(){if(!this.keywordsButtonsPanel){this.keywordsButtonsPanel=new Ext.Panel({autoScroll:true,border:false,layout:"table",style:{padding:"5px"},layoutConfig:{columns:1,tableAttrs:{style:{width:"100%",padding:"5px"}}}});
this.keywordsButtonsPanel.add(this.getRightButton());
this.keywordsButtonsPanel.add(this.getLeftButton());
this.keywordsButtonsPanel.add(this.getClearButton())
}return this.keywordsButtonsPanel
},getRightButton:function(){if(!this.rightButton){this.rightButton=new Ext.Button(new Ext.Action({iconCls:"icon-right",scope:this,handler:function(){var selectionsArray=[];
var selectionsArray=this.keywordsTreePanel.getSelectionModel().getSelectedNodes();
var records=[];
if(selectionsArray.length>0){for(var i=0;
i<selectionsArray.length;
i++){var textSelected=(selectionsArray[i]).attributes.text;
var idSelected=(selectionsArray[i]).attributes.id;
var keywordSelected=(selectionsArray[i]).attributes.keyword;
var inStore=this.getKeywordsMultiSelector().view.store.find("text",textSelected);
if(inStore==-1&&idSelected!=0){record=new Ext.data.Record({text:textSelected,keyword:keywordSelected});
records.push(record)
}}for(var i=0;
i<records.length;
i++){record=records[i];
this.getKeywordsMultiSelector().view.store.add(record);
selectionsArray.push((this.getKeywordsMultiSelector().view.store.getCount()-1))
}this.getKeywordsMultiSelector().view.refresh();
var si=this.getKeywordsMultiSelector().store.sortInfo;
if(si){this.getKeywordsMultiSelector().store.sort(si.field,si.direction)
}this.getKeywordsMultiSelector().view.select(selectionsArray)
}this.doLayout()
}}))
}return this.rightButton
},getLeftButton:function(){if(!this.leftButton){this.leftButton=new Ext.Button(new Ext.Action({iconCls:"icon-remove",scope:this,handler:function(){var selectionsArray=this.getKeywordsMultiSelector().view.getSelectedIndexes();
var records=[];
if(selectionsArray.length>0){for(var i=0;
i<selectionsArray.length;
i++){record=this.getKeywordsMultiSelector().view.store.getAt(selectionsArray[i]);
records.push(record)
}for(var i=0;
i<records.length;
i++){record=records[i];
this.getKeywordsMultiSelector().view.store.remove(record)
}this.getKeywordsMultiSelector().view.refresh();
this.doLayout()
}}}))
}return this.leftButton
},getClearButton:function(){if(!this.clearButton){this.clearButton=new Ext.Button(new Ext.Action({iconCls:"icon-removeAll",scope:this,handler:function(){this.getKeywordsMultiSelector().view.store.removeAll();
this.getKeywordsMultiSelector().view.refresh();
this.doLayout()
}}))
}return this.clearButton
},getSelectedNodeInfos:function(){var selectedNodeInfos={};
selectedNodeInfos.text=this.keywordsTreePanel.getSelectionModel().getSelectedNode().attributes.text;
return selectedNodeInfos
},getAddAction:function(){if(!this.addAction){this.addAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Add"),scope:this,handler:function(){if(this.isXML){var getHandler=new Openwis.Handler.GetNoJsonResponse({url:configOptions.locService+"/xml.thesaurus.getKeywordsXml",params:this.getKeywordsInfos(),listeners:{success:function(result){var keywordsSelected=new Array();
var keyword=result;
if(keyword.indexOf("<gmd:MD_Keywords")!=-1){keywordsSelected.push(keyword)
}this.fireEvent("keywordsSelection",keywordsSelected);
this.close()
},scope:this}});
getHandler.proceed()
}else{var records=this.getKeywordsMultiSelector().view.store.collect("text");
var returnArray=[];
for(var i=0;
i<records.length;
i++){returnArray.push(records[i])
}returnArray=returnArray.join(" | ");
this.fireEvent("keywordsSelection",returnArray);
this.close()
}}})
}return this.addAction
},getCancelAction:function(){if(!this.cancelAction){this.cancelAction=new Ext.Action({text:Openwis.i18n("Common.Btn.Close"),scope:this,handler:function(){this.close()
}})
}return this.cancelAction
},getKeywordsInfos:function(){var keywordsInfos={};
var allKeywords=new Array();
var record=null;
var keyword=null;
for(var i=0;
i<this.getKeywordsMultiSelector().view.store.getCount();
i++){record=this.getKeywordsMultiSelector().view.store.getAt(i);
keyword=record.data.keyword;
var aKeyword={};
aKeyword.code=keyword.code;
aKeyword.thesaurus=keyword.thesaurus;
aKeyword.value=keyword.value;
allKeywords.push(aKeyword)
}keywordsInfos.keywordListDTO=allKeywords;
return keywordsInfos
}});Ext.ns("geonet");
geonet.Constants={DIALOG_MAIN_DIV:"md_dialog_tb",TAB_MENU:"md_dialog_tab_menu",VIEW_SIMPLE:"simple",VIEW_ISO_MIN:"ISOMinimum",VIEW_ISO_CORE:"ISOCore",VIEW_ISO_ALL:"ISOAll",VIEW_METADATA:"metadata",VIEW_IDENTIFICATION:"identification",VIEW_MAINTENANCE:"maintenance",VIEW_CONSTRAINTS:"constraints",VIEW_SPATIAL:"spatial",VIEW_REFSYS:"refSys",VIEW_DISTRIBUTION:"distribution",VIEW_DATAQUALITY:"dataQuality",VIEW_APPSCHEMA:"appSchInfo",VIEW_PORCAT:"porCatInfo",VIEW_CONTENTINFO:"contentInfo",VIEW_EXTINFO:"extensionInfo",VIEW_XML:"xml",STATE_VIEW:"show",STATE_EDIT:"edit",DIALOG_CONTENT:"md_dialog_content",BTN_DISPLAY_TOGGLE:"md_dialog_edit_btn",BTN_SAVE:"md_dialog_save_btn",BTN_REVERT:"md_dialog_revert_btn",BTN_PREFIX:"btn_",ACTION_UPDATE:"metadata.update",ACTION_REVERT:"metadata.update.forgetandfinish",ACTION_EMBEDDED_UPDATE:"metadata.update.embedded"};
geonet.MetadataDialog={errorTemplate:new Ext.Template('<div id="error" style="margin:10px">',"<h2>Error loading page</h2>","<ul>","<li>Metadata id: {id}</li>","<li>Failing service: {service}</li>","<li>Error code: {code}</li>","<li>Error Message: {msg}</li>","</ul>","</div>"),window:null,idParams:null,view:geonet.Constants.VIEW_SIMPLE,state:geonet.Constants.STATE_VIEW,downloadEditScripts:true,loadWithId:function(id,title,tab,edit,editable){geonet.MetadataDialog.prepareLoad(title,tab,{id:id},edit,editable)
},loadWithUUID:function(uuid,title,tab,edit,editable){geonet.MetadataDialog.prepareLoad(title,tab,{uuid:uuid},edit,editable)
},changeView:function(viewId,skipLoad,button){Ext.getCmp(geonet.Constants.BTN_PREFIX+geonet.MetadataDialog.view).enable();
button=button||Ext.getCmp(geonet.Constants.BTN_PREFIX+viewId).disable();
button.disable();
var dialog=geonet.MetadataDialog;
dialog.view=viewId;
var menu=Ext.getCmp(geonet.Constants.TAB_MENU);
menu.setText(dialog.viewButtonName());
if(!skipLoad){if(dialog.state===geonet.Constants.STATE_EDIT){var formParams=$("editForm").serialize(true);
var otherParams={method:"POST"};
var params=dialog.requestParams(geonet.Constants.ACTION_EMBEDDED_UPDATE,formParams,otherParams);
dialog.customLoad(params)
}else{dialog.load()
}}},prepareLoad:function(title,tab,id,edit,editable){var self=geonet.MetadataDialog;
if(self.window===null){self.window=new Ext.Window({title:Ext.util.Format.htmlEncode(title),items:{html:"<div></div>",id:geonet.Constants.DIALOG_CONTENT,bodyStyle:"overflow-y:auto; width: 90%",tbar:new Ext.Toolbar({id:geonet.Constants.DIALOG_MAIN_DIV,items:[{xtype:"tbbutton",id:geonet.Constants.BTN_DISPLAY_TOGGLE,text:Openwis.i18n("Common.Btn.Edit"),disabled:true,handler:function(btn){self.toggleState(btn)
}},{xtype:"tbbutton",id:geonet.Constants.BTN_SAVE,text:Openwis.i18n("Common.Btn.Save"),hidden:true,handler:self.save},{xtype:"tbbutton",id:geonet.Constants.BTN_REVERT,text:Openwis.i18n("Common.Btn.Revert"),hidden:true,handler:self.revert},{xtype:"tbfill"},{xtype:"tbbutton",id:geonet.Constants.TAB_MENU,text:self.viewButtonName(),menu:[self.viewButton(geonet.Constants.VIEW_SIMPLE,true),"-",self.viewButton(geonet.Constants.VIEW_ISO_MIN),self.viewButton(geonet.Constants.VIEW_ISO_CORE),self.viewButton(geonet.Constants.VIEW_ISO_ALL),"-",self.viewButton(geonet.Constants.VIEW_METADATA),self.viewButton(geonet.Constants.VIEW_IDENTIFICATION),self.viewButton(geonet.Constants.VIEW_MAINTENANCE),self.viewButton(geonet.Constants.VIEW_CONSTRAINTS),self.viewButton(geonet.Constants.VIEW_SPATIAL),self.viewButton(geonet.Constants.VIEW_REFSYS),self.viewButton(geonet.Constants.VIEW_DISTRIBUTION),self.viewButton(geonet.Constants.VIEW_DATAQUALITY),self.viewButton(geonet.Constants.VIEW_APPSCHEMA),self.viewButton(geonet.Constants.VIEW_PORCAT),self.viewButton(geonet.Constants.VIEW_CONTENTINFO),self.viewButton(geonet.Constants.VIEW_EXTINFO),"-",self.viewButton(geonet.Constants.VIEW_XML)],listeners:{itemclick:function(baseItem,e){alert(e)
}}}]})},layout:"fit",constrain:true,maximizable:true,width:875,height:500,onEsc:function(){self.window.close()
},listeners:{beforeclose:function(){var close=true;
if(self.state===geonet.Constants.STATE_EDIT){close=false;
self.state="quitting";
self.window.disable();
self.endEdit(function(){setTimeout(function(){self.window.close()
},100)
},function(btn){if(btn==="cancel"){self.window.enable();
self.state=geonet.Constants.STATE_EDIT
}})
}return close
},close:function(){self.window=null;
self.state=geonet.Constants.STATE_VIEW
}}});
self.window.show();
self.window.center();
self.idParams=id;
if(edit){self.toggleState(undefined)
}else{self.load(id)
}if(!editable){var toggleButton=Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
toggleButton.setVisible(false)
}}else{var oldTitle=self.window.title;
self.window.setTitle(title||Openwis.i18n("Metadata.Viewer.Title"));
if(!self.window.isVisible()){self.window.show()
}var toggleButton=Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
self.idParams=id;
if(self.state===geonet.Constants.STATE_VIEW){if(edit){self.toggleState(toggleButton)
}else{self.load(id)
}}else{if(edit){self.load(id)
}else{self.toggleState(toggleButton,function(btn){if(btn==="cancel"){self.window.setTitle(oldTitle)
}})
}}}},requestParams:function(service,formParams,attributes){var dialog=geonet.MetadataDialog;
service=service||"metadata."+dialog.state+".embedded";
var params={};
Ext.apply(params,dialog.idParams);
Ext.apply(params,formParams);
params.currTab=dialog.view;
params.download_scripts=dialog.downloadEditScripts;
var finalAttributes={};
if(attributes!==undefined&&attributes!==null){finalAttributes=Ext.apply(finalAttributes,attributes)
}Ext.applyIf(finalAttributes,{service:service,url:getGNServiceURL(service),method:"GET",params:params,disableCaching:false,text:Openwis.i18n("Common.Loading.Message"),timeout:30000,scripts:dialog.state===geonet.Constants.STATE_EDIT&&dialog.downloadEditScripts});
if(finalAttributes.method.toUpperCase()==="POST"){finalAttributes.headers={"Content-Type":"application/x-www-form-urlencoded; charset=UTF-8"}
}return finalAttributes
},load:function(id){var dialog=geonet.MetadataDialog;
if(id){dialog.idParams=id
}var params=Ext.apply(dialog.requestParams(),id);
dialog.customLoad(params)
},customLoad:function(request){var self=geonet.MetadataDialog;
var content=Ext.getCmp(geonet.Constants.DIALOG_CONTENT);
var toggleButton=Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
var saveButton=Ext.getCmp(geonet.Constants.BTN_SAVE);
var revertButton=Ext.getCmp(geonet.Constants.BTN_REVERT);
toggleButton.disable();
saveButton.disable();
revertButton.disable();
content.getUpdater().abort();
var finalRequest=Ext.apply({callback:function(el,success,response,options){if(success){extentMap.initMapDiv();
if(self.state===geonet.Constants.STATE_EDIT){self.downloadEditScripts=false;
initCalendar(content.el.dom);
validateMetadataFields()
}toggleButton.enable();
saveButton.enable();
revertButton.enable()
}else{var id=self.idParams.id?self.idParams.id:self.idParams.uuid;
self.errorTemplate.overwrite(el,{code:response.status,id:id,msg:response.statusText,service:options.service})
}}},request);
content.load(finalRequest)
},action:function(action,view){var dialog=geonet.MetadataDialog;
if(view){if(dialog.view===view&&action===geonet.Constants.ACTION_UPDATE){return
}dialog.changeView(view,true)
}var formParams=$("editForm").serialize(true);
var otherParams={method:"POST"};
dialog.customLoad(dialog.requestParams(action+".embedded",formParams,otherParams))
},viewButton:function(viewId,selected){var self=geonet.MetadataDialog;
return{id:geonet.Constants.BTN_PREFIX+viewId,disabled:selected,text:self.viewName(viewId),handler:function(btn){self.changeView(viewId,false,btn)
}}
},viewName:function(viewId){viewId=viewId||geonet.MetadataDialog.view;
return Openwis.i18n("Metadata.ViewerEditor.View."+viewId)
},viewButtonName:function(){return"View - "+geonet.MetadataDialog.viewName()
},toggleState:function(btn,endEditBtnFn){if(btn===undefined){btn=Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE)
}btn.disable();
var setButtonVisibility=function(visible){Ext.getCmp(geonet.Constants.BTN_SAVE).setVisible(visible);
Ext.getCmp(geonet.Constants.BTN_REVERT).setVisible(visible)
};
var dialog=geonet.MetadataDialog;
if(geonet.MetadataDialog.state===geonet.Constants.STATE_VIEW){dialog.state=geonet.Constants.STATE_EDIT;
btn.setText(Openwis.i18n("Common.Btn.View"));
setButtonVisibility(true);
geonet.MetadataDialog.load()
}else{dialog.endEdit(function(){dialog.state=geonet.Constants.STATE_VIEW;
btn.setText(Openwis.i18n("Common.Btn.Edit"));
setButtonVisibility(false);
dialog.load()
},endEditBtnFn)
}},endEdit:function(endFunction,postAnswerFn){postAnswerFn=postAnswerFn||function(){};
var dialog=geonet.MetadataDialog;
Ext.Msg.show({title:Openwis.i18n("Metadata.ViewerEditor.View.endEdit.title"),msg:Openwis.i18n("Metadata.ViewerEditor.View.confirmEndEdit"),buttons:Ext.Msg.YESNOCANCEL,fn:function(btn){postAnswerFn(btn);
if(btn==="yes"){if($("editForm")==null){dialog.editAction(geonet.Constants.ACTION_REVERT,{version:dialog.getVersion()},endFunction)
}else{var formParams=$("editForm").serialize(true);
dialog.editAction(geonet.Constants.ACTION_EMBEDDED_UPDATE,formParams,endFunction)
}}else{if(btn==="no"){dialog.editAction(geonet.Constants.ACTION_REVERT,{version:dialog.getVersion()},endFunction)
}}},animEl:"elId",icon:Ext.MessageBox.QUESTION})
},save:function(){var dialog=geonet.MetadataDialog;
var formParams=$("editForm").serialize(true);
var otherParams={method:"POST",text:Openwis.i18n("Metadata.ViewerEditor.View.Saving")};
dialog.customLoad(dialog.requestParams(geonet.Constants.ACTION_EMBEDDED_UPDATE,formParams,otherParams))
},getVersion:function(){var content=Ext.get(geonet.Constants.DIALOG_CONTENT);
var versionInput=content.query("input[@name=version]");
if(versionInput.length>=1){return versionInput[0].getValue()
}else{return -1
}},revert:function(){var dialog=geonet.MetadataDialog;
if(confirm(Openwis.i18n("Metadata.ViewerEditor.View.confirmRevert"))){dialog.editAction(geonet.Constants.ACTION_REVERT,{version:dialog.getVersion()},dialog.load)
}},editAction:function(service,postData,endFunction){var dialog=geonet.MetadataDialog;
var content=Ext.get(geonet.Constants.DIALOG_CONTENT);
var params=dialog.requestParams(service,postData,{method:"POST"});
var mask=new Ext.LoadMask(content,{removeMask:true});
mask.show();
content.getUpdater().abort();
params=Ext.apply(params,{callback:function(options,success,response){mask.hide();
if(success){endFunction()
}else{alert("failed to save data: "+response.statusText)
}}});
Ext.Ajax.request(params)
}};
Ext.ns("metadata.edit.embedded");
metadata.edit.embedded.doTabAction=geonet.MetadataDialog.action;
metadata.edit.embedded.doAction=geonet.MetadataDialog.action;