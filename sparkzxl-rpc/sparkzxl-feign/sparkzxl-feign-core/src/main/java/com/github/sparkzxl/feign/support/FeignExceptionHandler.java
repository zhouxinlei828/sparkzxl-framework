package com.github.sparkzxl.feign.support;

import cn.hutool.core.lang.Opt;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import com.github.sparkzxl.feign.exception.RemoteCallTransferException;
import feign.*;
import feign.codec.DecodeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
        return R.failDetail(ExceptionErrorCode.TIME_OUT_ERROR.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(RetryableException.class)
    public R<?> handleRetryableException(RetryableException e) {
        log.error("RetryableException 异常:", e);
        String applicationName = Opt.ofNullable(e.request()).map(Request::requestTemplate)
                .map(RequestTemplate::feignTarget).map(Target::name).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format(ExceptionErrorCode.RETRY_ABLE_EXCEPTION.getErrorMsg(), applicationName);
        return R.failDetail(ExceptionErrorCode.RETRY_ABLE_EXCEPTION.getErrorCode(), message);
    }


    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public R<?> handleServiceUnavailableException(FeignException.ServiceUnavailable e) {
        log.error("ServiceUnavailable异常:", e);
        String applicationName = Opt.ofNullable(e.request()).map(Request::requestTemplate)
                .map(RequestTemplate::feignTarget).map(Target::name).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format(ExceptionErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorMsg(), applicationName);
        return R.failDetail(ExceptionErrorCode.OPEN_SERVICE_UNAVAILABLE.getErrorCode(), message);
    }

    @ExceptionHandler(DecodeException.class)
    public R<?> handleDecodeException(DecodeException e) {
        log.error("DecodeException 异常:", e);
        return R.failDetail(ExceptionErrorCode.DECODE_EXCEPTION.getErrorCode(), ExceptionErrorCode.DECODE_EXCEPTION.getErrorMsg());
    }

    @ExceptionHandler(RemoteCallTransferException.class)
    public R<?> handleRemoteCallException(RemoteCallTransferException e) {
        log.error("RemoteCallTransferException 异常:", e);
        String applicationName = Opt.ofNullable(e.request()).map(Request::requestTemplate)
                .map(RequestTemplate::feignTarget).map(Target::name).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format("【{}】异常,{}", applicationName, e.getErrorMsg());
        return R.failDetail(e.getErrorCode(), message);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.FEIGN_EXCEPTION_ORDER.getOrder();
    }
}
