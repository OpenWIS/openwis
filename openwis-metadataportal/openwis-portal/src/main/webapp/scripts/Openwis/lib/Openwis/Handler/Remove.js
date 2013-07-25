Ext.ns('Openwis.Handler');

Openwis.Handler.Remove = Ext.extend(Ext.util.Observable, {
	constructor: function(config) {
        this.url = config.url;
		if (typeof(config.params) == 'number') {
        	this.params = config.params + '';
        } else {
			this.params = config.params || {};
		}
		this.confirmMsg = config.confirmMsg;
		if (this.confirmMsg == null) {
		this.confirmMsg = 'Do you confirm the action ?';
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
		if(response.ok) {
			Openwis.Utils.MessageBox.displaySuccessMsg("Deletion completed successfully.", this.fireSuccessEvent, this);
		} else {
			Openwis.Utils.MessageBox.displayErrorMsg(response.o, this.fireFailureEvent, this);
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