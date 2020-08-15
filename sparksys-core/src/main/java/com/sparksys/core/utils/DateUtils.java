package com.sparksys.core.utils;

import cn.hutool.core.date.*;
import com.sparksys.core.entity.DateInfo;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

/**
 * description: DateUtils工具类
 *
 * @author zhouxinlei
 * @date 2020-05-24 12:52:19
 */
@Slf4j
public class DateUtils extends DateUtil {

    /**
     * @param dateTimeFormatter 格式化日期
     * @return String
     * @author zhouxinlei
     * @date 2020-01-21 09:59:33
     * @see DateTimeFormatter
     */
    public static String now(DateTimeFormatter dateTimeFormatter) {
        return formatDate(LocalDateTime.now(), dateTimeFormatter);
    }

    public static LocalDateTime formatDate(String date, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    public static String formatDate(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

    /**
     * 判断选择的日期是否是今天
     *
     * @param date
     * @return
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    /**
     * 判断选择的日期是否是今天
     *
     * @param localDateTime
     * @return boolean
     */
    public static boolean isToday(LocalDateTime localDateTime) {
        return isSameDay(localDateTime2Date(localDateTime), new Date());
    }

    /**
     * 判断选择的日期是否是本周
     *
     * @param time
     * @return boolean
     */
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    /**
     * 判断选择的日期是否是本周
     *
     * @param date
     * @return boolean
     */
    public static boolean isThisWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(date);
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    /**
     * 判断选择的日期是否是本周
     *
     * @param localDateTime
     * @return boolean
     */
    public static boolean isThisWeek(LocalDateTime localDateTime) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(localDateTime2Date(localDateTime));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }

    /**
     * 判断选择的日期是否是本月
     *
     * @param date
     * @return boolean
     */
    public static boolean isThisMonth(Date date) {
        return isThisTime(date, "yyyy-MM");
    }

    /**
     * 判断选择的日期是否是本月
     *
     * @param localDateTime
     * @return boolean
     */
    public static boolean isThisMonth(LocalDateTime localDateTime) {
        return isThisTime(localDateTime2Date(localDateTime), "yyyy-MM");
    }

    public static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        //参数时间
        String param = sdf.format(date);
        //当前时间
        String now = sdf.format(new Date());
        return param.equals(now);
    }

    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static DateInfo getDateInfo(Date startDate, Date endDate) {
        DateInfo dateInfo = new DateInfo();
        Optional.ofNullable(startDate).ifPresent(value -> {
            dateInfo.setStartTime(beginOfDay(startDate).toString(DatePattern.NORM_DATETIME_PATTERN));
            dateInfo.setStartDate(startDate);
        });
        String endTime =
                Optional.ofNullable(endDate).map(value -> endOfDay(value).toString(DatePattern.NORM_DATETIME_PATTERN)).orElseGet(DateUtil::now);
        dateInfo.setEndTime(endTime);
        dateInfo.setEndDate(parse(endTime));
        return dateInfo;
    }

    public static String formatBetween(LocalDateTime localDateTime, Date endDate, BetweenFormater.Level level) {
        return formatBetween(DateUtils.localDateTime2Date(localDateTime), endDate, level);
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.formatBetween(377820));
    }
}
