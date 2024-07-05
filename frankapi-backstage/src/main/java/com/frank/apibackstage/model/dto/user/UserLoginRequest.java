package com.frank.apibackstage.model.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "frank")
    @NotEmpty(message = "账号不能为空")
    @Length(min = 4, max = 16, message = "账号长度为 4-16 字符")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号由数字和字母组成")
    private String userAccount;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminFrank")
    @NotEmpty(message = "密码不能为空")
    @Length(min = 8, max = 16, message = "密码长度为 8-16 字符")
    private String userPassword;
}
