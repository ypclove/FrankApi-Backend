package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 文件上传类型枚举
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum FileUploadBizEnum {

    /**
     * 用户头像
     */
    USER_AVATAR("用户头像", "user_avatar"),

    /**
     * 接口头像
     */
    INTERFACE_AVATAR("接口头像", "interface_avatar");

    private final String text;

    private final String value;

    FileUploadBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
}
