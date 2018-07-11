package com.quancheng.spider.impl;

import com.quancheng.spider.model.City;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

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
public class CityJsonTest {
    @Value("classpath:meituan/city.json")
    private Resource resource;

    @Test
    public void testCityJson() throws IOException {
        InputStream stream = resource.getInputStream();
        String json = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        List<City> cities = MeituanJsonHandler.parseCity(json);
        Assert.notEmpty(cities, "城市数据解析失败");
    }
}