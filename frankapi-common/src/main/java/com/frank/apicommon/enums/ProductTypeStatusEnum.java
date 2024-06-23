package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 产品类型状态枚举
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum ProductTypeStatusEnum {

    /**
     * VIP会员
     */
    VIP("VIP会员", "VIP"),

    /**
     * 余额充值
     */
    RECHARGE("余额充值", "RECHARGE"),

    /**
     * 充值活动
     */
    RECHARGE_ACTIVITY("充值活动", "RECHARGEACTIVITY");

    private final String text;

    private final String value;

    ProductTypeStatusEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
