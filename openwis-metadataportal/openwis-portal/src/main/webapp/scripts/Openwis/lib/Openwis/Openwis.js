/**
 * USER.
 */
function doAdhocRequest(productMetadataUrn, gtsCategory) {
	if (gtsCategory == "WMO Additional") {
		function showResult(btn) {
			if (btn == "yes") {
				displayAdhocRequestWizard(productMetadataUrn);
			}
		};
		Openwis.Utils.MessageBox.warningMsg(Openwis.i18n('AdditionalProduct.warning.title'), Openwis.i18n('AdditionalProduct.warning.msg'), showResult,this);
	} else {
		displayAdhocRequestWizard(productMetadataUrn);
	}
}

function displayAdhocRequestWizard(productMetadataUrn) {
	var wizard = new Openwis.RequestSubscription.Wizard();
	wizard.initialize(productMetadataUrn, 'ADHOC', false, 'Create', null, false);
}

function doAdhocRequestFromCache(productMetadataUrn, gtsCategory) {
	if (gtsCategory == "WMO Additional") {
		function showResult(btn) {
			if (btn == "yes") {
				displayAdhocRequestFromCacheWizard(productMetadataUrn);
			}
		};
		Openwis.Utils.MessageBox.warningMsg(Openwis.i18n('AdditionalProduct.warning.title'), Openwis.i18n('AdditionalProduct.warning.msg'), showResult,this);
	} else {
		displayAdhocRequestFromCacheWizard(productMetadataUrn);
	}
}

function displayAdhocRequestFromCacheWizard(productMetadataUrn) {
	var wizard = new Openwis.RequestSubscription.Wizard();
	wizard.initialize(productMetadataUrn, 'ADHOC', true, 'Create', null, false);
}

function doSubscription(productMetadataUrn, gtsCategory) {
	if (gtsCategory == "WMO Additional") {
		function showResult(btn) {
			if (btn == "yes") {
				displaySubscriptionWizard(productMetadataUrn);
			}
		};
		Openwis.Utils.MessageBox.warningMsg(Openwis.i18n('AdditionalProduct.warning.title'), Openwis.i18n('AdditionalProduct.warning.msg'), showResult,this);
	} else {
		displaySubscriptionWizard(productMetadataUrn);
	}
}

function displaySubscriptionWizard(productMetadataUrn) {
	var wizard = new Openwis.RequestSubscription.Wizard();
	wizard.initialize(productMetadataUrn, 'SUBSCRIPTION', false, 'Create', null, false);
}

function doSubscriptionFromCache(productMetadataUrn, gtsCategory, backupRequestId, backupDeployment) {
	if (gtsCategory == "WMO Additional") {
		function showResult(btn) {
			if (btn == "yes") {
				displaySubscriptionFromCacheWizard(productMetadataUrn);
			}
		};
		Openwis.Utils.MessageBox.warningMsg(Openwis.i18n('AdditionalProduct.warning.title'), Openwis.i18n('AdditionalProduct.warning.msg'), showResult,this);
	} else {
		displaySubscriptionFromCacheWizard(productMetadataUrn, backupRequestId, backupDeployment);
	}
}

function displaySubscriptionFromCacheWizard(productMetadataUrn, backupRequestId, backupDeployment) {
	var wizard = new Openwis.RequestSubscription.Wizard();
	wizard.initialize(productMetadataUrn, 'SUBSCRIPTION', true, 'Create', null, false, backupRequestId, backupDeployment);
}

function doFollowMyAdhocs(configOptions) {
    var myAdhocs = new Openwis.MyAccount.FollowMyAdhocs();
    myAdhocs.initFollowMyAdhocs(configOptions);
}

function doFollowMySubscriptions(configOptions) {
    var mySubscriptions = new Openwis.MyAccount.FollowMySubscriptions();
    mySubscriptions.initFollowMySubscriptions(configOptions);
}

function doLastProducts(configOptions) {
    var lastProds = new Openwis.HomePage.LastProducts();
    lastProds.initLastProducts(configOptions);
}

/**
 *	My Account
 */
function doDisplayMyAccountHomePage(configOptions) {
	var myAccountHomePage = new Openwis.MyAccount.HomePage();
	myAccountHomePage.initHomePage(configOptions);
}

/**
 *	ADMIN
 */
function doDisplayAdminHomePage(configOptions) {
	var adminHomePage = new Openwis.Admin.HomePage();
	adminHomePage.initHomePage(configOptions);
}

/**
 *    Common.
 */
 
var metadataWindow = null;
 
function doShowMetadataById(id, title, editable) {
    doShowMetadataViewer({id: id, title: title, editable: editable});
}

function doShowMetadataByUrn(urn, title) {
    doShowMetadataViewer({uuid: urn, title: title, editable: false});
}

function doShowMetadataByUrn(urn, title, editable) {
    doShowMetadataViewer({uuid: urn, title: title, editable: editable});
}


function doEditMetadataById(id, title) {
    doShowMetadataViewer({id: id, title: title, edit: true, editable: true});
}

function doEditMetadataByUrn(urn, title) {
    doShowMetadataViewer({uuid: urn, title: title, edit: true, editable: true});
}

function doShowMetadataViewer(params) {
    if(params.id) {
        geonet.MetadataDialog.loadWithId(params.id, params.title, null, params.edit, params.editable);
    } else {
        geonet.MetadataDialog.loadWithUUID(params.uuid, params.title, null, params.edit, params.editable);
    }
}

function addMetadataDialogCloseListener(listener, scope) {
	geonet.MetadataDialog.window.addListener('beforeclose', listener, scope);
}

function removeMetadataDialogCloseListener(listener, scope) {
	geonet.MetadataDialog.window.removeListener('beforeclose', listener, scope);
}

// old editor translation adapter
function translate(word){
	// append editor translation key
	var key = 'Metadata.ViewerEditor.EditorTranslations.' + word;
	return Openwis.i18n(key);
}

// Show related services
function doShowRelatedServices(id, uuid) {
	showRelatedServicesPanel(id, uuid);
}