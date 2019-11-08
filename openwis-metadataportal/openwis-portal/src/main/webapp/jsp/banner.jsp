<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>
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
				<td align="left" width="25%">
					<img src="<%= context.getBaseUrl() %>/images/openwis/header-left.jpg" alt="World picture" align="top">
				</td>
				<td align="center" width="50%">
					<img src="<%= context.getBaseUrl() %>/images/openwis/titre_site.png" alt="World picture" align="top">
				</td>
				<td align="right" width="25%">
					<img src="<%= context.getBaseUrl() %>/images/openwis/header-right.gif" alt="GeoNetwork opensource logo" align="top">
				</td>
			</tr>

			<!-- buttons -->
			<tr id="banner-menu">
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
				<td class="banner-menu">
					<table id="menu" cellpading="0" cellspacing="0" border="0" align="center">
						<tr>
							<td class="leftEl">&#160;</td>
							<td class="centerEl">
							<% if ("main.home".equals(context.getService())) { %>
										<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Home'))</script></font>
                            <% } else { %>
										<a class="banner" href="<%= locService %>/main.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Home'))</script></a>
                            <% } %>
								|
                            <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null 
                                  && "user".equals(portalType)) { %>
                               <% if ("myaccount".equals(context.getService())) { %>
											<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></font>
	                           <% } else { %>
    										<a class="banner" href="<%= locService %>/myaccount"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></a>
   	                           <% } %>
    								|
                            <% } %>
                            
                              <% if (context.getUserSession() != null && context.getUserSession().getUserId() == null 
                                  && selfRegistrationEnabled && "user".equals(portalType)) { %>
                               <% if ("user.register.get".equals(context.getService())) { %>
											<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register'))</script></font>
                               <% } else { %>
											<a class="banner" href="<%= locService %>/user.register.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register'))</script></a>
                               <% } %>
									|
                            <% } %>
                            <% if ("about.home".equals(context.getService())) { %>
										<font class="menu-active"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.About'))</script></font>
                            <% } else { %>
										<a class="banner" href="<%= locService %>/about.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.About'))</script></a>
                            <% } %>
								|
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
				<td>
					<div id="login">

                            <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) { 
                            
                               String entityID = (String) context.getUserSession().getProperty("idpEntityID");
                               String nameForHtml = StringEscapeUtils.escapeHtml(context.getUserSession().getName());
                               String surnameForHtml = StringEscapeUtils.escapeHtml(context.getUserSession().getSurname());
                            %>

								<form name="logout" action="<%= context.getBaseUrl() %>/openWisLogout" method="post" id="loginFormEl">
									<%= nameForHtml %> <%= surnameForHtml %> (<%= entityID %>)
									<button type="submit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Logout'))</script></button>
                                    <input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
								</form>
								
                            <% } else { %>
                            	<table>
                            	<tr>
                            	<td  width="55%" align="right">
								<form name="login" action="<%= context.getBaseUrl() %>/openWisInit" method="post">
									<button type="submit"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></button>
									<input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
								</form>
								<td>&nbsp;&nbsp;<td/>
								<td>
									<a class="banner" href="<%= locService %>/user.choose.domain" id="loginFormEl"><script type="text/javascript">document.write(Openwis.i18n('Banner.Choose.Domain'))</script></a>                          	
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
		
		

		