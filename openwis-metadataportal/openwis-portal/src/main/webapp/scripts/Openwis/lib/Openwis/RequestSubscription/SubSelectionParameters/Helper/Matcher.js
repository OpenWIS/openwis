Ext.ns('Openwis.RequestSubscription.SubSelectionParameters.Helper');

Openwis.RequestSubscription.SubSelectionParameters.Helper.Matcher = function() {
	
	/**
	 * Matches function.
	 */
	this.matches = function(availableFor, type, values) {
	    if(Ext.isEmpty(availableFor)) {
	        return true;
	    }
	    
	    if(type == 'SingleSelection' || type == 'MultipleSelection') {
	        return this.simpleMatch(availableFor, values);
	    } else if(type == 'DayPeriodSelection') {
	        return this.periodMatch(availableFor, values);
	    }
	    return true;
	};
	
	this.simpleMatch = function(availableFor, values) {
        return Openwis.Utils.Array.containsAny(availableFor, values);
	};
	
	this.periodMatch = function(availableFor, values) {
	     for(var i = 0; i < availableFor.length; i++) {
	         var period = availableFor[i].split("/");
	         for(var j = 0; j < values.length; j++) {
    	         if(values[j] >= period[0] && values[j] <= period[1]) {
    	             return true;
    	         }
    	     }
	     }
	     return false;
	};
};