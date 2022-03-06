package com.github.sparkzxl.gateway.plugin.common.condition.data;

import com.github.sparkzxl.spi.SPI;
import org.springframework.web.server.ServerWebExchange;

/**
 * description: the interface for parameter data.
 *
 * @author zhouxinlei
 * @date 2022-01-10 10:25:48
 */
@SPI
public interface ParameterData {

    /**
     * Builder string.
     *
     * @param paramName the param name
     * @param exchange  the exchange
     * @return the string
     */
    default String builder(final String paramName, final ServerWebExchange exchange) {
        return "";
    }

}
