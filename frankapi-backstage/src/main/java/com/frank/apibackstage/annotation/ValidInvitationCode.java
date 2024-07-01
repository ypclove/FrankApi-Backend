package com.frank.apibackstage.annotation;

import com.frank.apibackstage.validator.InvitationCodeValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Frank
 * @date 2024/6/30
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InvitationCodeValidator.class)
public @interface ValidInvitationCode {

    String message() default "邀请码无效";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}