<%@page import="org.openwis.metadataportal.common.configuration.ConfigurationConstants"%>
<%@page import="org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig"%>
<%@page import="jeeves.server.context.ServiceContext"%>
<%@page import="org.fao.geonet.GeonetContext"%>
<%@page import="org.fao.geonet.kernel.setting.SettingManager"%>
<%@page import="org.fao.geonet.constants.Geonet"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
ServiceContext context = (ServiceContext) request.getAttribute("context");
String portalType = (String) request.getAttribute("portalType");
String locService = context.getBaseUrl() + "/srv/" + context.getLanguage();

//Gt self registration enablement from DB
GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
SettingManager sm = gc.getSettingManager();
boolean selfRegistrationEnabled = sm.getValueAsBool("system/userSelfRegistration/enable");

// dev mode 
boolean devMode = context.isDebug();
%>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><%= title %></title>

	<link href="<%= context.getBaseUrl() %>/favicon.ico" rel="shortcut icon" type="image/x-icon">
	<link href="<%= context.getBaseUrl() %>/favicon.ico" rel="icon" type="image/x-icon">
	
	<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
	<META HTTP-EQUIV="Expires" CONTENT="-1">
	
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/scripts/ext/resources/css/ext-all.css">
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/scripts/geoext/resources/css/geoext-all-debug.css">
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis.css">
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-common.css">
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-metadataeditor.css">
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/scripts/ext-ux/css/fileuploadfield.css">
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/scripts/ext-ux/css/MultiSelect.css">
        
<%
if (devMode) {
%>  
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/prototype.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext/ext-all-debug.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext-ux/GroupTab.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext-ux/GroupTabPanel.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext-ux/CheckColumn.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext-ux/ItemSelector.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext-ux/MultiSelect.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext-ux/FileUploadField.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/openlayers/lib/OpenLayers.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/geoext/GeoExt.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Lang/Lang.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Lang/Locales/<%= context.getLanguage() %>.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/OpenwisCommon.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/map/Ext.ux/form/DateTime.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/form_check.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/geo/extentMap.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/Metadata/ViewerEditor.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/app.CRSSelectionPanel.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/app.KeywordSelectionPanel.js"></script><!-- To be updated -->
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/app.LinkedMetadataSelectionPanel.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/app.SearchField.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/metadata-show.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/metadata-editor.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/simpletooltip.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/tooltip-manager.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/tooltip.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/editor/csw.SearchTools.js"></script>
    
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Openwis.js"></script>
<%
} else {
%>  
	<!-- TODO Compress all these files in one. (Openwis.Libs.js) -->    
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/gn.libs.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext/adapter/ext/ext-base.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/ext/ext-all.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/OpenLayers.js"></script>	
    <script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/gn.geo.libs.js"></script>
    <script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/ext.libs.js"></script>
    
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Lang/Lang.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Lang/Locales/<%= context.getLanguage() %>.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/OpenwisCommon.js"></script>
		
    <script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/gn.libs.scriptaculous.js"></script>
    <script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/gn.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/lib/gn.editor.js"></script>
    
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Openwis.js"></script>
<%
}
%>  
	<script type="text/javascript" language="JavaScript">
	    var configOptions = {};
	    
	    Ext.QuickTips.init();

	    configOptions.locService = "<%= locService %>";
	    configOptions.url = "<%= context.getBaseUrl() %>";
	    
	    var g_userConnected = <%= context.getUserSession() != null && context.getUserSession().getUserId() != null %>;
	    
	    var selfRegistrationEnabled = <%= selfRegistrationEnabled %>;
	    
	    var Env = new Object();
        Env.locService=  "<%= locService %>";
        Env.url       = "<%= context.getBaseUrl() %>";
        Env.lang      = "<%= context.getLanguage() %>";
        // TODO check if needed.
        // Env.host = "http://<xsl:value-of select="/root/gui/env/server/host"/>:<xsl:value-of select="/root/gui/env/server/port"/>";
        // Env.locUrl    = "<xsl:value-of select="/root/gui/locUrl"/>";
        // Env.proxy     = "<xsl:value-of select="/root/gui/config/proxy-url"/>";
        
        var translations = {};
        
        OpenLayers.ImgPath = "<%= context.getBaseUrl() %>/scripts/openlayers/img/";
        
        // Local Centre name (found in openwis-metadataportal.properties)
        var localCentreName = "<%= OpenwisMetadataPortalConfig.getString(ConfigurationConstants.DEPLOY_NAME) %>";

        // track page loaded status for openlayers map creation
        var pageLoaded;
        window.onload = function() {
            pageLoaded = true;
        }

        // Fix scrolling issue with IE7
        Ext.Viewport.override({
            setAutoScroll: function() {
                if (this.rendered && this.autoScroll) {
                    var el = this.body || this.el;
                    if (el) {
                        el.setOverflow('auto');
			            // Following line required to fix autoScroll
			            el.dom.style.position = 'relative';
			        }
                }
            }
        });
        
	</script>

    <script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/core/kernel/kernel.js"></script>
