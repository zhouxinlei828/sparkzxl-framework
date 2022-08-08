package com.github.sparkzxl.monitor;

import com.github.sparkzxl.core.context.IContextHolder;
import com.github.sparkzxl.monitor.constant.MonitorConstant;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:41:05
 */
public class DefaultStrategyLogger implements StrategyLogger {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStrategyLogger.class);

    @Value("${" + StrategyConstant.SPRING_MONITOR_LOGGER_ENABLED + ":false}")
    protected Boolean loggerEnabled;
    @Value("${" + StrategyConstant.SPRING_MONITOR_LOGGER_DEBUG_ENABLED + ":false}")
    protected Boolean loggerDebugEnabled;
    @Value("${" + StrategyConstant.SPRING_APPLICATION_STRATEGY_LOGGER_MDC_KEY_SHOWN + ":true}")
    protected Boolean loggerMdcKeyShown;

    @Autowired
    protected IContextHolder contextHolder;
    @Autowired
    private StrategyMonitorContext strategyMonitorContext;

    @Override
    public void loggerOutput() {
        if (!loggerEnabled) {
            return;
        }

        Map<String, String> customizationMap = strategyMonitorContext.getCustomizationMap();
        if (MapUtils.isNotEmpty(customizationMap)) {
            for (Map.Entry<String, String> entry : customizationMap.entrySet()) {
                MDC.put(entry.getKey(), (loggerMdcKeyShown ? entry.getKey() + "=" : StringUtils.EMPTY) + entry.getValue());
            }
        }

        String traceId = strategyMonitorContext.getTraceId();
        String spanId = strategyMonitorContext.getSpanId();
        MDC.put(MonitorConstant.TRACE_ID, (loggerMdcKeyShown ? MonitorConstant.TRACE_ID + "=" : StringUtils.EMPTY) + (StringUtils.isNotEmpty(traceId) ? traceId : StringUtils.EMPTY));
        MDC.put(MonitorConstant.SPAN_ID, (loggerMdcKeyShown ? MonitorConstant.SPAN_ID + "=" : StringUtils.EMPTY) + (StringUtils.isNotEmpty(spanId) ? spanId : StringUtils.EMPTY));

    }

    @Override
    public void loggerClear() {
        if (!loggerEnabled) {
            return;
        }
        MDC.clear();
    }

    @Override
    public void loggerDebug() {
        if (!loggerEnabled) {
            return;
        }
        String traceId = strategyMonitorContext.getTraceId();
        String spanId = strategyMonitorContext.getSpanId();
        String routeAddress = contextHolder.getHeader(MonitorConstant.ROUTE_ADDRESS);
        logger.debug("----------------------- Logger Debug Start-----------------------");
        StringBuilder format = new StringBuilder("traceId:{},spanId:{},routeAddress:{}");
        logger.debug("traceId:{},spanId:{},routeAddress:{}", traceId, spanId, routeAddress);
        Map<String, String> customizationMap = strategyMonitorContext.getCustomizationMap();
        if (MapUtils.isNotEmpty(customizationMap)) {
            for (Map.Entry<String, String> entry : customizationMap.entrySet()) {
                format.append(entry.getKey())
                        .append(":")
                        .append(entry.getValue());
            }
        }
        logger.debug(format.toString(), traceId, spanId, routeAddress);
        logger.debug("----------------------- Logger Debug End-----------------------");
    }
}
