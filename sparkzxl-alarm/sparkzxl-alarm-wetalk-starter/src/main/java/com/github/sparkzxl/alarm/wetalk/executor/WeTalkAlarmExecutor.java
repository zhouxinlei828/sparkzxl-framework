package com.github.sparkzxl.alarm.wetalk.executor;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.exception.AsyncCallException;
import com.github.sparkzxl.alarm.executor.AbstractAlarmExecutor;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;

/**
 * description: 企业微信告警执行器
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:19:34
 */
@Slf4j
public class WeTalkAlarmExecutor extends AbstractAlarmExecutor {
    public WeTalkAlarmExecutor() {
        log.debug("WeTalk Alarm Executor has been loaded, className:{}", this.getClass().getName());
    }

    @Override
    protected <T extends MsgType> AlarmResponse sendAlarm(String alarmId, AlarmProperties.AlarmConfig alarmConfig, T message) {
        try {
            StringBuilder webhook = new StringBuilder();
            webhook.append(alarmConfig.getRobotUrl()).append(alarmConfig.getTokenId());
            String jsonStr = JSONUtil.toJsonStr(message);
            if (alarmConfig.isAsync()) {
                CompletableFuture<AlarmResponse> alarmResponseCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        String body = HttpRequest.post(webhook.toString())
                                .contentType(ContentType.JSON.getValue())
                                .body(jsonStr)
                                .execute()
                                .body();
                        alarmAsyncCallback.execute(alarmId, body);
                    } catch (HttpException e) {
                        exceptionCallback(alarmId, message, new AsyncCallException(e));
                    }
                    return AlarmResponse.success(alarmId, alarmId);
                }, alarmThreadPoolExecutor);
                return alarmResponseCompletableFuture.get();
            }
            String body = HttpRequest.post(webhook.toString())
                    .contentType(ContentType.JSON.getValue())
                    .body(jsonStr)
                    .execute()
                    .body();
            if (log.isDebugEnabled()) {
                log.debug("weTalk send message call [{}], param:{}, resp:{}", webhook, jsonStr, body);
            }
            return AlarmResponse.success(alarmId, body);
        } catch (Exception e) {
            exceptionCallback(alarmId, message, new AlarmException(e));
            return AlarmResponse.failed(alarmId, AlarmResponseCodeEnum.SEND_MESSAGE_FAILED);
        }
    }

    @Override
    public String named() {
        return AlarmType.WETALK.getType();
    }
}
