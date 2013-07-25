Ext.ns('Openwis.Admin.Availability');

Openwis.Admin.Availability.LocalAvailability = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Availability.LocalAvailability.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},

    getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.avalaibility.get',
			params: {},
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},

    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //The availability panel.
        this.add(this.getDeploymentAvailabilityPanel());
		
		this.doLayout();
		
		this.fireEvent("panelInitialized");
    },

    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Availability.Local.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },

    /**
	 *	The form panel.
	 */
	getDeploymentAvailabilityPanel: function() {
		if(!this.deploymentAvailabilityPanel) {
			this.deploymentAvailabilityPanel = new Openwis.Admin.Availability.DeploymentAvailability({
				config: this.config,
				local: true
			});
		}
		return this.deploymentAvailabilityPanel;
	}
});