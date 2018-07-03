package com.quancheng.spider.job;

import com.quancheng.spider.meituan.MeiTuanProcessor;
import com.quancheng.spider.meituan.MeituanCityProcessor;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
@Service
@JobHander(value = "meituanSpiderJob")
public class MeituanSpiderHandler extends IJobHandler {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        new Thread(() -> {
            List<String> targetUrls = new ArrayList<>();
            new MeituanCityProcessor().exec(targetUrls);

            logger.info("City list:{}", targetUrls);
            new MeiTuanProcessor().exec(targetUrls);
            logger.info("Job exec end");
        }).start();
        return ReturnT.SUCCESS;
    }
}
