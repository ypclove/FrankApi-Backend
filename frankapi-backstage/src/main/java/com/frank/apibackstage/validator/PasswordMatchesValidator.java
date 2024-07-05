package com.frank.apibackstage.validator;

import com.frank.apibackstage.annotation.PasswordMatches;
import com.frank.apibackstage.model.validgroup.UserRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Frank
 * @date 2024/6/24
 */
public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, UserRequest> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }

    @Override
    public boolean isValid(UserRequest user, ConstraintValidatorContext context) {
        return user.getUserPassword().equals(user.getCheckPassword());
    }
}
