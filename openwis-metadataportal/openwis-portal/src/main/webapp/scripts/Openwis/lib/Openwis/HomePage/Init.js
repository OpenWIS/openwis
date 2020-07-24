var homePageViewport;

Ext.onReady(function() {
    Ext.QuickTips.init();


    homePageViewport = new Openwis.HomePage.Viewport();

    /*
    * Best solution I can find. totusi ramane un kkt.
    * So the idea is to be able to jumb from other pages (like Help) by click on a footer product link and
    * automatically search for a product. But the pages are a mixed between ExtJS and jsp and you're not sure that a function
    * in jsp like window.onload will be executed AFTER ExtJS initialization. As the matter of fact, it never will.
    * Hence this fkup solution where we search for a product after the initialization of Viewport.
    * Function to automatically search if the url contains a query string like productKey=noaa
    * This hack allows the search to be done at init so you go to homepage & search
    */
    doMenuSearch(homePageViewport);

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

function doMenuSearch(searchObj) {
     const urlParams = new URLSearchParams(window.location.search);
     const productKey = urlParams.get('productKey');
     if (productKey !== null) {
          var product = {'urn':getSearchKey(productKey)};
          searchObj.getSearchPanel().getNormalSearchPanel().whatTextField.setValue(getSearchKey(productKey));
          searchObj.doSearch(product);
     }
}

function getParameterByName(name, url) {
    if (!url) url = window.location.href;
    name = name.replace(/[\[\]]/g, '\\$&');
    var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';
    return decodeURIComponent(results[2].replace(/\+/g, ' '));
}

function getSearchKey(productKey) {
const searchKeys = {
        noaa20: 'noaa-20 image',
        suomiNpp: 'suomi npp image',
        aqua: 'Satellite Imagery AQUA',
        terra: 'Satellite Imagery TERRA',
        haze: 'haze_map',
        hotspotNoaa20: 'noaa20 record',
        hotspotNpp: 'npp record',
        hotspotAqua: 'AQUA_hotspot_location',
        hotspotTerra: 'TERRA_hotspot_location',
        hazeDispersion: 'haze_dispersion',
    };

    return searchKeys[productKey];
}