package com.quancheng.spider.meituan;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.dao.PoiInfoMapper;
import com.quancheng.spider.dataobject.PoiInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @program: webmagic-parent
 * @author: Robert
 * @create: 2018-06-26
 **/
@Component
public class MeiTuanProcessor implements PageProcessor, Task {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final String DATA_KEY = "POIINFOLIST";

    @Autowired
    private PoiInfoMapper poiInfoMapper;

    @Override
    public void process(Page page) {
        if (null == page.getJson()) {
            page.setSkip(true);
        } else {
            parseJson(page);
        }
    }

    private void parseJson(Page page) {
        try {
            Pattern pattern = compile("window._appState =(.*);</script>");
            Matcher matcher = pattern.matcher(page.getJson().toString());
            while (matcher.find()) {
                String data = matcher.group(1);
                if (StringUtils.isNotEmpty(data)) {
                    JSONObject jsonObject = JSON.parseObject(data);
                    JSONObject poiLists = jsonObject.getJSONObject("poiLists");
                    JSONArray poiInfos = poiLists.getJSONArray("poiInfos");
                    List<PoiInfo> poiInfoList = poiInfos.toJavaList(PoiInfo.class);
                    page.putField(DATA_KEY, poiInfoList);

                    Integer totalCounts = poiLists.getInteger("totalCounts");
                    logger.info("Page url:{}, total size:{}", page.getUrl(), totalCounts);
                    if (totalCounts > 0) {
                        String pageUrl = page.getUrl().toString();
                        pattern = compile("(\\d+)");
                        matcher = pattern.matcher(pageUrl);
                        if (matcher.find()) {
                            String pageNum = matcher.group(1) == null ? "" : matcher.group(1);
                            int pageIndex = Integer.valueOf(pageNum) + 1;
                            pageUrl = matcher.replaceFirst(pageIndex + "");
                            logger.info("Target url is:{}", pageUrl);
                            page.addTargetRequest(pageUrl);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Parse json failed>", e);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(2000).setTimeOut(3 * 60 * 1000)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
    }

    @Override
    public void exec(Object param) {
        CollectorPipeline<ResultItems> pipeline = new ResultItemsCollectorPipeline();
        List<String> urls = (List<String>) param;
        logger.info("tagert url list:{}", urls);
        Spider.create(new MeiTuanProcessor())
                .startUrls(urls)
                .addPipeline(pipeline)
                .thread(5)
                .run();
        List<PoiInfo> result = new ArrayList<>();
        List<ResultItems> resultItems = pipeline.getCollected();
        resultItems.forEach(rs -> result.addAll(rs.get(DATA_KEY)));
        logger.info("Get result list size:{}", result.size());
        result.forEach(this::saveOrUpdate);
    }

    private void saveOrUpdate(PoiInfo rs) {
        try {
            poiInfoMapper.insert(rs);
        } catch (Exception e) {
            logger.error("Save or update failed>", e);
        }
    }
}