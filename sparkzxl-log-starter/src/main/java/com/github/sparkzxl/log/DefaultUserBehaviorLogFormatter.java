package com.github.sparkzxl.log;

import org.slf4j.Logger;
import wiki.xsx.core.log.Level;

/**
 * description: 默认用户行为格式化
 *
 * @author zhouxinlei
 */
public class DefaultUserBehaviorLogFormatter implements UserBehaviorLogFormatter {

    @Override
    public String format(Logger log, Level level, String busName, Object[] args) {
        return "";
    }
}
