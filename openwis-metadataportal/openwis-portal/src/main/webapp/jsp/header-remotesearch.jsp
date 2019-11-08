<%@page import="javax.servlet.http.HttpSession"%>
<%@page import="jeeves.server.UserSession"%>
<%@page import="org.openwis.metadataportal.services.login.LoginConstants"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="org.apache.commons.lang.StringEscapeUtils"%>


	<script type="text/javascript" language="JavaScript">
	    var remoteSearch = {};
	    
	    Ext.QuickTips.init();

	    <%
	    HttpSession httpSession = request.getSession();
	    UserSession userSession = (UserSession) httpSession.getAttribute(LoginConstants.SESSION);
	    String relayState = (String) userSession.getProperty(LoginConstants.RELAY_STATE);
	    if (StringUtils.isNotBlank(relayState) && !relayState.startsWith(LoginConstants.LANG))  {
		    String[] relayStateResult = relayState.split(LoginConstants.RELAY_STATE_SEPARATOR);
	    	if (relayStateResult.length >= 2) { %>
	    		remoteSearch.type = "<%= relayStateResult[0]%>";
	    		remoteSearch.urn = "<%= relayStateResult[1] %>";
		    <%
		    }
	    	if (relayStateResult.length == 4) { %>
		    	remoteSearch.backupRequestId = "<%= relayStateResult[2] %>";
		    	remoteSearch.backupDeployment = "<%= relayStateResult[3] %>";
			<%
		    }
		    %>
	    	remoteSearch.url = "<%=request.getContextPath()%>" + "/srv/en/main.search.embedded";
	    	<% userSession.removeProperty(LoginConstants.RELAY_STATE); %>
	   	<%} else if (userSession.getProperty(LoginConstants.MAIN_SEARCH) != null) {
	   		String connection = (String)(userSession.getProperty(LoginConstants.NOT_CONNECTED_TO_CONNECTED));
	   		if (LoginConstants.NOT_CONNECTED_TO_CONNECTED.equals(connection)) {%>
	   			remoteSearch.url = "<%=request.getContextPath()%>" + "/srv/en/main.search.embedded";
	   			remoteSearch.connection = "<%=LoginConstants.NOT_CONNECTED_TO_CONNECTED%>";
	   		<%}
	   		userSession.removeProperty(LoginConstants.NOT_CONNECTED_TO_CONNECTED);
	   	}
	   	%>
	    
	  <%
	  	String urnParam = request.getParameter("urn");
	  	if (urnParam != null && urnParam.length() > 0) {
	  %>
		   	remoteSearch.url = "<%=request.getContextPath()%>" + "/srv/en/main.search.embedded";
		   	remoteSearch.urn = "<%= StringEscapeUtils.escapeJavaScript(urnParam) %>";
	  <%
	  	}
	  %>
	   	
	</script>