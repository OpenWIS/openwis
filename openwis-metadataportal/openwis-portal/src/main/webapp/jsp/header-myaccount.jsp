<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/scripts/ext-ux/css/GroupTab.css">
<%
if (devMode) {
%>
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/OpenwisMyAccount.js"></script>
<%
} else {
%>  
	<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/OpenwisMyAccount.js"></script>
<%
}
%>  
    <script type="text/javascript">
		var accessibleServices = new Array();
		<% for (String service : availableServices) { %>
        accessibleServices.push("<%= service %>");
		<% } %>
    </script>