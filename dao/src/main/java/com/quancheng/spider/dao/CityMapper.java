package com.quancheng.spider.dao;

import com.quancheng.spider.dataobject.City;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
@Mapper
public interface CityMapper {
    int insert(City city);
}
