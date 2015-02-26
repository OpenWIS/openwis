

Ext.ns('Openwis.RequestSubscription');

Openwis.RequestSubscription.Wizard = Ext.extend(Ext.Window, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
			width:620,
			height:620,
			closeAction:'close',
			closable: false,
			autoScroll: true,
			resizable: true,
			plain: true,
			layout: 'fit'
		});
		Openwis.RequestSubscription.Wizard.superclass.initComponent.apply(this, arguments);
	},
	
	//----------------------------------------------------------------- Initialization.
	
	initialize: function(productMetadataUrn, typeRequest, isGlobal, operationMode, requestID, isMssFss, backupRequestId, backupDeployment) {
		
		//Intialization from context.
		this.productMetadataUrn = productMetadataUrn;
		this.isSubscription = (typeRequest == 'SUBSCRIPTION');
		this.isGlobal = isGlobal;
		this.operationMode = operationMode;
		this.requestID = requestID;
		this.isMssFss = isMssFss;
		this.backupRequestId = backupRequestId;
		this.backupDeployment = backupDeployment;
		this.resetTitle();
		
		//ReadOnly flag.
		this.readOnly = (operationMode == 'View' && typeRequest == 'ADHOC');
		
		if(operationMode != 'Create') {
		    //Operation mode is View / Edit.
		    var url = configOptions.locService;
		    if(this.isMssFss) {
		        url += '/xml.mssfss.subscription.get';
		    } else {
		        if(typeRequest == 'ADHOC') {
		            url += '/xml.adhoc.get';
		        } else {
		            url += '/xml.subscription.get';
		        }
		    }
		    
		    new Openwis.Handler.Get({
    			url: url ,
    			params: {
    			    id: this.requestID
    			},
    			listeners: {
    				success: function(config) {
    				    this.config = config;
    				    this.config.isSubscription = this.isSubscription;
    					this.refresh();
    				},
    				failure: function() {
    					this.close();
    				},
    				scope: this
    			}
    		}).proceed();
		} else {
		    this.refresh();
		}
	},
	
	refresh: function() {
	    //Depending on the operationmode, initialize the window.
		this.initializeTabs();
		
		//Adding Tab Panel.
		this.add(this.getMainTabPanel());
		
		
		//Add Next-Previous Button
		
		this.addButton(new Ext.Button(this.getPreviousAction()));
		
		this.addButton(new Ext.Button(this.getNextAction()));
		
		//Add buttons.
		if(!this.readOnly) {
		    this.addButton(new Ext.Button(this.getSaveAction()));
		}
		this.addButton(new Ext.Button(this.getCloseWindowAction()));
		
		// set Y before showing, as center seems not to work in some cases (pb of viewport size/position?)
		this.y = 20;
		this.show();
	},
	
	//----------------------------------------------------------------- Panel references.
	
	getMainTabPanel: function() {
	    if(!this.mainTabPanel) {
	        this.mainTabPanel = new Ext.TabPanel({
	            border: false,
			    autoScroll: true,
	            listeners: {
	                tabchange: function(tabPanel, tab) {
	                    this.activateActions();
	                    tab.initializeAndShow();
	                },
	                scope: this
	            }   
	        });
	    }
	    return this.mainTabPanel;
	},
	
	initializeTabs: function() {
    	this.getMainTabPanel().add(this.getSubSelectionParametersPanel());
    	this.getMainTabPanel().add(this.getPrimaryDisseminationPanel());
    	this.getMainTabPanel().add(this.getSecondaryDisseminationPanel());
    	
    	// Avoid javascript init issues when initializing ssp panel on local product during edition
    	// The panel will be initialized on tab selection
    	if(this.operationMode == 'Create' || this.isGlobal) {
    		this.getSubSelectionParametersPanel().initializeAndShow();
    	}
    	
    	this.getPrimaryDisseminationPanel().initializeAndShow();
	    this.getSecondaryDisseminationPanel().initializeAndShow();
	    
    	var indexOfSummaryTab = 3;
    	if(this.isSubscription && !this.backupRequestId && this.isGlobal) {
    	    this.getMainTabPanel().add(this.getSelectBackupPanel());
    	    this.getSelectBackupPanel().initializeAndShow();
    	    indexOfSummaryTab = 4;
    	}
	    this.getMainTabPanel().add(this.getSummaryPanel());
	    
	   
    	
    	if(this.operationMode == 'Create') {
    	    this.getSummaryPanel().disable();
    	    this.getSelectBackupPanel().disable();
    	    this.getMainTabPanel().setActiveTab(0);
    	} else {
    		this.getSelectBackupPanel().enable();
    		this.getMainTabPanel().setActiveTab(indexOfSummaryTab);
    	}
	},
	
	//----------------------------------------------------------------- Panel references.
	
	getSummaryPanel: function() {
		if(!this.summaryPanel) {
		    if(this.isMssFss) {
		        this.summaryPanel = new Openwis.RequestSubscription.MSSFSSSummary({
    		        config: this.config,
    		        title: Openwis.i18n('RequestSubscription.Summary.Step')
    			});
		    } else {
		        this.summaryPanel = new Openwis.RequestSubscription.Summary({
    		        config: this.config,
    		        title: Openwis.i18n('RequestSubscription.Summary.Step')
    			});
		    }
		}
		return this.summaryPanel;
	},

	getSubSelectionParametersPanel: function() {
		if(!this.subSelectionParametersPanel) {
    	    if(this.isGlobal) {
    			this.subSelectionParametersPanel = new Openwis.RequestSubscription.SubSelectionParameters.SSPGlobalProduct({
    			    title: Openwis.i18n('RequestSubscription.SSP.Step'),
    				productMetadataUrn: this.productMetadataUrn,
    				isSubscription: this.isSubscription,
    				ssp: this.config ? this.config.ssp : null,
    				frequency: this.config ? this.config.frequency : null,
    				readOnly: this.readOnly,
        			listeners: {
        				panelInitialized: function() {
        					this.doLayout();
        				},
        				scope: this
        			}
    			});
    		} else {
    			this.subSelectionParametersPanel = new Openwis.RequestSubscription.SubSelectionParameters.SSPStandardProduct({
    			    title: Openwis.i18n('RequestSubscription.SSP.Step'),
    				productMetadataUrn: this.productMetadataUrn,
    				isSubscription: this.isSubscription,
    				readOnly: this.readOnly,
    				ssp: this.config ? this.config.ssp : null,
    				frequency: this.config ? this.config.frequency : null,
        			listeners: {
        				panelInitialized: function() {
        					this.doLayout();
        				},
        				scope: this
        			}
    			});
    		}
		}
		return this.subSelectionParametersPanel;
	},
	
	getPrimaryDisseminationPanel: function() {
		if(!this.primaryDisseminationPanel) {
			this.primaryDisseminationPanel = new Openwis.RequestSubscription.DisseminationParameters.Selection({
			    title: Openwis.i18n('RequestSubscription.Dissemination.Step1'),
				productMetadataUrn: this.productMetadataUrn,
    			isSubscription: this.isSubscription,
				config: this.config ? this.config.primaryDissemination : null,
				optional: false,
    			readOnly: this.readOnly,
    			operationMode: this.operationMode,
				listeners: {
					panelInitialized: function() {
					    if(this.isMssFss) {
					        this.getPrimaryDisseminationPanel().setDisseminationVisible('RMDCN', false);
					        this.getPrimaryDisseminationPanel().setDisseminationVisible('PUBLIC', false);
					        this.getPrimaryDisseminationPanel().setDisseminationVisible('STAGING_POST', false);
					    }
						this.doLayout();
					},
					disseminationChanged: function(type) {
					    if(type == 'MSS_FSS') {
					        this.getSecondaryDisseminationPanel().disable();
					    } else {
					        this.getSecondaryDisseminationPanel().enable();
					    }
					},
					scope: this
				}
			});
		}
		return this.primaryDisseminationPanel;
	},
	
	getSecondaryDisseminationPanel: function() {
		if(!this.secondaryDisseminationPanel) {
			this.secondaryDisseminationPanel = new Openwis.RequestSubscription.DisseminationParameters.Selection({
			    title: Openwis.i18n('RequestSubscription.Dissemination.Step2'),
				productMetadataUrn: this.productMetadataUrn,
    			readOnly: this.readOnly,
    			optional: true,
    			isSubscription: this.isSubscription,
				config: this.config ? this.config.secondaryDissemination : null,
				disabled: this.isMssFss,
				operationMode: this.operationMode,
				listeners: {
					panelInitialized: function() {
					    this.getSecondaryDisseminationPanel().setDisseminationVisible('MSS_FSS', false);
						this.doLayout();
					},
					scope: this
				}
			});
		}
		return this.secondaryDisseminationPanel;
	},
	
	getSelectBackupPanel: function() {
		if(!this.selectBackupPanel) {
			this.selectBackupPanel = new Openwis.RequestSubscription.BackUp.BackupSelection({
				config: this.config,
				title: Openwis.i18n('RequestSubscription.Backup.Step')
			});
		}
		return this.selectBackupPanel;
	},

	//-------- Methods to interact with server.
	
	getRequest: function() {
		var request = {};
		request.requestID = this.requestID;
		request.productMetadataURN = this.productMetadataUrn;
		request.subscription = this.isSubscription;
		request.extractMode = this.isGlobal ? 'GLOBAL' : 'NOT_IN_LOCAL_CACHE';
		if (!this.getSubSelectionParametersPanel().isInitialized) {
			request.parameters = this.createParametersForNotInitialized();
			request.frequency = this.config.frequency;
		} else {
			request.parameters = this.getSubSelectionParametersPanel().buildSSPs();
			request.frequency = this.getSubSelectionParametersPanel().buildFrequency();
		}
		request.primaryDissemination = this.getPrimaryDisseminationPanel().buildDissemination();
		request.secondaryDissemination = this.secondaryDisseminationPanel ? this.getSecondaryDisseminationPanel().buildDissemination() : null;
		request.backupRequestId = this.backupRequestId;
		request.backupDeployment = this.backupDeployment;
		return request;
	},
	
	createParametersForNotInitialized: function() {
		var ssps = [];
		Ext.each(this.config.ssp, 
		    function(item, index, allItems) {
				var ssp = {};
            	ssp.code = item.code;
            	    
            	var valueArray = [];
        	    Ext.each(item.value, 
        	    	function(item, index, allItems) {
        	    		valueArray.push(item);
                   	},
                   	this
        	    );
        	    
        	    ssp.values = valueArray;
    			ssps.push(ssp);	
		   	},
		    this
		);
		
		return ssps;
	},
	
	//----------------------------------------------------------------- Managing the panels in the wizard.
	
	activateActions: function() {
	    var currentPanel = this.getCurrentPanel();
		if(currentPanel == this.getSummaryPanel()) {
			this.getCloseWindowAction().setText(Openwis.i18n('Common.Btn.Close'));
			this.getNextAction().disable();
			this.getPreviousAction().enable();
		} else if(currentPanel == this.getSubSelectionParametersPanel()) {
			this.getCloseWindowAction().setText(Openwis.i18n('Common.Btn.Cancel'));
			this.getNextAction().enable();
			this.getPreviousAction().disable();
		} else if(currentPanel == this.getPrimaryDisseminationPanel()) {
			this.getCloseWindowAction().setText(Openwis.i18n('Common.Btn.Cancel'));
			this.getNextAction().enable();
			this.getPreviousAction().enable();
		} else if(currentPanel == this.getSecondaryDisseminationPanel()) {
			this.getCloseWindowAction().setText(Openwis.i18n('Common.Btn.Cancel'));
			if (this.getSummaryPanel().disabled) {
				this.getNextAction().disable();
			} else {
				this.getNextAction().enable();
			}
			this.getPreviousAction().enable();
		} else if(currentPanel == this.getSelectBackupPanel()) {
			this.getCloseWindowAction().setText(Openwis.i18n('Common.Btn.Close'));
			this.getNextAction().enable();
			this.getPreviousAction().enable();
		}
	},
	
	getCurrentPanel: function() {
		return this.getMainTabPanel().getActiveTab();
	},
	
	//----------------------------------------------------------------- Actions.
	
	getSaveAction: function() {
		if(!this.saveAction) {
			this.saveAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Save'),
				scope: this,
				handler: function() {
		        	var task = new Ext.util.DelayedTask(this.performSave, this);
		        	task.delay(300);
				}
			});
		}
		return this.saveAction;
	},
	
	performSave: function() {
		var violations = [];
	    
	    var sspFormPanel = this.getSubSelectionParametersPanel();
	    if(!sspFormPanel.getForm() || !sspFormPanel.getForm().isValid()) {
	        violations.push(Openwis.i18n('RequestSubscription.Validation.SSP'));
	    }
	    
	    var primaryDissFormPanel = this.getPrimaryDisseminationPanel();
	    if(!primaryDissFormPanel.getForm() || !primaryDissFormPanel.getForm().isValid()) {
	        violations.push(Openwis.i18n('RequestSubscription.Validation.PrimaryDissemination'));
	    }
	    
	    var secondaryDissFormPanel = this.getSecondaryDisseminationPanel();
	    if(secondaryDissFormPanel.getForm() && !secondaryDissFormPanel.getForm().isValid()) {
	        violations.push(Openwis.i18n('RequestSubscription.Validation.SecondaryDissemination'));
	    }
	    
	    if(Ext.isEmpty(violations)) {
	        var url = configOptions.locService;
	        if(this.operationMode == 'Create') {
	            url += '/xml.create.request.subscription'
	        } else {
	            url += '/xml.update.request.subscription'
	        }
	    
	        new Openwis.Handler.Save({
				url: url,
				params: this.getRequest(),
				listeners: {
					success: function(config) {
						var wizard = new Openwis.RequestSubscription.Wizard();
						if (!this.backupRequestId) {
							wizard.initialize(this.productMetadataUrn, this.isSubscription ? 'SUBSCRIPTION' : 'ADHOC', this.isGlobal, 'View', config.requestID, 
									this.getRequest().primaryDissemination.mssFssDissemination != null);
						} else {
							wizard.initialize(this.productMetadataUrn, this.isSubscription ? 'SUBSCRIPTION' : 'ADHOC', this.isGlobal, 'View', config.requestID, 
									this.getRequest().primaryDissemination.mssFssDissemination != null, this.backupRequestId, this.backupDeployment);
						}
        				this.close();
					},
					scope: this
				}
			}).proceed();
	    } else {
	        var msg = Openwis.i18n('RequestSubscription.Validation.Failed') + '<br/>';
	        Ext.each(violations, function(item, index) {
	            msg += (index + 1) + ". " + item + "<br/>";
	        }, this);
	        Openwis.Utils.MessageBox.displayErrorMsg(msg);
	    }
	},
	
	getCloseWindowAction: function() {
		if(!this.closeWindowAction) {
			this.closeWindowAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Cancel'),
				scope: this,
				handler: function() {
					this.close();
				}
			});
		}
		return this.closeWindowAction;
	},
	
	getNextAction: function() {
		if(!this.nextAction) {
			this.nextAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Next'),
				scope: this,
				handler: function() {
				    var currentPanel = this.getCurrentPanel();	
				    
					if(currentPanel == this.getSubSelectionParametersPanel()) {
						this.getMainTabPanel().setActiveTab(this.getPrimaryDisseminationPanel());
					} else if(currentPanel == this.getPrimaryDisseminationPanel()) {
						this.getMainTabPanel().setActiveTab(this.getSecondaryDisseminationPanel());
					} else if(currentPanel == this.getSecondaryDisseminationPanel()) {
						this.getMainTabPanel().setActiveTab(this.getSummaryPanel());
					}
				}
			});
		}
		return this.nextAction;
	},
	
	getPreviousAction: function() {
		if(!this.previousAction) {
			this.previousAction = new Ext.Action({
				text: Openwis.i18n('Common.Btn.Previous'),
				scope: this,
				handler: function() {
				    var currentPanel = this.getCurrentPanel();	

				    if(currentPanel == this.getPrimaryDisseminationPanel()) {
						this.getMainTabPanel().setActiveTab(this.getSubSelectionParametersPanel());
					} else if(currentPanel == this.getSecondaryDisseminationPanel()) {
						this.getMainTabPanel().setActiveTab(this.getPrimaryDisseminationPanel());
					} else if(currentPanel == this.getSummaryPanel()) {
						this.getMainTabPanel().setActiveTab(this.getSecondaryDisseminationPanel());
					}
				}
			});
		}
		return this.previousAction;
	},
	
	resetTitle: function() {
	    if(this.requestID) {
		    if(this.isSubscription) {
		        this.setTitle(Openwis.i18n('RequestSubscription.ViewEdit.Subscription', {requestID: this.requestID}));
		    } else {
		        this.setTitle(Openwis.i18n('RequestSubscription.View.Request', {requestID: this.requestID}));
		    }
		} else {
		    if(this.isGlobal) {
		        if(this.isSubscription) {
		            this.setTitle(Openwis.i18n('RequestSubscription.Create.Subscription.Cache'));
    		    } else {
		            this.setTitle(Openwis.i18n('RequestSubscription.Create.Request.Cache'));
    		    }
		    } else {
    		    if(this.isSubscription) {
		            this.setTitle(Openwis.i18n('RequestSubscription.Create.Subscription'));
    		    } else {
		            this.setTitle(Openwis.i18n('RequestSubscription.Create.Request'));
    		    }
		    }
		}
	
	}
	
	//---------
});