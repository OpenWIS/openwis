Ext.ns('Openwis.Admin.Thesauri');

Openwis.Admin.Thesauri.ViewEdit = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: this.title,
			width:650,
			height:500,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.Thesauri.ViewEdit.superclass.initComponent.apply(this, arguments);

		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var params = this.params;
		var thesaurusType = this.thesaurusType;
		var mode = this.mode;
		var isBn = this.isBn;
		
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.thesaurus.list',
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
	
		//-- Create form panel.
		this.add(this.getSearchFormPanel());
		this.add(this.getSearchResultPanel());
		
		//-- Add buttons.
		if (this.isBn)
		{
			this.addButton(new Ext.Button(this.getSubmitAction()));
		}
		this.addButton(new Ext.Button(this.getCancelAction()));

		this.show();
	},
	
	getSearchFormPanel: function() {
		if(!this.searchFormPanel) {
			this.searchFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 80,
				style : {
		               padding: '5px'
	            }
			});
			this.searchFormPanel.add(this.getSearchInfo());
			this.searchFormPanel.add(this.getKeyWordsTextField());
			this.searchFormPanel.add(this.getSearchRadioGroup());
			this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
			if (this.isEdition())
			{
				this.searchFormPanel.addButton(new Ext.Button(this.getAddElementAction()));
			}
		}
		return this.searchFormPanel;
	},

	getSearchInfo: function() {
		if(!this.searchInfo) {
			this.searchInfo = new Ext.Container({
		        html: Openwis.i18n('ThesauriManagement.ViewEdit.SearchInfo'),
		        border: false,
		        cls: 'infoMsg',
		        style: {
		            margin: '0px 0px 5px 0px'
		        }
		   });
		}
		return this.searchInfo;
	},

	getSearchResultPanel: function() {
		if(!this.searchResultPanel) {
			this.searchResultPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 120,
				border: false,
				style : {
		               padding: '5px'
	            }
			});
			this.searchResultPanel.add(this.getSearchResultDisplayField());
			this.searchResultPanel.add(this.getSearchResultGrid());
		}
		return this.searchResultPanel;
	},

	/**
	 * The text field for the KeyWords.
	 */
	getKeyWordsTextField: function() {
		if(!this.keyWordsTextField) {
			this.keyWordsTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('ThesauriManagement.ViewEdit.KeyWords'),
				name: 'name',
				width: 150
			});
		}
		return this.keyWordsTextField;
	},
	
	getSearchRadioGroup: function() {
		if(!this.searchRadioGroup) {
			this.searchRadioGroup = new Ext.form.RadioGroup({
				//fieldLabel: Openwis.i18n('MetadataInsert.FileType'),
				items:
				[
					this.getStartWithRadio(),
					this.getContainsRadio(),
					this.getExactTermRadio()
				]
			});
		}
		return this.searchRadioGroup;
	},
	
	getStartWithRadio: function() {
		if(!this.startWithRadio) {
			this.startWithRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('ThesauriManagement.ViewEdit.StartWith'), 
				name: 'searchType', 
				inputValue: 0
			});
		}
		return this.startWithRadio;
	},
	
	getContainsRadio: function() {
		if(!this.containsRadio) {
			this.containsRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('ThesauriManagement.ViewEdit.Contains'), 
				name: 'searchType', 
				inputValue: 1,
				checked: true
			});
		}
		return this.containsRadio;
	},
	
	getExactTermRadio: function() {
		if(!this.exactTermRadio) {
			this.exactTermRadio = new Ext.form.Radio({
				boxLabel: Openwis.i18n('ThesauriManagement.ViewEdit.ExactTerm'), 
				name: 'searchType', 
				inputValue: 2
			});
		}
		return this.exactTermRadio;
	},

	/**
	 * The Add element action.
	 */
	getAddElementAction: function() {
		if(!this.addElementAction) {
			this.addElementAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Add'),
				scope: this,
				handler: function() {
					new Openwis.Admin.Thesauri.EditElement({
						thesaurus: this.params.thesaurus,
						thesaurusType: this.thesaurusType,
						params : this.params,
						mode: this.mode,
						isUpdate: false,
						listeners: {
							elementSubmitted: function() {
                                this.getSearchAction().execute();
                            },
                            scope: this
                        }
					});
				}
			});
		}
		return this.addElementAction;
	},

	/**
	 * The searchResult df.
	 */
	getSearchResultDisplayField: function() {
		if(!this.searchResultDisplayField) {
			this.searchResultDisplayField = new Ext.form.DisplayField({
				fieldLabel: Openwis.i18n('ThesauriManagement.EditElement.NbTerms'),
				name: 'searchResultDisplayField',
				width: 150
			});
			this.searchResultDisplayField.setVisible(false);
		}
		return this.searchResultDisplayField;
	},

	getSearchResultGrid: function() {
		if(!this.searchResultGrid) {
			this.searchResultGrid = new Ext.grid.GridPanel({
				store: new Ext.data.JsonStore({
					autoDestroy: true,
	    			idProperty: 'id',
	                fields: [
	                    {name: 'id'},
	                    {name: 'value'},
	                    {name: 'thesaurus'},
	                    {name: 'code'}
	    			]
	    		}),
				style : {
		               padding: '5px'
	            },
				id: 'searchResultGrid',
				height: 260,
				border: true,
				loadMask: true,
				columns: [
				    {id:'value', header:Openwis.i18n('ThesauriManagement.ViewEdit.Label'), dataIndex:'value', sortable: true, width: 100}
				],
				autoExpandColumn: 'value',
				sm: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							editElementAction.setDisabled(sm.getCount() != 1);
							deleteElementAction.setDisabled(sm.getCount() == 0);
							this.getSubmitAction().setDisabled(sm.getCount() == 0);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                        	editElementAction.setDisabled(sm.getCount() != 1);
                        	deleteElementAction.setDisabled(sm.getCount() == 0);
                        	this.getSubmitAction().setDisabled(sm.getCount() == 0);
                        },
        				scope: this
					}
				})
			});

			var viewEditButtonLabel = Openwis.i18n('Common.Btn.View');
			if (this.isEdition())
			{
				viewEditButtonLabel = Openwis.i18n('Common.Btn.Edit');
			}

			var editElementAction = new Ext.Action({
				disabled: true,
				text: viewEditButtonLabel,
				scope: this,
				handler: function() {
					//Get the element to edit.
					var selectedRec = this.getSearchResultGrid().getSelectionModel().getSelected();
					var oldParams = this.params;
					//var params = this.getViewEditThesaurusInfos(selectedRec.json);
					new Openwis.Admin.Thesauri.EditElement({
						params : oldParams,
						thesaurus: this.params.thesaurus,
						thesaurusType: this.thesaurusType,
						mode: this.mode,
						isUpdate: true,
						keywordSelected: selectedRec.json,
						listeners: {
							elementSubmitted: function() {
                                this.getSearchAction().execute();
                            },
                            scope: this
                        }
					});
				}
			});

			var deleteElementAction = new Ext.Action({
				disabled: true,
				text:Openwis.i18n('Common.Btn.Delete'),
				scope: this,
				handler: function() {
					//Get the keyword ids to delete.
					var selection = this.getSearchResultGrid().getSelectionModel().getSelections();
					var params = {keywordListDTO: []};
					Ext.each(selection, function(item, index, allItems) {
						params.keywordListDTO.push({thesaurus: item.get('thesaurus'), code: item.get('code')});
					}, this);
					
					var msg = null;
					//Invoke the remove handler to remove the elements by an ajax request.
					var removeHandler = new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.thesaurus.deleteElement',
						params: params,
						confirmMsg: msg,
						listeners: {
							success: function() {
								this.getSearchAction().execute();
								editElementAction.setDisabled(true);
								deleteElementAction.setDisabled(true);
							},
							scope: this
						}
					});
					removeHandler.proceed();
				}
			});

			if (!this.isBn)
			{
				if (this.isEdition())
				{
					this.searchResultGrid.addButton(new Ext.Button(this.getAddElementAction()));
					this.searchResultGrid.addButton(new Ext.Button(deleteElementAction));
				}
				this.searchResultGrid.addButton(new Ext.Button(editElementAction));
			}
			this.searchResultGrid.setVisible(false);
		}
		return this.searchResultGrid;
	},

	/**
	 * The Search action.
	 */
	getSearchAction: function() {
		if(!this.searchAction) {
			this.searchAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Search'),
				scope: this,
				handler: function() {
					if(this.getSearchFormPanel().getForm().isValid()) {
						var getHandler = new Openwis.Handler.Get({
							url: configOptions.locService+ '/xml.thesaurus.viewEdit.search',
							params: this.getViewEditSearchInfos(),
							listeners: {
								success: function(config) {
									this.getSearchResultDisplayField().setValue(config.size());
									this.getSearchResultDisplayField().setVisible(true);
									this.getSearchResultGrid().getStore().loadData(config);
									this.getSearchResultGrid().setVisible(true);
									this.doLayout();
								},
								failure: function(config) {
				        			alert("failed");
								},
								scope: this
							}
						});
						getHandler.proceed();
					}
				}
			});
		}
		return this.searchAction;
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
	 * The Submit action.
	 */
	getSubmitAction: function() {
		if(!this.submitAction) {
			this.submitAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Submit'),
				scope: this,
				disabled: true,
				handler: function() {
					var selectionBn = this.getSearchResultGrid().getSelectionModel().getSelections();
					this.fireEvent("viewEditBnSelection", selectionBn);
					this.close();
				}
			});
		}
		return this.submitAction;
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
    getViewEditSearchInfos: function(selectedRec) {
		var viewEditSearchInfos = {};
		if (this.getKeyWordsTextField().getValue().trim() == '')
		{
			viewEditSearchInfos.keyword = '*';
		}
		else
		{
			viewEditSearchInfos.keyword = this.getKeyWordsTextField().getValue();
		}
		viewEditSearchInfos.thesauri = this.params.thesaurus;
		viewEditSearchInfos.typeSearch = this.getSearchRadioGroup().getValue().inputValue;
		viewEditSearchInfos.maxResults = "200";
		return viewEditSearchInfos;
	}
});