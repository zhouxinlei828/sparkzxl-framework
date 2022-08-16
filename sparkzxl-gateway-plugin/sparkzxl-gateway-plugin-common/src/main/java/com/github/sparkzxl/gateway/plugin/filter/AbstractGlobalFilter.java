package com.github.sparkzxl.gateway.plugin.filter;

import com.github.sparkzxl.core.spring.SpringContextUtils;
import com.github.sparkzxl.gateway.plugin.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.handler.FilterDataHandler;
import com.github.sparkzxl.gateway.plugin.properties.GatewayPluginProperties;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.Map;

/**
 * description: abstract global filter
 *
 * @author zhouxinlei
 * @since 2022-01-08 21:48:39
 */
public abstract class AbstractGlobalFilter implements GlobalFilter, Ordered, InitializingBean {

    private final Map<String, FilterData> filterDataMap = Maps.newHashMap();
    private final Map<String, FilterDataHandler> filterDataHandlerMap = Maps.newHashMap();
    @Autowired
    protected GatewayPluginProperties gatewayPluginProperties;
    @Autowired
    protected List<FilterDataHandler> filterDataHandlerList;

    public AbstractGlobalFilter() {
    }

    public FilterData loadFilterData() {
        return filterDataMap.get(named());
    }

    public FilterDataHandler getFilterDataHandler() {
        return filterDataHandlerMap.get(named());
    }

    /**
     * 过滤器名称
     *
     * @return String
     */
    public abstract String named();


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, FilterDataHandler> beansOfType = SpringContextUtils.getBeansOfType(FilterDataHandler.class);
        if (MapUtils.isNotEmpty(beansOfType)) {
            filterDataHandlerMap.putAll(beansOfType);
        }
        if (MapUtils.isNotEmpty(gatewayPluginProperties.getFilter())) {
            filterDataMap.putAll(gatewayPluginProperties.getFilter());
        }
    }
}
