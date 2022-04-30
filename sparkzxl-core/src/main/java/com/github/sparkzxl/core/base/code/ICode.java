package com.github.sparkzxl.core.base.code;

/**
 * description: 封装API响应状态码
 *
 * @author zhouxinlei
 */
public interface ICode {
    /**
     * code
     *
     * @return long
     */
    String getCode();

    /**
     * getMessage
     *
     * @return String
     */
    String getMessage();
}
