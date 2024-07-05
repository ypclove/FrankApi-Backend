package com.frank.apicommon.constant;

/**
 * 邮件常量类
 *
 * @author Frank
 * @date 2024/06/22
 */
public class EmailConstant {

    /**
     * 合法邮箱校验模式
     */
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * 电子邮件 html 内容路径：resources 目录下
     */
    public static final String EMAIL_HTML_CONTENT_PATH = "email.html";

    /**
     * 电子邮件 html 支付成功路径
     */
    public static final String EMAIL_HTML_PAY_SUCCESS_PATH = "pay.html";

    /**
     * 电子邮件主题
     */
    public static final String EMAIL_SUBJECT = "验证码邮件";

    /**
     * 电子邮件标题
     */
    public static final String EMAIL_TITLE = "FrankApi接口开放平台";

    /**
     * 电子邮件标题英语
     */
    public static final String EMAIL_TITLE_ENGLISH = "FrankApi Open Interface Platform";

    /**
     * 平台负责人
     */
    public static final String PLATFORM_RESPONSIBLE_PERSON = "Frank";

    /**
     * TODO: 平台地址
     */
    public static final String PLATFORM_ADDRESS = "<a href='https://baidu.com/'>请联系我们</a>";

    /**
     * TODO: 路径地址
     */
    public static final String PATH_ADDRESS = "'https://baidu.com/'";
}
