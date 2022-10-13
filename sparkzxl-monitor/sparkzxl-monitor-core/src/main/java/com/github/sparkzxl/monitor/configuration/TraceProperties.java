package com.github.sparkzxl.monitor.configuration;

import com.github.sparkzxl.monitor.enums.TraceEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * description:
 *
 * @author zhouxinlei
 * @since 2022-08-18 14:54:52
 */
@Data
@ConfigurationProperties(prefix = "spring.monitor.tracer")
public class TraceProperties {

    private boolean enabled;

    private TraceEnum type = TraceEnum.CUSTOMIZE;

}
