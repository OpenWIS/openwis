Ext.ns('Openwis.Admin.Category');

Openwis.Admin.DataService.Blacklist = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.DataService.Blacklist.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());

		// create search text form
        this.add(this.getSearchFormPanel());

		//Create Category grid.
		this.add(this.getBlacklistGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Blacklist.Administration.Title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
	/****************************/
    /*  Search text form Panel  */
    
    getSearchFormPanel: function() {
        if(!this.searchFormPanel) {
            this.searchFormPanel = new Ext.form.FormPanel({
                labelWidth: 100,
                border: false,
                buttonAlign: 'center'
            });
            this.searchFormPanel.add(this.getSearchTextField());
            this.searchFormPanel.addButton(new Ext.Button(this.getSearchAction()));
            this.searchFormPanel.addButton(new Ext.Button(this.getResetAction()));
        }
        return this.searchFormPanel;
    },

    // The text field for the text search accross metadata.
    getSearchTextField: function() {
        if(!this.searchTextField) {
            this.searchTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Blacklist.UserName.Search'),
                name: 'any',
                enableKeyEvents: true, // is that the best? or change event?
                width: 150,
                listeners: {
                    keyup: function(){
                        var searchOn = Ext.isEmpty(this.getSearchTextField().getValue().trim()); 
                        this.getSearchAction().setDisabled(searchOn);
                        this.getResetAction().setDisabled(searchOn);
                        if (searchOn) {
                            this.getBlacklistStore().setBaseParam(
                                this.getSearchTextField().getName(),
                                this.getSearchTextField().getValue()
                            );
                            this.getBlacklistStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                        }
                    },
                    specialkey: function(f,e){
	                    if (e.getKey() == e.ENTER) {
	                      this.getSearchAction().execute();
	                    }
	                  },
                    scope: this
                }
            });
        }
        return this.searchTextField;
    },

    // Performs search
    getSearchAction: function() {
        if (!this.searchAction) {
            this.searchAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Btn.Search'),
                scope: this,
                handler: function() {
                    this.getBlacklistStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getBlacklistStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                }
            });
        }
        return this.searchAction;
    },

    // Reset search and update grid
    getResetAction: function() {
        if (!this.resetAction) {
            this.resetAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Btn.Reset'),
                scope: this,
                handler: function() {
                    this.getSearchTextField().setValue('');
                    this.getBlacklistStore().setBaseParam(
                        this.getSearchTextField().getName(),
                        this.getSearchTextField().getValue()
                    );
                    this.getBlacklistStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    this.getSearchAction().setDisabled(true);
                    this.getResetAction().setDisabled(true);
                }
            });
        }
        return this.resetAction;
    },

	//-- Grid and Store.
	
	getBlacklistGrid: function() {
		if(!this.blacklistGrid) {
			this.blacklistGrid = new Ext.grid.GridPanel({
				id: 'blacklistGrid',
				height: 400,
				border: true,
				store: this.getBlacklistStore(),
				loadMask: true,
				columns: [
					{id:'user', header:Openwis.i18n('Blacklist.user'), dataIndex:'user', sortable: true, width: 120},
					{id:'nbDisseminationWarnThreshold', header:Openwis.i18n('Blacklist.nbWarn'), dataIndex:'nbDisseminationWarnThreshold', sortable: true, width: 90},
					{id:'volDisseminationWarnThreshold', header:Openwis.i18n('Blacklist.volWarn'), dataIndex:'volDisseminationWarnThreshold', sortable: true, width: 90},
					{id:'nbDisseminationBlacklistThreshold', header:Openwis.i18n('Blacklist.nbBlacklist'), dataIndex:'nbDisseminationBlacklistThreshold', sortable: true, width: 90},
					{id:'volDisseminationBlacklistThreshold', header:Openwis.i18n('Blacklist.volBlacklist'), dataIndex:'volDisseminationBlacklistThreshold', sortable: true, width: 90},
					{id:'userDisseminatedDataDTO.dissToolNbFiles', header:Openwis.i18n('Blacklist.nbCurrent'), dataIndex:'userDisseminatedDataDTO.dissToolNbFiles', width: 90},
					{id:'userDisseminatedDataDTO.dissToolSize', header:Openwis.i18n('Blacklist.volCurrent'), dataIndex:'userDisseminatedDataDTO.dissToolSize', width: 90},
					{id:'blacklisted', header:Openwis.i18n('Blacklist.blacklist'), dataIndex:'blacklisted', sortable: true, width: 50, renderer: this.renderBlacklistState}
				],
				//autoExpandColumn: 'user',
				listeners: { 
                    afterrender: function (grid) {
                       grid.loadMask.show();
                       grid.getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
                    },
                    scope:this
                },
				sm: new Ext.grid.RowSelectionModel({
					listeners: { 
						rowselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
						},
                        rowdeselect: function (sm, rowIndex, record) {
                        	sm.grid.ownerCt.getEditAction().setDisabled(sm.getCount() != 1);
                        }
					}
				}),
                // paging bar on the bottom
                bbar: new Ext.PagingToolbar({
                    pageSize: Openwis.Conf.PAGE_SIZE,
                    store: this.getBlacklistStore(),
                    displayInfo: true,
                    beforePageText: Openwis.i18n('Common.Grid.BeforePageText'),
    		        afterPageText: Openwis.i18n('Common.Grid.AfterPageText'),
    		        firstText: Openwis.i18n('Common.Grid.FirstText'),
    		        lastText: Openwis.i18n('Common.Grid.LastText'),
    		        nextText: Openwis.i18n('Common.Grid.NextText'),
    		        prevText: Openwis.i18n('Common.Grid.PrevText'),
    		        refreshText: Openwis.i18n('Common.Grid.RefreshText'),
                    displayMsg: Openwis.i18n('Common.Grid.Range'),
                    emptyMsg: Openwis.i18n('Common.Grid.No.Data')
                })
			});
			this.blacklistGrid.addButton(new Ext.Button(this.getEditAction()));
		}
		return this.blacklistGrid;
	},
	
	getBlacklistStore: function() {
		if(!this.blacklistStore) {
			this.blacklistStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.blacklist.all',
				remoteSort: true,
				root: 'allBlackList',
                totalProperty: 'count',
				idProperty: 'id',
				fields: [
					{
						name:'id'
					},{
						name:'user',
						sortType: Ext.data.SortTypes.asUCString
					},{
						name:'nbDisseminationWarnThreshold'
					},{
						name:'volDisseminationWarnThreshold'
					},{
						name:'nbDisseminationBlacklistThreshold'
					},{
						name:'volDisseminationBlacklistThreshold'
					},{
						name:'userDisseminatedDataDTO.dissToolNbFiles'
					},{
						name:'userDisseminatedDataDTO.dissToolSize'
//					},{
//						name:'status'
					},{
						name:'blacklisted'
					}
				],
				sortInfo: {
				    field: 'user',
				    direction: 'ASC'
				}

			});
		}
		return this.blacklistStore;
	},
	
	//-- Utilities
    renderBlacklistState: function(value) {
        return (value) ? Openwis.i18n('Blacklist.renderBlacklistState.True'): Openwis.i18n('Blacklist.renderBlacklistState.False');
    },
	
	//-- Actions implemented on Data Policy Administration.
	
	getEditAction: function() {
		if(!this.editAction) {
			this.editAction = new Ext.Action({
				disabled: true,
				text: Openwis.i18n('Common.Btn.Edit'),
				scope: this,
				handler: function() {
					var selectedRec = this.getBlacklistGrid().getSelectionModel().getSelected();
					new Openwis.Admin.DataService.EditBlacklist({
						operationMode: 'Edit',
						selectedRec: selectedRec.json,
						listeners: {
							blacklistSaved: function() {
								this.getBlacklistGrid().getStore().reload();
							},
							scope: this
						}
					});
				}
			});
		}
		return this.editAction;
	}
});