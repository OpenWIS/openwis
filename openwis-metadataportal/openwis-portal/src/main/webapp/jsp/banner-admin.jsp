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
				<td align="left" width="50%">
					<img src="<%= context.getBaseUrl() %>/images/openwis/header-left.png" alt="World picture" align="top">
				</td>
				<td align="center" width="0%">
					<!-- <img src="<%= context.getBaseUrl() %>/images/openwis/titre_site.png" alt="World picture" align="top"> -->
				</td>
				<td align="right" width="48%">
					<img src="<%= context.getBaseUrl() %>/images/openwis/header-right.png" width="240" alt="GeoNetwork opensource logo" align="top">
				</td>
			</tr>

			<!-- buttons -->
			<tr id="banner-menu">
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
				<%--location.replace('../' + this.options[this.selectedIndex].value + '/main.home); --%>
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
								    <%if (request.getAttribute("userLastLogin") !=  null) {%>
								        <div><%=request.getAttribute("userLastLogin")%></div>
								     <%}%>
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
                            	    <% if ("user".equals(portalType) && !context.isDebug() ) { %>
                            	        <div class="loginDiv">
                            	   		    <a class="banner" href="<%= locService %>/user.loginCaptcha.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></a>
		    						    </div>
		    						 <%} else {%>
                                        <div class="loginDiv">
                                           <a class="banner" href="<%= context.getBaseUrl() %>/openWisInit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></a>
                                        </div>
                                    <% } %>
								<td>
									<a class="banner" href="<%= locService %>/user.choose.domain" id="loginFormEl"><script type="text/javascript">document.write(Openwis.i18n('Banner.Choose.Domain'))</script></a>                          	
									<i class="iconIOS7-bt_choose_off"></i>
								</td>
								</tr>
                            	</table>
                            <% } %>
					</div>
				</td>
				
			</tr>
			<tr id="banner-bottom">
				<td colspan="3">
					&#160;
				</td>
			</tr>
		</table>
		
		

		