Ext.ns('Openwis');

/**
 *    This class is an i18n helper stringly inspired of OpenLayers.
 *    This is a simple adaptation to openWIS' needs. 
 */

Openwis.Lang = {

    translate: function(key, context) {
        var dictionary = Openwis.Lang.Dictionary;
        var message = dictionary[key];
        if(!message) {
            message = key;
        }
        if(context) {
            message = Openwis.Lang.format(message, context);
        }
        return message;
    },
    
    format: function(template, context, args) {
        if(!context) {
            context = window;
        }

        // Example matching: 
        // str   = ${foo.bar}
        // match = foo.bar
        var replacer = function(str, match) {
            var replacement;

            // Loop through all subs. Example: ${a.b.c}
            // 0 -> replacement = context[a];
            // 1 -> replacement = context[a][b];
            // 2 -> replacement = context[a][b][c];
            var subs = match.split(/\.+/);
            for (var i=0; i< subs.length; i++) {
                if (i == 0) {
                    replacement = context;
                }

                replacement = replacement[subs[i]];
            }

            if(typeof replacement == "function") {
                replacement = args ?
                    replacement.apply(null, args) :
                    replacement();
            }

            // If replacement is undefined, return the string 'undefined'.
            // This is a workaround for a bugs in browsers not properly 
            // dealing with non-participating groups in regular expressions:
            // http://blog.stevenlevithan.com/archives/npcg-javascript
            if (typeof replacement == 'undefined') {
                return 'undefined';
            } else {
                return replacement; 
            }
        };

        return template.replace(Openwis.Lang.tokenRegEx, replacer);
    },

    /**
     * Used to find tokens in a string.
     * Examples: ${a}, ${a.b.c}, ${a-b}, ${5}
     */
    tokenRegEx:  /\$\{([\w.]+?)\}/g
    
};

Openwis.i18n = Openwis.Lang.translate;
