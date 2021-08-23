package com.github.sparkzxl.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: 日志告警全局配置
 *
 * @author zhoux
 * @date 2021-08-21 13:47:51
 */
@Data
@ConfigurationProperties(prefix = "spring.alarm-log")
public class AlarmLogConfig {

    private Boolean printStackTrace = false;

    private Boolean simpleWarnInfo = false;

    private Boolean warnExceptionExtend = false;

    private List<Class<? extends Throwable>> doWarnException;
}
