package com.quancheng.spider.model.meituan;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-13
 **/
public class RecommendedInfo {
    private String name;
    private String price;
    private String frontImgUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getFrontImgUrl() {
        return frontImgUrl;
    }

    public void setFrontImgUrl(String frontImgUrl) {
        this.frontImgUrl = frontImgUrl;
    }
}
