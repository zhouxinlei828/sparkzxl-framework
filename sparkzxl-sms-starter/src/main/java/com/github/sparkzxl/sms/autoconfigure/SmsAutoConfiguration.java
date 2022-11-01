package com.github.sparkzxl.sms.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
import com.github.sparkzxl.sms.executor.AliyunSmsHandlerExecutor;
import com.github.sparkzxl.sms.executor.SmsHandlerExecutor;
import com.github.sparkzxl.sms.executor.TencentSmsHandlerExecutor;
import com.github.sparkzxl.sms.factory.DefaultSmsHandlerFactory;
import com.github.sparkzxl.sms.factory.SmsHandlerFactory;
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
    public SmsHandlerFactory smsHandlerFactory(List<SmsHandlerExecutor> smsHandlerExecutorList) {
        DefaultSmsHandlerFactory smsHandlerFactory = new DefaultSmsHandlerFactory();
        if (CollectionUtil.isNotEmpty(smsHandlerExecutorList)) {
            for (SmsHandlerExecutor smsHandlerExecutor : smsHandlerExecutorList) {
                smsHandlerFactory.addStrategy(smsHandlerExecutor);
            }
        }
        return smsHandlerFactory;
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "channel", havingValue = "aliyun")
    public SmsHandlerExecutor aliyunSmsHandlerStrategy(SmsProperties smsProperties) {
        return new AliyunSmsHandlerExecutor(smsProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "channel", havingValue = "tencent")
    public SmsHandlerExecutor tencentSmsHandlerStrategy(SmsProperties smsProperties) {
        return new TencentSmsHandlerExecutor(smsProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ISmsService.class)
    public ISmsService smsService(SmsHandlerFactory smsHandlerFactory, SmsProperties smsProperties) {
        return new SmsServiceImpl(smsHandlerFactory, smsProperties);
    }


}
