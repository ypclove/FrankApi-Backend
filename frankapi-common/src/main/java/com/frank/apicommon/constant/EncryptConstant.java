package com.frank.apicommon.constant;

/**
 * @author Frank
 * @date 2024/6/26
 */
public class EncryptConstant {

    /**
     * RSA 加密算法
     */
    public static final String RSA = "RSA";

    /**
     * AES 加密算法
     */
    public static final String AES = "AES";

    /**
     * AES 加密解密算法（加密模式/填充方式）
     */
    public static final String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    /**
     * 签名摘要算法
     */
    public static final String SIGN_ALGORITHM = "SHA256WithRSA";

    /**
     * 解密请求超时时间
     */
    public final static Integer TIMEOUT = 60000;
}
