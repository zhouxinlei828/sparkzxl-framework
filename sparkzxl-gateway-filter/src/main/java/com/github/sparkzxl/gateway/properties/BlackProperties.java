package com.github.sparkzxl.gateway.properties;

import com.github.sparkzxl.constant.ConfigurationConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-08-06 09:38:34
 */
@Data
@ConfigurationProperties(prefix = ConfigurationConstant.BLACK_PREFIX)
public class BlackProperties {

    private boolean enabled = false;

    private List<String> blackList;
}
