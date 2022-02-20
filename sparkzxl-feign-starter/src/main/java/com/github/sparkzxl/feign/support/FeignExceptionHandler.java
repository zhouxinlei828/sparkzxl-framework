package com.github.sparkzxl.feign.support;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ExceptionErrorCode;
import com.github.sparkzxl.entity.response.Response;
import com.github.sparkzxl.feign.exception.RemoteCallException;
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
 */
@Slf4j
@ResponseResultStatus
@RestControllerAdvice
public class FeignExceptionHandler implements Ordered {

    @ExceptionHandler(SocketTimeoutException.class)
    public Response<?> handleSocketTimeoutException(SocketTimeoutException e) {
        log.error("SocketTimeoutException异常:", e);
        return Response.failDetail(ExceptionErrorCode.TIME_OUT_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public Response<?> handleRetryableException(FeignException e) {
        log.error("FeignException异常:", e);
        return Response.failDetail(ExceptionErrorCode.RETRY_ABLE_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(RemoteCallException.class)
    public Response<?> handleRemoteCallException(RemoteCallException e) {
        log.error("RemoteCallException异常:", e);
        String applicationName = OptionalBean.ofNullable(e.getApplicationName()).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format("【{}】发生异常,{}", applicationName, e.getMessage());
        e.printStackTrace();
        return Response.failDetail(e.getErrorCode(), message);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.FEIGN_EXCEPTION_ORDER.getOrder();
    }
}
