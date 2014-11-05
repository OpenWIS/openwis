Ext.ns('Openwis.Admin.Synchro');

Openwis.Admin.Synchro.Manage = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Synchro.Manage.Title'),
			layout: 'fit',
			width:500,
			height:620,
			modal: true,
			border: false,
			autoScroll: true,
			closeAction:'close'
		});
		Openwis.Admin.Synchro.Manage.superclass.initComponent.apply(this, arguments);
		
		if(this.isEdition()) {
			this.getInfosAndInitialize();
		} else {
			this.initialize();
		}
	},
	
	getInfosAndInitialize: function() {
		var params = this.editTaskId;
		
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.harvest.get',
			params: params,
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
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add data policy saved event.
		this.addEvents("synchroTaskSaved");
		
		//-- Create form panel.
		this.add(this.getTaskFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		this.getLocalCategoryStore().load();
		
		//-- If specified, populate forms from specified data policy.
		if(this.isEdition()) {
			this.getNameTextField().setValue(this.config.name);
			
			this.getUrlTextField().setValue(this.config.configuration.url);
			
			var username = this.config.configuration.userName;
			var password = this.config.configuration.password;
			if((username && username.trim() != "") || (password && password.trim() != "")) {
			    this.getUseAccountCheckBox().setValue(true);
			    this.getUsernameTextField().setValue(username);
		        this.getPasswordTextField().setValue(password);
			    this.getUsernameTextField().show();
			    this.getPasswordTextField().show();
			} else {
			    this.getUseAccountCheckBox().setValue(false);
			}
			
			this.getDateFrom().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.configuration.dateFrom));
			this.getDateTo().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.configuration.dateTo));
			
		    var recurrencePeriod = this.config.runMode.recurrentPeriod;

		    var recurrencePeriodHour = recurrencePeriod /3600;
		    
		    if ( recurrencePeriodHour % 24 == 0 ) {
		    	this.getRecurrentProcessingFrequencyCombobox().setValue("DAY");
		    	this.getRecurrentProcessingNumberField().setValue(recurrencePeriodHour/24);
		    } else {
		    	this.getRecurrentProcessingFrequencyCombobox().setValue("HOUR");
		    	this.getRecurrentProcessingNumberField().setValue(recurrencePeriodHour);
		    	
		    }
		    
//		    var days = Math.floor(recurrencePeriod / (24 * 3600));
//		    if(days > 0) {
//		        recurrencePeriod %= (24 * 3600);
//		        this.getFrequencyRecurrentDayTextField().setValue(days);
//		    }
//		    
//		    var hours = Math.floor(recurrencePeriod / 3600);
//		    if(hours > 0) {
//	            this.getFrequencyRecurrentHourTextField().setValue(hours);
//		        recurrencePeriod %= 3600;
//		    }
//		    
//		    var minuts = recurrencePeriod / 60;
//		    if(minuts > 0) {
//		        this.getFrequencyRecurrentMinuteTextField().setValue(minuts);
//		    }
			
		    this.getStartingDateField().setValue(Openwis.Utils.Date.ISODateToCalendar(this.config.runMode.startingDate));
		    this.getStartingDateTimeField().setValue(Openwis.Utils.Date.ISODateToTime(this.config.runMode.startingDate));
		    
			this.getValidationCombobox().setValue(this.config.validationMode);
	    
    	    if(this.config.backup) {
    	        this.getBackupsCombobox().setValue(this.config.backup.name);
    	    }
	    }
		
		this.show();
	},
	
	getTaskFormPanel: function() {
		if(!this.taskFormPanel) {
			this.taskFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 80,
				border: false
			});
			this.taskFormPanel.add(this.getNameTextField());
			this.taskFormPanel.add(this.getUrlTextField());
			this.taskFormPanel.add(this.getUseAccountCheckBox());
			this.taskFormPanel.add(this.getUseAccountCompositeField());
            this.taskFormPanel.add(this.getDateInterval());
			this.taskFormPanel.add(this.getProviderConfigurationFieldSet());
			this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
			this.taskFormPanel.add(this.getBackupsCombobox());
			this.taskFormPanel.add(this.getValidationCombobox());
		}
		return this.taskFormPanel;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getNameTextField: function() {
		if(!this.nameTextField) {
			this.nameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.Name'),
				name: 'name',
				allowBlank:false,
				width: 150
			});
		}
		return this.nameTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getUrlTextField: function() {
		if(!this.urlTextField) {
			this.urlTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.URL'),
				name: 'url',
				allowBlank:false,
				width: 300
			});
		}
		return this.urlTextField;
	},
	
	/**
	 * The text area for the group global status.
	 */
	getUseAccountCheckBox: function() {
		if(!this.useAccountCheckBox) {
			this.useAccountCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('Harvesting.Account.Use'),
				name: 'useAccount',
				width: 125,
    			listeners : {
    				check: function(checkbox, checked) {
    				    if(!checked) {
    					    this.getUseAccountCompositeField().hide();
    				    } else {
    					    this.getUseAccountCompositeField().show();
    				    }
    				},
    				scope: this
    			}
			});
		}
		return this.useAccountCheckBox;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getUseAccountCompositeField: function() {
		if(!this.useAccountCompositeField) {
			this.useAccountCompositeField = new Ext.form.CompositeField({
				name: 'useAccount',
				hidden: true,
				allowBlank:false,
				width: 330,
				items:
				[
				    new Ext.Container({
        				border: false,
        				html: Openwis.i18n('Harvesting.Account.UserName') + ':',
        				cls: 'formItems'
        			}),
				    this.getUsernameTextField(),
				    new Ext.Container({
        				border: false,
        				html: Openwis.i18n('Harvesting.Account.Password') + ':',
        				cls: 'formItems'
        			}),
				    this.getPasswordTextField()
				]
			});
		}
		return this.useAccountCompositeField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getUsernameTextField: function() {
		if(!this.usernameTextField) {
			this.usernameTextField = new Ext.form.TextField({
				name: 'username',
				allowBlank:true,
				width: 100
			});
		}
		return this.usernameTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getPasswordTextField: function() {
		if(!this.passwordTextField) {
			this.passwordTextField = new Ext.form.TextField({
				name: 'password',
				allowBlank:true,
				inputType: 'password',
				width: 100
			});
		}
		return this.passwordTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getFrequencyRunModeCompositeField: function() {
		if(!this.frequencyRunModeCompositeField) {
			this.frequencyRunModeCompositeField = new Ext.form.FieldSet({
			//this.frequencyRunModeCompositeField = new Ext.form.CompositeField({
				name: 'recurrentRunMode',
				hidden: false,
				allowBlank:false,
				fieldLabel: Openwis.i18n('Harvesting.Options.RunMode.Recurrent.Frequency'),
				width: 400,
				items:
					[
					    new Ext.Container({
	        				border: false,
	        				html: Openwis.i18n('Harvesting.Options.RunMode.Recurrent.Frequency') + ':',
	        				cls: 'formItems'
	        			}),
					    //this.getFrequencyRecurrentDayTextField(),
					    //this.getFrequencyRecurrentHourTextField(),
					    //this.getFrequencyRecurrentMinuteTextField(),
					    this.getStartingDateCompositeField(),
					    this.getRecurrentProcessingCompositeField()
//					    new Ext.Container({
//	        				border: false,
//	        				html: Openwis.i18n('Harvesting.Options.RunMode.Recurrent.Frequency.Detail'),
//	        				cls: 'formItems'
//	        			})
					]
			});
		}
		return this.frequencyRunModeCompositeField;
	},
	
	getFrequencyRecurrentDayTextField: function() {
		if(!this.frequencyRecurrentDayTextField) {
			this.frequencyRecurrentDayTextField = new Ext.form.TextField({
				name: 'day',
        		allowBlank:true,
        		width: 50
			});
		}
		return this.frequencyRecurrentDayTextField;
	},
	
	getFrequencyRecurrentHourTextField: function() {
		if(!this.frequencyRecurrentHourTextField) {
			this.frequencyRecurrentHourTextField = new Ext.form.TextField({
				name: 'hour',
        		allowBlank:true,
        		width: 50
			});
		}
		return this.frequencyRecurrentHourTextField;
	},
	
	getFrequencyRecurrentMinuteTextField: function() {
		if(!this.frequencyRecurrentMinuteTextField) {
			this.frequencyRecurrentMinuteTextField = new Ext.form.TextField({
				name: 'minute',
        		allowBlank:true,
        		width: 50
			});
		}
		return this.frequencyRecurrentMinuteTextField;
	},
	
    getLocalCategoryStore: function() {
        if(!this.categoryStore) {
            this.categoryStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.category.all',
    			idProperty: 'id',
    			autoLoad: true,
    			fields: [
    				{
    					name:'id'
    				},{
    					name:'name'
    				}
    			]
    		});
        }
        return this.categoryStore;
    },	
    
    getBackupsCombobox: function() {
        if(!this.backupsCombobox) {
            var backupsStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.get.all.backup.centres',
    			idProperty: 'name',
    			autoLoad: true,
    			fields: [
    			    {
    					name:'name'
    				}
    			],
    			listeners: {
    			    load: function(store, records, options) {
        			    if(this.isEdition() && this.config.backup) {
                            this.getBackupsCombobox().setValue(this.config.backup.name);
        			    }
        			},
        			scope: this
    			}
    		});
        
            this.backupsCombobox = new Ext.form.ComboBox({
                fieldLabel: Openwis.i18n('Harvesting.Backup'),
                store: backupsStore,
				valueField: 'name',
				displayField:'name',
                name: 'backups',
				mode: 'local',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200,
				height: 120
            });
        }
        return this.backupsCombobox;
    },	
    
    getValidationCombobox: function() {
        if(!this.validationCombobox) {
            this.validationCombobox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['NONE', Openwis.i18n('Metadata.Validation.None')], 
					    ['XSD_ONLY', Openwis.i18n('Metadata.Validation.XsdOnly')], 
					    ['FULL',     Openwis.i18n('Metadata.Validation.Full')] 
					]
				}),
				valueField: 'id',
				displayField:'value',
				value: 'NONE',
                name: 'validationMode',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200,
				fieldLabel: Openwis.i18n('Harvesting.Validation')
            });
        }
        return this.validationCombobox;
    },
    
    /**
	 * The text field for the data policy name.
	 */
	getDateInterval: function() {
		if(!this.dateInterval) {
			this.dateInterval = new Ext.form.CompositeField({
				name: 'dateInterval',
				fieldLabel: Openwis.i18n('Harvesting.DateInterval'),
				style: {
				      margin: '0px 5px 0px 5px'  
				},
				items:
				[
				    new Ext.Container({
        				border: false,
        				html: Openwis.i18n('Common.Extent.Temporal.From'),
        				cls: 'formItems'
        			}),
        			this.getDateFrom(),
				    new Ext.Container({
        				border: false,
        				html: Openwis.i18n('Common.Extent.Temporal.To'),
        				cls: 'formItems'
        			}),
				    this.getDateTo()
				]
			});
		}
		return this.dateInterval;
	},
	
	getDateFrom: function() {
	    if(!this.dateFrom) {
    	   this.dateFrom = new Ext.form.DateField({
                name: 'dateFrom',
                editable: true,
                format: 'Y-m-d',
                width: 120
            });
        }
        return this.dateFrom;
	},
	
	getDateTo: function() {
	    if(!this.dateTo) {
    	   this.dateTo = new Ext.form.DateField({
                name: 'dateTo',
                editable: true,
                format: 'Y-m-d',
                width: 120
            });
        }
        return this.dateTo;
	},
    
    getProviderConfigurationFieldSet: function() {
        if(!this.providerConfigurationFieldSet) {
            this.providerConfigurationFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('Harvesting.ProviderConfiguration'),
				autoHeight:true,
				collapsed: false,
				collapsible: true
            });
            this.providerConfigurationFieldSet.add(new Ext.Container({
               html: Openwis.i18n('Harvesting.ProviderConfiguration.Fetch.Note'),
               border: false,
               cls: 'infoMsg',
               style: {
                   margin: '0px 0px 5px 0px'
               }
            }));
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationSetCombobox());
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationPrefixCombobox());
            this.providerConfigurationFieldSet.addButton(new Ext.Button(this.getFetchRemoteInfoAction()));
        }
        return this.providerConfigurationFieldSet;
    },
	
	getProviderConfigurationSetCombobox: function() {
	    if(!this.providerConfigurationSetCombobox) {
            this.providerConfigurationSetCombobox = new Ext.form.ComboBox({
                store: new Ext.data.JsonStore({
        			idProperty: 'name',
        			fields: [
        				{
        					name:'name'
        				},{
        					name:'label'
        				}
        			]
        		}),
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Set'),
				valueField: 'name',
				displayField:'label',
                disabled: true,
                name: 'set',
				mode: 'local',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.providerConfigurationSetCombobox; 
	},
	
	getProviderConfigurationPrefixCombobox: function() {
	    if(!this.providerConfigurationPrefixCombobox) {
            this.providerConfigurationPrefixCombobox = new Ext.form.ComboBox({
                store: [],
                disabled: true,
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Prefix'),
                name: 'prefix',
				mode: 'local',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.providerConfigurationPrefixCombobox; 
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getFetchRemoteInfoAction: function() {
		if(!this.fetchRemoteInfoAction) {
			this.fetchRemoteInfoAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Fetch'),
				scope: this,
				handler: function() {
				    var url = this.getUrlTextField().getValue();
				    if(url.trim() != '') {
    				    var params = {url: url, synchronization: true};
    					new Openwis.Handler.Get({
    					    url: configOptions.locService+ '/xml.harvest.oaipmh.info',
                			params: params,
                			listeners: {
                				success: function(remoteConfig) {
                					this.getProviderConfigurationSetCombobox().getStore().loadData(remoteConfig.sets);
                					this.getProviderConfigurationSetCombobox().setDisabled(false);
                					if(this.isEdition() && this.config.configuration.criteriaSet) {
                					    this.getProviderConfigurationSetCombobox().setValue(this.config.configuration.criteriaSet);
                					}
                					this.getProviderConfigurationPrefixCombobox().getStore().loadData(remoteConfig.formats);
                					this.getProviderConfigurationPrefixCombobox().setDisabled(false);
                					if(this.isEdition() && this.config.configuration.criteriaPrefix) {
                					    this.getProviderConfigurationPrefixCombobox().setValue(this.config.configuration.criteriaPrefix);
                					}
                				},
                				scope: this
                			}
    					}).proceed();
    				} else {
    				    Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n('Harvesting.ProviderConfiguration.Fetch.Remote.Url.Mandatory'));
    				}
				}
			});
		}
		return this.fetchRemoteInfoAction;
	},
	
	//rajout de l'ihm pour la récurrence
	getRecurrentProcessingCompositeField: function() {
	    if(!this.recurrentProcessingCompositeField) {
    	    this.recurrentProcessingCompositeField = new Ext.form.CompositeField({
    			width:360,
    			items: 
    			[
    			    {
    			        xtype: 'container',
    			        style: {
    			            paddingLeft: '20px'
    			        }
    			    },
    			    new Ext.form.Label({
        	    		html : Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod.Label')
        	    	}),
    				this.getRecurrentProcessingNumberField(),
    				this.getRecurrentProcessingFrequencyCombobox()
    			]
    		});
		}
		return this.recurrentProcessingCompositeField;
	},
	
	getRecurrentProcessingNumberField: function() {
	    if(!this.recurrentProcessingNumberField) {
    	    this.recurrentProcessingNumberField = new Ext.form.NumberField({
    	        name: 'frequencyNumber',
    	        width: 40,
    	        allowDecimals: false,
    	        allowNegative: false,
    	        minValue: 1,
    	        value: ''
    	    });
    	}
    	return this.recurrentProcessingNumberField;
	},
	
	getRecurrentProcessingFrequencyCombobox: function() {
        if(!this.recurrentProcessingFrequencyCombobox) {
            this.recurrentProcessingFrequencyCombobox = new Ext.form.ComboBox({
                store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['DAY', Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod.Day')], 
					    ['HOUR', Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod.Hour')]
					]
				}),
				valueField: 'id',
				displayField:'value',
    	        value: 'HOUR',
                name: 'frequencyComboBox',
                typeAhead: true,
				mode: 'local',
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 80
            });
        }
        return this.recurrentProcessingFrequencyCombobox;
    },
	
	//rajout de l'ihm pour saisir date et heure de lancement de tache
	getStartingDateCompositeField: function() {
	    if(!this.startingDateCompositeField) {
    	    this.startingDateCompositeField = new Ext.form.CompositeField({
    	    	width: 470,
    			items: 
    			[
    			    {
    			        xtype: 'container',
    			        style: {
    			            paddingLeft: '20px'
    			        }
    			    },
    			    new Ext.form.Label({
        	    		html : Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod.StartingAt')
        	    	}),
    				this.getStartingDateField(),
    				this.getStartingDateTimeField()
    			]
    		});
		}
		return this.startingDateCompositeField;
	},

    getStartingDateField: function() {
	    if(!this.startingDateField) {
    	   this.startingDateField = new Ext.form.DateField({
                name: 'startingDate',
                editable: false,
                format: 'Y-m-d',
                // Openwis.Utils.Date.ISODateToCalendar(this.frequency.startingDate)
                value: new Date()
            });
        }
        return this.startingDateField;
	},

	getStartingDateTimeField: function() {
		if(!this.startingDateTimeField) {
	    	this.startingDateTimeField = new Ext.form.TimeField({
	    		name: 'startingDateTimeField',
				increment: 15,
				format: "H:i",
				// Openwis.Utils.Date.ISODateToTime(this.frequency.startingDate)
				value: '00:00',
	    		width: 60
	    	});
		}
        return this.startingDateTimeField;
	},
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
					if(this.getTaskFormPanel().getForm().isValid() && this.validate()) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.harvest.save',
							params: this.getHarvestingTask(),
							listeners: {
								success: function(config) {
									this.fireEvent("synchroTaskSaved");
									this.close();
								},
								scope: this
							}
						});
						saveHandler.proceed();
					}
				}
			});
		}
		return this.saveAction;
	},
	
	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	//----- Utility methods.
	
	getHarvestingTask: function() {
		var task = {};
		if(this.isEdition()) {
			task.id = this.editTaskId;
			task.status = this.config.status;
		} else {
			task.status = 'ACTIVE';
		}
		
		task.name = this.getNameTextField().getValue();
		task.type = 'oaipmh';
		
		
		if(this.getBackupsCombobox().getValue() != '') {
			task.backup = {name: this.getBackupsCombobox().getValue()};
		}
		
		task.synchronizationTask = true;
		
		task.validationMode = this.getValidationCombobox().getValue();
		task.incremental = true;
		
		//Run mode.
		task.runMode = {};
		task.runMode.recurrent = true;
		if(task.runMode.recurrent) {
			 /*
			var recurrentDay = Ext.num(this.getFrequencyRecurrentDayTextField().getValue(), 0);
		    var recurrentHour = Ext.num(this.getFrequencyRecurrentHourTextField().getValue(), 0);
		    var recurrentMinute = Ext.num(this.getFrequencyRecurrentMinuteTextField().getValue(), 0);
		    
		    var period = 0;
		    if(recurrentMinute > 0) {
		        period += recurrentMinute * 60; //1 mn = 60s
		    }
		    if(recurrentHour > 0) {
		        period += recurrentHour * 3600; //1 hour = 3600s
		    }
		    if(recurrentDay > 0) {
		        period += recurrentDay * 24 * 3600; //1 day = 24h * 3600s
		    }
		    
		    task.runMode.recurrentPeriod = period;
		    */
			var recurrenceValue;
			var frequencyUnit = this.getRecurrentProcessingFrequencyCombobox().getValue();
			if (frequencyUnit == "HOUR") {
				recurrenceValue = this.getRecurrentProcessingNumberField().getValue()*3600;
			} else {
				recurrenceValue = this.getRecurrentProcessingNumberField().getValue()*3600*24;
			}
				
			
		    task.runMode.recurrentScale = this.getRecurrentProcessingFrequencyCombobox().getValue(); 
		    task.runMode.recurrencePeriod = recurrenceValue;
		    task.runMode.startingDate = this.getStartingDateField().getValue().format('Y-m-d') + 'T' + this.getStartingDateTimeField().getValue() + ':00Z';

		}
		
		//Task configuration.
		task.configuration = {};
		task.configuration.url = this.getUrlTextField().getValue();
		if(this.getUseAccountCheckBox().checked) {
		    task.configuration.userName = this.getUsernameTextField().getValue();
		    task.configuration.password = this.getPasswordTextField().getValue();
		}
		
		if(this.getDateFrom().getValue() != "") {
		    task.configuration.dateFrom = Openwis.Utils.Date.formatToISODate(this.getDateFrom().getValue());
	    }
	    
	    if(this.getDateTo().getValue() != "") {
		    task.configuration.dateTo = Openwis.Utils.Date.formatToISODate(this.getDateTo().getValue());
	    }
	    
	    //Tests if edition mode as provider conf. sets and prefix are loaded on user request.
	    if(this.isEdition()) {
	        if(this.getProviderConfigurationSetCombobox().disabled) {
		        task.configuration.criteriaSet = this.config.configuration.criteriaSet;
		    } else if(this.getProviderConfigurationSetCombobox().getValue() != "") {
		        task.configuration.criteriaSet = this.getProviderConfigurationSetCombobox().getValue();
		    }
		    
		    if(this.getProviderConfigurationPrefixCombobox().disabled) {
		        task.configuration.criteriaPrefix = this.config.configuration.criteriaPrefix;
		    } else if(this.getProviderConfigurationPrefixCombobox().getValue() != "") {
    		    task.configuration.criteriaPrefix = this.getProviderConfigurationPrefixCombobox().getValue();
    		}
	    } else {
	        if(this.getProviderConfigurationSetCombobox().getValue() != "") {
		        task.configuration.criteriaSet = this.getProviderConfigurationSetCombobox().getValue();
		    }
		    
		    if(this.getProviderConfigurationPrefixCombobox().getValue() != "") {
    		    task.configuration.criteriaPrefix = this.getProviderConfigurationPrefixCombobox().getValue();
    		}
		}
		
		var indexOfLocalCategory = this.getLocalCategoryStore().findExact("name", task.configuration.criteriaSet);
		var localCategory = this.getLocalCategoryStore().getAt(indexOfLocalCategory);
		task.category = {};
		task.category.id = localCategory.get('id');
		task.category.name = localCategory.get('name');
		
		return task; 
	},
	
	validate: function() {
	    if(this.getDateFrom().getValue() != "" && this.getDateTo().getValue() != "" && this.getDateFrom().getValue() > this.getDateTo().getValue()) {
		    Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
		    return false;
	    }
	    return true;
	},
	
	/**
	 * Returns true if it is a creation, false otherwise.
	 */
	isCreation: function() {
		return (this.operationMode == 'Create');
	},
	
	/**
	 * Returns true if it is an edition, false otherwise.
	 */
	isEdition: function() {
		return (this.operationMode == 'Edit');
	}
});