package com.github.sparkzxl.entity.response;

import cn.hutool.http.HttpStatus;
import com.github.sparkzxl.entity.response.ICode;
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
    SUCCESS(HttpStatus.HTTP_OK, "请求成功"),

    /**
     * 业务异常
     */
    FAILURE(HttpStatus.HTTP_INTERNAL_ERROR, "系统繁忙，请稍候再试"),

    OPEN_SERVICE_UNAVAILABLE(HttpStatus.HTTP_UNAVAILABLE, "【{}】服务不可用，请联系管理员！"),
    
    ;

    final int code;

    final String message;
}
