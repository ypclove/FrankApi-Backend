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
public enum ProductStatusEnum {

    OFFLINE(0, "下架"),
    ONLINE(1, "上架"),
    AUDITING(2, "审核中");

    private final int code;

    private final String text;

    ProductStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
