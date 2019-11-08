Ext.namespace("Openwis.Data");
 
 
/**
 * The JSON Submit is a Submit action that send JSON instead of send URL Encoded data...
 * @param form The form to submit
 * @param options The options of the HTTP Request
 */
Openwis.Data.JeevesJsonSubmit = function(form, options) {
    Openwis.Data.JeevesJsonSubmit.superclass.constructor.call(this, form, options);
};
 
/**
 * We are extending the default Action Submit...
 */
Ext.extend(Openwis.Data.JeevesJsonSubmit, Ext.form.Action.Submit, {
    type: 'jeevesjsonsubmit',
 
    run : function() {
        var o = this.options;
        var method = this.getMethod();
        var isGet = method == 'GET';
        if (o.clientValidation === false || this.form.isValid()) {
            var encodedParams = Ext.encode(this.form.getValues());
 
			var box = Ext.MessageBox.wait('Please wait...', 'Submitting data');
 
            Ext.Ajax.request(Ext.apply(this.createCallback(o), {
                url:this.getUrl(isGet),
                method: method,
				success: function() {
					box.hide();
					o.success.apply(o.scope);
				},
				failure: function() {
					box.hide();
					o.failure.apply(o.scope);
				},
				scope: o.scope,
                headers: {'Content-Type': 'application/json'},
				params: encodedParams,
                isUpload: this.form.fileUpload
            }));
        } else if (o.clientValidation !== false) { // client validation failed
            this.failureType = Ext.form.Action.CLIENT_INVALID;
            this.form.afterAction(this, false);
        }
    }
});
 
/**
 * We register the new action type...
 */
Ext.apply(Ext.form.Action.ACTION_TYPES, {
    'jeevesjsonsubmit' : Openwis.Data.JeevesJsonSubmit
});