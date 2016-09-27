package com.weinong.images.modules.upload;

import com.weinong.images.Conf;
import com.weinong.images.bean.Images;
import com.weinong.images.bean.UploadLog;
import com.weinong.images.service.ImagesService;
import com.weinong.images.service.UploadLogService;
import com.weinong.images.util.ImageUtils;
import org.apache.commons.fileupload.*;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import yao.management.purview.LoginInfo;
import yao.management.purview.YaoManagement;
import yao.util.file.FileUtil;
import yao.util.file.FilenameUtil;
import yao.util.md5.MD5Util;
import yao.util.object.FieldUtil;
import yao.util.string.StringUtil;

import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/***
 * @author jiawenfeng
 * @date 2015.08.24 15:00:00
 */
public class UploadImageToInnerAction extends UploadImageAction {
    private String filename;
    private String app;
    private String text;
    private Integer scale;

    private String url;

    private void preHandler() throws Exception{
        RequestContext requestContext = new ServletRequestContext(request);
        if (!FileUpload.isMultipartContent(requestContext)) {}

        // 为基于硬盘文件的项目集创建一个工厂
        FileItemFactory factory = new DiskFileItemFactory();

        // 创建一个新的文件上传处理器
        ServletFileUpload upload = new ServletFileUpload(factory);
        // 解析请求
        List<?> items;
        items = upload.parseRequest(request);
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
    }

    private boolean saveImages(String url, Date now, Integer uid, String username)throws SQLException {
        Images images = new Images(null, url);
        if (ImagesService.save(images)) {
            UploadLog uploadLog = new UploadLog(null, images.getId(), uid, username, app, now);
            return UploadLogService.save(uploadLog);
        }
        return  false;
    }

    @Override
    public String execute() throws Exception {
        //登录认证
        LoginInfo li = YaoManagement.getPurviewCheckClient().loginCheck(getRequest());
        if(li == null){
            return error("用户未登录", null);
        }

        try {
            preHandler();//预处理（参数处理）
        } catch (FileUploadException e) {
            return error("上传时出错：" + e.getMessage(), e);
        }
        if (filename == null || app == null){
            return error("上传参数错误 >_<", null);
        }
        String fileAbsolutePath = Conf.image_dir + "/" + app + "/"+ filename;
        File file = new File(fileAbsolutePath);
        if (ImageUtils.getImgSize(file) > 5*1024*1024){
            return error("上传图片大小不能超过5M >_<", null);
        }
        String type = ImageUtils.getImgType(file);
        if (type == null || !(type.toLowerCase().equals(ImageUtils.IMAGE_TYPE_JPG) || type.toLowerCase().equals(ImageUtils.IMAGE_TYPE_PNG))){
            return error("上传只支持(png,jpg)类型的图片 >_<", null);
        }

        //图片处理【缩略、加水印】
        if (scale != null){//缩略
            ImageUtils.thumbnailImage(fileAbsolutePath);
        }
        if (text == null){
            text = "微农";
        }
        ImageUtils.pressText(text, fileAbsolutePath, fileAbsolutePath, "宋体", Font.BOLD, Color.white, 20, 0, 0, 0.5f);
        url =  Conf.image_http_page + app + "/" + file.getName();


        //记录记录上传应用、上传人、上传时间
        try {
            Date now = new Date();

            saveImages(url, now, li.getUid(), li.getUsername());
        }catch (SQLException e){
            return error(e.getMessage(), e);
        }
        return SUCCESS;
    }


    @Override
    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String getApp() {
        return app;
    }

    @Override
    public void setApp(String app) {
        this.app = app;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }


    @Override
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
