Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.SourceSelection');

Openwis.RequestSubscription.SubSelectionParameters.SourceSelection.Source = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			layout: 'column'
		});
		Openwis.RequestSubscription.SubSelectionParameters.SourceSelection.Source.superclass.initComponent.apply(this, arguments);
		
		this.addEvents("valueChanged");
	},
	
	initialize: function() {
	    this.add(this.getGeographicalExtentContainer());
	    this.add(this.getListBoxContainer());
	},
	
	//----------------------------------------------------------------- Initialization of the panels.
	
	getGeographicalExtentContainer: function() {
	    if(!this.geographicalExtentContainer) {
	        this.geographicalExtentContainer = new Ext.Container ({
				columnWidth: .5,
				style: {
					margin: '5px'
				}
			});
			this.add(this.getGeographicalExtentRadio());
	        this.add(this.getGeographicalExtent());
	        
	    }
	    return this.geographicalExtentContainer;
	},
	
	
	getGeographicalExtentRadio: function() {
	    if(!this.geographicalExtentRadio) {
	        this.geographicalExtentRadio = new Ext.form.Radio({
				name: 'rb-col',
				inputValue: 'GEO_EXTENT',
				boxLabel: "In the map",
				checked: true,
				listeners : {
					check: function(checkbox, checked) {
						if(checked) {
                			this.getGeographicalExtent().enable();
                		} else {
                			this.getGeographicalExtent().disable();
                		}
					},
					scope: this
				}
			});
	    }
	    return this.geographicalExtentRadio;
	},
	
	getGeographicalExtent: function() {
        if(!this.geographicalExtent) {
            var configGeo = {};
            Ext.apply(configGeo, this.config, {
                width: 200,
				height: 150,
				style: {
					marginTop: '5px'
				}
			});
            
            this.geographicalExtent = new Openwis.RequestSubscription.SubSelectionParameters.GeographicalSelection.GeographicalExtent(configGeo);
            this.geographicalExtent.on('valueChanged', 
                function(args) {
                    this.fireEvent('valueChanged', args);
                }, 
                this
            );
        }
        return this.geographicalExtent;
	},
	
	getListBoxContainer: function() {
	    if(!this.listBoxContainer) {
	        this.listBoxContainer = new Ext.Container ({
				columnWidth: .5,
				style: {
					margin: '5px'
				}
			});
			this.add(this.getListBoxRadio());
	        this.add(this.getListBox());
	        
	    }
	    return this.listBoxContainer;
	},
	
	getListBoxRadio: function() {
	    if(!this.listBoxRadio) {
	        this.listBoxRadio = new Ext.form.Radio({
				name: 'rb-col',
				inputValue: 'LIST',
				boxLabel: "In the list",
				checked: true,
				listeners : {
					check: function(checkbox, checked) {
						if(checked) {
                			this.getListBox().enable();
                		} else {
                			this.getListBox().disable();
                		}
					},
					scope: this
				}
			});
	    }
	    return this.listBoxRadio;
	},
	
	getListBox: function() {
	    if(!this.listBox) {
            var configListBox = {};
            Ext.apply(configListBox, this.config, {
                disabled: true,
				style: {
					marginTop: '5px'
				}
			});
            
            this.listBox = new Openwis.RequestSubscription.SubSelectionParameters.MultipleSelection.ListBox(configListBox);
            this.listBox.on('valueChanged', 
                function(args) {
                    this.fireEvent('valueChanged', args);
                }, 
                this
            );
        }
        return this.listBox;
	},
	
	
	//----------------------------------------------------------------- Generic methods used by the wizard.
	
	isValueDefined: function() {
	    if(this.getGeographicalExtentRadio().checked) {
	        return this.getGeographicalExtent().isValueDefined();
	    } else {
	        return this.getListBox().isValueDefined();
	    }
	},
	
	
	getSelectedValue: function() {
	    if(this.getGeographicalExtentRadio().checked) {
	        return this.getGeographicalExtent().getSelectedValue();
	    } else {
	        return this.getListBox().getSelectedValue();
	    }
	},
	
	
	buildSSP: function() {
	    if(this.getGeographicalExtentRadio().checked) {
	        return this.getGeographicalExtent().buildSSP();
	    } else {
	        return this.getListBox().buildSSP();
	    }
	},
	
	validate: function() {
	     if(this.getGeographicalExtentRadio().checked) {
	        return this.getGeographicalExtent().validate();
	    } else {
	        return this.getListBox().validate();
	    }
	}
});