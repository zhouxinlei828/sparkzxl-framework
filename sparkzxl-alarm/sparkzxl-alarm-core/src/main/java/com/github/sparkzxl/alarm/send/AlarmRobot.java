package com.github.sparkzxl.alarm.send;

import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.executor.AlarmExecutor;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.message.MarkDownMessage;
import com.github.sparkzxl.alarm.message.TextMessage;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
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

    public AlarmRobot(AlarmProperties alarmProperties,
                      TextMessage textMessage,
                      MarkDownMessage markDownMessage,
                      List<AlarmExecutor> alarmExecutorList) {
        super(alarmProperties, textMessage, markDownMessage, alarmExecutorList);
    }

    @Override
    public AlarmResponse send(MessageSubType messageSubType, AlarmRequest request) {
        return send(alarmProperties.getPrimaryAlarm(), messageSubType, request);
    }

    @Override
    public AlarmResponse send(AlarmType alarmType, MessageSubType messageSubType, AlarmRequest request) {
        if (!messageSubType.isSupport()) {
            return AlarmResponse.failed(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED);
        }
        CustomMessage customMessage = customMessage(messageSubType);
        String msgContent = customMessage.message(request);
        request.setContent(msgContent);
        MsgType msgType = messageSubType.msgType(alarmType, request);
        AlarmExecutor alarmExecutor = executorMap.get(msgType.getAlarmType().getType());
        if (ObjectUtils.isEmpty(alarmExecutor)) {
            String errorMessage = MessageFormat.format(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorMessage(),
                    msgType.getAlarmType().getType(),
                    msgType.getAlarmType().getType());
            throw new AlarmException(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorCode(), errorMessage);
        }
        return alarmExecutor.send(msgType, request.getVariables());
    }

    @Override
    public AlarmResponse designatedRobotSend(String robotId, MessageSubType messageSubType, AlarmRequest request) {
        return designatedRobotSend(robotId, alarmProperties.getPrimaryAlarm(), messageSubType, request);
    }

    @Override
    public AlarmResponse designatedRobotSend(String robotId, AlarmType alarmType, MessageSubType messageSubType, AlarmRequest request) {
        if (!messageSubType.isSupport()) {
            return AlarmResponse.failed(AlarmResponseCodeEnum.MESSAGE_TYPE_UNSUPPORTED);
        }
        CustomMessage customMessage = customMessage(messageSubType);
        String msgContent = customMessage.message(request);
        request.setContent(msgContent);
        MsgType msgType = messageSubType.msgType(alarmType, request);
        AlarmExecutor alarmExecutor = executorMap.get(msgType.getAlarmType().getType());
        if (ObjectUtils.isEmpty(alarmExecutor)) {
            String errorMessage = MessageFormat.format(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorMessage(),
                    msgType.getAlarmType().getType(),
                    msgType.getAlarmType().getType());
            throw new AlarmException(AlarmResponseCodeEnum.ALARM_TYPE_UNSUPPORTED.getErrorCode(), errorMessage);
        }
        return alarmExecutor.designatedRobotSend(robotId, msgType, request.getVariables());
    }
}
