Ext.ns('Openwis.Admin.Group');

Openwis.Admin.Group.All = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Group.All.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create Data policy grid.
		this.add(this.getGroupGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n("Security.Group.Title"),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	//-- Grid and Store.
	
	getGroupGrid: function() {
		if(!this.groupGrid) {
			this.groupGrid = new Ext.grid.GridPanel({
				id: 'groupGrid',
				height: 400,
				border: true,
				store: this.getGroupStore(),
				loadMask: true,
				columns: [
					{id:'name', header:Openwis.i18n("Security.Group.GroupName.Column"), dataIndex:'name', sortable: true, width: 300},
					{id:'global', header:Openwis.i18n("Security.Group.Global.Column"), dataIndex:'global', sortable: true, width: 100, xtype: 'booleancolumn'}
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
							sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1 || record.data.global);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
						},
						rowdeselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
						}
					}
				})
			});
			this.groupGrid.addButton(new Ext.Button(this.getNewAction()));
			this.groupGrid.addButton(new Ext.Button(this.getEditAction()));
			this.groupGrid.addButton(new Ext.Button(this.getRemoveAction()));
			this.groupGrid.addButton(new Ext.Button(this.getPrepareSynchronizeAction()));
		}
		return this.groupGrid;
	},
	
	getGroupStore: function() {
		if(!this.groupStore) {
			this.groupStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.group.all',
				idProperty: 'id',
				fields: [
					{
						name:'id'
					},{
						name:'name',
						sortType: Ext.data.SortTypes.asUCString
					},{
						name:'global'
					}
				],
				sortInfo: {
				    field: 'name',
				    direction: 'ASC'
				}
			});
		}
		return this.groupStore;
	},
	
	//-- Actions implemented on Data Policy Administration.
	
	getNewAction: function() {
		if(!this.newAction) {
			this.newAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				scope: this,
				handler: function() {
					new Openwis.Admin.Group.Manage({
						operationMode: 'Create',
						listeners: {
							groupSaved: function() {
								this.getGroupGrid().getStore().reload();
							},
							scope: this
						}
					});
				}
			});
		}
		return this.newAction;
	},
	
	getEditAction: function() {
		if(!this.editAction) {
			this.editAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Edit'),
				scope: this,
				handler: function() {
					var selectedRec = this.getGroupGrid().getSelectionModel().getSelected();
					new Openwis.Admin.Group.Manage({
						operationMode: 'Edit',
						editGroupName: selectedRec.get('name'),
						listeners: {
							groupSaved: function() {
								this.getGroupGrid().getStore().reload();
							},
							scope: this
						}
					});
				}
			});
		}
		return this.editAction;
	},
	
	getRemoveAction: function() {
		if(!this.removeAction) {
			this.removeAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Remove'),
				scope: this,
				handler: function() {
					//Get the group ids to delete.
					var selection = this.getGroupGrid().getSelectionModel().getSelections();
					var params = {groups: []};
					var global = false;
					Ext.each(selection, function(item, index, allItems) {
						params.groups.push({id: item.get('id'), name: item.get('name'), global: item.get('global')});
						if (item.get('global')) {
						    global = true;
						}
					}, this);
					
					var msg = null;
					if (global)  {
					    msg = 'Global group(s) will be removed. This modification will impact all other deployments of the circle of trust. Do you confirm the action ?';
					}
					//Invoke the remove handler to remove the elements by an ajax request.
					var removeHandler = new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.group.remove',
						params: params,
						confirmMsg: msg,
						listeners: {
							success: function() {
								this.getGroupGrid().getStore().reload();
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
	
	getPrepareSynchronizeAction: function() {
		if(!this.prepareSynchronizeAction) {
			this.prepareSynchronizeAction = new Ext.Action({
				text: Openwis.i18n('Security.Group.Synchronize'),
				scope: this,
				handler: function() {
					var saveHandler = new Openwis.Handler.Get({
						url: configOptions.locService+ '/xml.group.synchronize',
						params: {perform:false},
						listeners: {
							success: function(prepareResult) {
							    var msg = "";
							    for(var i=0;i<prepareResult.prepSynchro.length;i++) {
							        msg+=prepareResult.prepSynchro[i] + "<br/>";
							    }
							    if (prepareResult.prepSynchro.length == 0) {
							        Ext.Msg.show({
                                       title:Openwis.i18n("Security.Group.Synchronize.NoGroupDlg.Title"),
                                       msg: Openwis.i18n("Security.Group.Synchronize.NoGroupDlg.Msg"),
                                       buttons: Ext.Msg.OK,
                                       scope: this,
                                       icon: Ext.MessageBox.INFO
                                    });
							    } else {
							        Ext.Msg.show({
                                       title:'Confirm Synchronize',
                                       msg: msg,
                                       buttons: Ext.Msg.YESNO,
                                       fn: function(buttonId) {
                                           if (buttonId == "yes") {
                                                  var saveHandler = new Openwis.Handler.Save({
                            						url: configOptions.locService+ '/xml.group.synchronize',
                            						params: {perform:true},
                            						listeners: {
                            							success: function() {
                            								this.getGroupGrid().getStore().reload();
                            							},
                            							scope: this
                            						}
                            					});
                            					saveHandler.proceed();
                                           }
                                       },
                                       scope: this,
                                       icon: Ext.MessageBox.QUESTION
                                    });
							    }
							},
							scope: this
						}
					});
					saveHandler.proceed();
				}
			});
		}
		return this.prepareSynchronizeAction;
	}
});