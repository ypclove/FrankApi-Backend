package com.frank.apibackstage.model.alipay;

import lombok.Data;

import java.io.Serializable;

/**
 * alipay 支付异步响应
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class AliPayAsyncResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 通知时间
     */
    private String notifyTime;

    /**
     * 通知的类型
     */
    private String notifyType;

    /**
     * 通知校验 Id
     */
    private String notifyId;

    /**
     * 卖家 Id
     */
    private String sellerId;

    /**
     * 买方 Id
     */
    private String buyerId;

    /**
     * 编码格式
     */
    private String charset;

    /**
     * 接口版本
     */
    private String version;

    /**
     * 授权方的 app_id
     */
    private String authAppId;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * APP_ID
     */
    private String appId;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 订单金额
     */
    private String totalAmount;

    /**
     * 实收金额
     */
    private String receiptAmount;

    /**
     * 付款金额
     */
    private String buyerPayAmount;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 商品描述
     */
    private String body;

    /**
     * 交易创建时间
     */
    private String gmtCreate;
}
