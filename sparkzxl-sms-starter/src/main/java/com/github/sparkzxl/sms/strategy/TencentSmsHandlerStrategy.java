package com.github.sparkzxl.sms.strategy;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.constant.enums.SendStatusEnum;
import com.github.sparkzxl.sms.constant.enums.SmsChannel;
import com.github.sparkzxl.sms.request.QuerySendDetailsReq;
import com.github.sparkzxl.sms.request.SendSmsReq;
import com.github.sparkzxl.sms.response.SendSmsResult;
import com.github.sparkzxl.sms.response.SmsSignDetail;
import com.github.sparkzxl.sms.response.common.SmsListResp;
import com.github.sparkzxl.sms.response.common.SmsResp;
import com.github.sparkzxl.sms.response.SmsSendDetail;
import com.github.sparkzxl.sms.support.SmsException;
import com.google.common.collect.Lists;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20190711.SmsClient;
import com.tencentcloudapi.sms.v20190711.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * description: 腾讯云短信发送策略
 *
 * @author zhouxinlei
 * @date 2022-01-03 12:45:45
 */
@Slf4j
public class TencentSmsHandlerStrategy implements SmsHandlerStrategy, InitializingBean {

    private final SmsProperties smsProperties;
    private SmsClient client;

    private final static String CN_PREFIX = "+86";

    public TencentSmsHandlerStrategy(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
    }

    @Override
    public SmsResp<SendSmsResult> sendSms(SendSmsReq sendSmsReq) {
        SmsResp<SendSmsResult> sendSmsResultSmsResp = new SmsResp<>();
        try {
            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 你可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
            SendSmsRequest req = new SendSmsRequest();

            /* 填充请求参数,这里request对象的成员变量即对应接口的入参
             * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
             * 基本类型的设置:
             * 帮助链接：
             * 短信控制台: https://console.cloud.tencent.com/smsv2
             * sms helper: https://cloud.tencent.com/document/product/382/3773 */

            /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
            req.setSmsSdkAppid(smsProperties.getSdkAppId());
            /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
            req.setSign(sendSmsReq.getSign());

            /* 国际/港澳台短信 SenderId: 国内短信填空，默认未开通，如需开通请联系 [sms helper] */
            String senderid = "";
            req.setSenderId(senderid);

            /* 短信号码扩展号: 默认未开通，如需开通请联系 [sms helper] */
            String extendCode = "";
            req.setExtendCode(extendCode);

            /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
            req.setTemplateID(sendSmsReq.getTemplateId());

            /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
            String phoneNumber = CN_PREFIX.concat(sendSmsReq.getPhoneNumber());
            String[] phoneNumberSet = {phoneNumber};
            req.setPhoneNumberSet(phoneNumberSet);
            /* 模板参数: 若无模板参数，则设置为空 */
            Map<String, String> templateParamMap = sendSmsReq.getTemplateParams();
            if (MapUtil.isNotEmpty(templateParamMap)) {
                String[] templateParamSet = ArrayUtil.toArray(templateParamMap.values(), String.class);
                req.setTemplateParamSet(templateParamSet);
            }

            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse sendSmsResponse = client.SendSms(req);

            // 输出json格式的字符串回包
            log.info("腾讯云短信发送结果：【{}】", SendSmsResponse.toJsonString(sendSmsResponse));
            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            SendStatus[] sendStatusSet = sendSmsResponse.getSendStatusSet();
            SendStatus sendStatus = sendStatusSet[0];
            System.out.println(sendSmsResponse.getRequestId());
            sendSmsResultSmsResp.setCode(sendStatus.getCode());
            sendSmsResultSmsResp.setMessage(sendStatus.getMessage());
            sendSmsResultSmsResp.setRequestId(sendSmsResponse.getRequestId());

            SendSmsResult sendSmsResult = new SendSmsResult();
            sendSmsResult.setBizId(sendStatus.getSerialNo());
            sendSmsResult.setPhoneNumber(sendStatus.getPhoneNumber());
            sendSmsResultSmsResp.setData(sendSmsResult);
            return sendSmsResultSmsResp;
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信发送异常：", e);
            sendSmsResultSmsResp.setCode("ERROR");
            sendSmsResultSmsResp.setMessage(e.getMessage());
            return sendSmsResultSmsResp;
        }
    }

    @Override
    public SmsListResp<SendSmsResult> sendBatchSms(SendSmsReq sendSmsReq) {
        SmsListResp<SendSmsResult> smsResultSmsListResp = new SmsListResp<>();
        try {
            /* 实例化一个请求对象，根据调用的接口和实际情况，可以进一步设置请求参数
             * 你可以直接查询SDK源码确定接口有哪些属性可以设置
             * 属性可能是基本类型，也可能引用了另一个数据结构
             * 推荐使用IDE进行开发，可以方便的跳转查阅各个接口和数据结构的文档说明 */
            SendSmsRequest req = new SendSmsRequest();

            /* 填充请求参数,这里request对象的成员变量即对应接口的入参
             * 你可以通过官网接口文档或跳转到request对象的定义处查看请求参数的定义
             * 基本类型的设置:
             * 帮助链接：
             * 短信控制台: https://console.cloud.tencent.com/smsv2
             * sms helper: https://cloud.tencent.com/document/product/382/3773 */

            /* 短信应用ID: 短信SdkAppId在 [短信控制台] 添加应用后生成的实际SdkAppId，示例如1400006666 */
            req.setSmsSdkAppid(smsProperties.getSdkAppId());
            /* 短信签名内容: 使用 UTF-8 编码，必须填写已审核通过的签名，签名信息可登录 [短信控制台] 查看 */
            req.setSign(sendSmsReq.getSign());

            /* 国际/港澳台短信 SenderId: 国内短信填空，默认未开通，如需开通请联系 [sms helper] */
            String senderid = "";
            req.setSenderId(senderid);

            /* 短信号码扩展号: 默认未开通，如需开通请联系 [sms helper] */
            String extendCode = "";
            req.setExtendCode(extendCode);

            /* 模板 ID: 必须填写已审核通过的模板 ID。模板ID可登录 [短信控制台] 查看 */
            req.setTemplateID(sendSmsReq.getTemplateId());

            /* 下发手机号码，采用 E.164 标准，+[国家或地区码][手机号]
             * 示例如：+8613711112222， 其中前面有一个+号 ，86为国家码，13711112222为手机号，最多不要超过200个手机号 */
            List<String> phoneNumberList = sendSmsReq.getPhoneNumberList();
            if (CollectionUtil.isEmpty(phoneNumberList)) {
                throw new SmsException(500, "手机号不能为空");
            }
            List<String> phoneList = Lists.newArrayList();
            for (String phone : phoneNumberList) {
                phoneList.add(CN_PREFIX.concat(phone));
            }
            String[] phoneNumberSet = ArrayUtil.toArray(phoneList, String.class);
            req.setPhoneNumberSet(phoneNumberSet);

            /* 模板参数: 若无模板参数，则设置为空 */
            Map<String, String> templateParamMap = sendSmsReq.getTemplateParams();
            if (MapUtil.isNotEmpty(templateParamMap)) {
                String[] templateParamSet = ArrayUtil.toArray(templateParamMap.values(), String.class);
                req.setTemplateParamSet(templateParamSet);
            }

            /* 通过 client 对象调用 SendSms 方法发起请求。注意请求方法名与请求对象是对应的
             * 返回的 res 是一个 SendSmsResponse 类的实例，与请求对象对应 */
            SendSmsResponse sendSmsResponse = client.SendSms(req);

            // 输出json格式的字符串回包
            log.info("腾讯云短信发送结果：【{}】", SendSmsResponse.toJsonString(sendSmsResponse));
            // 也可以取出单个值，你可以通过官网接口文档或跳转到response对象的定义处查看返回字段的定义
            SendStatus[] sendStatusSet = sendSmsResponse.getSendStatusSet();
            List<SendSmsResult> sendSmsResultList = Lists.newArrayList();
            for (SendStatus sendStatus : sendStatusSet) {
                SendSmsResult sendSmsResult = new SendSmsResult();
                sendSmsResult.setCode(sendStatus.getCode());
                sendSmsResult.setMessage(sendStatus.getMessage());
                sendSmsResult.setBizId(sendStatus.getSerialNo());
                sendSmsResult.setPhoneNumber(sendStatus.getPhoneNumber());
                sendSmsResultList.add(sendSmsResult);
            }
            smsResultSmsListResp.setCode("OK");
            smsResultSmsListResp.setMessage("成功");
            smsResultSmsListResp.setRequestId(sendSmsResponse.getRequestId());
            smsResultSmsListResp.setDataList(sendSmsResultList);
            return smsResultSmsListResp;
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云短信发送异常：", e);
            smsResultSmsListResp.setCode("ERROR");
            smsResultSmsListResp.setMessage(e.getMessage());
            return smsResultSmsListResp;
        }
    }

    @Override
    public SmsListResp<SmsSendDetail> querySendDetails(QuerySendDetailsReq querySendDetailsReq) {
        PullSmsSendStatusByPhoneNumberRequest pullSmsSendStatusByPhoneNumber = new PullSmsSendStatusByPhoneNumberRequest();
        pullSmsSendStatusByPhoneNumber.setOffset(ObjectUtils.isEmpty(querySendDetailsReq.getPageNum()) ? 1L : querySendDetailsReq.getPageNum());
        pullSmsSendStatusByPhoneNumber.setLimit(ObjectUtils.isEmpty(querySendDetailsReq.getPageSize()) ? 10L : querySendDetailsReq.getPageSize());
        if (StringUtils.isNotBlank(querySendDetailsReq.getPhoneNumber())) {
            pullSmsSendStatusByPhoneNumber.setPhoneNumber(CN_PREFIX.concat(querySendDetailsReq.getPhoneNumber()));
        }
        if (ObjectUtils.isNotEmpty(querySendDetailsReq.getSendDate())) {
            ZoneId zoneId = ZoneId.systemDefault();
            ZonedDateTime zdt = querySendDetailsReq.getSendDate().atZone(zoneId);
            Date date = Date.from(zdt.toInstant());
            DateTime startTime = DateUtil.beginOfDay(date);
            pullSmsSendStatusByPhoneNumber.setSendDateTime(startTime.getTime());
            DateTime endTime = DateUtil.endOfDay(date);
            pullSmsSendStatusByPhoneNumber.setEndDateTime(endTime.getTime());
        }
        SmsListResp<SmsSendDetail> smsSendDetailSmsListResp = new SmsListResp<>();
        try {
            PullSmsSendStatusByPhoneNumberResponse pullSmsSendStatusByPhoneNumberResponse = client.PullSmsSendStatusByPhoneNumber(pullSmsSendStatusByPhoneNumber);
            PullSmsSendStatus[] pullSmsSendStatusSet = pullSmsSendStatusByPhoneNumberResponse.getPullSmsSendStatusSet();
            List<PullSmsSendStatus> smsSendStatusList = Lists.newArrayList(pullSmsSendStatusSet);
            List<SmsSendDetail> smsSendDetailList = Lists.newArrayList();
            for (PullSmsSendStatus pullSmsSendStatus : smsSendStatusList) {
                SendStatusEnum sendStatusEnum = SendStatusEnum.get(pullSmsSendStatus.getReportStatus());
                SmsSendDetail smsSendDetail = new SmsSendDetail()
                        .setSendStatus(ObjectUtils.isEmpty(sendStatusEnum) ? null : sendStatusEnum.getCode())
                        .setPhoneNumber(pullSmsSendStatus.getPurePhoneNumber())
                        .setTemplateId(pullSmsSendStatus.getSerialNo())
                        .setReceiveDate(pullSmsSendStatus.getUserReceiveTime())
                        .setDescription(pullSmsSendStatus.getDescription());
                smsSendDetailList.add(smsSendDetail);
            }
            smsSendDetailSmsListResp.setCode("OK");
            smsSendDetailSmsListResp.setMessage("成功");
            smsSendDetailSmsListResp.setRequestId(pullSmsSendStatusByPhoneNumberResponse.getRequestId());
            smsSendDetailSmsListResp.setDataList(smsSendDetailList);
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云查询短信发送记录异常：", e);
            smsSendDetailSmsListResp.setCode("ERROR");
            smsSendDetailSmsListResp.setMessage(e.getMessage());
        }
        return smsSendDetailSmsListResp;
    }

    @Override
    public SmsResp<SmsSignDetail> querySmsSign(String sign) {
        SmsResp<SmsSignDetail> smsSignDetailSmsResp = new SmsResp<>();
        try {
            DescribeSmsSignListRequest describeSmsSignListRequest = new DescribeSmsSignListRequest();
            Long[] signIdSet = {Long.valueOf(sign)};
            describeSmsSignListRequest.setSignIdSet(signIdSet);
            DescribeSmsSignListResponse describeSmsSignListResponse = client.DescribeSmsSignList(describeSmsSignListRequest);
            smsSignDetailSmsResp.setCode("OK");
            smsSignDetailSmsResp.setMessage("成功");
            smsSignDetailSmsResp.setRequestId(describeSmsSignListResponse.getRequestId());
            DescribeSignListStatus[] describeSignListStatusSet = describeSmsSignListResponse.getDescribeSignListStatusSet();
            if (ArrayUtils.isNotEmpty(describeSignListStatusSet)) {
                DescribeSignListStatus describeSignListStatus = describeSignListStatusSet[0];
                SmsSignDetail smsSignDetail = new SmsSignDetail();
                smsSignDetail.setSignId(describeSignListStatus.getSignId());
                smsSignDetail.setSignName(describeSignListStatus.getSignName());
                smsSignDetail.setStatus(describeSignListStatus.getStatusCode().intValue());
                smsSignDetail.setReason(describeSignListStatus.getReviewReply());
                smsSignDetail.setCreateDate(DateUtil.format(DateUtil.date(describeSignListStatus.getCreateTime()),
                        DatePattern.NORM_DATETIME_PATTERN));
                smsSignDetailSmsResp.setData(smsSignDetail);
            }
        } catch (TencentCloudSDKException e) {
            log.error("腾讯云查询短信签名异常：", e);
            smsSignDetailSmsResp.setCode("ERROR");
            smsSignDetailSmsResp.setMessage("失败");
        }
        return smsSignDetailSmsResp;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
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
        this.client = new SmsClient(cred, smsProperties.getRegion(), clientProfile);
    }

    @Override
    public String support() {
        return SmsChannel.TENCENT.getValue();
    }
}
