package com.frank.apibackstage.model.dto.interfaceinfo;

import com.frank.apibackstage.annotation.EnumCheck;
import com.frank.apicommon.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 接口查询请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceInfoQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "发布人 Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1L")
    @NotNull(message = "发布人 Id 不能为空")
    @Min(value = 1L, message = "发布人 Id 错误")
    private Long userId;

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
    @NotEmpty(message = "请求方法不能为空")
    @Length(max = 200, message = "请求方法长度不能超过 200 字符")
    private Integer method;

    @Schema(description = "接口响应参数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "https://example.com/api/12345678")
    private List<ResponseParamsField> responseParams;

    @Schema(description = "接口返回格式", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "json")
    @Length(max = 250, message = "接口返回格式长度不能超过 250 字符")
    private String returnFormat;

    @Schema(description = "接口状态", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    @EnumCheck(value = {0, 1, 2}, message = "接口状态只允许为关闭、开启或审核中")
    private Integer status;


    @Schema(description = "扣除积分个数", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    @NotEmpty(message = "扣除积分个数不能为空")
    @Min(value = 1, message = "扣除积分个数不能小于 1")
    private Integer reduceScore;
}