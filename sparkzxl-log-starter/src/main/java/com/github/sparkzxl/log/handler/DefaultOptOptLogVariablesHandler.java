package com.github.sparkzxl.log.handler;

import cn.hutool.core.map.MapUtil;
import com.github.sparkzxl.core.context.RequestLocalContextHolder;
import com.github.sparkzxl.core.util.AopUtil;
import com.github.sparkzxl.core.util.StrPool;
import com.github.sparkzxl.log.annotation.OptLogParam;
import com.github.sparkzxl.log.annotation.OptLogRecord;
import com.google.common.collect.Maps;
import java.lang.reflect.Method;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * description: 默认操作日志变量参数实现类
 *
 * @author zhouxinlei
 * @since 2022-01-05 12:32:01
 */
public class DefaultOptOptLogVariablesHandler implements IOptLogVariablesHandler {

    public DefaultOptOptLogVariablesHandler() {
    }

    @Override
    public Map<String, Object> getVariables(Method method, Object[] args, OptLogRecord optLogRecord) {
        Map<String, Object> attributeMapping = Maps.newHashMap();
        Map<String, Object> paramMap = AopUtil.generateMap(method, args, OptLogParam.class, "value");
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
        return attributeMapping;
    }
}
