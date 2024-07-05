package com.frank.apibackstage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoAddRequest;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoUpdateRequest;
import com.frank.apibackstage.model.entity.ProductInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Frank
 * @date 2024/06/22
 */
public interface ProductInfoService extends IService<ProductInfo> {

    /**
     * 创建产品
     *
     * @param productInfoAddRequest 产品创建请求
     * @param request               HttpServletRequest
     * @return 产品 Id
     */
    Long addProduct(ProductInfoAddRequest productInfoAddRequest, HttpServletRequest request);

    /**
     * 删除产品
     *
     * @param productId 产品 Id
     * @param request   HttpServletRequest
     * @return 删除产品是否成功
     */
    Boolean deleteProduct(Long productId, HttpServletRequest request);

    /**
     * 更新产品
     *
     * @param productInfoUpdateRequest 产品更新请求
     * @param request                  HttpServletRequest
     * @return 更新产品是否成功
     */
    Boolean updateProduct(ProductInfoUpdateRequest productInfoUpdateRequest, HttpServletRequest request);
}
