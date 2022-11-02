package com.github.sparkzxl.mybatis.send;

import com.github.sparkzxl.mybatis.plugins.SlowSqlMonitorInterceptor;
import lombok.extern.slf4j.Slf4j;

/**
 * description: 消息通知抽象类
 *
 * @author zhouxinlei
 * @since 2022-06-16 17:37:21
 */
@Slf4j
public abstract class AbstractSendNoticeService implements SendNoticeService {

    @Override
    public void send(SqlMonitorMessage sqlMonitorMessage) {
        String sqlMsg;
        if (sqlMonitorMessage.getType() == SlowSqlMonitorInterceptor.Type.SLOW_SQL) {
            sqlMsg = String.format("\n慢sql, 执行耗时: [%d]ms，检测耗时：[%d]ms\nSQLId: %s\nSQL语句: %s \n方法调用信息: %s",
                    sqlMonitorMessage.getExecuteTime(), sqlMonitorMessage.getCheckTime(), sqlMonitorMessage.getSqlId(),
                    sqlMonitorMessage.getSql(), sqlMonitorMessage.getStackTrace());
        } else {
            sqlMsg = String.format("\n异常sql, 检测耗时：[%d]ms\nSQLId: %s\nSQL语句: %s \n异常信息: %s \n方法调用信息: %s",
                    sqlMonitorMessage.getCheckTime(), sqlMonitorMessage.getSqlId(), sqlMonitorMessage.getSql(),
                    sqlMonitorMessage.getExceptionMsg(), sqlMonitorMessage.getStackTrace());
        }
        log.warn(sqlMsg);
        sendNotice(sqlMonitorMessage);
    }

    /**
     * 发送通知
     *
     * @param sqlMonitorMessage SQL监控消息
     */
    public abstract void sendNotice(SqlMonitorMessage sqlMonitorMessage);
}
