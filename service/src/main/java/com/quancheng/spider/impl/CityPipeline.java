package com.quancheng.spider.impl;

import com.quancheng.spider.core.AbstractDbPipeline;
import com.quancheng.spider.core.PageEnum;
import com.quancheng.spider.dao.CityMapper;
import com.quancheng.spider.dataobject.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.ResultItems;

import java.util.List;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-12
 **/
@Component
public class CityPipeline extends AbstractDbPipeline<City> {
    @Autowired
    private CityMapper cityMapper;

    @Override
    public List<City> getItems(ResultItems resultItems) {
        return resultItems.get(PageEnum.RESULT_CITY_KEY.name());
    }

    @Override
    public int save(City city) {
        return cityMapper.insert(city);
    }
}
