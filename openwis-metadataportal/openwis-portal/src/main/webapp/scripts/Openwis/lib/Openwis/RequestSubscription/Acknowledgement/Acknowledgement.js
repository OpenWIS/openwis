

Ext.ns('Openwis.RequestSubscription.Acknowlegment');

Openwis.RequestSubscription.Acknowlegment.Acknowlegment = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			layout:'fit'
		});
		Openwis.RequestSubscription.Acknowlegment.Acknowlegment.superclass.initComponent.apply(this, arguments);
		
		this.addEvents("panelInitialized");
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var saveHandler = new Openwis.Handler.Save({
			url: configOptions.locService+ '/xml.create.request.subscription',
			params: this.request,
			successWindow: false,
			listeners: {
				success: function(acknowledgement) {
					this.config = acknowledgement;
					this.initialize();
				},
				scope: this
			}
		});
		saveHandler.proceed();
	},
	
	initialize: function() {
	    var msg = "";
	    
	    if(this.request.isSubscription) {
	        msg += '<p>' + Openwis.i18n('RequestSubscription.Acknowledgement.Line1.Subscription', {requestID: this.config.requestID}) + '</p>';
	        msg += '<br/>';
	        msg += '<p>' + Openwis.i18n('RequestSubscription.Acknowledgement.Line2.Subscription', {locService: configOptions.locService}) + '</p>';
	        msg += '<br/>';
	        msg += '<p>' + Openwis.i18n('RequestSubscription.Acknowledgement.Line3.Subscription') + '</p>';
	    } else {
	        msg += '<p>' + Openwis.i18n('RequestSubscription.Acknowledgement.Line1.Request', {requestID: this.config.requestID}) + '</p>';
	        msg += '<br/>';
	        msg += '<p>' + Openwis.i18n('RequestSubscription.Acknowledgement.Line2.Request', {locService: configOptions.locService}) + '</p>';
	    }
	
		this.add(new Ext.Container({
			html: msg
		}));
		
		this.fireEvent("panelInitialized");
	},
	
	validate: function() {
		return true;
	}
});