package com.github.sparkzxl.gateway.plugin.dubbo;

/**
 * description: Param check utils
 *
 * @author zhouxinlei
 * @since 2022-08-13 12:37:27
 */
public class ParamCheckUtils {

    /**
     * Dubbo body is empty boolean.
     *
     * @param body the body
     * @return the boolean
     */
    public static boolean dubboBodyIsEmpty(final String body) {
        return null == body || "".equals(body) || "{}".equals(body) || "null".equals(body);
    }
}
