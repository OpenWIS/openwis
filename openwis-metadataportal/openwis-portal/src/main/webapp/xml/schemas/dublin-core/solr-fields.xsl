<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/" version="1.0">

	
	
	
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	
	
	
	<xsl:template match="/">
		<doc>
	
			
			<xsl:variable name="coverage" select="/simpledc/dc:coverage"/>
			<xsl:variable name="n" select="substring-after($coverage,'North ')"/>
			<xsl:variable name="north" select="substring-before($n,',')"/>
			<xsl:variable name="s" select="substring-after($coverage,'South ')"/>
			<xsl:variable name="south" select="substring-before($s,',')"/>
			<xsl:variable name="e" select="substring-after($coverage,'East ')"/>
			<xsl:variable name="east" select="substring-before($e,',')"/>
			<xsl:variable name="w" select="substring-after($coverage,'West ')"/>
			<xsl:variable name="west" select="substring-before($w,'. ')"/>
			<xsl:variable name="p" select="substring-after($coverage,'(')"/>
			<xsl:variable name="place" select="substring-before($p,')')"/>
			
			<xsl:for-each select="/simpledc/dc:identifier">
				<field name="identifier"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			<xsl:for-each select="/simpledc/dct:abstract">
				<field name="abstract"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>

			<xsl:for-each select="/simpledc/dc:date">
			  <field name="createDate"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>

			
			<xsl:for-each select="/simpledc/dct:modified">
				<field name="changeDate"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			<xsl:for-each select="/simpledc/dc:format">
				<field name="format"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			<xsl:for-each select="/simpledc/dc:type">
				<field name="type"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			<xsl:for-each select="/simpledc/dc:relation">
				<field name="relation"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			<xsl:for-each select="/simpledc/dct:spatial">
				<field name="spatial"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			
	
			<xsl:for-each select="/simpledc/dc:title">
                <field name="_title"><xsl:value-of select="string(.)"/></field>
			</xsl:for-each>
	
			<xsl:apply-templates select="/simpledc/dc:title">
				<xsl:with-param name="name" select="'title'"/>
				<xsl:with-param name="token" select="'true'"/>
			</xsl:apply-templates>
	
			<xsl:apply-templates select="/simpledc/dc:description">
				<xsl:with-param name="name" select="'description'"/>
				<xsl:with-param name="token" select="'true'"/>
			</xsl:apply-templates>
			
			<field name="westBL"><xsl:value-of select="$west  + 360"/></field>
			<field name="eastBL"><xsl:value-of select="$east  + 360"/></field>
			<field name="southBL"><xsl:value-of select="$south + 360"/></field>
			<field name="northBL"><xsl:value-of select="$north + 360"/></field>
			
			<field name="keyword"><xsl:value-of select="$place"/></field>
	
			
			<xsl:apply-templates select="/simpledc/dc:subject">
				<xsl:with-param name="name" select="'keyword'"/>
				<xsl:with-param name="store" select="'true'"/>
	
				
	
				<xsl:with-param name="token" select="'false'"/> 
			</xsl:apply-templates>
	
			<field name="any">
					<xsl:apply-templates select="/simpledc" mode="allText"/>
			</field>
	
			
			
			
			<field name="digital">true</field>
				
		</doc>
	</xsl:template>
	
	
	
	
	<xsl:template match="*">
		<xsl:param name="name" select="name(.)"/>
		<xsl:param name="store" select="'false'"/>
		<xsl:param name="index" select="'true'"/>
		<xsl:param name="token" select="'false'"/>
		
	   <field name="{$name}"><xsl:value-of select="string(.)"/></field>
	</xsl:template>
	
	
	
	
	<xsl:template match="*" mode="allText">
		<xsl:for-each select="@*"><xsl:value-of select="concat(string(.),' ')"/></xsl:for-each>
		<xsl:choose>
			<xsl:when test="*"><xsl:apply-templates select="*" mode="allText"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="concat(string(.),' ')"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>