package com.frank.apicommon.utils;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

import static com.frank.apicommon.constant.EncryptConstant.AES;
import static com.frank.apicommon.constant.EncryptConstant.CIPHER_ALGORITHM;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AES 工具类
 *
 * @author Frank
 * @date 2024/6/25
 */
@Slf4j
public class AESUtil {

    static {
        // 通过在运行环境中设置以下属性启用 AES-256 支持
        Security.setProperty("crypto.policy", "unlimited");
        // 解决 java 不支持 AES/CBC/PKCS7Padding 模式解密
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成 AES 密钥
     *
     * @return AES 密钥
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     */
    public static String generateAESKey(int keyLength) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(AES);
        keyGen.init(keyLength);
        SecretKey secretKey = keyGen.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }

    /**
     * AES 加密
     *
     * @param AESKey AES 密钥
     * @param data   明文内容
     * @param keyVI  AES 偏移量
     * @return AES 密文
     */
    public static String AESEncrypt(String AESKey, String data, String keyVI) {
        try {
            // 将 Base64 编码的密钥解码为字节数组
            byte[] decodedKey = Base64.getDecoder().decode(AESKey);
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
            // SecretKey secretKey = new SecretKeySpec(AESKey.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(keyVI.getBytes(UTF_8)));
            // 获取加密内容的字节数组（这里要设置为 utf-8）不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byteEncode = data.getBytes(UTF_8);
            // 根据密码器的初始化方式加密
            byte[] aesEncryptData = cipher.doFinal(byteEncode);
            // 将加密后的数据转换为字符串
            return Base64.getEncoder().encodeToString(aesEncryptData);
        } catch (Exception e) {
            log.error("AES 加密失败：", e);
        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param AESKey AES 密钥
     * @param data   密文内容
     * @param keyVI  AES 偏移量
     * @return 明文内容
     */
    public static String AESDecrypt(String AESKey, String data, String keyVI) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(AESKey);
            // 0 表示偏移量，从 decodedKey 字节数组的第一个字节开始使用
            SecretKey secretKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, AES);
            // SecretKey secretKey = new SecretKeySpec(AESKey.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(keyVI.getBytes()));
            // 将加密并编码后的内容解码成字节数组
            byte[] byteContent = Base64.getDecoder().decode(data);
            // 解密
            byte[] aesDecryptData = cipher.doFinal(byteContent);
            return new String(aesDecryptData, UTF_8);
        } catch (Exception e) {
            log.error("AES 解密失败：", e);
        }
        return null;
    }
}