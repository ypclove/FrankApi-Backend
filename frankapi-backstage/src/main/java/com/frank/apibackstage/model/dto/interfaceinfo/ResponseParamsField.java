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
    private String id;
    private String fieldName;
    private String type;
    private String desc;
}