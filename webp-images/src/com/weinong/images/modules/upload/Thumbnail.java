package com.weinong.images.modules.upload;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weinong.images.ImageConsts;
import com.weinong.images.base.PictureParams;
import com.weinong.images.core.ConstsForStatus;
import yao.util.string.StringUtil;

public class Thumbnail {

	//这个封装一开始没有设计好，有些参数是可有可无的，像尺寸参数，在这个封装当中应该设计成独立的 宽和高参数   没有的话应该可以在设计一个布尔型的值加以判断就好
	
	private static final Set<String> tureType = new HashSet<String>(){
		private static final long serialVersionUID = 9187373367484881331L;

		{
			add("fix_width");
			add("fix_height");
			add("fix_smart");
			add("scale");
			add("cut");
		}
		
	};
	private String type;
	private String size;//格式为"20,300"宽高
	private Integer quality;

	private Integer width;
	private Integer height;

	private boolean have_size;//标示size参数是否初始化，即



	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	public String getSize() {
		return size;
	}


	public void setSize(String size) {
		this.size = size;
		if (!size.equals(ImageConsts.ThumbnailConsts.size_no_scal)) {

			this.width = Integer.valueOf(StringUtil.getLeft(this.size,"*"));
			this.height = Integer.valueOf(StringUtil.getRight(this.size,"*"));
		}
	}
	/*public void setSize(PictureParams size) {

			this.width = size.getWidth();
			this.height = size.getHeight();
	}*/
	public Integer getQuality() {
		return quality;
	}
	public void setQuality(Integer quality) {
		this.quality = quality;
	}

	public Integer getWidth(){

		return width;
	}
	public Integer getHeight(){

		return height;
	}

	public boolean isHave_size() {
		return have_size;
	}

	public void setHave_size(boolean have_size) {
		this.have_size = have_size;
	}

	public static  int check(Thumbnail thumbnail){
		
		//String[]  tureParameter = new String[]{"fix_width" , "fix_height" , "fix_smart" , "scale" ,"cut"};
		//tureParameter.toString().concat("");
		String regForSize ="\\d+\\*\\d+" ; ;
//		
//		Pattern pattern = Pattern.compile(regForSize);
//		Matcher matcher = pattern.matcher(thumbnail.getSize());

		if(thumbnail.getSize().equals(ImageConsts.ThumbnailConsts.size_no_scal)){
			return ConstsForStatus.SUCCESS;
		}
		
		if(!thumbnail.getSize().matches(regForSize)){
//			System.out.println(!matcher.find());
			return ConstsForStatus.WRONG_THUMBNAIL_SIZE;
		}
		
		if(!tureType.contains(thumbnail.getType())){
			return ConstsForStatus.WRONG_THUMBNAIL_TYPE;
		}
		if(!(thumbnail.getQuality()>1&&thumbnail.getQuality()<100)){
			return  ConstsForStatus.WRONG_THUMBNAIL_QUALITY;
		}
		return ConstsForStatus.SUCCESS;
	}
	
	 
	
	public static void main(String[] args){
		String[]  tureParameter = new String[]{"fix_width" , "fix_height" , "fix_smart" , "scale" ,"cut"};
		System.out.println(tureParameter.toString());
		String regForSize ="\\d+\\*\\d+" ;
		Pattern pattern = Pattern.compile(regForSize);
		Matcher matcher = pattern.matcher("*180");
		System.out.println(""+matcher.find());
		System.out.println("*180".matches(regForSize));
	}

}
