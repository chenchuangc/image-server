<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>My JSP 'index.jsp' starting page</title>
<script type="text/javascript" src="upload-image.js"></script>
</head>
<body>
	<input type="button" value="上传图片" onclick="openUploadImage('test1', 'upload_succ');" />
	<script type="text/javascript">
		function upload_succ(url) {
			alert(url);
		}
	</script>
</body>
</html>
