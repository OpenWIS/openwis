<%@page import="java.util.List"%>
<%@page import="org.openwis.metadataportal.common.configuration.ConfigurationConstants"%>
<%@page import="org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
List<String> langList = OpenwisMetadataPortalConfig.getList(ConfigurationConstants.LANGUAGE_LIST);
String serviceName = context.getService();
%>
		<table width="100%" cellspacing="0">

			<!-- title -->
			<tr id="banner-logo">
				<td align="left" width="33%" style="margin-top:30px;">
					<%-- <img src="<%= context.getBaseUrl() %>/images/openwis/header-left.png" alt="World picture" align="top"> --%>
					<img src="<%= context.getBaseUrl() %>/images/openwis/logo-asmc.png" alt="World picture" align="top">
				</td>
				<td align="center" width="33%" style="margin-top:50px;">
				  <a href="<%= locService %>/about.home" style="color: #808080;">ABOUT ASMC</a>&nbsp;
				  <a href="" style="color: #808080;">CONTACT US</a>
				</td>
				<%-- <td align="center" width="0%" style="margin-top:30px;">
					<!-- <img src="<%= context.getBaseUrl() %>/images/openwis/titre_site.png" alt="World picture" align="top"> -->
				</td> --%>
				<td align="right" width="33%">
					<%-- <img src="<%= context.getBaseUrl() %>/images/openwis/header-right.png" width="240" alt="GeoNetwork opensource logo" align="top"> --%>
					<img src="<%= context.getBaseUrl() %>/images/openwis/logo-asean.png" width="150" alt="GeoNetwork opensource logo" align="top">
				</td>
			</tr>
			<tr style="background-color:white; font-weight: bold; font: arial; color:#808080; font-size: 20px;">
			 <td align="left" width="83%" >&nbsp;&nbsp;DATA CATALOG&nbsp;&nbsp;&nbsp;&nbsp;<a href="<%= locService %>/help.home" style="color: #808080;">HELP</a></td>
			 <td align="center" width="17%"><a href="<%= context.getBaseUrl() %>/openWisInit" style="color: #808080;">LOGIN</a></td>
			</tr>
			<tr style="background-color:#ECF0F1; font-weight: bold;">
			  <td>&nbsp;&nbsp;&nbsp;&nbsp;ASMC /</td>
			  <td>
				  <ul>
					  <li class="dropdown">
					     <a href="javascript:void(0)" style="color: black;">&nbsp;DATA CATALOG /</a>
					    <div class="dropdown-content">
					       <a class="prod" id="prod1" onclick="showItem('prod1')" href="#">Satellite Images (JPSS 1/NOAA-20)</a>
					       <a class="prod" id="prod2" onclick="showItem('prod2')" href="#">Satellite Images (Suomi-NPP)</a>
					       <a class="prod" id="prod3" onclick="showItem('prod3')" href="#">Satellite Images (AQUA)</a>
					       <a class="prod" id="prod4" onclick="showItem('prod4')" href="#">Satellite Images (TERRA)</a>
					       <a class="prod" id="prod5" onclick="showItem('prod5')" href="#">Regional Haze Situation</a>
					       <a class="prod" id="prod6" onclick="showItem('prod6')" href="#">Hotspot Reports (JPSS 1/NOAA-20)</a>
					       <a class="prod" id="prod7" onclick="showItem('prod7')" href="#">Hotspot Reports (Suomi-NPP)</a>
					       <a class="prod" id="prod8" onclick="showItem('prod8')" href="#">Hotspot Reports (AQUA)</a>
					       <a class="prod" id="prod9" onclick="showItem('prod9')" href="#">Hotspot Reports (TERRA)</a>
					       <a class="prod" id="prod10" onclick="showItem('prod10')" href="#">Smoke Haze Dispersion Model</a>
					    </div>
					  </li>
				  </ul>
			  </td>
			  <td id="produit">
				<script type="text/javascript">
					/* document.write(produit) */
					function showItem(id){
						document.getElementById("produit").innerHTML=document.getElementById(id).innerHTML;
						document.getElementById("ext-comp-1019").value=document.getElementById(id).innerHTML;
						document.getElementById("ext-gen105").click();
					}
				</script>
			  </td>
			</tr>

			<!-- buttons -->
			<%-- <tr id="banner-menu">
				<td class="banner-menu">
					<table id="menu" cellpading="0" cellspacing="0" border="0" align="center">
						<tr>
							<td class="leftEl"><div id="menuIcon">&#9776;</div></td>
							<td class="centerEl">
							<% if ("main.home".equals(context.getService())) { %>
										<font class="menu-active">
											<script type="text/javascript">
												document.write(Openwis.i18n('Common.Banner.Home'))
											</script>
										</font>
                            <% } else { %>
										<a class="banner" href="<%= locService %>/main.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Home'))</script></a>
                            <% } %>
								
                            <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null 
                                  && "user".equals(portalType)) { %>
                               <% if ("myaccount".equals(context.getService())) { %>
											<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></font>
	                           <% } else { %>
    										<a class="banner" href="<%= locService %>/myaccount"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></a>
   	                           <% } %>
    								
                            <% } %>
                            
                              <% if (context.getUserSession() != null && context.getUserSession().getUserId() == null 
                                  && selfRegistrationEnabled && "user".equals(portalType)) { %>
                               <% if ("user.register.get".equals(context.getService())) { %>
											<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register'))</script></font>
                               <% } else { %>
											<a class="banner" href="<%= locService %>/user.register.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register'))</script></a>
                               <% } %>
									
                            <% } %>
                            <% if ("about.home".equals(context.getService())) { %>
										<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.About'))</script></font>
                            <% } else { %>
										<a class="banner" href="<%= locService %>/about.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.About'))</script></a>
                            <% } %>
								
                            <% if ("help.home".equals(context.getService())) { %>
                                        <font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Help'))</script></font>
                            <% } else { %>
                                        <a class="banner" href="<%= locService %>/help.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Help'))</script></a>
                            <% } %>
							</td>
							<td class="rightEl">&#160;</td>
						</tr>
					</table>
				</td>
				<td align="left">
				location.replace('../' + this.options[this.selectedIndex].value + '/main.home);
				    <select class="banner-content content" 
				        onchange="location.replace('../' + this.options[this.selectedIndex].value + '/<%= serviceName %>');">
			        <% for (String lang : langList) {
			             String[] langParams = lang.split("/");
			             String langValue = langParams[0];
				         String langLabel = langParams[1];
				         String selected = "";
				         if (context.getLanguage().equals(langValue)) {
				            selected = "selected='selected'";
				         }
			         %>
					   <option value="<%= langValue %>" <%= selected %>><%= langLabel %></option>
                     <% } %>
					</select>         
				</td>
				<td>
					<div id="login">

                            <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) { 
                            
                               String entityID = (String) context.getUserSession().getProperty("idpEntityID");
                            %>

								<form name="logout" action="<%= context.getBaseUrl() %>/openWisLogout" method="post" id="loginFormEl">
									<div class="logoutDiv">
									<%= context.getUserSession().getName() %> <%= context.getUserSession().getSurname() %><br/> (<%= entityID %>)
									</div>
									<div class="logoutDiv">
										<button type="submit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Logout'))</script></button>
										<i class="iconIOS7-bt_link_on"></i>
										<input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
									</div>
								</form>
								
                            <% } else { %>
                            	<table>
                            	<tr>
                            	<td  width="55%" align="right">
								<form name="login" action="<%= context.getBaseUrl() %>/openWisInit" method="post">
									<button type="submit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></button>
									<i class="iconIOS7-bt_link_on"></i>
									<input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
								</form><!--  
								<td>
									<a class="banner" href="<%= locService %>/user.choose.domain" id="loginFormEl"><script type="text/javascript">document.write(Openwis.i18n('Banner.Choose.Domain'))</script></a>                          	
									<i class="iconIOS7-bt_choose_off"></i>
								</td>
								-->
								</tr>
                            	</table>
                            <% } %>
					</div>
				</td>
				
			</tr> --%>
			<tr id="banner-bottom">
				<td colspan="3">
					&#160;
				</td>
			</tr>
		</table>
		
		

