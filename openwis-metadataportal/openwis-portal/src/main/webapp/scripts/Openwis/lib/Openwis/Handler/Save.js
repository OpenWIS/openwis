Ext.ns('Openwis.Handler');

Openwis.Handler.Save = Ext.extend(Ext.util.Observable, {
	constructor: function(config) {
        this.url = config.url;
        if (typeof(config.params) == 'number') {
        	this.params = config.params + '';
        } else {
			this.params = config.params || {};
		}
		this.successWindow = config.successWindow ? config.successWindow : false;
		
        this.addEvents("success", "failure");
        this.listeners = config.listeners;
		
        // Call our superclass constructor to complete construction process.
        Openwis.Handler.Save.superclass.constructor.call(this, config)
    },
	
	proceed: function() {
		this.window = Ext.MessageBox.wait('Please wait...', 'Submitting data');
		Ext.Ajax.request({
			url: this.url,
			success: this.cbSuccessful,
			failure: this.cbFailure,
			method: "POST",
			headers: {
			   'Content-Type': 'application/json; charset=utf-8',
			   'Accept':'application/json; charset=utf-8'
			},
			jsonData: this.params,
			scope: this
		});
	},
	
	cbSuccessful: function(ajaxResponse) {
		this.window.hide();
		var responseHandler = new Openwis.Data.JeevesJsonResponseHandler();
		var response = responseHandler.handleResponse(ajaxResponse);
		if(response.ok) {
			if(this.successWindow) {
				Openwis.Utils.MessageBox.displaySuccessMsg("Changes saved successfully.", this.fireSuccessEvent, this);
			} else {
				this.fireSuccessEvent(response.o);
			}
		} else {
			Openwis.Utils.MessageBox.displayErrorMsg(response.o);
		}
	},
	
	fireSuccessEvent: function(o) {
		if(o) {
			this.fireEvent("success", o);
		} else {
			this.fireEvent("success");
		}
	},
	
	cbFailure: function(response) {
		this.window.hide();
		Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent, this);
	},
	
	fireFailureEvent: function() {
		this.fireEvent("failure");
	}
});