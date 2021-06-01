package com.github.sparkzxl.log;

import org.slf4j.Logger;
import wiki.xsx.core.log.Level;

/**
 * description: 用户行为日志格式化
 *
 * @author zhouxinlei
 * @date 2021-05-23 20:56:18
 */
public interface UserBehaviorLogFormatter {

    /**
     * 日志格式化
     *
     * @param log     log
     * @param level   日志级别
     * @param busName 业务名称
     * @param args    参数
     * @return String
     */
    String format(Logger log, Level level, String busName, Object[] args);

}
