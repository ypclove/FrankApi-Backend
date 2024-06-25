package com.frank.apicommon.constant;

/**
 * 用户常量类
 *
 * @author Frank
 * @data 2024/06/22
 */
public class UserConstant {

    /**
     * 用户登录态键
     */
    public static final String USER_LOGIN_STATE = "userLoginState";


    // region 权限

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "frank";

    /**
     * accessKey / secretKey 混淆
     */
    public static final String VOUCHER = "accessKey_secretKey";

    /**
     * 用户昵称最大长度
     */
    public static final Integer USER_NAME_MAX_LENGTH = 40;

    /**
     * 用户账号最小长度
     */
    public static final Integer USER_ACCOUNT_MIN_LENGTH = 4;

    /**
     * 用户账号最大长度
     */
    public static final Integer USER_ACCOUNT_MAX_LENGTH = 16;

    /**
     * 账户特殊字符匹配模式
     */
    public static final String ACCOUNT_PATTERN = "^[A-Za-z0-9]+$";

    /**
     * 用户密码最小长度
     */
    public static final Integer USER_PASSWORD_MIN_LENGTH = 8;

    /**
     * 用户密码最大长度
     */
    public static final Integer USER_PASSWORD_MAX_LENGTH = 16;

    /**
     * 验证码的长度
     */
    public static final Integer CAPTCHA_LENGTH = 6;

    /**
     * 通过邀请码注册的用户默认积分
     */
    public static final Integer INIT_BALANCE = 100;

    /**
     * 用户通过邀请码注册成功时，邀请人积分增加的数量
     */
    public static final Integer INVITER_ADD_BALANCE = 100;

    /**
     * 邀请码长度
     */
    public static final Integer INVITATION_CODE_LENGTH = 8;

    /**
     * accessKey 随机 Byte 长度
     */
    public static final Integer ACCESS_KEY_RANDOM_BYTES_LENGTH = 10;

    /**
     * secretKey 随机 Byte 长度
     */
    public static final Integer SECRET_KEY_RANDOM_BYTES_LENGTH = 10;
}
