Ext.ns('Openwis.MyAccount.TrackMyRequests');

Openwis.MyAccount.TrackMyRequests.TrackMyMSSFSSSubscriptions = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.MyAccount.TrackMyRequests.TrackMyMSSFSSSubscriptions.superclass.initComponent.apply(this, arguments);
		
		//Initialize static elements.
		this.initialize();
		
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());
		
		//Create Local Requests Grid.
		this.add(new Ext.Container({
			html: Openwis.i18n('TrackMyMSSFSSSubscriptions.Local.Title'),
			cls: 'myAccountTitle2'
		}));
		this.add(this.getLocalSubscriptionsGrid());
	},
	
	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('TrackMyMSSFSSSubscriptions.Title'),
				cls: 'myAccountTitle1'
			});
		}
		return this.header;
	},
	
	getLocalSubscriptionsGrid: function() {
		if(!this.localSubscriptionsGrid) {
			this.localSubscriptionsGrid = new Openwis.MyAccount.TrackMyRequests.MyMSSFSSSubscriptionsGridPanel({
			    isLocal: true,
			    url: configOptions.locService + '/xml.follow.my.mssfss.subscriptions'
			});
		}
		return this.localSubscriptionsGrid;
	}
});