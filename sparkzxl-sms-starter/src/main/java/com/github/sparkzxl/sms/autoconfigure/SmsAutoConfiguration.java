package com.github.sparkzxl.sms.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
import com.github.sparkzxl.sms.executor.AliyunSmsExecutor;
import com.github.sparkzxl.sms.executor.SmsExecutor;
import com.github.sparkzxl.sms.executor.TencentSmsExecutor;
import com.github.sparkzxl.sms.factory.DefaultSmsFactory;
import com.github.sparkzxl.sms.factory.SmsFactory;
import com.github.sparkzxl.sms.service.ISmsService;
import com.github.sparkzxl.sms.service.SmsServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
    public SmsFactory smsHandlerFactory(List<SmsExecutor> smsExecutorList) {
        DefaultSmsFactory smsHandlerFactory = new DefaultSmsFactory();
        if (CollectionUtil.isNotEmpty(smsExecutorList)) {
            for (SmsExecutor smsExecutor : smsExecutorList) {
                smsHandlerFactory.addStrategy(smsExecutor);
            }
        }
        return smsHandlerFactory;
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "channel", havingValue = "aliyun")
    public SmsExecutor aliyunSmsHandlerStrategy(SmsProperties smsProperties) {
        return new AliyunSmsExecutor(smsProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "channel", havingValue = "tencent")
    public SmsExecutor tencentSmsHandlerStrategy(SmsProperties smsProperties) {
        return new TencentSmsExecutor(smsProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ISmsService.class)
    public ISmsService smsService(SmsFactory smsFactory, SmsProperties smsProperties) {
        return new SmsServiceImpl(smsFactory, smsProperties);
    }


}
