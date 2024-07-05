package com.frank.apibackstage.model.dto.user;

import com.frank.apibackstage.annotation.EnumCheck;
import com.frank.apicommon.common.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author Frank
 * @date 2024/6/22
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Schema(description = "用户 Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "1L")
    @NotNull(message = "用户 Id 不能为空")
    @Min(value = 1L, message = "用户 Id 错误")
    private Long id;

    @Schema(description = "用户账号", requiredMode = Schema.RequiredMode.REQUIRED, example = "frank")
    @NotEmpty(message = "账号不能为空")
    @Length(min = 4, max = 16, message = "账号长度为 4-16 字符")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "账号由数字和字母组成")
    private String userAccount;

    @Schema(description = "用户性别", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    @EnumCheck(value = {1, 0}, message = "性别只允许为男或女")
    private Integer gender;

    @Schema(description = "用户角色", requiredMode = Schema.RequiredMode.NOT_REQUIRED, example = "1")
    @EnumCheck(value = {1, 0}, message = "用户角色只允许为管理员或普通用户")
    private Integer userRole;
}