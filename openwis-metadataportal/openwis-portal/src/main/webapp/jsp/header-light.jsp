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
request.getSession().setAttribute("context", context);
String portalType = (String) request.getAttribute("portalType");
String locService = context.getBaseUrl() + "/srv/" + context.getLanguage();

//Gt self registration enablement from DB
GeonetContext  gc = (GeonetContext) context.getHandlerContext(Geonet.CONTEXT_NAME);
SettingManager sm = gc.getSettingManager();
boolean selfRegistrationEnabled = sm.getValueAsBool("system/userSelfRegistration/enable");


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
	<link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-custom.css">
  
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Lang/Lang.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Lang/Locales/<%= context.getLanguage() %>.js"></script>
	
	
    
