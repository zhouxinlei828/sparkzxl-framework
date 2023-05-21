package com.github.sparkzxl.feign.resilience4j.decoder;

import static feign.FeignException.errorStatus;

import com.github.sparkzxl.feign.resilience4j.annotation.RetryableMethod;
import com.github.sparkzxl.feign.resilience4j.enums.RetryableHttpStatus;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import java.lang.reflect.Method;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 默认异常解码器
 *
 * @author zhouxinlei
 * @since 2022-06-03 14:08:56
 */
@Slf4j
public class DefaultErrorDecoder implements ErrorDecoder {

    /**
     * 判断一个 OpenFeign 的请求是否是可以重试类型的请求 根据方法是否为 GET，以及方法和方法所在类上面是否有 RetryableMethod 注解来判定
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

    @Override
    public Exception decode(String methodKey, Response response) {
        boolean queryRequest = isRetryableRequest(response.request());
        boolean shouldThrowRetryable = queryRequest
                || response.status() == RetryableHttpStatus.CIRCUIT_BREAKER_ON.getValue()
                || response.status() == RetryableHttpStatus.BULKHEAD_FULL.getValue()
                || response.status() == RetryableHttpStatus.RETRYABLE_IO_EXCEPTION.getValue();
        log.info("{} response: {}-{}, should retry: {}", methodKey, response.status(), response.reason(), shouldThrowRetryable);
        //对于查询请求以及可以重试的响应码的异常，进行重试，即抛出可重试异常 RetryableException
        if (shouldThrowRetryable) {
            log.info("{} response: {}-{}, should retry: {}", methodKey, response.status(), response.reason(), shouldThrowRetryable);
            throw new RetryableException(response.status(), response.reason(), response.request().httpMethod(), null, response.request());
        } else {
            throw errorStatus(methodKey, response);
        }
    }

}
