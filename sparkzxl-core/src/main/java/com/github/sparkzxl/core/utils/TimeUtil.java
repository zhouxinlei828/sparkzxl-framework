package com.github.sparkzxl.core.utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * description: 时间转换
 *
 * @author zhouxinlei
 */
public class TimeUtil {

    public static boolean hasMillis(Duration duration) {
        return duration.toMillis() % 1000L != 0L;
    }

    public static long toSeconds(Duration duration) {
        return roundUpIfNecessary(duration.toMillis(), duration.getSeconds());
    }

    public static long toSeconds(long timeout, TimeUnit unit) {
        return roundUpIfNecessary(timeout, unit.toSeconds(timeout));
    }

    public static long toMillis(long timeout, TimeUnit unit) {
        return roundUpIfNecessary(timeout, unit.toMillis(timeout));
    }

    private static long roundUpIfNecessary(long timeout, long convertedTimeout) {
        return timeout > 0L && convertedTimeout == 0L ? 1L : convertedTimeout;
    }
}
