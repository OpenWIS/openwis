Ext.ns('Openwis.Common.Dissemination');

Openwis.Common.Dissemination.FavoriteFTPWindow = Ext.extend(Ext.Window, {

initComponent: function() {
        Ext.apply(this, 
        {
            title: Openwis.i18n('Common.Dissemination.FTP.Title'),
            layout: 'fit',
            width:400,
            height:350,
            modal: true,
            closeAction:'close'
        });
        Openwis.Common.Dissemination.FavoriteFTPWindow.superclass.initComponent.apply(this, arguments);

        this.initialize();

    },
    
    /**
     * Initializes the window.
     */
    initialize: function() {
        this.addEvents("favoriteFTPSaved");
        
        //-- Create form panel.
        this.add(this.getFavoriteFtpPanel());
        
        //-- Add buttons.
        this.addButton(new Ext.Button(this.getSaveAction()));
        this.addButton(new Ext.Button(this.getCancelAction()));

        if(this.isEdition) {
            this.getFavoriteFtpPanel().initializeFields(this.ftp);
        }
        
        this.show();
    },
    
    getFavoriteFtpPanel: function() {
        if (this.favoriteFtpPanel == null) {
            this.favoriteFtpPanel = new Openwis.Common.Dissemination.FTPDiffusion({allowHostEdition: !this.isEdition});
        }                      
        return this.favoriteFtpPanel;
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
                    if (this.getFavoriteFtpPanel().getForm().isValid()) {
                        var ftpEdited = this.getFavoriteFtpPanel().getDisseminationValue();
                        ftpEdited.disseminationTool = this.ftp.disseminationTool;
                        this.fireEvent("favoriteFTPSaved", ftpEdited);
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
                text:Openwis.i18n('Common.Btn.Cancel'),
                scope: this,
                handler: function() {
                    this.close();
                }
            });
        }
        return this.cancelAction;
    }
});