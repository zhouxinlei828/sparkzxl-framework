package com.github.sparkzxl.service;

import lombok.extern.slf4j.Slf4j;

/**
 * description:
 *
 * @author zhoux
 */
@Slf4j
public abstract class BaseWarnService implements AlarmWarnService {

    @Override
    public boolean send(String message) {
        try {
            doSend(message);
            return true;
        } catch (Exception e) {
            log.error("send warn message error", e);
            return false;
        }
    }

    protected abstract void doSend(String message) throws Exception;
}
