package com.github.sparkzxl.gateway.plugin.handler;

import com.github.sparkzxl.gateway.common.entity.FilterData;

/**
 * description:  The interface Filter data handler.
 *
 * @author zhouxinlei
 * @since 2022-08-15 14:45:12
 */
public interface FilterDataHandler {

    /**
     * Handler filter .
     *
     * @param filterData the Filter data
     */
    default void handlerFilter(FilterData filterData) {
    }


    /**
     * Filter named string.
     *
     * @return the string
     */
    String filterNamed();

}
