package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 接口状态枚举类
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum InterfaceStatusEnum {

    /**
     * 关闭
     */
    OFFLINE(0, "关闭"),

    /**
     * 开启
     */
    ONLINE(1, "开启"),

    /**
     * 审核中
     */
    AUDITING(2, "审核中");

    private final int code;

    private final String text;

    InterfaceStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
