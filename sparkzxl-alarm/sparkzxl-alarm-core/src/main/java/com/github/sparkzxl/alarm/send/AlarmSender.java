package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;

/**
 * description: 告警发送
 *
 * @author zhouxinlei
 * @since 2022-05-18 10:53:02
 */
public interface AlarmSender {

    /**
     * 发送消息到指定群
     *
     * @param messageSubType 消息类型{@link MessageSubType}
     * @param request        请求体 {@link AlarmRequest}
     * @return AlarmResponse 响应报文
     */
    AlarmResponse send(MessageSubType messageSubType, AlarmRequest request);

    /**
     * 发送消息到指定群
     *
     * @param alarmType      告警类型 {@link AlarmType}
     * @param messageSubType 消息类型{@link MessageSubType}
     * @param request        请求体 {@link AlarmRequest}
     * @return AlarmResponse 响应报文
     */
    AlarmResponse send(AlarmType alarmType, MessageSubType messageSubType, AlarmRequest request);

}
