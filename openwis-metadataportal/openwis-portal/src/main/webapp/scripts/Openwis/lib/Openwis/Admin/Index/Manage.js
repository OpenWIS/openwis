Ext.ns('Openwis.Admin.Index');

Openwis.Admin.Index.Manage = Ext.extend(Ext.Container, {
    
    initComponent: function() {
        Ext.apply(this, {
            style: {
                margin: '10px 30px 10px 30px'
            }
        });
        Openwis.Admin.Index.Manage.superclass.initComponent.apply(this, arguments);
        
        this.initialize();
    },
	
    initialize: function() {
        //Create Header.
        this.add(this.getHeader());
        
        //Create Index form.
        this.getIndexFormPanel().addButton(new Ext.Button(this.getRebuildAction()));
        this.getIndexFormPanel().addButton(new Ext.Button(this.getOptimizeAction()));
		this.add(this.getIndexFormPanel());
		
		this.doLayout();
    },

    getHeader: function() {
        if(!this.header) {
            this.header = new Ext.Container({
                html: Openwis.i18n('Index.Administration.Title'),
                cls: 'administrationTitle1'
            });
        }
        return this.header;
    },
    
    //-- Form for metadata creation.

    getRebuildAction: function() {
        if(!this.rebuildAction) {
            this.rebuildAction = new Ext.Action({
                text: Openwis.i18n('Common.Btn.Rebuild'),
                scope: this,
                handler: function() {
					var getHandler = new Openwis.Handler.Index({
		    			url: configOptions.locService+ '/metadata.admin.index.rebuild',
		    			params: {}
		    		});
		    		getHandler.proceed();
                }
            });
        }
        return this.rebuildAction;
    },

    getOptimizeAction: function() {
        if(!this.optimizeAction) {
            this.optimizeAction = new Ext.Action({
                text: Openwis.i18n('Common.Btn.Optimize'),
                scope: this,
                handler: function() {
					var getHandler = new Openwis.Handler.Index({
		    			url: configOptions.locService+ '/metadata.admin.index.optimize',
		    			params: {}
		    		});
		    		getHandler.proceed();
                }
            });
        }
        return this.optimizeAction;
    },

	/**
	 *	The form panel.
	 */
	getIndexFormPanel: function() {
		if(!this.indexFormPanel) {
			this.indexFormPanel = new Ext.form.FormPanel({
				itemCls: 'formItems',
				border: false
			});
		}
		return this.indexFormPanel;
	}
});