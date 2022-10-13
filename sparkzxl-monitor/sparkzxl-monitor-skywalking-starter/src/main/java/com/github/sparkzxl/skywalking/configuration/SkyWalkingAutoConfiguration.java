package com.github.sparkzxl.skywalking.configuration;

import com.github.sparkzxl.monitor.TracerExecutor;
import com.github.sparkzxl.monitor.constant.MonitorConstant;
import com.github.sparkzxl.skywalking.monitor.SkyWalkingStrategyTracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:29:57
 */
@Configuration
@ConditionalOnProperty(value = MonitorConstant.SPRING_MONITOR_TRACER_ENABLED)
public class SkyWalkingAutoConfiguration {

    @Bean
    public TracerExecutor skyWalkingStrategyTracer() {
        return new SkyWalkingStrategyTracer();
    }
}