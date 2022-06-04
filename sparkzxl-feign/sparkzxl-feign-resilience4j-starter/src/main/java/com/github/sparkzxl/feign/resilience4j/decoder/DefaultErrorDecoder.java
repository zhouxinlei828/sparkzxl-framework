package com.github.sparkzxl.feign.resilience4j.decoder;

import com.github.sparkzxl.feign.resilience4j.Resilience4jUtil;
import com.github.sparkzxl.feign.resilience4j.enums.RetryableHttpStatus;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import static feign.FeignException.errorStatus;

/**
 * description: 默认异常解码器
 *
 * @author zhouxinlei
 * @since 2022-06-03 14:08:56
 */
@Slf4j
public class DefaultErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        boolean queryRequest = Resilience4jUtil.isRetryableRequest(response.request());
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
