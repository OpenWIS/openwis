Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.Cache');

Openwis.RequestSubscription.SubSelectionParameters.Cache.Period = Ext.extend(Ext.form.Field, {
    
    defaultAutoCreate : {tag: "div"},
    
    initComponent: function() {
		Openwis.RequestSubscription.SubSelectionParameters.Cache.Period.superclass.initComponent.apply(this, arguments);
		
	    this.initEditConfig();
	},
    
    onRender: function(ct, position){
        Openwis.RequestSubscription.SubSelectionParameters.Cache.Period.superclass.onRender.call(this, ct, position);

        this.getTimeIntervalFieldSet().add(this.getTimeIntervalCheckboxGroup());
        this.getTimeIntervalFieldSet().add(new Ext.Button(this.getSelectAllAction()));
        this.getCachePeriodPanel().add(this.getTimeIntervalFieldSet());
        
        this.getTimePeriodFieldSet().add(this.getTimeExtentSelection());
		this.getCachePeriodPanel().add(this.getTimePeriodFieldSet());
        
        this.getCachePeriodPanel().doLayout();
    },

    getSelectAllAction: function() {
    	if (!this.selectAction) {
    		this.selectAction = new Ext.Action({
				text: 'Select All',
				scope: this,
				handler: function() {
					if (!this.selected || this.selected == false) {
		    			this.selected = true;
		    			this.selectedText = 'Unselect All';
		    		} else {
		    			this.selected = false;
		    			this.selectedText = 'Select All';
		    		}
				   for(var i= 0; i < this.getTimeIntervalCheckboxGroup().items.length; i++)
					{
					     this.getTimeIntervalCheckboxGroup().items.get(i).setValue(this.selected);
					}
					this.getSelectAllAction().setText(this.selectedText);
				}
			});
    	}
    	return this.selectAction;
    },
    
    getCachePeriodPanel: function() {
	    if(!this.cachePeriodPanel) {
	        this.cachePeriodPanel = new Ext.Container({
	            renderTo: this.el
	        });
	    }
	    return this.cachePeriodPanel;
	},
    
    getTimeIntervalFieldSet: function() {
        if(!this.timeIntervalFieldSet) {
	        this.timeIntervalFieldSet = new Ext.form.FieldSet({
    			title: Openwis.i18n('RequestSubscription.SSP.Cache.TimeInterval'),
    			collapsed: this.editConfig.type != "INTERVALS",
    			collapsible: true,
    			width: 470,
    			listeners: {
    				"beforeexpand": function (panel, animate) {
    					this.getTimePeriodFieldSet().collapse(false);
    				},
    				scope: this
    			}
    		});
	    }
	    return this.timeIntervalFieldSet;
    },
    
    getTimeIntervalCheckboxGroup: function() {
        if(!this.timeIntervalCheckboxGroup) {
            var timeIntervalCheckboxes = [];
    		for(var i = 0; i < 24; i++) {
    			var timeIntervalId = "";
    			timeIntervalId += String.leftPad(i, 2, "0") + ":00Z/";
    			timeIntervalId += String.leftPad(i, 2, "0") + ":59Z";
    			timeIntervalCheckboxes.push({boxLabel: "["+i+","+(i+1)+"]", name: "timeInterval", id: timeIntervalId, checked: (this.editConfig.intervals.indexOf(timeIntervalId) != -1)});
    		}
        
	        this.timeIntervalCheckboxGroup = new Ext.form.CheckboxGroup({
    			hideLabel: true,
    			columns: 4,
    			items: timeIntervalCheckboxes,
    			allowBlank: false
    		});
	    }
	    return this.timeIntervalCheckboxGroup;
    },
    
    getTimePeriodFieldSet: function() {
        if(!this.timePeriodFieldSet) {
	        this.timePeriodFieldSet = new Ext.form.FieldSet({
    			title: Openwis.i18n('RequestSubscription.SSP.Cache.TimePeriod'),
    			collapsed: this.editConfig.type != "PERIOD",
    			collapsible: true,
    			width: 470,
    			listeners: {
    				"beforeexpand": function (panel, animate) {
    					this.getTimeIntervalFieldSet().collapse(false);
    				},
    				scope: this
    			}
    		});
	    }
	    return this.timePeriodFieldSet;
    },
   
    getTimeExtentSelection: function() {
        if(!this.timeExtentSelection) {
            this.timeExtentSelection = new Openwis.Common.Components.DateTimeExtentSelection({
	            hideLabel: true,
	            dateSelection: false,
			    timeSelection: true,
			    defaultTimeFrom: this.editConfig.defaultTimeFrom,
			    defaultTimeTo: this.editConfig.defaultTimeTo,
			    processValue: function(value) {
			        if(value.timeFrom && value.timeTo) {
			            var tf = value.timeFrom;
			            var tt = value.timeTo;
			            return Openwis.Utils.Date.formatTimeInterval(tf, tt);
			        } else {
			            return null;
			        }
			    }
	        });
	    }
	    return this.timeExtentSelection;
    },
    
    //-------------------------------------------------------------
    
    initEditConfig: function() {
        this.editConfig = {};
        this.editConfig.type = "INTERVALS";
        this.editConfig.defaultTimeFrom = null;
        this.editConfig.defaultTimeTo = null;
        this.editConfig.intervals = [];
        
        if(this.ssp) {
            var intervals = this.ssp.value;
            
            if(intervals.length == 1) { //This is a simple period.
                var period = intervals[0].split("/");
                period[0] = period[0].replace(/Z/gi, "");
                period[1] = period[1].replace(/Z/gi, "");
                this.editConfig.type = "PERIOD";
                this.editConfig.defaultTimeFrom = period[0];
                this.editConfig.defaultTimeTo = period[1];
            } else {
                this.editConfig.type = "INTERVALS";
                this.editConfig.intervals = intervals;
            }
        }
    },
    
    getRawValue: function() {
		if(!this.getTimeIntervalFieldSet().collapsed) {
		    var out = [];
            this.getTimeIntervalCheckboxGroup().eachItem(function(item){
                if(item.checked){
                    out.push(item.id);
                }
            });
            return out;
		} else if(!this.getTimePeriodFieldSet().collapsed) {
			return this.getTimeExtentSelection().getValue();
		}
	},
	
	getValue: function() {
		return this.getRawValue();
	},
	
	buildValue: function() {
		return this.getValue();
	},
	
	validateValue : function(){
        if(!this.getTimeIntervalFieldSet().collapsed) {
			return this.getTimeIntervalCheckboxGroup().validate();
		} else if(!this.getTimePeriodFieldSet().collapsed) {
			return this.getTimeExtentSelection().validateValue();
		}
    },
    
    reset: function() {
	    this.getTimeIntervalFieldSet().reset();
	    this.getTimeIntervalCheckboxGroup().reset();
	    this.getTimePeriodFieldSet().reset();
	    this.getTimeExtentSelection().reset();
	}
});
