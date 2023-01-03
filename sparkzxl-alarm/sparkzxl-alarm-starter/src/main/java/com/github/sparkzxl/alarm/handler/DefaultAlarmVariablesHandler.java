package com.github.sparkzxl.alarm.handler;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.alarm.annotation.Alarm;
import com.github.sparkzxl.alarm.annotation.AlarmParam;
import com.github.sparkzxl.alarm.entity.ExpressionTemplate;
import com.github.sparkzxl.core.json.JsonUtils;
import com.github.sparkzxl.core.util.AopUtil;
import com.google.common.collect.Maps;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * description: 默认告警处理变量参数
 *
 * @author zhouxinlei
 * @since 2022-05-25 10:16:00
 */
public class DefaultAlarmVariablesHandler implements IAlarmVariablesHandler {

    private final Function<String, String> function;

    public DefaultAlarmVariablesHandler(Function<String, String> function) {
        this.function = function;
    }

    @Override
    public Map<String, Object> getVariables(MethodInvocation invocation, Alarm alarm) {
        Map<String, Object> attributeMapping = Maps.newHashMap();
        attributeMapping.put("title", alarm.name());
        Map<String, Object> alarmParamMap = AopUtil.generateMap(invocation, AlarmParam.class, "value");
        if (MapUtil.isNotEmpty(alarmParamMap)) {
            attributeMapping.putAll(alarmParamMap);
        }
        String extractParams = alarm.extractParams();
        if (StringUtils.isNotEmpty(extractParams)) {
            String[] headerArray = StringUtils.split(extractParams, ",");
            for (String header : headerArray) {
                attributeMapping.put(header, function.apply(header));
            }
        }
        String expressionJson = alarm.expressionJson();
        if (StringUtils.isNotBlank(expressionJson)) {
            List<ExpressionTemplate> expressionTemplateList = JsonUtils.getJson().toJavaList(expressionJson, ExpressionTemplate.class);
            for (ExpressionTemplate expressionTemplate : expressionTemplateList) {
                String value = AopUtil.parseExpression(invocation, expressionTemplate.getExpression());
                attributeMapping.put(expressionTemplate.getKey(), value);
            }
        }
        return attributeMapping;
    }
}
