<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-homepage.css">
<%
if (devMode) {
%>  
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/geonetwork.js"></script>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/gn_search.js"></script>
	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/OpenwisHomePage.js"></script>
<%
} else {
%>  	
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/OpenwisHomePage.js"></script>
<%
}
%>  

<script type="text/javascript" language="JavaScript">

        // Parameter to show Advanced Search first
        var showAdvancedSearchFirst = false;
        
</script>
