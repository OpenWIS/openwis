<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
 <xsl:output method="text" indent="no" media-type="application/json"/>

 <xsl:variable name="siteURL"
  select="concat('http://',/root/gui/env/server/host,':',/root/gui/env/server/port, /root/gui/locService)"/>

 <xsl:template match="/">
  ["<xsl:value-of select="/root/request/q"/>"
  <!-- Add Completions (required) -->
  , 
    [<xsl:for-each
     select="/root/items/item"> "<xsl:value-of select="@term"/>" <xsl:if test="position()!=last()"
      >,</xsl:if>
    </xsl:for-each> ]
  <!-- Add Descriptions (not required) 
    @freq is the number of occurences of this term in the index (could be more than the number of results)
  , 
    [<xsl:for-each select="/root/items/item"> "<xsl:value-of select="@freq"/>" <xsl:if test="position()!=last()">,</xsl:if>
    </xsl:for-each> ]
  -->
  <!-- Query URLs (not required)
  , 
    [<xsl:for-each select="/root/items/item"> "<xsl:value-of
      select="concat($siteURL, '/rss.search?any=', @term)"/>" <xsl:if test="position()!=last()"
      >,</xsl:if>
    </xsl:for-each> ] 
  -->
  ] 
  </xsl:template>
</xsl:stylesheet>
