package com.frank.apibackstage.model.dto.productorder;

import com.frank.apicommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 产品订单查询请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductOrderQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 商品名称
     */
    private String orderName;

    /**
     * 微信订单 Id / 支付宝订单 Id
     */
    private String orderNo;

    /**
     * 金额
     */
    private Integer total;

    /**
     * 接口订单状态
     * 1：SUCCESS：支付成功
     * 2：REFUND：转入退款
     * 3：NOTPAY：未支付
     * 4：CLOSED：已关闭
     * 5：REVOKED：已撤销（仅付款码支付会返回）
     * 6：USERPAYING：用户支付中（仅付款码支付会返回）
     * 7：PAYERROR：支付失败（仅付款码支付会返回）
     */
    private Integer status;

    /**
     * 支付方式
     * 1：WX
     * 2：ZFB
     */
    private Integer payType;

    /**
     * 商品信息
     */
    private String productInfo;

    /**
     * 增加积分个数
     */
    private Integer addPoints;
}