package com.frank.apicommon.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frank.apicommon.common.BaseResponse;
import com.frank.apicommon.common.ResultUtils;
import com.frank.apicommon.common.StatusCode;
import com.frank.apicommon.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.lang.reflect.Field;

import static com.frank.apicommon.constant.NetConstant.GET_REQUEST_TIMEOUT;
import static com.frank.apicommon.constant.NetConstant.HTTP_CONNECT_TIMEOUT;

/**
 * @author Frank
 * @date 2024/7/2
 */
@Slf4j
public class RequestUtil {

    public static <T> String buildUrl(String baseUrl, T params) throws BusinessException {
        StringBuilder url = new StringBuilder(baseUrl);
        Field[] fields = params.getClass().getDeclaredFields();
        boolean isFirstParam = true;
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            // 跳过 serialVersionUID 属性
            if ("serialVersionUID".equals(name)) {
                continue;
            }
            try {
                Object value = field.get(params);
                if (value != null) {
                    if (isFirstParam) {
                        url.append("?").append(name).append("=").append(value);
                        isFirstParam = false;
                    } else {
                        url.append("&").append(name).append("=").append(value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new BusinessException(StatusCode.OPERATION_ERROR, "构建 url 异常");
            }
        }
        return url.toString();
    }

    public static String buildUrl(String baseUrl, String params) {
        return baseUrl + params;
    }

    public static <T> BaseResponse<T> sendGetRequest(String url, Class<T> responseType, String bodyName) {
        HttpClient httpGetClient = new HttpClient();
        GetMethod getMethod = getRequestInit(url, httpGetClient);
        T result;
        try {
            int statusCode = httpGetClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "服务端响应失败");
            }
            // 处理请求头
            /*
            Header[] headers = getMethod.getResponseHeaders();
            for (Header header : headers) {
                log.info("{} : {}", header.getName(), header.getValue());
            }
            */
            // 处理响应体
            String response = getMethod.getResponseBodyAsString();
            JSONObject jsonObject = JSON.parseObject(response);
            String info = jsonObject.getString(bodyName);
            result = JSON.parseObject(info, responseType);
            // result.setIp(jsonObject.getString(additionalInfo));
            // 在网页内容数据量大时候推荐读取为输入流
            // InputStream response = getMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            log.error("HTTP 异常：{}", e.getMessage());
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请检查输入的 URL");
        } catch (IOException e) {
            log.error("网络异常：{}", e.getMessage());
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "网络异常，请检查网络是否连接");
        } finally {
            getMethod.releaseConnection();
        }
        return ResultUtils.success(result);
    }

    public static JSONObject sendGetRequest(String url, String... additionalInfo) {
        // 生成 HttpClient 对象并设置参数
        HttpClient httpGetClient = new HttpClient();
        GetMethod getMethod = getRequestInit(url, httpGetClient);
        JSONObject combinedData = new JSONObject();
        try {
            int statusCode = httpGetClient.executeMethod(getMethod);
            if (statusCode != HttpStatus.SC_OK) {
                throw new BusinessException(StatusCode.SYSTEM_ERROR, "服务端响应失败");
            }
            // 处理请求头
            /*
            Header[] headers = getMethod.getResponseHeaders();
            for (Header header : headers) {
                log.info("{} : {}", header.getName(), header.getValue());
            }
            */
            // 处理响应体
            String response = getMethod.getResponseBodyAsString();
            JSONObject jsonObject = JSON.parseObject(response);
            for (String bodyName : additionalInfo) {
                if (bodyName.contains(".")) {
                    String[] parts = bodyName.split("\\.");
                    String realData = jsonObject.getJSONObject(parts[0]).getString(parts[1]);
                    if (isJsonArray(realData)) {
                        combinedData.put(parts[1], JSON.parseArray(realData));
                    } else {
                        combinedData.put(parts[1], JSON.parseObject(realData));
                    }
                } else {
                    String realData = jsonObject.getString(bodyName);
                    if (isJsonArray(realData)) {
                        combinedData.put(bodyName, JSON.parseArray(realData));
                    } else {
                        combinedData.put(bodyName, JSON.parseObject(realData));
                    }
                }
            }
            // 在网页内容数据量大时候推荐读取为输入流
            // InputStream response = getMethod.getResponseBodyAsStream();
        } catch (HttpException e) {
            // 发生致命的异常，可能是协议不对或者返回的内容有问题
            log.error("HTTP 异常：{}", e.getMessage());
            throw new BusinessException(StatusCode.PARAMS_ERROR, "请检查输入的 URL");
        } catch (IOException e) {
            log.error("网络异常：{}", e.getMessage());
            throw new BusinessException(StatusCode.SYSTEM_ERROR, "网络异常，请检查网络是否连接");
        } finally {
            getMethod.releaseConnection();
        }
        return combinedData;
    }

    /**
     * 初始化 get 请求
     *
     * @param url        路由
     * @param httpClient HTTP 客户端
     * @return GetMethod
     */
    private static GetMethod getRequestInit(String url, HttpClient httpClient) {
        // 设置 Http 连接超时为 5 秒
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(HTTP_CONNECT_TIMEOUT);
        // 生成 GetMethod 对象并设置参数
        GetMethod getMethod = new GetMethod(url);
        // 设置 get 请求超时为 5 秒
        getMethod.getParams().setParameter(HttpMethodParams.SO_TIMEOUT, GET_REQUEST_TIMEOUT);
        // 设置请求头
        getMethod.setRequestHeader("", "");
        // 设置请求重试处理，用的是默认的重试处理：请求三次
        getMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        return getMethod;
    }

    /**
     * 判断能否将 jsonStr 解析成 JsonArray
     *
     * @param jsonStr json 字符串
     * @return 能否将 jsonStr 解析成 JsonArray
     */
    private static boolean isJsonArray(String jsonStr) {
        try {
            return JSON.parseArray(jsonStr) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
