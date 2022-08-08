package com.github.sparkzxl.monitor;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:44:37
 */
public class StrategyMonitorContext {

    @Autowired(required = false)
    protected TracerExecutor tracerExecutor;

    @Autowired(required = false)
    protected TracerAdapter tracerAdapter;

    public String getTraceId() {
        if (tracerExecutor != null) {
            return tracerExecutor.getTraceId();
        }
        if (tracerAdapter != null) {
            return tracerAdapter.getTraceId();
        }
        return null;
    }


    public String getSpanId() {
        if (tracerExecutor != null) {
            return tracerExecutor.getSpanId();
        }
        if (tracerAdapter != null) {
            return tracerAdapter.getSpanId();
        }
        return null;
    }

    public Map<String, String> getCustomizationMap() {
        if (tracerAdapter != null) {
            return tracerAdapter.getCustomizationMap();
        }
        return null;
    }

}
