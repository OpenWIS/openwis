Ext.ns('Openwis.Admin.Availability');

Openwis.Admin.Availability.RemoteAvailability = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.Availability.RemoteAvailability.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},

    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //The availability panel.
        this.add(this.getDeploymentsComboBox());
		
		this.doLayout();
		
		this.fireEvent("panelInitialized");
    },

    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Availability.Remote.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
	
	getDeploymentsComboBox: function() {
		if(!this.deploymentsComboBox) {
			var deploymentStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.get.all.backup.centres',
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
				selectOnFocus:true,
				width: 200,
				listeners: {
				    select: function(combo, record, index) {
				        var logicalRemoteDeploymentName = this.getDeploymentsComboBox().getValue();
				        var getHandler = new Openwis.Handler.Get({
                			url: configOptions.locService+ '/xml.avalaibility.remote.get',
                			params: {content: logicalRemoteDeploymentName},
                			listeners: {
                				success: function(config) {
                				    if(this.deploymentAvailabilityPanel) {
                				        this.remove(this.deploymentAvailabilityPanel);
                				        this.deploymentAvailabilityPanel = null;
                				        this.remove(this.switchToBackupModeInfo);
                				        this.switchToBackupModeInfo = null;
                				        this.remove(this.switchToBackupModeButton);
                				        this.switchToBackupModeButton = null;
                				    }
                					this.deploymentAvailabilityPanel = new Openwis.Admin.Availability.DeploymentAvailability({
                        				config: config.deploymentAvailability
                        			});
                        			this.add(this.getDeploymentAvailabilityPanel());
                        			this.getSwitchToBackupModeInfo().setInitialState(config.backupedByLocalServer);
                        			this.add(this.getSwitchToBackupModeInfo());
                        			this.getSwitchToBackupModeButton().setInitialState(config.backupedByLocalServer);
                        			this.add(this.getRetroProcessHourContainer());
                        			
                        			if (!this.retroProcessHourField) {
	                        			this.getRetroProcessHourContainer().add(new Ext.form.Label({
	                        	    		html : Openwis.i18n('Availability.Remote.retro.process.start') 
	                        	    	}));
	                        			this.getRetroProcessHourContainer().add(this.getRetroProcessHourField());
	                        			this.getRetroProcessHourContainer().add(new Ext.form.Label({
	                        				html : Openwis.i18n('Availability.Remote.retro.process.end')
	                        	    	}));
                        			}

                        			this.getRetroProcessHourContainer().setInitialState(config.backupedByLocalServer);

                        			this.add(this.getSwitchToBackupModeButton());
                        			this.doLayout();
                        			this.fireEvent("panelInitialized");
                				},
                				scope: this
                			}
                		});
                		getHandler.proceed();
				    },
				    scope: this
				}
            });
		}
		return this.deploymentsComboBox;
	},

    /**
	 *	The form panel.
	 */
	getDeploymentAvailabilityPanel: function() {
		if(!this.deploymentAvailabilityPanel) {
			this.deploymentAvailabilityPanel = new Openwis.Admin.Availability.DeploymentAvailability({
				config: this.config,
				local: false
			});
		}
		return this.deploymentAvailabilityPanel;
	},
	
	getSwitchToBackupModeInfo: function() {
	    if(!this.switchToBackupModeInfo) {
            this.switchToBackupModeInfo = new Ext.Container({
                border: false,
                setInitialState: function(isBackupSwitchedOn) {
                    if(isBackupSwitchedOn) {
                        this.html = Openwis.i18n('Availability.Remote.SwitchToBackupInfo.BackupModeOn');
                        this.addClass('backupModeOn');
                        this.removeClass('backupModeOff');
                    } else {
                        this.html = Openwis.i18n('Availability.Remote.SwitchToBackupInfo.BackupModeOff');
                        this.addClass('backupModeOff');
                        this.removeClass('backupModeOn');
                    }
                }
            });
        }
        return this.switchToBackupModeInfo;
	},
	
	getSwitchToBackupModeButton: function() {
	    if(!this.switchToBackupModeButton) {
            this.switchToBackupModeButton = new Ext.Button({
                text: Openwis.i18n('Availability.Remote.SwitchToBackupBtn'),
                scope: this,
                handler: function() {
                	// check retro process hour validity
                	if (!this.getRetroProcessHourField().isValid()) {
                		Ext.Msg.alert('Error', this.getRetroProcessHourField().invalidText);
                		return
                	}
                	
                    var deploymentName = this.getDeploymentsComboBox().getValue();
                    var isBackupSwitchedOn = this.getSwitchToBackupModeButton().isBackupSwitchedOn;
                    var msg = "";
                    if(isBackupSwitchedOn) {
                        msg = Openwis.i18n('Availability.Remote.SwitchToBackupTurnOffConfirmation', {deploymentName: deploymentName});
                    } else {
                        msg = Openwis.i18n('Availability.Remote.SwitchToBackupTurnOnConfirmation', {deploymentName: deploymentName});
                    }
                    var h = this.getRetroProcessHourField().getValue();
                    if (!h) {
                    	h = 2;
                    }
                    Ext.MessageBox.confirm(Openwis.i18n('Common.Confirm.Title'), msg, function(btnClicked) {
            				if(btnClicked == 'yes') {
            					var saveHandler = new Openwis.Handler.Save({
        							url: configOptions.locService+ '/xml.avalaibility.switch.to.backup',
        							params: {
        							    deploymentName: deploymentName,
        							    switchedOn: !isBackupSwitchedOn,
        							    hour: h
        							},
        							listeners: {
        								success: function(config) {
        								    this.getDeploymentsComboBox().fireEvent("select");
        								},
        								scope: this
        							}
        						});
        						saveHandler.proceed();
            				}
            			}, 
            			this
            		);
                },
                setInitialState: function(isBackupSwitchedOn) {
                    this.isBackupSwitchedOn = isBackupSwitchedOn;
                    if(isBackupSwitchedOn) {
                        this.setText(Openwis.i18n('Availability.Remote.SwitchToBackupOffBtn'));
                    } else {
                        this.setText(Openwis.i18n('Availability.Remote.SwitchToBackupOnBtn'));
                    }
                }
            });
        }
        return this.switchToBackupModeButton;
	},
	
	getRetroProcessHourContainer : function() {
	    if(!this.retroProcessHourContainer) {
	    	this.retroProcessHourContainer = new Ext.Container({
                border: false,
                setInitialState: function(isBackupSwitchedOn) {
                	if (isBackupSwitchedOn) {
                		this.disable();
                	} else {
                		this.enable();
                	}
                }
            });
	    }
	    return this.retroProcessHourContainer;
	},
	getRetroProcessHourField: function() {
		if(!this.retroProcessHourField) {
			this.retroProcessHourField = new Ext.form.NumberField({
				width: 20,
            	name: 'hour',
            	allowDecimals:false,
            	allowNegative:false,
            	minValue: 0,
            	maxValue:6,
            	value:2,
                setInitialState: function(isBackupSwitchedOn) {
                	if (isBackupSwitchedOn) {
                		this.disable();
                	} else {
                		this.enable();
                	}
                }
			});
		}
		return this.retroProcessHourField;
	}
});