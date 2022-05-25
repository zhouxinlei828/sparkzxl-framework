package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.aspect.AlarmNoticeAspect;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.handler.DefaultAlarmVariablesHandler;
import com.github.sparkzxl.alarm.handler.IAlarmVariablesHandler;
import com.github.sparkzxl.alarm.provider.AlarmTemplateProvider;
import com.github.sparkzxl.alarm.provider.YamlAlarmTemplateProvider;
import com.github.sparkzxl.alarm.send.AlarmSender;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * description: 日志告警自动装配
 *
 * @author zhoux
 */
@Slf4j
@ConditionalOnProperty(prefix = TemplateConfig.PREFIX, name = "enabled", havingValue = "true")
@Configuration
@EnableConfigurationProperties(TemplateConfig.class)
public class AlarmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AlarmTemplateProvider alarmTemplateProvider(TemplateConfig templateConfig) {
        return new YamlAlarmTemplateProvider(templateConfig);
    }

    @Bean(value = AlarmConstant.DEFAULT_ALARM_VARIABLES_HANDLER_BEAN_NAME)
    @ConditionalOnMissingBean
    public IAlarmVariablesHandler defaultAlarmVariablesHandler() {
        return new DefaultAlarmVariablesHandler(RequestLocalContextHolder::get);
    }

    @Bean
    @ConditionalOnMissingBean
    public AlarmNoticeAspect alarmAspect(AlarmTemplateProvider alarmTemplateProvider, AlarmSender alarmSender) {
        return new AlarmNoticeAspect(alarmTemplateProvider, alarmSender);
    }
}
