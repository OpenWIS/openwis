Ext.ns('Openwis.Utils.Misc');

Openwis.Utils.Misc.bytesToKMG = function(bytes) {
	var precision = 2;

    var kilobyte = 1024;
    var megabyte = kilobyte * 1024;
    var gigabyte = megabyte * 1024;
    var terabyte = gigabyte * 1024;
   
    if ((bytes >= 0) && (bytes < kilobyte)) {
        return bytes + ' Bytes';
 
    } else if ((bytes >= kilobyte) && (bytes < megabyte)) {
        return (bytes / kilobyte).toFixed(precision) + ' KBytes';
 
    } else if ((bytes >= megabyte) && (bytes < gigabyte)) {
        return (bytes / megabyte).toFixed(precision) + ' MBytes';
 
    } else if ((bytes >= gigabyte) && (bytes < terabyte)) {
        return (bytes / gigabyte).toFixed(precision) + ' GBytes';
 
    } else if (bytes >= terabyte) {
        return (bytes / terabyte).toFixed(precision) + ' TBytes';
    } else {
        return bytes + ' Bytes';
    }
};

Openwis.Utils.Misc.getMockHtml = function() {
	var msg = "";
    for(var i = 0; i < 5000;i++) {
        msg += "Testtt ";
    }
    return msg;
};

Openwis.Utils.Misc.createLabel = function(label) {
    return new Ext.Container({
        border: false,
        width: 100,
        html: label + ': ',
        style : {
           padding: '5px'
        }
    });
};
    
Openwis.Utils.Misc.createDummy = function() {
   return new Ext.Container({
        border: false,
        html: '&nbsp;'
   });
};