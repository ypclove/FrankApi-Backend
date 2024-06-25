package com.frank.apibackstage.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户通过邮件注册请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class UserEmailRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin@admin.com")
    @Email(message = "邮箱格式不正确")
    @NotEmpty(message = "邮箱不能为空")
    @Size(max = 50, message = "邮箱长度不能超过 50 个字符")
    private String emailAccount;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "a1b2C3")
    @NotEmpty(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度必须为 6 字符")
    private String captcha;

    @Schema(description = "邀请码（可选）", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "7TrsW6xB")
    private String invitationCode;

    // TODO: 需要判断是否同意隐私协议
    private String agreeToAnAgreement;
}
