Ext.ns('Openwis.Admin.Harvesting');

Openwis.Admin.Harvesting.All = Ext.extend(Ext.Container, {
    
    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Admin.Harvesting.All.superclass.initComponent.apply(this, arguments);
        
        this.initialize();
    },
    
    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //Create harvestingTask grid.
        this.add(this.getHarvestingTaskGrid());
    },
    
    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Harvesting.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
    
    //-- Grid and Store.
    
    getHarvestingTaskGrid: function() {
        if(!this.harvestingTaskGrid) {
            this.harvestingTaskGrid = new Ext.grid.GridPanel({
				id: 'harvestingGrid',
                height: 400,
                border: true,
                store: this.getHarvestingTaskStore(),
                loadMask: true,
                columns: [
                    {id:'monitorStatus', header: ' ', dataIndex:'monitor', sortable: false, hideable:false, renderer: this.monitorImg, width: 30},
                    {id:'monitorProgress', header: Openwis.i18n('Harvesting.Processed'), dataIndex:'progress', sortable: false, hideable:false, renderer: this.monitorProgress, width: 55},
                    {id:'name', header: Openwis.i18n('Harvesting.Name'), dataIndex:'name', sortable: true, hideable:false},
                    {id:'type', header: Openwis.i18n('Harvesting.Type'), dataIndex:'type', sortable: true, hideable:false},
                    {id:'lastRun', header: Openwis.i18n('Harvesting.LastRun'), dataIndex:'lastRun', sortable: true, hideable:false, renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong},
                    {id:'backup', header: Openwis.i18n('Harvesting.Backup'), dataIndex:'backup', sortable: true, hideable:false, renderer: this.backupRenderer}
                ],
                autoExpandColumn: 'name',
                listeners: { 
                    afterrender: function (grid) {
                       grid.loadMask.show();
                       grid.getStore().load({params:{start:0, limit: Openwis.Conf.PAGE_SIZE}});
                    },
                    scope:this
                },
                sm: new Ext.grid.RowSelectionModel({
                    singleSelect: true,
                    listeners: {
                        rowselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getReportAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() == 0);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getActivateAction().setDisabled(sm.getCount() == 0 || record.get('status') != 'SUSPENDED' || record.get('status') == 'SUSPENDED_BACKUP');
                            sm.grid.ownerCt.getDeactivateAction().setDisabled(sm.getCount() == 0 || record.get('status') == 'SUSPENDED' || record.get('status') == 'SUSPENDED_BACKUP');
                            sm.grid.ownerCt.getRunAction().setDisabled(sm.getCount() == 0 || record.get('status') != 'ACTIVE');
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getReportAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() == 0);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
                            sm.grid.ownerCt.getActivateAction().setDisabled(sm.getCount() == 0 || record.get('status') != 'SUSPENDED' || record.get('status') == 'SUSPENDED_BACKUP');
                            sm.grid.ownerCt.getDeactivateAction().setDisabled(sm.getCount() == 0 || record.get('status') == 'SUSPENDED' || record.get('status') == 'SUSPENDED_BACKUP');
                            sm.grid.ownerCt.getRunAction().setDisabled(sm.getCount() == 0 || record.get('status') != 'ACTIVE');
                        }
                    }
                }),
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getHarvestingTaskStore(),
                    displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('Harvesting.Administration.All.Display.Range'),
                    emptyMsg: Openwis.i18n('Harvesting.Administration.All.No.Task')
                })
            });
            this.harvestingTaskGrid.addButton(this.getNewMenuButton());
            this.harvestingTaskGrid.addButton(new Ext.Button(this.getReportAction()));
            this.harvestingTaskGrid.addButton(new Ext.Button(this.getEditAction()));
            this.harvestingTaskGrid.addButton(new Ext.Button(this.getRemoveAction()));
            this.harvestingTaskGrid.addButton(new Ext.Button(this.getActivateAction()));
            this.harvestingTaskGrid.addButton(new Ext.Button(this.getDeactivateAction()));
            this.harvestingTaskGrid.addButton(new Ext.Button(this.getRunAction()));
        }
        return this.harvestingTaskGrid;
    },
    
    // HarvestingTask store
    getHarvestingTaskStore: function() {
        if (!this.harvestingTaskStore) {
            this.harvestingTaskStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.harvest.all',
                remoteSort: true,
                // reader configs
                root: 'rows',
                fields: [
                    {name: 'id', mapping: 'object.id'},
                    {name: 'uuid', mapping: 'object.uuid'},
                    {name: 'name', mapping: 'object.name', sortType: Ext.data.SortTypes.asUCString},
                    {name: 'type', mapping: 'object.type'},
                    {name: 'lastRun', mapping: 'object.lastRun'},
                    {name: 'backup', mapping: 'object.backup'},
                    {name: 'status', mapping: 'object.status'},
                    {name: 'lastResult', mapping: 'object.lastResult'},
                    {name: 'running'},
                    {name: 'progress'},
                    {name: 'monitor', convert: this.convertMonitor}
                ],
                sortInfo: {
                   field: 'name',
                   direction: 'ASC'
                }
            });
        }
        return this.harvestingTaskStore;
    },
    
    //---------------------------------------------------------------------------- Actions.
    
    getNewMenuButton: function() {
        if(!this.newMenuButton) {
            this.newMenuButton = new Ext.Button({
                text: Openwis.i18n('Common.Btn.New'),
                menu: new Ext.menu.Menu({
                    items: [
                        this.getNewOaipmhAction(),
                        this.getNewFileSystemAction(),
                        this.getNewGeonetwork20Action(),
                        this.getNewCSWAction(),
                        this.getNewWebDavAction()
                    ]
                })
            });
        }
        return this.newMenuButton;
    },
    
    getNewOaipmhAction: function() {
        if(!this.newOaipmhAction) {
            this.newOaipmhAction = new Ext.menu.Item({
                text: Openwis.i18n('Harvesting.Menu.Create.Oaipmh'),
                scope: this,
                handler: function() {
                    new Openwis.Admin.Harvesting.Harvester.Oaipmh({
                        operationMode: 'Create',
                        listeners: {
                            harvestingTaskSaved: function() {
                                this.getHarvestingTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.newOaipmhAction;
    },
    
    getNewFileSystemAction: function() {
        if(!this.newFileSystemAction) {
            this.newFileSystemAction = new Ext.menu.Item({
                text: Openwis.i18n('Harvesting.Menu.Create.FileSystem'),
                scope: this,
                handler: function() {
                    new Openwis.Admin.Harvesting.Harvester.FileSystem({
                        operationMode: 'Create',
                        listeners: {
                            harvestingTaskSaved: function() {
                                this.getHarvestingTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.newFileSystemAction;
    },
    
    getNewGeonetwork20Action: function() {
        if(!this.newGeonetwork20Action) {
            this.newGeonetwork20Action = new Ext.menu.Item({
                text: Openwis.i18n('Harvesting.Menu.Create.Geonetwork20'),
                scope: this,
                handler: function() {
                    new Openwis.Admin.Harvesting.Harvester.Geonetwork20({
                        operationMode: 'Create',
                        listeners: {
                            harvestingTaskSaved: function() {
                                this.getHarvestingTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.newGeonetwork20Action;
    },
    
    getNewCSWAction: function() {
        if(!this.newCSWAction) {
            this.newCSWAction = new Ext.menu.Item({
                text: Openwis.i18n('Harvesting.Menu.Create.CSW'),
                scope: this,
                handler: function() {
                    new Openwis.Admin.Harvesting.Harvester.CSW({
                        operationMode: 'Create',
                        listeners: {
                            harvestingTaskSaved: function() {
                                this.getHarvestingTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.newCSWAction;
    },
    
    getNewWebDavAction: function() {
        if(!this.newWebDavAction) {
            this.newWebDavAction = new Ext.menu.Item({
                text: Openwis.i18n('Harvesting.Menu.Create.WebDav'),
                scope: this,
                handler: function() {
                    new Openwis.Admin.Harvesting.Harvester.WebDav({
                        operationMode: 'Create',
                        listeners: {
                            harvestingTaskSaved: function() {
                                this.getHarvestingTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.newWebDavAction;
    },

    getReportAction: function() {
        if(!this.reportAction) {
            this.reportAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Report'),
                scope: this,
                handler: function() {
                    var rec = this.getHarvestingTaskGrid().getSelectionModel().getSelected();
                    new Openwis.Common.Metadata.Report({
                    	lastResult: rec.get('lastResult'),
                        harvestingTaskId: rec.get('id')
                    });
                }
            });
        }
        return this.reportAction;
    },

    getEditAction: function() {
        if(!this.editAction) {
            this.editAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Edit'),
                scope: this,
                handler: function() {
                    var rec = this.getHarvestingTaskGrid().getSelectionModel().getSelected();
                    if(rec.get('type') == 'oaipmh') {
                        new Openwis.Admin.Harvesting.Harvester.Oaipmh({
                            operationMode: 'Edit',
                            editTaskId: rec.get('id'),
                            listeners: {
                                harvestingTaskSaved: function() {
                                    this.getHarvestingTaskGrid().getStore().reload();
                                },
                                scope: this
                            }
                        });
                    } else if(rec.get('type') == 'localfilesystem') {
                         new Openwis.Admin.Harvesting.Harvester.FileSystem({
                            operationMode: 'Edit',
                            editTaskId: rec.get('id'),
                            listeners: {
                                harvestingTaskSaved: function() {
                                    this.getHarvestingTaskGrid().getStore().reload();
                                },
                                scope: this
                            }
                        });
                    } else if(rec.get('type') == 'geonetwork20') {
                         new Openwis.Admin.Harvesting.Harvester.Geonetwork20({
                            operationMode: 'Edit',
                            editTaskId: rec.get('id'),
                            listeners: {
                                harvestingTaskSaved: function() {
                                    this.getHarvestingTaskGrid().getStore().reload();
                                },
                                scope: this
                            }
                        });
                    } else if(rec.get('type') == 'csw') {
                         new Openwis.Admin.Harvesting.Harvester.CSW({
                            operationMode: 'Edit',
                            editTaskId: rec.get('id'),
                            listeners: {
                                harvestingTaskSaved: function() {
                                    this.getHarvestingTaskGrid().getStore().reload();
                                },
                                scope: this
                            }
                        });
                    } else if(rec.get('type') == 'webdav') {
                         new Openwis.Admin.Harvesting.Harvester.WebDav({
                            operationMode: 'Edit',
                            editTaskId: rec.get('id'),
                            listeners: {
                                harvestingTaskSaved: function() {
                                    this.getHarvestingTaskGrid().getStore().reload();
                                },
                                scope: this
                            }
                        });
                    }
                }
            });
        }
        return this.editAction;
    },
    
    getRemoveAction: function() {
        if(!this.removeAction) {
            this.removeAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Remove'),
                scope: this,
                handler: function() {
                    //Get the harvesting task ids to delete.
					var selection = this.getHarvestingTaskGrid().getSelectionModel().getSelected();
					
					//Invoke the Save handler to remove the elements by an ajax request.
					new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.harvest.remove',
						params: selection.get('id'),
						listeners: {
							success: function() {
								this.getHarvestingTaskGrid().getStore().reload();
							},
							scope: this
						}
					}).proceed();
                }
            });
        }
        return this.removeAction;
    },
    
    getActivateAction: function() {
        if(!this.activateAction) {
            this.activateAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Activate'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getHarvestingTaskGrid().getSelectionModel().getSelected();
                    new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.harvest.activation',
						params: {id: selectedRec.get('id'), activate: true},
						listeners: {
							success: function() {
								this.getHarvestingTaskGrid().getStore().reload();
                                this.getHarvestingTaskGrid().getSelectionModel().clearSelections(false);
							},
							scope: this
						}
					}).proceed();
                }
            });
        }
        return this.activateAction;
    },
    
    getDeactivateAction: function() {
        if(!this.deactivateAction) {
            this.deactivateAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Deactivate'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getHarvestingTaskGrid().getSelectionModel().getSelected();
                    new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.harvest.activation',
						params: {id: selectedRec.get('id'), activate: false},
						listeners: {
							success: function() {
								this.getHarvestingTaskGrid().getStore().reload();
                                this.getHarvestingTaskGrid().getSelectionModel().clearSelections(false);
							},
							scope: this
						}
					}).proceed();
                }
            });
        }
        return this.deactivateAction;
    },
    
    getRunAction: function() {
        if(!this.runAction) {
            this.runAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Run'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getHarvestingTaskGrid().getSelectionModel().getSelected();
                    var saveHandler = new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.harvest.run',
						params: selectedRec.get('id'),
						listeners: {
							success: function() {
								this.getHarvestingTaskGrid().getStore().reload();
							},
							scope: this
						}
					});
					saveHandler.proceed();
                }
            });
        }
        return this.runAction;
    },
    
    //---------------------------------------------------------------------------- Renderer.
    
    statusRenderer: function(status) {
        if(status == 'ACTIVE') {
            return Openwis.i18n('Harvesting.Status.Active');
        } else if(status == 'SUSPENDED') {
            return Openwis.i18n('Harvesting.Status.Suspended');
        } else if(status == 'SUSPENDED_BACKUP') {
            return Openwis.i18n('Harvesting.Status.SuspendedBackup');
        } else {
            return status;
        }
    },
    
    backupRenderer: function(backup) {
        if(backup && backup.name) {
            return backup.name;
        } else {
            return '';
        }
    },
    
    convertMonitor: function(v, rec) {
        if(rec.object.status == 'ACTIVE' && rec.running) {
            return 'RUNNING';
        } else {
            return rec.object.status;
        }
    },
    
    monitorImg: function(status) {
    	if(status == 'ACTIVE') {
        	return '<img src="' + configOptions.url + '/images/openwis/icons/harvesting_active.gif"/>';
        } else if(status == 'RUNNING') {
            return '<img src="' + configOptions.url + '/images/openwis/icons/harvesting_inprogress.png"/>';
        } else if(status == 'SUSPENDED') {
            return '<img src="' + configOptions.url + '/images/openwis/icons/harvesting_inactive.gif"/>';
        } else if(status == 'SUSPENDED_BACKUP') {
            return '<img src="' + configOptions.url + '/images/openwis/icons/harvesting_inactive_backup.gif"/>';
        }
    },
    
    monitorProgress: function(progress) {
    	result = ' ';
    	if (Ext.isNumber(progress)) {
            if(progress > 0) {
            	result = progress;
            }
    	}
    	return result;
    }
});