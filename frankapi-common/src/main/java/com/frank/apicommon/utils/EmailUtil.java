package com.frank.apicommon.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import static com.frank.apicommon.constant.EmailConstant.*;

/**
 * 电子邮件工具类
 *
 * @author Frank
 * @data 2024/06/22
 */
@Slf4j
public class EmailUtil {

    /**
     * 生成普通电子邮件内容
     *
     * @param captcha       验证码
     * @param emailHtmlPath 电子邮件 html 路径
     * @return 邮件内容
     */
    public static String buildEmailContent(String emailHtmlPath, String captcha) {
        // 加载邮件 html 模板
        ClassPathResource resource = new ClassPathResource(emailHtmlPath);
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuilder buffer = new StringBuilder();
        String line;
        try {
            inputStream = resource.getInputStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.info("发送邮件读取模板失败：{}", e.getMessage());
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    log.error("exception message：", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("exception message：", e);
                }
            }
        }
        // 替换 html 模板中的参数
        return MessageFormat.format(buffer.toString(), captcha, EMAIL_TITLE, EMAIL_TITLE_ENGLISH, PLATFORM_RESPONSIBLE_PERSON, PLATFORM_ADDRESS);
    }


    /**
     * 支付成功时，生成支付成功的电子邮件内容
     *
     * @param emailHtmlPath 电子邮件 html 路径
     * @param orderName     订单名称
     * @param orderTotal    订单总额
     * @return 邮件内容
     */
    public static String buildPaySuccessEmailContent(String emailHtmlPath, String orderName, String orderTotal) {
        // 加载邮件 html 模板
        ClassPathResource resource = new ClassPathResource(emailHtmlPath);
        InputStream inputStream = null;
        BufferedReader fileReader = null;
        StringBuilder buffer = new StringBuilder();
        String line;
        try {
            inputStream = resource.getInputStream();
            fileReader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = fileReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            log.error("发送邮件读取模板失败：{}", e.getMessage());
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    log.error("exception message：", e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("exception message：", e);
                }
            }
        }
        // 替换 html 模板中的参数
        return MessageFormat.format(buffer.toString(), orderName, orderTotal, PLATFORM_RESPONSIBLE_PERSON, PATH_ADDRESS, EMAIL_TITLE);
    }
}
