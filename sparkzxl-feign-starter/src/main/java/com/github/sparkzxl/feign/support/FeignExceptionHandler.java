package com.github.sparkzxl.feign.support;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.feign.exception.RemoteCallException;
import com.github.sparkzxl.model.exception.ExceptionChain;
import com.netflix.client.ClientException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;

/**
 * Springboot WEB应用全局异常处理
 *
 * @author wh_king
 */
@Slf4j
@ResponseBody
@RestControllerAdvice
public class FeignExceptionHandler implements Ordered {

    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleSocketTimeoutException(SocketTimeoutException e) {
        log.error("SocketTimeoutException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.TIME_OUT_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<?> handleRetryableException(FeignException e) {
        log.error("FeignException：[{}]", e.getMessage());
        return ApiResult.apiResult(ApiResponseStatus.RETRY_ABLE_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(RemoteCallException.class)
    public ApiResult<?> handleRemoteCallException(RemoteCallException e) {
        String applicationName = OptionalBean.ofNullable(e.getRawExceptionInfo()).getBean(ExceptionChain::getApplicationName).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format("【{}】发生异常,{}", applicationName, e.getMessage());
        log.error("RemoteCallException：[{}]", message);
        return ApiResult.apiResult(e.getCode(), message);
    }

    @ExceptionHandler(ClientException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiResult<?> handleClientException(ClientException e) {
        log.error("ClientException：[{}]", e.getMessage());
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
