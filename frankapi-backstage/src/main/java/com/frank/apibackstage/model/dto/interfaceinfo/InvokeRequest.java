package com.frank.apibackstage.model.dto.interfaceinfo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接口调用请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class InvokeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private List<Field> requestParams;

    private String userRequestParams;

    @Data
    public static class Field {
        private String fieldName;
        private String value;
    }
}

