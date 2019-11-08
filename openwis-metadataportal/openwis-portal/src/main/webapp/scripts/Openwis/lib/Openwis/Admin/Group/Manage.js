Ext.ns('Openwis.Admin.Group');

Openwis.Admin.Group.Manage = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n("Security.Group.Manage.Title"),
			layout: 'fit',
			width:350,
			height:150,
			modal: true,
			closeAction:'close'
		});
		Openwis.Admin.Group.Manage.superclass.initComponent.apply(this, arguments);
		
		if(this.isEdition()) {
			this.getInfosAndInitialize();
		} else {
			this.initialize();
		}
	},
	
	getInfosAndInitialize: function() {
		var params = {};
		params.name = this.editGroupName;
		
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.group.get',
			params: params,
			listeners: {
				success: function(config) {
					this.config = config;
					this.initialize();
				},
				failure: function(config) {
					this.close();
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {
		//-- Add data policy saved event.
		this.addEvents("groupSaved");
		
		//-- Create form panel.
		this.add(this.getGroupFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getSaveAction()));
		this.addButton(new Ext.Button(this.getCancelAction()));
		
		//-- If specified, populate forms from specified data policy.
		if(this.isEdition()) {
			this.getNameTextField().setValue(this.config.name);
			this.getGlobalCheckBox().setValue(this.config.global);
			this.getGlobalCheckBox().disable();
		}
		
		this.show();
	},
	
	getGroupFormPanel: function() {
		if(!this.groupFormPanel) {
			this.groupFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				labelWidth: 125
			});
			this.groupFormPanel.add(this.getNameTextField());
			this.groupFormPanel.add(this.getGlobalCheckBox());
		}
		return this.groupFormPanel;
	},
	
	/**
	 * The text field for the data policy name.
	 */
	getNameTextField: function() {
		if(!this.nameTextField) {
			this.nameTextField = new Ext.form.TextField({
				fieldLabel: Openwis.i18n("Security.Group.Manage.GroupName.Label"),
				name: 'name',
				allowBlank:false,
				width: 150
			});
		}
		return this.nameTextField;
	},
	
	/**
	 * The text area for the group global status.
	 */
	getGlobalCheckBox: function() {
		if(!this.globalCheckBox) {
			this.globalCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n("Security.Group.Manage.GlobalCheckBox.Label"),
				name: 'global',
				width: 125
			});
		}
		return this.globalCheckBox;
	},
	
	/**
	 * The Save action.
	 */
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text:'Save',
				scope: this,
				handler: function() {
					if(this.getGroupFormPanel().getForm().isValid()) {
						var saveHandler = new Openwis.Handler.Save({
							url: configOptions.locService+ '/xml.group.save',
							params: this.getGroup(),
							listeners: {
								success: function(config) {
									this.fireEvent("groupSaved");
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
				text:'Cancel',
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},
	
	//----- Utility methods.
	
	getGroup: function() {
		var group = {};
		
		//If edition...
		if(this.isEdition()) {
			group.id = this.config.id;
		}
		
		//The group attributes.
		group.name = this.getNameTextField().getValue();
		group.global = this.getGlobalCheckBox().getValue();
		
		return group;
	},
	
	/**
	 * Returns true if it is a creation, false otherwise.
	 */
	isCreation: function() {
		return (this.operationMode == 'Create');
	},
	
	/**
	 * Returns true if it is an edition, false otherwise.
	 */
	isEdition: function() {
		return (this.operationMode == 'Edit');
	}
});