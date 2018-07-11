package com.quancheng.spider.api;

import com.quancheng.spider.core.DataSourceEnum;
import com.quancheng.spider.core.Executable;
import com.quancheng.spider.core.PageEnum;
import com.quancheng.spider.impl.DianpingPageProcessor;
import com.quancheng.spider.impl.MeiTuanPageProcessor;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        Executable executable = null;
        DataSourceEnum sourceEnum = DataSourceEnum.valueOf(origin);
        if (sourceEnum == DataSourceEnum.dianping) {
            executable = dianpingPageProcessor;
        } else if (sourceEnum == DataSourceEnum.meituan) {
            executable = meiTuanPageProcessor;
        }
        if (null != executable) {
            executable.exec(targetUrl, PageEnum.valueOf(pageType));
        }
        return SUCCESS;
    }
}
