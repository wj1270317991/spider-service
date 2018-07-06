package com.quancheng.spider.core;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
public interface Executable {
    void exec();

    /**
     * 获取指定URL的数据Executable
     * @param url
     * @param pageEnum
     */
    void exec(String url, PageEnum pageEnum);
}
