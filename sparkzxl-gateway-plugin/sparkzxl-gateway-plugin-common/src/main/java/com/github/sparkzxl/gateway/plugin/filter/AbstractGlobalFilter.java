package com.github.sparkzxl.gateway.plugin.filter;

import com.github.sparkzxl.gateway.plugin.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.properties.GatewayPluginProperties;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

import javax.annotation.Resource;
import java.util.Map;

/**
 * description: abstract global filter
 *
 * @author zhouxinlei
 * @date 2022-01-08 21:48:39
 */
public abstract class AbstractGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    protected GatewayPluginProperties gatewayPluginProperties;

    protected FilterData loadFilterData() {
        Map<String, FilterData> filterDataMap = gatewayPluginProperties.getFilter();
        return filterDataMap.get(named());
    }

    /**
     * 过滤器名称
     *
     * @return String
     */
    protected abstract String named();
}
