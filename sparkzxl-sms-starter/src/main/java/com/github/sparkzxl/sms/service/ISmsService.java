package com.github.sparkzxl.sms.service;

import com.github.sparkzxl.sms.request.QuerySendDetailsReq;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.response.SendSmsResult;
import com.github.sparkzxl.sms.response.SmsSendDetail;
import com.github.sparkzxl.sms.response.SmsSignDetail;
import com.github.sparkzxl.sms.response.common.SmsListResp;
import com.github.sparkzxl.sms.response.common.SmsResp;

/**
 * description: 短信接口API
 *
 * @author zhouxinlei
 * @date 2022-01-03 14:38:41
 */
public interface ISmsService {

    /**
     * 发送短信
     *
     * @param sendSmsReq 短信入参
     * @return SendSmsResp
     */
    SmsResp<SendSmsResult> sendSms(SendSmsReq sendSmsReq);

    /**
     * 发送短信
     *
     * @param sendSmsReq 短信入参
     * @return SendSmsResp
     */
    SmsListResp<SendSmsResult> sendBatchSms(SendSmsReq sendSmsReq);

    /**
     * 查询短信发送记录
     *
     * @param querySendDetailsReq 查询发送详情入参
     * @return SmsListResp<SmsSendDetail> 发送状态结果
     */
    SmsListResp<SmsSendDetail> querySendDetails(QuerySendDetailsReq querySendDetailsReq);

    /**
     * 查询短信签名
     *
     * @param sign 短信签名
     * @return SmsResp<SmsSendDetail>
     */
    SmsResp<SmsSignDetail> querySmsSign(String sign);

}
