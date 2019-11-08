<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output
		omit-xml-declaration="yes" 
		method="html" 
		doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
		doctype-system="http://www.w3.org/TR/html4/loose.dtd"
		indent="yes"
		encoding="UTF-8" />
	
	<xsl:include href="openwis-header.xsl"/>
	<xsl:include href="openwis-banner.xsl"/>
	<xsl:include href="utils.xsl"/>
	
	<!--
	main page
	-->
	<xsl:template match="/">
		<html>
			<head>
			
				<xsl:call-template name="header"/>
				<xsl:apply-templates mode="script" select="/"/>
				
			</head>
			<body onload="init()">
				<!-- banner -->
				<div id="header">
					<xsl:call-template name="banner"/>
				</div>
				
				<div id="content_container" style="display:none">
					<xsl:call-template name="content"/>
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template mode="script" match="/"/>
	<xsl:template mode="css" match="/"/>
	
</xsl:stylesheet>
