package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.callback.AlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.AlarmExceptionCallback;
import com.github.sparkzxl.alarm.callback.DefaultAlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.DefaultAlarmExceptionCallback;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.executor.AlarmExecutor;
import com.github.sparkzxl.alarm.loadbalancer.AlarmLoadBalancer;
import com.github.sparkzxl.alarm.loadbalancer.RandomAlarmLoadBalancer;
import com.github.sparkzxl.alarm.message.MarkDownMessageTemplate;
import com.github.sparkzxl.alarm.message.TextMessageTemplate;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.properties.AlarmThreadPoolProperties;
import com.github.sparkzxl.alarm.send.AlarmRobot;
import com.github.sparkzxl.alarm.send.AlarmSender;
import com.github.sparkzxl.alarm.strategy.AlarmMessageFactory;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import com.github.sparkzxl.alarm.support.AlarmExceptionHandler;
import com.github.sparkzxl.alarm.support.AlarmIdGenerator;
import com.github.sparkzxl.alarm.support.DefaultAlarmIdGenerator;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * description: 告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.enabled", havingValue = "true")
@EnableConfigurationProperties({AlarmProperties.class, AlarmThreadPoolProperties.class})
@Import(AlarmExceptionHandler.class)
@Configuration
public class AlarmAutoConfig {

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

    @Bean
    public AlarmMessageFactory alarmMessageFactory(List<MsgHandleStrategy> msgHandleStrategyList) {
        return new AlarmMessageFactory(msgHandleStrategyList);
    }

    /**
     * 默认Text消息格式配置
     *
     * @return CustomMessage
     */
    @ConditionalOnMissingBean(name = AlarmConstant.TEXT_MESSAGE)
    @Bean(AlarmConstant.TEXT_MESSAGE)
    public TextMessageTemplate textMessage() {
        return new TextMessageTemplate();
    }

    /**
     * 默认markdown消息格式配置
     *
     * @return CustomMessage
     */
    @ConditionalOnMissingBean(name = AlarmConstant.MARKDOWN_MESSAGE)
    @Bean(AlarmConstant.MARKDOWN_MESSAGE)
    public MarkDownMessageTemplate markDownMessage() {
        return new MarkDownMessageTemplate();
    }

    /**
     * 默认的告警id生成算法
     *
     * @return AlarmIdGenerator
     */
    @Bean
    @ConditionalOnMissingBean(AlarmIdGenerator.class)
    public AlarmIdGenerator alarmIdGenerator() {
        return new DefaultAlarmIdGenerator();
    }

    /**
     * 异步执行回调接口
     *
     * @return AlarmAsyncCallback
     */
    @Bean
    @ConditionalOnMissingBean(AlarmAsyncCallback.class)
    public AlarmAsyncCallback alarmAsyncCallback() {
        return new DefaultAlarmAsyncCallback();
    }

    /**
     * AlarmExceptionCallback
     *
     * @return 通知异常回调
     */
    @Bean
    @ConditionalOnMissingBean(AlarmExceptionCallback.class)
    public AlarmExceptionCallback alarmExceptionCallback() {
        return new DefaultAlarmExceptionCallback();
    }

    @Bean
    @ConditionalOnMissingBean(AlarmSender.class)
    public AlarmSender alarmSender(AlarmProperties alarmProperties,
                                   TextMessageTemplate textMessage,
                                   MarkDownMessageTemplate markDownMessage,
                                   List<AlarmExecutor> alarmExecutorList,
                                   AlarmMessageFactory alarmMessageFactory) {
        return new AlarmRobot(alarmProperties, textMessage, markDownMessage, alarmExecutorList, alarmMessageFactory);
    }

    @Bean
    @ConditionalOnMissingBean(AlarmLoadBalancer.class)
    public AlarmLoadBalancer alarmLoadBalancer() {
        return new RandomAlarmLoadBalancer();
    }
}
