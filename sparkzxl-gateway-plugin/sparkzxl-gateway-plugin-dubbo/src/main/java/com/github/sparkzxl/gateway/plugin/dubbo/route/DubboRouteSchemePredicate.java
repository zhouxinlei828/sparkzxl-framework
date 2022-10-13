package com.github.sparkzxl.gateway.plugin.dubbo.route;

import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

import java.net.URI;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;

/**
 * description: dubbo 请求断言
 *
 * @author zhouxinlei
 * @since 2022-08-15 13:59:09
 */
public class DubboRouteSchemePredicate implements DubboRoutePredicate {

    private final static String DUBBO_SCHEME = "dubbo";

    @Override
    public boolean test(ServerWebExchange exchange, Route route) {
        URI url = route.getUri();
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        return url != null && (DUBBO_SCHEME.equals(url.getScheme()) || DUBBO_SCHEME.equals(schemePrefix));
    }
}
