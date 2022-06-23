package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.support.AlarmErrorCodeEnum;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 抽象告警模板
 *
 * @author zhouxinlei
 */
public abstract class BaseAlarmTemplateProvider implements AlarmTemplateProvider {

    @Override
    public AlarmTemplate loadingAlarmTemplate(String templateId) {
        if (StringUtils.isEmpty(templateId)) {
            throw new AlarmException(AlarmErrorCodeEnum.TEMPLATE_ID_NOT_FOUND);
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
