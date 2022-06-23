package com.github.sparkzxl.feign.resilience4j.decoder;

import com.github.sparkzxl.feign.resilience4j.OpenfeignUtil;
import com.github.sparkzxl.feign.resilience4j.enums.Resilience4jHttpStatus;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import static feign.FeignException.errorStatus;

/**
 * description: 默认异常解码器
 *
 * @author zhouxinlei
 * @since 2022-04-04 12:19:53
 */
@Slf4j
public class DefaultErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        boolean queryRequest = OpenfeignUtil.isRetryableRequest(response.request());
        boolean shouldThrowRetryable = queryRequest
                || response.status() == Resilience4jHttpStatus.CIRCUIT_BREAKER_ON.getValue()
                || response.status() == Resilience4jHttpStatus.BULKHEAD_FULL.getValue()
                || response.status() == Resilience4jHttpStatus.RETRYABLE_IO_EXCEPTION.getValue();
        log.info("{} response: {}-{}, should retry: {}", methodKey, response.status(), response.reason(), shouldThrowRetryable);
        //对于查询请求以及可以重试的响应码的异常，进行重试，即抛出可重试异常 RetryableException
        if (shouldThrowRetryable) {
            throw new RetryableException(response.status(), response.reason(), response.request().httpMethod(), null, response.request());
        } else {
            throw errorStatus(methodKey, response);
        }
    }
}
