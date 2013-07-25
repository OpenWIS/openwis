Ext.ns('Openwis.Utils.MessageBox');

Openwis.Utils.MessageBox.displayInternalError = function(resultFn, scope) {
	Ext.MessageBox.show({
		title: 'Error',
		msg: 'An error occurred. Please contact your service desk.',
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.ERROR,
		fn: resultFn,
		scope: scope
	});
};

Openwis.Utils.MessageBox.displayErrorMsg = function(errorMsg, resultFn, scope) {
	Ext.MessageBox.show({
		title: 'Error',
		msg: errorMsg,
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.ERROR,
		fn: resultFn,
		scope: scope
	});
};

Openwis.Utils.MessageBox.displayMustLogin = function() {
	Openwis.Utils.MessageBox.displayErrorMsg("You must log in to perform this action.");
};

Openwis.Utils.MessageBox.displaySaveSuccessful = function(resultFn, scope) {
	Ext.MessageBox.show({
		title: 'Success',
		msg: 'Changes saved successfully.',
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.INFO,
		fn: resultFn,
		scope: scope
	});
};

Openwis.Utils.MessageBox.displaySuccessMsg = function(msg, resultFn, scope) {
	Ext.MessageBox.show({
		title: 'Success',
		msg: msg,
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.INFO,
		fn: resultFn,
		scope: scope
	});
};
	
Openwis.Utils.MessageBox.warningMsg = function(title, msg, resultFn, scope) {
	Ext.MessageBox.show({
		title: title,
		msg: msg,
		buttons: Ext.MessageBox.YESNO,
		icon: Ext.MessageBox.WARNING,
		fn: resultFn,
		scope: scope
	});
};
