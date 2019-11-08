<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:dc="http://purl.org/dc/elements/1.1/" xmlns:dct="http://purl.org/dc/terms/">

	<xsl:template match="simpledc">
		<importInfo>
			<uuid>
				<xsl:value-of select="dc:identifier"/>
			</uuid>
			<dateStamp>
				<xsl:value-of select="dct:modified"/>
			</dateStamp>
		</importInfo>

	</xsl:template>

</xsl:stylesheet>
