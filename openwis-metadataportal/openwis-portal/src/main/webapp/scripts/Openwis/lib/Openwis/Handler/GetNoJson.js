Ext.ns('Openwis.Handler');

Openwis.Handler.GetNoJson = Ext.extend(Ext.util.Observable, {
	constructor: function(config) {
        this.url = config.url;
        if (typeof(config.params) == 'number') {
        	this.params = config.params + '';
        } else {
			this.params = config.params || {};
		}
		this.maskEl = config.maskEl ? config.maskEl : Ext.getBody();
		this.useLoadMask = (config.useLoadMask != 'undefined') ? config.useLoadMask : true;
		this.useHTMLMask = (config.useHTMLMask != 'undefined') ? config.useHTMLMask : false;
		this.loadingMessage = config.loadingMessage || Openwis.i18n('Common.Loading.Message');
		
        this.addEvents("success", "failure");
        this.listeners = config.listeners;

        // Call our superclass constructor to complete construction process.
        Openwis.Handler.GetNoJson.superclass.constructor.call(this, config)
    },
	
	proceed: function() {
	    if(this.useLoadMask) {
		    this.loadMask = new Ext.LoadMask(this.maskEl, {msg: this.loadingMessage});
		    this.loadMask.show();
		} else if(this.useHTMLMask) {
		    var innerHTML = this.maskEl.body.dom.innerHTML;
		    this.maskEl.body.dom.innerHTML = this.loadingMessage + innerHTML;
		}
		Ext.Ajax.request({
			url: this.url,
			success: this.cbSuccessful,
			failure: this.cbFailure,
			method: 'POST',
			params: this.params,
			scope: this
		});
	},
	
	cbSuccessful: function(ajaxResponse) {
		if(this.useLoadMask) { 
		    this.loadMask.hide();
		}
		this.fireSuccessEvent(ajaxResponse);
	},
	
	fireSuccessEvent: function(response) {
		this.fireEvent("success", response.responseText);
	},
	
	cbFailure: function(response) {
		if(this.useLoadMask) { 
		    this.loadMask.hide();
		}
		Openwis.Utils.MessageBox.displayInternalError(this.fireFailureEvent, this);
	},
	
	fireFailureEvent: function() {
		this.fireEvent("failure");
	}
});