package com.quancheng.spider.dataobject;

import java.util.Date;

/**
 * 商家信息
 * @author Robert
 */
public class Merchant {
    private String merchantId;

    /**
     * 商家名称
     */
    private String name;

    /**
     * 门头图片
     */
    private String frontImg;

    /**
     * 地址
     */
    private String address;

    /**
     * 人均消费
     */
    private String price;

    /**
     * 总评论数
     */
    private String totalComment;

    /**
     * 评分
     */
    private String score;

    /**
     * 级别(星级)
     */
    private String starLevel;

    /**
     * 分类
     */
    private String category;

    /**
     * 区域
     */
    private String area;

    /**
     * 商圈
     */
    private String businessCircle;

    /**
     * 数据来源(美团、点评)
     */
    private String dataSource;

    /**
     * 特色菜名
     */
    private String recommendedDishes;

    private Date gmtCreated;

    private Date gmtModified;

    public Date getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(Date gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public Date getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(Date gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFrontImg() {
        return frontImg;
    }

    public void setFrontImg(String frontImg) {
        this.frontImg = frontImg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStarLevel() {
        return starLevel;
    }

    public void setStarLevel(String starLevel) {
        this.starLevel = starLevel;
    }

    public String getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(String totalComment) {
        this.totalComment = totalComment;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getBusinessCircle() {
        return businessCircle;
    }

    public void setBusinessCircle(String businessCircle) {
        this.businessCircle = businessCircle;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getRecommendedDishes() {
        return recommendedDishes;
    }

    public void setRecommendedDishes(String recommendedDishes) {
        this.recommendedDishes = recommendedDishes;
    }
}