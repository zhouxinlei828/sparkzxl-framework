package com.github.sparkzxl.monitor;

import java.util.Map;

/**
 * description: 追踪执行器接口类
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:49:04
 */
public interface TracerExecutor {

    void spanBuild();

    void spanOutput(Map<String, String> contextMap);

    void spanError(Throwable e);

    void spanFinish();

    String getTraceId();

    String getSpanId();

}
