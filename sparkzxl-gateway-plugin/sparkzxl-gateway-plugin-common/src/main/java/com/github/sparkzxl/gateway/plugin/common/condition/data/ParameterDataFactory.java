package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.ExtensionLoader;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: The type Parameter data factory.
 *
 * @author zhouxinlei
 * @date 2022-01-10 10:42:46
 */
public class ParameterDataFactory {

    /**
     * New instance parameter data.
     *
     * @param paramType the param type
     * @return the parameter data
     */
    public static ParameterData newInstance(final String paramType) {
        return ExtensionLoader.getExtensionLoader(ParameterData.class).getJoin(paramType);
    }

    /**
     * Builder data string.
     *
     * @param paramType the param type
     * @param paramName the param name
     * @param exchange  the exchange
     * @return the string
     */
    public static String builderData(final String paramType, final String paramName, final ServerWebExchange exchange) {
        return newInstance(paramType).builder(paramName, exchange);
    }

}
