package com.sparksys.commons.core.base.api.code;

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
     * @author zhouxinlei
     * @date 2019-09-27 16:56:40
     */
    int getCode();

    /**
     * getMessage
     *
     * @return String
     * @author zhouxinlei
     * @date 2019-09-27 16:56:49
     */
    String getMessage();
}
