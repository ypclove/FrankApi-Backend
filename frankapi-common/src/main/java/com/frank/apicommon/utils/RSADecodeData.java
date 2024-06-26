package com.frank.apicommon.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * RSA 解密数据
 *
 * @author Frank
 * @date 2024/6/26
 */
@Data
public class RSADecodeData implements Serializable {

    private static final long serialVersionUID = -4296258656223039373L;

    /**
     * AES 密钥
     */
    private String key;

    /**
     * AES 偏移量（AES 初始化向量）
     * 初始向量 IV 的长度规定为 128 位，16 个字节，初始向量的来源为随机生成
     */
    private String keyVI;

    /**
     * 请求时间，判断用户是否重复提交
     */
    private Long time;
}
