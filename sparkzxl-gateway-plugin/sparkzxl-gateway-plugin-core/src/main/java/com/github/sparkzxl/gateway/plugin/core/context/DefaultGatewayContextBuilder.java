package com.github.sparkzxl.gateway.plugin.core.context;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.gateway.common.condition.data.ParameterDataFactory;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.common.constant.ParameterDataConstant;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Optional;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-01-10 12:16:24
 */
public class DefaultGatewayContextBuilder implements GatewayContextBuilder {

    @Override
    public GatewayContext build(ServerWebExchange exchange) {
        return buildDefaultContext(exchange);
    }

    private GatewayContext buildDefaultContext(final ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String appKey = request.getHeaders().getFirst(GatewayConstant.APP_KEY);
        String sign = request.getHeaders().getFirst(GatewayConstant.SIGN);
        String timestamp = request.getHeaders().getFirst(GatewayConstant.TIMESTAMP);
        String tenantId = request.getHeaders().getFirst(BaseContextConstants.TENANT_ID);
        String version = request.getHeaders().getFirst(BaseContextConstants.VERSION);
        Route route = (Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        String host = ParameterDataFactory.builderData(ParameterDataConstant.HOST, "", exchange);
        String ip = ParameterDataFactory.builderData(ParameterDataConstant.IP, "", exchange);
        GatewayContext gatewayContext = new GatewayContext();
        gatewayContext.setRouteId(route.getId());
        String url = request.getURI().getPath();
        gatewayContext.setHost(host);
        gatewayContext.setIp(ip);
        gatewayContext.setUrl(url);
        gatewayContext.setPath(url.replaceFirst(StrPool.PATH_SEPARATOR.concat(route.getId()), ""));
        gatewayContext.setAppKey(appKey);
        gatewayContext.setSign(sign);
        gatewayContext.setTimestamp(timestamp);
        gatewayContext.setTenantId(tenantId);
        gatewayContext.setVersion(version);
        Optional.ofNullable(request.getMethod()).ifPresent(httpMethod -> gatewayContext.setHttpMethod(httpMethod.name()));
        return gatewayContext;
    }

}
