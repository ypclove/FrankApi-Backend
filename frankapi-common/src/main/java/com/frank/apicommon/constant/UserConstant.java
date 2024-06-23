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
     * 系统用户 id（虚拟用户）
     */
    public static final long SYSTEM_USER_ID = 0;

    // region 权限

    /**
     * 默认权限
     */
    public static final String DEFAULT_ROLE = "user";

    /**
     * 管理员权限
     */
    public static final String ADMIN_ROLE = "admin";

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "frank";

    /**
     * accessKey / secretKey 混淆
     */
    public static final String VOUCHER = "accessKey_secretKey";
}
