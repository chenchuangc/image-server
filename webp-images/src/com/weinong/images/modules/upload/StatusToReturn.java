package com.weinong.images.modules.upload;

import java.util.HashMap;
import java.util.Map;

public class StatusToReturn {
	
	public static final Map<String, String> map = new HashMap<String, String>();
	static {
		map.put("1", "上传成功");
		map.put("0", "系统异常");
		map.put("-1", "缺失sign参数");
		map.put("-2", "缺失app参数");
		map.put("-3", "缺失meta参数");
		map.put("-4", "缺失meta.md5参数");
		map.put("-5", "缺失meta.filename参数");
		map.put("-9", "文件内容为空");
		map.put("-11", "应用编号不存在");
		map.put("-21", "签名错误");
		map.put("-22", "文件md5错误");
		map.put("-23", "文件名错误");
		map.put("-31", "水印内容长度不正确");
		map.put("-32", "水印坐标格式不规范");
		map.put("-33", "水印大小不在规定范围内");
		map.put("-41", "缩略图类型不正确");
		map.put("-42", "缩略图大小不正确");
		map.put("-43", "缩略图质量不正确");
	}
	
	public static String getMessageForStatus(String code){
		return map.get(code);
	}
}
