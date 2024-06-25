package com.frank.apibackstage.validator;

import com.frank.apibackstage.annotation.PasswordMatches;
import com.frank.apibackstage.model.dto.user.UserRegisterRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Frank
 * @date 2024/6/24
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRegisterRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserRegisterRequest user, ConstraintValidatorContext context) {
        return user.getUserPassword().equals(user.getCheckPassword());
    }
}
