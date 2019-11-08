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
    window.OpenwisScriptsHelperMyAccount = {
        
        /**
         * Property: _scriptName
         * {String} Relative path of this script.
         */
        _scriptName: "lib/OpenwisMyAccount.js",

        /**
         * Function: _getScriptLocation
         * Return the path to this script.
         *
         * Returns:
         * {String} Path to this script
         */
        _getScriptLocation: function () {
            var scriptLocation = "";            
            var isOL = new RegExp("(^|(.*?\\/))(" + OpenwisScriptsHelperMyAccount._scriptName + ")(\\?|$)");
         
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
            "Openwis/Common/Metadata/Create.js",
            "Openwis/Common/Metadata/Insert.js",
        	"Openwis/Common/Metadata/Report.js",
            "Openwis/Common/Metadata/BatchImport.js",
            "Openwis/Common/Metadata/MonitorCatalog.js",
            "Openwis/Common/Request/Utils.js",
            "Openwis/Common/Dissemination/MailDiffusion.js",
            "Openwis/Common/Dissemination/FTPDiffusion.js",
            "Openwis/Common/Components/DateTimeExtentSelection.js",
            "Openwis/Common/Components/GeographicalExtentSelection.js",
            	
            "Openwis/RequestSubscription/SubSelectionParameters/Helper/Matcher.js",
            "Openwis/RequestSubscription/SubSelectionParameters/Helper/MockSSPanel.js",
            "Openwis/RequestSubscription/SubSelectionParameters/Helper/SSPCatalog.js",
            "Openwis/RequestSubscription/SubSelectionParameters/Helper/SSPToExt.js",
            	
            "Openwis/RequestSubscription/SubSelectionParameters/ScheduleSelection/Schedule.js",
            "Openwis/RequestSubscription/SubSelectionParameters/PeriodSelection/Day.js",
            "Openwis/RequestSubscription/SubSelectionParameters/Cache/Period.js",
            "Openwis/RequestSubscription/SubSelectionParameters/Cache/File.js",
            "Openwis/RequestSubscription/SubSelectionParameters/MultipleSelection/Checkbox.js",
            "Openwis/RequestSubscription/SubSelectionParameters/MultipleSelection/ListBox.js",
            "Openwis/RequestSubscription/SubSelectionParameters/SingleSelection/Radio.js",
            "Openwis/RequestSubscription/SubSelectionParameters/SingleSelection/ComboBox.js",
            "Openwis/RequestSubscription/SubSelectionParameters/SingleSelection/ListBox.js",
            "Openwis/RequestSubscription/SubSelectionParameters/SourceSelection/Source.js",
            	
            "Openwis/RequestSubscription/SubSelectionParameters/SSPStandardProduct.js",
            "Openwis/RequestSubscription/SubSelectionParameters/SSPGlobalProduct.js",
            	
            "Openwis/RequestSubscription/BackUp/BackupSelection.js",
            
            "Openwis/RequestSubscription/DisseminationParameters/Components/Diffusion.js",
            "Openwis/RequestSubscription/DisseminationParameters/Components/MSSFSS.js",
            "Openwis/RequestSubscription/DisseminationParameters/Components/StagingPost.js",
            "Openwis/RequestSubscription/DisseminationParameters/Selection.js",
            "Openwis/RequestSubscription/Summary/Summary.js",
            "Openwis/RequestSubscription/Summary/MSSFSSSummary.js",
            "Openwis/RequestSubscription/Acknowledgement/Acknowledgement.js",
            "Openwis/RequestSubscription/Wizard.js",
            	
            "Openwis/MyAccount/TrackMyRequests/MyAdhocsGridPanel.js",
            "Openwis/MyAccount/TrackMyRequests/TrackMyAdhocs.js",
            "Openwis/MyAccount/TrackMyRequests/MySubscriptionsGridPanel.js",
            "Openwis/MyAccount/TrackMyRequests/TrackMySubscriptions.js",
            "Openwis/MyAccount/TrackMyRequests/MyMSSFSSSubscriptionsGridPanel.js",
            "Openwis/MyAccount/TrackMyRequests/TrackMyMSSFSSSubscriptions.js",
            "Openwis/MyAccount/Browser.js",
            "Openwis/MyAccount/Viewport.js",
            "Openwis/MyAccount/Init.js"
        ); // etc.

        var allScriptTags = new Array(jsfiles.length);
        var host = OpenwisScriptsHelperMyAccount._getScriptLocation() + "lib/";   
        for (var i=0, len=jsfiles.length; i<len; i++) {
            allScriptTags[i] = "<script src='" + host + jsfiles[i] +
                               "'></script>";
        }
        document.write(allScriptTags.join(""));
    }
})();
