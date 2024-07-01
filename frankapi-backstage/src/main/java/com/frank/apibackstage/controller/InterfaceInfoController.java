package com.frank.apibackstage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frank.apibackstage.annotation.AuthCheck;
import com.frank.apibackstage.model.dto.interfaceinfo.*;
import com.frank.apibackstage.model.entity.InterfaceInfo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.InterfaceInfoService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.constant.CommonConstant;
import com.frank.apicommon.enums.InterfaceStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Frank
 * @since 2024/06/26
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/interfaceInfo")
public class InterfaceInfoController {

    @Resource
    private UserService userService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    private final Gson gson = new Gson();

    /**
     * 创建接口
     *
     * @param interfaceInfoAddRequest 接口创建请求
     * @param request                 HttpServletRequest
     * @return 接口 Id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Long> addInterface(@Valid @RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest,
                                           HttpServletRequest request) {
        Long interfaceId = interfaceInfoService.addInterface(interfaceInfoAddRequest, request);
        return ResultUtils.success(interfaceId);
    }

    /**
     * 删除接口信息
     *
     * @param interfaceId 接口 Id
     * @param request     HttpServletRequest
     * @return 删除接口是否成功
     */
    @DeleteMapping("/delete/{interfaceId}")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Boolean> deleteInterface(@PathVariable
                                                 @NotNull(message = "接口 Id 不能为空")
                                                 @Min(value = 1L, message = "接口 Id 错误")
                                                 Long interfaceId,
                                                 HttpServletRequest request) {
        Boolean result = interfaceInfoService.deleteInterface(interfaceId, request);
        return ResultUtils.success(result);
    }

    /**
     * 更新接口
     *
     * @param interfaceInfoUpdateRequest 接口更新请求
     * @param request                    HttpServletRequest
     * @return 更新接口是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = 0)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateInterface(@Valid @RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest,
                                                 HttpServletRequest request) {
        Boolean result = interfaceInfoService.updateInterface(interfaceInfoUpdateRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据 Id 查询接口
     *
     * @param interfaceId 接口 Id
     * @return 接口信息
     */
    @GetMapping("/get/{interfaceId}")
    public BaseResponse<InterfaceInfo> getInterfaceById(@PathVariable @Valid
                                                        @NotNull(message = "接口 Id 不能为空")
                                                        @Min(value = 1L, message = "接口 Id 错误")
                                                        Long interfaceId) {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 分页获取接口列表
     *
     * @param interfaceInfoQueryRequest 接口查询请求
     * @param request                   HttpServletRequest
     * @return 接口分页列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<InterfaceInfo>> getInterfaceListByPage(InterfaceInfoQueryRequest interfaceInfoQueryRequest,
                                                                    HttpServletRequest request) {
        if (Objects.isNull(interfaceInfoQueryRequest)) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);

        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        String url = interfaceInfoQueryRequest.getUrl();
        String name = interfaceInfoQueryRequest.getName();
        Integer method = interfaceInfoQueryRequest.getMethod();
        String description = interfaceInfoQueryRequest.getDescription();
        Integer status = interfaceInfoQueryRequest.getStatus();
        Integer reduceScore = interfaceInfoQueryRequest.getReduceScore();
        String returnFormat = interfaceInfoQueryRequest.getReturnFormat();
        long current = interfaceInfoQueryRequest.getCurrent();
        long pageSize = interfaceInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(description)) {
            queryWrapper.and(qw -> qw.like("name", name).or().like("description", description));
        }
        queryWrapper
                .like(StringUtils.isNotBlank(url), "url", url)
                .like(StringUtils.isNotBlank(returnFormat), "returnFormat", returnFormat)
                .eq(ObjectUtils.anyNotNull(method), "method", method)
                .eq(ObjectUtils.isNotEmpty(status), "status", status)
                .eq(ObjectUtils.isNotEmpty(reduceScore), "reduceScore", reduceScore);
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, pageSize), queryWrapper);

        Boolean result = userService.isTourist(request);
        // 不是管理员只能查看已经开启的，不能查看"审核中"或者"关闭"的接口
        if (BooleanUtils.isTrue(result)) {
            List<InterfaceInfo> interfaceInfoList = interfaceInfoPage
                    .getRecords()
                    .stream()
                    .filter(interfaceInfo -> interfaceInfo.getStatus().equals(InterfaceStatusEnum.ONLINE.getCode()))
                    .collect(Collectors.toList());
            interfaceInfoPage.setRecords(interfaceInfoList);
        }
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 根据搜索文本分页获取接口列表
     *
     * @param interfaceInfoQueryRequest 文本搜索请求
     * @param request                   HttpServletRequest
     * @return 接口列表
     */
    @GetMapping("/get/searchText")
    public BaseResponse<Page<InterfaceInfo>> getInterfaceListBySearchTextPage(InterfaceInfoSearchTextRequest interfaceInfoQueryRequest,
                                                                              HttpServletRequest request) {
        if (interfaceInfoQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        InterfaceInfo interfaceInfoQuery = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoQueryRequest, interfaceInfoQuery);

        String searchText = interfaceInfoQueryRequest.getSearchText();
        String sortField = interfaceInfoQueryRequest.getSortField();
        String sortOrder = interfaceInfoQueryRequest.getSortOrder();
        long current = interfaceInfoQueryRequest.getCurrent();
        long pageSize = interfaceInfoQueryRequest.getPageSize();

        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like(StringUtils.isNotBlank(searchText), "name", searchText)
                    .or().like(StringUtils.isNotBlank(searchText), "description", searchText));
        }
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, pageSize), queryWrapper);
        // 不是管理员只能查看已经上线的，不能查看"审核中"或者"关闭"的接口
        if (!userService.isAdmin(request)) {
            List<InterfaceInfo> interfaceInfoList = interfaceInfoPage
                    .getRecords()
                    .stream()
                    .filter(interfaceInfo -> interfaceInfo.getStatus().equals(InterfaceStatusEnum.ONLINE.getCode()))
                    .collect(Collectors.toList());
            interfaceInfoPage.setRecords(interfaceInfoList);
        }
        return ResultUtils.success(interfaceInfoPage);
    }

    /**
     * 开启接口
     *
     * @param interfaceId 接口 Id
     * @return 开启接口是否成功
     */
    @AuthCheck(mustRole = 0)
    @PostMapping("/online/{interfaceId}")
    public BaseResponse<Boolean> onlineInterface(@PathVariable @Valid
                                                 @NotNull(message = "接口 Id 不能为空")
                                                 @Min(value = 1L, message = "接口 Id 错误")
                                                 Long interfaceId) {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        interfaceInfo.setStatus(InterfaceStatusEnum.ONLINE.getCode());
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    /**
     * 关闭接口
     *
     * @param interfaceId 接口 Id
     * @return 关闭接口是否成功
     */
    @PostMapping("/offline/{interfaceId}")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Boolean> offlineInterface(@PathVariable @Valid
                                                  @NotNull(message = "接口 Id 不能为空")
                                                  @Min(value = 1L, message = "接口 Id 错误")
                                                  Long interfaceId) {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(interfaceId);
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        interfaceInfo.setStatus(InterfaceStatusEnum.OFFLINE.getCode());
        return ResultUtils.success(interfaceInfoService.updateById(interfaceInfo));
    }

    /**
     * 调用接口
     *
     * @param invokeRequest 接口调用请求
     * @param request       HttpServletRequest
     * @return 接口信息
     */
    @PostMapping("/invoke")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Object> invokeInterface(@Valid @RequestBody InvokeRequest invokeRequest,
                                                HttpServletRequest request) {
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(invokeRequest.getId());
        if (Objects.isNull(interfaceInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        if (!interfaceInfo.getStatus().equals(InterfaceStatusEnum.ONLINE.getCode())) {
            throw new BusinessException(StatusCode.PARAMS_ERROR, "接口未开启");
        }
        // 构建请求参数
        List<InvokeRequest.Field> fieldList = invokeRequest.getRequestParams();
        String requestParams = "{}";
        if (fieldList != null && !fieldList.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            for (InvokeRequest.Field field : fieldList) {
                jsonObject.addProperty(field.getFieldName(), field.getValue());
            }
            requestParams = gson.toJson(jsonObject);
        }
        Map<String, Object> params = new Gson().fromJson(requestParams, new TypeToken<Map<String, Object>>() {
        }.getType());
        UserVO loginUser = userService.getLoginUser(request);
        // String accessKey = loginUser.getAccessKey();
        // String secretKey = loginUser.getSecretKey();
        // TODO：继续优化
        // try {
        //     QiApiClient qiApiClient = new QiApiClient(accessKey, secretKey);
        //     CurrencyRequest currencyRequest = new CurrencyRequest();
        //     currencyRequest.setMethod(interfaceInfo.getMethod());
        //     currencyRequest.setPath(interfaceInfo.getUrl());
        //     currencyRequest.setRequestParams(params);
        //     ResultResponse response = apiService.request(qiApiClient, currencyRequest);
        //     return ResultUtils.success(response.getData());
        // } catch (Exception e) {
        //     throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        // }
        return ResultUtils.success(1);
    }
}
