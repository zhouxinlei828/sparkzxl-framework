package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.autoconfigure.TemplateConfig;
import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.support.AlarmException;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * description: YAML告警模板加载
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:23
 */
@RequiredArgsConstructor
public class YamlAlarmTemplateProvider extends BaseAlarmTemplateProvider {

    private final TemplateConfig templateConfig;

    @Override
    AlarmTemplate getAlarmTemplate(String templateId) {
        Map<String, AlarmTemplate> configTemplates = templateConfig.getTemplates();
        AlarmTemplate alarmTemplate = configTemplates.get(templateId);
        if (ObjectUtils.isEmpty(alarmTemplate)) {
            throw new AlarmException(400, "未发现告警配置模板");
        }
        return alarmTemplate;
    }
}
