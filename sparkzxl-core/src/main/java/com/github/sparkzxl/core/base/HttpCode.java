package com.github.sparkzxl.core.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * description: API响应操作码
 *
 * @author zhouxinlei
 */
@Getter
@AllArgsConstructor
public enum HttpCode {

    /**
     * 成功
     */
    SUCCESS(HttpStatus.OK.value(), "请求成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpStatus.INTERNAL_SERVER_ERROR.value(), "请求失败"),

    /**
     * 业务异常
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase()),
    ;

    final int code;

    final String message;
}
