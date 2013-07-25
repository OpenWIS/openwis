Ext.ns('Openwis.Admin.DataPolicy');

Openwis.Admin.DataPolicy.All = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.DataPolicy.All.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create Data policy grid.
		this.add(this.getDataPolicyGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Security.DataPolicy.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	//-- Grid and Store.
	
	getDataPolicyGrid: function() {
		if(!this.dataPolicyGrid) {
			this.dataPolicyGrid = new Ext.grid.GridPanel({
				id: 'dataPolicyGrid',
				height: 400,
				border: true,
				store: this.getDataPolicyStore(),
				loadMask: true,
				columns: [
					{id:'name', header:Openwis.i18n('Security.DataPolicy.Header.DataPolicy'), dataIndex:'name', sortable: true},
					{id:'description', header:Openwis.i18n('Security.DataPolicy.Header.Description'), dataIndex:'description', sortable: true},
					{id:'aliases', header:Openwis.i18n('Security.DataPolicy.Header.Aliases'), dataIndex:'aliases', sortable: true, renderer: this.renderAliases}
				],
				autoExpandColumn: 'aliases',
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
						},
						rowdeselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
						}
					}
				})
			});
			this.dataPolicyGrid.addButton(new Ext.Button(this.getNewAction()));
			this.dataPolicyGrid.addButton(new Ext.Button(this.getEditAction()));
			this.dataPolicyGrid.addButton(new Ext.Button(this.getRemoveAction()));
			//this.dataPolicyGrid.addButton(new Ext.Button(this.getImportAction()));
			this.dataPolicyGrid.addButton(new Ext.Button(this.getExportAction()));
		}
		return this.dataPolicyGrid;
	},
	
	getDataPolicyStore: function() {
		if(!this.dataPolicyStore) {
			this.dataPolicyStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.datapolicy.all',
				idProperty: 'id',
				fields: [
				    {
						name:'id'
					},{
						name:'name'
					},{
						name:'description'
					},{
						name:'aliases'
					}
				]
			});
		}
		return this.dataPolicyStore;
	},
	
	//-- Actions implemented on Data Policy Administration.
	
	getNewAction: function() {
		if(!this.newAction) {
			this.newAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.New'),
				scope: this,
				handler: function() {
					new Openwis.Admin.DataPolicy.Manage({
						operationMode: 'Create',
						listeners: {
							dataPolicySaved: function() {
								this.getDataPolicyGrid().getStore().reload();
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
				text:Openwis.i18n('Common.Btn.Edit'),
				scope: this,
				handler: function() {
					var selectedRec = this.getDataPolicyGrid().getSelectionModel().getSelected();
					new Openwis.Admin.DataPolicy.Manage({
						operationMode: 'Edit',
						editDataPolicyName: selectedRec.get('name'),
						listeners: {
							dataPolicySaved: function() {
								this.getDataPolicyGrid().getStore().reload();
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
				text:Openwis.i18n('Common.Btn.Remove'),
				scope: this,
				handler: function() {
					//Get the data policy names to delete.
					var selection = this.getDataPolicyGrid().getSelectionModel().getSelections();
					var dataPolicies = [];
					Ext.each(selection, function(item, index, allItems) {
						dataPolicies.push({id: item.get('id'), name: item.get('name')});
					}, this);
					
					//Invoke the Save handler to remove the elements by an ajax request.
					var removeHandler = new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.datapolicy.remove',
						params: {dataPolicies: dataPolicies},
						listeners: {
							success: function() {
								this.getDataPolicyGrid().getStore().reload();
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
	
	getImportAction: function() {
		if(!this.importAction) {
			this.importAction = new Ext.Action({
				text:Openwis.i18n('Security.DataPolicy.Btn.Import'),
				scope: this,
				handler: function() {
					
				}
			});
		}
		return this.importAction;
	},
	
	getExportAction: function() {
		if(!this.exportAction) {
			this.exportAction = new Ext.Action({
				text:Openwis.i18n('Security.DataPolicy.Btn.Export'),
				scope: this,
				handler: function() {
					// TODO Igor: call the 'datapolicy.export' 
					window.open('./datapolicy.export','DataPolicies Export');
				}
			});
		}
		return this.exportAction;
	},
	
	//Utility methods.
	renderAliases: function(val) {
		var arr = [];
		for(var i=0; i < val.length; i++) {
			arr.push(val[i].alias);
		}
		return arr.join();
	}
});