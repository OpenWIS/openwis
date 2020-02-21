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
				  <a href="">ABOUT ASMC</a>&nbsp;
				  <a href="">CONTACT US</a>
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
			 <td align="left" width="83%" >&nbsp;&nbsp;DATA CATALOG&nbsp;&nbsp;&nbsp;&nbsp;HELP</td>
			 <td align="center" width="17%">LOGIN</td>
			</tr>
			<tr style="background-color:#ECF0F1; font-weight: bold;">
			  <td>&nbsp;&nbsp;&nbsp;&nbsp;ASMC /</td>
			  <td>&nbsp;DATA CATALOG /</td>
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