Ext.ns('Openwis.Admin.Category');

Openwis.Admin.Category.Edit = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: 'Edit Category ...',
			layout: 'fit',
			width:480,
			height:140,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.Category.Edit.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		//if (this.multiple) 
		// var getHandler = new Openwis.Handler.Get({
		// 	url: configOptions.locService+ '/xml.metainfo.get',
		// 	params: this.metadataURNs,
		// 	listeners: {
		// 		success: function(config) {
		// 			this.config = config;
		// 			this.initialize();
		// 		},
		// 		failure: function(config) {
		// 			this.close();
		// 		},
		// 		scope: this
		// 	}
		// });
		this.initialize();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add metaInfo saved event.
		this.addEvents("editCategorySaved");
		
		//-- Create single or multiple form panel.
	    this.add(this.getCategoryFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		this.show();
	},
	
	/**
	 * The categories panel.
	 */
	getCategoryFormPanel: function() {
		if(!this.categoryFormPanel) {
			this.categoryFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 125
			});
			this.categoryFormPanel.add(this.getCategoriesComboBox());
		}
		return this.categoryFormPanel;
	},

	/**
	 * The categories combo box.
	 */
	getCategoriesComboBox: function() {	
		if(!this.categoriesComboBox) {
			this.categoriesComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('MetadataInsert.Category'),
				name: 'category',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				editable: false,
				allowBlank: false,
				width: 330,
				store: this.getCategoryStore(),
				displayField: 'name',
				value: this.categoryName,
				valueField: 'id'
			});
			
			//Load Data into store.
			this.categoriesComboBox.getStore().load();
		}

		return this.categoriesComboBox;
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
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
                    // Initialize parameters to save. 
					var params= {
					    productsMetadataUrn : this.metadataURNs,
					    category : this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data
				    };
					var saveHandler = new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.category.edit',
						params: params,
						listeners: {
							success: function(config) {
								this.fireEvent("editCategorySaved");
								this.close();
							},
							scope: this
						}
					});
					saveHandler.proceed();
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
				text: Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	}

});
