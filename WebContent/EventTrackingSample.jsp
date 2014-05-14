<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ayudante.jp/tags/uga" prefix="uga" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Google Anayltics Sample (EVENT REDIRECT)</title>
</head>
<body>

<%
	String strRequestUrl = request.getRequestURL().toString();
	java.net.URI objUrl = null;
	objUrl = new java.net.URI(strRequestUrl);

	String strRedirectUrl = objUrl.resolve("index.jsp").toString();
%>
	<a href="<uga:event_redirect redirecturi='<%= strRedirectUrl %>' category = 'user' action = 'click'  label = 'buy' value = '32'/>"><img src="eclipse32.png" /></a>
</body>
</html>