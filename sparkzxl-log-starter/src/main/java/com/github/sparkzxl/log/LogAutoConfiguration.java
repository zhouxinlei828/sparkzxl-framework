package com.github.sparkzxl.log;

import com.github.sparkzxl.log.aspect.OptLogRecordAspect;
import com.github.sparkzxl.log.aspect.HttpRequestLogAspect;
import com.github.sparkzxl.log.netty.LogWebSocketHandler;
import com.github.sparkzxl.log.properties.LogProperties;
import com.github.sparkzxl.log.store.DefaultLogRecordServiceImpl;
import com.github.sparkzxl.log.store.DefaultOperatorServiceImpl;
import com.github.sparkzxl.log.store.ILogRecordService;
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
    public LogWebSocketHandler logWebSocketHandler() {
        Environment env = applicationContext.getEnvironment();
        String applicationName = env.getProperty("spring.application.name");
        String logPath = logProperties.getFile().getPath().concat("/") + applicationName + ".log";
        LogWebSocketHandler logWebSocketHandler = new LogWebSocketHandler();
        logWebSocketHandler.setLogPath(logPath);
        return logWebSocketHandler;
    }

    @Bean
    @ConditionalOnMissingBean(ILogRecordService.class)
    public ILogRecordService logRecordService() {
        return new DefaultLogRecordServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(IOperatorService.class)
    public IOperatorService operatorService() {
        return new DefaultOperatorServiceImpl();
    }

    @Bean
    public OptLogRecordAspect optLogRecordAspect(ILogRecordService logRecordService, IOperatorService operatorService) {
        return new OptLogRecordAspect(logRecordService, operatorService);
    }
}
