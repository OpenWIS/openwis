Ext.ns('Openwis.HomePage.Search');

Openwis.HomePage.Search.SearchResultsPanel = Ext.extend(Ext.Panel, {

	initComponent: function() {
		Ext.apply(this, {
				region:'center',
        		border: false,
        		boxMinHeight: 400,
        		html: Openwis.i18n('HomePage.Main.Content')
		});
		Openwis.HomePage.Search.SearchResultsPanel.superclass.initComponent.apply(this, arguments);
	},

	loadSearchResults: function(url, params) {
		var getHandler = new Openwis.Handler.GetNoJson({
            url: url,
            params: params,
            useLoadMask: false,
            useHTMLMask: true,
            loadingMessage: Openwis.i18n('HomePage.Search.Loading'),
            maskEl: this,
            listeners: {
            	success: function(result) {
            		this.body.dom.innerHTML = result;
            		this.fireEvent('searchResultsDisplayed');
            	},
            	scope: this
            }
         });
         getHandler.proceed();
     }

});


        		