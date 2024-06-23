package com.frank.apicommon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 电子邮件配置
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class EmailConfig {

    private String emailFrom;
}
