package com.github.sparkzxl.gateway.filter.log;

import cn.hutool.core.text.StrFormatter;
import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.util.HttpRequestUtils;
import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.util.RequestIpUtil;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.BaseSubscriber;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * description: 日志订阅
 *
 * @author zhouxinlei
 */
@Slf4j
public class LogBaseSubscriber extends BaseSubscriber {

    private Subscriber<Object> actual;

    private ServerWebExchange exchange;

    public LogBaseSubscriber(Subscriber actual, ServerWebExchange exchange) {
        this.actual = actual;
        this.exchange = exchange;
    }

    static void recordLog(ServerWebExchange exchange) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayContext.CACHE_GATEWAY_CONTEXT);
        LogParam logParam = buildLogParam(exchange, gatewayContext);
        if (gatewayContext.isLogRequest()) {
            log.info("请求日志：IP:{},host:{},httpMethod:{},path:{},timeCost:{}",
                    logParam.getIp(),
                    logParam.getHost(),
                    logParam.getHttpMethod(),
                    logParam.getPath(),
                    logParam.getTimeCost()
            );
        }
        if (gatewayContext.isReadRequestData()) {
            if (StringUtils.isNotBlank(logParam.getQueryParams())) {
                log.info("请求参数：queryParams:{}", logParam.getQueryParams());
            }
            if (StringUtils.isNotBlank(logParam.getReqFormData())) {
                log.info("请求参数：requestFormData:{}", logParam.getReqFormData());
            }
            if (StringUtils.isNotBlank(logParam.getReqBody())) {
                log.info("请求参数：requestBody:{}", logParam.getReqBody());
            }
        }
        if (gatewayContext.isReadResponseData()) {
            log.info("请求结果：responseBody:{}", logParam.getRespBody());
        }
    }

    static LogParam buildLogParam(ServerWebExchange exchange, GatewayContext gatewayContext) {
        LogParam logParam = new LogParam();
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        LocalDateTime requestDateTime = gatewayContext.getRequestDateTime();
        ServerHttpRequest request = exchange.getRequest();
        URI uri = request.getURI();
        HttpHeaders headers = request.getHeaders();
        String username = HttpRequestUtils.urlDecode(ReactorHttpHelper.getHeader(BaseContextConstants.JWT_KEY_NAME, request));
        logParam.setIp(RequestIpUtil.getIp(request))
                .setUsername(username)
                .setHttpMethod(request.getMethod())
                .setHttpStatus(exchange.getResponse().getStatusCode().value())
                .setPath(uri.getPath())
                .setHost(headers.getFirst(HttpHeaders.HOST))
                .setRouteId(route.getId())
                .setRouterToUri(route.getUri().toString())
                .setReqTime(requestDateTime)
                .setReqBody(gatewayContext.getRequestBody())
                .setQueryParams(JsonUtil.toJson(gatewayContext.getAllRequestData()))
                .setReqFormData(JsonUtil.toJson(gatewayContext.getFormData()))
                .setTimeCost(StrFormatter.format("{}ms", Duration.between(logParam.getReqTime(), LocalDateTime.now()).toMillis()))
                .setRespBody(Optional.ofNullable(gatewayContext.getResponseBody()).orElse(StringUtils.EMPTY));
        return logParam;
    }

    @Override
    protected void hookOnNext(Object value) {
        actual.onNext(value);
    }

    @Override
    protected void hookOnComplete() {
        try {
            recordLog(exchange);
        } finally {
            actual.onComplete();
        }
    }

    @Override
    protected void hookOnSubscribe(Subscription subscription) {
        actual.onSubscribe(this);
    }

    @Override
    protected void hookOnError(Throwable t) {
        try {
            recordLog(exchange);
        } finally {
            actual.onError(t);
        }
    }
}
