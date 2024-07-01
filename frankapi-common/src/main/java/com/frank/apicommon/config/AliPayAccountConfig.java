package com.frank.apicommon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 阿里支付账户配置
 *
 * @author Frank
 * @data 2024/06/22
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "alipay")
public class AliPayAccountConfig {

    /**
     * appId
     */
    private String appId;

    /**
     * 卖家 Id
     */
    private String sellerId;

    /**
     * 是否使用沙箱
     */
    private Boolean sandbox;

    /**
     * 网关
     */
    private String gatewayUrl;

    /**
     * 异步通知 url
     */
    private String notifyUrl;

    /**
     * 同步返回 url
     */
    private String returnUrl;

    /**
     * 应用私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String aliPayPublicKey;

    /**
     * 接口内容加密秘钥
     */
    private String contentKey;
}