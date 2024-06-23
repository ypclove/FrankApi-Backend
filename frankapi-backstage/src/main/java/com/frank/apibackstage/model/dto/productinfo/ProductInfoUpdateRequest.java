package com.frank.apibackstage.model.dto.productinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 产品更新请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class ProductInfoUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private long id;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 金额（单位：分）
     */
    private Integer total;

    /**
     * 增加积分个数
     */
    private Integer addPoints;

    /**
     * 产品类型
     * VIP：会员
     * RECHARGE：充值
     */
    private String productType;

    /**
     * 过期时间
     */
    private Date expirationTime;
}