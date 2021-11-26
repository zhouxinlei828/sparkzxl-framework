package com.github.sparkzxl.gateway.filter.log;

/**
 * description: 操作日志配置
 *
 * @author zhouxinlei
 * @date 2021-11-26 16:09:38
 */
public interface OpLogPropertiesLocator {

    /**
     * 获取操作日志配置
     *
     * @return OpLogProperties
     */
    OpLogRuleMatch getOpLogProperties();
}
