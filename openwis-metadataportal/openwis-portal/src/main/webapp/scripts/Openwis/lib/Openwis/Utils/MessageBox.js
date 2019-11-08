Ext.ns('Openwis.Utils.MessageBox');

Openwis.Utils.MessageBox.displayInternalError = function(resultFn, scope) {
	Ext.MessageBox.show({
		title: Openwis.i18n('MessageBox.displayInternalError.Title'),
		msg: Openwis.i18n('MessageBox.displayInternalError.Message'),
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.ERROR,
		fn: resultFn,
		scope: scope
	});
};

Openwis.Utils.MessageBox.displayErrorMsg = function(errorMsg, resultFn, scope) {
	Ext.MessageBox.show({
		title: Openwis.i18n('MessageBox.displayErrorMsg.Title'),
		msg: errorMsg,
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.ERROR,
		fn: resultFn,
		scope: scope
	});
};

Openwis.Utils.MessageBox.displayMustLogin = function() {
	Openwis.Utils.MessageBox.displayErrorMsg(Openwis.i18n('MessageBox.displayMustLogin.Message'));
};

Openwis.Utils.MessageBox.displaySaveSuccessful = function(resultFn, scope) {
	Ext.MessageBox.show({
		title: Openwis.i18n('MessageBox.displaySaveSuccessful.Title'),
		msg: Openwis.i18n('MessageBox.displaySaveSuccessful.Message'),
		buttons: Ext.MessageBox.OK,
		icon: Ext.MessageBox.INFO,
		fn: resultFn,
		scope: scope
	});
};

Openwis.Utils.MessageBox.displaySuccessMsg = function(msg, resultFn, scope) {
	Ext.MessageBox.show({
		title: Openwis.i18n('MessageBox.displaySuccessMsg.Title'),
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
