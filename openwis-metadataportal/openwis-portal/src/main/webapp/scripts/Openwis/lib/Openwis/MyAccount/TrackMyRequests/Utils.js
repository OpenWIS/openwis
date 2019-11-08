Ext.ns('Openwis.MyAccount.TrackMyRequests.Utils');

Openwis.MyAccount.TrackMyRequests.Utils.statusRendererImg = function(val) {
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

Openwis.MyAccount.TrackMyRequests.Utils.statusRenderer = function(val) {
	if(val == "FAILED") {
		return "Failed";
	} else if(val == "IN_PROGRESS") {
		return "In progress";
	} else if(val == "COMPLETE") {
		return "Complete";
	} else {
		return "Unknown";
	}
};

Openwis.MyAccount.TrackMyRequests.Utils.sizeRenderer = function(val) {
	if(val == 0) {
		return "";
	} else {
		return Openwis.Utils.Misc.bytesToKMG(val);
	}
};

Openwis.MyAccount.TrackMyRequests.Utils.backupRenderer = function(val) {
	if(val && val.name && val.name.trim() != "") {
		return val.name;
	} else {
		return "";
	}
};