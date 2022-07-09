package com.github.sparkzxl.feign.resilience4j;

import com.github.sparkzxl.feign.annotation.RetryableMethod;
import feign.Request;

import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * description: Resilience4j工具类
 *
 * @author zhouxinlei
 * @since 2022-05-20 10:44:48
 */
public class Resilience4jUtil {

    public static String getServiceInstance(URL url) {
        return getServiceInstance(url.getHost(), url.getPort());
    }

    public static String getServiceInstance(String host, int port) {
        return MessageFormat.format("{0}:{1}", host, port);
    }

    public static String getServiceInstanceMethodId(URL url, Method method) {
        return getServiceInstance(url) + ":" + method.toGenericString();
    }

    public static String getServiceInstanceMethodId(String host, int port, Method method) {
        return getServiceInstance(host, port) + ":" + method.toGenericString();
    }

    public static String getServiceInstanceMethodId(String host, int port, String path) {
        return getServiceInstance(host, port) + path;
    }


    /**
     * 判断一个 OpenFeign 的请求是否是可以重试类型的请求
     * 根据方法是否为 GET，以及方法和方法所在类上面是否有 RetryableMethod 注解来判定
     *
     * @param request 请求
     * @return boolean
     */
    public static boolean isRetryableRequest(Request request) {
        Request.HttpMethod httpMethod = request.httpMethod();
        if (Objects.equals(httpMethod, Request.HttpMethod.GET)) {
            return true;
        }
        Method method = request.requestTemplate().methodMetadata().method();
        RetryableMethod annotation = method.getAnnotation(RetryableMethod.class);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(RetryableMethod.class);
        }
        //如果类上面或者方法上面有注解，则为查询类型的请求，是可以重试的
        return annotation != null;
    }
}
