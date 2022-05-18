package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.callback.AlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.AlarmExceptionCallback;
import com.github.sparkzxl.alarm.callback.DefaultAlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.DefaultAlarmExceptionCallback;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.message.MarkDownMessage;
import com.github.sparkzxl.alarm.message.TextMessage;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.send.AlarmManagerBuilder;
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

    @Bean
    @ConditionalOnMissingBean(AlarmAsyncCallback.class)
    public AlarmAsyncCallback alarmAsyncCallback() {
        return new DefaultAlarmAsyncCallback();
    }

    @Bean
    @ConditionalOnMissingBean(AlarmExceptionCallback.class)
    public AlarmExceptionCallback alarmExceptionCallback() {
        return new DefaultAlarmExceptionCallback();
    }


    @Bean
    public AlarmManagerBuilder alarmManagerBuilder(AlarmIdGenerator alarmIdGenerator,
                                                   AlarmExceptionCallback alarmExceptionCallback,
                                                   CustomMessage textMessage,
                                                   CustomMessage markDownMessage,
                                                   AlarmSignAlgorithm alarmSignAlgorithm) {
        AlarmManagerBuilder alarmManagerBuilder = new AlarmManagerBuilder();
        alarmManagerBuilder.setAlarmExceptionCallback(alarmExceptionCallback);
        alarmManagerBuilder.setTextMessage(textMessage);
        alarmManagerBuilder.setMarkDownMessage(markDownMessage);
        alarmManagerBuilder.setAlarmIdGenerator(alarmIdGenerator);
        alarmManagerBuilder.setAlarmSignAlgorithm(alarmSignAlgorithm);
        return alarmManagerBuilder;
    }


    @Bean
    public AlarmSender alarmRobot(AlarmProperties alarmProperties,
                                  AlarmManagerBuilder alarmManagerBuilder) {
        return new AlarmRobot(alarmProperties, alarmManagerBuilder);
    }
}
