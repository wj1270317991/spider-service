package com.quancheng.spider.dao;

import com.quancheng.spider.dataobject.PoiInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-03
 **/
@Mapper
public interface PoiInfoMapper {

    int insert(PoiInfo poiInfo);
}
