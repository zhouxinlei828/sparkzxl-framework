package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.properties.AlarmThreadPoolProperties;
import com.github.sparkzxl.alarm.send.AlarmRobot;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * DINGTALK线程池配置类
 *
 * @author Jaemon
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.alarm.enabled", havingValue = "true")
@ConditionalOnBean(AlarmRobot.class)
@EnableConfigurationProperties({AlarmThreadPoolProperties.class})
public class AlarmThreadPoolConfig {


    @ConditionalOnMissingBean(name = AlarmConstant.ALARM_THREAD_POOL_EXECUTOR)
    @Bean(name = AlarmConstant.ALARM_THREAD_POOL_EXECUTOR)
    public ThreadPoolExecutor alarmThreadPoolExecutor(AlarmThreadPoolProperties alarmThreadPoolProperties) {
        return new ThreadPoolExecutor(alarmThreadPoolProperties.getCoreSize(),
                alarmThreadPoolProperties.getMaxSize(),
                alarmThreadPoolProperties.getKeepAliveSeconds(),
                TimeUnit.SECONDS, new ArrayBlockingQueue<>(alarmThreadPoolProperties.getQueueCapacity()),
                new BasicThreadFactory.Builder().namingPattern(alarmThreadPoolProperties.getThreadNamePrefix()).build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

}