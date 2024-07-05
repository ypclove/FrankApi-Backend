package com.frank.apiinterface.model.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Frank
 * @date 2024/7/4
 */
@Data
public class DailyEnglishRespParams implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 英文句子
     */
    private String en;

    /**
     * 图片
     */
    private String pic;

    /**
     * 中文句子
     */
    private String zh;
}