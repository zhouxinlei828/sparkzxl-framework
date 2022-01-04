package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.support.AlarmException;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.function.Function;

/**
 * description: 数据库告警模板加载
 *
 * @author zhouxinlei
 * @date 2021-12-28 11:23
 */
@RequiredArgsConstructor
public class JdbcAlarmTemplateProvider extends BaseAlarmTemplateProvider {

    private final Function<String, AlarmTemplate> function;

    @Override
    AlarmTemplate getAlarmTemplate(String templateId) {
        AlarmTemplate alarmTemplate = function.apply(templateId);
        if (ObjectUtils.isEmpty(alarmTemplate)) {
            throw new AlarmException(400, "未发现告警配置模板");
        }
        return alarmTemplate;
    }
}
