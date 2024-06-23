package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 用户状态枚举类
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum UserAccountStatusEnum {

    /**
     * 正常
     */
    NORMAL("正常", 0),

    /**
     * 封号
     */
    BAN("封禁", 1);

    private final String text;

    private final int value;

    UserAccountStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }
}
