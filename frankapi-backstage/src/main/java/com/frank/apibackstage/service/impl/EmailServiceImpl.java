package com.frank.apibackstage.service.impl;

import com.frank.apibackstage.service.EmailService;
import com.frank.apicommon.config.EmailConfig;
import com.frank.apicommon.utils.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static com.frank.apicommon.constant.EmailConstant.*;

/**
 * @author Frank
 * @date 2024/6/24
 */
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private EmailConfig emailConfig;

    @Resource
    private JavaMailSender mailSender;

    /**
     * 发送邮件
     *
     * @param emailAccount 邮件账号
     * @param captcha      验证码
     */
    @Override
    public boolean sendEmail(String emailAccount, String captcha) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            // 邮箱发送内容组成
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setSubject(EMAIL_SUBJECT);
            helper.setText(EmailUtil.buildEmailContent(EMAIL_HTML_CONTENT_PATH, captcha), true);
            helper.setTo(emailAccount);
            helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            return false;
        }
    }

    /**
     * 发送支付成功电子邮件
     *
     * @param emailAccount 电子邮件帐户
     * @param emailConfig  电子邮件配置
     * @param orderName    订单名称
     * @param orderTotal   订单总额
     * @throws MessagingException 消息传递异常
     */
    @Override
    public boolean sendPaySuccessEmail(String emailAccount, EmailConfig emailConfig, String orderName, String orderTotal) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        // 邮箱发送内容组成
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject("【" + EMAIL_TITLE + "】感谢您的购买，请查收您的订单");
        helper.setText(EmailUtil.buildPaySuccessEmailContent(EMAIL_HTML_PAY_SUCCESS_PATH, orderName, orderTotal), true);
        helper.setTo(emailAccount);
        helper.setFrom(EMAIL_TITLE + '<' + emailConfig.getEmailFrom() + '>');
        mailSender.send(message);
        return false;
    }
}
