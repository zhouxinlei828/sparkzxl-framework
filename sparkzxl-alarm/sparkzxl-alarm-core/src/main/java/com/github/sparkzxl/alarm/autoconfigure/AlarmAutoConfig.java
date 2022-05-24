package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.callback.AlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.AlarmExceptionCallback;
import com.github.sparkzxl.alarm.callback.DefaultAlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.DefaultAlarmExceptionCallback;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.executor.AlarmExecutor;
import com.github.sparkzxl.alarm.executor.DingTalkAlarmExecutor;
import com.github.sparkzxl.alarm.executor.MailAlarmExecutor;
import com.github.sparkzxl.alarm.executor.WeTalkAlarmExecutor;
import com.github.sparkzxl.alarm.loadbalancer.AlarmLoadBalancer;
import com.github.sparkzxl.alarm.loadbalancer.RandomAlarmLoadBalancer;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.message.MarkDownMessage;
import com.github.sparkzxl.alarm.message.TextMessage;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.send.AlarmRobot;
import com.github.sparkzxl.alarm.send.AlarmSender;
import com.github.sparkzxl.alarm.sign.AlarmSignAlgorithm;
import com.github.sparkzxl.alarm.sign.DingTalkAlarmSignAlgorithm;
import com.github.sparkzxl.alarm.support.AlarmIdGenerator;
import com.github.sparkzxl.alarm.support.DefaultAlarmIdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description: 告警自动装配
 *
 * @author zhouxinlei
 * @since 2022-05-18 15:24:13
 */
@ConditionalOnProperty(name = "spring.alarm.enabled", havingValue = "true")
@EnableConfigurationProperties(AlarmProperties.class)
@Configuration
public class AlarmAutoConfig {

    /**
     * 默认Text消息格式配置
     *
     * @return CustomMessage
     */
    @ConditionalOnMissingBean(name = AlarmConstant.TEXT_MESSAGE)
    @Bean(AlarmConstant.TEXT_MESSAGE)
    public CustomMessage textMessage() {
        return new TextMessage();
    }

    /**
     * 默认markdown消息格式配置
     *
     * @return CustomMessage
     */
    @ConditionalOnMissingBean(name = AlarmConstant.MARKDOWN_MESSAGE)
    @Bean(AlarmConstant.MARKDOWN_MESSAGE)
    public CustomMessage markDownMessage() {
        return new MarkDownMessage();
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
     * 默认的DingTalk签名算法
     *
     * @return AlarmSignAlgorithm
     */
    @Bean
    @ConditionalOnMissingBean(AlarmSignAlgorithm.class)
    public AlarmSignAlgorithm alarmSignAlgorithm() {
        return new DingTalkAlarmSignAlgorithm();
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

    /**
     * 邮件告警执行器
     *
     * @return MailAlarmExecutor
     */
    @Bean
    @ConditionalOnMissingBean(MailAlarmExecutor.class)
    public MailAlarmExecutor mailAlarmExecutor() {
        return new MailAlarmExecutor();
    }

    @Bean
    @ConditionalOnMissingBean(AlarmSender.class)
    public AlarmSender alarmSender(AlarmProperties alarmProperties,
                                   TextMessage textMessage,
                                   MarkDownMessage markDownMessage,
                                   List<AlarmExecutor> alarmExecutorList) {
        return new AlarmRobot(alarmProperties, textMessage, markDownMessage, alarmExecutorList);
    }

    @Bean
    @ConditionalOnMissingBean(AlarmLoadBalancer.class)
    public AlarmLoadBalancer alarmLoadBalancer() {
        return new RandomAlarmLoadBalancer();
    }
}
