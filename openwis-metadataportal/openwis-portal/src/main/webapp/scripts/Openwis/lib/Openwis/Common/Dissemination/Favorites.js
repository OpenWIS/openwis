Ext.ns('Openwis.Common.Dissemination');

Openwis.Common.Dissemination.Favorites = Ext.extend(Ext.Panel, {
    
    initComponent: function() {
        Ext.apply(this, {
            title: Openwis.i18n('Common.Dissemination.Favorites.Title'),
            style: {
                margin: '10px 30px 10px 30px'
            },
            width:600,
			height:500
        });
        Openwis.Common.Dissemination.Favorites.superclass.initComponent.apply(this, arguments);
        
        this.initialize();
    },
    
    initialize: function() {
        
        //Create Favorites grid.
        this.add(this.getFtpFavoritesGrid());
        
        //Create Emails grid.
        this.add(this.getEmailFavoritesGrid());
    },
    
    //-- Grid and Store.
    
    getFtpFavoritesGrid: function() {
        if(!this.ftpFavoritesGrid) {
            this.ftpFavoritesGrid = new Ext.grid.GridPanel({
                height: 150,
                width: 400,
                border: true,
                store: this.getFtpStore(),
                loadMask: true,
                columns: [
                    {id:'disseminationTool', header: Openwis.i18n('Common.Dissemination.Favorites.FTP.Tool'), dataIndex:'disseminationTool', sortable: true, width: 200},
                    {id:'host', header: Openwis.i18n('Common.Dissemination.Favorites.FTP.Destination'), dataIndex:'host', sortable: true, width: 200, renderer: Openwis.Common.Request.Utils.htmlSafeRenderer}
                ],
                sm: new Ext.grid.RowSelectionModel({
                    listeners: { 
                        rowselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getFtpEditAction().setDisabled(sm.getCount() != 1 );
                            sm.grid.ownerCt.getFtpRemoveAction().setDisabled(sm.getCount() == 0);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getFtpEditAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getFtpRemoveAction().setDisabled(sm.getCount() == 0);
                        }
                    }
                })
            });
            this.ftpFavoritesGrid.addButton(new Ext.Button(this.getNewFtpRMDCNAction()));
            this.ftpFavoritesGrid.addButton(new Ext.Button(this.getNewFtpPublicAction()));
            this.ftpFavoritesGrid.addButton(new Ext.Button(this.getFtpEditAction()));
            this.ftpFavoritesGrid.addButton(new Ext.Button(this.getFtpRemoveAction()));
        }
        return this.ftpFavoritesGrid;
    },

	getFtpStore: function() {
		if(!this.ftpStore) {
            this.ftpStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				fields: [
                    {name:'host', sortType: Ext.data.SortTypes.asUCString},
					{name:'path'},
					{name:'user'},
					{name:'password'},
					{name:'port'},
					{name:'passive'},
					{name:'checkFileSize'},
					{name:'fileName'},
					{name:'encrypted'},
					{name:'disseminationTool'}
				]
			});
		}
		return this.ftpStore;
	},
	
	
    
    //-- Actions implemented on FTP Favorites Administration.

    getNewFtpRMDCNAction: function() {
        if(!this.newFtpRMDCNAction) {
            this.newFtpRMDCNAction = new Ext.Action({
                text:Openwis.i18n('Common.Dissemination.Favorites.FTP.NewRMDCNFTP.button'),
                scope: this,
                handler: function() {
                    new Openwis.Common.Dissemination.FavoriteFTPWindow({
                        listeners: {
                            favoriteFTPSaved: function(ftpCreated) {
                                 this.getFtpFavoritesGrid().getStore().add(new Ext.data.Record(ftpCreated));
                            },
                            scope: this
                        },
                        ftp: {disseminationTool : 'RMDCN'},
                        isEdition : false
                    });
                }
            });
        }
        return this.newFtpRMDCNAction;
    },
    
    getNewFtpPublicAction: function() {
        if(!this.newFtpPublicAction) {
            this.newFtpPublicAction = new Ext.Action({
                text: Openwis.i18n('Common.Dissemination.Favorites.FTP.NewPublicFTP.button'),
                scope: this,
                handler: function() {
                    new Openwis.Common.Dissemination.FavoriteFTPWindow({
                        listeners: {
                            favoriteFTPSaved: function(ftpCreated) {
                                this.getFtpFavoritesGrid().getStore().add(new Ext.data.Record(ftpCreated));
                            },
                            scope: this
                        },
                        ftp: {disseminationTool : 'PUBLIC'},
                        isEdition : false
                    });
                }
            });
        }
        return this.newFtpPublicAction;
    },
    
    getFtpEditAction: function() {
        if(!this.editAction) {
            this.editAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Dissemination.Favorites.FTP.Edit.button'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getFtpFavoritesGrid().getSelectionModel().getSelected();
                    new Openwis.Common.Dissemination.FavoriteFTPWindow({
                        listeners: {
                            favoriteFTPSaved: function(ftpUpdated) {
                                var ftpToRemove;
                                for (var i=0; i < this.getFtpFavoritesGrid().getStore().getCount(); i++) {
                                    var record = this.getFtpFavoritesGrid().getStore().getAt(i);
                                    if ((record.get('host') == ftpUpdated.host) && (record.get('disseminationTool') == ftpUpdated.disseminationTool)) {
                                         ftpToRemove = record;
                                    }
                                }
                                if (ftpToRemove) {
                                    this.getFtpFavoritesGrid().getStore().remove(ftpToRemove);
                                }
                                this.getFtpFavoritesGrid().getStore().add(new Ext.data.Record(ftpUpdated));
                            },
                            scope: this
                        },
                        ftp: selectedRec.data ,
                        isEdition : true
                    });
                }
            });
        }
        return this.editAction;
    },
    
    getFtpRemoveAction: function() {
        if(!this.removeAction) {
            this.removeAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Dissemination.Favorites.FTP.Remove.button'),
                scope: this,
                handler: function() {
                    //Get the username to delete.
                    var selection = this.getFtpFavoritesGrid().getSelectionModel().getSelections();
                    this.getFtpFavoritesGrid().getStore().remove(selection);
                }
            });
        }
        return this.removeAction;
    },

     //-- Grid and Store.
    
    getEmailFavoritesGrid: function() {
        if(!this.emailFavoritesGrid) {
            this.emailFavoritesGrid = new Ext.grid.GridPanel({
                height: 150,
                width: 400,
                border: true,
                store: this.getEmailStore(),
                loadMask: true,
                columns: [
                    {id:'disseminationTool', header:Openwis.i18n('Common.Dissemination.Favorites.Email.Tool'), dataIndex:'disseminationTool', sortable: true, width: 200},
                    {id:'address', header:Openwis.i18n('Common.Dissemination.Favorites.Email.Address'), dataIndex:'address', sortable: true, width: 200}
                ],
                sm: new Ext.grid.RowSelectionModel({
                    listeners: { 
                        rowselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getEmailEditAction().setDisabled(sm.getCount() != 1 );
                            sm.grid.ownerCt.getEmailRemoveAction().setDisabled(sm.getCount() == 0);
                        },
                        rowdeselect: function (sm, rowIndex, record) {
                            sm.grid.ownerCt.getEmailEditAction().setDisabled(sm.getCount() != 1);
                            sm.grid.ownerCt.getEmailRemoveAction().setDisabled(sm.getCount() == 0);
                        }
                    }
                })
            });
            this.emailFavoritesGrid.addButton(new Ext.Button(this.getNewEmailRMDCNAction()));
            this.emailFavoritesGrid.addButton(new Ext.Button(this.getNewEmailPublicAction()));
            this.emailFavoritesGrid.addButton(new Ext.Button(this.getEmailEditAction()));
            this.emailFavoritesGrid.addButton(new Ext.Button(this.getEmailRemoveAction()));
        }
        return this.emailFavoritesGrid;
    },

	getEmailStore: function() {
		if(!this.emailStore) {
            this.emailStore = new Ext.data.JsonStore({
				// store configs
				autoDestroy: true,
				// reader configs
				fields: [
                    {name:'address', sortType: Ext.data.SortTypes.asUCString},
					{name:'headerLine'},
					{name:'mailDispatchMode'},
					{name:'subject'},
					{name:'mailAttachmentMode'},
					{name:'fileName'},
					{name:'disseminationTool'}
				]
			});
		}
		return this.emailStore;
	},
	
    //-- Actions implemented on Email Favorites Administration.

    getNewEmailRMDCNAction: function() {
        if(!this.newEmailRMDCNAction) {
            this.newEmailRMDCNAction = new Ext.Action({
                text:Openwis.i18n('Common.Dissemination.Favorites.Email.NewRMDCN.button'),
                scope: this,
                handler: function() {
                    new Openwis.Common.Dissemination.FavoriteEmailWindow({
                        listeners: {
                            favoriteEmailSaved: function(emailCreated) {
                                 this.getEmailFavoritesGrid().getStore().add(new Ext.data.Record(emailCreated));
                            },
                            scope: this
                        },
                        mail: {disseminationTool : 'RMDCN'},
                        isEdition : false
                    });
                }
            });
        }
        return this.newEmailRMDCNAction;
    },
    
    getNewEmailPublicAction: function() {
        if(!this.newEmailPublicAction) {
            this.newEmailPublicAction = new Ext.Action({
                text:Openwis.i18n('Common.Dissemination.Favorites.Email.NewPublic.button'),
                scope: this,
                handler: function() {
                    new Openwis.Common.Dissemination.FavoriteEmailWindow({
                        listeners: {
                            favoriteEmailSaved: function(emailCreated) {
                                this.getEmailFavoritesGrid().getStore().add(new Ext.data.Record(emailCreated));
                            },
                            scope: this
                        },
                        mail: {disseminationTool : 'PUBLIC'},
                        isEdition : false
                    });
                }
            });
        }
        return this.newEmailPublicAction;
    },
    
    getEmailEditAction: function() {
        if(!this.editEmailAction) {
            this.editEmailAction = new Ext.Action({
                disabled: true,
                text:Openwis.i18n('Common.Dissemination.Favorites.Email.Edit.button'),
                scope: this,
                handler: function() {
                    var selectedRec = this.getEmailFavoritesGrid().getSelectionModel().getSelected();
                    new Openwis.Common.Dissemination.FavoriteEmailWindow({
                        listeners: {
                            favoriteEmailSaved: function(emailUpdated) {
                                var emailToRemove;
                                for (var i=0; i < this.getEmailFavoritesGrid().getStore().getCount(); i++) {
                                    var record = this.getEmailFavoritesGrid().getStore().getAt(i);
                                   if ((record.get('address') == emailUpdated.address) && (record.get('disseminationTool') == emailUpdated.disseminationTool)) {
                                         emailToRemove = record;
                                    }
                                }
                                if (emailToRemove) {
                                    this.getEmailFavoritesGrid().getStore().remove(emailToRemove);
                                }
                                this.getEmailFavoritesGrid().getStore().add(new Ext.data.Record(emailUpdated));
                            },
                            scope: this
                        },
                        mail: selectedRec.data ,
                        isEdition : true
                    });
                }
            });
        }
        return this.editEmailAction;
    },
    
    getEmailRemoveAction: function() {
        if(!this.removeEmailAction) {
            this.removeEmailAction = new Ext.Action({
                disabled: true,
                text: Openwis.i18n('Common.Dissemination.Favorites.Email.Remove.button'),
                scope: this,
                handler: function() {
                    //Get the username to delete.
                    var selection = this.getEmailFavoritesGrid().getSelectionModel().getSelections();
                    this.getEmailFavoritesGrid().getStore().remove(selection);
                }
            });
        }
        return this.removeEmailAction;
    },

    // Getter & Setter user.

    getUser: function(user) {
        if (!user) {
            user = {};
        }
        
        if (!Ext.isDefined(user.ftps)) {
            user.ftps = [];
        }
        
        for (var i=0; i < this.getFtpFavoritesGrid().getStore().getCount(); i++) {
            var ftp = this.getFtpFavoritesGrid().getStore().getAt(i).data;
            user.ftps.push(ftp);
        }
        
        if (!Ext.isDefined(user.emails)) {
            user.emails = [];
        }
        
        for (var i=0; i < this.getEmailFavoritesGrid().getStore().getCount(); i++) {
            var email = this.getEmailFavoritesGrid().getStore().getAt(i);
            user.emails.push(email.data);
        }
         
        return user;
   },
   
    /**
     * Set the user favorites
     */
    setFavorites: function(user) {
        if (!Ext.isEmpty(user.ftps)) {
           // ftp favorites
           this.getFtpFavoritesGrid().getStore().loadData(user.ftps);
        }
        
        if (!Ext.isEmpty(user.emails)) {
           // emails favorites
           this.getEmailFavoritesGrid().getStore().loadData(user.emails);
        } 
    } 
        
});