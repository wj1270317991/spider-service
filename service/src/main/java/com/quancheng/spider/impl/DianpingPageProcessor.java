package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.quancheng.spider.core.AbstractPageProcessor;
import com.quancheng.spider.core.DataSourceEnum;
import com.quancheng.spider.core.PageEnum;
import com.quancheng.spider.dataobject.Merchant;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-04
 **/
@Component("dianpingPageProcessor")
public class DianpingPageProcessor extends AbstractPageProcessor {
    private static final String ID_PREFIX = "DP";

    @Value("${dp.target.url}")
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
        String pageUrl = StringUtils.isNotEmpty(url) ? url : targetUrl;
        Spider.create(this).addRequest(getRequest(pageUrl, pageEnum.name()))
                .addPipeline(pipeline)
                //.setScheduler(new RedisScheduler(jedisPool))
                .thread(spiderThread).runAsync();
    }


    @Override
    public void nextTarget(Page page) {
    }

    @Override
    public void getUrls(Page page) {

    }

    @Override
    public void parseJson(Page page) {
    }

    @Override
    public void parseHtml(Page page) {
        Document document = page.getHtml().getDocument();
        Element shopAllList = document.getElementById("shop-all-list");
        Elements liElements = shopAllList.select("li");
        List<Merchant> result = new ArrayList<>(liElements.size());
        liElements.forEach(rs -> {
            Merchant merchant = this.transform(rs);
            if (null != merchant) {
                result.add(merchant);
            }
        });
        page.putField(PageEnum.DATA_KEY.name(), result);
        nextTarget(page);
    }

    @Override
    public void parseDetailPage(Page page) {

    }

    @Override
    public Merchant transform(Object object) {
        try {
            Element element = (Element) object;
            System.err.println(element);
            Merchant merchant = new Merchant();
            Element mapElement = element.getElementsByClass("o-map J_o-map").first();
            String merchantId = mapElement.attr("data-shopid");
            merchant.setMerchantId(ID_PREFIX + merchantId);

            Element imgElement = element.getElementsByTag("img").first();
            String image = imgElement.attr("src");
            merchant.setFrontImg(image);

            Element nameElement = element.getElementsByTag("h4").first();
            String name = nameElement.text().trim();
            merchant.setName(name);

            Element avgPriceElement = element.getElementsByClass("mean-price").first();
            if (avgPriceElement.children().size() > 0) {
                Element child = avgPriceElement.child(0);
                String avgPrice = avgPriceElement.text().replace("￥", "");
                if (StringUtils.isNotEmpty(avgPrice)) {
                    merchant.setPrice(avgPrice);
                }
            }

            Element reviewNumElement = element.getElementsByClass("review-num").first().child(0);
            String reviewNum = reviewNumElement.text().trim();
            if (StringUtils.isNotEmpty(reviewNum)) {
                merchant.setTotalComment(reviewNum);
            }

            Score score = new Score();
            Element commentList = element.getElementsByClass("comment-list").first();
            Elements subNodes = commentList.children();
            score.setTaste(subNodes.get(0).child(0).text().trim());
            score.setAmbient(subNodes.get(1).child(0).text().trim());
            score.setService(subNodes.get(2).child(0).text().trim());
            merchant.setScore(JSON.toJSONString(score));

            Element levelElement = element.getElementsByClass("comment").first().child(0);
            merchant.setStarLevel(levelElement.attr("title"));

            Elements tagElement = element.getElementsByClass("tag");
            merchant.setCategory(tagElement.get(0).text());
            merchant.setBusinessCircle(tagElement.get(1).text());

            String address = element.getElementsByClass("addr").first().text();
            merchant.setAddress(address);
            merchant.setDataSource(DataSourceEnum.dianping.name());

            StringBuilder sb = new StringBuilder();
            element.getElementsByClass("recommend-click").forEach(ele -> sb.append(ele.text()));
            merchant.setRecommendedDishes(sb.toString());
            return merchant;
        } catch (Exception e) {
            logger.error("Transform object to merchant failed>", e);
        }
        return null;
    }

    static class Score {
        private String taste;
        private String ambient;
        private String service;

        public String getTaste() {
            return taste;
        }

        public void setTaste(String taste) {
            this.taste = taste;
        }

        public String getAmbient() {
            return ambient;
        }

        public void setAmbient(String ambient) {
            this.ambient = ambient;
        }

        public String getService() {
            return service;
        }

        public void setService(String service) {
            this.service = service;
        }
    }
}
