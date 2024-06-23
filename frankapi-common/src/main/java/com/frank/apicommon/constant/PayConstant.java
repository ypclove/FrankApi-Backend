package com.frank.apicommon.constant;

/**
 * 支付常量类
 *
 * @author Frank
 * @data 2024/06/22
 */
public class PayConstant {

    /**
     * 查询订单状态 Key
     */
    public static final String QUERY_ORDER_STATUS = "query:orderStatus:";

    /**
     * 订单前缀
     */
    public static final String ORDER_PREFIX = "order_";

    /**
     * 查询订单信息
     */
    public static final String QUERY_ORDER_INFO = "query:orderInfo:";

    /**
     * 支付宝响应代码表示成功
     */
    public static final String RESPONSE_CODE_SUCCESS = "10000";

    /**
     * 商户签约的产品支持退款功能的前提下，买家付款成功
     */
    public static final String TRADE_SUCCESS = "TRADE_SUCCESS";
}
