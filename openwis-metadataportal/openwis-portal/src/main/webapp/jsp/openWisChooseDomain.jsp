<%--
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
   Copyright (c) 2008 Sun Microsystems Inc. All Rights Reserved
  
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.

   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.

   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"

   $Id: fedletSSOInit.jsp,v 1.8 2009/06/24 23:05:30 mrudulahg Exp $

--%>
<%@ page import="com.sun.identity.shared.debug.Debug" %>
<%@ page import="com.sun.identity.saml.common.SAMLUtils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Constants" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Utils" %>
<%@ page import="com.sun.identity.saml2.common.SAML2Exception" %>
<%@ page import="com.sun.identity.saml2.meta.SAML2MetaManager" %>
<%@ page import="com.sun.identity.saml2.profile.SPCache" %>
<%@ page import="com.sun.identity.saml2.profile.SPSSOFederate" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.StringTokenizer" %>
<%@page import="org.openwis.metadataportal.services.login.LoginConstants"%>

<%--
    openWisChooseDomain.jsp initiates the Single Sign-On at the Service Provider.

--%>


<%@page import="jeeves.server.context.ServiceContext"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="jeeves.server.context.ServiceContext"%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
   <head>
	<% String title = "OpenWIS Choose Domain";%>
	<%@include file="header-common.jsp" %>
   </head>
   <body>
      <div id="header">
        <%@include file="banner.jsp" %>

		<link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-homepage.css">
        <link rel="stylesheet" type="text/css" href="<%= context.getBaseUrl() %>/css/openwis-html-content.css">
		    
		<%
		if (devMode) {
		%>
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Viewport.js"></script>
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Init.js"></script>
		<%
		} else {
		%>  
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Viewport.js"></script>
			<script type="text/javascript" src="<%= context.getBaseUrl() %>/scripts/Openwis/lib/Openwis/Common/HTMLContent/Init.js"></script>
		<%
		}
		%>  

      </div>
      <div id="contentElement" align=center>
    
    <%
    // Retreive the Request Query Parameters
    // metaAlias and idpEntiyID are the required query parameters
    // metaAlias - Service Provider Entity Id
    // idpEntityID - Identity Provider Identifier
    // Query parameters supported will be documented.
	
    String idpEntityID = null;
    String metaAlias= null;
    Map paramsMap = null;
    List idpEntities = new ArrayList();
    try {
		//get the preferred idp
		SAML2MetaManager manager = new SAML2MetaManager();
		idpEntities = manager.getAllRemoteIdentityProviderEntities("/");
		// Choose domain
    } catch (SAML2Exception sse) {
        SAML2Utils.debug.error("Error sending AuthnRequest " , sse);
        SAMLUtils.sendError(request, response,
            response.SC_BAD_REQUEST, "requestProcessingError", 
            SAML2Utils.bundle.getString("requestProcessingError") + " " +
            sse.getMessage());
    } catch (Exception e) {
        SAML2Utils.debug.error("Error processing Request ",e);
        SAMLUtils.sendError(request, response,
            response.SC_BAD_REQUEST, "requestProcessingError",
            SAML2Utils.bundle.getString("requestProcessingError") + " " +
            e.getMessage());
    }
		%>

		<h4><script type="text/javascript">document.write(Openwis.i18n('Choose.Domain.msg'))</script></h4>
		<br>
		<br>
		<br>
		<FORM METHOD="POST" ACTION="<%= context.getBaseUrl() %>/openWisDomainSelected">
			<SELECT NAME="radio-value">
			<%
			  for(Iterator it = idpEntities.iterator(); it.hasNext();) {
			     String v = it.next().toString();
				 %>
				 <option value="<%=v %>"><%=v %></option>
			<%
			  }
			%>
			  </SELECT>
			<% if (request.getAttribute(LoginConstants.RELAY_STATE) != null) { %>
			  <INPUT TYPE="hidden" NAME="RelayState"
					 VALUE ="<%= request.getAttribute(LoginConstants.RELAY_STATE)%>" />
            <%  } %>
              <INPUT TYPE="hidden" NAME="lang"
                     VALUE ="<%= context.getLanguage() %>" />
			  <br>
			  <br>
			  <br>
			  <INPUT TYPE="submit" value="Validate">
		</FORM>
      </div>
   </body>
</html>



