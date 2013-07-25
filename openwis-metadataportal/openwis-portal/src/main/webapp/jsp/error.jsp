<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="jeeves.server.context.ServiceContext"%>

<%
String title = "OpenWIS Error";
String pageName = null;
String message = String.valueOf(request.getAttribute("errorMsg"));
String needLogout = null;
if (request.getAttribute("needLogout") != null) {
   needLogout = String.valueOf(request.getAttribute("needLogout"));
}


%>    
<%@include file="html-content.jsp" %>
