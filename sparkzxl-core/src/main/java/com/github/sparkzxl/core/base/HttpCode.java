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
     * 认证异常
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED.value(), "用户登录已过期"),

    /**
     * 认证异常
     */
    FORBIDDEN(HttpStatus.FORBIDDEN.value(), "用户访问未授权"),
    ;

    final int code;

    final String message;
}
