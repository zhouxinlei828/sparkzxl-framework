package com.github.sparkzxl.alarm.provider;

import com.github.sparkzxl.alarm.entity.AlarmTemplate;
import com.github.sparkzxl.alarm.exception.AlarmException;
import com.github.sparkzxl.alarm.support.AlarmErrorCodeEnum;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.util.ObjectUtils;

/**
 * description: 数据库告警模板加载
 *
 * @author zhouxinlei
 */
@RequiredArgsConstructor
public class JdbcAlarmTemplateProvider extends BaseAlarmTemplateProvider {

    private final Function<String, AlarmTemplate> function;

    @Override
    AlarmTemplate getAlarmTemplate(String templateId) {
        AlarmTemplate alarmTemplate = function.apply(templateId);
        if (ObjectUtils.isEmpty(alarmTemplate)) {
            throw new AlarmException(AlarmErrorCodeEnum.TEMPLATE_NOT_FOUND);
        }
        return alarmTemplate;
    }
}
