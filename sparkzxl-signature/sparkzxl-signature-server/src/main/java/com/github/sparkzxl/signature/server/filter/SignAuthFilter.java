package com.github.sparkzxl.signature.server.filter;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.signature.server.cache.SignCache;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.support.ArgumentException;
import com.github.sparkzxl.signature.constant.SignatureConstant;
import com.github.sparkzxl.signature.executor.SignatureExecutor;
import com.github.sparkzxl.signature.executor.SignatureExecutorContext;
import com.github.sparkzxl.signature.properties.SignatureProperties;
import com.github.sparkzxl.signature.server.properties.SignatureServerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * description: 验签过滤器
 *
 * @author zhouxinlei
 * @since 2024-05-21 11:03:48
 */
public class SignAuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private SignatureExecutorContext signatureExecutorContext;
    @Autowired
    private SignCache signCache;
    @Autowired
    private SignatureProperties signatureProperties;
    @Autowired
    private SignatureServerProperties signatureServerProperties;

    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        List<String> excludePatterns = signatureServerProperties.getExcludePatterns();
        boolean matched = excludePatterns.stream().anyMatch(url -> ANT_PATH_MATCHER.match(url, path)
                || ANT_PATH_MATCHER.matchStart(url, path));
        if (matched) {
            return chain.filter(exchange);
        }
        List<String> includePatterns = signatureServerProperties.getIncludePatterns();
        boolean includeMatched = includePatterns.stream().anyMatch(url -> ANT_PATH_MATCHER.match(url, path)
                || ANT_PATH_MATCHER.matchStart(url, path));
        if (!includeMatched) {
            return chain.filter(exchange);
        }
        ServerHttpRequest request = exchange.getRequest();
        String contentType = request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
        // 获取时间戳
        String appKey = request.getHeaders().getFirst(SignatureConstant.APP_KEY);
        // 获取时间戳
        String timestamp = request.getHeaders().getFirst(SignatureConstant.TIMESTAMP);
        // 获取随机字符串
        String nonce = request.getHeaders().getFirst(SignatureConstant.NONCE);
        // 获取签名
        String signature = request.getHeaders().getFirst(SignatureConstant.SIGNATURE);
        if (StrUtil.isEmpty(appKey)) {
            throw new ArgumentException("invalid appKey");
        }

        // 判断时间是否大于xx秒(防止重放攻击)
        if (StrUtil.isEmpty(timestamp) || DateUtil.between(DateUtil.date(Long.parseLong(timestamp) * 1000), DateUtil.date(), DateUnit.SECOND) > signatureServerProperties.getNonceTimeoutSeconds()) {
            throw new ArgumentException("invalid  timestamp");
        }

        // 判断该用户的nonce参数是否已经在redis中（防止短时间内的重放攻击）
        boolean haveNonce = signCache.containsKey(nonce);
        if (StrUtil.isEmpty(nonce) || haveNonce) {
            throw new ArgumentException("invalid nonce");
        }

        // 对请求头参数进行签名
        if (StrUtil.isEmpty(signature)) {
            throw new ArgumentException("验签失败");
        }

        if (StringUtils.startsWithIgnoreCase(contentType, MediaType.APPLICATION_JSON_VALUE)
                || StringUtils.startsWithIgnoreCase(contentType, MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return readBody(signature, appKey, timestamp, nonce, exchange, chain);
        }
        if (MediaType.APPLICATION_FORM_URLENCODED_VALUE.equals(contentType)) {
            return readFormData(signature, appKey, timestamp, nonce, exchange, chain);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> readFormData(String signature, String appKey, String timestamp, String nonce, ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        return exchange.getFormData()
                .doOnNext(x -> {
                    if (MapUtil.isNotEmpty(x)) {
                        formData.putAll(x);
                    }
                })
                .then(Mono.defer(() -> {
                    Charset charset = Objects.requireNonNull(headers.getContentType()).getCharset();
                    charset = charset == null ? StandardCharsets.UTF_8 : charset;
                    String charsetName = charset.name();
                    /*
                     * formData is empty just return
                     */
                    if (formData.isEmpty()) {
                        return chain.filter(exchange);
                    }
                    StringBuilder formDataBodyBuilder = new StringBuilder();
                    Map<String, Object> formDataBodyMap = Maps.newConcurrentMap();
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
                                    formDataBodyBuilder.append(entryKey).append("=").append(URLEncoder.encode(value, charsetName))
                                            .append("&");
                                }
                            } else {
                                formDataBodyBuilder.append(entryKey).append("=").append(URLEncoder.encode(entryValue.get(0), charsetName))
                                        .append("&");
                            }
                            formDataBodyMap.put(entryKey, entryValue.get(0));
                        }
                    } catch (UnsupportedEncodingException ignored) {
                    }
                    /*
                     * substring with the last char '&'
                     */
                    String formDataBodyString = "";
                    if (formDataBodyBuilder.length() > 0) {
                        formDataBodyString = formDataBodyBuilder.substring(0, formDataBodyBuilder.length() - 1);
                    }
                    boolean verified = this.verifySignature(signature, appKey, timestamp, nonce, formDataBodyMap);
                    if (!verified) {
                        return Mono.error(new ArgumentException("验签失败"));
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
                    BodyInserter<String, ReactiveHttpOutputMessage> bodyInserter = BodyInserters.fromValue(formDataBodyString);
                    CachedBodyOutputMessage cachedBodyOutputMessage = new CachedBodyOutputMessage(exchange, httpHeaders);
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
     * @param exchange exchange
     * @param chain    chain
     * @return Mono<Void>
     */
    private Mono<Void> readBody(String signature, String appKey, String timestamp, String nonce, ServerWebExchange exchange, GatewayFilterChain chain) {
        return DataBufferUtils.join(exchange.getRequest().getBody())
                .flatMap(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    DataBufferUtils.release(dataBuffer);
                    String requestData = new String(bytes, StandardCharsets.UTF_8);
                    Map<String, Object> requestBodyMap = JsonUtils.getJson().toMap(requestData);
                    boolean verified = this.verifySignature(signature, appKey, timestamp, nonce, requestBodyMap);
                    if (!verified) {
                        return Mono.error(new ArgumentException("验签失败"));
                    }
                    Flux<DataBuffer> cachedFlux = Flux.defer(() -> {
                        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
                        DataBufferUtils.retain(buffer);
                        return Mono.just(buffer);
                    });
                    ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
                        @Override
                        public Flux<DataBuffer> getBody() {
                            return cachedFlux;
                        }
                    };
                    // 将本次用户请求的nonceStr参数存到redis中设置xx秒后自动删除
                    signCache.set(nonce, nonce, signatureServerProperties.getNonceTimeoutSeconds());
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    /**
     * 校验签名是否一致
     *
     * @param signature 请求的sign
     * @param appKey    应用ID
     * @param timestamp 时间戳
     * @param nonce     随机值
     * @param params    请求参数
     * @return boolean
     */
    private boolean verifySignature(String signature, String appKey, String timestamp, String nonce, Map<String, Object> params) {
        Map<String, SignatureProperties.AppProperties> provider = signatureProperties.getProvider();
        SignatureProperties.AppProperties appProperties = provider.get(appKey);
        ArgumentAssert.notNull(appProperties, "应用程序ID[{}]签名配置不存在", appKey);
        SignatureExecutor signatureExecutor = signatureExecutorContext.getExecutor(appProperties.getSignType().name());
        return signatureExecutor.verify(appKey, Long.valueOf(timestamp), nonce, signature, params);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
