package com.frank.apicommon.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 * 由三部分组成：状态码，响应数据，提示消息
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = -1693660536490703953L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 全参构造器
     *
     * @param code 状态码
     * @param data 响应数据
     * @param msg  提示消息
     */
    public BaseResponse(int code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }
}
