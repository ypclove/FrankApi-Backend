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
    SUCCESS(1, "SUCCESS"),

    /**
     * 转入退款
     */
    REFUND(2, "REFUND"),

    /**
     * 未支付
     */
    NOTPAY(3, "NOTPAY"),

    /**
     * 已关闭
     */
    CLOSED(4, "CLOSED"),

    /**
     * 撤销
     */
    REVOKED(5, "REVOKED"),

    /**
     * 用户支付中
     */
    USERPAYING(6, "USERPAYING"),

    /**
     * 支付失败
     */
    PAYERROR(7, "PAYERROR"),

    /**
     * 退款中
     */
    PROCESSING(8, "PROCESSING"),

    /**
     * 未知
     */
    UNKNOW(9, "UNKNOW");

    private final int code;

    private final String text;

    PaymentStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
