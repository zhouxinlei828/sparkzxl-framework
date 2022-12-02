package com.github.sparkzxl.sms.executor;

import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import com.github.sparkzxl.sms.event.SmsSendFailEvent;
import com.github.sparkzxl.sms.event.SmsSendSuccessEvent;
import com.github.sparkzxl.sms.request.SendSmsReq;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;

/**
 * description: 抽象sms执行器
 *
 * @author zhouxinlei
 * @since 2022-09-28 15:12:19
 */
public abstract class AbstractSmsExecutor<C> implements SmsExecutor, InitializingBean {

    protected final SmsProperties smsProperties;
    private final ApplicationEventPublisher eventPublisher;

    private C client;

    public AbstractSmsExecutor(SmsProperties smsProperties,
                               ApplicationEventPublisher eventPublisher) {
        this.smsProperties = smsProperties;
        this.eventPublisher = eventPublisher;
    }

    protected C obtainClient() {
        return client;
    }

    /**
     * 初始化客户端信息
     *
     * @param smsProperties 短信配置信息
     * @return C
     * @throws Exception 配置异常
     */
    protected abstract C initClient(SmsProperties smsProperties) throws Exception;

    /**
     * 发布发送成功事件.
     *
     * @param response   发送结果
     * @param sendSmsReq 发送参数
     */
    protected final void publishSendSuccessEvent(String response, String content, SendSmsReq sendSmsReq) {
        if (eventPublisher == null) {
            return;
        }
        eventPublisher.publishEvent(
                new SmsSendSuccessEvent(response, named(), sendSmsReq.getPhones(), content, sendSmsReq.getTemplateParams()));
    }

    /**
     * 发布发送失败事件.
     *
     * @param response   发送结果
     * @param sendSmsReq 发送参数
     * @param cause      源异常
     */
    protected final void publishSendFailEvent(String response, SendSmsReq sendSmsReq, Throwable cause) {
        if (eventPublisher == null) {
            return;
        }
        eventPublisher.publishEvent(
                new SmsSendFailEvent(response, named(), sendSmsReq.getPhones(), sendSmsReq.getTemplateParams(), cause));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.client = initClient(smsProperties);
    }
}
