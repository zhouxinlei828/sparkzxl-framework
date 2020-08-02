package com.sparksys.oauth.resource.filter;

import com.sparksys.core.constant.BaseContextConstants;
import com.sparksys.oauth.resource.properties.ResourceProperties;
import com.sparksys.oauth.resource.utils.WebFluxUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * description: 白名单路径访问时需要移除JWT请求头
 *
 * @author: zhouxinlei
 * @date: 2020-08-02 18:28:31
 */
public class IgnoreUrlsRemoveJwtFilter implements WebFilter {

    private final ResourceProperties resourceProperties;

    public IgnoreUrlsRemoveJwtFilter(ResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String header = WebFluxUtils.getHeader(BaseContextConstants.JWT_TOKEN_HEADER, request);
        if (header.startsWith("Basic")) {
            return chain.filter(exchange);
        }
        URI uri = request.getURI();
        PathMatcher pathMatcher = new AntPathMatcher();
        //白名单路径移除JWT请求头
        String[] ignoreUrls = resourceProperties.getIgnorePatterns();
        if (ArrayUtils.isNotEmpty(ignoreUrls)) {
            for (String ignoreUrl : ignoreUrls) {
                if (pathMatcher.match(ignoreUrl, uri.getPath())) {
                    request = exchange.getRequest().mutate().header("Authorization", "").build();
                    exchange = exchange.mutate().request(request).build();
                    return chain.filter(exchange);
                }
            }
        }
        return chain.filter(exchange);
    }
}
