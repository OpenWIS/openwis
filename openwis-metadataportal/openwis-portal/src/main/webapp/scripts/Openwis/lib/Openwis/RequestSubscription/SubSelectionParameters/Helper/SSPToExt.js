Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.Helper');

Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPToExt = function() {
	
	/**
	 * Creates a Mock Panel to validate the sub selection parameters wizard. 
	 */
	this.createComponent = function(config) {
	    var panel = null;
	    var panelConfig = {config: config, fieldLabel: config.parameter.label};
	    
	    if(config.parameter.selectionType == "SingleSelection") {
	        if(config.parameter.type == 'RADIO') {
	            panel = new Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.Radio(panelConfig);
	        } else if(config.parameter.type == 'DROPDOWNLIST') {
	            panel = new Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ComboBox(panelConfig);
	        } else if(config.parameter.type == 'LISTBOX') {
	            panel = new Openwis.RequestSubscription.SubSelectionParameters.SingleSelection.ListBox(panelConfig);
	        }
	    } else if(config.parameter.selectionType == "MultipleSelection") {
	        if(config.parameter.type == 'CHECKBOX') {
	            panel = new Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.Checkbox(panelConfig);
	        } else if(config.parameter.type == 'LISTBOX') {
	            panel = new Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.ListBox(panelConfig);
	        }
	    } else if(config.parameter.selectionType == "ScheduleSelection") {
	        panel = new Openwis.RequestSubscription.SubSelectionParameters.ScheduleSelection.Schedule(panelConfig);
	    } else if(config.parameter.selectionType == "GeographicalAreaSelection") {
	        panel = new Openwis.Common.Components.GeographicalExtentSelection({
	            fieldLabel: config.parameter.label,
	            maxExtent: Openwis.Utils.Geo.getBoundsFromWKT(config.parameter.geoWKTMaxExtent),
    			geoWKTSelection: this.toSingletonValue(config.editValue) || config.parameter.geoWKTSelection,
    			wmsUrl: config.parameter.geoConfig.wmsUrl,
    			layerName: config.parameter.geoConfig.layerName,
    			geoExtentType: config.parameter.geoExtentType,
    			maxResolution : 'auto'
	        });
	    } else if(config.parameter.selectionType == "SourceSelection") {
	        panel = Openwis.RequestSubscription.SubSelectionParameters.SourceSelection.Source(panelConfig);
	    } else if(config.parameter.selectionType == "DayPeriodSelection") {
	        panel = new Openwis.RequestSubscription.SubSelectionParameters.PeriodSelection.Day(panelConfig);
	    } else if(config.parameter.selectionType == "DatePeriodSelection") {
	        var defaultDateFrom = config.parameter.from;
			var defaultDateTo = config.parameter.to;
	        
	        if(config.editValue) {
	            var period = config.editValue.split("/");
	            defaultDateFrom = period[0];
			    defaultDateTo = period[1];
	        }
	        
	        panel = new Openwis.Common.Components.DateTimeExtentSelection({
	            fieldLabel: config.parameter.label,
	            dateSelection: true,
	            defaultDateFrom: defaultDateFrom,
			    defaultDateTo: defaultDateTo,
			    dateMin: config.parameter.periodMinExtent,
			    dateMax: config.parameter.periodMaxExtent,
			    excludedDates: config.parameter.excludedDates,
			    timeSelection: false,
			    processValue: function(value) {
			        if(value.dateFrom && value.dateTo) {
			            return Openwis.Utils.Date.formatDateInterval(value.dateFrom, value.dateTo);
			        } else {
			            return null;
			        }
			    }
	        });
	    } else if(config.parameter.selectionType == "TimePeriodSelection") {
	        var defaultTimeFrom = config.parameter.from;
			var defaultTimeTo = config.parameter.to;
	        
	        if(config.editValue) {
	            var period = config.editValue.split("/");
	            defaultTimeFrom = period[0].replace(/Z/ig, "");
			    defaultTimeTo = period[1].replace(/Z/ig, "");
	        }
	        panel = new Openwis.Common.Components.DateTimeExtentSelection({
	            fieldLabel: config.parameter.label,
	            dateSelection: false,
			    timeSelection: true,
	            defaultTimeFrom: defaultTimeFrom,
			    defaultTimeTo: defaultTimeTo,
			    timeMin: config.parameter.periodMinExtent,
			    timeMax: config.parameter.periodMaxExtent,
			    processValue: function(value) {
			        if(value.timeFrom && value.timeTo) {
			            return Openwis.Utils.Date.formatTimeInterval(value.timeFrom, value.timeTo);
			        } else {
			            return null;
			        }
			    }
	        });
	    }
	    
	    if(!panel) {
    	   panel = new Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel(panelConfig);
    	}
	    return panel;
	};
	
	this.createReadOnlyComponent = function(config) {
	    var labelValue = "N/A";
	    
	    if(config.parameter.selectionType == "SingleSelection") {
	        for(var i = 0; i < config.parameter.values.length; i++) {
	            var value = config.parameter.values[i];
	            if(value.code == this.toSingletonValue(config.editValue)) {
	                labelValue = value.value;
	                break;
	            }
	        }
	    } else if(config.parameter.selectionType == "MultipleSelection") {
	        var selectedValues = config.editValue;
	        labelValue = "";
	        for(var i = 0; i < config.parameter.values.length; i++) {
	            var value = config.parameter.values[i];
	            if(selectedValues.indexOf(value.code) > -1) {
	                labelValue += value.value + " ";
	            }
	        }
	    } else if(config.parameter.selectionType == "GeographicalAreaSelection") {
	        return new Openwis.Common.Components.GeographicalExtentSelection({
	            fieldLabel: config.parameter.label,
	            maxExtent: Openwis.Utils.Geo.getBoundsFromWKT(config.parameter.geoWKTMaxExtent),
    			geoWKTSelection: this.toSingletonValue(config.editValue),
    			wmsUrl: config.parameter.geoConfig.wmsUrl,
    			layerName: config.parameter.geoConfig.layerName,
    			geoExtentType: config.parameter.geoExtentType,
    			maxResolution : 'auto',
    			readOnly: true
	        });
	    } else if(config.parameter.selectionType == "SourceSelection") {
	        labelValue = this.toSingletonValue(config.editValue);
	    } else if(config.parameter.selectionType == "DayPeriodSelection") {
	        labelValue = this.toSingletonValue(config.editValue);
	    } else if(config.parameter.selectionType == "DatePeriodSelection") {
	        var period = this.toSingletonValue(config.editValue).split("/");
	        labelValue = Openwis.i18n('Common.Extent.Temporal.From.To', {from: period[0], to: period[1]});
	    } else if(config.parameter.selectionType == "TimePeriodSelection") {
	        var period = this.toSingletonValue(config.editValue).split("/");
	        labelValue = Openwis.i18n('Common.Extent.Temporal.From.To', {from: period[0], to: period[1]});
	    }
	    
	    return new Ext.form.DisplayField({
            fieldLabel: config.parameter.label,
            value: labelValue,
            buildValue: function() {return labelValue;}
        });
	};
	
	/**
	 * If the value is an array, returns the first element of the array.  Otherwise,
	 * returns the value unmodified.
	 */
	this.toSingletonValue = function(value) {
		if ((value != null) && (value.constructor === Array)) {
			return value[0];
		} else {
			return value;
		}
	};
	
	this.createEmptyComponent = function(label) {
	    return new Ext.form.DisplayField({
            fieldLabel: label,
            value: ' ',
            buildValue: function() {return null;}
	    });
	};
	
	this.createMock = function(config) {
	    var panel = null;
	    var panelConfig = {config: config};
	    panel = new Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel(panelConfig);
	    return panel;
	};
	
	this.createLabel = function(label){
	    return new Ext.Container({
			html: label + ":",
			width: 100,
			border: false,
			cellCls: 'SSPCell',
			style: {
				margin: '10px'
			}
		});
	};
	
	this.createSSPContainer = function() {
	    return new Ext.Container({
			width: 442,
			border: false,
			cellCls: 'SSPCell',
			style: {
				margin: '10px'
			}
		});
	};
	
	this.createNoSSPAvailableContainer = function() {
	    return new Ext.Container({
			width: 542,
			colspan: 2,
			border: false,
			cellCls: 'SSPCell',
			style: {
				margin: '10px'
			},
			html: Openwis.i18n('RequestSubscription.SSP.Unavailable')
		});
	};
};