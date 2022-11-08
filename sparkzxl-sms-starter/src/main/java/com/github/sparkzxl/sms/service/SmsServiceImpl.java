package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.entity.SmsSignDetail;
import com.github.sparkzxl.sms.entity.SmsTemplateDetail;
import com.github.sparkzxl.sms.executor.SmsExecutor;
import com.github.sparkzxl.sms.factory.SmsFactory;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.resp.SmsResult;
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
        return loadSmsExecutor().send(sendSmsReq);
    }

    @Override
    public SmsSignDetail findSmsSign(String sign) {
        return loadSmsExecutor().findSmsSign(sign);
    }

    @Override
    public SmsTemplateDetail findSmsTemplate(String templateId) {
        return loadSmsExecutor().findSmsTemplate(templateId);
    }

    private SmsExecutor loadSmsExecutor() {
        return smsFactory.getExecutor(smsProperties.getRegister());
    }

}
