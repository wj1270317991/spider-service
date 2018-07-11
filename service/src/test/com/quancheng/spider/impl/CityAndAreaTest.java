package com.quancheng.spider.impl;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.model.Area;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@RunWith(SpringRunner.class)
public class CityAndAreaTest {
    @Value("classpath:meituan/itemlist.html")
    private Resource resource;

    @Test
    public void testParseArea() throws IOException {
        InputStream stream = resource.getInputStream();
        String html = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        html = html.substring(html.indexOf("window._appState") + "window._appState".length());
        html = html.substring(html.indexOf("{"));
        html = html.substring(0, html.indexOf(";</script>"));

        JSONObject appObj = JSON.parseObject(html);
        JSONObject filtersObj = appObj.getJSONObject("filters");
        JSONArray areas = filtersObj.getJSONArray("areas");
        List<Area> result = new ArrayList<>();
        for (int i = 0; i < areas.size(); i++) {
            JSONObject jsonObject = areas.getJSONObject(i);
            JSONArray subAreas = jsonObject.getJSONArray("subAreas");
            List<Area> areaList = subAreas.toJavaList(Area.class);
            result.addAll(areaList);
        }

        List<Area> collect = result.stream().filter(rs -> !StringUtils.equals("全部", rs.getName())).collect(Collectors.toList());
        Assert.assertTrue(collect.size() > 0);
    }
}
