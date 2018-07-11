package com.quancheng.spider.model;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class Province {
    private String provinceCode;
    private String provinceName;
    private List<City> cityInfoList;

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public List<City> getCityInfoList() {
        return cityInfoList;
    }

    public void setCityInfoList(List<City> cityInfoList) {
        this.cityInfoList = cityInfoList;
    }
}
