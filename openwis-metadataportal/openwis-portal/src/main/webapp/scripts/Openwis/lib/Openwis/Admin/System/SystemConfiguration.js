Ext.ns('Openwis.Admin.System.SystemConfiguration');

Openwis.Admin.System.SystemConfiguration = Ext.extend(Ext.Container, {

	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Admin.System.SystemConfiguration.superclass.initComponent.apply(this, arguments);
		
		this.getInfosAndInitialize();
	},

    getInfosAndInitialize: function() {
		var getHandler = new Openwis.Handler.Get({
			url: configOptions.locService+ '/xml.system.configuration.form',
			params: {},
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

    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //Create System Config form.
//        this.getSystemConfigurationFormPanel().add(this.getSiteFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getServerFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getIndexFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getZ39ServerFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getXlinkResolverFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getCswFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getInspireFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getProxyFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getFeedBackFieldSet());
		this.getSystemConfigurationFormPanel().add(this.getAuthenticationFieldSet());

		this.add(this.getSystemConfigurationFormPanel());
		
		this.doLayout();
		
		this.fireEvent("panelInitialized");
    },

    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('SystemConfiguration.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },

    /**
	 *	The form panel.
	 */
	getSystemConfigurationFormPanel: function() {
		if(!this.systemConfigurationFormPanel) {
			this.systemConfigurationFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				border: false,
				buttons:
				[
					{
						text: Openwis.i18n('Common.Btn.Save'),
						handler: function(btn, e) {
							if(this.getSystemConfigurationFormPanel().getForm().isValid()) {
								var saveHandler = new Openwis.Handler.Save({
									url: configOptions.locService+ '/xml.system.configuration',
									params: this.getSystemConfigInfos(),
									listeners: {
										success: function(config) {
											//console.log("config update");
										},
										scope: this
									}
								});
								saveHandler.proceed();
							}
						},
						scope: this
					}
				]
			});
		}
		return this.systemConfigurationFormPanel;
	},

    getSiteFieldSet: function() {
        if(!this.siteFieldSet) {
            this.siteFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Site.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.siteFieldSet.add(this.getSiteNameTextField());
        }
        return this.siteFieldSet;
    },

    getSiteNameTextField: function() {
        if(!this.siteNameTextField) {
            this.siteNameTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Site.Name'),
                allowBlank: false,
                border: false,
                disabled: true,
                width: 220,
                value: this.config.siteName,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.siteNameTextField;
	},

    getServerFieldSet: function() {
        if(!this.serverFieldSet) {
            this.serverFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Server.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.serverFieldSet.add(this.getServerHostTextField());
            this.serverFieldSet.add(this.getServerPortTextField());
        }
        return this.serverFieldSet;
    },

    getServerHostTextField: function() {
        if(!this.serverHostTextField) {
            this.serverHostTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Server.Host.Name'),
                allowBlank: false,
                border: false,
                width: 220,
                value: this.config.serverHost,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.serverHostTextField;
	},

    getServerPortTextField: function() {
        if(!this.serverPortTextField) {
            this.serverPortTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Server.Port.Name'),
                border: false,
                width: 220,
                value: this.config.serverPort,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.serverPortTextField;
	},

    getIndexFieldSet: function() {
        if(!this.indexFieldSet) {
            this.indexFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Index.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.indexFieldSet.add(this.getIndexEnableCheckBox());
            this.indexFieldSet.add(this.getIndexRunAtFormPanel());
            this.indexFieldSet.add(this.getIndexRunAgainCombobox());
            if (!this.config.indexEnable)
        	{
            	this.getIndexRunAtFormPanel().hide();
			    this.getIndexRunAgainCombobox().hide();
        	}
        }
        return this.indexFieldSet;
    },

    getIndexEnableCheckBox: function() {
		if(!this.indexEnableCheckBox) {
			this.indexEnableCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Index.Enable'),
				allowBlank: false,
				checked: this.config.indexEnable,
				width: 125,
				listeners : {
    				check: function(checkbox, checked) {
    				    if(!checked) {
    					    this.getIndexRunAtFormPanel().hide();
    					    this.getIndexRunAgainCombobox().hide();
    				    } else {
    					    this.getIndexRunAtFormPanel().show();
    					    this.getIndexRunAgainCombobox().show();
    				    }
    				},
    				scope: this
    			}
			});
		}
		return this.indexEnableCheckBox;
	},

    getIndexRunAtFormPanel: function() {
		if(!this.indexRunAtFormPanel) {
			this.indexRunAtFormPanel = new Ext.Panel({
				fieldLabel: Openwis.i18n('SystemConfiguration.Index.RunAt'),
				layout:'table',
                defaults: {
                   style: {
                       width: '100%'
                   }
                },
                layoutConfig: {
                    columns: 4
                },
				border: false
			});

            // Fill the table layout panel
			this.indexRunAtFormPanel.add(this.getIndexRunAtHourComboBox());
            this.indexRunAtFormPanel.add(new Ext.form.Label({
				text: ' : '
			}));
            this.indexRunAtFormPanel.add(this.getIndexRunAtMinuteComboBox());
            this.indexRunAtFormPanel.add(this.getIndexRunAtHelpLabel());
		}
		return this.indexRunAtFormPanel;
	},

    getIndexRunAtHourComboBox: function() {	
		if(!this.indexRunAtHourComboBox) {
			this.indexRunAtHourComboBox = new Ext.form.ComboBox({
			    store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['0', 0], ['1', 1], ['2', 2], ['3', 3], ['4', 4], ['5', 5], ['6', 6], ['7', 7], ['8', 8],
					    ['9', 9], ['10', 10], ['11', 11], ['12', 12], ['13', 13], ['14', 14], ['15', 15], ['16', 16],
					    ['17', 17], ['18', 18], ['19', 19], ['20', 20], ['21', 21], ['22', 22], ['23', 23]
					]
				}),
				style: {
                    margin: '0px 0px 5px 0px'
                },
                allowBlank: false,
				name: 'runAtHour',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				editable: false,
				allowBlank: false,
				width: 40,
				displayField: 'value',
				valueField: 'id',
				value: this.config.indexRunAtHour
			});
		}
		
		return this.indexRunAtHourComboBox;
	},

    getIndexRunAtMinuteComboBox: function() {	
		if(!this.indexRunAtMinuteComboBox) {
			this.indexRunAtMinuteComboBox = new Ext.form.ComboBox({
			    store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['0', 0], ['15', 15], ['30', 30], ['45', 45]
					]
				}),
				allowBlank: false,
				name: 'runAtMinute',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				editable: false,
				allowBlank: false,
				width: 40,
				displayField: 'value',
				valueField: 'id',
				value: this.config.indexRunAtMinute
			});
		}
		
		return this.indexRunAtMinuteComboBox;
	},

    getIndexRunAtHelpLabel: function() {
		if(!this.indexRunAtHelpLabel) {
			this.indexRunAtHelpLabel = new Ext.form.Label({
				text: Openwis.i18n('SystemConfiguration.Index.RunAt.help'),
				labelStyle: 'font-weight:bold;'
			});
		}
		return this.indexRunAtHelpLabel;
	},
			
    getIndexRunAgainCombobox: function() {	
		if(!this.indexRunAgainCombobox) {
			this.indexRunAgainCombobox = new Ext.form.ComboBox({
			    store: new Ext.data.ArrayStore ({
					id: 0,
					fields: ['id', 'value'],
					data: [
					    ['1', '1 hour (not recommended)'], ['3', '3 hours'], ['6', '6 hours'], ['12', '12 hours'],
					    ['24', '1 day'], ['48', '2 days'], ['72', '3 days'], ['96', '4 days']
					]
				}),
				allowBlank: false,
				fieldLabel: Openwis.i18n('SystemConfiguration.Index.RunAgain'),
				name: 'runAgain',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				editable: false,
				allowBlank: false,
				width: 200,
				displayField: 'value',
				valueField: 'id',
				value: this.config.indexRunAgain
			});
		}
		
		return this.indexRunAgainCombobox;
	},
	
    getZ39ServerFieldSet: function() {
        if(!this.z39ServerFieldSet) {
            this.z39ServerFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Z39Server.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.z39ServerFieldSet.add(this.getZ39ServerEnableCheckBox());
            this.z39ServerFieldSet.add(this.getZ39ServerPortTextField());
            if (!this.config.z3950ServerEnable)
        	{
            	this.getZ39ServerPortTextField().hide();
        	}
        }
        return this.z39ServerFieldSet;
    },

    getZ39ServerEnableCheckBox: function() {
		if(!this.z39ServerEnableCheckBox) {
			this.z39ServerEnableCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Z39Server.Enable'),
				allowBlank: false,
				checked: this.config.z3950ServerEnable,
				width: 125,
				listeners : {
    				check: function(checkbox, checked) {
    				    if(!checked) {
    					    this.getZ39ServerPortTextField().hide();
    				    } else {
    					    this.getZ39ServerPortTextField().show();
    				    }
    				},
    				scope: this
    			}
			});
		}
		return this.z39ServerEnableCheckBox;
	},

    getZ39ServerPortTextField: function() {
        if(!this.z39ServerPortTextField) {
            this.z39ServerPortTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Z39Server.Port'),
                border: false,
                width: 220,
                value: this.config.z3950ServerPort,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.z39ServerPortTextField;
	},

    getXlinkResolverFieldSet: function() {
        if(!this.xlinkResolverFieldSet) {
            this.xlinkResolverFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Xlink.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.xlinkResolverFieldSet.add(this.getXlinkResolverEnableCheckBox());
        }
        return this.xlinkResolverFieldSet;
    },

    getXlinkResolverEnableCheckBox: function() {
		if(!this.xlinkResolverEnableCheckBox) {
			this.xlinkResolverEnableCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Xlink.Enable'),
				allowBlank: false,
				checked: this.config.xlinkResolverEnable,
				width: 125
			});
		}
		return this.xlinkResolverEnableCheckBox;
	},

    getCswFieldSet: function() {
        if(!this.cswFieldSet) {
            this.cswFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Csw.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.cswFieldSet.add(this.getCswEnableCheckBox());
            //this.cswFieldSet.add(this.getCswContactCombobox());
            this.cswFieldSet.add(this.getCswTitleFormTextField());
            this.cswFieldSet.add(this.getCswAbstractTextField());
            this.cswFieldSet.add(this.getCswFeesTextField());
            this.cswFieldSet.add(this.getCswAccessTextField());
        }
        return this.cswFieldSet;
    },

    getCswEnableCheckBox: function() {
		if(!this.cswEnableCheckBox) {
			this.cswEnableCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Csw.Enable'),
				allowBlank: false,
				checked: this.config.cswEnable,
				width: 125
			});
		}
		return this.cswEnableCheckBox;
	},

    getCswContactCombobox: function() {	
		if(!this.cswContactComboBox) {
			var cswContactStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				idProperty: 'id',
				fields: [
					{name: 'id'},{name: 'name'}
				]
			});
		
			this.cswContactComboBox = new Ext.form.ComboBox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Csw.Contact'),
				name: 'cswContact',
				mode: 'local',
				typeAhead: true,
				triggerAction: 'all',
				selectOnFocus:true,
				store: cswContactStore,
				editable: false,
				allowBlank: false,
				width: 330,
				displayField: 'name',
				valueField: 'id'
			});
			
			//Load Data into store.
			this.cswContactComboBox.getStore().loadData(this.config.cswContactAllUsers);
		}
		
		return this.categoriesComboBox;
	},

    getCswTitleFormTextField: function() {
        if(!this.cswTitleFormTextField) {
            this.cswTitleFormTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Csw.TitleForm'),
                border: false,
                width: 220,
                value: this.config.cswTitle,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.cswTitleFormTextField;
	},
	
	getCswAbstractTextField: function() {
	    if(!this.cswAbstractTextField) {
            this.cswAbstractTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Csw.Abstract'),
                border: false,
                width: 220,
                value: this.config.cswAbstract,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.cswAbstractTextField;
	},
	
	getCswFeesTextField: function() {
	    if(!this.cswFeesTextField) {
            this.cswFeesTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Csw.Fees'),
                border: false,
                width: 220,
                value: this.config.cswFees,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.cswFeesTextField;
	},
	
	getCswAccessTextField: function() {
	    if(!this.cswAccessTextField) {
            this.cswAccessTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Csw.Access'),
                border: false,
                width: 220,
                value: this.config.cswAccess,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.cswAccessTextField;
	},
	
	getInspireFieldSet: function() {
	    if(!this.inspireFieldSet) {
            this.inspireFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Inspire.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.inspireFieldSet.add(this.getInspireEnableCheckBox());
        }
        return this.inspireFieldSet;
    },

    getInspireEnableCheckBox: function() {
		if(!this.inspireEnableCheckBox) {
			this.inspireEnableCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Inspire.Enable'),
				allowBlank: false,
				checked: this.config.inspireEnable,
				width: 125
			});
		}
		return this.inspireEnableCheckBox;
	},

    getProxyFieldSet: function() {
        if(!this.proxyFieldSet) {
            this.proxyFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Proxy.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.proxyFieldSet.add(this.getProxyUseCheckBox());
            this.proxyFieldSet.add(this.getProxyHostTextField());
            this.proxyFieldSet.add(this.getProxyPortTextField());
            this.proxyFieldSet.add(this.getProxyUserNameTextField());
            this.proxyFieldSet.add(this.getProxyPasswordTextField());
            if (!this.config.proxyUse)
        	{
            	this.getProxyHostTextField().hide();
			    this.getProxyPortTextField().hide();
			    this.getProxyUserNameTextField().hide();
			    this.getProxyPasswordTextField().hide();
        	}
        }
        return this.proxyFieldSet;
    },

    getProxyUseCheckBox: function() {
		if(!this.proxyUseCheckBox) {
			this.proxyUseCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Proxy.Use'),
				allowBlank: false,
				checked: this.config.proxyUse,
				width: 125,
				listeners : {
    				check: function(checkbox, checked) {
    				    if(!checked) {
    					    this.getProxyHostTextField().hide();
    					    this.getProxyPortTextField().hide();
    					    this.getProxyUserNameTextField().hide();
    					    this.getProxyPasswordTextField().hide();
    				    } else {
    					    this.getProxyHostTextField().show();
    					    this.getProxyPortTextField().show();
    					    this.getProxyUserNameTextField().show();
    					    this.getProxyPasswordTextField().show();
    				    }
    				},
    				scope: this
    			}
			});
		}
		return this.proxyUseCheckBox;
	},

	getProxyHostTextField: function() {
        if(!this.proxyHostTextField) {
            this.proxyHostTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Proxy.Host'),
                border: false,
                width: 220,
                value: this.config.proxyHost,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.proxyHostTextField;
	},

    getProxyPortTextField: function() {
        if(!this.proxyPortTextField) {
            this.proxyPortTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Proxy.Port'),
                border: false,
                width: 220,
                value: this.config.proxyPort,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.proxyPortTextField;
	},

    getProxyUserNameTextField: function() {
        if(!this.proxyUserNameTextField) {
            this.proxyUserNameTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Proxy.UserName'),
                border: false,
                width: 220,
                value: this.config.proxyUserName,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.proxyUserNameTextField;
	},

    getProxyPasswordTextField: function() {
        if(!this.proxyPasswordTextField) {
            this.proxyPasswordTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Proxy.Password'),
                border: false,
                width: 220,
                value: this.config.proxyPassword,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.proxyPasswordTextField;
	},

	getFeedBackFieldSet: function() {
	    if(!this.feedBackFieldSet) {
            this.feedBackFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Feedback.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.feedBackFieldSet.add(this.getFeedBackEmailTextField());
            this.feedBackFieldSet.add(this.getFeedBackSmtpHostTextField());
            this.feedBackFieldSet.add(this.getFeedBackSmtpPortTextField());
        }
        return this.feedBackFieldSet;
    },
	
	getFeedBackEmailTextField: function() {
	    if(!this.feedBackEmailTextField) {
            this.feedBackEmailTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Feedback.mail'),
                border: false,
                width: 220,
                value: this.config.feedBackEmail,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.feedBackEmailTextField;
	},
	
	getFeedBackSmtpHostTextField: function() {
	    if(!this.feedBackSmtpHostTextField) {
            this.feedBackSmtpHostTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Feedback.SMTP.Host'),
                border: false,
                width: 220,
                value: this.config.feedBackSmtpHost,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.feedBackSmtpHostTextField;
	},
	
	getFeedBackSmtpPortTextField: function() {
	    if(!this.feedBackSmtpPortTextField) {
            this.feedBackSmtpPortTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('SystemConfiguration.Feedback.SMTP.Port'),
                border: false,
                width: 220,
                value: this.config.feedBackSmtpPort,
                style: {
                   margin: '0px 0px 5px 0px'
                }
            });
        }
	    return this.feedBackSmtpPortTextField;
	},

	getAuthenticationFieldSet: function() {
	    if(!this.authenticationFieldSet) {
            this.authenticationFieldSet = new Ext.form.FieldSet({
                title: Openwis.i18n('SystemConfiguration.Authentication.Title'),
    			autoHeight:true,
    			collapsed: false,
    			collapsible: true
            });
            this.authenticationFieldSet.add(this.getUserSelfRegistrationEnableCheckBox());
        }
        return this.authenticationFieldSet;
    },

    getUserSelfRegistrationEnableCheckBox: function() {
		if(!this.userSelfRegistrationEnableCheckBox) {
			this.userSelfRegistrationEnableCheckBox = new Ext.form.Checkbox({
				fieldLabel: Openwis.i18n('SystemConfiguration.Authentication.UserSelfRegistration.Enable'),
				allowBlank: false,
				checked: this.config.userSelfRegistrationEnable,
				width: 125
			});
		}
		return this.userSelfRegistrationEnableCheckBox;
	},

    /**
	 *	The JSON object submitted to the server.
	 */
	getSystemConfigInfos: function() {
		var systemConfigInfos = {};
		// SITE
		systemConfigInfos.siteName = this.getSiteNameTextField().getValue();
		// SERVER
		systemConfigInfos.serverHost = this.getServerHostTextField().getValue();
		systemConfigInfos.serverPort = this.getServerPortTextField().getValue();
		// INDEX OPTIMIZER
		systemConfigInfos.indexEnable = this.getIndexEnableCheckBox().getValue();
		systemConfigInfos.indexRunAtHour = this.getIndexRunAtHourComboBox().getValue();
		systemConfigInfos.indexRunAtMinute = this.getIndexRunAtMinuteComboBox().getValue();
		systemConfigInfos.indexRunAgain = this.getIndexRunAgainCombobox().getValue();
		// Z39.50 SERVER
		systemConfigInfos.z3950ServerEnable = this.getZ39ServerEnableCheckBox().getValue();
		systemConfigInfos.z3950ServerPort = this.getZ39ServerPortTextField().getValue();
		// XLINK RESOLVER
		systemConfigInfos.xlinkResolverEnable = this.getXlinkResolverEnableCheckBox().getValue();
		// CSW ISO PROFILE
		systemConfigInfos.cswEnable = this.getCswEnableCheckBox().getValue();
		// systemConfigInfos.cswContactId = this.getXlinkResolverEnableCheckBox().getValue();
		systemConfigInfos.cswTitle = this.getCswTitleFormTextField().getValue();
		systemConfigInfos.cswAbstract = this.getCswAbstractTextField().getValue();
		systemConfigInfos.cswFees = this.getCswFeesTextField().getValue();
		systemConfigInfos.cswAccess = this.getCswAccessTextField().getValue();
		// INSPIRE
		systemConfigInfos.inspireEnable = this.getInspireEnableCheckBox().getValue();
		// PROXY
		systemConfigInfos.proxyUse = this.getProxyUseCheckBox().getValue();
		systemConfigInfos.proxyHost = this.getProxyHostTextField().getValue();
		systemConfigInfos.proxyPort = this.getProxyPortTextField().getValue();
		systemConfigInfos.proxyUserName = this.getProxyUserNameTextField().getValue();
		systemConfigInfos.proxyPassword = this.getProxyPasswordTextField().getValue();
		// FEEDBACK
		systemConfigInfos.feedBackEmail = this.getFeedBackEmailTextField().getValue();
		systemConfigInfos.feedBackSmtpHost = this.getFeedBackSmtpHostTextField().getValue();
		systemConfigInfos.feedBackSmtpPort = this.getFeedBackSmtpPortTextField().getValue();
		// AUTHENTICATION
		systemConfigInfos.userSelfRegistrationEnable = this.getUserSelfRegistrationEnableCheckBox().getValue();
		return systemConfigInfos;
	}
});