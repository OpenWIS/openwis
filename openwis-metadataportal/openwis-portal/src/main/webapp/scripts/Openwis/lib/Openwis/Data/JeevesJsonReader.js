Ext.ns('Openwis.Data');

Openwis.Data.JeevesJsonReader = Ext.extend(Ext.data.JsonReader, {
    
    read : function(response) {
		var responseHandler = new Openwis.Data.JeevesJsonResponseHandler();
		var o = responseHandler.handleResponse(response);
        if(!o) {
            throw {message: 'JsonReader.read: Json object not found'};
        }
        return this.readRecords(o);
    }
});