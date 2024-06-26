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

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "frank";

    /**
     * accessKey / secretKey 混淆
     */
    public static final String VOUCHER = "accessKey_secretKey";

    /**
     * 验证码长度
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
