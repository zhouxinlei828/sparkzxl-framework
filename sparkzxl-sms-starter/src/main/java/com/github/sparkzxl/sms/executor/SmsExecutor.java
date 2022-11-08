package com.github.sparkzxl.sms.executor;

import com.github.sparkzxl.sms.entity.SmsSendRecord;
import com.github.sparkzxl.sms.entity.SmsSignDetail;
import com.github.sparkzxl.sms.entity.SmsTemplateDetail;
import com.github.sparkzxl.sms.request.SendSmsReq;

import java.util.List;

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
     * @return SmsResp<SendSmsResult> 发送结果
     */
    List<SmsSendRecord> send(SendSmsReq sendSmsReq);

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

    /**
     * 支持类型名称
     *
     * @return String
     */
    String named();
}
