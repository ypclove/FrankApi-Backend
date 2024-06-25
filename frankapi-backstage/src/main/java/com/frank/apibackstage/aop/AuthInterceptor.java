package com.frank.apibackstage.aop;

import com.frank.apibackstage.annotation.AuthCheck;
import com.frank.apibackstage.model.vo.UserVO;
import com.frank.apibackstage.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AuthInterceptor
 *
 * @author Frank
 * @date 2024/6/23
 */
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 权限校验拦截器
     *
     * @param joinPoint 切入点（实际增强的方法）
     * @param authCheck 权限校验注解
     * @return 通过权限校验的方法
     * @throws Throwable Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        List<String> anyRole = Arrays.stream(authCheck.anyRole()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        Integer mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        // 当前登录用户
        UserVO user = userService.getLoginUser(request);

        // // 拥有任意权限即通过
        // if (CollectionUtils.isNotEmpty(anyRole)) {
        //     Integer userRole = user.getUserRole();
        //     if (!anyRole.contains(userRole)) {
        //         throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        //     }
        // }

        // // 必须有所有权限才通过
        // if (Objects.nonNull(mustRole)) {
        //     String userRole = user.getUserRole();
        //     if (!mustRole.equals(userRole)) {
        //         throw new BusinessException(StatusCode.NO_AUTH_ERROR);
        //     }
        // }

        // 通过权限校验，放行
        return joinPoint.proceed();
    }
}
