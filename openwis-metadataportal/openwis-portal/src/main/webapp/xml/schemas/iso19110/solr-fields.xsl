<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gfc="http://www.isotc211.org/2005/gfc" xmlns:gmd="http://www.isotc211.org/2005/gmd"
	xmlns:gco="http://www.isotc211.org/2005/gco" version="1.0">




	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />



	<xsl:template match="/">
		<doc>



			<xsl:apply-templates select="/gfc:FC_FeatureCatalogue/gfc:name/gco:CharacterString">
				<xsl:with-param name="name" select="'title'" />
				<xsl:with-param name="token" select="'true'" />
			</xsl:apply-templates>


			<field name="_title">
				<xsl:value-of select="string(/gfc:FC_FeatureCatalogue/gfc:name/gco:CharacterString)" />
			</field>




			<xsl:apply-templates select="/gfc:FC_FeatureCatalogue/gfc:scope/gco:CharacterString">
				<xsl:with-param name="name" select="'abstract'" />
				<xsl:with-param name="token" select="'true'" />
			</xsl:apply-templates>



			<xsl:for-each select="/gfc:FC_FeatureCatalogue/gfc:versionDate/gco:Date">
				<field name="revisionDate">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="/gfc:FC_FeatureCatalogue/gfc:language/gmd:LanguageCode">
				<field name="language">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:apply-templates select="/gfc:FC_FeatureCatalogue/gfc:name/gco:CharacterString">
				<xsl:with-param name="name" select="'fileId'" />
				<xsl:with-param name="token" select="'false'" />
			</xsl:apply-templates>



			<xsl:for-each select="/gfc:FC_FeatureCatalogue/gfc:producer/gmd:CI_ResponsibleParty">

				<field name="orgName">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<field name="any">
					<xsl:apply-templates select="/gfc:FC_FeatureCatalogue" mode="allText" />
			</field>



			<field name="type">model</field>


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




	<xsl:template match="*[./*/@value]">
		<xsl:param name="name" select="name(.)" />

		<field name="{$name}">
			<xsl:value-of select="*/@value" />
		</field>
	</xsl:template>




	<xsl:template match="*" mode="latLon">
		<xsl:param name="name" select="name(.)" />

		<field name="{$name}">
			<xsl:value-of select="string(.) + 360" />
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