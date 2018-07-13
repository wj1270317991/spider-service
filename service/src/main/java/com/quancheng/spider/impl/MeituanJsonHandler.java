package com.quancheng.spider.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.quancheng.spider.core.ExtraKeyEnum;
import com.quancheng.spider.dataobject.City;
import com.quancheng.spider.model.meituan.AreaInfo;
import com.quancheng.spider.model.meituan.CityInfo;
import com.quancheng.spider.model.meituan.ProvinceInfo;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Request;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.regex.Pattern.compile;

/**
 * @program: spider.all
 * @author: Robert
 * @create: 2018-07-11
 **/
public class MeituanJsonHandler {
    private static final String CITY_TEMPLATE = "http://%s.meituan.com/meishi/";
    private static final String PAGE_AREA_SUFFIX = "pn1/";



    public static List<City> parseCity(String json) {
        if (StringUtils.isEmpty(json)) {
            return Collections.emptyList();
        }
        JSONArray provinces = JSON.parseArray(json);
        List<ProvinceInfo> provincesList = provinces.toJavaList(ProvinceInfo.class);
        List<City> result = new ArrayList<>();
        City city;
        for (ProvinceInfo provinceInfo : provincesList) {
            List<CityInfo> cityInfoList = provinceInfo.getCityInfoList();
            for (CityInfo cityInfo : cityInfoList) {
                city = new City();
                city.setName(cityInfo.getName());
                city.setProvinceName(provinceInfo.getProvinceName());
                city.setAcronym(cityInfo.getAcronym());
                result.add(city);
            }
        }
        return result;
    }

    public static List<AreaInfo> parseArea(String json) {
        JSONObject appObj = JSON.parseObject(json);
        JSONObject filtersObj = appObj.getJSONObject("filters");
        JSONArray areas = filtersObj.getJSONArray("areas");
        List<AreaInfo> result = new ArrayList<>();
        for (int i = 0; i < areas.size(); i++) {
            JSONObject jsonObject = areas.getJSONObject(i);
            AreaInfo areaInfo = jsonObject.toJavaObject(AreaInfo.class);

            // TODO 为防止被封IP仅供测试
            if (!StringUtils.equals("浦东新区", areaInfo.getName())) {
                continue;
            }

            JSONArray subAreas = jsonObject.getJSONArray("subAreas");
            List<AreaInfo> areaList = subAreas.toJavaList(AreaInfo.class);
            if (null != areaList && areaList.size() > 1) {
                areaList = areaList.stream().filter(rs -> !StringUtils.equals("全部", rs.getName()))
                        .collect(Collectors.toList());
            }

            // TODO 为防止被封IP仅供测试
            for (AreaInfo area : areaList) {
                area.setAreaName(areaInfo.getName());
            }
            result.addAll(areaList);
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

    public static List<Request> getCityRequests(List<City> cities) {
        List<Request> requests = new ArrayList<>(cities.size());
        Map<String, Object> extraMap;
        Request request;
        for (City city : cities) {
            // TODO 临时测试
            if (!StringUtils.equals(city.getProvinceName(), "上海")) {
                continue;
            }

            request = new Request();
            request.setUrl(String.format(CITY_TEMPLATE, city.getAcronym()));
            extraMap = new HashMap<>();
            extraMap.put(ExtraKeyEnum.KEY.name(), ExtraKeyEnum.URLS.name());
            extraMap.put(ExtraKeyEnum.URL_TYPE.name(), "area");
            extraMap.put(ExtraKeyEnum.CITY.name(), city);
            request.setExtras(extraMap);
            requests.add(request);
        }
        return requests;
    }

    public static List<Request> getAreaRequests(City city, List<AreaInfo> list) throws CloneNotSupportedException {
        List<Request> requests = new ArrayList<>(list.size());
        Map<String, Object> extraMap;
        Request request;
        for (AreaInfo areaInfo : list) {
            City ct = (City) city.clone();
            ct.setAreaName(areaInfo.getAreaName());
            ct.setTradingArea(areaInfo.getName());

            request = new Request();
            request.setUrl(areaInfo.getUrl() + PAGE_AREA_SUFFIX);
            extraMap = new HashMap<>();
            extraMap.put(ExtraKeyEnum.KEY.name(), ExtraKeyEnum.ITEM.name());
            extraMap.put(ExtraKeyEnum.CITY.name(), ct);
            request.setExtras(extraMap);
            requests.add(request);
        }
        return requests;
    }
}
