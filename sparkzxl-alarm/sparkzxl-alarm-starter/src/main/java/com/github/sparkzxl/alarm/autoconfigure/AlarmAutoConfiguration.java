package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.annotation.Alarm;
import com.github.sparkzxl.alarm.aop.AlarmAnnotationAdvisor;
import com.github.sparkzxl.alarm.aop.AlarmAnnotationInterceptor;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.handler.DefaultAlarmVariablesHandler;
import com.github.sparkzxl.alarm.handler.IAlarmVariablesHandler;
import com.github.sparkzxl.alarm.provider.AlarmTemplateProvider;
import com.github.sparkzxl.alarm.provider.FileAlarmTemplateProvider;
import com.github.sparkzxl.alarm.provider.JdbcAlarmTemplateProvider;
import com.github.sparkzxl.alarm.provider.YamlAlarmTemplateProvider;
import com.github.sparkzxl.alarm.send.AlarmClient;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

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
    @ConditionalOnProperty(prefix = TemplateConfig.PREFIX, name = "channel", havingValue = "yaml")
    @ConditionalOnMissingBean(YamlAlarmTemplateProvider.class)
    public AlarmTemplateProvider yamlAlarmTemplateProvider(TemplateConfig templateConfig) {
        return new YamlAlarmTemplateProvider(templateConfig);
    }

    @Bean
    @ConditionalOnProperty(prefix = TemplateConfig.PREFIX, name = "channel", havingValue = "file")
    @ConditionalOnMissingBean(FileAlarmTemplateProvider.class)
    public AlarmTemplateProvider fileAlarmTemplateProvider(TemplateConfig templateConfig) {
        return new FileAlarmTemplateProvider(templateConfig);
    }

    @Bean
    @ConditionalOnProperty(prefix = TemplateConfig.PREFIX, name = "channel", havingValue = "jdbc")
    @ConditionalOnMissingBean(JdbcAlarmTemplateProvider.class)
    public AlarmTemplateProvider jdbcAlarmTemplateProvider() {
        return new JdbcAlarmTemplateProvider((templateId) -> null);
    }

    @Bean(value = AlarmConstant.DEFAULT_ALARM_VARIABLES_HANDLER_BEAN_NAME)
    @ConditionalOnMissingBean
    public IAlarmVariablesHandler defaultAlarmVariablesHandler() {
        return new DefaultAlarmVariablesHandler(RequestLocalContextHolder::get);
    }

    @Bean
    public AlarmAnnotationInterceptor alarmAnnotationInterceptor(AlarmTemplateProvider alarmTemplateProvider, AlarmClient alarmClient) {
        return new AlarmAnnotationInterceptor(alarmTemplateProvider, alarmClient);
    }

    @Bean
    public AlarmAnnotationAdvisor alarmAnnotationAdvisor(AlarmAnnotationInterceptor alarmAnnotationInterceptor) {
        return new AlarmAnnotationAdvisor(alarmAnnotationInterceptor, Alarm.class, Ordered.HIGHEST_PRECEDENCE);
    }
}
