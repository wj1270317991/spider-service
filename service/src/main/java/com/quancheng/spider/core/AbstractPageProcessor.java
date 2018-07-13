package com.quancheng.spider.core;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Map;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-05
 **/
public abstract class AbstractPageProcessor implements PageProcessor, Executable {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${cycle.retry.times}")
    private int cycleRetryTimes;

    @Value("${sleep.time}")
    private int sleepTime;

    protected Request getRequest(String url, String pageType) {
        Request request = new Request(url);
        if (StringUtils.isNotEmpty(pageType)) {
            request.putExtra(ExtraKeyEnum.KEY.name(), pageType);
        }
        return request;
    }

    protected Request getRequest(String url, Map<String, Object> paramMap) {
        Request request = new Request(url);
        if (MapUtils.isNotEmpty(paramMap)) {
            for (String key : paramMap.keySet()) {
                request.putExtra(key, paramMap.get(key));
            }
        }
        return request;
    }

    @Override
    public Site getSite() {
        return Site.me().setCycleRetryTimes(cycleRetryTimes).setRetryTimes(3).setSleepTime(sleepTime).setTimeOut(2 * 60 * 1000)
                .setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.87 Safari/537.36")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
    }

    @Override
    public void process(Page page) {
        String pageType = (String) page.getRequest().getExtra(ExtraKeyEnum.KEY.name());
        ExtraKeyEnum keyEnum = ExtraKeyEnum.valueOf(pageType);
        switch (keyEnum) {
            case URLS:
                getUrls(page);
                break;
            case ITEM:
                parseJson(page);
                parseHtml(page);
                break;
            case DETAIL:
                parseDetailPage(page);
                break;
            default:
                break;
        }
    }

    /**
     * 获取下一页url
     *
     * @param page
     */
    public abstract void nextPage(Page page);

    /**
     * 获取详情页URL
     *
     * @param page
     */
    public abstract void getDetailUrl(Page page);

    /**
     * 获取URL列表(如从城市列表中获取目标URL集合)
     *
     * @param page
     */
    public abstract void getUrls(Page page);

    /**
     * 解析JSON
     *
     * @param page
     */
    public abstract void parseJson(Page page);

    /**
     * 解析HTML
     *
     * @param page
     */
    public abstract void parseHtml(Page page);

    /**
     * 解析HTML
     *
     * @param page
     */
    public abstract void parseDetailPage(Page page);

    /**
     * 对象转换
     *
     * @param object
     * @param <T>
     * @return
     */
    public abstract <T> T transform(Object object);
}
