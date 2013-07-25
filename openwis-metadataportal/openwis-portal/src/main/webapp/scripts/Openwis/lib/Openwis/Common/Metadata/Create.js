Ext.ns('Openwis.Common.Metadata');

Openwis.Common.Metadata.Create = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Common.Metadata.Create.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.metadata.create.form',
			params: {},
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
	 * Initializes the form.
	 */
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create Metadata form.
		this.getCreateMetadataFormPanel().add(this.getUrnInfoFormPanel());
		/*this.getCreateMetadataFormPanel().add(this.getDataPoliciesComboBox());*/
		this.getCreateMetadataFormPanel().add(this.getTemplatesComboBox());
		this.getCreateMetadataFormPanel().add(this.getCategoriesComboBox());
		
		this.add(this.getCreateMetadataFormPanel());
		
		this.doLayout();
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('MetadataCreate.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	//-- Form for metadata creation.
	
	/**
	 *	The form panel.
	 */
	getCreateMetadataFormPanel: function() {
		if(!this.createMetadataFormPanel) {
			this.createMetadataFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				border: false,
				buttons:
				[
					{
						text: Openwis.i18n('Common.Btn.Create'),
						handler: function(btn, e) {
							if(this.getCreateMetadataFormPanel().getForm().isValid()) {
								var saveHandler = new Openwis.Handler.Save({
									url: configOptions.locService+ '/xml.metadata.create',
									params: this.getMetadataCreationInfos(),
									listeners: {
										success: function(pm) {
											//TODO Go to browse my metadata.
											doEditMetadataByUrn(pm.urn, pm.title);
										},
										scope: this
									}
								});
								saveHandler.proceed();
							}
						},
						scope: this
					}
				]
			});
		}
		return this.createMetadataFormPanel;
	},

	getUrnInfoFormPanel: function() {
		if(!this.urnInfoFormPanel) {
			this.urnInfoFormPanel = new Ext.Panel({
				fieldLabel: Openwis.i18n('MetadataCreate.URN'),
				border: false
			});

            // Fill the table layout panel
			this.urnInfoFormPanel.add(this.getUrnAuthLabel());
            this.urnInfoFormPanel.add(this.getUrnAuthTextField());
            this.urnInfoFormPanel.add(this.getUrnIdLabel());
            this.urnInfoFormPanel.add(this.getUrnIdTextField());
		}
		return this.urnInfoFormPanel;
	},

	/**
	 * The label for the Urn Authority.
	 */
	getUrnAuthLabel: function() {
		if(!this.urnAuthLabel) {
			this.urnAuthLabel = new Ext.form.Label({
				text: Openwis.i18n('MetadataCreate.URN.Prefix')
			});
		}
		return this.urnAuthLabel;
	},

	/**
	 * The label for the Urn unique identifier.
	 */
	getUrnIdLabel: function() {
		if(!this.urnIdLabel) {
			this.urnIdLabel = new Ext.form.Label({
				text: Openwis.i18n('MetadataCreate.URN.Middle')
			});
		}
		return this.urnIdLabel;
	},

	/**
	 * The text field for the Urn Authority.
	 */
	getUrnAuthTextField: function() {
		if(!this.urnAuthTextField) {
			this.urnAuthTextField = new Ext.form.TextField({
				name: 'urnAuth',
				allowBlank:false,
				width: 120,
				emptyText: Openwis.i18n('MetadataCreate.Authority')
			});
		}
		return this.urnAuthTextField;
	},

	/**
	 * The text field for the Urn unique identifier.
	 */
	getUrnIdTextField: function() {
		if(!this.urnIdTextField) {
			this.urnIdTextField = new Ext.form.TextField({
				name: 'urnId',
				allowBlank:false,
				width: 120,
				emptyText: Openwis.i18n('MetadataCreate.Id')
			});
		}
		return this.urnIdTextField;
	},

	/**
	 * The data policies combo box.
	 */
	getDataPoliciesComboBox: function() {	
		if(!this.dataPoliciesComboBox) {
			var dataPoliciesStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idProperty: 'id',
				fields: [
					{name: 'id'},{name: 'name'}
				]
			});
		
			this.dataPoliciesComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('MetadataCreate.Data.Policy'),
				name: 'dataPolicy',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				store: dataPoliciesStore,
				editable: false,
				allowBlank: false,
				width: 330,
				displayField: 'name',
				valueField: 'id'
			});
			
			//Load Data into store.
			this.dataPoliciesComboBox.getStore().loadData(this.config.dataPolicies);
		}
		
		return this.dataPoliciesComboBox;
	},
	
	/**
	 * The templates combobox.
	 */
	getTemplatesComboBox: function() {
		var templatesStore = new Ext.data.JsonStore({
			// store configs
			autoDestroy: true,
			// reader configs
			idProperty: 'id',
			fields: [
				{name: 'id'},{name: 'title'}
			]
		});
		
		if(!this.templatesComboBox) {
			this.templatesComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('MetadataCreate.Template'),
				name: 'template',
				store: templatesStore,
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				editable: false,
				allowBlank: false,
				width: 330,
				displayField: 'title',
				valueField: 'id'
			});
			
			//Load Data into store.
			this.templatesComboBox.getStore().loadData(this.config.templates);
		}
		return this.templatesComboBox;
	},

	/**
	 * The categories combo box.
	 */
	getCategoriesComboBox: function() {	
		if(!this.categoriesComboBox) {
			var categoriesStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idProperty: 'id',
				fields: [
					{name: 'id'},{name: 'name'}
				]
			});
		
			this.categoriesComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('MetadataCreate.Category'),
				name: 'categories',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				store: categoriesStore,
				editable: false,
				allowBlank: false,
				width: 330,
				displayField: 'name',
				valueField: 'id'
			});
			
			//Load Data into store.
			this.categoriesComboBox.getStore().loadData(this.config.categories);
		}
		
		return this.categoriesComboBox;
	},

	/**
	 *	The JSON object submitted to the server.
	 */
	getMetadataCreationInfos: function() {
		var metadata = {};
		metadata.uuid = Openwis.i18n('MetadataCreate.URN.Prefix') + this.getUrnAuthTextField().getValue() + Openwis.i18n('MetadataCreate.URN.Middle') + this.getUrnIdTextField().getValue();
/*		metadata.dataPolicy = this.getDataPoliciesComboBox().getStore().getById(this.getDataPoliciesComboBox().getValue()).data;*/
		metadata.template = this.getTemplatesComboBox().getStore().getById(this.getTemplatesComboBox().getValue()).data;
		metadata.category = this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data;
		return metadata;
	}
});