<%@page import="java.util.List"%>
<%@page import="org.openwis.metadataportal.common.configuration.ConfigurationConstants"%>
<%@page import="org.openwis.metadataportal.common.configuration.OpenwisMetadataPortalConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
List<String> langList = OpenwisMetadataPortalConfig.getList(ConfigurationConstants.LANGUAGE_LIST);
String serviceName = context.getService();
%>
<div class="dss-logo-container">
    <div>
        <div class="dss-logo">
            <div class="dss-logo-left dss-logo-common">
                <div>
                    <img src="<%= context.getBaseUrl() %>/images/logos/logo-asmc.png" alt="World picture" align="top">
                </div>
            </div>
            <div class="dss-logo-middle dss-logo-common">
                <div class="dss-logo-middle-container dss-logo-common">
                    <div>
                        <a class="dss-logo-middle-link" href="<%= locService %>/about.home">About ASMC</a>&nbsp;
                    </div>
                    <div>
                        <a class="dss-logo-middle-link" href="">Contact Us</a>
                    </div>
                </div>
            </div>
            <div class="dss-logo-right dss-logo-common">
                <div class="dss-logo-right-container">
                    <img src="<%= context.getBaseUrl() %>/images/logos/logo-asean.png" width="150"
                         alt="GeoNetwork opensource logo" align="top">
                </div>
            </div>
        </div>
        <div class="dss-main-nav">
            <div class="dss-nav-catalog"><a href="<%= locService %>/main.home">DATA CATALOG</a></div>
            <div><a href="<%= locService %>/help.home">HELP</a></div>
            <div class="dss-nav-login"><a href="<%= context.getBaseUrl() %>/openWisInit">LOGIN</a></div>
        </div>
    </div>
</div>

