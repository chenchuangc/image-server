<%@page import="yao.util.date.DateUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title></title>
</head>
<body>

	<form name="form" action="upload_image.wn" method="post" enctype="multipart/form-data">

		<input type="hidden" name="app" value="${app}" />
		<input type="hidden" name="callback" value="${callback}" />

		<table class="input_trade" cellpadding="5" cellspacing="0">
			<tr>
				<td>选择图片</td>
				<td>
					<input type="file" name="file" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td>选择需要上传的图片，大小不能超过5M，只支持(png,jpg,bmp)类型的图片</td>
			</tr>

			<tr>
				<td>&nbsp;</td>
				<td>
					<input type="submit" value="上传" />
				</td>
			</tr>
		</table>

	</form>


</body>
</html>