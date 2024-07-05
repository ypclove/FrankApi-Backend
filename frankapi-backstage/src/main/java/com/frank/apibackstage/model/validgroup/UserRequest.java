package com.frank.apibackstage.model.validgroup;

import com.frank.apibackstage.annotation.EnumCheck;
import com.frank.apibackstage.annotation.PasswordMatches;
import com.frank.apibackstage.annotation.ValidInvitationCode;
import com.frank.apicommon.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 用户请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@PasswordMatches(groups = UserValidGroup.Register.PlateRegister.class)
public class UserRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户 Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1L")
    @NotNull(groups = {UserValidGroup.Crud.Update.class,
            UserValidGroup.Crud.Delete.class,
            UserValidGroup.Crud.Query.class},
            message = "用户 Id 不能为空")
    @Min(groups = {UserValidGroup.Crud.Update.class,
            UserValidGroup.Crud.Delete.class,
            UserValidGroup.Crud.Query.class},
            value = 1L, message = "用户 Id 错误")
    private Long id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "frank")
    @NotEmpty(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Crud.Update.class,
            UserValidGroup.Login.PlateLogin.class,
            UserValidGroup.Register.PlateRegister.class},
            message = "账号不能为空")
    @Length(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Crud.Update.class,
            UserValidGroup.Login.PlateLogin.class,
            UserValidGroup.Register.PlateRegister.class},
            min = 4, max = 16, message = "账号长度为 4-16 字符")
    @Pattern(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Crud.Update.class,
            UserValidGroup.Login.PlateLogin.class,
            UserValidGroup.Register.PlateRegister.class},
            regexp = "^[A-Za-z0-9]+$", message = "账号由数字和字母组成")
    private String userAccount;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminFrank")
    @NotEmpty(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Login.PlateLogin.class,
            UserValidGroup.Register.PlateRegister.class},
            message = "密码不能为空")
    @Length(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Login.PlateLogin.class,
            UserValidGroup.Register.PlateRegister.class},
            min = 8, max = 16, message = "密码长度为 8-16 字符")
    private String userPassword;

    @Schema(description = "用户性别", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "男")
    @EnumCheck(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Crud.Update.class},
            value = {1, 0}, message = "性别只允许为男或女")
    private Integer gender;

    @Schema(description = "用户角色", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "user")
    @EnumCheck(groups = {UserValidGroup.Crud.Create.class,
            UserValidGroup.Crud.Update.class},
            value = {1, 0}, message = "用户角色只允许为管理员或普通用户")
    private Integer userRole;

    @Schema(description = "积分", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "30")
    @Min(groups = UserValidGroup.Crud.Update.class, value = 0, message = "积分不能为负数")
    private Integer balance;

    @Schema(description = "邮箱", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin@admin.com")
    @Email(groups = {UserValidGroup.Login.EmailLogin.class,
            UserValidGroup.Register.EmailRegister.class,
            UserValidGroup.EmailBind.BindEmail.class,
            UserValidGroup.EmailBind.UnBindEmail.class},
            message = "邮箱格式不正确")
    @NotEmpty(groups = {UserValidGroup.Login.EmailLogin.class,
            UserValidGroup.Register.EmailRegister.class,
            UserValidGroup.EmailBind.BindEmail.class,
            UserValidGroup.EmailBind.UnBindEmail.class},
            message = "邮箱不能为空")
    @Size(groups = {UserValidGroup.Login.EmailLogin.class,
            UserValidGroup.Register.EmailRegister.class,
            UserValidGroup.EmailBind.BindEmail.class,
            UserValidGroup.EmailBind.UnBindEmail.class},
            max = 50, message = "邮箱长度不能超过 50 个字符")
    private String emailAccount;

    @Schema(description = "验证码", requiredMode = Schema.RequiredMode.REQUIRED, example = "a1b2C3")
    @NotEmpty(groups = {UserValidGroup.Login.EmailLogin.class,
            UserValidGroup.Register.EmailRegister.class,
            UserValidGroup.EmailBind.BindEmail.class,
            UserValidGroup.EmailBind.UnBindEmail.class},
            message = "验证码不能为空")
    @Size(groups = {UserValidGroup.Login.EmailLogin.class,
            UserValidGroup.Register.EmailRegister.class,
            UserValidGroup.EmailBind.BindEmail.class,
            UserValidGroup.EmailBind.UnBindEmail.class},
            min = 6, max = 6, message = "验证码长度必须为 6 字符")
    private String captcha;

    @Schema(description = "确认密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "adminFrank")
    @NotEmpty(groups = UserValidGroup.Register.PlateRegister.class, message = "确认密码不能为空")
    @Length(groups = UserValidGroup.Register.PlateRegister.class, min = 8, max = 16, message = "确认密码长度为 8-16 字符")
    private String checkPassword;

    @Schema(description = "邀请码（可选）", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "7TrsW6xB")
    @ValidInvitationCode(groups = {UserValidGroup.Register.PlateRegister.class,
            UserValidGroup.Register.EmailRegister.class})
    private String invitationCode;
}