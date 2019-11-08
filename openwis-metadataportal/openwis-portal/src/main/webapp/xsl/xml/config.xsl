<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- ============================================================================================= -->
	<!-- === This stylesheet is used by the xml.config.get service -->
	<!-- ============================================================================================= -->

	<xsl:template match="/system">
		<xsl:variable name="site"       select="children/site/children"/>
		<xsl:variable name="server"     select="children/server/children"/>
		<xsl:variable name="intranet"   select="children/intranet/children"/>
		<xsl:variable name="selectionmanager"      select="children/selectionmanager/children"/>
		<xsl:variable name="indexoptimizer" select="children/indexoptimizer/children"/>
		<xsl:variable name="indexoptimizerAt" select="$indexoptimizer/at/children"/>
		<xsl:variable name="indexoptimizerInterval" select="$indexoptimizer/interval/children"/>
		<xsl:variable name="z3950"      select="children/z3950/children"/>
		<xsl:variable name="oai"      	select="children/oai/children"/>
		<xsl:variable name="xlinkResolver"        select="children/xlinkResolver/children"/>
		<xsl:variable name="downloadservice"      select="children/downloadservice/children"/>
		<xsl:variable name="proxy"      select="children/proxy/children"/>
		<xsl:variable name="feedback"   select="children/feedback/children"/>
		<xsl:variable name="removedMd"  select="children/removedMetadata/children"/>
		<xsl:variable name="ldap"       select="children/ldap/children"/>
		<xsl:variable name="ldapLogin"  select="$ldap/login/children"/>
		<xsl:variable name="ldapDisNam" select="$ldap/distinguishedNames/children"/>
		<xsl:variable name="ldapUsrAtt" select="$ldap/userAttribs/children"/>
		<xsl:variable name="userSelfRegistration" select="children/userSelfRegistration/children"/>
		<xsl:variable name="clickablehyperlinks" select="children/clickablehyperlinks/children"/>		
		<xsl:variable name="localrating" select="children/localrating/children"/>		
		<xsl:variable name="shib"       select="children/shib/children"/>
		<xsl:variable name="shibAttrib" select="$shib/attrib/children"/>
		<xsl:variable name="csw"        select="children/csw/children"/>
		<xsl:variable name="cswInfo"    select="$csw/contactInfo/children"/>
		<xsl:variable name="cswPhone"   select="$cswInfo/phone/children"/>
		<xsl:variable name="cswAddress" select="$cswInfo/address/children"/>
        <xsl:variable name="inspire"    select="children/inspire/children"/>
		<xsl:variable name="cache"      select="children/cache/children"/>

		<config>
			<site>
				<name><xsl:value-of select="$site/name/value"/></name>
				<organization><xsl:value-of select="$site/organization/value"/></organization>
			</site>

			<server>
				<host><xsl:value-of select="$server/host/value"/></host>
				<port><xsl:value-of select="$server/port/value"/></port>
			</server>

			<intranet>
				<network><xsl:value-of select="$intranet/network/value"/></network>
				<netmask><xsl:value-of select="$intranet/netmask/value"/></netmask>
			</intranet>

			<selectionmanager>
				<maxrecords><xsl:value-of select="$selectionmanager/maxrecords/value"/></maxrecords>
			</selectionmanager>

			<indexoptimizer>
				<enable><xsl:value-of select="$indexoptimizer/enable/value"/></enable>
				<at>
					<hour><xsl:value-of select="$indexoptimizerAt/hour/value"/></hour>
					<min><xsl:value-of select="$indexoptimizerAt/min/value"/></min>
					<sec><xsl:value-of select="$indexoptimizerAt/sec/value"/></sec>
				</at>
				<interval>
					<day><xsl:value-of select="$indexoptimizerInterval/day/value"/></day>
					<hour><xsl:value-of select="$indexoptimizerInterval/hour/value"/></hour>
					<min><xsl:value-of select="$indexoptimizerInterval/min/value"/></min>
				</interval>
			</indexoptimizer>

			<z3950>
				<enable><xsl:value-of select="$z3950/enable/value"/></enable>
				<port><xsl:value-of select="$z3950/port/value"/></port>
			</z3950>

			<oai>
				<mdmode><xsl:value-of select="$oai/mdmode/value"/></mdmode>
				<tokentimeout><xsl:value-of select="$oai/tokentimeout/value"/></tokentimeout>
				<cachesize><xsl:value-of select="$oai/cachesize/value"/></cachesize>
			</oai>
			
			<xlinkResolver>
				<enable><xsl:value-of select="$xlinkResolver/enable/value"/></enable>
			</xlinkResolver>

			<downloadservice>
				<leave><xsl:value-of select="$downloadservice/leave/value"/></leave>
				<simple><xsl:value-of select="$downloadservice/simple/value"/></simple>
				<withdisclaimer><xsl:value-of select="$downloadservice/withdisclaimer/value"/></withdisclaimer>
			</downloadservice>

			<userSelfRegistration>
				<enable><xsl:value-of select="$userSelfRegistration/enable/value"/></enable>
			</userSelfRegistration>

			<csw>
				<enable><xsl:value-of select="$csw/enable/value"/></enable>
				<contactId><xsl:value-of select="$csw/contactId/value"/></contactId>
				<individualName><xsl:value-of select="$csw/individualName/value"/></individualName>
				<positionName><xsl:value-of select="$csw/positionName/value"/></positionName>
				<contactInfo>
					<phone>
						<voice><xsl:value-of select="$cswPhone/voice/value"/></voice>
						<facsimile><xsl:value-of select="$cswPhone/facsimile/value"/></facsimile>
					</phone>
					<address>
						<deliveryPoint><xsl:value-of select="$cswAddress/deliveryPoint/value"/></deliveryPoint>
						<city><xsl:value-of select="$cswAddress/city/value"/></city>
						<administrativeArea><xsl:value-of select="$cswAddress/administrativeArea/value"/></administrativeArea>
						<postalCode><xsl:value-of select="$cswAddress/postalCode/value"/></postalCode>
						<country><xsl:value-of select="$cswAddress/country/value"/></country>
						<email><xsl:value-of select="$cswAddress/email/value"/></email>
					</address>
					<hoursOfService><xsl:value-of select="$cswInfo/hoursOfService/value"/></hoursOfService>
					<contactInstructions><xsl:value-of select="$cswInfo/contactInstructions/value"/></contactInstructions>
				</contactInfo>
				<role><xsl:value-of select="$csw/role/value"/></role>
				<title><xsl:value-of select="$csw/title/value"/></title>
				<abstract><xsl:value-of select="$csw/abstract/value"/></abstract>
				<fees><xsl:value-of select="$csw/fees/value"/></fees>
				<accessConstraints><xsl:value-of select="$csw/accessConstraints/value"/></accessConstraints>
                <metadataPublic><xsl:value-of select="$csw/metadataPublic/value"/></metadataPublic>			
            </csw>
			
			<clickablehyperlinks>
				<enable><xsl:value-of select="$clickablehyperlinks/enable/value"/></enable>
			</clickablehyperlinks>			
			
			<localrating>
				<enable><xsl:value-of select="$localrating/enable/value"/></enable>
			</localrating>			

            <inspire>
                <enable><xsl:value-of select="$inspire/enable/value"/></enable>
            </inspire>

            <cache>
                <enable><xsl:value-of select="$cache/enable/value"/></enable>
            </cache>

			<proxy>
				<use><xsl:value-of select="$proxy/use/value"/></use>
				<host><xsl:value-of select="$proxy/host/value"/></host>
				<port><xsl:value-of select="$proxy/port/value"/></port>
				<username><xsl:value-of select="$proxy/username/value"/></username>
				<password><xsl:value-of select="$proxy/password/value"/></password>
			</proxy>

			<feedback>
				<email><xsl:value-of select="$feedback/email/value"/></email>
				<mailServer>
					<host><xsl:value-of select="$feedback/mailServer/children/host/value"/></host>
					<port><xsl:value-of select="$feedback/mailServer/children/port/value"/></port>
				</mailServer>
			</feedback>

			<removedMetadata>
				<dir><xsl:value-of select="$removedMd/dir/value"/></dir>
			</removedMetadata>
			
			<ldap>
				<use><xsl:value-of select="$ldap/use/value"/></use>
				<host><xsl:value-of select="$ldap/host/value"/></host>
				<port><xsl:value-of select="$ldap/port/value"/></port>
				<defaultProfile><xsl:value-of select="$ldap/defaultProfile/value"/></defaultProfile>				
				<distinguishedNames>
					<base><xsl:value-of select="$ldapDisNam/base/value"/></base>
					<users><xsl:value-of select="$ldapDisNam/users/value"/></users>
				</distinguishedNames>
				<userAttribs>
					<name><xsl:value-of select="$ldapUsrAtt/name/value"/></name>
					<profile><xsl:value-of select="$ldapUsrAtt/profile/value"/></profile>
				</userAttribs>
			</ldap>

			<shib>
				<use><xsl:value-of select="$shib/use"/></use>
				<path><xsl:value-of select="$shib/path"/></path>
				<attrib>
					<username><xsl:value-of select="$shibAttrib/username"/></username>
					<surname><xsl:value-of select="$shibAttrib/surname"/></surname>
					<firstname><xsl:value-of select="$shibAttrib/firstname"/></firstname>
					<profile><xsl:value-of select="$shibAttrib/profile"/></profile>
				</attrib>
			</shib>			

		</config>
	</xsl:template>

	<!-- ============================================================================================= -->

</xsl:stylesheet>
