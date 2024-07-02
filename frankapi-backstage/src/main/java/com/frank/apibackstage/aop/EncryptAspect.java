package com.frank.apibackstage.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Frank
 * @date 2024/7/1
 */
@Aspect
@Component
public class EncryptAspect {

    @Value("${encrypt.rsa.rsa-public-key}")
    private String RSAPublicKey;

    @Value("${encrypt.rsa.rsa-private-key}")
    private String RSAPrivateKey;

    @Value("${encrypt.aes.key}")
    private String AESKey;

    @Value("${encrypt.aes.keyVI}")
    private String AESKeyVI;

    /**
     * 对 apiackstage 包下所有的 controller 进行 AOP 切面
     */
    @Pointcut("execution(public * com.frank.apibackstage.controller.*.*(..))")
    public void encryptRequest() {
    }

    /**
     * 思路：
     * 1. 获取该执行方法的注解
     * 2. 如果是 Encrypt 注解，并且响应成功，对响应数据进行加密，并返回
     * 3. 否则，程序继续从原来的地方执行
     *
     * @param joinPoint 实际执行方法
     * @return Object
     * @throws Throwable Throwable
     */
    // @Around("encryptRequest()")
    // public Object encryptResponse(ProceedingJoinPoint joinPoint) throws Throwable {
    //     MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    //     Method methods = methodSignature.getMethod();
    //     // 获取方法的注解
    //     Encrypt annotation = methods.getAnnotation(Encrypt.class);
    //     if (Objects.nonNull(annotation)) {
    //         Object methodResp = joinPoint.proceed();
    //         String jsonStrResp = JSON.toJSONString(methodResp);
    //
    //         String encryptRespData = AESUtil.AESEncrypt(AESKey, jsonStrResp, AESKeyVI);
    //         // String encryptAESKey = RSAUtil.RSAPublicKeyEncrypt(AESKey, RSAUtil.getPublicKeyInstance(RSAPublicKey));
    //
    //         HashMap<String, String> map = new HashMap<>();
    //         map.put("encryptRespData", encryptRespData);
    //         // map.put("encryptAESKey", encryptAESKey);
    //         return ResultUtils.success(map);
    //     }
    //     return joinPoint.proceed();
    // }
}
