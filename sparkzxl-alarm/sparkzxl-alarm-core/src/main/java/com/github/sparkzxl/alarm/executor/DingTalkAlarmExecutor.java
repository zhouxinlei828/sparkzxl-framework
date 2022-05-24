package com.github.sparkzxl.alarm.executor;

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
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.sign.BaseSign;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * description: 钉钉告警执行器
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:03:17
 */
@Slf4j
public class DingTalkAlarmExecutor extends AbstractAlarmExecutor {

    @Override
    protected <T extends MsgType> AlarmResponse sendAlarm(String alarmId, AlarmProperties.AlarmConfig alarmConfig, T message) {
        try {
            StringBuilder webhook = new StringBuilder();
            webhook.append(alarmConfig.getRobotUrl()).append(alarmConfig.getTokenId());
            // 处理签名问题(只支持DingTalk)
            if (StringUtils.isNotEmpty((alarmConfig.getSecret()))) {
                BaseSign sign = alarmSignAlgorithm.sign(alarmConfig.getSecret().trim());
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
                log.debug("dingtalk send message call [{}], param:{}, resp:{}", webhook, jsonStr, body);
            }
            return AlarmResponse.success(alarmId, body);
        } catch (Exception e) {
            exceptionCallback(alarmId, message, new AlarmException(e));
            return AlarmResponse.failed(alarmId, AlarmResponseCodeEnum.SEND_MESSAGE_FAILED);
        }
    }

    @Override
    public String named() {
        return AlarmType.DINGTALK.getType();
    }
}
