package com.quancheng.spider.core;

import us.codecraft.webmagic.Request;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
public interface Executable {
    void exec();

    void exec(Request request);
}
