package com.github.sparkzxl.gateway.filter;

import com.github.sparkzxl.core.utils.DateUtils;
import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import com.github.sparkzxl.gateway.utils.WebFluxUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

/**
 * description: 请求日志记录
 *
 * @author zhoux
 * @date 2021-10-23 16:54:05
 */
@Slf4j
public class RequestLogFilter implements GlobalFilter, Ordered {

    private static final String START_TIME = "startTime";

    private static final String HTTP_SCHEME = "http";

    private static final String HTTPS_SCHEME = "https";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        URI requestURI = request.getURI();
        String scheme = requestURI.getScheme();
        /*
         * not http or https scheme
         */
        if ((!HTTP_SCHEME.equalsIgnoreCase(scheme) && !HTTPS_SCHEME.equals(scheme))) {
            return chain.filter(exchange);
        }
        logRequest(exchange);
        return chain.filter(exchange).then(Mono.fromRunnable(() -> logResponse(exchange)));
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.REQUEST_LOG_FILTER.getOrder();
    }

    /**
     * log request
     *
     * @param exchange
     */
    private void logRequest(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        URI requestURI = request.getURI();
        String scheme = requestURI.getScheme();
        HttpHeaders headers = request.getHeaders();
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME, startTime);
        log.info("Request Start dateTime:{}", DateUtils.formatDateTime(new Date()));
        log.info("Scheme:{},Path:{},Method:{},IP:{},Host:{}", scheme, requestURI.getPath(), request.getMethod(),
                WebFluxUtils.getIpAddress(request), requestURI.getHost());
        headers.forEach((key, value) -> log.debug("Headers:Key->{},Value->{}", key, value));
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        if (!gatewayContext.isReadRequestData()) {
            log.debug("[RequestLogFilter] Properties Set Not To Read Request Data");
            return;
        }
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if (!queryParams.isEmpty()) {
            queryParams.forEach((key, value) -> log.info("Query Param :Key->({}),Value->({})", key, value));
        }
        MediaType contentType = headers.getContentType();
        long length = headers.getContentLength();
        log.info("ContentType:{},Content Length:{}", contentType, length);
        if (length > 0 && null != contentType && (contentType.includes(MediaType.APPLICATION_JSON)
                || contentType.includes(MediaType.APPLICATION_JSON_UTF8))) {
            log.info("JsonBody:{}", gatewayContext.getRequestBody());
        }
        if (length > 0 && null != contentType && contentType.includes(MediaType.APPLICATION_FORM_URLENCODED)) {
            log.info("FormData:{}", gatewayContext.getFormData());
        }
    }

    /**
     * log response exclude response body
     *
     * @param exchange 请求
     */
    private void logResponse(ServerWebExchange exchange) {
        Long startTime = exchange.getAttribute(START_TIME);
        Long executeTime = (System.currentTimeMillis() - startTime);
        ServerHttpResponse response = exchange.getResponse();
        log.info("HttpStatus:{}", response.getStatusCode());
        HttpHeaders headers = response.getHeaders();
        headers.forEach((key, value) -> log.debug("Headers:Key->{},Value->{}", key, value));
        MediaType contentType = headers.getContentType();
        long length = headers.getContentLength();
        log.info("ContentType:{},Content Length:{}", contentType, length);
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        if (gatewayContext.isReadResponseData()) {
            log.info("Response Body:{}", gatewayContext.getResponseBody());
        }
        log.info("Original Path:{},Cost:{} ms", exchange.getRequest().getURI().getPath(), executeTime);
    }

}
