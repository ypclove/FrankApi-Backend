package com.frank.apibackstage.controller;

import com.alibaba.fastjson.JSONObject;
import com.frank.apibackstage.model.request.IpReqParams;
import com.frank.apibackstage.model.request.SearchWordReqParams;
import com.frank.apibackstage.service.InterfaceService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

/**
 * @author Frank
 * @date 2024/7/2
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/")
public class InterfaceController {

    @Resource
    private InterfaceService interfaceService;

    /**
     * 获取 IP 信息
     *
     * @param ipReqParams IP 请求参数
     * @return IP 信息
     */
    @PostMapping("/ipInfo")
    public BaseResponse<JSONObject> getIpInfo(@Valid @RequestBody IpReqParams ipReqParams) {
        JSONObject ipInfo = interfaceService.getIpInfo(ipReqParams);
        return ResultUtils.success(ipInfo);
    }

    /**
     * 获取手机号码相关信息
     *
     * @param phone 手机号码
     * @return 手机号码信息
     */
    @GetMapping("/phoneInfo")
    public BaseResponse<JSONObject> getPhoneInfo(@Valid @NotEmpty(message = "手机号不能为空")
                                                 @Pattern(regexp = "^1(3|4|5|6|7|8|9)\\d{9}$", message = "手机号码无效（仅支持国内）")
                                                 String phone) {
        JSONObject phoneInfo = interfaceService.getPhoneInfo(phone);
        return ResultUtils.success(phoneInfo);
    }

    /**
     * 每日一句励志英语
     *
     * @return 励志英语
     */
    @GetMapping("/dailyEnglish")
    public BaseResponse<JSONObject> getDailyEnglish() {
        JSONObject dailyEnglish = interfaceService.getDailyEnglish();
        return ResultUtils.success(dailyEnglish);
    }

    /**
     * 查单词
     *
     * @return 单词
     */
    @PostMapping("/searchWord")
    public BaseResponse<JSONObject> searchWord(@Valid @RequestBody SearchWordReqParams searchWordReqParams) {
        JSONObject words = interfaceService.searchWord(searchWordReqParams);
        return ResultUtils.success(words);
    }

    /**
     * 天气预报
     *
     * @return 天气预报
     */
    @GetMapping("/getWeather")
    public BaseResponse<JSONObject> getWeather() {
        JSONObject weather = interfaceService.getWeather();
        return ResultUtils.success(weather);
    }
}
