package com.frank.apicommon.constant;

import com.frank.apicommon.utils.AESUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;

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
     * Base64 编码器
     */
    public static final Base64.Encoder BASE64_ENCODER = getEncoder();

    /**
     * Base64 解码器
     */
    public static final Base64.Decoder BASE64_DECODER = getDecoder();

    /**
     * 日志记录器
     */
    public static final Logger LOG = LoggerFactory.getLogger(AESUtil.class);

    /**
     * 解密请求超时时间
     */
    public final static Integer TIMEOUT = 60000;
}
