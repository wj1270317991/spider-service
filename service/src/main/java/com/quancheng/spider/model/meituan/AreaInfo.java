package com.quancheng.spider.model.meituan;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class AreaInfo {
    private String id;
    private String name;
    private String areaName;
    private String url;
    private List<AreaInfo> subAreas;

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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

    public List<AreaInfo> getSubAreas() {
        return subAreas;
    }

    public void setSubAreas(List<AreaInfo> subAreas) {
        this.subAreas = subAreas;
    }
}
