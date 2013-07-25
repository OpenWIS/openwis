Ext.ns('Openwis.Common.Metadata');

Openwis.Common.Metadata.BatchImport = Ext.extend(Ext.Container, {
    
    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Common.Metadata.BatchImport.superclass.initComponent.apply(this, arguments);
        
        this.getInfosAndInitialize();
    },
    
    getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.metadata.batchimport.form',
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
	
    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //Create Metadata form.
        this.getBatchImportMetadataFormPanel().add(this.getMetadataDirectoryTextField());
		this.getBatchImportMetadataFormPanel().add(this.getFileTypeRadioGroup());
		this.getBatchImportMetadataFormPanel().add(this.getStyleSheetComboBox());
		this.getBatchImportMetadataFormPanel().add(this.getValidationCombobox());
		this.getBatchImportMetadataFormPanel().add(this.getCategoriesComboBox());

		this.add(this.getBatchImportMetadataFormPanel());
		
		this.doLayout();
    },

    initializeRes: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //Create Metadata form.
        this.getBatchImportMetadataFormPanel().add(this.getResultatLabel());

		this.add(this.getBatchImportMetadataFormPanel());
		
		this.doLayout();
    },

    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('MetadataBatchImport.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
    
    //-- Form for metadata creation.
	
	/**
	 *	The form panel.
	 */
	getBatchImportMetadataFormPanel: function() {
		if(!this.batchImportMetadataFormPanel) {
			this.batchImportMetadataFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				border: false,
				buttons:
				[
					{
						text: Openwis.i18n('Common.Btn.Upload'),
						handler: function(btn, e) {
							if(this.getBatchImportMetadataFormPanel().getForm().isValid()) {
								var saveHandler = new Openwis.Handler.Save({
									url: configOptions.locService+ '/xml.metadata.batchimport',
									params: this.getMetadataBatchImportInfos(),
									listeners: {
										success: function(config) {
										    this.configRes = config;
											this.initializeRes();
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
		return this.batchImportMetadataFormPanel;
	},

    getResultatLabel: function() {
		if(!this.resultatLabel) {
			this.resultatLabel = new Ext.form.Label({
				text: this.configRes,
				labelStyle: 'font-weight:bold;'
			});
		}
		return this.resultatLabel;
	},

    getFileTypeRadioGroup: function() {
		if(!this.fileTypeRadioGroup) {
			this.fileTypeRadioGroup = new Ext.form.RadioGroup({
				fieldLabel: Openwis.i18n('MetadataBatchImport.FileType'),
				items:
				[
					this.getSingleFileRadio(),
					this.getMefFileRadio()
				]
			});
		}
		return this.fileTypeRadioGroup;
	},
	
	getSingleFileRadio: function() {
		if(!this.singleFileRadio) {
			this.singleFileRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('MetadataBatchImport.FileType.SingleFile'), 
				name: 'fileType', 
				inputValue: 'single',
				checked: true
			});
		}
		return this.singleFileRadio;
	},
	
	getMefFileRadio: function() {
		if(!this.mefFileRadio) {
			this.mefFileRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('MetadataBatchImport.FileType.MefFile'), 
				name: 'fileType', 
				inputValue: 'mef'
			});
		}
		return this.mefFileRadio;
	},

	getMetadataDirectoryTextField: function() {
		if(!this.metadataDirectoryTextField) {
			this.metadataDirectoryTextField = new Ext.form.TextField({
				fieldLabel : Openwis.i18n('MetadataBatchImport.Directory'),
          		allowBlank : false,
          		width: 330
			});
		}
		return this.metadataDirectoryTextField;
	},

	/**
	 * The style sheet combo box.
	 */
	getStyleSheetComboBox: function() {	
		if(!this.styleSheetComboBox) {
			var styleSheetStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idProperty: 'id',
				fields: [
					{name: 'id'},{name: 'name'}
				]
			});
		
			this.styleSheetComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('MetadataBatchImport.StyleSheet'),
				name: 'stylesheet',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				store: styleSheetStore,
				editable: false,
				allowBlank: false,
				width: 330,
				value: 'NONE',
				displayField: 'name',
				valueField: 'id'
			});
			
			//Load Data into store.
			this.styleSheetComboBox.getStore().loadData(this.config.styleSheet);
			// Add an empty value for overridden stylesheet value reset. 
            this.styleSheetComboBox.getStore().insert(0, 
                [new Ext.data.Record({id:'NONE',  name:Openwis.i18n('Common.List.None')})]);
		}
		
		return this.styleSheetComboBox;
	},

	/**
	 * The validation combo box.
	 */
	getValidationCombobox: function() {
        if(!this.validationCombobox) {
            this.validationCombobox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['NONE', Openwis.i18n('Metadata.Validation.None')], 
					    ['XSD_ONLY', Openwis.i18n('Metadata.Validation.XsdOnly')], 
					    ['FULL',     Openwis.i18n('Metadata.Validation.Full')] 
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: 'NONE',
                name: 'validationMode',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200,
				fieldLabel: Openwis.i18n('MetadataBatchImport.Validate')
            });
        }
        return this.validationCombobox;
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
				fieldLabel: Openwis.i18n('MetadataBatchImport.Category'),
				name: 'category',
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
	getMetadataBatchImportInfos: function() {
		var metadataBatchImport = {};
		metadataBatchImport.directory = this.getMetadataDirectoryTextField().getValue();
		metadataBatchImport.fileType = this.getFileTypeRadioGroup().getValue().inputValue;
		if (this.getStyleSheetComboBox().getValue() == 'NONE') {
		    metadataBatchImport.stylesheet = null;
		} else {
		    metadataBatchImport.stylesheet = this.getStyleSheetComboBox().getStore().getById(this.getStyleSheetComboBox().getValue()).data;
		}
		metadataBatchImport.validationMode = this.getValidationCombobox().getValue();
		metadataBatchImport.category = this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data;
		return metadataBatchImport;
	}
});