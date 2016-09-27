package com.weinong.images.modules.upload;

import java.util.HashMap;
import java.util.Map;

import yao.util.json.JSONUtil;

public class BodyFromImageServer {
	
//	Map<String , Info> body = new HashMap<String , Info>();
//	public static class Info{
		
		public String sourcename = null;
		public String size = null;
		public String url = null;
		
		public String getSourcename() {
			return sourcename;
		}
		public void setSourcename(String sourcename) {
			this.sourcename = sourcename;
		}
		public String getSize() {
			return size;
		}
		public void setSize(String size) {
			this.size = size;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
//	}
	
	
	
	public static void main(String args[]){
		BodyFromImageServer server = new BodyFromImageServer();
//		Info server = new Info();
		server.sourcename = "comefromcanaial";
		server.size = "5*43";
		server.url = "http://com.cn";
//		
		Map<String, BodyFromImageServer> bb = new HashMap<String, BodyFromImageServer>();
		bb.put("thumbnail88", server);
		bb.put("thumbnai3333", server);
		bb.put("thumbnai66", server);
		bb.put("thumbnail4", server);
		bb.put("thumbnail5", server);
//		server1.body.put("thumbnail2", server);
		JSONUtil json = new JSONUtil();
		System.out.println(json.parserString(bb));
	}

}
