package com.github.sparkzxl.opentracing.configuration;

import com.github.sparkzxl.monitor.TracerExecutor;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import com.github.sparkzxl.opentracing.monitor.OpenTracingTracerExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:18:01
 */
@Configuration
public class OpenTracingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_MONITOR_LOGGER_ENABLED)
    public TracerExecutor tracerExecutor() {
        return new OpenTracingTracerExecutor();
    }
}