package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;

/**
 * description:
 *
 * @author zhouxinlei
 */
public interface AlarmTemplateProvider {


    /**
     * 加载告警模板
     *
     * @param templateId 模板id
     * @return AlarmTemplate
     */
    AlarmTemplate loadingAlarmTemplate(String templateId);
}
