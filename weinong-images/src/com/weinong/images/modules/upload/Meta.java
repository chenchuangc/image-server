package com.weinong.images.modules.upload;

import java.util.HashSet;
import java.util.Set;

import com.weinong.images.core.ConstsForStatus;

import yao.util.string.StringUtil;

public class Meta {
	
	public static final Set<String> trueFile = new HashSet<String>(){
		private static final long serialVersionUID = 9187373367484881331L;

		{
			add("jpg");
			add("jpeg");
			add("png");
			add("bmp");
			add("JPG");
			add("GPEG");
			add("PNG");
			add("BMP");
		}
		
	};
	
	String md5ForFile;
	String fileName;
	public String getMd5ForFile() {
		return md5ForFile;
	}
	public void setMd5ForFile(String md5ForFile) {
		this.md5ForFile = md5ForFile;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	/**
	 * 对参数进行检查，不能为空或者错误，MD5的“错误检查”只能是在文件接收结束以后进行检查
	 * @param meta
	 * @return
	 */
	public static int check(Meta meta){
		
		String extendName;
		if(meta == null){
			return ConstsForStatus.UNFOUND_META;
		}
		
		if(StringUtil.isTrimEmpty(meta.getFileName())){
			return ConstsForStatus.UNFOUND_META_FILE;
		}
		try{
			extendName = StringUtil.getRight(meta.getFileName(), ".");
		}catch(Exception e){
			e.printStackTrace();
			return ConstsForStatus.WRONG_META_FILE;
		}
		if(!trueFile.contains(extendName)){
			return ConstsForStatus.WRONG_META_FILE;
		}
		
		if(StringUtil.isTrimEmpty(meta.getMd5ForFile())){
			return ConstsForStatus.UNFOUND_META_MD5;
		}
		
		return ConstsForStatus.SUCCESS;
	}
//	public static int checkMd5ForFile(Meta meta){
//		
//		if(StringUtil.isTrimEmpty(meta.getMd5ForFile())){
//			return ConstsForStatus.UNFOUND_META_MD5;
//		}
//		
//		return ConstsForStatus.SUCCESS;
//	}
}
