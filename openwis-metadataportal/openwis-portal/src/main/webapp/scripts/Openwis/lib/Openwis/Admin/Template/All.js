Ext.ns('Openwis.Admin.Template');

Openwis.Admin.Template.All = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Template.All.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var params = {};
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.template.allShema',
			params: params,
			listeners: {
				success: function(config) {
					this.allShemaStore = config;
					this.initialize();
				},
				failure: function(config) {
        			alert("failed");
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
        var p = new Ext.Panel({
            layout:"column"
        });

		var upDownPanel = new Ext.Panel({
			itemCls: 'formItems',
			border: false,
			vertical: true,
			columnWidth:.25,
	        style: {
	            padding: '10px'
	        },
			items:
			[
                new Ext.Button(this.getUpAction()),
                new Ext.Button(this.getDownAction()),
                new Ext.Button(this.getSaveOrderAction())
            ]
        });
		p.add(this.getTemplateGrid());
		p.add(upDownPanel);
		this.add(p);
		//Create Data policy grid.
//		this.add(this.getTemplateGrid());
		this.doLayout();
        this.getSaveOrderAction().setDisabled(1);

	},
    getAddMenuButton: function() {
        if(!this.addMenuButton) {
            this.addMenuButton = new Ext.Button({
                text: Openwis.i18n('TemplateManagement.Btn.Add'),
                menu: new Ext.menu.Menu()
            });
        }
        for (var i=0; i<this.allShemaStore.length; i++) {
        	var menuitm = new Ext.menu.Item({
                text: this.allShemaStore[i].name,
                scope: this,
                handler: function(item) {
					var msg = null;
					var params = {content: item.text};
					var addHandler = new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.template.addDefault',
						params: params,
						confirmMsg: msg,
						listeners: {
							success: function() {
								this.getTemplateGrid().getStore().reload();
							},
							scope: this
						}
					});
					addHandler.proceed();
                }
            });
	       var item = this.addMenuButton.menu.add(menuitm);
        }
 
         
       return this.addMenuButton;
    },
    
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('TemplateManagement.Template.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	getTemplateGrid: function() {
		if(!this.templateGrid) {
			this.templateGrid = new Ext.grid.GridPanel({
				id: 'templateGrid',
				height: 400,
				columnWidth:.75,
				border: true,
				store: this.getAllTemplateStore(),
				loadMask: true,
				columns: [
					{id:'name', header:Openwis.i18n('TemplateManagement.Name'), dataIndex:'title', sortable: false, width: 200},
					{id:'schema', header:Openwis.i18n('TemplateManagement.Schema'), dataIndex:'schema', sortable: false, width: 100}
				],
				autoExpandColumn: 'name',
				listeners: { 
					afterrender: function (grid) {
					   grid.loadMask.show();
					   grid.getStore().load();
					}
				},
				sm: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.ownerCt.getDuplicateAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.ownerCt.getRemoveAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.ownerCt.disableUpAndDown(rowIndex);
					    },
                        rowdeselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.ownerCt.getDuplicateAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.ownerCt.getRemoveAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.ownerCt.disableUpAndDown(rowIndex);
                        }
					}
				})
			});
			this.templateGrid.addButton(this.getAddMenuButton());
			this.templateGrid.addButton(this.getDuplicateAction());
			this.templateGrid.addButton(this.getEditAction());
			this.templateGrid.addButton(this.getRemoveAction());
		}
        return this.templateGrid;
    },
    
	disableUpAndDown: function(rowIndex) {
		if (rowIndex == 0){
			this.getUpAction().setDisabled(true);
			this.getDownAction().setDisabled(this.getTemplateGrid().getStore().getCount() == 1);
		} else if (rowIndex == this.getTemplateGrid().getStore().getCount() - 1){
			this.getUpAction().setDisabled(false);
			this.getDownAction().setDisabled(true);
		} else {
			this.getUpAction().setDisabled(false);
			this.getDownAction().setDisabled(false);		
		}
		
	},
	
	getAllTemplateStore: function() {
		if(!this.allTemplateStore) {
			this.allTemplateStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.template.all',
                // store configs
                autoDestroy: true,
				idProperty: 'urn',
				fields: ['id','urn','title','schema']
			});
		}
		return this.allTemplateStore;
	},
	
	//-- Actions implemented on Data Policy Administration.
	
	getSaveOrderAction: function() {
		if(!this.saveOrderAction) {
			this.saveOrderAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.SaveOrder'),
				scope: this,
				width:100,
				handler: function() {
					//Get the category ids to delete.
					var records = this.getTemplateGrid().getStore().data.items;
					var params = [];
					Ext.each(records, function(item, index, allItems) {
						params.push(item.get('urn'));
					}, this);
					
					var msg = null;
					//Invoke the up handler to up the elements by an ajax request.
					var saveOrderHandler = new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.template.saveOrder',
						params: params,
						confirmMsg: msg,
						listeners: {
							success: function() {
						        this.getSaveOrderAction().setDisabled(1);
								this.getTemplateGrid().getStore().reload();
							},
							scope: this
						}
					});
					saveOrderHandler.proceed();
				}
			});
		}
		return this.saveOrderAction;
	},
	getUpAction: function() {
		if(!this.upAction) {
			this.upAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Up'),
				scope: this,
				width:100,
				handler: function() {
					this.up();
				}
			});
		}
		return this.upAction;
	},
    up : function() {
        var record = null;
        var selectedIndex = -1;
		// recherche du selectedindex
		for (var i=0; i<this.templateGrid.store.getCount(); i++) {
        	if (this.getTemplateGrid().getSelectionModel().isSelected(i)) selectedIndex = i;
        }
		if (selectedIndex != -1){
	        record = this.templateGrid.store.getAt(selectedIndex);
	        if ((selectedIndex - 1) >= 0) {
	            this.templateGrid.store.remove(record);
	            this.templateGrid.store.insert(selectedIndex - 1, record);
	        }
	        this.templateGrid.getView().refresh();
	        this.templateGrid.getSelectionModel().selectRow(selectedIndex-1);	
	        this.getSaveOrderAction().setDisabled(0);
	        this.disableUpAndDown(selectedIndex-1);
		}
    },

	getDownAction: function() {
		if(!this.downAction) {
			this.downAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Down'),
				scope: this,
				width:100,
				handler: function() {
					this.down();
				}
			});
		}
		return this.downAction;
	},
    down : function() {
        var record = null;
        var selectedIndex = -1;
		// recherche du selectedindex
		for (var i=0; i<this.templateGrid.store.getCount(); i++) {
        	if (this.getTemplateGrid().getSelectionModel().isSelected(i)) selectedIndex = i;
        }
		if (selectedIndex != -1){
	        record = this.templateGrid.store.getAt(selectedIndex);
	        if ((selectedIndex + 1) < this.templateGrid.store.getCount()) {
	            this.templateGrid.store.remove(record);
	            this.templateGrid.store.insert(selectedIndex + 1, record);
	        }
	        this.templateGrid.getView().refresh();
	        this.templateGrid.getSelectionModel().selectRow(selectedIndex+1);			
	        this.getSaveOrderAction().setDisabled(0);
	        this.disableUpAndDown(selectedIndex+1);
		}
    },
	getDuplicateAction: function() {
		if(!this.duplicateAction) {
			this.duplicateAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Duplicate'),
                tooltip: 'Create a new metadata from the selected one.',
				scope: this,
				handler: function() {
                    var selectedRec = this.getTemplateGrid().getSelectionModel().getSelected();
                    
                    Ext.MessageBox.confirm('Confirm ?', Openwis.i18n('TemplateManagement.Confirm.Duplicate') , function(btnClicked) {
            				if(btnClicked == 'yes') {
            					new Openwis.Handler.Save({
                                    url: configOptions.locService+ '/xml.template.duplicate',
                                    params: {urn: selectedRec.get('urn')},
                                    listeners: {
                                        success: function(config) {
                                            this.getAllTemplateStore().load();
                                        },
                                        scope: this
                                    }
                                }).proceed();
            				}
            			}, 
            			this
            		);
				}
			});
		}
		return this.duplicateAction;
	},
	getEditAction: function() {
		if(!this.editAction) {
			this.editAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Edit'),
				scope: this,
				handler: function() {
                    var selectedRec = this.getTemplateGrid().getSelectionModel().getSelected();
                    doEditMetadataById(selectedRec.get('id'), selectedRec.get('title'));
                    addMetadataDialogCloseListener(this.dialogCloseListener, this);
				}
			});
		}
		return this.editAction;
	},
	
	dialogCloseListener: function(ct) {
		this.getTemplateGrid().getStore().reload();
		removeMetadataDialogCloseListener(this.dialogCloseListener, this);
    },
	
	getRemoveAction: function() {
		if(!this.removeAction) {
			this.removeAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Remove'),
				scope: this,
				handler: function() {
					//Get the category ids to delete.
					var selection = this.getTemplateGrid().getSelectionModel().getSelected();
					var msg = null;
					//Invoke the remove handler to remove the elements by an ajax request.
					var removeHandler = new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.template.remove',
						params: {urn: selection.get('urn')},
						confirmMsg: msg,
						listeners: {
							success: function() {
								this.getTemplateGrid().getStore().reload();
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
	
    //Utility methods.
    isValidURN: function(value) {
        // TODO check URN is valid.
        return true;
    }

});