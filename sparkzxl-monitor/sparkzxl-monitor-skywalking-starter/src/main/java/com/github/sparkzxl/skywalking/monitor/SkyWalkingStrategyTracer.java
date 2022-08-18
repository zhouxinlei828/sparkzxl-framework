package com.github.sparkzxl.skywalking.monitor;

import com.github.sparkzxl.monitor.TracerExecutor;
import com.github.sparkzxl.monitor.enums.TraceEnum;
import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:23:53
 */
public class SkyWalkingStrategyTracer implements TracerExecutor {

    @Override
    public String getTraceId() {
        return TraceContext.traceId();
    }

    @Override
    public String name() {
        return TraceEnum.SKYWALKING.name();
    }
}
