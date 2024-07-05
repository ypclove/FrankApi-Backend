package com.frank.apibackstage.model.dto.interfaceinfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

/**
 * 接口调用请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class InvokeRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "接口 Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1L")
    @NotNull(message = "接口 Id 不能为空")
    @Min(value = 1L, message = "用户 Id 错误")
    private Long id;

    @Schema(description = "接口请求参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "id")
    private Map<String, String> requestParams;
}

