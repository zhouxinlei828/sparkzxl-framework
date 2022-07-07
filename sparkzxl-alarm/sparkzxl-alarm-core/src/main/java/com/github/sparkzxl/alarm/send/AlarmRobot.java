package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.executor.AlarmExecutor;
import com.github.sparkzxl.alarm.message.MarkDownMessageTemplate;
import com.github.sparkzxl.alarm.message.TextMessageTemplate;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.strategy.AlarmMessageFactory;
import com.github.sparkzxl.alarm.strategy.MsgHandleStrategy;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.text.MessageFormat;
import java.util.List;

/**
 * description: 告警机器人
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:34:28
 */
@Slf4j
public class AlarmRobot extends AbstractAlarmSender {

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
    public AlarmResponse send(AlarmType alarmType, MessageSubType messageSubType, AlarmRequest request) {
        return Try.of(() -> {
            if (!messageSubType.isSupport()) {
                return AlarmResponse.failed(MessageFormat.format(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                        alarmType.getType(),
                        messageSubType.getCode()));
            }
            convertMessage(messageSubType, request);
            MsgHandleStrategy msgHandleStrategy = alarmMessageFactory.create(alarmType.getType(), messageSubType.getCode());
            MsgType msgType = msgHandleStrategy.newInstance(request);
            AlarmExecutor alarmExecutor = executorMap.get(msgType.getAlarmType().getType());
            if (ObjectUtils.isEmpty(alarmExecutor)) {
                String errorMsg = MessageFormat.format(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorMsg(),
                        msgType.getAlarmType().getType(),
                        msgType.getAlarmType().getType());
                throw new AlarmException(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorCode(), errorMsg);
            }
            return alarmExecutor.send(msgType, request.getVariables());
        }).get();
    }

    @Override
    public AlarmResponse designatedRobotSend(String robotId, MessageSubType messageSubType, AlarmRequest request) {
        return designatedRobotSend(robotId, alarmProperties.getPrimaryAlarm(), messageSubType, request);
    }

    @Override
    public AlarmResponse designatedRobotSend(String robotId, AlarmType alarmType, MessageSubType messageSubType, AlarmRequest request) {
        return Try.of(() -> {
            if (!messageSubType.isSupport()) {
                return AlarmResponse.failed(MessageFormat.format(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED.getErrorMsg(),
                        alarmType.getType(),
                        messageSubType.getCode()));
            }
            convertMessage(messageSubType, request);
            MsgHandleStrategy msgHandleStrategy = alarmMessageFactory.create(alarmType.getType(), messageSubType.getCode());
            MsgType msgType = msgHandleStrategy.newInstance(request);
            AlarmExecutor alarmExecutor = executorMap.get(msgType.getAlarmType().getType());
            if (ObjectUtils.isEmpty(alarmExecutor)) {
                String errorMsg = MessageFormat.format(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorMsg(),
                        msgType.getAlarmType().getType(),
                        msgType.getAlarmType().getType());
                throw new AlarmException(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorCode(), errorMsg);
            }
            return alarmExecutor.designatedRobotSend(robotId, msgType, request.getVariables());
        }).get();

    }
}
