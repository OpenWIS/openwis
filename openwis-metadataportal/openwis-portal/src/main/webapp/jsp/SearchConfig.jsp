<%@page
	import="org.openwis.metadataportal.common.configuration.OpenwisSearchConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>SearchConfig</title>


</head>


<%
   if (Boolean.parseBoolean(request.getParameter("reset"))) {
      OpenwisSearchConfig.reset();
   } else {
      String newTitle = request.getParameter("title");
      if (newTitle != null && newTitle.length() > 0) {
         OpenwisSearchConfig.setTitleWeight(Integer.parseInt(newTitle));

      }

      String newAbstract = request.getParameter("abstract");
      if (newAbstract != null && newAbstract.length() > 0) {
         OpenwisSearchConfig.setAbstractWeight(Integer.parseInt(newAbstract));

      }
      String newKeywords = request.getParameter("keywords");
      if (newKeywords != null && newKeywords.length() > 0) {
         OpenwisSearchConfig.setKeywordsWeight(Integer.parseInt(newKeywords));

      }
   }
%>


<body>
	<h1>Config Search</h1>


	<form action="SearchConfig.jsp" method="get">

		<table>
			<tr>
				<td>title</td>
				<td><input name="title"
					value="<%=OpenwisSearchConfig.getTitleWeight()%>" /></td>
			</tr>
			<tr>
				<td>abstract</td>
				<td><input name="abstract"
					value="<%=OpenwisSearchConfig.getAbstractWeight()%>" /></td>
			</tr>
			<tr>
				<td>keywords</td>
				<td><input name="keywords"
					value="<%=OpenwisSearchConfig.getKeywordsWeight()%>" /></td>
			</tr>
		</table>
		
		<input type="submit" value="OK" />
		<input type="button" value="Reset" onclick="location.replace('SearchConfig.jsp?reset=true');"/>
	
	</form>


</body>
</html>