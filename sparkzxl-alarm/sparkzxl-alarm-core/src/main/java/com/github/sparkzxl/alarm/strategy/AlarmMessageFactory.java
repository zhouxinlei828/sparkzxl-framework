package com.github.sparkzxl.alarm.strategy;

import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.ObjectUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

/**
 * description: 告警消息工厂
 *
 * @author zhouxinlei
 * @since 2022-07-05 16:59:08
 */
public class AlarmMessageFactory {

    private final Map<String, MsgHandleStrategy> ALARM_MESSAGE_STRATEGY_MAP = Maps.newHashMap();

    public AlarmMessageFactory(List<MsgHandleStrategy> msgHandleStrategyList) {
        msgHandleStrategyList.forEach(msgHandleStrategy -> ALARM_MESSAGE_STRATEGY_MAP.put(msgHandleStrategy.unionId(), msgHandleStrategy));
    }

    public MsgHandleStrategy create(String type, String messageType) {
        String unionId = MessageFormat.format("{0}#{1}", type, messageType);
        MsgHandleStrategy msgHandleStrategy = ALARM_MESSAGE_STRATEGY_MAP.get(unionId);
        if (ObjectUtils.isEmpty(msgHandleStrategy)) {
            throw new AlarmException(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorCode(),
                    MessageFormat.format(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                            type,
                            messageType));
        }
        return msgHandleStrategy;
    }
}
