<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String title = "OpenWIS Administration";

List<String> availableServices = (List<String>) request.getAttribute("availableServices");
if (availableServices == null) {
   availableServices = new ArrayList<String>();
}
%>

	<%@include file="header-common.jsp" %>

	<%@include file="header-admin.jsp" %>
        
   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>
         
      </div>
   </body>
</html>