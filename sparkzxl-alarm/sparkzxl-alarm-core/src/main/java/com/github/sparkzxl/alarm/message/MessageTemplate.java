package com.github.sparkzxl.alarm.message;

import com.github.sparkzxl.alarm.entity.AlarmRequest;

/**
 * description: 自定义消息接口
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:28:40
 */
public interface MessageTemplate {

    /**
     * 自定义消息
     *
     * @param request 消息请求体 {@link com.github.sparkzxl.alarm.entity.AlarmRequest}
     * @return String 消息内容字符串
     */
    String message(AlarmRequest request);
}
