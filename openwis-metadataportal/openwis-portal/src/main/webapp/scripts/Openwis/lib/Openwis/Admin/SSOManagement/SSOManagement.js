Ext.ns('Openwis.Admin.SSOManagement');

Openwis.Admin.SSOManagement.SSOManagement = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.SSOManagement.SSOManagement.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.sso.management',
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
					
				},
				failure: function(config) {
					this.close();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());

		//Create SSO Management link form.
		this.add(this.getSSOLink());
		this.doLayout();
	},

	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Security.SSOManagement.title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
    
    getSSOLink: function() {
        return new Ext.Container({
           border: false,
           width: 500,
           html: '<a href = ' + this.config + ' target="_blank">' + Openwis.i18n('Security.SSOManagement.msg') + '</a>',
           style : {
              padding: '5px'
           }
       });
    }
});