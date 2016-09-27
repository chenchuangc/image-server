<%@page import="yao.util.date.DateUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>上传成功</title>
<script type="text/javascript">
	document.domain = "wn518.com";
	function success(url) {
		var fn;
		eval("fn = window.opener.${callback}");
		fn(url);
		window.opener = null;
		window.close();
	}
</script>
</head>
<body>
	<img src="${url}" />
	<br /> 图片地址：
	<input value="${url}" style="width: 300px;" />
	<br />
	<input type="button" value="确定" onclick="success('${url}');" />
</body>
</html>