package com.github.sparkzxl.dubbo.filter;

import com.github.sparkzxl.core.jackson.JsonUtil;
import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.dubbo.properties.DubboCustomProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.apache.dubbo.rpc.service.GenericService;
import org.springframework.boot.logging.LogLevel;

import java.text.MessageFormat;

/**
 * description: dubbo日志过滤器
 *
 * @author zhouxinlei
 * @since 2022-08-06 14:21:30
 */
@Slf4j
@Getter
@Setter
@Activate(group = {CommonConstants.PROVIDER, CommonConstants.CONSUMER}, order = Integer.MAX_VALUE)
public class RequestLogFilter implements Filter {

    private DubboCustomProperties properties;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (ObjectUtils.isEmpty(properties)) {
            DubboCustomProperties properties = SpringContextUtils.getBean(DubboCustomProperties.class);
            setProperties(properties);
        }
        if (!properties.isRequestLog()) {
            // 未开启则跳过日志逻辑
            return invoker.invoke(invocation);
        }
        String client = CommonConstants.PROVIDER;
        if (RpcContext.getServiceContext().isConsumerSide()) {
            client = CommonConstants.CONSUMER;
        }
        String baseLog = MessageFormat.format("Client[{0}]],InterfaceName=[{1}],MethodName=[{2}]",
                client,
                invocation.getInvoker().getInterface().getSimpleName(),
                invocation.getMethodName());
        if (properties.getLevel() == LogLevel.INFO) {
            log.info("dubbo -> 服务调用: {}", baseLog);
        } else if (properties.getLevel() == LogLevel.DEBUG) {
            log.debug("dubbo -> 服务调用: {}, Parameters={}", baseLog, invocation.getArguments());
        }

        long startTime = System.currentTimeMillis();
        // 执行接口调用逻辑
        Result result = invoker.invoke(invocation);
        // 调用耗时
        long elapsed = System.currentTimeMillis() - startTime;
        // 如果发生异常 则打印异常日志
        if (result.hasException() && invoker.getInterface().equals(GenericService.class)) {
            log.error("dubbo -> 服务异常: {},Exception={}", baseLog, result.getException());
        } else {
            if (properties.getLevel() == LogLevel.INFO) {
                log.info("dubbo -> 服务响应: {},consumeTime=[{}ms]", baseLog, elapsed);
            } else if (properties.getLevel() == LogLevel.DEBUG) {
                log.info("dubbo -> 服务响应: {},consumeTime=[{}ms],ApiResult={}", baseLog, elapsed, JsonUtil.toJson(new Object[]{result.getValue()}));
            }
        }
        return result;
    }

}
