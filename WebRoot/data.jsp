<%@ page language="java" import="java.util.*,spring.*" pageEncoding="UTF-8"%>

<%
String content = request.getParameter("content");
String from = request.getParameter("from");
String to = request.getParameter("to");

String result = BaiduTranslateDemo.translate(content,from,to);
out.println(result);
%>


