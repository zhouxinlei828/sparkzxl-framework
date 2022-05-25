package com.github.sparkzxl.alarm.autoconfigure;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.enums.TemplateChannel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:01
 */
@Data
@ConfigurationProperties(prefix = TemplateConfig.PREFIX)
public class TemplateConfig {

    public static final String PREFIX = "spring.alarm.template";

    private boolean enabled = false;

    private TemplateChannel channel;
    /**
     * 模板列表
     */
    private Map<String, AlarmTemplate> templates;

    /**
     * 模板配置路径
     */
    private String templatePath;

}
