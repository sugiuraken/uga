<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ayudante.jp/tags/uga" prefix="uga" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Google Anayltics Sample (PAGE)</title>
</head>
<body>
	<uga:analytics hittype="page" doctitle="グーグル アナリティクス Page 漢字">
		<uga:customdim index = "1" value = "aaa" />
		<uga:customdim index = "20" value = "bbb" />
		<uga:custommet index = "1" value = "100" />
	</uga:analytics>
	<br />
	グールグ アナリティクス Page 漢字 <br />
	Version : <uga:version />
</body>
</html>

