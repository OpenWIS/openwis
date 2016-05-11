Ext.ns('Openwis.Common.Request.Utils');

Openwis.Common.Request.Utils.statusRendererImg = function(val) {
	if(val == "FAILED") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_failed.png"/>';
	} else if(val == "IN_PROGRESS") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_inprogress.png"/>';
	} else if(val == "COMPLETE") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_complete.png"/>';
	} else {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_unknown.png"/>';
	}
};

Openwis.Common.Request.Utils.stateRendererImg = function(val) {
	if(val == "INVALID") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/subscription_invalid.png" title="'+Openwis.i18n('Subscription.state.invalid')+'"/>';
	} else if(val == "ACTIVE") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/subscription_active.gif" title="'+Openwis.i18n('Subscription.state.active')+'"/>';
	} else if(val == "SUSPENDED") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/subscription_inactive.gif" title="'+Openwis.i18n('Subscription.state.suspended')+'"/>';
	} else if(val == "SUSPENDED_BACKUP") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/subscription_inactive_backup.gif" title="'+Openwis.i18n('Subscription.state.suspended.backup')+'"/>';
	} else {
		return '<img src="' + configOptions.url + '/images/openwis/icons/state_unknown.png" title="'+Openwis.i18n('Subscription.state.unkown')+'"/>';
	}
};

Openwis.Common.Request.Utils.processedRequestStatusRendererImg = function(val) {
    if(val == "FAILED") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_failed.png"/>';
	} else if(val == "DISSEMINATED") {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_complete.png"/>';
	} else {
		return '<img src="' + configOptions.url + '/images/openwis/icons/status_inprogress.png"/>';
	}
};

Openwis.Common.Request.Utils.statusRenderer = function(val) {
	if(val == "FAILED") {
		return Openwis.i18n('Common.Status.Failed');
	} else if(val == "IN_PROGRESS") {
		return Openwis.i18n('Common.Status.InProgress');
	} else if(val == "COMPLETE") {
		return Openwis.i18n('Common.Status.Complete');
	} else {
		return Openwis.i18n('Common.Status.Unknown');
	}
};

Openwis.Common.Request.Utils.sizeRenderer = function(val) {
	if(val == 0) {
		return "";
	} else {
		return Openwis.Utils.Misc.bytesToKMG(val);
	}
};

Openwis.Common.Request.Utils.backupRenderer = function(val) {
	if(val && val.name && val.name.trim() != "") {
		return val.name;
	} else {
		return "";
	}
};

Openwis.Common.Request.Utils.requestTypeRenderer = function(val) {
	if(val == 'ADHOC') {
		return "R";
	}
	return "S";
};

Openwis.Common.Request.Utils.htmlSafeRenderer = function(val) {
	return Ext.util.Format.htmlEncode(val);
};