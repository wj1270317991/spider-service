package com.quancheng.spider.model;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class Area {
    private String id;
    private String name;
    private String url;
    private List<Area> subAreas;

    public List<Area> getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(List<Area> subAreas) {
        this.subAreas = subAreas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
