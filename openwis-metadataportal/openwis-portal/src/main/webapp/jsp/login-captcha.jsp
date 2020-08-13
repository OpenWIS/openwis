<%@page import="jeeves.server.context.ServiceContext"%>
<%@page import="org.openwis.metadataportal.common.configuration.ConfigurationConstants"%>
<%@page import="org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="jeeves.server.context.ServiceContext"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript">
      var onloadCallback = function() {
        grecaptcha.render('html_element', {
          'sitekey' : '<%=OpenwisMetadataPortalConfig.getString(ConfigurationConstants.GOOGLE_RECAPTCHA_SITE_KEY)%>'
        });
      };
</script>

<%
String title = "ASEAN | WIS Portal";
%>

	<%@include file="header-common.jsp" %>

   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>
      </div>
      <div class="main">
        <div class="form-container">
            <form action="<%= context.getBaseUrl() %>/loginCaptcha" method="POST">
                  <div id="html_element"></div>
                <br>
                <% String errorMessage = (String) request.getServletContext().getAttribute("errorMessage");
                if (errorMessage != null) { %>
                    <div class="error">
                        <p><%=errorMessage%></p>
                    </div>
                <% } %>
                <div class="submitContainer">
                    <input id="submitButton" type="submit" value="Submit">
                </div>
            </form>
            <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit"
                 async defer>
            </script>
        </div>
            <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-homepage.css">
            <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-login-captcha.css">
        <% if ("user".equals(portalType)) { %>
            <%@include file="footer-dss.jsp"%>
        <% } else { %>
            <%@include file="footer-common.jsp"%>
            <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-footer-user.css">
        <% } %>
      </div>
   </body>
</html>

