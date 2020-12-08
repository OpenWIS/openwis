<%@ tag import="java.util.Set"%>
<%@ tag import="org.openwis.metadataportal.model.datapolicy.OperationEnum"%>
<%@ tag import="org.apache.commons.lang.StringUtils"%>
<%@ tag import="org.apache.commons.lang.StringEscapeUtils"%>
<%@ tag import="org.fao.geonet.kernel.search.IndexField"%>
<%@ attribute name="doc" required="true" rtexprvalue="true" type="org.openwis.metadataportal.kernel.search.query.SearchResultDocument" %>
<%@ attribute name="ops" required="true" rtexprvalue="true" type="java.util.Set" %>
<%@ attribute name="url" required="false" type="java.lang.String" %>
<%@ attribute name="localDataSource" required="false" type="java.lang.String" %>
<%@ attribute name="forceUrl" required="false" type="java.lang.Boolean" %>
<%
   boolean isCacheEnable = ((Boolean) request.getAttribute("isCacheEnable")).booleanValue();
   boolean isGlobal = "true".equals(doc.getField(IndexField.IS_GLOBAL));
   boolean isBlacklisted = ((Boolean) request.getAttribute("isBlacklisted")).booleanValue();
   String uuid = (String) doc.getField(IndexField.UUID);
   String username = (String) request.getAttribute("username");
   String gtsCategory  = doc.getFieldAsString(IndexField.GTS_CATEGORY);

   if (username == null) {
      %> new Openwis.Utils.MessageMustLogin();<%
   } else if (ops==null || !ops.contains(OperationEnum.DOWNLOAD)) {
      %> new Openwis.Utils.MessageBoxAccessDenied({urn : "<%=uuid%>"});<%
   } else if (forceUrl!=null && forceUrl.booleanValue()) {
      %>window.open("<%=url%>")<%
   } else if (isCacheEnable && isGlobal) {
      if (isBlacklisted) {%>
	   	new Openwis.Utils.MessageBox.displayErrorMsg("Could not create AdHoc request, the user " + "<%=username%>" + " is blacklisted.");
	   <%} else { %>
	   	doSubscriptionFromCache("<%=StringEscapeUtils.escapeJavaScript(uuid)%>", "<%= StringEscapeUtils.escapeJavaScript(gtsCategory) %>", null);<%
	   }
   } else if (isCacheEnable && StringUtils.isNotBlank(url)) {
      %>window.open("<%=url%>")<%
   } else {
      if (isBlacklisted) {%>
	   	new Openwis.Utils.MessageBox.displayErrorMsg("Could not create AdHoc request, the user " + "<%=username%>" + " is blacklisted.");
	   <%} else { %>
	   	doSubscription("<%=StringEscapeUtils.escapeJavaScript(uuid)%>", "<%= StringEscapeUtils.escapeJavaScript(gtsCategory) %>");<%
	   }
   }
%>