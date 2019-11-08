Ext.ns('Openwis.Common.Components');

Openwis.Common.Components.DateTimeExtentSelection = Ext.extend(Ext.form.CompositeField, {
	
	dateSelection: true,
	defaultDateFrom: null,
	defaultDateTo: null,
	dateMin: null,
	dateMax: null,
	excludedDates: null,
			
	timeSelection: true,
	allowBlankTimeSelection: false,
	defaultTimeFrom: null,
	defaultTimeTo: null,
	timeMin: null,
	timeMax: null,
	timeEditable: false,
    
	
	initComponent: function() {
		Ext.apply(this, 
		{
			labelWidth: 120,
			items:
			[
			    {
			        xtype: 'container', border:false, html: Openwis.i18n('Common.Extent.Temporal.From')
			    },
			    this.getFromDateField(),
		        this.getFromTimeField(),
		        {
			        xtype: 'container', border:false, html: Openwis.i18n('Common.Extent.Temporal.To')
			    },
		        this.getToDateField(),
		        this.getToTimeField()
			]
		});
	    this.initialize();
	    
		Openwis.Common.Components.DateTimeExtentSelection.superclass.initComponent.apply(this, arguments);
		
	    this.addEvents("valueChanged");
	},
	
	initialize: function() {
	    if(this.defaultDateFrom) {
    		this.getFromDateField().setValue(this.defaultDateFrom);
    	}
    	if(this.defaultDateTo) {
    		this.getToDateField().setValue(this.defaultDateTo);
    	}
    	if(this.dateMin) {
    		this.getFromDateField().setMinValue(this.dateMin);
    		this.getToDateField().setMinValue(this.dateMin);
    	}
    	if(this.dateMax) {
    		this.getFromDateField().setMaxValue(this.dateMax);
    		this.getToDateField().setMaxValue(this.dateMax);
    	}
    	if(this.excludedDates && !Ext.isEmpty(this.excludedDates)) {
    		this.getFromDateField().setDisabledDates(this.excludedDates);
    		this.getToDateField().setDisabledDates(this.excludedDates);
    	}
    	
    	if(this.defaultTimeFrom) {
    		this.getFromTimeField().setValue(this.defaultTimeFrom);
    	}
    	if(this.defaultTimeTo) {
    		this.getToTimeField().setValue(this.defaultTimeTo);
    	}
    	if(this.timeMin) {
    		this.getFromTimeField().setMinValue(this.timeMin);
    		this.getToTimeField().setMinValue(this.timeMin);
    	}
    	if(this.timeMax) {
    		this.getFromTimeField().setMaxValue(this.timeMax);
    		this.getToTimeField().setMaxValue(this.timeMax);
    	}
	},
	
	//----------------------------------------------------------------- Initialization of the panels.
	
	getFromDateField: function() {
	    if(!this.fromDateField) {
	        this.fromDateField = this.createDateField();
	    }
	    return this.fromDateField;
	},
	
	getFromTimeField: function() {
	    if(!this.fromTimeField) {
	        this.fromTimeField = this.createTimeField();
	    }
	    return this.fromTimeField;
	},
	
	getToDateField: function() {
	    if(!this.toDateField) {
	        this.toDateField = this.createDateField();
	    }
	    return this.toDateField;
	},
	
	getToTimeField: function() {
	    if(!this.toTimeField) {
	        this.toTimeField = this.createTimeField();
	    }
	    return this.toTimeField;
	},
	
	createDateField: function() {
	    return new Ext.form.DateField({
    		allowBlank: false,
    		editable: false,
    		hidden: !this.dateSelection,
    		disabled: !this.dateSelection,
    		format: 'm/d/Y',
    		listeners : {
    			select: function() {
    				this.fireEvent("valueChanged");
    			},
    			scope: this
    		}
    	});
	},
	
	createTimeField: function() {
	    return new Ext.form.TimeField({
    		allowBlank: false,
    		editable: this.timeEditable,
    		hidden: !this.timeSelection,
    		disabled: !this.timeSelection,
			increment: 15,
			format: "H:i",
    		width: 60,
    		listeners : {
    			select: function() {
    				this.fireEvent("valueChanged");
    			},
    			scope: this
    		}
    	});
	},
	
	//----------------------------------------------------------------- Overriden methods.
	
	getRawValue: function() {
		var value = {};
		if(this.dateSelection) {
		    value.dateFrom = this.getFromDateField().getValue(); 
		    value.dateTo   = this.getToDateField().getValue();
		}
		
		if(this.timeSelection) {
		    value.timeFrom = this.getFromTimeField().getValue(); 
		    value.timeTo = this.getToTimeField().getValue();
		}
		
		return value;
	},
	
	getValue: function() {
        return this.processValue(this.getRawValue());
    },
	
	buildValue: function() {
		return this.getValue();
	},
    
    processValue: function(value) {
        return value;
    },
    
	validateValue: function() {
	    Ext.QuickTips.init();
	    var simpleValidation = Openwis.Common.Components.DateTimeExtentSelection.superclass.validateValue.call(this);
	    if(simpleValidation) {
	        var value = this.getRawValue();
	        if(this.dateSelection) {
	            if(value.dateFrom > value.dateTo) {
	                this.getFromDateField().markInvalid(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
	                this.getToDateField().markInvalid(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
                    return false;
	            } else if(value.dateFrom == value.dateTo && value.timeFrom > value.timeTo) {
	                this.getFromTimeField().markInvalid(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
	                this.getToTimeField().markInvalid(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
                    return false;
	            }
	        } else if(this.timeSelection) {
	            if(value.timeFrom > value.timeTo) {
	                this.getFromTimeField().markInvalid(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
	                this.getToTimeField().markInvalid(Openwis.i18n('Common.Extent.Temporal.Error.From.After.To'));
                    return false;
	            }
	        }
	        this.getFromDateField().clearInvalid();
	        this.getToDateField().clearInvalid();
	        this.getFromTimeField().clearInvalid();
	        this.getToTimeField().clearInvalid();
	        return true;
	    } else {
	        return false;
	    }
	}
});