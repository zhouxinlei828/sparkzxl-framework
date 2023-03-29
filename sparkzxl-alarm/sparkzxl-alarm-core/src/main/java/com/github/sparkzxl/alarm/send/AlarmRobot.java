package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.AlarmErrorEnum;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.executor.AlarmExecutor;
import com.github.sparkzxl.alarm.message.MarkDownMessageTemplate;
import com.github.sparkzxl.alarm.message.TextMessageTemplate;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.strategy.AlarmMessageFactory;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import java.text.MessageFormat;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * description: 告警机器人
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:34:28
 */
@Slf4j
public class AlarmRobot extends AbstractAlarmClient {

    private final AlarmMessageFactory alarmMessageFactory;

    public AlarmRobot(AlarmProperties alarmProperties,
            TextMessageTemplate textMessage,
            MarkDownMessageTemplate markDownMessage,
            List<AlarmExecutor> alarmExecutorList, AlarmMessageFactory alarmMessageFactory) {
        super(alarmProperties, textMessage, markDownMessage, alarmExecutorList);
        this.alarmMessageFactory = alarmMessageFactory;
    }

    @Override
    public AlarmResponse send(MessageSubType messageSubType, AlarmRequest request) {
        return send(alarmProperties.getPrimaryAlarm(), messageSubType, request);
    }

    @Override
    public AlarmResponse send(AlarmChannel alarmChannel, MessageSubType messageSubType, AlarmRequest request) {
        if (!messageSubType.isSupport()) {
            return AlarmResponse.failed(MessageFormat.format(AlarmErrorEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                    alarmChannel.getType(),
                    messageSubType.getCode()));
        }
        convertMessage(messageSubType, request);
        MsgHandleStrategy msgHandleStrategy = alarmMessageFactory.create(alarmChannel.getType(), messageSubType.getCode());
        MsgType msgType = msgHandleStrategy.newInstance(request);
        AlarmExecutor alarmExecutor = executorMap.get(msgType.getAlarmChannel().getType());
        if (ObjectUtils.isEmpty(alarmExecutor)) {
            String errorMsg = MessageFormat.format(AlarmErrorEnum.ALARM_TYPE_UNSUPPORTED.getErrorMsg(),
                    msgType.getAlarmChannel().getType(),
                    msgType.getAlarmChannel().getType());
            throw new AlarmException(AlarmErrorEnum.ALARM_TYPE_UNSUPPORTED.getErrorCode(), errorMsg);
        }
        return alarmExecutor.send(msgType, request.getVariables());
    }

    @Override
    public AlarmResponse designatedRobotSend(String robotId, MessageSubType messageSubType, AlarmRequest request) {
        return designatedRobotSend(robotId, alarmProperties.getPrimaryAlarm(), messageSubType, request);
    }

    @Override
    public AlarmResponse designatedRobotSend(String robotId, AlarmChannel alarmChannel, MessageSubType messageSubType,
            AlarmRequest request) {
        if (!messageSubType.isSupport()) {
            return AlarmResponse.failed(MessageFormat.format(AlarmErrorEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                    alarmChannel.getType(),
                    messageSubType.getCode()));
        }
        convertMessage(messageSubType, request);
        MsgHandleStrategy msgHandleStrategy = alarmMessageFactory.create(alarmChannel.getType(), messageSubType.getCode());
        MsgType msgType = msgHandleStrategy.newInstance(request);
        AlarmExecutor alarmExecutor = executorMap.get(msgType.getAlarmChannel().getType());
        if (ObjectUtils.isEmpty(alarmExecutor)) {
            String errorMsg = MessageFormat.format(AlarmErrorEnum.ALARM_TYPE_UNSUPPORTED.getErrorMsg(),
                    msgType.getAlarmChannel().getType(),
                    msgType.getAlarmChannel().getType());
            throw new AlarmException(AlarmErrorEnum.ALARM_TYPE_UNSUPPORTED.getErrorCode(), errorMsg);
        }
        return alarmExecutor.designatedRobotSend(robotId, msgType, request.getVariables());
    }
}
