<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gmd="http://www.isotc211.org/2005/gmd" xmlns:gco="http://www.isotc211.org/2005/gco"
	xmlns:gml="http://www.opengis.net/gml/3.2" xmlns:srv="http://www.isotc211.org/2005/srv" xmlns:gmx="http://www.isotc211.org/2005/gmx" 
	xmlns:xlink="http://www.w3.org/1999/xlink" version="2.0">

	
	<xsl:include href="../iso19139/convert/functions.xsl" />




	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />




	<xsl:param name="inspire">true</xsl:param>


	<xsl:variable name="useDateAsTemporalExtent" select="false()" />



	<xsl:template match="/">
		<doc>
			<xsl:apply-templates select="gmd:MD_Metadata" mode="metadata" />
			
			<!-- isGlobal -->
			<xsl:for-each select="//gmd:MD_DataIdentification/gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword[gco:CharacterString/text() = 'GlobalExchange']">">
					<field name="_isGlobal">true</field>
			</xsl:for-each>
			<xsl:for-each select="//gmd:MD_LegalConstraints/*">
				<xsl:if 
					test="(name(.)='gmd:useLimitation' or name(.)='gmd:otherConstraints') and 
						(contains(string(./gco:CharacterString),'WMO Essential')
	                       or contains(string(./gco:CharacterString),'WMOEssential')
	                       or contains(string(./gco:CharacterString),'WMOAdditional') 
						or contains(string(./gco:CharacterString),'WMO Additional'))">
					<field name="_isGlobal">true</field>
				</xsl:if>
			</xsl:for-each>
			
			<!-- OpenWIS distribution / online resources -->
			<xsl:for-each select="//gmd:CI_OnlineResource">
				
				<xsl:variable name="linkage" select="normalize-space(gmd:linkage/gmd:URL)" />

				<xsl:if test="string($linkage)!=''">
					<xsl:choose>
						<xsl:when test="contains($linkage,'/retrieve/subscribe/')">
							<field name="_linkOpenwisSubscribeUrl"><xsl:value-of select="$linkage"/></field>
						</xsl:when>
						<xsl:when test="contains($linkage,'/retrieve/request/')">
							<field name="_linkOpenwisRequestUrl"><xsl:value-of select="$linkage"/></field>
						</xsl:when>
						<xsl:otherwise>
							<xsl:variable name="actionName">
								<!--Conditionally instantiate a value to be assigned to the variable -->
								<xsl:variable name="nameStr" select="normalize-space(gmd:name/gco:CharacterString)"/>
								<xsl:variable name="ciOnlineFunctionCode" select="normalize-space(gmd:function/gmd:CI_OnLineFunctionCode)"/>
								<xsl:choose>
									<xsl:when test="string($nameStr)!=''">
										<xsl:value-of select="string($nameStr)"/>
									</xsl:when>
									<xsl:when test="string($ciOnlineFunctionCode)!=''">
										<xsl:value-of select="string($ciOnlineFunctionCode)"/>
									</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="$linkage"/>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:variable>
							
							<xsl:variable name="tooltip" select="normalize-space(gmd:description/gco:CharacterString)" />
							
							<field name="_linkOtherActions"><xsl:value-of select="$actionName"/>@@@<xsl:value-of select="$linkage"/>@@@<xsl:value-of select="$tooltip"/></field>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>
			</xsl:for-each>
		</doc>
	</xsl:template>





	<xsl:template match="*" mode="metadata">





		<xsl:for-each select="gmd:identificationInfo//gmd:MD_DataIdentification|gmd:identificationInfo/srv:SV_ServiceIdentification">

			<xsl:for-each select="gmd:citation/gmd:CI_Citation">
				<xsl:for-each select="gmd:identifier/gmd:MD_Identifier/gmd:code/gco:CharacterString">
					<field name="identifier">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="gmd:title/gco:CharacterString">
					<field name="_title">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="gmd:alternateTitle/gco:CharacterString">
					<field name="altTitle">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='revision']/gmd:date">
					<field name="revisionDate">
						<xsl:value-of select="string(gco:Date|gco:DateTime)" />
					</field>
					<xsl:if test="$useDateAsTemporalExtent">
						<field name="tempExtentBegin">
							<xsl:value-of select="string(gco:Date|gco:DateTime)" />
						</field>
					</xsl:if>
				</xsl:for-each>

				<xsl:for-each select="gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='creation']/gmd:date">
					<field name="createDate">
						<xsl:value-of select="string(gco:Date|gco:DateTime)" />
					</field>
					<xsl:if test="$useDateAsTemporalExtent">
						<field name="tempExtentBegin">
							<xsl:value-of select="string(gco:Date|gco:DateTime)" />
						</field>
					</xsl:if>
				</xsl:for-each>

				<xsl:for-each select="gmd:date/gmd:CI_Date[gmd:dateType/gmd:CI_DateTypeCode/@codeListValue='publication']/gmd:date">
					<field name="publicationDate">
						<xsl:value-of select="string(gco:Date|gco:DateTime)" />
					</field>
					<xsl:if test="$useDateAsTemporalExtent">
						<field name="tempExtentBegin">
							<xsl:value-of select="string(gco:Date|gco:DateTime)" />
						</field>
					</xsl:if>
				</xsl:for-each>



				<xsl:for-each select="gmd:presentationForm">
					<xsl:if test="contains(gmd:CI_PresentationFormCode/@codeListValue, 'Digital')">
						<field name="digital">true</field>
					</xsl:if>

					<xsl:if test="contains(gmd:CI_PresentationFormCode/@codeListValue, 'Hardcopy')">
						<field name="paper">true</field>
					</xsl:if>
				</xsl:for-each>
			</xsl:for-each>



			<xsl:for-each select="gmd:abstract/gco:CharacterString">
				<field name="abstract">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="*/gmd:EX_Extent">

				<xsl:for-each
					select="gmd:geographicElement/gmd:EX_GeographicDescription/gmd:geographicIdentifier/gmd:MD_Identifier/gmd:code/gco:CharacterString">
					<field name="geoDescCode">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="gmd:temporalElement/gmd:EX_TemporalExtent/gmd:extent">
					<xsl:for-each select="gml:TimePeriod">
						<field name="tempExtentBegin">
							<xsl:value-of select="gml:beginPosition|gml:begin/gml:TimeInstant/gml:timePosition" />
						</field>
						<field name="tempExtentEnd">
							<xsl:value-of select="gml:endPosition|gml:end/gml:TimeInstant/gml:timePosition" />
						</field>
					</xsl:for-each>

				</xsl:for-each>
			</xsl:for-each>



			<xsl:for-each select="*/gmd:MD_Keywords">
				<xsl:for-each select="gmd:keyword/gco:CharacterString|gmd:keyword/gmd:PT_FreeText/gmd:textGroup/gmd:LocalisedCharacterString">
					<xsl:variable name="keywordLower" select="lower-case(.)" />
					<field name="keyword">
						<xsl:value-of select="string(.)" />
					</field>
					<field name="subject">
						<xsl:value-of select="string(.)" />
					</field>

					<xsl:if test="string-length(.) &gt; 0">
						<xsl:if
							test="$keywordLower='coordinate reference systems' or $keywordLower='geographical grid systems' or $keywordLower='geographical names' or $keywordLower='administrative units' or $keywordLower='addresses' or $keywordLower='cadastral parcels' or $keywordLower='transport networks' or $keywordLower='hydrography' or $keywordLower='protected sites'">
							<field name="inspiretheme"><xsl:value-of select="string(.)" /></field>
							<field name="inspireannex">i</field>
							<field name="inspirecat">true</field>
						</xsl:if>

						<xsl:if test="$keywordLower='elevation' or $keywordLower='land cover' or $keywordLower='orthoimagery' or $keywordLower='geology'">
							<field name="inspiretheme"><xsl:value-of select="string(.)" /></field>
							<field name="inspireannex">ii</field>
							<field name="inspirecat">true</field>
						</xsl:if>

						<xsl:if
							test="$keywordLower='statistical units' or $keywordLower='buildings' or $keywordLower='soil' or $keywordLower='land use' or $keywordLower='human health and safety' or $keywordLower='utility and government services' or $keywordLower='environmental monitoring facilities' or $keywordLower='production and industrial facilities' or $keywordLower='agricultural and aquaculture facilities' or $keywordLower='population distribution - demography' or $keywordLower='area management/restriction/regulation zones and reporting units' or $keywordLower='natural risk zones' or $keywordLower='atmospheric conditions' or $keywordLower='meteorological geographical features' or $keywordLower='oceanographic geographical features' or $keywordLower='sea regions' or $keywordLower='bio-geographical regions' or $keywordLower='habitats and biotopes' or $keywordLower='species distribution' or $keywordLower='energy resources' or $keywordLower='mineral resources'">
							<field name="inspiretheme"><xsl:value-of select="string(.)" /></field>
							<field name="inspireannex">iii</field>
							<field name="inspirecat">true</field>
						</xsl:if>
					</xsl:if>
				</xsl:for-each>

				<xsl:for-each select="gmd:type/gmd:MD_KeywordTypeCode/@codeListValue">
					<field name="keywordType">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
			</xsl:for-each>



			<xsl:for-each select="gmd:pointOfContact/gmd:CI_ResponsibleParty/gmd:organisationName/gco:CharacterString">
				<field name="orgName">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:choose>
				<xsl:when test="gmd:resourceConstraints/gmd:MD_SecurityConstraints">
					<field name="secConstr">true</field>
				</xsl:when>
				<xsl:otherwise>
					<field name="secConstr">false</field>
				</xsl:otherwise>
			</xsl:choose>



			<xsl:for-each
				select="gmd:topicCategory/gmd:MD_TopicCategoryCode|         gmd:descriptiveKeywords/gmd:MD_Keywords/gmd:keyword/gco:CharacterString">
				<field name="subject">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="gmd:topicCategory/gmd:MD_TopicCategoryCode">
				<field name="topicCat">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="gmd:language/gco:CharacterString">
				<field name="datasetLang">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="gmd:spatialResolution/gmd:MD_Resolution">
				<xsl:for-each select="gmd:equivalentScale/gmd:MD_RepresentativeFraction/gmd:denominator/gco:Integer">
					<field name="denominator">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="gmd:distance/gco:Distance">
					<field name="distanceVal">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="gmd:distance/gco:Distance/@uom">
					<field name="distanceUom">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
			</xsl:for-each>



			<xsl:for-each select="gmd:resourceConstraints">
				<xsl:for-each select="//gmd:accessConstraints/gmd:MD_RestrictionCode/@codeListValue">
					<field name="accessConstr">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
				<xsl:for-each select="//gmd:otherConstraints/gco:CharacterString">
					<field name="otherConstr">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
				<xsl:for-each select="//gmd:classification/gmd:MD_ClassificationCode/@codeListValue">
					<field name="classif">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
				<xsl:for-each select="//gmd:useLimitation/gco:CharacterString">
					<field name="conditionApplyingToAccessAndUse">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
			</xsl:for-each>




			<xsl:for-each select="srv:serviceType/gco:LocalName">
				<field name="serviceType">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="srv:serviceTypeVersion/gco:CharacterString">
				<field name="serviceTypeVersion">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="//srv:SV_OperationMetadata/srv:operationName/gco:CharacterString">
				<field name="operation">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="srv:operatesOn/@uuidref">
				<field name="operatesOn">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>
			<xsl:for-each select="srv:operatesOn/@xlink:href">
				<field name="operatesOn">
					<xsl:value-of select="substring-after(string(.),'uuid=')" />
				</field>
			</xsl:for-each>
			<xsl:for-each select="srv:coupledResource">
				<xsl:for-each select="srv:SV_CoupledResource/srv:identifier/gco:CharacterString">
					<field name="operatesOnIdentifier">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>

				<xsl:for-each select="srv:SV_CoupledResource/srv:operationName/gco:CharacterString">
					<field name="operatesOnName">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
			</xsl:for-each>

			<xsl:for-each select="//srv:SV_CouplingType/srv:code/@codeListValue">
				<field name="couplingType">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

		</xsl:for-each>




		<xsl:for-each select="gmd:distributionInfo/gmd:MD_Distribution">
			<xsl:for-each select="gmd:distributionFormat/gmd:MD_Format/gmd:name/gco:CharacterString">
				<field name="format">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>



			<xsl:for-each select="gmd:transferOptions/gmd:MD_DigitalTransferOptions/gmd:onLine/gmd:CI_OnlineResource/gmd:protocol/gco:CharacterString">
				<xsl:variable name="download_check">
					<xsl:text>&amp;fname=&amp;access</xsl:text>
				</xsl:variable>
				<xsl:variable name="linkage" select="../../gmd:linkage/gmd:URL" />


				<xsl:if test="string($linkage)!='' and not(contains($linkage,$download_check))">
					<field name="protocol">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:if>

				<xsl:variable name="mimetype" select="../../gmd:name/gmx:MimeFileType/@type" />
				<xsl:if test="normalize-space($mimetype)!=''">
					<field name="mimetype">
						<xsl:value-of select="$mimetype" />
					</field>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>



		<xsl:for-each select="gmd:dataQualityInfo/*/gmd:report/*/gmd:result">

			<xsl:for-each select="//gmd:pass/gco:Boolean">
				<field name="degree">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="//gmd:specification/*/gmd:title/gco:CharacterString">
				<field name="specificationTitle">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="//gmd:specification/*/gmd:date/*/gmd:date/gco:DateTime">
				<field name="specificationDate">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>

			<xsl:for-each select="//gmd:specification/*/gmd:date/*/gmd:dateType/gmd:CI_DateTypeCode/@codeListValue">
				<field name="specificationDateType">
					<xsl:value-of select="string(.)" />
				</field>
			</xsl:for-each>
		</xsl:for-each>
		<xsl:for-each select="gmd:dataQualityInfo/*/gmd:lineage/*/gmd:statement/gco:CharacterString">
			<field name="lineage">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>




		<xsl:choose>
			<xsl:when test="gmd:hierarchyLevel">
				<xsl:for-each select="gmd:hierarchyLevel/gmd:MD_ScopeCode/@codeListValue">
					<field name="type">
						<xsl:value-of select="string(.)" />
					</field>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<field name="type">dataset</field>
			</xsl:otherwise>
		</xsl:choose>



		<xsl:for-each select="gmd:hierarchyLevelName/gco:CharacterString">
			<field name="levelName">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>



		<xsl:for-each select="gmd:language/gco:CharacterString">
			<field name="language">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>



		<xsl:for-each select="gmd:fileIdentifier/gco:CharacterString">
			<field name="fileId">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>



		<xsl:for-each select="gmd:parentIdentifier/gco:CharacterString">
			<field name="parentUuid">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>



		<xsl:for-each select="gmd:dateStamp/gco:DateTime">
			<field name="changeDate">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>



		<xsl:for-each select="gmd:contact/*/gmd:organisationName/gco:CharacterString">
			<field name="metadataPOC">
				<xsl:value-of select="string(.)" />
			</field>
		</xsl:for-each>




		<xsl:for-each select="gmd:referenceSystemInfo/gmd:MD_ReferenceSystem">
			<xsl:for-each select="gmd:referenceSystemIdentifier/gmd:RS_Identifier">
				<xsl:variable name="crs" select="concat(string(gmd:codeSpace/gco:CharacterString),'::',string(gmd:code/gco:CharacterString))" />

				<xsl:if test="$crs != '::'">
					<field name="crs">
						<xsl:value-of select="$crs" />
					</field>
				</xsl:if>
			</xsl:for-each>
		</xsl:for-each>

		<xsl:for-each select="gmd:referenceSystemInfo/gmd:MD_ReferenceSystem">
			<xsl:for-each select="gmd:referenceSystemIdentifier/gmd:RS_Identifier">
				<field name="authority">
					<xsl:value-of select="string(gmd:codeSpace/gco:CharacterString)" />
				</field>
				<field name="crsCode">
					<xsl:value-of select="string(gmd:code/gco:CharacterString)" />
				</field>
				<field name="crsVersion">
					<xsl:value-of select="string(gmd:version/gco:CharacterString)" />
				</field>
			</xsl:for-each>
		</xsl:for-each>




		<field name="any">
			<xsl:value-of select="normalize-space(string(.))" />
			<xsl:text> </xsl:text>
			<xsl:for-each select="//@codeListValue">
				<xsl:value-of select="concat(., ' ')" />
			</xsl:for-each>
		</field>



	</xsl:template>




	<xsl:template match="*[./*/@codeListValue]" mode="codeList">
		<xsl:param name="name" select="name(.)" />

		<field name="{$name}">
			<xsl:value-of select="*/@codeListValue" />
		</field>
	</xsl:template>



	<xsl:template match="*" mode="codeList">
		<xsl:apply-templates select="*" mode="codeList" />
	</xsl:template>



</xsl:stylesheet>