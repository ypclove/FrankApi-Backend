package com.frank.apibackstage.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.frank.apibackstage.mapper.ProductInfoMapper;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoAddRequest;
import com.frank.apibackstage.model.dto.productinfo.ProductInfoUpdateRequest;
import com.frank.apibackstage.model.entity.ProductInfo;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.ProductInfoService;
import com.frank.apibackstage.service.UserService;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author Frank
 * @date 2024/06/22
 */
@Service
public class ProductInfoServiceImpl extends ServiceImpl<ProductInfoMapper, ProductInfo> implements ProductInfoService {

    @Resource
    private UserService userService;

    @Resource
    private ProductInfoMapper productInfoMapper;

    /**
     * 创建产品
     *
     * @param productInfoAddRequest 产品创建请求
     * @param request               HttpServletRequest
     * @return 产品 Id
     */
    @Override
    public Long addProduct(ProductInfoAddRequest productInfoAddRequest, HttpServletRequest request) {
        ProductInfo productInfo = new ProductInfo();
        BeanUtils.copyProperties(productInfoAddRequest, productInfo);
        UserVO loginUser = userService.getLoginUser(request);
        productInfo.setUserId(loginUser.getId());
        int result = productInfoMapper.insert(productInfo);
        if (result <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "创建产品失败");
        }
        return productInfo.getId();
    }

    /**
     * 删除产品
     *
     * @param productId 产品 Id
     * @param request   HttpServletRequest
     * @return 删除产品是否成功
     */
    @Override
    public Boolean deleteProduct(Long productId, HttpServletRequest request) {
        // 判断是否存在
        ProductInfo productInfo = productInfoMapper.selectById(productId);
        if (Objects.isNull(productId)) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR);
        }
        UserVO user = userService.getLoginUser(request);
        // 仅本人或管理员可删除
        if (!productInfo.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR, "操作无权限");
        }
        int result = productInfoMapper.deleteById(productId);
        if (result <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "删除产品失败");
        }
        return true;
    }

    /**
     * 更新产品
     *
     * @param productInfoUpdateRequest 产品更新请求
     * @param request                  HttpServletRequest
     * @return 更新产品是否成功
     */
    @Override
    public Boolean updateProduct(ProductInfoUpdateRequest productInfoUpdateRequest, HttpServletRequest request) {
        // 判断是否存在
        ProductInfo productInfo = productInfoMapper.selectById(productInfoUpdateRequest.getId());
        if (productInfo == null) {
            throw new BusinessException(StatusCode.NOT_FOUND_ERROR, "产品不存在");
        }
        BeanUtils.copyProperties(productInfoUpdateRequest, productInfo);
        UserVO user = userService.getLoginUser(request);
        // 仅本人或管理员可修改
        if (!userService.isAdmin(request) && !productInfo.getUserId().equals(user.getId())) {
            throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        }
        int result = productInfoMapper.updateById(productInfo);
        if (result <= 0) {
            throw new BusinessException(StatusCode.OPERATION_ERROR, "更新产品失败");
        }
        return true;
    }
}
