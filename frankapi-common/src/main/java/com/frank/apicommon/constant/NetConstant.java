package com.frank.apicommon.constant;

/**
 * @author Frank
 * @date 2024/7/2
 */
public class NetConstant {

    /**
     * 未知 IP 地址
     */
    public static final String IP_UNKNOWN = "unknown";

    /**
     * 本地 IP 地址
     */
    public static final String IP_LOCAL = "127.0.0.1";

    /**
     * IP 地址最大长度
     */
    public static final int IP_MAX_LEN = 15;

    /**
     * HTTP 连接超时时间（单位：毫秒）
     */
    public static final int HTTP_CONNECT_TIMEOUT = 5000;

    /**
     * GET 请求超时时间（单位：毫秒）
     */
    public static final int GET_REQUEST_TIMEOUT = 5000;
}
