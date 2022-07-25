package com.github.sparkzxl.monitor;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * description: 策略监控类
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:39:04
 */
public class StrategyMonitor {

    @Autowired(required = false)
    protected StrategyLogger strategyLogger;

    @Autowired(required = false)
    protected TracerExecutor tracerExecutor;

    public void loggerOutput() {
        if (strategyLogger != null) {
            strategyLogger.loggerOutput();
        }
    }

    public void loggerClear() {
        if (strategyLogger != null) {
            strategyLogger.loggerClear();
        }
    }

    public void loggerDebug() {
        if (strategyLogger != null) {
            strategyLogger.loggerDebug();
        }
    }

    public void spanBuild() {
        if (tracerExecutor != null) {
            tracerExecutor.spanBuild();
        }
    }

    public void spanOutput(Map<String, String> contextMap) {
        if (tracerExecutor != null) {
            tracerExecutor.spanOutput(contextMap);
        }
    }

    public void spanError(Throwable e) {
        if (tracerExecutor != null) {
            tracerExecutor.spanError(e);
        }
    }

    public void spanFinish() {
        if (tracerExecutor != null) {
            tracerExecutor.spanFinish();
        }
    }

    public StrategyLogger getStrategyLogger() {
        return strategyLogger;
    }

    public TracerExecutor getStrategyTracer() {
        return tracerExecutor;
    }

}
