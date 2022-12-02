package com.github.sparkzxl.feign.resilience4j.utils;

import feign.Request;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-12-01 11:04:58
 */
public class ServiceInstanceUtil {

    public static String getServiceInstance(String host, int port) {
        return MessageFormat.format("{0}:{1}", host, String.valueOf(port));
    }

    public static String getServiceInstanceId(Request request) throws MalformedURLException {
        URL url = new URL(request.url());
        return getServiceInstance(url.getHost(), url.getPort());
    }

    public static String getServiceInstanceMethodId(Request request) throws MalformedURLException {
        URL url = new URL(request.url());
        //通过微服务名称 + 实例 + 方法的方式，获取唯一id
        return getServiceInstanceMethodId(url.getHost(), url.getPort(), request.requestTemplate().methodMetadata().method());
    }

    public static String getServiceInstanceMethodId(String host, int port, Method method) {
        return getServiceInstance(host, port) + ":" + MessageFormat.format("{0}:{1}",
                method.getDeclaringClass().getName(),
                method.getName());
    }

    public static String getServiceInstanceMethodId(String host, int port, String path) {
        return getServiceInstance(host, port) + path;
    }

}
