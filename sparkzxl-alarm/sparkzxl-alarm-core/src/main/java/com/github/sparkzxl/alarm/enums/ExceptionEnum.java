package com.github.sparkzxl.alarm.enums;

import lombok.Getter;

/**
 * description: 异常枚举
 *
 * @author zhouxinlei
 * @since 2022-05-18 16:18:55
 */
@Getter
public enum ExceptionEnum {

    /**
     * 异步调用异常
     */
    ASYNC_CALL("Y101", "异步调用异常"),
    ;


    private final String code;
    private final String message;

    ExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}