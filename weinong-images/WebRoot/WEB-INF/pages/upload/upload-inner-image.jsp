<%@page import="yao.util.date.DateUtil"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title></title>
</head>
<body>

	<form name="form" action="upload_image_inner.wn" method="post" enctype="multipart/form-data">


		<table class="input_trade" cellpadding="5" cellspacing="0">
			<tr><td>APP名称</td><td><input type="text" name="app" value="${app}" /></td></tr>
			<tr><td>生成缩略图</td><td><input type="checkbox" checked name="scale" value="1" /></tr>
			<tr><td>水&nbsp;&nbsp;印</td><td><input type="text" name="text" value="微农" /></tr>
			<tr>
				<td>选择图片</td>
				<td>
					<input type="file" name="file" />
				</td>
			</tr>
			<tr>
				<td></td>
				<td>选择需要上传的图片，大小不能超过5M，只支持(png,jpg)类型的图片</td>
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