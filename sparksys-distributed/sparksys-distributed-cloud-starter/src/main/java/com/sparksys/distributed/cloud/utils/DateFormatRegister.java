package com.sparksys.distributed.cloud.utils;

import cn.hutool.core.date.DatePattern;
import com.sparksys.core.utils.DateUtils;
import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.format.FormatterRegistry;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * description: 在feign调用方配置， 解决入参和出参是 date 类型.
 *
 * @author: zhouxinlei
 * @date: 2020-08-21 17:45:19
 */
public class DateFormatRegister implements FeignFormatterRegistrar {

    public DateFormatRegister() {
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(Date.class, String.class, date -> DateUtils.format(date, DatePattern.NORM_DATETIME_PATTERN));
        registry.addConverter(LocalDateTime.class, String.class, date -> DateUtils.format(date, DatePattern.NORM_DATETIME_PATTERN));
        registry.addConverter(LocalDate.class, String.class,
                date -> date.format(DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN)));
        registry.addConverter(LocalTime.class, String.class, date -> date.format(DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN)));
    }
}
