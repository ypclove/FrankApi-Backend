package com.frank.apibackstage.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.InterfaceInfoMapper;
import com.frank.apibackstage.model.dto.interfaceinfo.InterfaceInfoAddRequest;
import com.frank.apibackstage.model.dto.interfaceinfo.InterfaceInfoUpdateRequest;
import com.frank.apibackstage.model.dto.interfaceinfo.RequestParamsField;
import com.frank.apibackstage.model.dto.interfaceinfo.ResponseParamsField;
import com.frank.apibackstage.model.entity.InterfaceInfo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.InterfaceInfoService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.exception.BusinessException;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Frank
 * @since 2024/06/26
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo> implements InterfaceInfoService {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 创建接口
     *
     * @param interfaceInfoAddRequest 接口创建请求
     * @param request                 HttpServletRequest
     * @return 接口 Id
     */
    @Override
    public Long addInterface(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getRequestParams())) {
            // 获取请求参数列表
            List<RequestParamsField> requestParamsFields = interfaceInfoAddRequest
                    .getRequestParams()
                    .stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String requestParams = JSONUtil.toJsonStr(requestParamsFields);
            interfaceInfo.setRequestParams(requestParams);
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoAddRequest.getResponseParams())) {
            // 获取响应参数
            List<ResponseParamsField> responseParamsFields = interfaceInfoAddRequest
                    .getResponseParams()
                    .stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String responseParams = JSONUtil.toJsonStr(responseParamsFields);
            interfaceInfo.setResponseParams(responseParams);
        }
        BeanUtils.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        interfaceInfo.setMethod(interfaceInfo.getMethod());
        interfaceInfo.setUrl(interfaceInfo.getUrl().trim());
        UserVO loginUser = userService.getLoginUser(request);
        interfaceInfo.setUserId(loginUser.getId());
        int result = interfaceInfoMapper.insert(interfaceInfo);
        if (result <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR);
        }
        return interfaceInfo.getId();
    }

    /**
     * 删除接口
     *
     * @param interfaceId 接口 Id
     * @param request     HttpServletRequest
     * @return 删除接口是否成功
     */
    @Override
    public Boolean deleteInterface(Long interfaceId, HttpServletRequest request) {
        UserVO user = userService.getLoginUser(request);
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceId);
        // 判断是否存在
        if (ObjectUtils.anyNull(interfaceInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!interfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }
        int result = interfaceInfoMapper.deleteById(interfaceId);
        if (result <= 0) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR, "删除接口失败");
        }
        return true;
    }

    /**
     * 更新接口
     *
     * @param interfaceInfoUpdateRequest 更新接口请求
     * @param request                    HttpServletRequest
     * @return 更新接口是否成功
     */
    @Override
    public Boolean updateInterface(InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {
        InterfaceInfo interfaceInfo = interfaceInfoMapper.selectById(interfaceInfoUpdateRequest.getId());
        // 判断是否存在
        if (ObjectUtils.anyNull(interfaceInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getRequestParams())) {
            List<RequestParamsField> requestParamsFields = interfaceInfoUpdateRequest
                    .getRequestParams()
                    .stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String requestParams = JSONUtil.toJsonStr(requestParamsFields);
            interfaceInfo.setRequestParams(requestParams);
        } else {
            interfaceInfo.setRequestParams("[]");
        }
        if (CollectionUtils.isNotEmpty(interfaceInfoUpdateRequest.getResponseParams())) {
            List<ResponseParamsField> responseParamsFields = interfaceInfoUpdateRequest
                    .getResponseParams()
                    .stream()
                    .filter(field -> StringUtils.isNotBlank(field.getFieldName()))
                    .collect(Collectors.toList());
            String responseParams = JSONUtil.toJsonStr(responseParamsFields);
            interfaceInfo.setResponseParams(responseParams);
        } else {
            interfaceInfo.setResponseParams("[]");
        }

        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        interfaceInfo.setMethod(interfaceInfo.getMethod());
        interfaceInfo.setUrl(interfaceInfo.getUrl().trim());
        UserVO user = userService.getLoginUser(request);

        // 仅本人或管理员可修改
        if (!interfaceInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }
        int update = interfaceInfoMapper.updateById(interfaceInfo);
        if (update <= 0) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR, "更新接口失败");
        }
        return true;
    }
}
