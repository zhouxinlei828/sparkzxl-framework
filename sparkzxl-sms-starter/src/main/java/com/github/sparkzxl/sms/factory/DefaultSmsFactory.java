package com.github.sparkzxl.sms.factory;

import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.executor.SmsExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * description: sms处理工厂
 *
 * @author zhouxinlei
 * @since 2022-01-03 14:45:43
 */
public class DefaultSmsFactory implements SmsFactory {

    private final Map<String, SmsExecutor> strategyContainer;

    public DefaultSmsFactory() {
        this.strategyContainer = new HashMap<>();
    }

    @Override
    public SmsExecutor getExecutor(SmsRegister smsRegister) {
        return strategyContainer.get(smsRegister.getName());
    }

    public void addStrategy(SmsExecutor smsExecutor) {
        this.strategyContainer.put(smsExecutor.named(), smsExecutor);
    }
}
