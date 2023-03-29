package com.github.sparkzxl.gateway.plugin.dubbo;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.common.constant.RpcConstant;
import com.github.sparkzxl.gateway.common.constant.enums.FilterEnum;
import com.github.sparkzxl.gateway.common.entity.FilterData;
import com.github.sparkzxl.gateway.common.entity.MetaData;
import com.github.sparkzxl.gateway.plugin.core.context.GatewayContext;
import com.github.sparkzxl.gateway.plugin.core.filter.AbstractGlobalFilter;
import com.github.sparkzxl.gateway.plugin.dubbo.constant.DubboConstant;
import com.github.sparkzxl.gateway.plugin.dubbo.message.DubboMessageWriter;
import com.github.sparkzxl.gateway.plugin.dubbo.route.DubboMetaDataFactory;
import com.github.sparkzxl.gateway.plugin.dubbo.route.DubboRoutePredicate;
import com.github.sparkzxl.gateway.plugin.dubbo.rule.DubboRuleHandle;
import com.github.sparkzxl.gateway.utils.ReactorHttpHelper;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * description: dubbo 过滤器
 *
 * @author zhouxinlei
 * @since 2022-08-12 16:40:07
 */
public class ApacheDubboFilter extends AbstractGlobalFilter {

    private static final Logger logger = LoggerFactory.getLogger(ApacheDubboFilter.class);

    private static final String REQUEST_LOCAL_CONTEXT = "request-local-context";

    private final DubboMetaDataFactory dubboMetaDataFactory;
    private final DubboRoutePredicate predicate;
    private final ApacheDubboProxyService dubboProxyService;
    private final DubboMessageWriter writer;

    public ApacheDubboFilter(DubboMetaDataFactory dubboMetaDataFactory,
            DubboRoutePredicate predicate,
            ApacheDubboProxyService dubboProxyService,
            DubboMessageWriter writer) {
        this.dubboMetaDataFactory = dubboMetaDataFactory;
        this.predicate = predicate;
        this.dubboProxyService = dubboProxyService;
        this.writer = writer;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String rpcType = ReactorHttpHelper.getHeader(exchange.getRequest(), GatewayConstant.RPC_TYPE);
        if (StringUtils.isBlank(rpcType) || !StringUtils.equals(rpcType, DubboConstant.DUBBO)) {
            return chain.filter(exchange);
        }
        Route route = exchange.getRequiredAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        boolean matched = predicate.test(exchange, route);
        if (!matched) {
            return chain.filter(exchange);
        }
        GatewayContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_CONTEXT_CONSTANT);
        assert gatewayContext != null;
        // check filter config data
        FilterData filterData = loadFilterData();
        getFilterDataHandler().handlerFilter(filterData);
        exchange.getAttributes().put(ServerWebExchangeUtils.GATEWAY_ALREADY_ROUTED_ATTR, true);
        MetaData metaData = dubboMetaDataFactory.get(route);
        metaData.setPath(gatewayContext.getPath());
        if (!checkMetaData(metaData)) {
            logger.error(" path is : {}, meta data have error : {}", gatewayContext.getPath(), metaData);
            exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
            return ReactorHttpHelper.error(exchange.getResponse(), "500", "dubbo元数据信息有误");
        }
        this.rpcContext(exchange);
        String param = exchange.getAttribute(RpcConstant.PARAM_TRANSFORM);
        return doDubboInvoker(exchange, chain, metaData, param);
    }

    /**
     * do dubbo invoker.
     *
     * @param exchange exchange the current server exchange {@linkplain ServerWebExchange}
     * @param chain    chain the current chain  {@linkplain ServerWebExchange}
     * @param metaData the medata
     * @param param    the param
     * @return {@code Mono<Void>} to indicate when request handling is complete
     */
    protected Mono<Void> doDubboInvoker(ServerWebExchange exchange,
            GatewayFilterChain chain,
            MetaData metaData,
            String param) {
        RpcContext.getServiceContext().setAttachment(RpcConstant.DUBBO_REMOTE_ADDRESS,
                Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
        final Mono<Object> result = dubboProxyService.genericInvoker(param, metaData);
        return result.map(resp -> {
                    FilterData filterData = loadFilterData();
                    String ruleHandle;
                    if (ObjectUtils.isEmpty(filterData.getRule())) {
                        ruleHandle = "{\"converter\":\"noOps\"}";
                    } else {
                        ruleHandle = filterData.getRule().getHandle();
                    }
                    DubboRuleHandle dubboRuleHandle = JsonUtils.getJson().toJavaObject(ruleHandle, DubboRuleHandle.class);
                    assert dubboRuleHandle != null;
                    return dubboRuleHandle.convert(exchange, resp);
                })
                .flatMap(resp -> this.writer.write(exchange, resp));
    }

    private boolean checkMetaData(final MetaData metaData) {
        return Objects.nonNull(metaData)
                && StringUtils.isNoneBlank(metaData.getMethodName())
                && StringUtils.isNoneBlank(metaData.getServiceName());
    }

    private void rpcContext(final ServerWebExchange exchange) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_CONTEXT_CONSTANT);
        Optional.ofNullable(gatewayContext)
                .map(val -> JsonUtils.getJson().toMap(val))
                .ifPresent(this::transmitRpcContext);
    }

    protected void transmitRpcContext(Map<String, Object> rpcContextMap) {
        RpcServiceContext serviceContext = RpcContext.getServiceContext();
        Map<String, Object> threadLocalMap = RequestLocalContextHolder.getLocalMap();
        threadLocalMap.putAll(rpcContextMap);
        serviceContext.setObjectAttachment(REQUEST_LOCAL_CONTEXT, threadLocalMap);
    }

    @Override
    public int getOrder() {
        return FilterEnum.DUBBO.getCode();
    }

    @Override
    public String named() {
        return FilterEnum.DUBBO.getName();
    }
}
