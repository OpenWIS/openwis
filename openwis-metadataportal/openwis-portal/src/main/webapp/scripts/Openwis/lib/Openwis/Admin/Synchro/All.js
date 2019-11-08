Ext.ns('Openwis.Admin.Synchro');

Openwis.Admin.Synchro.All = Ext.extend(Ext.Container, {
    
    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Admin.Synchro.All.superclass.initComponent.apply(this, arguments);
        
        this.initialize();
    },
    
    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //Create synchroTask grid.
        this.add(this.getSynchroTaskGrid());
    },
    
    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Synchro.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
    
    //-- Grid and Store.
    
    getSynchroTaskGrid: function() {
        if(!this.synchroTaskGrid) {
            this.synchroTaskGrid = new Ext.grid.GridPanel({
				id: 'synchroGrid',
                height: 400,
                border: true,
                store: this.getSynchroTaskStore(),
                loadMask: true,
                columns: [
                    {id:'monitorStatus', header: ' ', dataIndex:'monitor', sortable: false, hideable:false, renderer: this.monitorImg, width: 30},
                    {id:'monitorProgress', header: Openwis.i18n('Synchro.Processed'), dataIndex:'progress', sortable: false, hideable:false, renderer: this.monitorProgress, width: 55},
                    {id:'name', header: Openwis.i18n('Synchro.Name'), dataIndex:'name', sortable: true, hideable:false},
                    {id:'lastRun', header: Openwis.i18n('Synchro.LastRun'), dataIndex:'lastRun', sortable: true, hideable:false, renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong},
                    {id:'backup', header: Openwis.i18n('Synchro.Backup'), dataIndex:'backup', sortable: true, hideable:false, renderer: this.backupRenderer}
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
                    store: this.getSynchroTaskStore(),
                    displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('Synchro.Administration.All.Display.Range'),
                    emptyMsg: Openwis.i18n('Synchro.Administration.All.No.Task')
                })
            });
            this.synchroTaskGrid.addButton(new Ext.Button(this.getNewAction()));
            this.synchroTaskGrid.addButton(new Ext.Button(this.getReportAction()));
            this.synchroTaskGrid.addButton(new Ext.Button(this.getEditAction()));
            this.synchroTaskGrid.addButton(new Ext.Button(this.getRemoveAction()));
            this.synchroTaskGrid.addButton(new Ext.Button(this.getActivateAction()));
            this.synchroTaskGrid.addButton(new Ext.Button(this.getDeactivateAction()));
            this.synchroTaskGrid.addButton(new Ext.Button(this.getRunAction()));
        }
        return this.synchroTaskGrid;
    },
    
    // SynchroTask store
    getSynchroTaskStore: function() {
        if (!this.synchroTaskStore) {
            this.synchroTaskStore = new Openwis.Data.JeevesJsonStore({
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
            this.synchroTaskStore.setBaseParam("isSynchronization", true);
        }
        return this.synchroTaskStore;
    },
    
    //---------------------------------------------------------------------------- Actions.
    
    getNewAction: function() {
        if(!this.newAction) {
            this.newAction = new Ext.menu.Item({
                text: Openwis.i18n('Common.Btn.New'),
                scope: this,
                handler: function() {
                    new Openwis.Admin.Synchro.Manage({
                        operationMode: 'Create',
                        listeners: {
                            synchroTaskSaved: function() {
                                this.getSynchroTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
                }
            });
        }
        return this.newAction;
    },

    getReportAction: function() {
        if(!this.reportAction) {
            this.reportAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Btn.Report'),
                scope: this,
                handler: function() {
                    var rec = this.getSynchroTaskGrid().getSelectionModel().getSelected();
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
                    var rec = this.getSynchroTaskGrid().getSelectionModel().getSelected();
                    new Openwis.Admin.Synchro.Manage({
                        operationMode: 'Edit',
                        editTaskId: rec.get('id'),
                        listeners: {
                            synchroTaskSaved: function() {
                                this.getSynchroTaskGrid().getStore().reload();
                            },
                            scope: this
                        }
                    });
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
                    //Get the synchro task ids to delete.
					var selection = this.getSynchroTaskGrid().getSelectionModel().getSelected();
					
					//Invoke the Save handler to remove the elements by an ajax request.
					new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.harvest.remove',
						params: selection.get('id'),
						listeners: {
							success: function() {
								this.getSynchroTaskGrid().getStore().reload();
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
                    var selectedRec = this.getSynchroTaskGrid().getSelectionModel().getSelected();
                    new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.harvest.activation',
						params: {id: selectedRec.get('id'), activate: true},
						listeners: {
							success: function() {
								this.getSynchroTaskGrid().getStore().reload();
                                this.getSynchroTaskGrid().getSelectionModel().clearSelections(false);
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
                    var selectedRec = this.getSynchroTaskGrid().getSelectionModel().getSelected();
                    new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.harvest.activation',
						params: {id: selectedRec.get('id'), activate: false},
						listeners: {
							success: function() {
								this.getSynchroTaskGrid().getStore().reload();
                                this.getSynchroTaskGrid().getSelectionModel().clearSelections(false);
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
                    var selectedRec = this.getSynchroTaskGrid().getSelectionModel().getSelected();
                    var saveHandler = new Openwis.Handler.Save({
						url: configOptions.locService+ '/xml.harvest.run',
						params: selectedRec.get('id'),
						listeners: {
							success: function() {
								this.getSynchroTaskGrid().getStore().reload();
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
            return Openwis.i18n('Synchro.Status.Active');
        } else if(status == 'SUSPENDED') {
            return Openwis.i18n('Synchro.Status.Suspended');
        } else if(status == 'SUSPENDED_BACKUP') {
            return Openwis.i18n('Synchro.Status.SuspendedBackup');
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