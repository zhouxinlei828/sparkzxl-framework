package com.github.sparkzxl.alarm.strategy;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.MsgType;

/**
 * description: 告警消息策略类
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:29:14
 */
public interface MsgHandleStrategy {

    /**
     * 构建消息体
     *
     * @param request 告警请求对象
     * @return MsgType
     */
    MsgType getMessage(AlarmRequest request);

    /**
     * 消息unionId
     *
     * @return String
     */
    String unionId();
}
