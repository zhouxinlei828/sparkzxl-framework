package com.github.sparkzxl.core.base.code;

/**
 * description: 封装API响应状态码
 *
 * @author zhouxinlei
 */
public interface ResponseInfo {
    /**
     * code
     *
     * @return long
     */
    int getCode();

    /**
     * getMessage
     *
     * @return String
     */
    String getMessage();
}
