package com.github.sparkzxl.oauth.filter;

import com.github.sparkzxl.core.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.ListUtils;
import com.github.sparkzxl.core.util.PathMatchUtils;
import com.github.sparkzxl.oauth.properties.ResourceProperties;
import com.github.sparkzxl.oauth.util.WebFluxUtils;
import java.net.URI;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * description: 白名单路径访问时需要移除JWT请求头
 *
 * @author zhouxinlei
 */
public class IgnoreUrlsRemoveJwtFilter implements WebFilter {

    private final ResourceProperties resourceProperties;

    public IgnoreUrlsRemoveJwtFilter(ResourceProperties resourceProperties) {
        this.resourceProperties = resourceProperties;
    }

    @Nonnull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, @Nonnull WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String header = WebFluxUtils.getHeader(BaseContextConstants.JWT_TOKEN_HEADER, request);
        if (StringUtils.isNotEmpty(header)) {
            if (header.startsWith(BaseContextConstants.BASIC_AUTH)) {
                return chain.filter(exchange);
            }
            URI uri = request.getURI();
            //白名单路径移除JWT请求头
            String[] ignorePatterns = resourceProperties.getIgnore();
            List<String> ignoreUrls = ListUtils.arrayToList(ignorePatterns);
            boolean match = PathMatchUtils.matchUrl(ignoreUrls, uri.getPath());
            if (match) {
                request = exchange.getRequest().mutate().header(BaseContextConstants.JWT_TOKEN_HEADER, "").build();
                exchange = exchange.mutate().request(request).build();
            }
        }
        return chain.filter(exchange);
    }
}
