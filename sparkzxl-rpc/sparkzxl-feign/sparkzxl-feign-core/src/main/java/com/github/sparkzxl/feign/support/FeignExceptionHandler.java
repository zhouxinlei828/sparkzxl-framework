package com.github.sparkzxl.feign.support;

import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.feign.exception.RemoteCallTransferException;
import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import feign.codec.DecodeException;
import java.net.SocketTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * description: Springboot WEB应用全局异常处理
 *
 * @author zhouxinlei
 */
@Slf4j
@RestControllerAdvice
public class FeignExceptionHandler implements Ordered {

    @ExceptionHandler(SocketTimeoutException.class)
    public R<?> handleSocketTimeoutException(SocketTimeoutException e) {
        log.error("SocketTimeoutException 异常:", e);
        return R.failDetail(ResultErrorCode.TIME_OUT_ERROR.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(RetryableException.class)
    public R<?> handleRetryableException(RetryableException e) {
        log.error("RetryableException 异常:", e);
        Request request = e.request();
        String applicationName;
        if (ObjectUtils.isNotEmpty(request)) {
            applicationName = request.requestTemplate().feignTarget().name();
        } else {
            applicationName = "unKnownServer";
        }
        String message = StrFormatter.format(ResultErrorCode.RETRY_ABLE_EXCEPTION.getErrorMsg(), applicationName);
        return R.failDetail(ResultErrorCode.RETRY_ABLE_EXCEPTION.getErrorCode(), message);
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public R<?> handleServiceUnavailableException(FeignException.ServiceUnavailable e) {
        log.error("ServiceUnavailable异常:", e);
        Request request = e.request();
        String applicationName;
        if (ObjectUtils.isNotEmpty(request)) {
            applicationName = request.requestTemplate().feignTarget().name();
        } else {
            applicationName = "unKnownServer";
        }
        String message = StrFormatter.format(ResultErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorMsg(), applicationName);
        return R.failDetail(ResultErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorCode(), message);
    }

    @ExceptionHandler(DecodeException.class)
    public R<?> handleDecodeException(DecodeException e) {
        log.error("DecodeException 异常:", e);
        return R.failDetail(ResultErrorCode.DECODE_EXCEPTION.getErrorCode(), ResultErrorCode.DECODE_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(RemoteCallTransferException.class)
    public R<?> handleRemoteCallException(RemoteCallTransferException e) {
        log.error("RemoteCallTransferException 异常:", e);
        Request request = e.request();
        String applicationName;
        if (ObjectUtils.isNotEmpty(request)) {
            applicationName = request.requestTemplate().feignTarget().name();
        } else {
            applicationName = "unKnownServer";
        }
        String message = StrFormatter.format("【{}】异常,{}", applicationName, e.getErrorMsg());
        return R.failDetail(e.getErrorCode(), message);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.FEIGN_EXCEPTION_ORDER.getOrder();
    }
}
