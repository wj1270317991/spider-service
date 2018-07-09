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

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-04
 **/
@Component("dianpingPageProcessor")
public class DianpingPageProcessor extends AbstractPageProcessor {
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
        exec(null, PageEnum.URLS);
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
    public void nextPage(Page page) {
        Element next = page.getHtml().getDocument().getElementsByClass("next").first();
        String href = next.attr("href");
        page.addTargetRequest(getRequest(href, PageEnum.ITEM.name()));
    }

    @Override
    public void getDetailUrl(Page page) {
        Element content = page.getHtml().getDocument().getElementById("shop-all-list");
        Elements elements = content.select("a[data-click-name=shop_img_click]");
        elements.stream().map(rs -> rs.attr("href"))
                .filter(StringUtils::isNotEmpty)
                .map(rs -> getRequest(rs, PageEnum.DETAIL.name()))
                .forEach(page::addTargetRequest);
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
        List<Merchant> result = liElements.stream().map(this::transform)
                .filter(Objects::nonNull).collect(Collectors.toList());
        page.putField(PageEnum.RESULT_ITEMS_KEY.name(), result);
        getDetailUrl(page);
//        nextPage(page);
    }

    @Override
    public void parseDetailPage(Page page) {
        System.err.println(page.getUrl());
    }

    @Override
    public Merchant transform(Object object) {
        Element element = (Element) object;
        Merchant merchant = new Merchant();
        try {
            parseMerchantId(element, merchant);
            parseFrontImage(element, merchant);
            parseName(element, merchant);
            parsePrice(element, merchant);
            parseTotalComment(element, merchant);
            parseScore(element, merchant);
            Element levelElement = element.getElementsByClass("sml-rank-stars sml-str40").first();
            if (null != levelElement) {
                merchant.setStarLevel(levelElement.attr("title"));
            }
            Elements tagElement = element.getElementsByClass("tag");
            merchant.setCategory(tagElement.get(0).text());
            merchant.setBusinessCircle(tagElement.get(1).text());

            String address = element.getElementsByClass("addr").first().text();
            merchant.setAddress(address);
            merchant.setDataSource(DataSourceEnum.dianping.name());

            List<String> collect = element.getElementsByClass("recommend-click").stream()
                    .map(Element::text).collect(Collectors.toList());
            String dishes = String.join(",", collect);
            merchant.setRecommendedDishes(dishes);
            return merchant;
        } catch (Exception e) {
            logger.error("Transform object to merchant failed>", e);
        }
        return null;
    }

    private void parseName(Element element, Merchant merchant) {
        Element nameElement = element.getElementsByTag("h4").first();
        if (null != nameElement) {
            merchant.setName(nameElement.text());
        }
    }

    private void parseFrontImage(Element element, Merchant merchant) {
        Element imgElement = element.getElementsByTag("img").first();
        String image = imgElement.attr("src");
        merchant.setFrontImg(image);
    }

    private void parseMerchantId(Element element, Merchant merchant) {
        Element mapElement = element.getElementsByClass("o-map J_o-map").first();
        String merchantId = mapElement.attr("data-shopid");
        merchant.setMerchantId(merchantId);
    }

    private void parsePrice(Element element, Merchant merchant) {
        Element node = element.getElementsByClass("mean-price").first();
        Elements elem = node.getElementsByTag("b");
        if (null != elem) {
            String price = elem.text();
            if (StringUtils.isNotEmpty(price)) {
                price = price.trim().replace("￥", "");
                merchant.setPrice(price);
            }
        }
    }

    private void parseTotalComment(Element element, Merchant merchant) {
        Element node = element.getElementsByClass("review-num").first();
        if (null == node) {
            return;
        }
        Elements elm = node.getElementsByTag("b");
        if (null != elm && StringUtils.isNotEmpty(elm.text())) {
            merchant.setTotalComment(elm.text());
        }
    }

    private void parseScore(Element element, Merchant merchant) {
        Element commentList = element.getElementsByClass("comment-list").first();
        if (null != commentList) {
            Score score = new Score();
            Elements subNodes = commentList.getElementsByTag("b");
            score.setTaste(subNodes.get(0).text());
            score.setAmbient(subNodes.get(1).text());
            score.setService(subNodes.get(2).text());
            merchant.setScore(JSON.toJSONString(score));
        }
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
