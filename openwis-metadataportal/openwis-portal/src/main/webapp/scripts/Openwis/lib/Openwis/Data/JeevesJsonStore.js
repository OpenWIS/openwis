Ext.ns('Openwis.Data');

Openwis.Data.JeevesJsonStore = Ext.extend(Ext.data.Store, {
    
    constructor: function(config){
        Openwis.Data.JeevesJsonStore.superclass.constructor.call(this, Ext.apply(config, {
            reader: new Openwis.Data.JeevesJsonReader(config)
        }));
    },
    listeners: {
    	exception: function(misc, type, action, options, response, arg) {
    		if (response.status == 401) {
    			//display Session Expired -> button reconnect
				new Openwis.Utils.MessageBoxServiceNotAllowed();
    		}
    	}
    }
});