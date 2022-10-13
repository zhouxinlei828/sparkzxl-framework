package com.github.sparkzxl.alarm.properties;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.constant.AlarmConstant;
import com.github.sparkzxl.alarm.enums.AlarmChannel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    private final Map<AlarmChannel, AlarmChannelConfig> channel = new LinkedHashMap<>();

    private AlarmChannel primaryAlarm;

    @Override
    public void afterPropertiesSet() {
        if (MapUtil.isEmpty(channel)) {
            throw new InvalidParameterException("alarm can not be empty");
        }
        channel.forEach((key, value) -> {
            if (!key.isEnabled()) {
                throw new InvalidParameterException(String.format("alarm=%s is disabled.", key.getType()));
            }
            if (!value.isEnabled()) {
                throw new InvalidParameterException(String.format("Alarm Channel Config =%s is disabled.", key.getType()));
            }
            List<AlarmConfig> alarmConfigs = value.getConfigs();
            if (CollectionUtils.isEmpty(alarmConfigs)) {
                throw new InvalidParameterException(String.format("alarm=%s config is empty.", key.getType()));
            }
            alarmConfigs.forEach(config -> {
                if (StringUtils.isEmpty(config.getTokenId())) {
                    throw new InvalidParameterException(String.format("alarm=%s tokenId is empty.", key.getType()));
                }
                if (StringUtils.isEmpty(config.getRobotUrl())) {
                    config.setRobotUrl(key.getRobotUrl());
                }
                if (key == AlarmChannel.WETALK) {
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

    /**
     * description: 告警配置
     *
     * @author zhouxinlei
     * @since 2022-05-24 09:50:42
     */
    @Data
    public static class AlarmConfig {

        /**
         * 机器人id
         */
        protected String robotId = "default";
        /**
         * 请求地址前缀-选填
         */
        private String robotUrl;
        /**
         * 获取 access_token, 必填
         * 填写机器人设置中 webhook access_token | key后面的值
         */
        private String tokenId;
        /**
         * 选填, 签名秘钥。 需要验签时必填(机器人提供)
         */
        private String secret;

        /**
         * 选填, 是否开启异步处理, 默认： false
         */
        private boolean async;
    }

    /**
     * description: 告警配置
     *
     * @author zhouxinlei
     * @since 2022-05-24 09:50:42
     */
    @Data
    public static class AlarmChannelConfig {

        private boolean enabled;

        private List<AlarmConfig> configs;
    }
}
