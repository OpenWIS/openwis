Ext.ns('Openwis.Admin.Harvesting.Harvester');

Openwis.Admin.Harvesting.Harvester.Geonetwork20 = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Harvesting.Geonetwork20.Title'),
			layout: 'fit',
			width:450,
			height:680,
			modal: true,
			border: false,
			autoScroll: true,
			closeAction:'close'
		});
		Openwis.Admin.Harvesting.Harvester.Geonetwork20.superclass.initComponent.apply(this, arguments);
		
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
		this.addEvents("harvestingTaskSaved");
		
		//-- Create form panel.
		this.add(this.getTaskFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
        //If edition.		
		if(this.isEdition()) {
			this.getNameTextField().setValue(this.config.name);
			this.getHostTextField().setValue(this.config.configuration.host);
			this.getPortTextField().setValue(this.config.configuration.port);
			this.getServletTextField().setValue(this.config.configuration.servlet);
			
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
			
			if(this.config.runMode.recurrent)  {
			    this.getRunModeRadioGroup().setValue('RECURRENT');
			    var recurrencePeriod = this.config.runMode.recurrentPeriod;
			    var days = Math.floor(recurrencePeriod / (24 * 3600));
			    if(days > 0) {
			        recurrencePeriod %= (24 * 3600);
			        this.getFrequencyRecurrentDayTextField().setValue(days);
			    }
			    
			    var hours = Math.floor(recurrencePeriod / 3600);
			    if(hours > 0) {
		            this.getFrequencyRecurrentHourTextField().setValue(hours);
			        recurrencePeriod %= 3600;
			    }
			    
			    var minuts = recurrencePeriod / 60;
			    if(minuts > 0) {
			        this.getFrequencyRecurrentMinuteTextField().setValue(minuts);
			    }
		        this.getFrequencyRunModeCompositeField().show();
			} else {
			    this.getRunModeRadioGroup().setValue('USER_TRIGERRED');
			}
			
			this.getProviderConfigurationOptionsSearchAnyTextField().setValue(this.config.configuration.any);
			this.getProviderConfigurationOptionsSearchTitleTextField().setValue(this.config.configuration.title);
			this.getProviderConfigurationOptionsSearchAbstractTextField().setValue(this.config.configuration['abstract']);
			this.getProviderConfigurationOptionsSearchKeywordsTextField().setValue(this.config.configuration.themekey);
			this.getProviderConfigurationOptionsCheckBoxGroup().setValue('digital', this.config.configuration.digital);
			this.getProviderConfigurationOptionsCheckBoxGroup().setValue('hardcopy', this.config.configuration.hardcopy);
			
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
			this.taskFormPanel.add(this.getHostTextField());
			this.taskFormPanel.add(this.getPortTextField());
			this.taskFormPanel.add(this.getServletTextField());
			this.taskFormPanel.add(this.getUseAccountCheckBox());
			this.taskFormPanel.add(this.getUseAccountCompositeField());
			this.taskFormPanel.add(this.getProviderConfigurationFieldSet());
			this.taskFormPanel.add(this.getRunModeRadioGroup());
			this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
			this.taskFormPanel.add(this.getBackupsCombobox());
			this.taskFormPanel.add(this.getValidationCombobox());
			this.taskFormPanel.add(this.getCategoryCombobox());
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
	getHostTextField: function() {
		if(!this.hostTextField) {
			this.hostTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.Host'),
				name: 'host',
				allowBlank:false,
				width: 150
			});
		}
		return this.hostTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getPortTextField: function() {
		if(!this.portTextField) {
			this.portTextField = new Ext.form.NumberField({
				fieldLabel: Openwis.i18n('Harvesting.Port'),
				name: 'port',
				allowBlank:false,
				width: 60
			});
		}
		return this.portTextField;
	},
	
	
	/**
	 * The text field for the data policy name.
	 */
	getServletTextField: function() {
		if(!this.servletTextField) {
			this.servletTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.Servlet'),
				name: 'servlet',
				allowBlank:false,
				width: 100
			});
		}
		return this.servletTextField;
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
	getRunModeRadioGroup: function() {
		if(!this.runModeRadioGroup) {
			this.runModeRadioGroup = new Ext.form.RadioGroup({
				fieldLabel: Openwis.i18n('Harvesting.Options.RunMode'),
				name: 'runMode',
				allowBlank:false,
				columns: 1,
				width: 150,
				items:
				[
				    {boxLabel: Openwis.i18n('Harvesting.Options.RunMode.UserTriggerred'), name: 'runMode', inputValue: 'USER_TRIGERRED', checked: true, id: 'USER_TRIGERRED'},
				    {boxLabel: Openwis.i18n('Harvesting.Options.RunMode.Recurrent'), name: 'runMode', inputValue: 'RECURRENT', id: 'RECURRENT'}
				],
    			listeners : {
    				change: function(group, radioChecked) {
    				    if(radioChecked.inputValue == 'USER_TRIGERRED') {
    					    this.getFrequencyRunModeCompositeField().hide();
    				    } else {
    				        this.getFrequencyRunModeCompositeField().show();
    				    }
    				},
    				scope: this
    			}
			});
		}
		return this.runModeRadioGroup;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getFrequencyRunModeCompositeField: function() {
		if(!this.frequencyRunModeCompositeField) {
			this.frequencyRunModeCompositeField = new Ext.form.CompositeField({
				name: 'recurrentRunMode',
				hidden: true,
				allowBlank:false,
				width: 350,
				items:
				[
				    new Ext.Container({
        				border: false,
        				html: Openwis.i18n('Harvesting.Options.RunMode.Recurrent.Frequency') + ':',
        				cls: 'formItems'
        			}),
				    this.getFrequencyRecurrentDayTextField(),
				    this.getFrequencyRecurrentHourTextField(),
				    this.getFrequencyRecurrentMinuteTextField(),
				    new Ext.Container({
        				border: false,
        				html: Openwis.i18n('Harvesting.Options.RunMode.Recurrent.Frequency.Detail'),
        				cls: 'formItems'
        			})
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
	
    getCategoryCombobox: function() {
        if(!this.categoryCombobox) {
            var categoryStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.category.all',
    			idProperty: 'id',
    			autoLoad: true,
    			fields: [
    				{
    					name:'id'
    				},{
    					name:'name'
    				}
    			],
    			listeners: {
    			    load: function(store, records, options) {
        			    if(this.isEdition() && this.config.category) {
                        	this.getCategoryCombobox().setValue(this.config.category.id);
        			    }
        			},
        			scope: this
    			}
    		});
        
            this.categoryCombobox = new Ext.form.ComboBox({
                fieldLabel: Openwis.i18n('Harvesting.Category.After.Harvest'),
                store: categoryStore,
				valueField: 'id',
				displayField:'name',
                name: 'category',
				mode: 'local',
				allowBlank: false,
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.categoryCombobox;
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
    
    getProviderConfigurationFieldSet: function() {
        if(!this.providerConfigurationFieldSet) {
            this.providerConfigurationFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('Harvesting.ProviderConfiguration'),
				autoHeight:true,
				collapsed: true,
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
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchAnyTextField());
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchTitleTextField());
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchAbstractTextField());
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsSearchKeywordsTextField());
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationOptionsCheckBoxGroup());
            this.providerConfigurationFieldSet.add(this.getProviderConfigurationSitesCombobox());
            this.providerConfigurationFieldSet.addButton(new Ext.Button(this.getFetchRemoteInfoAction()));
        }
        return this.providerConfigurationFieldSet;
    },
    
    /**
	 * The text field for the data policy name.
	 */
	getProviderConfigurationOptionsSearchAnyTextField: function() {
		if(!this.providerConfigurationOptionsSearchAnyTextField) {
			this.providerConfigurationOptionsSearchAnyTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Any'),
				name: 'any',
				allowBlank:true,
				width: 150
			});
		}
		return this.providerConfigurationOptionsSearchAnyTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getProviderConfigurationOptionsSearchTitleTextField: function() {
		if(!this.providerConfigurationOptionsSearchTitleTextField) {
			this.providerConfigurationOptionsSearchTitleTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Title'),
				name: 'title',
				allowBlank:true,
				width: 150
			});
		}
		return this.providerConfigurationOptionsSearchTitleTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getProviderConfigurationOptionsSearchAbstractTextField: function() {
		if(!this.providerConfigurationOptionsSearchAbstractTextField) {
			this.providerConfigurationOptionsSearchAbstractTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Abstract'),
				name: 'abstract',
				allowBlank:true,
				width: 150
			});
		}
		return this.providerConfigurationOptionsSearchAbstractTextField;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getProviderConfigurationOptionsSearchKeywordsTextField: function() {
		if(!this.providerConfigurationOptionsSearchKeywordsTextField) {
			this.providerConfigurationOptionsSearchKeywordsTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Keywords'),
				name: 'keywords',
				allowBlank:true,
				width: 150
			});
		}
		return this.providerConfigurationOptionsSearchKeywordsTextField;
	},
	
	/**
	 * The text area for the group global status.
	 */
	getProviderConfigurationOptionsCheckBoxGroup: function() {
		if(!this.providerConfigurationOptionsCheckboxGroup) {
			this.providerConfigurationOptionsCheckboxGroup = new Ext.form.CheckboxGroup({
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options'),
				name: 'options',
				columns: 2,
				width: 175,
				columns: 1,
				items:
				[
				    {boxLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Digital'), name: 'digital', id: 'digital'},
				    {boxLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Hardcopy'), name: 'hardcopy', id: 'hardcopy'}
				]
			});
		}
		return this.providerConfigurationOptionsCheckboxGroup;
	},
	
	getProviderConfigurationSitesCombobox: function() {
	    if(!this.providerConfigurationSitesCombobox) {
            this.providerConfigurationSitesCombobox = new Ext.form.ComboBox({
                store: new Openwis.Data.JeevesJsonStore({
        			idProperty: 'id',
        			fields: [
        				{
        					name:'id'
        				},{
        					name:'name'
        				}
        			]
        		}),
                displayField: 'name',
                valueField: 'id',
                disabled: true,
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Search.Site'),
                name: 'siteId',
				mode: 'local',
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
        }
        return this.providerConfigurationSitesCombobox; 
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
				    var url ='http://'+this.getHostTextField().getValue()+':'+this.getPortTextField().getValue()+'/'+this.getServletTextField().getValue();
				    if(url.trim() != '') {
    				    var params = {content: url};
    					new Openwis.Handler.Get({
    					    url: configOptions.locService+ '/xml.get.geonetwork.sources.info',
                			params: params,
                			listeners: {
                				success: function(remoteConfig) {
                					this.getProviderConfigurationSitesCombobox().getStore().loadData(remoteConfig);
                					this.getProviderConfigurationSitesCombobox().setDisabled(false);
                					if(this.isEdition() && this.config.configuration.siteId) {
                					    this.getProviderConfigurationSitesCombobox().setValue(this.config.configuration.siteId);
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
									this.fireEvent("harvestingTaskSaved");
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
		task.type = 'geonetwork20';
		
		
		if(this.getBackupsCombobox().getValue() != '') {
			task.backup = {name: this.getBackupsCombobox().getValue()};
		}
		
		task.synchronizationTask = false;
		task.incremental = false;
		task.validationMode = this.getValidationCombobox().getValue();
		
		
		//Run mode.
		task.runMode = {};
		task.runMode.recurrent = this.getRunModeRadioGroup().getValue().inputValue == 'RECURRENT';
		if(task.runMode.recurrent) {
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
		}
		
		//Task configuration.
		task.configuration = {};
		task.configuration.host = this.getHostTextField().getValue();
		task.configuration.port = this.getPortTextField().getValue();
		task.configuration.servlet = this.getServletTextField().getValue();
		
		if(this.getUseAccountCheckBox().checked) {
		    task.configuration.userName = this.getUsernameTextField().getValue();
		    task.configuration.password = this.getPasswordTextField().getValue();
		}
		if(this.getCategoryCombobox().getValue()) {
		    task.category = {id: this.getCategoryCombobox().getValue()};
		}
		
		task.configuration.any = this.getProviderConfigurationOptionsSearchAnyTextField().getValue();
		task.configuration.title = this.getProviderConfigurationOptionsSearchTitleTextField().getValue();
		task.configuration['abstract'] = this.getProviderConfigurationOptionsSearchAbstractTextField().getValue();
		task.configuration.themekey = this.getProviderConfigurationOptionsSearchKeywordsTextField().getValue();
		
		var values = this.getProviderConfigurationOptionsCheckBoxGroup().getValue();
		Ext.each(values, function(item, index, allItems) {
		    if(item.name == 'digital') {
		        task.configuration.digital = true;
		    } else if(item.name == 'hardcopy') {
		        task.configuration.hardcopy = true;
		    }
		}, this);
		
	    //Tests if edition mode as provider conf. sites are loaded on user request.
	    if(this.isEdition()) {
		    if(this.getProviderConfigurationSitesCombobox().disabled) {
		        task.configuration.siteId = this.config.configuration.siteId;
		    } else if(this.getProviderConfigurationSitesCombobox().getValue() != "") {
    		    task.configuration.siteId = this.getProviderConfigurationSitesCombobox().getValue();
    		}
	    } else {
		    if(this.getProviderConfigurationSitesCombobox().getValue() != "") {
    		    task.configuration.siteId = this.getProviderConfigurationSitesCombobox().getValue();
    		}
		}
		
		return task; 
	},
	
	validate: function() {
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