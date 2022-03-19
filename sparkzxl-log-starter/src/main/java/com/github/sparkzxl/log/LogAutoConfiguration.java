package com.github.sparkzxl.log;

import com.github.sparkzxl.log.aspect.HttpRequestLogAspect;
import com.github.sparkzxl.log.aspect.OptLogRecordAspect;
import com.github.sparkzxl.log.event.HttpRequestLogListener;
import com.github.sparkzxl.log.event.OptLogListener;
import com.github.sparkzxl.log.properties.LogProperties;
import com.github.sparkzxl.log.store.DefaultOperatorServiceImpl;
import com.github.sparkzxl.log.store.IOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Optional;

/**
 * description: 日志增强自动装配
 *
 * @author zhouxinlei
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(value = {LogProperties.class})
public class LogAutoConfiguration {

    @Autowired
    private LogProperties logProperties;
    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    void setAlarmLogConfig(LogProperties logProperties) {
        LogProperties.AlarmProperties alarmProperties = logProperties.getAlarm();
        if (alarmProperties.isEnabled()) {
            Optional.ofNullable(alarmProperties.getDoWarnException()).ifPresent(AlarmLogContext::addDoWarnExceptionList);
            Optional.of(alarmProperties.isWarnExceptionExtend()).ifPresent(AlarmLogContext::setWarnExceptionExtend);
            Optional.of(alarmProperties.isSimpleWarnInfo()).ifPresent(AlarmLogContext::setPrintStackTrace);
            Optional.of(alarmProperties.isSimpleWarnInfo()).ifPresent(AlarmLogContext::setSimpleWarnInfo);
        }
    }

    @Bean
    public HttpRequestLogAspect webLogAspect() {
        return new HttpRequestLogAspect();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpRequestLogListener httpRequestLogListener() {
        return new HttpRequestLogListener(log -> {
        });
    }

    @Bean
    @ConditionalOnMissingBean(IOperatorService.class)
    public IOperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    public OptLogRecordAspect optLogRecordAspect(IOperatorService operatorService) {
        return new OptLogRecordAspect(operatorService);
    }

    @Bean
    @ConditionalOnMissingBean
    public OptLogListener optLogListener() {
        return new OptLogListener(log -> {
        });
    }
}
