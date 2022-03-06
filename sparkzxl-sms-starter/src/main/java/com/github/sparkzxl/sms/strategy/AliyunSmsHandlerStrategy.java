package com.github.sparkzxl.sms.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.*;
import com.aliyun.teaopenapi.models.Config;
import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.constant.enums.SmsChannel;
import com.github.sparkzxl.sms.request.QuerySendDetailsReq;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.response.SendSmsResult;
import com.github.sparkzxl.sms.response.SmsSendDetail;
import com.github.sparkzxl.sms.response.SmsSignDetail;
import com.github.sparkzxl.sms.response.common.SmsListResp;
import com.github.sparkzxl.sms.response.common.SmsResp;
import com.github.sparkzxl.sms.support.SmsException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * description: 阿里云短信发送策略
 *
 * @author zhouxinlei
 * @date 2022-01-03 12:45:45
 */
@Slf4j
public class AliyunSmsHandlerStrategy implements SmsHandlerStrategy, InitializingBean {

    private final SmsProperties smsProperties;

    private Client client;

    public AliyunSmsHandlerStrategy(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public SmsResp<SendSmsResult> sendSms(SendSmsReq sendSmsReq) {
        if (StringUtils.isEmpty(sendSmsReq.getPhoneNumber())) {
            throw new SmsException(500, "手机号不能为空");
        }
        SendSmsRequest smsRequest = new SendSmsRequest()
                .setSignName(sendSmsReq.getSign())
                .setTemplateCode(sendSmsReq.getTemplateId())
                .setTemplateParam(JSONUtil.toJsonStr(sendSmsReq.getTemplateParams()))
                .setPhoneNumbers(sendSmsReq.getPhoneNumber());

        SmsResp<SendSmsResult> resultSmsResp = new SmsResp<>();
        try {
            SendSmsResponse response = client.sendSms(smsRequest);
            SendSmsResponseBody sendSmsResponseBody = response.getBody();
            log.info("阿里云短信发送结果：【{}】", JSONUtil.toJsonStr(sendSmsResponseBody));
            SendSmsResult sendSmsResult = new SendSmsResult();
            resultSmsResp.setCode(sendSmsResponseBody.getCode());
            resultSmsResp.setMessage(sendSmsResponseBody.getMessage());
            resultSmsResp.setRequestId(sendSmsResponseBody.getRequestId());
            sendSmsResult.setBizId(sendSmsResponseBody.getBizId());
            sendSmsResult.setPhoneNumber(sendSmsReq.getPhoneNumber());
            resultSmsResp.setData(sendSmsResult);
            return resultSmsResp;
        } catch (Exception e) {
            log.error("阿里云短信发送异常：", e);
            resultSmsResp.setCode("ERROR");
            resultSmsResp.setMessage(e.getMessage());
            return resultSmsResp;
        }
    }

    @Override
    public SmsListResp<SendSmsResult> sendBatchSms(SendSmsReq sendSmsReq) {
        List<String> phoneNumberList = sendSmsReq.getPhoneNumberList();
        String phoneNumberListStr;
        if (CollectionUtil.isEmpty(phoneNumberList)) {
            throw new SmsException(500, "手机号不能为空");
        }
        phoneNumberListStr = StringUtils.joinWith(",", phoneNumberList);
        SendSmsRequest smsRequest = new SendSmsRequest()
                .setSignName(sendSmsReq.getSign())
                .setTemplateCode(sendSmsReq.getTemplateId())
                .setTemplateParam(JSONUtil.toJsonStr(sendSmsReq.getTemplateParams()))
                .setPhoneNumbers(phoneNumberListStr);

        SmsListResp<SendSmsResult> resultSmsResp = new SmsListResp<>();
        try {
            SendSmsResponse response = client.sendSms(smsRequest);
            SendSmsResponseBody sendSmsResponseBody = response.getBody();
            log.info("阿里云短信发送结果：【{}】", JSONUtil.toJsonStr(sendSmsResponseBody));
            resultSmsResp.setCode(sendSmsResponseBody.getCode());
            resultSmsResp.setMessage(sendSmsResponseBody.getMessage());
            resultSmsResp.setRequestId(sendSmsResponseBody.getRequestId());
            List<SendSmsResult> sendSmsResultList = Lists.newArrayList();
            for (String phoneNumber : phoneNumberList) {
                SendSmsResult sendSmsResult = new SendSmsResult();
                sendSmsResult.setCode("OK");
                sendSmsResult.setMessage("成功");
                sendSmsResult.setBizId(sendSmsResponseBody.getBizId());
                sendSmsResult.setPhoneNumber(phoneNumber);
                sendSmsResultList.add(sendSmsResult);
            }
            resultSmsResp.setDataList(sendSmsResultList);
            return resultSmsResp;
        } catch (Exception e) {
            log.error("阿里云短信发送异常：", e);
            resultSmsResp.setCode("ERROR");
            resultSmsResp.setMessage(e.getMessage());
            return resultSmsResp;
        }
    }

    @Override
    public SmsListResp<SmsSendDetail> querySendDetails(QuerySendDetailsReq querySendDetailsReq) {
        QuerySendDetailsRequest querySendDetailsRequest = new QuerySendDetailsRequest();
        querySendDetailsRequest.setCurrentPage(ObjectUtils.isEmpty(querySendDetailsReq.getPageNum()) ? 1L : querySendDetailsReq.getPageNum());
        querySendDetailsRequest.setPageSize(ObjectUtils.isEmpty(querySendDetailsReq.getPageSize()) ? 10L : querySendDetailsReq.getPageSize());
        if (StringUtils.isNotBlank(querySendDetailsReq.getPhoneNumber())) {
            querySendDetailsRequest.setPhoneNumber(querySendDetailsReq.getPhoneNumber());
        }
        if (ObjectUtils.isNotEmpty(querySendDetailsReq.getSendDate())) {
            String sendDate = DateUtil.format(querySendDetailsReq.getSendDate(), DatePattern.PURE_DATE_PATTERN);
            querySendDetailsRequest.setSendDate(sendDate);
        }
        SmsListResp<SmsSendDetail> smsSendDetailSmsResp = new SmsListResp<>();
        try {
            QuerySendDetailsResponse querySendDetailsResponse = client.querySendDetails(querySendDetailsRequest);
            QuerySendDetailsResponseBody querySendDetailsResponseBody = querySendDetailsResponse.getBody();
            List<QuerySendDetailsResponseBody.QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO> smsSendDetailDTOS =
                    querySendDetailsResponseBody.getSmsSendDetailDTOs().getSmsSendDetailDTO();
            List<SmsSendDetail> smsSendDetailList = Lists.newArrayList();
            for (QuerySendDetailsResponseBody.QuerySendDetailsResponseBodySmsSendDetailDTOsSmsSendDetailDTO smsSendDetailDTO : smsSendDetailDTOS) {
                SmsSendDetail smsSendDetail = new SmsSendDetail()
                        .setSendStatus(smsSendDetailDTO.getSendStatus())
                        .setPhoneNumber(smsSendDetailDTO.getPhoneNum())
                        .setTemplateId(smsSendDetailDTO.getTemplateCode())
                        .setReceiveDate(smsSendDetailDTO.getReceiveDate())
                        .setDescription(smsSendDetailDTO.getContent());
                smsSendDetailList.add(smsSendDetail);
            }
            smsSendDetailSmsResp.setCode(querySendDetailsResponseBody.getCode());
            smsSendDetailSmsResp.setMessage(querySendDetailsResponseBody.getMessage());
            smsSendDetailSmsResp.setDataList(smsSendDetailList);
            return smsSendDetailSmsResp;
        } catch (Exception e) {
            log.error("阿里云短信状态查询异常：", e);
            smsSendDetailSmsResp.setCode("ERROR");
            smsSendDetailSmsResp.setMessage(e.getMessage());
        }
        return smsSendDetailSmsResp;
    }

    @Override
    public SmsResp<SmsSignDetail> querySmsSign(String sign) {
        SmsResp<SmsSignDetail> templateDetailSmsResp = new SmsResp<>();
        try {
            QuerySmsSignRequest querySmsSignRequest = new QuerySmsSignRequest();
            querySmsSignRequest.setSignName(sign);
            QuerySmsSignResponse querySmsSignResponse = client.querySmsSign(querySmsSignRequest);
            QuerySmsSignResponseBody signResponseBody = querySmsSignResponse.getBody();
            templateDetailSmsResp.setCode(signResponseBody.getCode());
            templateDetailSmsResp.setMessage(signResponseBody.getMessage());
            templateDetailSmsResp.setRequestId(signResponseBody.getRequestId());
            SmsSignDetail smsSignDetail = new SmsSignDetail();
            smsSignDetail.setSignName(signResponseBody.getSignName());
            smsSignDetail.setStatus(signResponseBody.getSignStatus());
            smsSignDetail.setCreateDate(signResponseBody.getCreateDate());
            smsSignDetail.setReason(signResponseBody.getReason());
            templateDetailSmsResp.setData(smsSignDetail);
        } catch (Exception e) {
            log.error("阿里云短信查询签名异常：", e);
            templateDetailSmsResp.setCode("ERROR");
            templateDetailSmsResp.setMessage(e.getMessage());
        }
        return templateDetailSmsResp;
    }

    @Override
    public String support() {
        return SmsChannel.ALIYUN.getValue();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Config config = new Config()
                .setAccessKeyId(smsProperties.getAccessKeyId())
                .setAccessKeySecret(smsProperties.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsProperties.getEndpoint();
        this.client = new Client(config);
    }
}
