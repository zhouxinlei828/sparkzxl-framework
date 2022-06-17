package com.github.sparkzxl.database.send;

import com.github.sparkzxl.database.plugins.SqlMonitorMessage;

/**
 * description: 推送消息service
 *
 * @author zhouxinlei
 * @since 2022-06-16 17:19:29
 */
public interface ISendNoticeService {
    /**
     * 发送消息
     *
     * @param msg 消息
     */
    void send(SqlMonitorMessage msg);
}
