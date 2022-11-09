package com.github.sparkzxl.sms.event;

import com.github.sparkzxl.sms.constant.enums.SmsRegister;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationEvent;

import java.util.Map;
import java.util.Set;

/**
 * description: 发送成功事件
 *
 * @author zhouxinlei
 * @since 2022-11-09 09:15:24
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class SmsSendSuccessEvent extends ApplicationEvent {

    /**
     * 发送渠道.
     */
    private final String smsRegister;

    /**
     * 电话号码列表.
     */
    private final Set<String> phones;

    /**
     * 参数列表.
     */
    private final Map<String, Object> params;

    public SmsSendSuccessEvent(String response, String smsRegister, Set<String> phones, Map<String, Object> params) {
        super(response);
        this.smsRegister = smsRegister;
        this.phones = phones;
        this.params = params;
    }
}
