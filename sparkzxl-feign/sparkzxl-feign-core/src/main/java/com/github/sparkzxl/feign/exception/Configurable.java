package com.github.sparkzxl.feign.exception;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-05-09 13:49:18
 */
public interface Configurable<C> {

    Class<C> getConfigClass();

    C newConfig();

}
