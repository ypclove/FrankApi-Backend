package com.frank.apibackstage.model.dto.user;

import com.frank.apibackstage.annotation.PasswordMatches;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@PasswordMatches
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    // @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "frank")
    // @NotEmpty(message = "账号不能为空")
    // @Length(min = 4, max = 16, message = "账号长度为 4-16 字符")
    // @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号由数字和字母组成")
    // private String userAccount;
    //
    // @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminFrank")
    // @NotEmpty(message = "密码不能为空")
    // @Length(min = 8, max = 16, message = "密码长度为 8-16 字符")
    // private String userPassword;
    //
    // @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminFrank")
    // @NotEmpty(message = "确认密码不能为空")
    // @Length(min = 8, max = 16, message = "确认密码长度为 8-16 字符")
    // private String checkPassword;
    //
    // @Schema(description = "邀请码（可选）", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "7TrsW6xB")
    // private String invitationCode;
}
