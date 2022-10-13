package com.github.sparkzxl.monitor;

import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.monitor.enums.TraceEnum;

/**
 * description:  自定义链路执行器
 *
 * @author zhouxinlei
 * @since 2022-08-18 16:26:39
 */
public class CustomizeTracerExecutor implements TracerExecutor {
    @Override
    public String getTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    @Override
    public String name() {
        return TraceEnum.CUSTOMIZE.name();
    }
}
