Ext.ns('Openwis.Utils');

Openwis.Utils.MessageMustLogin = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			closeAction:'close',
			autoScroll: false,
			resizable: false,
			layout: 'table',
			title: Openwis.i18n('MessageMustLogin.title'),
			height: 145,
			width: 221,
			layoutConfig: {
		        columns:1
		    },
		    items: [
			    this.getLoginText(),
				this.getLoginButton(),
				this.getHaveAccountText(),
		    	this.getHaveAccountLink()
		    ]
		});
		Openwis.Utils.MessageMustLogin.superclass.initComponent.apply(this, arguments);
		this.show();
	},
	
	//----------------------------------------------------------------- Panel references.
	
	getLoginText: function() {
		return new Ext.Container({
				html: Openwis.i18n('MessageMustLogin.msg'),
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
	
	getHaveAccountText: function() {
		var htmlTxt = Openwis.i18n('MessageMustLogin.register.msg');
		// selfRegistrationEnabled initialized in header-common.jsp
		if (!selfRegistrationEnabled) {
			htmlTxt = '';
		}
		return new Ext.Container({
				html: htmlTxt,
				style: {
					marginTop: '10px',
					textAlign: 'center'
				}
			});
	},
	
	getHaveAccountLink: function() {
		var htmlTxt = '<a href="'+ configOptions.locService +'/user.register.get">Register</a>';
		// selfRegistrationEnabled initialized in header-common.jsp
		if (!selfRegistrationEnabled) {
			htmlTxt = '';
		}
		return new Ext.Container({
			html: htmlTxt,
			style: {
				textAlign: 'center'
			}
		});
	},
	
	//----------------------------------------------------------------- Actions.
	
	getLoginAction: function() {
		if(!this.loginAction) {
			this.loginAction = new Ext.Action({
				text: 'Login',
				scope: this,
				style: {
					textAlign: 'center'
				},
				handler: function() {					
				    window.location.href= configOptions.url +  "/openWisInit?lang=" + Env.lang;
				}
			});
		}
		return this.loginAction;
	}
});