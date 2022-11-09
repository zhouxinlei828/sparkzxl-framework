package com.github.sparkzxl.sms.executor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import com.aliyun.teaopenapi.models.Config;
import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.entity.SmsResult;
import com.github.sparkzxl.sms.parser.TemplateParamParser;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.support.SmsException;
import com.github.sparkzxl.sms.support.SmsExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * description: 阿里云短信发送
 *
 * @author zhouxinlei
 * @since 2022-01-03 12:45:45
 */
@Slf4j
public class AliYunSmsExecutor extends AbstractSmsExecutor<Client> {

    public AliYunSmsExecutor(SmsProperties smsProperties, ApplicationEventPublisher eventPublisher) {
        super(smsProperties, eventPublisher);
    }

    @Override
    public SmsResult send(SendSmsReq sendSmsReq) {
        Set<String> phones = sendSmsReq.getPhones();
        if (CollectionUtils.isEmpty(phones)) {
            throw new SmsException(SmsExceptionCodeEnum.PHONE_IS_EMPTY);
        }
        String phoneNumberListStr = StringUtils.joinWith(",", phones);
        try {
            SendSmsRequest smsRequest = new SendSmsRequest().setSignName(sendSmsReq.getSign()).setTemplateCode(sendSmsReq.getTemplateId()).setTemplateParam(JSONUtil.toJsonStr(sendSmsReq.getTemplateParams())).setPhoneNumbers(phoneNumberListStr);
            SendSmsResponse response = obtainClient().sendSms(smsRequest);
            SendSmsResponseBody sendSmsResponseBody = response.getBody();
            String smsResponseBody = JSON.toJSONString(sendSmsResponseBody);
            log.info("阿里云短信发送结果：【{}】", smsResponseBody);
            boolean success = "OK".equals(sendSmsResponseBody.getCode());

            if (!success) {
                publishSendFailEvent(smsResponseBody, sendSmsReq, new SmsException(sendSmsResponseBody.getCode(), sendSmsResponseBody.getMessage()));
            }
            String content = sendSmsReq.getTemplateContent();
            if (MapUtil.isNotEmpty(sendSmsReq.getTemplateParams())) {
                content = TemplateParamParser.replaceContent(content, sendSmsReq.getTemplateParams());
            }
            publishSendSuccessEvent(smsResponseBody, content, sendSmsReq);
            return SmsResult.builder().code(SmsExceptionCodeEnum.SUCCESS.getErrorCode()).isSuccess(success).message(sendSmsResponseBody.getMessage()).response(smsResponseBody).build();
        } catch (Exception e) {
            log.error("阿里云短信发送异常：", e);
            publishSendFailEvent(null, sendSmsReq, e);
            return SmsResult.builder().code(SmsExceptionCodeEnum.SMS_SEND_FAIL.getErrorCode()).isSuccess(false).message(e.getMessage()).build();
        }
    }

    @Override
    protected Client initClient(SmsProperties smsProperties) throws Exception {
        Config config = new Config().setAccessKeyId(smsProperties.getAccessKeyId()).setAccessKeySecret(smsProperties.getAccessKeySecret());
        // 访问的域名
        config.endpoint = smsProperties.getEndpoint();
        return new Client(config);
    }

    @Override
    public String named() {
        return SmsRegister.ALIYUN.getCode();
    }
}
