package com.github.sparkzxl.log.handler;

import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.entity.ExpressionTemplate;
import com.github.sparkzxl.core.util.AopUtil;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.log.annotation.OptLogParam;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 默认操作日志变量参数实现类
 *
 * @author zhouxinlei
 * @since 2022-01-05 12:32:01
 */
public class DefaultOptLogVariablesHandler implements IOptLogVariablesHandler {

    public DefaultOptLogVariablesHandler() {
    }

    @Override
    public Map<String, Object> getVariables(Method method, Object[] args, OptLogRecord optLogRecord) {
        Map<String, Object> attributeMapping = Maps.newHashMap();
        Map<String, Object> paramMap = AopUtil.getParameterAnnotationMap(method, args, OptLogParam.class, "value");
        if (MapUtil.isNotEmpty(paramMap)) {
            attributeMapping.putAll(paramMap);
        }
        String extractParams = optLogRecord.extractParams();
        if (StringUtils.isNotEmpty(extractParams)) {
            String[] headerArray = StringUtils.split(extractParams, StrPool.COMMA);
            for (String header : headerArray) {
                attributeMapping.put(header, RequestLocalContextHolder.get(header));
            }
        }
        String expressionJson = optLogRecord.expressionJson();
        if (StringUtils.isNotBlank(expressionJson)) {
            List<ExpressionTemplate> expressionTemplateList = JSONArray.parseArray(expressionJson, ExpressionTemplate.class);
            for (ExpressionTemplate expressionTemplate : expressionTemplateList) {
                String value = AopUtil.parseExpression(method, args, expressionTemplate.getExpression());
                attributeMapping.put(expressionTemplate.getKey(), value);
            }
        }
        return attributeMapping;
    }
}
