package com.frank.apicommon.utils;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import static com.frank.apicommon.constant.EncryptConstant.*;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * RSA 工具类
 *
 * @author Frank
 * @date 2024/6/25
 */
public class RSAUtil {

    /**
     * 生成密钥对
     * RSA 默认加密填充模式：RSA/None/PKCS1Padding
     *
     * @param keyLength 密钥长度，建议设置为 2048 的整数倍
     * @return KeyPair
     */
    public static KeyPair getKeyPair(int keyLength) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
            keyPairGenerator.initialize(keyLength);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("生成密钥对时遇到异常" + e.getMessage());
        }
    }

    /**
     * 获取公钥
     *
     * @param keyPair 密钥对
     * @return publicKey
     */
    public static byte[] getPublicKey(KeyPair keyPair) {
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        return rsaPublicKey.getEncoded();
    }

    /**
     * 获取私钥
     *
     * @param keyPair 密钥对
     * @return privateKey
     */
    public static byte[] getPrivateKey(KeyPair keyPair) {
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        return rsaPrivateKey.getEncoded();
    }

    /**
     * 公钥字符串转 PublicKey 实例
     *
     * @param publicKey 公钥字符串
     * @return PublicKey
     * @throws Exception e
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKey.getBytes());
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * 私钥字符串转 PrivateKey 实例
     *
     * @param privateKey 私钥字符串
     * @return PrivateKey
     * @throws Exception e
     */
    public static PrivateKey getPrivateKey(String privateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKey.getBytes());
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA);
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * 获取公钥字符串
     *
     * @param keyPair KeyPair
     * @return 公钥字符串
     */
    public static String getPublicKeyString(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return new String(encodeBase64(publicKey.getEncoded()));
    }

    /**
     * 获取私钥字符串
     *
     * @param keyPair KeyPair
     * @return 私钥字符串
     */
    public static String getPrivateKeyString(KeyPair keyPair) {
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new String(encodeBase64((privateKey.getEncoded())));
    }

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
            LOG.error("签名失败：", e);
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
            LOG.error("验签失败：", e);
        }
        return false;
    }

    /**
     * 公钥加密
     *
     * @param data      明文
     * @param publicKey 公钥
     * @return 密文
     */
    public static String publicEncrypt(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] bytes = rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(UTF_8), publicKey.getModulus().bitLength());
            return new String(encodeBase64(bytes));
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + data + "]时遇到异常" + e.getMessage());
        }
    }

    /**
     * 私钥解密
     *
     * @param data       密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static String privateDecrypt(String data, RSAPrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.getDecoder().decode(data), privateKey.getModulus().bitLength()), UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("privateKey解密字符串[" + data + "]时遇到异常" + e.getMessage());
        }
    }

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
     * String 转 PublicKey
     *
     * @param publicKey 公钥
     * @return PublicKey
     */
    public static RSAPublicKey getRSAPublicKeyByString(String publicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("String转PublicKey出错" + e.getMessage());
        }
    }

    /**
     * String 转 PrivateKey
     *
     * @param privateKey 私钥
     * @return PrivateKey
     */
    public static RSAPrivateKey getRSAPrivateKeyByString(String privateKey) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance(RSA);
            return (RSAPrivateKey) keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        } catch (Exception e) {
            throw new RuntimeException("String转PrivateKey出错" + e.getMessage());
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
