package com.frank.apicommon.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static com.frank.apicommon.constant.EncryptConstant.RSA;
import static com.frank.apicommon.constant.EncryptConstant.SIGN_ALGORITHM;

/**
 * 签名、验签工具类
 *
 * @author Frank
 * @date 2024/7/1
 */
@Slf4j
public class SignUtil {

    /**
     * 签名
     * 建议使用 RSA 私钥进行签名
     *
     * @param privateKey 私钥
     * @param content    签名内容
     * @return 签名
     */
    public static String sign(String privateKey, byte[] content) {
        try {
            // privateKey 进行 base64 编码，然后生成 PKCS8 格式私钥
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64Utils.decode(privateKey.getBytes()));
            KeyFactory key = KeyFactory.getInstance(RSA);
            PrivateKey priKey = key.generatePrivate(priPKCS8);
            // 签名摘要算法
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            // 用私钥初始化此对象以进行签名
            signature.initSign(priKey);
            // 使用指定的字节数组更新签名或验证
            signature.update(content);
            // 获得签名字节
            byte[] signed = signature.sign();
            // 进行base64编码返回
            return new String(Base64Utils.encode(signed));
        } catch (Exception e) {
            log.error("签名失败：", e);
        }
        return null;
    }

    /**
     * 验签
     * 建议使用 RSA 公钥验签
     *
     * @param publicKey 公钥
     * @param content   验签内容
     * @param sign      签名
     * @return 验签结果
     */
    public static boolean checkSign(String publicKey, byte[] content, String sign) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            // 进行 base64 解码
            byte[] encodedKey = Base64Utils.decodeFromString(publicKey);
            // 生成公钥
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            // 签名摘要算法
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            // 用公钥初始化签名
            signature.initVerify(pubKey);
            // 使用指定的字节数组更新签名或验证
            signature.update(content);
            // base64 解码后进行验证
            return signature.verify(Base64Utils.decodeFromString(sign));
        } catch (Exception e) {
            log.error("验签失败：", e);
        }
        return false;
    }
}
