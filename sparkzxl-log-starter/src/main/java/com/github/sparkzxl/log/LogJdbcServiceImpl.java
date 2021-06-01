package com.github.sparkzxl.log;

import com.github.sparkzxl.log.entity.RequestInfo;
import wiki.xsx.core.support.MethodInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * description: 日志持久化存储 服务类
 *
 * @author zhouxinlei
 * @date 2021-05-23 16:25:21
 */
public class LogJdbcServiceImpl implements LogStoreService {

    @Override
    public boolean saveLog(RequestInfo requestInfo) {
        return false;
    }

    @Override
    public boolean saveLog(Annotation annotation, MethodInfo methodInfo, Map<String, Object> paramMap, Object result) {
        return false;
    }
}
