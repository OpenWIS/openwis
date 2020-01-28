<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="jeeves.server.context.ServiceContext"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<%
String title = "Login captcha verification";
%>

	<%@include file="header-common.jsp" %>

   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>
      </div>
        <%
        if (request.getParameter("errorMessage") != null) {
        %>
            <script type="text/javascript">
            var Msg ='<%=request.getParameter("errorMessage")%>';
            alert(Msg);
            </script>
        <%
        }
        %>
      <div class="form-container">
            <form action="<%= context.getBaseUrl() %>/loginCaptcha" method="post">
                <div>
                    <label for="captcha" class="for-captcha">Type the letters and numbers exactly as they appear in this image:</label>

                    <div class="captcha captcha-image">
                        <img src="<%= context.getBaseUrl() %>/openWisRequestAccountCaptcha" />
                    </div>
                    <div class="captcha">
                        <input name="jcaptcha" type="text" autocomplete="off" required="required" value="" data-validation="required"/>
                    </div>
                </div>

                <div class="button captcha">
                    <input id="submit" type="submit" value="Submit"/>
                    <img src="<%= context.getBaseUrl() %>/images/openwis/ajax-loader.gif" id="ajax-loader" class="ajax-loader" style="display:none"/>
                </div>
            </form>
        <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-homepage.css">
        <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-login-captcha.css">
      </div>
   </body>
</html>

