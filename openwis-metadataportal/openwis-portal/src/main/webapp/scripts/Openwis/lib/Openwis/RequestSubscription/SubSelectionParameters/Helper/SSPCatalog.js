Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.Helper');

Openwis.RequestSubscription.SubSelectionParameters.Helper.SSPCatalog = function() {
	
	/**
	 * Sub-Selection parameters catalog management.
	 */
	this.setSubSelectionParameters = function(subSelectionParameters) {
	    this.subSelectionParameters = {};
	    Ext.each(subSelectionParameters, 
	        function(item) {
	            this.subSelectionParameters[item.code] = item;
	        }, 
	        this);
	};
	
	this.getParameterByCode = function(code) {
	    return this.subSelectionParameters[code];
	};
	
	/**
	 * Components managed by the catalog.
	 */
	 
	this.registerComponent = function(code, cmp, idx) {
	    var index = this.indexOf(code);
	    var struct = {code: code, component: cmp, indexInForm: idx};
	    if(index == -1) {
	        this.getComponentsCatalog().push(struct);
	    } else {
	        this.getComponentsCatalog()[index] = struct;
	    }
	};
	
	this.getComponent = function(code) {
	    var itemToFind = null;
	    Ext.each(this.getComponentsCatalog(), 
	        function(item, index, allItems) {
	            if(item.code == code) {
	                itemToFind = item;
	            }
	        }, 
	        this
	    );
	    return itemToFind;
	};
	
	this.getNextParameterCode = function(code) {
		var indexToFind = this.indexOf(code) + 1;
		
		if(indexToFind >= 0 && indexToFind < this.getComponentsCatalog().length) {
		    return this.getComponentsCatalog()[indexToFind].code;
		}
		return null;
	};
	
	this.indexOf = function(code) {
	    var indexOf = -1;
	    Ext.iterate(this.getComponentsCatalog(), 
		   function(item, index, allItems) {
		        if(item.code == code) {
		            indexOf = index;
		        }
		    }, 
		    this
		);
		return indexOf;
	};
	
	this.getComponentsCatalog = function() {
	    if(!this.componentsCatalog) {
	        this.componentsCatalog = [];
	    }
	    return this.componentsCatalog;
	};
	
	this.getScheduleCode = function() {
	    return 'CODE.SCHEDULE';
	};
};