package com.sparksys.cloud.utils;

import org.springframework.cloud.openfeign.FeignFormatterRegistrar;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;


/**
 * 在feign调用方配置， 解决入参和出参是 date 类型.
 *
 * @author zuihou
 * @return
 */
public class DateFormatRegister implements FeignFormatterRegistrar {

    public final static String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public final static String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    public DateFormatRegister() {
    }

    @Override
    public void registerFormatters(FormatterRegistry registry) {
        registry.addConverter(Date.class, String.class, new Date2StringConverter());
        registry.addConverter(LocalDateTime.class, String.class, new LocalDateTime2StringConverter());
        registry.addConverter(LocalDate.class, String.class, new LocalDate2StringConverter());
        registry.addConverter(LocalTime.class, String.class, new LocalTime2StringConverter());
    }

    private static class Date2StringConverter implements Converter<Date, String> {
        @Override
        public String convert(Date source) {
            return new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT).format(source);
        }
    }

    private static class LocalDateTime2StringConverter implements Converter<LocalDateTime, String> {
        @Override
        public String convert(LocalDateTime source) {
            return source.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT));
        }
    }

    private static class LocalDate2StringConverter implements Converter<LocalDate, String> {
        @Override
        public String convert(LocalDate source) {
            return source.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT));
        }
    }

    private static class LocalTime2StringConverter implements Converter<LocalTime, String> {
        @Override
        public String convert(LocalTime source) {
            return source.format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
        }
    }
}
