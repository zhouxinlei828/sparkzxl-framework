package com.github.sparkzxl.monitor;

import com.github.sparkzxl.monitor.configuration.TraceProperties;
import com.google.common.collect.Maps;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:44:37
 */
public class MonitorContext implements ApplicationContextAware, InitializingBean {

    private ApplicationContext applicationContext;

    private final Map<String, TracerExecutor> tracerExecutorMap;
    private final TraceProperties traceProperties;

    public MonitorContext(TraceProperties traceProperties) {
        this.traceProperties = traceProperties;
        this.tracerExecutorMap = Maps.newHashMap();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public String getTraceId() {
        if (!traceProperties.isEnabled()) {
            return null;
        }
        TracerExecutor tracerExecutor = tracerExecutorMap.get(traceProperties.getType().name());
        if (tracerExecutor != null) {
            return tracerExecutor.getTraceId();
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, TracerExecutor> executorMap = applicationContext.getBeansOfType(TracerExecutor.class);
        for (TracerExecutor executor : executorMap.values()) {
            tracerExecutorMap.put(executor.name(), executor);
        }
    }
}
