<?xml version="1.0" encoding="shift-jis" ?>
<%@ page language="java" contentType="text/html;charset=Shift_JIS" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>
<%@ taglib uri="http://www.ayudante.jp/tags/uga" prefix="uga" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=Shift_JIS" />
<title>Google Anayltics Sample (PAGE)</title>
</head>
<body>

<%
	String url = "PageTrackingSampleSJIS.jsp?"
				     + "utm_campaign=" + URLEncoder.encode("セールセール9", "UTF-8") + "&"
				     + "utm_source=" + URLEncoder.encode("レター9", "UTF-8") + "&"
				     + "utm_medium=" + URLEncoder.encode("メール9", "UTF-8") + "&"
				     + "utm_term=" + URLEncoder.encode("アっクセス解析9", "UTF-8") + "&"
				     + "utm_content=" + URLEncoder.encode("テキストリンク9", "UTF-8");
%>
	<a href="PageTrackingSampleSJIS.jsp?utm_campaign=セールセール9&utm_source=レター9&utm_medium=メール9&utm_term=アっクセス解析9&utm_content=テキストリンク9">Page Tracking Sample[No Encode]</a>
	<br />
	<a href="<%= url %>">Page Tracking Sample[UTF-8 Encode]</a>
	<br />
</body>
</html>

