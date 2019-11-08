Ext.ns('Openwis.Data');

Openwis.Data.JeevesJsonResponseHandler = function () {
	
	this.handleResponse = function(response) {
		var jsonData = response.responseText;
		var format = new OpenLayers.Format.XML();
		var xmlResponse = format.read(jsonData);
		
//		Debug 
//		console.log(xmlResponse);
		
		var childEl = format.getChildEl(xmlResponse);
		
		switch (childEl.nodeName)
		{
			case 'jsonData' :
				var json = "";
				for(var i = 0; i < childEl.childNodes.length; i++) {
					json += childEl.childNodes[i].nodeValue;
				}
//				Debug
//				console.log(json);
				return Ext.decode(json);
				break;
			case 'error':
//				Debug
//				console.info("TODO handle errors");
				break;
			default :
//				FIXME Handle not recognized child nodes
				//console.info("FIXME : childEl not recognized" + childEl);
		}
	};
	
	this.getJsonText = function(response) {
		var jsonData = response.responseText;
		var format = new OpenLayers.Format.XML();
		var xmlResponse = format.read(jsonData);
		
//		Debug 
//		console.log(xmlResponse);
		
		var childEl = format.getChildEl(xmlResponse);
		
		switch (childEl.nodeName)
		{
			case 'jsonData' :
				var json = "";
				for(var i = 0; i < childEl.childNodes.length; i++) {
					json += childEl.childNodes[i].nodeValue;
				}
//				Debug
//				console.log(json);
				return json;
				break;
			case 'error':
//				Debug
//				console.info("TODO handle errors");
				break;
			default :
//				FIXME Handle not recognized child nodes
				//console.info("FIXME : childEl not recognized" + childEl);
		}
	}
};