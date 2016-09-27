package com.weinong.images.modules.upload;

import com.weinong.images.core.BaseAction;

public class ShowUploadImageAction extends BaseAction {

	private String app;
	private String callback;

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

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

}
