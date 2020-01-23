<!DOCTYPE html>
<html lang="en">
	<head>
		<% String title = "Captcha verification";%>
		<%@include file="header-light.jsp" %>
		<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/RegistrationUser/RequestAccount.js"></script>
		<link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-requestAccount.css">

	</head>
	<body>
		<div class="main-content">
			<div class="heading">
				<h1>Captcha verification</h1>
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

			<form action="<%= context.getBaseUrl() %>/loginCaptcha" method="post">
				<div class="spacer"></div>

				<div class="captcha">
					<label for="captcha" class="for-captcha">Type the letters and numbers exactly as they appear in this image:</label>

					<div>
						<img src="<%= context.getBaseUrl() %>/openWisRequestAccountCaptcha" />
					</div>
					<div>
						<input name="jcaptcha" type="text" autocomplete="off" required="required" value="" data-validation="required"/>
					</div>
				</div>

				<div class="button">
					<input id="submit" type="submit" value="Submit"/>
					<img src="<%= context.getBaseUrl() %>/images/openwis/ajax-loader.gif" id="ajax-loader" class="ajax-loader" style="display:none"/>
				</div>
			</form>
		</div>
	</body>
</html>
