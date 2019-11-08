<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- ============================================================================================= -->

	<xsl:include href="../main.xsl"/>

	<!-- ============================================================================================= -->

	<xsl:variable name="style" select="'margin-left:50px;'"/>
	<xsl:variable name="width" select="'70px'"/>
	
	<!-- ============================================================================================= -->
	
	<xsl:template mode="script" match="/">
		<script type="text/javascript" src="{/root/gui/url}/scripts/core/kernel/kernel.js"/>
		<script type="text/javascript" src="{/root/gui/url}/scripts/core/gui/gui.js"/>
		<script type="text/javascript" src="{/root/gui/url}/scripts/config/config.js"/>
	</xsl:template>

	<!-- ============================================================================================= -->
	<!-- === page content -->
	<!-- ============================================================================================= -->

	<xsl:template name="content">
		<xsl:call-template name="formLayout">
			<xsl:with-param name="title" select="/root/gui/strings/systemConfig"/>

			<xsl:with-param name="content">
				<xsl:call-template name="panel"/>
			</xsl:with-param>

			<xsl:with-param name="buttons">
				<xsl:call-template name="buttons"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	<!-- === Panel -->
	<!-- ============================================================================================= -->

	<xsl:template name="panel">
		<xsl:call-template name="site"/>
		<xsl:call-template name="server"/>
		<xsl:call-template name="intranet"/>
		<xsl:call-template name="selectionmanager"/>
		<xsl:call-template name="indexoptimizer"/>
		<xsl:call-template name="z3950"/>
		<xsl:call-template name="oai"/>
		<xsl:call-template name="xlinkResolver"/>
		<xsl:call-template name="downloadservice"/>
		<xsl:call-template name="csw"/>
		<xsl:call-template name="hyperlinks"/>
		<xsl:call-template name="localrating"/>
        <xsl:call-template name="inspire"/>
        <xsl:call-template name="cache"/>
		<xsl:call-template name="proxy"/>
		<xsl:call-template name="feedback"/>
		<xsl:call-template name="removedMetadata"/>
		<xsl:call-template name="authentication"/>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="xlinkResolver">
		<h1 align="left"><xsl:value-of select="/root/gui/config/xlinkResolver"/></h1>
		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="xlinkResolver.enable" class="content" type="checkbox"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

    <!-- ============================================================================================= -->

	<xsl:template name="inspire">
		<h1 align="left"><xsl:value-of select="/root/gui/config/inspire"/></h1>
		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="inspire.enable" class="content" type="checkbox"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>
	
	<!-- ============================================================================================= -->

	<xsl:template name="cache">
		<h1 align="left"><xsl:value-of select="/root/gui/config/cache"/></h1>
		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="cache.enable" class="content" type="checkbox"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="authentication">
		<h1 align="left"><xsl:value-of select="/root/gui/config/authentication"/></h1>
		<div align="left" style="{$style}">
			<b><xsl:value-of select="concat(/root/gui/config/loginuses,': ')"/></b>
			<div align="left" style="{$style}">
				<input align="left" type="radio" id="geonetworkdb.use" name="authentication" value="default"><xsl:value-of select="/root/gui/config/geonetworkdb"/></input>
				<xsl:call-template name="geonetworkdb"/>
			</div>
			<div align="left" style="{$style}">
				<input align="left" type="radio" id="ldap.use" name="authentication" value="ldap"><xsl:value-of select="/root/gui/config/ldap"/></input>
				<xsl:call-template name="ldap"/>
			</div>
		</div>
		
		<div align="left" style="{$style}">
			<b><xsl:value-of select="concat(/root/gui/config/otherlogins,': ')"/></b>
			<div align="left" style="{$style}">
				<input align="left" type="checkbox" id="shib.use" name="authentication" value="shib">
					<xsl:value-of select="/root/gui/config/shib"/> 
				</input>
				<xsl:call-template name="shib"/>
			</div>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="site">		
		<h1 align="left"><xsl:value-of select="/root/gui/config/site"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/name"/></td>
					<td class="padded"><input id="site.name" class="content" type="text" value="" size="30"/></td>
				</tr>
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/organ"/></td>
					<td class="padded"><input id="site.organ" class="content" type="text" value="" size="30"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="server">
		<h1 align="left"><xsl:value-of select="/root/gui/config/server"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/host"/></td>
					<td class="padded"><input id="server.host" class="content" type="text" value="" size="30"/></td>
				</tr>
				
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/port"/></td>
					<td class="padded"><input id="server.port" class="content" type="text" value="" size="30"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="intranet">
		<h1 align="left"><xsl:value-of select="/root/gui/config/intranet"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/network"/></td>
					<td class="padded"><input id="intranet.network" class="content" type="text" value="" size="30"/></td>
				</tr>
				
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/netmask"/></td>
					<td class="padded"><input id="intranet.netmask" class="content" type="text" value="" size="30"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="downloadservice">
		<h1 align="left"><xsl:value-of select="/root/gui/config/downloadservice"/></h1>

		<div align="left" style="{$style}">
			<input align="left" type="radio" id="downloadservice.simple" value="simple" name="downloadservice"><xsl:value-of select="/root/gui/config/simple"/></input>
			<div align="left" style="{$style}">
				<span id="downloadservice_simple.subpanel">
					<xsl:value-of select="/root/gui/config/tips/tip[id='downloadservice.simple']"/>
				</span>
			</div>
		</div>
		<div align="left" style="{$style}">
			<input align="left" type="radio" id="downloadservice.withdisclaimer" value="disclaimer" name="downloadservice"><xsl:value-of select="/root/gui/config/withdisclaimer"/></input>
			<div align="left" style="{$style}">
				<span id="downloadservice_withdisclaimer.subpanel">
					<xsl:value-of select="/root/gui/config/tips/tip[id='downloadservice.withdisclaimer']"/>
				</span>
			</div>
		</div>
		<div align="left" style="{$style}">
			<input align="left" type="radio" id="downloadservice.leave" value="leave" name="downloadservice"><xsl:value-of select="/root/gui/config/leave"/></input>
			<div align="left" style="{$style}">
				<span id="downloadservice_leave.subpanel">
					<xsl:value-of select="/root/gui/config/tips/tip[id='downloadservice.leave']"/>
				</span>
			</div>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="z3950">
		<h1 align="left"><xsl:value-of select="/root/gui/config/z3950"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="z3950.enable" class="content" type="checkbox"/></td>
				</tr>
	
				<tr>
					<td/>
					<td>
						<table id="z3950.subpanel">
							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/port"/></td>
								<td class="padded"><input id="z3950.port" class="content" type="text" value="" size="20"/></td>
							</tr>
						</table>
					</td>
				</tr>			
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="selectionmanager">
		<h1 align="left"><xsl:value-of select="/root/gui/config/selectionmanager"/></h1>

		<div align="left" style="{$style}">
			<table>
				<td class="padded"><xsl:value-of select="/root/gui/config/maxrecords"/></td>
				<td class="padded"><input id="selection.maxrecords" class="content" type="text" value="" size="20"/></td>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template mode="selectoptions" match="day|hour|minute|dsopt">
		<option>
			<xsl:attribute name="value">
				<xsl:value-of select="."/>
			</xsl:attribute>
			<xsl:value-of select="@label"/>
		</option>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="indexoptimizer">
		<h1 align="left"><xsl:value-of select="/root/gui/config/indexoptimizer"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="indexoptimizer.enable" class="content" type="checkbox"/></td>
				</tr>
	
				<tr>
					<td/>
					<td>
						<table id="indexoptimizer.subpanel">
							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/at"/></td>
								<td class="padded">
									<select id="indexoptimizer.at.hour" class="content">
										<xsl:apply-templates mode="selectoptions" select="/root/gui/config/hours/hour"/>
									</select>:
									<select id="indexoptimizer.at.min" class="content">
										<xsl:apply-templates mode="selectoptions" select="/root/gui/config/minutes/minute"/>
									</select>
									<!-- leave seconds hidden - not really necessary? -->
									<input id="indexoptimizer.at.sec"  class="content" type="hidden" value="0" size="2"/>
									&#160;
									<xsl:value-of select="/root/gui/config/atSpec"/>
								</td>
							</tr>
							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/interval"/></td>
								<td class="padded">
									<!-- leave days hidden - not really necessary? -->
									<input id="indexoptimizer.interval.day" class="content" type="hidden" value="0" size="2"/>
									<select id="indexoptimizer.interval.hour" class="content">
										<xsl:apply-templates mode="selectoptions" select="/root/gui/config/hourintervals/hour"/>
									</select>
									<!-- leave minutes hidden - not really necessary? -->
									<input id="indexoptimizer.interval.min" class="content" type="hidden" value="0" size="2"/>
									&#160;
									<xsl:value-of select="/root/gui/config/intervalSpec"/>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="oai">
		<h1 align="left"><xsl:value-of select="/root/gui/config/oai"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/datesearch"/></td>
					<td class="padded">
						<select id="oai.mdmode" class="content">
							<xsl:apply-templates mode="selectoptions" select="/root/gui/config/datesearchopt/dsopt"/>
						</select>
					</td>
				</tr>
	
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/tokentimeout"/></td>
					<td class="padded"><input id="oai.tokentimeout" class="content" type="text" value="" size="20"/></td>
				</tr>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/cachesize"/></td>
					<td class="padded"><input id="oai.cachesize" class="content" type="text" value="" size="20"/></td>
				</tr>
							
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="csw">
		<script type="text/javascript" language="JavaScript1.2">
			function updateContact(id) {
				if (id == -1)
					return;
					
				var records = {
					<xsl:for-each select="/root/gui/users/record">
						record_<xsl:value-of select="id"/> : {
							username: "<xsl:value-of select="username"/>",
							surname: "<xsl:value-of select="surname"/>",
							name: "<xsl:value-of select="name"/>",
							profile: "<xsl:value-of select="profile"/>",
							address: "<xsl:value-of select="address"/>",
							city: "<xsl:value-of select="city"/>",
							state: "<xsl:value-of select="state"/>",
							country:"<xsl:value-of select="country"/>",
							zip:"<xsl:value-of select="zip"/>",
							email:"<xsl:value-of select="email"/>",
							organisation:"<xsl:value-of select="organisation"/>",
							kind:"<xsl:value-of select="kind"/>"
						}
						<xsl:if test="position()!=last()">
							<xsl:text>,</xsl:text>
						</xsl:if>
					</xsl:for-each>
				}
				$('csw.individualName').value      = records['record_'+id].name +' '+ records['record_'+id].surname; 
				$('csw.positionName').value        = records['record_'+id].profile;
				$('csw.administrativeArea').value  = records['record_'+id].state;
				$('csw.postalCode').value          = records['record_'+id].zip;
				$('csw.country').value             = records['record_'+id].country;
				$('csw.deliveryPoint').value       = records['record_'+id].address;
				$('csw.city').value                = records['record_'+id].city;
				$('csw.email').value               = records['record_'+id].email ;
				$('csw.role').value                = records['record_'+id].kind;
				$('csw.contactInstructions').value = records['record_'+id].organisation;
			}
		</script>
		
		<h1 align="left"><xsl:value-of select="/root/gui/config/csw"/></h1>
		
		<div align="left" style="{$style}">
            <table>
            	<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="csw.enable" class="content" type="checkbox"/></td>
				</tr>
            	<tr>
            		<td class="padded"><xsl:value-of select="/root/gui/config/contactId"/></td>
            		<td class="padded">
            			<select name="csw.contactId" id="csw.contactId" onchange="javascript:updateContact(this.value)">
            				<option value="-1"></option>
            				<xsl:for-each select="/root/gui/users/record">
            					<xsl:sort select="username"/>
            					<option>
            						<xsl:attribute name="value">
            							<xsl:value-of select="id"/>
            						</xsl:attribute>
            						<xsl:value-of select="username"/>
            						<xsl:text> ( </xsl:text><xsl:value-of select="surname"/>
            						<xsl:text> </xsl:text>
            						<xsl:value-of select="name"/><xsl:text> ) </xsl:text>
            					</option>
            				</xsl:for-each>
            			</select>
            		</td>
            	</tr>
            	<tr>
            		<td class="padded"><xsl:value-of select="/root/gui/config/title"/></td>
            		<td class="padded"><input id="csw.title" class="content" type="text" value="" size="40"/></td>
            	</tr>
            	<tr>
            		<td class="padded"><xsl:value-of select="/root/gui/config/abstract"/></td>
            		<td class="padded"><input id="csw.abstract" class="content" type="text" value="" size="40"/></td>
            	</tr>
            	<tr>
            		<td class="padded"><xsl:value-of select="/root/gui/config/fees"/></td>
            		<td class="padded"><input id="csw.fees" class="content" type="text" value="" size="40"/></td>
            	</tr>
            	<tr>
            		<td class="padded"><xsl:value-of select="/root/gui/config/accessConstraints"/></td>
            		<td class="padded"><input id="csw.accessConstraints" class="content" type="text" value="" size="40"/></td>
            	</tr>
            	<tr>
            		<td class="padded" colspan="2">
            			<input id="csw.individualName" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.positionName" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.voice" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.facsimile" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.deliveryPoint" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.city" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.administrativeArea" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.postalCode" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.country" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.email" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.hoursOfService" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.contactInstructions" class="content" type="hidden" value="" size="20"/>
            			<input id="csw.role" class="content" type="hidden" value="" size="20"/>
            		</td>
            	</tr>
                <tr>
                    <td class="padded"><xsl:value-of select="/root/gui/config/cswMetadataPublic"/></td>
                    <td class="padded"><input id="csw.metadataPublic" class="content" type="checkbox" value=""/></td>
                </tr>                
            </table>
        </div>
	</xsl:template>

	<!-- ============================================================================================= -->
	<xsl:template name="hyperlinks">
		<h1 align="left"><xsl:value-of select="/root/gui/config/clickablehyperlinks"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="clickablehyperlinks.enable" class="content" type="checkbox" value=""/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->
	<xsl:template name="localrating">
		<h1 align="left"><xsl:value-of select="/root/gui/config/localrating"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/enable"/></td>
					<td class="padded"><input id="localrating.enable" class="content" type="checkbox" value=""/></td>
				</tr>
			</table>
		</div>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	<xsl:template name="proxy">
		<h1 align="left"><xsl:value-of select="/root/gui/config/proxy"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/use"/></td>
					<td class="padded"><input id="proxy.use" class="content" type="checkbox" value=""/></td>
				</tr>
				<tr>
					<td/>
					<td>
						<table id="proxy.subpanel">
							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/host"/></td>
								<td class="padded"><input id="proxy.host" class="content" type="text" value="" size="20"/></td>
							</tr>
			
							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/port"/></td>
								<td class="padded"><input id="proxy.port" class="content" type="text" value="" size="20"/></td>
							</tr>

							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/username"/></td>
								<td class="padded"><input id="proxy.username" class="content" type="text" value="" size="20"/></td>
							</tr>

							<tr>
								<td class="padded"><xsl:value-of select="/root/gui/config/password"/></td>
								<td class="padded"><input id="proxy.password" class="content" type="password" value="" size="20"/></td>
							</tr>
						</table>
					</td>
				</tr>			
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="feedback">
		<h1 align="left"><xsl:value-of select="/root/gui/config/feedback"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/email"/></td>
					<td class="padded"><input id="feedback.email" class="content" type="text" value=""/></td>
				</tr>
				
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/smtpHost"/></td>
					<td class="padded"><input id="feedback.mail.host" class="content" type="text" value=""/></td>
				</tr>
				
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/smtpPort"/></td>
					<td class="padded"><input id="feedback.mail.port" class="content" type="text" value=""/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->

	<xsl:template name="removedMetadata">
		<h1 align="left"><xsl:value-of select="/root/gui/config/removedMetadata"/></h1>

		<div align="left" style="{$style}">
			<table>
				<tr>
					<td class="padded" width="{$width}"><xsl:value-of select="/root/gui/config/dir"/></td>
					<td class="padded"><input id="removedMd.dir" class="content" type="text" value=""/></td>
				</tr>			
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->
	<!-- === Geonetwork DB panels === -->
	<!-- ============================================================================================= -->
	
	<xsl:template name="geonetworkdb">
		<div align="left" style="{$style}">
			<table id="geonetworkdb.subpanel">
				<tr>
					<td class="padded" width="40%"><xsl:value-of select="concat(/root/gui/config/enable,' ',/root/gui/config/userSelfRegistration)"/></td>
					<td class="padded"><input id="userSelfRegistration.enable" class="content" type="checkbox"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	<!-- === LDAP panels === -->
	<!-- ============================================================================================= -->
	
	<xsl:template name="ldap">
		<div align="left" style="{$style}">
			<table id="ldap.subpanel">
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/host"/></td>
					<td class="padded"><input id="ldap.host" class="content" type="text" value="" size="20"/></td>
				</tr>
			
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/port"/></td>
					<td class="padded"><input id="ldap.port" class="content" type="text" value="" size="20"/></td>
				</tr>
							
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/defProfile"/></td>
					<td class="padded"><xsl:call-template name="ldapDefProfile"/></td>
				</tr>
							
				<!-- distinguished names -->
							
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/distNames"/></td>
					<td/>
				</tr>
				<tr>
					<td/>
					<td class="padded"><xsl:call-template name="ldapDistNames"/></td>
				</tr>
							
				<!-- user's attributes -->
							
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/userAttribs"/></td>
					<td/>
				</tr>
				<tr>
					<td/>
					<td class="padded"><xsl:call-template name="ldapUserAttribs"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>

	<!-- ============================================================================================= -->
	
	<xsl:template name="ldapDefProfile">
		<select class="content" size="1" name="profile" id="ldap.defProfile">
			<!--option value="Administrator">
				<xsl:value-of select="/root/gui/strings/Administrator"/>
			</option-->
		
			<!--option value="UserAdmin">
				<xsl:value-of select="/root/gui/strings/UserAdmin"/>
			</option-->
		
			<option value="Reviewer">
				<xsl:value-of select="/root/gui/strings/Reviewer"/>
			</option>
		
			<option value="Editor">
				<xsl:value-of select="/root/gui/strings/Editor"/>
			</option>
			
			<option value="RegisteredUser">
				<xsl:value-of select="/root/gui/strings/RegisteredUser"/>
			</option>
		</select>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	
	<xsl:template name="ldapDistNames">
		<table>
			<tr>
				<td class="padded" width="60px"><xsl:value-of select="/root/gui/config/baseDN"/></td>
				<td class="padded"><input id="ldap.baseDN" class="content" type="text" value="" size="20"/></td>
			</tr>
			
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/config/usersDN"/></td>
				<td class="padded"><input id="ldap.usersDN" class="content" type="text" value="" size="20"/></td>
			</tr>
		</table>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	
	<xsl:template name="ldapUserAttribs">
		<table>
			<tr>
				<td class="padded" width="60px"><xsl:value-of select="/root/gui/config/name"/></td>
				<td class="padded"><input id="ldap.nameAttr" class="content" type="text" value="" size="20"/></td>
			</tr>
			
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/config/profile"/></td>
				<td class="padded"><input id="ldap.profileAttr" class="content" type="text" value="" size="20"/></td>
			</tr>
		</table>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	<!-- === Shibboleth panels === -->
	<!-- ============================================================================================= -->
	
	<xsl:template name="shib">

		<div align="left" style="{$style}">
			<table id="shib.subpanel">
				<tr>
					<td class="padded"><xsl:value-of select="/root/gui/config/path"/></td>
					<td class="padded"><input id="shib.path" class="content" type="text" size="256"/></td>
				</tr>
					
				<!-- shibboleth attributes -->
									
				<tr>
					<td class="padded" colspan="2"><xsl:value-of select="/root/gui/config/attributes"/></td>
				</tr>
				<tr>
					<td/>
					<td class="padded"><xsl:call-template name="shibAttribs"/></td>
				</tr>
			</table>
		</div>
	</xsl:template>
	
	<!-- ============================================================================================= -->
	
	<xsl:template name="shibAttribs">
		<table>
			<tr>
				<td class="padded" width="60px"><xsl:value-of select="/root/gui/config/username"/></td>
				<td class="padded"><input id="shib.attrib.username" class="content" type="text" value="" size="150"/></td>
			</tr>
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/config/surname"/></td>
				<td class="padded"><input id="shib.attrib.surname" class="content" type="text" value="" size="150"/></td>
			</tr>
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/config/firstname"/></td>
				<td class="padded"><input id="shib.attrib.firstname" class="content" type="text" value="" size="150"/></td>
			</tr>
			<tr>
				<td class="padded"><xsl:value-of select="/root/gui/config/profile"/></td>
				<td class="padded"><input id="shib.attrib.profile" class="content" type="text" value="" size="150"/></td>
			</tr>
		</table>
	</xsl:template>

	<!-- ============================================================================================= -->
	<!-- === Buttons -->
	<!-- ============================================================================================= -->

	<xsl:template name="buttons">
		<button class="content" onclick="load('{/root/gui/locService}/admin')">
			<xsl:value-of select="/root/gui/strings/back"/>
		</button>
		&#160;
		<button class="content" onclick="config.save()">
			<xsl:value-of select="/root/gui/config/save"/>
		</button>
		&#160;
		<button class="content" onclick="config.refresh()">
			<xsl:value-of select="/root/gui/config/refresh"/>
		</button>
	</xsl:template>

	<!-- ============================================================================================= -->

</xsl:stylesheet>
