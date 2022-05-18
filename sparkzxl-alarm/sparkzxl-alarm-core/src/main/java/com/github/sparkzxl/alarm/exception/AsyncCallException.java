package com.github.sparkzxl.alarm.exception;

import com.github.sparkzxl.alarm.enums.ExceptionEnum;

/**
 * description: 异步调用异常
 *
 * @author zhouxinlei
 * @since 2022-05-18 16:16:17
 */
public class AsyncCallException extends AlarmException {
    public AsyncCallException(String msg) {
        super(ExceptionEnum.ASYNC_CALL.getCode(), msg);
    }

    public AsyncCallException(Throwable cause) {
        super(cause, ExceptionEnum.ASYNC_CALL.getCode(), ExceptionEnum.ASYNC_CALL.getMessage());
    }
}