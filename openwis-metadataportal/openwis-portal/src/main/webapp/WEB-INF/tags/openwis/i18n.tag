<%@ tag import="java.util.ResourceBundle"%><%@ tag import="java.text.MessageFormat"%><%@ attribute name="key" required="true" type="java.lang.String"%><%
	ResourceBundle bundle = (ResourceBundle) request.getAttribute("message");
	if (bundle==null || key==null) {
      %><%="!!! No bundle or key define !!!"%><%
	} else {
	   try {
	      String msg = bundle.getString(key);
	      %><%=msg%><%
	   } catch (Exception e) { 
	      %><%=MessageFormat.format("!!! {0} !!!",key)%><%
	   }
	}
%>