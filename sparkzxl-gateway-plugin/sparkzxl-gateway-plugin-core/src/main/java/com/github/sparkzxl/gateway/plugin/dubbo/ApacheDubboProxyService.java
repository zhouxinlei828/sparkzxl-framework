package com.github.sparkzxl.gateway.plugin.dubbo;

import com.github.sparkzxl.core.support.code.ResultErrorCode;
import com.github.sparkzxl.gateway.common.entity.MetaData;
import com.github.sparkzxl.gateway.plugin.dubbo.config.ApacheDubboConfigCache;
import com.github.sparkzxl.gateway.plugin.dubbo.constant.DubboConstant;
import com.github.sparkzxl.gateway.plugin.dubbo.param.DubboParamResolveService;
import com.github.sparkzxl.gateway.support.GatewayException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * description: dubbo proxy service is  use GenericService.
 *
 * @author zhouxinlei
 * @since 2022-08-12 17:33:28
 */
@Slf4j
public class ApacheDubboProxyService {

    private final DubboParamResolveService dubboParamResolveService;

    public ApacheDubboProxyService(DubboParamResolveService dubboParamResolveService) {
        this.dubboParamResolveService = dubboParamResolveService;
    }

    /**
     * Generic invoker object.
     *
     * @param body     the body
     * @param metaData the meta data
     * @param exchange the exchange
     * @return the object
     * @throws RuntimeException the exception
     */
    public Mono<Object> genericInvoker(final String body, final MetaData metaData, final ServerWebExchange exchange) throws RuntimeException {
        String referenceKey = metaData.getPath();
        String namespace = "";
        if (CollectionUtils.isNotEmpty(exchange.getRequest().getHeaders().get(DubboConstant.NAMESPACE))) {
            namespace = Objects.requireNonNull(exchange.getRequest().getHeaders().get(DubboConstant.NAMESPACE)).get(0);
            referenceKey = namespace + ":" + referenceKey;
        }
        ReferenceConfig<GenericService> reference = ApacheDubboConfigCache.getInstance().get(referenceKey);
        if (Objects.isNull(reference) || StringUtils.isEmpty(reference.getInterface())) {
            ApacheDubboConfigCache.getInstance().invalidate(metaData.getPath());
            reference = ApacheDubboConfigCache.getInstance().initRef(metaData, namespace);
        }
        GenericService genericService = reference.get();
        Pair<String[], Object[]> pair;
        if (StringUtils.isBlank(metaData.getParameterTypes()) || ParamCheckUtils.bodyIsEmpty(body)) {
            pair = new ImmutablePair<>(new String[]{}, new Object[]{});
        } else {
            pair = dubboParamResolveService.buildParameter(body, metaData.getParameterTypes());
        }
        return Mono.fromFuture(invokeAsync(genericService, metaData.getMethodName(), pair.getLeft(), pair.getRight(), metaData).thenApply(ret -> {
            if (Objects.isNull(ret)) {
                ret = DubboConstant.DUBBO_RPC_RESULT_EMPTY;
            }
            if (log.isDebugEnabled()) {
                log.debug("Invoke dubbo succeed service:{}, method:{}, parameters:{},result:{}", metaData.getServiceName(), metaData.getMethodName(), pair.getRight(), ret);
            }
            return ret;
        })).onErrorMap(exception -> exception instanceof GenericException ? new GatewayException(ResultErrorCode.FAILURE.getErrorCode(), ((GenericException) exception).getExceptionMessage()) : new GatewayException(exception));
    }

    @SuppressWarnings("unchecked")
    private CompletableFuture<Object> invokeAsync(final GenericService genericService, final String method, final String[] parameterTypes, final Object[] args, final MetaData metaData) throws GenericException {
        //Compatible with asynchronous calls of lower Dubbo versions
        if (log.isDebugEnabled()) {
            log.debug("Async invoke dubbo succeed, service:{} , method:{}, parameters:{}", metaData.getServiceName(), method, args);
        }
        genericService.$invoke(method, parameterTypes, args);
        Object resultFromFuture = RpcContext.getServiceContext().getFuture();
        return resultFromFuture instanceof CompletableFuture ? (CompletableFuture<Object>) resultFromFuture : CompletableFuture.completedFuture(resultFromFuture);
    }

}
