package com.frank.apibackstage.aop;


import com.alibaba.fastjson.JSONObject;
import com.frank.apibackstage.annotation.RequestRSA;
import com.frank.apicommon.utils.RequestDecryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Frank
 * @date 2024/6/25
 */
@Slf4j
@Aspect
@Order(2)
@Component
public class RequestRSAAspect {

    /**
     * 1. 获取请求参数
     * 2. 获取被请求接口的入参类型
     * 3. 判断是否为 get 请求，是则跳过 AES 解密判断
     * 4. 请求参数解密，封装到接口的入参
     */
    @Pointcut("execution(public * com.frank.apibackstage.controller.*.*(..))")
    public void requestRAS() {
    }

    /**
     * 思路：
     * 1. 判断 controller 接收到请求是否带有 @RequestRSA 注解
     * 2. 如果带有注解，通过 ProceedingJoinPoint 类 getArgs() 方法获取请求的 body 参数
     * 3. 将 body 参数转换为 JSONObject 类，获取到 asy 和 sym 属性，再调用 RequestDecryptionUtil 解密获取接口传递的真实参数
     * 4. 获取接口入参的类
     * 5. 将获取解密后的真实参数，封装到接口入参的类中
     *
     * @param joinPoint 切入点（实际增强的方法）
     * @return Object
     * @throws Throwable Throwable
     */
    @Around("requestRAS()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method methods = methodSignature.getMethod();
        // 获取方法的注解
        RequestRSA annotation = methods.getAnnotation(RequestRSA.class);
        if (Objects.nonNull(annotation)) {
            // 获取请求的 body 参数
            Object data = getParameter(methods, joinPoint.getArgs());
            // 将 body 参数转换为 JSONObject 类
            String body = JSONObject.toJSONString(data);
            // 获取 asy 和 sym 的值
            JSONObject jsonObject = JSONObject.parseObject(body);
            String asy = jsonObject.get("asy").toString();
            String sym = jsonObject.get("sym").toString();
            // 调用 RequestDecryptionUtil 方法解密，获取解密后的真实参数
            JSONObject decryption = RequestDecryptionUtil.getRequestDecryption(sym, asy);
            // 获取接口入参的类
            String typeName = joinPoint.getArgs()[0].getClass().getTypeName();
            System.out.println("参数值类型：" + typeName);
            Class<?> aClass = joinPoint.getArgs()[0].getClass();
            // 将获取解密后的真实参数，封装到接口入参的类中
            Object o = JSONObject.parseObject(decryption.toJSONString(), aClass);
            Object[] as = {o};
            return joinPoint.proceed(as);
        }
        return joinPoint.proceed();
    }

    /**
     * 根据方法和传入的参数获取请求参数（获取的是接口的入参）
     *
     * @param method 方法名
     * @param args   参数
     * @return 返回请求的 body 参数
     */
    private Object getParameter(Method method, Object[] args) {
        List<Object> argList = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            // 将 RequestBody 注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if (requestBody != null) {
                argList.add(args[i]);
            }
        }
        if (argList.isEmpty()) {
            return null;
        } else if (argList.size() == 1) {
            return argList.get(0);
        } else {
            return argList;
        }
    }
}
