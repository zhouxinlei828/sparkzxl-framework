package com.github.sparkzxl.sms.factory;

import com.github.sparkzxl.sms.constant.enums.SmsChannel;
import com.github.sparkzxl.sms.strategy.SmsHandlerStrategy;

/**
 * description: 短信处理工厂
 *
 * @author zhouxinlei
 * @date 2022-01-03 16:29:08
 */
public interface SmsHandlerFactory {

    /**
     * 获取短信策略
     *
     * @param smsChannel 短信渠道
     * @return SmsHandlerStrategy
     */
    SmsHandlerStrategy getStrategy(SmsChannel smsChannel);
}
