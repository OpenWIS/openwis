<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:geonet="http://www.fao.org/geonetwork" exclude-result-prefixes="xsl geonet">

	<!--xsl:variable name="lang" select="/root/gui/language"/-->

	<!--xsl:template match="/"-->
	<xsl:template name="advanced_search_fields">
		<form name="advsearch" id="advsearch" onsubmit="javascript:runAdvancedSearch();" action="">
			<!-- <div style="border-bottom: 1px solid;"> -->
			<div id="bloc_search">

				<div id="onglets_search">
					<a class="titre_lien_search" onClick="showSimpleSearch();"
						style="cursor:pointer; padding-right:10px;">
						<xsl:value-of select="/root/gui/strings/hideAdvancedOptions"/>
					</a>
					<span class="titre_pipe"> | </span>
					<span class="titre_search">
						<xsl:value-of select="/root/gui/strings/extended"/>
					</span>
				</div>

				<xsl:comment>ADVANCED SEARCH</xsl:comment>

				<xsl:comment>ADV SEARCH: WHAT?</xsl:comment>
				<xsl:call-template name="adv_what"/>

				<xsl:comment>ADV SEARCH: WHERE?</xsl:comment>
				<xsl:call-template name="adv_where"/>

				<xsl:comment>ADV SEARCH: WHEN?</xsl:comment>
				<xsl:call-template name="adv_when"/>

				<xsl:comment>ADV SEARCH: INSPIRE</xsl:comment>
				<xsl:if test="/root/gui/env/inspire/enable = 'true'">
					<xsl:call-template name="adv_inspire"/>
				</xsl:if>

				<!-- Search button -->
				<!-- OpenWIS
			<div>		
				<table class="advsearchfields" width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
						<td style="background: url({/root/gui/url}/images/arrow-bg.gif) repeat-x;" height="29px" width="50%">
						</td>
						<td style="padding:0px; margin:0px;" width="36px">
							<img width="36px" style="padding:0px; margin:0px;"  src="{/root/gui/url}/images/arrow-right.gif" alt="" />
						</td>
						<td style="padding:0px; margin:0px;" width="13px">
							<img width="13px" style="padding:0px; margin:0px;"  src="{/root/gui/url}/images/search-left.gif" alt="" />
						</td>
						<td align="center" style="background: url({/root/gui/url}/images/search-bg.gif) repeat-x; width: auto; white-space: nowrap; padding-bottom: 8px; vertical-align: bottom; cursor:hand;  cursor:pointer;" onclick="runAdvancedSearch();" >
							<font color="#FFFFFF"><strong><xsl:value-of select="/root/gui/strings/search"/></strong></font>
						</td>
						<td style="padding:0px; margin:0px;" width="12px">
							<img width="12px" style="padding:0px; margin:0px;"  src="{/root/gui/url}/images/search-right.gif" alt="" />
						</td>
					</tr>
				</table>		
			</div>
			-->

				<!-- RESTRICT TO + -->
				<div style="padding-top:5px;" align="left">
					<a onclick="showFields('restrictions.img','restrictions.table')"
						style="cursor:pointer;cursor:hand;padding-right:10px;">
						<img id="restrictions.img" src="{/root/gui/url}/images/openwis/plus.gif"
							alt=""/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="/root/gui/strings/restrictTo"/>
					</a>
				</div>

				<!-- Restrictions -->
				<div id="restrictions.table" style="display:none; margin-top:5px; margin-bottom:5px">
					<!-- Source -->
					<div class="row">
						<span class="labelField">
							<xsl:value-of select="/root/gui/strings/porCatInfoTab"/>
						</span>

						<select class="content" name="siteId" id="siteId">
							<option value="">
								<xsl:if test="/root/gui/searchDefaults/siteId=''">
									<xsl:attribute name="selected"/>
								</xsl:if>
								<xsl:value-of select="/root/gui/strings/any"/>
							</option>
							<xsl:for-each select="/root/gui/sources/record">
								<!--
								<xsl:sort order="ascending" select="name"/>
							-->
								<xsl:variable name="source" select="siteid/text()"/>
								<xsl:variable name="sourceName" select="name/text()"/>
								<option value="{$source}">
									<xsl:if test="$source=/root/gui/searchDefaults/siteId">
										<xsl:attribute name="selected"/>
									</xsl:if>
									<xsl:value-of select="$sourceName"/>
								</option>
							</xsl:for-each>
						</select>
					</div>

					<!-- Group -->
					<xsl:if test="string(/root/gui/session/userId)!=''">
						<div class="row">
							<span class="labelField">
								<xsl:value-of select="/root/gui/strings/group"/>
							</span>

							<select class="content" name="group" id="group">
								<option value="">
									<xsl:if test="/root/gui/searchDefaults/group=''">
										<xsl:attribute name="selected"/>
									</xsl:if>
									<xsl:value-of select="/root/gui/strings/any"/>
								</option>
								<xsl:for-each select="/root/gui/groups/record">
									<xsl:sort order="ascending" select="name"/>
									<option value="{id}">
										<!-- after a search, many groups are defined in 
									searchDefaults (FIXME ?) and the last group in group list
									was selected by default even if none was
									used in last search. Only set selected one when only one is define in searchDefaults. -->
										<xsl:if
											test="id=/root/gui/searchDefaults/group and count(/root/gui/searchDefaults/group)=1">
											<xsl:attribute name="selected"/>
										</xsl:if>
										<xsl:value-of select="name"/>
									</option>
								</xsl:for-each>
							</select>
						</div>
					</xsl:if>

					<!-- Template -->
					<xsl:if
						test="string(/root/gui/session/userId)!='' and /root/gui/services/service[@name='metadata.edit']">
						<div class="row">
							<span class="labelField">
								<xsl:value-of select="/root/gui/strings/kind"/>
							</span>

							<select class="content" id="template" name="template" size="1">
								<option value="n">
									<xsl:if test="/root/gui/searchDefaults/template='n'">
										<xsl:attribute name="selected">true</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="/root/gui/strings/metadata"/>
								</option>
								<option value="y">
									<xsl:if test="/root/gui/searchDefaults/template='y'">
										<xsl:attribute name="selected">true</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="/root/gui/strings/template"/>
								</option>
								<!-- <option value="s">
								<xsl:if test="/root/gui/searchDefaults/template='s'">
									<xsl:attribute name="selected">true</xsl:attribute>
								</xsl:if>
								<xsl:value-of select="/root/gui/strings/subtemplate"/>
							</option> -->
							</select>
						</div>
					</xsl:if>

					<!-- Category -->
					<xsl:if test="/root/gui/config/category/admin">
						<div class="row">
							<span class="labelField">
								<xsl:value-of select="/root/gui/strings/category"/>
							</span>

							<select class="content" name="category" id="category">
								<option value="">
									<xsl:if test="/root/gui/searchDefaults/category=''">
										<xsl:attribute name="selected"/>
									</xsl:if>
									<xsl:value-of select="/root/gui/strings/any"/>
								</option>

								<xsl:for-each select="/root/gui/categories/record">
									<xsl:sort select="label/child::*[name() = $lang]"
										order="ascending"/>

									<option value="{name}">
										<xsl:if test="name = /root/gui/searchDefaults/category">
											<xsl:attribute name="selected"/>
										</xsl:if>
										<xsl:value-of select="label/child::*[name() = $lang]"/>
									</option>
								</xsl:for-each>
							</select>
						</div>
					</xsl:if>
				</div>

				<div style="padding-top:5px;" align="left">
					<a onclick="showFields('advoptions.img','advoptions.table')"
						style="cursor:pointer;cursor:hand;padding-right:10px;">
						<img id="advoptions.img" src="{/root/gui/url}/images/openwis/plus.gif"
							alt=""/>
						<xsl:text> </xsl:text>
						<xsl:value-of select="/root/gui/strings/options"/>
					</a>
				</div>

				<!-- Options panel in advanced search -->
				<div id="advoptions.table" style="display:none; margin-top:5px; margin-bottom:5px">

					<!-- sort by - - - - - - - - - - - - - - - - - - - - -->
					<div class="row">
						<span class="labelField">
							<xsl:value-of select="/root/gui/strings/sortBy"/>
						</span>
						<select id="sortBy" size="1" class="content"
							onChange="$('sortBy_simple').value = this.options[this.selectedIndex].value; if (this.options[this.selectedIndex].value=='title') $('sortOrder').value = 'reverse'; else $('sortOrder').value = ''">
							<xsl:for-each select="/root/gui/strings/sortByType">
								<option value="{@id}">
									<xsl:if test="@id = /root/gui/searchDefaults/sortBy">
										<xsl:attribute name="selected">selected</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="."/>
								</option>
							</xsl:for-each>
						</select>
						<input type="hidden" name="sortOrder" id="sortOrder"/>
					</div>

					<!-- hits per page - - - - - - - - - - - - - - - - - - -->
					<div class="row">
						<span class="labelField">
							<xsl:value-of select="/root/gui/strings/hitsPerPage"/>
						</span>
						<select class="content" id="hitsPerPage" name="hitsPerPage"
							onchange="$('hitsPerPage_simple').value = this.options[this.selectedIndex].value">
							<!-- onchange="profileSelected()" -->
							<xsl:for-each select="/root/gui/strings/hitsPerPageChoice">
								<option>
									<xsl:if
										test="string(@value)=string(/root/gui/searchDefaults/hitsPerPage)">
										<xsl:attribute name="selected">selected</xsl:attribute>
									</xsl:if>
									<xsl:attribute name="value">
										<xsl:value-of select="@value"/>
									</xsl:attribute>
									<xsl:value-of select="."/>
								</option>
							</xsl:for-each>
						</select>
					</div>

					<!-- output - - - - - - - - - - - - - - - - - - - - - - -->
					<div class="row">
						<span class="labelField">
							<xsl:value-of select="/root/gui/strings/output"/>
						</span>

						<select id="output" size="1" class="content"
							onchange="$('output_simple').value = this.options[this.selectedIndex].value">
							<xsl:for-each select="/root/gui/strings/outputType">
								<option value="{@id}">
									<xsl:if test="@id = /root/gui/searchDefaults/output">
										<xsl:attribute name="selected">selected</xsl:attribute>
									</xsl:if>
									<xsl:value-of select="."/>
								</option>
							</xsl:for-each>
						</select>
					</div>
				</div>

				<div id="btns_search">
					<div id="btn_reset" class="btn_taille_fixe">
						<a onClick="resetAdvancedSearch();"
							style="cursor:pointer; padding-right:10px; padding-left:10px;">
							<img id="options.img"
								src="{/root/gui/url}/images/openwis/fleche_precedent.png" alt=""/>
							<xsl:value-of select="/root/gui/strings/reset"/>
						</a>
					</div>
					<div id="btn_search" class="btn_taille_fixe" onclick="runSimpleSearch();">
						<b>
							<xsl:value-of select="/root/gui/strings/search"/>
							<img id="options.img"
								src="{/root/gui/url}/images/openwis/fleche_suivant.png" alt=""/>
						</b>
					</div>
				</div>

				<!-- Links to Reset fields, Advanced Search and Options panel -->
				<!--
			<div style="padding-left:10px;padding-top:5px;" align="right">
				<a onClick="resetAdvancedSearch();" style="cursor:pointer; padding-right:10px; padding-left:10px;"><xsl:value-of select="/root/gui/strings/reset"/></a>

				<a onClick="showSimpleSearch();" style="cursor:pointer; padding-right:10px;"><xsl:value-of select="/root/gui/strings/hideAdvancedOptions"/></a>				
			</div>

			<div style="padding-left:10px;padding-top:5px;" align="right">
				<a onclick="showFields('restrictions.img','restrictions.table')" style="cursor:pointer;cursor:hand;padding-right:10px;">
					<img id="restrictions.img" src="{/root/gui/url}/images/plus.gif" alt="" />
					<xsl:text> </xsl:text>	
					<xsl:value-of select="/root/gui/strings/restrictTo"/>
				</a>
				
				<a onclick="showFields('advoptions.img','advoptions.table')" style="cursor:pointer;cursor:hand;padding-right:10px;">
						<img id="advoptions.img" src="{/root/gui/url}/images/plus.gif" alt="" />
						<xsl:text> </xsl:text>	
						<xsl:value-of select="/root/gui/strings/options"/>
				</a>
			</div>
			-->
			</div>
		</form>
	</xsl:template>

	<!-- ============================================================
        INSPIRE
    ======================================= ===================== -->
	<xsl:template name="adv_inspire">
		<h1 style="margin-top:5px;margin-bottom:5px">
			<a href="#" onclick="toggleInspire()" style="margin-right:2px">
				<img id="i_inspire" src="{/root/gui/url}/images/openwis/plus.gif" alt=""/>
			</a>
			<xsl:value-of select="/root/gui/strings/inspire/what/l1"/>
		</h1>

		<!-- INSPIRE search elements -->
		<div id="inspiresearchfields" style="display:none">
			<div>
				<!-- style="float:left;"-->
				<div style="margin-bottom: 10px">
					<!-- div row-->
					<input type="checkbox" id="inspire" name="inspire"/>
					<!--Alleen INSPIRE metadata-->
					<xsl:value-of select="/root/gui/strings/inspire/what/l3"/>
				</div>

				<!-- div row-->
				<!--div class="row">
                <span class="labelField"><xsl:value-of select="/root/gui/strings/rtitle"/></span>
                <input type="text" class="content" style="width:200px; !important" id="title" name="title" value=""/>
            </div-->

				<div class="row">
					<!-- div row-->
					<span class="labelField">
						<xsl:value-of select="/root/gui/strings/inspire/what/l6"/>
					</span>
					<select id="inspireannex" name="inspireannex" class="content"
						style="width:200px; !important" onchange="inspireAnnexChanged(this.value)">
						<option value="" selected="selected"/>
						<option value="I"><xsl:value-of select="/root/gui/strings/inspire/what/l6"/>
							I</option>
						<option value="II"><xsl:value-of select="/root/gui/strings/inspire/what/l6"
							/> II</option>
						<option value="III"><xsl:value-of select="/root/gui/strings/inspire/what/l6"
							/> III</option>
					</select>
				</div>

				<div class="row">
					<!-- div row-->
					<span class="labelField">
						<!--Brontype-->
						<xsl:value-of select="/root/gui/strings/inspire/what/l7"/>
					</span>
					<select id="inspirebrontype" class="content" name="inspirebrontype"
						style="width:200px; !important"
						onchange="inspireBrontypeChanged(this.value)">
						<option value="" selected="selected"/>
						<option value="dataset">
							<!--Datasets en dataset series-->
							<xsl:value-of select="/root/gui/strings/inspire/what/l9"/>
						</option>
						<option value="service">
							<!--Services-->
							<xsl:value-of select="/root/gui/strings/inspire/what/l10"/>
						</option>
					</select>
				</div>

				<div class="row">
					<!-- div row-->
					<span class="labelField">
						<!--Service type-->
						<xsl:value-of select="/root/gui/strings/inspire/what/l15"/>
					</span>
					<select id="protocol" class="content" style="width:200px; !important">
						<option value="" selected="selected"/>
						<xsl:for-each select="/root/gui/strings/protocolChoice[@show='y']">
							<option value="{@value}">
								<xsl:if test="@value=/root/gui/searchDefaults/protocol">
									<xsl:attribute name="selected"/>
								</xsl:if>
								<xsl:value-of select="."/>
							</option>
						</xsl:for-each>
					</select>
				</div>
			</div>

			<!-- INSPIRE Thema -->
			<div>
				<!-- style="float:left; margin-left: 20px;"-->
				<fieldset>
					<legend>
						<!--INSPIRE Thema-->
						<xsl:value-of select="/root/gui/strings/inspire/what/l14"/>
					</legend>

					<div id="inspirethemesdiv">
						<div>
							<!--style="max-height:170px;height:170px;overflow:auto;"-->

							<div class="inspireThemeTitle"><xsl:value-of
									select="/root/gui/strings/inspire/what/l6"/> I</div>

							<div class="inspireThemeElement">
								<input type="checkbox" value="Geographical names"
									name="Geographical names" id="inspire_GeographicalNames"/>

								<span>
									<label for="inspire_GeographicalNames">
										<!--Geographical names-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l3"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Administrative units"
									name="Administrative units" id="inspire_AdministrativeUnits"/>

								<span>
									<label for="inspire_AdministrativeUnits">
										<!--Administrative units-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l4"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Addresses" name="Addresses"
									id="inspire_Addresses"/>

								<span>
									<label for="inspire_Addresses">
										<!--Addresses-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l5"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Cadastral parcels"
									name="Cadastral parcels" id="inspire_CadastralParcels"/>

								<span>
									<label for="inspire_CadastralParcels">
										<!--Cadastral parcels-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l6"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Transport networks"
									name="Transport networks" id="inspire_TransportNetworks"/>

								<span>
									<label for="inspire_TransportNetworks">
										<!--Transport networks-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l7"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Hydrography" name="Hydrography"
									id="inspire_Hydrography"/>

								<span>
									<label for="inspire_Hydrography">
										<!--Hydrography-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l8"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Protected sites"
									name="Protected sites" id="inspire_ProtectedSites"/>

								<span>
									<label for="inspire_ProtectedSites">
										<!--Protected sites-->
										<xsl:value-of select="/root/gui/strings/inspire/annex1/l9"/>
									</label>
								</span>
							</div>


							<div class="inspireThemeTitle"><xsl:value-of
									select="/root/gui/strings/inspire/what/l6"/> II</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Elevation" name="Elevation"
									id="inspire_Elevation"/>

								<span>
									<label for="inspire_Elevation">
										<!--Elevation-->
										<xsl:value-of select="/root/gui/strings/inspire/annex2/l1"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Land cover" name="Land cover"
									id="inspire_LandCover"/>

								<span>
									<label for="inspire_LandCover">
										<!--Land cover-->
										<xsl:value-of select="/root/gui/strings/inspire/annex2/l2"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Orthoimagery" name="Orthoimagery"
									id="inspire_Orthoimagery"/>

								<span>
									<label for="inspire_Orthoimagery">
										<!--Orthoimagery-->
										<xsl:value-of select="/root/gui/strings/inspire/annex2/l3"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Geology" name="Geology"
									id="inspire_Geology"/>

								<span>
									<label for="inspire_Geology">
										<!--Geology-->
										<xsl:value-of select="/root/gui/strings/inspire/annex2/l4"/>
									</label>
								</span>
							</div>


							<div class="inspireThemeTitle"><xsl:value-of
									select="/root/gui/strings/inspire/what/l6"/> III</div>

							<div class="inspireThemeElement">
								<input type="checkbox" value="Statistical units"
									name="Statistical units" id="inspire_StatisticalUnits"/>

								<span>
									<label for="inspire_StatisticalUnits">
										<!--Statistical units-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l1"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Buildings" name="Buildings"
									id="inspire_Buildings"/>

								<span>
									<label for="inspire_Buildings">
										<!--Buildings-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l2"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Soil" name="Soil" id="inspire_Soil"/>

								<span>
									<label for="inspire_Soil">
										<!--Soil-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l3"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Land use" name="Land use"
									id="inspire_LandUse"/>

								<span>
									<label for="inspire_LandUse">
										<!--Land use-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l4"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Human health and safety"
									name="Human health and safety" id="inspire_HumanHealthAndSafety"/>

								<span>
									<label for="inspire_HumanHealthAndSafety">
										<!--Human health and safety-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l5"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Utility and Government services"
									name="Utility and Government services"
									id="inspire_UtilityAndGovernmentServices"/>

								<span>
									<label for="inspire_UtilityAndGovernmentServices">
										<!--Utility and Government services-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l6"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Environmental monitoring facilities"
									name="Environmental monitoring facilities"
									id="inspire_EnvironmentalMonitoringFacilities"/>

								<span>
									<label for="inspire_EnvironmentalMonitoringFacilities">
										<!--Environmental monitoring facilities-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l7"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Production and industrial facilities"
									name="Production and industrial facilities"
									id="inspire_ProductionAndIndustrialFacilities"/>

								<span>
									<label for="inspire_ProductionAndIndustrialFacilities">
										<!--Production and industrial facilities-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l8"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox"
									value="Agricultural and aquaculture facilities"
									name="Agricultural and aquaculture facilities"
									id="inspire_AgriculturalAndAquacultureFacilities"/>

								<span>
									<label for="inspire_AgriculturalAndAquacultureFacilities">
										<!--Agricultural and aquaculture facilities-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l9"/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Population distribution - demography"
									name="Population distribution - demography"
									id="inspire_PopulationDistribution-Demography"/>
								<span>
									<label for="inspire_PopulationDistribution-Demography">
										<!--Population distribution - demography-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l10"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox"
									value="Area management/restriction/regulation zones and reporting units"
									name="Area management/restriction/regulation zones and reporting units"
									id="inspire_AreaManagementRestrictionRegulationZonesAndReportingUnits"/>
								<span>
									<label
										for="inspire_AreaManagementRestrictionRegulationZonesAndReportingUnits">
										<!--Area management/restriction/regulation zones and reporting units-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l11"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Natural risk zones"
									name="Natural risk zones" id="inspire_NaturalRiskZones"/>
								<span>
									<label for="inspire_NaturalRiskZones">
										<!--Natural risk zones-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l12"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Atmospheric conditions"
									name="Atmospheric conditions" id="inspire_AtmosphericConditions"/>
								<span>
									<label for="inspire_AtmosphericConditions">
										<!--Atmospheric conditions-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l13"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Meteorological geographical features"
									name="Meteorological geographical features"
									id="inspire_MeteorologicalGeographicalFeatures"/>
								<span>
									<label for="inspire_MeteorologicalGeographicalFeatures">
										<!--Meteorological geographical features-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l14"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Oceanographic geographical features"
									name="Oceanographic geographical features"
									id="inspire_OceanographicGeographicalFeatures"/>
								<span>
									<label for="inspire_OceanographicGeographicalFeatures">
										<!--Oceanographic geographical features-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l15"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Sea regions" name="Sea regions"
									id="inspire_SeaRegions"/>
								<span>
									<label for="inspire_SeaRegions">
										<!--Sea regions-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l16"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Bio-geographical regions"
									name="Bio-geographical regions"
									id="inspire_Bio-geographicalRegions"/>
								<span>
									<label for="inspire_Bio-geographicalRegions">
										<!--Bio-geographical regions-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l17"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Habitats and biotopes"
									name="Habitats and biotopes" id="inspire_HabitatsAndBiotopes"/>
								<span>
									<label for="inspire_HabitatsAndBiotopes">
										<!--Habitats and biotopes-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l18"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Species distribution"
									name="Species distribution" id="inspire_SpeciesDistribution"/>
								<span>
									<label for="inspire_SpeciesDistribution">
										<!--Species distribution-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l19"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Energy resources"
									name="Energy resources" id="inspire_EnergyResources"/>
								<span>
									<label for="inspire_EnergyResources">
										<!--Energy resources-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l20"
										/>
									</label>
								</span>
							</div>
							<div class="inspireThemeElement">
								<input type="checkbox" value="Mineral resources"
									name="Mineral resources" id="inspire_MineralResources"/>
								<span>
									<label for="inspire_MineralResources">
										<!--Mineral resources-->
										<xsl:value-of select="/root/gui/strings/inspire/annex3/l21"
										/>
									</label>
								</span>
							</div>
						</div>
					</div>
				</fieldset>
			</div>

		</div>
		<!-- end INSPIRE search elements -->
	</xsl:template>

	<!-- ============================================================ 
		WHAT
	======================================= ===================== -->

	<xsl:template name="adv_what">
		<h1 style="margin-bottom:5px">
			<xsl:value-of select="/root/gui/strings/what"/>
		</h1>

		<!-- Either Of The Words -->
		<div class="row">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/searchEitherOfTheWords"/>
			</span>
			<input name="or" id="or" class="content" size="25" value=""/>
			<br/>
			<a href="#" onclick="toggleMoreFields()" style="margin-left:2px">
				<img id="i_morefields" src="{/root/gui/url}/images/openwis/plus.gif"
					title="{/root/gui/strings/showMoreSearchFields}"
					alt="{/root/gui/strings/showMoreSearchFields}"/>
			</a>
		</div>

		<!-- Exact Phrase -->
		<div class="row" id="phrase_search_row" style="display:none">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/searchExactPhrase"/>
			</span>
			<input name="phrase" id="phrase" class="content" size="25" value=""/>
		</div>

		<!-- All Text -->
		<div class="row" id="all_search_row" style="display:none">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/searchAllText"/>
			</span>
			<input name="all" id="all" class="content" size="25" value=""/>
		</div>

		<!-- Without Words -->
		<div class="row" id="without_search_row" style="display:none">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/searchWithoutWords"/>
			</span>
			<input name="without" id="without" class="content" size="25" value=""/>
		</div>

		<!-- Title -->
		<div class="row">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/rtitle"/>
			</span>
			<span title="{/root/gui/strings/searchhelp/rtitle}">
				<input name="title" id="title" class="content" size="25"
					value="{/root/gui/searchDefaults/title}"/>
			</span>
		</div>

		<!-- Abstract -->
		<div class="row">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/abstract"/>
			</span>
			<span title="{/root/gui/strings/searchhelp/abstract}">
				<input name="abstract" id="abstract" class="content" size="25"
					value="{/root/gui/searchDefaults/abstract}"/>
			</span>
		</div>

		<!-- Keywords -->
		<div class="row">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/keywords"/>
			</span>
			<span title="{/root/gui/strings/searchhelp/keywords}">
				<input id="themekey" name="themekey"
					onClick="javascript:popKeyword (this, $('keywordSelectorFrame'));"
					class="content" size="25" value="{/root/gui/searchDefaults/themekey}"/>
			</span>

			<xsl:if test="/root/gui/config/search/keyword-selection-panel">
				<a style="cursor:pointer;" onclick="javascript:showSearchKeywordSelectionPanel();">
					<img src="{/root/gui/url}/images/find.png"
						alt="{/root/gui/strings/searchhelp/thesaurus}"
						title="{/root/gui/strings/searchhelp/thesaurus}"/>
				</a>
			</xsl:if>

			<div id="keywordSelectorFrame" class="keywordSelectorFrame"
				style="display:none;z-index:1000;">
				<div id="keywordSelector" class="keywordSelector"/>
			</div>

			<div id="keywordList" class="keywordList"/>
		</div>

		<!--div class="row"-->
		<!-- div row-->
		<!--span class="labelField"><xsl:value-of select="/root/gui/strings/category"/></span>
		<select class="content" name="category" id="category">
			<option value="">
				<xsl:if test="/root/gui/searchDefaults/category=''">
					<xsl:attribute name="selected"/>
				</xsl:if>
				<xsl:value-of select="/root/gui/strings/any"/>
			</option>
			
			<xsl:for-each select="/root/gui/categories/record">
				<xsl:sort select="label/child::*[name() = $lang]" order="ascending"/>
				
				<option value="{name}">
					<xsl:if test="name = /root/gui/searchDefaults/category">
						<xsl:attribute name="selected"/>
					</xsl:if>
					<xsl:value-of select="label/child::*[name() = $lang]"/>
				</option>
			</xsl:for-each>
		</select>
	</div-->

		<!-- Map type -->
		<div class="row">
			<!-- div row-->
			<a onclick="showFields('maptype.img','maptype.table')"
				style="cursor:pointer;cursor:hand;">
				<img id="maptype.img" src="{/root/gui/url}/images/plus.gif" alt=""/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="/root/gui/strings/mapType"/>
			</a>

			<table id="maptype.table"
				style="display:none;border-color:#2a628f;border-style:solid;width:80%;margin:5px;margin-left:15px">
				<tr>
					<td>
						<input name="digital" id="digital" type="checkbox" value="on">
							<xsl:if test="/root/gui/searchDefaults/digital='on'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
							<label for="digital">
								<xsl:value-of select="/root/gui/strings/digital"/>
							</label>
						</input>
						<br/>
						<input name="paper" id="paper" type="checkbox" value="on">
							<xsl:if test="/root/gui/searchDefaults/paper='on'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
							<label for="paper">
								<xsl:value-of select="/root/gui/strings/paper"/>
							</label>
						</input>
					</td>
					<td>
						<input name="dynamic" id="dynamic" type="checkbox">
							<xsl:if test="/root/gui/searchDefaults/dynamic='on'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
							<label for="dynamic">
								<xsl:value-of select="/root/gui/strings/dynamic"/>
							</label>
						</input>
						<br/>
						<input name="download" id="download" type="checkbox">
							<xsl:if test="/root/gui/searchDefaults/download='on'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
							<label for="download">
								<xsl:value-of select="/root/gui/strings/downloadable"/>
							</label>
						</input>
					</td>
				</tr>
			</table>
		</div>

		<!-- Fuzzy search -->
		<div class="row">
			<a onclick="showFields('fuzzy.img','fuzzy.td')" style="cursor:pointer;cursor:hand;">
				<img id="fuzzy.img" src="{/root/gui/url}/images/plus.gif" alt=""/>
				<xsl:text> </xsl:text>
				<xsl:value-of select="/root/gui/strings/fuzzy"/>
			</a>
			<table id="fuzzy.td"
				style="display:none;border-color:#2a628f;border-style:solid;margin:5px;margin-left:10px">
				<tr>
					<td>
						<xsl:value-of select="/root/gui/strings/fuzzyPrecise"/>
						<input type="radio" id="similarity1" name="similarity" value="1">
							<xsl:if test="/root/gui/searchDefaults/similarity='1'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
						</input>
						<input type="radio" id="similarity08" name="similarity" value=".8">
							<xsl:if test="/root/gui/searchDefaults/similarity='.8'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
						</input>
						<input type="radio" id="similarity06" name="similarity" value=".6">
							<xsl:if test="/root/gui/searchDefaults/similarity='.6'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
						</input>

						<input type="radio" id="similarity04" name="similarity" value=".4">
							<xsl:if test="/root/gui/searchDefaults/similarity='.4'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
						</input>
						<input type="radio" id="similarity02" name="similarity" value=".2">
							<xsl:if test="/root/gui/searchDefaults/similarity='.2'">
								<xsl:attribute name="checked">CHECKED</xsl:attribute>
							</xsl:if>
						</input>
						<xsl:value-of select="/root/gui/strings/fuzzyImprecise"/>
					</td>
				</tr>
			</table>

		</div>
	</xsl:template>


	<!-- ============================================================ 
		WHERE
	============================================================= -->

	<xsl:template name="adv_where">

		<h1 style="margin-bottom:5px; margin-top: 20px;">
			<xsl:value-of select="/root/gui/strings/where"/>
		</h1>

		<xsl:comment>MINIMAP</xsl:comment>

		<!-- Map and coordinates container -->
		<!--
	<table id="minimap_root" width="340px">
		<tr>
			<td colspan="3" align="center" style="padding: 3px;">
				<small><xsl:value-of select="/root/gui/strings/latMax"/></small> <input type="text" class="content" id="northBL" name="northBL"  size="5"
					value="{/root/gui/searchDefaults/northBL}" onChange="javascript:AoIrefresh();"
					alt="{/root/gui/strings/latitude}" title="{/root/gui/strings/latitude}"/>
			</td>
			
		</tr>
	
		<tr>
		
			<td width="52px" style="padding-top: 25px; align: center;">
				<small><xsl:value-of select="/root/gui/strings/longMin"/></small>
				<br />
				<input type="text" class="content" id="westBL" name="westBL" size="5"
					value="{/root/gui/searchDefaults/westBL}" onChange="javascript:AoIrefresh();"
					alt="{/root/gui/strings/longitude}" title="{/root/gui/strings/longitude}"/>
			</td>
			
			<td style="padding: 3px;">
				<div id="ol_minimap2" />
			</td>
			
			
			<td width="52px" style="padding-top: 25px; align: center;">
				<small><xsl:value-of select="/root/gui/strings/longMax"/></small>
				<br />
				<input type="text" class="content" id="eastBL" name="eastBL" size="5"
					value="{/root/gui/searchDefaults/eastBL}" onChange="javascript:AoIrefresh();"
					alt="{/root/gui/strings/longitude}" title="{/root/gui/strings/longitude}"/>
			</td>
		</tr>
	
		<tr>
			<td />
			<td colspan="2" align="center" style="padding: 3px;">
				<small><xsl:value-of select="/root/gui/strings/latMin"/></small> <input type="text" class="content" id="southBL" name="southBL" size="5"
					value="{/root/gui/searchDefaults/southBL}" onChange="javascript:AoIrefresh();"
					alt="{/root/gui/strings/latitude}" title="{/root/gui/strings/latitude}"/>
			</td>
			<td>
			-->
		<!--img src="{/root/gui/url}/images/update.png" id="updateBB" name="updateBB" style="visibility:hidden;border:2px solid red;" title="Update Area Of Interest" alt="Update Area Of Interest" onClick="javascript:updateAoIFromForm();"/-->
		<!--
			</td>
		</tr>
	</table>
	-->

		<div id="ol_minimap2"/>
		<div id="fond-carte-bas"/>

		<div id="lat_long_Min">
			<div id="latMin">
				<div class="lat_long">
					<small>
						<xsl:value-of select="/root/gui/strings/latMin"/>
					</small>
				</div>
				<div class="lat_long">
					<input type="text" class="content" id="southBL" name="southBL" size="5"
						value="{/root/gui/searchDefaults/southBL}"
						onChange="javascript:AoIrefresh();" alt="{/root/gui/strings/latitude}"
						title="{/root/gui/strings/latitude}"/>
				</div>
			</div>

			<div id="longMin">
				<div class="lat_long">
					<small>
						<xsl:value-of select="/root/gui/strings/longMin"/>
					</small>
				</div>
				<div class="lat_long">
					<input type="text" class="content" id="westBL" name="westBL" size="5"
						value="{/root/gui/searchDefaults/westBL}"
						onChange="javascript:AoIrefresh();" alt="{/root/gui/strings/longitude}"
						title="{/root/gui/strings/longitude}"/>
				</div>
			</div>
		</div>

		<div id="lat_long_Max">
			<div id="latMax">
				<div class="lat_long">
					<small>
						<xsl:value-of select="/root/gui/strings/latMax"/>
					</small>
				</div>
				<div class="lat_long">
					<input type="text" class="content" id="northBL" name="northBL" size="5"
						value="{/root/gui/searchDefaults/northBL}"
						onChange="javascript:AoIrefresh();" alt="{/root/gui/strings/latitude}"
						title="{/root/gui/strings/latitude}"/>
				</div>
			</div>

			<div id="longMax">
				<div class="lat_long">
					<small>
						<xsl:value-of select="/root/gui/strings/longMax"/>
					</small>
				</div>
				<div class="lat_long">
					<input type="text" class="content" id="eastBL" name="eastBL" size="5"
						value="{/root/gui/searchDefaults/eastBL}"
						onChange="javascript:AoIrefresh();" alt="{/root/gui/strings/longitude}"
						title="{/root/gui/strings/longitude}"/>
				</div>
			</div>
		</div>

		<!-- Bounding box relation -->
		<div class="row">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/type"/>
			</span>
			<select class="content" name="relation" id="relation">
				<xsl:for-each select="/root/gui/strings/boundingRelation">
					<option value="{@value}">
						<xsl:if test="@value=/root/gui/searchDefaults/relation">
							<xsl:attribute name="selected">selected</xsl:attribute>
						</xsl:if>
						<xsl:value-of select="."/>
					</option>
				</xsl:for-each>
			</select>
		</div>

		<!-- Region -->
		<div class="row">
			<!-- div row-->
			<span class="labelField">
				<xsl:value-of select="/root/gui/strings/region"/>
			</span>
			<select class="content" name="region" id="region"
				onchange="javascript:doRegionSearchAdvanced();">
				<option value="">
					<xsl:if test="/root/gui/searchDefaults/theme='_any_'">
						<xsl:attribute name="selected">selected</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="/root/gui/strings/any"/>
				</option>
				<option value="userdefined">
					<xsl:if test="/root/gui/searchDefaults/theme='_userdefined_'">
						<xsl:attribute name="selected">selected</xsl:attribute>
					</xsl:if>
					<xsl:value-of select="/root/gui/strings/userDefined"/>
				</option>

				<xsl:for-each select="/root/gui/regions/record">
					<xsl:sort select="label/child::*[name() = $lang]" order="ascending"/>
					<option value="{id}">
						<xsl:if test="id=/root/gui/searchDefaults/region">
							<xsl:attribute name="selected">selected</xsl:attribute>
						</xsl:if>
						<xsl:attribute name="value">
							<xsl:value-of select="id"/>
						</xsl:attribute>
						<xsl:value-of select="label/child::*[name() = $lang]"/>
					</option>
				</xsl:for-each>
			</select>
		</div>
	</xsl:template>

	<!-- ============================================================ 
		WHEN
	============================================================= -->

	<xsl:template name="adv_when">
		<br/>
		<h1 style="margin-top:5px;margin-bottom:5px">
			<a href="#" onclick="toggleWhen()" style="margin-right:2px">
				<img id="i_when" src="{/root/gui/url}/images/openwis/plus.gif" alt=""/>
			</a>
			<xsl:value-of select="/root/gui/strings//when"/>
		</h1>

		<div id="whensearchfields" style="display:none">
			<div class="row">
				<input onclick="setDates(0);" value="" name="radfrom" id="radfrom0" type="radio">
					<xsl:if
						test="string(/root/gui/searchDefaults/dateFrom)='' and string(/root/gui/searchDefaults/dateTo)=''
							and string(/root/gui/searchDefaults/extFrom)='' and string(/root/gui/searchDefaults/extTo)=''">
						<xsl:attribute name="checked">CHECKED</xsl:attribute>
					</xsl:if>
					<label for="radfrom0">
						<xsl:value-of select="/root/gui/strings/anytime"/>
					</label>
				</input>
			</div>

			<div class="row">
				<input value="" name="radfrom" id="radfrom1" type="radio" disabled="disabled">
					<xsl:if
						test="string(/root/gui/searchDefaults/dateFrom)!='' and string(/root/gui/searchDefaults/dateTo)!=''">
						<xsl:attribute name="checked">CHECKED</xsl:attribute>
					</xsl:if>
					<label for="radfrom1">
						<xsl:value-of select="/root/gui/strings/changeDate"/>
					</label>
				</input>
			</div>

			<!-- Change format to %Y-%m-%dT%H:%M:00 in order to have DateTime field instead of DateField -->
			<table>
				<tr>
					<td>
						<xsl:value-of select="/root/gui/strings/from"/>
					</td>
					<td>
						<div class="cal" id="dateFrom"
							onclick="$('radfrom1').checked=true;$('radfrom1').disabled='';$('radfromext1').disabled='disabled';"/>
						<input type="hidden" id="dateFrom_format" value="%Y-%m-%d"/>
						<input type="hidden" id="dateFrom_cal" value=""/>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="/root/gui/strings/to"/>
					</td>
					<td>
						<div class="cal" id="dateTo"
							onclick="$('radfrom1').checked=true;$('radfrom1').disabled='';$('radfromext1').disabled='disabled';"/>
						<input type="hidden" id="dateTo_format" value="%Y-%m-%d"/>
						<input type="hidden" id="dateTo_cal" value=""/>
					</td>
				</tr>
			</table>


			<div class="row">
				<input value="" name="radfrom" id="radfromext1" type="radio" disabled="disabled">
					<xsl:if
						test="string(/root/gui/searchDefaults/extFrom)!='' and string(/root/gui/searchDefaults/extTo)!=''">
						<xsl:attribute name="checked"/>
					</xsl:if>
					<label for="radfromext1">
						<xsl:value-of select="/root/gui/strings/datasetIssued"/>
					</label>
				</input>
			</div>

			<!-- Change format to %Y-%m-%dT%H:%M:00 in order to have DateTime field instead of DateField -->
			<table>
				<tr>
					<td>
						<xsl:value-of select="/root/gui/strings/from"/>
					</td>
					<td>
						<div class="cal" id="extFrom"
							onclick="$('radfromext1').checked=true;$('radfromext1').disabled='';$('radfrom1').disabled='disabled';"/>
						<input type="hidden" id="extFrom_format" value="%Y-%m-%d"/>
						<input type="hidden" id="extFrom_cal" value=""/>
					</td>
				</tr>
				<tr>
					<td>
						<xsl:value-of select="/root/gui/strings/to"/>
					</td>
					<td>
						<div class="cal" id="extTo"
							onclick="$('radfromext1').checked=true;$('radfromext1').disabled='';$('radfrom1').disabled='disabled';"/>
						<input type="hidden" id="extTo_format" value="%Y-%m-%d"/>
						<input type="hidden" id="extTo_cal" value=""/>
					</td>
				</tr>
			</table>

		</div>


		<!-- restrict to - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->


		<!-- now make sure we open expanded if any restrictions are selected -->
		<xsl:if
			test="/root/gui/searchDefaults/siteId!='' or
				  /root/gui/searchDefaults/groups/group!='' or
				  /root/gui/searchDefaults/ownergroups='on' or
	              /root/gui/searchDefaults/owner='on' or
	              /root/gui/searchDefaults/notgroups='on' or
 				  ( /root/gui/searchDefaults/template!='' and /root/gui/searchDefaults/template!='n' ) or
				  /root/gui/searchDefaults/category!=''">
			<script type="text/javascript">
			showFields('restrictions.img','restrictions.table');
		</script>
		</xsl:if>

		<!-- options - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -->

		<!-- now make sure we open expanded if any options are selected -->
		<xsl:if
			test="/root/gui/searchDefaults/sortBy!='relevance' or
				  /root/gui/searchDefaults/hitsPerPage!='10' or
				  /root/gui/searchDefaults/output!='full'">
			<script type="text/javascript">
			showFields('advoptions.img','advoptions.fieldset');
		</script>
		</xsl:if>
	</xsl:template>


</xsl:stylesheet>
