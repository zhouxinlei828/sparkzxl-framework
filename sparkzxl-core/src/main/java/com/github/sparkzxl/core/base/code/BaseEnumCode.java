package com.github.sparkzxl.core.base.code;

/**
 * description: 封装API响应状态码
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:45:46
 */
public interface BaseEnumCode {
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
