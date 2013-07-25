Ext.ns('Openwis.Admin.Thesauri');

Openwis.Admin.Thesauri.EditElement = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('ThesauriManagement.EditElement.Title'),
			width:650,
			height:720,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.Thesauri.EditElement.superclass.initComponent.apply(this, arguments);

		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var params = this.params;
		var keywordSelected = this.keywordSelected;
		var thesaurus = this.thesaurus;
		var thesaurusType = this.thesaurusType;
		var mode = this.mode;
		var isUpdate = this.isUpdate;
		if(!this.newBnList) {
			this.newBnList = [];
		}
		if(!this.deleteBnList) {
			this.deleteBnList = [];
		}

		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.thesaurus.editElement',
			params: this.getKeywordInfos(),
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize(config);
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
	initialize: function(config) {

		//-- Create form panel.
		this.add(this.getEditElementFormPanel(config));
	
		//-- Add buttons.
		if(this.isEdition())
		{
			this.addButton(new Ext.Button(this.getSubmitEltAction()));
		}
		this.addButton(new Ext.Button(this.getCancelAction()));

		this.doLayout();
		this.show();
	},

	getEditElementFormPanel: function(config) {
		if(!this.editElementFormPanel) {
			this.editElementFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 80,
				style : {
		               padding: '5px'
	            }
			});
			if (config.keywordRef.edition)
			{
				this.getIdentifierTextField().setValue(config.keywordRef.relativeCode);
				this.getLabelTextField().setValue(config.keywordRef.value);
				this.getDefinitionTextField().setValue(config.keywordRef.definition);
				this.editElementFormPanel.add(this.getIdentifierTextField());
				this.editElementFormPanel.add(this.getLabelTextField());
				this.editElementFormPanel.add(this.getDefinitionTextField());
			}
			else
			{
				this.getIdentifierDisplayField().setValue(config.keywordRef.relativeCode);
				this.getLabelDisplayField().setValue(config.keywordRef.value);
				this.getDefinitionDisplayField().setValue(config.keywordRef.definition);
				this.editElementFormPanel.add(this.getIdentifierDisplayField());
				this.editElementFormPanel.add(this.getLabelDisplayField());
				this.editElementFormPanel.add(this.getDefinitionDisplayField());
			}

			if (this.thesaurusType == 'place')
			{
				this.getNBCoordinateTextField().setValue(config.keywordRef.coordNorth);
				this.getWBCoordinateTextField().setValue(config.keywordRef.coordWest);
				this.getEBCoordinateTextField().setValue(config.keywordRef.coordEast);
				this.getSBCoordinateTextField().setValue(config.keywordRef.coordSouth);
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.createLabel(Openwis.i18n('ThesauriManagement.EditElement.NBCoord')));
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.getNBCoordinateTextField());
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.createLabel(Openwis.i18n('ThesauriManagement.EditElement.WBCoord')));
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.createLabel(Openwis.i18n('ThesauriManagement.EditElement.EBCoord')));
				this.getCoordPanel().add(this.getWBCoordinateTextField());
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.getEBCoordinateTextField());
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.createLabel(Openwis.i18n('ThesauriManagement.EditElement.SBCoord')));
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.createDummy());
				this.getCoordPanel().add(this.getSBCoordinateTextField());
				this.getCoordPanel().add(this.createDummy());
				this.editElementFormPanel.add(this.getCoordPanel());
			}

			Ext.each(config.broadNarrListDTO, function(broadNarrListDTO, index) {
				if (broadNarrListDTO.keywordType == 'broader')
				{
					this.editElementFormPanel.add(this.getBroadGrid(config, broadNarrListDTO));
				}
				else if (broadNarrListDTO.keywordType == 'narrower')
				{	
					this.editElementFormPanel.add(this.getNarrGrid(config, broadNarrListDTO));
				}
	        }, this);
		}
		return this.editElementFormPanel;
	},

	getCoordPanel: function() {
		if(!this.coordPanel) {
			this.coordPanel = new Ext.Panel({
				layout:'table',
				style : {
		               padding: '5px'
	            },
                layoutConfig: {
                    columns: 3,
                    tableAttrs: {
                        style: {
                            width: '100%',
                            padding: '20px'
                        }
                    }
                }
			});
		}
		return this.coordPanel;
	},

	createLabel: function(label) {
        return new Openwis.Utils.Misc.createLabel(label);
    },

	createDummy: function() {
        return new Openwis.Utils.Misc.createDummy();
    },

	/**
	 * The id df.
	 */
	getIdentifierDisplayField: function() {
		if(!this.identifierDisplayField) {
			this.identifierDisplayField = new Ext.form.DisplayField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.Id'),
				name: 'id',
				width: 150
			});
		}
		return this.identifierDisplayField;
	},
	
	/**
	 * The label df.
	 */
	getLabelDisplayField: function() {
		if(!this.labelDisplayField) {
			this.labelDisplayField = new Ext.form.DisplayField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.Label'),
				name: 'label',
				width: 150
			});
		}
		return this.labelDisplayField;
	},

	/**
	 * The definition df.
	 */
	getDefinitionDisplayField: function() {
		if(!this.definitionDisplayField) {
			this.definitionDisplayField = new Ext.form.DisplayField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.Definition'),
				name: 'def',
				width: 150
			});
		}
		return this.definitionDisplayField;
	},

	/**
	 * The id tf.
	 */
	getIdentifierTextField: function() {
		if(!this.identifierTextField) {
			this.identifierTextField = new Ext.form.NumberField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.Id'),
				allowDecimals: false,
				allowNegative: false,
				name: 'id',
				allowBlank : false,
				width: 150
			});
		}
		return this.identifierTextField;
	},

	/**
	 * The label tf.
	 */
	getLabelTextField: function() {
		if(!this.labelTextField) {
			this.labelTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.Label'),
				name: 'label',
				allowBlank : false,
				width: 150
			});
		}
		return this.labelTextField;
	},

	/**
	 * The definition tf.
	 */
	getDefinitionTextField: function() {
		if(!this.definitionTextField) {
			this.definitionTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.Definition'),
				name: 'def',
				allowBlank : false,
				width: 150
			});
		}
		return this.definitionTextField;
	},

	/**
	 * The nbc tf.
	 */
	getNBCoordinateTextField: function() {
		if(!this.nBCoordinateTextField) {
			this.nBCoordinateTextField = new Ext.form.TextField({
				name: 'nbc',
				allowBlank : !this.isEdition(),
				width: 100
			});
		}
		return this.nBCoordinateTextField;
	},

	/**
	 * The wbc tf.
	 */
	getWBCoordinateTextField: function() {
		if(!this.wBCoordinateTextField) {
			this.wBCoordinateTextField = new Ext.form.TextField({
				name: 'wbc',
				allowBlank : !this.isEdition(),
				width: 100
			});
		}
		return this.wBCoordinateTextField;
	},

	/**
	 * The ebc tf.
	 */
	getEBCoordinateTextField: function() {
		if(!this.eBCoordinateTextField) {
			this.eBCoordinateTextField = new Ext.form.TextField({
				name: 'ebc',
				allowBlank : !this.isEdition(),
				width: 100
			});
		}
		return this.eBCoordinateTextField;
	},

	/**
	 * The sbc tf.
	 */
	getSBCoordinateTextField: function() {
		if(!this.sBCoordinateTextField) {
			this.sBCoordinateTextField = new Ext.form.TextField({
				name: 'sbc',
				allowBlank : !this.isEdition(),
				width: 100
			});
		}
		return this.sBCoordinateTextField;
	},

	getBroadGrid: function(config, broadNarrListDTO) {
		if(!this.broadGrid) {
			this.broadGrid = new Ext.grid.GridPanel({
				store: new Ext.data.JsonStore({
					autoDestroy: true,
	    			idProperty: 'id',
	    			fields: [
						{
							name:'id'
						},{
							name:'value',
							sortType: Ext.data.SortTypes.asUCString
						}
					],
					sortInfo: {
					    field: 'value',
					    direction: 'ASC'
					}
	    		}),
				style : {
		               padding: '5px'
	            },
				id: 'broadGrid',
				height: 120,
				border: true,
				loadMask: true,
				columns: [
				    {id:'value', header:broadNarrListDTO.keywordType + " " + Openwis.i18n('ThesauriManagement.EditElement.Term'), dataIndex:'value', sortable: true, width: 100}
				],
				autoExpandColumn: 'value',
				listeners: { 
					afterrender: function (grid) {
					   grid.loadMask.show();
					   grid.getStore().loadData(broadNarrListDTO.keywordListDTO);
					}
				},
				sm: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							this.grid.ownerCt.ownerCt.getDeleteElementAction().setDisabled(sm.getCount() != 1);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                        	this.grid.ownerCt.ownerCt.getDeleteElementAction().setDisabled(sm.getCount() != 1);
                        }
					}
				})
			});
			
			if (config.keywordRef.edition)
			{
				this.broadGrid.addButton(new Ext.Button(this.getAddElementAction()));

				this.broadGrid.addButton(new Ext.Button(this.getDeleteElementAction()));
			}
		}
		return this.broadGrid;
	},
	
	getDeleteElementAction: function() {
		if (!this.deleteElementAction) {
		this.deleteElementAction = new Ext.Action({
					disabled: true,
					text: Openwis.i18n('Common.Btn.Delete'),
					scope: this,
					handler: function() {
						//Get the element to delete.
						var selectedRec = this.getBroadGrid().getSelectionModel().getSelected();
						this.getBroadGrid().getStore().remove(selectedRec);
		            	this.newBnList.remove(selectedRec);
		            	this.deleteBnList.push(selectedRec);
		            	this.getBroadGrid().getView().refresh();
		            	this.deleteElementAction.setDisabled(true);
		            	this.doLayout();
					}
				});
		}
		return this.deleteElementAction;
	},

	getAddElementAction: function() {
		if (!this.addElementAction) {
			this.addElementAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Add'),
				scope: this,
				handler: function() {
					new Openwis.Admin.Thesauri.ViewEdit({
						title: Openwis.i18n('ThesauriManagement.EditElement.AddBN'),
						thesaurusType: this.thesaurus,
						mode : 'edit',
						isBn :  true,
						params: this.params,
						listeners: {
							viewEditBnSelection: function(records) {
	    			            for (var i=0; i<records.length; i++) {
	    			            	var record =  new Ext.data.Record(records[i].json);
	    			            	this.getBroadGrid().getStore().add(record);
	    			            	this.newBnList.push(record);
	    			            	this.getBroadGrid().getView().refresh();
	    			            	this.doLayout();
	    			            }
							},
							scope: this
						}
					});
				}
			});
		}
		return this.addElementAction;
	},

	getNarrGrid: function(config, broadNarrListDTO) {
			var narrGrid = new Ext.grid.GridPanel({
				store: new Ext.data.JsonStore({
					autoDestroy: true,
	    			idProperty: 'id',
	    			fields: [
						{
							name:'id'
						},{
							name:'value',
							sortType: Ext.data.SortTypes.asUCString
						}
					],
					sortInfo: {
					    field: 'value',
					    direction: 'ASC'
					}
	    		}),
				style : {
		               padding: '5px'
	            },
				id: 'narrGrid',
				height: 160,
				border: true,
				loadMask: true,
				columns: [
				    {id:'value', header:broadNarrListDTO.keywordType + " " + Openwis.i18n('ThesauriManagement.EditElement.Term'), dataIndex:'value', sortable: true, width: 100}
				],
				autoExpandColumn: 'value',
				listeners: { 
					afterrender: function (grid) {
					   grid.loadMask.show();
					   grid.getStore().loadData(broadNarrListDTO.keywordListDTO);
					}
				}
			});
		return narrGrid;
	},

	/**
	 * The Submit element action.
	 */
	getSubmitEltAction: function() {
		if(!this.submitEltAction) {
			this.submitEltAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Submit'),
				scope: this,
				handler: function() {
					var urlServ = '/xml.thesaurus.addElement';
					if (this.isUpdate)
					{
						urlServ = '/xml.thesaurus.updateElement';
					}
					if(this.getEditElementFormPanel().getForm().isValid()) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService + urlServ,
							params: this.getEltInfos(),
							listeners: {
								success: function(config) {
									this.fireEvent("elementSubmitted");
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
		return this.submitEltAction;
	},

	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Close'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	/**
	 * Returns true if it is an edition, false otherwise.
	 */
	isEdition: function() {
		return (this.mode == 'local');
	},
	
	/**
	 *	The JSON object submitted to the server.
	 */
    getKeywordInfos: function() {
		var keywordInfos = {};
		if (this.keywordSelected != null)
		{
			keywordInfos.id = this.keywordSelected.id;
			keywordInfos.value = this.keywordSelected.value;
			keywordInfos.lang = this.keywordSelected.lang;
			keywordInfos.definition = this.keywordSelected.definition;
			keywordInfos.code = this.keywordSelected.code;
			keywordInfos.coordEast = this.keywordSelected.coordEast;
			keywordInfos.coordWest = this.keywordSelected.coordWest;
			keywordInfos.coordSouth = this.keywordSelected.coordSouth;
			keywordInfos.coordNorth = this.keywordSelected.coordNorth;
			keywordInfos.thesaurus = this.keywordSelected.thesaurus;
		}
		else
		{
			keywordInfos.thesaurus = this.thesaurus;
		}
		keywordInfos.edition = this.isEdition();
		return keywordInfos;
	},

    getEltInfos: function(selectedRec) {
		var eltsInfos = {};
		if (this.isUpdate)
		{
			eltsInfos.code = this.keywordSelected.code;
		}
		eltsInfos.id = this.getIdentifierTextField().getValue();
		eltsInfos.value = this.getLabelTextField().getValue();
		eltsInfos.definition = this.getDefinitionTextField().getValue();
		eltsInfos.coordSouth = this.getSBCoordinateTextField().getValue();
		eltsInfos.coordEast = this.getEBCoordinateTextField().getValue();
		eltsInfos.coordWest = this.getWBCoordinateTextField().getValue();
		eltsInfos.coordNorth = this.getNBCoordinateTextField().getValue();
//		if (this.getNarrowerComboBox().getStore().getById(this.getNarrowerComboBox().getValue()) != null)
//		{
//			eltsInfos.narrower = this.getNarrowerComboBox().getStore().getById(this.getNarrowerComboBox().getValue()).data;
//		}
		var newBnArray = new Array();
		Ext.each(this.newBnList, function(bn, index) {
			var n = {};
			n.code = bn.data.code;
			newBnArray.push(n);
        });
		var bnList = {};
		bnList.keywordListDTO = newBnArray;
		eltsInfos.broadNarrListDTO = bnList;
		var delBnArray = new Array();
		Ext.each(this.deleteBnList, function(bn, index) {
			var n = {};
			n.code = bn.json.code;
			delBnArray.push(n);
        });
		var delBnList = {};
		delBnList.keywordListDTO = delBnArray;
		eltsInfos.delBroadNarrListDTO = delBnList;
		eltsInfos.thesaurus = this.thesaurus;
		return eltsInfos;
	}
});