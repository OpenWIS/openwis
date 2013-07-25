<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!--
	main html banner
	-->
	<xsl:template name="banner">

		<table width="100%" cellspacing="0">

			<!-- title -->
			<tr id="banner-logo">
				<td align="left" width="20%">
					<img src="{/root/gui/url}/images/openwis/header-left.jpg" alt="World picture" align="top" />
				</td>
				<td align="center" width="60%">
					<img src="{/root/gui/url}/images/openwis/titre_site.png" alt="World picture" align="top" />
				</td>
				<td align="right" width="20%">
					<img src="{/root/gui/url}/images/openwis/header-right.gif" alt="GeoNetwork opensource logo" align="top" />
				</td>
			</tr>

			<!-- buttons -->
			<tr id="banner-menu">
				<td align="left">
					
				</td>
				<td class="banner-menu">
					<table id="menu" cellpading="0" cellspacing="0" border="0" align="center">
						<tr>
							<td class="leftEl">&#160;</td>
							<td class="centerEl">
								<a class="banner" href="{/root/gui/locService}/main.home">
									<xsl:value-of select="/root/gui/strings/home"/>
								</a>
								|
								<xsl:if test="string(/root/gui/session/userId)!=''">
								<xsl:choose>
									<xsl:when test="/root/gui/reqService='admin'">
										<font class="menu-active"><xsl:value-of select="/root/gui/strings/admin"/></font>
									</xsl:when>
									<xsl:otherwise>
										<a class="banner" href="{/root/gui/locService}/admin"><xsl:value-of select="/root/gui/strings/admin"/></a>
									</xsl:otherwise>
								</xsl:choose>
								|
								</xsl:if>
								<xsl:if test="string(/root/gui/session/userId)!=''">
									<xsl:choose>
										<xsl:when test="/root/gui/reqService='myaccount'">
											<font class="menu-active">My account</font>
										</xsl:when>
										<xsl:otherwise>
											<a class="banner" href="{/root/gui/locService}/myaccount">My account</a>
										</xsl:otherwise>
									</xsl:choose>
									|
								</xsl:if>
								<xsl:if test="string(/root/gui/session/userId)='' and
											     string(/root/gui/env/userSelfRegistration/enable)='true'">
									<xsl:choose>
										<xsl:when test="/root/gui/reqService='user.register.get'">
											<font class="menu-active"><xsl:value-of select="/root/gui/strings/register"/></font>
										</xsl:when>
										<xsl:otherwise>
											<a class="banner" href="{/root/gui/locService}/user.register.get"><xsl:value-of select="/root/gui/strings/register"/></a>
										</xsl:otherwise>
									</xsl:choose>
									|
								</xsl:if>
								<xsl:choose>
									<xsl:when test="/root/gui/reqService='about'">
										<font class="menu-active"><xsl:value-of select="/root/gui/strings/about"/></font>
									</xsl:when>
									<xsl:otherwise>
										<a class="banner" href="{/root/gui/locService}/about"><xsl:value-of select="/root/gui/strings/about"/></a>
									</xsl:otherwise>
								</xsl:choose>
								|
								<xsl:choose>
									<xsl:when test="/root/gui/language='fr'">
										<a class="banner" href="{/root/gui/url}/docs/fra/users" target="_blank"><xsl:value-of select="/root/gui/strings/help"/></a>
									</xsl:when>
									<xsl:otherwise>
										<a class="banner" href="{/root/gui/url}/docs/eng/users" target="_blank"><xsl:value-of select="/root/gui/strings/help"/></a>
									</xsl:otherwise>
								</xsl:choose>
							</td>
							<td class="rightEl">&#160;</td>
						</tr>
					</table>
				</td>
				<td>
					<div id="login">
						<xsl:choose>
							<xsl:when test="string(/root/gui/session/userId)!=''">
								<form name="logout" action="{/root/gui/url}/openWisLogout" method="post">
									<xsl:value-of select="/root/gui/strings/user"/>
									<xsl:text>: </xsl:text>
									<xsl:value-of select="/root/gui/session/name"/>
									<xsl:text> </xsl:text>
									<xsl:value-of select="/root/gui/session/surname"/>
									<xsl:text> </xsl:text>
									<button class="banner" onclick="goSubmit('logout')"><xsl:value-of select="/root/gui/strings/logout"/></button>
								</form>
							</xsl:when>
							<xsl:otherwise>
								<form name="login" action="{/root/gui/url}/openWisInit" method="post">
									<button class="banner" onclick="goSubmit('login')"><xsl:value-of select="/root/gui/strings/login"/></button>
								</form>
							</xsl:otherwise>
						</xsl:choose>       
					</div>
				</td>
			</tr>
			<tr id="banner-bottom">
				<td colspan="3">
					&#160;
				</td>
			</tr>
		</table>
	</xsl:template>

	<!--
	main html banner in a popup window
	-->
	<xsl:template name="bannerPopup">

		<table width="100%">

			<!-- title -->
			<!-- TODO : Mutualize with main banner template -->
			<tr class="banner">
				<td class="banner">
					<img src="{/root/gui/url}/images/openwis/header-left.jpg" alt="GeoNetwork opensource" align="top" />
				</td>
				<td align="right" class="banner">
					<img src="{/root/gui/url}/images/openwis/header-right.gif" alt="World picture" align="top" />
				</td>
			</tr>

			<!-- buttons -->
			<tr class="banner">
				<td class="banner-menu" colspan="2">
				</td>
			</tr>

			<tr class="banner">
				<td class="banner-login" colspan="2">
				</td>
			</tr>
		</table>
	</xsl:template>


</xsl:stylesheet>

