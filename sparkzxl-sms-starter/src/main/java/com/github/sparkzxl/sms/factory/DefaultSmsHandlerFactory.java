package com.github.sparkzxl.sms.factory;

import com.github.sparkzxl.sms.constant.enums.SmsChannel;
import com.github.sparkzxl.sms.strategy.SmsHandlerStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * description: sms处理工厂
 *
 * @author zhouxinlei
 * @since  2022-01-03 14:45:43
 */
public class DefaultSmsHandlerFactory implements SmsHandlerFactory {

    private final Map<String, SmsHandlerStrategy> strategyContainer;

    public DefaultSmsHandlerFactory() {
        this.strategyContainer = new HashMap<>();
    }

    @Override
    public SmsHandlerStrategy getStrategy(SmsChannel smsChannel) {
        return strategyContainer.get(smsChannel.getName());
    }

    public void addStrategy(SmsHandlerStrategy smsHandlerStrategy) {
        this.strategyContainer.put(smsHandlerStrategy.support(), smsHandlerStrategy);
    }
}
