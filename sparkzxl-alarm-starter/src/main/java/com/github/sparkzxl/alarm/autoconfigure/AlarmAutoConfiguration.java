package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.AlarmFactoryExecute;
import com.github.sparkzxl.alarm.aspect.AlarmAspect;
import com.github.sparkzxl.alarm.provider.AlarmTemplateProvider;
import com.github.sparkzxl.alarm.provider.YamlAlarmTemplateProvider;
import com.github.sparkzxl.alarm.service.dingtalk.DingTalkWarnService;
import com.github.sparkzxl.alarm.service.mail.MailWarnService;
import com.github.sparkzxl.alarm.service.wechat.WorkWeXinWarnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Configuration
public class AlarmAutoConfiguration {

    @Configuration
    @ConditionalOnProperty(prefix = MailConfig.PREFIX, name = "enabled", havingValue = "true")
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
            AlarmFactoryExecute.addAlarmLogWarnService(mailWarnService);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = WorkWeXinConfig.PREFIX, name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(WorkWeXinConfig.class)
    static class WorkWechatWarnServiceMethod {

        @Bean
        @ConditionalOnMissingBean(MailWarnService.class)
        public WorkWeXinWarnService workWechatWarnService(final WorkWeXinConfig workWeXinConfig) {
            return new WorkWeXinWarnService(workWeXinConfig.getTo(), workWeXinConfig.getApplicationId(), workWeXinConfig.getCorpId(), workWeXinConfig.getCorpSecret());
        }

        @Autowired
        void setDataChangedListener(WorkWeXinWarnService workWeXinWarnService) {
            AlarmFactoryExecute.addAlarmLogWarnService(workWeXinWarnService);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = DingTalkConfig.PREFIX, name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(DingTalkConfig.class)
    static class DingTalkWarnServiceMethod {

        @Bean
        @ConditionalOnMissingBean(DingTalkWarnService.class)
        public DingTalkWarnService dingTalkWarnService(final DingTalkConfig dingtalkConfig) {
            return new DingTalkWarnService(dingtalkConfig.getToken(), dingtalkConfig.getSecret());
        }

        @Autowired
        void setDataChangedListener(DingTalkWarnService dingTalkWarnService) {
            AlarmFactoryExecute.addAlarmLogWarnService(dingTalkWarnService);
        }
    }

    @Configuration
    @ConditionalOnProperty(prefix = TemplateConfig.PREFIX, name = "enabled", havingValue = "true")
    @EnableConfigurationProperties(TemplateConfig.class)
    static class TemplateConfigServiceMethod {

        @Bean
        @ConditionalOnMissingBean
        public AlarmTemplateProvider alarmTemplateProvider(TemplateConfig templateConfig) {
            return new YamlAlarmTemplateProvider(templateConfig);
        }

        @Bean
        public AlarmAspect alarmAspect(AlarmTemplateProvider alarmTemplateProvider) {
            return new AlarmAspect(alarmTemplateProvider);
        }

    }
}
