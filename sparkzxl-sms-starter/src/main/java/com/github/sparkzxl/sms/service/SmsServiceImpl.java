package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.factory.SmsHandlerFactory;
import com.github.sparkzxl.sms.request.QuerySendDetailsReq;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.response.SendSmsResult;
import com.github.sparkzxl.sms.response.SmsSendDetail;
import com.github.sparkzxl.sms.response.SmsSignDetail;
import com.github.sparkzxl.sms.response.common.SmsListResp;
import com.github.sparkzxl.sms.response.common.SmsResp;
import com.github.sparkzxl.sms.strategy.SmsHandlerStrategy;
import lombok.RequiredArgsConstructor;

/**
 * description: 短信接口API实现类
 *
 * @author zhouxinlei
 * @date 2022-01-03 14:39:54
 */
@RequiredArgsConstructor
public class SmsServiceImpl implements ISmsService {

    private final SmsHandlerFactory smsHandlerFactory;
    private final SmsProperties smsProperties;

    @Override
    public SmsResp<SendSmsResult> sendSms(SendSmsReq sendSmsReq) {
        SmsHandlerStrategy smsHandlerStrategy = smsHandlerFactory.getStrategy(smsProperties.getChannel());
        return smsHandlerStrategy.sendSms(sendSmsReq);
    }

    @Override
    public SmsListResp<SendSmsResult> sendBatchSms(SendSmsReq sendSmsReq) {
        SmsHandlerStrategy smsHandlerStrategy = smsHandlerFactory.getStrategy(smsProperties.getChannel());
        return smsHandlerStrategy.sendBatchSms(sendSmsReq);
    }

    @Override
    public SmsListResp<SmsSendDetail> querySendDetails(QuerySendDetailsReq querySendDetailsReq) {
        SmsHandlerStrategy smsHandlerStrategy = smsHandlerFactory.getStrategy(smsProperties.getChannel());
        return smsHandlerStrategy.querySendDetails(querySendDetailsReq);
    }

    @Override
    public SmsResp<SmsSignDetail> querySmsSign(String sign) {
        SmsHandlerStrategy smsHandlerStrategy = smsHandlerFactory.getStrategy(smsProperties.getChannel());
        return smsHandlerStrategy.querySmsSign(sign);
    }
}
