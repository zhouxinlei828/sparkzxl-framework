package com.github.sparkzxl.alarm.feishutalk.autoconfigure;

import com.github.sparkzxl.alarm.feishutalk.executor.FeiShuTalkAlarmExecutor;
import com.github.sparkzxl.alarm.feishutalk.sign.FeiShuTalkAlarmSignAlgorithm;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 飞书告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.channel.feishu.enabled", havingValue = "true")
@Configuration
public class FeiShuTalkAutoConfig {

    /**
     * FeiShuTalk签名算法
     *
     * @return FeiShuTalkAlarmSignAlgorithm
     */
    @Bean
    public FeiShuTalkAlarmSignAlgorithm feiShuTalkAlarmSignAlgorithm() {
        return new FeiShuTalkAlarmSignAlgorithm();
    }

    /**
     * 飞书告警执行器
     *
     * @return FeiShuTalkAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(FeiShuTalkAlarmExecutor.class)
    public FeiShuTalkAlarmExecutor feiShuTalkAlarmExecutor(FeiShuTalkAlarmSignAlgorithm feiShuTalkAlarmSignAlgorithm) {
        return new FeiShuTalkAlarmExecutor(feiShuTalkAlarmSignAlgorithm);
    }

}
