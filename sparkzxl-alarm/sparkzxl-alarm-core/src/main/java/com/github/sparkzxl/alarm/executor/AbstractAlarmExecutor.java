package com.github.sparkzxl.alarm.executor;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.callback.AlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.AlarmExceptionCallback;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmResponseCodeEnum;
import com.github.sparkzxl.alarm.enums.AlarmType;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.loadbalancer.AlarmLoadBalancer;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.send.AlarmCallback;
import com.github.sparkzxl.alarm.sign.AlarmSignAlgorithm;
import com.github.sparkzxl.alarm.support.AlarmIdGenerator;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:08:24
 */
@Slf4j
@RequiredArgsConstructor
public abstract class AbstractAlarmExecutor implements AlarmExecutor {

    protected AlarmIdGenerator alarmIdGenerator;
    protected AlarmProperties alarmProperties;
    protected AlarmExceptionCallback alarmExceptionCallback;
    protected AlarmAsyncCallback alarmAsyncCallback;
    protected AlarmSignAlgorithm alarmSignAlgorithm;
    protected AlarmLoadBalancer alarmLoadBalancer;
    protected ThreadPoolExecutor alarmThreadPoolExecutor;


    @Autowired
    public void setAlarmIdGenerator(AlarmIdGenerator alarmIdGenerator) {
        this.alarmIdGenerator = alarmIdGenerator;
    }

    @Autowired
    public void setAlarmProperties(AlarmProperties alarmProperties) {
        this.alarmProperties = alarmProperties;
    }

    @Autowired
    public void setAlarmExceptionCallback(AlarmExceptionCallback alarmExceptionCallback) {
        this.alarmExceptionCallback = alarmExceptionCallback;
    }

    @Autowired
    public void setAlarmAsyncCallback(AlarmAsyncCallback alarmAsyncCallback) {
        this.alarmAsyncCallback = alarmAsyncCallback;
    }

    @Autowired
    public void setAlarmSignAlgorithm(AlarmSignAlgorithm alarmSignAlgorithm) {
        this.alarmSignAlgorithm = alarmSignAlgorithm;
    }

    @Autowired
    public void setAlarmLoadBalancer(AlarmLoadBalancer alarmLoadBalancer) {
        this.alarmLoadBalancer = alarmLoadBalancer;
    }

    @Autowired
    public void setAlarmThreadPoolExecutor(ThreadPoolExecutor alarmThreadPoolExecutor) {
        this.alarmThreadPoolExecutor = alarmThreadPoolExecutor;
    }

    @Override
    public <T extends MsgType> AlarmResponse send(T message, Map<String, Object> variables) {
        return Try.of(() -> {
            // 告警唯一id
            String alarmId = alarmIdGenerator.nextAlarmId();
            AlarmType alarmType = message.getAlarmType();
            if (MapUtil.isNotEmpty(variables)) {
                message.transfer(variables);
            }
            AlarmProperties.AlarmConfig alarmConfig = getAlarmConfig(alarmType, (configs) -> alarmLoadBalancer.choose(configs));
            if (log.isDebugEnabled()) {
                log.debug("alarmId={} send message and use alarm type ={}, tokenId={}.", alarmId, alarmType.getType(), alarmConfig.getTokenId());
            }
            return sendAlarm(alarmId, alarmConfig, message);
        }).get();
    }

    @Override
    public <T extends MsgType> AlarmResponse designatedRobotSend(String robotId, T message, Map<String, Object> variables) {
        return Try.of(() -> {
            // 告警唯一id
            String alarmId = alarmIdGenerator.nextAlarmId();
            AlarmType alarmType = message.getAlarmType();
            if (MapUtil.isNotEmpty(variables)) {
                message.transfer(variables);
            }
            AlarmProperties.AlarmConfig alarmConfig = getAlarmConfig(alarmType, (configs) -> alarmLoadBalancer.chooseDesignatedRobot(robotId, configs));
            if (log.isDebugEnabled()) {
                log.debug("alarmId={} send message and use alarm type ={}, tokenId={}.", alarmId, alarmType.getType(), alarmConfig.getTokenId());
            }
            return sendAlarm(alarmId, alarmConfig, message);
        }).get();
    }

    public AlarmProperties.AlarmConfig getAlarmConfig(AlarmType alarmType, Function<List<AlarmProperties.AlarmConfig>, AlarmProperties.AlarmConfig> function) {
        Map<AlarmType, AlarmProperties.AlarmTypeConfig> alarms = alarmProperties.getChannel();
        if (alarmProperties.isEnabled() && !alarms.containsKey(alarmType)) {
            throw new AlarmException(AlarmResponseCodeEnum.ALARM_DISABLED);
        }
        AlarmProperties.AlarmTypeConfig alarmTypeConfig = alarms.get(alarmType);
        AlarmProperties.AlarmConfig alarmConfig = function.apply(alarmTypeConfig.getConfigs());
        if (ObjectUtils.isEmpty(alarmConfig)) {
            throw new AlarmException(AlarmResponseCodeEnum.CONFIG_NOT_FIND);
        }
        return alarmConfig;
    }

    /**
     * 异常回调
     *
     * @param alarmId   alarmId
     * @param message   message
     * @param exception ex
     * @param <T>       T
     */
    protected <T> void exceptionCallback(String alarmId, T message, AlarmException exception) {
        AlarmCallback<T> alarmCallback = new AlarmCallback<>(alarmId, message, exception);
        alarmExceptionCallback.execute(alarmCallback);
    }

    /**
     * 发送消息通知
     *
     * @param alarmId     告警id
     * @param alarmConfig 消息配置
     * @param message     消息
     * @param <T>         泛型
     * @return AlarmResponse
     */
    protected abstract <T extends MsgType> AlarmResponse sendAlarm(String alarmId, AlarmProperties.AlarmConfig alarmConfig, T message);
}
