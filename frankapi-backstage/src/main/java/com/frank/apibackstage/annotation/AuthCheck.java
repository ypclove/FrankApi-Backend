package com.frank.apibackstage.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验
 *
 * @author Frank
 * @date 2024/6/23
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 有任何一个角色
     *
     * @return 角色名称
     */
    String[] anyRole() default "";

    /**
     * 必须有某个角色
     *
     * @return 角色名称
     */
    int mustRole() default 1;
}
