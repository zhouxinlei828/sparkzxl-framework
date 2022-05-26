package com.github.sparkzxl.alarm.wetalk.autoconfigure;

import com.github.sparkzxl.alarm.wetalk.executor.WeTalkAlarmExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 企业微信告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.channel.wetalk.enabled", havingValue = "true")
@Configuration
public class WeTalkAutoConfig {

    /**
     * 企业微信告警执行器
     *
     * @return WeTalkAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(WeTalkAlarmExecutor.class)
    public WeTalkAlarmExecutor weTalkAlarmExecutor() {
        return new WeTalkAlarmExecutor();
    }

}
