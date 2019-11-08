Ext.ns('Openwis.Common.User');

Openwis.Common.User.PersonalInformation = Ext.extend(Ext.form.FormPanel, {  
	
    initComponent: function() {
        Ext.apply(this, 
        {
                itemCls: 'formItems',
                title: Openwis.i18n('Security.User.PersoInfo.Title'),
                width:600,
			    height:500,
                items: [{
                    layout:'table',
                    border : false,
                    layoutConfig: {
                        // The total column count must be specified here
                        columns: 4
                    },
                    items: this.getFormItems()
                }]
        });
        Openwis.Common.User.PersonalInformation.superclass.initComponent.apply(this, arguments);
    },
    
    getFormItems: function() {
    	var items = [
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.UserName.Label')),
	        this.getUserNameTextField(),
	        this.createDummy(),
	        this.createDummy(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.LastName.Label')),
	        this.getSurNameTextField(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.FirstName.Label')),
	        this.getNameTextField()
        ];
    	
    	if (!this.hidePassword) {
    		items = items.concat([
      	        this.createPasswordLabel(),
    	        this.createPasswordTextField(),
    	        this.createDummy(),
    	        this.createDummy()
            ]);
    	}
    	
    	items = items.concat([
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.Address.Label')),
	        this.getAddressTextField(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.City.Label')),
	        this.getCityTextField(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.State.Label')),
	        this.getStateTextField(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.Zip.Label')),
	        this.getZipTextField(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.Country.Label')),
	        this.getCountryTextField(),
	        this.createDummy(),
	        this.createDummy(),
	        this.createLabel(Openwis.i18n('Security.User.PersoInfo.ContactEmail.Label')),
	        this.getEmailTextField()
	    ]);
    	return items;
    },
    
    createLabel: function(label) {
        return new Openwis.Utils.Misc.createLabel(label);
    },
    
    createDummy: function() {
        return new Openwis.Utils.Misc.createDummy();
    },
    
    createPasswordLabel: function() {
        if(this.isEdition) {
        	return this.createLabel(Openwis.i18n('Security.User.PersoInfo.NewPassword.Label'));
        } else {
            return this.createLabel(Openwis.i18n('Security.User.PersoInfo.Password.Label'));
        }
    },
    
   createPasswordTextField: function() {
        if(this.isEdition) {
            return this.getPasswordTextField(true);
        } else {
            return this.getPasswordTextField(false);
        }
    },
    

   /**
     * The text field for the user username.
     */
    setUserInformation: function(user) {
            this.getUserNameTextField().setValue(user.username);
            this.getUserNameTextField().disable();
            this.getNameTextField().setValue(user.name);
            this.getSurNameTextField().setValue(user.surname);
            this.getEmailTextField().setValue(user.emailContact);

            if (!this.hidePassword) {
            	this.getPasswordTextField().setValue(user.password);
            }
            
            // Address
           this.getAddressTextField().setValue(user.address.address);
           this.getZipTextField().setValue(user.address.zip);
           this.getStateTextField().setValue(user.address.state);
           this.getCityTextField().setValue(user.address.city);
           this.getCountryTextField().setValue(user.address.country);
    },
    
    
        /**
     * The text field for the user username.
     */
    getUserNameTextField: function() {
        if(!this.usernameTextField) {
            this.usernameTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.UserName.Label'),
                name: 'username',
                allowBlank:false,
                width: 150
            });
        }
        return this.usernameTextField;
    },
    
    
    /**
     * The text field for the user name.
     */
    getNameTextField: function() {
        if(!this.nameTextField) {
            this.nameTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.FirstName.Label'),
                name: 'name',
                allowBlank:false,
                width: 150
            });
        }
        return this.nameTextField;
    },
    
    /**
     * The text field for the user surname.
     */
    getSurNameTextField: function() {
        if(!this.surnameTextField) {
            this.surnameTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.LastName.Label'),
                name: 'surname',
                allowBlank:false,
                width: 150
            });
        }
        return this.surnameTextField;
    },
    
    getPasswordTextField: function(allowblankValue) {
        if(!this.passwordTextField) {
            this.passwordTextField = new Ext.form.TextField({
                fieldLabel: 'Password',
                inputType: 'password',
                name: 'password',
                allowBlank:allowblankValue,
                width: 150
            });
        }
        return this.passwordTextField;
    },
    
    /**
     * The text field for the user address.
     */
    getAddressTextField: function() {
        if(!this.addressTextField) {
            this.addressTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.Address.Label'),
                name: 'address',
                allowBlank:true,
                width: 150
            });
        }
        return this.addressTextField;
    },
    
    /**
     * The text field for the user address state.
     */
    getStateTextField: function() {
        if(!this.stateTextField) {
            this.stateTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.State.Label'),
                name: 'state',
                allowBlank:true,
                width: 150
            });
        }
        return this.stateTextField;
    },
    
    /**
     * The text field for the user zip address.
     */
    getZipTextField: function() {
        if(!this.zipTextField) {
            this.zipTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.Zip.Label'),
                name: 'zip',
                allowBlank:true,
                width: 150
            });
        }
        return this.zipTextField;
    },
    
    /**
     * The text field for the user address city.
     */
    getCityTextField: function() {
        if(!this.cityTextField) {
            this.cityTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.City.Label'),
                name: 'city',
                allowBlank:true,
                width: 150
            });
        }
        return this.cityTextField;
    },
    
    /**
     * The text field for the user address country.
     */
    getCountryTextField: function() {
        if(!this.countryTextField) {
            this.countryTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.Country.Label'),
                name: 'country',
                allowBlank:true,
                width: 150
            });
        }
        return this.countryTextField;
    },
    
    /**
     * The text field for the user email.
     */
    getEmailTextField: function() {
        if(!this.emailTextField) {
            this.emailTextField = new Ext.form.TextField({
                fieldLabel: Openwis.i18n('Security.User.PersoInfo.ContactEmail.Label'),
                name: 'email',
                vtype:'email',
                allowBlank:false,
                width: 150
            });
        }
        return this.emailTextField;
    },
    
    getUser: function(user) {
        if (!user) {
            user = {};
        }        
        
        //The user attributes.
        
       user.name = this.getNameTextField().getValue();
       user.username = this.getUserNameTextField().getValue();
       user.surname = this.getSurNameTextField().getValue();
       user.emailContact = this.getEmailTextField().getValue();

       //password
       if (!this.hidePassword) {
    	   user.password = this.getPasswordTextField().getValue();
       }
       
       // Address
       user.address = {};
       user.address.address = this.getAddressTextField().getValue();
       user.address.zip = this.getZipTextField().getValue();
       user.address.state = this.getStateTextField().getValue();
       user.address.city = this.getCityTextField().getValue();
       user.address.country = this.getCountryTextField().getValue();
        
       return user;
    }
});