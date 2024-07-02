package com.frank.apibackstage.aop;


import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * 对响应体进行加密
 *
 * @author Frank
 * @date 2024/7/1
 */
@ControllerAdvice
public class EncryptRespBodyAdvice {

    // implements ResponseBodyAdvice<BaseResponse<?>>

    // @Value("${encrypt.aes.key}")
    // private String AESKey;
    //
    // @Value("${encrypt.aes.keyVI}")
    // private String AESKeyVI;
    //
    // private final ObjectMapper objectMapper = new ObjectMapper();
    //
    // @Override
    // public boolean supports(MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {
    //     return returnType.hasMethodAnnotation(Encrypt.class) || returnType.getContainingClass().isAnnotationPresent(ApiEncrypt.class);
    // }
    //
    // @Override
    // public BaseResponse<?> beforeBodyWrite(BaseResponse body,
    //                                        @NotNull MethodParameter returnType,
    //                                        @NotNull MediaType selectedContentType,
    //                                        @NotNull Class selectedConverterType,
    //                                        @NotNull ServerHttpRequest request,
    //                                        @NotNull ServerHttpResponse response) {
    //     try {
    //         if (Objects.isNull(body.getData())) {
    //             return body;
    //         }
    //         String responseData = objectMapper.writeValueAsString(body);
    //         String jsonStrResp = JSON.toJSONString(responseData);
    //
    //         String encryptRespData = AESUtil.AESEncrypt(AESKey, jsonStrResp, AESKeyVI);
    //         HashMap<String, String> map = new HashMap<>();
    //         map.put("encryptRespData", encryptRespData);
    //
    //         body.setData(map);
    //         return body;
    //     } catch (Exception e) {
    //         throw new RuntimeException("响应数据加密失败：{}", e);
    //     }
    // }
}
