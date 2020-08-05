Ext.ns("Openwis.Utils.Storage");

Openwis.Utils.Storage.save = function(key, value) {
    window.sessionStorage.setItem(key,value);
}

Openwis.Utils.Storage.get = function(key) {
    return window.sessionStorage.getItem(key);
}

Openwis.Utils.Storage.remove = function(key) {
    if (window.sessionStorage.key(key) >= 0) {
        window.sessionStorage.removeItem(key);
    }
}