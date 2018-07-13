package com.quancheng.spider.core;

import us.codecraft.webmagic.Request;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
public interface Executable {
    /**
     * 执行抓取任务
     */
    void exec();

    /**
     * 抓取指定Request数据
     *
     * @param request
     */
    void exec(Request request);
}
