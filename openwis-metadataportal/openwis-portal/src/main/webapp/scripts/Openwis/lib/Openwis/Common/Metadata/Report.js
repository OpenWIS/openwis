Ext.ns('Openwis.Common.Metadata.Report');

Openwis.Common.Metadata.Report = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			title: Openwis.i18n('Metadata.Report'),
			layout: 'fit',
			width:720,
			height:480,
			modal: true,
			border: false,
			autoScroll: true,
			closeAction:'close'
		});
		Openwis.Common.Metadata.Report.superclass.initComponent.apply(this, arguments);
		this.getInfosAndInitialize();
	},
	
	getInfosAndInitialize: function() {
		if (this.lastResult != null && this.lastResult != '')
		{
			this.initialize();
		}
		else
		{
			Openwis.Utils.MessageBox.displaySuccessMsg("No Report available.", this.fireSuccessEvent, this);
		}
	},
	
	/**
	 * Initializes the window.
	 */
	initialize: function() {

		//-- Create form panel.
		this.add(this.getReportFormPanel());
		
		//-- Add buttons.
		this.addButton(new Ext.Button(this.getCancelAction()));

		// Display fields
		this.getDateDisplayField().setValue(Openwis.Utils.Date.formatDateTimeUTCfromLong(this.lastResult.date));
		this.getTotalDisplayField().setValue(this.lastResult.total);
		if (this.lastResult.fail)
		{
			this.getFailDisplayField().setValue("Task Failed");
		}
		this.getAddedDisplayField().setValue(this.lastResult.added);
		this.getUpdatedDisplayField().setValue(this.lastResult.updated);
		this.getUnchangedDisplayField().setValue(this.lastResult.unchanged);
		this.getLocallyRemovedDisplayField().setValue(this.lastResult.locallyRemoved);
		this.getUnknownSchemaDisplayField().setValue(this.lastResult.unknownSchema);
		this.getUnexpectedDisplayField().setValue(this.lastResult.unexpected);
		this.getBadFormatDisplayField().setValue(this.lastResult.badFormat);
		this.getDoesNotValidateDisplayField().setValue(this.lastResult.doesNotValidate);
		this.getIgnoredDisplayField().setValue(this.lastResult.ignored);
		this.show();
	},
	
	getReportFormPanel: function() {
		if(!this.reportFormPanel) {
			this.reportFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				width:600,
				border: false,
				items:[
				{
                    layout:'table',
                    border : false,
                    layoutConfig: {
                        columns: 4
                    },
                    defaults: {
                    	bodyStyle: 'padding:0 18px 0 0'
				    },
                    items: [
						this.getFailDisplayField(),
						new Openwis.Utils.Misc.createDummy(),
						new Openwis.Utils.Misc.createDummy(),
						new Openwis.Utils.Misc.createDummy(),
						   
						this.createLabel(Openwis.i18n('Metadata.Report.Date')),
						this.getDateDisplayField(),
						new Openwis.Utils.Misc.createDummy(),
						new Openwis.Utils.Misc.createDummy(),
						
						new Openwis.Utils.Misc.createDummy(),
						new Openwis.Utils.Misc.createDummy(),
						new Openwis.Utils.Misc.createDummy(),
						new Openwis.Utils.Misc.createDummy(),
						
						this.createLabel(Openwis.i18n('Metadata.Report.Total')),
						this.getTotalDisplayField(),
						this.createLabel(Openwis.i18n('Metadata.Report.UnknownSchema')),
						this.getUnknownSchemaDisplayField(),
						
						
						this.createLabel(Openwis.i18n('Metadata.Report.Added')),
						this.getAddedDisplayField(),
						this.createLabel(Openwis.i18n('Metadata.Report.Unexpected')),
						this.getUnexpectedDisplayField(),
						
						this.createLabel(Openwis.i18n('Metadata.Report.Updated')),
						this.getUpdatedDisplayField(),
						this.createLabel(Openwis.i18n('Metadata.Report.BadFormat')),
						this.getBadFormatDisplayField(),
						
						this.createLabel(Openwis.i18n('Metadata.Report.LocallyRemoved')),
						this.getLocallyRemovedDisplayField(),
						this.createLabel(Openwis.i18n('Metadata.Report.DoesNotValidate')),
						this.getDoesNotValidateDisplayField(),
						
						this.createLabel(Openwis.i18n('Metadata.Report.Unchanged')),
						this.getUnchangedDisplayField(),
						this.createLabel(Openwis.i18n('Metadata.Report.Ignored')),
						this.getIgnoredDisplayField()
				
					]
				},
				
				// add a button to download the last report details
				this.addDownloadReportButton(),
				
				new Ext.Container({
			        html: Openwis.i18n('Metadata.Report.Error.Info'),
					border: false,
					cls: 'infoMsg',
					style: {
					    margin: '0px 0px 5px 0px'
					}
				}),
				this.getReportErrorGrid()
				]
			});
		}
		return this.reportFormPanel;
	},

	/**
	 * Create label with width to 200.
	 */
	createLabel: function(label) {
		return new Ext.Container({
        	border: false,
        	width: 200,
        	html: label + ': ',
        	style : {
           	padding: '5px'
        	}
   	 	});
	},
	
	/**
	 * The display field for the date.
	 */
	getDateDisplayField: function() {
		if(!this.dateDisplayField) {
			this.dateDisplayField = new Ext.form.DisplayField({
				name: 'date'
			});
		}
		return this.dateDisplayField;
	},

	/**
	 * The display field for the total.
	 */
	getTotalDisplayField: function() {
		if(!this.totalDisplayField) {
			this.totalDisplayField = new Ext.form.DisplayField({
				name: 'total'
			});
		}
		return this.totalDisplayField;
	},

	/**
	 * The display field for the fail.
	 */
	getFailDisplayField: function() {
		if(!this.failDisplayField) {
			this.failDisplayField = new Ext.form.DisplayField({
				hideLabel: true,
				cls: 'errorMsg',
				name: 'fail'
			});
		}
		return this.failDisplayField;
	},

	/**
	 * The display field for the added.
	 */
	getAddedDisplayField: function() {
		if(!this.addedDisplayField) {
			this.addedDisplayField = new Ext.form.DisplayField({
				name: 'added'
			});
		}
		return this.addedDisplayField;
	},

	/**
	 * The display field for the updated.
	 */
	getUpdatedDisplayField: function() {
		if(!this.updatedDisplayField) {
			this.updatedDisplayField = new Ext.form.DisplayField({
				name: 'updated'
			});
		}
		return this.updatedDisplayField;
	},

	/**
	 * The display field for the unchanged.
	 */
	getUnchangedDisplayField: function() {
		if(!this.unchangedDisplayField) {
			this.unchangedDisplayField = new Ext.form.DisplayField({
				name: 'unchanged'
			});
		}
		return this.unchangedDisplayField;
	},

	/**
	 * The display field for the locallyRemoved.
	 */
	getLocallyRemovedDisplayField: function() {
		if(!this.locallyRemovedDisplayField) {
			this.locallyRemovedDisplayField = new Ext.form.DisplayField({
				name: 'locallyRemoved'
			});
		}
		return this.locallyRemovedDisplayField;
	},

	/**
	 * The display field for the unknownSchema.
	 */
	getUnknownSchemaDisplayField: function() {
		if(!this.unknownSchemaDisplayField) {
			this.unknownSchemaDisplayField = new Ext.form.DisplayField({
				name: 'unknownSchema'
			});
		}
		return this.unknownSchemaDisplayField;
	},

	/**
	 * The display field for the unexpected.
	 */
	getUnexpectedDisplayField: function() {
		if(!this.unexpectedDisplayField) {
			this.unexpectedDisplayField = new Ext.form.DisplayField({
				name: 'unexpected'
			});
		}
		return this.unexpectedDisplayField;
	},

	/**
	 * The display field for the badFormat.
	 */
	getBadFormatDisplayField: function() {
		if(!this.badFormatDisplayField) {
			this.badFormatDisplayField = new Ext.form.DisplayField({
				name: 'badFormat'
			});
		}
		return this.badFormatDisplayField;
	},

	/**
	 * The display field for the doesNotValidate.
	 */
	getDoesNotValidateDisplayField: function() {
		if(!this.doesNotValidateDisplayField) {
			this.doesNotValidateDisplayField = new Ext.form.DisplayField({
				name: 'doesNotValidate'
			});
		}
		return this.doesNotValidateDisplayField;
	},

	/**
	 * The display field for the ignored.
	 */
	getIgnoredDisplayField: function() {
		if(!this.ignoredDisplayField) {
			this.ignoredDisplayField = new Ext.form.DisplayField({
				name: 'ignored'
			});
		}
		return this.ignoredDisplayField;
	},

	//-- Grid and Store.
    getReportErrorGrid: function() {
		if(!this.reportErrorGrid) {
			var reportErrorStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idProperty: 'urn',
				fields: [
					{name: 'urn'},{name: 'message'}
				]
			});
			
			this.reportErrorGrid = new Ext.grid.GridPanel({
				id: 'reportErrorGrid',
				height: 200,
				width: 700,
				border: true,
				store: reportErrorStore,
				loadMask: true,
				columns: [
				    {id:'urn', header:Openwis.i18n('Metadata.Report.Error.Urn'), dataIndex:'urn', sortable: true, width: 150},
					{id:'error', header:Openwis.i18n('Metadata.Report.Error.Name'), dataIndex:'message', renderer: Openwis.Utils.Tooltip.Display, sortable: true, width: 150}
				],
				autoExpandColumn: 'error'
			});
			
			//Load Data into store.
			this.reportErrorGrid.getStore().loadData(this.lastResult.errors);
		}
		return this.reportErrorGrid;
	},

	/**
	 * The Close action.
	 */
	getCancelAction: function() {
		if(!this.cancelAction) {
			this.cancelAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Close'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.cancelAction;
	},

	addDownloadReportButton: function() {
		// Add download button only when showing report from harvesting/synchro
		if (this.harvestingTaskId) {
			return this.addButton(new Ext.Button(this.getReportAction()));
		}
		return new Ext.Container({
        	border: false,
        	width: 10,
        	html: '',
        	style : {
           	padding: '2px'
        	}
   	 	});;
	},
	
	getReportAction: function() {
        if(!this.reportAction) {
            this.reportAction = new Ext.Action({
            	disabled: false,
				text: Openwis.i18n('Metadata.Report.Download.Last.Report'),
				scope: this,
				handler: function() {
					window.location.href = configOptions.locService +  "/xml.harvesting.last.report.file?id=" + this.harvestingTaskId;
				}
			});
        }
        return this.reportAction;
    }

});