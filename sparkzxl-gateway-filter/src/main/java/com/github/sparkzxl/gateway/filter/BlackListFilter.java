package com.github.sparkzxl.gateway.filter;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.CharsetUtil;
import com.github.sparkzxl.core.base.result.ApiResponseStatus;
import com.github.sparkzxl.gateway.properties.BlackProperties;
import com.github.sparkzxl.gateway.utils.WebFluxUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: 自定义全局过滤器实现IP访问限制（黑白名单）
 *
 * @author zhouxinlei
 * @date 2021-08-06 09:35:10
 */
@RequiredArgsConstructor
@Slf4j
public class BlackListFilter implements GlobalFilter, Ordered {

    private final BlackProperties blackProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!blackProperties.isEnabled()) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String referer = WebFluxUtils.getHeader(HttpHeaders.REFERER, request);
        String requestHost = request.getURI().getHost();
        log.info("请求host：[{}]", requestHost);
        if (StringUtils.isNotEmpty(referer)) {
            UrlBuilder refererBuilder = UrlBuilder.ofHttp(referer, CharsetUtil.CHARSET_UTF_8);
            log.info("请求referer: [{}]", refererBuilder.getHost());
            // 判断请求域名和referer域名是否相同
            if (!StringUtils.equals(requestHost, refererBuilder.getHost())) {
                boolean match = validateBlacklist(requestHost);
                if (!match) {
                    return WebFluxUtils.errorResponse(response, ApiResponseStatus.REQ_BLACKLIST.getCode(), ApiResponseStatus.REQ_BLACKLIST.getMessage());
                }
            }
        }

        boolean match = validateBlacklist(requestHost);
        if (!match) {
            return WebFluxUtils.errorResponse(response, ApiResponseStatus.REQ_BLACKLIST.getCode(), ApiResponseStatus.REQ_BLACKLIST.getMessage());
        }
        return chain.filter(exchange);
    }


    /**
     * 判断域名是否在白名单中
     *
     * @param host host
     * @return boolean
     */
    public boolean validateBlacklist(String host) {
        boolean match = false;
        // 判断referer域名是否在白名单中
        if (CollectionUtils.isNotEmpty(blackProperties.getBlackList())) {
            for (String black : blackProperties.getBlackList()) {
                if (black.equals(host)) {
                    match = true;
                    break;
                }
            }
        }
        return match;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 1;
    }
}
