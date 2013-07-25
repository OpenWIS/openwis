<%--
	Display this page when an error occurs.
--%>

<%@ page language="java" import="java.io.*" isErrorPage="true"%>

<link rel="stylesheet" href="scripts/ext/resources/css/ext-all.css"
	type="text/css" />
<link rel="stylesheet" href="css/geonetwork.css" type="text/css" />
<link rel="stylesheet" href="css/openwis.css" type="text/css" />

<html class="ext-strict x-viewport">

<head>
<title>Error</title>
</head>

<body>


<div id="main-panel" class="headerContentCls">

<div id="header">
<table width="100%">
	<tbody>
		<tr class="banner">
			<td class="banner"><img align="top" alt="World picture"
				src="images/openwis/header-left.jpg"></td>
			<td align="right" class="banner"><img align="top"
				alt="GeoNetwork opensource logo"
				src="images/openwis/header-right.gif"></td>
		</tr>
		<tr class="banner" id="banner-bottom">
			<td width="380px" align="right" class="banner-login"></td>
		</tr>
	</tbody>
</table>
</div>

<div style="" id="content_container" align="center">
<h1>Login Error</h1>
<br>
<h2><%=request.getParameter("message")%></h2>
<br>
<button type="button" onclick="window.location='openWisLogout'">Return
to the home page.</button>
</div>


</div>

</body>

</html>

