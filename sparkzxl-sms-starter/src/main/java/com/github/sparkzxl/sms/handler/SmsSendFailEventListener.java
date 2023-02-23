package com.github.sparkzxl.sms.handler;

import com.github.sparkzxl.sms.event.SmsSendFailEvent;
import org.springframework.context.ApplicationListener;

/**
 * description: 发送失败事件监听接口.
 *
 * @author zhouxinlei
 * @since 2022-11-09 11:30:15
 */
public interface SmsSendFailEventListener extends ApplicationListener<SmsSendFailEvent> {

}
