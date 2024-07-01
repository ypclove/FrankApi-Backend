package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 文件上传状态枚举类
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum FileUploadStatusEnum {

    /**
     * 成功
     */
    SUCCESS(1, "success"),

    /**
     * 失败
     */
    ERROR(0, "error");

    private final int code;

    private final String text;

    FileUploadStatusEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
