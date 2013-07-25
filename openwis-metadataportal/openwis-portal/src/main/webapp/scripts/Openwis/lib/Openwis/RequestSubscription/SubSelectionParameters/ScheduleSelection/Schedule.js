Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection');

Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule = Ext.extend(Ext.form.Field, {

	defaultAutoCreate : {tag: "div"},
	
    //--------------------------------------------------------------------- Overriden methods.
	initComponent: function() {
		Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule.superclass.initComponent.apply(this, arguments);
		
		//Sets by default.
	    if(!this.frequency) {
            this.frequency = (this.config ? this.config.editValue : null) || {type: 'ON_PRODUCT_ARRIVAL'};
        }
	},
	
	
	onRender: function(ct, position){
        Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule.superclass.onRender.call(this, ct, position);

        this.getSchedulePanel().add(this.getScheduleModeRadioGroup());
		this.getStartingDateFieldSet().add(this.getStartingDateCompositeField());
		this.getSchedulePanel().add(this.getStartingDateFieldSet());
		this.getRecurrentProcessingFieldSet().add(this.getRecurrentProcessingCompositeField());
        this.getSchedulePanel().add(this.getRecurrentProcessingFieldSet());
		this.getSchedulePanel().doLayout();
    },

    getRecurrentProcessingFieldSet: function() {
        if(!this.recurrentProcessingFieldSet) {
	        this.recurrentProcessingFieldSet = new Ext.form.FieldSet({
    			title: Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod'),
    			collapsed: false,
    			collapsible: false,
    			border: false,
    			width: 470
    		});
	    }
	    return this.recurrentProcessingFieldSet;
    },
    
    getStartingDateFieldSet: function() {
        if(!this.startingDateFieldSet) {
	        this.startingDateFieldSet = new Ext.form.FieldSet({
    			title: Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod.StartingAt'),
    			collapsed: false,
    			collapsible: false,
    			border: false,
    			width: 470
    		});
	    }
	    return this.startingDateFieldSet;
    },
	
    getRawValue: function() {
		var obj = {};
		obj.type = this.getScheduleModeRadioGroup().getValue().inputValue;

        if(obj.type == 'RECURRENT_PROCESSING') {
            obj.recurrentScale = this.getRecurrentProcessingFrequencyCombobox().getValue(); 
            obj.recurrencePeriod = this.getRecurrentProcessingNumberField().getValue();
            obj.startingDate = this.getStartingDateField().getValue().format('Y-m-d') + 'T' + this.getStartingDateTimeField().getValue() + ':00Z';
		}
		return obj;
	},
	
	getValue: function() {
		return this.getRawValue();
	},
	
	buildValue: function() {
		return this.getValue();
	},
	
	validateValue : function(value){
        if(value.type == 'RECURRENT_PROCESSING') {
            if(Ext.num(value.recurrencePeriod, -1) < 0) {
                this.getRecurrentProcessingNumberField().markInvalid();
                return false;
            }
        }
        return true;
    },
    
    reset: function() {
	    this.getScheduleModeRadioGroup().reset();
	},
	
    //--------------------------------------------------------------------- Panels.
	
	getSchedulePanel: function() {
	    if(!this.schedulePanel) {
	        this.schedulePanel = new Ext.Container({
	            renderTo: this.el
	        });
	    }
	    return this.schedulePanel;
	},
	
	getScheduleModeRadioGroup: function() {
	    if(!this.scheduleModeRadioGroup) {
	        this.scheduleModeRadioGroup = new Ext.form.RadioGroup({
                hideLabel: true,
                columns: 1,
                items: [
                    {boxLabel: Openwis.i18n('RequestSubscription.SSP.Schedule.OnProductArrival'), inputValue: 'ON_PRODUCT_ARRIVAL', name: 'scheduleMode', checked: this.frequency.type == 'ON_PRODUCT_ARRIVAL'},
                    {boxLabel: Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentProcessing'), inputValue: 'RECURRENT_PROCESSING', name: 'scheduleMode', checked: this.frequency.type == 'RECURRENT_PROCESSING'}
                ],
                listeners: {
                    change: function(radioGroup, radio) {
    					if(radio.inputValue == 'RECURRENT_PROCESSING') {
    					    this.getRecurrentProcessingCompositeField().enable();
    					    this.getStartingDateCompositeField().enable();
    					} else {
    					    this.getRecurrentProcessingCompositeField().disable();
    					    this.getStartingDateCompositeField().disable();
    					}
    				},
    				scope: this
                }
            });
        }
        return this.scheduleModeRadioGroup;
	},

	getRecurrentProcessingCompositeField: function() {
	    if(!this.recurrentProcessingCompositeField) {
    	    this.recurrentProcessingCompositeField = new Ext.form.CompositeField({
    			disabled: (this.frequency.type != 'RECURRENT_PROCESSING'),
//    			width:360,
    			items: 
    			[
    			    {
    			        xtype: 'container',
    			        //html: Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod'),
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
    	        value: (this.frequency.type == 'RECURRENT_PROCESSING') ? this.frequency.recurrencePeriod : ''
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
    	        value: (this.frequency.type == 'RECURRENT_PROCESSING') ? this.frequency.recurrentScale : 'HOUR',
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

    getStartingDateCompositeField: function() {
	    if(!this.startingDateCompositeField) {
    	    this.startingDateCompositeField = new Ext.form.CompositeField({
    			disabled: (this.frequency.type != 'RECURRENT_PROCESSING'),
//    			width:360,
    			items: 
    			[
    			    {
    			        xtype: 'container',
    			        //html: Openwis.i18n('RequestSubscription.SSP.Schedule.RecurrentPeriod'),
    			        style: {
    			            paddingLeft: '20px'
    			        }
    			    },
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
                value: (this.frequency.type == 'RECURRENT_PROCESSING') ? Openwis.Utils.Date.ISODateToCalendar(this.frequency.startingDate) : new Date()
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
				value: (this.frequency.type == 'RECURRENT_PROCESSING') ? Openwis.Utils.Date.ISODateToTime(this.frequency.startingDate) : '00:00',
	    		width: 60
	    	});
		}
        return this.startingDateTimeField;
	}
});