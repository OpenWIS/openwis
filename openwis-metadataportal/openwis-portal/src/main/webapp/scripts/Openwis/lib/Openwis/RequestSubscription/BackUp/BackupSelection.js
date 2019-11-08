Ext.ns('Openwis.RequestSubscription.BackUp');

Openwis.RequestSubscription.BackUp.BackupSelection = Ext.extend(Ext.Panel, {
	
	initComponent: function() {
		Ext.apply(this, 
		{
		    width: 650,
			layout:'table',
			layoutConfig: {
			    columns: 2
			},
			style: {
                padding: '10px 10px 10px 30px'
            }
		});
		Openwis.RequestSubscription.BackUp.BackupSelection.superclass.initComponent.apply(this, arguments);

	},

    initializeAndShow: function() {
        //The back up combo box.
        this.add(this.getDeploymentsComboBox());
		
        this.add(this.getBackUpCentreButton());
		this.doLayout();
    },
	
	getDeploymentsComboBox: function() {
		if(!this.deploymentsComboBox) {
			var deploymentStore = new Openwis.Data.JeevesJsonStore({
                url: configOptions.locService+ '/xml.get.user.backup.centres',
                idProperty: 'name',
                fields: [
                    {
                        name:'name'
                    },{
                        name:'url'
                    }
                ],
                listeners: {
			    	load: function(store, records, options) {
			    		if (store.getCount() == 0) {
			    			this.getBackUpCentreButton().disable();
			    		}
			    	},
			    	scope: this
			    }
            });
        
            this.deploymentsComboBox = new Ext.form.ComboBox({
                store: deploymentStore,
				valueField: 'url',
				displayField:'name',
                name: 'deployment',
                emptyText: Openwis.i18n('TrackMySubscriptions.Remote.Select.Deployment'),
                typeAhead: true,
				triggerAction: 'all',
				editable: false,
				selectOnFocus:true,
				width: 200
            });
		}
		return this.deploymentsComboBox;
	},

	getBackUpCentreButton: function() {
		if (!this.backUpCentreButton) {
			this.backUpCentreButton = new Ext.Button(this.getGoToBackupAction());
		}
		return this.backUpCentreButton;
	},
	
	getGoToBackupAction: function() {
	    if(!this.goToBackupAction) {
            this.goToBackupAction = new Ext.Action({
				text: "Go to Back Up centre",
                scope: this,
                handler: function() {
                	 var logicalRemoteDeploymentURL = this.getDeploymentsComboBox().getValue();
                	 var requestIdentifier = this.config.requestID;
                	 window.open(logicalRemoteDeploymentURL + '/retrieve/subscribe/'+ this.config.productMetadataURN 
                			 + '?backupRequestId=' + requestIdentifier + "&deployment=" + localCentreName);
                }
            });
        }
        return this.goToBackupAction;
	}
	
});