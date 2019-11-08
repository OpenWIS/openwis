var homePageViewport;

Ext.onReady(function() {
    Ext.QuickTips.init();
    
    homePageViewport = new Openwis.HomePage.Viewport();
    
    //if params.....
    if (remoteSearch.url) {
    	var params = {};
    	if (!remoteSearch.connection){
	    	params.any = remoteSearch.urn;
	        params.sortBy = 'relevance';
	        params.hitsPerPage = 10;
	        params.relation = 'overlaps';
	        params.permanentLink = true;
    	}
       
        homePageViewport.getSearchResultsPanel().loadSearchResults(remoteSearch.url, params);
        
        if (remoteSearch.type == 'ADHOC') {
            doAdhocRequest(remoteSearch.urn);
        } else if (remoteSearch.type == 'SUBSCRIPTION') {
        	if (!remoteSearch.backupRequestId) {
        		doSubscription(remoteSearch.urn);
        	} else {
        		//category is null -> no message box is displayed.
        		doSubscriptionFromCache(remoteSearch.urn, null, remoteSearch.backupRequestId, remoteSearch.backupDeployment);
        	}
            
        } 
    }
});