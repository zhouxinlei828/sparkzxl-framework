package com.github.sparkzxl.sms.autoconfigure;

import cn.hutool.core.collection.CollectionUtil;
import com.github.sparkzxl.sms.factory.DefaultSmsHandlerFactory;
import com.github.sparkzxl.sms.factory.SmsHandlerFactory;
import com.github.sparkzxl.sms.service.ISmsService;
import com.github.sparkzxl.sms.service.SmsServiceImpl;
import com.github.sparkzxl.sms.strategy.AliyunSmsHandlerStrategy;
import com.github.sparkzxl.sms.strategy.SmsHandlerStrategy;
import com.github.sparkzxl.sms.strategy.TencentSmsHandlerStrategy;
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
 * @date 2022-01-03 13:57:49
 */
@Configuration
@ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(SmsProperties.class)
public class SmsAutoConfig {

    @Bean
    public SmsHandlerFactory smsHandlerFactory(List<SmsHandlerStrategy> smsHandlerStrategyList) {
        DefaultSmsHandlerFactory smsHandlerFactory = new DefaultSmsHandlerFactory();
        if (CollectionUtil.isNotEmpty(smsHandlerStrategyList)) {
            for (SmsHandlerStrategy smsHandlerStrategy : smsHandlerStrategyList) {
                smsHandlerFactory.addStrategy(smsHandlerStrategy);
            }
        }
        return smsHandlerFactory;
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "channel", havingValue = "aliyun")
    public SmsHandlerStrategy aliyunSmsHandlerStrategy(SmsProperties smsProperties) {
        return new AliyunSmsHandlerStrategy(smsProperties);
    }

    @Bean
    @ConditionalOnProperty(prefix = SmsProperties.PREFIX, name = "channel", havingValue = "tencent")
    public SmsHandlerStrategy tencentSmsHandlerStrategy(SmsProperties smsProperties) {
        return new TencentSmsHandlerStrategy(smsProperties);
    }

    @Bean
    @ConditionalOnMissingBean(ISmsService.class)
    public ISmsService smsService(SmsHandlerFactory smsHandlerFactory, SmsProperties smsProperties) {
        return new SmsServiceImpl(smsHandlerFactory, smsProperties);
    }


}
