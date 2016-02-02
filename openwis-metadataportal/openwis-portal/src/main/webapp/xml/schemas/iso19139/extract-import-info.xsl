<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
	xmlns:gco="http://www.isotc211.org/2005/gco" xmlns:gmd="http://www.isotc211.org/2005/gmd">
	
	<xsl:template match="gmd:MD_Metadata">
		<importInfo>
			<uuid>
				<xsl:value-of select="gmd:fileIdentifier/gco:CharacterString"/>
			</uuid>
			<dateStamp>
				<xsl:value-of select="gmd:dateStamp/gco:DateTime | gmd:dateStamp/gco:Date"/>
			</dateStamp>
		</importInfo>
	</xsl:template>
	
</xsl:stylesheet>
