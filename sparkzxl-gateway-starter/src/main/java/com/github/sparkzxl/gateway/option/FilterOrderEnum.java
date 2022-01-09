package com.github.sparkzxl.gateway.option;

import org.springframework.cloud.gateway.filter.NettyWriteResponseFilter;

/**
 * description: The Order Of Plugin Filter
 *
 * @author zhoux
 */
public enum FilterOrderEnum {

    /**
     * Gateway Context Filter
     */
    GATEWAY_CONTEXT_FILTER(10),
    /**
     * authorization Filter
     */
    AUTHORIZATION_FILTER(20),
    /**
     * MDCLog Filter
     */
    MDC_LOG_FILTER(30),
    /**
     * Request Log Filter
     */
    REQUEST_LOG_FILTER(40),
    /**
     * Cache Response Data Filter
     */
    RESPONSE_DATA_FILTER(50),

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
