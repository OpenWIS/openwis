Ext.ns('Openwis.Utils');

Openwis.Utils.MessageBoxServiceNotAllowed = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			closeAction:'close',
			autoScroll: false,
			resizable: false,
			layout: 'table',
			title: Openwis.i18n('MessageBoxServiceNotAllowed.title'),
			height: 120,
			width: 221,
			layoutConfig: {
		        columns:1
		    },
		    items: [
			    this.getServiceNotAllowedText(),
				this.getLoginButton()
		    ]
		});
		Openwis.Utils.MessageMustLogin.superclass.initComponent.apply(this, arguments);
		this.show();
	},
	
	//----------------------------------------------------------------- Panel references.
	
	getServiceNotAllowedText: function() {
		return new Ext.Container({
				html: Openwis.i18n('MessageBoxServiceNotAllowed.msg'),
				style: {
					marginTop: '10px',
					marginLeft: '10px',
					marginRight: '10px',
					textAlign: 'center'
				}
			});
	},
	
	getLoginButton: function() {
		this.loginButton = new Ext.Panel({
			buttonAlign:"center",
			border: false,
			buttons:[new Ext.Button(this.getLoginAction())]
		});		
		return this.loginButton;
	},

	
	//----------------------------------------------------------------- Actions.
	
	getLoginAction: function() {
		if(!this.loginAction) {
			this.loginAction = new Ext.Action({
				text: 'Reconnect',
				scope: this,
				style: {
					textAlign: 'center'
				},
				handler: function() {					
				    window.location.href= configOptions.url +  "/openWisInit";
				}
			});
		}
		return this.loginAction;
	}
});