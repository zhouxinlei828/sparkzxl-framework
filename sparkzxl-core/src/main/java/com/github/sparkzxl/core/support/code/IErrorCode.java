package com.github.sparkzxl.core.support.code;

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
    String getErrorCode();

    /**
     * getMessage
     *
     * @return String
     */
    String getErrorMsg();
}
