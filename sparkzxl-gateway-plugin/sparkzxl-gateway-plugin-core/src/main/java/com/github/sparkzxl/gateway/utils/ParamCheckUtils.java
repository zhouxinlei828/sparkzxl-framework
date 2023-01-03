package com.github.sparkzxl.gateway.utils;

/**
 * description: Param check utils
 *
 * @author zhouxinlei
 * @since 2022-08-13 12:37:27
 */
public class ParamCheckUtils {

    /**
     * body is empty boolean.
     *
     * @param body the body
     * @return the boolean
     */
    public static boolean bodyIsEmpty(final String body) {
        return null == body || "".equals(body) || "null".equals(body);
    }
}
