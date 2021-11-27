package com.github.sparkzxl.gateway.option;

import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;

/**
 * description: The Order Of Plugin Filter
 *
 * @author zhoux
 * @date 2021-10-23 21:32:22
 */
public enum FilterOrderEnum {

    /**
     * Gateway Context Filter
     */
    GATEWAY_CONTEXT_FILTER(Integer.MIN_VALUE),
    /**
     * Request Log Filter
     */
    REQUEST_LOG_FILTER(Integer.MIN_VALUE + 2),
    /**
     * Request Log Filter
     */
    AUTHORIZATION_FILTER(Integer.MIN_VALUE + 3),
    /**
     * Cache Response Data Filter
     */
    RESPONSE_DATA_FILTER(NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER - 1),

    /**
     * load balancer client filter
     */
    LOAD_BALANCER_CLIENT_FILTER(10150),

    ;

    private final int order;

    FilterOrderEnum(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
