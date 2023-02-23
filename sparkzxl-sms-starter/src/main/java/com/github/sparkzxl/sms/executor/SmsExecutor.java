package com.github.sparkzxl.sms.executor;

import com.github.sparkzxl.sms.entity.SmsResult;
import com.github.sparkzxl.sms.request.SendSmsReq;

/**
 * description: 短信处理策略类
 *
 * @author zhouxinlei
 * @since 2022-01-03 12:31:56
 */
public interface SmsExecutor {

    /**
     * 发送短信
     *
     * @param sendSmsReq 发送参数
     * @return SmsResult 发送结果
     */
    SmsResult send(SendSmsReq sendSmsReq);

    /**
     * 支持类型名称
     *
     * @return String
     */
    String named();
}
