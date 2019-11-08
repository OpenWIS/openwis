<%@ page language="java" contentType="text/XML; charset=UTF-8"
	pageEncoding="UTF-8"
%><%@ page import="java.util.*"
%><%@ page import="org.openwis.metadataportal.model.datapolicy.*" 
%><%@ page import="org.openwis.metadataportal.services.util.*" 
%><%
	List<DataPolicy> dataPolicies = (List<DataPolicy>) request.getAttribute("dataPolicies");
	String siteName = (String) request.getAttribute("siteName");
	String siteId = (String) request.getAttribute("siteId");
%><%="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"%>
<!-- Exported DataPolicies -->
<!-- From <%=siteName%> - <%=siteId%> -->
<!-- At <%=DateTimeUtils.format(DateTimeUtils.getUTCDate())%> -->
<DataPolicies><%
	for (DataPolicy dp : dataPolicies) { 
%>
	<DataPolicy name="<%=dp.getName()%>"><%
	if (dp.getDescription() != null) { 
%>
		<Description><%=dp.getDescription()%></Description><%
	} else { 
	%>
	<!-- No Description --><%
	} 
	%><%-- Alias --%><%
	if (!dp.getAliases().isEmpty()) {%>
		<!-- Aliases --><%
		for (DataPolicyAlias alias : dp.getAliases()) { %>
		<Alias name="<%=alias.getAlias()%>" /><%
		}
	} else { %>
		<!-- No Aliases --><%
	} 
%><%--Groups --%>
  	<% if (!dp.getDpOpPerGroup().isEmpty()) { %>
  		<!-- Groups and operations --><%
  	for (DataPolicyOperationsPerGroup grpOpe : dp.getDpOpPerGroup()) {
  		if (grpOpe.getGroup().isGlobal()) {%>
		<Group name="<%=grpOpe.getGroup().getName()%>" isGlobal="<%=grpOpe.getGroup().isGlobal()%>"><%
  	 		for (DataPolicyGroupPrivileges priv : grpOpe.getPrivilegesPerOp()) {
  	 	  	 if (priv.isAuthorized()) { %>
			<Operation name="<%=priv.getOperation().getName()%>"/><%
	  	 		} else { %>
	  	 	<!-- <%=priv.getOperation().getName()%> not allowed --><%
	  		 	} 
  	 	  }%>
  	 	</Group><%
  			}
  		}
  	} else { %>
 	 	<!-- No Groups --><%
  	} %>
	</DataPolicy><%
  }%>
</DataPolicies>