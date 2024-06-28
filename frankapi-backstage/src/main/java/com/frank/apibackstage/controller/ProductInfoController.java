package com.frank.apibackstage.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.frank.apibackstage.annotation.AuthCheck;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoAddRequest;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoQueryRequest;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoUpdateRequest;
import com.frank.apibackstage.model.entity.ProductInfo;
import com.frank.apibackstage.service.ProductInfoService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.enums.ProductStatusEnum;
import com.frank.apicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Frank
 * @data 2024/06/22
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/productInfo")
public class ProductInfoController {

    @Resource
    private UserService userService;

    @Resource
    private ProductInfoService productInfoService;

    /**
     * 创建产品
     *
     * @param productInfoAddRequest 产品创建请求
     * @param request               HttpServletRequest
     * @return 产品 Id
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Long> addProduct(@Valid @RequestBody ProductInfoAddRequest productInfoAddRequest,
                                         HttpServletRequest request) {
        Long productId = productInfoService.addProduct(productInfoAddRequest, request);
        return ResultUtils.success(productId);
    }

    /**
     * 删除产品
     *
     * @param productId 产品 Id
     * @param request   HttpServletRequest
     * @return 删除产品是否成功
     */
    @DeleteMapping("/delete/{productId}")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Boolean> deleteProduct(@PathVariable @Valid
                                               @NotNull(message = "产品  Id 不能为空")
                                               @Min(value = 1L, message = "产品 Id 错误")
                                               Long productId, HttpServletRequest request) {
        Boolean result = productInfoService.deleteProduct(productId, request);
        return ResultUtils.success(result);
    }

    /**
     * 更新产品
     *
     * @param productInfoUpdateRequest 产品更新请求
     * @param request                  HttpServletRequest
     * @return 更新产品是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = 0)
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Boolean> updateProduct(@Valid @RequestBody ProductInfoUpdateRequest productInfoUpdateRequest,
                                               HttpServletRequest request) {
        Boolean result = productInfoService.updateProduct(productInfoUpdateRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 根据产品 Id 获取产品信息
     *
     * @param productId 产品  Id
     * @return 产品信息
     */
    @GetMapping("/get/{productId}")
    public BaseResponse<ProductInfo> getProductById(@PathVariable @Valid
                                                    @NotNull(message = "产品  Id 不能为空")
                                                    @Min(value = 1L, message = "产品 Id 错误")
                                                    Long productId) {
        ProductInfo productInfo = productInfoService.getById(productId);
        if (Objects.isNull(productInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(productInfo);
    }

    /**
     * 分页获取产品列表
     *
     * @param productInfoQueryRequest 产品查询请求
     * @param request                 HttpServletRequest
     * @return 产品分页列表
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<ProductInfo>> getProductListByPage(ProductInfoQueryRequest productInfoQueryRequest,
                                                                HttpServletRequest request) {
        if (productInfoQueryRequest == null) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        ProductInfo productInfoQuery = new ProductInfo();
        BeanUtils.copyProperties(productInfoQueryRequest, productInfoQuery);
        String name = productInfoQueryRequest.getName();
        String description = productInfoQueryRequest.getDescription();
        Integer productType = productInfoQueryRequest.getProductType();
        Integer addPoints = productInfoQueryRequest.getAddPoints();
        Long total = productInfoQueryRequest.getTotal();
        long current = productInfoQueryRequest.getCurrent();
        long pageSize = productInfoQueryRequest.getPageSize();
        // 限制爬虫
        if (pageSize > 50) {
            throw new BusinessException(StatusCode.PARAMS_ERROR);
        }
        QueryWrapper<ProductInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name)
                .like(StringUtils.isNotBlank(description), "description", description)
                .eq(Objects.nonNull(productType), "productType", productType)
                .eq(ObjectUtils.isNotEmpty(addPoints), "addPoints", addPoints)
                .eq(ObjectUtils.isNotEmpty(total), "total", total);
        // 根据金额升序排列
        queryWrapper.orderByAsc("total");
        Page<ProductInfo> productInfoPage = productInfoService.page(new Page<>(current, pageSize), queryWrapper);
        // 不是管理员只能查看已经上线的
        if (!userService.isAdmin(request)) {
            List<ProductInfo> productInfoList = productInfoPage
                    .getRecords()
                    .stream()
                    .filter(productInfo -> productInfo.getStatus().equals(ProductStatusEnum.ONLINE.getCode()))
                    .collect(Collectors.toList());
            productInfoPage.setRecords(productInfoList);
        }
        return ResultUtils.success(productInfoPage);
    }

    /**
     * 发布（上架）产品
     *
     * @param productId 产品 Id
     * @return 发布产品是否成功
     */
    @AuthCheck(mustRole = 0)
    @PostMapping("/online/{productId}")
    public BaseResponse<Boolean> onlineProductInfo(@PathVariable @Valid
                                                   @NotNull(message = "产品  Id 不能为空")
                                                   @Min(value = 1L, message = "产品 Id 错误")
                                                   Long productId) {
        ProductInfo productInfo = productInfoService.getById(productId);
        if (Objects.isNull(productInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductStatusEnum.ONLINE.getCode());
        return ResultUtils.success(productInfoService.updateById(productInfo));
    }

    /**
     * 下架产品
     *
     * @param productId 产品 Id
     * @return 下架产品是否成功
     */
    @PostMapping("/offline/{productId}")
    @AuthCheck(mustRole = 0)
    public BaseResponse<Boolean> offlineProductInfo(@PathVariable @Valid
                                                    @NotNull(message = "产品  Id 不能为空")
                                                    @Min(value = 1L, message = "产品 Id 错误")
                                                    Long productId) {
        ProductInfo productInfo = productInfoService.getById(productId);
        if (Objects.isNull(productInfo)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        productInfo.setStatus(ProductStatusEnum.OFFLINE.getCode());
        return ResultUtils.success(productInfoService.updateById(productInfo));
    }
}
