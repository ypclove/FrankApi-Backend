package com.frank.apibackstage.model.dto.productorder;

import lombok.Data;

import java.io.Serializable;

/**
 * 产品订单创建请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class ProductOrderAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品 Id
     */
    private String productId;

    /**
     * 支付类型
     */
    private String payType;

    /**
     * 订单号
     */
    private String orderNo;
}