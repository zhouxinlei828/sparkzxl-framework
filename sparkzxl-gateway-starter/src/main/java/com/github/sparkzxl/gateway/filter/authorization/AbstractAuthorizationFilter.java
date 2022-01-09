package com.github.sparkzxl.gateway.filter.authorization;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ExceptionCode;
import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.gateway.constant.ExchangeAttributeConstant;
import com.github.sparkzxl.gateway.constant.enums.FilterOrderEnum;
import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.properties.GatewayResourceProperties;
import com.github.sparkzxl.gateway.support.GatewayException;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.Map;

/**
 * description: 通用鉴权抽象过滤器
 *
 * @author zhouxinlei
 */
@Slf4j
public abstract class AbstractAuthorizationFilter implements GlobalFilter, Ordered {

    @Resource
    private GatewayResourceProperties gatewayResourceProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.GATEWAY_CONTEXT_CONSTANT);
        assert gatewayContext != null;
        String tenantId = ReactorHttpHelper.getHeader(BaseContextConstants.TENANT_ID, request);
        MDC.put(BaseContextConstants.TENANT_ID, String.valueOf(tenantId));
        log.info("请求租户id：{}，版本：{}，请求路由：{}，请求接口：{}", tenantId, gatewayContext.getVersion(), gatewayContext.getRouteId(), gatewayContext.getUrl());
        // 请求放行后置操作
        if (gatewayResourceProperties.match(gatewayContext.getUrl())) {
            // 放行请求清除token
            ignoreCheckAfterCompletion(exchange);
            return chain.filter(exchange);
        }
        // 鉴权
        return authentication(exchange, chain);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.AUTHORIZATION_FILTER.getOrder();
    }

    private Mono<Void> authentication(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = ReactorHttpHelper.getHeader(getHeaderKey(), request);
        // 校验是否存在token
        if (preAuthenticationCheck(exchange)) {
            if (token.startsWith(BaseContextConstants.BASIC_AUTH)) {
                return chain.filter(exchange);
            }
            onAuthSuccess(exchange);
        } else {
            // 鉴权失败，后置操作
            onAuthFail(exchange, chain);
        }
        return chain.filter(exchange);
    }

    /**
     * 请求放行后置操作
     *
     * @param exchange exchange
     */
    protected void ignoreCheckAfterCompletion(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders -> httpHeaders.remove(getHeaderKey())).build();
        exchange.mutate().request(serverHttpRequest).build();
        Map<String, Object> attributeMap = exchange.getAttributes();
        attributeMap.put(ExchangeAttributeConstant.IS_AUTHENTICATION, Boolean.FALSE);
    }

    protected void checkTokenAuthority(ServerWebExchange exchange, String token) throws GatewayException {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(token);
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new GatewayException(ExceptionCode.TOKEN_EXPIRED_ERROR);
        }
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().headers(httpHeaders -> {
            httpHeaders.add(BaseContextConstants.JWT_KEY_USER_ID, ReactorHttpHelper.formatHeader(jwtUserInfo.getId()));
            httpHeaders.add(BaseContextConstants.JWT_KEY_ACCOUNT, ReactorHttpHelper.formatHeader(jwtUserInfo.getUsername()));
            httpHeaders.add(BaseContextConstants.JWT_KEY_NAME, ReactorHttpHelper.formatHeader(jwtUserInfo.getName()));
        }).build();
        exchange.mutate().request(serverHttpRequest).build();
        Map<String, Object> attributeMap = exchange.getAttributes();
        attributeMap.put(ExchangeAttributeConstant.USER_INFO, jwtUserInfo);
        attributeMap.put(ExchangeAttributeConstant.IS_AUTHENTICATION, Boolean.TRUE);
    }

    /**
     * 鉴权前校验
     *
     * @param exchange exchange
     * @return true 通过 ; false 不通过
     */
    protected boolean preAuthenticationCheck(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String token = ReactorHttpHelper.getHeader(getHeaderKey(), request);
        return !StringUtils.isEmpty(token);
    }

    protected void onAuthFail(ServerWebExchange exchange, GatewayFilterChain chain) {
        throw new GatewayException(ExceptionCode.JWT_EMPTY_ERROR);
    }

    /**
     * 鉴权成功操作
     *
     * @param exchange exchange
     */
    protected void onAuthSuccess(ServerWebExchange exchange) throws GatewayException {
        String token = ReactorHttpHelper.getHeader(getHeaderKey(), exchange.getRequest());
        token = StringUtils.removeStartIgnoreCase(token, BaseContextConstants.BEARER_TOKEN);
        checkTokenAuthority(exchange, token);
    }

    /**
     * 获取header
     *
     * @return 返回值
     */
    public abstract String getHeaderKey();

    /**
     * 获取token用户信息
     *
     * @param token token值
     * @return JwtUserInfo
     * @throws GatewayException 异常
     */
    public abstract JwtUserInfo getJwtUserInfo(String token) throws GatewayException;

}
