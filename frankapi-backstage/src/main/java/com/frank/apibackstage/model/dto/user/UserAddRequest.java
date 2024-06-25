package com.frank.apibackstage.model.dto.user;

import com.frank.apibackstage.annotation.EnumCheck;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户创建请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
public class UserAddRequest implements Serializable {

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

    @Schema(description = "用户性别", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "男")
    @EnumCheck(value = {1, 0}, message = "性别只允许为男或女")
    private Integer gender;

    @Schema(description = "用户角色", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "user")
    @EnumCheck(value = {1, 0}, message = "用户角色只允许为管理员或普通用户")
    private Integer userRole;
}