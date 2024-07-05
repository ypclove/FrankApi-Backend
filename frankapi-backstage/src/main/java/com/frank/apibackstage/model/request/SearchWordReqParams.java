package com.frank.apibackstage.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @author Frank
 * @date 2024/7/4
 */
@Data
public class SearchWordReqParams implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "查询的单词", requiredMode = Schema.RequiredMode.REQUIRED, example = "love")
    @NotEmpty(message = "查询单词不能为空")
    private String q;

    @Schema(description = "单词个数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "5")
    @Min(value = 1, message = "单词个数最少为 1 个")
    @Max(value = 5, message = "单词个数不能超过 5 个")
    private String n;

    @Schema(description = "返回格式", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "json")
    private String doctype;
}
