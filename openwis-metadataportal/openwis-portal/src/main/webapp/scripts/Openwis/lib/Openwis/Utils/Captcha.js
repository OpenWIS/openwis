Ext.ns('Openwis.Utils');

Openwis.Utils.Captcha = Ext.extend(Ext.form.NumberField, {
	
	initComponent: function() {
		this.initialize();
		
		Ext.apply(this, 
		{
			allowBlank: false,
			fieldLabel: this.a + ' + ' + this.b
		});
		Openwis.Utils.Captcha.superclass.initComponent.apply(this, arguments);
		
		
	},
	
	initialize:function() {
		 this.a = Math.ceil(Math.random() * 10);
	     this.b = Math.ceil(Math.random() * 10);       
	     this.c = this.a + this.b;
	},
	
	validator: function(value) {
		return value == this.c;
	}
});