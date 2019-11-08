Ext.ns('Openwis.RequestSubscription.DisseminationParameters.Components');

Openwis.RequestSubscription.DisseminationParameters.Components.MSSFSS = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('RequestSubscription.Dissemination.MSSFSS.Title'),
			border: false
		});
		Openwis.RequestSubscription.DisseminationParameters.Components.MSSFSS.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	//----------------------------------------------------------------- Initialization.
	
	initialize: function() {
		this.add(this.getChannelComboBox());
	},
	
	//----------------------------------------------------------------- Components.
	
	getChannelComboBox: function() {
		if(!this.channelComboBox) {
			var channelStore = new Ext.data.JsonStore ({
				fields: ['code', 'label']
			});
		
			this.channelComboBox = new Ext.form.ComboBox({
				store: channelStore,
				valueField: 'code',
				displayField:'label',
				mode: 'local',
				selectOnFocus:true,
				typeAhead: true,
				triggerAction: 'all',
				fieldLabel: Openwis.i18n('RequestSubscription.Dissemination.MSSFSS.Channel'),
				allowBlank: false,
				editable: false,
				width: 350
			});
		}
		return this.channelComboBox;
	},
	
	
	//----------------------------------------------------------------- Generic methods used by the Dissemination selection panel.
	
	getDisseminationValue: function() {
		var obj = {};
		obj.channel = {channel: this.getChannelComboBox().getValue()};
		return obj;
	},
	
	refresh: function(mssFss) {
	    var channels = [];
	    Ext.each(mssFss.mssFssChannels, 
	        function(item, index, allItems) {
	            channels.push({code: item, label: item});
	        }, 
	        this
	    );
		this.getChannelComboBox().getStore().loadData(channels);
		this.setVisible(mssFss.authorized);
	},
	
	initializeFields: function(configObject) {
	    this.getChannelComboBox().setValue(configObject);
	}
});