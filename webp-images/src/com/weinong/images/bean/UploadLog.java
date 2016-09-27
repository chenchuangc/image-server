package com.weinong.images.bean;


import yao.util.db.bean.TableDefined;

import java.util.Date;

@TableDefined(table = "upload_log", primaryKey = "id")
public class UploadLog {
    private Integer id;
    private Integer img_id;
    private Integer user_id;
    private String user_name;
    private String app;
    private Date create_time;;

    public UploadLog(){

    }
    public UploadLog(Integer id, Integer img_id, Integer user_id, String user_name, String app, Date create_time) {
        this.id = id;
        this.img_id = img_id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.app = app;
        this.create_time = create_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getImg_id() {
        return img_id;
    }

    public void setImg_id(Integer img_id) {
        this.img_id = img_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }
}
