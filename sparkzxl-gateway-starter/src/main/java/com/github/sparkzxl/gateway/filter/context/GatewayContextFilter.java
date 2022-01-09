package com.github.sparkzxl.gateway.filter.context;

import com.github.sparkzxl.constant.BaseContextConstants;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.gateway.context.GatewayContext;
import com.github.sparkzxl.gateway.option.FilterOrderEnum;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import com.github.sparkzxl.gateway.properties.LogRequestProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * description: Gateway Context Filter
 *
 * @author zhouxinlei
 */
@Slf4j
@RefreshScope
@AllArgsConstructor
public class GatewayContextFilter implements GlobalFilter, Ordered {

    /**
     * default HttpMessageReader
     */
    private static final List<HttpMessageReader<?>> MESSAGE_READERS = HandlerStrategies.withDefaults().messageReaders();
    private GatewayPluginProperties gatewayPluginProperties;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        // 构建网关请求域信息
        GatewayContext gatewayContext = buildDefaultContext(exchange);
        boolean readRequestData = shouldReadRequestData();
        HttpHeaders headers = request.getHeaders();
        gatewayContext.setRequestHeaders(headers);
        if (!readRequestData) {
            exchange.getAttributes().put(GatewayContext.GATEWAY_CONTEXT_CONSTANT, gatewayContext);
            log.debug("[GatewayContext]Properties Set To Not Read Request Data");
            return chain.filter(exchange);
        }
        exchange.getAttributes().put(GatewayContext.GATEWAY_CONTEXT_CONSTANT, gatewayContext);
        String contentType = headers.getFirst(HttpHeaders.CONTENT_TYPE);
        if (headers.getContentLength() > 0) {
            if (StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)
                    || StringUtils.startsWithIgnoreCase(contentType, MediaType.MULTIPART_FORM_DATA_VALUE)) {
                return readBody(exchange, chain, gatewayContext);
            }
            if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)) {
                return readFormData(exchange, chain, gatewayContext);
            }
        }
        log.debug("[GatewayContext]ContentType:{},Gateway context is set with {}", contentType, gatewayContext);
        return chain.filter(exchange);

    }


    @Override
    public int getOrder() {
        return FilterOrderEnum.GATEWAY_CONTEXT_FILTER.getOrder();
    }

    /**
     * check should read request data whether or not
     *
     * @return boolean
     */
    private boolean shouldReadRequestData() {
        LogRequestProperties logging = gatewayPluginProperties.getLogging();
        if (logging.isReadRequestData()) {
            log.debug("[GatewayContext]Properties Set Read All Request Data");
            return true;
        }
        return false;
    }

    /**
     * ReadFormData
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> readFormData(ServerWebExchange exchange, GatewayFilterChain chain, GatewayContext gatewayContext) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        return exchange.getFormData()
                .doOnNext(multiValueMap -> {
                    gatewayContext.setFormData(multiValueMap);
                    log.debug("[GatewayContext]Read FormData Success");
                })
                .then(Mono.defer(() -> {
                    Charset charset = headers.getContentType().getCharset();
                    charset = charset == null ? StandardCharsets.UTF_8 : charset;
                    String charsetName = charset.name();
                    MultiValueMap<String, String> formData = gatewayContext.getFormData();
                    /*
                     * formData is empty just return
                     */
                    if (null == formData || formData.isEmpty()) {
                        return chain.filter(exchange);
                    }
                    StringBuilder formDataBodyBuilder = new StringBuilder();
                    String entryKey;
                    List<String> entryValue;
                    try {
                        /*
                         * repackage form data
                         */
                        for (Map.Entry<String, List<String>> entry : formData.entrySet()) {
                            entryKey = entry.getKey();
                            entryValue = entry.getValue();
                            if (entryValue.size() > 1) {
                                for (String value : entryValue) {
                                    formDataBodyBuilder.append(entryKey).append("=").append(URLEncoder.encode(value, charsetName)).append("&");
                                }
                            } else {
                                formDataBodyBuilder.append(entryKey).append("=").append(URLEncoder.encode(entryValue.get(0), charsetName)).append("&");
                            }
                        }
                    } catch (UnsupportedEncodingException e) {
                    }
                    /*
                     * substring with the last char '&'
                     */
                    String formDataBodyString = "";
                    if (formDataBodyBuilder.length() > 0) {
                        formDataBodyString = formDataBodyBuilder.substring(0, formDataBodyBuilder.length() - 1);
                    }
                    /*
                     * get data bytes
                     */
                    byte[] bodyBytes = formDataBodyString.getBytes(charset);
                    int contentLength = bodyBytes.length;
                    HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.putAll(exchange.getRequest().getHeaders());
                    httpHeaders.remove(HttpHeaders.CONTENT_LENGTH);
                    /*
                     * in case of content-length not matched
                     */
                    httpHeaders.setContentLength(contentLength);
                    /*
                     * use BodyInserter to InsertFormData Body
                     */
                    BodyInserter<String, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromObject(formDataBodyString);
                    CachedBodyOutputMessage cachedBodyOutputMessage = new CachedBodyOutputMessage(exchange, httpHeaders);
                    log.debug("[GatewayContext]Rewrite Form Data :{}", formDataBodyString);
                    return bodyInserter.insert(cachedBodyOutputMessage, new BodyInserterContext())
                            .then(Mono.defer(() -> {
                                ServerHttpRequestDecorator decorator = new ServerHttpRequestDecorator(
                                        exchange.getRequest()) {
                                    @Override
                                    public HttpHeaders getHeaders() {
                                        return httpHeaders;
                                    }

                                    @Override
                                    public Flux<DataBuffer> getBody() {
                                        return cachedBodyOutputMessage.getBody();
                                    }
                                };
                                return chain.filter(exchange.mutate().request(decorator).build());
                            }));
                }));
    }

    /**
     * ReadJsonBody
     *
     * @param exchange
     * @param chain
     * @return
     */
    private Mono<Void> readBody(ServerWebExchange exchange, GatewayFilterChain chain, GatewayContext gatewayContext) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String requestData = new String(bytes, StandardCharsets.UTF_8);
                    gatewayContext.setRequestBody(requestData);
                    log.debug("[GatewayContext]Read JsonBody Success");
                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        DataBufferUtils.retain(buffer);
                        return Mono.just(buffer);
                    });
                    /*
                     * repackage ServerHttpRequest
                     */
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    private GatewayContext buildDefaultContext(final ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        String tenantId = request.getHeaders().getFirst(BaseContextConstants.TENANT_ID);
        String version = request.getHeaders().getFirst(BaseContextConstants.VERSION);
        GatewayContext gatewayContext = new GatewayContext();
        Route route = (Route) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        gatewayContext.setRouteId(route.getId());
        String url = request.getURI().getPath();
        gatewayContext.setUrl(url);
        gatewayContext.setPath(url.replaceFirst(StrPool.SLASH.concat(route.getId()), ""));
        gatewayContext.setRequestDateTime(LocalDateTime.now());
        gatewayContext.setTenantId(tenantId);
        gatewayContext.setVersion(version);
        Optional.ofNullable(request.getMethod()).ifPresent(httpMethod -> gatewayContext.setHttpMethod(httpMethod.name()));
        return gatewayContext;
    }

}