package com.github.sparkzxl.alarm.properties;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.enums.AlarmType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.security.InvalidParameterException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * description: 告警属性配置
 *
 * @author zhouxinlei
 * @since 2022-05-18 11:11:52
 */
@Data
@Slf4j
@ConfigurationProperties(prefix = "spring.alarm")
public class AlarmProperties implements InitializingBean {

    private boolean enabled;

    private final Map<AlarmType, AlarmConfig> alarms = new LinkedHashMap<>();

    private AlarmType primaryAlarm;

    public static class AlarmConfig {
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
         * 选填, 签名秘钥。 需要验签时必填(钉钉机器人提供)
         */
        private String secret;

        /**
         * 选填, 是否开启异步处理, 默认： false
         */
        private boolean async = false;

        public String getRobotUrl() {
            return robotUrl;
        }

        public void setRobotUrl(String robotUrl) {
            this.robotUrl = robotUrl;
        }

        public String getTokenId() {
            return tokenId;
        }

        public void setTokenId(String tokenId) {
            this.tokenId = tokenId;
        }

        public String getSecret() {
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public boolean isAsync() {
            return async;
        }

        public void setAsync(boolean async) {
            this.async = async;
        }
    }


    @Override
    public void afterPropertiesSet() {
        if (MapUtil.isEmpty(alarms)) {
            throw new InvalidParameterException("alarm Can not be empty");
        }
        alarms.forEach((key, value) -> {
            if (!key.isEnabled()) {
                throw new InvalidParameterException(String.format("alarm=%s is disabled.", key.getType()));
            }
            if (StringUtils.isEmpty(value.tokenId)) {
                throw new InvalidParameterException(String.format("alarm=%s tokenId is empty.", key.getType()));
            }
            if (StringUtils.isEmpty(value.robotUrl)) {
                value.robotUrl = key.getRobotUrl();
            }
            if (key == AlarmType.WETALK) {
                value.secret = null;
            }
            if (primaryAlarm == null) {
                primaryAlarm = key;
                if (log.isDebugEnabled()) {
                    log.debug("primaryAlarm undeclared and use first Alarms alarmType, primaryAlarm={}.", primaryAlarm);
                }
            }
        });
    }
}
