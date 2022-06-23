package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.autoconfigure.TemplateConfig;
import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.support.AlarmErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Map;

/**
 * description: YAML告警模板加载
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class YamlAlarmTemplateProvider extends BaseAlarmTemplateProvider {

    private final TemplateConfig templateConfig;

    @Override
    AlarmTemplate getAlarmTemplate(String templateId) {
        Map<String, AlarmTemplate> configTemplates = templateConfig.getTemplates();
        AlarmTemplate alarmTemplate = configTemplates.get(templateId);
        if (ObjectUtils.isEmpty(alarmTemplate)) {
            throw new AlarmException(AlarmErrorCodeEnum.TEMPLATE_NOT_FOUND);
        }
        return alarmTemplate;
    }
}
