package com.github.sparkzxl.alarm.executor;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.callback.AlarmAsyncCallback;
import com.github.sparkzxl.alarm.callback.AlarmExceptionCallback;
import com.github.sparkzxl.alarm.entity.AlarmResponse;
import com.github.sparkzxl.alarm.entity.MsgType;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import com.github.sparkzxl.alarm.enums.AlarmErrorEnum;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.loadbalancer.AlarmLoadBalancer;
import com.github.sparkzxl.alarm.properties.AlarmProperties;
import com.github.sparkzxl.alarm.send.AlarmCallback;
import com.github.sparkzxl.alarm.support.AlarmIdGenerator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-05-24 09:08:24
 */
@Slf4j
public abstract class AbstractAlarmExecutor implements AlarmExecutor {

    @Resource
    protected AlarmIdGenerator alarmIdGenerator;
    @Resource
    protected AlarmProperties alarmProperties;
    @Resource
    protected AlarmExceptionCallback alarmExceptionCallback;
    @Resource
    protected AlarmAsyncCallback alarmAsyncCallback;
    @Resource
    protected AlarmLoadBalancer alarmLoadBalancer;
    @Resource
    protected ThreadPoolExecutor alarmThreadPoolExecutor;

    public AbstractAlarmExecutor() {
    }

    @Override
    public <T extends MsgType> AlarmResponse send(T message, Map<String, Object> variables) {
        // 告警唯一id
        String alarmId = alarmIdGenerator.nextAlarmId();
        AlarmChannel alarmChannel = message.getAlarmChannel();
        if (MapUtil.isNotEmpty(variables)) {
            message.transfer(variables);
        }
        AlarmProperties.AlarmConfig alarmConfig = getAlarmConfig(alarmChannel, (configs) -> alarmLoadBalancer.choose(configs));
        if (log.isDebugEnabled()) {
            log.debug("alarmId={} send message and use alarm type ={}, tokenId={}.", alarmId, alarmChannel.getType(),
                    alarmConfig.getTokenId());
        }
        return sendAlarm(alarmId, alarmConfig, message);
    }

    @Override
    public <T extends MsgType> AlarmResponse designatedRobotSend(String robotId, T message, Map<String, Object> variables) {
        // 告警唯一id
        String alarmId = alarmIdGenerator.nextAlarmId();
        AlarmChannel alarmChannel = message.getAlarmChannel();
        if (MapUtil.isNotEmpty(variables)) {
            message.transfer(variables);
        }
        AlarmProperties.AlarmConfig alarmConfig = getAlarmConfig(alarmChannel,
                (configs) -> alarmLoadBalancer.chooseDesignatedRobot(robotId, configs));
        if (log.isDebugEnabled()) {
            log.debug("alarmId={} send message and use alarm type ={}, tokenId={}.", alarmId, alarmChannel.getType(),
                    alarmConfig.getTokenId());
        }
        return sendAlarm(alarmId, alarmConfig, message);
    }

    public AlarmProperties.AlarmConfig getAlarmConfig(AlarmChannel alarmChannel,
            Function<List<AlarmProperties.AlarmConfig>, AlarmProperties.AlarmConfig> function) {
        Map<AlarmChannel, AlarmProperties.AlarmChannelConfig> alarms = alarmProperties.getChannel();
        if (alarmProperties.isEnabled() && !alarms.containsKey(alarmChannel)) {
            throw new AlarmException(AlarmErrorEnum.ALARM_DISABLED);
        }
        AlarmProperties.AlarmChannelConfig alarmTypeConfig = alarms.get(alarmChannel);
        AlarmProperties.AlarmConfig alarmConfig = function.apply(alarmTypeConfig.getConfigs());
        if (ObjectUtils.isEmpty(alarmConfig)) {
            throw new AlarmException(AlarmErrorEnum.CONFIG_NOT_FIND);
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
