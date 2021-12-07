package com.github.sparkzxl.gateway.filter.authorization;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.base.result.ResponseInfoStatus;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.StringHandlerUtil;
import com.github.sparkzxl.core.util.SwaggerStaticResource;
import com.github.sparkzxl.entity.core.JwtUserInfo;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import com.github.sparkzxl.gateway.support.GatewayException;
import com.github.sparkzxl.gateway.util.WebFluxUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * description: JWT授权校验管理过滤器
 *
 * @author zhouxinlei
 */
@Slf4j
public abstract class AbstractAuthorizationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = request.mutate();
        String requestUrl = request.getPath().toString();
        String tenantId = WebFluxUtils.getHeader(BaseContextConstants.TENANT_ID, request);
        MDC.put(BaseContextConstants.TENANT_ID, String.valueOf(tenantId));
        log.info("请求租户id：[{}]，请求接口：[{}]", tenantId, requestUrl);
        String token = WebFluxUtils.getHeader(getHeaderKey(), request);
        RequestLocalContextHolder.setVersion(WebFluxUtils.getHeader(BaseContextConstants.VERSION, request));
        // 校验是否需要拦截地址
        if (StringHandlerUtil.matchUrl(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS, request.getPath().toString())
                || StringHandlerUtil.matchUrl(ignorePatterns(), request.getPath().toString())) {
            // 放行请求清除token
            clearTokenRequest(exchange);
            return chain.filter(exchange);
        }

        if (StringUtils.isEmpty(token)) {
            return handleTokenEmpty(exchange, chain, token);
        } else {
            if (token.startsWith(BaseContextConstants.BASIC_AUTH)) {
                return chain.filter(exchange);
            }
            token = StringUtils.removeStartIgnoreCase(token, BaseContextConstants.BEARER_TOKEN);
            try {
                JwtUserInfo jwtUserInfo = checkTokenAuthority(token, exchange);
                if (jwtUserInfo != null) {
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_ACCOUNT, jwtUserInfo.getUsername());
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_USER_ID, jwtUserInfo.getId());
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_NAME, jwtUserInfo.getName());
                    MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(jwtUserInfo.getId()));
                }
                ServerHttpRequest serverHttpRequest = mutate.build();
                exchange = exchange.mutate().request(serverHttpRequest).build();
            } catch (GatewayException e) {
                log.error("网关鉴权发生异常：[{}]", ExceptionUtil.getMessage(e));
                return WebFluxUtils.errorResponse(response, e.getCode(), e.getMessage());
            }
        }
        return chain.filter(exchange.mutate().request(request.mutate().build()).build());
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.AUTHORIZATION_FILTER.getOrder();
    }

    /**
     * 获取header
     *
     * @return 返回值
     */
    public abstract String getHeaderKey();

    /**
     * 放行地址集合
     *
     * @return List<String>
     */
    protected List<String> ignorePatterns() {
        return Lists.newArrayList();
    }

    protected Mono<Void> handleTokenEmpty(ServerWebExchange exchange, GatewayFilterChain chain, String token) {
        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isEmpty(token)) {
            return WebFluxUtils.errorResponse(response, ResponseInfoStatus.JWT_EMPTY_ERROR.getCode(), ResponseInfoStatus.JWT_EMPTY_ERROR.getMessage());
        }
        return null;
    }

    protected void clearTokenRequest(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().header(BaseContextConstants.JWT_TOKEN_HEADER, "").build();
        exchange.mutate().request(serverHttpRequest).build();
    }

    protected JwtUserInfo checkTokenAuthority(String token, ServerWebExchange exchange) throws GatewayException {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(token);
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new GatewayException(ResponseInfoStatus.TOKEN_EXPIRED_ERROR);
        }
        return jwtUserInfo;
    }

    /**
     * 获取token用户信息
     *
     * @param token token值
     * @return JwtUserInfo
     * @throws GatewayException 异常
     */
    public abstract JwtUserInfo getJwtUserInfo(String token) throws GatewayException;

}