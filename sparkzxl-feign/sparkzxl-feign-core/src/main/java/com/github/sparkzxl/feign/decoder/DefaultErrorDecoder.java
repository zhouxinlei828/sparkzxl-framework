package com.github.sparkzxl.feign.decoder;

import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.feign.enums.FeignStatusEnum;
import com.github.sparkzxl.feign.enums.RetryableHttpStatus;
import com.github.sparkzxl.feign.exception.RemoteCallTransferException;
import com.github.sparkzxl.feign.util.OpenfeignUtil;
import feign.Response;
import feign.RetryableException;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Reader;
import java.nio.charset.StandardCharsets;

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
        boolean queryRequest = OpenfeignUtil.isRetryableRequest(response.request());
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
            try {
                Reader reader = response.body().asReader(StandardCharsets.UTF_8);
                String body = Util.toString(reader);
                if (StringUtils.isNotEmpty(body)) {
                    com.github.sparkzxl.entity.response.Response<?> responseResult = JsonUtil.parse(body, com.github.sparkzxl.entity.response.Response.class);
                    return new RemoteCallTransferException(FeignStatusEnum.TRANSFER_EXCEPTION.getCode(),
                            responseResult.getErrorCode(),
                            responseResult.getErrorMsg(),
                            responseResult,
                            response.request());
                }
                return new RemoteCallTransferException(FeignStatusEnum.UNKNOWN_EXCEPTION.getCode(), ExceptionErrorCode.FAILURE.getErrorCode(), "unKnowException", null, response.request());
            } catch (Exception e) {
                log.error("[{}] has an unknown exception.", methodKey, e);
                return new RemoteCallTransferException(FeignStatusEnum.UNKNOWN_EXCEPTION.getCode(), ExceptionErrorCode.FAILURE.getErrorCode(), "unKnowException", e, null, response.request());
            }
        }
    }
}
