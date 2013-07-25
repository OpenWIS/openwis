<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no" />
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<!-- Document -->
	<xsl:template name="CreateDocument" match="Document">
		<xsl:element name="doc">
			<xsl:apply-templates />
		</xsl:element>
	</xsl:template>


	<!-- Fields -->
	<xsl:template name="CreateFields" match="Field">
		<field name="{@name}"><xsl:choose><xsl:when test="starts-with(@string,'{')"><xsl:element name="xsl:value-of"><xsl:attribute name="select"><xsl:value-of select="substring-after(substring-before(@string,'}'),'{') " /></xsl:attribute></xsl:element></xsl:when><xsl:when test="@string"><xsl:value-of select="@string" /></xsl:when><xsl:otherwise><xsl:copy-of select="child::node()"></xsl:copy-of> </xsl:otherwise></xsl:choose></field>
	</xsl:template>

	<!-- Others -->
	<xsl:template match="*" name="all">
		<xsl:copy>
			<!-- Attributes -->
			<xsl:for-each select="@*">
				<xsl:attribute name="{name(.)}"><xsl:value-of select="." /></xsl:attribute>
			</xsl:for-each>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>