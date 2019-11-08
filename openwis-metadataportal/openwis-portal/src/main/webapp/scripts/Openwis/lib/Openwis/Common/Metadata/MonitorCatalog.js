Ext.ns('Openwis.Common.Metadata');

Openwis.Common.Metadata.MonitorCatalog = Ext.extend(Ext.Container, {
    
    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Common.Metadata.MonitorCatalog.superclass.initComponent.apply(this, arguments);
        
        this.initialize();
    },
    
    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        // create search text form
        this.add(this.getSearchFormPanel());
        
        //Create metadata grid.
        this.add(this.getMetadataGrid());
    },
    
    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Metadata.CatalogContent.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
    
    //-- Grid and Store.
    
    getMetadataGrid: function() {
        
        // build an array of columns..
    
        if(!this.metadataGrid) {
        
            /* Build columns table for metadata grid. */
            var columns = [];
            columns.push(new Ext.grid.Column({id:'urn', header:Openwis.i18n('Metadata.CatalogContent.Header.MetadataIdentifier'), dataIndex:'urn', sortable: true, hideable:false, renderer: Openwis.Common.Request.Utils.htmlSafeRenderer}));
            columns.push(new Ext.grid.Column({id:'title', header:Openwis.i18n('Metadata.CatalogContent.Header.MetadataTitle'), dataIndex:'title', sortable: true, renderer: Openwis.Common.Request.Utils.htmlSafeRenderer}));
            columns.push(new Ext.grid.Column({id:'category', header:Openwis.i18n('Metadata.CatalogContent.Header.MetadataCategory'), dataIndex:'category', sortable: true}));
            
            if(this.isAdmin) {
                columns.push(new Ext.grid.Column({id:'originator', header:Openwis.i18n('Metadata.CatalogContent.Header.Originator'), dataIndex:'originator', sortable: true, width: 60, renderer: Openwis.Common.Request.Utils.htmlSafeRenderer}));
                columns.push(new Ext.grid.Column({id:'process', header:Openwis.i18n('Metadata.CatalogContent.Header.Process'), dataIndex:'process', sortable:true, width: 50}));
            }
            
            columns.push(new Ext.grid.Column({id:'gtsCategory', header:Openwis.i18n('Metadata.CatalogContent.Header.GTSCategory'), dataIndex:'gtsCategory', sortable: true, width: 80, renderer: this.renderGtsCategory}));
            columns.push(new Ext.grid.Column({id:'fncPattern', header:Openwis.i18n('Metadata.CatalogContent.Header.FNCPattern'), dataIndex:'fncPattern', sortable: false, width: 70, renderer: this.renderFncPattern}));
            columns.push(new Ext.grid.Column({id:'priority', header:Openwis.i18n('Metadata.CatalogContent.Header.Priority'), tooltip:'GTS Priority', dataIndex:'priority', sortable: false, width: 45, renderer: this.renderPriority}));
            columns.push(new Ext.grid.Column({id:'dataPolicy', header:Openwis.i18n('Metadata.CatalogContent.Header.DataPolicy'), dataIndex:'dataPolicy', sortable: false, width: 70, renderer: this.renderDataPolicy}));
            columns.push(new Ext.grid.Column({id:'localDataSource', header:Openwis.i18n('Metadata.CatalogContent.Header.LocalDataSource'), dataIndex:'localDataSource', sortable: true}));
            
            if(this.isAdmin) {
                columns.push(new Ext.grid.Column({id:'ingested', header:Openwis.i18n('Metadata.CatalogContent.Header.Ingested'), dataIndex:'ingested', sortable: true, width: 40, hidden:true, renderer: this.renderBoolean}));
                columns.push(new Ext.grid.Column({id:'fed', header:Openwis.i18n('Metadata.CatalogContent.Header.Fed'), dataIndex:'fed', sortable: true, width: 40, hidden:true, renderer: this.renderBoolean}));
                columns.push(new Ext.grid.Column({id:'fileExtension', header:Openwis.i18n('Metadata.CatalogContent.Header.FileExtension'), dataIndex:'fileExtension', sortable: false, width: 40, hidden:true, renderer: this.renderFileExtension}));
            }
            
            this.metadataGrid = new Ext.grid.GridPanel({
				id: 'metadataGrid',
                height: 400,
                border: true,
                store: this.getMetadataStore(),
                loadMask: true,
                viewConfig: {
                    forceFit: true
                },
                columns: columns,
                listeners: { 
                    afterrender: function (grid) {
                       grid.loadMask.show();
                       grid.getStore().setBaseParam("myMetadataOnly", !this.isAdmin);
                       grid.getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    },
                    scope:this
                },
                sm: new Ext.grid.RowSelectionModel({
                    listeners: {
                        rowselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getDuplicateMetadataAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getEditMetadataAction().setDisabled(sm.getCount() != 1 || record.get('process') != 'LOCAL');
                            if (sm.grid.ownerCt.isAdmin) {
                                sm.grid.ownerCt.getEditMetaInfoAction().setDisabled(sm.getCount() <= 0);
                                sm.grid.ownerCt.getEditCategoryAction().setDisabled(sm.getCount() <= 0);
                            }
                            sm.grid.ownerCt.getRemoveMetadataAction().setDisabled(sm.getCount() <= 0);
                            sm.grid.ownerCt.getAsXmlAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getAsWmoCoreProfileAction().setDisabled(sm.getCount() != 1);
                            if (sm.getCount() == 1 && sm.getSelected().get('schema') == 'iso19139')
                        	{
                            	sm.grid.ownerCt.getAsWmoCoreProfileAction().setDisabled(true);
                        	}
                            sm.grid.ownerCt.getAsMefAction().setDisabled(sm.getCount() <= 0);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getDuplicateMetadataAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getViewMetadataAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getEditMetadataAction().setDisabled(sm.getCount() != 1 || record.get('process') != 'LOCAL');
                            if (sm.grid.ownerCt.isAdmin) {
                                sm.grid.ownerCt.getEditMetaInfoAction().setDisabled(sm.getCount() <= 0);
                                sm.grid.ownerCt.getEditCategoryAction().setDisabled(sm.getCount() <= 0);
                            }
                            sm.grid.ownerCt.getRemoveMetadataAction().setDisabled(sm.getCount() <= 0);
                            sm.grid.ownerCt.getAsXmlAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getAsWmoCoreProfileAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getAsMefAction().setDisabled(sm.getCount() <= 0);
                        }
                    }
                }),
                // paging bar on the bottom
                bbar: this.getPagingToolbar()
            });
            this.metadataGrid.addButton(new Ext.Button(this.getDuplicateMetadataAction()));
            this.metadataGrid.addButton(new Ext.Button(this.getViewMetadataAction()));
            this.metadataGrid.addButton(new Ext.Button(this.getEditMetadataAction()));
            if (this.isAdmin) {
                this.metadataGrid.addButton(new Ext.Button(this.getEditMetaInfoAction()));
                this.metadataGrid.addButton(new Ext.Button(this.getEditCategoryAction()));
            }
            this.metadataGrid.addButton(new Ext.Button(this.getRemoveMetadataAction()));
            this.metadataGrid.addButton(new Ext.Button(this.getExportMetadataMenuButton()));
        }
        return this.metadataGrid;
    },
    
    // PagingToolbar
    getPagingToolbar: function() {
	    if (!this.pagingToolbar) {
	    	this.pagingToolbar = new Ext.PagingToolbar({
		        pageSize: Openwis.Conf.PAGE_SIZE,
		        store: this.getMetadataStore(),
		        displayInfo: true,
		        beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
		        firstText: Openwis.i18n('Common.Grid.FirstText'),
		        lastText: Openwis.i18n('Common.Grid.LastText'),
		        nextText: Openwis.i18n('Common.Grid.NextText'),
		        prevText: Openwis.i18n('Common.Grid.PrevText'),
		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
		        displayMsg: Openwis.i18n('Metadata.CatalogContent.Display.Range'),
		        emptyMsg: Openwis.i18n('Metadata.CatalogContent.No.Metadata')
		    });
	    }
	    return this.pagingToolbar;
    },
    
    // Metadata store
    getMetadataStore: function() {
        if (!this.metadataStore) {
            this.metadataStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.metadata.all',
                remoteSort: true,
                // reader configs
                root: 'metadatas',
                totalProperty: 'count',
                idProperty: 'urn',
                fields: [
                 	{name: 'id'},
                    {name: 'urn', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'title', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'category', mapping: 'category.name', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'originator'},
                    {name: 'process'},
                    {name: 'gtsCategory'},
                    {name: 'fncPattern'},
                    {name: 'priority'},
                    {name: 'dataPolicy', mapping:'dataPolicy.name'},
                    {name: 'localDataSource'},
                    {name: 'ingested'},
                    {name: 'fed'},
                    {name: 'fileExtension'},
                    {name: 'overridenGtsCategory'},   // in order to display them when exist.
                    {name: 'overridenFncPattern'}, 
                    {name: 'overridenPriority'},
                    {name: 'overridenDataPolicy'},
                    {name: 'overridenFileExtension'},
                    {name: 'schema'}
                ],
                sortInfo: {
                    field: 'urn',
                    direction: 'ASC'
                }
            });
        }
        return this.metadataStore;
    }, 
    
    /********************************************/
    /*  Actions available on selected metadata  */
    
    // Duplicate metadata from selected one
    getDuplicateMetadataAction: function() {
        if(!this.duplicateAction) {
            this.duplicateAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Metadata.MetaInfo.Btn.Duplicate'),
                tooltip: 'Create a new metadata from the selected one.',
                scope: this,
                handler: function() {
                    var selectedRec = this.getMetadataGrid().getSelectionModel().getSelected();
                    Ext.MessageBox.prompt('Metadata URN', 'Please enter a valid URN :', this.handleCreation, this);
                }
            });
        }
        return this.duplicateAction;
    }, 
    
    // View selected metadata
    getViewMetadataAction: function() {
        if(!this.viewAction) {
            this.viewAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.View'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getMetadataGrid().getSelectionModel().getSelected();
                    var editable = selectedRec.get('process') == 'LOCAL';
                    doShowMetadataByUrn(selectedRec.get('urn'), selectedRec.get('title'), editable);
                }
            });
        }
        return this.viewAction;
    }, 
    
    // Edit selected metadata
    getEditMetadataAction: function() {
        if(!this.editMetadataAction) {
            this.editMetadataAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Edit'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getMetadataGrid().getSelectionModel().getSelected();
                    doEditMetadataByUrn(selectedRec.get('urn'), selectedRec.get('title'));
                }
            });
        }
        return this.editMetadataAction;
    },
    
    // Edit the meta Info for selected metadata(s)
    getEditMetaInfoAction: function() {
        if(!this.editMetaInfoAction) {
            this.editMetaInfoAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Metadata.MetaInfo.Btn.EditMetaInfo'),
                scope: this,
                handler: function() {
                    var selections = this.getMetadataGrid().getSelectionModel().getSelections();
                    var urns = [];
                    Ext.each(selections, function(item, index, allItems) {
                        urns.push(item.get('urn'));
                    }, this);
                    new Openwis.Admin.MetaInfo.Manage({
                        multiple: urns.length > 1,
                        metadataURNs : urns,
                        listeners: {
                            metaInfoSaved: function() {
                                this.getMetadataGrid().getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.editMetaInfoAction;
    },
    
    // Edit the metadata category
    getEditCategoryAction: function() {
        if(!this.editCategoryAction) {
            this.editCategoryAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Metadata.MetaInfo.Btn.EditCategory'),
                scope: this,
                handler: function() {
                    var selections = this.getMetadataGrid().getSelectionModel().getSelections();
                    var categoryName = '';
                    var urns = [];
                    Ext.each(selections, function(item, index, allItems) {
                        urns.push(item.get('urn'));
                        categoryName = item.get('category');
                    }, this);
                    if (urns.length > 1) {
			        	categoryName = '';
			        }
			        
                    new Openwis.Admin.Category.Edit({
                        multiple : urns.length > 1,
                        metadataURNs : urns,
                        categoryName : categoryName,
                        listeners: {
                            editCategorySaved: function() {
                                //this.getMetadataGrid().getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                                Openwis.Utils.MessageBox.displaySuccessMsg("Update category was successful.", this.fireSuccessEvent, this);
                                this.getPagingToolbar().doRefresh();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.editCategoryAction;
    },
    
    // Remove selected metadata(s)
    getRemoveMetadataAction: function() {
        if(!this.removeAction) {
            this.removeAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Remove'),
                scope: this,
                handler: function() {
                    var selections = this.getMetadataGrid().getSelectionModel().getSelections();
                    var params = [];
                    Ext.each(selections, function(item, index, allItems) {
                        params.push(item.get('urn'));
                    }, this);
                    var msg = "Metadata with the followings URNs will be removed : "+params+"<br> Do you confirm the action?";
                    //Invoke the remove handler to remove the elements by an ajax request.
                    var removeHandler = new Openwis.Handler.Remove({
                        url: configOptions.locService+ '/xml.metadata.remove',
                        params: params,
                        confirmMsg: msg,
                        listeners: {
                            success: function() {
                                this.getMetadataGrid().getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                            },
                            failure: function() {
                                this.getMetadataGrid().getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                            },
                            scope: this
                        }
                    });
                    removeHandler.proceed();
                }
            });
        }
        return this.removeAction;
    },
    
    // Export selected metadata(s)
    getExportMetadataMenuButton: function() {
        if(!this.exportMetadataMenuButton) {
            this.exportMetadataMenuButton = new Ext.Button({
                text: Openwis.i18n('Common.Btn.Export'),
                menu: new Ext.menu.Menu({
                    items: [
                        this.getAsXmlAction(),
                        this.getAsWmoCoreProfileAction(),
                        this.getAsMefAction()
                    ]
                })
            });
        }
        return this.exportMetadataMenuButton;
    },

    getAsXmlAction: function() {
        if(!this.asXmlAction) {
            this.asXmlAction = new Ext.menu.Item({
            	disabled: true,
                text: Openwis.i18n('Metadata.MetaInfo.Item.AsXml'),
                scope: this,
                handler: function() {
                    if (this.getMetadataGrid().getSelectionModel().getCount() == 1 ) {
                        var selectedRec = this.getMetadataGrid().getSelectionModel().getSelected();
                        var schema = selectedRec.get('schema');
                        var id = selectedRec.get('id');
                        if (schema == 'dublin-core')
                    	{
                        	window.open(configOptions.locService +  "/dc.xml?id=" + id, '_blank', '');
                    	} else if (schema == 'fgdc-std')
                    	{
                    		window.open(configOptions.locService +  "/fgdc.xml?id=" + id, '_blank', '');
                    	} else if (schema == 'iso19115')
                    	{
                    		window.open(configOptions.locService +  "/iso19115to19139.xml?id=" + id, '_blank', '');
                    	} else if (schema == 'iso19139')
                    	{
                    		window.open(configOptions.locService +  "/iso19139.xml?id=" + id, '_blank', '');
                    	} else if (schema == 'iso19110')
                    	{
                    		window.open(configOptions.locService +  "/iso19110.xml?id=" + id, '_blank', '');
                    	}
                    	else
                		{
                    		Ext.Msg.show({
        					    title: Openwis.i18n('Metadata.MetaInfo.NoSchema.WarnDlg.Title'),
    	    				    msg: Openwis.i18n('Metadata.MetaInfo.NoSchema.WarnDlg.Msg'),
    		                    buttons: Ext.MessageBox.OK,
    		                    icon: Ext.MessageBox.WARNING
    		               });
                		}
                    }
                }
            });
        }
        return this.asXmlAction;
    },

    getAsWmoCoreProfileAction: function() {
        if(!this.asWmoCoreProfileAction) {
            this.asWmoCoreProfileAction = new Ext.menu.Item({
            	disabled: true,
                text: Openwis.i18n('Metadata.MetaInfo.Item.AsWmoCoreProfile'),
                scope: this,
                handler: function() {
                    if (this.getMetadataGrid().getSelectionModel().getCount() == 1 ) {
                        var selectedRec = this.getMetadataGrid().getSelectionModel().getSelected();
                        var schema = selectedRec.get('schema');
                        var id = selectedRec.get('id');
                    	window.open(configOptions.locService +  "/" + schema + "_to_coreProfile.xml?id=" + id, '_blank', '');
                    }
                }
            });
        }
        return this.asWmoCoreProfileAction;
    },

    getAsMefAction: function() {
        if(!this.asMefAction) {
            this.asMefAction = new Ext.menu.Item({
            	disabled: true,
                text: Openwis.i18n('Metadata.MetaInfo.Item.AsMef'),
                scope: this,
                handler: function() {
                	var selection = this.getMetadataGrid().getSelectionModel().getSelections();
					var uuids = [];
					Ext.each(selection, function(item, index, allItems) {
						uuids.push(item.get('urn'));
					}, this);
                	window.location.href = configOptions.locService +  "/xml.metadata.export?uuid=" + uuids + "&format=full&version=2";
                }
            });
        }
        return this.asMefAction;
    },

    /****************************/
    /*  Search text form Panel  */
    
    getSearchFormPanel: function() {
        if(!this.searchFormPanel) {
            this.searchFormPanel = new Ext.form.FormPanel({
                labelWidth: 100,
                border: false,
                buttonAlign: 'center'
            });
            this.searchFormPanel.add(this.getSearchTextField());
            this.searchFormPanel.add(this.getSearchFieldCombo());
            this.searchFormPanel.add(this.getCategoriesComboBox());
            this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
            this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()));
        }
        return this.searchFormPanel;
    },
    
    // The text field for the text search accross metadata.
    getSearchTextField: function() {
        if(!this.searchTextField) {
            this.searchTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Metadata.CatalogContent.TextSearch'),
                name: 'any',
                enableKeyEvents: true, // is that the best? or change event?
                width: 150,
                listeners: {
                    keyup: function(){
                        var searchOn = Ext.isEmpty(this.getSearchTextField().getValue().trim()); 
                        Ext.isEmpty(this.getCategoriesComboBox().getRawValue().trim()) ;
                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        if (searchOn) {
                            this.getMetadataStore().setBaseParam(
                                this.getSearchTextField().getName(),
                                this.getSearchTextField().getValue()
                            );
                            this.getMetadataStore().setBaseParam("myMetadataOnly", !this.isAdmin);
                            this.getMetadataStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                        }
                    },
                    specialkey: function(f,e){
	                    if (e.getKey() == e.ENTER) {
	                      this.getSearchAction().execute();
	                    }
	                  },
                    scope: this
                }
            });
        }
        return this.searchTextField;
    },

    // The combo field for the text field search accross metadata.
    getSearchFieldCombo: function() {
        if(!this.searchFieldCombo) {
        	var columns = [];
        	columns.push(['', Openwis.i18n('Metadata.CatalogContent.SearchField.any')]);
        	columns.push(['uuid', Openwis.i18n('Metadata.CatalogContent.SearchField.uuid')]);
        	columns.push(['title', Openwis.i18n('Metadata.CatalogContent.SearchField.title')]);
        	/*columns.push(['_categoryName', Openwis.i18n('Metadata.CatalogContent.SearchField.category')]);*/
            
            if(this.isAdmin) {
                columns.push(['_originator',Openwis.i18n('Metadata.CatalogContent.SearchField.originator')]);
                columns.push(['_process',Openwis.i18n('Metadata.CatalogContent.SearchField.process')]);
            }

            columns.push(['_gtsCategory',Openwis.i18n('Metadata.CatalogContent.SearchField.gtsCategory')]);
            columns.push(['_overriddenGtsCategory',Openwis.i18n('Metadata.CatalogContent.SearchField.overriddenGtsCategory')]);
            columns.push(['_fncPattern',Openwis.i18n('Metadata.CatalogContent.SearchField.fncPattern')]);
            columns.push(['_overriddenFncPattern',Openwis.i18n('Metadata.CatalogContent.SearchField.overriddenFncPattern')]);
            columns.push(['_priority', Openwis.i18n('Metadata.CatalogContent.SearchField.priority')]);
            columns.push(['_overriddenPriority',Openwis.i18n('Metadata.CatalogContent.SearchField.overriddenPriority')]);
            columns.push(['_datapolicy',Openwis.i18n('Metadata.CatalogContent.SearchField.datapolicy')]);
            columns.push(['_overriddenDatapolicy',Openwis.i18n('Metadata.CatalogContent.SearchField.overriddenDatapolicy')]);
            columns.push(['_localDataSource',Openwis.i18n('Metadata.CatalogContent.SearchField.localDataSource')]);
            
            if(this.isAdmin) {
            	columns.push(['_isIngested',Openwis.i18n('Metadata.CatalogContent.SearchField.isIngested')]);
            	columns.push(['_isFed',Openwis.i18n('Metadata.CatalogContent.SearchField.isFed')]);
            	columns.push(['_fileExtension',Openwis.i18n('Metadata.CatalogContent.SearchField.fileExtension')]);
            }
      
            this.searchFieldCombo = new Ext.form.ComboBox({
                fieldLabel: Openwis.i18n('Metadata.CatalogContent.SearchField'),
                name: 'searchField',
                enableKeyEvents: true,
                width: 150,
                mode: 'local',
                store: new Ext.data.ArrayStore({
                    id: '_priority',
                    fields: [
                        'fieldKey', 
                        'fieldName'
                    ],
                    data: columns
                }),
                valueField: 'fieldKey',
                displayField: 'fieldName',
                triggerAction: 'all',
                listeners: {
                    'select': function(){
                    	var searchOn = 
                            Ext.isEmpty(this.getSearchFieldCombo().getRawValue().trim()) ||
                            Ext.isEmpty(this.getCategoriesComboBox().getRawValue().trim()) ; 

                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        if (searchOn) {
                            this.getMetadataStore().setBaseParam(
                                this.getSearchFieldCombo().getName(),
                                this.getSearchFieldCombo().getValue()
                            );
                            this.getMetadataStore().setBaseParam("myMetadataOnly", !this.isAdmin);
                            this.getMetadataStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                        }
                    },
                    specialkey: function(f,e){
	                    if (e.getKey() == e.ENTER) {
	                      this.getSearchAction().execute();
	                    }
	                  },
                    scope: this
                }
            });
            this.searchFieldCombo.setValue('');
        }
        return this.searchFieldCombo;
    },
    /**
     * The categories combo box.
     */
    getCategoriesComboBox: function() { 
        if(!this.categoriesComboBox) {
            var anyRecord = new Ext.data.Record({
                id: '',
                name: Openwis.i18n('Common.List.Any')
            });
            var categoryStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.category.all',
                idProperty: 'id',
                fields: [
                    {
                        name:'id'
                    },{
                        name:'name'
                    }
                ]
            });
        
            this.categoriesComboBox = new Ext.form.ComboBox({
                fieldLabel: Openwis.i18n('MetadataCreate.Category'),
                name: 'categories',
                store: categoryStore,
                valueField: 'id',
                displayField:'name',
                typeAhead: true,
                triggerAction: 'all',
                editable: false,
                selectOnFocus:true,
                width: 200,
                listeners: {
                    'select': function(){
                        var searchOn = 
                            Ext.isEmpty(this.getSearchFieldCombo().getRawValue().trim()) ||
                            Ext.isEmpty(this.getCategoriesComboBox().getRawValue().trim()) ;
                        
                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        
                        if (searchOn) {
                            this.getMetadataStore().setBaseParam(
                                this.getCategoriesComboBox().getName(),
                                this.getCategoriesComboBox().getValue()
                            );
                            this.getMetadataStore().setBaseParam("myMetadataOnly", !this.isAdmin);
                            this.getMetadataStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                        }
                    },
                    specialkey: function(f,e){
                        if (e.getKey() == e.ENTER) {
                          this.getSearchAction().execute();
                        }
                      },
                    scope: this
                }
            });
            var me = this.categoriesComboBox

            categoryStore.on('load', function(store, records, options) {
                    store.insert(0, [anyRecord]);
                    me.setValue('');
                });
            categoryStore.load();
        }
        
        return this.categoriesComboBox;
    },
    
    // Performs search
    getSearchAction: function() {
        if (!this.searchAction) {
            this.searchAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Btn.Search'),
                scope: this,
                handler: function() {
                    this.getMetadataStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getMetadataStore().setBaseParam(
                        this.getSearchFieldCombo().getName(),
                        this.getSearchFieldCombo().getValue()
                    );
                    this.getMetadataStore().setBaseParam(
                            this.getCategoriesComboBox().getName(),
                            this.getCategoriesComboBox().getValue()
                    );
                    this.getMetadataStore().setBaseParam("myMetadataOnly", !this.isAdmin);
                    this.getMetadataStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                }
            });
        }
        return this.searchAction;
    },
    
    // Reset search and update grid
    getResetAction: function() {
        if (!this.resetAction) {
            this.resetAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Btn.Reset'),
                scope: this,
                handler: function() {
                    this.getSearchTextField().setValue('');
                    this.getSearchFieldCombo().setValue('');
                    this.getCategoriesComboBox().setValue('');
                    this.getMetadataStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getMetadataStore().setBaseParam(
                            this.getSearchFieldCombo().getName(),
                            this.getSearchFieldCombo().getValue()
                        );
                    this.getMetadataStore().setBaseParam(
                            this.getCategoriesComboBox().getName(),
                            this.getCategoriesComboBox().getValue()
                        );
                    this.getMetadataStore().setBaseParam("myMetadataOnly", !this.isAdmin);
                    this.getMetadataStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    this.getSearchAction().setDisabled(true);
                    this.getResetAction().setDisabled(true);
                }
            });
        }
        return this.resetAction;
    },
    
    // Callback for duplicate metadata action 
    handleCreation: function(btn, text) {
        if (btn == 'ok' && this.isValidURN(text)) {
            var duplicateParams = {};
            duplicateParams.toURN = text; 
            duplicateParams.fromURN = this.getMetadataGrid().getSelectionModel().getSelected().data.urn;
            var saveHandler = new Openwis.Handler.Save({
                url: configOptions.locService+ '/xml.metadata.duplicate',
                params: duplicateParams,
                listeners: {
                    success: function(config) {
                        this.getMetadataStore().load({
                            params:{start:0, limit:Openwis.Conf.PAGE_SIZE}, 
                            callback : function () {
                                // Try to select the duplicated metadata and then open the editor.
                                // FIXME we are not sure that this metadata will be in the current store (first page).
                                // We should maybe filter/search by URN ... TBD.
                                var duplicate = this.getMetadataStore().getById(text);
                                if (Ext.isDefined(duplicate)) {
                                    this.getMetadataGrid().getSelectionModel().selectRecords([duplicate]);
                                    doEditMetadataByUrn(duplicate.get('urn'), duplicate.get('title'));
                                }
                            },
                            scope: this
                        });
                    },
                    scope: this
                }
            });
            saveHandler.proceed();
        }
    },
    
    //Utility methods.
    isValidURN: function(value) {
        // TODO check URN is valid.
        return true;
    },

    renderBoolean: function(value) {
        return value==true? 'yes':'no';
    },
    
    renderGtsCategory: function(value, metadata, record) {
        var overridenValue = record.data.overridenGtsCategory;
        if ( overridenValue!= undefined && overridenValue!='') {
            value = overridenValue;
            metadata.attr = 'style="color:red;"'; // TODO use a css class...
        }
        return Ext.util.Format.htmlEncode(value);
    },
    
    renderFncPattern: function(value, metadata, record) {
        var overridenValue = record.data.overridenFncPattern;
        if ( overridenValue!= undefined && overridenValue!='') {
            value = overridenValue;
            metadata.attr = 'style="color:red;"'; // TODO use a css class...
        }
        return Ext.util.Format.htmlEncode(value);
    },
    
    renderPriority: function(value, metadata, record) {
        var overridenValue = record.data.overridenPriority;
        if ( overridenValue!= undefined && overridenValue!='') {
            value = overridenValue;
            metadata.attr = 'style="color:red;"'; // TODO use a css class...
        }
        return Ext.util.Format.htmlEncode(value);
    }, 

    renderDataPolicy: function(value, metadata, record) {
        var overridenValue = record.data.overridenDataPolicy;
        if ( overridenValue!= undefined && overridenValue!='') {
            value = overridenValue;
            metadata.attr = 'style="color:red;"'; // TODO use a css class...
        }
        return Ext.util.Format.htmlEncode(value);
    }, 
    
    renderFileExtension: function(value, metadata, record) {
        var overridenValue = record.data.overridenFileExtension;
        if ( overridenValue!= undefined && overridenValue!='') {
            value = overridenValue;
            metadata.attr = 'style="color:red;"'; // TODO use a css class...
        }
        return Ext.util.Format.htmlEncode(value);
    }
});