package com.weinong.images.bean;

import yao.util.db.bean.TableDefined;


@TableDefined(table = "images", primaryKey = "id")
public class Images {
    private Integer id;
    private String name;

    public Images(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Images() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
