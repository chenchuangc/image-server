package com.weinong.images.modules.upload;

import com.weinong.images.core.BaseAction;

public class ShowUploadInnerImageAction extends BaseAction {

	private String app;

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}
}
