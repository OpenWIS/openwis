Ext.ns('Openwis.Admin.DataPolicy');

Openwis.Admin.DataPolicy.Manage = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Security.DataPolicy.Manage.Title'),
			layout: 'fit',
			width:750,
			height:600,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.DataPolicy.Manage.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var params = {};
		if(this.isEdition()) {
			params.name = this.editDataPolicyName;
		}
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.datapolicy.get',
			params: params,
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
				},
				failure: function(config) {
					this.close();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add data policy saved event.
		this.addEvents("dataPolicySaved");
		
		//-- Create form panel.
		this.add(this.getDataPolicyFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		//-- If specified, populate forms from specified data policy.
		if(this.isEdition()) {
			this.getNameTextField().setValue(this.config.dataPolicy.name);
			this.getDescriptionTextArea().setValue(this.config.dataPolicy.description);
		}
		
		this.show();
	},
	
	getDataPolicyFormPanel: function() {
		if(!this.dataPolicyFormPanel) {
			this.dataPolicyFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 250
			});
			this.dataPolicyFormPanel.add(this.getNameTextField());
			this.dataPolicyFormPanel.add(this.getDescriptionTextArea());
			this.dataPolicyFormPanel.add(this.getAliasesGrid());
			this.dataPolicyFormPanel.add(new Ext.form.Label({
				fieldLabel:Openwis.i18n('Security.DataPolicy.Manage.OpAllowedLabel')
			}));
			this.dataPolicyFormPanel.add(this.getOperationsAllowedGrid());
		}
		return this.dataPolicyFormPanel;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getNameTextField: function() {
		if(!this.nameTextField) {
			this.nameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Security.DataPolicy.Manage.Name'),
				name: 'name',
				allowBlank:false,
				disabled: this.isEdition(),
				width: 250
			});
		}
		return this.nameTextField;
	},
	
	/**
	 * The text area for the data policy description.
	 */
	getDescriptionTextArea: function() {
		if(!this.descriptionTextArea) {
			this.descriptionTextArea = new Ext.form.TextArea({
				fieldLabel: Openwis.i18n('Security.DataPolicy.Manage.Description'),
				allowBlank:true,
				name: 'description',
				width: 250
			});
		}
		return this.descriptionTextArea;
	},
	
	/**
	 *	The data policy groups and operations allowed.
	 */
	getOperationsAllowedGrid: function() {
		if(!this.operationsAllowedGrid) {
			/* Build columns table for colModel and fields for store.*/
			var columns = [];
			var fieldsStore = [];
			
			//Add group column.
			columns.push(new Ext.grid.Column({id:'group', header:Openwis.i18n('Security.DataPolicy.Manage.Header.Groups'), dataIndex:'groupName', sortable: true}));
			fieldsStore.push(new Ext.data.Field({name: 'groupName', mapping: 'group.name'}));
			
			//Add operations columns.
			for(var i = 0; i < this.config.operations.length; i++) {
				var operation = (this.config.operations)[i];
				// ignore FTPSecured
				if (operation.name != 'FTPSecured') {
					columns.push(new Ext.ux.grid.CheckColumn({id:operation.name, header:operation.name, dataIndex:operation.name, sortable: false, align: 'center'}));
				}
				fieldsStore.push(
					new Ext.data.Field({
						name: operation.name,
						type: 'boolean',
						convert: function(v, record) {
							for(var i = 0; i < record.privilegesPerOp.length;i++) {
								if(this.name == record.privilegesPerOp[i].operation.name) {
									return record.privilegesPerOp[i].authorized;
								}
							}
							return false;
						}
					})
				);
			}
			
			//Create Column Model.
			var colModel = new Ext.grid.ColumnModel({
				defaults: {
					menuDisabled: true,
					width: 70
				},
				columns: columns
			});
			
			/* Build store */
			var operationsAllowedStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idIndex: 0,
				fields: fieldsStore
			});
			//Load Data into store.
			operationsAllowedStore.loadData(this.config.dataPolicy.dpOpPerGroup);
		
			this.operationsAllowedGrid = new Ext.grid.GridPanel({
				id: 'operationsAllowedGrid',
				height: 200,
				border: true,
				loadMask: true,
				colModel: colModel,
				store: operationsAllowedStore,
				style: {
					margin: '0px 10px 0px 10px'
				},
				autoExpandColumn: 'group'
			});
		}
		return this.operationsAllowedGrid;
	},
	
	/**
	 *	The aliases table.
	 */
	getAliasesGrid: function() {
		if(!this.aliasesGrid) {
			/* Build store */
			var aliasesStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idIndex: 0,
				fields: [
					{name: 'alias'}
				]
			});
			//Load Data into store.
			aliasesStore.loadData(this.config.dataPolicy.aliases);
		
			this.aliasesGrid = new Ext.grid.GridPanel({
				height: 150,
				width: 	250,
				border: true,
				loadMask: true,
				fieldLabel: Openwis.i18n('Security.DataPolicy.Manage.Aliases'),
				colModel: new Ext.grid.ColumnModel({
					defaults: {
						width: 120,
						sortable: true
					},
					columns: [
						{id: 'aliases', header: Openwis.i18n('Security.DataPolicy.Header.Aliases'), sortable: true, dataIndex: 'alias'}
					]
				}),
				store: aliasesStore,
				style: {
					margin: '0px 10px 0px 0px'
				},
				autoExpandColumn: 'aliases',
				sm: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.ownerCt.getAliasesRemoveAction().setDisabled(sm.getCount() == 0);
						},
						rowdeselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.ownerCt.getAliasesRemoveAction().setDisabled(sm.getCount() == 0);
						}
					}
				})
			});
			this.aliasesGrid.addButton(new Ext.Button(this.getAliasesAddAction()));
			this.aliasesGrid.addButton(new Ext.Button(this.getAliasesRemoveAction()));
		}
		return this.aliasesGrid;
	},
	
	/**
	 *	The action trigerred when trying to add an alias.
	 */
	getAliasesAddAction: function() {
		if(!this.aliasesAddAction) {
			this.aliasesAddAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Add'),
				scope: this,
				handler: function() {
					var msgPrompt = Ext.Msg.prompt(Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Title'), Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.AliasLabel'), function(btn, text){
						if (btn == 'ok'){
							if (Ext.isEmpty(text)) {
								Ext.Msg.show({
								   title:Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error'),
								   msg: Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error.NameMandatory'),
								   buttons: Ext.Msg.OK,
								   icon: Ext.MessageBox.ERROR
								});
							} else if (text.trim() == this.getNameTextField().getValue().trim()) {
								Ext.Msg.show({
								   title:Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error'),
								   msg: Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error.NameNotEquals'),
								   buttons: Ext.Msg.OK,
								   icon: Ext.MessageBox.ERROR
								});
							} else if (this.getAliasesGrid().getStore().findExact("alias", text.trim()) != -1) {
								Ext.Msg.show({
								   title:Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error'),
								   msg: Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error.AlreadyExists'),
								   buttons: Ext.Msg.OK,
								   icon: Ext.MessageBox.ERROR
								});
							} else {
								this.getAliasesGrid().getStore().add(new Ext.data.Record({alias: text}));
							}
						}
					}, this);
				}
			});
		}
		return this.aliasesAddAction;
	},
	
	/**
	 *	The action trigerred when removing an alias.
	 */
	getAliasesRemoveAction: function() {
		if(!this.aliasesRemoveAction) {
			this.aliasesRemoveAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Remove'),
				scope: this,
				disabled: true,
				handler: function() {
					var selection = this.getAliasesGrid().getSelectionModel().getSelections();
					this.getAliasesGrid().getStore().remove(selection);
				}
			});
		}
		return this.aliasesRemoveAction;
	},
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
					if(this.isValid()) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.datapolicy.save',
							params: this.getDataPolicy(),
							listeners: {
								success: function(config) {
									this.fireEvent("dataPolicySaved");
									this.close();
								},
								scope: this
							}
						});
						saveHandler.proceed();
					}
				}
			});
		}
		return this.saveAction;
	},
	
	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	//----- Utility methods.
	
	isValid: function() {
		if(this.getDataPolicyFormPanel().getForm().isValid()) {
			/** Extented validation.**/
			//-- Check alias and data policy name are not equals.
			if (this.getAliasesGrid().getStore().findExact("alias", this.getNameTextField().getValue().trim()) != -1) {
				Ext.Msg.show({
				   title:Openwis.i18n('Security.DataPolicy.Manage.Aliases.Add.Error'),
				   msg: Openwis.i18n('The alias cannot be equals to the data policy name.'),
				   buttons: Ext.Msg.OK,
				   icon: Ext.MessageBox.ERROR
				});
				return false;
			}
			return true;
		} else {
			return false;
		}
	},
	
	getDataPolicy: function() {
		var dataPolicy = {};
		
		if(this.isEdition()) {
		    dataPolicy.id = this.config.dataPolicy.id;
		}
		
		//The data policy attributes.
		dataPolicy.name = this.getNameTextField().getValue();
		dataPolicy.description = this.getDescriptionTextArea().getValue();
		
		//Aliases.
		dataPolicy.aliases = [];
		this.getAliasesGrid().getStore().each(function(rec) {
			dataPolicy.aliases.push(rec.data);
		}, this);
		
		//Operations Allowed.
		dataPolicy.dpOpPerGroup = [];
		for(var i = 0; i < this.getOperationsAllowedGrid().getStore().getCount();i++) {
			var rec = this.getOperationsAllowedGrid().getStore().getAt(i);
			
			//Affect initial content to operation.
			var opAllowed = rec.json;
			
			//If record has been modified, apply the modifications.
			if(rec.dirty) {
				Ext.iterate(rec.modified, function(key, value) {
					for(var i = 0; i < opAllowed.privilegesPerOp.length; i++) {
						if(opAllowed.privilegesPerOp[i].operation.name == key) {
							opAllowed.privilegesPerOp[i].authorized = rec.data[key];
							break;
						}
					}
				});
			}
			
			//Add to operations allowed of data policy.
			dataPolicy.dpOpPerGroup.push(opAllowed);
		}
		
		return dataPolicy;
	},
	
	/**
	 * Returns true if it is a creation, false otherwise.
	 */
	isCreation: function() {
		return (this.operationMode == 'Create');
	},
	
	/**
	 * Returns true if it is an edition, false otherwise.
	 */
	isEdition: function() {
		return (this.operationMode == 'Edit');
	}
});