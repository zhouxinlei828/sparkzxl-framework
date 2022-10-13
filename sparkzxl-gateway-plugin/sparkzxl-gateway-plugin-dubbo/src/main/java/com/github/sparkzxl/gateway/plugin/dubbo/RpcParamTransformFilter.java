package com.github.sparkzxl.gateway.plugin.dubbo;

import com.github.sparkzxl.core.util.HttpParamConverter;
import com.github.sparkzxl.gateway.plugin.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.plugin.common.constant.RpcConstant;
import com.github.sparkzxl.gateway.plugin.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.plugin.common.utils.BodyParamUtils;
import com.github.sparkzxl.gateway.plugin.context.GatewayContext;
import com.github.sparkzxl.gateway.plugin.filter.AbstractGlobalFilter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * description: Rpc参数转换过滤器
 *
 * @author zhouxinlei
 * @since 2022-08-12 17:12:57
 */
public class RpcParamTransformFilter extends AbstractGlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        GatewayContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_CONTEXT_CONSTANT);
        if (ObjectUtils.isNotEmpty(gatewayContext)) {
            MediaType mediaType = request.getHeaders().getContentType();
            if (MediaType.APPLICATION_JSON.isCompatibleWith(mediaType)) {
                return body(exchange, request, chain);
            }
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(mediaType)) {
                return formData(exchange, request, chain);
            }
            return query(exchange, request, chain);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> body(final ServerWebExchange exchange, final ServerHttpRequest serverHttpRequest, final GatewayFilterChain chain) {
        return Mono.from(DataBufferUtils.join(serverHttpRequest.getBody())
                .flatMap(data -> Mono.just(Optional.of(data)))
                .defaultIfEmpty(Optional.empty())
                .flatMap(body -> {
                    body.ifPresent(dataBuffer -> exchange.getAttributes().put(RpcConstant.PARAM_TRANSFORM, resolveBodyFromRequest(dataBuffer)));
                    return chain.filter(exchange);
                }));
    }

    private Mono<Void> formData(final ServerWebExchange exchange, final ServerHttpRequest serverHttpRequest, final GatewayFilterChain chain) {
        return Mono.from(DataBufferUtils.join(serverHttpRequest.getBody())
                .flatMap(data -> Mono.just(Optional.of(data)))
                .defaultIfEmpty(Optional.empty())
                .flatMap(map -> {
                    if (map.isPresent()) {
                        String param = resolveBodyFromRequest(map.get());
                        LinkedMultiValueMap<String, String> linkedMultiValueMap;
                        try {
                            linkedMultiValueMap = BodyParamUtils.buildBodyParams(URLDecoder.decode(param, StandardCharsets.UTF_8.name()));
                        } catch (UnsupportedEncodingException e) {
                            return Mono.error(e);
                        }
                        exchange.getAttributes().put(RpcConstant.PARAM_TRANSFORM, HttpParamConverter.toMap(() -> linkedMultiValueMap));
                    }
                    return chain.filter(exchange);
                }));
    }

    private Mono<Void> query(final ServerWebExchange exchange, final ServerHttpRequest serverHttpRequest, final GatewayFilterChain chain) {
        exchange.getAttributes().put(RpcConstant.PARAM_TRANSFORM, HttpParamConverter.ofString(() -> serverHttpRequest.getURI().getQuery()));
        return chain.filter(exchange);
    }

    @NonNull
    private String resolveBodyFromRequest(final DataBuffer dataBuffer) {
        byte[] bytes = new byte[dataBuffer.readableByteCount()];
        dataBuffer.read(bytes);
        DataBufferUtils.release(dataBuffer);
        return new String(bytes, StandardCharsets.UTF_8);
    }


    @Override
    public String named() {
        return FilterEnum.RPC_PARAM_TRANSFORM.getName();
    }

    @Override
    public int getOrder() {
        return FilterEnum.RPC_PARAM_TRANSFORM.getCode();
    }
}
