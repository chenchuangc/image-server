package com.weinong.images.modules.upload;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import yao.util.file.FileUtil;
import yao.util.file.FilenameUtil;
import yao.util.log.Console;
import yao.util.md5.MD5Util;
import yao.util.object.FieldUtil;
import yao.util.string.StringUtil;

import com.weinong.images.Conf;
import com.weinong.images.core.BaseAction;

public class UploadImageAction extends BaseAction {

	private String app;
	private String callback;

	private String filename;
	private String url;

	private int error;
	private String message;
	private Exception exception;

	@Override
	public String execute() throws Exception {

		RequestContext requestContext = new ServletRequestContext(request);
		if (!FileUpload.isMultipartContent(requestContext)) {}

		// 为基于硬盘文件的项目集创建一个工厂
		FileItemFactory factory = new DiskFileItemFactory();

		// 创建一个新的文件上传处理器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 解析请求
		List<?> items;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			return error("上传时出错：" + e.getMessage(), e);
		}
		// 处理上传项目
		Iterator<?> iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (item.isFormField()) {
				FieldUtil.set(this, item.getFieldName(), item.getString("utf8"));
			}
		}
		iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();
			if (!item.isFormField()) {
				String fieldName = item.getFieldName();
				String fileName = item.getName();
				if ("file".equals(fieldName)) {
					byte[] bytes = item.get();
					if (!StringUtil.isTrimEmpty(fileName)) {
						String name = MD5Util.getMD5FromBytes(bytes);
						filename = name + "." + FilenameUtil.getExtension(fileName);
						File dir = new File(Conf.image_dir, app);
						if (!dir.exists()) {
							dir.mkdirs();
						}
						File file = new File(new File(Conf.image_dir, app), filename);
						FileUtil.writeFile(file, bytes);
					}
				}
			}
		}

		doSomeThing(Conf.image_dir + "/" + app, filename);

		url = Conf.image_http_page + app + "/" + filename;

		Console.info(this, "Upload image success: " + filename + ", " + url);

		return SUCCESS;
	}

	protected void doSomeThing(String path, String filename) throws Exception {}

	protected String error(String message, Exception exception) {
		this.message = message;
		this.error = 1;
		this.exception = exception;
		return "error";
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getError() {
		return error;
	}

	public Exception getException() {
		return exception;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getFilename() {
		return filename;
	}

	public String getUrl() {
		return url;
	}

}
