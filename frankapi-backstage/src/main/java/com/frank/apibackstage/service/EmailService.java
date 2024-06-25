package com.frank.apibackstage.service;

import com.frank.apicommon.config.EmailConfig;

import javax.mail.MessagingException;

/**
 * @author Frank
 * @date 2024/6/24
 */
public interface EmailService {

    /**
     * 发送邮件
     *
     * @param emailAccount 邮件账号
     * @param captcha      验证码
     * @return 是否发送成功
     * @throws MessagingException MessagingException
     */
    boolean sendEmail(String emailAccount, String captcha) throws MessagingException;

    /**
     * 发送支付成功电子邮件
     *
     * @param emailAccount 电子邮件帐户
     * @param emailConfig  电子邮件配置
     * @param orderName    订单名称
     * @param orderTotal   订单总额
     * @return 是否发送成功
     * @throws MessagingException 消息传递异常
     */
    boolean sendPaySuccessEmail(String emailAccount, EmailConfig emailConfig, String orderName, String orderTotal) throws MessagingException;
}
