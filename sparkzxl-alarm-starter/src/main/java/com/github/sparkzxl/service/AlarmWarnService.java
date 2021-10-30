package com.github.sparkzxl.service;

/**
 * description: 日志告警服务类
 *
 * @author zhoux
 */
@FunctionalInterface
public interface AlarmWarnService {

    /**
     * 发送信息
     *
     * @param message message
     * @return boolean
     */
    boolean send(String message);
}
