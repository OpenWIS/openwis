Ext.ns('Openwis.Admin.User');

Openwis.Admin.User.Privileges = Ext.extend(Ext.form.FormPanel, {  

    initComponent: function() {
        Ext.apply(this, 
        {
                itemCls: 'formItems',
                title: Openwis.i18n('Security.User.Privileges.Title'),
                width:600,
			    height:500,
                items: [{
                    layout:'table',
                    border : false,
                    layoutConfig: {
                        columns: 2
                    },
                    items: [
                        this.createLabel(Openwis.i18n('Security.User.Privileges.Profile')),
                        this.getProfileComboBox(),
                        this.createLabel(Openwis.i18n('Security.User.Privileges.Groups')),
                        this.getGroupsMultiSelector(),
                        this.createLabel(Openwis.i18n('Security.User.Privileges.ClassOfService')),
                        this.getClassOfServiceComboBox(),
                        this.createLabel(Openwis.i18n('Security.User.Privileges.NeedUserAccount')),
                        this.getNeedUserAccountCheckBox(),
                        this.createLabel(Openwis.i18n('Security.User.Privileges.Backup')),
                        this.getBackupMultiSelector()
                    ]
                }]
        });
        Openwis.Admin.User.Privileges.superclass.initComponent.apply(this, arguments);
    },

    createLabel: function(label) {
        return new Openwis.Utils.Misc.createLabel(label);
    },
    
    createDummy: function() {
        return new Openwis.Utils.Misc.createDummy();
    },
    
    
    init: function(profiles, groups, backups, classOfServices) {
        this.getProfileComboBox().getStore().loadData(profiles);
        this.getGroupsMultiSelector().multiselects[0].store.loadData(groups);
        this.getBackupMultiSelector().multiselects[0].store.loadData(backups);
        this.getClassOfServiceComboBox().getStore().loadData(classOfServices);
   
    },
    
    /**
     * Set the user information
     */
    setUserInformation: function(user) {
          // needUserAccount
          this.getNeedUserAccountCheckBox().setValue(user.needUserAccount);
          // profile
          this.getProfileComboBox().setValue(user.profile);
          // classOfService
          this.getClassOfServiceComboBox().setValue(user.classOfService);
          // backUps
          this.getBackupMultiSelector().multiselects[1].store.loadData(user.backUps);
          // groups;
          this.getGroupsMultiSelector().multiselects[1].store.loadData(user.groups);
          
    },

    getProfileComboBox: function() {
        if(!this.profileComboBox) {
            var profilesStore = new Ext.data.JsonStore({
                // store configs
                autoDestroy: true,
                // reader configs
                idProperty: 'id',
                fields: [
                    {name: 'id'},{name: 'name'}
                ]
            });
        
            this.profileComboBox = new Ext.form.ComboBox({
                fieldLabel: Openwis.i18n('Security.User.Privileges.Profile'),
                name: 'profile',
                mode: 'local',
                typeAhead: true,
                triggerAction: 'all',
                selectOnFocus:true,
                store: profilesStore,
                editable: false,
                allowBlank: false,
                width: 250,
                displayField: 'name',
                valueField: 'id'
            });
        }
        
        return this.profileComboBox;
    },
    
    getClassOfServiceComboBox: function() {
        if(!this.classOfServiceComboBox) {
            var classOfServiceStore = new Ext.data.JsonStore({
                // store configs
                autoDestroy: true,
                // reader configs
                idProperty: 'id',
                fields: [
                    {name: 'id'},{name: 'name'}
                ]
            });
        
            this.classOfServiceComboBox = new Ext.form.ComboBox({
                fieldLabel: Openwis.i18n('Security.User.Privileges.ClassOfService'),
                name: 'Class Of Service',
                mode: 'local',
                typeAhead: true,
                triggerAction: 'all',
                selectOnFocus:true,
                store: classOfServiceStore,
                editable: false,
                allowBlank: false,
                width: 250,
                displayField: 'name',
                valueField: 'id'
            });
        }
        
        return this.classOfServiceComboBox;
    },

    /**
     * The check box for the need user account.
     */
    getNeedUserAccountCheckBox: function() {
        if(!this.needUserAccountCheckBox) {
            this.needUserAccountCheckBox = new Ext.form.Checkbox({
                fieldLabel: Openwis.i18n('Security.User.Privileges.NeedUserAccount'),
                name: 'needUserAccount',
                width: 125
            });
        }
        return this.needUserAccountCheckBox;
    },
    
    getGroupsMultiSelector: function() {
        if(!this.isForm) {
            var ds = new Ext.data.JsonStore({
                    // store configs
                    autoDestroy: true,
                    // reader configs
                    idProperty: 'name',
                    fields: [
                        {name: 'id'},{name: 'name'},{name: 'global'}
                    ]
                });
                
             var ds2 = new Ext.data.JsonStore({
                    // store configs
                    autoDestroy: true,
                    // reader configs
                    idProperty: 'name',
                    fields: [
                        {name: 'id'},{name: 'name'},{name: 'global'}
                    ]
                });

            this.isForm = new Ext.ux.form.ItemSelector({
                        xtype: 'itemselector',
                        name: 'itemselector',
                        fieldLabel: 'ItemSelector',
                        width: 400,
                        drawUpIcon: false,
                        drawDownIcon: false,
                        drawTopIcon: false,
                        drawBotIcon: false,
                        imagePath: '../../scripts/ext-ux/images/',
                        multiselects: [{
                            width: 150,
                            height: 150,
                            store: ds,
                            legend: Openwis.i18n('Security.User.Privileges.Available'),
                            displayField: 'name',
                            valueField: 'name'
                        },{
                            width: 150,
                            height: 150,
                            store: ds2,
                            legend: Openwis.i18n('Security.User.Privileges.Selected'),
                            displayField: 'name',
                            valueField: 'name'
                        }]
                });
        }
        return this.isForm;
    },

    getBackupMultiSelector: function() {
        if(!this.isBackupForm) {
            var ds = new Ext.data.JsonStore({
                    // store configs
                    autoDestroy: true,
                    // reader configs
                    idProperty: 'name',
                    fields: [
                        {name: 'name'}
                    ]
                });
                
             var ds2 = new Ext.data.JsonStore({
                    // store configs
                    autoDestroy: true,
                    // reader configs
                    idProperty: 'name',
                    fields: [
                        {name: 'name'}
                    ]
                });

            this.isBackupForm = new Ext.ux.form.ItemSelector({
                        xtype: 'itemselector',
                        name: 'itemselector',
                        fieldLabel: 'ItemSelector',
                        width: 400,
                        drawUpIcon: false,
                        drawDownIcon: false,
                        drawTopIcon: false,
                        drawBotIcon: false,
                        imagePath: '../../scripts/ext-ux/images/',
                        multiselects: [{
                            width: 150,
                            height: 150,
                            store: ds,
                            legend: Openwis.i18n('Security.User.Privileges.Available'),
                            displayField: 'name',
                            valueField: 'name'
                        },{
                            width: 150,
                            height: 150,
                            store: ds2,
                            legend: Openwis.i18n('Security.User.Privileges.Selected'),
                            displayField: 'name',
                            valueField: 'name'
                        }]
                });
        }
        return this.isBackupForm;
    },
    
    getUser: function(user) {
        if (!user) {
            user = {};
        }
        
        // needUserAccount
        user.needUserAccount = this.getNeedUserAccountCheckBox().getValue();
        // profile
        user.profile = this.getProfileComboBox().getValue();
        // classOfService
        user.classOfService = this.getClassOfServiceComboBox().getValue();
        // groups;
        user.groups = [];
        var groupStore = this.getGroupsMultiSelector().multiselects[1].store;
        for(var i=0; i<groupStore.getCount(); i++) {
            var group = groupStore.getAt(i);
            user.groups.push({name : group.get("name"), global : group.get("global"), id : group.get("id")});
        }
        // backUps
        var backUps = this.getBackupMultiSelector().multiselects[1].store;
        user.backUps = [];
        for(var i=0; i<backUps.getCount(); i++) {
            var backUp = backUps.getAt(i);
            user.backUps.push({name : backUp.get("name")});
        }
        return user;
   }

});