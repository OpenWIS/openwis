Ext.ns('Openwis.Admin.User');

Openwis.Admin.User.Report = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.User.Report.superclass.initComponent.apply(this, arguments);

		this.initialize();
	},

	initialize: function() {
		this.add(this.getHeader());
		this.add(this.getLimitWarningLabel());
		this.add(this.getReportGrid());
	},

	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n("Security.Report.Title"),
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

	getReportGrid: function() {
	    var that = this;
		if(!this.reportGrid) {
			this.reportGrid = new Ext.grid.GridPanel({
				id: 'reportGrid',
				height: 400,
				border: true,
				store: this.getReportStore(),
				loadMask: true,
				columns: [
					{id: 'date', header:Openwis.i18n("Security.Report.Date.Column"), dataIndex:'date', sortable: true, width:100,renderer: Openwis.Utils.Date.formatDateTimeUTCfromLong},
					{id:'username', header: Openwis.i18n("Security.Report.UserName.Column"), dataIndex:'username', sortable: true, width: 180},
					{id:'action', header: Openwis.i18n("Security.Report.Action.Column"), dataIndex:'action', sortable: true, width: 100},
					{id:'attribute', header:Openwis.i18n("Security.Report.Attribute.Column"), dataIndex:'attribute', sortable: true, width: 100},
					{id:'actioner', header: Openwis.i18n("Security.Report.Actioner.Column"), dataIndex:'actioner', sortable: true, width: 180},
				],
				autoExpandColumn: 'date',
				listeners: {
					afterrender: function (grid) {
					   grid.loadMask.show();
					   grid.getStore().load();
					}
				},
            });
		}
		return this.reportGrid;
	},
	getReportStore: function() {
		if(!this.reportStore) {
			this.reportStore = new Openwis.Data.JeevesJsonStore({
				url: configOptions.locService+ '/xml.user.report',
				idProperty: 'id',
				fields: [
				    {
				        id: 'id',
				        sortType: 'asUCString'
				    },
					{
						name:'date',
					},{
						name:'username',
						sortType:'asUCString'
					},{
						name:'action',
						sortType:'asUCString'
					},{
						name:'attribute',
						sortType:'asUCString'
					},{
					    name:'actioner',
					    sortType:'asUCString'
					},
				],
				sortInfo: {
                    field: 'date',
                   direction: 'DESC'
                },
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
		return this.reportStore;
	},
});
