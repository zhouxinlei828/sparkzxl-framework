package com.github.sparkzxl.monitor.configuration;

import com.github.sparkzxl.monitor.MonitorContext;
import com.github.sparkzxl.monitor.TracerContextListener;
import com.github.sparkzxl.monitor.aop.FeignMonitorRpcInterceptor;
import com.github.sparkzxl.monitor.constant.StrategyConstant;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-07-25 15:36:49
 */
@Configuration
public class MonitorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_MONITOR_LOGGER_ENABLED)
    public MonitorContext strategyMonitorContext() {
        return new MonitorContext();
    }

    @Bean
    @ConditionalOnProperty(value = StrategyConstant.SPRING_MONITOR_LOGGER_ENABLED)
    public TracerContextListener tracerContextListener() {
        return new TracerContextListener();
    }

    @ConditionalOnClass(Feign.class)
    protected static class FeignStrategyConfiguration {

        @Bean
        @ConditionalOnProperty(value = StrategyConstant.SPRING_MONITOR_REST_INTERCEPT_ENABLED, matchIfMissing = true)
        public FeignMonitorRpcInterceptor feignMonitorInterceptor() {
            return new FeignMonitorRpcInterceptor("", "");
        }
    }


}
