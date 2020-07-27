package com.sparksys.core.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

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
        return isThisTime(date, "yyyy-MM-dd");
    }

    /**
     * 判断选择的日期是否是本周
     *
     * @param time
     * @return
     */
    public static boolean isThisWeek(long time) {
        Calendar calendar = Calendar.getInstance();
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        calendar.setTime(new Date(time));
        int paramWeek = calendar.get(Calendar.WEEK_OF_YEAR);
        return paramWeek == currentWeek;
    }


    /**
     * 判断选择的日期是否是本月
     *
     * @param date
     * @return
     */
    public static boolean isThisMonth(Date date) {
        return isThisTime(date, "yyyy-MM");
    }

    public static boolean isThisTime(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        //参数时间
        String param = sdf.format(date);
        //当前时间
        String now = sdf.format(new Date());
        return param.equals(now);
    }

    public static String getDatePoor(Date endDate, Date nowDate) {
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        return getDatePoor(diff);
    }

    /**
     * 获取时间差天数：小时：分钟
     *
     * @param diff 时间差
     * @return String
     */
    public static String getDatePoor(Long diff) {
        if (ObjectUtils.isEmpty(diff)) {
            return null;
        }
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    public static void main(String[] args) {
        String date = "2020-04-29 10:54:00";
        System.out.println(DateUtil.between(new Date(), DateUtil.parseDate(date), DateUnit.SECOND, false));
        System.out.println(getDatePoor(450372L));
        System.out.println(getDatePoor(new Date(), DateUtil.parseDate(date)));
    }
}
