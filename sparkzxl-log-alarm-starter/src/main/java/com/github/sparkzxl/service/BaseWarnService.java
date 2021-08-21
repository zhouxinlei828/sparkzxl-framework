package com.github.sparkzxl.service;

import com.github.sparkzxl.entity.core.ErrorInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * description:
 *
 * @author zhoux
 * @date 2021-08-21 12:11:43
 */
@Slf4j
public abstract class BaseWarnService implements LogAlarmWarnService {


    @Override
    public boolean send(ErrorInfo context, Throwable throwable) {
        try {
            doSend(context, throwable);
            return true;
        } catch (Exception e) {
            log.error("send warn message error", e);
            return false;
        }
    }

    protected abstract void doSend(ErrorInfo context, Throwable throwable) throws Exception;
}
