Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.TrackMySubscriptions = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.MyAccount.TrackMyRequests.TrackMySubscriptions.superclass.initComponent.apply(this, arguments);
		
		//Initialize static elements.
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
			html: Openwis.i18n('TrackMySubscriptions.Local.Title'),
			cls: 'myAccountTitle2'
		}));
		this.add(this.getLocalSubscriptionsGrid());
		
		//Create Remote Requests Grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('TrackMySubscriptions.Remote.Title'),
			cls: 'myAccountTitle2'
		}));
		this.add(this.getDeploymentsComboBox());
		this.add(this.getRemoteSubscriptionsGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('TrackMySubscriptions.Title'),
				cls: 'myAccountTitle1'
			});
		}
		return this.header;
	},
	
	getUserAlarmsGrid: function() {
		if (!this.userAlarmsGrid) {
			this.userAlarmsGrid = new Openwis.MyAccount.TrackMyRequests.MyUserAlarmsPanel({
				isSubscription: true
			});
		}
		return this.userAlarmsGrid;
	},

	getLocalSubscriptionsGrid: function() {
		if(!this.localSubscriptionsGrid) {
			this.localSubscriptionsGrid = new Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel({
			    isLocal: true,
			    userAlarmGridPanel: this.getUserAlarmsGrid(),
			    url: configOptions.locService + '/xml.follow.my.subscriptions'
			});
		}
		return this.localSubscriptionsGrid;
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
                emptyText: Openwis.i18n('TrackMySubscriptions.Remote.Select.Deployment'),
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
				          this.getRemoteSubscriptionsGrid().getStore().setBaseParam("deployment", record.get('name'));
				          this.getRemoteSubscriptionsGrid().getStore().load({params:{start:0, limit:Openwis.Conf.PAGE_SIZE}});
				    },
				    scope: this
				}
            });
		}
		return this.deploymentsComboBox;
	},
	
	getRemoteSubscriptionsGrid: function() {
		if(!this.remoteSubscriptionsGrid) {
			this.remoteSubscriptionsGrid = new Openwis.MyAccount.TrackMyRequests.MySubscriptionsGridPanel({
			    isLocal: false,
			    url: configOptions.locService + '/xml.follow.my.remote.subscriptions'
			});
		}
		return this.remoteSubscriptionsGrid;
	}
});