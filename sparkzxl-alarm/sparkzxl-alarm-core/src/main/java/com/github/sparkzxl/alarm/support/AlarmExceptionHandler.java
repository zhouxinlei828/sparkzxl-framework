package com.github.sparkzxl.alarm.support;

import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.exception.AsyncCallException;
import com.github.sparkzxl.core.constant.enums.BeanOrderEnum;
import com.github.sparkzxl.core.base.result.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * description: 告警全局异常处理
 *
 * @author zhouxinlei
 */
@Slf4j
@RestControllerAdvice
public class AlarmExceptionHandler implements Ordered {

    @ExceptionHandler(AlarmException.class)
    public ApiResult<?> handleAlarmException(AlarmException e) {
        log.error("AlarmException 异常:", e);
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    @ExceptionHandler(AsyncCallException.class)
    public ApiResult<?> handleAsyncCallException(AsyncCallException e) {
        log.error("AsyncCallException 异常:", e);
        return ApiResult.fail(e.getErrorCode(), e.getErrorMsg());
    }

    @Override
    public int getOrder() {
        return BeanOrderEnum.ALARM_EXCEPTION_ORDER.getOrder();
    }
}
