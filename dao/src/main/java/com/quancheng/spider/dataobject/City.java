package com.quancheng.spider.dataobject;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class City implements Cloneable {
    /**
     * 城市名称
     */
    private String name;

    /**
     * 省份
     */
    private String provinceName;

    /**
     * 区域
     */
    private String areaName;

    /**
     * 商圈
     */
    private String tradingArea;

    private String acronym;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getTradingArea() {
        return tradingArea;
    }

    public void setTradingArea(String tradingArea) {
        this.tradingArea = tradingArea;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }
}
