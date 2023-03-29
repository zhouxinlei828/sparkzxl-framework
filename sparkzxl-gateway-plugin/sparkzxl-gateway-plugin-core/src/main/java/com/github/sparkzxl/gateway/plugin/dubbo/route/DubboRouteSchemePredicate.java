package com.github.sparkzxl.gateway.plugin.dubbo.route;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_SCHEME_PREFIX_ATTR;

import com.github.sparkzxl.gateway.plugin.dubbo.constant.DubboConstant;
import java.net.URI;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: dubbo 请求断言
 *
 * @author zhouxinlei
 * @since 2022-08-15 13:59:09
 */
public class DubboRouteSchemePredicate implements DubboRoutePredicate {

    @Override
    public boolean test(ServerWebExchange exchange, Route route) {
        URI url = route.getUri();
        String schemePrefix = exchange.getAttribute(GATEWAY_SCHEME_PREFIX_ATTR);
        return url != null && (DubboConstant.DUBBO.equals(url.getScheme()) || DubboConstant.DUBBO.equals(schemePrefix));
    }
}
