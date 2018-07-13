package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.dataobject.City;
import com.quancheng.spider.model.dianping.CityInfo;
import com.quancheng.spider.model.dianping.ProvinceInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;
import us.codecraft.webmagic.Request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@RunWith(SpringRunner.class)
public class DianpingPageProcessorTest {
    @Value("classpath:dianping/city.json")
    private Resource cityResource;

    @Value("classpath:dianping/area.html")
    private Resource areaResource;

    @Test
    public void testCityJson() throws IOException {
        String areaTemplate = "http://www.dianping.com/%s/food";

        InputStream stream = cityResource.getInputStream();
        String json = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        JSONObject jsonObject = JSON.parseObject(json);
        JSONArray provinceList = jsonObject.getJSONArray("provinceList");
        List<ProvinceInfo> provinceInfos = provinceList.toJavaList(ProvinceInfo.class);
        JSONObject cityMap = jsonObject.getJSONObject("cityMap");

        City city;
        Request request;
        List<Request> result = new ArrayList<>();
        for (ProvinceInfo province : provinceInfos) {
            JSONArray cityArray = cityMap.getJSONArray(province.getProvinceId());
            List<CityInfo> cityInfos = cityArray.toJavaList(CityInfo.class);
            for (CityInfo cityInfo : cityInfos) {
                city = new City();
                city.setName(cityInfo.getCityName());
                city.setProvinceName(province.getProvinceName());
                city.setAcronym(cityInfo.getCityPyName());

                request = new Request();
                request.setUrl(String.format(areaTemplate, city.getAcronym()));
                request.putExtra("city", city);
                result.add(request);
            }
        }

        System.err.println(JSON.toJSONString(result));
        Assert.assertTrue(result.size() > 0);
    }

    @Test
    public void testArea() throws IOException {
        InputStream stream = areaResource.getInputStream();
        String html = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));

        Document document = Jsoup.parse(html);
        Element ele = document.select("script[class=J_auto-load]").first();
        String text = ele.getElementsByTag("script").first().data();
        Document allNode = Jsoup.parse(text);
        Elements elements = allNode.getElementsByClass("list");
        for (Element node : elements) {
            Element aNode = node.getElementsByTag("a").first();
            System.err.println(aNode.attr("href") + "===>" + aNode.text());

            Elements select = node.getElementsByTag("li");
            select.forEach(t -> System.err.println(t.getElementsByTag("a").first().text()));
        }
    }
}
