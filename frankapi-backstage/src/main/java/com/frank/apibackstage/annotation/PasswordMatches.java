package com.frank.apibackstage.annotation;

import com.frank.apibackstage.validator.PasswordMatchesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 密码校验器
 *
 * @author Frank
 * @date 2024/6/24
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchesValidator.class)
public @interface PasswordMatches {

    String message() default "密码和确认密码不匹配";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
