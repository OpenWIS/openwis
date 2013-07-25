Ext.ns('Openwis.Admin.Category');

Openwis.Admin.Category.All = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Category.All.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create Category grid.
		this.add(this.getCategoryGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('CategoryManagement.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	//-- Grid and Store.
	
	getCategoryGrid: function() {
		if(!this.categoryGrid) {
			this.categoryGrid = new Ext.grid.GridPanel({
				id: 'categoryGrid',
				height: 400,
				border: true,
				store: this.getCategoryStore(),
				loadMask: true,
				columns: [
					{id:'name', header:Openwis.i18n('CategoryManagement.Name'), dataIndex:'name', sortable: true, width: 300}
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
							sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
						}
					}
				})
			});
			this.categoryGrid.addButton(new Ext.Button(this.getNewAction()));
			this.categoryGrid.addButton(new Ext.Button(this.getEditAction()));
			this.categoryGrid.addButton(new Ext.Button(this.getRemoveAction()));
		}
		return this.categoryGrid;
	},
	
	getCategoryStore: function() {
		if(!this.categoryStore) {
			this.categoryStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.category.all',
				idProperty: 'id',
				fields: [
					{
						name:'id'
					},{
						name:'name',
						sortType: Ext.data.SortTypes.asUCString
					}
				],
				sortInfo: {
				    field: 'name',
				    direction: 'ASC'
				}

			});
		}
		return this.categoryStore;
	},
	
	//-- Actions implemented on Data Policy Administration.
	
	getNewAction: function() {
		if(!this.newAction) {
			this.newAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				scope: this,
				handler: function() {
					new Openwis.Admin.Category.Manage({
						operationMode: 'Create',
						listeners: {
							categorySaved: function() {
								this.getCategoryGrid().getStore().reload();
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
					var selectedRec = this.getCategoryGrid().getSelectionModel().getSelected();
					new Openwis.Admin.Category.Manage({
						operationMode: 'Edit',
						editCategoryName: selectedRec.get('name'),
						listeners: {
							categorySaved: function() {
								this.getCategoryGrid().getStore().reload();
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
					//Get the category ids to delete.
					var selection = this.getCategoryGrid().getSelectionModel().getSelections();
					var params = {categories: []};
					Ext.each(selection, function(item, index, allItems) {
						params.categories.push({id: item.get('id'), name: item.get('name')});
					}, this);
					
					var msg = null;
					//Invoke the remove handler to remove the elements by an ajax request.
					var removeHandler = new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.category.remove',
						params: params,
						confirmMsg: msg,
						listeners: {
							success: function() {
								this.getCategoryGrid().getStore().reload();
							},
							scope: this
						}
					});
					removeHandler.proceed();
				}
			});
		}
		return this.removeAction;
	}
});