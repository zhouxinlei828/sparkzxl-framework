package com.github.sparkzxl.skywalking.configuration;

import com.github.sparkzxl.monitor.TracerExecutor;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import com.github.sparkzxl.skywalking.monitor.SkyWalkingStrategyTracer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class SkyWalkingAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_MONITOR_LOGGER_ENABLED, matchIfMissing = false)
    public TracerExecutor tracerExecutor() {
        return new SkyWalkingStrategyTracer();
    }
}