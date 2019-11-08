Ext.ns('Openwis.Admin.Availability');

Openwis.Admin.Availability.DeploymentAvailability = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Availability.DeploymentAvailability.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},

    initialize: function() {
        //Create System Config form.
        this.getAvailabilityFormPanel().add(this.getMetadataServiceFieldSet());
		this.getAvailabilityFormPanel().add(this.getDataServiceFieldSet());
		this.getAvailabilityFormPanel().add(this.getSecurityServiceFieldSet());
		this.add(this.getAvailabilityFormPanel());
		this.doLayout();
		this.fireEvent("panelInitialized");
    },

    /**
	 *	The form panel.
	 */
	getAvailabilityFormPanel: function() {
		if(!this.availabilityFormPanel) {
			this.availabilityFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				border: false,
				labelWidth: 150
			});
		}
		return this.availabilityFormPanel;
	},

    getMetadataServiceFieldSet: function() {
        if(!this.metadataServiceFieldSet) {
            this.metadataServiceFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('Availability.MetadataService'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true,
    			items: 
    			[	
    				this.getServicePanel(this.config.metadataServiceAvailability.userPortal, Openwis.i18n('Availability.MetadataService.UserPortal'), "userPortal", "Metadata"),
    				this.getServicePanel(this.config.metadataServiceAvailability.synchronization, Openwis.i18n('Availability.MetadataService.Synchro'), "synchronization", "Metadata"),
    				this.getServicePanel(this.config.metadataServiceAvailability.harvesting, Openwis.i18n('Availability.MetadataService.Harvesting'), "harvesting", "Metadata"),
    				this.getServicePanel(this.config.metadataServiceAvailability.indexing, Openwis.i18n('Availability.MetadataService.Indexing'), "indexing", "Metadata")
    			]
            });
        }
        return this.metadataServiceFieldSet;
    },
    
    getDataServiceFieldSet: function() {
        if(!this.dataServiceFieldSet) {
            this.dataServiceFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('Availability.DataService'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true,
    			items: 
    			[
    				this.getServicePanel(this.config.dataServiceAvailability.replicationProcess, Openwis.i18n('Availability.DataService.ReplicationProcess'), "replication", "Data"),
    				this.getServicePanel(this.config.dataServiceAvailability.ingestion, Openwis.i18n('Availability.DataService.Ingestion'), "ingestion", "Data"),
    				this.getServicePanel(this.config.dataServiceAvailability.subscriptionQueue, Openwis.i18n('Availability.DataService.SubscriptionQueue'), "subscriptionProcessing", "Data"),
    				this.getServicePanel(this.config.dataServiceAvailability.disseminationQueue, Openwis.i18n('Availability.DataService.DisseminationQueue'), "dissemination", "Data")    				
    			]
            });

        }
        return this.dataServiceFieldSet;
    },
    
    getSecurityServiceFieldSet: function() {
        if(!this.securityServiceFieldSet) {
            this.securityServiceFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('Availability.SecurityService'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true,
    			items: 
    			[
	    			this.getServicePanel(this.config.securityServiceAvailability.securityService, Openwis.i18n('Availability.SecurityService'), "Security"),
	    			this.getServicePanel(this.config.securityServiceAvailability.ssoService, Openwis.i18n('Availability.SecurityService.SSO'), "Security")
    			]
            });
        }
        return this.securityServiceFieldSet;
    },

    getServicePanel: function(service, serviceLabel, serviceName, serviceType) {
    	var value = Openwis.Admin.Availability.DeploymentAvailabilityUtils.getAvailabilityRenderer(service, serviceName);

    	var serviceAvailability = {
					xtype: 'displayfield',
		    		cls: Openwis.Admin.Availability.DeploymentAvailabilityUtils.clsAvailability(service), 
		    		value: value, 
		    		fieldLabel: serviceLabel,
		    		width : 420
					};
		
		var items = {};
		if (Openwis.Admin.Availability.DeploymentAvailabilityUtils.isStartStopButtonDisplayed(this.local, serviceType, serviceName, service)) {
			items = [serviceAvailability, new Ext.Button(this.getStartStopServiceAction(serviceName, service, serviceType))];
		} else {
			items = [serviceAvailability];
		}
					
    	var servicePanel = new Ext.form.CompositeField({
				items: items
    	});
    	return servicePanel;
    },
    
    getStartStopServiceAction: function(serviceName, service, serviceType) {
		var params = {};
		params.serviceName = serviceName;
		
		// Set button label
		var text = {};
		if (Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(service)) {
			text = 'Stop';
		} else {
			text = 'Start';
		}
		
		//Set url
		var url = {};
		if (serviceType == 'Metadata') {
			url = configOptions.locService+ '/xml.backup.start.stop.metadata.service';
		} else {
			url = configOptions.locService+ '/xml.backup.start.stop.data.service';
		}

		this.startStopMetadataServiceAction = new Ext.Action({
			text: text,
			handler: function() {
				// Set button label
				params.started = Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(service);
				new Openwis.Handler.Get({
					button: this,
				    url: url,
        			params: params,
        			listeners: {
        				success: function(config) {
        					// Set button label
							if (Openwis.Admin.Availability.DeploymentAvailabilityUtils.isServiceStarted(config)) {
								this.setText('Stop');
							} else {
								this.setText('Start');
							}
							service = config;
							//Refresh the status level
					    	this.ownerCt.items.items[0].setValue(Openwis.Admin.Availability.DeploymentAvailabilityUtils.getAvailabilityRenderer(config, serviceName));
					    	// remove all possible classes, before adding the right one
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelUp');
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelWarn');
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelDown');
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelUnknown');
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelStopped');
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelAllSuspended');
					    	this.ownerCt.items.items[0].removeClass('availabilityLevelNone');
					    	this.ownerCt.items.items[0].addClass(Openwis.Admin.Availability.DeploymentAvailabilityUtils.clsAvailability(config));
        					this.ownerCt.items.items[0].show();
        				}
        				,
        				scope: this
        			}
				}).proceed();
			}
		});
		return this.startStopMetadataServiceAction;
	}
});