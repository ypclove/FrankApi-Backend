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

    /**
     * 获取订单 Key
     */
    public static final String GET_ORDER = "getOrder_";

    /**
     * 创建订单 Key
     */
    public static final String CREATE_ORDER = "createOrder_";

    /**
     * Alipay 异步通知 Key
     */
    public static final String NOTIFY_ALIPAY_ORDER = "notify:AlipayOrder:lock:";

    /**
     * WX 异步通知 Key
     */
    public static final String NOTIFY_WX_ORDER = "notify:WxOrder:lock";

    /**
     * Alipay 产品码
     */
    public static final String ALIPAY_PRODUCT_CODE = "FAST_INSTANT_TRADE_PAY";

    /**
     * 订单有效期
     */
    public static final Integer ORDER_TTL = 5;

    /**
     * 订单 Id 的随机字符长度
     */
    public static final Integer ORDER_ID_LENGTH = 20;

    /**
     * 订单过期时间格式化
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";

    /**
     * 将 "分" 转换为 "￥" 的进制
     */
    public static final String AMOUNT_TRANSFER = "100";

    /**
     * 以 "￥" 表示的金额保留的小数
     */
    public static final Integer DECIMAL_POINT = 2;

    /**
     * 交易币种
     */
    public static final String ORDER_CURRENCY = "CNY";

    /**
     * 交易类型
     */
    public static final String ORDER_TYPE = "电脑网站支付";

    /**
     * 交易成功的描述
     */
    public static final String ORDER_SUCCESS_DESC = "支付成功";
}
