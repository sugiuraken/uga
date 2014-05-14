<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.ayudante.jp/tags/uga" prefix="uga" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Google Anayltics Sample (ECOM)</title>
</head>
<body>
		<uga:analytics hittype="ecom" doctitle="GoogleAnalytics Page">
	     <uga:transaction id = "0D563" affiliation = "westernWear" revenue= "50.0" shipping = "32.0" tax = "12.0">
          <uga:item price = "300"
                     quantity = "2"
                     code = "u3eqds34"
                     name = "sofa"
                     variation = "furniture" />
          <uga:item price = "400"
                     quantity = "5"
                     code = "u3eqds99"
                     name = "chair"
                     variation = "etc" />
	     </uga:transaction>
	</uga:analytics>
</body>
</html>