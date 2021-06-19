package com.github.sparkzxl.log;

import com.github.sparkzxl.log.entity.RequestInfo;
import wiki.xsx.core.support.MethodInfo;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * description: 日志持久化存储 服务类
 *
 * @author zhouxinlei
 */
public interface LogStoreService {

    /**
     * 保存web日志
     *
     * @param requestInfo 请求日志实
     * @return boolean
     */
    boolean saveLog(RequestInfo requestInfo);

    /**
     * 保存日志
     *
     * @param annotation 注解
     * @param methodInfo 方法信息
     * @param paramMap   参数
     * @param result     结果
     * @return boolean
     */
    boolean saveLog(Annotation annotation, MethodInfo methodInfo, Map<String, Object> paramMap, Object result);

}
