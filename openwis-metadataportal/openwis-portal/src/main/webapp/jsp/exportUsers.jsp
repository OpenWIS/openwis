<%@ page language="java" contentType="applicatiob/csv; charset=UTF-8"
        pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*" %>
<%@ page import="java.servlet.*" %>
<%@ page import="java.servlet.http.*"%>
<%@ page import="org.openwis.metadataportal.model.user.*"%>
<%@ page import="org.openwis.metadataportal.services.util.*"%>
<%
        String users = (String) request.getAttribute("users");
        String file_name = "users.txt";

        response.setContentType("application/csv");
        response.setHeader("content-disposition","filename="+file_name);

        PrintWriter outx = response.getWriter();
        outx.println(users);
        outx.flush();
        outx.close();

%>
