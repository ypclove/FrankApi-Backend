package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * 错误码
 * TODO:应该删除
 *
 * @author Frank
 * @date 2024/6/22
 */
@Getter
public enum ImageStatusEnum {

    /**
     * 成功
     */
    SUCCESS("success", "done"),

    /**
     * 参数错误
     */
    ERROR("error", "error");

    private final String status;

    private final String value;

    ImageStatusEnum(String status, String value) {
        this.status = status;
        this.value = value;
    }
}
