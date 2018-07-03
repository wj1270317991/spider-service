package com.quancheng.spider.dataobject;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
public class PoiInfo {
    private String poiId;
    // 地址
    private String address;
    // 综合评分
    private String avgScore;
    // 评论数
    private String allCommentNum;
    // 门店图片
    private String frontImg;
    // 人均消费
    private String avgPrice;

    // 店名
    private String title;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvgScore() {
        return avgScore;
    }

    public void setAvgScore(String avgScore) {
        this.avgScore = avgScore;
    }

    public String getAllCommentNum() {
        return allCommentNum;
    }

    public void setAllCommentNum(String allCommentNum) {
        this.allCommentNum = allCommentNum;
    }

    public String getFrontImg() {
        return frontImg;
    }

    public void setFrontImg(String frontImg) {
        this.frontImg = frontImg;
    }

    public String getAvgPrice() {
        return avgPrice;
    }

    public void setAvgPrice(String avgPrice) {
        this.avgPrice = avgPrice;
    }

    public String getPoiId() {
        return poiId;
    }

    public void setPoiId(String poiId) {
        this.poiId = poiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
