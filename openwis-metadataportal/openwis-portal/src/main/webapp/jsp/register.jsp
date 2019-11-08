<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="jeeves.server.context.ServiceContext"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String title = "OpenWIS Register User";
%>

	<%@include file="header-common.jsp" %>
	
   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>
        <% if (selfRegistrationEnabled) { %>
		<link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-homepage.css">
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Utils/Captcha.js"></script>
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/RegistrationUser/RegistrationUserSuccessful.js"></script>
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/RegistrationUser/RegistrationUser.js"></script>
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/RegistrationUser/Viewport.js"></script>
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/RegistrationUser/Init.js"></script>
        <% } %>
      </div>
   </body>
</html>