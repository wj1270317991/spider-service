package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.model.DetailInfo;
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

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@RunWith(SpringRunner.class)
public class MerchantDetailTest {
    @Value("classpath:meituan/meituan_detail.html")
    private Resource resource;

    @Test
    public void parseDetail() throws IOException {
        InputStream stream = resource.getInputStream();
        String html = StreamUtils.copyToString(stream, Charset.forName("UTF-8"));
        html = html.substring(html.indexOf("window._appState") + "window._appState".length());
        html = html.substring(html.indexOf("{"));
        html = html.substring(0, html.indexOf(";</script>"));

        JSONObject appObj = JSON.parseObject(html);
        JSONObject detailInfo = appObj.getJSONObject("detailInfo");
        DetailInfo detail = detailInfo.toJavaObject(DetailInfo.class);
        Assert.assertNotNull(detail);
    }
}
