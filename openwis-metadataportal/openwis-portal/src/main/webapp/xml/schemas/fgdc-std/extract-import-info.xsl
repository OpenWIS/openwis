<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

   <xsl:template match="metadata">
      <!-- Transform FGDC date in ISO date -->
      <xsl:variable name='datetime' select='normalize-space(metainfo/metd)' />
      <!-- Pull the pieces apart -->
      <xsl:variable name='year' select='substring( $datetime, 1 , 4 )' />
      <xsl:variable name='month' select='substring( $datetime, 5 , 2 )' />
      <xsl:variable name='day' select='substring( $datetime, 7 , 2 )' />

      <importInfo>
         <uuid>
            <xsl:value-of select="normalize-space(idinfo/datsetid)" />
         </uuid>
         <dateStamp>
            <xsl:value-of select="concat($year, '-', $month, '-', $day, 'T00:00:00.000')" />
         </dateStamp>
      </importInfo>
   </xsl:template>

</xsl:stylesheet>
