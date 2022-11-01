package com.github.sparkzxl.mybatis.send;

import com.github.sparkzxl.mybatis.plugins.SlowSqlMonitorInterceptor;
import lombok.Data;

/**
 * description: SQL监控消息
 *
 * @author zhouxinlei
 * @since 2022-06-16 17:23:12
 */
@Data
public class SqlMonitorMessage {

    /**
     * 监控类型
     */
    private SlowSqlMonitorInterceptor.Type type;

    /**
     * sqlId
     */
    private String sqlId;

    /**
     * sql
     */
    private String sql;

    /**
     * 异常信息
     */
    private String exceptionMsg;

    /**
     * 方法调用堆栈
     */
    private String stackTrace;

    /**
     * 检测耗时
     */
    private long checkTime;

    /**
     * 执行耗时
     */
    private long executeTime;
}
