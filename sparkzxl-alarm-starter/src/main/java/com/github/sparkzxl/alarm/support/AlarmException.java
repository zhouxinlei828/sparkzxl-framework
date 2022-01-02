package com.github.sparkzxl.alarm.support;

import lombok.Getter;

/**
 * description: 告警异常
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:19
 */
@Getter
public class AlarmException extends RuntimeException {

    private static final long serialVersionUID = 8615941358820234217L;

    private int code;

    private String message;


    public AlarmException(Throwable cause) {
        super(cause);
    }

    public AlarmException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
