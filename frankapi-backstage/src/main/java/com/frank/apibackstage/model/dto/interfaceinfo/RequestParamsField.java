package com.frank.apibackstage.model.dto.interfaceinfo;

import lombok.Data;

/**
 * 请求参数字段
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class RequestParamsField {
    private String id;
    private String fieldName;
    private String type;
    private String desc;
    private String required;
}