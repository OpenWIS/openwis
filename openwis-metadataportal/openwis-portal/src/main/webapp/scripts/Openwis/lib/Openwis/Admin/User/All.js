Ext.ns('Openwis.Admin.User');

Openwis.Admin.User.All = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.User.All.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		this.add(this.getFilterFormPanel());
		this.add(this.getLimitWarningLabel());
		this.add(this.getUserGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n("Security.User.Title"),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	getLimitWarningLabel: function() {
		if(!this.limitWarningLabel) {
			this.limitWarningLabel = new Ext.Container({
				html: Openwis.i18n("Security.User.Grid.Label"),
				cls: 'administrationTitle2'
			});
			this.limitWarningLabel.setVisible(false);
		}
		return this.limitWarningLabel;
	},
	
	//-- Grid and Store.
	
	getUserGrid: function() {
	    var that = this;
		if(!this.userGrid) {
			this.userGrid = new Ext.grid.GridPanel({
				id: 'userGrid',
				height: 400,
				border: true,
				store: this.getUserStore(),
				loadMask: true,
				columns: [
					{id: 'inetUserStatus', header:Openwis.i18n("Security.User.Active.Column"), dataIndex:'inetUserStatus', renderer: Openwis.Common.Request.Utils.accountStatusRendererImg, width: 50, sortable: true},
					{id:'username', header: Openwis.i18n("Security.User.UserName.Column"), dataIndex:'username', sortable: true, width: 180},
					{id:'name', header: Openwis.i18n("Security.User.LastName.Column"), dataIndex:'surname', sortable: true, width: 180},
					{id:'surname', header: Openwis.i18n("Security.User.FirstName.Column"), dataIndex:'name', sortable: true, width: 180},
					{id:'profile', header:Openwis.i18n("Security.User.Profile.Column"), dataIndex:'profile', sortable: true, width: 180},
					{id:'lastLogin', header:Openwis.i18n("Security.User.LastLogin.Column"), dataIndex:'lastLogin', sortable: true, width: 180},
				],
				autoExpandColumn: 'name',
				listeners: { 
					afterrender: function (grid) {
					   grid.loadMask.show();
					   grid.getStore().load();
					}
				},
				sm: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
						    sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
							if (sm.getCount() == 1) {
							    sm.grid.ownerCt.getLockAccountAction().setDisabled(!that.canLockAccount(record.get('profile')));
							    sm.grid.ownerCt.getLockAccountAction().setText(that.getLockAccountActionText(record.get('inetUserStatus')));
                            } else {
                            	sm.grid.ownerCt.getLockAccountAction().setDisabled(true);
                            }
						},
						rowdeselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
							sm.grid.ownerCt.getRemoveAction().setDisabled(sm.getCount() == 0);
							if (sm.getCount() == 1) {
                                sm.grid.ownerCt.getLockAccountAction().setDisabled(!that.canLockAccount(record.get('profile')));
                                sm.grid.ownerCt.getLockAccountAction().setText(that.getLockAccountActionText(record.get('inetUserStatus')));
                            } else {
                                sm.grid.ownerCt.getLockAccountAction().setDisabled(true);
                            }
						}
					}
				})
			});
			this.userGrid.addButton(new Ext.Button(this.getNewAction()));
			this.userGrid.addButton(new Ext.Button(this.getEditAction()));
			this.userGrid.addButton(new Ext.Button(this.getRemoveAction()));
			this.userGrid.addButton(new Ext.Button(this.getLockAccountAction()));
			this.userGrid.addButton(new Ext.Button(this.getImportUserAction()));
		}
		return this.userGrid;
	},
	
	getUserStore: function() {
		if(!this.userStore) {
			this.userStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.user.all',
				idProperty: 'username',
				fields: [
					{
						name:'username',
						sortType:'asUCString'
					},{
						name:'name',
						sortType:'asUCString'
					},{
						name:'surname',
						sortType:'asUCString'
					},{
						name:'profile',
						sortType:'asUCString'
					},{
					    name:'lastLogin',
					    sortType:'asUCString'
					},
					{
					    name:'inetUserStatus',
					    sortType:'asUCString'
					},
				],
				listeners: { 
					load: function (records) {
						if (records && records.totalLength > 999) {
							this.getLimitWarningLabel().setVisible(true);
						} else {
							this.getLimitWarningLabel().setVisible(false);
						}
					},
					scope: this
				}
			});
		}
		return this.userStore;
	},
	
	getFilterFormPanel: function() {
	    if(!this.filterFormPanel) {
	        this.filterFormPanel = new Ext.form.FormPanel({
	            border: false,
	            buttonAlign: 'center',
	            labelWidth: 200
	        });
	        this.filterFormPanel.add(this.getGroupsListbox());
	        this.filterFormPanel.add(this.getUsernameSearchTextField());
	        this.filterFormPanel.addButton(new Ext.Button(this.getSearchAction()));
	    }
	    return this.filterFormPanel;
	},

	
	getUsernameSearchTextField: function() {
		if (!this.usernameSearchTextField) {
			this.usernameSearchTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.Filter.User'),
                name: 'filter',
                width: 300
            });
		}
		return this.usernameSearchTextField;
	},
	
	getGroupsListbox: function() {
	    if(!this.groupsListBox) {
	        this.groupsListBox = new Ext.ux.form.MultiSelect({
    			store: new Openwis.Data.JeevesJsonStore({
    				url: configOptions.locService+ '/xml.group.all',
    				idProperty: 'id',
    				autoLoad: true,
    				fields: [
    					{
    						name:'id'
    					},{
    						name:'name',
    						sortType: Ext.data.SortTypes.asUCString
    					},{
    						name:'global'
    					}
    				]
    			}),
    			fieldLabel: Openwis.i18n('MonitorCurrentRequests.FilterByGroups'),
    			displayField: 'name',
    			valueField: 'id',
    			width: 300
	        });
	    }
	    return this.groupsListBox;
	},
	
	//-- Actions implemented on Data Policy Administration.

	getNewAction: function() {
		if(!this.newAction) {
			this.newAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.New'),
				scope: this,
				handler: function() {
					new Openwis.Admin.User.Manage({
						operationMode: 'Create',
						listeners: {
							userSaved: function() {
								this.getUserGrid().getStore().reload();
							},
							scope: this
						}
					});
				}
			});
		}
		return this.newAction;
	},
	
	getEditAction: function() {
		if(!this.editAction) {
			this.editAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Edit'),
				scope: this,
				handler: function() {
					var selectedRec = this.getUserGrid().getSelectionModel().getSelected();
					new Openwis.Admin.User.Manage({
						operationMode: 'Edit',
						editUserName: selectedRec.get('username'),
						listeners: {
							userSaved: function() {
								this.getUserGrid().getStore().reload();
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
					//Get the username to delete.
					var selection = this.getUserGrid().getSelectionModel().getSelections();
					var params = {users: []};
					Ext.each(selection, function(item, index, allItems) {
						params.users.push({username: item.get('username')});
					}, this);
					
					var msg = null;

					//Invoke the Save handler to remove the elements by an ajax request.
					var removeHandler = new Openwis.Handler.Remove({
						url: configOptions.locService+ '/xml.user.remove',
						params: params,
						confirmMsg: msg,
						listeners: {
							success: function() {
								this.getUserGrid().getStore().reload();
							},
							scope: this
						}
					});
					removeHandler.proceed();
				}
			});
		}
		return this.removeAction;
	},

	getLockAccountAction: function() {
    		if(!this.lockAction) {
    			this.lockAction = new Ext.Action({
    				disabled: true,
    				text: Openwis.i18n('Common.Btn.Lock'),
    				scope: this,
    				handler: function() {
                        var selectedRec = this.getUserGrid().getSelectionModel().getSelected();
		                var params = {};
                		params.user = {};
                        params.user.username= selectedRec.get('username');
                        var msg = null;

                        //Invoke the Lock handler to lock user.
                        var lockHandler = new Openwis.Handler.Lock({
                            url: configOptions.locService+ '/xml.user.lock',
                            params: params,
                            confirmMsg: msg,
                            listeners: {
                                success: function() {
                                    this.getUserGrid().getStore().reload();
                                },
                                scope: this
                            }
                        });
                        lockHandler.proceed();
                    }
    			});
    		}
    		return this.lockAction;
    	},

	getImportUserAction: function() {
		if(!this.importUserAction) {
			this.importUserAction = new Ext.Action({
				text: Openwis.i18n('Security.User.Import.Button'),
				scope: this,
				handler: function() {
					new Openwis.Admin.User.ImportUser({
						listeners: {
							userImported: function() {
								this.getUserGrid().getStore().reload();
							},
							scope: this
						}
					});
				}
			});
		}
		return this.importUserAction;
	},
	
	getSearchAction: function() {
	    if(!this.searchAction) {
	        this.searchAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Search'),
				scope: this,
				handler: function() {
					var selectedGroups = this.getGroupsListbox().getValue(); 
					this.getUserStore().baseParams = {};
                   if (selectedGroups) {
                        this.getUserStore().setBaseParam(
			                'groups',
			                 selectedGroups
			            );
                   }
                   var username = this.getUsernameSearchTextField().getValue(); 
                  if (username) {
                        this.getUserStore().setBaseParam(
			                'userFilter',
			                 username
			            );
                   }
                   this.getUserStore().load();
				}
			});
		}
		return this.searchAction;
	},

     getLockAccountActionText: function(inetUserStatus) {
        if (inetUserStatus === 'ACTIVE') {
            return Openwis.i18n('Common.Btn.Lock');
        } else {
            return Openwis.i18n('Common.Btn.Unlock');
        }
     },

     canLockAccount: function(userProfile) {
        return userProfile !== 'Administrator';
     }
});