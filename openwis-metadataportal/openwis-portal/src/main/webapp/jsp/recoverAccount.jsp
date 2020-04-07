<!DOCTYPE html>
<html lang="en">
	<head>
		<% String title = "Request Account";%>
		<%@include file="header-light.jsp" %>
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/RegistrationUser/RequestAccount.js"></script>
		<link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-requestAccount.css">
		
	    <script type="text/javascript">
            var onloadCallback = function() {
                grecaptcha.render('google-captcha', {
                'sitekey' : '<%=OpenwisMetadataPortalConfig.getString(ConfigurationConstants.GOOGLE_RECAPTCHA_SITE_KEY)%>'
                });
            };
          window.addEventListener("load", function() {
                 document.getElementById("submit").onclick = function() {
                     var att = document.createAttribute("disabled");
                     document.getElementById("submit").setAttributeNode(att);
                 };
          });

        </script>
	</head>
	<body>
		<div class="main-content">
			<div class="heading">
				<h1>Recover a lost account</h1>
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
			
			<!--  <form action="<%= context.getBaseUrl() %>/srv/en/user.accountRequest.submit" method="post">  -->
			<form action="<%= context.getBaseUrl() %>/openWisRecoverAccount" method="post">
				<div>
					<label for="email">Email Address:</label>
					<input name="email" id="email" type="email" required="required" data-validation="isEmail"/>
				</div>
				<div class="spacer"></div>
				<div class="captcha">
				    <div id="google-captcha"></div>
                    <br>
				</div>
				<div class="button">
					<input id="submit" type="submit" value="Submit"/>
					<img src="<%= context.getBaseUrl() %>/images/openwis/ajax-loader.gif" id="ajax-loader" class="ajax-loader" style="display:none"/>
				</div>
			</form>
            <script src="https://www.google.com/recaptcha/api.js?onload=onloadCallback&render=explicit"
                async defer>
            </script>
		</div>
	</body>
</html>
