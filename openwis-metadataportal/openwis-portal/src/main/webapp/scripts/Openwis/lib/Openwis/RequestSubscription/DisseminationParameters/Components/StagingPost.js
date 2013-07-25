Ext.ns('Openwis.RequestSubscription.DisseminationParameters.Components');

Openwis.RequestSubscription.DisseminationParameters.Components.StagingPost = Ext.extend(Ext.form.FormPanel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('RequestSubscription.Dissemination.StagingPost.Title'),
			border: false
		});
		Openwis.RequestSubscription.DisseminationParameters.Components.StagingPost.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	//----------------------------------------------------------------- Initialization.
	
	initialize: function() {
		this.add(this.getZipMode());
		this.add(this.getInformationPanel());
	},
	
	//----------------------------------------------------------------- Components.
	
	getZipMode:function() {
		if (!this.zipMode) {
			this.zipMode =  new Ext.form.ComboBox({
				store: new Ext.data.ArrayStore({
			        id: 0,
			        fields: [
			            'zipMode'
			        ],
			        data: [['NONE'], ['ZIPPED'], ['WMO_FTP']]
			    }),
				valueField: 'zipMode',
				displayField:'zipMode',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				fieldLabel: Openwis.i18n('RequestSubscription.Dissemination.ZippedMode.Title'),
				editable: false,
				value: 'NONE',
				width: 210
			});
		}
		return this.zipMode;
	},
	
	getInformationPanel: function() {
		if(!this.informationPanel) {
			this.informationPanel = new Ext.form.DisplayField({
			    hideLabel: true,
				value: Openwis.i18n('RequestSubscription.Dissemination.StagingPost.Warning', {purgeDays: 5})
			});
		}
		return this.informationPanel;
	},
	
	
	//----------------------------------------------------------------- Generic methods used by the Dissemination selection panel.
	
	getDisseminationValue: function() {
		return {zipMode : this.getZipMode().getValue()};
	},
	
	validate: function() {
		return {ok: true};
	},
	
	initializeFields: function(configObject) {
	    this.getZipMode().setValue(configObject.zipMode);
	}
});