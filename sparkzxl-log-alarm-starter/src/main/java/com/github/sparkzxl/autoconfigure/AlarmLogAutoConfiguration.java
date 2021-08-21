package com.github.sparkzxl.autoconfigure;

import com.github.sparkzxl.AlarmLogContext;
import com.github.sparkzxl.factory.AlarmLogWarnServiceFactory;
import com.github.sparkzxl.service.dingtalk.DingTalkWarnService;
import com.github.sparkzxl.service.mail.MailWarnService;
import com.github.sparkzxl.service.wechat.WorkWechatWarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

/**
 * description: 日志告警自动装配
 *
 * @author zhoux
 * @date 2021-08-21 13:30:21
 */
@Slf4j
@Configuration
@ConditionalOnProperty(name = "logging.enable-alarm", havingValue = "true")
@EnableConfigurationProperties(AlarmLogConfig.class)
public class AlarmLogAutoConfiguration {

    @Autowired
    void setAlarmLogConfig(AlarmLogConfig alarmLogConfig) {
        Optional.ofNullable(alarmLogConfig.getDoWarnException()).ifPresent(AlarmLogContext::addDoWarnExceptionList);
        Optional.ofNullable(alarmLogConfig.getWarnExceptionExtend()).ifPresent(AlarmLogContext::setWarnExceptionExtend);
        Optional.ofNullable(alarmLogConfig.getPrintStackTrace()).ifPresent(AlarmLogContext::setPrintStackTrace);
        Optional.ofNullable(alarmLogConfig.getSimpleWarnInfo()).ifPresent(AlarmLogContext::setSimpleWarnInfo);
    }

    @Configuration
    @ConditionalOnProperty(name = "spring.alarm-log.warn.mail.enabled", havingValue = "true")
    @EnableConfigurationProperties(MailConfig.class)
    static class MailWarnServiceMethod {

        @Bean
        @ConditionalOnMissingBean(MailWarnService.class)
        public MailWarnService mailWarnService(final MailConfig mailConfig) {
            MailWarnService mailWarnService = new MailWarnService(mailConfig.getSmtpHost(), mailConfig.getSmtpPort(), mailConfig.getTo(), mailConfig.getFrom(), mailConfig.getUsername(), mailConfig.getPassword());
            mailWarnService.setSsl(mailConfig.getSsl());
            mailWarnService.setDebug(mailConfig.getDebug());
            return mailWarnService;
        }

        @Autowired
        void setDataChangedListener(MailWarnService mailWarnService) {
            AlarmLogWarnServiceFactory.setAlarmLogWarnService(mailWarnService);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "spring.alarm-log.warn.wechat.enabled", havingValue = "true")
    @EnableConfigurationProperties(WorkWechatConfig.class)
    static class WorkWechatWarnServiceMethod {

        @Bean
        @ConditionalOnMissingBean(MailWarnService.class)
        public WorkWechatWarnService workWechatWarnService(final WorkWechatConfig workWechatConfig) {
            return new WorkWechatWarnService(workWechatConfig.getTo(), workWechatConfig.getApplicationId(), workWechatConfig.getCorpId(), workWechatConfig.getCorpSecret());
        }

        @Autowired
        void setDataChangedListener(WorkWechatWarnService workWechatWarnService) {
            AlarmLogWarnServiceFactory.setAlarmLogWarnService(workWechatWarnService);
        }
    }

    @Configuration
    @ConditionalOnProperty(value = "spring.alarm-log.warn.dingtalk.enabled", havingValue = "true")
    @EnableConfigurationProperties(DingtalkConfig.class)
    static class DingTalkWarnServiceMethod {

        @Bean
        @ConditionalOnMissingBean(DingTalkWarnService.class)
        public DingTalkWarnService dingTalkWarnService(final DingtalkConfig dingtalkConfig) {
            return new DingTalkWarnService(dingtalkConfig.getToken(), dingtalkConfig.getSecret());
        }

        @Autowired
        void setDataChangedListener(DingTalkWarnService dingTalkWarnService) {
            AlarmLogWarnServiceFactory.setAlarmLogWarnService(dingTalkWarnService);
        }
    }
}
