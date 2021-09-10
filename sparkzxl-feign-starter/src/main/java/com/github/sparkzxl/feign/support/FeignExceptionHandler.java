package com.github.sparkzxl.feign.support;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.netflix.client.ClientException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;

/**
 * description: Springboot WEB应用全局异常处理
 *
 * @author zhouxinlei
 * @date 2021-08-25 12:05:06
 */
@Slf4j
@ResponseResultStatus
@RestControllerAdvice
public class FeignExceptionHandler implements Ordered {

    @ExceptionHandler(SocketTimeoutException.class)
    public ApiResult<?> handleSocketTimeoutException(SocketTimeoutException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.TIME_OUT_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ApiResult<?> handleRetryableException(FeignException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(ApiResponseStatus.RETRY_ABLE_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(RemoteCallException.class)
    public ApiResult<?> handleRemoteCallException(RemoteCallException e) {
        String applicationName = OptionalBean.ofNullable(e.getApplicationName()).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format("【{}】发生异常,{}", applicationName, e.getMessage());
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ApiResult.apiResult(e.getCode(), message);
    }

    @ExceptionHandler(ClientException.class)
    public ApiResult<?> handleClientException(ClientException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        String matchString = "Load balancer does not have available server for client: ";
        String message = e.getMessage();
        if (message.contains(matchString)) {
            int indexOf = message.lastIndexOf(": ") + 2;
            String serviceName = message.substring(indexOf);
            String applicationName = OptionalBean.ofNullable(serviceName).orElseGet(() -> "unKnownServer");
            message = StrFormatter.format(ApiResponseStatus.OPEN_SERVICE_UNAVAILABLE.getMessage(), applicationName);
        }
        return ApiResult.apiResult(ApiResponseStatus.OPEN_SERVICE_UNAVAILABLE.getCode(), message);
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 10;
    }
}
