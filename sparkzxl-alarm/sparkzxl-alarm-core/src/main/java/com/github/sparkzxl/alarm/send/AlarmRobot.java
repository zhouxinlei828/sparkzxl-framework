package com.github.sparkzxl.alarm.send;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.alarm.entity.AlarmRequest;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.enums.MessageSubType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.exception.AsyncCallException;
import com.github.sparkzxl.alarm.message.CustomMessage;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.sign.BaseSign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * description: 告警机器人
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:34:28
 */
@Slf4j
public class AlarmRobot extends AbstractAlarmSender {

    public AlarmRobot(AlarmProperties alarmProperties, AlarmManagerBuilder alarmManagerBuilder) {
        super(alarmProperties, alarmManagerBuilder);
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
        return send(msgType);
    }

    protected <T extends MsgType> AlarmResponse send(T message) {
        AlarmType alarmType = message.getAlarmType();
        String alarmId = alarmManagerBuilder.getAlarmIdGenerator().nextAlarmId();
        Map<AlarmType, AlarmProperties.AlarmConfig> alarms = alarmProperties.getAlarms();
        if (alarmProperties.isEnabled() && !alarms.containsKey(alarmType)) {
            return AlarmResponse.failed(alarmId, AlarmResponseCodeEnum.ALARM_DISABLED);
        }
        try {
            AlarmProperties.AlarmConfig alarmConfig = alarms.get(alarmType);
            StringBuilder webhook = new StringBuilder();
            webhook.append(alarmConfig.getRobotUrl()).append(alarmConfig.getTokenId());
            if (log.isDebugEnabled()) {
                log.debug("alarmId={} send message and use alarm type ={}, tokenId={}.", alarmId, alarmType.getType(), alarmConfig.getTokenId());
            }
            // 处理签名问题(只支持DingTalk)
            if (alarmType == AlarmType.DINGTALK && StringUtils.isNotEmpty((alarmConfig.getSecret()))) {
                BaseSign sign = alarmManagerBuilder.alarmSignAlgorithm.sign(alarmConfig.getSecret().trim());
                webhook.append(sign.transfer());
            }
            String jsonStr = JSONUtil.toJsonStr(message);
            if (alarmConfig.isAsync()) {
                CompletableFuture<AlarmResponse> alarmResponseCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        String body = HttpRequest.post(webhook.toString())
                                .contentType(ContentType.JSON.getValue())
                                .body(jsonStr)
                                .execute()
                                .body();
                        alarmManagerBuilder.getAlarmAsyncCallback().execute(alarmId, body);
                    } catch (HttpException e) {
                        exceptionCallback(alarmId, message, new AsyncCallException(e));
                    }
                    return AlarmResponse.success(alarmId, alarmId);
                });
                return alarmResponseCompletableFuture.get();
            }
            String body = HttpRequest.post(webhook.toString())
                    .contentType(ContentType.JSON.getValue())
                    .body(jsonStr)
                    .execute()
                    .body();
            return AlarmResponse.success(alarmId, body);
        } catch (Exception e) {
            exceptionCallback(alarmId, message, new AlarmException(e));
            return AlarmResponse.failed(alarmId, AlarmResponseCodeEnum.SEND_MESSAGE_FAILED);
        }
    }
}
