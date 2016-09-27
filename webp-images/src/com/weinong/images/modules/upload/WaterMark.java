package com.weinong.images.modules.upload;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.weinong.images.core.ConstsForStatus;

import yao.util.string.StringUtil;

/**
 *  记录水印的相关设置：文字，坐标， 字号大小
 * @author xxx
 *
 */
public class WaterMark {

	private String textForWaterMark;
	private String coordinate;				//Sring 类型的用于接受传过来的坐标字符串
	private int  size;						//字体大小
	private int coordinateX ;				//由coordiante解析出来的具体的整形的坐标值
	private int coordinateY ;

	private boolean haveWaterMark;

	public boolean isHaveWaterMark() {
		return haveWaterMark;
	}

	public void setHaveWaterMark(boolean haveWaterMark) {
		this.haveWaterMark = haveWaterMark;
	}

	public String getTextForWaterMark() {
		return textForWaterMark;
	}
	public void setTextForWaterMark(String textForWaterMark) {
		this.textForWaterMark = textForWaterMark;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
		this.coordinateX = Integer.parseInt(StringUtil.getLeft(coordinate, ","));
		this.coordinateY = Integer.parseInt(StringUtil.getRight(coordinate, ","));
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public int getCoordinateX() {
		return coordinateX;
	}
	public void setCoordinateX(int coordinateX) {
		this.coordinateX = coordinateX;
	}
	public int getCoordinateY() {
		return coordinateY;
	}
	public void setCoordinateY(int cootdinateY) {
		this.coordinateY = cootdinateY;
	}
	
	
	
	public static int check(WaterMark waterMark){
		
		String regForSize ="(-)?[0-9]+(,)(-)?" +
				"[0-9]+" ;					//判断水印的坐标格式是否正确
		
		if(null == waterMark){
			return ConstsForStatus.SUCCESS;											//waterMark有两种情况，一种是整体为空，是允许的，还有就是具备水印内容（其他参数会被默认赋值），其余返回失败
		}

		if (!waterMark.isHaveWaterMark()) {
			return ConstsForStatus.SUCCESS;
		}
		if(!waterMark.getCoordinate().matches(regForSize)){
			return ConstsForStatus.WRONG_WATERMARK_COORDINATE;
		}
		
		if(StringUtil.isTrimEmpty(waterMark.getTextForWaterMark())){
			return ConstsForStatus.WRONG_WATERMARK_LENGTH;
		}
		if(StringUtil.isTrimEmpty(waterMark.getTextForWaterMark())){
//			return ConstsForStatus.UNFOUND_WATERMARK_CONTENT;
			return ConstsForStatus.WRONG_WATERMARK_LENGTH;							//因为水印为非必须项，所以不做空的检查，只在有传递的有水印参数的时候对内容进行校验   只返回内容长度不够作为提示
		}
		if(waterMark.getTextForWaterMark().length()>64){
			return ConstsForStatus.WRONG_WATERMARK_LENGTH;
		}
		if(waterMark.getSize()<6||waterMark.getSize()>16){
			return ConstsForStatus.WRONG_WATERMARK_SIZE;
		}
		return ConstsForStatus.SUCCESS;
	}
	
	public static void main(String args[]){
		String regForSize ="(-)?[0-9]+(,)(-)?[0-9]+" ;		
		System.out.println("-5,0".matches(regForSize));
		
	}
}
