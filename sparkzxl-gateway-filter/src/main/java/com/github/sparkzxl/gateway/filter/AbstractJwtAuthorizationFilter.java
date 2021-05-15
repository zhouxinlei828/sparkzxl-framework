package com.github.sparkzxl.gateway.filter;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.core.base.result.ApiResult;
import com.github.sparkzxl.core.context.BaseContextConstants;
import com.github.sparkzxl.core.entity.JwtUserInfo;
import com.github.sparkzxl.core.resource.SwaggerStaticResource;
import com.github.sparkzxl.core.support.BaseException;
import com.github.sparkzxl.core.support.JwtExpireException;
import com.github.sparkzxl.core.utils.StringHandlerUtils;
import com.github.sparkzxl.gateway.utils.WebFluxUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * description: JWT授权校验管理过滤器
 *
 * @author zhouxinlei
 */
@Slf4j
public abstract class AbstractJwtAuthorizationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        ServerHttpRequest.Builder mutate = request.mutate();
        String requestUrl = request.getPath().toString();
        log.info("请求路径：[{}]", requestUrl);
        String token = WebFluxUtils.getHeader(getHeaderKey(), request);
        // 校验是否需要拦截地址
        if (StringHandlerUtils.isIgnore(SwaggerStaticResource.EXCLUDE_STATIC_PATTERNS, request.getPath().toString())
                || StringHandlerUtils.isIgnore(ignorePatterns(), request.getPath().toString())) {
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
                JwtUserInfo jwtUserInfo = verifyToken(token);
                if (jwtUserInfo != null) {
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_ACCOUNT, jwtUserInfo.getUsername());
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_USER_ID, jwtUserInfo.getId());
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_NAME, jwtUserInfo.getName());

                    String realm = WebFluxUtils.getHeader(BaseContextConstants.JWT_KEY_REALM, request);
                    final String realmCode = StringUtils.isEmpty(realm) ? jwtUserInfo.getRealm() : realm;
                    WebFluxUtils.addHeader(mutate, BaseContextConstants.JWT_KEY_REALM, realmCode);
                    MDC.put(BaseContextConstants.JWT_KEY_USER_ID, String.valueOf(jwtUserInfo.getId()));
                    MDC.put(BaseContextConstants.JWT_KEY_REALM, String.valueOf(realmCode));
                }
                ServerHttpRequest serverHttpRequest = mutate.build();
                exchange = exchange.mutate().request(serverHttpRequest).build();
            } catch (BaseException e) {
                log.error("jwt 获取用户发生异常：[{}]", ExceptionUtil.getMessage(e));
                return errorResponse(response, e.getCode(), e.getMessage());
            }
        }
        return chain.filter(exchange.mutate().request(request.mutate().build()).build());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 2;
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
            return errorResponse(response, ApiResponseStatus.JWT_EMPTY_ERROR.getCode(), ApiResponseStatus.JWT_EMPTY_ERROR.getMessage());
        }
        return null;
    }

    protected void clearTokenRequest(ServerWebExchange exchange) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest().mutate().header(BaseContextConstants.JWT_TOKEN_HEADER, "").build();
        exchange.mutate().request(serverHttpRequest).build();
    }

    protected JwtUserInfo verifyToken(String token) throws BaseException {
        JwtUserInfo jwtUserInfo = getJwtUserInfo(token);
        if (jwtUserInfo.getExpire().getTime() < System.currentTimeMillis()) {
            throw new JwtExpireException(ApiResponseStatus.JWT_EXPIRED_ERROR);
        }
        return jwtUserInfo;
    }

    /**
     * 获取token用户信息
     *
     * @param token token值
     * @return JwtUserInfo
     * @throws BaseException 异常
     */
    public abstract JwtUserInfo getJwtUserInfo(String token) throws BaseException;

    protected Mono<Void> errorResponse(ServerHttpResponse response, int code, String message) {
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
        byte[] bytes = JSON.toJSONString(ApiResult.apiResult(code, message)).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));
    }

}
