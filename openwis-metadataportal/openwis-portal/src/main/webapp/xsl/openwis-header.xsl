<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:include href="geo/utils.xsl"/>
	
	<!--
	main html header
	-->
	<xsl:template name="header">
		
		<!-- title -->
		<title><xsl:value-of select="/root/gui/strings/title"/></title>
		<link href="{/root/gui/url}/favicon.ico" rel="shortcut icon" type="image/x-icon" />
		<link href="{/root/gui/url}/favicon.ico" rel="icon" type="image/x-icon" />

		<!-- Recent updates newsfeed -->
		<link href="{/root/gui/locService}/rss.latest?georss=gml" rel="alternate" type="application/rss+xml" title="GeoNetwork opensource GeoRSS | {/root/gui/strings/recentAdditions}" />
		<link href="{/root/gui/locService}/portal.opensearch" rel="search" type="application/opensearchdescription+xml">
		<xsl:attribute name="title"><xsl:value-of select="//site/name"/> (GeoNetwork)</xsl:attribute>
		</link>

		<!-- meta tags -->
		<xsl:copy-of select="/root/gui/strings/header_meta/meta"/>
		<META HTTP-EQUIV="Pragma"  CONTENT="no-cache"/>
		<META HTTP-EQUIV="Expires" CONTENT="-1"/>
		
		<!-- stylesheet -->
		<link rel="stylesheet" type="text/css" href="{/root/gui/url}/css/openwis.css"/>
		
		<xsl:apply-templates mode="css" select="/"/>
		
		<!-- JS -->
		<xsl:call-template name="jsHeader"/>
		
	</xsl:template>
</xsl:stylesheet>
