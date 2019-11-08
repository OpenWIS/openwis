/**
 * Input Dialog for Replication Filter
 */
Ext.ns('Openwis.Admin.DataService');

Openwis.Admin.DataService.ReplicationFilterDialog = Ext.extend(Ext.Window, {
	saveRequestType: 'ADD_FILTER',
	isNewFilter: true,
	
	initComponent: function() {
		Ext.apply(this, {
			title: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.Title'),
			layout: 'fit',
			width: 350,
			height: 222,
			modal: true,
			closeAction: 'close',
			resizable: false
		});
		Openwis.Admin.DataService.ReplicationFilterDialog.superclass.initComponent.apply(this, arguments);
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
		this.getSourceTextField().setValue(this.selectedFilter.source);
		this.getTypeTextField().setValue(this.selectedFilter.type);
		this.getActiveCheckBox().setValue(this.selectedFilter.active);
		
		this.setTitle(Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.Title'));
		
		this.isNewFilter = this.operationMode == "New";
		this.saveRequestType = this.isNewFilter ? "ADD_FILTER" : "UPDATE_FILTER";
		
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
			this.filterInputFormPanel.add(this.getActiveCheckBox());
			this.filterInputFormPanel.add(this.getSourceTextField());
			this.filterInputFormPanel.add(this.getTypeTextField());
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
				fieldLabel: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.RegEx'),
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
				fieldLabel: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.Description'),
				name: 'decription',
				width: 250
			});
		}
		return this.descriptionTextField;
	},
	
	/**
	 * The text field for the filter source.
	 */
	getSourceTextField: function() {
		if(!this.sourceTextField) {
			this.sourceTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.Gisc'),
				name: 'source',
				width: 250
			});
		}
		return this.sourceTextField;		
	},

	/**
	 * The text field for the filter type.
	 */
	getTypeTextField: function() {
		if(!this.sourceTypeField) {
			this.sourceTypeField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.Type'),
				name: 'type',
				width: 250
			});
		}
		return this.sourceTypeField;		
	},

	/**
	 * The check box for the filter active.
	 */
	getActiveCheckBox: function() {
		if(!this.activeCheckBox) {
			this.activeCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.Active'),
				name: 'active',
				width: 250
			});
		}
		return this.activeCheckBox;		
	},
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if (!this.saveAction) {
			this.saveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {				
					if (this.getFilterInputFormPanel().getForm().isValid()) {
						var saveHandler = new Openwis.Handler.GetNoJson({
							url: configOptions.locService + this.locationService,
							params: {
								requestType: this.saveRequestType,								
								regex: this.getRegExTextField().getValue(),
								description: this.getDescriptionTextField().getValue(),
								source: this.getSourceTextField().getValue(),
								type: this.getTypeTextField().getValue(),
								active: this.getActiveCheckBox().getValue(),
								editSource: this.isNewFilter ? null : this.selectedFilter.source,
								editRegex: this.isNewFilter ? null : this.selectedFilter.regex
							},
							listeners: {
								success: function(responseText) {
									if (this.checkSaveResponse(responseText)) {
										this.fireEvent("filterSaved");
										this.close();
									}
								},
								failure: function() {
									this.showError(Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.ErrorDuplicate'));
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
				text: Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	/**
	  * Checks the response from the save request. Shows errors if any.
	  * @responseText xml response from service to be evaluated
	  */
	checkSaveResponse: function(responseText) {
		var result = false;
		
		var resultElement = Openwis.Utils.Xml.getElement(responseText, 'result');
		var attributes = resultElement.attributes;
		var success = Openwis.Utils.Xml.getAttribute(attributes, 'success');					
		var msg = null;
		
		if (success != null) {
			if (success.nodeValue == 'true') {
				result = true;
			}
			else {
				var errorMsg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
				if (errorMsg != null) {
					msg = errorMsg;
				}
				else {
					msg = Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.ErrorDuplicate');
				}
			}
		}
		else {
			var errorMsg = Openwis.Utils.Xml.getAttributeValue(attributes, 'error');
			if (errorMsg != null) {
				msg = errorMsg;
			}
			else {
				msg = Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.ErrorDuplicate');
			}
		}
		if (msg != null) {
			this.showError(msg);
		}
		return result;
	},
	
	showError: function(msg) {
		var msgText = Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.ErrorMsg') + msg;
		Ext.Msg.show({
			title: Openwis.i18n('CacheConfiguration.ReplicationFilterDialog.ErrorTitle'),
			msg:  msgText,
			buttons: Ext.Msg.OK,
			scope: this,
			icon: Ext.MessageBox.ERROR
		});									
	}
});