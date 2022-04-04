package com.github.sparkzxl.feign.resilience4j;

import com.github.sparkzxl.feign.resilience4j.annotation.RetryableMethod;
import feign.Request;
import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.http.HttpStatus;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.Supplier;

import static feign.FeignException.errorStatus;

/**
 * description: feign工具类
 *
 * @author zhouxinlei
 * @since 2022-04-04 11:52:15
 */
public class OpenfeignUtil {
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
