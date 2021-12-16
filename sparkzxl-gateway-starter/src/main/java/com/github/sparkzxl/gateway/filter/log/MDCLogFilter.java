package com.github.sparkzxl.gateway.filter.log;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.gateway.constant.ExchangeAttributeConstant;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import com.github.sparkzxl.gateway.util.ReactorHttpHelper;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: MDC记录filter
 *
 * @author zhouxinlei
 */
public class MDCLogFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Boolean isAuthentication = exchange.getAttribute(ExchangeAttributeConstant.IS_AUTHENTICATION);
        if (ObjectUtils.isNotEmpty(isAuthentication) && isAuthentication) {
            MDC.put(BaseContextConstants.JWT_KEY_USER_ID, ReactorHttpHelper.getHeader(BaseContextConstants.JWT_KEY_USER_ID, exchange.getRequest()));
            MDC.put(BaseContextConstants.JWT_KEY_ACCOUNT, ReactorHttpHelper.getHeader(BaseContextConstants.JWT_KEY_ACCOUNT, exchange.getRequest()));
            MDC.put(BaseContextConstants.JWT_KEY_NAME, ReactorHttpHelper.getHeader(BaseContextConstants.JWT_KEY_NAME, exchange.getRequest()));
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrderEnum.MDC_LOG_FILTER.getOrder();
    }
}
