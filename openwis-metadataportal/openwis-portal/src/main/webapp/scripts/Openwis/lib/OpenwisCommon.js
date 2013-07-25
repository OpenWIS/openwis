/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/* 
 * @requires OpenLayers/BaseTypes.js
 * @requires OpenLayers/Lang/en.js
 * @requires OpenLayers/Console.js
 */ 

(function() {
    /**
     * Before creating the OpenLayers namespace, check to see if
     * OpenLayers.singleFile is true.  This occurs if the
     * OpenLayers/SingleFile.js script is included before this one - as is the
     * case with single file builds.
     */
    var singleFile = false;
    
    /**
     * Namespace: OpenLayers
     * The OpenLayers object provides a namespace for all things OpenLayers
     */
    window.OpenwisScriptsHelperCommon = {
        
        /**
         * Property: _scriptName
         * {String} Relative path of this script.
         */
        _scriptName: "lib/OpenwisCommon.js",

        /**
         * Function: _getScriptLocation
         * Return the path to this script.
         *
         * Returns:
         * {String} Path to this script
         */
        _getScriptLocation: function () {
            var scriptLocation = "";            
            var isOL = new RegExp("(^|(.*?\\/))(" + OpenwisScriptsHelperCommon._scriptName + ")(\\?|$)");
         
            var scripts = document.getElementsByTagName('script');
            for (var i=0, len=scripts.length; i<len; i++) {
                var src = scripts[i].getAttribute('src');
                if (src) {
                    var match = src.match(isOL);
                    if(match) {
                        scriptLocation = match[1];
                        break;
                    }
                }
            }
            return scriptLocation;
        }
    };
    /**
     * OpenLayers.singleFile is a flag indicating this file is being included
     * in a Single File Library build of the OpenLayers Library.
     * 
     * When we are *not* part of a SFL build we dynamically include the
     * OpenLayers library code.
     * 
     * When we *are* part of a SFL build we do not dynamically include the 
     * OpenLayers library code as it will be appended at the end of this file.
      */
    if(!singleFile) {
        var jsfiles = new Array(
            "Openwis/Utils/Array.js",
            "Openwis/Utils/Geo.js",
            "Openwis/Utils/Date.js",
            "Openwis/Utils/Tooltip.js",
            "Openwis/Utils/MessageBox.js",
            "Openwis/Utils/Override.js",
            "Openwis/Utils/Misc.js",
            "Openwis/Utils/MessageMustLogin.js",
            "Openwis/Utils/MessageBoxAccessDenied.js",
            "Openwis/Utils/MessageBoxServiceNotAllowed.js",
            "Openwis/Utils/Xml.js",
            "Openwis/Handler/Save.js",
            "Openwis/Handler/Remove.js",
            "Openwis/Handler/Get.js",
            "Openwis/Handler/Index.js",
            "Openwis/Handler/GetNoJson.js",
            "Openwis/Handler/GetNoJsonResponse.js",
            "Openwis/Handler/GetWithoutError.js",
            "Openwis/Data/JeevesJsonResponseHandler.js",
            "Openwis/Data/JeevesJsonReader.js",
            "Openwis/Data/JeevesJsonStore.js",
            "Openwis/Data/JeevesJsonSubmit.js",
            "Openwis/Data/JeevesJsonTreeLoader.js",
            "Openwis/Conf/Conf.js",
            "Openwis/RequestSubscription/BackUp/BackupSelection.js",
            "Openwis/Common/Dissemination/FavoriteFTPWindow.js",
        	"Openwis/Common/Dissemination/FavoriteEmailWindow.js",
        	"Openwis/Common/Dissemination/Favorites.js",
        	"Openwis/Common/User/PersonalInformation.js",
        	"Openwis/Common/User/UserInformation.js", 
        	"Openwis/Common/User/ChangePassword.js",
        	"Openwis/Common/Search/KeywordsSearch.js"
        ); // etc.

        var allScriptTags = new Array(jsfiles.length);
        var host = OpenwisScriptsHelperCommon._getScriptLocation() + "lib/";   
        for (var i=0, len=jsfiles.length; i<len; i++) {
            allScriptTags[i] = "<script src='" + host + jsfiles[i] +
                               "'></script>";
        }
        document.write(allScriptTags.join(""));
    }
})();
