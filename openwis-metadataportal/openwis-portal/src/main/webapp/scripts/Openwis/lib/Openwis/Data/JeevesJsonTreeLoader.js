Ext.ns('Openwis.Data');

Openwis.Data.JeevesJsonTreeLoader = Ext.extend(Ext.tree.TreeLoader, {
    
	processResponse : function(response, node, callback, scope){
		var responseHandler = new Openwis.Data.JeevesJsonResponseHandler();
		var json = responseHandler.getJsonText(response);
        if(!json) {
            throw {message: 'JsonReader.read: Json object not found'};
        }
        
        //var json = response.responseText;
        try {
            var o = response.responseData || Ext.decode(json);
            node.beginUpdate();
            for(var i = 0, len = o.length; i < len; i++){
                var n = this.createNode(o[i]);
                if(n){
                    node.appendChild(n);
                }
            }
            node.endUpdate();
            this.runCallback(callback, scope || node, [node]);
        }catch(e){
            this.handleFailure(response);
        }
    }

});