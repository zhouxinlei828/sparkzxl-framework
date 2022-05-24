package com.github.sparkzxl.alarm.handler;

import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.properties.AlarmConfig;

import java.util.Map;

/**
 * description: 告警执行器
 *
 * @author zhouxinlei
 * @since 2022-05-24 08:59:21
 */
public interface AlarmExecutor {

    /**
     * 发送通知
     *
     * @param message   通知消息
     * @param variables 变量参数
     * @param <T>       泛型
     * @return AlarmResponse
     */
    <T extends MsgType> AlarmResponse send(T message, Map<String, Object> variables);

    /**
     * 指定机器人发送通知
     *
     * @param robotId   告警机器人id {@link AlarmConfig#getRobotId()}
     * @param message   通知消息
     * @param variables 变量参数
     * @param <T>       泛型
     * @return AlarmResponse
     */
    <T extends MsgType> AlarmResponse designatedRobotSend(String robotId, T message, Map<String, Object> variables);


    /**
     * 执行器名称
     *
     * @return String
     */
    String named();

}
