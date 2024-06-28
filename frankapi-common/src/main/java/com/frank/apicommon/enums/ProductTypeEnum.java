package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 产品类型状态枚举
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum ProductTypeEnum {

    /**
     * VIP会员
     */
    VIP(1, "VIP"),

    /**
     * 积分充值
     */
    RECHARGE(2, "RECHARGE"),

    /**
     * 充值活动
     */
    RECHARGE_ACTIVITY(3, "RECHARGEACTIVITY");

    private final int code;

    private final String text;

    ProductTypeEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
