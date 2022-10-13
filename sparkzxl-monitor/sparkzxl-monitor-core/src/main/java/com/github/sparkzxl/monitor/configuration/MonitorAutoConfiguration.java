package com.github.sparkzxl.monitor.configuration;

import com.github.sparkzxl.monitor.CustomizeTracerExecutor;
import com.github.sparkzxl.monitor.MonitorContext;
import com.github.sparkzxl.monitor.TracerExecutor;
import com.github.sparkzxl.monitor.constant.MonitorConstant;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 日志监控
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:36:49
 */
@EnableConfigurationProperties(TraceProperties.class)
@ConditionalOnProperty(value = MonitorConstant.SPRING_MONITOR_TRACER_ENABLED)
@Configuration
public class MonitorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MonitorContext monitorContext(TraceProperties traceProperties) {
        return new MonitorContext(traceProperties);
    }

    @Bean
    public TracerExecutor customizeTracerExecutor() {
        return new CustomizeTracerExecutor();
    }
}
