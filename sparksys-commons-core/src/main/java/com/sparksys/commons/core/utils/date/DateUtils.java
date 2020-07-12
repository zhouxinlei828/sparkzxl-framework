package com.sparksys.commons.core.utils.date;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;

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
        String param = sdf.format(date);//参数时间
        String now = sdf.format(new Date());//当前时间
        return param.equals(now);
    }

    public static void main(String[] args) {
        String date = "2020-04-29 10:54:00";
        System.out.println(DateUtil.between(new Date(), DateUtil.parseDate(date), DateUnit.SECOND, false));
    }
}
