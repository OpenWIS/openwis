<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/">
		<fields>
			<!-- XXX -->
			<xsl:comment>Generated Elements</xsl:comment>
			<xsl:apply-templates select="//Field"/>

		</fields>
	</xsl:template>

	<xsl:template match="Field">
		<xsl:call-template name="Field" />
	</xsl:template>

	<xsl:template name="Field">
		<xsl:element name="field">
			<xsl:attribute name="name">
			  	<xsl:value-of select="@name" />
			</xsl:attribute>
			<!-- Define type -->
			<xsl:attribute name="type">
			<xsl:choose>
				<xsl:when test="@token='true'">text</xsl:when>
				<xsl:when test="contains(@name,'Date')">date</xsl:when>
				<xsl:otherwise>string</xsl:otherwise>
			</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="indexed">
				<xsl:value-of select="@index" />
			</xsl:attribute>
			<xsl:attribute name="stored">
				<xsl:value-of select="@store" />
			</xsl:attribute>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>