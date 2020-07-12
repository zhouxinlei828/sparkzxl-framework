package com.sparksys.commons.security.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * description: 用于配置不需要保护的资源路径
 *
 * @author zhouxinlei
 * @date 2020-05-24 13:38:12
 */
@Data
@ConfigurationProperties(value = "security.ignored")
public class IgnoreUrlsProperties {

    private List<String> urls;

}
