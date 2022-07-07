package com.github.sparkzxl.alarm.feishutalk.executor;

import cn.hutool.http.ContentType;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.exception.AsyncCallException;
import com.github.sparkzxl.alarm.executor.AbstractAlarmExecutor;
import com.github.sparkzxl.alarm.feishutalk.sign.FeiShuTalkAlarmSignAlgorithm;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.sign.SignResult;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * description: 飞书告警执行器
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:19:34
 */
@Slf4j
public class FeiShuTalkAlarmExecutor extends AbstractAlarmExecutor {

    private final FeiShuTalkAlarmSignAlgorithm alarmSignAlgorithm;

    public FeiShuTalkAlarmExecutor(FeiShuTalkAlarmSignAlgorithm alarmSignAlgorithm) {
        this.alarmSignAlgorithm = alarmSignAlgorithm;
        log.debug("FeiShuTalk Alarm Executor has been loaded, className:{}", this.getClass().getName());
    }

    @Override
    protected <T extends MsgType> AlarmResponse sendAlarm(String alarmId, AlarmProperties.AlarmConfig alarmConfig, T message) {
        return Try.of(() -> {
            StringBuilder webhook = new StringBuilder();
            webhook.append(alarmConfig.getRobotUrl()).append(alarmConfig.getTokenId());
            // 处理签名问题
            if (StringUtils.isNotEmpty((alarmConfig.getSecret()))) {
                SignResult signResult = alarmSignAlgorithm.sign(alarmConfig.getSecret().trim());
                message.signAttributes(signResult);
            }
            String json = message.toJson();
            if (alarmConfig.isAsync()) {
                CompletableFuture<AlarmResponse> alarmResponseCompletableFuture = CompletableFuture.supplyAsync(() -> {
                    try {
                        String body = HttpRequest.post(webhook.toString()).contentType(ContentType.JSON.getValue()).body(json).execute().body();
                        alarmAsyncCallback.execute(alarmId, body);
                    } catch (HttpException e) {
                        exceptionCallback(alarmId, message, new AsyncCallException(e));
                    }
                    return AlarmResponse.success(alarmId, alarmId);
                }, alarmThreadPoolExecutor);
                return alarmResponseCompletableFuture.get();
            }
            String body = HttpRequest.post(webhook.toString()).contentType(ContentType.JSON.getValue()).body(json).execute().body();
            if (log.isDebugEnabled()) {
                log.debug("FeiShuTalk send message call [{}], param:{}, resp:{}", webhook, json, body);
            }
            return AlarmResponse.success(alarmId, body);
        }).getOrElseThrow(throwable -> {
            exceptionCallback(alarmId, message, new AlarmException(throwable));
            return new AlarmException(throwable);
        });
    }

    @Override
    public String named() {
        return AlarmType.FEISHU.getType();
    }
}
