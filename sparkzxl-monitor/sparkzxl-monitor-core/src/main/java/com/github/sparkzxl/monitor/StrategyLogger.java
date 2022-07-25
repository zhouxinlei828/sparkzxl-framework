package com.github.sparkzxl.monitor;

/**
 * description: 策略日志接口类
 *
 * @author zhouxinlei
 * @since 2022-07-25 13:39:30
 */
public interface StrategyLogger {

    /**
     * 控制台输出
     */
    void loggerOutput();

    /**
     * 清除日志
     */
    void loggerClear();

    /**
     * debug 日志
     */
    void loggerDebug();

}
