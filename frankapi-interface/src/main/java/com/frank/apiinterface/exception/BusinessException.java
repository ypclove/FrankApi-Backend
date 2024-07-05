package com.frank.apiinterface.exception;

import com.frank.apicommon.common.StatusCode;
import lombok.Getter;

/**
 * 自定义异常类
 *
 * @author Frank
 * @date 2024/06/22
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 状态码
     */
    private final int code;

    public BusinessException(StatusCode code) {
        super(code.getMsg());
        this.code = code.getCode();
    }

    public BusinessException(StatusCode code, String message) {
        super(message);
        this.code = code.getCode();
    }
}
