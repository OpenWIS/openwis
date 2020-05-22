<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String title = "ASMC | WIS Portal";
%>

	<%@include file="header-common.jsp" %>

	<%@include file="header-homepage.jsp" %>
    
    <%@include file="header-remotesearch.jsp" %>
    <% if ("user".equals(portalType)) { %>
	    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-custom-user.css">
    <%} else {%>
	    <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-custom.css">
    <%  } %>
   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>
         
      </div>
    <% if ("user".equals(portalType)) { %>
        <%@include file="footer-dss.jsp" %>
    <%} else {%>
        <%@include file="footer-common.jsp" %>
    <%  } %>
   </body>
</html>
