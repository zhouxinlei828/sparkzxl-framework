package com.github.sparkzxl.gateway.plugin.common.utils;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import org.springframework.cloud.gateway.support.ipresolver.RemoteAddressResolver;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 主机地址获取
 *
 * @author zhouxinlei
 * @date 2022-01-10 11:14:56
 */
public class HostAddressUtils {

    private HostAddressUtils() {
    }

    /**
     * Acquire host string.
     *
     * @param exchange the exchange
     * @return the string
     */
    public static String acquireHost(final ServerWebExchange exchange) {
        return SpringContextUtils.getBean(RemoteAddressResolver.class).resolve(exchange).getHostString();
    }

    /**
     * Acquire ip string.
     *
     * @param exchange the exchange
     * @return the string
     */
    public static String acquireIp(final ServerWebExchange exchange) {
        return SpringContextUtils.getBean(RemoteAddressResolver.class).resolve(exchange).getAddress().getHostAddress();
    }

}
