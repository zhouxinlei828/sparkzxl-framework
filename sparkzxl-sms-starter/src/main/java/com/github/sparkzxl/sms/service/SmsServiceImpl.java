package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.entity.SmsResult;
import com.github.sparkzxl.sms.factory.SmsFactory;
import com.github.sparkzxl.sms.request.SendSmsReq;
import lombok.RequiredArgsConstructor;

/**
 * description: 短信接口API实现类
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class SmsServiceImpl implements ISmsService {

    private final SmsFactory smsFactory;
    private final SmsProperties smsProperties;

    @Override
    public SmsResult sendSms(SendSmsReq sendSmsReq) {
        return smsFactory.getExecutor(smsProperties.getRegister()).send(sendSmsReq);
    }

}
