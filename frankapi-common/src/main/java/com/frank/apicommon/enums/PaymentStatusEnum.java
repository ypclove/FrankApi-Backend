package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 支付状态枚举
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum PaymentStatusEnum {

    /**
     * 支付成功
     */
    SUCCESS("支付成功", "SUCCESS"),

    /**
     * 转入退款
     */
    REFUND("转入退款", "REFUND"),

    /**
     * 未支付
     */
    NOTPAY("未支付", "NOTPAY"),

    /**
     * 已关闭
     */
    CLOSED("已关闭", "CLOSED"),

    /**
     * 撤销
     */
    REVOKED("已撤销（刷卡支付）", "REVOKED"),

    /**
     * 支付失败
     */
    PAY_ERROR("支付失败", "PAYERROR"),

    /**
     * 用户付费中
     */
    USER_PAYING("用户支付中", "USER_PAYING"),

    /**
     * 退款中
     */
    PROCESSING("退款中", "PROCESSING"),

    /**
     * 未知
     */
    UNKNOW("未知状态", "UNKNOW");

    private final String text;

    private final String value;

    PaymentStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
