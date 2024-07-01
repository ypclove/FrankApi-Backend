package com.frank.apibackstage.validator;

import com.frank.apibackstage.annotation.ValidInvitationCode;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static com.frank.apicommon.constant.UserConstant.INVITATION_CODE_PATTERN;

/**
 * @author Frank
 * @date 2024/6/30
 */
public class InvitationCodeValidator implements ConstraintValidator<ValidInvitationCode, String> {

    @Override
    public void initialize(ValidInvitationCode constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 如果邀请码为空，则无需校验
        if (value == null || value.isEmpty()) {
            return true;
        }
        // 校验邀请码格式是否符合要求
        return value.matches(INVITATION_CODE_PATTERN);
    }
}
