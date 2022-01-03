package com.github.sparkzxl.sms.strategy;

import com.github.sparkzxl.sms.request.QuerySendDetailsReq;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.response.SendSmsResult;
import com.github.sparkzxl.sms.response.SmsSignDetail;
import com.github.sparkzxl.sms.response.common.SmsListResp;
import com.github.sparkzxl.sms.response.common.SmsResp;
import com.github.sparkzxl.sms.response.SmsSendDetail;

/**
 * description: 短信处理策略类
 *
 * @author zhouxinlei
 * @date 2022-01-03 12:31:56
 */
public interface SmsHandlerStrategy {

    /**
     * 发送短信
     *
     * @param sendSmsReq 发送参数
     * @return SmsResp<SendSmsResult> 发送结果
     */
    SmsResp<SendSmsResult> sendSms(SendSmsReq sendSmsReq);

    /**
     * 批量发送短信
     *
     * @param sendSmsReq 发送参数
     * @return SmsListResp<SendSmsResult> 发送结果
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

    /**
     * 支持类型
     *
     * @return String
     */
    String support();
}
