Ext.ns("Openwis.Utils.Header");

Openwis.Utils.Header.getHeaders = function(headerString) {
    var headerArray = headerString.replace(/\r\n/g,"$").split("$");
    var header = {};
    for(var i=0; i<headerArray.length; i++) {
        if (headerArray[i] !== "") {
            var items = headerArray[i].split(":");
            header[items[0].trim()] = items[1].trim()
        }
    }

    return header;
}
