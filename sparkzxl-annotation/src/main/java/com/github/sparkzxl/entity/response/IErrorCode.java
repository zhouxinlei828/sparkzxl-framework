package com.github.sparkzxl.entity.response;

/**
 * description: 封装异常状态码
 *
 * @author zhouxinlei
 */
public interface IErrorCode {
    /**
     * code
     *
     * @return String
     */
    String getCode();

    /**
     * getMessage
     *
     * @return String
     */
    String getMessage();
}
