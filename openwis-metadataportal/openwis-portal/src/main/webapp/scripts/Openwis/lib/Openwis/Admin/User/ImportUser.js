Ext.ns('Openwis.Admin.User');

Openwis.Admin.User.ImportUser = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Security.User.Import.Title'),
//			layout: 'fit',
			width:600,
			height:700,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.User.ImportUser.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		this.addEvents("userImported");

		this.add(this.getFilterFormPanel());

		//-- Create Import User Grid.
		this.add(this.getImportUserGrid());

		this.show();
	},

	getImportUserStore: function() {
		if(!this.userImportStore) {
			this.userImportStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.user.allImport',
				idProperty: 'userName',
				fields: [
					{
						name:'userName'
					},{
						name:'name'
					},{
						name:'surName'
					},{
						name:'profile'
					}
				]
			});
		}
		return this.userImportStore;
	},

	getImportUserGrid: function() {
		if(!this.importUsergrid) {
			this.importUsergrid = new Ext.grid.GridPanel({
				id: 'importUserGrid',
				height: 600,
				border: true,
				store: this.getImportUserStore(),
				loadMask: true,
				columns: [
					{id:'username', header:Openwis.i18n("Security.User.UserName.Column"), dataIndex:'userName', sortable: true, width: 150},
					{id:'surname', header:Openwis.i18n("Security.User.LastName.Column"), dataIndex:'name', sortable: true, width: 150},
					{id:'name', header:Openwis.i18n("Security.User.FirstName.Column"), dataIndex:'surName', sortable: true, width: 150},
					{id:'profile', header:Openwis.i18n("Security.User.Profile.Column"), dataIndex:'profile', sortable: true, width: 150}
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
							sm.grid.ownerCt.getImportAction().setDisabled(sm.getCount() != 1 );
						},
						rowdeselect: function (sm, rowIndex, record) {
							sm.grid.ownerCt.getImportAction().setDisabled(sm.getCount() != 1);
						}
					}
				})
			});
			this.importUsergrid.addButton(new Ext.Button(this.getImportAction()));
			this.importUsergrid.addButton(new Ext.Button(this.getCancelAction()));
		}
		return this.importUsergrid;
	},



	/**
	 * The Import action.
	 */
	getImportAction: function() {
		if(!this.importAction) {
			this.importAction = new Ext.Action({
				text:Openwis.i18n('Security.User.Import.Validate.Button'),
				scope: this,
				handler: function() {
				    //Get the username to import.
					var selection = this.getImportUserGrid().getSelectionModel().getSelections();
					var params = {users: []};
					Ext.each(selection, function(item, index, allItems) {
						params.users.push({username: item.get('userName')});
					}, this);
					
				    var importHandler = new Openwis.Handler.Save({
					    url: configOptions.locService+ '/xml.user.import',
						params: params,
						listeners: {
    						success: function(config) {
    								    this.fireEvent("userImported");
    									this.close();
    								},
    						scope: this
    					}
				    });
				    importHandler.proceed();
			    }
		    });
		}
		return this.importAction;
	},
	
	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	getFilterFormPanel: function() {
	    if(!this.filterFormPanel) {
	        this.filterFormPanel = new Ext.form.FormPanel({
	            border: false,
	            buttonAlign: 'center',
	            labelWidth: 200
	        });
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
	
	getSearchAction: function() {
	    if(!this.searchAction) {
	        this.searchAction = new Ext.Action({
	            text: Openwis.i18n('Common.Btn.Search'),
				scope: this,
				handler: function() {
    			  this.getImportUserStore().baseParams = {};
                  var username = this.getUsernameSearchTextField().getValue(); 
                  if (username) {
                        this.getImportUserStore().setBaseParam(
			                'userFilter',
			                 username
			            );
                   }
                   this.getImportUserStore().load();
				}
			});
		}
		return this.searchAction;
	}
});