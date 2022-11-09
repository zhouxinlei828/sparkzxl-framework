package com.github.sparkzxl.sms.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
import com.github.sparkzxl.sms.executor.AliYunSmsExecutor;
import com.github.sparkzxl.sms.executor.SmsExecutor;
import com.github.sparkzxl.sms.executor.TencentSmsExecutor;
import com.github.sparkzxl.sms.factory.DefaultSmsFactory;
import com.github.sparkzxl.sms.factory.SmsFactory;
import com.github.sparkzxl.sms.service.ISmsService;
import com.github.sparkzxl.sms.service.SmsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-01-03 13:57:49
 */
@Configuration
@ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfiguration {

    @Bean
    public SmsFactory smsFactory(List<SmsExecutor> smsExecutorList) {
        DefaultSmsFactory smsFactory = new DefaultSmsFactory();
        if (CollectionUtil.isNotEmpty(smsExecutorList)) {
            for (SmsExecutor smsExecutor : smsExecutorList) {
                smsFactory.addExecutor(smsExecutor);
            }
        }
        return smsFactory;
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "register", havingValue = "aliyun")
    public SmsExecutor aliYunSmsExecutor(SmsProperties smsProperties, ApplicationEventPublisher eventPublisher) {
        return new AliYunSmsExecutor(smsProperties, eventPublisher);
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "register", havingValue = "tencent")
    public SmsExecutor tencentSmsExecutor(SmsProperties smsProperties, ApplicationEventPublisher eventPublisher) {
        return new TencentSmsExecutor(smsProperties, eventPublisher);
    }

    @Bean
    @ConditionalOnMissingBean(ISmsService.class)
    public ISmsService smsService(SmsFactory smsFactory, SmsProperties smsProperties) {
        return new SmsServiceImpl(smsFactory, smsProperties);
    }


}
