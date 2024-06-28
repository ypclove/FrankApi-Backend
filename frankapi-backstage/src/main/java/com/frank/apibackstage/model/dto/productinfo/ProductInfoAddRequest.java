package com.frank.apibackstage.model.dto.productinfo;

import com.frank.apibackstage.annotation.EnumCheck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 产品创建请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class ProductInfoAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "手机")
    @NotEmpty(message = "产品名称不能为空")
    @Length(max = 50, message = "产品名称长度不能超过 50 字符")
    private String name;

    @Schema(description = "产品描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "这是一部手机")
    @Length(max = 200, message = "产品描述长度不能超过 200 字符")
    private String description;

    @Schema(description = "产品金额", requiredMode = Schema.RequiredMode.REQUIRED, example = "30000")
    @NotNull(message = "产品金额不能为空")
    @Min(value = 0, message = "产品金额不能为负数")
    private Long total;

    @Schema(description = "增加积分数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    @NotNull(message = "增加积分数不能为空")
    @Min(value = 0, message = "增加积分不能为负数")
    private Integer addPoints;

    @Schema(description = "产品类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "VIP")
    @NotNull(message = "产品类型不能为空")
    @EnumCheck(value = {1, 2, 3}, message = "产品类型只允许为 VIP，RECHARGE，RECHARGEACTIVITY")
    private Integer productType;

    @Schema(description = "过期时间", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "2100-12-31 12:00:00")
    private Date expirationTime;
}