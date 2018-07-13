package com.quancheng.spider.impl;

import com.quancheng.spider.dao.CityMapper;
import com.quancheng.spider.dataobject.City;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class CityDaoTest {
    @Autowired
    private CityMapper cityMapper;

    @Test
    public void save() {
        City city = new City();
        city.setName("上海");
        city.setProvinceName("上海市");
        city.setAreaName("浦东新区");
        int result = cityMapper.insert(city);
        Assert.assertTrue(result > 0);
    }

}
