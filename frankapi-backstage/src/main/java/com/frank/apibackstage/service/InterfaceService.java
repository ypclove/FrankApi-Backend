package com.frank.apibackstage.service;

import com.alibaba.fastjson.JSONObject;
import com.frank.apibackstage.model.request.IpReqParams;
import com.frank.apibackstage.model.request.SearchWordReqParams;

/**
 * @author Frank
 * @date 2024/7/5
 */
public interface InterfaceService {

    /**
     * 获取 IP 信息
     *
     * @param ipReqParams IP 请求参数
     * @return IP 信息
     */
    JSONObject getIpInfo(IpReqParams ipReqParams);

    /**
     * 获取手机号码相关信息
     *
     * @param phone 手机号码
     * @return 手机号码信息
     */
    JSONObject getPhoneInfo(String phone);

    /**
     * 每日一句励志英语
     *
     * @return 励志英语
     */
    JSONObject getDailyEnglish();

    /**
     * 查单词
     *
     * @param searchWordReqParams 查单词请求参数
     * @return 单词
     */
    JSONObject searchWord(SearchWordReqParams searchWordReqParams);

    /**
     * 天气预报
     *
     * @return 天气预报
     */
    JSONObject getWeather();

}
