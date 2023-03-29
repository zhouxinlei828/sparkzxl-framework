package com.github.sparkzxl.core.util;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.format.FastDateFormat;
import com.github.sparkzxl.core.entity.DateInfo;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * description: DateUtils工具类
 *
 * @author zhouxinlei
 */
@Slf4j
public class DateUtils extends DateUtil {

    /**
     * 格式化日期
     *
     * @param dateTimeFormatter 格式
     * @return String
     */
    public static String now(DateTimeFormatter dateTimeFormatter) {
        return formatDate(LocalDateTime.now(), dateTimeFormatter);
    }

    /**
     * 格式化时间
     *
     * @param date              时间
     * @param dateTimeFormatter 格式
     * @return LocalDateTime
     */
    public static LocalDateTime formatDate(String date, DateTimeFormatter dateTimeFormatter) {
        return LocalDateTime.parse(date, dateTimeFormatter);
    }

    /**
     * 格式化时间
     *
     * @param localDateTime     时间
     * @param dateTimeFormatter 格式
     * @return String
     */
    public static String formatDate(LocalDateTime localDateTime, DateTimeFormatter dateTimeFormatter) {
        return localDateTime.format(dateTimeFormatter);
    }

    public static Date formatDate(String dateStr, String datePattern) {
        FastDateFormat fastDateFormat = FastDateFormat.getInstance(datePattern);
        try {
            return fastDateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.warn("格式化日期发生异常：[{}]", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断选择的日期是否是今天
     *
     * @param date 日期
     * @return boolean
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    /**
     * 判断选择的日期是否是今天
     *
     * @param localDateTime 时间
     * @return boolean
     */
    public static boolean isToday(LocalDateTime localDateTime) {
        return isSameDay(localDateTime2Date(localDateTime), new Date());
    }

    /**
     * 判断选择的日期是否是本周
     *
     * @param time 时间
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
     * @param date 日期
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
     * @param localDateTime 时间
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
     * @param date 时间
     * @return boolean
     */
    public static boolean isThisMonth(Date date) {
        return isThisTime(date, "yyyy-MM");
    }

    /**
     * 判断选择的日期是否是本月
     *
     * @param localDateTime 时间
     * @return boolean
     */
    public static boolean isThisMonth(LocalDateTime localDateTime) {
        return isThisTime(localDateTime2Date(localDateTime), "yyyy-MM");
    }

    public static boolean isThisTime(Date date, String pattern) {
        FastDateFormat fastDateFormat = FastDateFormat.getInstance(pattern);
        //参数时间
        String param = fastDateFormat.format(date);
        //当前时间
        String now = fastDateFormat.format(new Date());
        return param.equals(now);
    }

    /**
     * LocalDateTime转换为Date格式
     *
     * @param localDateTime 时间
     * @return Date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    /**
     * 获取开始时间结束时间信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return DateInfo
     */
    public static DateInfo getDateInfo(Date startDate, Date endDate) {
        DateInfo dateInfo = new DateInfo();
        Optional.ofNullable(startDate).ifPresent(value -> {
            DateTime beginOfDay = beginOfDay(startDate);
            dateInfo.setStartTime(DatePattern.NORM_DATETIME_FORMAT.format(beginOfDay));
            dateInfo.setStartDate(beginOfDay);
        });
        Optional.ofNullable(endDate).ifPresent(value -> {
            DateTime endOfDay = endOfDay(startDate);
            dateInfo.setEndTime(DatePattern.NORM_DATETIME_FORMAT.format(endOfDay));
            dateInfo.setEndDate(endOfDay);
        });
        return dateInfo;
    }

    /**
     * 计算时间差
     *
     * @param beginTime 开始时间
     * @param endTime   结束时间
     * @param level     格式化类型
     * @return String
     */
    public static String formatBetween(LocalDateTime beginTime, LocalDateTime endTime, BetweenFormatter.Level level) {
        return formatBetween(DateUtils.localDateTime2Date(beginTime), DateUtils.localDateTime2Date(endTime), level);
    }

    /**
     * 查询指定日期月份总天数
     *
     * @param date 指定日期
     * @return int
     */
    public static int getDaysOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    public static Date localDate2Date(LocalDate localDate) {
        if (null == localDate) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
        return Date.from(zonedDateTime.toInstant());
    }

    public static long millisecond(LocalDateTime localDateTime) {
        return localDateTime.toEpochSecond(ZoneOffset.of("+8"));
    }
}
