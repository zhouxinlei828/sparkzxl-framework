package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;

/**
 * description:
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:05
 */
public interface AlarmTemplateProvider {


    /**
     * 加载告警模板
     *
     * @param templateCode 模板code
     * @return AlarmTemplate
     */
    AlarmTemplate loadingAlarmTemplate(String templateCode);
}
