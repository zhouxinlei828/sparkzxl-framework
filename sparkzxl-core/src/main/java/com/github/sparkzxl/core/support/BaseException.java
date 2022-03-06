package com.github.sparkzxl.core.support;


/**
 * description：BaseException
 *
 * @author zhouxinlei
 */
public interface BaseException {

    /**
     * 返回异常信息
     *
     * @return String
     */
    String getErrorMessage();

    /**
     * 返回异常编码
     *
     * @return String
     */
    String getErrorCode();
}
