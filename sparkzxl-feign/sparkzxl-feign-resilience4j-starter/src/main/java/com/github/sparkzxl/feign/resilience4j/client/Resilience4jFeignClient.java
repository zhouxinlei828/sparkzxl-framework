package com.github.sparkzxl.feign.resilience4j.client;

import com.alibaba.fastjson.JSON;
import com.github.sparkzxl.core.util.ArgumentAssert;
import com.github.sparkzxl.feign.resilience4j.Resilience4jUtil;
import com.github.sparkzxl.feign.resilience4j.enums.RetryableHttpStatus;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.okhttp.OkHttpClient;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.ThreadPoolBulkhead;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadConfig;
import io.github.resilience4j.bulkhead.ThreadPoolBulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.annotation.AnnotationUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * description: 粘合断路器，线程隔离的核心代码,同时也记录了负载均衡的实际调用数据
 *
 * @author zhouxinlei
 * @since 2022-04-04 11:09:22
 */
@Slf4j
public class Resilience4jFeignClient implements Client {

    private final OkHttpClient okHttpClient;
    private final ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry;
    private final ConcurrentHashMap<String, ThreadPoolBulkheadConfig> threadPoolBulkheadConfigs = new ConcurrentHashMap<>();
    private final Function<String, ThreadPoolBulkheadConfig> defaultThreadPoolBulkheadConfig;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final ConcurrentHashMap<String, CircuitBreakerConfig> circuitBreakerConfigConfigs = new ConcurrentHashMap<>();
    private final Function<String, CircuitBreakerConfig> defaultCircuitBreakerConfig;

    public Resilience4jFeignClient(OkHttpClient okHttpClient,
                                   ThreadPoolBulkheadRegistry threadPoolBulkheadRegistry,
                                   CircuitBreakerRegistry circuitBreakerRegistry) {
        this.okHttpClient = okHttpClient;
        this.threadPoolBulkheadRegistry = threadPoolBulkheadRegistry;
        this.defaultThreadPoolBulkheadConfig = id -> threadPoolBulkheadRegistry.getDefaultConfig();
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.defaultCircuitBreakerConfig = id -> circuitBreakerRegistry.getDefaultConfig();
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        // 获取定义 FeignClient 的接口的 FeignClient 注解
        FeignClient feignClient = AnnotationUtils.getAnnotation(request.requestTemplate().feignTarget().type(), FeignClient.class);
        assert feignClient != null;
        ArgumentAssert.notNull(feignClient.contextId(), "@FeignClient未配置contextId");
        //和 Retry 保持一致，使用 contextId，而不是微服务名称
        //contextId 会作为我们后面读取断路器以及线程隔离配置的key
        String contextId = feignClient.contextId();

        //获取实例唯一id
        String serviceInstanceId = getServiceInstanceId(request);
        //获取实例+方法唯一id
        String serviceInstanceMethodId = getServiceInstanceMethodId(request);

        String bulkheadName = MessageFormat.format("{0}:{1}", contextId, serviceInstanceId);
        ThreadPoolBulkheadConfig threadPoolBulkheadConfig = threadPoolBulkheadConfigs.computeIfAbsent(contextId, defaultThreadPoolBulkheadConfig);
        //每个实例一个线程池
        ThreadPoolBulkhead threadPoolBulkhead = threadPoolBulkheadRegistry.bulkhead(bulkheadName, threadPoolBulkheadConfig);
        String circuitBreakerName = getServiceInstanceMethodId(request);
        CircuitBreakerConfig circuitBreakerConfig = circuitBreakerConfigConfigs.computeIfAbsent(contextId, defaultCircuitBreakerConfig);
        //每个服务实例具体方法一个resilience4j熔断记录器，在服务实例具体方法维度做熔断，所有这个服务的实例具体方法共享这个服务的resilience4j熔断配置
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName, circuitBreakerConfig);
        Supplier<Response> responseCopier = () -> Try.of(() -> {
            if (log.isDebugEnabled()) {
                log.debug("call url: {} -> {}, ThreadPoolStats({}): {}, CircuitBreakStats({}): {}",
                        request.httpMethod(),
                        request.url(),
                        serviceInstanceId,
                        JSON.toJSONString(threadPoolBulkhead.getMetrics()),
                        serviceInstanceMethodId,
                        JSON.toJSONString(circuitBreaker.getMetrics()));
            }
            Response execute = okHttpClient.execute(request, options);
            log.info("response: {} - {}", execute.status(), execute.reason());
            return execute;
        }).getOrElseThrow(throwable -> new CompletionException(throwable));
        try {
            Supplier<CompletionStage<Response>> completionStageSupplier = threadPoolBulkhead.decorateSupplier(responseCopier);
            Supplier<CompletionStage<Response>> decorateCompletionStage = circuitBreaker.decorateCompletionStage(completionStageSupplier);
            return Try.ofSupplier(decorateCompletionStage).get().toCompletableFuture().join();
        } catch (BulkheadFullException e) {
            //线程池限流异常
            return Response.builder().request(request).status(RetryableHttpStatus.BULKHEAD_FULL.getValue()).reason(e.getLocalizedMessage()).requestTemplate(request.requestTemplate()).build();
        } catch (CompletionException e) {
            //内部抛出的所有异常都被封装了一层 CompletionException，所以这里需要取出里面的 Exception
            Throwable cause = e.getCause();
            //对于断路器打开，返回对应特殊的错误码
            if (cause instanceof CallNotPermittedException) {
                return Response.builder().request(request).status(RetryableHttpStatus.CIRCUIT_BREAKER_ON.getValue()).reason(cause.getLocalizedMessage()).requestTemplate(request.requestTemplate()).build();
            }
            //对于 IOException，需要判断是否请求已经发送出去了
            //对于 connect time out 的异常，则可以重试，因为请求没发出去，但是例如 read time out 则不行，因为请求已经发出去了
            if (cause instanceof IOException) {
                String message = cause.getMessage().toLowerCase();
                boolean containsRead = message.contains("read") || message.contains("respon");
                if (containsRead) {
                    log.info("{}-{} exception contains read, which indicates the request has been sent", e.getMessage(), cause.getMessage());
                    //如果是 read 异常，则代表请求已经发了出去，则不能重试（除非是 GET 请求或者有 RetryableMethod 注解，这个在 DefaultErrorDecoder 判断）
                    return Response.builder().request(request).status(RetryableHttpStatus.NOT_RETRYABLE_IO_EXCEPTION.getValue()).reason(cause.getLocalizedMessage()).requestTemplate(request.requestTemplate()).build();
                } else {
                    return Response.builder().request(request).status(RetryableHttpStatus.RETRYABLE_IO_EXCEPTION.getValue()).reason(cause.getLocalizedMessage()).requestTemplate(request.requestTemplate()).build();
                }
            }
            throw e;
        }
    }

    private String getServiceInstanceId(Request request) throws MalformedURLException {
        URL url = new URL(request.url());
        return Resilience4jUtil.getServiceInstance(url);
    }

    private String getServiceInstanceMethodId(Request request) throws MalformedURLException {
        URL url = new URL(request.url());
        //通过微服务名称 + 实例 + 方法的方式，获取唯一id
        return Resilience4jUtil.getServiceInstanceMethodId(url, request.requestTemplate().methodMetadata().method());
    }
}
