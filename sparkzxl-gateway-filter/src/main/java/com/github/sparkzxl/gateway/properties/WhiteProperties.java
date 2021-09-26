package com.github.sparkzxl.gateway.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description:
 *
 * @author zhouxinlei
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.BLACK_PREFIX)
public class WhiteProperties {

    private boolean enabled = false;

    private List<String> whiteList;
}
