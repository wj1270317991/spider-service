package com.quancheng.spider.model.meituan;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class ProvinceInfo {
    private String provinceCode;
    private String provinceName;
    private List<CityInfo> cityInfoList;

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

    public List<CityInfo> getCityInfoList() {
        return cityInfoList;
    }

    public void setCityInfoList(List<CityInfo> cityInfoList) {
        this.cityInfoList = cityInfoList;
    }
}