package com.github.sparkzxl.feign.support;

import cn.hutool.core.bean.OptionalBean;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.annotation.ResponseResultStatus;
import com.github.sparkzxl.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.base.result.ResponseResult;
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
    public ResponseResult<?> handleSocketTimeoutException(SocketTimeoutException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.TIME_OUT_ERROR.getCode(), e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseResult<?> handleRetryableException(FeignException e) {
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(ResponseInfoStatus.RETRY_ABLE_EXCEPTION.getCode(), e.getMessage());
    }

    @ExceptionHandler(RemoteCallException.class)
    public ResponseResult<?> handleRemoteCallException(RemoteCallException e) {
        String applicationName = OptionalBean.ofNullable(e.getApplicationName()).orElseGet(() -> "unKnownServer");
        String message = StrFormatter.format("【{}】发生异常,{}", applicationName, e.getMessage());
        e.printStackTrace();
        log.error(ExceptionUtil.getSimpleMessage(e));
        return ResponseResult.result(e.getCode(), message);
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.FEIGN_EXCEPTION_ORDER.getOrder();
    }
}
