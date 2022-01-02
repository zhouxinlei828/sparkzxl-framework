package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.support.AlarmException;
import org.springframework.util.StringUtils;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:12
 */
public abstract class BaseAlarmTemplateProvider implements AlarmTemplateProvider {

    @Override
    public AlarmTemplate loadingAlarmTemplate(String templateCode) {
        if (StringUtils.isEmpty(templateCode)) {
            throw new AlarmException(400, "告警模板配置code不能为空");
        }
        return getAlarmTemplate(templateCode);
    }

    /**
     * 查询告警模板
     *
     * @param templateCode 模板code
     * @return AlarmTemplate
     */
    abstract AlarmTemplate getAlarmTemplate(String templateCode);
}
