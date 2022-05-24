package com.github.sparkzxl.alarm.properties;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.enums.AlarmType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;

import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * description: 告警属性配置
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:11:52
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = AlarmConstant.ALARM_CONFIG_PREFIX)
public class AlarmProperties implements InitializingBean {

    private boolean enabled;

    @NestedConfigurationProperty
    private final Map<AlarmType, List<AlarmConfig>> alarms = new LinkedHashMap<>();

    private AlarmType primaryAlarm;

    @Override
    public void afterPropertiesSet() {
        if (MapUtil.isEmpty(alarms)) {
            throw new InvalidParameterException("alarm Can not be empty");
        }
        alarms.forEach((key, value) -> {
            if (!key.isEnabled()) {
                throw new InvalidParameterException(String.format("alarm=%s is disabled.", key.getType()));
            }
            if (CollectionUtils.isEmpty(value)) {
                throw new InvalidParameterException(String.format("alarm=%s config is empty.", key.getType()));
            }
            value.forEach(config -> {
                if (StringUtils.isEmpty(config.getTokenId())) {
                    throw new InvalidParameterException(String.format("alarm=%s tokenId is empty.", key.getType()));
                }
                if (StringUtils.isEmpty(config.getRobotUrl())) {
                    config.setRobotUrl(key.getRobotUrl());
                }
                if (key == AlarmType.WETALK) {
                    config.setSecret(null);
                }
            });
            if (primaryAlarm == null) {
                primaryAlarm = key;
                if (log.isDebugEnabled()) {
                    log.debug("primaryAlarm undeclared and use first Alarms alarmType, primaryAlarm={}.", primaryAlarm);
                }
            }
        });
    }
}
