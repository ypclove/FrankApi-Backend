package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 接口状态枚举
 * TODO:应该删除
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum ProductInfoStatusEnum {

    /**
     * 开启
     */
    ONLINE("开启", 1),

    /**
     * 关闭
     */
    OFFLINE("关闭", 2),

    /**
     * 审核中
     */
    AUDITING("审核中", 0);

    private final String text;

    private final int value;

    ProductInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
