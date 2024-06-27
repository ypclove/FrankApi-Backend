package com.frank.apibackstage.model.dto.interfaceinfo;

import lombok.Data;

/**
 * 响应参数字段
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class ResponseParamsField {

    /**
     * 接口响应参数 Id
     */
    private String id;

    /**
     * 接口响应字段
     */
    private String fieldName;

    /**
     * 接口响应类型
     */
    private String type;

    /**
     * 接口响应描述
     */
    private String desc;
}