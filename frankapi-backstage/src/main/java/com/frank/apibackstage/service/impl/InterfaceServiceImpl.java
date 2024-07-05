package com.frank.apibackstage.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.frank.apibackstage.model.request.IpReqParams;
import com.frank.apibackstage.model.request.SearchWordReqParams;
import com.frank.apibackstage.service.InterfaceService;
import org.springframework.stereotype.Service;

import static com.frank.apicommon.utils.RequestUtil.buildUrl;
import static com.frank.apicommon.utils.RequestUtil.sendGetRequest;

/**
 * @author Frank
 * @date 2024/7/5
 */
@Service
public class InterfaceServiceImpl implements InterfaceService {

    /**
     * 获取 IP 信息
     *
     * @param ipReqParams IP 请求参数
     * @return IP 信息
     */
    @Override
    public JSONObject getIpInfo(IpReqParams ipReqParams) {
        String url = buildUrl("https://api.vvhan.com/api/ipInfo/", ipReqParams);
        return sendGetRequest(url, "info");
    }

    /**
     * 获取手机号码相关信息
     *
     * @param phone 手机号码
     * @return 手机号码信息
     */
    @Override
    public JSONObject getPhoneInfo(String phone) {
        String url = buildUrl("https://api.vvhan.com/api/phone/", phone);
        return sendGetRequest(url, "info");
    }

    /**
     * 每日一句励志英语
     *
     * @return 励志英语
     */
    @Override
    public JSONObject getDailyEnglish() {
        String url = "https://api.vvhan.com/api/dailyEnglish";
        return sendGetRequest(url, "data");
    }

    /**
     * 查单词
     *
     * @param searchWordReqParams 查单词请求参数
     * @return 单词
     */
    @Override
    public JSONObject searchWord(SearchWordReqParams searchWordReqParams) {
        String url = buildUrl("https://dict.youdao.com/suggest", searchWordReqParams);
        return sendGetRequest(url, "data.entries");
    }

    /**
     * 天气预报
     *
     * @return 天气预报
     */
    @Override
    public JSONObject getWeather() {
        String url = "https://query.asilu.com/weather/gaode/";
        return sendGetRequest(url, "forecasts");
    }
}
