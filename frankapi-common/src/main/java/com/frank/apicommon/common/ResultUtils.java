package com.frank.apicommon.common;

/**
 * 通用返回工具类
 *
 * @author Frank
 * @data 2024/06/22
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 成功数据
     * @param <T>  数据类型
     * @return ok
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 失败：只有错误码
     *
     * @param errorCode 错误码
     * @return BaseResponse
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 错误
     *
     * @param data    数据
     * @param message 错误消息
     * @param <T>     数据类型
     * @return error
     */
    public static <T> BaseResponse<T> error(T data, String message) {
        return new BaseResponse<>(202, data, message);
    }

    /**
     * 错误
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return error
     */
    public static <T> BaseResponse<T> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 错误
     *
     * @param data      数据
     * @param errorCode 错误码
     * @param <T>       数据类型
     * @return error
     */
    public static <T> BaseResponse<T> error(T data, ErrorCode errorCode) {
        return new BaseResponse<>(errorCode.getCode(), data, errorCode.getMessage());
    }

    /**
     * 错误
     *
     * @param data      数据
     * @param errorCode 错误码
     * @param message   错误消息
     * @param <T>       数据类型
     * @return error
     */
    public static <T> BaseResponse<T> error(T data, ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), data, message);
    }

    /**
     * 错误
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @param <T>       数据类型
     * @return error
     */
    public static <T> BaseResponse<T> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
