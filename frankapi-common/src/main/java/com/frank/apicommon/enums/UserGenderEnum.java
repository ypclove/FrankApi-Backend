package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 用户性别枚举类
 *
 * @author Frank
 * @date 2024/6/25
 */
@Getter
public enum UserGenderEnum {
    MALE(1, "男"),
    FEMALE(0, "女");

    private final int code;
    private final String text;

    UserGenderEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
