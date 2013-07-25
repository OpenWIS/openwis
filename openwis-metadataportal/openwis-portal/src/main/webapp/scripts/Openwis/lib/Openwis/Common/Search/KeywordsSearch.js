Ext.ns('Openwis.Common.Search');

Openwis.Common.Search.KeywordsSearch = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('ThesauriManagement.Search.Keywords'),
			width:650,
			height:520,
			modal: true,
			closeAction:'close'
		});
		Openwis.Common.Search.KeywordsSearch.superclass.initComponent.apply(this, arguments);

		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.thesaurus.getList',
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
		this.add(this.getKeywordsFormPanel());
		this.add(this.getKeywordsSelectPanel());

		//-- Add buttons.
		this.addButton(new Ext.Button(this.getAddAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));

		this.show();
		this.initKeywordsSelected();
	},

	initKeywordsSelected: function() {
		// add specific css
		this.getKeywordsMultiSelector().fs.addClass('multiSelectFsKeywords');
		this.getKeywordsMultiSelector().fs.body.addClass('multiSelectFsBodyKeywords');
	    this.getKeywordsMultiSelector().view.addClass('multiSelectViewKeywords');
	    // add keywords
		var initialKeywords = this.keywordsFromTf;
		if (initialKeywords.length > 0)
		{
			initialKeywords = initialKeywords.split(' | ');
			var records = [];
		    for (var i=0; i<initialKeywords.length; i++) {
		    	textSelected = initialKeywords[i];
		    	record =  new Ext.data.Record({text:textSelected});
		    	records.push(record);
		    }
		    for (var i=0; i<records.length; i++) {
	            record = records[i];
	            this.getKeywordsMultiSelector().view.store.add(record);
	        }
		    this.getKeywordsMultiSelector().view.refresh();
		}
		this.doLayout();
	},
    
	getKeywordsFormPanel: function() {
		if(!this.keywordsFormPanel) {
			this.keywordsFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 80,
				style : {
		               padding: '5px'
	            }
			});
			this.keywordsFormPanel.add(this.getThesaurusComboBox());
		}
		return this.keywordsFormPanel;
	},

	getKeywordsSelectPanel: function() {
		if(!this.keywordsSelectPanel) {
			this.keywordsSelectPanel = new Ext.Panel({
				border: false,
				autoScroll: true,
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
			this.keywordsSelectPanel.add(this.getKeywordsTreeFieldSet());
			this.keywordsSelectPanel.add(this.getKeywordsButtonsPanel());
			this.keywordsSelectPanel.add(this.getKeywordsMultiSelector());
		}
		return this.keywordsSelectPanel;
	},

	getKeywordsTreeFieldSet: function() {
		if(!this.keywordsTreeFieldSet) {
			this.keywordsTreeFieldSet = new Ext.form.FieldSet({
	             title: Openwis.i18n('ThesauriManagement.Search.AvailableKeywords'),
	             width: 275,
	             height: 340
	        });
		}
		return this.keywordsTreeFieldSet;
	},

	/**
	 * The Thesaurus combo box.
	 */
	getThesaurusComboBox: function() {	
		if(!this.thesaurusComboBox) {
			var thesaurusStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				idProperty: 'key',
				// reader configs
				fields: [
					{name: 'key'}, {name: 'dname'}, {name: 'fname'} ,{name: 'type'}
				],
				listeners: {
    			    load: function(store, records, options) {
            			// Add any thesaurus value
                        // store.insert(0, [new Ext.data.Record({key: 'ANY', dname: '', fname: Openwis.i18n('ThesauriManagement.Search.AnyThesaurus'), type: ''})]);
                        // Add an empty value
                        //store.insert(0, [new Ext.data.Record({key: 'EMPTY', dname: '', fname: '', type: ''})]);
        			},
        			scope: this
    			}
			});

			this.thesaurusComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('ThesauriManagement.Search.Thesaurus'),
				name: 'thesaurus',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				store: thesaurusStore,
				editable: false,
				width: 330,
				displayField: 'fname',
				valueField: 'key',
				value: '',
				listeners:{
			         scope: this,
			         'select': function (grid) {
			        		var thesaurusSelected = this.thesaurusComboBox.getStore().getById(this.thesaurusComboBox.getValue()).data;
			     			// set the root node
	     					var root = new Ext.tree.AsyncTreeNode({
	     					    text: thesaurusSelected.fname,
	     					    draggable: false,
	     					    id: '0'
	     					});
	     					if(!this.loader) {
	     						this.loader = new Openwis.Data.JeevesJsonTreeLoader({
	     				    		//preloadChildren: true,
	     				        	//requestMethod: 'GET',
	     				            url:configOptions.locService+ '/xml.thesaurus.getKeywordsNode',
	     				          	//baseParams: 'les params si necessaire',
	     				            createNode: function(attr){
	     				        	    attr.iconCls = 'item-agents';
	     				        	    attr.leaf = attr.keyword.leaf;
	     				        	    attr.text = attr.keyword.value;
	     				        	    return Ext.tree.TreeLoader.prototype.createNode.call(this, attr);
     				        	    },
	     						    listeners: {
	     								scope: this
	     							}
	     					    });
	     					}

	     					this.loader.on("beforeload", function(treeLoader, node) {
	     						this.loader.baseParams.nodeId = node.attributes.id;
	     						if (node.attributes.id != 0)
	     						{
	     							//this.loader.baseParams.nodeUri = node.attributes.keyword.value;
		     						this.loader.baseParams.code = node.attributes.keyword.relativeCode;
	     						}
	     						this.loader.baseParams.thesRef = this.getThesaurusComboBox().getValue();
	     				    }, this);

	     					if(!this.keywordsTreePanel) {
	     						this.keywordsTreePanel = new Ext.tree.TreePanel({
		     						width: 250,
		     						height: 300,
		     						autoScroll: true,
		     						bodyCssClass: 'treeForceVisible',
		     						containerScroll: true,
		     					    animate: true,
		     					    enableDD: false,
		     					    loader: this.loader,
		     					    selModel: new Ext.tree.MultiSelectionModel(),
		     					    rootVisible: true
		     					});
	     						this.keywordsTreePanel.setRootNode(root);
	     						this.getKeywordsTreeFieldSet().add(this.keywordsTreePanel);			     					}
	     					else
     						{
     							this.keywordsTreePanel.setRootNode(root);
     						}
	     					this.doLayout();
					}
			    }

			});
			
			//Load Data into store.
			this.thesaurusComboBox.getStore().loadData(this.config.thesaurusListDTO);
		}
		
		return this.thesaurusComboBox;
	},

	getKeywordsMultiSelector: function(data) {
        if(!this.isForm) {
            var ds = new Ext.data.JsonStore({
                    // store configs
                    autoDestroy: true,
                    // reader configs
                    idProperty: 'text',
                    fields: [
                        {name: 'text'}, {name: 'keyword'}
                    ]
                });

            this.isForm = new Ext.ux.form.MultiSelect(Ext.applyIf({
                store: ds,
                displayField: 'text',
                valueField: 'text'
            }, {
                legend: Openwis.i18n('ThesauriManagement.Search.SelectedKeywords'),
                droppable: true,
                draggable: true,
                width: 275,
                height: 340
            }));
        }
        return this.isForm;
    },

	getKeywordsButtonsPanel: function() {
		if(!this.keywordsButtonsPanel) {
			this.keywordsButtonsPanel = new Ext.Panel({
				autoScroll: true,
				border: false,
				layout:'table',
				style : {
		               padding: '5px'
	            },
                layoutConfig: {
                    columns: 1,
                    tableAttrs: {
                        style: {
                            width: '100%',
                            padding: '5px'
                        }
                    }
                }
			});
			this.keywordsButtonsPanel.add(this.getRightButton());
			this.keywordsButtonsPanel.add(this.getLeftButton());
			this.keywordsButtonsPanel.add(this.getClearButton());
		}
		return this.keywordsButtonsPanel;
	},

	getRightButton: function() {
		if(!this.rightButton) {
			this.rightButton = new Ext.Button(
			    new Ext.Action({
                    iconCls: 'icon-right',
    				scope: this,
    				handler: function() {
    					var selectionsArray = [];
    					var selectionsArray = this.keywordsTreePanel.getSelectionModel().getSelectedNodes();
    			        var records = [];
    			        if (selectionsArray.length > 0) {
    			            for (var i=0; i<selectionsArray.length; i++) {
    			            	var textSelected = (selectionsArray[i]).attributes.text;
    			            	var idSelected = (selectionsArray[i]).attributes.id;
    			            	var keywordSelected = (selectionsArray[i]).attributes.keyword;
    			            	var inStore = this.getKeywordsMultiSelector().view.store.find('text', textSelected);
			                    if (inStore == -1 && idSelected != 0)
		                    	{
			                    	record =  new Ext.data.Record({text:textSelected, keyword:keywordSelected});
	    			                records.push(record);
		                    	}
    			            }
    			            for (var i=0; i<records.length; i++) {
    			                record = records[i];
    			                this.getKeywordsMultiSelector().view.store.add(record);
			                    selectionsArray.push((this.getKeywordsMultiSelector().view.store.getCount() - 1));
    			            }
    			            this.getKeywordsMultiSelector().view.refresh();
	    			        var si = this.getKeywordsMultiSelector().store.sortInfo;
	    			        if (si){
	    			            this.getKeywordsMultiSelector().store.sort(si.field, si.direction);
	    			        }
	    			        this.getKeywordsMultiSelector().view.select(selectionsArray);
    			        }
    					this.doLayout();
    				}
    			})
			);
		}
		return this.rightButton;
	},

	getLeftButton: function() {
		if(!this.leftButton) {
			this.leftButton = new Ext.Button(
			    new Ext.Action({
                    iconCls: 'icon-remove',
    				scope: this,
    				handler: function() {
    					var selectionsArray = this.getKeywordsMultiSelector().view.getSelectedIndexes();
    			        var records = [];
    			        if (selectionsArray.length > 0) {
    			            for (var i=0; i<selectionsArray.length; i++) {
    			                record = this.getKeywordsMultiSelector().view.store.getAt(selectionsArray[i]);
    			                records.push(record);
    			            }
    			            for (var i=0; i<records.length; i++) {
    			                record = records[i];
    			                this.getKeywordsMultiSelector().view.store.remove(record);
    			            }
    			            this.getKeywordsMultiSelector().view.refresh();
        					this.doLayout();
    			        }
    				}
    			})
			);
		}
		return this.leftButton;
	},

	getClearButton: function() {
		if(!this.clearButton) {
			this.clearButton = new Ext.Button(
			    new Ext.Action({
                    iconCls: 'icon-removeAll',
    				scope: this,
    				handler: function() {
    			        this.getKeywordsMultiSelector().view.store.removeAll();
    			        this.getKeywordsMultiSelector().view.refresh();
    					this.doLayout();
    				}
    			})
			);
		}
		return this.clearButton;
	},

	getSelectedNodeInfos: function() {
		var selectedNodeInfos = {};
		selectedNodeInfos.text = this.keywordsTreePanel.getSelectionModel().getSelectedNode().attributes.text;
		return selectedNodeInfos;
	},

	/**
	 * The Add action.
	 */
	getAddAction: function() {
		if(!this.addAction) {
			this.addAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Add'),
				scope: this,
				handler: function() {
					if (this.isXML)
					{
						var getHandler = new Openwis.Handler.GetNoJsonResponse({
							url: configOptions.locService+ '/xml.thesaurus.getKeywordsXml',
							params: this.getKeywordsInfos(),
				            listeners: {
				            	success: function(result) {
				            		var keywordsSelected = new Array();
				            		var keyword = result;
				                    if (keyword.indexOf('<gmd:MD_Keywords') != -1)
			                    	{
				                    	keywordsSelected.push(keyword);
			                    	}
				            		this.fireEvent("keywordsSelection", keywordsSelected);
									this.close();
				            	},
				            	scope: this
				            }
				         });
				         getHandler.proceed();
					}
					else
					{
						var records = this.getKeywordsMultiSelector().view.store.collect('text');
				        var returnArray = [];
				        for (var i=0; i<records.length; i++) {
				            returnArray.push(records[i]);
				        }
				        returnArray = returnArray.join(" | ");
				        this.fireEvent("keywordsSelection", returnArray);
						this.close();
					}
				}
			});
		}
		return this.addAction;
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
	 *	The JSON object submitted to the server.
	 */
	getKeywordsInfos: function() {
		var keywordsInfos = {};
		var allKeywords = new Array();
		var record = null;
		var keyword = null;
		for (var i=0; i<this.getKeywordsMultiSelector().view.store.getCount(); i++) {
			record = this.getKeywordsMultiSelector().view.store.getAt(i);
			keyword = record.data.keyword;
			var aKeyword = {};
			aKeyword.code = keyword.code;
			aKeyword.thesaurus = keyword.thesaurus;
			aKeyword.value = keyword.value;
			allKeywords.push(aKeyword);
        }
		keywordsInfos.keywordListDTO = allKeywords;
		return keywordsInfos;
	}

});