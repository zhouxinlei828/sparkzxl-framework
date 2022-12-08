package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.entity.SmsResult;
import com.github.sparkzxl.sms.request.SendSmsReq;

/**
 * description: 短信接口API
 *
 * @author zhouxinlei
 * @since 2022-05-26 14:28:46
 */
public interface ISmsService {

    /**
     * 发送短信
     *
     * @param sendSmsReq 短信入参
     * @return SmsResult
     */
    SmsResult sendSms(SendSmsReq sendSmsReq);

}
