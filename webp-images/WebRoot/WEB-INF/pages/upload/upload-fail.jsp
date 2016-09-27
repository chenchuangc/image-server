<%@page import="yao.util.web.HTMLUtil"%>
<%@page import="java.io.PrintWriter"%>
<%@page import="java.io.StringWriter"%>
<%@page import="yao.util.date.DateUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>上传失败</title>
<script type="text/javascript">
	function fail(url) {
		window.parent = null;
		window.close();
	}
</script>
</head>
<body>
	<h1>上传失败</h1>
	<p>${message}</p>
	<input type="button" value="确定" onclick="fail();" />
	<%
		Exception e = (Exception) request.getAttribute("exception");
		if (null != e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.close();
			sw.close();
			String string = HTMLUtil.toHTML(sw.toString());
			pageContext.setAttribute("exceptionStackTrace", string);
		}
	%>
	<div>${exceptionStackTrace}</div>
</body>
</html>