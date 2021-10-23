package com.github.sparkzxl.gateway.context;

/**
 * description:
 *
 * @author zhoux
 * @date 2021-10-23 16:51:05
 */
public interface GatewayContextExtraData<T> {

    /**
     * get context extra data
     *
     * @return
     */
    T getData();
}
