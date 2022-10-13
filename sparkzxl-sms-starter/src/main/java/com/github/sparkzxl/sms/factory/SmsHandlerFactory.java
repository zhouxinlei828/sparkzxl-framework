package com.github.sparkzxl.sms.factory;

import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.executor.SmsHandlerExecutor;

/**
 * description: 短信处理工厂
 *
 * @author zhouxinlei
 * @since 2022-01-03 16:29:08
 */
public interface SmsHandlerFactory {

    /**
     * 获取短信策略
     *
     * @param smsRegister 短信注册商
     * @return SmsHandlerExecutor
     */
    SmsHandlerExecutor getExecutor(SmsRegister smsRegister);
}
