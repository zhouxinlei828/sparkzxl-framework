package com.github.sparkzxl.alarm.dingtalk.autoconfigure;

import com.github.sparkzxl.alarm.dingtalk.executor.DingTalkAlarmExecutor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 钉钉告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.channel.dingtalk.enabled", havingValue = "true")
@Configuration
public class DingTalkAutoConfig {

    /**
     * 钉钉告警执行器
     *
     * @return DingTalkAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(DingTalkAlarmExecutor.class)
    public DingTalkAlarmExecutor dingTalkAlarmExecutor() {
        return new DingTalkAlarmExecutor();
    }

}
