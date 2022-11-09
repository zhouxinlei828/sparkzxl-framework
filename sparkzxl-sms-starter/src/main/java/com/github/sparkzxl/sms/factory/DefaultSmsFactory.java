package com.github.sparkzxl.sms.factory;

import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.executor.SmsExecutor;
import com.github.sparkzxl.sms.support.SmsException;
import com.github.sparkzxl.sms.support.SmsExceptionCodeEnum;

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
        SmsExecutor smsExecutor = strategyContainer.get(smsRegister.getCode());
        if (smsExecutor == null) {
            throw new SmsException(SmsExceptionCodeEnum.NOT_FOUND_SMS_REGISTER);
        }
        return smsExecutor;
    }

    public void addExecutor(SmsExecutor smsExecutor) {
        this.strategyContainer.put(smsExecutor.named(), smsExecutor);
    }
}
