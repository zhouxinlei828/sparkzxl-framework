package com.github.sparkzxl.core.base;

import cn.hutool.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * description: API响应操作码
 *
 * @author zhouxinlei
 */
@Getter
@AllArgsConstructor
public enum ResponseError {

    /**
     * 成功
     */
    SUCCESS(HttpStatus.HTTP_OK, "请求成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpStatus.HTTP_INTERNAL_ERROR, "请求失败"),
    ;

    final int code;

    final String message;
}
