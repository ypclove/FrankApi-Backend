package com.frank.apibackstage.model.dto.pay;

import com.frank.apibackstage.annotation.EnumCheck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 订单创建请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class PayCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "产品 Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1L")
    @NotNull(message = "产品 Id 不能为空")
    @Min(value = 1L, message = "产品 Id 错误")
    private Long productId;

    @Schema(description = "支付方式", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    @NotNull(message = "支付方式不能为空")
    @EnumCheck(value = {1, 2}, message = "支付方式只允许为微信或支付宝")
    private Integer payType;
}