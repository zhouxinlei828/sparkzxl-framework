package com.github.sparkzxl.entity.response;

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
public enum ResponseCode implements ICode {

    /**
     * 成功
     */
    SUCCESS(HttpStatus.HTTP_OK, "请求处理成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpStatus.HTTP_INTERNAL_ERROR, "请求处理失败"),

    ;

    final int code;

    final String message;
}
