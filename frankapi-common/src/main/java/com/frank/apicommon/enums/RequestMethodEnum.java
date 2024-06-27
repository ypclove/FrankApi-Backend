package com.frank.apicommon.enums;

import lombok.Getter;

/**
 * @author Frank
 * @date 2024/6/27
 */
@Getter
public enum RequestMethodEnum {
    GET(1, "GET"),
    POST(2, "POST"),
    PUT(3, "PUT"),
    DELETE(4, "DELETE");

    private final int code;
    private final String text;

    RequestMethodEnum(int code, String text) {
        this.code = code;
        this.text = text;
    }
}
