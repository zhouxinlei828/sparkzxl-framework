package com.github.sparkzxl.feign.resilience4j;

import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;

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
}
