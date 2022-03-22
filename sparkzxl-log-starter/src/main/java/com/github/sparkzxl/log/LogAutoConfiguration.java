package com.github.sparkzxl.log;

import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.log.aspect.HttpRequestLogAspect;
import com.github.sparkzxl.log.aspect.ILogAttribute;
import com.github.sparkzxl.log.aspect.LogAttributeImpl;
import com.github.sparkzxl.log.event.HttpRequestLogListener;
import com.github.sparkzxl.log.properties.LogProperties;
import com.github.sparkzxl.log.store.OperatorService;
import com.github.sparkzxl.log.store.IOperatorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @ConditionalOnMissingBean
    public ILogAttribute logAttribute() {
        return new LogAttributeImpl(RequestLocalContextHolder::get);
    }

    @Bean
    public HttpRequestLogAspect httpRequestLogAspect() {
        return new HttpRequestLogAspect(logAttribute());
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
        return new OperatorService();
    }

}
