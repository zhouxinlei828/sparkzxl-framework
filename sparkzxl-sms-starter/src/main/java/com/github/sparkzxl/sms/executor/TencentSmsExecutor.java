package com.github.sparkzxl.sms.executor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import com.github.sparkzxl.sms.entity.SmsResult;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.support.SmsException;
import com.github.sparkzxl.sms.support.SmsExceptionCodeEnum;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.CollectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * description: 腾讯云短信发送策略
 *
 * @author zhouxinlei
 * @since 2022-01-03 12:45:45
 */
@Slf4j
public class TencentSmsExecutor extends AbstractSmsExecutor<SmsClient> {

    public TencentSmsExecutor(SmsProperties smsProperties, ApplicationEventPublisher eventPublisher) {
        super(smsProperties, eventPublisher);
    }

    @Override
    public SmsResult send(SendSmsReq sendSmsReq) {
        Set<String> phones = sendSmsReq.getPhones();
        if (CollectionUtils.isEmpty(phones)) {
            throw new SmsException(SmsExceptionCodeEnum.PHONE_IS_EMPTY);
        }
        try {
            SendSmsRequest req = new SendSmsRequest();
            req.setSmsSdkAppId(smsProperties.getSdkAppId());
            req.setSignName(sendSmsReq.getSign());
            req.setTemplateId(sendSmsReq.getTemplateId());
            String[] phoneNumberSet = phones.stream().map(String::valueOf).toArray(String[]::new);
            req.setPhoneNumberSet(phoneNumberSet);
            /* 模板参数: 若无模板参数，则设置为空 */
            Map<String, Object> templateParamMap = sendSmsReq.getTemplateParams();
            if (MapUtil.isNotEmpty(templateParamMap)) {
                String[] templateParamSet = templateParamMap.values().stream().map(String::valueOf).toArray(String[]::new);
                req.setTemplateParamSet(templateParamSet);
            }
            req.setSessionContext(IdUtil.fastSimpleUUID());
            SendSmsResponse response = obtainClient().SendSms(req);
            String sendSmsResponseJson = SendSmsResponse.toJsonString(response);
            // 输出json格式的字符串回包
            log.info("腾讯云短信发送结果：【{}】", sendSmsResponseJson);
            boolean success = true;
            String code = SmsExceptionCodeEnum.SUCCESS.getErrorCode();
            String message = "";
            for (SendStatus sendStatus : response.getSendStatusSet()) {
                if (!"Ok".equals(sendStatus.getCode())) {
                    success = false;
                    code = SmsExceptionCodeEnum.SMS_SEND_FAIL.getErrorCode();
                    message = sendStatus.getMessage();
                    break;
                }
            }
            if (!success) {
                publishSendFailEvent(sendSmsResponseJson, sendSmsReq, new SmsException(SmsExceptionCodeEnum.SMS_SEND_FAIL.getErrorCode(), message));
            }
            publishSendSuccessEvent(sendSmsResponseJson, sendSmsReq);
            return SmsResult.builder()
                    .code(code)
                    .isSuccess(success)
                    .response(sendSmsResponseJson)
                    .message(message).build();
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信发送异常：", e);
            publishSendFailEvent(null, sendSmsReq, e);
            return SmsResult.builder()
                    .code(SmsExceptionCodeEnum.SMS_SEND_FAIL.getErrorCode())
                    .isSuccess(false)
                    .message(e.getMessage())
                    .build();
        }
    }

    @Override
    protected SmsClient initClient(SmsProperties smsProperties) throws Exception {
        /* 必要步骤：
         * 实例化一个认证对象，入参需要传入腾讯云账户密钥对secretId，secretKey。
         * 这里采用的是从环境变量读取的方式，需要在环境变量中先设置这两个值。
         * 你也可以直接在代码中写死密钥对，但是小心不要将代码复制、上传或者分享给他人，
         * 以免泄露密钥对危及你的财产安全。
         * CAM密匙查询: https://console.cloud.tencent.com/cam/capi*/
        Credential cred = new Credential(smsProperties.getAccessKeyId(), smsProperties.getAccessKeySecret());
        // 实例化一个http选项，可选，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod(HttpProfile.REQ_POST);
        /* SDK会自动指定域名。通常是不需要特地指定域名的，但是如果你访问的是金融区的服务
         * 则必须手动指定域名，例如sms的上海金融区域名： sms.ap-shanghai-fsi.tencentcloudapi.com */
        if (StringUtils.isNotBlank(smsProperties.getEndpoint())) {
            httpProfile.setEndpoint(smsProperties.getEndpoint());
        } else {
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
        }

        /* 非必要步骤:
         * 实例化一个客户端配置对象，可以指定超时时间等配置 */
        ClientProfile clientProfile = new ClientProfile();
        /* SDK默认用TC3-HMAC-SHA256进行签名
         * 非必要请不要修改这个字段 */
        clientProfile.setSignMethod("HmacSHA256");
        clientProfile.setHttpProfile(httpProfile);
        /* 实例化要请求产品(以sms为例)的client对象
         * 第二个参数是地域信息，可以直接填写字符串ap-guangzhou，或者引用预设的常量 */
        return new SmsClient(cred, smsProperties.getRegion(), clientProfile);
    }

    @Override
    public String named() {
        return SmsRegister.TENCENT.getCode();
    }
}
