package com.github.sparkzxl.alarm.exception;

import com.github.sparkzxl.alarm.enums.AlarmErrorEnum;

/**
 * description: 异步调用异常
 *
 * @author zhouxinlei
 * @since 2022-05-18 16:16:17
 */
public class AsyncCallException extends AlarmException {
    public AsyncCallException(String msg) {
        super(AlarmErrorEnum.ASYNC_CALL.getErrorCode(), msg);
    }

    public AsyncCallException(Throwable cause) {
        super(cause, AlarmErrorEnum.ASYNC_CALL.getErrorCode(), AlarmErrorEnum.ASYNC_CALL.getErrorMsg());
    }
}