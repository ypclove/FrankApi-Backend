package com.frank.apibackstage.model.dto.interfaceinfo;

import com.frank.apibackstage.annotation.EnumCheck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 接口创建请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class InterfaceInfoAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "接口名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "每日壁纸")
    @NotEmpty(message = "接口名称不能为空")
    @Length(max = 50, message = "接口名称长度不能超过 50 字符")
    private String name;

    @Schema(description = "接口地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com/api")
    @NotEmpty(message = "接口地址不能为空")
    @URL(message = "接口地址格式有误")
    @Length(max = 200, message = "接口地址长度长度不能超过 200 字符")
    private String url;

    @Schema(description = "接口描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "每天获取一张壁纸")
    @Length(max = 100, message = "接口描述长度不能超过 100 字符")
    private String description;

    @Schema(description = "请求方法", requiredMode = Schema.RequiredMode.REQUIRED, example = "getWallpaper")
    @NotNull(message = "请求方法不能为空")
    @EnumCheck(value = {1, 2, 3, 4}, message = "请求方法只允许为 GET，POST，PUT，DELETE")
    private Integer method;

    @Schema(description = "接口请求参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "id")
    private List<RequestParamsField> requestParams;

    @Schema(description = "接口响应参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "https://example.com/api/12345678")
    private List<ResponseParamsField> responseParams;

    @Schema(description = "请求头", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private String requestHeader;

    @Schema(description = "响应头", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private String responseHeader;

    @Schema(description = "接口返回格式", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "json")
    @Length(max = 250, message = "接口返回格式长度不能超过 250 字符")
    private String returnFormat;

    @Schema(description = "请求示例", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "")
    private String requestExample;

    @Schema(description = "扣除积分个数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    @NotNull(message = "扣除积分个数不能为空")
    @Min(value = 1, message = "扣除积分个数不能小于 1")
    private Integer reduceScore;
}