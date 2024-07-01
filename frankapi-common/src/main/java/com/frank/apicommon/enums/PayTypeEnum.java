package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 支付方式枚举类
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum PayTypeEnum {

    /**
     * 微信支付
     */
    WX(1, "WX"),

    /**
     * 支付宝支付
     */
    ALIPAY(2, "ALIPAY");

    private final int code;

    private final String text;

    PayTypeEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
