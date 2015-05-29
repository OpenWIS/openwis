Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection');

Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day = Ext.extend(Ext.form.DateField, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
		    allowBlank: false,
			width: 150,
			format: 'm/d/Y',
			listeners : {
				select: function() {
					this.fireEvent("valueChanged");
				}
			}
		});
		Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day.superclass.initComponent.apply(this, arguments);
		
	    this.addEvents("valueChanged");
	    
	    this.initialize();
	},
	
	initialize: function() {
		if(this.config.editValue) {
		    this.setValue(this.config.editValue.toString());
		} else if(this.config.parameter.date) {
			this.setValue(this.config.parameter.date);
		}
		if(this.config.parameter.periodMinExtent) {
			this.setMinValue(this.config.parameter.periodMinExtent);
		}
		if(this.config.parameter.periodMaxExtent) {
			this.setMaxValue(this.config.parameter.periodMaxExtent);
		}
		if(this.config.parameter.excludedDates && !Ext.isEmpty(this.config.parameter.excludedDates)) {
			this.setDisabledDates(this.config.parameter.excludedDates);
		}
	},
	
	//----------------------------------------------------------------- Generic methods used by the wizard.
	
	buildValue: function() {
	    var tmp = Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day.superclass.getValue.call(this);
	    if(tmp != "") {
	        return Openwis.Utils.Date.formatDateForServer(tmp);
	    } else {
	        return null;
	    }
	}
});