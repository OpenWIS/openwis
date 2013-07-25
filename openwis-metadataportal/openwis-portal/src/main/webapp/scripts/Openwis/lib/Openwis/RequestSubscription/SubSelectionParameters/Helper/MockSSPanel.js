Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.Helper');

Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel = Ext.extend(Ext.form.DisplayField, {
	
	initComponent: function() {
		Ext.apply(this, {
		    value: 'TOTOOOOO',
		    fieldLabel: this.config.parameter.label
		});
		Openwis.RequestSubscription.SubSelectionParameters.Helper.MockSSPanel.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	valueDefined: true,
	
	initialize: function() {
	    this.addEvents("valueChanged");
	    
	    /*this.add(this.getSetUnsetDefinedButton());
	    this.add(this.getFireChangedEventButton());*/

        var matchingElements = this.getMatchingSelection(this.config.previousElementSelection);
    },
    
    isValueDefined: function() {
        return this.valueDefined;
    },
    
    getSelectedValue: function() {
        return [];
    },
    
    validate: function() {
        return {ok: true};
    },
    
    getMatchingSelection: function(values) {
        return values;
    },
    
    buildSSP: function() {
        return {code: this.config.parameter.code, values: ['mockValue01','mockValue02']};
    },
    
    //------------------------------------------------- Dummy buttons.
    
    getTestItems: function() {
        var testItems = [];
        for(var i = 0; i < 2; i++) {
            testItems.push(new Ext.Panel({html: 'Pan'+i, border: false}));
        }
        return testItems;
    },
    
    getSetUnsetDefinedButton: function() {
        if(!this.setUnsetDefinedButton) {
            this.setUnsetDefinedButton = new Ext.Button({
                text: 'Set/Unset for ' + this.config.parameter.code + ' ' + new Date().getTime(),
                handler: function() {
                    this.valueDefined = !this.valueDefined;
                    this.fireEvent("valueChanged", this.config.parameter.code);
                },
                scope: this
            });
        }
        return this.setUnsetDefinedButton;
    },
    
    getFireChangedEventButton: function() {
        if(!this.fireChangedEventButton) {
            this.fireChangedEventButton = new Ext.Button({
               text: 'Changed for ' + this.config.parameter.code + ' ' + new Date().getTime(),
                handler: function() {
                    this.fireEvent("valueChanged", this.config.parameter.code);
                },
                scope: this
            });
        }
        return this.fireChangedEventButton;
    }
});