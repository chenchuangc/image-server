package com.weinong.images.modules.upload;

import java.util.List;

import com.weinong.base.ApiDefined;
import com.weinong.base.IDPage;
import com.weinong.base.ParamDefined;
import com.weinong.images.bean.Images;
import com.weinong.images.service.ImagesService;
import yao.springmvc.Action;


//list_images.wn?page.lastId=32&page.pageSize=3&callback=jkgldf
@ApiDefined(label = "图片列表", description = "分页查询图片列表")
public class ListImagesAction extends Action{
	
	@ParamDefined(label = "")
	protected IDPage page;
	
	private List<Images> imagesList;
	
	@ParamDefined(label = "")
	private String callback;
	
	@Override
	public String execute() throws Exception {
		List<Images> rl = ImagesService.queryImages(getPage());
		
		setImagesList(rl);
		
		return SUCCESS;
	}

	public IDPage getPage() {
		return page;
	}

	public void setPage(IDPage page) {
		this.page = page;
	}

	public List<Images> getImagesList() {
		return imagesList;
	}

	public void setImagesList(List<Images> imagesList) {
		this.imagesList = imagesList;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}
	
	

}
