package com.frank.apibackstage.validator;

import com.frank.apibackstage.annotation.EnumCheck;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Frank
 * @date 2024/6/24
 */
public class EnumValidator implements ConstraintValidator<EnumCheck, Integer> {

    private List<Integer> enumIntList;

    @Override
    public void initialize(EnumCheck constraintAnnotation) {
        enumIntList = Arrays.stream(constraintAnnotation.value()).boxed().collect(Collectors.toList());
        // enumIntList = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // 如果值为 null，则不进行校验，返回 true
        if (value == null) {
            return true;
        }
        // 判断值是否在枚举值列表中
        return enumIntList.contains(value);
    }
}
