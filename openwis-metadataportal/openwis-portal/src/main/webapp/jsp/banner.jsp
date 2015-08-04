<%@page import="java.util.List"%>
<%@page import="org.openwis.metadataportal.common.configuration.ConfigurationConstants"%>
<%@page import="org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
List<String> langList = OpenwisMetadataPortalConfig.getList(ConfigurationConstants.LANGUAGE_LIST);
String serviceName = context.getService();
%>

<script>

	function login(){

		var frm = document.getElementById("login");
		frm.submit();
	}

	function logout(){
		var frm = document.getElementById("loginFormEl");
		frm.submit();
	}

	function changeLang( lang ){
		location.replace( lang );
	}
	
	
	
	function openPop(method){
		var url;
		if(method == 'kma'){
			url = Openwis.i18n('Common.Url.kma');
		}else if(method == 'openwis'){
			url = Openwis.i18n('Common.Url.openwis');
		}else {
			url = Openwis.i18n('Common.Url.kma');
		}
		//window.open(url,"", "top=100px, left=100px, width=800px, height=800px, scrollbars=yes");
		window.open(url, method, "fullscreen=no, toolbar=yes, location=yes, directories=no, status=no, menubar=yes,scrollbars=yes ") ;
	}
	
</script>

<div class="header_top" >
	<h1><a href="<%= locService %>/main.home" ><img src="<%= context.getBaseUrl() %>/images/openwis/top_logo_giscSeoul.png" alt="GISC Seoul - Global Information System Center" /></a></h1>
	<div class="util"><div>
		<% if (context.getUserSession() != null && context.getUserSession().getUserId() != null) { 
                      
		   String entityID = (String) context.getUserSession().getProperty("idpEntityID");
		%>
			<form name="logout" action="<%= context.getBaseUrl() %>/openWisLogout" method="post" id="loginFormEl">				
				<p class="user_name" >Welcome,<strong><%= context.getUserSession().getName() %> <%= context.getUserSession().getSurname() %><!-- (<%= entityID %>)--></strong></p>
				<a class="user_logout" href="javascript:logout()"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Logout'))</script></a>
				<input type="hidden" name="lang" value="<%= context.getLanguage() %>"/>
			
			
		<% } else { %>
			<form name="login" action="<%= context.getBaseUrl() %>/openWisInit" method="post" id="login">
				<a class="user_login" href="javascript:login()"><script type="text/javascript">document.write(Openwis.i18n('Common.Btn.Login'))</script></a>
				<input type="hidden" name="lang" id="lang" value="<%= context.getLanguage() %>"/>
			
		<% } %>
		
<%-- 		<a class="banner" href="javascript:changeLang('../ko/<%= serviceName %>')" id="loginFormEl"><img src="<%= context.getBaseUrl() %>/images/openwis/util_korean.png" alt="Korea" /></a>
		<a class="banner lastChild" href="javascript:changeLang('../en/<%= serviceName %>')"><img src="<%= context.getBaseUrl() %>/images/openwis/util_english.png" alt="English" /></a>
--%>
		<ul class="changeLang">
			 <% for (String lang : langList) {
			             String[] langParams = lang.split("/");
			             String langValue = langParams[0];
				         String langLabel = langParams[1];
				         String selected = "";
				         if (context.getLanguage().equals(langValue)) {
				            selected = "class='selected'";
				         }
			         %>
        	
			<li <%= selected %>><a href="javascript:changeLang('../<%= langValue %>/<%= serviceName %>')" ><%= langLabel %></a></li>
        	<% } %>
		</ul>		
        	</form>
	</div></div>
	<div class="gnb">
		
		
		
					<% if ("main.home".equals(context.getService())) { %>
						<span><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Home'))</script></span>
                    <% } else { %>
						<a class="banner" href="<%= locService %>/main.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Home'))</script></a>
                    <% } %>
					
                    <% if (context.getUserSession() != null && context.getUserSession().getUserId() != null 
                                && "user".equals(portalType)) { %>
                        <% if ("myaccount".equals(context.getService())) { %>
							<span><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></span>
                        <% } else { %>
  							<a class="banner" href="<%= locService %>/myaccount"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.MyAccount'))</script></a>
 	                    <% } %>
  						
                    <% } %>
                          
                    <% if (context.getUserSession() != null && context.getUserSession().getUserId() == null 
                                && selfRegistrationEnabled && "user".equals(portalType)) { %>
                    	<% if ("user.register.get".equals(context.getService())) { %>
							<span><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register'))</script></span>
                        <% } else { %>
							<a class="banner" href="<%= locService %>/user.register.get"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Register'))</script></a>
                        <% } %>
						
                    <% } %>
                    <% if ("about.home".equals(context.getService())) { %>
						<span><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.About'))</script></span>
                    <% } else { %>
						<a class="banner" href="<%= locService %>/about.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.About'))</script></a>
                    <% } %>
					
                    <% if ("help.home".equals(context.getService())) { %>
                        <span><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Help'))</script></span>
                    <% } else { %>
                        <a class="banner" href="<%= locService %>/help.home"><script type="text/javascript">document.write(Openwis.i18n('Common.Banner.Help'))</script></a>
                    <% } %>

		
	</div>
	<ul class="logoList">
		<li><a href="javascript:openPop('kma');" ><img src="<%= context.getBaseUrl() %>/images/openwis/top_logo_kma.png" alt="KMA" /></a></li>
		<li><a href="javascript:openPop('openwis');" ><img src="<%= context.getBaseUrl() %>/images/openwis/top_logo_openwis.png" alt="OpenWIS" /></a></li>
		
	</ul>	
</div>