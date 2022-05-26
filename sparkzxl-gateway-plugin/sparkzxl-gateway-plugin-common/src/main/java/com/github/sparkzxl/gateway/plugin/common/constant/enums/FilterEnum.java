package com.github.sparkzxl.gateway.plugin.common.constant.enums;

import java.util.Arrays;

import static org.springframework.cloud.gateway.filter.NettyWriteResponseFilter.WRITE_RESPONSE_FILTER_ORDER;

/**
 * description: FilterEnum
 *
 * @author zhouxinlei
 * @since 2022-01-07 12:47:53
 */
public enum FilterEnum {

    /**
     * Global Filter enum.
     */
    CONTEXT(Integer.MIN_VALUE, "context"),

    /**
     * Sign Filter enum.
     */
    SIGN(Integer.MIN_VALUE + 1, "sign"),

    /**
     * Jwt Filter enum.
     */
    JWT(Integer.MIN_VALUE + 2, "jwt"),

    /**
     * authorization Filter enum.
     */
    OAUTH2(Integer.MIN_VALUE + 3, "oauth2"),

    /**
     * Rate limiter Filter enum.
     */
    RATE_LIMITER(Integer.MIN_VALUE + 5, "rateLimiter"),

    /**
     * Request Filter enum.
     */
    REQUES_TLOG(Integer.MIN_VALUE + 6, "requestLog"),

    /**
     * response Filter enum.
     */
    LOGGING(WRITE_RESPONSE_FILTER_ORDER - 1, "logging"),

    /**
     * Waf Filter enum.
     */
    WAF(10, "waf"),

    /**
     * loadBalancer Filter enum.
     */
    LOAD_BALANCER_CLIENT_FILTER(10150, "loadbalancer"),
    ;

    private final int code;

    private final String name;

    /**
     * all args constructor.
     *
     * @param code code
     * @param name name
     */
    FilterEnum(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * get plugin enum by name.
     *
     * @param name plugin name.
     * @return plugin enum.
     */
    public static FilterEnum getFilterEnumByName(final String name) {
        return Arrays.stream(FilterEnum.values())
                .filter(pluginEnum -> pluginEnum.getName().equals(name))
                .findFirst().orElse(FilterEnum.CONTEXT);
    }

    /**
     * get code.
     *
     * @return code code
     */
    public int getCode() {
        return code;
    }

    /**
     * get name.
     *
     * @return name name
     */
    public String getName() {
        return name;
    }
}
