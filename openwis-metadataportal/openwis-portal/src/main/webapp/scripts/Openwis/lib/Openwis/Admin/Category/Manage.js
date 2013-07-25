Ext.ns('Openwis.Admin.Category');

Openwis.Admin.Category.Manage = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('CategoryManagement.Manage.Title'),
			layout: 'fit',
			width:350,
			height:150,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.Category.Manage.superclass.initComponent.apply(this, arguments);
		
		if(this.isEdition()) {
			this.getInfosAndInitialize();
		} else {
			this.initialize();
		}
	},
	
	getInfosAndInitialize: function() {
		var params = {};
		params.name = this.editCategoryName;
		
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.category.get',
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
		this.addEvents("categorySaved");
		
		//-- Create form panel.
		this.add(this.getCategoryFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		//-- If specified, populate forms from specified data policy.
		if(this.isEdition()) {
			this.getNameTextField().setValue(this.config.name);
		}
		
		this.show();
	},
	
	getCategoryFormPanel: function() {
		if(!this.categoryFormPanel) {
			this.categoryFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 125
			});
			this.categoryFormPanel.add(this.getNameTextField());
		}
		return this.categoryFormPanel;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getNameTextField: function() {
		if(!this.nameTextField) {
			this.nameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('CategoryManagement.Manage.Name'),
				name: 'name',
				allowBlank:false,
				width: 150
			});
		}
		return this.nameTextField;
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
					if(this.getCategoryFormPanel().getForm().isValid()) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.category.save',
							params: this.getCategory(),
							listeners: {
								success: function(config) {
									this.fireEvent("categorySaved");
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
	
	getCategory: function() {
		var category = {};
		
		//If edition...
		if(this.isEdition()) {
			category.id = this.config.id;
		}
		
		//The category attributes.
		category.name = this.getNameTextField().getValue();
		
		return category;
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