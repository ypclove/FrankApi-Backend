package com.frank.apibackstage.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.frank.apibackstage.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.frank.apibackstage.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.frank.apibackstage.model.dto.interfaceinfo.InvokeRequest;
import com.frank.apibackstage.model.entity.InterfaceInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Frank
 * @since 2024/06/26
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    /**
     * 创建接口
     *
     * @param interfaceInfoAddRequest 接口创建请求
     * @param request                 HttpServletRequest
     * @return 接口 Id
     */
    Long addInterface(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request);

    /**
     * 删除接口
     *
     * @param interfaceId 接口 Id
     * @param request     HttpServletRequest
     * @return 删除接口是否成功
     */
    Boolean deleteInterface(Long interfaceId, HttpServletRequest request);

    /**
     * 更新接口
     *
     * @param interfaceInfoUpdateRequest 更新接口请求
     * @param request                    HttpServletRequest
     * @return 更新接口是否成功
     */
    Boolean updateInterface(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request);

    /**
     * 调用接口
     *
     * @param invokeRequest 调用接口请求
     * @param request       HttpServletRequest
     * @return 接口响应
     */
    JSONObject invokeInterface(InvokeRequest invokeRequest, HttpServletRequest request);
}
