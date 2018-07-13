package com.quancheng.spider.api;

import com.quancheng.spider.core.DataSourceEnum;
import com.quancheng.spider.core.ExtraKeyEnum;
import com.quancheng.spider.impl.DianpingPageProcessor;
import com.quancheng.spider.impl.MeiTuanPageProcessor;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import us.codecraft.webmagic.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-06
 **/
@Api
@RestController
@RequestMapping(value = "/spider")
public class SpiderController {
    private static final String SUCCESS = "SUCCESS";
    @Autowired
    private MeiTuanPageProcessor meiTuanPageProcessor;
    @Autowired
    private DianpingPageProcessor dianpingPageProcessor;

    @ResponseBody
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public String getPage(@RequestParam("targetUrl") String targetUrl,
                          @RequestParam("origin") String origin,
                          @RequestParam("pageType") String pageType) {
        DataSourceEnum sourceEnum = DataSourceEnum.valueOf(origin);
        if (sourceEnum == DataSourceEnum.dianping) {
        } else if (sourceEnum == DataSourceEnum.meituan) {
            Request request = new Request();
            request.setUrl(targetUrl);
            Map<String, Object> map = new HashMap<>();
            map.put(ExtraKeyEnum.KEY.name(), ExtraKeyEnum.valueOf(pageType));
            map.put(ExtraKeyEnum.URL_TYPE.name(), "city");
            request.setExtras(map);
            meiTuanPageProcessor.exec(request);
        }
        return SUCCESS;
    }
}
