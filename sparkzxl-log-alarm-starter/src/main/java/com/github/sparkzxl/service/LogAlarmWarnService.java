package com.github.sparkzxl.service;

import com.github.sparkzxl.entity.core.ErrorInfo;

/**
 * description: 日志告警服务类
 *
 * @author zhoux
 * @date 2021-08-21 11:36:13
 */
public interface LogAlarmWarnService {

    /**
     * 发送信息
     *
     * @param context   上下文
     * @param throwable 异常
     * @return boolean
     */
    boolean send(ErrorInfo context, Throwable throwable);
}
