<%@page import="jeeves.server.context.ServiceContext"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="jeeves.server.context.ServiceContext"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<%@include file="header-common.jsp" %>
   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>

		<link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-homepage.css">
        <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-html-content.css">
		    
		<%
		if (devMode) {
		%>
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Viewport.js"></script>
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Init.js"></script>
		<%
		} else {
		%>  
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Viewport.js"></script>
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Init.js"></script>
		<%
		}
		%>  

      </div>
      <div id="contentElement">
      <%
      if (pageName != null) {
         String contentPage = "/loc/" +context.getLanguage() + "/xml/" + pageName;
      %>
        <jsp:include page="<%= contentPage %>"></jsp:include>
      <% } else { %>
        <div class="errorMsg">ERROR: <%= StringEscapeUtils.escapeHtml(message) %>
        	<br><br><br>
	        <%
	        if (needLogout != null) {
	        %>
	        	<form name="buttonReturnToHome" action="<%= context.getBaseUrl() %>/openWisLogout" method="post" align="center">
					<input type="submit" value="Go to Home Page" name="ReturnToHome"/>
				</form>
	        <%
	        } else {
	        %>
	            <form name="buttonReturnToHome" action="<%= context.getBaseUrl() %>/" method="post">
					<input type="submit" value="Go to Home Page" name="ReturnToHome"/>
				</form>
	        <%
	        }
	        %>
        </div>
        </div>
      <% } %>
      </div>
   </body>
</html>