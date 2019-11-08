Ext.ns('Openwis.RegistrationUser');

Openwis.RegistrationUser.RegistrationUserSuccessful = Ext.extend(Ext.Window, {

	initComponent: function() {
		Ext.apply(this, 
		{
			closeAction:'close',
			autoScroll: false,
			resizable: false,
			closable: false,
			layout: 'table',
			title: Openwis.i18n('RegistrationUserSuccessful.title'),
			layoutConfig: {
		        columns:2
		    },
		    items: [
			    this.getSuccessText(),
				this.getLoginButton(),
				this.getHomePageButton()
		    ]
		});
		Openwis.RegistrationUser.RegistrationUserSuccessful.superclass.initComponent.apply(this, arguments);
		this.show();
	},
	
	//----------------------------------------------------------------- Panel references.
	
	getSuccessText: function() {
		return new Ext.Container({
				html:Openwis.i18n('RegistrationUserSuccessful.msg'),
				colspan: 2,
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
	
	getHomePageButton: function() {
		this.homePageButton = new Ext.Panel({
			buttonAlign:"center",
			border: false,
			buttons:[new Ext.Button(this.getHomePageAction())]
		});		
		return this.homePageButton;
	},
	
	//----------------------------------------------------------------- Actions.
	
	getLoginAction: function() {
		if(!this.loginAction) {
			this.loginAction = new Ext.Action({
				text: Openwis.i18n('RegistrationUserSuccessful.redirect.login'),
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
	},
	
	getHomePageAction: function() {
		if(!this.homePageButton) {
			this.homePageButton = new Ext.Action({
				text: Openwis.i18n('RegistrationUserSuccessful.redirect.homepage'),
				scope: this,
				style: {
					textAlign: 'center'
				},
				handler: function() {
					window.location.href= configOptions.locService +'/main.home';
				}
			});
		}
		return this.homePageButton;
	}
	
});