package com.frank.apicommon.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

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
    USER_AVATAR(1, "用户头像"),

    /**
     * 接口头像
     */
    INTERFACE_AVATAR(2, "接口头像");

    private final int code;

    private final String text;

    FileUploadBizEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值
     * @return FileUploadBizEnum
     */
    public static FileUploadBizEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
            if (anEnum.text.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
