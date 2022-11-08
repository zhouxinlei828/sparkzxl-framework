package com.github.sparkzxl.sms.executor;

import com.github.sparkzxl.sms.autoconfigure.SmsProperties;
import org.springframework.beans.factory.InitializingBean;

/**
 * description: 抽象sms执行器
 *
 * @author zhouxinlei
 * @since 2022-09-28 15:12:19
 */
public abstract class AbstractSmsExecutor<C> implements SmsExecutor, InitializingBean {

    private final SmsProperties smsProperties;

    private C client;

    public AbstractSmsExecutor(SmsProperties smsProperties) {
        this.smsProperties = smsProperties;
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

    @Override
    public void afterPropertiesSet() throws Exception {
        this.client = initClient(smsProperties);
    }
}
