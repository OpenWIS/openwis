Ext.ns('geonet');

geonet.Constants ={
    DIALOG_MAIN_DIV: 'md_dialog_tb',
    TAB_MENU: 'md_dialog_tab_menu',
    VIEW_SIMPLE: 'simple',
    VIEW_ISO_MIN: 'ISOMinimum',
    VIEW_ISO_CORE: 'ISOCore',
    VIEW_ISO_ALL: 'ISOAll',
    VIEW_METADATA: 'metadata',
    VIEW_IDENTIFICATION: 'identification',
    VIEW_MAINTENANCE: 'maintenance',
    VIEW_CONSTRAINTS: 'constraints',
    VIEW_SPATIAL: 'spatial',
    VIEW_REFSYS: 'refSys',
    VIEW_DISTRIBUTION: 'distribution',
    VIEW_DATAQUALITY: 'dataQuality',
    VIEW_APPSCHEMA: 'appSchInfo',
    VIEW_PORCAT: 'porCatInfo',
    VIEW_CONTENTINFO: 'contentInfo',
    VIEW_EXTINFO: 'extensionInfo',
    VIEW_XML: 'xml',
    STATE_VIEW: 'show',
    STATE_EDIT: 'edit',
    DIALOG_CONTENT: 'md_dialog_content',
    BTN_DISPLAY_TOGGLE: 'md_dialog_edit_btn',
    BTN_SAVE: 'md_dialog_save_btn',
    BTN_REVERT: 'md_dialog_revert_btn',
    BTN_PREFIX: 'btn_',
    ACTION_UPDATE: 'metadata.update',
    ACTION_REVERT: 'metadata.update.forgetandfinish',
    ACTION_EMBEDDED_UPDATE: 'metadata.update.embedded'
};

geonet.MetadataDialog = {

    errorTemplate: new Ext.Template(
    '<div id="error" style="margin:10px">',
        '<h2>Error loading page</h2>',
        '<ul>',
        '<li>Metadata id: {id}</li>',
        '<li>Failing service: {service}</li>',
        '<li>Error code: {code}</li>',
        '<li>Error Message: {msg}</li>',
        '</ul>',
    '</div>'
    ),
    window: null,
    idParams:null,
    view: geonet.Constants.VIEW_SIMPLE,
    state: geonet.Constants.STATE_VIEW,
    downloadEditScripts: true,

    loadWithId: function(id,title,tab,edit,editable) {
        geonet.MetadataDialog.prepareLoad(title,tab,{id:id},edit,editable);
    },
    loadWithUUID: function(uuid,title,tab,edit,editable) {
        geonet.MetadataDialog.prepareLoad(title,tab,{uuid:uuid},edit,editable);
    },
    changeView: function(viewId, skipLoad, button) {
        Ext.getCmp(geonet.Constants.BTN_PREFIX+geonet.MetadataDialog.view).enable();

        button = button || Ext.getCmp(geonet.Constants.BTN_PREFIX+viewId).disable();
        button.disable();

        var dialog = geonet.MetadataDialog;

        dialog.view = viewId;
        var menu = Ext.getCmp(geonet.Constants.TAB_MENU);
        menu.setText(dialog.viewButtonName());
        if(!skipLoad) {
            if(dialog.state === geonet.Constants.STATE_EDIT) {
                var formParams = $('editForm').serialize(true);
                var otherParams = {method:'POST'};

                var params = dialog.requestParams(geonet.Constants.ACTION_EMBEDDED_UPDATE, formParams,otherParams);
                dialog.customLoad(params);
            } else {
                dialog.load();
            }
        }
    },
    prepareLoad: function(title,tab,id,edit,editable) {
        var self = geonet.MetadataDialog;

        if(self.window === null) {
            self.window = new Ext.Window({
                title: Ext.util.Format.htmlEncode(title),
                items: {
                    html: "<div></div>",
                    id: geonet.Constants.DIALOG_CONTENT,
                    bodyStyle: 'overflow-y:auto; width: 90%',
                    tbar: new Ext.Toolbar({
                        id: geonet.Constants.DIALOG_MAIN_DIV,
                        items: [{
                            xtype: 'tbbutton',
                            id: geonet.Constants.BTN_DISPLAY_TOGGLE,
                            text: Openwis.i18n('Common.Btn.Edit'),
                            disabled: true,
                            handler: function(btn) { self.toggleState(btn); }
                        },{
                            xtype: 'tbbutton',
                            id: geonet.Constants.BTN_SAVE,
                            text: Openwis.i18n('Common.Btn.Save'),
                            hidden: true,
                            handler: self.save
                        },{
                            xtype: 'tbbutton',
                            id: geonet.Constants.BTN_REVERT,
                            text: Openwis.i18n('Common.Btn.Revert'),
                            hidden: true,
                            handler: self.revert
                        },{
                            xtype: 'tbfill'
                        },{
                            xtype: 'tbbutton',
                            id: geonet.Constants.TAB_MENU,
                            text: self.viewButtonName(),
                            menu: [
                                self.viewButton(geonet.Constants.VIEW_SIMPLE, true),
                                '-',
                                self.viewButton(geonet.Constants.VIEW_ISO_MIN),
                                self.viewButton(geonet.Constants.VIEW_ISO_CORE),
                                self.viewButton(geonet.Constants.VIEW_ISO_ALL),
                                '-',
                                self.viewButton(geonet.Constants.VIEW_METADATA),
                                self.viewButton(geonet.Constants.VIEW_IDENTIFICATION),
                                self.viewButton(geonet.Constants.VIEW_MAINTENANCE),
                                self.viewButton(geonet.Constants.VIEW_CONSTRAINTS),
                                self.viewButton(geonet.Constants.VIEW_SPATIAL),
                                self.viewButton(geonet.Constants.VIEW_REFSYS),
                                self.viewButton(geonet.Constants.VIEW_DISTRIBUTION),
                                self.viewButton(geonet.Constants.VIEW_DATAQUALITY),
                                self.viewButton(geonet.Constants.VIEW_APPSCHEMA),
                                self.viewButton(geonet.Constants.VIEW_PORCAT),
                                self.viewButton(geonet.Constants.VIEW_CONTENTINFO),
                                self.viewButton(geonet.Constants.VIEW_EXTINFO),
                                '-',
                                self.viewButton(geonet.Constants.VIEW_XML)
                            ],
                            listeners: {
                                itemclick: function(baseItem, e) {
                                    alert(e);
                                }
                            }
                        }]
                    })
                },
                layout: 'fit',
                constrain: true,
                maximizable: true,
                width: 875,
                height: 500,
                onEsc: function(){
                    self.window.close();
                },
                listeners: {
                    beforeclose: function() {
                        var close = true;
            			if(self.state === geonet.Constants.STATE_EDIT) {
                            close = false;
                            self.state = 'quitting';
                            self.window.disable();
                            self.endEdit(function(){
                            setTimeout(function(){self.window.close();},100);
                            }, function(btn){
                                if(btn === 'cancel') {
                                    self.window.enable();
                                    self.state = geonet.Constants.STATE_EDIT;
                                }
                            });
                        }
                        return close;
                    },
                    close: function() {
                        self.window = null;
                        self.state = geonet.Constants.STATE_VIEW;
                    }
                }
            });
            self.window.show();
            self.window.center();
            self.idParams=id;
	        if(edit) {
                self.toggleState(undefined);
            } else {
	            self.load(id);
            }
	        if (!editable) {
	        	var toggleButton = Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
	        	toggleButton.setVisible(false);
	        }
        } else {
	    var oldTitle =  self.window.title;
            self.window.setTitle(title || Openwis.i18n('Metadata.Viewer.Title'));
            if(!self.window.isVisible()) {
                self.window.show();
            }
            var toggleButton = Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
            self.idParams=id;
            if(self.state === geonet.Constants.STATE_VIEW) {
                if(edit) {
                    self.toggleState(toggleButton);
                } else {
                    self.load(id);
                }
            } else {
		        if(edit) {
                    self.load(id);
                } else {
                    self.toggleState(toggleButton, function(btn){
                        if(btn === 'cancel') {
                            self.window.setTitle(oldTitle);
                        }
                    });
                }
	        }
        }
    },
    requestParams: function(service, formParams, attributes) {
        var dialog = geonet.MetadataDialog;
        service = service || 'metadata.'+dialog.state+'.embedded';

        var params = {};

        Ext.apply(params,dialog.idParams);
        Ext.apply(params,formParams);
        params.currTab = dialog.view;
        params.download_scripts = dialog.downloadEditScripts;

        var finalAttributes = {};
        if(attributes !== undefined && attributes !== null) {
            finalAttributes = Ext.apply(finalAttributes, attributes);
        }
        Ext.applyIf(finalAttributes, {
            service: service,
            url: getGNServiceURL(service),
            method: 'GET',
            params: params,
            disableCaching:false,
            text: Openwis.i18n('Common.Loading.Message'),
            timeout: 30000,
            scripts: dialog.state === geonet.Constants.STATE_EDIT && dialog.downloadEditScripts
        });

        if(finalAttributes.method.toUpperCase() === 'POST') {
            finalAttributes.headers = {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'};
        }

        return finalAttributes;
    },
    load: function(id) {
        var dialog = geonet.MetadataDialog;
        if(id) {
            dialog.idParams=id;
        }
        var params = Ext.apply(dialog.requestParams(),id);
        dialog.customLoad(params);
    },
    customLoad: function(request) {

        var self = geonet.MetadataDialog;
        var content = Ext.getCmp(geonet.Constants.DIALOG_CONTENT);
        
        var toggleButton = Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
        var saveButton = Ext.getCmp(geonet.Constants.BTN_SAVE);
        var revertButton = Ext.getCmp(geonet.Constants.BTN_REVERT);

        toggleButton.disable();
        saveButton.disable();
        revertButton.disable();
        
        content.getUpdater().abort();
        var finalRequest = Ext.apply({
            callback: function(el,success,response,options){
                if(success) {
                    // Init maps contained in search results
                    extentMap.initMapDiv();
                    if(self.state === geonet.Constants.STATE_EDIT) {
                        self.downloadEditScripts = false;
                        initCalendar(content.el.dom);
                        validateMetadataFields();
                    }
                    toggleButton.enable();
                    saveButton.enable();
                    revertButton.enable();
                } else {
                    var id = self.idParams.id ? self.idParams.id : self.idParams.uuid;
                    self.errorTemplate.overwrite(el,{code:response.status, id:id, msg: response.statusText, service:options.service});
                }
            }
        },request);

        content.load(finalRequest);
    },
    action: function(action,view) {
        var dialog = geonet.MetadataDialog;
        if(view) {
            if(dialog.view === view && action === geonet.Constants.ACTION_UPDATE) {return;}
            dialog.changeView(view,true);
        }

        var formParams = $('editForm').serialize(true);
        var otherParams = {method:'POST'};

        dialog.customLoad(dialog.requestParams(action+".embedded", formParams, otherParams));
    },
    viewButton: function(viewId, selected) {
        var self = geonet.MetadataDialog;
        return {
            id: geonet.Constants.BTN_PREFIX+viewId,
            disabled: selected,
            text: self.viewName(viewId),
            handler: function(btn) {
                self.changeView(viewId,false,btn);
            }
        };
    },
    viewName: function(viewId) {
        viewId = viewId || geonet.MetadataDialog.view;
        return Openwis.i18n('Metadata.ViewerEditor.View.'+viewId);
    },
    viewButtonName: function() {
        return 'View - '+geonet.MetadataDialog.viewName();
    },
    /**
     * @param endEditBtnFn if in edit mode this method will be called with the button that was pressed
     */
    toggleState: function(btn,endEditBtnFn){
        if(btn === undefined) {
            btn = Ext.getCmp(geonet.Constants.BTN_DISPLAY_TOGGLE);
        }

        btn.disable();
        var setButtonVisibility = function(visible) {
            Ext.getCmp(geonet.Constants.BTN_SAVE).setVisible(visible);
            Ext.getCmp(geonet.Constants.BTN_REVERT).setVisible(visible);
        };

        var dialog = geonet.MetadataDialog;

        if(geonet.MetadataDialog.state === geonet.Constants.STATE_VIEW) {
            dialog.state = geonet.Constants.STATE_EDIT;
            btn.setText(Openwis.i18n('Common.Btn.View'));
            setButtonVisibility(true);
            geonet.MetadataDialog.load();
        } else {
            dialog.endEdit(function(){
                dialog.state = geonet.Constants.STATE_VIEW;
                btn.setText(Openwis.i18n('Common.Btn.Edit'));
                setButtonVisibility(false);
                dialog.load();
            },endEditBtnFn);
        }
    },
    endEdit: function(endFunction, postAnswerFn) {
	    postAnswerFn = postAnswerFn || function(){};
        var dialog = geonet.MetadataDialog;
        Ext.Msg.show({
                title: Openwis.i18n('Metadata.ViewerEditor.View.endEdit.title'),
                msg: Openwis.i18n('Metadata.ViewerEditor.View.confirmEndEdit'),
                buttons: Ext.Msg.YESNOCANCEL,
                fn: function(btn) {
		                postAnswerFn(btn);
                        if (btn === 'yes'){
                        	if ($('editForm') == null)
                    		{
                        		dialog.editAction(geonet.Constants.ACTION_REVERT,{version: dialog.getVersion()},endFunction);
                    		}
                        	else
                    		{
                        		var formParams = $('editForm').serialize(true);
    		                    dialog.editAction(geonet.Constants.ACTION_EMBEDDED_UPDATE,formParams,endFunction);
                    		}
                            
                        } else if (btn === 'no') {
			                dialog.editAction(geonet.Constants.ACTION_REVERT,{version: dialog.getVersion()},endFunction);
                        }
                },
               animEl: 'elId',
               icon: Ext.MessageBox.QUESTION
            });
    },
    save: function() {
        var dialog = geonet.MetadataDialog;
        var formParams = $('editForm').serialize(true);
        var otherParams = {method:'POST',text:Openwis.i18n("Metadata.ViewerEditor.View.Saving")};

        dialog.customLoad(dialog.requestParams(geonet.Constants.ACTION_EMBEDDED_UPDATE, formParams,otherParams));
    },
    getVersion: function() {
        var content = Ext.get(geonet.Constants.DIALOG_CONTENT);
        var versionInput = content.query('input[@name=version]');
        if(versionInput.length >= 1) {
            return versionInput[0].getValue();
        } else {
            return -1;
        }
    },
    revert:function() {
        var dialog = geonet.MetadataDialog;
        if(confirm(Openwis.i18n("Metadata.ViewerEditor.View.confirmRevert"))) {
            dialog.editAction(geonet.Constants.ACTION_REVERT,{version: dialog.getVersion()}, dialog.load);
        }
    },
    editAction: function(service, postData, endFunction) {
        var dialog = geonet.MetadataDialog;
        var content = Ext.get(geonet.Constants.DIALOG_CONTENT);

        var params = dialog.requestParams(service,postData, {method:'POST'});
        var mask = new Ext.LoadMask(content,{removeMask:true});

        mask.show();

        content.getUpdater().abort();
        params = Ext.apply(params, {
            callback:function(options,success,response){
                mask.hide();
                if(success) {
                    // HACK: need to download all response or geonetwork breaks.  Correct implementation is to create a new save
                    //       service with no return data(other than version perhaps
                    endFunction();
                } else {
                    alert("failed to save data: "+ response.statusText);
                }
            }
        });
        Ext.Ajax.request(params);
    }

};

Ext.ns("metadata.edit.embedded");
metadata.edit.embedded.doTabAction = geonet.MetadataDialog.action;
metadata.edit.embedded.doAction = geonet.MetadataDialog.action;
