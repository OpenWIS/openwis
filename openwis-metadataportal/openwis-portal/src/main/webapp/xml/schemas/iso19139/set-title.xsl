<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet   xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
						xmlns:gco="http://www.isotc211.org/2005/gco"
						xmlns:gmd="http://www.isotc211.org/2005/gmd">
   
    <xsl:param name="title"/>

	<!-- ================================================================= --><!--
	
	<xsl:template match="/root">
		 <xsl:apply-templates select="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation"/>
	</xsl:template>

	--><!-- ================================================================= -->
	
	<xsl:template match="gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title">
        <xsl:copy>
            <gco:CharacterString><xsl:value-of select="$title"/></gco:CharacterString>
        </xsl:copy>
	</xsl:template>
	
	<!-- ================================================================= -->
	<xsl:template match="@*|node()">
		 <xsl:copy>
			  <xsl:apply-templates select="@*|node()"/>
		 </xsl:copy>
	</xsl:template>

	<!-- ================================================================= -->

</xsl:stylesheet>
