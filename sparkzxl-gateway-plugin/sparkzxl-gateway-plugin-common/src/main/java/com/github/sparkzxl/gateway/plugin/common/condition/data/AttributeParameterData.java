package com.github.sparkzxl.gateway.plugin.common.condition.data;

import cn.hutool.core.bean.OptionalBean;
import com.github.sparkzxl.spi.Join;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;

/**
 * description: 获取属性参数
 *
 * @author zhouxinlei
 * @date 2022-01-10 11:13:28
 */
@Join
public class AttributeParameterData implements ParameterData {

    @Override
    public String builder(String paramName, ServerWebExchange exchange) {
        return String.valueOf(ObjectUtils.isEmpty(exchange.getAttribute(paramName)) ? "" : exchange.getAttribute(paramName));
    }
}
