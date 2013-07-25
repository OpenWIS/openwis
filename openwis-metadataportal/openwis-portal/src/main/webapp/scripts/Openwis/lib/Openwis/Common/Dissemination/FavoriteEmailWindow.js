Ext.ns('Openwis.Common.Dissemination');

Openwis.Common.Dissemination.FavoriteEmailWindow = Ext.extend(Ext.Window, {

initComponent: function() {
        Ext.apply(this, 
        {
            title: Openwis.i18n('Common.Dissemination.Email.Title'),
            layout: 'fit',
            width:400,
            height:300,
            modal: true,
            closeAction:'close'
        });
        Openwis.Common.Dissemination.FavoriteEmailWindow.superclass.initComponent.apply(this, arguments);

        this.initialize();

    },
    
    /**
     * Initializes the window.
     */
    initialize: function() {
        this.addEvents("favoriteEmailSaved");
        
        //-- Create form panel.
        this.add(this.getFavoriteEmailPanel());
        
        //-- Add buttons.
        this.addButton(new Ext.Button(this.getSaveAction()));
        this.addButton(new Ext.Button(this.getCancelAction()));

        if(this.isEdition) {
            this.getFavoriteEmailPanel().initializeFields(this.mail);
        }
        
        this.show();
    },
    
    getFavoriteEmailPanel: function() {
        if (this.favoriteEmailPanel == null) {
            this.favoriteEmailPanel = new Openwis.Common.Dissemination.MailDiffusion({allowAddressEdition: !this.isEdition});
        }                      
        return this.favoriteEmailPanel;
    },
    
    /**
     * The Save action.
     */
    getSaveAction: function() {
        if(!this.saveAction) {
            this.saveAction = new Ext.Action({
                text: Openwis.i18n('Common.Btn.Save'),
                scope: this,
                handler: function() {
                    if (this.getFavoriteEmailPanel().getForm().isValid()) {
                        var emailEdited = this.getFavoriteEmailPanel().getDisseminationValue();
                        emailEdited.disseminationTool = this.mail.disseminationTool;
                        this.fireEvent("favoriteEmailSaved", emailEdited);
                        this.close();
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
    }
});