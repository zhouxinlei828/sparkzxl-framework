package com.github.sparkzxl.feign.resilience4j;

import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.net.URL;
import java.text.MessageFormat;
import java.util.function.Supplier;

import static feign.FeignException.errorStatus;

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
     * 针对 OpenFeign 的 circuitBreaker 封装，根据响应进行断路
     *
     * @param circuitBreaker 断路器
     * @param supplier
     * @return Supplier<Response>
     */
    public static Supplier<Response> decorateSupplier(CircuitBreaker circuitBreaker, Supplier<Response> supplier) {
        return () -> {
            circuitBreaker.acquirePermission();
            long start = circuitBreaker.getCurrentTimestamp();

            long duration;
            try {
                Response result = supplier.get();
                HttpStatus httpStatus = HttpStatus.valueOf(result.status());
                duration = circuitBreaker.getCurrentTimestamp() - start;
                //这里的修改只是为了断路器针对 500 响应正常断路，因为这里还没走到 Feign 的 ErrorDecoder，所以无法抛出异常
                if (httpStatus.is2xxSuccessful()) {
                    circuitBreaker.onResult(duration, circuitBreaker.getTimestampUnit(), result);
                } else {
                    circuitBreaker.onError(duration, circuitBreaker.getTimestampUnit(), errorStatus("not useful", result));
                }
                return result;
            } catch (Exception var7) {
                duration = circuitBreaker.getCurrentTimestamp() - start;
                circuitBreaker.onError(duration, circuitBreaker.getTimestampUnit(), var7);
                throw var7;
            }
        };
    }
}
