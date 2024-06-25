package com.frank.apicommon.constant;

/**
 * Redis 常量类
 *
 * @author Frank
 * @date 2024/6/23
 */
public class RedisConstant {

    /**
     * 用户注册 key
     */
    public static final String REGISTER_KEY = "userRegister_";

    /**
     * 邮箱验证码缓存 Key
     */
    public static final String CAPTCHA_CACHE_KEY = "user:email:captcha:";

    /**
     * 验证码的缓存时长
     */
    public static final Long CAPTCHA_CACHE_TTL = 5L;
}
