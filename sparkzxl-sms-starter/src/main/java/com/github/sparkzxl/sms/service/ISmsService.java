package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.entity.SmsSendRecord;
import com.github.sparkzxl.sms.entity.SmsSignDetail;
import com.github.sparkzxl.sms.entity.SmsTemplateDetail;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.resp.SmsResult;

import java.util.List;

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

    /**
     * 查询短信签名
     *
     * @param sign 短信签名
     * @return SmsSignDetail
     */
    SmsSignDetail findSmsSign(String sign);

    /**
     * 查询短信签名
     *
     * @param templateId 模板id
     * @return SmsTemplateDetail
     */
    SmsTemplateDetail findSmsTemplate(String templateId);

}
