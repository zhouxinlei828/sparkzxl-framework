package com.github.sparkzxl.gateway.plugin.core.filter;

import com.github.sparkzxl.gateway.common.entity.FilterData;
import com.github.sparkzxl.gateway.plugin.handler.FilterDataHandler;
import com.github.sparkzxl.gateway.properties.GatewayPluginProperties;
import com.google.common.collect.Maps;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Map;

/**
 * description: abstract global filter
 *
 * @author zhouxinlei
 * @since 2022-01-08 21:48:39
 */
public abstract class AbstractGlobalFilter implements GlobalFilter, Ordered, InitializingBean, ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final Map<String, FilterData> filterDataMap = Maps.newHashMap();
    private final Map<String, FilterDataHandler> filterDataHandlerMap = Maps.newHashMap();
    @Resource
    protected GatewayPluginProperties gatewayPluginProperties;
    public AbstractGlobalFilter() {
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
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
        Map<String, FilterDataHandler> dataHandlerMap = applicationContext.getBeansOfType(FilterDataHandler.class);
        if (MapUtils.isNotEmpty(dataHandlerMap)) {
            Collection<FilterDataHandler> filterDataHandlers = dataHandlerMap.values();
            for (FilterDataHandler filterDataHandler : filterDataHandlers) {
                filterDataHandlerMap.put(filterDataHandler.filterNamed(), filterDataHandler);
            }
        }
        if (MapUtils.isNotEmpty(gatewayPluginProperties.getFilter())) {
            filterDataMap.putAll(gatewayPluginProperties.getFilter());
        }
    }
}
