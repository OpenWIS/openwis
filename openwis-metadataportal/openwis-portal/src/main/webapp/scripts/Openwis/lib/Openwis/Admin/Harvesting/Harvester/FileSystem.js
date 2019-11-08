Ext.ns('Openwis.Admin.Harvesting.Harvester');

Openwis.Admin.Harvesting.Harvester.FileSystem = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Harvesting.FileSystem.Title'),
			layout: 'fit',
			width:450,
			height:620,
			modal: true,
			border: false,
			autoScroll: true,
			closeAction:'close'
		});
		Openwis.Admin.Harvesting.Harvester.FileSystem.superclass.initComponent.apply(this, arguments);
		
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
		
		//-- If specified, populate forms from specified data policy.
		if(this.isEdition()) {
			this.getNameTextField().setValue(this.config.name);
			
			this.getDirectoryTextField().setValue(this.config.configuration.dir);
			
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
			
			this.getStyleSheetComboBox().setValue(this.config.configuration.styleSheet);
			this.getValidationCombobox().setValue(this.config.validationMode);
			this.getConfigurationOptionsCheckBoxGroup().setValue(
					{'recursive': this.config.configuration.recursive,
					 'keepLocalIfDeleted': this.config.configuration.keepLocalIfDeleted,
					 'localImport': this.config.configuration.localImport});
			this.getConfigurationFileTypeRadioGroup().setValue(this.config.configuration.fileType, true);
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
			this.taskFormPanel.add(this.getDirectoryTextField());
			this.taskFormPanel.add(this.getConfigurationFieldSet());
			this.taskFormPanel.add(this.getRunModeRadioGroup());
			this.taskFormPanel.add(this.getFrequencyRunModeCompositeField());
			this.taskFormPanel.add(this.getStyleSheetComboBox());
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
	getDirectoryTextField: function() {
		if(!this.directoryTextField) {
			this.directoryTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Harvesting.Directory'),
				name: 'directory',
				allowBlank:false,
				width: 300
			});
		}
		return this.directoryTextField;
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
    
    /**
	 * The style sheet combo box.
	 */
	getStyleSheetComboBox: function() {	
		if(!this.styleSheetComboBox) {
			var styleSheetStore = new Openwis.Data.JeevesJsonStore({
    			url: configOptions.locService+ '/xml.stylesheet.all',
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
            			// Add an empty value for overridden stylesheet value reset. 
                        store.insert(0, [new Ext.data.Record({id:'NONE',  name:Openwis.i18n('Common.List.None')})]);
                
        			    if(this.isEdition() && this.config.configuration.styleSheet) {
                        	this.getStyleSheetComboBox().setValue(this.config.configuration.styleSheet);
        			    } else {
        			        this.getStyleSheetComboBox().setValue('NONE');
        			    }
        			},
        			scope: this
    			}
    		});
		
			this.styleSheetComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('MetadataBatchImport.StyleSheet'),
				name: 'stylesheet',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				store: styleSheetStore,
				editable: false,
				allowBlank: false,
				width: 330,
				displayField: 'name',
				valueField: 'id'
			});
		}
		
		return this.styleSheetComboBox;
	},
    
    getConfigurationFieldSet: function() {
        if(!this.configurationFieldSet) {
            this.configurationFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('Harvesting.ProviderConfiguration.Options'),
				autoHeight:true,
				collapsed: true,
				collapsible: true
            });
            this.configurationFieldSet.add(this.getConfigurationOptionsCheckBoxGroup());
            this.configurationFieldSet.add(this.getConfigurationFileTypeRadioGroup());
        }
        return this.configurationFieldSet;
    },
	
	/**
	 * The text area for the group global status.
	 */
	getConfigurationOptionsCheckBoxGroup: function() {
		if(!this.configurationOptionsCheckboxGroup) {
			this.configurationOptionsCheckboxGroup = new Ext.form.CheckboxGroup({
				fieldLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options'),
				name: 'options',
				width: 175,
				columns: 1,
				items:
				[
				    {boxLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.Recursive'), name: 'recursive', id: 'recursive', checked: false},
				    {boxLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.KeepLocal'), name: 'keepLocalIfDeleted', id: 'keepLocalIfDeleted', checked: false},
				    {boxLabel: Openwis.i18n('Harvesting.ProviderConfiguration.Options.LocalImport'), name: 'localImport', id: 'localImport', checked: false}
				]
			});
		}
		return this.configurationOptionsCheckboxGroup;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getConfigurationFileTypeRadioGroup: function() {
		if(!this.configurationFileTypeRadioGroup) {
			this.configurationFileTypeRadioGroup = new Ext.form.RadioGroup({
				fieldLabel: Openwis.i18n('MetadataBatchImport.FileType'),
				name: 'fileType',
				allowBlank:false,
				columns: 1,
				width: 150,
				items:
				[
				    {boxLabel: Openwis.i18n('MetadataBatchImport.FileType.SingleFile'), name: 'fileType', inputValue: 'single', checked: true, id: 'single'},
				    {boxLabel: Openwis.i18n('MetadataBatchImport.FileType.MefFile'), name: 'fileType', inputValue: 'mef', id: 'mef'}
				]
			});
		}
		return this.configurationFileTypeRadioGroup;
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
		task.type = 'localfilesystem';
		
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
		task.configuration.dir = this.getDirectoryTextField().getValue();
		if(this.getCategoryCombobox().getValue()) {
		    task.category = {id: this.getCategoryCombobox().getValue()};
		}
		
		if(this.getStyleSheetComboBox().getValue() != 'NONE') {
		    task.configuration.styleSheet = this.getStyleSheetComboBox().getValue();
		}
		
		task.configuration.fileType = this.getConfigurationFileTypeRadioGroup().getValue().getId();
		
		task.configuration.recursive = false;
		task.configuration.keepLocalIfDeleted = false;
		task.configuration.localImport = false;
		var values = this.getConfigurationOptionsCheckBoxGroup().getValue();
		Ext.each(values, function(item, index, allItems) {
		    if(item.name == 'recursive') {
		        task.configuration.recursive = true;
		    } else if(item.name == 'keepLocalIfDeleted') {
		        task.configuration.keepLocalIfDeleted = true;
		    } else if(item.name == 'localImport') {
		        task.configuration.localImport = true;
		    }
		}, this);
		
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