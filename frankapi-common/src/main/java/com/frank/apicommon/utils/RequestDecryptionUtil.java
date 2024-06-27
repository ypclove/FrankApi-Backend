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

    private static final String PRIVATE_KEY = "";

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
