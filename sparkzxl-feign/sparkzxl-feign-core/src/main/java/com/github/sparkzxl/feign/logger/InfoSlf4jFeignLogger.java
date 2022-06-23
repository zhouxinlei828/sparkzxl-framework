package com.github.sparkzxl.feign.logger;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Slf4j
public class InfoSlf4jFeignLogger extends feign.Logger {

    @Override
    protected void log(String configKey, String format, Object... args) {
        if (log.isInfoEnabled()) {
            log.info(String.format(methodTag(configKey) + format, args));
        }
    }

}