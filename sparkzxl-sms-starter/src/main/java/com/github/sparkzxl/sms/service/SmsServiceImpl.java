package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.entity.SmsSendRecord;
import com.github.sparkzxl.sms.entity.SmsSignDetail;
import com.github.sparkzxl.sms.entity.SmsTemplateDetail;
import com.github.sparkzxl.sms.executor.SmsHandlerExecutor;
import com.github.sparkzxl.sms.factory.SmsHandlerFactory;
import com.github.sparkzxl.sms.request.SendSmsReq;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * description: 短信接口API实现类
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class SmsServiceImpl implements ISmsService {

    private final SmsHandlerFactory smsHandlerFactory;
    private final SmsProperties smsProperties;

    @Override
    public List<SmsSendRecord> sendSms(SendSmsReq sendSmsReq) {
        return loadSmsHandler().send(sendSmsReq);
    }

    @Override
    public SmsSignDetail findSmsSign(String sign) {
        return loadSmsHandler().findSmsSign(sign);
    }

    @Override
    public SmsTemplateDetail findSmsTemplate(String templateId) {
        return loadSmsHandler().findSmsTemplate(templateId);
    }

    private SmsHandlerExecutor loadSmsHandler() {
        return smsHandlerFactory.getExecutor(smsProperties.getRegister());
    }

}
