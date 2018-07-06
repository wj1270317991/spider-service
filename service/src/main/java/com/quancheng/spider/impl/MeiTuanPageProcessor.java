package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.core.AbstractPageProcessor;
import com.quancheng.spider.core.DataSourceEnum;
import com.quancheng.spider.core.PageEnum;
import com.quancheng.spider.dataobject.Merchant;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;
import us.codecraft.webmagic.selector.Html;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @program: webmagic-parent
 * @author: Robert
 * @create: 2018-06-26
 **/
@Component("meiTuanPageProcessor")
public class MeiTuanPageProcessor extends AbstractPageProcessor {
    private static final String TARGET_URL = "http:%s/meishi/pn1/";
    private static final String ID_PREFIX = "MT";

    @Value("${mt.target.url}")
    private String targetUrl;
    @Value("${mt.spider.thread}")
    private Integer spiderThread;
    @Autowired
    private JedisPool jedisPool;
    @Resource(name = "merchantPipeline")
    private Pipeline pipeline;

    @Override
    public void exec() {
        exec(null, null);
    }

    @Override
    public void exec(String url, PageEnum pageEnum) {
        String pageUrl = StringUtils.isNotEmpty(url) ? url :targetUrl;
        Spider.create(this).addRequest(getRequest(pageUrl, pageEnum.name()))
                .addPipeline(pipeline)
                //.setScheduler(new RedisScheduler(jedisPool))
                .thread(spiderThread).runAsync();
    }

    @Override
    public void nextTarget(Page page) {
        String pageUrl = page.getUrl().toString();
        Pattern pattern = compile("(\\d+)");
        Matcher matcher = pattern.matcher(pageUrl);
        if (matcher.find()) {
            String pageNum = matcher.group(1);
            if (StringUtils.isNotEmpty(pageNum)) {
                int pageIndex = Integer.valueOf(pageNum) + 1;
                pageUrl = matcher.replaceFirst(pageIndex + "");
                logger.info("Target url is:{}", pageUrl);
                page.addTargetRequest(getRequest(pageUrl, PageEnum.DATA.name()));
            }
        }
    }

    @Override
    public void getUrls(Page page) {
        Html html = page.getHtml();
        Element element = html.getDocument().getElementsByClass("alphabet-city-area").first();
        Elements cityElements = element.select("a[href]");
        cityElements.forEach(ele -> {
            String href = ele.attr("href");
            href = String.format(TARGET_URL, href);
            page.addTargetRequest(getRequest(href, PageEnum.DATA.name()));
        });
    }

    @Override
    public void parseJson(Page page) {
        Pattern pattern = compile("window._appState =(.*);</script>");
        Matcher matcher = pattern.matcher(page.getJson().toString());
        if (matcher.find()) {
            String data = matcher.group(1);
            if (StringUtils.isNotEmpty(data)) {
                JSONObject jsonObject = JSON.parseObject(data);
                JSONObject poiLists = jsonObject.getJSONObject("poiLists");
                Integer totalCounts = poiLists.getInteger("totalCounts");
                logger.info("Current page url:{}, total size:{}", page.getUrl(), totalCounts);

                JSONArray poiInfos = poiLists.getJSONArray("poiInfos");
                if (null != totalCounts && totalCounts > 0 && null != poiInfos && poiInfos.size() > 0) {
                    List<PoiInfo> poiInfoList = poiInfos.toJavaList(PoiInfo.class);
                    List<Merchant> result = new ArrayList<>(poiInfoList.size());
                    poiInfoList.forEach(rs -> {
                        Merchant merchant = this.transform(rs);
                        if (null != merchant) {
                            result.add(merchant);
                        }
                        // 详情页
                        //page.addTargetRequest(getRequest(pageUrl, PAGE_DETAIL));
                    });
                    page.putField(PageEnum.DATA_KEY.name(), result);
                    nextTarget(page);
                }
            }
        }
    }

    @Override
    public void parseHtml(Page page) {
    }

    @Override
    public void parseDetailPage(Page page) {
    }

    @Override
    public Merchant transform(Object object) {
        PoiInfo poiInfo = (PoiInfo) object;
        Merchant merchant = new Merchant();
        merchant.setMerchantId(ID_PREFIX + poiInfo.getPoiId());
        merchant.setName(poiInfo.getTitle());
        merchant.setAddress(poiInfo.getAddress());
        merchant.setFrontImg(poiInfo.getFrontImg());
        merchant.setPrice(poiInfo.getAvgPrice());
        merchant.setTotalComment(poiInfo.getAllCommentNum());
        merchant.setScore(poiInfo.getAvgScore());
        merchant.setDataSource(DataSourceEnum.meituan.name());
        return merchant;
    }

    static class PoiInfo {
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
}