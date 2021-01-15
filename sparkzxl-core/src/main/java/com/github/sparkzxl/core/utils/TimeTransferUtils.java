package com.github.sparkzxl.core.utils;

/**
 * description: 时间转换
 *
 * @author: zhouxinlei
 * @date: 2021-01-04 10:54:49
 */
public class TimeTransferUtils {

    public static long secondsToMilliseconds(long time) {
        return 1000 * time;
    }

    public static long minutesToMilliseconds(long time) {
        return (60 * 1000) * time;
    }

    public static long hoursToMilliseconds(long time) {
        return (60 * 60 * 1000) * time;
    }

    public static long hoursToSeconds(long time) {
        return 3600 * time;
    }
    public static long daysToMilliseconds(long time) {
        return (24 * 60 * 60 * 1000) * time;
    }
}
