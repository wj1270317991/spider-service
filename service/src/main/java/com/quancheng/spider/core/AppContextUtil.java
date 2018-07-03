package com.quancheng.spider.core;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppContextUtil implements ApplicationContextAware {

    private static ApplicationContext ctx;

    public static Object getBean(String name) {
        return ctx.getBean(name);
    }

    public static void setCtx(ApplicationContext ctx) {
        AppContextUtil.ctx = ctx;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        ctx = context;
    }
}