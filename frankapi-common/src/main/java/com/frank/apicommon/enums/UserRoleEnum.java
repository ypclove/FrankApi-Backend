package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 用户角色枚举类
 *
 * @author Frank
 * @date 2024/6/25
 */
@Getter
public enum UserRoleEnum {

    ADMIN(0, "管理员"),
    USER(1, "普通用户");

    private final int code;
    private final String text;

    UserRoleEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
