package com.frank.apiinterface.controller;

import com.alibaba.fastjson.JSONObject;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apiinterface.model.request.IpReqParams;
import com.frank.apiinterface.model.request.SearchWordReqParams;
import com.frank.apiinterface.model.response.DailyEnglishRespParams;
import com.frank.apiinterface.model.response.IpRespParams;
import com.frank.apiinterface.model.response.PhoneRespParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

import static com.frank.apiinterface.utils.RequestUtil.*;

/**
 * @author Frank
 * @date 2024/7/2
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/")
public class InterfaceController {

    /**
     * 获取 ip 地址相关信息
     */
    @PostMapping("/ipInfo")
    public BaseResponse<IpRespParams> getIpInfo(@Valid @RequestBody IpReqParams ipReqParams) {
        String url = buildUrl("https://api.vvhan.com/api/ipInfo/", ipReqParams);
        return sendGetRequest(url, IpRespParams.class, "info");
    }

    /**
     * 获取手机号码相关信息
     */
    @GetMapping("/phoneInfo")
    public BaseResponse<PhoneRespParams> getPhoneInfo(@Valid @NotEmpty(message = "手机号不能为空")
                                                      @Pattern(regexp = "^1(3|4|5|6|7|8|9)\\d{9}$", message = "手机号码无效（仅支持国内）")
                                                      String phone) {
        String url = buildUrl("https://api.vvhan.com/api/phone/", phone);
        return sendGetRequest(url, PhoneRespParams.class, "info");
    }

    /**
     * 每日一句励志英语
     */
    @GetMapping("/dailyEnglish")
    public BaseResponse<DailyEnglishRespParams> getDailyEnglish() {
        String url = "https://api.vvhan.com/api/dailyEnglish";
        return sendGetRequest(url, DailyEnglishRespParams.class, "data");
    }

    /**
     * 查单词
     */
    @PostMapping("/searchWord")
    public BaseResponse<?> searchWord(@Valid @RequestBody SearchWordReqParams searchWordReqParams) {
        String url = buildUrl("https://dict.youdao.com/suggest", searchWordReqParams);
        JSONObject jsonObject = sendGetRequest2(url, "data.entries");
        return ResultUtils.success(jsonObject);
    }

    /**
     * 获取今天和未来三天的天气情况
     *
     * @return 天气状况
     */
    @GetMapping("/getWeather")
    public BaseResponse<?> getWeather() {
        String url = "https://query.asilu.com/weather/gaode/";
        JSONObject jsonObject = sendGetRequest2(url, "forecasts");
        return ResultUtils.success(jsonObject);
    }
}
