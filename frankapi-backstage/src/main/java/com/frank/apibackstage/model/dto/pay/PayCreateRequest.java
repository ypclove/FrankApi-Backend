package com.frank.apibackstage.model.dto.pay;

import lombok.Data;

import java.io.Serializable;

/**
 * 付款创建请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class PayCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接口 id
     */
    private String productId;

    /**
     * 支付类型
     */
    private String payType;
}