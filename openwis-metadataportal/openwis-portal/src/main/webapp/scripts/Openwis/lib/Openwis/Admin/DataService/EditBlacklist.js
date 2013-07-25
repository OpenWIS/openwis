Ext.ns('Openwis.Admin.Category');

Openwis.Admin.DataService.EditBlacklist = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Blacklist.Edit.Title'),
			layout: 'fit',
			width:350,
			height:380,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.DataService.EditBlacklist.superclass.initComponent.apply(this, arguments);
		this.initialize();
	},

	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add blacklist saved event.
		this.addEvents("blacklistSaved");
		
		//-- Create form panel.
		this.add(this.getBlacklistFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		//-- If specified, populate forms from specified data policy.
		this.getUserTextField().setValue(this.selectedRec.user);
		this.getNbWarnNumberField().setValue(this.selectedRec.nbDisseminationWarnThreshold);
		this.getVolWarnNumberField().setValue(this.selectedRec.volDisseminationWarnThreshold);
		this.getNbBlNumberField().setValue(this.selectedRec.nbDisseminationBlacklistThreshold);
		this.getVolBlNumberField().setValue(this.selectedRec.volDisseminationBlacklistThreshold);
		this.getNbCurrentTextField().setValue(this.selectedRec.userDisseminatedDataDTO.nbFiles);
		this.getVolCurrentTextField().setValue(this.selectedRec.userDisseminatedDataDTO.size);
		this.getBlacklistCheckBox().setValue(this.selectedRec.blacklisted);
		
		this.show();
	},
	
	getBlacklistFormPanel: function() {
		if(!this.blacklistFormPanel) {
			this.blacklistFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 125
			});
			this.blacklistFormPanel.add(this.getUserTextField());
			this.blacklistFormPanel.add(this.getNbWarnNumberField());
			this.blacklistFormPanel.add(this.getVolWarnNumberField());
			this.blacklistFormPanel.add(this.getNbBlNumberField());
			this.blacklistFormPanel.add(this.getVolBlNumberField());
			this.blacklistFormPanel.add(this.getNbCurrentTextField());
			this.blacklistFormPanel.add(this.getVolCurrentTextField());
			this.blacklistFormPanel.add(this.getBlacklistCheckBox());
		}
		return this.blacklistFormPanel;
	},

	/**
	 * The text field for the username.
	 */
	getUserTextField: function() {
		if(!this.userTextField) {
			this.userTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Blacklist.user'),
				name: 'username',
				disabled: true,
				width: 150
			});
		}
		return this.userTextField;
	},

	/**
	 * The number field for the nbWarnNumberField.
	 */
	getNbWarnNumberField: function() {
		if(!this.nbWarnNumberField) {
			this.nbWarnNumberField = new Ext.form.NumberField({
				allowBlank : false,
				fieldLabel: Openwis.i18n('Blacklist.nbDisseminationWarnThreshold'),
				name: 'nbWarn',
				width: 150,
			    autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '16'}
			});
		}
		return this.nbWarnNumberField;
	},

	/**
	 * The number field for the volWarnNumberField.
	 */
	getVolWarnNumberField: function() {
		if(!this.volWarnNumberField) {
			this.volWarnNumberField = new Ext.form.NumberField({
				allowBlank : false,
				fieldLabel: Openwis.i18n('Blacklist.volDisseminationWarnThreshold'),
				name: 'volWarn',
				maxLength: '16',
				width: 150,
			    autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '16'}
			});
		}
		return this.volWarnNumberField;
	},

	/**
	 * The number field for the nbBlNumberField.
	 */
	getNbBlNumberField: function() {
		if(!this.nbBlNumberField) {
			this.nbBlNumberField = new Ext.form.NumberField({
				allowBlank : false,
				fieldLabel: Openwis.i18n('Blacklist.nbDisseminationBlacklistThreshold'),
				name: 'nbBl',
				width: 150,
			    autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '16'}
			});
		}
		return this.nbBlNumberField;
	},

	/**
	 * The number field for the volBlNumberField.
	 */
	getVolBlNumberField: function() {
		if(!this.volBlNumberField) {
			this.volBlNumberField = new Ext.form.NumberField({				
				allowBlank : false,
				fieldLabel: Openwis.i18n('Blacklist.volDisseminationBlacklistThreshold'),
				name: 'volBl',
				maxLength: '16',
				width: 150,
			    autoCreate: {tag: 'input', type: 'text', size: '20', autocomplete: 'off', maxlength: '16'}
			});
		}
		return this.volBlNumberField;
	},

	/**
	 * The text field for the nbCurrentTextField.
	 */
	getNbCurrentTextField: function() {
		if(!this.nbCurrentTextField) {
			this.nbCurrentTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Blacklist.nbDisseminationCurrent'),
				name: 'nbCurrent',
				disabled: true,
				width: 150
			});
		}
		return this.nbCurrentTextField;
	},

	/**
	 * The text field for the volCurrentTextField.
	 */
	getVolCurrentTextField: function() {
		if(!this.volCurrentTextField) {
			this.volCurrentTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n('Blacklist.volDisseminationCurrent'),
				name: 'volCurrent',
				disabled: true,
				width: 150
			});
		}
		return this.volCurrentTextField;
	},

	/**
	 * The blacklist checkbox.
	 */
	getBlacklistCheckBox: function() {
		if(!this.blacklistCheckBox) {
			this.blacklistCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('Blacklist.blacklist'),
				name: 'blacklistCb',
				width: 150,
    			listeners : {
    				check: function(checkbox, checked) {
    				    if(!checked) {
    					    //this.getUseAccountCompositeField().hide();
    				    } else {
    					    //this.getUseAccountCompositeField().show();
    				    }
    				},
    				scope: this
    			}
			});
		}
		return this.blacklistCheckBox;
	},

	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text:Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
					if(this.getBlacklistFormPanel().getForm().isValid()) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.editBlacklist.save',
							params: this.getBlacklistUpdated(),
							listeners: {
								success: function(config) {
									this.fireEvent("blacklistSaved");
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
	},
	
	//----- Utility methods.
	
	getBlacklistUpdated: function() {
		var blacklistUpdated = {};

		//The category attributes.
		blacklistUpdated.user = this.selectedRec.user;
		blacklistUpdated.nbDisseminationWarnThreshold = this.getNbWarnNumberField().getValue();
		blacklistUpdated.volDisseminationWarnThreshold = this.getVolWarnNumberField().getValue();
		blacklistUpdated.nbDisseminationBlacklistThreshold = this.getNbBlNumberField().getValue();
		blacklistUpdated.volDisseminationBlacklistThreshold = this.getVolBlNumberField().getValue();
		blacklistUpdated.blacklisted = this.getBlacklistCheckBox().checked;
		return blacklistUpdated;
	}

});