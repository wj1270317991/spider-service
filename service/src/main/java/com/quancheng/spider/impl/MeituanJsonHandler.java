package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.quancheng.spider.model.City;
import com.quancheng.spider.model.Province;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class MeituanJsonHandler {
    public static List<City> parseCity(String json) {
        if (StringUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        JSONArray provinces = JSON.parseArray(json);
        List<Province> provincesList = provinces.toJavaList(Province.class);
        List<City> result = new ArrayList<>();
        for (Province province : provincesList) {
            List<City> cityInfoList = province.getCityInfoList();
            cityInfoList.forEach(rs -> rs.setProvinceName(province.getProvinceName()));
            result.addAll(cityInfoList);
        }
        return result;
    }

    /**
     * 提取美团window._appState JSON
     *
     * @param page
     * @return
     */
    public static String extractAppJson(String page) {
        Pattern pattern = compile("window._appState =(.*);</script>");
        Matcher matcher = pattern.matcher(page);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
