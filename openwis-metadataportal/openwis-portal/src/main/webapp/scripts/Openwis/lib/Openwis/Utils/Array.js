Ext.ns('Openwis.Utils.Array');

Openwis.Utils.Array.inArray = function(array, value) {
	for(var i = 0; i < array.length; i++) {
        if(array[i] == value) { 
			return true;
		}
    }
    return false;
};

Openwis.Utils.Array.intersection = function(array1, array2) {
    var intersection = [];
	for(var i = 0; i < array1.length; i++) {
        if(Openwis.Utils.Array.inArray(array2, array1[i])) {
            intersection.push(array1[i]);
		}
    }
    return intersection;
};

Openwis.Utils.Array.containsAny = function(array1, array2) {
    return Openwis.Utils.Array.intersection(array1, array2).length > 0;
};


Openwis.Utils.Array.isEmpty = function(array) {
	return array.length < 1;
};