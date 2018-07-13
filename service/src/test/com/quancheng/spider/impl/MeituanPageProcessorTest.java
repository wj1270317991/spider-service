package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.dataobject.City;
import com.quancheng.spider.model.meituan.AreaInfo;
import com.quancheng.spider.model.meituan.DetailInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import us.codecraft.webmagic.Request;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@RunWith(SpringRunner.class)
public class MeituanPageProcessorTest {

    @Value("classpath:meituan/city.json")
    private Resource cityResource;

    @Value("classpath:meituan/itemlist.html")
    private Resource itemResource;

    @Value("classpath:meituan/meituan_detail.html")
    private Resource detailResource;

    @Test
    public void testCityJson() throws IOException {
        InputStream stream = cityResource.getInputStream();
        String json = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        List<City> cities = MeituanJsonHandler.parseCity(json);
        List<Request> cityRequests = MeituanJsonHandler.getCityRequests(cities);
        System.err.println(JSON.toJSONString(cityRequests));
        Assert.notEmpty(cities, "城市数据解析失败");
    }

    @Test
    public void testParseArea() throws IOException, CloneNotSupportedException {
        InputStream stream = itemResource.getInputStream();
        String html = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        html = html.substring(html.indexOf("window._appState") + "window._appState".length());
        html = html.substring(html.indexOf("{"));
        html = html.substring(0, html.indexOf(";</script>"));

        List<AreaInfo> areaInfos = MeituanJsonHandler.parseArea(html);
        City city = new City();

        List<Request> areaRequests = MeituanJsonHandler.getAreaRequests(city, areaInfos);
        System.err.println(JSON.toJSONString(areaRequests));
        org.junit.Assert.assertTrue(areaRequests.size() > 0);
    }

    @Test
    public void parseDetail() throws IOException {
        InputStream stream = detailResource.getInputStream();
        String html = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        html = html.substring(html.indexOf("window._appState") + "window._appState".length());
        html = html.substring(html.indexOf("{"));
        html = html.substring(0, html.indexOf(";</script>"));

        JSONObject appObj = JSON.parseObject(html);
        JSONObject detailInfo = appObj.getJSONObject("detailInfo");
        DetailInfo detail = detailInfo.toJavaObject(DetailInfo.class);
        org.junit.Assert.assertNotNull(detail);
    }
}
