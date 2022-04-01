package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.support.AlarmException;
import org.apache.commons.lang3.StringUtils;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:12
 */
public abstract class BaseAlarmTemplateProvider implements AlarmTemplateProvider {

    @Override
    public AlarmTemplate loadingAlarmTemplate(String templateId) {
        if (StringUtils.isEmpty(templateId)) {
            throw new AlarmException(400, "告警模板配置id不能为空");
        }
        return getAlarmTemplate(templateId);
    }

    /**
     * 查询告警模板
     *
     * @param templateId 模板id
     * @return AlarmTemplate
     */
    abstract AlarmTemplate getAlarmTemplate(String templateId);
}
