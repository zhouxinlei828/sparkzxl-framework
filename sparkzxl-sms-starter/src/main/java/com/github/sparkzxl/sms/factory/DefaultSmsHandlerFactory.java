package com.github.sparkzxl.sms.factory;

import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.executor.SmsHandlerExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * description: sms处理工厂
 *
 * @author zhouxinlei
 * @since 2022-01-03 14:45:43
 */
public class DefaultSmsHandlerFactory implements SmsHandlerFactory {

    private final Map<String, SmsHandlerExecutor> strategyContainer;

    public DefaultSmsHandlerFactory() {
        this.strategyContainer = new HashMap<>();
    }

    @Override
    public SmsHandlerExecutor getExecutor(SmsRegister smsRegister) {
        return strategyContainer.get(smsRegister.getName());
    }

    public void addStrategy(SmsHandlerExecutor smsHandlerExecutor) {
        this.strategyContainer.put(smsHandlerExecutor.named(), smsHandlerExecutor);
    }
}
