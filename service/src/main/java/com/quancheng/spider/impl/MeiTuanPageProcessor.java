package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.core.AbstractPageProcessor;
import com.quancheng.spider.core.DataSourceEnum;
import com.quancheng.spider.core.ExtraKeyEnum;
import com.quancheng.spider.core.PageEnum;
import com.quancheng.spider.dataobject.City;
import com.quancheng.spider.dataobject.Merchant;
import com.quancheng.spider.model.meituan.AreaInfo;
import com.quancheng.spider.model.meituan.DetailInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.RedisScheduler;

import javax.annotation.Resource;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * 美团商家数据爬虫
 *
 * @author Robert
 */
@Component("meiTuanPageProcessor")
public class MeiTuanPageProcessor extends AbstractPageProcessor {
    private static final String UUID = "meituan.com";
    private static final String DETAIL_URL = "http://www.meituan.com/meishi/%s/";

    @Value("${mt.target.url}")
    private String targetUrl;
    @Value("${mt.spider.thread}")
    private Integer spiderThread;
    @Autowired
    private JedisPool jedisPool;

    @Resource(name = "merchantPipeline")
    private Pipeline pipeline;

    @Resource(name = "cityPipeline")
    private Pipeline cityPipe;

    @Override
    public void exec() {
        Map<String, Object> map = new HashMap<>();
        map.put(ExtraKeyEnum.KEY.name(), ExtraKeyEnum.URLS.name());
        map.put(ExtraKeyEnum.URL_TYPE.name(), "city");
        Request request = this.getRequest(targetUrl, map);
        exec(request);
    }

    @Override
    public void exec(Request request) {
        String hostName = URI.create(request.getUrl()).getHost();
        String uuid = StringUtils.isNotEmpty(hostName) ? hostName : UUID;
        Spider.create(this).setUUID(uuid)
                .addRequest(request)
                .setScheduler(new RedisScheduler(jedisPool))
                .addPipeline(pipeline)
                .addPipeline(cityPipe)
                .thread(spiderThread).runAsync();
    }

    @Override
    public void nextPage(Page page) {
        String pageUrl = page.getUrl().toString();
        Pattern pattern = compile("(\\d+)");
        Matcher matcher = pattern.matcher(pageUrl);
        if (matcher.find()) {
            String pageNum = matcher.group(1);
            if (StringUtils.isNotEmpty(pageNum)) {
                int pageIndex = Integer.valueOf(pageNum) + 1;
                pageUrl = matcher.replaceFirst(pageIndex + "");
                logger.info("Target url is:{}", pageUrl);
                page.addTargetRequest(getRequest(pageUrl, ExtraKeyEnum.KEY.name()));
            }
        }
    }

    @Override
    public void getDetailUrl(Page page) {
        List<Merchant> merchants = page.getResultItems().get(PageEnum.RESULT_MERCHAANT_KEY.name());
        merchants.stream().map(rs -> format(DETAIL_URL, rs.getMerchantId()))
                .forEach(url -> page.addTargetRequest(getRequest(url, ExtraKeyEnum.DETAIL.name())));
    }

    private String format(String template, String value) {
        return String.format(template, value);
    }

    @Override
    public void getUrls(Page page) {
        String extra = (String) page.getRequest().getExtra(ExtraKeyEnum.URL_TYPE.name());
        switch (extra) {
            case "city":
                addTargetRequestOfCity(page);
                break;
            case "area":
                parseAllTarget(page);
                break;
            default:
                break;
        }
    }

    private void parseAllTarget(Page page) {
        List<Request> requestList = getTargetRequestOfArea(page);
        if (CollectionUtils.isEmpty(requestList)) {
            page.setSkip(true);
            return;
        }
        List<City> cityList = requestList.stream().map(req -> (City) req.getExtra(ExtraKeyEnum.CITY.name()))
                .collect(Collectors.toList());
        page.putField(PageEnum.RESULT_CITY_KEY.name(), cityList);
        requestList.forEach(page::addTargetRequest);
    }

    private List<Request> getTargetRequestOfArea(Page page) {
        String data = MeituanJsonHandler.extractAppJson(page.getJson().toString());
        List<AreaInfo> areaInfos = MeituanJsonHandler.parseArea(data);
        City city = getCity(page);
        try {
            return MeituanJsonHandler.getAreaRequests(city, areaInfos);
        } catch (CloneNotSupportedException e) {
            logger.error("Clone area failed>", e);
        }
        return Collections.emptyList();
    }

    private City getCity(Page page) {
        JSONObject extraObj = (JSONObject) page.getRequest().getExtra(ExtraKeyEnum.CITY.name());
        return (City) extraObj.toJavaObject(City.class);
    }

    private void addTargetRequestOfCity(Page page) {
        List<City> cities = MeituanJsonHandler.parseCity(page.getJson().toString());
        List<Request> requestList = MeituanJsonHandler.getCityRequests(cities);
        requestList.forEach(page::addTargetRequest);
    }

    @Override
    public void parseJson(Page page) {
        String data = MeituanJsonHandler.extractAppJson(page.getJson().toString());
        if (StringUtils.isNotEmpty(data)) {
            JSONObject jsonObject = JSON.parseObject(data);
            JSONObject poiLists = jsonObject.getJSONObject("poiLists");
            Integer totalCounts = poiLists.getInteger("totalCounts");
            logger.info("Current page url:{}, total size:{}", page.getUrl(), totalCounts);

            JSONArray poiInfos = poiLists.getJSONArray("poiInfos");
            if (null != totalCounts && totalCounts > 0 && null != poiInfos && poiInfos.size() > 0) {
                List<PoiInfo> poiInfoList = poiInfos.toJavaList(PoiInfo.class);
                List<Merchant> merchants = poiInfoList.stream().map(this::transform).collect(Collectors.toList());

                City city = getCity(page);
                for (Merchant merchant : merchants) {
                    merchant.setCity(city.getProvinceName());
                    merchant.setArea(city.getAreaName());
                    merchant.setBusinessCircle(city.getTradingArea());
                }

                page.putField(PageEnum.RESULT_MERCHAANT_KEY.name(), merchants);
                getDetailUrl(page);
                // nextPage(page);
            }
        }
    }

    @Override
    public void parseHtml(Page page) {
    }

    @Override
    public void parseDetailPage(Page page) {
        String json = MeituanJsonHandler.extractAppJson(page.getJson().toString());
        if (StringUtils.isNotEmpty(json)) {
            JSONObject jsonObject = JSON.parseObject(json);
            JSONObject detailInfo = jsonObject.getJSONObject("detailInfo");
            DetailInfo detail = detailInfo.toJavaObject(DetailInfo.class);

            Merchant merchant = new Merchant();
            merchant.setMerchantId(detail.getPoiId());
            merchant.setTelphone(detail.getPhone());
            merchant.setOfficeHours(detail.getOpenTime());
            merchant.setLongitude(detail.getLongitude());
            merchant.setLatitude(detail.getLatitude());
            page.putField(PageEnum.RESULT_MERCHAANT_KEY.name(), Collections.singletonList(merchant));
        }
    }

    @Override
    public Merchant transform(Object object) {
        PoiInfo poiInfo = (PoiInfo) object;
        Merchant merchant = new Merchant();
        merchant.setMerchantId(poiInfo.getPoiId());
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