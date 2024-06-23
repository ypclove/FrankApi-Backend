package com.frank.apibackstage.model.dto.productinfo;

import com.frank.apicommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 产品查询请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductInfoQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 产品名称
     */
    private String name;

    /**
     * 增加积分个数
     */
    private Integer addPoints;

    /**
     * 产品描述
     */
    private String description;

    /**
     * 金额（单位：分）
     */
    private Integer total;

    /**
     * 产品类型
     * VIP：会员
     * RECHARGE：充值
     */
    private String productType;

}