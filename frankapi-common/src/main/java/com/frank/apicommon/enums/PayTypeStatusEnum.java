package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 支付方式枚举类
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum PayTypeStatusEnum {

    /**
     * 微信支付
     */
    WX("微信支付", "WX"),

    /**
     * 支付宝支付
     */
    ALIPAY("支付宝支付", "ALIPAY");

    private final String text;

    private final String value;

    PayTypeStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
