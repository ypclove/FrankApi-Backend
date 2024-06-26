package com.frank.apibackstage.annotation;


import java.lang.annotation.*;

/**
 * 接口加密、解密注解
 *
 * @author Frank
 * @date 2024/6/25
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestRSA {
}
