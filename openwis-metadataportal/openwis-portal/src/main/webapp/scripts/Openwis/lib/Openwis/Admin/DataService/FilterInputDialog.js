/**
 * Input Dialog for Ingestion and Feeding Filters
 */
Ext.ns('Openwis.Admin.DataService');

Openwis.Admin.DataService.FilterInputDialog = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, {
			title: Openwis.i18n('CacheConfiguration.FilterInputDialog.Title'),
			layout: 'fit',
			width: 350,
			height: 135,
			modal: true,
			closeAction: 'close',
			resizable: false
		});
		Openwis.Admin.DataService.FilterInputDialog.superclass.initComponent.apply(this, arguments);
		this.initialize();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add blacklist saved event.
		this.addEvents("filterSaved");
		
		//-- Create form panel.
		this.add(this.getFilterInputFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		//-- If specified, populate forms from specified data policy.
		this.getRegExTextField().setValue(this.selectedFilter.regex);
		this.getDescriptionTextField().setValue(this.selectedFilter.description);
		
		this.setTitle(Openwis.i18n('CacheConfiguration.FilterInputDialog.Title'));
		
		// this.show();
	},
	
	getFilterInputFormPanel: function() {
		if(!this.filterInputFormPanel) {
			this.filterInputFormPanel = new Ext.form.FormPanel({
				border: false,
				itemCls: 'formItems',
				labelWidth: 70,
				style: {
					marginTop: '3px',
					marginLeft: '6px'
				}
			});
			this.filterInputFormPanel.add(this.getRegExTextField());
			this.filterInputFormPanel.add(this.getDescriptionTextField());
		}
		return this.filterInputFormPanel;
	},
	
	/**
	 * The text field for the regular expression.
	 */
	getRegExTextField: function() {
		if(!this.regExTextField) {
			this.regExTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('CacheConfiguration.FilterInputDialog.RegEx'),
				name: 'regex',
				width: 250
			});
		}
		return this.regExTextField;
	},

	/**
	 * The text field for the filter description.
	 */
	getDescriptionTextField: function() {
		if(!this.descriptionTextField) {
			this.descriptionTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('CacheConfiguration.FilterInputDialog.Description'),
				name: 'decription',
				width: 250
			});
		}
		return this.descriptionTextField;
	},
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
					if(this.getFilterInputFormPanel().getForm().isValid()) {
						var saveHandler = new Openwis.Handler.GetNoJson({
							url: configOptions.locService + this.locationService,
							params: {
			    				requestType: 'ADD_FILTER',								
								regex: this.getRegExTextField().getValue(),
								description: this.getDescriptionTextField().getValue()
							},
							listeners: {
								success: function(responseText) {
									var resultElement = Openwis.Utils.Xml.getElement(responseText, 'result');
									var attributes = resultElement.attributes;
									var success = Openwis.Utils.Xml.getAttribute(attributes, 'success');					
									var msg = 'The filter has been successfully added.';
									var isError = false;
									if (success != null) {
										if (!success.nodeValue == 'true') {
											isError = true;
											msg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
											Openwis.Utils.MessageBox.displayInternalError();		
										}
									}
									else {
										isError = true;
										msg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
										Openwis.Utils.MessageBox.displayInternalError();		
									}					
									
									this.fireEvent("filterSaved", msg, isError);
									this.close();
								},
								scope: this
							}
						});
						saveHandler.proceed();
					}
				}
			});
		}
		return this.saveAction;
	},
	
	/**
	 * The Cancel action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	}
});