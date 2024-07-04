package com.github.sparkzxl.gateway.plugin.forwardauth;

import cn.hutool.core.lang.TypeReference;
import com.github.sparkzxl.core.base.result.R;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.ArgumentException;
import com.github.sparkzxl.core.support.code.ExceptionErrorCode;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.core.filter.AbstractGlobalFilter;
import com.github.sparkzxl.gateway.support.AuthenticationException;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * description: forward-auth 插件使用的是经典外部认证。当身份认证失败时，可以实现自定义错误或者重定向到认证页面的场景。
 *
 * @author zhouxinlei
 * @since 2024-06-28 11:13:34
 */
@Slf4j
public class ForwardAuthFilter extends AbstractGlobalFilter {

    private final WebClient webClient;

    public ForwardAuthFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        HttpClient httpClient = HttpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) Duration.ofMillis(500).toMillis())
                .doOnConnected(connection ->
                        connection
                                .addHandlerLast(new ReadTimeoutHandler((int) Duration.ofSeconds(15).getSeconds()))
                                .addHandlerLast(new WriteTimeoutHandler((int) Duration.ofSeconds(15).getSeconds()))
                );
        webClient = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                //最大 body 占用 16m 内存
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                //负载均衡器，改写url
                .filter(lbFunction)
                .build();
    }

    @Override
    public String named() {
        return FilterEnum.FORWARD_AUTH.getName();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        log.info("开始检查用户token有效性：{}", path);
        FilterData filterData = loadFilterData();
        boolean needSkip = (boolean) exchange.getAttributes().get(GatewayConstant.NEED_SKIP);
        if (needSkip) {
            log.info("认证权限白名单：{}", path);
            return chain.filter(exchange);
        }
        ForwardAuthConfig forwardAuthConfig = JsonUtils.getJson().toJavaObject(filterData.getConfig(), ForwardAuthConfig.class);
        String requestMethod = forwardAuthConfig.getRequestMethod();
        HttpMethod httpMethod = HttpMethod.resolve(requestMethod);
        if (Objects.requireNonNull(httpMethod) == HttpMethod.GET) {
            Mono<ClientResponse> responseMono = webClient.get().uri(forwardAuthConfig.getUri()).headers((header) -> {
                List<String> requestHeaders = forwardAuthConfig.getRequestHeaders();
                if (CollectionUtils.isNotEmpty(requestHeaders)) {
                    for (String headerKey : requestHeaders) {
                        String headerVal = exchange.getRequest().getHeaders().getFirst(headerKey);
                        header.add(headerKey, headerVal);
                    }
                }
            }).exchange();
            return responseMono.flatMap((response) -> {
                HttpStatus httpStatus = response.statusCode();
                if (httpStatus == HttpStatus.OK) {
                    return response.bodyToMono(String.class)
                            .flatMap(body -> {
                                R<Boolean> resultData = JsonUtils.getJson().toJavaObject(body, new TypeReference<R<Boolean>>() {
                                });
                                int code = resultData.getCode();
                                if (code == HttpStatus.UNAUTHORIZED.value()) {
                                    return Mono.error(() -> new AuthenticationException(ExceptionErrorCode.LOGIN_EXPIRE));
                                }
                                return chain.filter(exchange);
                            });
                }
                return Mono.error(() -> new NotFoundException("【认证服务】服务不可用，请联系管理员！"));
            });
        } else {
            throw new ArgumentException("请求方法不支持");
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
