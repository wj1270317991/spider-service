package com.quancheng.spider.job;

import com.quancheng.spider.impl.MeiTuanPageProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
@Service
//@JobHander(value = "meituanSpiderJob")
public class MeituanSpiderHandler {
    @Autowired
    private MeiTuanPageProcessor meiTuanProcessor;
}
