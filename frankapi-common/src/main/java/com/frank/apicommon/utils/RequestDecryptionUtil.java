package com.frank.apicommon.utils;

import com.alibaba.fastjson.JSONObject;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.util.Objects;

import static com.frank.apicommon.constant.EncryptConstant.TIMEOUT;

/**
 * @author Frank
 * @date 2024/6/25
 */
@Component
public class RequestDecryptionUtil {

    @Value("${encrypt.rsa.rsa-public-key}")
    private static String PUBLIC_KEY;

    private static final String PRIVATE_KEY = "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";

    /**
     * 获取解密后的真实明文参数
     *
     * @param sym   AES 密钥的密文
     * @param asy   接口参数的密文
     * @param clazz 入参的类
     * @param <T>   数据类型
     * @return 接口参数的明文参数
     */
    public static <T> Object getRequestDecryption(String sym, String asy, Class<T> clazz) {
        // 验证密钥
        try {
            // 解密RSA
            RSAPrivateKey rsaPrivateKey = RSAUtil.getRSAPrivateKeyByString(PRIVATE_KEY);
            String RSAJson = RSAUtil.privateDecrypt(sym, rsaPrivateKey);
            RSADecodeData rsaDecodeData = JSONObject.parseObject(RSAJson, RSADecodeData.class);
            boolean isTimeout = Objects.nonNull(rsaDecodeData) && Objects.nonNull(rsaDecodeData.getTime()) && System.currentTimeMillis() - rsaDecodeData.getTime() < TIMEOUT;
            if (!isTimeout) {
                // 请求超时
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "请求超时，请重试");
            }
            // 解密AES
            String AESJson = AESUtil.decode(rsaDecodeData.getKey(), asy, rsaDecodeData.getKeyVI());
            System.out.println("AESJson: " + AESJson);
            return JSONObject.parseObject(AESJson, clazz);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败：" + e.getMessage());
        }
    }

    /**
     * 获取解密后的真实明文参数
     *
     * @param sym AES 密钥的密文
     * @param asy 接口参数的密文
     * @return 接口参数的明文参数
     */
    public static JSONObject getRequestDecryption(String sym, String asy) {
        // 验证密钥
        try {
            // 解密 RSA
            RSAPrivateKey rsaPrivateKey = RSAUtil.getRSAPrivateKeyByString(PRIVATE_KEY);
            String RSAJson = RSAUtil.privateDecrypt(sym, rsaPrivateKey);
            RSADecodeData rsaDecodeData = JSONObject.parseObject(RSAJson, RSADecodeData.class);
            boolean isTimeout = Objects.nonNull(rsaDecodeData) && Objects.nonNull(rsaDecodeData.getTime()) && System.currentTimeMillis() - rsaDecodeData.getTime() < TIMEOUT;
            if (!isTimeout) {
                // 请求超时
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "请求超时，请重试");
            }
            // 解密 AES
            String AESJson = AESUtil.decode(rsaDecodeData.getKey(), asy, rsaDecodeData.getKeyVI());
            System.out.println("AESJson: " + AESJson);
            return JSONObject.parseObject(AESJson);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败：" + e.getMessage());
        }
    }
}
