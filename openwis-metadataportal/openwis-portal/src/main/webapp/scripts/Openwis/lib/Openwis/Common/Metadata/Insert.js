Ext.ns('Openwis.Common.Metadata');

Openwis.Common.Metadata.Insert = Ext.extend(Ext.Container, {
    
    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Common.Metadata.Insert.superclass.initComponent.apply(this, arguments);
        
        this.getInfosAndInitialize();
    },
    
    getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.metadata.insert.form',
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
		this.add(this.getUploadForm());
		
        this.doLayout();
    },
    
    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('MetadataInsert.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
    
    // Form for metadata insert.
	
	getUploadForm : function() {
 	    var the = this;
		//this.getUploadTablePanel().add(newFile);
 	    if(!this.uploadForm) {
 	        var newFile = new Ext.ux.form.FileUploadField(
    		    {
        		    xtype: 'fileuploadfield',
                    allowBlank : false,
                    buttonCfg: {
                        text: Openwis.i18n('Common.Btn.Browse')
                    },
                    fieldLabel: Openwis.i18n('MetadataInsert.Metadata'),
                    width: 360
                }
		    );
    		this.getFileUploadArray().push(
    		    newFile
    		);
    		this.getUploadTablePanel().add(new Ext.form.Label({
				text: Openwis.i18n('MetadataInsert.Metadata') + ':'
			}));
    		this.getUploadTablePanel().add(
    	        newFile
    	    );
    		this.getUploadTablePanel().add(
    		    new Ext.form.Label({text: ''})
    		);
     	    this.uploadForm = new Ext.FormPanel(
     	    {
     	        itemCls: 'formItems',
     	        fileUpload : true,
     	        border: false,
                errorReader: new Ext.data.XmlReader({
                        record : 'field',
                        success: '@success'
                    }, [
                        'id', 'msg'
                    ]
                ),
     	        items: [
     	            this.getUploadTablePanel(),
     	            new Ext.Button(this.getNewAction()),
                    this.getFileTypeRadioGroup(),
                    this.getStyleSheetComboBox(),
                    this.getValidationCombobox(),
		            this.getCategoriesComboBox()
                ]
                ,
     	        buttons : [ 
     	            {
         	            text : Openwis.i18n('Common.Btn.Insert'),
         	            scope : the,
         	            handler : function() {
         	                if (this.uploadForm.getForm().isValid()) {
         	                    this.uploadForm.getForm().submit({
         	                        url : configOptions.locService+ '/xml.metadata.insert.upload',
         	                        scope : this,
         	                        params: this.getMetadataInsertInfos(),
         	                        success : function(fp, action) {
         	                            //var jsonData = fp.errorReader.xmlData.childNodes[0].textContent;
         	                            var jsonData = fp.errorReader.xmlData.getElementsByTagName("message")[0].childNodes[0].nodeValue;
         	                            var result = Ext.decode(jsonData);
         	                            new Openwis.Common.Metadata.Report({
                                            lastResult: result
                                        });
         	                        },
         	                        failure : function(response) {
         	                            Openwis.Utils.MessageBox.displayInternalError();
         	                        }
         	                    });
         	                }
         	            }
     	            }		
     	       ]
     	    });
     	}
 	    return this.uploadForm;
 	},

    getUploadTablePanel: function() {
		if(!this.uploadTablePanel) {
			this.uploadTablePanel = new Ext.Panel({
			    layout:'table',
                layoutConfig: {
                    columns: 3
                },
                // defaults are applied to items, not the container
                defaults: {
                   style: {
                            width: '100%'
                   }
                },
                border: false
			});
        }
        return this.uploadTablePanel;
    },
	
	//	The new action.
    getNewAction: function() {
		if(!this.newAction) {
			this.newAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				scope: this,
				handler: function() {
				    if(this.getFileUploadArray().size() < Openwis.Conf.UPLOAD_SIZE)
				    {
				    var newFile = new Ext.ux.form.FileUploadField(
					    {
    					    xtype: 'fileuploadfield',
                            allowBlank : false,
                            fieldLabel: Openwis.i18n('MetadataInsert.Metadata'),
                            width: 360
                        }
					);
					var metadataLabel = new Ext.form.Label({
				        text: Openwis.i18n('MetadataInsert.Metadata') + ':'
			        });
					this.getFileUploadArray().push(
            		    newFile
            		);
            		this.getUploadTablePanel().add(
            		    metadataLabel
            		);
					this.getUploadTablePanel().add(
					    newFile
					);
					var newRemoveBtn = new Ext.Button(
					    new Ext.Action({
                            iconCls: 'icon-discard-fileUpload',
            				scope: this,
            				handler: function() {
            					this.getUploadTablePanel().remove(newFile);
            					this.getUploadTablePanel().remove(newRemoveBtn);
            					this.getFileUploadArray().remove(
                        		    newFile
                        		);
                        		this.getUploadTablePanel().remove(metadataLabel);
            					this.doLayout();
            				}
            			})
					);
					this.getUploadTablePanel().add(
					    newRemoveBtn
					);
					this.doLayout();
				    }
				    
				}
			});
		}
		return this.newAction;
	},
	
    getFileUploadArray: function() {
		if(!this.fileUploadArray) {
			this.fileUploadArray = new Array();
		}
		return this.fileUploadArray;
	},

    getFileTypeRadioGroup: function() {
		if(!this.fileTypeRadioGroup) {
			this.fileTypeRadioGroup = new Ext.form.RadioGroup({
				fieldLabel: Openwis.i18n('MetadataInsert.FileType'),
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
				boxLabel: Openwis.i18n('MetadataInsert.FileType.SingleFile'), 
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
				boxLabel: Openwis.i18n('MetadataInsert.FileType.MefFile'), 
				name: 'fileType', 
				inputValue: 'mef'
			});
		}
		return this.mefFileRadio;
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
				fieldLabel: Openwis.i18n('MetadataInsert.StyleSheet'),
				name: 'stylesheetName',
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
            this.styleSheetComboBox.setValue('NONE');
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
                name: 'validationModeCombobox',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200,
				fieldLabel: Openwis.i18n('MetadataInsert.Validate')
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
				fieldLabel: Openwis.i18n('MetadataInsert.Category'),
				name: 'categoryCombobox',
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
	getMetadataInsertInfos: function() {
		var metadataInsert = {};
		var files = new Array();
		Ext.each(this.getFileUploadArray(), function(fileUpload, index) {
            files.push(
		        fileUpload.getValue()
		    );
        });
        metadataInsert.files = files;
		metadataInsert.fileType = this.getFileTypeRadioGroup().getValue().inputValue;
		if (this.getStyleSheetComboBox().getValue() == 'NONE') {
		    metadataInsert.stylesheet = null;
		} else {
		    metadataInsert.stylesheet = this.getStyleSheetComboBox().getValue();
		}
		
		metadataInsert.validationMode = this.getValidationCombobox().getValue();
		metadataInsert.categoryId = this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data.id;
		metadataInsert.categoryName = this.getCategoriesComboBox().getStore().getById(this.getCategoriesComboBox().getValue()).data.name;
		return metadataInsert;
	}

});