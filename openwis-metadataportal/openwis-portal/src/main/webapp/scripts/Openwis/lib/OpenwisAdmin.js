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
    window.OpenwisScriptsHelperAdmin = {
        
        /**
         * Property: _scriptName
         * {String} Relative path of this script.
         */
        _scriptName: "lib/OpenwisAdmin.js",

        /**
         * Function: _getScriptLocation
         * Return the path to this script.
         *
         * Returns:
         * {String} Path to this script
         */
        _getScriptLocation: function () {
            var scriptLocation = "";            
            var isOL = new RegExp("(^|(.*?\\/))(" + OpenwisScriptsHelperAdmin._scriptName + ")(\\?|$)");
         
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
        	"Openwis/Common/Metadata/BatchImport.js",
        	"Openwis/Common/Metadata/MonitorCatalog.js",
        	"Openwis/Common/Metadata/Report.js",
        	"Openwis/Admin/MetaInfo/Manage.js",
        	"Openwis/Admin/DataPolicy/Manage.js",
        	"Openwis/Admin/DataPolicy/All.js",
        	"Openwis/Admin/Harvesting/Harvester/Geonetwork20.js",
        	"Openwis/Admin/Harvesting/Harvester/FileSystem.js",
        	"Openwis/Admin/Harvesting/Harvester/Oaipmh.js",
        	"Openwis/Admin/Harvesting/Harvester/CSW.js",
        	"Openwis/Admin/Harvesting/Harvester/WebDav.js",
        	"Openwis/Admin/Harvesting/All.js",
        	"Openwis/Admin/Synchro/Manage.js",
        	"Openwis/Admin/Synchro/All.js",
        	"Openwis/Admin/Thesauri/Manage.js",
        	"Openwis/Admin/Thesauri/ViewEdit.js",
        	"Openwis/Admin/Thesauri/EditElement.js",
        	"Openwis/Admin/Category/Manage.js",
        	"Openwis/Admin/Category/All.js",
        	"Openwis/Admin/Category/Edit.js",
        	"Openwis/Admin/Group/Manage.js",
        	"Openwis/Admin/Group/All.js",
        	
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
            	
            "Openwis/RequestSubscription/DisseminationParameters/Components/Diffusion.js",
            "Openwis/RequestSubscription/DisseminationParameters/Components/MSSFSS.js",
            "Openwis/RequestSubscription/DisseminationParameters/Components/StagingPost.js",
            "Openwis/RequestSubscription/DisseminationParameters/Selection.js",
            "Openwis/RequestSubscription/Summary/Summary.js",
            "Openwis/RequestSubscription/Summary/MSSFSSSummary.js",
            "Openwis/RequestSubscription/Acknowledgement/Acknowledgement.js",
            "Openwis/RequestSubscription/Wizard.js",
        	
        	"Openwis/Common/Dissemination/MailDiffusion.js",
        	"Openwis/Common/Dissemination/FTPDiffusion.js",
        	"Openwis/Admin/User/Privileges.js",
        	"Openwis/Admin/User/ImportUser.js",
        	"Openwis/Admin/User/Manage.js",
        	"Openwis/Admin/User/All.js",
        	"Openwis/Admin/SSOManagement/SSOManagement.js",
        	"Openwis/Common/Request/Utils.js",
        	"Openwis/Admin/Statistics/RecentEvents.js",
        	"Openwis/Admin/Statistics/GlobalReports.js",
        	"Openwis/Admin/Statistics/CacheStatistics.js",
		"Openwis/Admin/Statistics/UserAlarms.js",
        	"Openwis/Admin/DataService/MonitorCurrentRequests.js",
        	"Openwis/Admin/DataService/BrowseContent.js",
        	"Openwis/Admin/DataService/CacheConfiguration.js",
        	"Openwis/Admin/DataService/FilterInputDialog.js",
        	"Openwis/Admin/DataService/ReplicationFilterDialog.js",
        	"Openwis/Admin/DataService/RequestsStatistics.js",
        	"Openwis/Admin/DataService/Blacklist.js",
        	"Openwis/Admin/DataService/EditBlacklist.js",
        	"Openwis/Admin/Browser.js",
        	"Openwis/Admin/Viewport.js",
        	"Openwis/Admin/Init.js",
        	"Openwis/Admin/System/SystemConfiguration.js",
        	"Openwis/Admin/Availability/DeploymentAvailabilityUtils.js",
        	"Openwis/Admin/Availability/DeploymentAvailability.js",
        	"Openwis/Admin/Availability/LocalAvailability.js",
        	"Openwis/Admin/Availability/RemoteAvailability.js",
        	"Openwis/Admin/Availability/Statistics.js",
        	"Openwis/Admin/Index/Manage.js",
        	"Openwis/Admin/CatalogStatistics/All.js",
        	"Openwis/Admin/Template/All.js"
        ); // etc.

        var allScriptTags = new Array(jsfiles.length);
        var host = OpenwisScriptsHelperAdmin._getScriptLocation() + "lib/";   
        for (var i=0, len=jsfiles.length; i<len; i++) {
            allScriptTags[i] = "<script src='" + host + jsfiles[i] +
                               "'></script>";
        }
        document.write(allScriptTags.join(""));
    }
})();
