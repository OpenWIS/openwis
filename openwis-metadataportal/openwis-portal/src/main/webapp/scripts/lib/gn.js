var getGNServiceURL=function(service){return Env.locService+"/"+service
};
function init(){}function translate(text){return translations[text]||text
}function replaceStringParams(text,params){var newText=text;
for(var i=0;
i<params.length;
i++){newText=newText.replace("$"+(i+1),params[i])
}return newText
}function get_cookie(cookie_name){var results=document.cookie.match(cookie_name+"=(.*?)(;|$)");
if(results){return(unescape(results[1]))
}else{return null
}}function popNew(a){msgWindow=window.open(a,"displayWindow","location=no, toolbar=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=600");
msgWindow.focus()
}function openPage(what,type){msgWindow=window.open(what,type,"location=yes, toolbar=yes, directories=yes, status=yes, menubar=yes, scrollbars=yes, resizable=yes, width=800, height=600");
msgWindow.focus()
}function popFeedback(a){msgWindow=window.open(a,"feedbackWindow","location=no, toolbar=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=600");
msgWindow.focus()
}function popWindow(a){msgWindow=window.open(a,"popWindow","location=no, toolbar=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=600");
msgWindow.focus()
}function popInterMap(a){msgWindow=window.open(a,"InterMap","location=no, toolbar=no, directories=no, status=no, menubar=no, scrollbars=yes, resizable=yes, width=800, height=600");
msgWindow.focus()
}function goSubmit(form_name){document.forms[form_name].submit()
}function goReset(form_name){document.forms[form_name].reset()
}function entSub(form_name){if(window.event&&window.event.keyCode==13){goSubmit(form_name)
}else{return true
}}function goBack(){history.back()
}function processCancel(){document.close()
}function load(url){document.location.href=url
}function doConfirm(url,message){if(confirm(message)){load(url);
return true
}return false
}function feedbackSubmit(){var f=$("feedbackf");
if(isWhitespace(f.comments.value)){f.comments.value=translate("noComment")
}if(isWhitespace(f.name.value)||isWhitespace(f.org.value)){alert(translate("addName"));
return
}else{if(!isEmail(f.email.value)){alert(translate("checkEmail"));
return
}}Modalbox.show(getGNServiceURL("file.download"),{height:400,width:600,params:f.serialize(true)})
}function doDownload(id,all){var list=$("downloadlist").getElementsByTagName("INPUT");
var pars="&id="+id+"&access=private";
var selected=false;
for(var i=0;
i<list.length;
i++){if(list[i].checked||all!=null){selected=true;
var name=list[i].getAttribute("name");
pars+="&fname="+name
}}if(!selected){alert(translate("selectOneFile"));
return
}Modalbox.show(getGNServiceURL("file.disclaimer")+"?"+pars,{height:400,width:600})
}function massiveOperation(service,title,width,message){if(message!=null){if(!confirm(message)){return
}}var url=Env.locService+"/"+service;
Modalbox.show(url,{title:title,width:width,afterHide:function(){if($("simple_search_pnl").visible()){runSimpleSearch()
}else{if($("advanced_search_pnl").visible()){runAdvancedSearch()
}else{$("search-results-content").hide()
}}runRssSearch()
}})
}function oActionsInit(name,id){if(id===undefined){id=""
}$(name+"Ele"+id).style.width=$(name+id).getWidth();
$(name+"Ele"+id).style.top=$(name+id).positionedOffset().top+$(name+id).getHeight()+"px";
$(name+"Ele"+id).style.left=$(name+id).positionedOffset().left+"px"
}function oActions(name,id){var on="../../images/plus.gif";
var off="../../images/minus.png";
if(id===undefined){id=""
}oActionsInit(name,id);
if($(name+"Ele"+id).style.display=="none"){$(name+"Ele"+id).style.display="block";
$(name+"Img"+id).src=off
}else{$(name+"Ele"+id).style.display="none";
$(name+"Img"+id).src=on
}}function actionOnSelect(msg){if($("nbselected").innerHTML==0&&$("oAcOsEle").style.display=="none"){alert(msg)
}else{oActions("oAcOs")
}}function checkMassiveNewOwner(action,title){if($("user").value==""){alert(translate("selectNewOwner"));
return false
}if($("group").value==""){alert(translate("selectOwnerGroup"));
return false
}Modalbox.show(getGNServiceURL(action),{title:title,params:$("massivenewowner").serialize(true),afterHide:function(){if($("simple_search_pnl").visible()){runSimpleSearch()
}else{if($("advanced_search_pnl").visible()){runAdvancedSearch()
}else{$("search-results-content").hide()
}}runRssSearch()
}})
}function addGroups(xmlRes){var list=xml.children(xmlRes,"group");
$("group").options.length=0;
for(var i=0;
i<list.length;
i++){var id=xml.evalXPath(list[i],"id");
var name=xml.evalXPath(list[i],"name");
var opt=document.createElement("option");
opt.text=name;
opt.value=id;
if(list.length==1){opt.selected=true
}$("group").options.add(opt)
}}function addGroupsCallback_OK(xmlRes){if(xmlRes.nodeName=="error"){ker.showError(translate("cannotRetrieveGroup"),xmlRes);
$("group").options.length=0;
$("group").value="";
var user=$("user");
for(i=0;
i<user.options.length;
i++){user.options[i].selected=false
}}else{addGroups(xmlRes)
}}function doGroups(userid){var request=ker.createRequest("id",userid);
ker.send("xml.usergroups.list",request,addGroupsCallback_OK)
}function processRegSub(url){var invalid=" ";
var minLength=6;
if(document.userregisterform.name.value.length==0){alert(translate("firstNameMandatory"));
return
}if(isWhitespace(document.userregisterform.name.value)){alert(translate("firstNameMandatory"));
return
}if(document.userregisterform.name.value.indexOf(invalid)>-1){alert(translate("spacesNot"));
return
}if(document.userregisterform.surname.value.length==0){alert(translate("lastNameMandatory"));
return
}if(isWhitespace(document.userregisterform.surname.value)){alert(translate("lastNameMandatory"));
return
}if(document.userregisterform.surname.value.indexOf(invalid)>-1){alert(translate("spacesNot"));
return
}if(!isEmail(document.userregisterform.email.value)){alert(translate("emailAddressInvalid"));
return
}var myAjax=new Ajax.Request(getGNServiceURL(url),{method:"post",parameters:$("userregisterform").serialize(true),onSuccess:function(req){var output=req.responseText;
var title=translate("yourRegistration");
Modalbox.show(output,{title:title,width:300})
},onFailure:function(req){alert(translate("registrationFailed")+" "+req.responseText+" status: "+req.status+" - "+translate("tryAgain"))
}})
}function displayBox(content,contentDivId,modal){var id=contentDivId+"Box";
var w=Ext.getCmp(id);
if(w==undefined){w=new Ext.Window({title:translate(contentDivId),id:id,layout:"fit",modal:modal,constrain:true,width:400,collapsible:(modal?false:true),autoScroll:true,iconCls:contentDivId+"Icon",closeAction:"hide",onEsc:"hide",listeners:{hide:function(){this.hide()
}},contentEl:contentDivId})
}if(w){if(content!=null){$(contentDivId).innerHTML="";
$(contentDivId).innerHTML=content;
$(contentDivId).style.display="block"
}w.show();
w.setHeight(345);
w.anchorTo(Ext.getBody(),(modal?"c-c":"tr-tr"))
}};if(!window.Modalbox){var Modalbox=new Object()
}Modalbox.Methods={overrideAlert:false,focusableElements:new Array,currFocused:0,initialized:false,active:true,options:{title:"ModalBox Window",overlayClose:true,width:500,height:90,overlayOpacity:0.65,overlayDuration:0.25,slideDownDuration:0.5,slideUpDuration:0.5,resizeDuration:0.25,inactiveFade:true,transitions:false,loadingString:"Please wait. Loading...",closeString:"Close window",closeValue:"&times;",params:{},method:"get",autoFocusing:true,aspnet:false},_options:new Object,setOptions:function(options){Object.extend(this.options,options||{})
},_init:function(options){Object.extend(this._options,this.options);
this.setOptions(options);
this.MBoverlay=new Element("div",{id:"MB_overlay",opacity:"0"});
this.MBwindow=new Element("div",{id:"MB_window",style:"display: none"}).update(this.MBframe=new Element("div",{id:"MB_frame"}).update(this.MBheader=new Element("div",{id:"MB_header"}).update(this.MBcaption=new Element("div",{id:"MB_caption"}))));
this.MBclose=new Element("a",{id:"MB_close",title:this.options.closeString,href:"#"}).update("<span>"+this.options.closeValue+"</span>");
this.MBheader.insert({bottom:this.MBclose});
this.MBcontent=new Element("div",{id:"MB_content"}).update(this.MBloading=new Element("div",{id:"MB_loading"}).update(this.options.loadingString));
this.MBframe.insert({bottom:this.MBcontent});
var injectToEl=this.options.aspnet?$(document.body).down("form"):$(document.body);
injectToEl.insert({top:this.MBwindow});
injectToEl.insert({top:this.MBoverlay});
this.initScrollX=window.pageXOffset||document.body.scrollLeft||document.documentElement.scrollLeft;
this.initScrollY=window.pageYOffset||document.body.scrollTop||document.documentElement.scrollTop;
this.hideObserver=this._hide.bindAsEventListener(this);
this.kbdObserver=this._kbdHandler.bindAsEventListener(this);
this._initObservers();
this.initialized=true
},show:function(content,options){if(!this.initialized){this._init(options)
}this.content=content;
this.setOptions(options);
if(this.options.title){$(this.MBcaption).update(this.options.title)
}else{$(this.MBheader).hide();
$(this.MBcaption).hide()
}if(this.MBwindow.style.display=="none"){this._appear();
this.event("onShow")
}else{this._update();
this.event("onUpdate")
}},hide:function(options){if(this.initialized){if(options&&typeof options.element!="function"){Object.extend(this.options,options)
}this.event("beforeHide");
if(this.options.transitions){Effect.SlideUp(this.MBwindow,{duration:this.options.slideUpDuration,transition:Effect.Transitions.sinoidal,afterFinish:this._deinit.bind(this)})
}else{$(this.MBwindow).hide();
this._deinit()
}}else{throw ("Modalbox is not initialized.")
}},_hide:function(event){event.stop();
if(event.element().id=="MB_overlay"&&!this.options.overlayClose){return false
}this.hide()
},alert:function(message){var html='<div class="MB_alert"><p>'+message+'</p><input type="button" onclick="Modalbox.hide()" value="OK" /></div>';
Modalbox.show(html,{title:"Alert: "+document.title,width:300})
},_appear:function(){if(Prototype.Browser.IE&&!navigator.appVersion.match(/\b7.0\b/)){window.scrollTo(0,0);
this._prepareIE("100%","hidden")
}this._setWidth();
this._setPosition();
if(this.options.transitions){$(this.MBoverlay).setStyle({opacity:0});
new Effect.Fade(this.MBoverlay,{from:0,to:this.options.overlayOpacity,duration:this.options.overlayDuration,afterFinish:function(){new Effect.SlideDown(this.MBwindow,{duration:this.options.slideDownDuration,transition:Effect.Transitions.sinoidal,afterFinish:function(){this._setPosition();
this.loadContent()
}.bind(this)})
}.bind(this)})
}else{$(this.MBoverlay).setStyle({opacity:this.options.overlayOpacity});
$(this.MBwindow).show();
this._setPosition();
this.loadContent()
}this._setWidthAndPosition=this._setWidthAndPosition.bindAsEventListener(this);
Event.observe(window,"resize",this._setWidthAndPosition)
},resize:function(byWidth,byHeight,options){var wHeight=$(this.MBwindow).getHeight();
var wWidth=$(this.MBwindow).getWidth();
var hHeight=$(this.MBheader).getHeight();
var cHeight=$(this.MBcontent).getHeight();
var newHeight=((wHeight-hHeight+byHeight)<cHeight)?(cHeight+hHeight-wHeight):byHeight;
if(options){this.setOptions(options)
}if(this.options.transitions){new Effect.ScaleBy(this.MBwindow,byWidth,newHeight,{duration:this.options.resizeDuration,afterFinish:function(){this.event("_afterResize");
this.event("afterResize")
}.bind(this)})
}else{this.MBwindow.setStyle({width:wWidth+byWidth+"px",height:wHeight+newHeight+"px"});
if(Prototype.Browser.Gecko){this.MBwindow.setStyle({overflow:"hidden"})
}setTimeout(function(){this.event("_afterResize");
this.event("afterResize")
}.bind(this),1)
}},resizeToContent:function(options){var byHeight=this.options.height-this.MBwindow.offsetHeight;
if(byHeight!=0){if(options){this.setOptions(options)
}Modalbox.resize(0,byHeight)
}},resizeToInclude:function(element,options){var el=$(element);
var elHeight=el.getHeight()+parseInt(el.getStyle("margin-top"))+parseInt(el.getStyle("margin-bottom"))+parseInt(el.getStyle("border-top-width"))+parseInt(el.getStyle("border-bottom-width"));
if(elHeight>0){if(options){this.setOptions(options)
}Modalbox.resize(0,elHeight)
}},_update:function(){$(this.MBcontent).update("");
this.MBcontent.appendChild(this.MBloading);
$(this.MBloading).update(this.options.loadingString);
this.currentDims=[this.MBwindow.offsetWidth,this.MBwindow.offsetHeight];
Modalbox.resize((this.options.width-this.currentDims[0]),(this.options.height-this.currentDims[1]),{_afterResize:this._loadAfterResize.bind(this)})
},loadContent:function(){if(this.event("beforeLoad")!=false){if(typeof this.content=="string"){var htmlRegExp=new RegExp(/<\/?[^>]+>/gi);
if(htmlRegExp.test(this.content)){this._insertContent(this.content.stripScripts());
this._putContent(function(){this.content.extractScripts().map(function(script){return eval(script.replace("<!--","").replace("// -->",""))
}.bind(window))
}.bind(this))
}else{new Ajax.Request(this.content,{method:this.options.method.toLowerCase(),parameters:this.options.params,onSuccess:function(transport){var response=new String(transport.responseText);
this._insertContent(transport.responseText.stripScripts());
this._putContent(function(){response.extractScripts().map(function(script){return eval(script.replace("<!--","").replace("// -->",""))
}.bind(window))
})
}.bind(this),onException:function(instance,exception){Modalbox.hide();
throw ("Modalbox Loading Error: "+exception)
}})
}}else{if(typeof this.content=="object"){this._insertContent(this.content);
this._putContent()
}else{Modalbox.hide();
throw ("Modalbox Parameters Error: Please specify correct URL or HTML element (plain HTML or object)")
}}}},_insertContent:function(content){$(this.MBcontent).hide().update("");
if(typeof content=="string"){setTimeout(function(){this.MBcontent.update(content)
}.bind(this),1)
}else{if(typeof content=="object"){var _htmlObj=content.cloneNode(true);
if(content.id){content.id="MB_"+content.id
}$(content).select("*[id]").each(function(el){el.id="MB_"+el.id
});
this.MBcontent.appendChild(_htmlObj);
this.MBcontent.down().show();
if(Prototype.Browser.IE){$$("#MB_content select").invoke("setStyle",{visibility:""})
}}}},_putContent:function(callback){if(this.options.height==this._options.height){setTimeout(function(){Modalbox.resize(0,$(this.MBcontent).getHeight()-$(this.MBwindow).getHeight()+$(this.MBheader).getHeight(),{afterResize:function(){this.MBcontent.show().makePositioned();
this.focusableElements=this._findFocusableElements();
this._setFocus();
setTimeout(function(){if(callback!=undefined){callback()
}this.event("afterLoad")
}.bind(this),1)
}.bind(this)})
}.bind(this),1)
}else{setTimeout(function(){this._setWidth();
if(Prototype.Browser.Gecko){this.MBwindow.setStyle({overflow:"hidden"})
}this.MBcontent.setStyle({overflow:"auto",height:$(this.MBwindow).getHeight()-$(this.MBheader).getHeight()-20+"px"});
this.MBcontent.show();
this.focusableElements=this._findFocusableElements();
this._setFocus();
if(callback!=undefined){callback()
}this.event("afterLoad")
}.bind(this),1)
}},activate:function(options){this.setOptions(options);
this.active=true;
$(this.MBclose).observe("click",this.hideObserver);
if(this.options.overlayClose){$(this.MBoverlay).observe("click",this.hideObserver)
}$(this.MBclose).show();
if(this.options.transitions&&this.options.inactiveFade){new Effect.Appear(this.MBwindow,{duration:this.options.slideUpDuration})
}},deactivate:function(options){this.setOptions(options);
this.active=false;
$(this.MBclose).stopObserving("click",this.hideObserver);
if(this.options.overlayClose){$(this.MBoverlay).stopObserving("click",this.hideObserver)
}$(this.MBclose).hide();
if(this.options.transitions&&this.options.inactiveFade){new Effect.Fade(this.MBwindow,{duration:this.options.slideUpDuration,to:0.75})
}},_initObservers:function(){$(this.MBclose).observe("click",this.hideObserver);
if(this.options.overlayClose){$(this.MBoverlay).observe("click",this.hideObserver)
}if(Prototype.Browser.IE){Event.observe(document,"keydown",this.kbdObserver)
}else{Event.observe(document,"keypress",this.kbdObserver)
}},_removeObservers:function(){$(this.MBclose).stopObserving("click",this.hideObserver);
if(this.options.overlayClose){$(this.MBoverlay).stopObserving("click",this.hideObserver)
}if(Prototype.Browser.IE){Event.stopObserving(document,"keydown",this.kbdObserver)
}else{Event.stopObserving(document,"keypress",this.kbdObserver)
}},_loadAfterResize:function(){this._setWidth();
this._setPosition();
this.loadContent()
},_setFocus:function(){if(this.focusableElements.length>0&&this.options.autoFocusing==true){var firstEl=this.focusableElements.find(function(el){return el.tabIndex==1
})||this.focusableElements.first();
this.currFocused=this.focusableElements.toArray().indexOf(firstEl);
firstEl.focus()
}else{if($(this.MBclose).visible()){$(this.MBclose).focus()
}}},_findFocusableElements:function(){var mycontent=[];
var content=this.MBcontent.descendants();
for(var index=0,len=content.length;
index<len;
++index){var elem=content[index];
if(["textarea","select","button"].include(elem.tagName.toLowerCase())){mycontent.push(elem)
}else{if(elem.tagName.toLowerCase()=="input"&&elem.visible()&&elem.type!="hidden"){mycontent.push(elem)
}else{if(elem.tagName.toLowerCase()=="a"&&elem.href){mycontent.push(elem)
}}}}mycontent.invoke("addClassName","MB_focusable");
return mycontent
},_kbdHandler:function(event){var node=event.element();
switch(event.keyCode){case Event.KEY_TAB:event.stop();
if(node!=this.focusableElements[this.currFocused]){this.currFocused=this.focusableElements.toArray().indexOf(node)
}if(!event.shiftKey){if(this.currFocused==this.focusableElements.length-1){if(this.focusableElements.first()!=null){this.focusableElements.first().focus()
}this.currFocused=0
}else{this.currFocused++;
this.focusableElements[this.currFocused].focus()
}}else{if(this.currFocused==0){this.focusableElements.last().focus();
this.currFocused=this.focusableElements.length-1
}else{this.currFocused--;
this.focusableElements[this.currFocused].focus()
}}break;
case Event.KEY_ESC:if(this.active){this._hide(event)
}break;
case 32:this._preventScroll(event);
break;
case 0:if(event.which==32){this._preventScroll(event)
}break;
case Event.KEY_UP:case Event.KEY_DOWN:case Event.KEY_PAGEDOWN:case Event.KEY_PAGEUP:case Event.KEY_HOME:case Event.KEY_END:if(Prototype.Browser.WebKit&&!["textarea","select"].include(node.tagName.toLowerCase())){event.stop()
}else{if((node.tagName.toLowerCase()=="input"&&["submit","button"].include(node.type))||(node.tagName.toLowerCase()=="a")){event.stop()
}}break
}},_preventScroll:function(event){if(!["input","textarea","select","button"].include(event.element().tagName.toLowerCase())){event.stop()
}},_deinit:function(){this._removeObservers();
Event.stopObserving(window,"resize",this._setWidthAndPosition);
if(this.options.transitions){Effect.toggle(this.MBoverlay,"appear",{duration:this.options.overlayDuration,afterFinish:this._removeElements.bind(this)})
}else{this.MBoverlay.hide();
this._removeElements()
}$(this.MBcontent).setStyle({overflow:"",height:""})
},_removeElements:function(){$(this.MBoverlay).remove();
$(this.MBwindow).remove();
if(Prototype.Browser.IE&&!navigator.appVersion.match(/\b7.0\b/)){this._prepareIE("","");
window.scrollTo(this.initScrollX,this.initScrollY)
}if(typeof this.content=="object"){if(this.content.id&&this.content.id.match(/MB_/)){this.content.id=this.content.id.replace(/MB_/,"")
}this.content.select("*[id]").each(function(el){el.id=el.id.replace(/MB_/,"")
})
}this.initialized=false;
this.event("afterHide");
this.setOptions(this._options)
},_setWidth:function(){$(this.MBwindow).setStyle({width:this.options.width+"px",height:this.options.height+"px"})
},_setPosition:function(){$(this.MBwindow).setStyle({left:Math.round((Element.getWidth(document.body)-Element.getWidth(this.MBwindow))/2)+"px"})
},_setWidthAndPosition:function(){$(this.MBwindow).setStyle({width:this.options.width+"px"});
this._setPosition()
},_getScrollTop:function(){var theTop;
if(document.documentElement&&document.documentElement.scrollTop){theTop=document.documentElement.scrollTop
}else{if(document.body){theTop=document.body.scrollTop
}}return theTop
},_prepareIE:function(height,overflow){$$("html, body").invoke("setStyle",{width:height,height:height,overflow:overflow});
$$("select").invoke("setStyle",{visibility:overflow})
},event:function(eventName){if(this.options[eventName]){var returnValue=this.options[eventName]();
this.options[eventName]=null;
if(returnValue!=undefined){return returnValue
}else{return true
}}return true
}};
Object.extend(Modalbox,Modalbox.Methods);
if(Modalbox.overrideAlert){window.alert=Modalbox.alert
}Effect.ScaleBy=Class.create();
Object.extend(Object.extend(Effect.ScaleBy.prototype,Effect.Base.prototype),{initialize:function(element,byWidth,byHeight,options){this.element=$(element);
var options=Object.extend({scaleFromTop:true,scaleMode:"box",scaleByWidth:byWidth,scaleByHeight:byHeight},arguments[3]||{});
this.start(options)
},setup:function(){this.elementPositioning=this.element.getStyle("position");
this.originalTop=this.element.offsetTop;
this.originalLeft=this.element.offsetLeft;
this.dims=null;
if(this.options.scaleMode=="box"){this.dims=[this.element.offsetHeight,this.element.offsetWidth]
}if(/^content/.test(this.options.scaleMode)){this.dims=[this.element.scrollHeight,this.element.scrollWidth]
}if(!this.dims){this.dims=[this.options.scaleMode.originalHeight,this.options.scaleMode.originalWidth]
}this.deltaY=this.options.scaleByHeight;
this.deltaX=this.options.scaleByWidth
},update:function(position){var currentHeight=this.dims[0]+(this.deltaY*position);
var currentWidth=this.dims[1]+(this.deltaX*position);
currentHeight=(currentHeight>0)?currentHeight:0;
currentWidth=(currentWidth>0)?currentWidth:0;
this.setDimensions(currentHeight,currentWidth)
},setDimensions:function(height,width){var d={};
d.width=width+"px";
d.height=height+"px";
var topd=Math.round((height-this.dims[0])/2);
var leftd=Math.round((width-this.dims[1])/2);
if(this.elementPositioning=="absolute"||this.elementPositioning=="fixed"){if(!this.options.scaleFromTop){d.top=this.originalTop-topd+"px"
}d.left=this.originalLeft-leftd+"px"
}else{if(!this.options.scaleFromTop){d.top=-topd+"px"
}d.left=-leftd+"px"
}this.element.setStyle(d)
}});var digits="0123456789";
var lowercaseLetters="abcdefghijklmnopqrstuvwxyz";
var uppercaseLetters="ABCDEFGHIJKLMNOPQRSTUVWXYZ";
var whitespace=" \t\n\r";
var decimalPointDelimiter=".";
var phoneNumberDelimiters="()- ";
var validUSPhoneChars=digits+phoneNumberDelimiters;
var validWorldPhoneChars=digits+phoneNumberDelimiters+"+";
var SSNDelimiters="- ";
var validSSNChars=digits+SSNDelimiters;
var digitsInSocialSecurityNumber=9;
var digitsInUSPhoneNumber=10;
var ZIPCodeDelimiters="-";
var ZIPCodeDelimeter="-";
var validZIPCodeChars=digits+ZIPCodeDelimiters;
var digitsInZIPCode1=5;
var digitsInZIPCode2=9;
var creditCardDelimiters=" ";
var mPrefix="You did not enter a value into the ";
var mSuffix=" field. This is a required field. Please enter it now.";
var sUSLastName="Last Name";
var sUSFirstName="First Name";
var sWorldLastName="Family Name";
var sWorldFirstName="Given Name";
var sTitle="Title";
var sCompanyName="Company Name";
var sUSAddress="Street Address";
var sWorldAddress="Address";
var sCity="City";
var sStateCode="State Code";
var sWorldState="State, Province, or Prefecture";
var sCountry="Country";
var sZIPCode="ZIP Code";
var sWorldPostalCode="Postal Code";
var sPhone="Phone Number";
var sFax="Fax Number";
var sDateOfBirth="Date of Birth";
var sExpirationDate="Expiration Date";
var sEmail="Email";
var sSSN="Social Security Number";
var sCreditCardNumber="Credit Card Number";
var sOtherInfo="Other Information";
var iStateCode="This field must be a valid two character U.S. state abbreviation (like CA for California). Please reenter it now.";
var iZIPCode="This field must be a 5 or 9 digit U.S. ZIP Code (like 94043). Please reenter it now.";
var iUSPhone="This field must be a 10 digit U.S. phone number (like 415 555 1212). Please reenter it now.";
var iWorldPhone="This field must be a valid international phone number. Please reenter it now.";
var iSSN="This field must be a 9 digit U.S. social security number (like 123 45 6789). Please reenter it now.";
var iEmail="This field must be a valid email address (like foo@bar.com). Please reenter it now.";
var iCreditCardPrefix="This is not a valid ";
var iCreditCardSuffix=" credit card number. (Click the link on this form to see a list of sample numbers.) Please reenter it now.";
var iDay="This field must be a day number between 1 and 31.  Please reenter it now.";
var iMonth="This field must be a month number between 1 and 12.  Please reenter it now.";
var iYear="This field must be a 2 or 4 digit year number.  Please reenter it now.";
var iDatePrefix="The Day, Month, and Year for ";
var iDateSuffix=" do not form a valid date.  Please reenter them now.";
var pEntryPrompt="Please enter a ";
var pStateCode="2 character code (like CA).";
var pZIPCode="5 or 9 digit U.S. ZIP Code (like 94043).";
var pUSPhone="10 digit U.S. phone number (like 415 555 1212).";
var pWorldPhone="international phone number.";
var pSSN="9 digit U.S. social security number (like 123 45 6789).";
var pEmail="valid email address (like foo@bar.com).";
var pCreditCard="valid credit card number.";
var pDay="day number between 1 and 31.";
var pMonth="month number between 1 and 12.";
var pYear="2 or 4 digit year number.";
var defaultEmptyOK=false;
function makeArray(n){for(var i=1;
i<=n;
i++){this[i]=0
}return this
}var daysInMonth=makeArray(12);
daysInMonth[1]=31;
daysInMonth[2]=29;
daysInMonth[3]=31;
daysInMonth[4]=30;
daysInMonth[5]=31;
daysInMonth[6]=30;
daysInMonth[7]=31;
daysInMonth[8]=31;
daysInMonth[9]=30;
daysInMonth[10]=31;
daysInMonth[11]=30;
daysInMonth[12]=31;
var USStateCodeDelimiter="|";
var USStateCodes="AL|AK|AS|AZ|AR|CA|CO|CT|DE|DC|FM|FL|GA|GU|HI|ID|IL|IN|IA|KS|KY|LA|ME|MH|MD|MA|MI|MN|MS|MO|MT|NE|NV|NH|NJ|NM|NY|NC|ND|MP|OH|OK|OR|PW|PA|PR|RI|SC|SD|TN|TX|UT|VT|VI|VA|WA|WV|WI|WY|AE|AA|AE|AE|AP";
function isEmpty(s){return((s==null)||(s.length==0))
}function isWhitespace(s){var i;
if(isEmpty(s)){return true
}for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if(whitespace.indexOf(c)==-1){return false
}}return true
}function stripCharsInBag(s,bag){var i;
var returnString="";
for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if(bag.indexOf(c)==-1){returnString+=c
}}return returnString
}function stripCharsNotInBag(s,bag){var i;
var returnString="";
for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if(bag.indexOf(c)!=-1){returnString+=c
}}return returnString
}function stripWhitespace(s){return stripCharsInBag(s,whitespace)
}function charInString(c,s){for(i=0;
i<s.length;
i++){if(s.charAt(i)==c){return true
}}return false
}function stripInitialWhitespace(s){var i=0;
while((i<s.length)&&charInString(s.charAt(i),whitespace)){i++
}return s.substring(i,s.length)
}function isLetter(c){return(((c>="a")&&(c<="z"))||((c>="A")&&(c<="Z")))
}function isDigit(c){return((c>="0")&&(c<="9"))
}function isLetterOrDigit(c){return(isLetter(c)||isDigit(c))
}function isInteger(s){var i;
if(isEmpty(s)){if(isInteger.arguments.length==1){return defaultEmptyOK
}else{return(isInteger.arguments[1]==true)
}}for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if(!isDigit(c)){return false
}}return true
}function isSignedInteger(s){if(isEmpty(s)){if(isSignedInteger.arguments.length==1){return defaultEmptyOK
}else{return(isSignedInteger.arguments[1]==true)
}}else{var startPos=0;
var secondArg=defaultEmptyOK;
if(isSignedInteger.arguments.length>1){secondArg=isSignedInteger.arguments[1]
}if((s.charAt(0)=="-")||(s.charAt(0)=="+")){startPos=1
}return(isInteger(s.substring(startPos,s.length),secondArg))
}}function isPositiveInteger(s){var secondArg=defaultEmptyOK;
if(isPositiveInteger.arguments.length>1){secondArg=isPositiveInteger.arguments[1]
}return(isSignedInteger(s,secondArg)&&((isEmpty(s)&&secondArg)||(parseInt(s)>0)))
}function isNonnegativeInteger(s){var secondArg=defaultEmptyOK;
if(isNonnegativeInteger.arguments.length>1){secondArg=isNonnegativeInteger.arguments[1]
}return(isSignedInteger(s,secondArg)&&((isEmpty(s)&&secondArg)||(parseInt(s)>=0)))
}function isNegativeInteger(s){var secondArg=defaultEmptyOK;
if(isNegativeInteger.arguments.length>1){secondArg=isNegativeInteger.arguments[1]
}return(isSignedInteger(s,secondArg)&&((isEmpty(s)&&secondArg)||(parseInt(s)<0)))
}function isNonpositiveInteger(s){var secondArg=defaultEmptyOK;
if(isNonpositiveInteger.arguments.length>1){secondArg=isNonpositiveInteger.arguments[1]
}return(isSignedInteger(s,secondArg)&&((isEmpty(s)&&secondArg)||(parseInt(s)<=0)))
}function isFloat(s){var i;
var seenDecimalPoint=false;
if(isEmpty(s)){if(isFloat.arguments.length==1){return defaultEmptyOK
}else{return(isFloat.arguments[1]==true)
}}if(s==decimalPointDelimiter){return false
}for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if((c==decimalPointDelimiter)&&!seenDecimalPoint){seenDecimalPoint=true
}else{if(!isDigit(c)){return false
}}}return true
}function isSignedFloat(s){if(isEmpty(s)){if(isSignedFloat.arguments.length==1){return defaultEmptyOK
}else{return(isSignedFloat.arguments[1]==true)
}}else{var startPos=0;
var secondArg=defaultEmptyOK;
if(isSignedFloat.arguments.length>1){secondArg=isSignedFloat.arguments[1]
}if((s.charAt(0)=="-")||(s.charAt(0)=="+")){startPos=1
}return(isFloat(s.substring(startPos,s.length),secondArg))
}}function isAlphabetic(s){var i;
if(isEmpty(s)){if(isAlphabetic.arguments.length==1){return defaultEmptyOK
}else{return(isAlphabetic.arguments[1]==true)
}}for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if(!isLetter(c)){return false
}}return true
}function isAlphanumeric(s){var i;
if(isEmpty(s)){if(isAlphanumeric.arguments.length==1){return defaultEmptyOK
}else{return(isAlphanumeric.arguments[1]==true)
}}for(i=0;
i<s.length;
i++){var c=s.charAt(i);
if(!(isLetter(c)||isDigit(c))){return false
}}return true
}function reformat(s){var arg;
var sPos=0;
var resultString="";
for(var i=1;
i<reformat.arguments.length;
i++){arg=reformat.arguments[i];
if(i%2==1){resultString+=arg
}else{resultString+=s.substring(sPos,sPos+arg);
sPos+=arg
}}return resultString
}function isSSN(s){if(isEmpty(s)){if(isSSN.arguments.length==1){return defaultEmptyOK
}else{return(isSSN.arguments[1]==true)
}}return(isInteger(s)&&s.length==digitsInSocialSecurityNumber)
}function isUSPhoneNumber(s){if(isEmpty(s)){if(isUSPhoneNumber.arguments.length==1){return defaultEmptyOK
}else{return(isUSPhoneNumber.arguments[1]==true)
}}return(isInteger(s)&&s.length==digitsInUSPhoneNumber)
}function isInternationalPhoneNumber(s){if(isEmpty(s)){if(isInternationalPhoneNumber.arguments.length==1){return defaultEmptyOK
}else{return(isInternationalPhoneNumber.arguments[1]==true)
}}return(isPositiveInteger(s))
}function isZIPCode(s){if(isEmpty(s)){if(isZIPCode.arguments.length==1){return defaultEmptyOK
}else{return(isZIPCode.arguments[1]==true)
}}return(isInteger(s)&&((s.length==digitsInZIPCode1)||(s.length==digitsInZIPCode2)))
}function isStateCode(s){if(isEmpty(s)){if(isStateCode.arguments.length==1){return defaultEmptyOK
}else{return(isStateCode.arguments[1]==true)
}}return((USStateCodes.indexOf(s)!=-1)&&(s.indexOf(USStateCodeDelimiter)==-1))
}function isEmail(s){if(isEmpty(s)){if(isEmail.arguments.length==1){return defaultEmptyOK
}else{return(isEmail.arguments[1]==true)
}}if(isWhitespace(s)){return false
}var i=1;
var sLength=s.length;
while((i<sLength)&&(s.charAt(i)!="@")){i++
}if((i>=sLength)||(s.charAt(i)!="@")){return false
}else{i+=2
}while((i<sLength)&&(s.charAt(i)!=".")){i++
}if((i>=sLength-1)||(s.charAt(i)!=".")){return false
}else{return true
}}function isYear(s){if(isEmpty(s)){if(isYear.arguments.length==1){return defaultEmptyOK
}else{return(isYear.arguments[1]==true)
}}if(!isNonnegativeInteger(s)){return false
}return((s.length==2)||(s.length==4))
}function isIntegerInRange(s,a,b){if(isEmpty(s)){if(isIntegerInRange.arguments.length==3){return defaultEmptyOK
}else{return(isIntegerInRange.arguments[3]==true)
}}if(!isInteger(s,false)){return false
}var num=parseInt(s);
return((num>=a)&&(num<=b))
}function isMonth(s){if(isEmpty(s)){if(isMonth.arguments.length==1){return defaultEmptyOK
}else{return(isMonth.arguments[1]==true)
}}return isIntegerInRange(s,1,12)
}function isDay(s){if(isEmpty(s)){if(isDay.arguments.length==1){return defaultEmptyOK
}else{return(isDay.arguments[1]==true)
}}return isIntegerInRange(s,1,31)
}function daysInFebruary(year){return(((year%4==0)&&((!(year%100==0))||(year%400==0)))?29:28)
}function isDate(year,month,day){if(!(isYear(year,false)&&isMonth(month,false)&&isDay(day,false))){return false
}var intYear=parseInt(year);
var intMonth=parseInt(month);
var intDay=parseInt(day);
if(intDay>daysInMonth[intMonth]){return false
}if((intMonth==2)&&(intDay>daysInFebruary(intYear))){return false
}return true
}function prompt(s){window.status=s
}function promptEntry(s){window.status=pEntryPrompt+s
}function warnEmpty(theField,s){theField.focus();
alert(mPrefix+s+mSuffix);
return false
}function warnInvalid(theField,s){theField.focus();
theField.select();
alert(s);
return false
}function checkString(theField,s,emptyOK){if(checkString.arguments.length==2){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}if(isWhitespace(theField.value)){return warnEmpty(theField,s)
}else{return true
}}function checkStateCode(theField,emptyOK){if(checkStateCode.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}else{theField.value=theField.value.toUpperCase();
if(!isStateCode(theField.value,false)){return warnInvalid(theField,iStateCode)
}else{return true
}}}function reformatZIPCode(ZIPString){if(ZIPString.length==5){return ZIPString
}else{return(reformat(ZIPString,"",5,"-",4))
}}function checkZIPCode(theField,emptyOK){if(checkZIPCode.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}else{var normalizedZIP=stripCharsInBag(theField.value,ZIPCodeDelimiters);
if(!isZIPCode(normalizedZIP,false)){return warnInvalid(theField,iZIPCode)
}else{theField.value=reformatZIPCode(normalizedZIP);
return true
}}}function reformatUSPhone(USPhone){return(reformat(USPhone,"(",3,") ",3,"-",4))
}function checkUSPhone(theField,emptyOK){if(checkUSPhone.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}else{var normalizedPhone=stripCharsInBag(theField.value,phoneNumberDelimiters);
if(!isUSPhoneNumber(normalizedPhone,false)){return warnInvalid(theField,iUSPhone)
}else{theField.value=reformatUSPhone(normalizedPhone);
return true
}}}function checkInternationalPhone(theField,emptyOK){if(checkInternationalPhone.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}else{if(!isInternationalPhoneNumber(theField.value,false)){return warnInvalid(theField,iWorldPhone)
}else{return true
}}}function checkEmail(theField,emptyOK){if(checkEmail.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}else{if(!isEmail(theField.value,false)){return warnInvalid(theField,iEmail)
}else{return true
}}}function reformatSSN(SSN){return(reformat(SSN,"",3,"-",2,"-",4))
}function checkSSN(theField,emptyOK){if(checkSSN.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}else{var normalizedSSN=stripCharsInBag(theField.value,SSNDelimiters);
if(!isSSN(normalizedSSN,false)){return warnInvalid(theField,iSSN)
}else{theField.value=reformatSSN(normalizedSSN);
return true
}}}function checkYear(theField,emptyOK){if(checkYear.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}if(!isYear(theField.value,false)){return warnInvalid(theField,iYear)
}else{return true
}}function checkMonth(theField,emptyOK){if(checkMonth.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}if(!isMonth(theField.value,false)){return warnInvalid(theField,iMonth)
}else{return true
}}function checkDay(theField,emptyOK){if(checkDay.arguments.length==1){emptyOK=defaultEmptyOK
}if((emptyOK==true)&&(isEmpty(theField.value))){return true
}if(!isDay(theField.value,false)){return warnInvalid(theField,iDay)
}else{return true
}}function checkDate(yearField,monthField,dayField,labelString,OKtoOmitDay){if(checkDate.arguments.length==4){OKtoOmitDay=false
}if(!isYear(yearField.value)){return warnInvalid(yearField,iYear)
}if(!isMonth(monthField.value)){return warnInvalid(monthField,iMonth)
}if((OKtoOmitDay==true)&&isEmpty(dayField.value)){return true
}else{if(!isDay(dayField.value)){return warnInvalid(dayField,iDay)
}}if(isDate(yearField.value,monthField.value,dayField.value)){return true
}alert(iDatePrefix+labelString+iDateSuffix);
return false
}function getRadioButtonValue(radio){for(var i=0;
i<radio.length;
i++){if(radio[i].checked){break
}}return radio[i].value
}function checkCreditCard(radio,theField){var cardType=getRadioButtonValue(radio);
var normalizedCCN=stripCharsInBag(theField.value,creditCardDelimiters);
if(!isCardMatch(cardType,normalizedCCN)){return warnInvalid(theField,iCreditCardPrefix+cardType+iCreditCardSuffix)
}else{theField.value=normalizedCCN;
return true
}}function isCreditCard(st){if(st.length>19){return(false)
}sum=0;
mul=1;
l=st.length;
for(i=0;
i<l;
i++){digit=st.substring(l-i-1,l-i);
tproduct=parseInt(digit,10)*mul;
if(tproduct>=10){sum+=(tproduct%10)+1
}else{sum+=tproduct
}if(mul==1){mul++
}else{mul--
}}if((sum%10)==0){return(true)
}else{return(false)
}}function isVisa(cc){if(((cc.length==16)||(cc.length==13))&&(cc.substring(0,1)==4)){return isCreditCard(cc)
}return false
}function isMasterCard(cc){firstdig=cc.substring(0,1);
seconddig=cc.substring(1,2);
if((cc.length==16)&&(firstdig==5)&&((seconddig>=1)&&(seconddig<=5))){return isCreditCard(cc)
}return false
}function isAmericanExpress(cc){firstdig=cc.substring(0,1);
seconddig=cc.substring(1,2);
if((cc.length==15)&&(firstdig==3)&&((seconddig==4)||(seconddig==7))){return isCreditCard(cc)
}return false
}function isDinersClub(cc){firstdig=cc.substring(0,1);
seconddig=cc.substring(1,2);
if((cc.length==14)&&(firstdig==3)&&((seconddig==0)||(seconddig==6)||(seconddig==8))){return isCreditCard(cc)
}return false
}function isCarteBlanche(cc){return isDinersClub(cc)
}function isDiscover(cc){first4digs=cc.substring(0,4);
if((cc.length==16)&&(first4digs=="6011")){return isCreditCard(cc)
}return false
}function isEnRoute(cc){first4digs=cc.substring(0,4);
if((cc.length==15)&&((first4digs=="2014")||(first4digs=="2149"))){return isCreditCard(cc)
}return false
}function isJCB(cc){first4digs=cc.substring(0,4);
if((cc.length==16)&&((first4digs=="3088")||(first4digs=="3096")||(first4digs=="3112")||(first4digs=="3158")||(first4digs=="3337")||(first4digs=="3528"))){return isCreditCard(cc)
}return false
}function isAnyCard(cc){if(!isCreditCard(cc)){return false
}if(!isMasterCard(cc)&&!isVisa(cc)&&!isAmericanExpress(cc)&&!isDinersClub(cc)&&!isDiscover(cc)&&!isEnRoute(cc)&&!isJCB(cc)){return false
}return true
}function isCardMatch(cardType,cardNumber){cardType=cardType.toUpperCase();
var doesMatch=true;
if((cardType=="VISA")&&(!isVisa(cardNumber))){doesMatch=false
}if((cardType=="MASTERCARD")&&(!isMasterCard(cardNumber))){doesMatch=false
}if(((cardType=="AMERICANEXPRESS")||(cardType=="AMEX"))&&(!isAmericanExpress(cardNumber))){doesMatch=false
}if((cardType=="DISCOVER")&&(!isDiscover(cardNumber))){doesMatch=false
}if((cardType=="JCB")&&(!isJCB(cardNumber))){doesMatch=false
}if((cardType=="DINERS")&&(!isDinersClub(cardNumber))){doesMatch=false
}if((cardType=="CARTEBLANCHE")&&(!isCarteBlanche(cardNumber))){doesMatch=false
}if((cardType=="ENROUTE")&&(!isEnRoute(cardNumber))){doesMatch=false
}return doesMatch
}function IsCC(st){return isCreditCard(st)
}function IsVisa(cc){return isVisa(cc)
}function IsVISA(cc){return isVisa(cc)
}function IsMasterCard(cc){return isMasterCard(cc)
}function IsMastercard(cc){return isMasterCard(cc)
}function IsMC(cc){return isMasterCard(cc)
}function IsAmericanExpress(cc){return isAmericanExpress(cc)
}function IsAmEx(cc){return isAmericanExpress(cc)
}function IsDinersClub(cc){return isDinersClub(cc)
}function IsDC(cc){return isDinersClub(cc)
}function IsDiners(cc){return isDinersClub(cc)
}function IsCarteBlanche(cc){return isCarteBlanche(cc)
}function IsCB(cc){return isCarteBlanche(cc)
}function IsDiscover(cc){return isDiscover(cc)
}function IsEnRoute(cc){return isEnRoute(cc)
}function IsenRoute(cc){return isEnRoute(cc)
}function IsJCB(cc){return isJCB(cc)
}function IsAnyCard(cc){return isAnyCard(cc)
}function IsCardMatch(cardType,cardNumber){return isCardMatch(cardType,cardNumber)
};var mainViewport;
function initSimpleSearch(wmc){}function gn_anyKeyObserver(e){if(e.keyCode==Event.KEY_RETURN){runSimpleSearch()
}}function runCsvSearch(){var serviceUrl=getGNServiceURL("csv.search");
if($("advanced_search_pnl").visible()){serviceUrl=serviceUrl+"?"+fetchParam("template")
}window.open(serviceUrl,"csv");
metadataselect(0,"remove-all")
}function runPdfSearch(onSelection){if(onSelection){var serviceUrl=getGNServiceURL("pdf.selection.search");
if($("advanced_search_pnl").visible()){serviceUrl=serviceUrl+"?"+fetchParam("template")
}location.replace(serviceUrl);
metadataselect(0,"remove-all")
}else{if(document.cookie.indexOf("search=advanced")!=-1){runAdvancedSearch("pdf")
}else{runSimpleSearch("pdf")
}}}function runSimpleSearch(type){if(type!="pdf"){preparePresent()
}setSort();
var pars="any="+encodeURIComponent($("any").value);
var region=$("region_simple").value;
if(region!=""){pars+="&"+im_mm_getURLselectedbbox();
pars+=fetchParam("relation");
pars+="&attrset=geo";
if(region!="userdefined"){pars+=fetchParam("region")
}}pars+=fetchParam("sortBy");
pars+=fetchParam("sortOrder");
pars+=fetchParam("hitsPerPage");
pars+=fetchParam("output");
if(type=="pdf"){gn_searchpdf(pars)
}else{gn_search(pars)
}}function resetSimpleSearch(){setParam("any","");
setParam("relation","overlaps");
setParam("region_simple",null);
setParam("region",null);
$("northBL").value="90";
$("southBL").value="-90";
$("eastBL").value="180";
$("westBL").value="-180";
resetMinimaps();
setParam("sortBy","relevance");
setParam("sortBy_simple","relevance");
setParam("sortOrder","");
setParam("hitsPerPage","10");
setParam("hitsPerPage_simple","10");
setParam("output","full");
setParam("output_simple","full")
}function resetMinimaps(){GeoNetwork.minimapSimpleSearch.clearExtentBox();
var minimap=GeoNetwork.minimapSimpleSearch.getMap();
if(minimap){var pnl=Ext.getCmp("mini_mappanel_ol_minimap1");
pnl.map.setCenter(pnl.center,pnl.zoom)
}GeoNetwork.minimapAdvancedSearch.clearExtentBox();
minimap=GeoNetwork.minimapAdvancedSearch.getMap();
if(minimap){var pnl=Ext.getCmp("mini_mappanel_ol_minimap2");
pnl.map.setCenter(pnl.center,pnl.zoom)
}}function showAdvancedSearch(){closeSearch("simple_search_pnl");
openSearch("advanced_search_pnl");
document.cookie="search=advanced";
initAdvancedSearch()
}function showSimpleSearch(){closeSearch("advanced_search_pnl");
openSearch("simple_search_pnl");
document.cookie="search=default";
initSimpleSearch()
}function openSearch(s){if(!Prototype.Browser.IE){Effect.BlindDown(s)
}else{$(s).show()
}}function closeSearch(s){if(!Prototype.Browser.IE){Effect.BlindUp($(s))
}else{$(s).hide()
}}function initAdvancedSearch(){new Ajax.Autocompleter("themekey","keywordList","portal.search.keywords?",{paramName:"keyword",updateElement:addQuote});
initCalendar()
}function runAdvancedSearch(type){if(type!="pdf"){preparePresent()
}setSort();
var pars=fetchParam("all");
pars+=fetchParam("phrase");
pars+=fetchParam("or");
pars+=fetchParam("without");
pars+=fetchParam("title");
pars+=fetchParam("abstract");
pars+=fetchParam("themekey");
pars+=fetchRadioParam("similarity");
var region=$("region").value;
if(region!=""){pars+="&attrset=geo";
pars+="&"+im_mm_getURLselectedbbox();
pars+=fetchParam("relation");
if(region!="userdefined"){pars+=fetchParam("region")
}}if($("radfrom1").checked){pars+=fetchParam("dateFrom");
pars+=fetchParam("dateTo")
}if($("radfromext1").checked){pars+=fetchParam("extFrom");
pars+=fetchParam("extTo")
}pars+=fetchParam("group");
pars+=fetchParam("category");
pars+=fetchParam("siteId");
pars+=fetchBoolParam("digital");
pars+=fetchBoolParam("paper");
pars+=fetchBoolParam("dynamic");
pars+=fetchBoolParam("download");
pars+=fetchParam("protocol").toLowerCase();
pars+=fetchParam("template");
pars+=fetchParam("sortBy");
pars+=fetchParam("sortOrder");
pars+=fetchParam("hitsPerPage");
pars+=fetchParam("output");
pars+=fetchParam("inspireannex");
pars+=addINSPIREThemes();
var inspire=$("inspire");
if(inspire){if(inspire.checked){pars+="&inspire=true"
}}if(type=="pdf"){gn_searchpdf(pars)
}else{gn_search(pars)
}}function resetAdvancedSearch(){setParam("all","");
setParam("phrase","");
setParam("or","");
setParam("without","");
setParam("title","");
setParam("abstract","");
setParam("themekey","");
var radioSimil=document.getElementsByName("similarity");
radioSimil[1].checked=true;
setParam("relation","overlaps");
setParam("region",null);
setParam("region_simple",null);
$("northBL").value="90";
$("southBL").value="-90";
$("eastBL").value="180";
$("westBL").value="-180";
resetMinimaps();
setParam("dateFrom","");
setParam("dateTo","");
$("radfrom0").checked=true;
$("radfrom1").disabled="disabled";
setParam("extFrom","");
setParam("extTo","");
$("radfromext1").disabled="disabled";
setParam("group","");
setParam("category","");
setParam("siteId","");
$("digital").checked=false;
$("paper").checked=false;
$("dynamic").checked=false;
$("download").checked=false;
setParam("protocol","");
setParam("template","n");
setParam("sortBy","relevance");
setParam("sortBy_simple","relevance");
setParam("sortOrder","");
setParam("hitsPerPage","10");
setParam("hitsPerPage_simple","10");
setParam("output","full");
setParam("output_simple","full");
resetInspireOptions()
}function showFields(img,div){var img=$(img);
if(img){var src=img.getAttribute("src");
var ndx=src.lastIndexOf("/");
var div=$(div);
src=src.substring(0,ndx+1);
if(div.visible()){img.setAttribute("src",src+"plus.gif")
}else{img.setAttribute("src",src+"minus.png")
}div.toggle()
}}function setSort(){if($("sortBy").value=="title"){$("sortOrder").value="reverse"
}else{$("sortOrder").value=""
}}function setSortAndSearch(){$("sortBy").value=$F("sortBy.live");
setSort();
if(document.cookie.indexOf("search=advanced")!=-1){runAdvancedSearch()
}else{runSimpleSearch()
}}var ratingPopup=null;
function showRatingPopup(id){if(ratingPopup==null){ker.loadURL("rating.popup",ker.wrap(this,function(t){var p=document.createElement("div");
p.className="ratingBox";
p.innerHTML=t.responseText;
p.style.display="none";
p.style.zIndex=32000;
p.setAttribute("id","rating.popup");
$("content").appendChild(p);
ratingPopup=p;
setTimeout(ker.wrap(this,function(){showRatingPopup(id)
}),10)
}));
return
}var pos=Position.positionedOffset($("rating.link."+id));
ratingPopup.style.left=pos[0]-100+"px";
ratingPopup.style.top=pos[1]+16+"px";
ratingPopup.setAttribute("mdid",id);
Element.show(ratingPopup)
}function hideRatingPopup(){var popup=$("rating.popup");
if(popup!=null){Element.hide(popup);
Element.hide("rating.image")
}}function rateMetadata(rating){var id=ratingPopup.getAttribute("mdid");
Element.show("rating.image");
var request="<request>   <id>"+id+"</id>   <rating>"+rating+"</rating></request>";
ker.send("xml.metadata.rate",request,ker.wrap(this,rateMetadata_OK))
}function rateMetadata_OK(xmlRes){if(xmlRes.nodeName=="error"){ker.showError(translate("rateMetadataFailed"),xmlRes)
}else{hideRatingPopup()
}}function doRegionSearchSimple(){doRegionSearch("region_simple");
$("region").value=$("region_simple").value
}function doRegionSearchAdvanced(){doRegionSearch("region");
$("region_simple").value=$("region").value
}function doRegionSearch(regionlist){var region=$(regionlist).value;
if(region==""){region=null;
$("northBL").value="90";
$("southBL").value="-90";
$("eastBL").value="180";
$("westBL").value="-180";
GeoNetwork.minimapSimpleSearch.updateExtentBox();
GeoNetwork.minimapAdvancedSearch.updateExtentBox()
}else{if(region=="userdefined"){}else{getRegion(region)
}}}function getRegion(region){if(region){var pars="id="+region
}var myAjax=new Ajax.Request(getGNServiceURL("xml.region.get"),{method:"get",parameters:pars,onSuccess:getRegion_complete,onFailure:getRegion_error})
}function getRegion_complete(req){var node=req.responseXML;
var northcc=xml.evalXPath(node,"response/record/north");
var southcc=xml.evalXPath(node,"response/record/south");
var eastcc=xml.evalXPath(node,"response/record/east");
var westcc=xml.evalXPath(node,"response/record/west");
$("northBL").value=northcc;
$("southBL").value=southcc;
$("eastBL").value=eastcc;
$("westBL").value=westcc;
GeoNetwork.minimapSimpleSearch.updateExtentBox();
GeoNetwork.minimapAdvancedSearch.updateExtentBox()
}function getRegion_error(){alert(translate("error"))
}function updateAoIFromForm(){var nU=Number($("northBL").value);
var sU=Number($("southBL").value);
var eU=Number($("eastBL").value);
var wU=Number($("westBL").value);
if(nU<sU){alert(translate("northSouth"))
}else{if(nU>90){alert(translate("north90"))
}else{if(sU<-90){alert(translate("south90"))
}else{if(eU<wU){alert(translate("eastWest"))
}else{if(eU>180){alert(translate("east180"))
}else{if(wU<-180){alert(translate("west180"))
}else{im_mm_redrawAoI();
im_mm_zoomToAoI();
$("updateBB").style.visibility="hidden"
}}}}}}}function AoIrefresh(){$("region").value="userdefined";
$("updateBB").style.visibility="visible"
}function im_mm_aoiUpdated(bUpdate){$("region").value="userdefined"
}function runRssSearch(){var myAjax=new Ajax.Request(getGNServiceURL("metadata.latest.updated"),{method:"get",parameters:null,onSuccess:gn_search_rss_complete})
}function gn_search_rss_complete(req){var rlist=$("latest_updates");
rlist.innerHTML=req.responseText
}function preparePresent(){}function gn_search(pars){var myAjax=new Ajax.Request(getGNServiceURL("main.search.embedded"),{method:"get",parameters:pars,onSuccess:gn_search_complete,onFailure:gn_search_error})
}function gn_searchpdf(pars){pars=pars.replace(/hitsPerPage=\d{2,3}/,"hitsPerPage=9999");
location.replace(getGNServiceURL("pdf.search")+"?"+pars)
}function gn_present(frompage,topage){var params="from="+frompage+"&to="+topage;
var url=configOptions.locService+"/main.search.embedded";
var myAjax=new Ajax.Request(url,{method:"get",parameters:params,onSuccess:gn_search_complete,onFailure:gn_search_error})
}function gn_search_complete(req){var rlist=$("search-results-content");
rlist.innerHTML=req.responseText
}function gn_showSingleMetadataUUID(uuid,title){var pars={uuid:uuid,currTab:"simple"};
gn_showSingleMet(pars,title)
}function gn_showSingleMetadata(id,title){gn_showSingleMet({id:id,currTab:"simple"},title)
}function gn_showSingleMet(pars,title){var showEl=$("gn_showmd_"+pars.id);
var loadEl=$("gn_loadmd_"+pars.id);
if(showEl&&loadEl){showEl.hide();
loadEl.show()
}title=title||translate("Metadata");
Ext.MessageBox.show({title:title,progressText:"Loading...",width:300,wait:true,waitConfig:{interval:200}});
if(!metadataWindow){Ext.Ajax.request({url:getGNServiceURL("metadata.show.embedded"),params:pars,method:"GET",success:function(response,request){if(showEl&&loadEl){showEl.show();
loadEl.hide()
}Ext.MessageBox.hide();
if(!metadataWindow){metadataWindow=new Ext.Window({title:title,html:response.responseText,bodyStyle:"overflow-y:auto; width: 90%",constrain:true,maximizable:true,width:600,height:500,onEsc:function(){metadataWindow.close()
},listeners:{close:function(){metadataWindow=null
},show:function(){extentMap.initMapDiv()
}}})
}metadataWindow.show()
},failure:function(){Ext.MessageBox.hide();
Openwis.Utils.MessageBox.displayInternalError();
return -1
}})
}else{metadataWindow.load({url:getGNServiceURL("metadata.show.embedded"),params:pars,method:"GET",callback:function(response,request){if(showEl&&loadEl){showEl.show();
loadEl.hide()
}Ext.MessageBox.hide();
metadataWindow.show();
metadataWindow.setTitle(title)
},failure:function(){Ext.MessageBox.hide();
Openwis.Utils.MessageBox.displayInternalError();
return -1
},discardUrl:false,nocache:false,text:translate("Loading"),timeout:30,scripts:false})
}}function gn_showMetadata(id,title){gn_showMetadataTab(id,"simple",title)
}var metadataWindow;
function gn_showMetadataTab(id,currTab,title){gn_showSingleMet({id:id,currTab:currTab},title)
}function gn_hideMetadata(id){var parent=$("mdwhiteboard_"+id);
var div=parent.firstChild;
Effect.BlindUp(div,{afterFinish:function(obj){clearNode(parent);
$("gn_showmd_"+id).show();
$("gn_hidemd_"+id).hide()
}})
}function a(msg){alert(msg)
}function gn_search_error(req){alert("ERROR "+req.responseText);
$("loadingMD").hide();
return -1
}function gn_filteredSearch(){var params="";
if($("advanced_search_pnl").visible()){params=fetchParam("template")
}var myAjax=new Ajax.Request(getGNServiceURL("selection.search"),{method:"get",parameters:params,onSuccess:gn_search_complete,onFailure:gn_search_error})
}function runCategorySearch(category){preparePresent();
var pars="category="+category;
gn_search(pars)
}function fetchParam(p){var pL=$(p);
if(!pL){return""
}else{var t=pL.value;
if(t){return"&"+p+"="+encodeURIComponent(t)
}else{return""
}}}function fetchBoolParam(p){var pL=$(p);
if(!pL){return""
}else{if(pL.checked){return"&"+p+"=on"
}else{return"&"+p+"=off"
}}}function fetchRadioParam(name){var radio=document.getElementsByName(name);
var value=getCheckedValue(radio);
return"&"+name+"="+value
}function getCheckedValue(radioObj){if(!radioObj){return""
}var radioLength=radioObj.length;
if(radioLength==undefined){if(radioObj.checked){return radioObj.value
}else{return""
}}for(var i=0;
i<radioLength;
i++){if(radioObj[i].checked){return radioObj[i].value
}}return""
}function setParam(p,val){var pL=$(p);
if(pL){pL.value=val
}}var keyordsSelected=false;
function addQuote(li){$("themekey").value='"'+li.innerHTML+'"'
}function popKeyword(el,pop){if(pop.style.display=="block"){pop.style.display="none";
return false
}pop.style.width="250px";
pop.style.display="block";
if(!keyordsSelected){new Ajax.Updater("keywordSelector","portal.search.keywords?mode=selector&keyword="+$("themekey").value);
keyordsSelected=true
}}function keywordCheck(k,check){k='"'+k+'"';
if(check){if($("themekey").value!=""){$("themekey").value+=" or "+k
}else{$("themekey").value=k
}}else{$("themekey").value=$("themekey").value.replace(" or "+k,"");
$("themekey").value=$("themekey").value.replace(k,"");
pos=$("themekey").value.indexOf(" or ");
if(pos==0){$("themekey").value=$("themekey").value.substring(4,$("themekey").value.length)
}}}function setDates(what){var xfrom=$("dateFrom");
var xto=$("dateTo");
var extfrom=$("extFrom");
var extto=$("extTo");
if(what==0){xfrom.value="";
xto.value="";
extfrom.value="";
extto.value="";
return
}today=new Date();
fday=today.getDate();
if(fday.toString().length==1){fday="0"+fday.toString()
}fmonth=today.getMonth()+1;
if(fmonth.toString().length==1){fmonth="0"+fmonth.toString()
}fyear=today.getYear();
if(fyear<1900){fyear=fyear+1900
}var todate=fyear+"-"+fmonth+"-"+fday+"T23:59:59";
var fromdate=(fyear-10)+"-"+fmonth+"-"+fday+"T00:00:00";
xto.value=todate;
xfrom.value=fromdate;
extto.value=todate;
extfrom.value=fromdate
}function check(status){var checks=$("search-results-content").getElementsByTagName("INPUT");
var checksLength=checks.length;
for(var i=0;
i<checksLength;
i++){checks[i].checked=status
}}function metadataselect(id,selected){if(selected===true){selected="add"
}else{if(selected===false){selected="remove"
}}var param="id="+id+"&selected="+selected;
var http=new Ajax.Request(Env.locService+"/metadata.select",{method:"get",parameters:param,onComplete:function(originalRequest){},onLoaded:function(originalRequest){},onSuccess:function(originalRequest){var xmlString=originalRequest.responseText;
var xmlobject=(new DOMParser()).parseFromString(xmlString,"text/xml");
var root=xmlobject.getElementsByTagName("response")[0];
var nbSelected=root.getElementsByTagName("Selected")[0].firstChild.nodeValue;
var item=document.getElementById("nbselected");
item.innerHTML=nbSelected
},onFailure:function(originalRequest){alert(translate("metadataSelectionError"))
}});
if(selected=="remove-all"){check(false)
}if(selected=="add-all"){check(true)
}}function toggleMoreFields(){$("all_search_row").toggle();
$("phrase_search_row").toggle();
$("without_search_row").toggle();
var src=$("i_morefields").getAttribute("src");
var ndx=src.lastIndexOf("/");
src=src.substring(0,ndx+1);
if($("all_search_row").visible()==true){$("i_morefields").setAttribute("src",src+"minus.png")
}else{$("i_morefields").setAttribute("src",src+"plus.gif")
}}function toggleInspire(){$("inspiresearchfields").toggle();
var src=$("i_inspire").getAttribute("src");
var ndx=src.lastIndexOf("/");
src=src.substring(0,ndx+1);
if($("inspiresearchfields").visible()==true){$("i_inspire").setAttribute("src",src+"minus.png")
}else{$("i_inspire").setAttribute("src",src+"plus.gif")
}}function toggleWhen(){$("whensearchfields").toggle();
var src=$("i_when").getAttribute("src");
var ndx=src.lastIndexOf("/");
src=src.substring(0,ndx+1);
if($("whensearchfields").visible()==true){$("i_when").setAttribute("src",src+"minus.png")
}else{$("i_when").setAttribute("src",src+"plus.gif")
}}function addWMSLayer(layers){Ext.getCmp("north-map-panel").expand();
mainViewport.doLayout();
GeoNetwork.mapViewer.addWMSLayer(layers)
}function addSelectedWMSLayers(metadataIdForm){var checkedBoxes=$$("#"+metadataIdForm+" input");
var wmsLayers=new Array();
for(var i=0;
i<checkedBoxes.length;
i++){if(checkedBoxes[i].checked){wmsLayers.push(checkedBoxes[i].value.split(","))
}}addWMSLayer(wmsLayers)
}function gn_showInterList(id){var pars="id="+id+"&currTab=distribution";
$("gn_showinterlist_"+id).hide();
$("gn_loadinterlist_"+id).show();
var myAjax=new Ajax.Request(getGNServiceURL("metadata.show.embedded"),{method:"get",parameters:pars,onSuccess:function(req){var parent=$("ilwhiteboard_"+id);
clearNode(parent);
parent.show();
$("gn_loadinterlist_"+id).hide();
$("gn_hideinterlist_"+id).show();
var div=document.createElement("div");
div.className="metadata_current";
div.style.width="100%";
$(div).hide();
parent.appendChild(div);
div.innerHTML=req.responseText;
Effect.BlindDown(div);
var tipman=new TooltipManager();
ker.loadMan.wait(tipman)
},onFailure:gn_search_error})
}function gn_hideInterList(id){var parent=$("ilwhiteboard_"+id);
var div=parent.firstChild;
Effect.BlindUp(div,{afterFinish:function(obj){clearNode(parent);
$("gn_showinterlist_"+id).show();
$("gn_hideinterlist_"+id).hide()
}})
}function showInspireSearch(){var inspire=$("inspire");
if(inspire.checked){inspire.value="true"
}else{inspire.value=""
}}function inspireAnnexChanged(inspireannex){var inspire=$("inspire");
if(inspireannex!=""){if(inspire){inspire.checked=true
}}else{if(inspire){inspire.checked=false
}}}function inspireOrganisationChanged(groupId){setParam("group",groupId)
}function inspireBrontypeChanged(brontype){setParam("type",brontype)
}function inspireServiceTypeChanged(servicetype){setParam("protocol",servicetype)
}function taggleVisibility(elementId){var element=$(elementId);
if(element!=null){if(element.style.display=="none"){element.style.display="block"
}else{element.style.display="none"
}}else{return
}}function addINSPIREThemes(){var allThemes="";
var prefix="&inspiretheme=";
var inspireThemeChk=$$('#inspirethemesdiv input[type="checkbox"]');
for(i=0;
i<inspireThemeChk.length;
i++){if(inspireThemeChk[i].checked){allThemes+=prefix+inspireThemeChk[i].value+"*"
}}return allThemes
}function resetInspireOptions(){if(!$("inspire")){return
}$("inspire").checked=false;
setParam("title","");
setParam("inspireannex","");
setParam("inspirebrontype","");
setParam("protocol","");
setParam("orgselect_inspire","");
$("inspire_GeographicalNames").checked=false;
$("inspire_AdministrativeUnits").checked=false;
$("inspire_Addresses").checked=false;
$("inspire_CadastralParcels").checked=false;
$("inspire_TransportNetworks").checked=false;
$("inspire_Hydrography").checked=false;
$("inspire_ProtectedSites").checked=false;
$("inspire_Elevation").checked=false;
$("inspire_LandCover").checked=false;
$("inspire_Orthoimagery").checked=false;
$("inspire_Geology").checked=false;
$("inspire_StatisticalUnits").checked=false;
$("inspire_Buildings").checked=false;
$("inspire_Soil").checked=false;
$("inspire_LandUse").checked=false;
$("inspire_HumanHealthAndSafety").checked=false;
$("inspire_UtilityAndGovernmentServices").checked=false;
$("inspire_EnvironmentalMonitoringFacilities").checked=false;
$("inspire_ProductionAndIndustrialFacilities").checked=false;
$("inspire_AgriculturalAndAquacultureFacilities").checked=false;
$("inspire_PopulationDistribution-Demography").checked=false;
$("inspire_AreaManagementRestrictionRegulationZonesAndReportingUnits").checked=false;
$("inspire_NaturalRiskZones").checked=false;
$("inspire_AtmosphericConditions").checked=false;
$("inspire_MeteorologicalGeographicalFeatures").checked=false;
$("inspire_OceanographicGeographicalFeatures").checked=false;
$("inspire_SeaRegions").checked=false;
$("inspire_Bio-geographicalRegions").checked=false;
$("inspire_HabitatsAndBiotopes").checked=false;
$("inspire_SpeciesDistribution").checked=false;
$("inspire_EnergyResources").checked=false;
$("inspire_MineralResources").checked=false;
$("inspire_MineralResources").checked=false;
$("inspire_MineralResources").checked=false
}function clearNode(node){var enode=$(node);
while(enode.firstChild){enode.removeChild(enode.firstChild)
}}function im_mm_getURLselectedbbox(){return"geometry=POLYGON(( "+$("westBL").value+" "+$("northBL").value+", "+$("eastBL").value+" "+$("northBL").value+", "+$("eastBL").value+" "+$("southBL").value+", "+$("westBL").value+" "+$("southBL").value+", "+$("westBL").value+" "+$("northBL").value+"))"
};