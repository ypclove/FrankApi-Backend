package com.frank.apicommon.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.Base64;

import static com.frank.apicommon.constant.EncryptConstant.*;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * AES 工具类
 *
 * @author Frank
 * @date 2024/6/25
 */
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
     * @param key     AES 密钥
     * @param content 明文内容
     * @param keyVI   AES 偏移量
     * @return AES 密文
     */
    public static String encode(String key, String content, String keyVI) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(keyVI.getBytes()));
            // 获取加密内容的字节数组（这里要设置为 utf-8）不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byteEncode = content.getBytes(UTF_8);
            // 根据密码器的初始化方式加密
            byte[] byteAES = cipher.doFinal(byteEncode);
            // 将加密后的数据转换为字符串
            return BASE64_ENCODER.encodeToString(byteAES);
        } catch (Exception e) {
            LOG.error("AES 加密失败：", e);
        }
        return null;
    }

    /**
     * AES 解密
     *
     * @param key     AES 密钥
     * @param content 密文内容
     * @param keyVI   AES 偏移量
     * @return 明文内容
     */
    public static String decode(String key, String content, String keyVI) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), AES);
            Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(keyVI.getBytes()));
            // 将加密并编码后的内容解码成字节数组
            byte[] byteContent = BASE64_DECODER.decode(content);
            // 解密
            byte[] byteDecode = cipher.doFinal(byteContent);
            return new String(byteDecode, UTF_8);
        } catch (Exception e) {
            LOG.error("AES 解密失败：", e);
        }
        return null;
    }

    /**
     * AES 加密：ECB 模式；PKCS7Padding 填充方式
     *
     * @param str 明文字符串
     * @param key AES 密钥
     * @return AES 密文
     * @throws Exception Exception
     */
    public static String aes256ECBPkcs7PaddingEncrypt(String str, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        byte[] keyBytes = key.getBytes(UTF_8);
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyBytes, AES));
        byte[] doFinal = cipher.doFinal(str.getBytes(UTF_8));
        return new String(Base64.getEncoder().encode(doFinal));
    }

    /**
     * AES解密：ECB 模式；PKCS7Padding 填充方式
     *
     * @param str 密文字符串
     * @param key AES 密钥
     * @return 明文字符串
     * @throws Exception Exception
     */
    public static String aes256ECBPkcs7PaddingDecrypt(String str, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS7Padding");
        byte[] keyBytes = key.getBytes(UTF_8);
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyBytes, AES));
        byte[] doFinal = cipher.doFinal(Base64.getDecoder().decode(str));
        return new String(doFinal);
    }
}