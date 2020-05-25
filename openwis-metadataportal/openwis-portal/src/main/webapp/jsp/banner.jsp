<%@page import="java.util.List"%>
<%@page import="org.openwis.metadataportal.common.configuration.ConfigurationConstants"%>
<%@page import="org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
List<String> langList = OpenwisMetadataPortalConfig.getList(ConfigurationConstants.LANGUAGE_LIST);
String serviceName = context.getService();
%>
<% if ("user".equals(portalType)) { %>
        <%@include file="banner-dss.jsp"%>
<%} else {%>
        <%@include file="banner-admin.jsp"%>
  <%  } %>

