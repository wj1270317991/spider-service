package com.quancheng.spider.meituan;

import com.quancheng.spider.core.AppContextUtil;
import com.quancheng.spider.core.Executable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.core.env.Environment;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.CollectorPipeline;
import us.codecraft.webmagic.pipeline.ResultItemsCollectorPipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: webmagic-parent
 * @author: Robert
 * @create: 2018-07-02
 **/
public class MeituanCityProcessor implements PageProcessor, Executable {
    private static final String CITYURL = "http://www.meituan.com/changecity/";
    private static final String CITY_KEY = "citys";
    private Environment environment = (Environment) AppContextUtil.getBean("environment");

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Elements cityArea = html.getDocument().getElementsByClass("alphabet-city-area");
        if (null != cityArea && cityArea.size() > 0) {
            List<String> result = new ArrayList<>(cityArea.size());

            Element element = cityArea.get(0);
            Elements cityElements = element.select("a[href]");
            for (Element ele : cityElements) {
                String text = ele.text();
                String href = ele.attr("href");
                result.add("http:" + href + "/meishi/pn1/");
            }
            page.putField(CITY_KEY, result);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setCycleRetryTimes(3).setRetryTimes(3).setSleepTime(1000).setTimeOut(3 * 60 * 1000)
                .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .setCharset("UTF-8");
    }

    public static void main(String[] args) throws IOException {
        Executable executable = new MeituanCityProcessor();
        executable.exec(null);
    }


    @Override
    public void exec(Object param) {
        if (null == param && !(param instanceof List)) {
            return;
        }
        List<String> list = (List<String>) param;
        String cityUrl = environment.getProperty("meituan.city.url");

        CollectorPipeline<ResultItems> collectorPipeline = new ResultItemsCollectorPipeline();
        Spider.create(new MeituanCityProcessor())
                .addUrl(cityUrl)
                .addPipeline(collectorPipeline)
                .thread(5)
                .run();
        ResultItems resultItems = collectorPipeline.getCollected().get(0);
        list.addAll(resultItems.get(CITY_KEY));
    }
}
