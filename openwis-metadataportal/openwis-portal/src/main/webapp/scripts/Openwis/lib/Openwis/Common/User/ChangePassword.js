Ext.ns('Openwis.Common.User');

Openwis.Common.User.ChangePassword = Ext.extend(Ext.Container, {
	
	initComponent: function() {
		Ext.apply(this, {
			style: {
				margin: '10px 30px 10px 30px'
			}
		});
		Openwis.Common.User.ChangePassword.superclass.initComponent.apply(this, arguments);
		
		this.initialize();
	},
	
	initialize: function() {
		//Create Header.
		this.add(this.getHeader());

		//Create new password form.
		this.add(this.getPswdForm());
	},

	getHeader: function() {
		if(!this.header) {
			this.header = new Ext.Container({
				html: Openwis.i18n('Security.User.ChangePswd.title'),
				cls: 'administrationTitle1'
			});
		}
		return this.header;
	},
	
    getPswdForm: function() {
		if(!this.pswdForm) {
            this.pswdForm = new Ext.form.FormPanel ({
                layout:'table',
                border : true,
                layoutConfig: {
                    // The total column count must be specified here
                    columns: 2
                },
                items: [
                    this.createLabel(Openwis.i18n('Security.User.EnterPswd.label')),
    		        this.getPasswordTextField(),
    		        this.createLabel(Openwis.i18n('Security.User.ConfirmPswd.label')),
        		    this.getConfirmPasswordTextField()
                ],
                buttons: [
    		        this.add(new Ext.Button(this.getChangePasswordAction()))
                ]
            });
		}
		return this.pswdForm;
	},
	
	//-- Password Label and Text Fields
	createLabel: function(label) {
        return new Openwis.Utils.Misc.createLabel(label);
    },
    
    getPasswordTextField: function() {
        if(!this.passwordTextField) {
            this.passwordTextField = new Ext.form.TextField({
                inputType: 'password',
                name: 'password',
                allowBlank:false,
                width: 150
            });
        }
        return this.passwordTextField;
    },
    
    getConfirmPasswordTextField: function() {
        if(!this.confirmPasswordTextField) {
            this.confirmPasswordTextField = new Ext.form.TextField({
                inputType: 'password',
                name: 'password',
                allowBlank:false,
                width: 150
            });
        }
        return this.confirmPasswordTextField;
    },
	
	//-- Actions implemented on Data Policy Administration.
	
	getChangePasswordAction: function() {
		if(!this.changePasswordAction) {
			this.changePasswordAction = new Ext.Action({
				text:Openwis.i18n('Security.User.ChangePswd.Btn'),
				scope: this,
				handler: function() {
				    var isValid = this.getPswdForm().getForm().isValid();
				    var firstPassword = this.getPasswordTextField().getValue();
    				var confirmPassword = this.getConfirmPasswordTextField().getValue();
				    if (isValid && (firstPassword == confirmPassword)) {
        
				        var params = {};
        				params.password = this.getPasswordTextField().getValue();
					
    					var saveHandler = new Openwis.Handler.Save({
    						url: configOptions.locService+ '/xml.user.changePassword',
    						params: params,
    						listeners: {
    							success: function() {
    								Ext.Msg.show({
                                       title: Openwis.i18n('Security.User.ChangePswdDlg.success.title'),
                                       msg: Openwis.i18n('Security.User.ChangePswdDlg.success.msg'),
                                       buttons: Ext.Msg.OK,
                                       scope: this,
                                       icon: Ext.MessageBox.INFO
                                    });
    							},
    							scope: this
    						}
    					});
    					saveHandler.proceed();
    				} else {
    				    Ext.Msg.show({
        				    title: Openwis.i18n('Security.User.ChangePswdDlg.failed.title'),
        				    msg: Openwis.i18n('Security.User.ChangePswdDlg.failed.msg'),
    	                    buttons: Ext.MessageBox.OK,
    	                    icon: Ext.MessageBox.WARNING
    	               });
    				}
				}
			});
		}
		return this.changePasswordAction;
	}
});