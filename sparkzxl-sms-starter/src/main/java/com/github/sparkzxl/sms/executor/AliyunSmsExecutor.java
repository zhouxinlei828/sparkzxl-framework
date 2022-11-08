package com.github.sparkzxl.sms.executor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.constant.enums.SmsStatus;
import com.github.sparkzxl.sms.entity.SmsSendRecord;
import com.github.sparkzxl.sms.entity.SmsSignDetail;
import com.github.sparkzxl.sms.entity.SmsTemplateDetail;
import com.github.sparkzxl.sms.parser.TemplateParamParser;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.support.SmsException;
import com.github.sparkzxl.sms.support.SmsExceptionCodeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * description: 阿里云短信发送策略
 *
 * @author zhouxinlei
 * @since 2022-01-03 12:45:45
 */
@Slf4j
public class AliyunSmsExecutor extends AbstractSmsExecutor<Client> {

    public AliyunSmsExecutor(SmsProperties smsProperties) {
        super(smsProperties);
    }

    @Override
    public List<SmsSendRecord> send(SendSmsReq sendSmsReq) {
        Set<String> phones = sendSmsReq.getPhones();
        if (CollectionUtils.isEmpty(phones)) {
            throw new SmsException(SmsExceptionCodeEnum.PHONE_IS_EMPTY);
        }
        String phoneNumberListStr = StringUtils.joinWith(",", phones);
        try {
            SendSmsRequest smsRequest = new SendSmsRequest()
                    .setSignName(sendSmsReq.getSign())
                    .setTemplateCode(sendSmsReq.getTemplateId())
                    .setTemplateParam(JSONUtil.toJsonStr(sendSmsReq.getTemplateParams()))
                    .setPhoneNumbers(phoneNumberListStr);
            SendSmsResponse response = obtainClient().sendSms(smsRequest);
            SendSmsResponseBody sendSmsResponseBody = response.getBody();
            String smsResponseBody = JSON.toJSONString(sendSmsResponseBody);
            log.info("阿里云短信发送结果：【{}】", smsResponseBody);
            LocalDateTime sendDateTime = LocalDateTime.now();
            String content = sendSmsReq.getTemplateContent();
            if (MapUtil.isNotEmpty(sendSmsReq.getTemplateParams())) {
                content = TemplateParamParser.replaceContent(content, sendSmsReq.getTemplateParams());
            }
            List<SmsSendRecord> sendRecordList = Lists.newArrayList();
            for (String phone : phones) {
                SmsSendRecord smsSendRecord = new SmsSendRecord()
                        .setId(IdUtil.getSnowflake().nextId())
                        .setPhone(phone)
                        .setBizId(sendSmsResponseBody.getBizId())
                        .setMsgContent(content)
                        .setSupplierId(SmsRegister.ALIYUN.getId())
                        .setSupplierName(SmsRegister.ALIYUN.getName())
                        .setReportContent(smsResponseBody)
                        .setStatus(SmsStatus.SEND_SUCCESS.getCode())
                        .setStatusName(SmsStatus.SEND_SUCCESS.getDescription())
                        .setSendDateTime(sendDateTime);
                sendRecordList.add(smsSendRecord);
            }
            return sendRecordList;
        } catch (Exception e) {
            log.error("阿里云短信发送异常：", e);
            return null;
        }
    }

    @Override
    public SmsSignDetail findSmsSign(String sign) {
        try {
            QuerySmsSignRequest querySmsSignRequest = new QuerySmsSignRequest();
            querySmsSignRequest.setSignName(sign);
            QuerySmsSignResponse querySmsSignResponse = obtainClient().querySmsSign(querySmsSignRequest);
            QuerySmsSignResponseBody signResponseBody = querySmsSignResponse.getBody();
            SmsSignDetail smsSignDetail = new SmsSignDetail();
            smsSignDetail.setSignName(signResponseBody.getSignName());
            smsSignDetail.setStatus(signResponseBody.getSignStatus());
            smsSignDetail.setCreateDate(signResponseBody.getCreateDate());
            smsSignDetail.setReason(signResponseBody.getReason());
            return smsSignDetail;
        } catch (Exception e) {
            log.error("阿里云短信查询签名异常：", e);
            return null;
        }
    }

    @Override
    public SmsTemplateDetail findSmsTemplate(String templateId) {
        try {
            QuerySmsTemplateRequest request = new QuerySmsTemplateRequest();
            request.setTemplateCode(templateId);
            QuerySmsTemplateResponse smsTemplateResponse = obtainClient().querySmsTemplate(request);
            QuerySmsTemplateResponseBody templateResponseBody = smsTemplateResponse.getBody();
            SmsTemplateDetail smsTemplateDetail = new SmsTemplateDetail();
            smsTemplateDetail.setTemplateId(templateResponseBody.getTemplateCode());
            smsTemplateDetail.setTemplateName(templateResponseBody.getTemplateName());
            smsTemplateDetail.setTemplateContent(templateResponseBody.getTemplateContent());
            smsTemplateDetail.setTemplateStatus(templateResponseBody.getTemplateStatus());
            smsTemplateDetail.setCreateDate(templateResponseBody.getCreateDate());
            smsTemplateDetail.setReason(templateResponseBody.getReason());
            return smsTemplateDetail;
        } catch (Exception e) {
            log.error("阿里云短信查询签名模板异常：", e);
        }
        return null;
    }

    @Override
    protected Client initClient(SmsProperties smsProperties) throws Exception{
        Config config = new Config()
                .setAccessKeyId(smsProperties.getAccessKeyId())
                .setAccessKeySecret(smsProperties.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsProperties.getEndpoint();
        return new Client(config);
    }

    @Override
    public String named() {
        return SmsRegister.ALIYUN.getName();
    }
}
