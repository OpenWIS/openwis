<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/"
	xmlns:ows="http://www.opengis.net/ows" xmlns:csw="http://www.opengis.net/cat/csw/2.0.2" version="1.0">




	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />



	<xsl:template match="/">
		<doc>


			<xsl:variable name="coverage" select="/csw:Record/ows:BoundingBox" />
			<xsl:variable name="north" select="substring-after($coverage/ows:UpperCorner,' ')" />
			<xsl:variable name="south" select="substring-after($coverage/ows:LowerCorner,' ')" />
			<xsl:variable name="east" select="substring-after($coverage/ows:UpperCorner,' ')" />
			<xsl:variable name="west" select="substring-after($coverage/ows:LowerCorner,' ')" />

			<xsl:for-each select="/csw:Record/dc:identifier">
				<field name="identifier">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="/csw:Record/dct:abstract">
				<field name="abstract">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="/csw:Record/dc:date">
				<field name="createDate">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>


			<xsl:for-each select="/csw:Record/dct:modified">
				<field name="changeDate">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="/csw:Record/dc:format">
				<field name="format">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="/csw:Record/dc:type">
				<field name="type">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="/csw:Record/dc:relation">
				<field name="relation">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="/csw:Record/dct:spatial">
				<field name="spatial">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="/csw:Record/dc:title">
				<field name="_title">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:apply-templates select="/csw:Record/dc:title">
				<xsl:with-param name="name" select="'title'" />
				<xsl:with-param name="token" select="'true'" />
			</xsl:apply-templates>

			<xsl:apply-templates select="/csw:Record/dc:description">
				<xsl:with-param name="name" select="'description'" />
				<xsl:with-param name="token" select="'true'" />
			</xsl:apply-templates>

			<field name="westBL">
				<xsl:value-of select="$west  + 360" />
			</field>
			<field name="eastBL">
				<xsl:value-of select="$east  + 360" />
			</field>
			<field name="southBL">
				<xsl:value-of select="$south + 360" />
			</field>
			<field name="northBL">
				<xsl:value-of select="$north + 360" />
			</field>

			<xsl:for-each select="/csw:Record/dc:subject">
				<xsl:apply-templates select=".">
					<xsl:with-param name="name" select="'keyword'" />
					<xsl:with-param name="store" select="'true'" />



					<xsl:with-param name="token" select="'false'" />
				</xsl:apply-templates>

				<xsl:apply-templates select=".">
					<xsl:with-param name="name" select="'subject'" />
					<xsl:with-param name="store" select="'true'" />
					<xsl:with-param name="token" select="'false'" />
				</xsl:apply-templates>
			</xsl:for-each>

			<field name="any">
					<xsl:apply-templates select="/csw:Record" mode="allText" />
			</field>




			<field name="digital">true</field>

		</doc>
	</xsl:template>




	<xsl:template match="*">
		<xsl:param name="name" select="name(.)" />
		<xsl:param name="store" select="'false'" />
		<xsl:param name="index" select="'true'" />
		<xsl:param name="token" select="'false'" />

		<field name="{$name}">
			<xsl:value-of select="string(.)" />
		</field>
	</xsl:template>




	<xsl:template match="*" mode="allText">
		<xsl:for-each select="@*">
			<xsl:value-of select="concat(string(.),' ')" />
		</xsl:for-each>
		<xsl:choose>
			<xsl:when test="*">
				<xsl:apply-templates select="*" mode="allText" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat(string(.),' ')" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>