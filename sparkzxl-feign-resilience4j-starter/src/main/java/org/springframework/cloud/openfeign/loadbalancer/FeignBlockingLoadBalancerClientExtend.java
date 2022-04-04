package org.springframework.cloud.openfeign.loadbalancer;

import com.google.common.collect.Maps;
import feign.Client;
import feign.Request;
import feign.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.*;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import static org.springframework.cloud.openfeign.loadbalancer.LoadBalancerUtils.executeWithLoadBalancerLifecycleProcessing;


/**
 * description: FeignBlockingLoadBalancerClient的改写
 * 将获取每个实例的断路器数据需要的信息填充到 lb 请求
 * 然后在负载均衡器进行获取从而拿到实例的负载均衡请求
 *
 * @author zhouxinlei
 * @since 2022-04-04 12:01:34
 */
@Slf4j
@RequiredArgsConstructor
public class FeignBlockingLoadBalancerClientExtend implements Client {

    private final Client delegate;

    private final LoadBalancerClient loadBalancerClient;

    private final LoadBalancerProperties properties;

    private final LoadBalancerClientFactory loadBalancerClientFactory;

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        final URI originalUri = URI.create(request.url());
        String serviceId = originalUri.getHost();
        Assert.state(serviceId != null, "Request URI does not contain a valid hostname: " + originalUri);
        String hint = getHint(serviceId);
        DefaultRequest<RequestDataContext> lbRequest = new DefaultRequest<>(
                new RequestDataContext(buildRequestData(request), hint));
        Set<LoadBalancerLifecycle> supportedLifecycleProcessors = LoadBalancerLifecycleValidator
                .getSupportedLifecycleProcessors(
                        loadBalancerClientFactory.getInstances(serviceId, LoadBalancerLifecycle.class),
                        RequestDataContext.class, ResponseData.class, ServiceInstance.class);
        supportedLifecycleProcessors.forEach(lifecycle -> lifecycle.onStart(lbRequest));
        ServiceInstance instance = loadBalancerClient.choose(serviceId, lbRequest);
        org.springframework.cloud.client.loadbalancer.Response<ServiceInstance> lbResponse = new DefaultResponse(
                instance);
        if (instance == null) {
            String message = "Load balancer does not contain an instance for the service " + serviceId;
            if (log.isWarnEnabled()) {
                log.warn(message);
            }
            supportedLifecycleProcessors.forEach(lifecycle -> lifecycle
                    .onComplete(new CompletionContext<ResponseData, ServiceInstance, RequestDataContext>(
                            CompletionContext.Status.DISCARD, lbRequest, lbResponse)));
            return Response.builder().request(request).status(HttpStatus.SERVICE_UNAVAILABLE.value())
                    .body(message, StandardCharsets.UTF_8).build();
        }
        String reconstructedUrl = loadBalancerClient.reconstructURI(instance, originalUri).toString();
        Request newRequest = buildRequest(request, reconstructedUrl);
        return executeWithLoadBalancerLifecycleProcessing(delegate, options, newRequest, lbRequest, lbResponse,
                supportedLifecycleProcessors);
    }

    /**
     * 修改的就是这里，原来这个方法是在 LoadBalancerUtils 里面
     * 这里就是将 Request 的额外信息放进去
     *
     * @param request
     * @return
     */
    static RequestData buildRequestData(Request request) {
        HttpHeaders requestHeaders = new HttpHeaders();
        request.headers().forEach((key, value) -> requestHeaders.put(key, new ArrayList<>(value)));
        Map<String, Object> attributes = Maps.newHashMap();
        attributes.put(REQUEST_TEMPLATE, request.requestTemplate());
        return new RequestData(HttpMethod.resolve(request.httpMethod().name()), URI.create(request.url()),
                requestHeaders, null, attributes);
    }

    public static final String REQUEST_TEMPLATE = "request_template";

    protected Request buildRequest(Request request, String reconstructedUrl) {
        return Request.create(request.httpMethod(), reconstructedUrl, request.headers(), request.body(),
                request.charset(), request.requestTemplate());
    }

    private String getHint(String serviceId) {
        String defaultHint = properties.getHint().getOrDefault("default", "default");
        String hintPropertyValue = properties.getHint().get(serviceId);
        return hintPropertyValue != null ? hintPropertyValue : defaultHint;
    }
}
