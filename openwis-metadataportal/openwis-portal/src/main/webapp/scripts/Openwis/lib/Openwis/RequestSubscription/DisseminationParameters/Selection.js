

Ext.ns('Openwis.RequestSubscription.DisseminationParameters');

Openwis.RequestSubscription.DisseminationParameters.Selection = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, {
			layout: 'form',
			border: false
		});
		Openwis.RequestSubscription.DisseminationParameters.Selection.superclass.initComponent.apply(this, arguments);
		
		this.addEvents("panelInitialized", "disseminationChanged"); 
		this.isInitialized = false;
	},
	//----------------------------------------------------------------- Main Panel.
	
	getAccordionMainPanel: function() {
	    if(!this.accordionMainPanel) {
	        this.accordionMainPanel = new Ext.Panel({
	            layout: 'accordion',
	            hidden: this.optional,
	            listeners: {
                    afterrender: function() {
                        if(this.config) {
                            this.getAccordionMainPanel().getLayout().setActiveItem(this.getPanelByType(this.config.type));
                        } else {
                            this.getAccordionMainPanel().getLayout().setActiveItem(this.getStagingPostPanel());
                        }
                    },
                    scope: this
	            }
	        });
	    }
	    return this.accordionMainPanel;
	},
	
	getDisseminationInfo: function() {
        if(!this.disseminationInfo) {
        	var identifier = null;
        	var identifierLabel = null;
        	if(this.config.type == 'RMDCN' || this.config.type == 'PUBLIC') {
        		if(this.config.o.diffusion.host) {
        			identifier = this.config.o.diffusion.host;
        			identifierLabel = Openwis.i18n('RequestSubscription.Dissemination.Diffusion.FTP.Title');
        		} else if(this.config.o.diffusion.address) {
        			identifier = this.config.o.diffusion.address;
        			identifierLabel = Openwis.i18n('RequestSubscription.Dissemination.Diffusion.Mail.Title');
        		}
        	}
        	
	        this.disseminationInfo = new Ext.form.FormPanel({
	            border: false,
				labelWidth: 120,
				items: [
				    {xtype: 'displayfield', value: this.config.type, fieldLabel: Openwis.i18n('RequestSubscription.Dissemination.Type')},
				    {xtype: 'displayfield', value: identifier, hidden: identifier, fieldLabel: identifierLabel}
				]
	        });
	    }
	    return this.disseminationInfo;
	},
	
	getSpecifyDissemination: function() {
		if (!this.specifyDissemination) {
			this.specifyDissemination = new Ext.form.Checkbox({
				boxLabel: Openwis.i18n('RequestSubscription.Specify.Dissemination'),
				hideLabel: true,
				handler: function() {
					if (this.specifyDissemination.checked) {
						this.getAccordionMainPanel().show();
					} else {
						this.getAccordionMainPanel().hide();
					}
				},
				scope : this
			});
		}
		return this.specifyDissemination;
	},

	//----------------------------------------------------------------- Initialization of the panels.,
    initializeAndShow: function() {
        if(!this.isInitialized) {
			this.isInitialized = true;
            if(!this.readOnly) {
                this.getInfosAndRefresh(true);
            } else if(this.config) {
                this.add(this.getDisseminationInfo());
            }
            this.doLayout();
        }
    },
	
	getMssFssPanel: function() {
		if(!this.mssFssPanel) {
			this.mssFssPanel = new Openwis.RequestSubscription.DisseminationParameters.Components.MSSFSS({
			    listeners: {
                    activate: function() {
                        this.fireEvent("disseminationChanged", "MSS_FSS");
                        this.mssFssPanel.addClass('dissemination-selected');
                    },
                    collapse: function() {
                        this.mssFssPanel.removeClass('dissemination-selected');
                    },
                    scope: this
	            }
			});
		}
		return this.mssFssPanel;
	},
	
	getRmdcnDiffusionPanel: function() {
		if(!this.rmdcnDiffusionPanel) {
			this.rmdcnDiffusionPanel = new Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion({
				disseminationTool: 'RMDCN',
				operationMode: this.operationMode,
				listeners: {
					processRefresh: function() {
						this.getInfosAndRefresh(false);
					},
                    activate: function() {
                    	this.fireEvent("disseminationChanged", "RMDCN");
                    	this.rmdcnDiffusionPanel.addClass('dissemination-selected');
                    },
                    collapse: function() {
                        this.rmdcnDiffusionPanel.removeClass('dissemination-selected');
                    },
					scope: this
				}
			});
		}
		return this.rmdcnDiffusionPanel;
	},
	
	getPublicDiffusionPanel: function() {
		if(!this.publicDiffusion) {
			this.publicDiffusion = new Openwis.RequestSubscription.DisseminationParameters.Components.Diffusion({
				disseminationTool: 'PUBLIC',
				operationMode: this.operationMode,
				listeners: {
					processRefresh: function() {
						this.getInfosAndRefresh(false);
					},
                    activate: function() {
                        this.fireEvent("disseminationChanged", "PUBLIC");
                        this.publicDiffusion.addClass('dissemination-selected');
                    },
                    collapse: function() {
                        this.publicDiffusion.removeClass('dissemination-selected');
                    },
					scope: this
				}
			});
		}
		return this.publicDiffusion;
	},
	
	getStagingPostPanel: function() {
		if(!this.stagingPostPanel) {
			this.stagingPostPanel = new Openwis.RequestSubscription.DisseminationParameters.Components.StagingPost({
			    listeners: {
                    activate: function() {
                        this.fireEvent("disseminationChanged", "STAGING_POST");
                        this.stagingPostPanel.addClass('dissemination-selected');
                    },
                    collapse: function() {
                        this.stagingPostPanel.removeClass('dissemination-selected');
                    },
                    scope: this
	            }
			});
		}
		return this.stagingPostPanel;
	},
	
	//----------------------------------------------------------------- Generic methods used by the wizard.
	
	getForm: function() {
	    if((!this.optional || (this.optional && this.getSpecifyDissemination().checked)) && this.getAccordionMainPanel().layout.activeItem) {
	        return this.getAccordionMainPanel().layout.activeItem.getForm();
	    } else if(!this.optional && !this.getAccordionMainPanel().layout.activeItem && !this.config) {
	        //Default Panel.
	        return this.getStagingPostPanel().getForm();
	    } else if(this.config) {
	        return this.getPanelByType(this.config.type).getForm();
	    } else {
	        return null;
	    }
	},
	
	buildDissemination: function() {
		var disseminationObject = {};
		if (this.optional && !this.getSpecifyDissemination().checked) {
			return null;
		}
		
		var activePanel = null;
		if(this.getAccordionMainPanel().layout.activeItem) {
		    activePanel = this.getAccordionMainPanel().layout.activeItem;
		} else if(!this.optional && !this.getAccordionMainPanel().layout.activeItem && !this.config) {
	        //Default Panel.
	        activePanel = this.getStagingPostPanel();
	    } else if(this.config) {
		    activePanel = this.getPanelByType(this.config.type);
		}
		if(activePanel) {
    		if(this.getMssFssPanel() == activePanel) {
    			disseminationObject.mssFssDissemination = this.getMssFssPanel().getDisseminationValue();
    		} else if(this.getRmdcnDiffusionPanel() == activePanel) {
    			disseminationObject.rmdcnDiffusion = this.getRmdcnDiffusionPanel().getDisseminationValue();
    		} else if(this.getPublicDiffusionPanel() == activePanel) {
    			disseminationObject.publicDiffusion = this.getPublicDiffusionPanel().getDisseminationValue();
    		} else if(this.getStagingPostPanel() == activePanel) {
    			disseminationObject.shoppingCartDissemination = this.getStagingPostPanel().getDisseminationValue();
    		}
		}
		return disseminationObject;
	},
	
	getInfosAndRefresh: function(isFirst) {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.get.user.dissemination.parameters',
			params: {urn: this.productMetadataUrn, subscription: this.isSubscription},
			listeners: {
				success: function(config) {
					this.refresh(config, isFirst);
				},
				failure: function(config) {
					//Close the wizard
					if (this.ownerCt) {
						this.ownerCt.ownerCt.close();
					}
				},
				scope: this
			}
		});
		getHandler.proceed();
	},
	
	refresh: function(dissParams, isFirst) {
		if(isFirst) {
			if (this.optional) {
				this.add(this.getSpecifyDissemination());
			}
		    this.getAccordionMainPanel().add(this.getMssFssPanel());
			this.getAccordionMainPanel().add(this.getRmdcnDiffusionPanel());
			this.getAccordionMainPanel().add(this.getPublicDiffusionPanel());
			this.getAccordionMainPanel().add(this.getStagingPostPanel());
			
			this.add(new Ext.Panel({
				layout: 'fit',
				height: 480,
				border: false,
				items : [this.getAccordionMainPanel()]
			}));
		}

		this.getMssFssPanel().refresh(dissParams.mssFss);
		this.getRmdcnDiffusionPanel().refresh(dissParams.rmdcnDiffusion);
		this.getPublicDiffusionPanel().refresh(dissParams.publicDiffusion);
		
		if(this.config) {
		    this.getSpecifyDissemination().setValue(true);
		    this.getAccordionMainPanel().show();
		    this.doLayout();
		    
		    //Hide MSS/FSS if setted dissemination is not MSS/FSS
		    this.setDisseminationVisible('MSS_FSS', this.config.type == 'MSS_FSS');
		    
		    //Set the config options.
		    this.getPanelByType(this.config.type).initializeFields(this.config.o);
		}
		
		if(isFirst) {
			this.fireEvent("panelInitialized");
		}
	},
	
	setDisseminationVisible: function(type, isVisible) {
        this.getPanelByType(type).setVisible(isVisible);
	},
	
	getPanelByType: function(type) {
	    if(type == 'MSS_FSS') {
	        return this.getMssFssPanel();
	    } else if(type == 'RMDCN') {
	        return this.getRmdcnDiffusionPanel();
	    } else if(type == 'PUBLIC') {
	        return this.getPublicDiffusionPanel();
	    } else if(type == 'STAGING_POST') {
	        return this.getStagingPostPanel();
	    }
	}
});