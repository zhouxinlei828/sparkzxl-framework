package com.github.sparkzxl.gateway.common.condition.data;

import cn.hutool.core.util.ReflectUtil;
import com.github.sparkzxl.gateway.common.constant.GatewayConstant;
import com.github.sparkzxl.gateway.plugin.core.context.GatewayContext;
import com.github.sparkzxl.spi.Join;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: 获取上下文属性值
 *
 * @author zhouxinlei
 * @since 2022-01-10 11:25:44
 */
@Join
public class ContextValueParameterData implements ParameterData {

    @Override
    public String builder(String paramName, ServerWebExchange exchange) {
        GatewayContext gatewayContext = exchange.getAttribute(GatewayConstant.GATEWAY_CONTEXT_CONSTANT);
        return (String) ReflectUtil.getFieldValue(gatewayContext, paramName);
    }
}
