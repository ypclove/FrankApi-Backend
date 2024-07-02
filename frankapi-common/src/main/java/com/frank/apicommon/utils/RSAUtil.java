package com.frank.apicommon.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.frank.apicommon.constant.EncryptConstant.RSA;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * RSA 工具类
 *
 * @author Frank
 * @date 2024/6/25
 */
@Slf4j
public class RSAUtil {

    /**
     * 生成密钥对
     * RSA 默认加密填充模式：RSA/None/PKCS1Padding
     *
     * @param keyLength 密钥长度，建议设置为 2048 的整数倍
     * @return KeyPair
     */
    public static KeyPair generateRSAKeyPair(int keyLength) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(keyLength);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成RSA密钥对失败" + e.getMessage());
        }
    }

    /**
     * 获取公钥
     *
     * @param rsaKeyPair 密钥对
     * @return rsaPublicKey
     */
    public static byte[] getRSAPublicKey(KeyPair rsaKeyPair) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        return rsaPublicKey.getEncoded();
    }

    /**
     * 获取私钥
     *
     * @param rsaKeyPair rsaKeyPair
     * @return privateKey
     */
    public static byte[] getRSAPrivateKey(KeyPair rsaKeyPair) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();
        return rsaPrivateKey.getEncoded();
    }

    /**
     * 获取公钥字符串
     *
     * @param rsaKeyPair rsaKeyPair
     * @return 公钥字符串
     */
    public static String getRSAPublicKeyString(KeyPair rsaKeyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) rsaKeyPair.getPublic();
        return new String(encodeBase64(publicKey.getEncoded()));
    }

    /**
     * 获取私钥字符串
     *
     * @param rsaKeyPair rsaKeyPair
     * @return 私钥字符串
     */
    public static String getRSAPrivateKeyString(KeyPair rsaKeyPair) {
        RSAPrivateKey privateKey = (RSAPrivateKey) rsaKeyPair.getPrivate();
        return new String(encodeBase64((privateKey.getEncoded())));
    }

    /**
     * 公钥字符串转 PublicKey 实例
     *
     * @param rsaPublicKey 公钥字符串
     * @return PublicKey
     * @throws Exception e
     */
    public static PublicKey getPublicKeyInstance(String rsaPublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(rsaPublicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 私钥字符串转 PrivateKey 实例
     *
     * @param rsaPrivateKey 私钥字符串
     * @return PrivateKey
     * @throws Exception e
     */
    public static PrivateKey getPrivateKeyInstance(String rsaPrivateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(rsaPrivateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 公钥字符串转 RSAPublicKey 实例
     *
     * @param rsaPublicKey 公钥字符串
     * @return PublicKey
     */
    public static RSAPublicKey getRSAPublicKeyInstance(String rsaPublicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(rsaPublicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("公钥字符串转 RSAPublicKey 失败" + e.getMessage());
        }
    }

    /**
     * 私钥字符串转 RSAPrivateKey 实例
     *
     * @param rsaPrivateKey 私钥
     * @return PrivateKey
     */
    public static RSAPrivateKey getRSAPrivateKeyInstance(String rsaPrivateKey) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(rsaPrivateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("私钥字符串转 RSAPrivateKey 失败" + e.getMessage());
        }
    }

    /**
     * RSA 公钥加密
     *
     * @param data         明文
     * @param rsaPublicKey RSA 公钥
     * @return 密文
     */
    public static String RSAPublicKeyEncrypt(String data, RSAPublicKey rsaPublicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            // byte[] bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(UTF_8), rsaPublicKey.getModulus().bitLength());
            byte[] rsaEncryptData = cipher.doFinal(data.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(rsaEncryptData);
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失败" + e.getMessage());
        }
    }

    /**
     * RSA 私钥解密
     *
     * @param data          密文
     * @param rsaPrivateKey RSA 私钥
     * @return 明文
     */
    public static String RSAPrivateKeyDecrypt(String data, RSAPrivateKey rsaPrivateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] decoded = Base64.getDecoder().decode(data);
            byte[] rsaDecryptData = cipher.doFinal(decoded);
            // return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), privateKey.getModulus().bitLength()), UTF_8);
            return new String(rsaDecryptData, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败" + e.getMessage());
        }
    }

    /**
     * RSA 公钥加密
     *
     * @param data         明文
     * @param rsaPublicKey RSA 公钥
     * @return 密文
     */
    public static String RSAPublicKeyEncrypt(String data, PublicKey rsaPublicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
            // byte[] bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(UTF_8), rsaPublicKey.getModulus().bitLength());
            byte[] rsaEncryptData = cipher.doFinal(data.getBytes(UTF_8));
            return Base64.getEncoder().encodeToString(rsaEncryptData);
        } catch (Exception e) {
            throw new RuntimeException("RSA 加密失败" + e.getMessage());
        }
    }

    /**
     * RSA 私钥解密
     *
     * @param data          密文
     * @param rsaPrivateKey RSA 私钥
     * @return 明文
     */
    public static String RSAPrivateKeyDecrypt(String data, PrivateKey rsaPrivateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
            byte[] decoded = Base64.getDecoder().decode(data);
            byte[] rsaDecryptData = cipher.doFinal(decoded);
            // return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), privateKey.getModulus().bitLength()), UTF_8);
            return new String(rsaDecryptData, UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("RSA 解密失败" + e.getMessage());
        }
    }

    // ==============================================================================================================

    /**
     * 私钥加密
     *
     * @param content    明文
     * @param privateKey 私钥
     * @return 密文
     */
    public static String encryptByPrivateKey(String content, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, privateKey);
            byte[] bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, content.getBytes(UTF_8), privateKey.getModulus().bitLength());
            return new String(encodeBase64(bytes));
        } catch (Exception e) {
            throw new RuntimeException("privateKey加密字符串[" + content + "]时遇到异常" + e.getMessage());
        }
    }

    /**
     * 公钥解密
     *
     * @param content   密文
     * @param publicKey 私钥
     * @return 明文
     */
    public static String decryByPublicKey(String content, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(content), publicKey.getModulus().bitLength()), UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("publicKey解密字符串[" + content + "]时遇到异常" + e.getMessage());
        }
    }

    /**
     * @param cipher  Cipher
     * @param opmode  模式
     * @param datas   加密数据
     * @param keySize key
     * @return 字节数组
     */
    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
        // 最大块
        int maxBlock;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    // 可以调用以下的 doFinal() 方法完成加密或解密数据：
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常: " + e.getMessage());
        }
        // IOUtils.closeQuietly(out);
        return out.toByteArray();
    }
}
