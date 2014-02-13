Ext.ns('Openwis.Admin.Thesauri');

Openwis.Admin.Thesauri.Manage = Ext.extend(Ext.Container, {
		
		initComponent: function() {
			Ext.apply(this, {
				style: {
					margin: '10px 30px 10px 30px'
				}
			});
			Openwis.Admin.Thesauri.Manage.superclass.initComponent.apply(this, arguments);
			
			this.getInfosAndInitialize();
		},
		
		getInfosAndInitialize: function() {
			var getHandler = new Openwis.Handler.Get({
				url: configOptions.locService+ '/xml.thesaurus.list',
				params: this.getManageThesaurusInfos(),
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
			//Create Main Header.
			this.add(this.getHeader());

			//Create Thesauri.
			Ext.each(this.config, function(thesauris, index) {
				var thesaurisGrid = this.getThesauriGrid(thesauris);

				var thesaurisFieldSet = new Ext.form.FieldSet({
	                title: thesauris.label,
	    			autoHeight: true,
	    			collapsed: true,
	    			collapsible: true,
	    			listeners: {
					    afterrender: function() {
					    	thesaurisFieldSet.addListener('collapse', this.onGuiChanged, this);
					    	thesaurisFieldSet.addListener('expand', this.onGuiChanged, this);
					    },
					    scope: this
					}
	            });
				
				var addBtn = new Ext.Button(
				    new Ext.Action({
                        iconCls: 'icon-add',
        				scope: this,
        				handler: function() {
        					createFormPanel.setVisible(true);
        					addBtn.setVisible(false);
        					this.doLayout();
        				}
        			})
				);

				var newFileTextField = new Ext.form.TextField({
					fieldLabel : Openwis.i18n('ThesauriManagement.Local'),
	          		allowBlank : false,
	          		width: 200
				});

				var createFormPanel = new Ext.form.FormPanel({
					itemCls: 'formItems',
					border: false,
					buttons:
					[
						{
							text: Openwis.i18n('Common.Btn.Create'),
							handler: function(btn, e) {
								if(createFormPanel.getForm().isValid()) {
									var saveHandler = new Openwis.Handler.Save({
										url: configOptions.locService+ '/xml.thesaurus.add',
										params: this.getThesaurisCreateInfos(newFileTextField, thesauris),
										listeners: {
											success: function(config) {
												newFileTextField.reset();
												thesauris.thesaurusListDTO.push(config);
												thesaurisGrid.getStore().loadData(thesauris.thesaurusListDTO);
			    								createFormPanel.setVisible(false);
			    	        					addBtn.setVisible(true);
			    	        					this.doLayout();
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
				createFormPanel.add(newFileTextField);
				createFormPanel.setVisible(false);

				thesaurisFieldSet.add(addBtn);
				thesaurisFieldSet.add(createFormPanel);
	            thesaurisFieldSet.add(thesaurisGrid);
				this.add(thesaurisFieldSet);
            }, this);

			//Create Upload Header.
			this.add(this.getUploadHeader());

			//Create Upload form.
			this.getUploadFormPanel().add(this.getThesaurusCategoryCombobox());
			this.getUploadFormPanel().add(this.getThesaurusFileUploadField());
			this.add(this.getUploadFormPanel());

			this.doLayout();
		},
		
		getThesaurisCreateInfos: function(newFileTextField, thesauris) {
			var thesaurusInfos = {};
			thesaurusInfos.fname = newFileTextField.getValue();
			thesaurusInfos.dname = thesauris.label;
			thesaurusInfos.type = "local";
			return thesaurusInfos;
		},
		
		getHeader: function() {
			if(!this.header) {
				this.header = new Ext.Container({
					html: Openwis.i18n('ThesauriManagement.Manage.Title'),
					cls: 'administrationTitle1'
				});
			}
			return this.header;
		},

		getUploadHeader: function() {
			if(!this.uploadHeader) {
				this.uploadHeader = new Ext.Container({
					html: Openwis.i18n('ThesauriManagement.Upload.Title'),
					cls: 'administrationTitle1'
				});
			}
			return this.uploadHeader;
		},

		//-- Grid and Store.
	    getThesauriGrid: function(thesauris) {
			//if(!this.thesauriGrid) {

				var thesauriGrid = new Ext.grid.GridPanel({
					store: new Ext.data.JsonStore({
						autoDestroy: true,
		    			idProperty: 'fname',
		                fields: [
		                    {name: 'type'}, {name: 'fname'}, {name: 'value'}, {name: 'dname'}
		    			]
		    		}),
					id: 'thesauri' + thesauris.label + 'Grid',
					height: 160,
					border: true,
					loadMask: true,
					columns: [
					    {id:'type', header:Openwis.i18n('ThesauriManagement.Type'), dataIndex:'type', sortable: true, width: 100},
						{id:'fname', header:Openwis.i18n('ThesauriManagement.Name'), dataIndex:'fname', sortable: true, width: 100}
					],
					autoExpandColumn: 'fname',
					listeners: { 
						afterrender: function (grid) {
						   grid.loadMask.show();
						   grid.getStore().loadData(thesauris.thesaurusListDTO);
						}
					},
					sm: new Ext.grid.RowSelectionModel({
						listeners: { 
							rowselect: function (sm, rowIndex, record) {
								if (sm.getCount() != 1)
								{
	                        		viewEditAction.setText(Openwis.i18n('Common.Btn.ViewEdit'));
								}
								else
								{
									if (record.data.type == 'local')
									{
										viewEditAction.setText(Openwis.i18n('Common.Btn.Edit'));
									}
									else
									{
										viewEditAction.setText(Openwis.i18n('Common.Btn.View'));
									}
								}
								downloadAction.setDisabled(sm.getCount() != 1);
								deleteAction.setDisabled(sm.getCount() == 0);
								viewEditAction.setDisabled(sm.getCount() != 1);
	                        },
	                        rowdeselect: function (sm, rowIndex, record) {
	                        	if (sm.getCount() != 1)
								{
	                        		viewEditAction.setText(Openwis.i18n('Common.Btn.ViewEdit'));
								}
								else
								{
									if (record.data.type == 'local')
									{
										viewEditAction.setText(Openwis.i18n('Common.Btn.Edit'));
									}
									else
									{
										viewEditAction.setText(Openwis.i18n('Common.Btn.View'));
									}
								}
	                        	downloadAction.setDisabled(sm.getCount() != 1);
	                        	deleteAction.setDisabled(sm.getCount() == 0);
	                        	viewEditAction.setDisabled(sm.getCount() != 1);
	                        }
						}
					})
				});
				
				var downloadAction = new Ext.Action({
					disabled: true,
					text: Openwis.i18n('Common.Btn.Download'),
					scope: this,
					handler: function() {
						var selectedRec = thesauriGrid.getSelectionModel().getSelected();
						window.location.href = configOptions.locService +  "/xml.thesaurus.download?thesaurus=" + selectedRec.data.value;
					}
				});
		    	
		    	var deleteAction = new Ext.Action({
		    		disabled: true,
					text: Openwis.i18n('Common.Btn.Delete'),
					scope: this,
					handler: function() {
						//Get the keyword ids to delete.
						var selection = thesauriGrid.getSelectionModel().getSelections();
						var params = {keywordListDTO: []};
						Ext.each(selection, function(item, index, allItems) {
							params.keywordListDTO.push({thesaurus: item.get('value')});
						}, this);
						var msg = null;
    					//Invoke the remove handler to remove the elements by an ajax request.
    					var removeHandler = new Openwis.Handler.Remove({
    						url: configOptions.locService+ '/xml.thesaurus.delete',
    						params: params,
    						confirmMsg: msg,
    						listeners: {
    							success: function() {
    								Ext.each(selection, function(item, index, allItems) {
    									thesauris.thesaurusListDTO.remove(item.json);
    								}, this);
    								thesauriGrid.getStore().loadData(thesauris.thesaurusListDTO);
    								downloadAction.setDisabled(true);
    								deleteAction.setDisabled(true);
    								viewEditAction.setDisabled(true);
    							},
    							scope: this
    						}
    					});
    					removeHandler.proceed();
					}
				});

		    	var viewEditAction = new Ext.Action({
		    		disabled: true,
					text: Openwis.i18n('Common.Btn.ViewEdit'),
					scope: this,
					handler: function() {
						//Get the thesauris to view/edit.
						var selectedRec = thesauriGrid.getSelectionModel().getSelected();
						var params = this.getViewEditThesaurusInfos(selectedRec.data);
						new Openwis.Admin.Thesauri.ViewEdit({
							title: selectedRec.data.value,
							thesaurusType: thesauris.label,
							mode : selectedRec.data.type,
							params: params
						});
					}
				});
		    	
				thesauriGrid.addButton(new Ext.Button(downloadAction));
				thesauriGrid.addButton(new Ext.Button(deleteAction));
				thesauriGrid.addButton(new Ext.Button(viewEditAction));

			//}
			return thesauriGrid;
		},
	
		/**
		 *	The form panel.
		 */
		getUploadFormPanel: function() {
			if(!this.uploadFormPanel) {
				this.uploadFormPanel = new Ext.FormPanel({
					fileUpload : true,
				    itemCls: 'formItems',
					border: false,
					errorReader: new Ext.data.XmlReader({
	                        record : 'field',
	                        success: '@success'
	                    }, [
	                        'id', 'msg'
	                    ]
	                ),
					buttons:
					[
						{
							text: Openwis.i18n('Common.Btn.Upload'),
							scope: this,
							handler: function() {
								if(this.getUploadFormPanel().getForm().isValid()) {
									this.getUploadFormPanel().getForm().submit({
	         	                        url : configOptions.locService+ '/xml.thesaurus.upload',
	         	                        scope : this,
	         	                        params: this.getUploadThesaurusInfos(),
	         	                        success : function(fp, action) {
	         	                        	var gridToReload = Ext.getCmp('thesauri' + this.getUploadThesaurusInfos().dname + 'Grid');
	         	                        	var jsonData = fp.errorReader.xmlData.getElementsByTagName("jsonData")[0].childNodes[0].nodeValue;
	         	                        	var result = Ext.decode(jsonData);
	         	                        	if (result.ok)
         	                        		{
	         	                        		gridToReload.getStore().add(new Ext.data.Record(result.o));
         	                        		}
	         	                        	else
	         	                        	{
	         	                        		Openwis.Utils.MessageBox.displayErrorMsg(result.o);
	         	                        	}
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
			return this.uploadFormPanel;
		},
		
		/**
		 * The thesaurus category combo box.
		 */
		getThesaurusCategoryCombobox: function() {
	        if(!this.thesaurusCategoryCombobox) {
	            this.thesaurusCategoryCombobox = new Ext.form.ComboBox({
	            	store: new Ext.data.JsonStore({
	            		// store configs
						autoDestroy: true,
						// reader configs
						idProperty: 'label',
						fields: [
							{name: 'label'}
						]
	        		}),
					valueField: 'label',
					displayField:'label',
					//value: 'NONE',
	                name: 'thesaurusCategory',
	                typeAhead: true,
					mode: 'local',
					triggerAction: 'all',
					editable: false,
					selectOnFocus:true,
					width: 200,
					allowBlank : false,
					fieldLabel: Openwis.i18n('ThesauriManagement.Category')
	            });
	            
	            //Load Data into store.
				this.thesaurusCategoryCombobox.getStore().loadData(this.config);
	        }
	        return this.thesaurusCategoryCombobox;
	    },

	    /**
		 * The thesaurus file to upload.
		 */
		getThesaurusFileUploadField: function() {
	        if(!this.thesaurusFileUploadField) {
	            this.thesaurusFileUploadField = new Ext.ux.form.FileUploadField(
    		    {
        		    xtype: 'fileuploadfield',
                    allowBlank : false,
                    buttonCfg: {
                        text: Openwis.i18n('Common.Btn.Browse')
                    },
                    fieldLabel: Openwis.i18n('ThesauriManagement.File'),
                    width: 360
                });
	        }
	        return this.thesaurusFileUploadField;
	    },
	    
	    /**
		 *	The JSON object submitted to the server.
		 */
	    getViewEditThesaurusInfos: function(selectedRec) {
			var viewEditThesaurusInfos = {};
			viewEditThesaurusInfos.thesaurus = selectedRec.value;
			viewEditThesaurusInfos.type = "all-thesauri";
			return viewEditThesaurusInfos;
		},

	    getUploadThesaurusInfos: function() {
			var uploadThesaurusInfos = {};
			uploadThesaurusInfos.file = this.getThesaurusFileUploadField().getValue();
			uploadThesaurusInfos.dname = this.getThesaurusCategoryCombobox().getValue();
			uploadThesaurusInfos.type = "external";
			return uploadThesaurusInfos;
		},

		getManageThesaurusInfos: function() {
			var manageThesaurusInfos = {};
			manageThesaurusInfos.type = "all-thesauri";
			return manageThesaurusInfos;
		},

		onGuiChanged: function() {
	        this.fireEvent('guiChanged', false, true);
	    }
});