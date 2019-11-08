Ext.ns('Openwis.Handler');

Openwis.Handler.Index = Ext.extend(Ext.util.Observable, {
	constructor: function(config) {
        this.url = config.url;
        if (typeof(config.params) == 'number') {
        	this.params = config.params + '';
        } else {
			this.params = config.params || {};
		}
        this.confirmMsg = config.confirmMsg;
		if (this.confirmMsg == null) {
		this.confirmMsg = 'This operation may take some time on large catalogs and should not be done during peak usage. Continue?';
		}
		
        this.addEvents("success", "failure");
        this.listeners = config.listeners;

        // Call our superclass constructor to complete construction process.
        Openwis.Handler.Remove.superclass.constructor.call(this, config)
    },
	
	proceed: function() {
	    
		Ext.MessageBox.confirm('Confirm ?', this.confirmMsg , function(btnClicked) {
				if(btnClicked == 'yes') {
					this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg:"Loading... Please wait..."});
					this.loadMask.show();
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
				}
			}, 
			this
		);
	},
	
	cbSuccessful: function(ajaxResponse) {
		this.loadMask.hide();
		var responseHandler = new Openwis.Data.JeevesJsonResponseHandler();
		var response = responseHandler.handleResponse(ajaxResponse);
		if (!response) {
			//display Session Expired -> button reconnect
			new Openwis.Utils.MessageBoxServiceNotAllowed();
		}
		if(response.ok) {
			Openwis.Utils.MessageBox.displaySuccessMsg("Index operation was successful.", this.fireSuccessEvent, this);
		} else {
			Openwis.Utils.MessageBox.displayErrorMsg(response.o);
		}
	},
	
	fireSuccessEvent: function() {
		this.fireEvent("success");
	},
	
	cbFailure: function(response) {
		this.loadMask.hide();
		Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent, this);
	},
	
	fireFailureEvent: function() {
		this.fireEvent("failure");
	}
});