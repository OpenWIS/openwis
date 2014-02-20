Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.TrackMyAdhocs = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.MyAccount.TrackMyRequests.TrackMyAdhocs.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create User Alarm Grid
		this.add(new Ext.Container({
			html: Openwis.i18n('TrackMyRequests.UserAlarms.Title'),
			cls: 'myAccountTitle2'
		}));
		this.add(this.getUserAlarmsGrid());

		//Create Local Requests Grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('TrackMyRequests.Local.Title'),
			cls: 'myAccountTitle2'
		}));
		this.add(this.getLocalRequestsGrid());
		
		//Create Remote Requests Grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('TrackMyRequests.Remote.Title'),
			cls: 'myAccountTitle2'
		}));
		this.add(this.getDeploymentsComboBox());
		this.add(this.getRemoteRequestsGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('TrackMyRequests.Title'),
				cls: 'myAccountTitle1'
			});
		}
		return this.header;
	},
	
	getUserAlarmsGrid: function() {
		if (!this.userAlarmsGrid) {
			this.userAlarmsGrid = new Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel({
				isSubscription: false
			});
		}
		return this.userAlarmsGrid;
	},

	getLocalRequestsGrid: function() {
		if(!this.localRequestsGrid) {
			this.localRequestsGrid = new Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel({
			    isLocal: true,
			    userAlarmGridPanel: this.getUserAlarmsGrid()
			});
		}
		return this.localRequestsGrid;
	},
	
	getDeploymentsComboBox: function() {
		if(!this.deploymentsComboBox) {
			var deploymentStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.deployment.cot.all',
                idProperty: 'name',
                fields: [
                    {
                        name:'name'
                    }
                ]
            });
        
            this.deploymentsComboBox = new Ext.form.ComboBox({
                store: deploymentStore,
				valueField: 'name',
				displayField:'name',
                name: 'deployment',
                emptyText: Openwis.i18n('TrackMyRequests.Remote.Select.Deployment'),
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				style: {
                    margin: '0px 0px 0px 30px'
                },
				selectOnFocus:true,
				width: 200,
				listeners: {
				    select: function(combo, record, index) {
                        this.getRemoteRequestsGrid().getStore().setBaseParam("deployment", record.get('name'));
				        this.getRemoteRequestsGrid().getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
				    },
				    scope: this
				}
            });
		}
		return this.deploymentsComboBox;
	},
	
	getRemoteRequestsGrid: function() {
		if(!this.remoteRequestsGrid) {
			this.remoteRequestsGrid = new Openwis.MyAccount.TrackMyRequests.MyAdhocsGridPanel({
			    isLocal: false
			});
		}
		return this.remoteRequestsGrid;
	}
});