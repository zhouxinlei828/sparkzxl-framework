package com.github.sparkzxl.alarm.exception;

import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;

/**
 * description: 异步调用异常
 *
 * @author zhouxinlei
 * @since 2022-05-18 16:16:17
 */
public class AsyncCallException extends AlarmException {
    public AsyncCallException(String msg) {
        super(AlarmResponseCodeEnum.ASYNC_CALL.getErrorCode(), msg);
    }

    public AsyncCallException(Throwable cause) {
        super(cause, AlarmResponseCodeEnum.ASYNC_CALL.getErrorCode(), AlarmResponseCodeEnum.ASYNC_CALL.getErrorMsg());
    }
}