<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="main.xsl"/>
	<xsl:include href="validation.xsl"/>

	<!--
	page content
	-->
	<xsl:template name="content">
		<xsl:call-template name="formLayout">
			<xsl:with-param name="title">My account</xsl:with-param>
			<xsl:with-param name="content">
		
				<h1 class="error"><xsl:value-of select="/root/gui/error/message"/></h1>
				<p/>
				<xsl:choose>
					<xsl:when test="/root/error/class='XSDValidationErrorEx'">
						<xsl:call-template name="xsd"/>
					</xsl:when>
					<xsl:when test="/root/error/class='SchematronValidationErrorEx'">
						<xsl:call-template name="schematron"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="/root/error/class"/> : <xsl:value-of select="/root/error/message"/>
					</xsl:otherwise>
				</xsl:choose>
				<p/>
				<button class="content" onclick="if (history.length>1) history.back(); else window.close();"><xsl:value-of select="/root/gui/strings/backToPreviousPage"/></button>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
