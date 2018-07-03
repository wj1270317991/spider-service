package com.quancheng.spider.job;

import com.quancheng.spider.dao.PoiInfoMapper;
import com.quancheng.spider.dataobject.PoiInfo;
import com.quancheng.spider.meituan.MeiTuanProcessor;
import com.quancheng.spider.meituan.MeituanCityProcessor;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHander;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    private MeituanCityProcessor meituanCityProcessor;
    @Autowired
    private MeiTuanProcessor meiTuanProcessor;
    @Autowired
    private PoiInfoMapper poiInfoMapper;

    @Override
    public ReturnT<String> execute(String... params) throws Exception {
        new Thread(() -> {
            List<String> targetUrls = new ArrayList<>();
            meituanCityProcessor.exec(targetUrls);

//            List<String> targetUrls = Arrays.asList(
//                    "http://as.meituan.com/meishi/pn1/",
//                    "http://anqing.meituan.com/meishi/pn1/");
            logger.info("City list:{}", targetUrls);
            meiTuanProcessor.exec(targetUrls);
            logger.info("Job exec end");
        }).start();
        return ReturnT.SUCCESS;
    }

    private void saveOrUpdate(PoiInfo rs) {
        try {
            poiInfoMapper.insert(rs);
        } catch (Exception e) {
            logger.error("Save or update failed>", e);
        }
    }
}
